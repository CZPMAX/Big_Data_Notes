# Presto - 分布式sql查询引擎

## 1. Presto--介绍

- 背景

  > 大数据分析类软件发展历程。

  ![image-20221001192428944](E:\黑马培训\Hadoop生态圈\assets\image-20221001192428944.png)

  - Apache Hadoop MapReduce 
    - 优点：统一、通用、简单的编程模型，分而治之思想处理海量数据。
    - 缺点：java学习成本、MR执行慢、内部过程繁琐
  - Apache Hive
    - 优点：==SQL on Hadoop==。sql语言上手方便。学习成本低。
    - 缺点：底层默认还是MapReduce引擎、慢、延迟高
    - hive的后续发展：改变自己的引擎  Tez Spark
  - 各种SQL类计算引擎开始出现，主要追求的就是一个问题：==怎么能计算的更快，延迟低==。
    - Spark On Hive、Spark SQL
    - Impala
    - Presto
    - ClickHouse
    - ........

- 介绍

  > Presto是一个开源的==分布式SQL查询引擎==，适用于==交互式查询==，数据量支持GB到PB字节。
  >
  > Presto的设计和编写完全是为了解决==Facebook==这样规模的商业数据仓库交互式分析和处理速度的问题。

  ![image-20211011184200604](E:\黑马培训\Hadoop生态圈\assets\image-20211011184200604.png)

  ```properties
  Presto支持在线数据查询，包括Hive、kafka、Cassandra、关系数据库以及专门数据存储;
  
  =一条Presto查询可以将多个数据源进行合并，可以跨越整个组织进行分析;
  
  Presto以分析师的需求作为目标，他们期望相应速度小于1秒到几分钟;
  
  Presto终结了数据分析的两难选择，要么使用速度快的昂贵的商业方案，要么使用消耗大量硬件的慢速的“免费”方案。
  ```

  ![image-20221001005614681](E:\黑马培训\Hadoop生态圈\assets\image-20221001005614681.png)

**Presto三级数据类型**

![5、presto的3级数据模型结构](E:\黑马培训\Hadoop生态圈\assets\5、presto的3级数据模型结构.png)

- 优缺点

  ```shell
  #优点
  1）Presto与Hive对比，都能够处理PB级别的海量数据分析，但Presto是基于内存运算，减少没必要的硬盘IO，所以更快。
  
  2）能够连接多个数据源，跨数据源连表查，如从Hive查询大量网站访问记录，然后从Mysql中匹配出设备信息。
  
  3）部署也比Hive简单，因为Hive是基于HDFS的，需要先部署HDFS。
  
  #缺点
  1）虽然能够处理PB级别的海量数据分析，但不是代表Presto把PB级别都放在内存中计算的。而是根据场景，如count，avg等聚合运算，是边读数据边计算，再清内存，再读数据再计算，这种耗的内存并不高。但是连表查，就可能产生大量的临时数据，因此速度会变慢，反而Hive此时会更擅长。
  
  2）为了达到实时查询，可能会想到用它直连MySql来操作查询，这效率并不会提升，瓶颈依然在MySql，此时还引入网络瓶颈，所以会比原本直接操作数据库要慢。
  ```

  ![img](E:\黑马培训\Hadoop生态圈\assets\Hive-vs-Presto_Horizontal.png)

---

## 2. Presto--架构、相关术语

