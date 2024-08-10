package com.wizz.fi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.enums.OrderStatus;
import com.wizz.fi.dao.enums.OrdinalStatus;
import com.wizz.fi.dao.mapper.OrderMapper;
import com.wizz.fi.dao.mapper.OrdinalMapper;
import com.wizz.fi.dao.model.Order;
import com.wizz.fi.dao.model.Ordinal;
import com.wizz.fi.dao.model.User;
import com.wizz.fi.util.BeanUtil;
import com.wizz.fi.util.ChallengeUtils;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class StakeService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrdinalMapper ordinalMapper;

    public OrderVO stakeOrdinal(String userAddress, String inputAddress, String inputTxid, String outputAddress) throws Exception {
        return createOrder(userAddress, inputAddress, Chain.BTC, inputTxid, outputAddress, Chain.ETH);
    }

    public OrderVO stakeToken(String userAddress, String inputAddress, String inputTxid, String outputAddress) throws Exception {
        return createOrder(userAddress, inputAddress, Chain.ETH, inputTxid, outputAddress, Chain.BTC);
    }

    public OrderVO createOrder(String userAddress, String inputAddress, Chain inputChain, String inputTxid, String outputAddress, Chain outputChain) throws Exception {
        // 1. create user
        User user = userService.getUser(userAddress);

        String orderNumber = ChallengeUtils.challenge();

        // 7. create job
        Order order = new Order();
        order.setUserId(user.getId())
                .setNumber(orderNumber)
                .setInputAddress(inputAddress)
                .setInputChain(inputChain)
                .setInputTxid(inputTxid)
                .setOutputAddress(outputAddress)
                .setOutputChain(outputChain)
                .setStatus(OrderStatus.INIT);

        orderMapper.insert(order);

        return toVO(order);
    }

    public Page<OrdinalVO> listOrdinals(String inscriptionNumber, Integer pageNum, Integer pageSize) {
        Page<Ordinal> ordinalPage = new Page<>();
        ordinalPage.setSize(pageSize);
        ordinalPage.setCurrent(pageNum);

        LambdaQueryWrapper<Ordinal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ordinal::getStatus, OrdinalStatus.AVAILABLE);
        queryWrapper.orderByDesc(Ordinal::getInscriptionNumber);

        IPage<Ordinal> iPage = ordinalMapper.selectPage(ordinalPage, queryWrapper);

        List<OrdinalVO> ordinalVOS = iPage.getRecords().stream().map(this::toVO).collect(Collectors.toList());

        return BeanUtil.iPage2Page(iPage, ordinalVOS);
    }

    public OrderVO toVO(Order order) {
        return BeanUtil.PO2VO(order, OrderVO.class);
    }

    public OrdinalVO toVO(Ordinal ordinal) {
        return BeanUtil.PO2VO(ordinal, OrdinalVO.class);
    }
}
