package com.Ma.entity;

import lombok.Data;

@Data
public class RestBean<T> {
    private int status;

    private boolean success;

    private T message;

    //因为@DATA并不会提供含有字段的全参构造方法，所以带有参数的构造方法需要我们自己生成
    private RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> RestBean<T> success() {
        return new RestBean<T>(200, true, null);
    }

    public static <T> RestBean<T> success(T data) {
        return new RestBean<T>(200, true, data);
    }

    public static <T> RestBean<T> failure(int status) {
        return new RestBean<T>(status, false, null);
    }

    public static <T> RestBean<T> failure(int status, T data) {
        return new RestBean<T>(status, false, data);
    }

}
