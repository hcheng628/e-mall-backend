package us.supercheng.emall.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int inStatus, String inMsg) {
        this.status = inStatus;
        this.msg = inMsg;
    }

    private ServerResponse(int inStatus, T inData) {
        this.status = inStatus;
        this.data = inData;
    }

    public int getStatus() {
        return this.status;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public static <T> ServerResponse<T> createServerResponse(int inStatus, String inMsg) {
        return new ServerResponse<T>(inStatus, inMsg);
    }

    public static <T> ServerResponse<T> createServerResponse(int inStatus, T inData) {
        return new ServerResponse<T>(inStatus, inData);
    }

    public static <T> ServerResponse<T> createServerResponseSuccess(T inDataOrMsg) {
        return createServerResponse(ResponseCode.SUCCESS.getCode(), inDataOrMsg);
    }

    public static <T> ServerResponse<T> createServerResponseError(String inMsg) {
        return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), inMsg);
    }
}