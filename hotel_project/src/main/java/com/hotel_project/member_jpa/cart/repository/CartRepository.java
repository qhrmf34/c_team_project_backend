package com.hotel_project.member_jpa.cart.repository;

import com.hotel_project.member_jpa.cart.dto.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    // 장바구니 단건 조회 (존재 여부 확인용)
    Optional<CartEntity> findByMemberEntity_IdAndRoomEntity_Id(Long memberId, Long roomId);

    // 장바구니 ID로 조회 (권한 확인용)
    Optional<CartEntity> findById(Long id);

    // 장바구니에서 삭제
    void deleteByMemberEntity_IdAndRoomEntity_Id(Long memberId, Long roomId);

    // 장바구니 전체 삭제 (회원별)
    void deleteByMemberEntity_Id(Long memberId);
}