- 架构图

  > Presto是一个运行在多台服务器上的分布式系统。 完整安装包括==一个coordinator和多个worker==。 
  > 由客户端提交查询，从Presto命令行CLI提交到coordinator; coordinator进行解析，分析并执行查询计划，然后分发处理队列到worker。

  ![image-20211011185512556](E:\黑马培训\Hadoop生态圈\assets\image-20211011185512556.png)

  ```properties
  Presto查询引擎是一个M-S的架构，由一个coordinator节点，一个Discovery Server节点，多个Worker节点组成,注意Discovery Server通常内嵌在Coordinator节点中。
  
  主角色:Coordinator负责SQL的解析，生成执行计划，分发给Worker节点进行执行;
  从角色:Worker节点负责实时查询执行任务。Worker节点启动后向discovery Server服务注册，Coordinator 从discovery server获取可以工作的Worker节点。
  
  如果配置了hive connector，需要配置hive MetaSote服务为Presto提供元信息，worker节点和HDFS进行交互数据。
  ```

  ![image-20211011185759216](E:\黑马培训\Hadoop生态圈\assets\image-20211011185759216.png)

- ==Connector== 连接器

  ```properties
  1、Presto通过Connector连接器来连接访问不同数据源，例如Hive或mysql。连接器功能类似于数据库的驱动程序。允许Presto使用标准API与资源进行交互。
  
  2、Presto包含几个内置连接器：JMX连接器，可访问内置系统表的System连接器，Hive连接器和旨在提供TPC-H基准数据的TPCH连接器。许多第三方开发人员都贡献了连接器，因此Presto可以访问各种数据源中的数据，比如：ES、Kafka、MongoDB、Redis、Postgre、Druid、Cassandra等。
  ```

- ==Catalog== 连接目录

  ```properties
  1、Presto Catalog是数据源schema的上一级，并通过连接器访问数据源。
  
  2、例如，可以配置Hive Catalog以通过Hive Connector连接器提供对Hive信息的访问。
  
  3、在Presto中使用表时，标准表名始终是被支持的。
  例如，hive.test_data.test的标准表名将引用hive catalog中test_data schema中的test table。
  Catalog需要在Presto的配置文件中进行配置。
  
  ```

- ==schema==

  ```properties
  Schema是组织表的一种方式。Catalog和Schema共同定义了一组可以查询的表。
  
  当使用Presto访问Hive或关系数据库（例如MySQL）时，Schema会转换为目标数据库中的对应Schema。
  
  =schema通俗理解就是我们所讲的database.
  =想一下在hive中，下面这两个sql是否相等。
  show databases;
  shwo schemas;
  ```

- ==table==

----

## 3. Presto--集群模式安装

- step1：集群规划

  ![image-20211011190542336](E:\黑马培训\Hadoop生态圈\assets\image-20211011190542336-166455722290920.png)

- step2：项目集群环境安装JDK

  > 已经安装好

  ```shell
  #可以手动安装oracle JDK
  
  #也可以使用yum在线安装 openjDK
  yum install java-1.8.0-openjdk* -y
  
  #安装完成后，查看jdk版本：
  java -version
  ```

- step3：上传Presto安装包（==hadoop01==）

  ```shell
  #创建安装目录
  mkdir -p /export/server
  
  
  #上传安装包到hadoop01的/export/server目录
  presto-server-0.245.1.tar.gz
  
  #解压、重命名
  cd /export/server/
  tar -xzvf presto-server-0.245.1.tar.gz
  mv presto-server-0.245.1 presto
  
  #创建配置文件存储目录
  mkdir -p /export/server/presto/etc
  ```

