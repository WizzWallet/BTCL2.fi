package com.wizz.fi.service;

import com.wizz.fi.dao.enums.OrdinalStatus;
import com.wizz.fi.dao.mapper.OrdinalMapper;
import com.wizz.fi.dao.mapper.UtxoMapper;
import com.wizz.fi.dao.model.Ordinal;
import com.wizz.fi.dto.unisat.InscriptionsData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.web3j.contracts.eip20.generated.ERC20.TRANSFER_EVENT;

@Service
@Slf4j
public class ChainMonitorService {
    @Autowired
    protected UtxoMapper utxoMapper;

    @Value("${token.contractAddress}")
    private String contractAddress;
    @Value("${token.rpc}")
    private String rpc;
    @Value("${token.address}")
    private String address;
    @Autowired
    private StakeService stakeService;
    @Autowired
    private UnisatService unisatService;
    @Autowired
    private OrdinalMapper ordinalMapper;

    @Value("${ordinal.address}")
    private String ordinalAddress;

    @PostConstruct
    private void checkStakeToken() {
        Event TRANSFER_EVENT = new Event("Transfer",
                Arrays.asList(
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Address>(true) {
                        },
                        new TypeReference<Uint256>() {
                        }));

        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.LATEST,
                DefaultBlockParameterName.LATEST,
                contractAddress
        );

        String encodedEventSignature = EventEncoder.encode(TRANSFER_EVENT);
        filter.addSingleTopic(encodedEventSignature);

        HttpService httpService = new HttpService(rpc);
        Web3j web3j = Web3j.build(httpService);

        web3j.ethLogFlowable(filter).subscribe(this::processLog, this::handleError);
    }

    private void processLog(Log log) {
        List<Type> results = FunctionReturnDecoder.decode(
                log.getData(),
                TRANSFER_EVENT.getNonIndexedParameters()
        );

        String from = log.getTopics().get(1);
        String to = log.getTopics().get(2);
        BigInteger value = (BigInteger) results.get(0).getValue();
        String txid = log.getTransactionHash();

        System.out.println("Transfer event: ");
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        System.out.println("Value: " + value);
        System.out.println("Txid: " + txid);

        if (address.equalsIgnoreCase(to)) {
            try {
                stakeService.confirmTokenStake(txid, value);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void handleError(Throwable error) {
        System.err.println("Error in event processing: " + error.getMessage());
        // 这里可以添加错误处理逻辑，比如重试、报警等
    }

    public void checkStakeOrdinal() {
        int cursor = 0;
        int total = 0;
        do {
            InscriptionsData inscriptionsData = unisatService.list(ordinalAddress, cursor);
            if (inscriptionsData == null) {
                break;
            }
            total = inscriptionsData.getTotal();
            for (InscriptionsData.Inscription inscription : inscriptionsData.getList()) {
                Ordinal ordinal = ordinalMapper.byInscriptionId(inscription.getInscriptionId());
                // 1. 检查是不是在 collection 里面
                if (ordinal != null) {
                    // remove utxo
                    utxoMapper.deleteUtxo(ordinal.getUtxoTxid(), ordinal.getUtxoVout());

                    if (ordinal.getStatus() == OrdinalStatus.UNAVAILABLE) {
                        String[] vs = inscription.getOutput().split(":");
                        String txid = vs[0];
                        Integer vout = Integer.parseInt(vs[1]);

                        // update ordinal status
                        ordinal.setStatus(OrdinalStatus.AVAILABLE);
                        ordinal.setUtxoTxid(txid);
                        ordinal.setUtxoVout(vout);
                        ordinal.setUtxoValue(inscription.getOutputValue());
                        ordinalMapper.updateById(ordinal);

                        try {
                            stakeService.confirmOrdinalStake(inscription.getInscriptionId(), inscription.getOutput(), inscription.getOutputValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
//                else {
//                    String[] vs = inscription.getOutput().split(":");
//                    String txid = vs[0];
//                    Integer vout = Integer.parseInt(vs[1]);
//
//                    ordinal = new Ordinal();
//
//                    // update ordinal status
//                    ordinal.setInscriptionId(inscription.getInscriptionId());
//                    ordinal.setInscriptionNumber(inscription.getInscriptionNumber());
//                    ordinal.setContent(inscription.getContent());
//                    ordinal.setStatus(OrdinalStatus.AVAILABLE);
//                    ordinal.setUtxoTxid(txid);
//                    ordinal.setUtxoVout(vout);
//                    ordinal.setUtxoValue(inscription.getOutputValue());
//                    ordinalMapper.insert(ordinal);
//                }
            }
            cursor += inscriptionsData.getList().size();
        } while (cursor < total);
    }
}
