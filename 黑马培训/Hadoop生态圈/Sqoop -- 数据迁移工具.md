# Apache Sqoop

## 1. Sqoop 介绍、工作机制

![image-20220928201703548](E:\黑马培训\Hadoop生态圈\assets\image-20220928201703548.png)

- Sqoop介绍 == > sqoop  sql+hadoop

  ```properties
  sqoop是apache旗下一款“Hadoop和关系数据库服务器之间传送数据”的工具。
  导入数据：MySQL，Oracle导入数据到Hadoop的HDFS、HIVE、HBASE等数据存储系统；
  导出数据：从Hadoop的HDFS、HIVE中导出数据到关系数据库mysql等。
  
  
  Hadoop 生态系统包括：HDFS、Hive、Hbase 等
  RDBMS 体系包括：Mysql、Oracle、DB2 等
  Sqoop 可以理解为：“SQL 到 Hadoop 和 Hadoop 到 SQL”。
  
  ```

  ![image-20211005184736673](E:\黑马培训\Hadoop生态圈\assets\image-20211005184736673.png)

站在 Apache 立场看待数据流转问题，可以分为数据的导入导出: 

- Import：数据导入。

RDBMS----->Hadoop 

- Export：数据导出。

Hadoop---->RDBMS

- Sqoop工作机制

  

  ```
  Sqoop工作机制是将导入或导出命令翻译成mapreduce程序来实现。
  在翻译出的mapreduce中主要是对inputformat和outputformat进行定制。
  
  DBInputFormat  DBOutPutformat
  ```

  ![image-20211005184826460](E:\黑马培训\Hadoop生态圈\assets\image-20211005184826460.png)

- sqoop安装、测试

  > ==项目的环境中sqoop是安装在hadoop01机器上的==

  ```shell
  sqoop list-databases --connect jdbc:mysql://hadoop200:3306/ --username root --password 123456
  
  #sqoop要求命令及参数 必须写在一行 如果遇到回车换行符 就自动提交
  
  sqoop list-databases
  --connect jdbc:mysql://localhost:3306/
  --username root
  --password 123456
  
  
  #也可以这么写  \表示命令未完待续 下一行还有命令参数  否则遇到回车换行就会自动提交执行
  sqoop list-databases \
  --connect jdbc:mysql://hadoop01:3306/ \
  --username root \
  --password 123456
  
  sqoop list-databases \
  --connect jdbc:mysql://localhost:3306/ \
  --username root \
  --password 123456
  
  sqoop list-databases \
  --connect jdbc:mysql://127.0.0.1:3306/ \
  --username root \
  --password 123456
  
  #上述3个效果一样 实际中推荐使用第一种方式  就是以IP或者主机名表示你要访问的机器。
  #127.0.0.1 ==localhost
  #能够写localhost的前提 就是你也在这台机器上  你的服务是本地的服务。
  ```

  - 细节1：sqoop命令要求在一行写完，如遇回车换行就自动提交，可以通过==\表示未完待续==；
  - 细节2：localhost代表什么？  代表的是本机的意思，就是执行命令所在的机器。
    - 如果你在hadoop01上执行sqoop命令，localhost代表的就是hadoop01。

----

## 2. 增量数据、全量数据

- ==全量数据（Full data）==

  > 就是全部数据，所有数据。如对于表来说，就是表中的所有数据。

- ==增量数据（Incremental data）==

  > 就是上次操作之后至今产生的新数据。

- 数据子集

  > 也叫做部分数据。整体当中的一部分。

----

## 3. Sqoop数据导入至HDFS

![image-20220926223632145](E:\黑马培训\Hadoop生态圈\assets\image-20220926223632145.png)

如果导入的数据量非常的大，这时候导入的是表，为了节省时间，那么现在就要进

行对表的分割，也就是相当于切片的操作，进而把一个任务分为多个小任务执行，

也就是调整了并行度，让一个任务拆分成多个小任务执行。

> sqoop导出，存放数据的目录不能提前存在