- step4：添加配置文件（==hadoop01==）

  - etc/config.properties

    ```properties
    cd /export/server/presto
    
    vim etc/config.properties
    
    #---------添加如下内容---------------
    coordinator=true
    node-scheduler.include-coordinator=true
    http-server.http.port=8090
    query.max-memory=4GB
    query.max-memory-per-node=2GB
    query.max-total-memory-per-node=2GB
    discovery-server.enabled=true
    discovery.uri=http://192.168.88.80:8090
    #---------end-------------------
    
    #参数说明
    coordinator:是否为coordinator节点，注意worker节点需要写false
    node-scheduler.include-coordinator:coordinator在调度时是否也作为worker
    discovery-server.enabled:Discovery服务开启功能。presto通过该服务来找到集群中所有的节点。每一个Presto实例都会在启动的时候将自己注册到discovery服务；  注意：worker节点不需要配 
    discovery.uri:Discovery server的URI。由于启用了Presto coordinator内嵌的Discovery服务，因此这个uri就是Presto coordinator的uri。
    ```

  - etc/jvm.config

    ```shell
    vim etc/jvm.config
    
    -server
    -Xmx3G
    -XX:+UseG1GC
    -XX:G1HeapRegionSize=32M
    -XX:+UseGCOverheadLimit
    -XX:+ExplicitGCInvokesConcurrent
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:+ExitOnOutOfMemoryError
    ```

  - etc/node.properties

    ```properties
    mkdir -p /export/data/presto
    vim etc/node.properties
    
    node.environment=cdhpresto
    node.id=presto-cdh01
    node.data-dir=/export/data/presto
    ```

  - etc/catalog/hive.properties

    ```properties
    mkdir -p etc/catalog
    vim etc/catalog/hive.properties
    
    connector.name=hive-hadoop2
    hive.metastore.uri=thrift://192.168.88.80:9083
    hive.max-partitions-per-writers=300
    ```

- step4：scp安装包到其他机器

  ```shell
  #在hadoop02创建文件夹
  mkdir -p /export/server
  
  #在hadoop01远程cp安装包
  cd /export/server
  scp -r presto hadoop02:$PWD
  
  #ssh的时候如果没有配置免密登录 需要输入密码scp  密码：123456
  ```

- step5：hadoop02配置修改

  - etc/config.properties

    ```properties
    cd /export/server/presto
    vim etc/config.properties
    
    #----删除之前文件中的全部内容 替换为以下的内容   vim编辑器删除命令 8dd
    coordinator=false
    http-server.http.port=8090
    query.max-memory=4GB
    query.max-memory-per-node=2GB
    query.max-total-memory-per-node=2GB
    discovery.uri=http://192.168.88.80:8090
    ```

  - etc/jvm.config

    > 和hadoop01一样，不变，唯一注意的就是如果机器内存小，需要调整-Xmx参数

    ```properties
    vim etc/jvm.config
    
    -server
    -Xmx3G
    -XX:+UseG1GC
    -XX:G1HeapRegionSize=32M
    -XX:+UseGCOverheadLimit
    -XX:+ExplicitGCInvokesConcurrent
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:+ExitOnOutOfMemoryError
    ```

  - etc/node.properties

    > 修改编号node.id

    ```properties
    mkdir -p /export/data/presto
    vim etc/node.properties
    
    node.environment=cdhpresto
    node.id=presto-cdh02
    node.data-dir=/export/data/presto
    ```

  - etc/catalog/hive.properties

    > 保持不变

    ```properties
    vim etc/catalog/hive.properties
    
    connector.name=hive-hadoop2
    hive.metastore.uri=thrift://192.168.88.80:9083
    hive.max-partitions-per-writers=300
    ```

----

## 4. Presto--集群启停

> ==**注意，每台机器都需要启动**==

- 前台启动

  ```shell
  [root@hadoop01 ~]# cd ~
  [root@hadoop01 ~]# /export/server/presto/bin/launcher run
  
  
  [root@hadoop02 ~]# cd ~
  [root@hadoop02 ~]# /export/server/presto/bin/launcher run
  
  
  #如果出现下面的提示 表示启动成功
  2021-09-15T18:24:21.780+0800    INFO    main    com.facebook.presto.server.PrestoServer ======== SERVER STARTED ========
  
  #前台启动使用ctrl+c进行服务关闭
  ```

- 后台启动

  ```shell
  [root@hadoop01 ~]# cd ~
  [root@hadoop01 ~]# /export/server/presto/bin/launcher start
  Started as 89560
  
  [root@hadoop02 ~]# cd ~
  [root@hadoop02 ~]# /export/server/presto/bin/launcher start
  Started as 92288
  
  
  #查看进程是否启动成功
  PrestoServer
  
  #后台启动使用jps 配合kill -9命令 关闭进程
  
  #也可以是这样关闭
  /export/server/presto/bin/launcher stop
  ```

