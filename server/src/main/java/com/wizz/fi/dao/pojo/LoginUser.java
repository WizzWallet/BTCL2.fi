package com.wizz.fi.dao.pojo;

import com.wizz.fi.dao.enums.Chain;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginUser {
    private String userAddress;
    private String pubkey;
    private Chain chain;
}
