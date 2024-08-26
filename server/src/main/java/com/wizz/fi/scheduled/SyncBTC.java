package com.wizz.fi.scheduled;

import com.wizz.fi.dao.enums.UtxoStatus;
import com.wizz.fi.dao.mapper.OrdinalMapper;
import com.wizz.fi.dao.mapper.UtxoMapper;
import com.wizz.fi.dto.mempool.Utxo;
import com.wizz.fi.service.ChainMonitorService;
import com.wizz.fi.service.MempoolService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableAsync
@EnableScheduling
@EnableSchedulerLock(defaultLockAtLeastFor = "PT5S", defaultLockAtMostFor = "PT70S")
@Slf4j
public class SyncBTC {
    @Autowired
    private ChainMonitorService chainMonitorService;

    @Autowired
    private MempoolService mempoolService;

    @Autowired
    private UtxoMapper utxoMapper;

    @Value("${wallet.change_address}")
    private String changeAddress;

    @Autowired
    private OrdinalMapper ordinalMapper;

    @Scheduled(cron = "0 */2 * * * ?")
    @SchedulerLock(name = "syncBTC")
    public void syncBTC() {
        utxoMapper.truncate();
        // 同步UTXO
        List<Utxo> utxos = mempoolService.getAddressUtxos(changeAddress);
        for (Utxo utxo : utxos) {
            try {
                com.wizz.fi.dao.model.Utxo _utxo = new com.wizz.fi.dao.model.Utxo();
                _utxo.setUtxoTxid(utxo.getTxid());
                _utxo.setUtxoVout(utxo.getVout());
                _utxo.setUtxoValue(utxo.getValue());
                _utxo.setStatus(UtxoStatus.NOT_USED);
                utxoMapper.insert(_utxo);
            } catch (Exception e) {

            }
        }

        // 同步Ordinal
        chainMonitorService.checkStakeOrdinal();
    }
}
