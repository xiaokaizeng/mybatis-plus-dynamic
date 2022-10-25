package com.example.mpdynamic.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import com.example.mpdynamic.repository.master.MasterUserDao;
import com.example.mpdynamic.service.IMasterUserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：
 *
 * @author xkz
 * @since 2022/10/21 16:29
 */
@Service
@DS("master")
public class MasterUserServiceImpl extends ServiceImpl<MasterUserDao, MasterUserEntity> implements IMasterUserService {
    private final MasterUserDao dao;

    public MasterUserServiceImpl(MasterUserDao dao) {
        this.dao = dao;
    }

    @Override
    public List<SlaveUserEntity> masterUsers() {
        return dao.getUserList();
    }

    @Override
    public List<MasterUserEntity> masterUsersByMybatisPlus() {
        return dao.selectList(Wrappers.lambdaQuery());
    }

    @Override
    public Integer insertBatch(List<MasterUserEntity> users) {
        return dao.insertBatch(users);
    }
}
