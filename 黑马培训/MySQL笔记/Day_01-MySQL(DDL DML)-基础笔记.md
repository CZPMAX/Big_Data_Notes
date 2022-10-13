

[TOC]

**Day01-MySQL基础**

**本机mysql中有两个用户分别为 root 和 czp  分别对应的密码为 123456 和 mysql**

#### 通识理解

**常握 DDL数据定义语言**：建库、建表, 数据库表, 数据库, 表字段  D 定义 就是有关数据库结构和数据库表的操作

**常握 DML数据操作语言**：表记录增、改、删                                  M ==> manipulation 操作 

**常握 DQL数据查询语言**：简单查询和条件查询                               Q ==> Query 查询

**了解 DCL数据控制语言**  : 管理用户的权限                                       C ==> Controller 控制

```
基本指令
    net start mysql 启动MySQL服务
    net stop mysql 停止MySQL服务
    mysql -u root -p + 密码登录进入mysql>
    mysql>内输入指令都需要用;结束
    mysql --version mysql -V 查看MySQL版本
    slect version(); 查看MySQL版本
    \c ctrl+c 终止一条正在编写的语句
    exit \q QUIT 退出mysql
对database的操作
    show databases; 显示所有数据库
    select database(); 查询当前使用的数据库
    create batabase dbName 创建数据库
    drop database dbName 删除数据库
    use dbName; 使用数据库
对table的操作
    show tables 显示数据库中的表
    show tables from dbName; 查看指定数据库的表
    source D:/Desktop/learn/MySQL/bjpowernode.sql; 导入sql文件，并执行里面的        sql语句
    select* from tableName 查看表中的全部数据
    desc tableName 查看表的结构 desc→describe
    show create table tableName; 查看表的创建语句
简单的查询语句
    select 字段名 from tableName; 查询某个字段的数据
    select 字段名,字段名 from tableName;查询多个字段可用 , 将不同字段间隔
    select* from tableName 查看表中的全部数据
    select empno as'员工编号',ename as '员工姓名',sal*12 as '年薪' from            emp; as 'xxx' 可以将指定的要查询字段用另一个名字显示
重命名以后原表名失效
```

#### 1. 数据库简介：作用和分类

**问题：**

```bash
1. 什么是数据库？有什么作用？
2. 数据库有哪些分类？常见的数据库有哪些？
```

**什么是数据库？有什么作用？**

数据库就是**存储和管理数据的仓库**，数据按照一定的格式进行存储，用户可以对数据库中的数据进行增加、修改、删除、查询等操作。

**数据库有哪些分类？常见的数据库有哪些？**

1）关系型数据库(SQL数据库)

* 数据库、数据表

* **数据存储形式：数据以行列表格的形式进行存储**
* 常见产品：**MySQL**、Oracle、DB2、SQL Server、Sqlite(小型关系型数据)
* **MySQL里面创建表都是以字段为基础来创建的,字段是竖着来的,mysql对数据的操作是以字段的为单位的 当要对数据库中表进行操作时,就是以字段为基础,来取相对应的一行,或者多行数据**

例如 现有字段en_score  要从学生成绩表Score中获取英语成绩大于等于60分的所有学生的全部信息, 那么sql语句得这么写

```sql
select * from Score where en_score>=60
```

2）非关系型数据库(NoSQL数据库)

* **数据存储形式：Key-Value形式进行数据存储**
* 常见产品：MongoDB、**Redis、HBase**

> 现阶段我们主要就是学习 MySQL 数据库软件的基本使用：常握 MySQL 数据库中数据的各种操作

#### 2. MySQL数据库简介和安装

**MySQL 是一个关系型数据库管理系统**，它是由瑞典MySQL AB公司开发，目前属于 Oracle 旗下产品，MySQL 是最流行的关系型数据库管理系统之一。

MySQL 数据库是 C/S 软件：分为客户端和服务器。

**MySQL的特点**：

1. MySQL是开源的，所以你不需要支付额外的费用（**MySQL5以上版本已经开始收费**）。
2. MySQL支持大型的数据库。可以处理拥有上千万条记录的大型数据库。
3. MySQL使用**标准的SQL数据语言**形式。
4. MySQL可以安装在不同的操作系统，并且提供多种编程语言的操作接口。这些编程语言包括C、C++、Python、Java、Ruby等等。
5. 支持多种存储引擎。

**MySQL的安装**：