- web UI页面

  http://192.168.88.80:8090/ui/

  ![image-20211011192331988](E:\黑马培训\Hadoop生态圈\assets\image-20211011192331988.png)

- 启动日志 --> 意思是可以通过这些文件来查看数据

  ```shell
  #日志路径：/export/data/presto/var/log/
  
  http-request.log
  launcher.log
  server.log
  
  ```

------

## 5. Presto--命令行客户端

- 下载CLI客户端

  ```shell
  presto-cli-0.241-executable.jar
  ```

- 上传客户端到Presto安装包

  ```shell
  #上传presto-cli-0.245.1-executable.jar到/export/server/presto/bin
  cd /export/server/presto/bin
  mv presto-cli-0.245.1-executable.jar presto
  chmod +x presto
  ```

- CLI客户端启动

  ```shell
  /export/server/presto/bin/presto --server hadoop01:8090 --catalog hive --schema userdb
  ```

- ctrl+D 退出客户端

## 6. Presto--Datagrip连接使用

> - JDBC 驱动：==presto-jdbc-0.245.1.jar==
> - JDBC 地址：==jdbc:presto://192.168.88.80:8090/hive==

- 新版datagrip直接在数据源其他那里选择presto数据源连接

**接着进入配置主页面，完成以下配置**

presto连接的三级目录 1. catalog 连接目录   2. schema数据库目录   3. table表目录 因为这一特性所以我们要指定

它的database的值，这个配置影响我们是连接hive数据库还是mysql数据库，还是其他数据库

==图示流程如下==

![image-20221001190553383](E:\黑马培训\Hadoop生态圈\assets\image-20221001190553383.png)

![a.datagrip如何连接presto--1](E:\黑马培训\Hadoop生态圈\assets\a.datagrip如何连接presto--1.png)

![b.datagrip如何连接presto--2](E:\黑马培训\Hadoop生态圈\assets\b.datagrip如何连接presto--2.png)

- step1：配置驱动

  ![image-20211011192825583](E:\黑马培训\Hadoop生态圈\assets\image-20211011192825583.png)

  ![image-20211011192905382](E:\黑马培训\Hadoop生态圈\assets\image-20211011192905382.png)

- step2：创建连接

  ![image-20211011193034508](E:\黑马培训\Hadoop生态圈\assets\image-20211011193034508.png)

  ![image-20211011193046337](E:\黑马培训\Hadoop生态圈\assets\image-20211011193046337.png)

  ![image-20211011193059716](E:\黑马培训\Hadoop生态圈\assets\image-20211011193059716.png)

- step3：测试体验

  ![image-20211011193404087](E:\黑马培训\Hadoop生态圈\assets\image-20211011193404087.png)

  ![image-20211011193414445](E:\黑马培训\Hadoop生态圈\assets\image-20211011193414445.png)

  ![image-20211011193433564](E:\黑马培训\Hadoop生态圈\assets\image-20211011193433564.png)

  ![image-20211011193445232](E:\黑马培训\Hadoop生态圈\assets\image-20211011193445232.png)

  ![image-20211011193743449](E:\黑马培训\Hadoop生态圈\assets\image-20211011193743449.png)

---

## 7. Presto--时间日期类型注意事项

在presto中使用date_format函数 第一个参数就是要处理的对象 都要事先声明这个对象是哪一类的 

- ==date_format==(timestamp, format)  ==> varchar 

  * 作用: 将指定的日期对象转换为字符串操作

