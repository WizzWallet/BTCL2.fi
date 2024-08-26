package com.wizz.fi.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wizz.fi.dao.model.Utxo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UtxoMapper extends BaseMapper<Utxo> {
    @Select("select * from utxos where status = 0 order by utxo_value desc limit 1")
    Utxo getBiggest();

    @Delete("delete from utxos where utxo_txid = #{utxoTxid} and utxo_vout = #{utxoVout}")
    void deleteUtxo(@Param("utxoTxid") String utxoTxid, @Param("utxoVout") Integer utxoVout);

    @Update("truncate utxos")
    void truncate();
}
