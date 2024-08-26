package com.wizz.fi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizz.fi.dao.mapper.OrderMapper;
import com.wizz.fi.dao.model.Order;
import com.wizz.fi.util.BeanUtil;
import com.wizz.fi.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    public List<OrderVO> list(String address) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("input_address", address);
        return list(queryWrapper).stream().map(this::toVO).collect(Collectors.toList());
    }

    public OrderVO toVO(Order order) {
        return BeanUtil.PO2VO(order, OrderVO.class);
    }
}