- 测试数据准备

  ![image-20211005211142826](E:\黑马培训\Hadoop生态圈\assets\image-20211005211142826.png)

```shell
#提前将sql文件上传到mysql所在的服务器 这里hadoop01机器


# 首先在node1上登录mysql  密码是123456
[root@hadoop01 ~]# mysql -u root -p
Enter password: 

mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| hive               |
| hue                |
| mysql              |
| oozie              |
| performance_schema |
| scm                |
| sys                |
+--------------------+
8 rows in set (0.00 sec)

#在mysql命令行中 创建一个数据库 名字叫做userdb
mysql> create database userdb;
Query OK, 1 row affected (0.00 sec)

#切换库
mysql> use userdb;
Database changed

##使用source命令加载执行sql文件。  
mysql> source /root/userdb.sql;
Query OK, 0 rows affected (0.00 sec)


Q:如何在linux上执行mysql sql脚本？
source +sql文件路径
```



- ==全量==导入MySQL数据到HDFS

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb --这是数据库名称 \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result1 \
  --table emp --m 1
  
  
  #sqoop把数据导入到HDFS  默认字段之间分隔符是,
  
  #如果输出路径已经存在 执行报错  怎么办？ --delete-target-dir
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result1 \
  --delete-target-dir \
  --table emp --m 1
  ```

- 指定分隔符

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result2 \
  --fields-terminated-by '\001' \
  --table emp --m 1
  
  #--fields-terminated-by 可以用于指定字段之间的分隔符
  
  ```

- 指定任务并行度（maptask个数）

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result3 \
  --table emp --m 2
  
  
  
  #上述执行报错 错误信息如下
  Import failed: No primary key could be found for table emp. Please specify one with --split-by or perform a sequential import with '-m 1'.
  
  
  
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result3 \
  --fields-terminated-by '\t' \
  --split-by id \
  --table emp --m 2
  
  #请结合一下的日志信息感受如何进行切片的
  BoundingValsQuery: SELECT MIN(`id`), MAX(`id`) FROM `emp`
  Split size: 2; Num splits: 2 from: 1201 to: 1205
  mapreduce.JobSubmitter: number of splits:2
  
  #下面这个命令是错误的  没有指定切割的判断依据
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result3 \
  --fields-terminated-by '\t' \
  --table emp --m 2
  
  #扩展知识点
  关于mr输出的结果文件名称
  
  part-r-00000  r表示reducetask 说明这个mr程序是一个标准的两个阶段的程序
  part-m-00000  m表示maptask   说明这个mr是一个只有map阶段没有reduce阶段的程序
  ```

----

## 4. Sqoop数据导入至Hive

- 测试准备

  ```sql
  --Hive中创建测试使用的数据库
  create database test;
  ```

- 方式一：先复制表结构、再导入数据

  ```shell
  #将关系型数据的表结构复制到hive中
  sqoop create-hive-table \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --table emp_add \
  --username root \
  --password 123456 \
  --hive-table test.emp_add_sp
  # 意思解释
  在hive中test这个数据库下，把mysql中的emp_add这个表创建出来，hive这个表的表名是emp_add_sp
  
  #其中 
  --table emp_add为mysql中的数据库sqoopdb中的表   
  --
  --hive-table test.emp_add_sp 为在hive的test库中新建一个表emp_add_sp。
  如不指定库名，将会在hive的default库下创建emp_add_sp
  如果不指定表名，将会在test库下创建与数据来源表相同名称的表
  ```

  可以在Hive中查看表结构信息

  ```sql
  desc formatted emp_add_sp;
  ```

  ![image-20211005210311530](E:\黑马培训\Hadoop生态圈\assets\image-20211005210311530.png)

  > 可以发现此时表的很多属性都是采用默认值来设定的。

  然后执行数据导入操作

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table emp_add \
  --hive-table test.emp_add_sp \
  --hive-import \
  --m 1
  
  sqoop import "-Dorg.apache.sqoop.splitter.allow_text_splitter=true" \
  --connect 'jdbc:mysql://106.75.33.59:3306/scrm?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true' \
  --username itcast_edu_stu \
  --password itcast_edu_stu \
  --table customer_relationship \
  --query "select * ,'2022-10-13' as dt from customer_relationship where 1=1 and  \$CONDITIONS" \
  --hive-table zx_ods.customer_relationship \
  --hive-import \
  -m 1
  ```

