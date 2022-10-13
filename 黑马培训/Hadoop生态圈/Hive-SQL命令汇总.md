### Hive命令汇总

#### 1.启动阶段

##### 1.1 首先启动metastore服务

```shell
nohup /export/server/apache-hive-3.1.2-bin/bin/hive --service metastore &
```

##### 1.2 启动hiveserver2服务

```shell
nohup /export/server/apache-hive-3.1.2-bin/bin/hive --service hiveserver2 &
```

##### 1.3 在任意机器上使用beeline客户端访问

```shell
/export/server/apache-hive-3.1.2-bin/bin/beeline
```

##### 1.4 jdbc访问HS2服务

```shell
beeline> ! connect jdbc:hive2://node1:10000 
```

#### 2.建表语句

![image-20220920222402106](img/image-20220920222402106.png)

```properties
蓝色字体是建表语法的关键字，用于指定某些功能。
[ ]中括号的语法表示可选。
|表示使用的时候，左右语法二选一。
建表语句中的语法顺序要和语法树中顺序保持一致。
temporary 临时的
external 外部的
partitioned 分割
clustered 聚集/桶、簇
sorted 排序
buckets 桶
delimited 限定
serde（Serializer/Deserializer）序列化/反序列化
stored 读取数据形式(SEQUENCEFILE|TEXTFILE|RCFILE)
location 存储位置(HDFS)
tblproperties 向表中添加自定义或预定义的元数据属性，并设置它们的赋值
row format delimited 定义行的格式(默认分割格式)
row format delimited
[fields terminated by char]  #指定字段之间的分隔符
[collection items terminated by char]  #指定集合元素之间的分隔符
[map keys terminated by char]         #指定map类型kv之间的分隔符
[lines terminated by char]            #指定换行符
```

##### 2.1**分隔符指定语法**

**语法格式**

```hive
ROW FORMAT DELIMITED | SERDE
ROW FORMAT DELIMITED  表示使用LazySimpleSerDe类进行序列化解析数据
ROW FORMAT SERDE      表示使用其他SerDe类进行序列化解析数据
```

##### **ROW FORMAT DELIMITED具体的子语法**

```hive
row format delimited
[fields terminated by char]  #指定字段之间的分隔符
[collection items terminated by char]  #指定集合元素之间的分隔符
[map keys terminated by char]         #指定map类型kv之间的分隔符
[lines terminated by char]            #指定换行符
```

##### 2.2 内部表、外部表

```hive
创建外部表 关键字external
create external table 表名(字段名 字段类型) 分割符;
```

##### 2.3 location存储位置

```hive
--在建表的时候 可以使用location关键字指定表的路径在HDFS任意位置
create table 表名(字段名 字段类型) 
location '在hdfs上的存储路径'
row format delimited
fields terminated by "分隔符";
--使用location关键字指定本张表数据在hdfs上的存储路径
```

##### 2.4分区表创建

```hive
create table 表名(字段名 字段类型)
partitioned by(分区字段 字段类型)  
row format delimited
fields terminated by "分隔符";
```

##### 2.5 静态加载

```hive
--静态加载分区表数据
load data [local] inpath '本地文件路径/hdfs路径' into table 表名 partition(分区字段='分区值');
```

##### 2.6 动态加载

```hive
--动态分区
set hive.exec.dynamic.partition=true; --注意hive3已经默认开启了
--模式分为strict严格模式  nonstrict非严格模式
严格模式要求 分区字段中至少有一个分区是静态分区。
set hive.exec.dynamic.partition.mode=nonstrict;
insert into table 表名 partition(自己设置的分区字段与插入的分区表分区字段一致) 
select 原表.*,原表.基于原表的分区字段 from 原表;
```

##### 2.7 多重分区表

```hive
partitioned by(字段1 ，字段2....)
```

##### 2.8 分桶表语法、创建、加载

```hive
CREATE TABLE 表名(字段名 字段类型)
CLUSTERED BY(分桶字段) --默认分解分桶字段升序排序，如果需要对其他字段排序加上
sorted by (排序字段) --指定每个分桶内部根据 cases倒序排序
INTO 分桶数 BUCKETS; --分桶的字段一定要是表中已经存在的字段
```

#### 3.Hive--DDL

##### 3.1 Database 数据库 DDL操作

