package com.wizz.fi.vo;

import com.wizz.fi.dao.enums.Chain;
import com.wizz.fi.dao.enums.OrderStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderVO {
    private String number;

    private String inputAddress;

    private Chain inputChain;

    private String inputTxid;

    private String outputAddress;

    private Chain outputChain;

    private String outputTxid;

    private OrderStatus status;

    private String broadcastResult;
}
