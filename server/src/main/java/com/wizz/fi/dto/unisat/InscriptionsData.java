package com.wizz.fi.dto.unisat;

import lombok.Data;

import java.util.List;

@Data
public class InscriptionsData {
    private Integer total;
    private List<Inscription> list;

    @Data
    public static class Inscription {
        private String inscriptionId;
        private Integer inscriptionNumber;
        private String address;
        private String content;
        private String contentType;
        private String contentBody;
        private String genesisTransaction;
        private String location;
        private String output;
        private Integer offset;
        private Integer utxoHeight;
        private Integer utxoConfirmation;
    }
}