```hive
--创建数据库
create database if not exists 库名
comment "注释"
with dbproperties ('createdBy'='Allen');
--描述数据库信息
desc database extended 库名;
--切换数据库
use 库名;
--删除数据库
--注意 CASCADE关键字慎重使用
--默认行为是RESTRICT，这意味着仅在数据库为空时才删除它。
--要删除带有表的数据库（不为空的数据库），我们可以使用CASCADE。
DROP (DATABASE|SCHEMA) [IF EXISTS] 库名 [RESTRICT|CASCADE];
drop database 库名 cascade ;
--更改数据库属性
ALTER (DATABASE|SCHEMA) 库名 SET DBPROPERTIES (property_name=property_value, ...);
--更改数据库所有者
ALTER (DATABASE|SCHEMA) database_name SET OWNER [USER|ROLE] user_or_role;
--更改数据库位置
ALTER (DATABASE|SCHEMA) database_name SET LOCATION hdfs_path;
```

##### 3.2 Table 表 DDL操作

```hive
--下面这两个需要记住
--查询指定表的元数据信息
desc formatted 表名;
show create table 表名;
--1、更改表名
ALTER TABLE table_name RENAME TO new_table_name;
--2、更改表属性
ALTER TABLE table_name SET TBLPROPERTIES (property_name = property_value, ... );
--更改表注释
ALTER TABLE student SET TBLPROPERTIES ('comment' = "new comment for student table");
--3、更改SerDe属性
ALTER TABLE table_name SET SERDE serde_class_name [WITH SERDEPROPERTIES (property_name = property_value, ... )];
ALTER TABLE table_name [PARTITION partition_spec] SET SERDEPROPERTIES serde_properties;
ALTER TABLE table_name SET SERDEPROPERTIES ('field.delim' = ',');
--移除SerDe属性
ALTER TABLE table_name [PARTITION partition_spec] UNSET SERDEPROPERTIES (property_name, ... );
--4、更改表的文件存储格式 该操作仅更改表元数据。现有数据的任何转换都必须在Hive之外进行。
ALTER TABLE table_name  SET FILEFORMAT file_format;
--5、更改表的存储位置路径
ALTER TABLE table_name SET LOCATION "new location";
--6、更改列名称/类型/位置/注释
CREATE TABLE test_change (a int, b int, c int);
--First change column a's name to a1.
ALTER TABLE test_change CHANGE a a1 INT;
-- Next change column a1's name to a2, its data type to string, and put it after column b.
ALTER TABLE test_change CHANGE a1 a2 STRING AFTER b;
--The new table's structure is:  b int, a2 string, c int.
-- Then change column c's name to c1, and put it as the first column.
ALTER TABLE test_change CHANGE c c1 INT FIRST;
-- The new table's structure is:  c1 int, b int, a2 string.
-- Add a comment to column a1
ALTER TABLE test_change CHANGE a1 a1 INT COMMENT 'this is column a1';
--7、添加/替换列
--使用ADD COLUMNS，您可以将新列添加到现有列的末尾但在分区列之前。
--REPLACE COLUMNS 将删除所有现有列，并添加新的列集。
ALTER TABLE table_name ADD|REPLACE COLUMNS (col_name data_type,...);
```

##### 3.3 Partition分区 DDL操作

```hive
--1、增加分区
--step1: 创建表 手动加载分区数据
load data local inpath '本地文件路径' into table 表名 partition(分区字段="分区值");

--step2：修改hive的分区，添加一个分区元数据
ALTER TABLE 表名 ADD PARTITION (分区字段="新分区值") location'新分区文件夹';


----此外还支持一次添加多个分区
ALTER TABLE table_name ADD PARTITION (分区字段="新分区值") location '新分区文件夹' PARTITION (分区字段="新分区值") location '新分区文件夹';


--2、重命名分区
ALTER TABLE  表名 PARTITION (分区字段="分区值") RENAME TO PARTITION (分区字段="新分区值");

--3、删除分区
ALTER TABLE 表名 DROP [IF EXISTS] PARTITION (分区字段="分区值");
ALTER TABLE 表名 DROP [IF EXISTS] PARTITION (分区字段="分区值") PURGE; --直接删除数据 不进垃圾桶 有点像skipTrash

--4、修复分区
MSCK [REPAIR] TABLE 表名 [ADD/DROP/SYNC PARTITIONS];

--5、修改分区
--更改分区文件存储格式
ALTER TABLE  表名 PARTITION (分区字段="分区值") SET FILEFORMAT 文件格式;
--更改分区位置
ALTER TABLE  表名 PARTITION (分区字段="分区值") SET LOCATION 新的存储位置;
```

