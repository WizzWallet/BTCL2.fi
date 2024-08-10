package com.wizz.fi.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StakeOrdinalDTO {
    private String txid;
    private String inputAddress;
    private String outputAddress;
}