- ==date_parse==(string, format) → timestamp 

  * 作用: 用于将字符串的日期数据转换为日期对象

  ```sql
  select date_format( timestamp '2020-10-10 12:50:50' , '%Y/%m/%d %H:%i:%s');
                     
                     -- 声明 后面这个字符串是timestamp类型的对象
  
  
  select date_format( date_parse('2020:10:10 12-50-50','%Y:%m:%d %H-%i-%s') ,'%Y/%m/%d %H:%i:%s');
                                  -- 没有声明单单是个字符串
  
  
  date_parse('2020:10:10 12-50-50','%Y:%m:%d %H-%i-%s') 
  -- 这个函数的使用 第一个参数是字符串  第二个参数写的格式要与第一个参数的格式一样 
  -- 最后转化输出的结果还是2020:10:10 12-50-50，但是他的数据类型已经从字符串转化为日期对象了
  -- 这样一转化告诉了presto这是一个标准的日期格式对象 ，接着就可以使用date_format函数把他转化为我们需要是数据类型了
  
  
  -- 大括号的第二个参数都是  我们自己设置的想要输出的日期的格式
  
  ----
  注意: 参数一必须是日期对象
  	所以如果传递的是字符串, 必须将先转换为日期对象:  
  		方式一:  标识为日期对象, 但是格式必须为标准日期格式
  			timestamp '2020-10-10 12:50:50'
  			date '2020-10-10'
  			-- 当在声明了这些日期的具体类型后 后面的时间格式就必须是它本身的标准时间格式
  			-- 当我们利用date_prase函数解析一个字符串后，转化为一个时间日期对象
  			-- 这个对象的标准就在第二个参数写着，这样一来一个字符串就被转化为一个标准的日期格式
  			-- 可以进行接着的format操作
  			
  		方式二: 如果不标准，先用date_parse解析成为标准 标准的格式就在后面指定着 第二个参数要与第一个参数格式一致  相当于自己指定标准了
  			date_parse('2020-10-10 12:50:50','%Y-%m-%d %H:%i:%s')  
  			date_parse('2020|10|10 12-50-50','%Y|%m|%d %H-%i-%s')
  
  扩展说明: 日期format格式说明
  	年：%Y
  	月：%m
  	日：%d
  	时：%H
  	分：%i
  	秒：%s
  	周几：%w(0..6)	
  ```

  ==实操==

  ```sql
  -- 指定了前面的字符串类型数据转化成日期类型的对象，并指定了它的标准日期格式
  -- 接下来就可以转化成我们需要的格式了
  -- date_format函数第一个参数必须是标准日期类型的对象 这个标准可以是本来就存在的标准 也能是使用date_parse函数转化出来的标准
  
  select date_format(date_parse('2021:03:06 14-23-03', '%Y:%m:%d %H-%i-%s'), '%Y-%m-%d %H:%i:%s' )
  ```

  ![image-20221001232820928](E:\黑马培训\Hadoop生态圈\assets\image-20221001232820928.png)

- ==date_add==(unit, value, timestamp) → [same as input]

  * 作用: 用于对日期数据进行 加 减 操作

- ==date_diff==(unit, timestamp1, timestamp2) → bigint

  - 作用: 用于比对两个日期之间差值

  ```sql
  select  date_add('hour',3,timestamp '2021-09-02 15:59:50');
  select  date_add('day',-1,timestamp '2021-09-02 15:59:50');
  select  date_add('month',-1,timestamp '2021-09-02 15:59:50');
  
  
  select date_diff('year',timestamp '2020-09-02 06:30:30',timestamp '2021-09-02 15:59:50')
  select date_diff('month',timestamp '2021-06-02 06:30:30',timestamp '2021-09-02 15:59:50')
  select date_diff('day',timestamp '2021-08-02 06:30:30',timestamp '2021-09-02 15:59:50')
  ```

-----

## 8. Presto--常规优化

