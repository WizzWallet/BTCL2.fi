package com.wizz.fi.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.model.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper extends BaseMapper<Order> {
    @Select("select * from orders where number = #{number}")
    Order byNumber(@Param("number") String number);

    @Select("select * from orders where input_chain = #{inputChain} and input_txid = #{inputTxid}")
    Order byInput(@Param("inputChain") Chain inputChain, @Param("inputTxid") String inputTxid);
}