##### 3.4 show语法

```hive
--1、显示所有数据库 SCHEMAS和DATABASES的用法 功能一样
show databases;
show schemas;

--2、显示当前数据库所有表/视图/物化视图/分区/索引
show tables;
SHOW TABLES [IN database_name]; --指定某个数据库

--3、显示当前数据库下所有视图
--视图相当于没有数据临时表  虚拟表
Show Views;
SHOW VIEWS 'test_*'; -- show all views that start with "test_"
SHOW VIEWS FROM 表名; -- show views from database test1
SHOW VIEWS [IN/FROM database_name];

--4、显示当前数据库下所有物化视图
SHOW MATERIALIZED VIEWS [IN/FROM database_name];

--5、显示表分区信息，分区按字母顺序列出，不是分区表执行该语句会报错
show partitions 表名;
show partitions itheima.student_partition;

--6、显示表/分区的扩展信息
SHOW TABLE EXTENDED [IN|FROM database_name] LIKE table_name;
show table extended like student;
describe formatted itheima.student;

--7、显示表的属性信息
SHOW TBLPROPERTIES table_name;
show tblproperties student;

--8、显示表、视图的创建语句
SHOW CREATE TABLE ([db_name.]table_name|view_name);
show create table student;

--9、显示表中的所有列，包括分区列。
SHOW COLUMNS (FROM|IN) table_name [(FROM|IN) db_name];
show columns  in student;

--10、显示当前支持的所有自定义和内置的函数
show functions;

--11、Describe desc
--查看表信息
desc extended table_name;
--查看表信息（格式化美观）
desc formatted table_name;
--查看数据库相关信息
describe database database_name;
```

#### 4.Hive--DML

##### 4.1 load加载数据

```hive
LOAD DATA [LOCAL] INPATH '文件路径' [OVERWRITE] INTO
TABLE 表名 [PARTITION (分区字段='分区值')]
```

```hive
--加载数据
-- 从本地加载数据  数据位于HS2（node1）本地文件系统  本质是hadoop fs -put上传操作
LOAD DATA LOCAL INPATH '本地文件路径' INTO TABLE 表名;
--从HDFS加载数据  数据位于HDFS文件系统根目录下  本质是hadoop fs -mv 移动操作
LOAD DATA INPATH '文件路径' INTO TABLE 表名;
----从HDFS加载数据到分区表中并指定分区  数据位于HDFS文件系统根目录下
LOAD DATA INPATH '文件路径' INTO TABLE 表名 partition(分区字段='分区值');
```

##### 4.2 insert插入语法

**insert+select**

```hive
--使用insert+select插入数据到新表中
insert into table 新表
select 老表字段 from 老表;
```

Multi Inserts(多重插入 多次插入)

```hive
--多重插入：
from 新表                     
insert overwrite table 老表1 
select 老表字段
insert overwrite table 老表2
select 老表字段;
--只需要扫描一次表  分别把不同字段插入到不同的表中即可 减少扫描次数 提高效率
```

Dynamic partition inserts (动态分区插入)

```hive
如果是通过insert+select 动态确定分区值的，叫做动态分区
insert table partition (分区字段) +select 
```

##### 4.3 insert导出数据操作

```hive
--1、导出查询结果到HDFS指定目录下
--overwrite会覆盖路径，谨慎使用
insert overwrite directory '导出文件路径' select 字段列表 from 表名;   --默认导出数据字段之间的分隔符是\001

--2、导出时指定分隔符和文件存储格式
insert overwrite directory '导出文件路径' row format delimited fields terminated by ','
select 字段列表 from 表名;

--3、导出数据到本地文件系统指定目录下
insert overwrite local directory '导出本地文件路径' select 字段列表 from 表名;
```

#### 5.Hive--DQL

##### 5.1select语法树

```hive
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
FROM table_reference
JOIN table_other ON expr
[WHERE where_condition]
[GROUP BY col_list [HAVING condition]]
[CLUSTER BY col_list
| [DISTRIBUTE BY col_list] [SORT BY| ORDER BY col_list]
]
[LIMIT number]
```

##### 5.2 CLUSTER BY  分桶查询

```hive
--CTAS
create table 新表名 as select 字段列表 from 老表名; 
--根据查询返回的结果去创建一张表 表的字段个数类型顺序数据都取决于后面的查询。
select 字段列表 from 表名 cluster by 分桶字段; --分桶查询 根据学生编号进行分桶查询
```

