package com.wizz.fi.dto.mempool;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Utxo {
    private String txid;
    private Integer vout;
    private Long value;
    private Status status;

    @Data
    public static class Status {
        private Boolean confirmed;
        private Integer block_height;
        private String block_hash;
        private Integer block_time;
    }
}
