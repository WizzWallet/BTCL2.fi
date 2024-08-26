package com.wizz.fi.dao.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Prevout {
    private String txid;
    private Long value;
    private Integer vout;
    private boolean isOrdinal;
}
