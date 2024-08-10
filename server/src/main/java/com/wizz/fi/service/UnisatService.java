package com.wizz.fi.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wizz.fi.dto.unisat.InscriptionsData;
import com.wizz.fi.dto.unisat.UnisatResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UnisatService {
    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${ordinal.address}")
    private String address;

    @Value("${ordinal.api}")
    private String api;

    public List<InscriptionsData.Inscription> list() {
        String url = String.format("%s?cursor=0&size=100&address=%s", api, address);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String body = response.body().string();
            log.debug(body);

            UnisatResponse<InscriptionsData> response1 = new Gson().fromJson(body, new TypeToken<UnisatResponse<InscriptionsData>>() {
            }.getType());
            if (response1.getCode() == 0) {
                return response1.getData().getList();
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
