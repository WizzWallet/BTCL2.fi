package com.wizz.fi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ETHService {
    @Value("${token.contractAddress}")
    private String contractAddress;

    @Value("${token.privateKey}")
    private String privateKey;

    @Value("${token.rpc}")
    private String rpc;

    @Async
    public CompletableFuture<String> transfer(String toAddress, String amount) throws Exception {
        // 连接到以太坊节点
        HttpService httpService = new HttpService(rpc);
        Web3j web3j = Web3j.build(httpService);

        Credentials credentials = Credentials.create(privateKey);

        EthBlock latestBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
        BigInteger blockGasLimit = latestBlock.getBlock().getGasLimit();

        // 设置合理的 gas limit，例如比区块限制低 10%
        BigInteger gasLimit = blockGasLimit.multiply(BigInteger.valueOf(10)).divide(BigInteger.valueOf(100));

        // 设定 gas price 和 gas limit
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        BigInteger gasPrice = blockGasPrice.multiply(BigInteger.valueOf(3)).divide(BigInteger.valueOf(2));  // 1.5倍的费用

        log.info("gasPrice:{}, gasLimit:{}", gasPrice, gasLimit);

        ContractGasProvider gasProvider = new StaticGasProvider(gasPrice, gasLimit);

        TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, 10023);

        ERC20 contract = ERC20.load(contractAddress, web3j, transactionManager, gasProvider);
        contract.setSyncThreshold(3600000); // hardhat hack

        log.info("transfer {} wToothy to {}", amount, toAddress);
        TransactionReceipt receipt = contract.transfer(toAddress, new BigInteger(amount)).send();
        log.info("gas used {}", receipt.getGasUsed());
        log.info("tx hash {}", receipt.getTransactionHash());
        log.info("transfer wToothy to {}", toAddress);
        
        // send gas to user
//        receipt = Transfer.sendFunds(web3j, credentials, toAddress, BigDecimal.valueOf(1500), Convert.Unit.GWEI).send();
//        log.info("tx hash {}", receipt.getTransactionHash());

        return CompletableFuture.completedFuture(receipt.getTransactionHash());
    }
}
