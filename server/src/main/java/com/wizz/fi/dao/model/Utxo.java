package com.wizz.fi.dao.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wizz.fi.dao.enums.UtxoStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("utxos")
@Accessors(chain = true)
public class Utxo extends BaseModel {
    @TableField("utxo_txid")
    private String utxoTxid;

    @TableField("utxo_vout")
    private Integer utxoVout;

    @TableField("utxo_value")
    private Long utxoValue;

    @TableField("status")
    private UtxoStatus status;
}
