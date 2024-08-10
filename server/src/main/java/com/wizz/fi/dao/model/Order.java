package com.wizz.fi.dao.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("orders")
@Accessors(chain = true)
public class Order extends BaseModel {

    @TableField("user_id")
    private Long userId;

    @TableField(value = "number")
    private String number;

    @TableField(value = "input_address")
    private String inputAddress;

    @TableField(value = "input_chain")
    private Chain inputChain;

    @TableField(value = "input_txid")
    private String inputTxid;

    @TableField(value = "output_address")
    private String outputAddress;

    @TableField(value = "output_chain")
    private Chain outputChain;

    @TableField(value = "output_txid")
    private String outputTxid;

    @TableField(value = "status")
    private OrderStatus status;
}
