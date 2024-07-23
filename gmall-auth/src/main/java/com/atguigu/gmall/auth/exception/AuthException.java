package com.atguigu.gmall.auth.exception;

/**
 * @author huXiuYuan
 * @Description：授权自定义异常
 * @date 2024/7/23 21:37
 */
public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 4725981045148802994L;

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
