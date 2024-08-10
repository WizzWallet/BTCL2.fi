package com.wizz.fi.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserLoginDTO {
    private String signature;

    private String userAddress;

    private String pubKey;
}
