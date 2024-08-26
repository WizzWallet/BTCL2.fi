package com.wizz.fi.service;

import com.wizz.fi.dto.unisat.InscriptionsData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class UnisatServiceTest {
    @Autowired
    private UnisatService unisatService;

    @Value("${ordinal.address}")
    private String ordinalAddress;

    @Test
    public void list() {
        InscriptionsData inscriptionsData = unisatService.list(ordinalAddress, 0);
        for (InscriptionsData.Inscription inscription : inscriptionsData.getList()) {
            System.out.println(inscription);
        }
    }
}
