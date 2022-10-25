package com.example.mpdynamic.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 描述：master库的user表实体类
 * 实体类也可以给slave使用，但是不能用mybatis的自带接口
 *
 * @author xkz
 * @since 2022/10/21 16:24
 */
@Data
@TableName("user")
public class MasterUserEntity {
    @TableId
    private Integer id;
    private String name;
    private Integer age;

    public String toString() {
        return String.format("===============================\n" +
                "user{id=%d,name=%s,age=%d}\n" +
                "===============================\n", id, name, age);
    }
}
