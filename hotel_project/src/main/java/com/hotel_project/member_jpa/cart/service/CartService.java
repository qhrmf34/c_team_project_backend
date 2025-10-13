package com.hotel_project.member_jpa.cart.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import com.hotel_project.hotel_jpa.hotel.repository.HotelRepository;
import com.hotel_project.member_jpa.cart.dto.CartDto;
import com.hotel_project.member_jpa.cart.dto.CartEntity;
import com.hotel_project.member_jpa.cart.mapper.CartMapper;
import com.hotel_project.member_jpa.cart.repository.CartRepository;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HotelRepository hotelRepository;

    /**
     * 장바구니 토글 (추가/제거) - Repository 사용
     */
    @Transactional
    public boolean toggle(Long memberId, Long hotelId) throws CommonExceptionTemplate {
        log.info("장바구니 토글 - memberId: {}, hotelId: {}", memberId, hotelId);

        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다."));

        HotelEntity hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "호텔을 찾을 수 없습니다."));

        Optional<CartEntity> existing = cartRepository
                .findByMemberEntity_IdAndHotelEntity_Id(memberId, hotelId);

        if (existing.isPresent()) {
            cartRepository.delete(existing.get());
            log.info("장바구니에서 제거 - cartId: {}", existing.get().getId());
            return false;
        } else {
            CartEntity cart = new CartEntity();
            cart.setMemberEntity(member);
            cart.setHotelEntity(hotel);
            cartRepository.save(cart);
            log.info("장바구니에 추가 - cartId: {}", cart.getId());
            return true;
        }
    }

    /**
     * 회원의 장바구니 목록 조회 (페이지네이션 지원)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findByMemberIdWithPagination(
            Long memberId,
            Integer offset,
            Integer size
    ) throws CommonExceptionTemplate {
        log.info("장바구니 조회 (페이지네이션) - memberId: {}, offset: {}, size: {}",
                memberId, offset, size);

        // 회원 존재 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다."));

        // 전체 개수 조회
        int totalCount = cartMapper.countByMemberId(memberId);

        // 장바구니 목록 조회 (페이지네이션)
        List<CartDto> carts = cartMapper.findByMemberId(memberId, offset, size);

        log.info("장바구니 조회 완료 - memberId: {}, count: {}, totalCount: {}",
                memberId, carts.size(), totalCount);

        Map<String, Object> result = new HashMap<>();
        result.put("hotels", carts);
        result.put("totalCount", totalCount);

        return result;
    }

    @Transactional(readOnly = true)
    public List<CartDto> findByMemberId(Long memberId) throws CommonExceptionTemplate {
        log.info("장바구니 조회 - memberId: {}", memberId);

        memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다."));

        List<CartDto> carts = cartMapper.findByMemberId(memberId, null, null);
        log.info("장바구니 조회 완료 - memberId: {}, count: {}", memberId, carts.size());

        return carts;
    }

    /**
     * 장바구니에서 삭제 (회원ID + 호텔ID) - Repository 사용
     */
    @Transactional
    public void delete(Long memberId, Long hotelId) throws CommonExceptionTemplate {
        log.info("장바구니 삭제 - memberId: {}, hotelId: {}", memberId, hotelId);

        CartEntity cart = cartRepository.findByMemberEntity_IdAndHotelEntity_Id(memberId, hotelId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "장바구니에 없습니다."));

        cartRepository.delete(cart);
        log.info("장바구니에서 삭제 완료 - cartId: {}", cart.getId());
    }

    /**
     * 장바구니에서 삭제 (장바구니 ID) - Repository 사용
     */
    @Transactional
    public void deleteById(Long memberId, Long cartId) throws CommonExceptionTemplate {
        log.info("장바구니 삭제 by ID - memberId: {}, cartId: {}", memberId, cartId);

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "장바구니 항목을 찾을 수 없습니다."));

        if (!cart.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "권한이 없습니다.");
        }

        cartRepository.delete(cart);
        log.info("장바구니에서 삭제 완료 - cartId: {}", cartId);
    }

    /**
     * 장바구니 전체 비우기 - Repository 사용
     */
    @Transactional
    public void clearCart(Long memberId) throws CommonExceptionTemplate {
        log.info("장바구니 전체 삭제 - memberId: {}", memberId);

        cartRepository.deleteByMemberEntity_Id(memberId);
        log.info("장바구니 전체 삭제 완료 - memberId: {}", memberId);
    }

    /**
     * 장바구니 포함 여부 확인 - Mapper 사용
     */
    @Transactional(readOnly = true)
    public boolean isInCart(Long memberId, Long hotelId) {
        if (memberId == null || hotelId == null) {
            return false;
        }
        return cartMapper.existsByMemberIdAndHotelId(memberId, hotelId) > 0;
    }

    /**
     * 장바구니의 호텔 ID 목록 조회 - Mapper 사용
     */
    @Transactional(readOnly = true)
    public List<Long> getCartHotelIds(Long memberId) {
        if (memberId == null) {
            return List.of();
        }
        return cartMapper.findHotelIdsByMemberId(memberId);
    }

    /**
     * 장바구니 항목 개수 조회 - Mapper 사용
     */
    @Transactional(readOnly = true)
    public long getCartCount(Long memberId) {
        if (memberId == null) {
            return 0;
        }
        return cartMapper.countByMemberId(memberId);
    }
}