package com.example.mpdynamic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;

import java.util.List;

/**
 * 描述：
 *
 * @author xkz
 * @since 2022/10/21 16:29
 */
public interface IMasterUserService extends IService<MasterUserEntity> {
    /**
     * 测试 用SlaveUserEntity实体接收master库的user查询结果；
     * 由于SlaveUserEntity对应的Mapper类是SlaveDao；
     * 所以，这里不能用mybatis-plus自带的接口取查询，需要自定义查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.SlaveUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:06
     */
    List<SlaveUserEntity> masterUsers();

    /**
     * 用MasterUserEntity实体接收master库的user查询结果；
     * 用mybatis-plus自带的接口取查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.MasterUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:07
     */
    List<MasterUserEntity> masterUsersByMybatisPlus();

    /**
     * 批量插入
     *
     * @param users List<MasterUserEntity>
     * @return java.lang.Integer
     * @author xkz
     * @since 2022年10月25日 15:22
     */
    Integer insertBatch(List<MasterUserEntity> users);
}
