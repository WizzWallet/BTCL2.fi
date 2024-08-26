package com.wizz.fi.api;

import com.wizz.fi.vo.OrdinalVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test3")
public class StakeAPITest {
    @Autowired
    private StakeAPI stakeAPI;

    @Test
    public void ordinals() {
        List<OrdinalVO> ordinalVOList = stakeAPI.ordinals(1, 10).getData().getRecords();
        for (OrdinalVO ordinalVO : ordinalVOList) {
            System.out.println(ordinalVO);
        }
    }
}