```bash
# 查看 mysql 数据库的版本
mysql --version
或
mysql -V
```

> 注：MySQL数据库的具体安装过程参考讲义。
>
> MySQL数据库服务端的端口：3306

#### 3. MySQL数据库登录和退出

**MySQL 登录**：

通过 MySQL 客户端软件连接到 MySQL 服务器，即可以对 MySQL 服务器上存储的数据进行操作。

```bash
mysql -h数据库服务端IP -P数据库服务端端口 -u用户名 -p密码
```

**说明**：

- -h 后面是数据库服务端IP，默认是 127.0.0.1，该参数可以省略
- -P 后面是数据库服务端端口，默认是 3306，该参数可以省略
- -u 后面是登录的用户名
- -p 后面是登录用户密码，如果不填写，回车之后，会提示输入密码

**MySQL 退出**：

```bash'
quit 
或
exit 
```

#### 4. DataGrip 软件的基本使用

DataGrip是JetBrains公司推出的管理数据库的产品，功能非常强大，可以兼容各种数据库，另外，JetBrains公司还有一款知名的IDE开发工具IDEA，用户体验非常不错。

它的设计符合数据库管理员、开发人员需要。

![img](E:\黑马培训\MySQL笔记\assets\DataGrip软件.png)

**DataGrip基本使用**：

- 创建工程
- 连接数据库
- DataGrip配置
- 创建查询
- 快捷键使用：Ctrl+Enter(快速执行选择的SQL语句)

#### 5. SQL语句按功能分类介绍

问题：

```bash
1. 什么是SQL语句？SQL语句有什么作用？
2. SQL语句有哪些分类？
```

**什么是SQL语句？SQL语句有什么作用？**

SQL全称是结构化查询语言(Structured Query Language)，是**关系型数据库管理系统都需要遵循的规范**，是操作关系型数据库的语言，只要是关系型数据库，都支持SQL。

> 虽然所有的关系型数据库都支持SQL，但是不同的关系型数据库都会有一些特殊的东西(特有的函数或语法)。

![img](E:\黑马培训\MySQL笔记\assets\SQL语言.png)

**SQL 语句的作用**：

- 数据库的增、删、改、查
- 数据表的增、删、改、查
- 表记录的增、删、改、查
- ......

**SQL 语句有哪些分类？**

* **DDL**(Data Definition Language)：数据定义语言，用来定义数据库对象：数据库，表，列等。
* **DML**(Data Manipulation Language)：数据操作语言，用来对数据库中表的记录进行增、删、改。
* **DQL**(Data Query Language)：**数据查询语言**，用来查询数据库中表的记录【**核心重点！！！**】
* **DCL**(Data Control Language)：数据控制语言，用来定义数据库的访问权限和安全级别，及创建用户。

**SQL 通用语法**：

1）SQL语句可以单行或多行书写，以分号结尾；

2）可使用空格和缩进来增强语句的可读性；

3）**MySQL数据库的SQL语句不区分大小写，关键字建议使用大写**

4）可以使用`/* */`、`--`、`#`的方式完成注释

#### 6. DDL 数据库操作

**DDL数据定义语言：**

主要时负责数据库及数据表的结构设置，也就是搭建保存数据的容器，并定义存储规则的语言。

**DDL-数据库操作**：

1）创建数据库

```sql
# 创建数据库
CREATE DATABASE 数据库名称;

# 当数据库不存在时，创建数据库
CREATE DATABASE IF NOT EXISTS 数据库名称;

# 创建数据库并指定utf8编码
CREATE DATABASE 数据库名称 CHARSET='utf8';
```

2）查看数据库

```sql
# 显示已有的数据库
SHOW DATABASES;
```

3）使用数据库

```sql
# 使用指定数据库
USE 数据库名称;
```

4）删除数据库

```sql
# 删除指定数据库
# 注意：实际工作不要删除数据库！！！
# 注意：实际工作不要删除数据库！！！
# 注意：实际工作不要删除数据库！！！
DROP DATABASE 数据库名;
```

#### 7. DDL 数据表操作

**DDL-数据表操作**：

1）创建数据表

```sql
CREATE TABLE 表名(
    字段名1 数据类型(长度) 约束,
    字段名2 数据类型(长度) 约束
    ...
);

# 创建表举例
CREATE TABLE category(
    # 字段1名称为cid，数据类型为整型，添加主键约束及非空约束
    cid INT PRIMARY KEY NOT NULL,
    # 字段2名称为cname，数据类型为varchar，最大长度为100
    cname VARCHAR(100)
);
```

