package com.wizz.fi.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderOrdinalStatus {
    INIT("INIT"),
    SEND("SEND"),
    RECEIVED("RECEIVED");

    @EnumValue
    private final String value;
}
