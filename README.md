# 工程简介（[项目地址](https://github.com/xiaokaizeng/mybatis-plus-dynamic)）

# springboot+mybatis-plus+dynamic(数据库mysql+sybase,支持hikari和druid配置)

因项目需要在sybase和mysql之前进行一部分数据同步，体量不大。打算用mybatis-plus的多数据源来做。因为没有使用过，所以边学习边做，有不对的地方，欢迎指出。

## 1.环境（[项目地址](https://github.com/xiaokaizeng/mybatis-plus-dynamic)）

MySQL：8.0.20

sybase：ase 15.7

springboot：2.6.3

mybatis-plus：3.5.2

JDK：1.8+

构建工具：gradle

IDE：idea 2022.3

## 2.表结构

1. MySQL

```sql
CREATE TABLE `user`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NULL,
    `age`  int NULL,
    PRIMARY KEY (`id`) USING BTREE
)
```

插入测试数据

```sql
INSERT INTO `user`
VALUES (1, 'Java', 22);
INSERT INTO `user`
VALUES (2, 'Python', 12);
INSERT INTO `user`
VALUES (3, 'C#', 21);
```

2.Sybase

```sql
--我这里用的是sybase数据库进行测试，也可以换成其他数据库
create table t_user
(
    id   int identity
        constraint user_pk
        primary key,
    name nvarchar not null,
    age  int      not null
) go
```

插入测试数据

```sybase
INSERT INTO t_user(id, name, age)
select 1, 'Go', 10
union all
select 2, 'C', 30
go
```

## 3.构建项目

1. 引入的gradle依赖

```gradle
    //引入本地jar，项目的libs目录下的jar包(这里主要是sybase和oracle先关驱动，需要的自取)
    implementation fileTree(dir: "libs", includes: ['*.jar'])
  
    implementation 'mysql:mysql-connector-java:8.0.30'
    implementation 'com.baomidou:mybatis-plus-boot-starter:3.5.2'
    implementation "com.baomidou:dynamic-datasource-spring-boot-starter:3.5.2"
    implementation 'com.baomidou:mybatis-plus-generator:3.5.3'
    implementation 'org.freemarker:freemarker:2.3.31'

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'

    implementation 'com.google.code.gson:gson:2.9.0'
```

2. 配置application.yml([``src/main/resources/application.yml``])

```yaml
spring:
  profiles:
    active: hikari
  application:
    name: mybatis-plus-dynamic # 应用名称

#应用服务 配置
server:
  port: 30000
  tomcat:
    max-threads: 300

# mybatis-plus相关配置
mybatis-plus:
  # xml扫描，多个目录用逗号或者分号分隔（告诉 Mapper 所对应的 XML 文件位置）
  mapper-locations: classpath:mapper/*.xml
  # 以下配置均有默认值,可以不设置
  auto-generator: true  # 自动生成代码的开关，默认为false
  global-config:
    db-config:
      #主键类型 AUTO:"数据库ID自增" INPUT:"用户输入ID",ID_WORKER:"全局唯一ID (数字类型唯一ID)", UUID:"全局唯一ID UUID";
      id-type: ASSIGN_UUID
      #字段策略 IGNORED:"忽略判断"  NOT_NULL:"非 NULL 判断")  NOT_EMPTY:"非空判断"
      insert-strategy: NOT_NULL
      update-strategy: NOT_NULL
      where-strategy: NOT_NULL
  configuration:
    # 是否开启自动驼峰命名规则映射:从数据库列名到Java属性驼峰命名的类似映射
    map-underscore-to-camel-case: true
    # 如果查询结果中包含空值的列，则 MyBatis 在映射的时候，不会映射这个字段
    call-setters-on-nulls: true
    # 这个配置会将执行的sql打印出来，在开发或测试的时候可以用
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

3. 配置application-hikari.yml([``src/main/resources/application-hikari.yml``])

```yaml
spring:
  #数据库配置----------------------------------------------------------
  datasource:
    dynamic:
      primary: test
      datasource:
        master:
          # spring boot 2.1及以上（内置jdbc8驱动）
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
          username: root
          password: root
        slave:
          driver-class-name: com.sybase.jdbc4.jdbc.SybDriver
          url: jdbc:sybase:Tds:127.0.0.1:5000/test?charset=cp936
          username: sa
          password: 123456
  hikari:
    connection-test-query: SELECT 1
    connection-timeout: 60000
    validation-timeout: 3000
    idle-timeout: 60000
    login-timeout: 5
    max-lifetime: 60000
    maximum-pool-size: 10
    minimum-idle: 10
    read-only: false
```

**注：这里用的是springboot自带的hikari连接池配置实现。后面会添加druid连接池的配置。**

## 4. 编码

1. 添加mybatis-plus配置
   创建``MybatisPlusConfiguration``,路径：``src/main/java/com/example/mpdynamic/config/MybatisPlusConfiguration.java``

```java