* 字段名：数据表中每一列的名称（列头）

* 数据类型：数据的存储形式（结构）

* 约束：数据存储遵循的规则

2）查看数据表和表结构

```sql
# 查看当前数据库中已有的数据表
SHOW TABLES;

# 查看指定数据表的结构
DESC 表名;
```

3）修改表名

```sql
# 修改指定表的名称
RENAME TABLE 表名 TO 新表名;
```

4）删除数据表

```sql
# 删除指定的数据表
DROP TABLE 表名;
```

#### 8. DDL 数据类型和约束

可以在创建表的时候，为表添加一些强制性的验证，比如：**数据类型和约束**。

**数据类型**是指在创建表的时候为表中字段指定数据类型，只有数据符合类型要求才能存储起来，使用数据类型的原则是：够用就行，尽量使用取值范围小的，而不用大的，这样可以更多的节省存储空间。

常用数据类型如下：

- 整数：int，bit

- 小数：decimal

- 字符串：varchar，char

- 日期时间：date， time， datetime

- 枚举类型(enum)：比如表中有一列存性别：男、女

  ```sql
  CREATE TABLE person (
    id INT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    gender ENUM('男', '女')
  );
  ```

数据类型说明：

- decimal表示浮点数，如 decimal(5， 2) 表示共存5位数，小数占 2 位。decimal(M, N)
- char表示固定长度的字符串，如char(3)，如果填充'ab'时会补一个空格为'ab '，3表示字符数
- varchar表示可变长度的字符串，如varchar(3)，填充'ab'时就会存储'ab'，3表示字符数
- **对于图片、音频、视频等文件，不存储在数据库中，而是上传到某个服务器上，然后在表中存储这个文件的保存路径。**
- 字符串 text 表示存储大文本，当字符大于 4000 时推荐使用， 比如技术博客。

**数据约束**是指数据在数据类型限定的基础上额外增加的要求。

常见的约束如下：

- **主键 primary key**：物理上存储的顺序。 MySQL 建议所有表的主键字段都叫 id， 类型为 int unsigned。
- **非空 not null**：此字段不允许填写空值。NULL表示空
- **惟一 unique**：此字段的值不允许重复。
- **默认 default**：当不填写字段对应的值会使用默认值，如果填写时以填写为准。
- **外键 foreign key**：对关系字段进行约束， 当为关系字段填写值时， 会到关联的表中查询此值是否存在， 如果存在则填写成功， 如果不存在则填写失败并抛出异常。

**数据类型附录表**

**整数类型**

| 类型        | 字节大小 | 有符号范围(signed)                         | 无符号范围(unsigned)     |
| :---------- | :------- | :----------------------------------------- | :----------------------- |
| TINYINT     | 1        | -128 ~ 127                                 | 0 ~ 255                  |
| SMALLINT    | 2        | -32768 ~ 32767                             | 0 ~ 65535                |
| MEDIUMINT   | 3        | -8388608 ~ 8388607                         | 0 ~ 16777215             |
| INT/INTEGER | 4        | -2147483648 ~2147483647                    | 0 ~ 4294967295           |
| BIGINT      | 8        | -9223372036854775808 ~ 9223372036854775807 | 0 ~ 18446744073709551615 |

**字符串**

| 类型     | 说明                        | 使用场景                     |
| :------- | :-------------------------- | :--------------------------- |
| CHAR     | 固定长度，小型数据          | 身份证号、手机号、电话、密码 |
| VARCHAR  | 可变长度，小型数据          | 姓名、地址、品牌、型号       |
| TEXT     | 可变长度，字符个数大于 4000 | 存储小型文章或者新闻         |
| LONGTEXT | 可变长度， 极大型文本数据   | 存储极大型文本数据           |

**时间类型**

| 类型      | 字节大小 | 示例                                                  |
| :-------- | :------- | :---------------------------------------------------- |
| DATE      | 4        | '2020-01-01'                                          |
| TIME      | 3        | '12:29:59'                                            |
| DATETIME  | 8        | '2020-01-01 12:29:59'                                 |
| YEAR      | 1        | '2017'                                                |
| TIMESTAMP | 4        | '1970-01-01 00:00:01' UTC ~ '2038-01-01 00:00:01' UTC |

#### 9. DDL 数据表字段操作

