package com.wizz.fi.integrate;

import com.wizz.fi.api.StakeAPI;
import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.enums.OrderStatus;
import com.wizz.fi.dao.enums.OrdinalStatus;
import com.wizz.fi.dao.enums.UtxoStatus;
import com.wizz.fi.dao.mapper.OrderMapper;
import com.wizz.fi.dao.mapper.OrderOrdinalMapper;
import com.wizz.fi.dao.mapper.OrdinalMapper;
import com.wizz.fi.dao.mapper.UtxoMapper;
import com.wizz.fi.dao.model.Order;
import com.wizz.fi.dao.model.OrderOrdinal;
import com.wizz.fi.dao.model.Ordinal;
import com.wizz.fi.dao.model.Utxo;
import com.wizz.fi.dto.StakeOrdinalDTO;
import com.wizz.fi.dto.StakeTokenDTO;
import com.wizz.fi.dto.unisat.InscriptionsData;
import com.wizz.fi.exception.ApiException;
import com.wizz.fi.service.ChainMonitorService;
import com.wizz.fi.service.StakeService;
import com.wizz.fi.service.UnisatService;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test_before.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:test_after.sql")
})
public class IntegrateTest {

    private static final String ordinalTxid = "4a8601d8a10abbd5bda475d403c04b4101848bf621ddd77d8106121da9001088";
    private static final Long ordinalValue = 100000000L;
    private static final Integer ordinalVout = 0;

    private static final String gasTxid = "0e1b067e177c307b76db3aa680962eb8cdd1e8fed24a931bc7467a11c7bd312d";
    private static final Long gasValue = 100000000L;
    private static final Integer gasVout = 0;

    @Autowired
    private StakeAPI stakeAPI;
    @Autowired
    private OrderOrdinalMapper orderOrdinalMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ChainMonitorService chainMonitorService;
    @MockBean
    private UnisatService unisatService;
    @Autowired
    private OrdinalMapper ordinalMapper;
    @Autowired
    private StakeService stakeService;
    @Autowired
    private UtxoMapper utxoMapper;
    @Value("${ordinal.address}")
    private String ordinalAddress;

    @Test
    public void ordinals() {
        List<OrdinalVO> ordinalVOList = stakeAPI.ordinals(1, 10).getData().getRecords();

        OrdinalVO ordinalVO = ordinalVOList.get(0);
        assertEquals("tx_1i0", ordinalVO.getInscriptionId());

        for (OrdinalVO ordinalVO1 : ordinalVOList) {
            assertEquals(OrdinalStatus.AVAILABLE, ordinalVO1.getStatus());
        }
    }

    @Test
    public void stakeOrdinal() throws Exception {
        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("evm_address_1");

        List<String> ordinals = Collections.singletonList("tx_2i0");
        stakeOrdinalDTO.setOrdinals(ordinals);

        OrderVO orderVO = stakeAPI.stakeOrdinal(stakeOrdinalDTO).getData();

        assertEquals("btc_address_1", orderVO.getInputAddress());
        assertEquals(Chain.BTC, orderVO.getInputChain());
        assertEquals("txid_1", orderVO.getInputTxid());

        assertEquals("evm_address_1", orderVO.getOutputAddress());
        assertEquals(Chain.ETH, orderVO.getOutputChain());
        assertNull(orderVO.getOutputTxid());

        assertEquals(OrderStatus.INIT, orderVO.getStatus());
        assertNotNull(orderVO.getNumber());

        Order order = orderMapper.byNumber(orderVO.getNumber());
        List<OrderOrdinal> orderOrdinals = orderOrdinalMapper.byOrderId(order.getId());
        assertEquals(1, orderOrdinals.size());
        OrderOrdinal orderOrdinal = orderOrdinals.get(0);
        assertEquals(order.getId(), orderOrdinal.getOrderId());
        assertEquals(2L, (long) orderOrdinal.getOrdinalId());
    }

    @Test
    public void stakeToken() throws Exception {
        StakeTokenDTO stakeTokenDTO = new StakeTokenDTO();
        stakeTokenDTO.setInputAddress("evm_address_1");
        stakeTokenDTO.setTxid("txid_1");
        stakeTokenDTO.setOutputAddress("btc_address_1");

        List<String> ordinals = Collections.singletonList("tx_1i0");
        stakeTokenDTO.setOrdinals(ordinals);

        OrderVO orderVO = stakeAPI.stakeToken(stakeTokenDTO).getData();

        assertEquals("evm_address_1", orderVO.getInputAddress());
        assertEquals(Chain.ETH, orderVO.getInputChain());
        assertEquals("txid_1", orderVO.getInputTxid());

        assertEquals("btc_address_1", orderVO.getOutputAddress());
        assertEquals(Chain.BTC, orderVO.getOutputChain());
        assertNull(orderVO.getOutputTxid());

        assertEquals(OrderStatus.INIT, orderVO.getStatus());
        assertNotNull(orderVO.getNumber());

        Order order = orderMapper.byNumber(orderVO.getNumber());
        List<OrderOrdinal> orderOrdinals = orderOrdinalMapper.byOrderId(order.getId());
        assertEquals(1, orderOrdinals.size());
        OrderOrdinal orderOrdinal = orderOrdinals.get(0);
        assertEquals(order.getId(), orderOrdinal.getOrderId());
        assertEquals(1L, (long) orderOrdinal.getOrdinalId());
    }

