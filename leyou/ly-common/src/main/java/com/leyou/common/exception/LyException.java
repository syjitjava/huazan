package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public class LyException extends RuntimeException {

    private int status;

    public LyException(int status,String message) {
        super(message);
        this.status = status;
    }

    public LyException(String message, Throwable cause,int status) {
        super(message, cause);
        this.status = status;
    }

    public LyException(ExceptionEnum e) {
        super(e.getMessage());
        this.status = e.getStatus();
    }
}