- 方式二：直接导入数据（包括建表）

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table emp_conn \
  --hive-import \
  --m 1 \
  --hive-database test
  ```

- 总结

  > 实际工作中通常采用自己在hive中建表 只使用sqoop导入数据操作。
  >
  > 理由是自己建表可以==精准的控制表的各种属性 包括分隔符 序列化类  存储格式 是否压缩==。

----

## 5. Sqoop数据导入至Hive--HCatalog API

- sqoop API 原生方式

  > 所谓sqoop原生的方式指的是sqoop自带的参数完成的数据导入。
  >
  > 但是有什么不好的地方呢？请看下面案例

  ```sql
  --手动在hive中建一张表
  create table test.emp_hive(id int,name string,deg string,salary int ,dept string) 
  row format delimited fields terminated by '\t'
  stored as orc;
  --注意，这里指定了表的文件存储格式为ORC。
  --从存储效率来说，ORC格式胜于默认的textfile格式。
  ```

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table emp \
  --fields-terminated-by '\t' \
  --hive-database test \
  --hive-table emp_hive \
  -m 1
  
  #注意，因为hive的表是用户自己创建的，并且建表中的分隔符是\t制表符
  #而sqoop默认导入hive中分隔符是\001 因此需要在导入数据的时候使用参数--fields-terminated-by 修改分隔符
  ```

  > 执行之后，可以发现虽然针对表emp_hive的sqoop任务成功，但是==Hive表中却没有数据==。

  ![image-20211005212152604](E:\黑马培训\Hadoop生态圈\assets\image-20211005212152604.png)

  ![image-20211005212245412](E:\黑马培训\Hadoop生态圈\assets\image-20211005212245412.png)

- ==HCatalog== API方式

  > Apache HCatalog是基于Apache Hadoop之上的数据表和存储管理服务。
  >
  > 包括：
  >
  > - 提供一个共享的模式和数据类型的机制。
  > - 抽象出表，使用户不必关心他们的数据怎么存储，底层什么格式。
  > - 提供可操作的跨数据处理工具，如Pig，MapReduce，Streaming，和Hive。

  sqoop的官网也做了相关的描述说明，使用HCatalog支持ORC等数据格式。

  ![image-20211005212545730](E:\黑马培训\Hadoop生态圈\assets\image-20211005212545730.png)

  ```shell
  #HCatalog API
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table emp \
  --fields-terminated-by '\t' \
  --hcatalog-database test \
  --hcatalog-table emp_hive \
  -m 1
  
  #sqoop原生的API
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table emp \
  --fields-terminated-by '\t' \
  --hive-database test \
  --hive-table emp_hive \
  -m 1
  ```

  > 可以发现数据导入成功，并且底层是使用ORC格式存储的。

  

- sqoop原生API和 HCatalog区别

  ```shell
  #数据格式支持（这是实际中使用HCatalog的主要原因，否则还是原生的灵活一些）
  	Sqoop方式支持的数据格式较少;
  	HCatalog支持的数据格式多，包括RCFile, ORCFile, CSV, JSON和SequenceFile等格式。
  
  #数据覆盖
  	Sqoop方式允许数据覆盖，HCatalog不允许数据覆盖，每次都只是追加。
  
  #字段名匹配
  	Sqoop方式比较随意，不要求源表和目标表字段相同(字段名称和个数都可以不相同)，它抽取的方式是将字段按顺序插入，比如目标表有3个字段，源表有一个字段，它会将数据插入到Hive表的第一个字段，其余字段为NULL。
  	但是HCatalog不同，源表和目标表字段名需要相同，字段个数可以不相等，如果字段名不同，抽取数据的时候会报NullPointerException错误。HCatalog抽取数据时，会将字段对应到相同字段名的字段上，哪怕字段个数不相等。
  ```

