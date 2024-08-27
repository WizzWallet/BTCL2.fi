package com.wizz.fi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wizz.fi.dao.enums.*;
import com.wizz.fi.dao.mapper.OrderMapper;
import com.wizz.fi.dao.mapper.OrderOrdinalMapper;
import com.wizz.fi.dao.mapper.OrdinalMapper;
import com.wizz.fi.dao.mapper.UtxoMapper;
import com.wizz.fi.dao.model.Order;
import com.wizz.fi.dao.model.OrderOrdinal;
import com.wizz.fi.dao.model.Ordinal;
import com.wizz.fi.dao.model.Utxo;
import com.wizz.fi.dao.pojo.Output;
import com.wizz.fi.dao.pojo.Prevout;
import com.wizz.fi.dto.unisat.InscriptionsData;
import com.wizz.fi.util.*;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class StakeService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrdinalMapper ordinalMapper;

    @Autowired
    private OrderOrdinalMapper orderOrdinalMapper;

    @Autowired
    private ETHService ethService;

    @Autowired
    private BTCService btcService;

    @Autowired
    private UtxoMapper utxoMapper;

    @Autowired
    private RedisLock redisLock;

    @Value("${wallet.change_address}")
    private String changeAddress;

    @Autowired
    private UnisatService unisatService;

    @Autowired
    private MempoolService mempoolService;

    public OrderVO stakeOrdinal(String inputAddress, String inputTxid, String outputAddress, List<String> ordinals) throws Exception {
        return createOrder(inputAddress, Chain.BTC, inputTxid, outputAddress, Chain.ETH, ordinals);
    }

    public OrderVO stakeToken(String inputAddress, String inputTxid, String outputAddress, List<String> ordinals) throws Exception {
        return createOrder(inputAddress, Chain.ETH, inputTxid, outputAddress, Chain.BTC, ordinals);
    }

    public void confirmOrdinalStake(String inscriptionId, String output, Long value) {
        Ordinal ordinal = ordinalMapper.byInscriptionId(inscriptionId);

        // 1. check order
        Order order = orderMapper.byInput(Chain.BTC, ordinal.getUtxoTxid());
        if (order == null || order.getStatus() != OrderStatus.INIT) {
            // 没有匹配进来的订单，或订单状态不对，直接忽略掉
            return;
        }

        // 2. check order ordinal
        OrderOrdinal orderOrdinal = orderOrdinalMapper.byOrderIdOrdinalId(order.getId(), ordinal.getId());
        if (orderOrdinal == null) {
            // 和找到的订单不匹配，忽略掉。多转了Ordinal
            return;
        }

        if (orderOrdinal.getStatus() == OrderOrdinalStatus.INIT) {
            orderOrdinal.setStatus(OrderOrdinalStatus.RECEIVED);
            orderOrdinalMapper.updateById(orderOrdinal);
        }

        // 3. check all order ordinal receive status
        List<OrderOrdinal> orderOrdinals = orderOrdinalMapper.byOrderId(order.getId());

        boolean receiveAll = !orderOrdinals.stream().anyMatch(orderOrdinal1 -> orderOrdinal1.getStatus() != OrderOrdinalStatus.RECEIVED);

        if (receiveAll) {
            // 4. update order status and send token
            order.setStatus(OrderStatus.SUCCESS);
            orderMapper.updateById(order);

            try {
                // 5. send token
                System.out.println("send token");
                String outputTxid = ethService.transfer(order.getOutputAddress(), String.valueOf(1000000 * orderOrdinals.size())).get();
                order.setOutputTxid(outputTxid);
                orderMapper.updateById(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void confirmTokenStake(String txid, BigInteger amount) {
        Order order = orderMapper.byInput(Chain.ETH, txid);

        MyAssert.notNull(order, ResultCode.ORDER_NOT_FOUND);
        MyAssert.isTrue(order.getStatus() == OrderStatus.INIT, ResultCode.ORDER_STATUS_INCORRECT);

        List<OrderOrdinal> orderOrdinals = orderOrdinalMapper.byOrderId(order.getId());

        long requiredAmount = 1000000L * orderOrdinals.size();
        MyAssert.isTrue(requiredAmount <= amount.longValue(), ResultCode.STAKE_TOKEN_AMOUNT_NOT_ENOUGH);

        order.setStatus(OrderStatus.SUCCESS);
        orderMapper.updateById(order);

        orderOrdinals.forEach(orderOrdinal -> {
            orderOrdinal.setStatus(OrderOrdinalStatus.SEND);
            orderOrdinalMapper.updateById(orderOrdinal);

            Ordinal ordinal = ordinalMapper.selectById(orderOrdinal.getOrdinalId());
            ordinal.setStatus(OrdinalStatus.UNAVAILABLE);
            ordinalMapper.updateById(ordinal);
        });

        List<Prevout> prevouts = orderOrdinals.stream().map(orderOrdinal -> {
            Ordinal ordinal = ordinalMapper.selectById(orderOrdinal.getOrdinalId());

            Prevout prevout = new Prevout();
            prevout.setTxid(ordinal.getUtxoTxid());
            prevout.setVout(ordinal.getUtxoVout());
            prevout.setValue(ordinal.getUtxoValue());
            prevout.setOrdinal(true);

            return prevout;
        }).collect(Collectors.toList());

        List<Output> outputs = orderOrdinals.stream().map(orderOrdinal -> {
            Ordinal ordinal = ordinalMapper.selectById(orderOrdinal.getOrdinalId());

            Output output = new Output();
            output.setValue(ordinal.getUtxoValue());
            output.setAddress(order.getOutputAddress());

            return output;
        }).collect(Collectors.toList());

        // 假定只需要使用一个utxo作为gas，也只有一个找零
        int fee = (1 + prevouts.size()) * 68 + (1 + outputs.size()) * 31 + 11;
        int rate = mempoolService.getRecommendedFee().getHourFee();
        if (rate < 5) {
            rate = 5;
        }
        int gas = fee * rate;

        String lockKey = "utxo_payment";
        String uniqueValue = String.valueOf(System.currentTimeMillis());

        if (redisLock.tryLock(lockKey, uniqueValue, 30000)) { // 锁定30秒
            try {
                // lock utxo
                Utxo utxo = utxoMapper.getBiggest();
                utxo.setStatus(UtxoStatus.USED);
                utxoMapper.updateById(utxo);

                // add gas
                Prevout prevout = new Prevout();
                prevout.setTxid(utxo.getUtxoTxid());
                prevout.setVout(utxo.getUtxoVout());
                prevout.setValue(utxo.getUtxoValue());
                prevout.setOrdinal(false);
                prevouts.add(prevout);

                // add change
                Output output = new Output();
                output.setValue(utxo.getUtxoValue() - gas);
                output.setAddress(changeAddress);
                outputs.add(output);

                BTCService.Result result = btcService.transfer(prevouts, outputs).get();
                order.setOutputTxid(result.getTxid());
                order.setBroadcastResult(result.getResp());
                orderMapper.updateById(order);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisLock.unlock(lockKey, uniqueValue);
            }
        } else {
            System.out.println("Failed to acquire lock");
        }
    }

    public OrderVO createOrder(String inputAddress, Chain inputChain, String inputTxid, String outputAddress, Chain outputChain, List<String> ordinals) throws Exception {
        MyAssert.isTrue(ordinals != null, ResultCode.PARAMETER_ERROR);
        MyAssert.isTrue(!ordinals.isEmpty(), ResultCode.PARAMETER_ERROR);

        String orderNumber = ChallengeUtils.challenge();

        // 7. create job
        Order order = new Order();
        order.setNumber(orderNumber)
                .setInputAddress(inputAddress)
                .setInputChain(inputChain)
                .setInputTxid(inputTxid)
                .setOutputAddress(outputAddress)
                .setOutputChain(outputChain)
                .setStatus(OrderStatus.INIT);

        orderMapper.insert(order);

        QueryWrapper<Ordinal> queryWrapper = new QueryWrapper<>();
        if (Chain.BTC == inputChain) {
            queryWrapper.eq("status", OrdinalStatus.UNAVAILABLE);
        } else {
            queryWrapper.eq("status", OrdinalStatus.AVAILABLE);
        }

        queryWrapper.in("inscription_id", ordinals);
        List<Ordinal> ordinalList = ordinalMapper.selectList(queryWrapper);

        MyAssert.isTrue(ordinals.size() == ordinalList.size(), ResultCode.PARAMETER_ERROR);
        for (Ordinal ordinal : ordinalList) {
            OrderOrdinal orderOrdinal = new OrderOrdinal();
            orderOrdinal.setOrderId(order.getId());
            orderOrdinal.setOrdinalId(ordinal.getId());
            orderOrdinal.setStatus(OrderOrdinalStatus.INIT);

            orderOrdinalMapper.insert(orderOrdinal);
        }

        return toVO(order);
    }

    public Page<OrdinalVO> listOrdinals(Integer pageNum, Integer pageSize) {
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

    public List<OrdinalVO> listUserOrdinals(String userAddress) {
        List<OrdinalVO> ordinalVOS = new ArrayList<>();
        int cursor = 0;
        int total = 0;
        do {
            InscriptionsData inscriptionsData = unisatService.list(userAddress, cursor);
            if (inscriptionsData == null) {
                break;
            }
            total = inscriptionsData.getTotal();
            for (InscriptionsData.Inscription inscription : inscriptionsData.getList()) {
                Ordinal ordinal = ordinalMapper.byInscriptionId(inscription.getInscriptionId());
                // 1. 检查是不是在 collection 里面
                if (ordinal != null) {
                    if (ordinal.getStatus() == OrdinalStatus.UNAVAILABLE) {
                        String[] vs = inscription.getOutput().split(":");
                        String txid = vs[0];
                        Integer vout = Integer.parseInt(vs[1]);

                        // update ordinal status
                        ordinal.setUtxoTxid(txid);
                        ordinal.setUtxoVout(vout);
                        ordinal.setInscriptionNumber(inscription.getInscriptionNumber());
                        ordinal.setInscriptionId(inscription.getInscriptionId());
                        ordinal.setUtxoValue(inscription.getOutputValue());
                        ordinalVOS.add(toVO(ordinal));
                    }
                }
            }
            cursor += inscriptionsData.getList().size();
        } while (cursor < total);

        return ordinalVOS;
    }

    public OrderVO toVO(Order order) {
        return BeanUtil.PO2VO(order, OrderVO.class);
    }

    public OrdinalVO toVO(Ordinal ordinal) {
        return BeanUtil.PO2VO(ordinal, OrdinalVO.class);
    }
}
