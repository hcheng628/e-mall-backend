package us.supercheng.emall.common;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
    private int status;
    private T data;

    private ServerResponse(int inStatus, T inData) {
        this.status = inStatus;
        this.data = inData;
    }

    public int getStatus() {
        return this.status;
    }

    public T getData() {
        return this.data;
    }

    public static <T> ServerResponse<T> createServerResponse(int inStatus, String inData) {
        return new ServerResponse(inStatus, inData);
    }

    public static <T> ServerResponse<T> createServerResponse(int inStatus, T inData) {
        return new ServerResponse(inStatus, inData);
    }

    public static <T> ServerResponse<T> createServerResponseSuccess(T inDataOrMsg) {
        return createServerResponse(ResponseCode.SUCCESS.getCode(), inDataOrMsg);
    }

    public static <T> ServerResponse<T> createServerResponseError(String inData) {
        return ServerResponse.createServerResponse(ResponseCode.ERROR.getCode(), inData);
    }
}