package com.wizz.fi.dao.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wizz.fi.dao.enums.OrderOrdinalStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("orders_ordinals")
@Accessors(chain = true)
public class OrderOrdinal extends BaseModel {
    @TableField("order_id")
    private Long orderId;

    @TableField("ordinal_id")
    private Long ordinalId;

    @TableField("status")
    private OrderOrdinalStatus status;
}