##### 5.3 DISTRIBUTE BY+SORT BY

```hive
--当后面分和排序的字段是同一个字段 加起来就相等于cluster by
CLUSTER BY(分且排序) = DISTRIBUTE BY（分）+SORT BY（排序） 
--下面两个功能一样的
select 字段列表 from 表名 cluster by 分桶字段;
select 字段列表 from 表名 distribute by 分桶字段 sort by 排序字段;
select 字段列表 from 表名 distribute by 分桶字段 sort by 排序字段 desc;
```

##### 5.4 ORDER BY

```hive
--首先我们设置一下reducetask个数，随便设置
--根据之前的探讨，貌似用户设置几个，结果就是几个，但是实际情况如何呢？
set mapreduce.job.reduces =2;
select 字段列表 from 表名 order by 排序字段 desc;
--原因：order by是全局排序。全局排序意味着数据只能输出在一个文件中。因此也只能有一个reducetask.
--在order by出现的情况下，不管用户设置几个reducetask,在编译执行期间都会变为一个，满足全局。
```

##### 5.5 Union联合查询

```hive
--语法规则
select_statement 
	UNION [ALL | DISTINCT] 
select_statement 
	UNION [ALL | DISTINCT] 
select_statement 
	...;
--使用DISTINCT关键字与使用UNION默认值效果一样，都会删除重复行。
select 字段列表 from 表名
UNION
select 字段列表 from 表名;

--和上面一样
select 字段列表 from 表名
UNION DISTINCT
select 字段列表 from 表名;

--使用ALL关键字会保留重复行。
select 字段列表 from 表名
UNION ALL
select 字段列表 from 表名 
limit 取得行数;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT应用于单个SELECT
--请将子句放在括住SELECT的括号内
SELECT 字段列表 FROM (select 字段列表 from 表名 LIMIT 2) 子表别名
UNION
SELECT 字段列表 FROM (select 字段列表 from 表名 LIMIT 3) 子表别名;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT子句应用于整个UNION结果
--请将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT放在最后一个之后。
select 字段列表 from 表名
UNION
select 字段列表 from 表名
order by 排序字段 排序方式;
```

##### 5.6 CTE表达式

```hive
--select语句中的CTE
with q1 as (select num,name,age from student where num = 95002)
select *
from q1;

-- from风格
with q1 as (select num,name,age from student where num = 95002)
from q1
select *;

-- chaining CTEs 链式
with q1 as ( select * from student where num = 95002),
     q2 as ( select num,name,age from q1)
select * from (select num from q2) a;

-- union
with q1 as (select * from student where num = 95002),
     q2 as (select * from student where num = 95004)
select * from q1 union all select * from q2;

-- ctas  
-- creat table as select 创建一张表来自于后面的查询语句  表的字段个数 名字 顺序和数据行数都取决于查询
-- create table t_ctas as select num,name from student limit 2;

create table s2 as
with q1 as ( select * from student where num = 95002)
select * from q1;

-- view
create view v1 as
with q1 as ( select * from student where num = 95002)
select * from q1;

select * from v1;
```

##### 5.7 join语法及其使用

```hive
--1、内连接  inner join == join
  返回左右两边同时满足条件的数据
  
select e.*,e_a.*
from employee e inner join employee_address e_a
on e.id =e_a.id;

--等价于 inner join
select e.*,e_a.*
from employee e join employee_address e_a
on e.id =e_a.id;

--等价于 隐式连接表示法
select e.*,e_a.*
from employee e , employee_address e_a
where e.id =e_a.id;

  
--2、左连接  left join  ==  left OUTER join
  左表为准，左表全部显示，右表与之关联 满足条件的返回，不满足条件显示null
  
select e.*,e_conn.*
from employee e left join employee_connection e_conn
on e.id =e_conn.id;

--等价于 left outer join 左外连接
select e.id,e.*,e_conn.*
from employee e left outer join  employee_connection e_conn
on e.id =e_conn.id;  
  
--3、右连接  right join  ==  right OUTER join 右外连接
  右表为准，右表全部显示，左表与之关联 满足条件的返回，不满足条件显示null
 
select e.id,e.*,e_conn.*
from employee e right join employee_connection e_conn
on e.id =e_conn.id;

--等价于 right outer join
select e.id,e.*,e_conn.*
from employee e right outer join employee_connection e_conn
on e.id =e_conn.id;

--4、外连接 全外连接 full join == full outer join
FULL OUTER JOIN 关键字只要左表（table1）和右表（table2）其中一个表中存在匹配，则返回行.
FULL OUTER JOIN 关键字结合了 LEFT JOIN 和 RIGHT JOIN 的结果。

select e.*,e_a.*
from employee e full outer join employee_address e_a
on e.id =e_a.id;
--等价于
select e.*,e_a.*
from employee e full join employee_address e_a
on e.id =e_a.id;

--5、左半连接 left semi join

select *
from employee e left semi join employee_address e_addr
on e.id =e_addr.id;

--相当于 inner join,但是只返回左表全部数据， 只不过效率高一些
select e.*
from employee e inner join employee_address e_addr
on e.id =e_addr.id;

--6、交叉连接cross join
将会返回被连接的两个表的笛卡尔积，返回结果的行数等于两个表行数的乘积。对于大表来说，cross join慎用。
在SQL标准中定义的cross join就是无条件的inner join。返回两个表的笛卡尔积,无需指
定关联键。
在HiveSQL语法中，cross join 后面可以跟where子句进行过滤，或者on条件过滤。
```

