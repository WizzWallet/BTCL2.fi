package com.wizz.fi.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test3")
public class ETHServiceTest {
    @Autowired
    private ETHService ethService;

    @Test
    public void transfer() throws Exception {
        CompletableFuture<String> result = ethService.transfer("0xF8c7721a60A98B3b3F221F4874803DA8a4E81c9E", "8000000");
        result.get();
    }
}
