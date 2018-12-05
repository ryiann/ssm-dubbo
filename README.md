# ssm-dubbo
SSM(SpringMVC + Spring + MyBatis)-Dubbo demo

- SpringMVC+Spring+MyBatis
- Dubbo

---

1、本文主要侧重于项目中Dubbo部分，SSM框架如何搭建的这里就不再赘述，关于SSM搭建部分

  参考文章：  
  
  [SSM框架——(SpringMVC+Spring+MyBatis+Maven多模块)整合][1]

2、阅读本文前需要知道Dubbo、Dubbo-Admin、Zookeeper是干什么用的

**Dubbo是什么？**  
  1.  Dubbo是一个分布式服务框架，致力于提供高性能和透明化的RPC远程服务调用方案，以及SOA服务治理方案，即远程服务调用的分布式框架 
  2.  大体来看，Dubbo分为消费者、提供者和注册中心
  3.  从服务模型的角度来看，Dubbo采用的是一种非常简单的模型，要么是提供方提供服务，要么是消费方消费服务，所以基于这一点可以抽象出服务提供方（Provider）和服务消费方（Consumer）两个角色
  4.  Dubbo采用全Spring配置方式，透明化接入应用，对应用没有任何API侵入，只需用Spring加载Dubbo的配置即可，Dubbo基于Spring的Schema扩展进行加载

**Dubbo能做什么？**
  1.  透明化的远程方法调用，就像调用本地方法一样调用远程方法，只需简单配置，没有任何API侵入
  2.  软负载均衡及容错机制，可在内网替代F5等硬件负载均衡器，降低成本，减少单点
  3.  服务自动注册与发现，不再需要写死服务提供方地址，注册中心基于接口名查询服务提供者的IP地址，并且能够平滑添加或删除服务提供者

**Dubbo核心功能：**
  1.  **Remoting:远程通讯**，提供对多种NIO框架抽象封装，包括“同步转异步”和“请求-响应”模式的信息交换方式
  2.  **Cluster: 服务框架**，提供基于接口方法的透明远程过程调用，包括多协议支持，以及软负载均衡，失败容错，地址路由，动态配置等集群支持
  3.  **Registry: 服务注册中心**，基于注册中心目录服务，使服务消费方能动态的查找服务提供方，使地址透明，使服务提供方可以平滑增加或减少机器


**Zookeeper在Dubbo中扮演了一个什么角色，起到了什么作用？**  
  -   Zookeeper是Dubbo推荐的注册中心， zookeeper用来注册服务和进行负载均衡

: **什么是Dubbo-Admin：**  
  -   用Zookeeper当注册中心，我们无法看到是否存在了什么提供者或消费者，这时就要借助Dubbo-Admin管理平台来实时的查看，也可以通过这个平台来管理提者和消费者

运行一个完整的Dubbo项目，除了需要Tomcat、Mysql外，还需要用到Zookeeper、Dubbo-Admin，请在运行项目前将所需环境准备好

## 项目结构

``` php
.
└── ssm-dubbo
    ├── background
    └── foreground
```

Dubbo服务可分为**提供者**和**消费者**，我把提供服务的统称为提供者，我将整套项目拆分成了background、foreground **两个**子项目，其中background项目是dubbo提供者，主要负责处理业务、操作数据库及暴露接口供消费者使用，foreground项目就很简单了，仅仅作为处理View的转发，调用接口。流程如下图