#### 6.内置常见运算符

```hive
--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;
--使用extended 可以查看更加详细的使用说明
describe function extended +;
```

##### 6.1 关系运算符

```hive
----------------Hive中关系运算符--------------------------
--is null空值判断
select 1 from dual where 'itcast' is null;

--is not null 非空值判断
select 1 from dual where 'itcast' is not null;

--like比较： _表示任意单个字符 %表示任意数量字符
--否定比较： NOT A like B
select 1 from dual where 'itcast' like 'it_';
select 1 from dual where 'itcast' like 'it%';
select 1 from dual where  'itcast' not like 'hadoo_';
select 1 from dual where  not 'itcast' like 'hadoo_';

--rlike：确定字符串是否匹配正则表达式，是REGEXP_LIKE()的同义词。
select 1 from dual where 'itcast' rlike '^i.*t$';
select 1 from dual where '123456' rlike '^\\d+$';  --判断是否全为数字
select 1 from dual where '123456aa' rlike '^\\d+$';

--regexp：功能与rlike相同 用于判断字符串是否匹配正则表达式
select 1 from dual where 'itcast' regexp '^i.*t$';
```

##### 6.2 算术运算符

```hive
-------------------Hive中算术运算符---------------------------------
--取整操作: div  给出将A除以B所得的整数部分。例如17 div 3得出5。
select 17 div 3;

--取余操作: %  也叫做取模mod  A除以B所得的余数部分
select 17 % 3;

--位与操作: &  A和B按位进行与操作的结果。 与表示两个都为1则结果为1
select 4 & 8 from dual;  --4转换二进制：0100 8转换二进制：1000
select 6 & 4 from dual;  --4转换二进制：0100 6转换二进制：0110

--位或操作: |  A和B按位进行或操作的结果  或表示有一个为1则结果为1
select 4 | 8 from dual;
select 6 | 4 from dual;

--位异或操作: ^ A和B按位进行异或操作的结果 异或表示两者的值不同,则结果为1
select 4 ^ 8 from dual;
select 6 ^ 4 from dual;
```

##### 6.3 逻辑运算符

```hive
--3、Hive逻辑运算符
--与操作: A AND B   如果A和B均为TRUE，则为TRUE，否则为FALSE。如果A或B为NULL，则为NULL。
select 1 from dual where 3>1 and 2>1;
--或操作: A OR B   如果A或B或两者均为TRUE，则为TRUE，否则为FALSE。
select 1 from dual where 3>1 or 2!=2;
--非操作: NOT A 、!A   如果A为FALSE，则为TRUE；如果A为NULL，则为NULL。否则为FALSE。
select 1 from dual where not 2>1;
select 1 from dual where !2=1;

--在:A IN (val1, val2, ...)  如果A等于任何值，则为TRUE。
select 1 from dual where 11  in(11,22,33);
--不在:A NOT IN (val1, val2, ...) 如果A不等于任何值，则为TRUE
select 1 from dual where 11 not in(22,33,44);
```

#### 7.字符串函数