为了使MySQL更加灵活，可扩展性更强，在MySQL语法中给我们添加了操作表结构（字段）的相关方法。

**思考**：

```bash
1. 为什么创建表之后？又要对表的字段进行操作？
```

在实际开发中，我们创建的数据库一般只满足第一版需求，随着日后的需求不断增加，数据维度不断扩展，还有更多的需求需要添加进来，此时，数据存储的结构就需要随之变化。

**数据表字段操作**：

1）添加表字段 **类型必须填写**

```sql
# 注意：
# 1. 约束按需求选择添加，可以没有约束，类型必须填写，并只能填写一个类型
# 2. 表字段名称不能重复，否则会出现 Duplicate column 错误
# 3. 如果列名和关键字相同，需要在列名两边加 ``
ALTER  TABLE  表名  ADD  列名  类型(长度)  [约束];

# 示例1
ALTER TABLE category ADD num INT NOT NULL;
# 示例2
ALTER TABLE category ADD `desc` VARCHAR(100);
```

**关键字做字段名称**

当我们添加的字段名称为关键字时，会出现错误，我们试着执行如下代码：

```sql
# 在category表中添加desc字段，数据类型为VARCHAR类型，最大长度为100
ALTER TABLE category ADD desc VARCHAR(100);
```

![image-20211115092716362](E:\黑马培训\MySQL笔记\assets\image-20211115092716362.png)

此时已经提示我们语法错误，因为desc为关键字，系统已经赋予了他特殊的功能。

此处，我们没有使用他原有的功能，系统认为我们书写的SQL语句语法格式存在问题。

那我们需要怎么做呢？？？ 

**很简单我们给desc添加``让系统知道他不是一个关键字即可，代码如下：**

```sql
# 在category表中添加desc字段，数据类型为VARCHAR类型，最大长度为100
ALTER TABLE category ADD `desc` VARCHAR(100);
# 查看表结构是否发生了改变
DESC category;
```

![image-20211115093054236](E:\黑马培训\MySQL笔记\assets\image-20211115093054236.png)

**注意：**这里添加的点是esc键下方的点，要使用英文输入法哦！

![image-20211115093237970](E:\黑马培训\MySQL笔记\assets\image-20211115093237970.png)

2）修改表字段

```sql
ALTER TABLE 表名 CHANGE 旧列名 新列名 类型(长度) 约束; # 类型是不可获缺的

# 示例1
ALTER TABLE category CHANGE `desc` description VARCHAR(100);
```

3）删除表字段

删除字段的方式与添加字段类似，只不过需要将ADD关键字改为DROP关键字，代码如下：

```sql
ALTER TABLE 表名 DROP 列名;

# 示例1
ALTER TABLE category DROP num;
```

**注意：此处不用指定数据类型及约束。**

例如：

```sql
# 删除分类表中num这列
ALTER TABLE category DROP num;
# 删除后，查看表结构
DESC category;
```

#### 10. DML 表记录增、改、删

**增加表记录**：

```sql
# 不指定字段插入：一次性插入一行，并且指定所有列
INSERT INTO 表 VALUES(值1, 值2, 值3...);

# 指定字段插入：一次性插入一行，并且指定插入列
INSERT INTO 表 (字段1, 字段2, 字段3...) VALUES(值1, 值2, 值3...);

# 不指定字段插入：一次性添加多条记录
INSERT INTO 表 VALUES(值1, 值2, 值3...), (值1, 值2, 值3...), ...;

# 指定字段插入：一次性添加多条记录
INSERT INTO 表 (字段1, 字段2, 字段3...) VALUES(值1, 值2, 值3...), (值1, 值2, 值3...)...;
```

**补充知识点:**

![image-20220903171959628](E:\黑马培训\MySQL笔记\assets\image-20220903171959628.png)

**更新表记录**：

```sql
# 更新所有行
UPDATE 表名 SET 字段名=值, 字段名=值, ...;

# 更新满足条件的行
UPDATE 表名 SET 字段名=值, 字段名=值, ... WHERE 条件;
```

**删除表记录**：

```sql
# 删除表中的所有数据：主键自增序列不清零
DELETE FROM 表名;

# 清空表数据：主键自增序列清零
TRUNCATE TABLE 表名;

# 删除表中满足条件的数据
DELETE FROM 表名 WHERE 条件;
```

**补充**：

**物理删除**：DELETE FROM 就是物理删除，真的将表数据删除。

