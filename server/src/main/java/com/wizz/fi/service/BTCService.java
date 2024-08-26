package com.wizz.fi.service;

import com.wizz.fi.dao.pojo.Output;
import com.wizz.fi.dao.pojo.Prevout;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class BTCService {
    @Value("${wallet.mnemonic}")
    private String mnemonic;

    @Value("${wallet.ordinal_path}")
    private String ordinalPath;

    @Value("${wallet.change_path}")
    private String changePath;


    @Autowired
    private MempoolService mempoolService;

    @Async
    public CompletableFuture<Result> transfer(List<Prevout> prevouts, List<Output> outputs) throws Exception {
        // 使用测试网络参数
        NetworkParameters params = TestNet3Params.get();

        DeterministicSeed seed = new DeterministicSeed(mnemonic, null, "", 0L);

        DeterministicKeyChain keyChain = DeterministicKeyChain.builder().seed(seed).build();

        DeterministicKey ordinalKey = keyChain.getKeyByPath(HDUtils.parsePath(ordinalPath), true);
        DeterministicKey changeKey = keyChain.getKeyByPath(HDUtils.parsePath(changePath), true);


        List<ECKey> keys = new ArrayList<>();
        prevouts.forEach(prevout -> {
            if (prevout.isOrdinal()) {
                keys.add(ordinalKey);
            } else {
                keys.add(changeKey);
            }
        });

        // 创建交易
        Transaction tx = new Transaction(params);
        tx.setVersion(2);

        // 添加多个输入
        addInputs(tx, prevouts, params);

        // 添加多个输出
        addOutputs(tx, outputs, params);

        signInputs(tx, keys);

        // 打印交易详情
        String txid = tx.getTxId().toString();
        String raw = Utils.HEX.encode(tx.bitcoinSerialize());

        System.out.println("交易构造完成。交易ID: " + txid);
        System.out.println("交易十六进制表示: " + raw);

        String resp = mempoolService.broadcast(raw);
        log.info("broadcase response: {}", resp);

        Result result = new Result();
        result.setTxid(txid);
        result.setResp(resp);

        return CompletableFuture.completedFuture(result);
    }

    private void addInputs(Transaction tx, List<Prevout> prevouts, NetworkParameters params) {
        for (Prevout prevout : prevouts) {
            Sha256Hash prevTxHash = Sha256Hash.wrap(prevout.getTxid());
            TransactionOutPoint outPoint = new TransactionOutPoint(params, prevout.getVout(), prevTxHash);
            TransactionInput input = new TransactionInput(params, tx, new byte[]{}, outPoint, Coin.valueOf(prevout.getValue()));
            input.setSequenceNumber(0xfffffffdL);
            input.setScriptSig(ScriptBuilder.createEmpty());
            input.setWitness(TransactionWitness.EMPTY);
            tx.addInput(input);
        }
    }

    private void addOutputs(Transaction tx, List<Output> outputs, NetworkParameters params) {
        // 添加多个输出
        for (Output output : outputs) {
            tx.addOutput(Coin.valueOf(output.getValue()), SegwitAddress.fromString(params, output.getAddress()));
        }
    }


    private void signInputs(Transaction tx, List<ECKey> keys) throws Exception {
        for (int i = 0; i < tx.getInputs().size(); i++) {
            TransactionInput input = tx.getInput(i);
            ECKey key = keys.get(i);

            Script scriptCode = (new ScriptBuilder()).op(ScriptOpCodes.OP_DUP).op(ScriptOpCodes.OP_HASH160).data(key.getPubKeyHash()).op(ScriptOpCodes.OP_EQUALVERIFY).op(ScriptOpCodes.OP_CHECKSIG).build();

//            Script scriptCode = ScriptBuilder.createP2WPKHOutputScript(key);

            Sha256Hash sigHash = tx.hashForWitnessSignature(i, scriptCode, input.getValue(), Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecdsaSignature = key.sign(sigHash);
            TransactionSignature txSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, false);

            input.setScriptSig(ScriptBuilder.createEmpty()); // P2WPKH 的 ScriptSig 是空的
            input.setWitness(TransactionWitness.redeemP2WPKH(txSignature, key));
        }
    }

    @Data
    public static class Result {
        private String txid;
        private String resp;
    }
}