```hive
--字符串截取函数：substr(str, pos[, len]) 或者  substring(str, pos[, len])
select substr("angelababy",-2); --pos是从1开始的索引，如果为负数则倒着数
select substr("angelababy",2,2);

--正则表达式替换函数：regexp_replace(str, regexp, rep)
select regexp_replace('100-200', '(\\d+)', 'num'); --正则分组

--正则表达式解析函数：regexp_extract(str, regexp[, idx]) 提取正则匹配到的指定组内容
select regexp_extract('100-200', '(\\d+)-(\\d+)', 2);

--URL解析函数：parse_url 注意要想一次解析出多个 可以使用parse_url_tuple这个UDTF函数
select parse_url('http://www.itcast.cn/path/p1.php?query=1', 'HOST');

--分割字符串函数: split(str, regex)
select split('apache hive', '\\s+');--匹配一个或者多个空白符

--json解析函数：get_json_object(json_txt, path)
--$表示json对象
select get_json_object('[{"website":"www.itcast.cn","name":"allenwoon"}, {"website":"cloud.itcast.com","name":"carbondata 中文文档"}]', '$.[1].website');

```

#### 8.时间日期、数值函数

##### 8.1 时间日期函数

```hive
--获取当前日期: current_date
select current_date();
--获取当前时间戳: current_timestamp
--同一查询中对current_timestamp的所有调用均返回相同的值。
select current_timestamp();
--获取当前UNIX时间戳函数: unix_timestamp
select unix_timestamp();
--日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp("2011-12-07 13:01:03");
--指定格式日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp('20111207 13:01:03','yyyyMMdd HH:mm:ss');
--UNIX时间戳转日期函数: from_unixtime
select from_unixtime(1620723323);
select from_unixtime(0, 'yyyy-MM-dd HH:mm:ss');
--日期比较函数: datediff  日期格式要求'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'
select datediff('2012-12-08','2012-05-09');
--日期增加函数: date_add
select date_add('2012-02-28',10);
--日期减少函数: date_sub
select date_sub('2012-01-1',10);
```

##### 8.2 数值函数

```hive
--取整函数: round  返回double类型的整数值部分 （遵循四舍五入）
select round(3.1415926);
--指定精度取整函数: round(double a, int d) 返回指定精度d的double类型
select round(3.1415926,4);
--向下取整函数: floor
select floor(3.1415926);
select floor(-3.1415926);
--向上取整函数: ceil
select ceil(3.1415926);
select ceil(-3.1415926);
--取随机数函数: rand 每次执行都不一样 返回一个0到1范围内的随机数
select rand();
--指定种子取随机数函数: rand(int seed) 得到一个稳定的随机数序列
select rand(5);
```

#### 9.条件转换、集合、加密函数

##### 9.1 条件函数

```hive
--if条件判断: if(boolean testCondition, T valueTrue, T valueFalseOrNull)
select if(1=2,100,200);
select if(sex ='男','M','W') from student limit 3;

--空判断函数: isnull( a )
select isnull("allen");
select isnull(null);

--非空判断函数: isnotnull ( a )
select isnotnull("allen");
select isnotnull(null);

--空值转换函数: nvl(T value, T default_value)
select nvl("allen","itcast");
select nvl(null,"itcast");

--非空查找函数: COALESCE(T v1, T v2, ...)
--返回参数中的第一个非空值；如果所有值都为NULL，那么返回NULL
select COALESCE(null,11,22,33);
select COALESCE(null,null,null,33);
select COALESCE(null,null,null);

--条件转换函数: CASE a WHEN b THEN c [WHEN d THEN e]* [ELSE f] END
select case 100 when 50 then 'tom' when 100 then 'mary' else 'tim' end;
select case sex when '男' then 'male' else 'female' end from student limit 3;
```

##### 9.2 数据类型转换函数

```hive
--任意数据类型之间转换:cast
select cast(12.14 as bigint);
select cast(12.14 as string);
select cast("hello" as int);
```

##### 9.3 数据脱敏函数 (Data Masking Functions )

```hive
--mask
--将查询回的数据，大写字母转换为X，小写字母转换为x，数字转换为n。
select mask("abc123DEF");
select mask("abc123DEF",'-','.','^'); --自定义替换的字母

--mask_first_n(string str[, int n]
--对前n个进行脱敏替换
select mask_first_n("abc123DEF",4);

--mask_last_n(string str[, int n])
select mask_last_n("abc123DEF",4);

--mask_show_first_n(string str[, int n])
--除了前n个字符，其余进行掩码处理
select mask_show_first_n("abc123DEF",4);

--mask_show_last_n(string str[, int n])
select mask_show_last_n("abc123DEF",4);

--mask_hash(string|char|varchar str)
--返回字符串的hash编码。
select mask_hash("abc123DEF");
```

