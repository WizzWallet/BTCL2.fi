package com.wizz.fi.dto.mempool;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Fee {
    private Integer fastestFee;
    private Integer halfHourFee;
    private Integer hourFee;
    private Integer economyFee;
    private Integer minimumFee;
}
