package com.wizz.fi.scheduled;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test3")
public class SyncBTCTest {
    @Autowired
    private SyncBTC syncBTC;

    @Test
    public void syncBTC() {
        syncBTC.syncBTC();
    }
}