**逻辑删除**：数据并没有真的从表中删除，而是将数据添加了一个删除标记。

![img](E:\黑马培训\MySQL笔记\assets\物理删除vs逻辑删除.png)

#### 11. DDL 数据约束进阶

**主键约束**：

什么是主键？

- PRIMARY KEY 约束唯一标识数据库表中的每条记录。
- 主键必须包含唯一的值。
- 主键列不能包含 NULL 值。
- 每个表都应该有一个主键，并且每个表只能有一个主键。

主键设置原则：

- 主键应当是对用户没有意义的。
- 永远也不要更新主键。
- 主键不应包含动态变化的数据，如时间戳、创建时间列、修改时间列等。
- 主键应当由计算机自动生成。

主键约束操作：

```sql
# 添加主键
# 在创建数据表时添加
CREATE TABLE person(
    id INT PRIMARY KEY,
    last_name VARCHAR(100),
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100)
);

CREATE TABLE person(
    id INT,
    last_name VARCHAR(100),
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100),
  	-- 将 id 设置为表的主键
   	PRIMARY KEY(id)
);

# 在创建后添加约束(了解)
CREATE TABLE person1(
    id INT,
    last_name VARCHAR(100),
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100)
);

# 创建表后，使用ALTER TABLE关键字添加主键
ALTER TABLE person1 ADD PRIMARY KEY (id);

# 删除主键约束
# 格式：ALTER TABLE 表名 DROP PRIMARY KEY;
ALTER TABLE person1 DROP PRIMARY KEY;


# 在创建表时添加主键自动增长
CREATE TABLE person2(
    id INT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(255),
    first_name VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255)
);

# 在创建表之后添加自动增长(了解)
ALTER TABLE person2 CHANGE id id INT AUTO_INCREMENT;
```

> 注意：
>
> 1）主键的值必须是唯一的。
>
> 2）在没设置主键自增之前，insert数据时，主键不能为NULL；在设置主键自增之后，insert数据时，主键位置可以为NULL，数据库会自动维护主键的值。

**非空约束**：

NOT NULL，非空约束，即对应列不接受空NULL值。

非空约束操作：

```sql
# 非空约束 not null
# 创建表时添加非空约束
CREATE TABLE person3(
    id INT NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100)
);
```

**唯一约束**：

UNIQUE，唯一约束，即对应列的值必须唯一。

唯一约束操作：

```sql
# 唯一约束
# 在创建表时添加唯一约束
CREATE TABLE person4 (
    id INT PRIMARY KEY,
    last_name VARCHAR(100) UNIQUE,
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100)
);
```

**默认值**：

DEFAULT，默认值，插入数据时当不填写字段对应的值会使用默认值，如果填写时以填写为准。

默认值操作：

```sql
# 默认值
# 在创建表时添加默认值
CREATE TABLE person5 (
    id INT PRIMARY KEY,
    last_name VARCHAR(100),
    first_name VARCHAR(100),
    address VARCHAR(100),
    city VARCHAR(100) DEFAULT '北京'
);
```

**建表时：字段类型 是否是主键 是否非空 是否为唯一 默认值 注释**

#### 12. DQL 简单查询

**SQL简单查询**：

```sql
# 查询表中的全部数据(所有行所有列的数据)
SELECT * FROM 表名;

# 查询表中的指定列数据(所有行指定列的数据)
SELECT 列1, 列2, ... FROM 表名;
```

#### 13. DQL 条件查询

**SQL条件查询**：

```sql
SELECT * FROM 表名 WHERE 条件;
```

WHERE支持的条件运算符：

1）比较运算符

```sql
=
>
>=
<
<=
!=：不等于
<>：不等于

# 示例
SELECT * FROM product WHERE price = 800;
SELECT * FROM product WHERE price > 600;
```

2）逻辑运算符

```sql
AND：与
OR：或
NOT：非

# 示例
SELECT * FROM product WHERE price >= 200 AND price <= 2000;
```

3）LIKE模糊查询

```sql
%：表示任意多个任意字符
_：表示任意一个字符

# 示例
SELECT * FROM product WHERE pname LIKE '%斯';
SELECT * FROM product WHERE pname LIKE '__斯';
```

4）范围查询

```sql
BETWEEN ... AND ... 表示在一个连续的范围内查询
# 示例
SELECT * FROM product WHERE price BETWEEN 800 AND 2000;

IN 表示在一个非连续的范围内查询
# 示例
SELECT * FROM product WHERE price IN (600, 800, 2000);
```

