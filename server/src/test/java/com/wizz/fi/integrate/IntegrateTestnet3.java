package com.wizz.fi.integrate;

import com.wizz.fi.service.StakeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigInteger;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test3")
public class IntegrateTestnet3 {
    @Autowired
    private StakeService stakeService;


    @Test
    public void confirmStakeOrdinals() throws Exception {
    }

    @Test
    public void confirmStakeTokens() throws Exception {
        // 4.stake token
        stakeService.confirmTokenStake("0x154db2ae5c2dd6a30e54ceb316d5400fb5e9cd2a86be5e673a7ce4e5fdcfe719", new BigInteger(String.valueOf(1000000)));
    }
}