![流程图](https://img-blog.csdn.net/2018053012570947?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lvcmlfY2hlbg==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

### background结构

``` php
.
└── background
    ├── background-api # 提供的dubbo接口
    │   ├── pom.xml
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    │                   └── ryan
    ├── background-common # 工具类
    │   ├── pom.xml # 引入常用工具依赖
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    │                   └── ryan
    ├── background-dao # 数据库访问层
    │   ├── pom.xml # 描述工程资源的目录,编译打包 mapper.xml
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    │                   └── ryan
    ├── background-domain # 域模型层
    │   └── pom.xml
    ├── background-service # 业务逻辑层
    │   ├── pom.xml # 引入Spring、JDBC依赖
    │   └── src
    │       └── main
    │           ├── java
    │           │   └── com
    │           │       └── ryan
    │           └── resources
    │               └── applicationContext-service.xml # 扫描注解配置
    ├── background-web #表现层
    │   ├── pom.xml # 定义一些常量 [jdk version]
    │   └── src
    │       └── main
    │           ├── java
    │           │   └── com
    │           │       └── ryan
    │           ├── resources
    │           │   ├── applicationContext-aop.xml # 事务配置文件
    │           │   ├── applicationContext.properties # 配置文件
    │           │   ├── applicationContext-thread.xml # 线程池配置文件
    │           │   ├── applicationContext-web.xml # Spring跳转相关配置文件
    │           │   ├── applicationContext.xml # Spring配置文件
    │           │   ├── dataSource.xml # 连接池数据源配置文件
    │           │   ├── dubbo-provider.xml # Dubbo提供者配置文件
    │           │   ├── log4j.properties # 日志配置文件
    │           │   └── mybatis-config.xml # Mybatis配置文件
    │           └── webapp # Web静态资源文件
    │               ├── index.jsp
    │               └── WEB-INF # Web应用程序配置文件
    │                   ├── rest-servlet.xml
    │                   └── web.xml
    └── pom.xml #父POM
```

### foreground结构

``` php
.
└── foreground
    ├── foreground-common # 工具类
    │   ├── pom.xml # 引入常用工具依赖
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    ├── foreground-dao # 数据库访问层
    │   ├── pom.xml
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    ├── foreground-domain # 域模型层
    │   ├── pom.xml
    │   └── src
    │       └── main
    │           └── java
    │               └── com
    ├── foreground-service # 业务逻辑层
    │   ├── pom.xml
    │   └── src
    │       └── main
    │           ├── java
    │           │   └── com
    │           └── resources
    │               └── applicationContext-service.xml # 扫描注解配置
    ├── foreground-web #表现层
    │   ├── pom.xml
    │   └── src
    │       └── main
    │           ├── java
    │           │   └── com
    │           ├── resources
    │           │   ├── applicationContext-aop.xml # 事务配置文件
    │           │   ├── applicationContext.properties # 配置文件
    │           │   ├── applicationContext-thread.xml # 线程池配置文件
    │           │   ├── applicationContext-web.xml # Spring跳转相关配置文件
    │           │   ├── applicationContext.xml # Spring配置文件
    │           │   ├── dataSource.xml # 连接池数据源配置文件
    │           │   ├── dubbo-consumer.xml # Dubbo消费者配置文件
    │           │   ├── log4j.properties # 日志配置文件
    │           │   └── mybatis-config.xml # Mybatis配置文件
    │           └── webapp # Web静态资源文件
    │               ├── index.jsp
    │               └── WEB-INF # Web应用程序配置文件
    └── pom.xml #父POM
```

## SSM框架Dubbo整合配置

简单来说，一个SSM项目想要整合Dubbo，只需要在SSM框架的基础之上引入Dubbo所需要的依赖，和新增dubbo-consumer.xml、dubbo-provider.xml配置文件就行了

### 引入dubbo依赖

``` xml
<!-- dubbo -->
    <!-- https://mvnrepository.com/artifact/com.alibaba/dubbo -->
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>dubbo</artifactId>
      <version>2.5.3</version>
      <exclusions>
        <exclusion>
          <artifactId>spring</artifactId>
          <groupId>org.springframework</groupId>
        </exclusion>
      </exclusions>
    </dependency>
    <!-- zookeeper -->
    <!-- https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper -->
    <dependency>
      <groupId>org.apache.zookeeper</groupId>
      <artifactId>zookeeper</artifactId>
      <version>3.4.6</version>
      <exclusions>
        <exclusion>
          <groupId>log4j</groupId>
          <artifactId>log4j</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    <!-- zkclient -->
    <!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
    <dependency>
      <groupId>com.101tec</groupId>
      <artifactId>zkclient</artifactId>
      <version>0.5</version>
    </dependency>
```

### 提供者配置

注：`${}`常量是从applicationContext.properties配置文件中读取的

dubbo-provider.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

	<!-- 提供方应用信息，用于计算依赖关系 -->
	<dubbo:application name="${dubbo.appname}" logger="slf4j" />

	<!-- 使用zookeeper注册中心暴露服务地址 -->
	<dubbo:registry protocol="zookeeper" address="${dubbo.url}" register="true"/>

	<!-- 用dubbo协议在20880端口暴露服务 -->
	<dubbo:protocol payload="${dubbo.upload}" name="dubbo" port="${dubbo.port}"/>

	<!-- 具体的实现bean -->
	<bean id="StudentDubboService" class="com.ryan.service.impl.StudentDubboServiceImpl"/>
	<!-- 声明需要暴露的服务接口 -->
	<dubbo:service interface="com.ryan.service.StudentDubboService"
				   ref="StudentDubboService"
				   timeout="${dubbo.timeout}"
				   version="${dubbo.version}" />
</beans>
```

### 消费者配置

dubbo-consumer.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://code.alibabatech.com/schema/dubbo
       http://code.alibabatech.com/schema/dubbo/dubbo.xsd">

    <!-- 提供方应用信息，用于计算依赖关系 -->
	<dubbo:application name="${dubbo.appname}" logger="slf4j" />

	<!-- 使用multicast广播注册中心暴露发现服务地址 -->
	<!-- register:false，只订阅，不注册 -->
	<dubbo:registry id="registry" protocol="zookeeper" address="${dubbo.url}"/>
	
	<!-- dubbo接口 -->
	<dubbo:reference registry="registry"
					 check="false"
					 connections="1"
					 interface="com.ryan.service.StudentDubboService"
					 id="studentDubboService"
					 timeout="${dubbo.timeout}"
					 version="${dubbo.version}"
					 retries="0"
					 />
</beans>
```

我们在SSM框架的基础上新增以上配置，就可以进行测试了

## 测试

JDK：1.8
Dubbo-Admin ：http://dubbo.lqiao.top/dubbo-admin

- 用户名: root
- 密码: root

1、先将background项目编译(install)打包下，因为foreground项目会依赖background-api.jar  
2、分别将background、foreground项目扔到tomcat里运行，可以在上方提供的dubbo-admin中查看是否dubbo服务注册成功  
3、调用foreground项目中的findStudentListByPage方法，验证dubbo是否配置成功，因为项目中没有写任何页面，所以直接在浏览器中访问地址，例：  
http://localhost:8080/foreground/json/findStudentListByPage  
如果能正常返回json格式的查询信息，那么就能证明我们的dubbo已经配置成功了

注：

- dubbo-admin仅为平时测试所用
- 项目配置文件中的数据库信息为测试库，只有select权限
- 该服务器为博主平时测试demo的服务器

## 数据库脚本

这里贴上demo的数据库，并为student表初始化一些数据

student.sql

``` sql
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_student_info
-- ----------------------------
DROP TABLE IF EXISTS `t_student_info`;
CREATE TABLE `t_student_info`  (
  `stu_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `stu_number` int(11) NULL DEFAULT NULL COMMENT '学号',
  `stu_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `stu_gender` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `stu_age` int(3) NULL DEFAULT NULL COMMENT '年龄',
  PRIMARY KEY (`stu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10004 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_student_info
-- ----------------------------
INSERT INTO `t_student_info` VALUES (10001, 95001, '张三', '男', 20);
INSERT INTO `t_student_info` VALUES (10002, 95002, '李四', '男', 21);
INSERT INTO `t_student_info` VALUES (10003, 95003, '王五', '女', 22);

SET FOREIGN_KEY_CHECKS = 1;
```

demo里实现了简单的增删改查，需要的同学可以去下载，感觉还不错就给个star吧！

---------

[1]: https://blog.csdn.net/yori_chen/article/details/80404930