5）空判断

```sql
IS NULL：判断为空
# 示例
SELECT * FROM product WHERE category_id IS NULL;

IS NOT NULL：判断不为空
# 示例
SELECT * FROM product WHERE category_id IS NOT NULL;
```

#### 14: 做题出现的问题与知识点补充

##### 14.1 char与vachar的区别：

 **当我们要在数据库出取出char类型的数据时，需要用trim()去除掉多余的空格**

首先char的长度不可变，而varchar的长度可变，char类型的要用trim去掉多余的空格，而varchar是不需要的，char的存储方式是对英文字符占用1个字节，对一个汉字占用两个字节，而varchar的存储方式则大大不同

​              **区别一：** char类型长度是不可变的，而varchar的长度可变

![image-20220824223754283](E:\黑马培训\MySQL笔记\assets\image-20220824223754283.png)

![image-20220824235856839](E:\黑马培训\MySQL笔记\assets\image-20220824235856839.png)

##### 14.2 把数据插入表中的注意事项与其他操作注意点

**主键的特性: 非空,唯一,一个表只能有一个主键,PRIMARY KEY 约束唯一标识数据库表中的每条记录: 意思是主键必须有特性,能区分出一个表中的多行数据。**

**当你在插入时也写主键时,要先查看列表中的主键已经自增到哪里了,只能写接下来的,不能写已经写过的**

1. 要是主键设置了自增，可以不写主键对应字段的值让系统来自己指定 但是插入语句里面要写上除主键的字段外的其他对应的字段 

**形式(字段名2, 字段名3, ......)**       不指定主键值插入数据只能这样插入

**代码示例：**

```sql
insert into 表名 (字段名2, 字段名3, ....) values (值2, 值3, ....)
```

2. 设置了逐渐自增但是以不写字段的形式加入数据会遇到的问题  如果直接不写主键对应的值就插入数据 会报错

**代码示例:**

```sql
insert into 表名 values (值2, 值3, .....) # 错误sql
```

3. 插入时指定的主键的值是NULL 或者 0 系统不会按照指定的来会按照自增的顺序把主键的值自增上去 但是当你写出比表中已存在主键大是数值时,系统会按你写的来记录数据

**代码示例:**

![image-20220824230955690](E:\黑马培训\MySQL笔记\assets\image-20220824230955690.png)

**主键写NULL时的代码示例:**  结果是主键还是会按原来的顺序自增下去

![image-20220824231203743](E:\黑马培训\MySQL笔记\assets\image-20220824231203743.png)

**主键写0时的代码示例: 结果还是主键会按原来的顺序自动自增下去**

![image-20220824233247323](E:\黑马培训\MySQL笔记\assets\image-20220824233247323.png)

4. **between ... and ...是包含左边和右边的**

5. **查询结构没有说查询数据库结构的,是查询表结构 语法 desc 表名**

6. 创建数据库表时指定它的字符编码以后,数据库表的字符编码会默认继承数据库编码  所以在创建数据库时 

   最好按照这个格式 

   1.判断数据库是否存在 

   2.指定数据库的字符编码方式

   注: 在表创建时也要判断下表是否存在

**代码格式:**

```sql
create database if not exists 数据库名 charset=utf8;
```

7. **涉及表里字段的操作  sql语句的开头都要写上 ALTER TABLE**

8. **在定义字段的 类型时要尽量定义小的类型,在保证够使用的前提下,能用就行,因为越大空间占用就越大,但是也要把后续使用会遇到的情况考虑进来**

9. 在往表中插入数据时,指定字段的顺序要与值的顺序一一对应

10. 字符串和日期型数据应该被包含在括号里

11. **涉及表中数据的操作都不用在sql句子中写table, 只有涉及表结构的操作时需要写table**

12. **字符串往数据库中加数据的时候需要在字符串两边加双引号**

13. 多表联合查询的一种连接方法

    ![image-20220910144318299](E:\黑马培训\MySQL笔记\assets\image-20220910144318299.png)

14. 另外一种连接方式

    ![image-20220910144404595](E:\黑马培训\MySQL笔记\assets\image-20220910144404595.png)

15. 不包含某个文字的写法

    ![image-20220910144645035](E:\黑马培训\MySQL笔记\assets\image-20220910144645035.png)

