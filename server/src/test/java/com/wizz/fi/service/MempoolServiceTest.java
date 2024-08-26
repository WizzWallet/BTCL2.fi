package com.wizz.fi.service;

import com.wizz.fi.dto.mempool.Fee;
import com.wizz.fi.dto.mempool.Utxo;
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
public class MempoolServiceTest {
    @Autowired
    private MempoolService mempoolService;

    @Test
    public void getAddressUtxos() {
        List<Utxo> utxoList = mempoolService.getAddressUtxos("tb1plvllnvrt0h24ed93ara3rdwu7lcnlcrffzkvzgcw86e0e0yljumqsvhrjr");
        for (Utxo utxo : utxoList) {
            System.out.println(utxo);
        }
    }

    @Test
    public void getRecommendedFee() {
        Fee fee = mempoolService.getRecommendedFee();
        System.out.println(fee);
    }

    @Test
    public void broadcast() {
        String resp = mempoolService.broadcast("02000000000102c6f7faba28245b55e536d935e4032e2ca76e28b0c59b2340d8ab362066b556610000000000fdffffffc44a1a463ae72566e3a3a357733ce8df24d5102b7e45d2bcc6bdf701f5fd35840100000000fdffffff02220200000000000022512011b6ce99eab0d8873d787e99e68a351358228893cdf1049ac48aae51391598abed30020000000000225120fb3ff9b06b7dd55cb4b1e8fb11b5dcf7f13fe06948acc1230e3eb2fcbc9f97360247304402201921f49f8afcd20e1726c2bb6350c3927de3debd0b31db23546528d0b93cb4170220666cb935f1b55fcee19df841ab71bf486811083fc2fc5b31dfc5497dbd8f72f20121036011a8363f914148b87977ea0a31dff271bfc4f987f1156d9583fffefc660c7802483045022100a799da47ff16f1fc43fe14c0ac292c7d8e2df02a2bb18fc3be80725caa07787902205c4effe39c5574772f382a5ddb9d11ea9148e0a44b30e904a027d629ccb185660121036011a8363f914148b87977ea0a31dff271bfc4f987f1156d9583fffefc660c7800000000");
        System.out.println(resp);
    }
}
