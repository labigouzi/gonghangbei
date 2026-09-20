package com.yilu.yinling.common.exception;
import com.yilu.yinling.common.response.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> validation(MethodArgumentNotValidException e) { return Result.fail(400, e.getBindingResult().getFieldError() == null ? "参数校验失败" : e.getBindingResult().getFieldError().getDefaultMessage()); }
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> business(BusinessException e) { return Result.fail(400, e.getMessage()); }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> system(Exception e) {
        log.error("Unhandled request exception class={}", e.getClass().getName());
        log.error("Unhandled request exception message={}", e.getMessage());
        log.error("Unhandled request exception stackTrace", e);
        return Result.fail(500, "系统异常");
    }
}