- 数据存储优化

  ```sql
  --1）合理设置分区
  	与Hive类似，Presto会根据元信息读取分区数据，合理的分区能减少Presto数据读取量，提升查询性能。
  
  --2）使用列式存储
  	Presto对ORC文件读取做了特定优化，因此在Hive中创建Presto使用的表时，建议采用ORC格式存储。相对于Parquet，Presto对ORC支持更好。
  	Parquet和ORC一样都支持列式存储，但是Presto对ORC支持更好，而Impala对Parquet支持更好。在数仓设计时，要根据后续可能的查询引擎合理设置数据存储格式。
  
  --3）使用压缩
  	数据压缩可以减少节点间数据传输对IO带宽压力，对于即席查询需要快速解压，建议采用Snappy压缩。
  
  --4）预先排序
  	对于已经排序的数据，在查询的数据过滤阶段，ORC格式支持跳过读取不必要的数据。比如对于经常需要过滤的字段可以预先排序。
  
  INSERT INTO table nation_orc partition(p) SELECT * FROM nation SORT BY n_name;
  如果需要过滤n_name字段，则性能将提升。
  
  SELECT count(*) FROM nation_orc WHERE n_name=’AUSTRALIA’; 
  
  ```

- SQL优化

  - 列裁剪
  - 分区裁剪
  - group by优化
    - 按照数据量大小降序排列
  - ==order by使用limit==
  - ==用regexp_like代替多个like语句==
  - ==join时候大表放置在左边==

- 替换非ORC格式的Hive表

----

## 9. Presto--内存调优

- 内存管理机制--内存分类

  > Presto管理的内存分为两大类：==user memory==和==system memory==

  - user memory用户内存

    ```
    跟用户数据相关的，比如读取用户输入数据会占据相应的内存，这种内存的占用量跟用户底层数据量大小是强相关的
    ```

  - system memory系统内存

    ```
    执行过程中衍生出的副产品，比如tablescan表扫描，write buffers写入缓冲区，跟查询输入的数据本身不强相关的内存。
    ```

- 内存管理机制--内存池

  > ==内存池中来实现分配user memory和system memory==。
  >
  > 内存池为常规内存池GENERAL_POOL、预留内存池RESERVED_POOL。

  ![image-20211011201539017](E:\黑马培训\Hadoop生态圈\assets\image-20211011201539017.png)

  ```properties
  1、GENERAL_POOL:在一般情况下，一个查询执行所需要的user/system内存都是从general pool中分配的，reserved pool在一般情况下是空闲不用的。
  
  2、RESERVED_POOL:大部分时间里是不参与计算的，但是当集群中某个Worker节点的general pool消耗殆尽之后，coordinator会选择集群中内存占用最多的查询，把这个查询分配到reserved pool，这样这个大查询自己可以继续执行，而腾出来的内存也使得其它的查询可以继续执行，从而避免整个系统阻塞。
  
  注意:
  reserved pool到底多大呢？这个是没有直接的配置可以设置的，他的大小上限就是集群允许的最大的查询的大小(query.total-max-memory-per-node)。
  reserved pool也有缺点，一个是在普通模式下这块内存会被浪费掉了，二是大查询可以用Hive来替代。因此也可以禁用掉reserved pool（experimental.reserved-pool-enabled设置为false），那系统内存耗尽的时候没有reserved pool怎么办呢？它有一个OOM Killer的机制，对于超出内存限制的大查询SQL将会被系统Kill掉，从而避免影响整个presto。
  
  
  OOM （Out	Of MemoryError）内存不足  内存使用量溢出 超出最大范围了
  ```

