package com.hotel_project.common_jpa.exception;

public enum MemberException {

    NOT_EXIST_MEMBERID(400400,"그런 아이디 없음"),
    NOT_EXIST_MEMBERID2(400400,"그런 아이디 없음"),
    NOT_EXIST_MEMBERID3(400400,"그런 아이디 없음"),
    NOT_EXIST_MEMBERID4(400400,"그런 아이디 없음"),
    NOT_EXIST_MEMBERID5(400400,"그런 아이디 없음"),
    NOT_EXIST_MEMBERID6(400400,"그런 아이디 없음");

    private CommonExceptionTemplate t;

    MemberException(int code, String message) {
        this.t=new CommonExceptionTemplate(code,message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }

}
