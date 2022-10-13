# 1，复习

![image-20220317140807761](day05.assets/image-20220317140807761.png)

# 2，maven

![image-20220317142450169](day05.assets/image-20220317142450169.png)

maven使用步骤

1，d盘创建文件夹mavenjar

2,拷贝settings.xml到d:

3,修改setting.xml

![image-20220317143059907](day05.assets/image-20220317143059907.png)

# 3,使用springboot创建服务器端程序

spring:是用来整合springmvc,mybatis-plus，写很多xml

springboot是spring升级，整合springmvc,mybatis-plus不用写xml,内置了tomcat

## 3.1 在idea中配置maven

![image-20220317143850827](day05.assets/image-20220317143850827.png)

![image-20220317144249751](day05.assets/image-20220317144249751.png)

## 3.2 创建springboot项目

![image-20220317144640645](day05.assets/image-20220317144640645.png)

![image-20220317144829351](day05.assets/image-20220317144829351.png)

![image-20220317145141089](day05.assets/image-20220317145141089.png)

![image-20220317145257372](day05.assets/image-20220317145257372.png)

![image-20220317145347605](day05.assets/image-20220317145347605.png)

本地仓库d:\mavenjar中有文件夹，说明maven配置成功了

![image-20220317145817784](day05.assets/image-20220317145817784.png)

## 3.3 写一个服务器端程序

创建controllor包，contollor控制

创建类

```java
@RestController//告诉框架为类创建一个对象
class CityTongjiController{
    //在浏览器中输入http://localhost:8080/test
    @RequestMappig("/test")
    public String test(){
        return "大数据统计结果"
    }
}
```

![image-20220317152940559](day05.assets/image-20220317152940559.png)

# 4,访问数据库

访问数据库用mybatis

讲mybatis-plus

![image-20220317154556923](day05.assets/image-20220317154556923.png)

## 4.1测试数据库

启动vmware

启动sqlyoug,连上了

select * from city_tongji

## 4.2创建项目

1,选择项目类型

2，输入包名，项目名

3，添加spring web,mysql依赖

![image-20220317161020115](day05.assets/image-20220317161020115.png)

4，在pom.xml中添加mybatis-plus依赖

![image-20220317162022488](day05.assets/image-20220317162022488.png)

5，启动报错

mybatis-plus要到application.yml中读取url属性中的数据库名

![image-20220317162356417](day05.assets/image-20220317162356417.png)

![image-20220317162801836](day05.assets/image-20220317162801836.png)

## 4.3创建控制层

## 4.4编写数据访问层

### 4.4.1 分析

![image-20220317165146874](day05.assets/image-20220317165146874.png)

### 4.4.2设计

```java
package com.tedu.mybatis01.pojo
    //实体类
    class DateTongjiEntity{
        Strring registerDate//表中的register_date列
        Integer total//表中total列
    }

//数据访问层
com.teud.mybatis01.mapper
    interface DateTongjiMapper extends BaseMapper<DateTongjiEntity>{
    
}

//接口的实现类是由mybatis生成
@mapperScan("com.teud.mybatis01.mapper")
    public class Mybatisplus01Application{}
//控制层
class CityController{
    //得到数据访问层的对象
    @autowired//自动注入，从框架中取一个对象
    DateTongjiMapper dateTongjiMapper
        
    public list<DateTongjiEntity> getDateTongji(){
        调用数据访问层
    }
}
```



### 4.4.3实现

### 4.4.4测试

# 5，小结

![image-20220317174958877](day05.assets/image-20220317174958877.png)