- 内存相关参数

  ![image-20211011201802703](E:\黑马培训\Hadoop生态圈\assets\image-20211011201802703.png)

  ```properties
  1、user memory用户内存参数
  query.max-memory-per-node:单个query操作在单个worker上user memory能用的最大值
  query.max-memory:单个query在整个集群中允许占用的最大user memory
  
  2、user+system总内存参数
  query.max-total-memory-per-node:单个query操作可在单个worker上使用的最大(user + system)内存
  query.max-total-memory:单个query在整个集群中允许占用的最大(user + system) memory
  
  当这些阈值被突破的时候，query会以insufficient memory（内存不足）的错误被终结。
  
  3、协助阻止机制
  在高内存压力下保持系统稳定。当general pool常规内存池已满时，操作会被置为blocked阻塞状态，直到通用池中的内存可用为止。此机制可防止激进的查询填满JVM堆并引起可靠性问题。
  
  4、其他参数
  memory.heap-headroom-per-node:这个内存是JVM堆中预留给第三方库的内存分配，presto无法跟踪统计，默认值是-Xmx * 0.3
  
  5、结论
  GeneralPool = 服务器总内存 - ReservedPool - memory.heap-headroom-per-node - Linux系统内存
  
  常规内存池内存大小=服务器物理总内存-服务器linux操作系统内存-预留内存池大小-预留给第三方库内存
  ```

- 内存优化建议

  - 常见的报错解决

    > total memory= user memory +system

    ```properties
    1、Query exceeded per-node total memory limit of xx
    适当增加query.max-total-memory-per-node。
    
    2、Query exceeded distributed user memory limit of xx
    适当增加query.max-memory。
    
    3、Could not communicate with the remote task. The node may have crashed or be under too much load
    内存不够，导致节点crash，可以查看/var/log/message。
    ```

  - 建议参数设置

    ```properties
    1、query.max-memory-per-node和query.max-total-memory-per-node是query操作使用的主要内存配置，因此这两个配置可以适当加大。
    memory.heap-headroom-per-node是三方库的内存，默认值是JVM-Xmx * 0.3，可以手动改小一些。
    
    1) 各节点JVM内存推荐大小: 当前节点剩余内存*80%
    
    2) 对于heap-headroom-pre-node第三方库的内存配置: 建议jvm内存的%15左右
    
    3) 在配置的时候, 不要正正好好, 建议预留一点点, 以免出现问题
    
    数据量在35TB , presto节点数量大约在30台左右 (128GB内存 + 8核CPU)   
    
    注意：
    1、query.max-memory-per-node小于query.max-total-memory-per-node。
    2、query.max-memory小于query.max-total-memory。
    3、query.max-total-memory-per-node 与memory.heap-headroom-per-node 之和必须小于 jvm max memory，也就是jvm.config 中配置的-Xmx。
    ```

    

------

## 10. Hive Map join优化

- Map Side Join 

  ```shell
  set hive.auto.convert.join=true;
  
  #如果参与连接的N个表(或分区)中的N-1个的总大小小于512MB，则直接将join转为Map端join,默认值为20MB
  set hive.auto.convert.join.noconditionaltask.size=512000000;
  ```

- ==Bucket-Map== Join

  ```shell
  1）	set hive.optimize.bucketmapjoin = true;
  
  2） 一个表的bucket数是另一个表bucket数的整数倍
  
  3） bucket分桶字段 == join的字段
  ```

- Sort Merge Bucket Join（==SMB Join==）

  > SMB是针对Bucket Map Join的一种优化。条件类似却有些不一样。

  ```shell
  1）
  	set hive.optimize.bucketmapjoin = true;
  	set hive.auto.convert.sortmerge.join=true;
  	set hive.optimize.bucketmapjoin.sortedmerge = true;
  	set hive.auto.convert.sortmerge.join.noconditionaltask=true;
  	
  2）	
  	Bucket 列 == Join 列 == sort 列
  	
  	#hive并不检查两个join的表是否已经做好bucket且sorted，需要用户自己去保证join的表数据sorted， 否则可能数据不正确。
  	
  3）
  	bucket数相等
  	
  
  #注意：
  	a、可以设置参数hive.enforce.sorting 为true，开启强制排序。插数据到表中会进行强制排序。
  	b、表创建时必须是CLUSTERED BY+SORTED BY
  ```

  

  













