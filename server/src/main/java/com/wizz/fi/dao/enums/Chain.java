package com.wizz.fi.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Chain {
    BTC("BTC"),
    ETH("ETH"),
    ;

    @EnumValue
    private final String value;
}
