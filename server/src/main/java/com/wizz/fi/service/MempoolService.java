package com.wizz.fi.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wizz.fi.dto.mempool.Fee;
import com.wizz.fi.dto.mempool.Utxo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MempoolService {
    @Value("${mempool.host}")
    private String mempoolHost;

    @Autowired
    private OkHttpClient okHttpClient;

    public List<Utxo> getAddressUtxos(String address) {
        try {
            String url = String.format("%s/address/%s/utxo", mempoolHost, address);

            log.info("getAddressUtxos.request {}", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();

            log.debug("getAddressUtxos.response {}", resp);
            return new Gson().fromJson(resp, new TypeToken<List<Utxo>>() {
            }.getType());
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return null;
        }
    }

    public Fee getRecommendedFee() {
        try {
            String url = String.format("%s/v1/fees/recommended", mempoolHost);

            log.info("getRecommendedFee.request {}", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();

            log.debug("getRecommendedFee.response {}", resp);
            return new Gson().fromJson(resp, new TypeToken<Fee>() {
            }.getType());
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return null;
        }
    }

    public String broadcast(String rawTx) {
        try {
            String url = String.format("%s/tx", mempoolHost);

            log.info("broadcast.request {}", url);

            // 创建请求体，指定 MIME 类型为 text/plain
            RequestBody body = RequestBody.create(
                    rawTx, MediaType.parse("text/plain")
            );

            // 构建 Request 对象
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            log.error("{}", e.getMessage());
            return null;
        }
    }
}