- ==**结论**==

  > 虽然sqoop支持很多种方式把数据从mysql导入到hive中，但是，实际中用的最多的是：
  >
  > 1、用户自己手动在hive中建表，并且根据自己的需求指定存储格式、分隔符、是否压缩等属性
  >
  > 2、使用sqoop新的API---Hcatalog API将输入导入到hive表中。

---

## 6. Sqoop数据导入--条件部分导入

> 所谓的条件部分指的就是==数据的子集==。全量数据一部分。

- where过滤

  > 功能类似于sql语法中where 可以实现过滤

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --where "city ='sec-bad'" \
  --target-dir /sqoop/result4 \
  --table emp_add --m 1
  ```

  ![image-20211005213141440](E:\黑马培训\Hadoop生态圈\assets\image-20211005213141440.png)

- query查询

  > query参数支持在sqoop中写sql查询语句 把查询语句的结果采集过来。

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result5 \
  --query 'select id,name,deg from emp WHERE  id>1203 and $CONDITIONS' \
  --fields-terminated-by '\001' \
  --m 1
  
  
  #可以在query sql中使用函数对数据进行处理
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result6 \
  --query 'select id,substring(name,1,3),deg from emp WHERE  id>1203 and $CONDITIONS' \
  --fields-terminated-by '\001' \
  --m 1
  
  
  
  #如果想全量抽取 怎么做
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --target-dir /sqoop/result5 \
  --query 'select * from emp WHERE 1=1 and $CONDITIONS' \
  --fields-terminated-by '\001' \
  --m 1
  
  #如果想增量抽取 也是可以滴
  ```

  > 使用sql query语句来进行查找时，==不能加参数--table==；
  >
  > 并且==必须要添加where条件==；
  >
  > 并且where条件后面==必须带一个$CONDITIONS==这个字符串；
  >
  > 并且这个sql语句==必须用单引号==，不能用双引号。

  ![image-20211005213336317](E:\黑马培训\Hadoop生态圈\assets\image-20211005213336317.png)

-----

## 7. Sqoop数据导入--增量导入

