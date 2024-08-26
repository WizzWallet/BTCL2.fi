package com.wizz.fi.dao.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Output {
    private String address;
    private Long value;
}
