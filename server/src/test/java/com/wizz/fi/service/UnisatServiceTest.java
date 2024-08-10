package com.wizz.fi.service;

import com.wizz.fi.dto.unisat.InscriptionsData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
public class UnisatServiceTest {
    @Autowired
    private UnisatService unisatService;

    @Test
    public void list() {
        List<InscriptionsData.Inscription> inscriptionsDataList = unisatService.list();
        for (InscriptionsData.Inscription inscription : inscriptionsDataList) {
            System.out.println(inscription);
        }
    }
}
