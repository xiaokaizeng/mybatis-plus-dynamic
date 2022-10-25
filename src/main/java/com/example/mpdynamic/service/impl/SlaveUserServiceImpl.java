package com.example.mpdynamic.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import com.example.mpdynamic.repository.slave.SlaveUserDao;
import com.example.mpdynamic.service.ISlaveUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 *
 * @author xkz
 * @since 2022/10/21 16:29
 */
@Service
@DS("slave")
public class SlaveUserServiceImpl extends ServiceImpl<SlaveUserDao, SlaveUserEntity> implements ISlaveUserService {
    private final SlaveUserDao dao;

    public SlaveUserServiceImpl(SlaveUserDao dao) {
        this.dao = dao;
    }

    @Override
    public List<MasterUserEntity> slaveUsers() {
        return dao.getUserList();
    }

    @Override
    public List<SlaveUserEntity> slaveUsersByMybatisPlus() {
        return dao.selectList(Wrappers.lambdaQuery());
    }

    @Override
    public Integer insertBatch(List<SlaveUserEntity> users) {
        return dao.insertBatch(users);
    }
}
