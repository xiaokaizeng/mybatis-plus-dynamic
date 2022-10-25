package com.example.mpdynamic.service.impl;

import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import com.example.mpdynamic.service.IMasterUserService;
import com.example.mpdynamic.service.ISlaveUserService;
import com.example.mpdynamic.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：
 *
 * @author xkz
 * @since 2022/10/21 16:31
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private final IMasterUserService masterService;
    private final ISlaveUserService slaveService;

    public UserServiceImpl(IMasterUserService masterService, ISlaveUserService slaveService) {
        this.masterService = masterService;
        this.slaveService = slaveService;
    }

    @Override
    public List<SlaveUserEntity> masterUsers() {
        return masterService.masterUsers();
    }

    @Override
    public List<MasterUserEntity> masterUsersByMybatisPlus() {
        return masterService.masterUsersByMybatisPlus();
    }

    @Override
    public List<MasterUserEntity> slaveUsers() {
        return slaveService.slaveUsers();
    }

    @Override
    public List<SlaveUserEntity> slaveUsersByMybatisPlus() {
        return slaveService.slaveUsersByMybatisPlus();
    }

    /**
     * 由于此方法内部既操作了slave，又操作了master库；
     * 而事务注解@Transactional默认是master,所以导致slave库的操作不成功。
     * 因此，这里不能用事务注解，不然会报错：
     * java.sql.SQLSyntaxErrorException: Table 'test.t_user' doesn't exist
     *
     * @author xkz
     * @since 2022年10月21日 17:22
     */
    //@Transactional(rollbackFor = Exception.class)
    @Override
    public void sync() {
        //取slave数据到内存,入master库
        List<MasterUserEntity> slaveUsers = slaveService.slaveUsers();
        //取master数据到内存
        List<SlaveUserEntity> masterUsers = masterService.masterUsers();

        //slave数据入master库
        log.info("master db user add {} column data[s].", masterService.insertBatch(slaveUsers));
        //master数据入slave库
        log.info("slave db user add {} column data[s].", slaveService.insertBatch(masterUsers));
    }
}
