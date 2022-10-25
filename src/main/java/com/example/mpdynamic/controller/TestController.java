package com.example.mpdynamic.controller;

import com.example.mpdynamic.entity.MasterUserEntity;
import com.example.mpdynamic.entity.SlaveUserEntity;
import com.example.mpdynamic.service.IUserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 描述：
 *
 * @author xkz
 * @since 2022/10/21 16:37
 */
@RestController
@Slf4j
@RequestMapping
public class TestController {
    private final IUserService userService;

    public TestController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("mUser")
    public Object mUser() {
        //用slave表的user实体接收master查询的结果
        List<SlaveUserEntity> list = userService.masterUsers();
        return new Gson().toJson(list);
    }

    @GetMapping("mmpUser")
    public Object mmpUser() {
        //mybatis-plus自带接口查询
        List<MasterUserEntity> list = userService.masterUsersByMybatisPlus();
        return new Gson().toJson(list);
    }

    @GetMapping("sUser")
    public Object sUser() {
        //用master表的user实体接收master查询的结果
        List<MasterUserEntity> list = userService.slaveUsers();
        return new Gson().toJson(list);
    }

    @GetMapping("smpUser")
    public Object smpUser() {
        //mybatis-plus自带接口查询
        List<SlaveUserEntity> list = userService.slaveUsersByMybatisPlus();
        return new Gson().toJson(list);
    }


    @GetMapping("sync")
    public String sync() {
        userService.sync();
        return "success";
    }
}