##### 9.4 其他杂项函数、加密函数 (Misc. Functions )

```hive
--如果你要调用的java方法所在的jar包不是hive自带的 可以使用add jar添加进来
--hive调用java方法: java_method(class, method[, arg1[, arg2..]])
select java_method("java.lang.Math","max",11,22);

--反射函数: reflect(class, method[, arg1[, arg2..]])
select reflect("java.lang.Math","max",11,22);

--取哈希值函数:hash
select hash("allen");

--current_user()、logged_in_user()、current_database()、version()

--SHA-1加密: sha1(string/binary)
select sha1("allen");

--SHA-2家族算法加密：sha2(string/binary, int)  (SHA-224, SHA-256, SHA-384, SHA-512)
select sha2("allen",224);
select sha2("allen",512);

--crc32加密:
select crc32("allen");

--MD5加密: md5(string/binary)
select md5("allen");
```

#### 10.Hive高阶函数

##### 10.1 explode函数（UDTF表生成函数）

```hive
select explode(array类型字段/map类型);
```

##### 10.2 lateral view侧视图的使用

```hive
--lateral view侧视图基本语法如下
select 字段列表 from 表名 lateral view UDTF类型函数(字段) 侧视图别名 as 字段别名;
```

##### 10.3多行转单列（collect_list、concat_ws）

数据收集函数

```hive
collect_set --把多行数据收集为一行  返回set集合  去重无序
collect_list --把多行数据收集为一行  返回list集合  不去重有序
```

字符串拼接函数

```hive
concat  --直接拼接字符串
concat_ws --指定分隔符拼接
```

类型转换函数

```hive
cast --改变数据类型为指定类型
```

最终SQL实现

```hive
select
    分组字段名，
    concat_ws('分隔符', collect_list(cast(字段名 as 数据类型))) as 字段名别名
from
    表名
group by
    分组字段;
```

##### 10.4 行列转换--单列转多行

--SQL最终实现

```hive
select
    字段名，
    视图名.字段别名1 as 字段别名2
from
    表名
        lateral view
            explode(split(字段名, '分隔符')) 视图名 as 字段别名1;
```

##### 10.5 json格式数据处理

```hive
-- get_json_object UDF函数 最大弊端是一次只能解析提取一个字段
select
    get_json_object(json,"json键值") as 字段别名,
from 表名;

--json_tuple 这是一个UDTF函数 可以一次解析提取多个字段
--单独使用 解析所有字段
select
    json_tuple(json,"json键值"......) as (字段别名)
from 表名.....;

--搭配侧视图使用
select
    字段列表
from 表名
         lateral view json_tuple(json,"json键值") b
         as 字段别名;


--方式2： 使用JsonSerDe类在建表的时候解析数据
--建表的时候直接使用JsonSerDe解析
create table t表名 (
 字段列表及字段类型
)
 ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'
 STORED AS TEXTFILE;
```

##### 10.6 窗口函数

```hive
Function OVER ([PARTITION BY <...>] [ORDER BY <....>] [<window_expression>])

--1、Function可以是下面分类中的任意一个
	--聚合函数：比如sum、max、avg、max、min等
    --排序函数：比如rank、row_number等
    --分析函数：比如lead、lag、first_value等

--2、OVER 窗口函数语法关键字与标识

--3、PARTITION BY <...>功能类似于group by，用于指定分组，相同的分为一组。如果没有指定PARTITION BY，那么整张表的所有行就是一组；

--4、ORDER BY <....> 用于指定每个分组内的数据排序规则 ，默认是升序ASC，支持ASC、DESC；

--5、window_expression window表达式，也叫window子句，用于指定每个窗口中操作的数据范围
```

##### 10.7 窗口函数--聚合函数

```hive
sum|max|min|avg  OVER ([PARTITION BY <...>] [ORDER BY <....>] [<window_expression>])
```

```hive
- 有没有partition by 影响的是全局聚合 还是分组之后 每个组内聚合。
- 没有order by，默认是rows between，首行到最后行，这里的"行"是物理上的行；
- 有order by，默认是range between，首行到当前行，这里的"行"是逻辑上的行，由字段的值的区间range来划分范围。
```

##### 10.8 窗口函数--window子句

