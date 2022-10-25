--我这里用的是sybase数据库进行测试，也可以换成其他数据库
create table t_user
(
    id   int identity
        constraint user_pk
        primary key,
    name nvarchar not null,
    age  int      not null
)  go

INSERT INTO t_user(id,name,age)
select 1,'Go',10 union all
select 2,'C',30 go
