package com.wizz.fi.integrate;

import com.wizz.fi.api.StakeAPI;
import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.enums.OrderStatus;
import com.wizz.fi.dao.enums.OrdinalStatus;
import com.wizz.fi.dao.pojo.LoginUser;
import com.wizz.fi.dto.StakeOrdinalDTO;
import com.wizz.fi.vo.OrderVO;
import com.wizz.fi.vo.OrdinalVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Autowired
    private StakeAPI stakeAPI;

    @Test
    public void ordinals() {
        List<OrdinalVO> ordinalVOList = stakeAPI.ordinals(null, 1, 10).getData().getRecords();

        OrdinalVO ordinalVO = ordinalVOList.get(0);
        assertEquals("tx_1i0", ordinalVO.getInscriptionId());

        for (OrdinalVO ordinalVO1 : ordinalVOList) {
            assertEquals(OrdinalStatus.AVAILABLE, ordinalVO1.getStatus());
        }
    }

    @Test
    public void stakeOrdinal() throws Exception {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserAddress("address_1");
        loginUser.setChain(Chain.BTC);

        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("btc_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("evm_address_1");
        OrderVO orderVO = stakeAPI.stakeOrdinal(stakeOrdinalDTO, loginUser).getData();

        assertEquals("btc_address_1", orderVO.getInputAddress());
        assertEquals(Chain.BTC, orderVO.getInputChain());
        assertEquals("txid_1", orderVO.getInputTxid());

        assertEquals("evm_address_1", orderVO.getOutputAddress());
        assertEquals(Chain.ETH, orderVO.getOutputChain());
        assertNull(orderVO.getOutputTxid());

        assertEquals(OrderStatus.INIT, orderVO.getStatus());
        assertNotNull(orderVO.getNumber());
    }

    @Test
    public void stakeToken() throws Exception {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserAddress("address_1");
        loginUser.setChain(Chain.ETH);

        StakeOrdinalDTO stakeOrdinalDTO = new StakeOrdinalDTO();
        stakeOrdinalDTO.setInputAddress("evm_address_1");
        stakeOrdinalDTO.setTxid("txid_1");
        stakeOrdinalDTO.setOutputAddress("btc_address_1");
        OrderVO orderVO = stakeAPI.stakeOrdinal(stakeOrdinalDTO, loginUser).getData();

        assertEquals("evm_address_1", orderVO.getInputAddress());
        assertEquals(Chain.BTC, orderVO.getInputChain());
        assertEquals("txid_1", orderVO.getInputTxid());

        assertEquals("btc_address_1", orderVO.getOutputAddress());
        assertEquals(Chain.ETH, orderVO.getOutputChain());
        assertNull(orderVO.getOutputTxid());

        assertEquals(OrderStatus.INIT, orderVO.getStatus());
        assertNotNull(orderVO.getNumber());
    }
}
