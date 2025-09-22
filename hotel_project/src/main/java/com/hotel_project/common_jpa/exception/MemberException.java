package com.hotel_project.common_jpa.exception;

public enum MemberException {

    // 공용 에러 메시지들
    INVALID_ID(400001, "유효하지 않은 ID입니다."),  //받은 id가 null일때
    NOT_EXIST_DATA(404001, "해당 데이터가 존재하지 않습니다."), //검색은 됐지만 데이터가 null
    INVALID_DATA(400002, "유효하지 않은 데이터입니다."),  //받은 데이터가 null
    DUPLICATE_DATA(409001, "이미 존재하는 데이터입니다."); //ex)회원가입시 이미 있는 아이디

    private CommonExceptionTemplate t;

    MemberException(int code, String message) {
        this.t=new CommonExceptionTemplate(code,message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }

}