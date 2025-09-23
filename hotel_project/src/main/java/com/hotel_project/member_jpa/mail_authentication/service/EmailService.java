package com.hotel_project.member_jpa.mail_authentication.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.member_jpa.mail_authentication.dto.MailAuthenticationDto;
import com.hotel_project.member_jpa.mail_authentication.dto.MailAuthenticationEntity;
import com.hotel_project.member_jpa.mail_authentication.mapper.MailAuthenticationMapper;
import com.hotel_project.member_jpa.mail_authentication.repository.MailAuthenticationRepository;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.dto.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MailAuthenticationRepository mailAuthenticationRepository;
    private final MailAuthenticationMapper mailAuthenticationMapper;
    private final MemberMapper memberMapper;

    @Value("${email.verification.expiration:300}") // 기본 5분
    private int verificationExpiration;

    @Value("${email.verification.from-email}")
    private String fromEmail;

    /**
     * 비밀번호 재설정용 인증 코드 전송
     * - 트랜잭션을 새로 시작하여 저장 후 즉시 커밋
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String sendPasswordResetCode(String email) throws CommonExceptionTemplate {
        // MyBatis로 회원 조회
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        if (member == null) {
            throw new CommonExceptionTemplate(404, "해당 이메일로 가입된 계정이 없습니다.");
        }

        // 소셜 로그인 계정 체크
        if (member.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(400, "소셜 로그인 계정은 비밀번호 재설정을 할 수 없습니다.");
        }

        // 기존 미인증 코드들 무효화 (JPA 사용)
        invalidateExistingCodes(member.getId());

        // 새 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // DB에 인증 코드 저장 (JPA 사용)
        MailAuthenticationEntity mailAuth = new MailAuthenticationEntity();
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(member.getId());
        mailAuth.setMemberEntity(memberEntity);
        mailAuth.setCode(verificationCode);
        mailAuth.setIsVerified(false);
        mailAuth.setCreatedAt(LocalDateTime.now());
        mailAuth.setExpiresAt(LocalDateTime.now().plusSeconds(verificationExpiration));

        mailAuthenticationRepository.save(mailAuth);

        // 여기서 트랜잭션이 커밋됨

        // 이메일 전송
        sendVerificationEmail(email, verificationCode, "비밀번호 재설정");

        return "인증 코드가 이메일로 전송되었습니다.";
    }

    /**
     * 인증 코드 검증
     * - 새 트랜잭션으로 시작하여 커밋된 데이터를 조회
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean verifyCode(String email, String code) {
        // MyBatis로 회원 조회
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        if (member == null) {
            return false;
        }

        // MyBatis로 최신 미인증 코드 조회
        MailAuthenticationDto mailAuth = mailAuthenticationMapper.findLatestUnverifiedByMemberId(member.getId());
        System.out.println("조회할 회원 ID: " + member.getId());
        System.out.println("조회된 인증 코드: " + (mailAuth != null ? mailAuth.getCode() : "없음"));
        System.out.println("입력된 코드: " + code);

        if (mailAuth == null) {
            return false;
        }

        // 코드 일치 및 만료 확인
        if (!code.equals(mailAuth.getCode()) || isExpired(mailAuth)) {
            return false;
        }

        // 인증 코드 사용 처리 (JPA 사용)
        MailAuthenticationEntity entity = new MailAuthenticationEntity();
        entity.setId(mailAuth.getId());
        entity.setIsVerified(true);

        // 기존 데이터 복사
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(member.getId());
        entity.setMemberEntity(memberEntity);
        entity.setCode(mailAuth.getCode());
        entity.setCreatedAt(mailAuth.getCreatedAt());
        entity.setExpiresAt(mailAuth.getExpiresAt());

        mailAuthenticationRepository.save(entity);

        return true;
    }
    @Transactional(readOnly = true)
    public boolean isCodeAlreadyVerified(String email, String code) {
        // MyBatis로 회원 조회
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        if (member == null) {
            return false;
        }

        // 인증된 코드를 찾는 별도 쿼리 필요 (is_verified = true인 것 조회)
        MailAuthenticationDto verifiedAuth = mailAuthenticationMapper.findVerifiedByMemberIdAndCode(
                member.getId(), code
        );

        return verifiedAuth != null && !isExpired(verifiedAuth);
    }
    // ========== ALTERNATIVE SOLUTION: MyBatis 사용 방식 ==========

    /**
     * 대안 1: verifyCode를 MyBatis로만 처리
     * - 조회와 업데이트 모두 MyBatis 사용
     */
    @Transactional
    public boolean verifyCodeWithMyBatis(String email, String code) {
        // MyBatis로 회원 조회
        MemberDto member = memberMapper.findByEmailAndProvider(email, Provider.local);
        if (member == null) {
            return false;
        }

        // MyBatis로 코드와 회원 ID로 조회
        MailAuthenticationDto mailAuth = mailAuthenticationMapper.findByMemberIdAndCode(member.getId(), code);
        System.out.println("조회할 회원 ID: " + member.getId());
        System.out.println("조회된 인증 코드: " + (mailAuth != null ? mailAuth.getCode() : "없음"));
        System.out.println("입력된 코드: " + code);

        if (mailAuth == null || isExpired(mailAuth)) {
            return false;
        }

        // MyBatis로 인증 상태 업데이트 (Mapper에 update 메서드 추가 필요)
        // mailAuthenticationMapper.updateVerifiedStatus(mailAuth.getId(), true);

        // 또는 JPA로 업데이트
        MailAuthenticationEntity entity = new MailAuthenticationEntity();
        entity.setId(mailAuth.getId());
        entity.setIsVerified(true);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(member.getId());
        entity.setMemberEntity(memberEntity);
        entity.setCode(mailAuth.getCode());
        entity.setCreatedAt(mailAuth.getCreatedAt());
        entity.setExpiresAt(mailAuth.getExpiresAt());

        mailAuthenticationRepository.save(entity);

        return true;
    }

    /**
     * 6자리 랜덤 인증 코드 생성
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 기존 미인증 코드들 무효화 (JPA 사용)
     */
    private void invalidateExistingCodes(Long memberId) {
        mailAuthenticationRepository.deleteByMemberEntityIdAndIsVerifiedFalse(memberId);
    }

    /**
     * 만료 여부 확인
     */
    private boolean isExpired(MailAuthenticationDto mailAuth) {
        return LocalDateTime.now().isAfter(mailAuth.getExpiresAt());
    }

    /**
     * 이메일 전송 (비동기)
     */
    @Async
    public void sendVerificationEmail(String toEmail, String verificationCode, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Hotel Project] " + purpose + " 인증 코드");
            message.setText(createEmailContent(verificationCode, purpose));

            mailSender.send(message);
            System.out.println("이메일 전송 성공: " + toEmail);
        } catch (Exception e) {
            System.err.println("이메일 전송 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 이메일 내용 생성
     */
    private String createEmailContent(String verificationCode, String purpose) {
        return String.format("""
                안녕하세요, Hotel Project입니다.
                
                %s를 위한 인증 코드입니다:
                
                인증 코드: %s
                
                이 코드는 5분간 유효합니다.
                본인이 요청하지 않았다면 이 이메일을 무시해주세요.
                
                감사합니다.
                Hotel Project 팀
                """, purpose, verificationCode);
    }

    /**
     * 만료된 인증 코드 정리 (스케줄러에서 사용) - JPA 사용
     */
    @Transactional
    public void cleanupExpiredCodes() {
        mailAuthenticationRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}