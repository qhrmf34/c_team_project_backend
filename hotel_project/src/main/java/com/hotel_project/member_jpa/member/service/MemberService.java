package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse signup(SignupRequest signupRequest) throws CommonExceptionTemplate {
        validatePasswordMatch(signupRequest.getPassword(), signupRequest.getConfirmPassword());
        checkDuplicateEmail(signupRequest.getEmail());
        checkDuplicatePhoneNumber(signupRequest.getPhoneNumber());

        MemberEntity newMember = createMemberEntity(signupRequest);
        MemberEntity savedMember = memberRepository.save(newMember);

        String token = jwtUtil.generateToken(savedMember.getId(), savedMember.getProvider().toString());

        return new LoginResponse(
                token,
                savedMember.getId(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail(),
                savedMember.getProvider().toString()
        );
    }

    public LoginResponse login(LoginRequest loginRequest) throws CommonExceptionTemplate {
        MemberDto member = memberMapper.findByEmailAndProvider(loginRequest.getEmail(), Provider.local);

        if (member == null) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CommonExceptionTemplate(401, "이메일 또는 비밀번호가 잘못되었습니다.");
        }

        String token = jwtUtil.generateToken(member.getId(), member.getProvider().toString());

        return new LoginResponse(
                token,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getProvider().toString()
        );
    }

    public boolean existsByEmail(String email) {
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        return member != null;
    }

    public String resetPassword(String email, String newPassword) throws CommonExceptionTemplate {
        return resetPasswordInternal(email, newPassword);
    }

    @Transactional
    public String resetPasswordDirect(String email, String newPassword) throws CommonExceptionTemplate {
        return resetPasswordInternal(email, newPassword);
    }

    public MemberDto getMemberDtoByToken(String token) throws CommonExceptionTemplate {
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        MemberDto member = memberMapper.findById(memberId);

        if (member == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        member.setPassword(null);
        return member;
    }

    public MemberDto updateMemberAndReturnDto(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        updateMember(memberId, memberDto);

        MemberDto responseDto = memberMapper.findById(memberId);
        responseDto.setPassword(null);

        return responseDto;
    }

    public String updateMember(Long memberId, MemberDto memberDto) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        MemberDto existingMemberDto = memberMapper.findById(memberId);
        if (existingMemberDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        checkDuplicateEmailForUpdate(memberDto.getEmail(), existingMember.getEmail(), memberId);
        checkDuplicatePhoneNumberForUpdate(memberDto.getPhoneNumber(), existingMember.getPhoneNumber(), memberId);

        if (memberDto.getPassword() != null && existingMember.getProvider() == Provider.local) {
            memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        }

        memberDto.setUpdatedAt(LocalDateTime.now());
        existingMember.copyNotNullMembers(memberDto);

        memberRepository.save(existingMember);
        return "update ok";
    }

    public String deleteMember(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        if (!memberRepository.existsById(memberId)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        memberRepository.deleteById(memberId);
        return "delete ok";
    }

    public String generateTokenForSocialLogin(MemberEntity member) {
        return jwtUtil.generateToken(member.getId(), member.getProvider().toString());
    }

    public boolean verifyCurrentPassword(Long memberId, String currentPassword) {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null || member.getProvider() != Provider.local) {
                return false;
            }

            return passwordEncoder.matches(currentPassword, member.getPassword());

        } catch (Exception e) {
            return false;
        }
    }

    public String changePassword(Long memberId, String newPassword) throws CommonExceptionTemplate {
        try {
            MemberDto member = memberMapper.findById(memberId);

            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다.");
            }

            if (member.getProvider() != Provider.local) {
                throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }

            String encodedPassword = passwordEncoder.encode(newPassword);

            member.setPassword(encodedPassword);
            member.setUpdatedAt(LocalDateTime.now());
            MemberEntity updatedMember = new MemberEntity();
            updatedMember.copyNotNullMembers(member);

            memberRepository.save(updatedMember);

            return "비밀번호가 성공적으로 변경되었습니다.";

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            throw new CommonExceptionTemplate(500, "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public LoginResponse completeSocialSignup(String tempToken, CompleteSignupRequest request) throws CommonExceptionTemplate {

        if (!jwtUtil.validateToken(tempToken)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        if (!jwtUtil.isSocialSignupToken(tempToken)) {
            throw new CommonExceptionTemplate(400, "소셜 회원가입 토큰이 아닙니다.");
        }

        String providerId = jwtUtil.getProviderIdFromToken(tempToken);
        String providerStr = jwtUtil.getProviderFromToken(tempToken);
        String emailFromToken = jwtUtil.getEmailFromToken(tempToken);
        String firstNameFromToken = jwtUtil.getFirstNameFromToken(tempToken);
        String lastNameFromToken = jwtUtil.getLastNameFromToken(tempToken);

        Provider provider = Provider.valueOf(providerStr);

        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, providerId);
        if (existingMember != null) {
            throw new CommonExceptionTemplate(409, "이미 가입된 회원입니다.");
        }

        checkDuplicatePhoneNumber(request.getPhoneNumber());

        String finalEmail = determineEmail(provider, providerId, emailFromToken);

        MemberEntity newMember = createSocialMemberEntity(
                provider, providerId, finalEmail,
                firstNameFromToken, lastNameFromToken, request
        );

        MemberEntity savedMember = memberRepository.save(newMember);

        String token = jwtUtil.generateToken(savedMember.getId(), provider.toString());

        return new LoginResponse(
                token,
                savedMember.getId(),
                savedMember.getFirstName(),
                savedMember.getLastName(),
                savedMember.getEmail(),
                provider.toString()
        );
    }

    private String determineEmail(Provider provider, String providerId, String emailFromToken)
            throws CommonExceptionTemplate {
        if (provider == Provider.google) {
            if (memberRepository.existsByEmail(emailFromToken)) {
                throw new CommonExceptionTemplate(409, "이미 사용 중인 이메일입니다.");
            }
            return emailFromToken;
        } else {
            return generateUniqueRandomEmail(provider, providerId);
        }
    }

    private String generateUniqueRandomEmail(Provider provider, String providerId) {
        String hash = Integer.toHexString(providerId.hashCode());
        if (hash.startsWith("-")) {
            hash = hash.substring(1);
        }
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);

        String email = String.format("%s_%s_%s@%s.social",
                provider.toString().toLowerCase(),
                hash,
                timestamp,
                provider.toString().toLowerCase()
        );

        int attempt = 0;
        while (memberRepository.existsByEmail(email) && attempt < 10) {
            timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
            email = String.format("%s_%s_%s_%d@%s.social",
                    provider.toString().toLowerCase(),
                    hash,
                    timestamp,
                    attempt,
                    provider.toString().toLowerCase()
            );
            attempt++;
        }

        return email;
    }

    private MemberEntity createMemberEntity(SignupRequest signupRequest) {
        MemberEntity member = new MemberEntity();
        member.setFirstName(signupRequest.getFirstName());
        member.setLastName(signupRequest.getLastName());
        member.setEmail(signupRequest.getEmail());
        member.setPhoneNumber(signupRequest.getPhoneNumber());
        member.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        member.setProvider(Provider.local);
        member.setRoadAddress(signupRequest.getRoadAddress());
        member.setDetailAddress(signupRequest.getDetailAddress());
        member.setProviderId(null);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return member;
    }

    private MemberEntity createSocialMemberEntity(Provider provider, String providerId, String email,
                                                  String firstName, String lastName,
                                                  CompleteSignupRequest request) {
        MemberEntity member = new MemberEntity();
        member.setProvider(provider);
        member.setProviderId(providerId);
        member.setEmail(email);
        member.setPhoneNumber(request.getPhoneNumber());
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setRoadAddress(request.getRoadAddress());
        member.setDetailAddress(request.getDetailAddress());
        member.setPassword(null);
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return member;
    }

    private String resetPasswordInternal(String email, String newPassword) throws CommonExceptionTemplate {
        MemberDto memberDto = memberMapper.findByEmailAndProvider(email, Provider.local);

        if (memberDto == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

        if (memberDto.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호를 재설정할 수 없습니다.");
        }

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.copyMembers(memberDto);
        memberEntity.setPassword(passwordEncoder.encode(newPassword));
        memberEntity.setUpdatedAt(LocalDateTime.now());

        memberRepository.save(memberEntity);

        return "비밀번호가 성공적으로 재설정되었습니다.";
    }

    private void validatePasswordMatch(String password, String confirmPassword) throws CommonExceptionTemplate {
        if (!password.equals(confirmPassword)) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }
    }

    private void checkDuplicateEmail(String email) throws CommonExceptionTemplate {
        if (memberRepository.existsByEmail(email)) {
            throw MemberException.DUPLICATE_DATA.getException();
        }
    }

    private void checkDuplicatePhoneNumber(String phoneNumber) throws CommonExceptionTemplate {
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }
    }

    private void checkDuplicateEmailForUpdate(String newEmail, String currentEmail, Long memberId)
            throws CommonExceptionTemplate {
        if (newEmail != null && !newEmail.equals(currentEmail) &&
                memberRepository.existsByEmailAndIdNot(newEmail, memberId)) {
            throw MemberException.DUPLICATE_DATA.getException();
        }
    }

    private void checkDuplicatePhoneNumberForUpdate(String newPhoneNumber, String currentPhoneNumber, Long memberId)
            throws CommonExceptionTemplate {
        if (newPhoneNumber != null && !newPhoneNumber.equals(currentPhoneNumber) &&
                memberRepository.existsByPhoneNumberAndIdNot(newPhoneNumber, memberId)) {
            throw new CommonExceptionTemplate(409, "이미 사용 중인 전화번호입니다.");
        }
    }
}