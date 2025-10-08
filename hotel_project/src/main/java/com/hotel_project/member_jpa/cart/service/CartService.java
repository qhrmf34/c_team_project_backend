package com.hotel_project.member_jpa.cart.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import com.hotel_project.hotel_jpa.room.repository.RoomRepository;
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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CartService {

    @Autowired
    private CartRepository cartRepository;  // JPA Repository - CUD용

    @Autowired
    private CartMapper cartMapper;  // MyBatis Mapper - 조회용

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * 장바구니 토글 (추가/제거) - Repository 사용
     */
    @Transactional
    public boolean toggle(Long memberId, Long roomId) throws CommonExceptionTemplate {
        log.info("장바구니 토글 - memberId: {}, roomId: {}", memberId, roomId);

        // 회원 존재 확인
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다."));

        // 객실 존재 확인
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "객실을 찾을 수 없습니다."));

        // 이미 장바구니에 있는지 확인
        Optional<CartEntity> existing = cartRepository
                .findByMemberEntity_IdAndRoomEntity_Id(memberId, roomId);

        if (existing.isPresent()) {
            // 이미 장바구니에 있는 경우 -> 삭제 (Repository 사용)
            cartRepository.delete(existing.get());
            log.info("장바구니에서 제거 - cartId: {}", existing.get().getId());
            return false;
        } else {
            // 장바구니에 없는 경우 -> 추가 (Repository 사용)
            CartEntity cart = new CartEntity();
            cart.setMemberEntity(member);
            cart.setRoomEntity(room);
            cartRepository.save(cart);
            log.info("장바구니에 추가 - cartId: {}", cart.getId());
            return true;
        }
    }

    /**
     * 회원의 장바구니 목록 조회 - Mapper 사용
     */
    @Transactional(readOnly = true)
    public List<CartDto> findByMemberId(Long memberId) throws CommonExceptionTemplate {
        log.info("장바구니 조회 - memberId: {}", memberId);

        // 회원 존재 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "회원을 찾을 수 없습니다."));

        List<CartDto> carts = cartMapper.findByMemberId(memberId);
        log.info("장바구니 조회 완료 - memberId: {}, count: {}", memberId, carts.size());

        return carts;
    }

    /**
     * 장바구니에서 삭제 (회원ID + 객실ID) - Repository 사용
     */
    @Transactional
    public void delete(Long memberId, Long roomId) throws CommonExceptionTemplate {
        log.info("장바구니 삭제 - memberId: {}, roomId: {}", memberId, roomId);

        CartEntity cart = cartRepository.findByMemberEntity_IdAndRoomEntity_Id(memberId, roomId)
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

        // 본인의 장바구니인지 확인
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
    public boolean isInCart(Long memberId, Long roomId) {
        if (memberId == null || roomId == null) {
            return false;
        }
        return cartMapper.existsByMemberIdAndRoomId(memberId, roomId) > 0;
    }

    /**
     * 장바구니의 객실 ID 목록 조회 - Mapper 사용
     */
    @Transactional(readOnly = true)
    public List<Long> getCartRoomIds(Long memberId) {
        if (memberId == null) {
            return List.of();
        }
        return cartMapper.findRoomIdsByMemberId(memberId);
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