package com.wizz.fi.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UtxoStatus {
    NOT_USED(0),
    USED(1);

    @EnumValue
    private final Integer value;
}
