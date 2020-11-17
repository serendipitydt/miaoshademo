package org.damein.miaosha.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.damein.miaosha.domain.MiaoshaUser;

@Mapper
public interface MiaoshaUserDao {
    @Select("select * from miaosha_user_cloud where id=#{id}")
    MiaoshaUser getById(@Param("id") long id);
}
