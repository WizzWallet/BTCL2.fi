package com.wizz.fi.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wizz.fi.dao.model.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("select * from users where address = #{address}")
    User byAddress(@Param("address") String address);
}
