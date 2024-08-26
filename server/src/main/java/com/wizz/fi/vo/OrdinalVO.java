package com.wizz.fi.vo;

import com.wizz.fi.dao.enums.OrdinalStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrdinalVO {
    private Integer inscriptionNumber;

    private String inscriptionId;

    private String content;

    private OrdinalStatus status;
}