```properties
unbounded 无边界
preceding 往前
following 往后
unbounded preceding 往前所有行，即初始行
n preceding 往前n行
unbounded following 往后所有行，即末尾行
n following 往后n行
current row 当前行
 
语法
(ROWS | RANGE) BETWEEN (UNBOUNDED | [num]) PRECEDING AND ([num] PRECEDING | CURRENT ROW | (UNBOUNDED | [num]) FOLLOWING)

(ROWS | RANGE) BETWEEN CURRENT ROW AND (CURRENT ROW | (UNBOUNDED | [num]) FOLLOWING)
(ROWS | RANGE) BETWEEN [num] FOLLOWING AND (UNBOUNDED | [num]) FOLLOWING
```

##### 10.9 窗口函数--排序函数

row_number、rank、dense_rank 函数

```hive
SELECT
    字段列表
    RANK() OVER(PARTITION BY 分区字段 ORDER BY 排序字段 desc) AS 函数字段别名,
    DENSE_RANK() OVER(PARTITION BY 分区字段 ORDER BY 排序字段 desc) AS 函数字段别名,
    ROW_NUMBER() OVER(PARTITION BY 分区字段 ORDER BY 排序字段 DESC) AS 函数字段别名
FROM 表名;
```

ntile函数

```hive
--功能：将分组排序之后的数据分成指定的若干个部分（若干个桶）
--规则：尽量平均分配 ，优先满足最小的桶，彼此最多不相差1个。
SELECT
    字段列表
    NTILE(分桶数) OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数字段别名
FROM 表名
ORDER BY 排序字段;
```

##### 10.10窗口函数--lag、lead函数

```hive
--LAG 用于统计窗口内往上第n行值(相当于把数据下移n行)
SELECT 字段列表
       LAG(偏移字段,偏移行数,'无值时返回的默认值') OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段,
       LAG(偏移字段,偏移行数) OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段
FROM 表名;

--LEAD 用于统计窗口内往下第n行值(相当于把数据上移n行)
SELECT 字段列表
       LEAD(偏移字段,偏移行数,'无值时返回的默认值') OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段,
       LEAD(偏移字段,偏移行数) OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段
FROM 表名;
```

##### 10.11窗口函数--FIRST_VALUE、LAST_VALUE函数

```hive
--FIRST_VALUE 取分组内排序后，截止到当前行，第一个值
SELECT 字段列表
       FIRST_VALUE(截取字段) OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段
FROM 表名;

--LAST_VALUE  取分组内排序后，截止到当前行，最后一个值
SELECT 字段列表
       LAST_VALUE(截取字段) OVER(PARTITION BY 分区字段 ORDER BY 排序字段) AS 函数别名字段
FROM 表名;
```

##### 10.12文件存储格式（text、ORC、parquet）

```hive
--1、创建表，存储数据格式为TEXTFILE/ORC/parquet
create table 表名(
字段列表
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '分隔符'
STORED AS TEXTFILE/orc/PARQUET ;  
--如果不写stored as textfile 默认就是textfile
```

##### 10.13 数据压缩和文件格式搭配（ORC+snappy）

```hive
--设置Hive的中间压缩 也就是map的输出压缩
1）开启 hive 中间传输数据压缩功能
set hive.exec.compress.intermediate=true;
2）开启 mapreduce 中 map 输出压缩功能
set mapreduce.map.output.compress=true;
3）设置 mapreduce 中 map 输出数据的压缩方式
set mapreduce.map.output.compress.codec = org.apache.hadoop.io.compress.SnappyCodec;

--设置Hive的最终输出压缩，也就是Reduce输出压缩
1）开启 hive 最终输出数据压缩功能
set hive.exec.compress.output=true;
2）开启 mapreduce 最终输出数据压缩
set mapreduce.output.fileoutputformat.compress=true;
3）设置 mapreduce 最终数据输出压缩方式
set mapreduce.output.fileoutputformat.compress.codec =org.apache.hadoop.io.compress.SnappyCodec;
4）设置 mapreduce 最终数据输出压缩为块压缩  还可以指定RECORD
set mapreduce.output.fileoutputformat.compress.type=BLOCK;  
```

```hive
--orc 存储文件默认采用ZLIB 压缩。比 snappy 压缩的小
STORED AS orc; 
STORED AS orc tblproperties ("orc.compress"="ZLIB");

--以ORC格式存储 不压缩
STORED AS orc tblproperties ("orc.compress"="NONE");  

--以ORC格式存储  使用snappy压缩
STORED AS orc tblproperties ("orc.compress"="SNAPPY"); 
```