@Configuration
@MapperScan(basePackages = {"com.example.mpdynamic.repository"})
public class MybatisPlusConfiguration {
}
```

2. 创建实体类``MasterUserEntity``和``SlaveUserEntity``，路径：``src/main/java/com/example/mpdynamic/entity/``下

**MasterUserEntity**

```java

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
```

**SlaveUserEntity**

```java

@Data
@TableName("t_user")
public class SlaveUserEntity {
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
```

**注意：这里为了区分，添加了2个结构一样的实体类。也可以只创建一个实体类，但是要注意``@TableName``
的注解只能映射一个库的实体类（Mapper）；另外一个需要写自定义SQL。这个源码有示例。**

3. 创建``Mapper``类，路径：``src/main/java/com/example/mpdynamic/repository/``下

**MasterUserDao**

```java

@DS("master")
public interface MasterUserDao extends BaseMapper<MasterUserEntity> {

}
```

**SlaveUserDao**

```java

@DS("slave")
public interface SlaveUserDao extends BaseMapper<SlaveUserEntity> {

}
```

4. 添加``Service``，路径：``src/main/java/com/example/mpdynamic/service/``
   **IMasterUserService**

```java
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
```

**IMasterUserService**

```java
public interface ISlaveUserService extends IService<SlaveUserEntity> {
    /**
     * 测试 用MasterUserEntity实体接收slave库的user查询结果；
     * 由于MasterUserEntity对应的Mapper类是MasterDao；
     * 所以，这里不能用mybatis-plus自带的接口取查询，需要自定义查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.MasterUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:09
     */
    List<MasterUserEntity> slaveUsers();

    /**
     * 用SlaveUserEntity实体接收slave库的user查询结果；
     * 用mybatis-plus自带的接口取查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.SlaveUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:11
     */
    List<SlaveUserEntity> slaveUsersByMybatisPlus();

    /**
     * 批量插入
     *
     * @param users List<SlaveUserEntity>
     * @return java.lang.Integer
     * @author xkz
     * @since 2022年10月25日 15:24
     */
    Integer insertBatch(List<SlaveUserEntity> users);
}

```

实现类，路径：``src/main/java/com/example/mpdynamic/service/impl/``
**MasterUserServiceImpl**

```java

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

```

**SlaveUserServiceImpl**

```java

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
```

**注：** 这里添加了几个测试方法，以及简单实现，这里我就不过多赘述了。说明一点：这里``Service``类都通过``@DS``注解指明了数据源。

5. 添加``Mapper``类实现

**MasterUserDao**

```java
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

```

**SlaveUserDao**

```java
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
```

由于``Service``已经添加了``@DS``注解了，因此``Mapper``的``@DS``注解可以不加了。
6. 添加测试``Service``
**IUserService**

```java
public interface IUserService {
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
     * 测试 用MasterUserEntity实体接收slave库的user查询结果；
     * 由于MasterUserEntity对应的Mapper类是MasterDao；
     * 所以，这里不能用mybatis-plus自带的接口取查询，需要自定义查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.MasterUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:09
     */
    List<MasterUserEntity> slaveUsers();

    /**
     * 用SlaveUserEntity实体接收slave库的user查询结果；
     * 用mybatis-plus自带的接口取查询
     *
     * @return java.util.List<com.example.mpdynamic.entity.SlaveUserEntity>
     * @author xkz
     * @since 2022年10月25日 15:11
     */
    List<SlaveUserEntity> slaveUsersByMybatisPlus();

    /**
     * 测试两表数据同步
     *
     * @author xkz
     * @since 2022年10月25日 15:11
     */
    void sync();
}
```

**UserServiceImpl**

```java
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
```

**TestController**

```java
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
```

8. 运行程序，访问接口测试（``测试就请自行测试``）。

## 附：添加druid相关配置

1. 配置application-druid.yml([``src/main/resources/application-druid.yml``])

```yaml
spring:
  #数据库配置----------------------------------------------------------
  #autoconfigure:
  #exclude: com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure # 去除druid配置
  datasource:
    # 指定使用 Druid 数据源
    type: com.alibaba.druid.pool.DruidDataSource
    dynamic:
      druid:
        initial-size: 5
        min-idle: 10
        max-active: 30
      primary: master
      datasource:
        master:
          # spring boot 2.1及以上（内置jdbc8驱动）
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://127.0.0.1:3306/test?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true
          username: root
          password: root
        slave:
          driver-class-name: com.sybase.jdbc4.jdbc.SybDriver
          url: jdbc:sybase:Tds:127.0.0.1:5000/test?charset=cp936
          username: sa
          password: 123456
```

2. 配置application.yml([``src/main/resources/application.yml``])

```yaml
spring:
  profiles:
    active: druid
```

这里只需把``spring.profiles.active``的值改为``druid``，其他保持不变即可。

## 项目源码地址

[源码地址](https://github.com/xiaokaizeng/mybatis-plus-dynamic)
