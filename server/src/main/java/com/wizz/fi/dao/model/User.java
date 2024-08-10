package com.wizz.fi.dao.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wizz.fi.dao.enums.Chain;
import lombok.Data;

@Data
@TableName("users")
public class User extends BaseModel {
    @TableField(value = "address")
    private String address;

    @TableField(value = "chain")
    private Chain chain;
}
