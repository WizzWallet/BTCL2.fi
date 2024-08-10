package com.wizz.fi.dto.unisat;

import lombok.Data;

@Data
public class UnisatResponse<T> {
    private Integer code;
    private String msg;
    private T data;
}
