package com.example.mpdynamic.repository.slave;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 描述：slave库userDao
 * 这里指定了 @DS("slave") 注解；
 * 由于添加了配置 primary: master;不添加默认就是master;
 * 所以这里必须指定 @DS("slave")
 *
 * @author xkz
 * @since 2022/10/21 16:10
 */
//@DS("slave")
public interface SlaveUserDao extends BaseMapper<SlaveUserEntity> {
    @Select("select id, name, age from t_user")
    List<MasterUserEntity> getUserList();

    @Insert("<script>" +
            "  insert into t_user(name, age)" +
            "  <foreach collection='users' item='user' separator='union all'>" +
            "    select #{user.name}, #{user.age}" +
            "  </foreach>" +
            "</script>")
    Integer insertBatch(List<SlaveUserEntity> users);
}
