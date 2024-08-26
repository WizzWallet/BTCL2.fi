package com.wizz.fi.service;

import com.wizz.fi.dao.pojo.Output;
import com.wizz.fi.dao.pojo.Prevout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test3")
public class BTCServiceTest {
    @Autowired
    private BTCService btcService;

    @Test
    public void transfer() throws Exception {
        List<Prevout> prevoutss = new ArrayList<>();
        Prevout prevout = null;

        prevout = new Prevout();
        prevout.setTxid("6156b5662036abd840239bc5b0286ea72c2e03e435d936e5555b2428bafaf7c6");
        prevout.setValue(546L);
        prevout.setVout(0);
        prevout.setOrdinal(true);
        prevoutss.add(prevout);

        prevout = new Prevout();
        prevout.setTxid("11164b7a1bd9cdbee26ed950a3dee91a75fb553bebfec82ffd24f164ea352579");
        prevout.setValue(139746L);
        prevout.setVout(1);
        prevout.setOrdinal(false);
        prevoutss.add(prevout);


//        prevout = new Prevout();
//        prevout.setTxid("4203c80481d4b1fdc75d593bffc112eaf2afeccb66bccff88cfe49bd39f3de7c");
//        prevout.setAmount(5000000000L);
//        prevout.setVout(0);
//        prevoutss.add(prevout);

        List<Output> outputs = new ArrayList<>();

        Output output = null;

        output = new Output();
        output.setAddress("bcrt1qk08mtpguwlddyyx2spkc8qd2g7umxz3ekn20ln");
        output.setValue(100000000L);
        outputs.add(output);

        output = new Output();
        output.setAddress("bcrt1qfgayfkpaxsy9ky9jfw8ht3qqt98xgj7zfahymz");
        output.setValue(4899999859L);
        outputs.add(output);

        CompletableFuture<BTCService.Result> result = btcService.transfer(prevoutss, outputs);
        result.get();
    }
}
