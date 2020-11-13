package org.damein.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.damein.miaosha.domain.User;


@Mapper
public interface UserDao {
    @Select("select * from user where id = #{id}")
    //@Para定义参数
    User getById(@Param("id") int id);

   @Insert("insert into user(id,name)values(#{id},#{name})")
   int Insert(User user);
}