- 方式一：sqoop自带参数实现

  > 设计思路：对某一列值进行判断，只要大于上一次的值就会导入。
  >
  > 所谓的增量实现，肯定需要一个判断的依据，上次到哪里了，这次从哪里开始。

  ```shell
     --check-column <column>        Source column to check for incremental
                                    change
     --incremental <import-type>    Define an incremental import of type
                                    'append' or 'lastmodified'
     --last-value <value>           Last imported value in the incremental
                                    check column
     
   #增量导入3个相关参数
    	--check-column：以哪一列的值作为增量的基准
  	--last-value：指定上一次这一列的值是什么
  	--incremental：指定增量的方式
     		- append模式
     		- lastmodified模式
  ```

  

  - ==append==模式

    > - 要求：必须有一列自增的值，按照==自增的int值==进行判断
    > - 特点：只能导入增加的数据，**无法导入更新的数据**

    ```shell
    #首先执行以下指令先将我们之前的数据导入
    sqoop import \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --target-dir /sqoop/appendresult \
    --table emp --m 1
    
    #查看生成的数据文件，发现数据已经导入到hdfs中.
    
    #然后在mysql的emp中插入2条数据:
    insert into `userdb`.`emp` (`id`, `name`, `deg`, `salary`, `dept`) values ('1206', 'allen', 'admin', '30000', 'tp');
    insert into `userdb`.`emp` (`id`, `name`, `deg`, `salary`, `dept`) values ('1207', 'woon', 'admin', '40000', 'tp');
    
    #执行如下的指令，实现增量的导入:
    sqoop import \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --table emp --m 1 \
    --target-dir /sqoop/appendresult \
    --incremental append \
    --check-column id \
    --last-value 1205
    
    
    ####如果想实现sqoop自动维护增量记录  可以使用sqoop job作业来实现
    21/10/09 15:03:37 INFO tool.ImportTool:  --incremental append
    21/10/09 15:03:37 INFO tool.ImportTool:   --check-column id
    21/10/09 15:03:37 INFO tool.ImportTool:   --last-value 1207
    21/10/09 15:03:37 INFO tool.ImportTool: (Consider saving this with 'sqoop job --create')
    ```

    ![image-20211005214836575](E:\黑马培训\Hadoop生态圈\assets\image-20211005214836575.png)

    > 并且还可以结合sqoop job作业，实现sqoop自动记录维护last-value值，详细可以参考课程资料。

    ![image-20211005214755032](E:\黑马培训\Hadoop生态圈\assets\image-20211005214755032.png)

  - lastmodifield模式

    > - 要求：==**必须包含动态时间变化这一列**==，按照数据变化的时间进行判断
    > - 特点：既导入新增的数据也导入更新的数据

    ```shell
    # 首先我们要在mysql中创建一个customer表，指定一个时间戳字段
    create table customertest(id int,name varchar(20),last_mod timestamp default current_timestamp on update current_timestamp);
    
    #此处的时间戳设置为在数据的产生和更新时都会发生改变. 
    
    #插入如下记录:
    insert into customertest(id,name) values(1,'neil');
    insert into customertest(id,name) values(2,'jack');
    insert into customertest(id,name) values(3,'martin');
    insert into customertest(id,name) values(4,'tony');
    insert into customertest(id,name) values(5,'eric');
    
    #此时执行sqoop指令将数据导入hdfs:
    sqoop import \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --target-dir /sqoop/lastmodifiedresult \
    --table customertest --m 1
    
    
    #再次插入一条数据进入customertest表
    insert into customertest(id,name) values(6,'james');
    #更新一条已有的数据，这条数据的时间戳会更新为我们更新数据时的系统时间.
    update customertest set name = 'Neil' where id = 1;
    
    
    #执行如下指令，把id字段作为merge-key:
    sqoop import \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --table customertest \
    --target-dir /sqoop/lastmodifiedresult \
    --check-column last_mod \
    --incremental lastmodified \
    --last-value "2022-09-23 15:23:32" \
    --m 1 \
    --merge-key id 
    
    #由于merge-key这种模式是进行了一次完整的mapreduce操作，
    #因此最终我们在lastmodifiedresult文件夹下可以发现id=1的name已经得到修改，同时新增了id=6的数据、
    #合并时，把增量表中id=1的数据，直接覆盖原表id=1的数据
    #合并：表中的数据发生更新，会产生多个相同编号的数据，这时候我们只要保留，最新的数据即可
    ```

==图示==

![4、sqoop增量导入--lastmodified](E:\黑马培训\Hadoop生态圈\assets\4、sqoop增量导入--lastmodified.png)

这个流程有两个mr，第一个mr把增量的数据采集过来，第二个mr将增量的数据与旧的数据进行合并

- 方式二：==用户条件过滤实现==

  > - 通过where对字段进行过滤
  > - 指定分区目录

  ```shell
  sqoop import \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --query "select * from emp where id>1203 and  \$CONDITIONS" \
  --fields-terminated-by '\001' \
  --hcatalog-database test \
  --hcatalog-table emp_hive \
  -m 1
  ```

---

==图示==

![5、sqoop增量导入--自定义查询条件实现【重要】](E:\黑马培训\Hadoop生态圈\assets\5、sqoop增量导入--自定义查询条件实现【重要】.png)

## 8. Sqoop数据导出

> sqoop导出操作最大的特点是，==目标表需要自己手动提前创建==。

