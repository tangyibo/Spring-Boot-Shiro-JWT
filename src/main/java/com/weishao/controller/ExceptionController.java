package com.weishao.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weishao.bean.ResponseBean;
import com.weishao.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.ShiroException;
import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @RequestMapping(path = "/401")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseBean unauthorized() {
    	logger.info("401 page");
        return new ResponseBean(401, "Unauthorized", HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }
	
    // 捕捉shiro的异常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public ResponseBean handle401(ShiroException e) {
    	logger.info("UNAUTHORIZED401 error:",e);
        return new ResponseBean(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseBean handle401(UnauthorizedException e) {
    	logger.info("UNAUTHORIZED error:",e);
        return new ResponseBean(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBean globalException(HttpServletRequest request, Throwable ex) {
    	logger.error("error:",ex);
        return new ResponseBean(getStatus(request).value(), ex.getMessage(), HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}

