package com.wizz.fi.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    INIT("INIT"),
    SUCCESS("SUCCESS");

    @EnumValue
    private final String value;
}
