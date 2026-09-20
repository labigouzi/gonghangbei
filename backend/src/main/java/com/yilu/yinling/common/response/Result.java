package com.yilu.yinling.common.response;
import java.time.Instant;
public record Result<T>(int code, String message, T data, String traceId) {
    public static <T> Result<T> ok(T data) { return new Result<>(0, "success", data, Instant.now().toString()); }
    public static <T> Result<T> fail(int code, String message) { return new Result<>(code, message, null, Instant.now().toString()); }
}
