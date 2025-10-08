package com.hotel_project.review_jpa.report.dto;

public enum ReportType {
    Spam,           // 스팸/광고
    Inappropriate,  // 부적절한 내용 - 추가 필요
    Fake,           // 허위 정보 - 추가 필요
    Abusive,        // 욕설/비방 - 추가 필요
    Other,          // 기타 - 추가 필요
    HateSpeech,     // 혐오 발언 (기존)
    Violence        // 폭력성 (기존)
}
