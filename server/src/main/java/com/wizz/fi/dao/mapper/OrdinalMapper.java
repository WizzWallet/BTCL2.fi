package com.wizz.fi.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wizz.fi.dao.model.Ordinal;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrdinalMapper extends BaseMapper<Ordinal> {
    @Select("select * from ordinals where inscription_id = #{inscriptionId}")
    Ordinal byInscriptionId(@Param("inscriptionId") String inscriptionId);

}
