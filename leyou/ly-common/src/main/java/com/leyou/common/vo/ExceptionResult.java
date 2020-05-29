package com.leyou.common.vo;

import com.leyou.common.exception.LyException;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;



@Getter
@Setter
public class ExceptionResult {
    private int status;
    private String message;
    private String timestamp;

    public ExceptionResult(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = DateTime.now().toString("YYYY-MM-dd HH:mm:ss");
    }
    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        this.timestamp = DateTime.now().toString("YYYY-MM-dd HH:mm:ss");
    }
}