- 全量数据导出

  ```shell
  #step1:MySQL中建表
  mysql> use userdb;
  mysql> create table employee ( 
     id int not null, 
     name varchar(20), 
     deg varchar(20),
     salary int,
     dept varchar(10));
     
  #step2:从HDFS导出数据到MySQL
  sqoop export \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table employee \
  --export-dir /sqoop/result1/ #数据从hdfs中的哪个目录导出
  
  
  sqoop export \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table employee \
  --export-dir /sqoop/result2/
  
  #sqoop底层是通过MapReduce程序来执行的。
  1、如果执行sqoop命令 立即报错    
  不用怀疑  sqoop命令语法格式错误  查询sqoop的语法手册
  
  2、如果执行sqoop  在MapReduce执行过程中  执行结束之前出错
  MapReduce执行的问题---->如何去看MapReduce的执行日志？
  
  首先想到的是去yarn上查看日志 但是MapReduce已经运行结束，此时日志只能通过jobhistory服务查看。
  
  3、自己手动启动jobhistory服务（在hadoop01机器）
  
  mapred --daemon start historyserver
  
  4、登录jobhistory页面
  http://hadoop01:19888/jobhistory
  
  
  #启动jobhistory服务 日志显示java.lang.RuntimeException: Can't parse input data: '1205kranthiadmin20000TP' 
  解析数据失败  失败的原因在于HDFS上sqoop默认分隔符是, 而数据中是\001 所以需要指定分隔符。
  
  
  sqoop export \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table employee \
  --input-fields-terminated-by '\001' \
  --export-dir /sqoop/result2/
  
  
  
  #step3:从Hive导出数据到MySQL
  #首先清空MySQL表数据
  truncate table employee;
  
  
  sqoop export \
  --connect jdbc:mysql://192.168.88.80:3306/userdb \
  --username root \
  --password 123456 \
  --table employee \    -- 这里是目标表
  
  --hcatalog-database test \
  --hcatalog-table emp_hive \ -- 这里是数据来源
  --input-fields-terminated-by '\t' \
  -m 1 
  
  #注意，如果Hive中的表底层是使用ORC格式存储的，那么必须使用hcatalog API进行操作
  #     如果是textfile文本格式存储的数据，那么使用 hive-database 与 hive-table即可。
  ```

- 增量数据导出

  > - updateonly：只增量导出更新的数据
  > - allowerinsert：既导出更新的数据，也导出新增的数据

  - updateonly模式

    ```shell
    #在HDFS文件系统中/sqoop/updateonly/目录的下创建一个文件updateonly_1.txt
    hadoop fs -mkdir -p /sqoop/updateonly/
    hadoop fs -put updateonly_1.txt /sqoop/updateonly/
    
    1201,gopal,manager,50000
    1202,manisha,preader,50000
    1203,kalil,php dev,30000
    
    #手动创建mysql中的目标表
    mysql> USE userdb;
    mysql> CREATE TABLE updateonly ( 
       id INT NOT NULL, 
       name VARCHAR(20), 
       deg VARCHAR(20),
       salary INT);
    
    #先执行全部导出操作：
    sqoop export \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --table updateonly \
    --export-dir /sqoop/updateonly/updateonly_1.txt
    
    #新增一个文件updateonly_2.txt：修改了前三条数据并且新增了一条记录
    1201,gopal,manager,1212
    1202,manisha,preader,1313
    1203,kalil,php dev,1414
    1204,allen,java,1515
    
    hadoop fs -put updateonly_2.txt /sqoop/updateonly/
    
    #执行更新导出：
    sqoop export \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --table updateonly \
    --export-dir /sqoop/updateonly/updateonly_2.txt \
    --update-key id \
    --update-mode updateonly
    ```

    > 适合场景：已有数据更新同步操作。

  - allowinsert模式

    ```shell
    #手动创建mysql中的目标表
    mysql> USE userdb;
    mysql> CREATE TABLE allowinsert ( 
       id INT NOT NULL PRIMARY KEY, 
       name VARCHAR(20), 
       deg VARCHAR(20),
       salary INT);
       
    #先执行全部导出操作
    sqoop export \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root \
    --password 123456 \
    --table allowinsert \
    --export-dir /sqoop/updateonly/updateonly_1.txt
    
    
    #执行更新导出
    sqoop export \
    --connect jdbc:mysql://192.168.88.80:3306/userdb \
    --username root --password 123456 \
    --table allowinsert \
    --export-dir /sqoop/updateonly/updateonly_2.txt \
    --update-key id \
    --update-mode allowinsert
    ```

  