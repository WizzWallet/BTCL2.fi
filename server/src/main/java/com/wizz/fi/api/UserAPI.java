package com.wizz.fi.api;

import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.mapper.UserMapper;
import com.wizz.fi.dto.UserLoginDTO;
import com.wizz.fi.service.RedisService;
import com.wizz.fi.sso.JwtTokenUtil;
import com.wizz.fi.sso.UnAuthorization;
import com.wizz.fi.util.BitcoinVerifier;
import com.wizz.fi.util.CommonResult;
import com.wizz.fi.util.MyAssert;
import com.wizz.fi.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserAPI {
    private static final String template = "Wizz.Cash wants you to sign this message with your Bitcoin account: \n" +
            "%s\n" +
            "\n" +
            "Click \"Sign\" only means you have proved this wallet is owned by you.\n" +
            "\n" +
            "This request will not trigger any blockchain transaction or cost any gas fee.\n" +
            "\n" +
            "Use of our website and service are subject to our Privacy Policy: https://sites.google.com/view/wizzwallet\n" +
            "\n" +
            "URI: https://wizz.cash\n" +
            "Version: 1\n" +
            "Chain ID: bitcoin\n" +
            "Nonce: %s\n" +
            "Issued At: %s";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @RequestMapping(value = "/btc_challenge", method = RequestMethod.GET)
    @UnAuthorization
    public CommonResult<String> challenge(@RequestParam("userAddress") String userAddress) {
        String challenge = RandomStringUtils.random(32, true, true);
        String key = "JAVA_ATOM_LOGIN:" + userAddress;

        String ts = ZonedDateTime.now(ZoneId.of("UTC")).format(formatter);
        String message = getMessage(userAddress, challenge, ts);

        redisService.set(key, message, 300); //5 min

        return CommonResult.success(message);
    }

    @RequestMapping(value = "/btc_login", method = RequestMethod.POST)
    @UnAuthorization
    public CommonResult<String> btcLogin(@RequestBody UserLoginDTO userLoginDTO) {
        String pubKey = userLoginDTO.getPubKey();
        String userAddress = userLoginDTO.getUserAddress();
        String signature = userLoginDTO.getSignature();

        MyAssert.notBlank(pubKey, ResultCode.PARAMETER_ERROR);
        MyAssert.notBlank(userAddress, ResultCode.PARAMETER_ERROR);
        MyAssert.notBlank(signature, ResultCode.PARAMETER_ERROR);

        String key = "JAVA_LOGIN:" + userAddress;
        MyAssert.isTrue(redisService.hasKey(key), ResultCode.PARAMETER_ERROR);

        String message = (String) redisService.get(key);

        boolean ret = BitcoinVerifier.verify(userAddress, pubKey, signature, message);

        MyAssert.isTrue(ret, ResultCode.VERIFY_FAILED);
        redisService.del(key);

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_address", userAddress);
        claims.put("chain", Chain.BTC);
        String jwt = jwtTokenUtil.generateToken(claims);

        return CommonResult.success(jwt);
    }

    @RequestMapping(value = "/evm_login", method = RequestMethod.POST)
    @UnAuthorization
    public CommonResult<String> evmLogin(@RequestBody UserLoginDTO userLoginDTO) {
        String pubKey = userLoginDTO.getPubKey();
        String userAddress = userLoginDTO.getUserAddress();
        String signature = userLoginDTO.getSignature();

        MyAssert.notBlank(pubKey, ResultCode.PARAMETER_ERROR);
        MyAssert.notBlank(userAddress, ResultCode.PARAMETER_ERROR);
        MyAssert.notBlank(signature, ResultCode.PARAMETER_ERROR);

        String key = "JAVA_LOGIN:" + userAddress;
        MyAssert.isTrue(redisService.hasKey(key), ResultCode.PARAMETER_ERROR);

        String message = (String) redisService.get(key);

        boolean ret = BitcoinVerifier.verify(userAddress, pubKey, signature, message);

        MyAssert.isTrue(ret, ResultCode.VERIFY_FAILED);
        redisService.del(key);

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_address", userAddress);
        claims.put("chain", Chain.ETH);
        String jwt = jwtTokenUtil.generateToken(claims);

        return CommonResult.success(jwt);
    }

    public String getMessage(String userAddress, String challenge, String ts) {
        return String.format(template, userAddress, challenge, ts);
    }
}
