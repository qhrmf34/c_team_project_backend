package com.hotel_project.payment_jpa.refund_reason.dto;

public enum RefundReasonType {
    CHANGE_OF_PLANS("일정 변경"),
    FOUND_BETTER_OPTION("더 나은 옵션 발견"),
    ROOM_ISSUE("객실 문제"),
    SERVICE_ISSUE("서비스 불만"),
    PRICE_ISSUE("가격 문제"),
    PERSONAL_EMERGENCY("개인 사정"),
    OTHER("기타");

    private final String description;

    RefundReasonType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}