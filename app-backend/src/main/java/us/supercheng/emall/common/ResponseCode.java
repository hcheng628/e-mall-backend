package us.supercheng.emall.common;

public enum ResponseCode {

    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    LOGIN_REQUIRED(10,"LOGIN_REQUIRED"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int inCode, String inDesc) {
        this.code = inCode;
        this.desc = inDesc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}