package com.wizz.fi.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wizz.fi.dao.model.OrderOrdinal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderOrdinalMapper extends BaseMapper<OrderOrdinal> {
    @Select("select * from orders_ordinals where order_id = #{orderId}")
    List<OrderOrdinal> byOrderId(@Param("orderId") Long orderId);

    @Select("select * from orders_ordinals where order_id = #{orderId} and ordinal_id = #{ordinalId}")
    OrderOrdinal byOrderIdOrdinalId(@Param("orderId") Long orderId, @Param("ordinalId") Long ordinalId);
}
