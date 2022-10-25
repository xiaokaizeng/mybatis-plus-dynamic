package com.example.mpdynamic.repository.master;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 描述：master库userDao
 * 这里显示添加了 @DS("master") 注解；
 * 由于添加了配置 primary: master;不添加默认就是master;
 * 所以这里不添加注解也是一样的效果
 *
 * @author xkz
 * @since 2022/10/21 16:07
 */
//@DS("master")
public interface MasterUserDao extends BaseMapper<MasterUserEntity> {
    @Select("select id, name, age from user")
    List<SlaveUserEntity> getUserList();

    @Insert("<script>" +
            "  insert into user(name, age) values" +
            "  <foreach collection='users' item='user' separator=','>" +
            "    (#{user.name}, #{user.age})" +
            "  </foreach>" +
            "</script>")
    Integer insertBatch(List<MasterUserEntity> users);
}