    // wrong status
    @Test
    public void stakeFailed1() throws Exception {
        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("evm_address_1");

        List<String> ordinals = Arrays.asList("tx_1i0", "tx_2i0");
        stakeOrdinalDTO.setOrdinals(ordinals);

        assertThrows(ApiException.class, () -> {
            stakeAPI.stakeOrdinal(stakeOrdinalDTO).getData();
        });
    }

    // empty ordinals
    @Test
    public void stakeFailed2() throws Exception {
        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("evm_address_1");

        stakeOrdinalDTO.setOrdinals(Collections.emptyList());

        assertThrows(ApiException.class, () -> {
            stakeAPI.stakeOrdinal(stakeOrdinalDTO).getData();
        });
    }

    // null ordinals
    @Test
    public void stakeFailed3() throws Exception {
        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("evm_address_1");

        assertThrows(ApiException.class, () -> {
            stakeAPI.stakeOrdinal(stakeOrdinalDTO).getData();
        });
    }

    @Test
    public void getStakeOrdinals() throws Exception {
        InscriptionsData inscriptionsData = mockInscriptionsData();
        Mockito.when(unisatService.list(ordinalAddress, 0)).thenReturn(inscriptionsData);

        chainMonitorService.checkStakeOrdinal();

        Mockito.verify(unisatService, Mockito.times(1)).list(ordinalAddress, 0);

        Ordinal ordinal = ordinalMapper.selectById(2);
        assertEquals(ordinalTxid, ordinal.getUtxoTxid());
        assertEquals(ordinalVout, ordinal.getUtxoVout());
        assertEquals(ordinalValue, ordinal.getUtxoValue());
    }

    @Test
    public void confirmStakeOrdinals() throws Exception {
        // 1.create order
        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid(ordinalTxid);
        stakeOrdinalDTO.setOutputAddress("0xF8c7721a60A98B3b3F221F4874803DA8a4E81c9E");

        List<String> ordinals = Collections.singletonList("tx_2i0");
        stakeOrdinalDTO.setOrdinals(ordinals);

        OrderVO orderVO = stakeAPI.stakeOrdinal(stakeOrdinalDTO).getData();
        assertNull(orderVO.getOutputTxid());
        assertEquals(OrderStatus.INIT, orderVO.getStatus());

        // 2.stake ordinal
        InscriptionsData inscriptionsData = mockInscriptionsData();
        Mockito.when(unisatService.list(ordinalAddress, 0)).thenReturn(inscriptionsData);

        chainMonitorService.checkStakeOrdinal();

        Mockito.verify(unisatService, Mockito.times(1)).list(ordinalAddress, 0);

        Order order = orderMapper.byNumber(orderVO.getNumber());
        assertNotNull(order.getOutputTxid());
        assertEquals(OrderStatus.SUCCESS, order.getStatus());
    }

    @Test
    public void confirmStakeTokens() throws Exception {
        // 1.create order
        StakeTokenDTO stakeTokenDTO = new StakeTokenDTO();
        stakeTokenDTO.setInputAddress("0x70997970C51812dc3A010C7d01b50e0d17dc79C8");
        stakeTokenDTO.setTxid("0xcd3b2428e79a5f5245535cd7a6e22bd2261ef0924df73cb8d9b38fd7dc56b32b");
        stakeTokenDTO.setOutputAddress("bcrt1qk08mtpguwlddyyx2spkc8qd2g7umxz3ekn20ln");

        List<String> ordinals = Collections.singletonList("tx_1i0");
        stakeTokenDTO.setOrdinals(ordinals);

        OrderVO orderVO = stakeAPI.stakeToken(stakeTokenDTO).getData();
        assertNull(orderVO.getOutputTxid());
        assertEquals(OrderStatus.INIT, orderVO.getStatus());

        // 2.save utxo
        Utxo utxo = new Utxo();
        utxo.setUtxoTxid(gasTxid);
        utxo.setUtxoVout(gasVout);
        utxo.setUtxoValue(gasValue);
        utxo.setStatus(UtxoStatus.NOT_USED);
        utxoMapper.insert(utxo);

        // 3.update ordinal
        Ordinal ordinal = ordinalMapper.selectById(1);
        ordinal.setUtxoTxid(ordinalTxid);
        ordinal.setUtxoVout(ordinalVout);
        ordinal.setUtxoValue(ordinalValue);
        ordinalMapper.updateById(ordinal);

        // 4.stake token
        stakeService.confirmTokenStake("0xcd3b2428e79a5f5245535cd7a6e22bd2261ef0924df73cb8d9b38fd7dc56b32b", new BigInteger(String.valueOf(1000000)));

        Order order = orderMapper.byNumber(orderVO.getNumber());
        assertNotNull(order.getOutputTxid());
        assertEquals(OrderStatus.SUCCESS, order.getStatus());
    }

    private InscriptionsData mockInscriptionsData() {
        InscriptionsData inscriptionsData = new InscriptionsData();
        List<InscriptionsData.Inscription> list = new ArrayList<>();
        InscriptionsData.Inscription inscription = null;

        inscription = new InscriptionsData.Inscription();
        inscription.setInscriptionNumber(2);
        inscription.setInscriptionId("tx_2i0");
        inscription.setContent(String.format("https://static-testnet.unisat.io/content/%si%s", ordinalTxid, ordinalVout));
        inscription.setOutput(String.format("%s:%s", ordinalTxid, ordinalVout));
        inscription.setOutputValue(ordinalValue);
        inscription.setOffset(0);
        list.add(inscription);
        inscriptionsData.setList(list);
        inscriptionsData.setTotal(list.size());

        return inscriptionsData;
    }
}
