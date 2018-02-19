package us.supercheng.emall.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
public class ServerRsponse<T> {
    private int status;
    private String msg;
    private T data;

    private ServerRsponse(int inStatus, String inMsg) {
        this.status = inStatus;
        this.msg = inMsg;
    }

    private ServerRsponse(int inStatus, T inData) {
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

    public static <T> ServerRsponse<T> createServerResponse(int inStatus, String inMsg) {
        return new ServerRsponse<T>(inStatus, inMsg);
    }

    public static <T> ServerRsponse<T> createServerResponse(int inStatus, T inData) {
        return new ServerRsponse<T>(inStatus, inData);
    }

    public static <T> ServerRsponse<T> createServerResponseSuccess(T inDataOrMsg) {
        return createServerResponse(ResponseCode.SUCCESS.getCode(), inDataOrMsg);
    }
}
