# 1. 索引的作用和相关使用命令

![image-20220829175124454](E:\黑马培训\MySQL笔记\assets\image-20220829175124454.png)

**新华字典：从新华字典中查一个字，怎么查询比较快？**

1）方式1：一页一页往后翻，直到找到要查的字；

2）方式2：先根据字典目录找到要查的字，然后直接翻到那一页。

索引在MySQL中也叫做“键”，它是一个特殊的文件，它保存着数据表里所有记录的位置信息，更通俗的来说，数据库索引好比是一本书前面的目录，能加快数据库的查询速度。

**索引应用场景**：数据量较大时，提高根据某些字段查询数据的效率。

**索引命令**：

```sql
SHOW INDEX FROM 表名;-- 查看指定表中有哪些索引
ALTER TABLE 表名 ADD INDEX 索引名 (列名, ...); -- 在表的指定列上添加索引
ALTER TABLE 表名 DROP INDEX 索引名; -- 删除索引
```

## 1.1 索引的介绍

索引在MySQL中也叫做“键”，它是一个特殊的文件，它保存着数据表里所有记录的位置信息，更通俗的来说，数据库索引好比是一本书前面的目录，能加快数据库的查询速度。

**应用场景:**

当数据库中数据量很大时，查找数据会变得很慢，我们就可以通过索引来提高数据库的查询效率。

## 1.2 索引的使用

**查看表中已有索引：**

- 格式：`show index from 表名;`

- 举例：

  ```
  show index from students;
  ```

  - 查看 students 表的索引，**主键列会自动创建索引**

**索引的创建：**

- 格式：

  ```
  alter table 表名 add index 索引名 (列名, ...)
  ```

  - 索引名不指定时，默认使用列名

- 举例：

  ```
  alter table students add index stu_name (Name);
  ```

  - 给 students 表的 Name 列创建索引

**索引的删除：**

- 格式： 

  ```
  alter table 表名 drop index 索引名
  ```

  - 如果不知道索引名，可以查看创表sql语句：`show create table students;`

- 举例：

  ```
  alter table students drop index stu_name;
  ```

  - 删除 students 表中的 stu_name 索引

## 1.3 案例-验证索引查询性能

**创建测试表并添加测试数据**：

1）创建测试表 `test_index`

```sql
# 创建 python 数据库
create database python charset=utf8;
# 创建 test_index 数据表
use python;
create table test_index(title varchar(10));
```

2）向 `test_index` 表中添加 10 万条数据

```python
from pymysql import connect


def main():
    # 创建数据库连接对象
    conn = connect(host='localhost', port=3306,
                   database='python', user='root', password='mysql')
    # 创建游标对象
    cursor = conn.cursor()
    # 循环向 test_index 表中添加 10 万条测试数据
    for i in range(100000):
        cursor.execute("insert into test_index values('py-%d')" % i)
    # 提交数据
    conn.commit()


if __name__ == "__main__":
    main()
```

**验证索引性能操作：**

1）未添加索引之前的查询

```sql
# 开启 sql 执行时间监测
set profiling=1;
# 查找第1万条数据'py-99999'
select * from test_index where title='py-99999';
# 查看 sql 执行的时间
show profiles;
```

![img](E:\黑马培训\MySQL笔记\assets\添加索引之前.png)

2）添加索引之后的查询

```sql
# 给 title 字段创建索引
alter table test_index add index (title);
# 再次执行 sql 查询语句
select * from test_index where title='py-99999';
# 再次查看 sql 执行的时间
show profiles;
```

![img](E:\黑马培训\MySQL笔记\assets\添加索引之后.png)

> 结果：给 title 字段添加索引之后，通过 title 字段筛选数据时，查询效率明显提升。

## 1.4 联合索引

联合索引又叫复合索引，即一个索引覆盖表中两个或者多个字段，一般用在多个字段一起查询的时候。

```sql
# 创建teacher表
create table teacher
(
    id int not null primary key auto_increment,
    name varchar(10),
    age int
);

# 创建联合索引
alter table teacher add index (name, age);
```

**联合索引的好处:**

- 减少磁盘空间开销，因为每创建一个索引，其实就是创建了一个索引文件，那么会增加磁盘空间的开销。

## 1.5 联合索引的最左原则

在使用联合索引的时候，我们要遵守一个最左原则，即index(name, age)支持 name 、name 和 age 组合查询，而不支持单独 age 查询，因为没有用到创建的联合索引。

**最左原则示例:**

```sql
# 下面的查询使用到了联合索引
# 示例1：这里使用了联合索引的name部分
select * from teacher where name='张三'
# 示例2：这里完整的使用联合索引，包括 name 和 age 部分
select * from teacher where name='李四' and age=10 

# 下面的查询没有使用到联合索引
# 示例3： 因为联合索引里面没有这个组合，只有【name】和【name age】这两种组合
select * from teacher where age=10
```

**说明:**

- 在使用联合索引的查询数据时候一定要保证联合索引的最左侧字段出现在查询条件里面，否则联合索引失效

## 1.6 MySQL中索引的优点和缺点和使用原则

**优点**：

1. 加快数据的查询速度

**缺点**：

1. 创建索引会耗费时间和占用磁盘空间，并且随着数据量的增加所耗费的时间也会增加

**使用原则**：

1. 通过优缺点对比，不是索引越多越好，而是需要自己合理的使用。
2. 对经常更新的表就避免对其进行过多索引的创建，对经常用于查询的字段应该创建索引，
3. 数据量小的表最好不要使用索引，因为由于数据较少，可能查询全部数据花费的时间比遍历索引的时间还要短，索引就可能不会产生优化效果。
4. 在一字段上相同值比较多不要建立索引，比如在学生表的"性别"字段上只有男，女两个不同值。相反的，在一个字段上不同值较多可以建立索引。

## 1.7 小结

- 索引是加快数据库的查询速度的一种手段
- 创建索引使用：`alter table 表名 add index 索引名[可选] (字段名, ...);`
- 删除索引使用：`alter table 表名 drop index 索引名;`
- 使用联合索引时要遵循最左原则

## 1.8 联合索引和使用的最左原则

- 联合索引又叫复合索引，即一个索引覆盖表中两个或者多个字段，一般用在多个字段一起查询的时候。
- 联合索引能够减少磁盘空间开销，因为每创建一个索引，其实就是创建了一个索引文件，那么会增加磁盘空间的开销。

```sql
# 创建teacher表
create table teacher
(
    id int not null primary key auto_increment,
    name varchar(10),
    age int
);

# 创建联合索引
alter table teacher add index (name, age);
```

> - 联合索引使用要遵循**最左原则**：在使用联合索引的查询数据时候一定要保证联合索引的最左侧字段出现在查询条件里面，否则联合索引失效

## 1.9 索引的优缺点及使用原则

**优点**：

1）加快数据的查询速度

**缺点**：

2）创建索引会耗费时间和占用磁盘空间，并且随着数据量的增加所耗费的时间也会增加

**使用原则**：

1）不要滥用索引，只针对经常查询的字段建立索引

2）数据量小的表最好不要使用索引

3）在一个字段上相同值比较多不要建立索引，比如性别

# 2. PyMySQL-模块简介

PyMSQL模块是Python中用来操作MySQL数据库的模块，作用就是利用程序操作 MySQL 数据库，进行数据库的增、删、改、查操作。

**安装**：

```bash
# 安装 pymysql 扩展包
pip install pymysql==1.0.2
```

**操作步骤**：

> 导入模块 → 创建连接 → 创建游标 → 执行SQL → 关闭游标 → 关闭连接

1）引入模块

```python
# 导入 pymysql 扩展包
import pymysql
```

2）创建连接(创建connection连接对象)

> **作用**：用于和数据库建立连接，调用pymysql模块中的connect()方法

```python
# 创建数据库连接对象
conn = pymysql.connect(参数列表)

参数说明：
* 参数host：连接的mysql主机，如果本机是'localhost'
* 参数port：连接的mysql主机的端口，默认是3306
* 参数database：数据库的名称
* 参数user：连接的用户名
* 参数password：连接的密码
* 参数charset：通信采用的编码方式，推荐使用utf8
```

3）创建游标(通过连接对象获取游标)

> 游标的作用：**执行sql语句**（使用频度最高的语句为select、insert、update、delete)

```python
# 通过conn对象的cursor方法，获取游标对象
cursor = conn.cursor()
```

4）执行SQL（这里需要使用游标执行SQL语句）

```python
# 通过游标对象的execute方法，执行SQL语句
# 注意：返回值是受影响的行数
num = cursor.execute(sql)
```

5）关闭游标

```python
# 注意：游标的关闭应该在连接关闭前
cursor.close()
```

6）关闭连接

```python
# 注意：先关闭游标后关闭连接
conn.close()
```

## 2.1 PyMySQL-查询操作

```python
# 导入 pymysql 模块
import pymysql

# 创建数据库连接
conn = pymysql.connect(host='localhost', port=3306,
                       user='root', password='123456',
                       database='winfunc', charset='utf8')

# 获取游标
cursor = conn.cursor()

# 准备执行的 SQL 语句
sql = 'SELECT * FROM students'

# 使用游标执行 SQL 语句
row_count = cursor.execute(sql)
print("SQL语句执行影响的行数：%d" % row_count)

# 关闭游标
cursor.close()

# 关闭连接
conn.close()
```

cursor游标对象中有三个方法，用于获取 SQL 查询的结果：

- fetchall()：获取查询的所有结果，返回值为tuple元祖类型【嵌套元祖】
- fetchone()：每次获取查询结果中的一条记录，返回值为tuple元祖类型【简单元祖】
- fetchmany(num)：每次获取查询结果中的num条记录，返回值为tuple元祖类型【嵌套元祖】

> 注意：调用获取数据的方法时，每次是基于上次的位置继续向后获取，获取不到数据时，返回空元祖

**游标回滚:**

![image-20220829175011581](E:\黑马培训\MySQL笔记\assets\image-20220829175011581.png)

![image-20220829175103398](E:\黑马培训\MySQL笔记\assets\image-20220829175103398.png)

![image-20220829174927717](E:\黑马培训\MySQL笔记\assets\image-20220829174927717.png)

## 2.2 PyMySQL-增删改操作

PyMySQL 在对 MySQL 数据库进行操作时，**当遇到非查询(即：增、删、改)操作时，自动开启一个默认事务**，后续的 SQL 操作都在这个事务中，在**操作完成之后，如果要结果生效，需要手动进行事务的 commit 提交，否则结果被撤销，即自动进行了事务的 rollback 操作**。

**问题**：

使用 PyMySQL 操作数据库，如何进行事务的开启、提交、回滚操作？

```python
# 通过通过创建的数据库连接对象，进行事务相关操作，方法如下：
conn.begin()：开启一个事务
conn.commit()：进行事务的提交操作，事务操作结果永久生效
conn.rollback()：进行事务的回滚操作，事务操作结果被撤销
```

# 3. Kettle的简介和安装

kettle文档：https://help.hitachivantara.com/Documentation/Pentaho/9.0/Products/Pentaho_Data_Integration

kettle组件文档：https://help.hitachivantara.com/Documentation/Pentaho/9.0/Products/Transformation_step_reference

**Kettle是一款国外开源的ETL工具。**

ETL（Extract-Transform-Load的缩写，即数据**抽取、转换、装载**的过程），对于企业或行业应用来说，我们经常会遇到各种数据的处理，转换，迁移，所以了解并掌握一种ETL工具的使用，必不可少的，Kettle就是强大的ETL工具。

kettle 是纯 java 开发，开源的 ETL工具，用于数据库间的数据迁移 。可以在 Linux、Windows、Unix 中运行。有图形界面，也有命令脚本，还可以二次开发。

Kettle 中文名称叫水壶，该项目的主程序员MATT 希望把各种数据放到一个壶里，然后以一种指定的格式流出。

kettle 的官网： https://community.hitachivantara.com/docs/DOC-1009855

**Kettle环境的搭建**：

* 安装 java jdk 环境
  * JAVA_HOME
  * PATH
* 解压 Kettle 软件
  * windows：**spoon.bat**
  * mac：**spoon.sh**
    * 终端：
      * cd 解压目录下
      * ./spoon.sh

## 3.1 txt数据转换Excel数据

### 1. 需求

小A现在有一个txt文件，文件内容如下：

```txt
id,name,age,gender,province,city,region,phone,birthday,hobby,register_date
392456197008193000,张三,20,0,北京市,昌平区,回龙观,18589407692,1970-8-19,美食;篮球;足球,2018-8-6 9:44
267456198006210000,李四,25,1,河南省,郑州市,郑东新区,18681109672,1980-6-21,音乐;阅读;旅游,2017-4-7 9:14
892456199007203000,王五,24,1,湖北省,武汉市,汉阳区,18798009102,1990-7-20,写代码;读代码;算法,2016-6-8 7:34
492456198712198000,赵六,26,2,陕西省,西安市,莲湖区,18189189195,1987-12-19,购物;旅游,2016-1-9 19:15
```

我们需要使用Kettle将这个文件中的数据抽取出来，然后装载到Excel中：

![img](E:\黑马培训\MySQL笔记\assets\kettle-01.png)

### 2. 新建转换

1）想要进行数据转换，我们首先要做的事情就是新建一个转换文件，我们点击窗口上的**文件 → 新建 → 转换**。

> 注：**也可以直接使用快捷键创建：Ctrl + N**

![image-20211116095029732](E:\黑马培训\MySQL笔记\assets\image-20211116095029732.png)

2）然后将需要的转换方式拖拽到右侧面板并运行即可完成转换

![image-20211116095848188](E:\黑马培训\MySQL笔记\assets\image-20211116095848188.png)

### 3. 构建Kettle的数据流图

要使用Kettle来进行数据处理，首先要做的是构建Kettle的数据流图，也就是用可视化操作像排积木的方式，把要做的事情排列出来。

![image-20211116102325879](E:\黑马培训\MySQL笔记\assets\image-20211116102325879.png)

1）将左边的核心对象中的`输入`文件夹下的`文本文件输入` 拖拽到中间空白区域。

![image-20211116102403505](E:\黑马培训\MySQL笔记\assets\image-20211116102403505.png)

2）从输出文件夹中将Excel输出组件拖拽到中间空白区域。

![image-20211116102510169](E:\黑马培训\MySQL笔记\assets\image-20211116102510169.png)

3）按住Shift键点击文本文件输入组件，移动鼠标到Excel输出，然后释放鼠标，这样就可以将两个组件连接到一起。

![image-20211116102605811](E:\黑马培训\MySQL笔记\assets\image-20211116102605811.png)

### 4. 配置数据流图中的各个组件

现在数据流的转换方式与转换方向已经确定，接下来要做的就是，配置每个组件，微调参数。

#### 4.1 配置文件文本输入组件

1）双击文本文件输入组件，在弹出窗口中点击「浏览」按钮。

![image-20211116102756959](E:\黑马培训\MySQL笔记\assets\image-20211116102756959.png)

2）选择 资料/测试数据 中的 user.txt 文件。

![image-20211116104215347](E:\黑马培训\MySQL笔记\assets\image-20211116104215347.png)

3）点击「增加」按钮，将文件加入到要抽取的数据中来。**注意：选中文件之后，一定要点击增加按钮！！！**。

![image-20211116103002661](E:\黑马培训\MySQL笔记\assets\image-20211116103002661.png)

4）点击「内容」选项卡，将分隔符修改为逗号（注意是英文状态的逗号），将编码方式修改为：UTF-8。

![image-20211116103024958](E:\黑马培训\MySQL笔记\assets\image-20211116103024958.png)

![image-20220904124543278](E:\黑马培训\MySQL笔记\assets\image-20220904124543278.png)

![image-20220904124616666](E:\黑马培训\MySQL笔记\assets\image-20220904124616666.png)

5）点击「字段」选项卡，再点击「获取字段」按钮，可以读取到txt文件中的所有字段。

![img](E:\黑马培训\MySQL笔记\assets\kettle-02.png)

![image-20220904124633674](E:\黑马培训\MySQL笔记\assets\image-20220904124633674.png)

6）点击「预览记录」按钮，查看Kettle是否能够读取到 user.txt 中的数据。

![img](E:\黑马培训\MySQL笔记\assets\kettle-03.png)



显示预览内容：

![img](E:\黑马培训\MySQL笔记\assets\kettle-04.png)

7）点击「确定」按钮保存，文本文件输入组件配置完成。

![image-20211126183839330](E:\黑马培训\MySQL笔记\assets\kettle-05.png)

#### 4.2 配置输出组件

双击输出组件,配置输出路径

![image-20220904124822597](E:\黑马培训\MySQL笔记\assets\image-20220904124822597.png)

接着检查字段查看是否能获取源数据字段

![image-20220904124909582](E:\黑马培训\MySQL笔记\assets\image-20220904124909582.png)

配置完成回到主页面,先保存项目,接着再运行

![image-20220904124959571](E:\黑马培训\MySQL笔记\assets\image-20220904124959571.png)

输出的地方与输出文件内容

![image-20220904125036549](E:\黑马培训\MySQL笔记\assets\image-20220904125036549.png)

kettel项目文件保存位置,这都是自己设置的

![image-20220904125104645](E:\黑马培训\MySQL笔记\assets\image-20220904125104645.png)

因为想要去除小数点,返回输出的execl组件,配置相应的字段

![image-20220904125230846](E:\黑马培训\MySQL笔记\assets\image-20220904125230846.png)

最终转换的完美结果

![image-20220904125257810](E:\黑马培训\MySQL笔记\assets\image-20220904125257810.png)

## 3.2 Excel数据转换MySQL数据

### 1. 提取需求

公司来了个漂亮的程序员小姐姐叫小花，她刚大学毕业，项目经理安排她这样一项工作：

有这样一个Excel文件：**user.xls**，这个文件内容是这样的。

![img](E:\黑马培训\MySQL笔记\assets\kettle-07.png)

项目经理想要让她将这些数据导入到MySQL中来。小花刚来，急得团团转，不知所措，机会来了。

**最终需求：**

- **需要从Excel中将这些用户的数据，使用Kettle抽取到MySQL中**

### 2. 准备工作

为了完成本案例，我们需要准备以下几件工作：

#### 2.1 找到小花的Excel文件

在data文件夹中可以找到user.xls文件

![img](E:\黑马培训\MySQL笔记\assets\kettle-07.png)

#### 2.2 在MySQL数据库中创建数据库

为了方便将Excel文件中的数据抽取到MySQL中，我们必须要创建一个名字叫kettle_demo的数据库，后续Excel中的数据会装载到该数据库的表中。

1）在DataGrip中右键点击MySQL连接，选择**New → Query Console**

![image-20211116162633942](E:\黑马培训\MySQL笔记\assets\image-20211116162633942.png)

2）在Datagrip中书写SQL语句并运行，我们可以看到kettle_demo数据库名称就已经创建好了

```sql
# 创建新的数据库，数据库名称为kettle_demo，字符集为utf8
CREATE DATABASE kettle_demo CHARACTER SET utf8;
```

![image-20211116163102232](E:\黑马培训\MySQL笔记\assets\image-20211116163102232.png)

3）在Kettle中加载MySQL驱动

Kettle要想连接到MySQL，必须要安装一个MySQL的驱动，就好比我们装完操作系统要安装显卡驱动一样。加载MySQL驱动只需以下三步：

- 将资料中的 MySQL jdbc 驱动包`mysql-connector-java-8.0.13.jar`导入到 `data-integration/lib`中。
- 找到 data-integration\simple-jndi\jdbc.properties 文件编辑，在末尾加上连接信息：

![image-20211116232601050](E:\黑马培训\MySQL笔记\assets\image-20211116232601050.png)

```
MYSQL_DB/type=javax.sql.DataSource  
MYSQL_DB/driver=com.mysql.cj.jdbc.Driver
MYSQL_DB/url=jdbc:mysql://localhost:3306/kettle_demo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=GMT
MYSQL_DB/user=root
MYSQL_DB/password=123456
```



**注意：账号，密码以及数据库名称根据实际情况进行更改**。

4）重启Kettle即可

### 3. 新建转换

1）想要进行数据转换，我们首先要做的事情就是新建一个转换文件，我们点击窗口上的**文件 → 新建 → 转换**。

> 注：**也可以直接使用快捷键创建：Ctrl + N**

![image-20211116095029732](E:\黑马培训\MySQL笔记\assets\image-20211116095029732-166202431720221.png)

2）然后将需要的转换方式拖拽到右侧面板并运行即可完成转换

![image-20211116095848188](E:\黑马培训\MySQL笔记\assets\image-20211116095848188-166202431720322.png)

### 4. 构建Kettle的数据流图

要使用Kettle来进行数据处理，首先要做的是构建Kettle的数据流图，也就是用可视化操作像排积木的方式，把要做的事情排列出来。

![image-20211116170258509](E:\黑马培训\MySQL笔记\assets\image-20211116170258509.png)

1）从左边的核心对象中，分别拖入「输入/Excel输入」、「输出/表输出」两个组件到中间区域

![image-20211116173015000](E:\黑马培训\MySQL笔记\assets\image-20211116173015000.png)

2）然后按住Shift键，在 「Excel输入」组件上点击鼠标左键，拖动到「表输出」组件上，连接两个组件，这样数据流图就构建好了

![image-20211116173226405](E:\黑马培训\MySQL笔记\assets\image-20211116173226405.png)

### 5. 配置数据流图中的各个组件

刚刚已经把数据流图构建好了，那么Kettle就可以将Excel文件中的数据抽取到MySQL中吗？

显然是不行的。Kettle根本不知道要将哪个Excel文件中的数据，抽取到哪个MySQL中。我们需要配置这两个组件，告诉Kettle从哪个Excel文件中抽取，以及将数据装载到哪个MySQL中。

#### 5.1 配置Excel输入组件

1）双击Excel输入组件，会弹出一个对话框，我们可以再该对话框中配置该组件。

![image-20211116173405546](E:\黑马培训\MySQL笔记\assets\image-20211116173405546.png)

2）随后我们需要找到要抽取的那个Excel文件，点击「浏览」按钮，找到 「kettle练习/data/user.xlsx」文件。

![img](E:\黑马培训\MySQL笔记\assets\image-20211116173650006.png)

3）再点击旁边的「增加」按钮，**切记：一定要点击增加按钮哦！否则没有效果！**

![img](E:\黑马培训\MySQL笔记\assets\image-20211116173723055.png)

4）在弹出菜单中，点击「Sheet1」工作簿，并点击 「>」 按钮移动到右边。

![image-20211116173803357](E:\黑马培训\MySQL笔记\assets\image-20211116173803357.png)

5）点击「字段」选项卡，点击「获取来自头部数据的字段...」按钮，Kettle会从Excel中读取第一行字段名称。

![image-20211116173830179](E:\黑马培训\MySQL笔记\assets\image-20211116173830179.png)

6）将 age 和 gender 字段的格式设置为#

![img](E:\黑马培训\MySQL笔记\assets\kettle-26.png)

7）点击「预览记录」按钮查看抽取到的数据。

![img](E:\黑马培训\MySQL笔记\assets\kettle-27.png)

![image-20220904144833299](E:\黑马培训\MySQL笔记\assets\image-20220904144833299.png)

8）点击「确定」按钮保存。

![image-20211126202031917](E:\黑马培训\MySQL笔记\assets\kettle-28.png)

#### 5.2 配置MySQL组件

要使用Kettle操作MySQL，必须要建立Kettle与MySQL的连接，否则Kettle也不知道操作哪个MySQL库。

1）双击「表输入」组件，会自动弹出配置窗口，点击「新建」按钮。

![image-20211116175623495](E:\黑马培训\MySQL笔记\assets\image-20211116175623495.png)

2）配置MySQL连接

- 输入连接名称，此处用mysql_开头，数据库名称kettle_demo为结尾
- 在连接类型列表中，选择MySQL
- 输入连接方式：JNDI
- JNDI名称填写：MYSQL_DB

![image-20211116233101692](E:\黑马培训\MySQL笔记\assets\image-20211116233101692.png)

3）点击测试按钮，测试Kettle是否能够正确连接到MySQL

![image-20211116233251933](E:\黑马培训\MySQL笔记\assets\image-20211116233251933.png)

![image-20220904143251239](E:\黑马培训\MySQL笔记\assets\image-20220904143251239.png)

4）点击确认保存，到这里数据库连接就应该创建好了。

![image-20211116233331849](E:\黑马培训\MySQL笔记\assets\image-20211116233331849.png)

#### 5.3 使用Kettle在MySQL中自动创建表

要保存数据到MySQL，必须先要创建好表。那么，我们是否需要自己手动在MySQL中创建一个表，用来保存Excel中抽取过来的数据呢？

答案是：**不需要。Kettke可以自动为我们在MySQL中创建表**。

1）输入目标表的名称为：t_user，后续Kettle将在MySQL中创建一张名为 t_user 的表格。

![image-20211116233729175](E:\黑马培训\MySQL笔记\assets\image-20211116233729175.png)

2）点击下方的「SQL」按钮，可以看到Kettle会自动帮助我们生成MySQL创建表的SQL语句，我们将age和gender字段的数据类型改为INT类型，register_date字段修改为DATETIME类型

![img](E:\黑马培训\MySQL笔记\assets\kettle-08.png)

![image-20220904143216690](E:\黑马培训\MySQL笔记\assets\image-20220904143216690.png)

3）点击执行按钮。Kettle将会让MySQL执行该SQL脚本。执行完后，可以在DataGrip中刷新在数据库，可以查看到Kettle帮助我们创建的t_user表。

![img](E:\黑马培训\MySQL笔记\assets\kettle-09.png)

4）点击「确定」按钮，保存配置。

![image-20211116234406225](E:\黑马培训\MySQL笔记\assets\image-20211116234406225.png)

### 6. 保存并启动执行Kettle转换

1）点击保存按钮保存转换。

![image-20211116234548542](E:\黑马培训\MySQL笔记\assets\image-20211116234548542.png)

2）点击工具栏上的播放按钮启动执行。

![image-20211116234627176](E:\黑马培训\MySQL笔记\assets\image-20211116234627176.png)

![image-20211116234641159](E:\黑马培训\MySQL笔记\assets\image-20211116234641159.png)

3）执行成功后，可以看到以下界面。组件上都显示了绿色的对号，执行结果中可以看到：转换完成！日志，说明Kettle的转换已经执行成功！！

![image-20211116234717792](E:\黑马培训\MySQL笔记\assets\image-20211116234717792.png)

### 7. 确认执行结果

Kettle是否已经帮助我们将Excel中的数据抽取并装载到MySQL呢？

我们需要到MySQL中看一看，t_user表中是否有数据呢。

1）在DataGrip中双击 t_user 表格，可以看到Excel中的数据都已经抽取到了MySQL。

![img](E:\黑马培训\MySQL笔记\assets\kettle-10.png)

到这里，恭喜你，程序员小姐姐小花的问题你已经帮她解决了，晚上让她请你吃个饭吧。

### 8. 小结

- Excel数据转换MySQL数据使用到的组件为：
  - Excel输入
  - 表输出
- 操作步骤为：
  - 新建转换
  - 构建kettle的数据流图
  - 配置数据流图中的各个组件
  - 保存并启动执行
- 使用Kettle连接MySQL需要先下载驱动并配置数据
- 连接MySQL后，可以使用Kettle在MySQL中自动创建表

## 3.3 MySQL表间转换

### 1. 共享数据连接

在后续的Kettle中，我们需要多次用到上面的数据库连接。那么是不是每一次都要创建数据库连接呢？这样就比较麻烦了。在Kettle中，可以将一个数据库**共享，**这样其他的Kettle转换就都可以复用该数据库连接了。

1）在转换中，点击「主对象树」，点击[DB连接]右键新建数据库连接（如果连接已经存在，则无需创建）

![image-20211116235108828](E:\黑马培训\MySQL笔记\assets\image-20211116235108828.png)

2）右键单击需要的数据库连接，选择「共享」

![image-20211116235132841](E:\黑马培训\MySQL笔记\assets\image-20211116235132841.png)

3）发现刚刚选择的数据库连接已经变成黑体字，说明已经共享成功。

![image-20211116235245153](E:\黑马培训\MySQL笔记\assets\image-20211116235245153.png)

我们兴奋地发现，在新建的转换中，都可以看到该数据库连接了。这样就避免了每次我们都需要配置该数据库连接了。

![image-20211116235307814](E:\黑马培训\MySQL笔记\assets\image-20211116235307814.png)

### 2. 需求

前面我们已经将Excel中数据抽取到了MySQL的t_user表中：

![image-20211117000146774](E:\黑马培训\MySQL笔记\assets\image-20211117000146774.png)

现在有了新需求，要将MySQL数据库中的 t_user 表中的数据抽取出来，装载到另外一张表 t_user1中

### 3. 新建转换

1）想要进行数据转换，我们首先要做的事情就是新建一个转换文件，我们点击窗口上的**文件 → 新建 → 转换**。

![image-20211116095029732](E:\黑马培训\MySQL笔记\assets\image-20211116095029732-166202579706853.png)

2）然后将需要的转换方式拖拽到右侧面板并运行即可完成转换

![image-20211116095848188](E:\黑马培训\MySQL笔记\assets\image-20211116095848188-166202579706954.png)

### 4. 构建Kettle数据流图

要使用Kettle来进行数据处理，首先要做的是构建Kettle的数据流图，也就是用可视化操作像排积木的方式，把要做的事情排列出来。

![image-20211117000405391](E:\黑马培训\MySQL笔记\assets\image-20211117000405391.png)

1）从核心对象的输入组件中，将「表输入」组件拖拽到中间的空白处。

![image-20211117000518488](E:\黑马培训\MySQL笔记\assets\image-20211117000518488.png)

2）从输出中将「 表输出」组件拖拽到中间空白处。

![image-20211117000544917](E:\黑马培训\MySQL笔记\assets\image-20211117000544917.png)

3）安装Shift键，并鼠标左键点击表输入组件，并拉动鼠标，移动到表输出组件，松开鼠标。

![image-20211117000734276](E:\黑马培训\MySQL笔记\assets\image-20211117000734276.png)

### 5. 配置Kettle数据流图中的组件

#### 5.1 配置表输入组件

1）双击表输入组件，在弹出对话框中选择「获取SQL查询语句」。

![image-20211117001948348](E:\黑马培训\MySQL笔记\assets\image-20211117001948348.png)

2）选择 t_user 表，点击确定。

![image-20211117002017875](E:\黑马培训\MySQL笔记\assets\image-20211117002017875.png)

3）在弹出对话框中选择「否」。

![image-20211117002044191](E:\黑马培训\MySQL笔记\assets\image-20211117002044191.png)

4）点击「预览」按钮，查看是否能够从MySQL读取数据。

![image-20211117002106531](E:\黑马培训\MySQL笔记\assets\image-20211117002106531.png)

5）可以看到，Kettle中可以查看到 t_user 表中的数据。

![img](E:\黑马培训\MySQL笔记\assets\kettle-11.png)

#### 5.2 配置表输出组件

1）双击表输出组件，在目标表输入t_user1。

![image-20211117002250382](E:\黑马培训\MySQL笔记\assets\image-20211117002250382.png)

2）点击[SQL]按钮，让Kettle中自动创建表结构。

![image-20211117002339555](E:\黑马培训\MySQL笔记\assets\image-20211117002339555.png)

3）可以看到，Kettle自动为我们生成了创建表的SQL语句。

![img](E:\黑马培训\MySQL笔记\assets\kettle-12.png)

![img](E:\黑马培训\MySQL笔记\assets\kettle-13.png)

4）我们可以到DataGrip中看到 t_user1 已经创建，但表中没有任何数据。

![image-20211117002524771](E:\黑马培训\MySQL笔记\assets\image-20211117002524771.png)

5）点击「确定」按钮保存。

![image-20211117002559705](E:\黑马培训\MySQL笔记\assets\image-20211117002559705.png)

### 6. 保存并启动执行

1）点击保存按钮保存该转换

![image-20211117002702920](E:\黑马培训\MySQL笔记\assets\image-20211117002702920.png)

2）点击播放按钮执行，如果看到每个组件上都打上了绿色的对号，说明转换已经执行成功。

![image-20211117002732940](E:\黑马培训\MySQL笔记\assets\image-20211117002732940.png)

3）再打开DataGrip，刷新下表格，可以看到数据已经加载到 t_user1中来了。

![image-20211117002809048](E:\黑马培训\MySQL笔记\assets\image-20211117002809048.png)

### 7. 小结

- MySQL表间转换使用到的组件为：
  - 表输入
  - 表输出
- 操作步骤为：
  - 新建转换
  - 构建Kettle的数据流图
  - 配置数据流图中的各个组件
  - 保存并启动执行

## 3.4 Kettle插入-更新组件的使用

**示例需求**：

* 将 tb_user 表中的更新/新增的数据，抽取更新/新增到 tb_user1 表中

**关键要点**：

* 表输入组件
  * 配置输入的 tb_user 表
* 插入/更新组件
  * 配置输出的 tb_user1 表：更新条件设置
  * ![image-20220829175039566](E:\黑马培训\MySQL笔记\assets\image-20220829175039566.png)

### 插入/更新组件

### 1. 组件介绍

插入/更新组件能够将Kettle抽取的数据，与某个表的数据进行对比，如果数据存在就更新，不存在就插入。

![image-20211117010625336](E:\黑马培训\MySQL笔记\assets\image-20211117010625336.png)

### 2. 需求

修改 t_user中的张三这一行数据，修改 age 为 22：

![image-20211117010724582](E:\黑马培训\MySQL笔记\assets\image-20211117010724582.png)

同时，我们想要使用Kettle将 t_user1 中的张三这一行数据的age也修改为22。

![image-20211117010746341](E:\黑马培训\MySQL笔记\assets\image-20211117010746341.png)

**思考：**

我们是否能够使用 t_user_to_t_user1.ktr 转换来进行数据的同步呢？

直接执行转换，我们发现，Kettle又将t_user表中的数据新增到了t_user1表中。

![image-20211117010927352](E:\黑马培训\MySQL笔记\assets\image-20211117010927352.png)

说明，表输入组件根本无法同步数据，只是将抽取到的数据，装载到指定的表中。

### 3. 恢复数据

刚才测试了输入组件，现在我们将数据进行恢复。

1）清空 t_user1 中的数据

![image-20211117012833916](E:\黑马培训\MySQL笔记\assets\image-20211117012833916.png)

2）点击OK执行清空

![image-20211117013145697](E:\黑马培训\MySQL笔记\assets\image-20211117013145697.png)

3）点击刷新按钮，发现数据已经清空

![image-20211117013248784](E:\黑马培训\MySQL笔记\assets\image-20211117013248784.png)

4）并将张三对应的年龄恢复为20

![image-20211117013343173](E:\黑马培训\MySQL笔记\assets\image-20211117013343173.png)

5）重新运行 t_user_to_t_user1.ktr 转换

### 4. 新建转换

1）想要进行数据转换，我们首先要做的事情就是新建一个转换文件，我们点击窗口上的**文件 → 新建 → 转换**。

![image-20211116095029732](E:\黑马培训\MySQL笔记\assets\image-20211116095029732-166202602329480.png)

2）然后将需要的转换方式拖拽到右侧面板并运行即可完成转换

![image-20211116095848188](E:\黑马培训\MySQL笔记\assets\image-20211116095848188-166202602329481.png)

### 5. 构建Kettle数据流图

流程图如下：

![image-20211117013504688](E:\黑马培训\MySQL笔记\assets\image-20211117013504688.png)

1）拖入一个表输入组件，用于读取 t_user 表中的数据

![image-20211117013916209](E:\黑马培训\MySQL笔记\assets\image-20211117013916209.png)

2）从 输出中拖入 「插入/更新」组件

![image-20211117014002135](E:\黑马培训\MySQL笔记\assets\image-20211117014002135.png)

3）按住shift，将两个组件连接起来。

![image-20211117014030213](E:\黑马培训\MySQL笔记\assets\image-20211117014030213.png)

### 6. 配置Kettle数据流图中的组件

#### 6.1 配置表输入组件

1）双击表输入组件，点击获取SQL查询语句，选择 t_user 表。

![image-20211117014141388](E:\黑马培训\MySQL笔记\assets\image-20211117014141388.png)

2）点击 预览按钮，查看Kettle是否能够从MySQL中读取数据。

![image-20211117014238158](E:\黑马培训\MySQL笔记\assets\image-20211117014238158.png)

![image-20211117014250320](E:\黑马培训\MySQL笔记\assets\image-20211117014250320.png)

#### 6.2 配置插入/更新组件

1）双击 插入/更新 组件，点击浏览按钮，找到t_user1表。

![image-20211117014343638](E:\黑马培训\MySQL笔记\assets\image-20211117014343638.png)

2）添加用来查询的关键字，设置表字段为：id，比较符为：=，流里的字段为：id。

![image-20211117014607101](E:\黑马培训\MySQL笔记\assets\image-20211117014607101.png)

3）点击「获取和更新字段」，这样Kettle将会自动更新、或插入所有的字段。

![image-20211117014630159](E:\黑马培训\MySQL笔记\assets\image-20211117014630159.png)

#### 7. 保存并启动执行Kettle转换

1）t_user中的张三这一行数据，修改 age 为 22

![image-20211117010724582](E:\黑马培训\MySQL笔记\assets\image-20211117010724582.png)

2）保存并启动该Kettle转换。

![image-20211117014722626](E:\黑马培训\MySQL笔记\assets\image-20211117014722626.png)

3）执行完后，打开DataGrip刷新 t_user1 表格，发现张三的年龄已经更新为22。

![image-20211117014741017](E:\黑马培训\MySQL笔记\assets\image-20211117014741017.png)

### 7. 小结

- 【插入/更新】组件能够将Kettle抽取的数据，与某个表的数据进行对比，如果数据存在就更新，不存在就插入。
- 插入/更新组件可以设置比较字段，比较方式等。

## 3.5 Kettle swith-case组件的使用

**示例需求**：

* 将 tb_user 表中的数据，抽取并按照性别输入到 3 个不同的 Excel 文件中

**关键要点**：

* 表输入组件
  * 配置输入的 tb_user 表
* swith-case 组件
  * 配置输出条件
    * 男 0 → user_男.xls
    * 女 1 → user_女.xls
    * 其他 → user_其他.xls

* Excel输出组件-3个
  * 配置 3 个输出的 Excel 组件

### switch/case组件

### 1. switch/case介绍

有一天，体育老师要让班上的男女同学分别排成两队。但这个班上还有几名同学，很特殊——他们是**蜘蛛**！！所以，机智的体育老师需要把他们排成三队，男同学一队，女同学一队，蜘蛛一队。

![image-20211117092914439](E:\黑马培训\MySQL笔记\assets\image-20211117092914439.png)

体育老师要做一件非常重要的事情：判断学生是男孩还是女孩、或者是蜘蛛，然后让他们各自到指定的队伍中。

体育老师做的事情，我们同样也会在Kettle中会经常用来。在Kettle中，switch/case组件可以来做类似于体育老师要做的事情——判断。

kettle中给我们提供了一种实现判断的组件叫做switch/case。

![image-20211117102044217](E:\黑马培训\MySQL笔记\assets\image-20211117102044217.png)

### 2. 需求

从 t_user 表中读取所有用户数据，我们需要将性别为男的用户导出到一个Excel、性别为女的导出到另外一个Excel、性别其他的导出到另一个Excel。

**效果图如下：**

![img](E:\黑马培训\MySQL笔记\assets\kettle-14.png)

### 3. 构建Kettle数据流图

1）将表输入组件拖拽到中间的空白区域。

![image-20211117102744870](E:\黑马培训\MySQL笔记\assets\image-20211117102744870.png)

2）从流程文件夹中将 Switch/case 组件拖拽到中间的空白区域。

![image-20211117102835440](E:\黑马培训\MySQL笔记\assets\image-20211117102835440.png)

3）再分别拖入三个Excel输出组件。

![img](E:\黑马培训\MySQL笔记\assets\kettle-15.png)

4）按住shift键，将组件按照下图方式连接起来。

![img](E:\黑马培训\MySQL笔记\assets\kettle-16.png)

### 4. 配置表输入组件

#### 4.1 配置表输入组件

双击表输入组件，指定从 t_user 表中抽取数据。

![image-20211117103034996](E:\黑马培训\MySQL笔记\assets\image-20211117103034996.png)

#### 4.2 配置switch/case组件

此处要指定，按照性别来判断输出到Excel文件。需要设置  Switch字段为gender，在Case值列表中将值分别改为0、1。如果gender是0的话，则将数据装载到  Excel输出-男；如果gender是1的话，将数据装载到 Excel输出-女；如果gender是其他的值，将数据装载到 Excel输出-其他。

![img](E:\黑马培训\MySQL笔记\assets\kettle-17.png)

#### 4.3 配置Excel输出组件

**Excel输出-男**

双击Excel输出组件，分别指定输出到指定的文件夹中。

![img](E:\黑马培训\MySQL笔记\assets\kettle-18.png)

获取字段，将age和gender字段的格式设置为0。

![img](E:\黑马培训\MySQL笔记\assets\kettle-19.png)

**Excel输出-女**

双击Excel输出组件，分别指定输出到指定的文件夹中。

![img](E:\黑马培训\MySQL笔记\assets\kettle-20.png)

获取字段，将gender和age字段的格式设置为0。

![img](E:\黑马培训\MySQL笔记\assets\kettle-21.png)

**Excel输出-其他**

双击Excel输出组件，分别指定输出到指定的文件夹中。

![img](E:\黑马培训\MySQL笔记\assets\kettle-22.png)

获取字段，将gender和age字段的格式设置为0。

![img](E:\黑马培训\MySQL笔记\assets\kettle-23.png)

### 5. 启动执行

保存并启动该Kettle转换。

![img](E:\黑马培训\MySQL笔记\assets\kettle-24.png)

Kettle会自动生成三个Excel文件，一个文件保存了性别为男的用户，另一个文件保存了性别为女的用户，还有一个保存了性别为其他的用户。

![image-20211126201005265](E:\黑马培训\MySQL笔记\assets\kettle-25.png)

### 6. 小结

- switch/case提供了一种条件判断的实现。

## 3.6 Kettle SQL脚本组件使用

**示例需求**：

* 使用Kettle执行SQL脚本，将 t_user1 表中的数据清空

**关键要点**：

* 执行SQL脚本组件
  * 配置执行的 SQL 语句

### SQL脚本组件

接下来，我们来讲解一个高级用法。在实际开发中，也经常容易使用得到。假设我们有一段SQL脚本，想要用Kettle来执行，此时该使用哪个组件呢？没错，在Kettle中有专门的SQL脚本组件。

### 1. 组件介绍

执行SQL脚本组件，可以让Kettle执行一段SQL脚本。我们可以利用它来自动执行某些操作。

### 2. 需求

使用Kettle执行SQL脚本，将 t_user1 表中的数据清空。

### 3. 构建Kettle数据流图

我们在核心对象中，将脚本文件夹下的【执行SQL脚本】拖拽到右侧的空白中，如图所示：

![image-20211117104502318](E:\黑马培训\MySQL笔记\assets\image-20211117104502318.png)

### 4. 配置Kettle数据流图组件参数

我们双击【执行SQL脚本】组件，选择要连接的数据库，以及要执行的SQL命令。

我们要将 t_user1 表中的数据清空，所以脚本中要执行的语句如下：

```sql
TRUNCATE TABLE t_user1;
```

![image-20211117104515121](E:\黑马培训\MySQL笔记\assets\image-20211117104515121.png)

### 5. 启动执行

保存并启动该Kettle转换。

![image-20211117104559855](E:\黑马培训\MySQL笔记\assets\image-20211117104559855.png)

我们发现，t_user1中的数据已经被完全清除掉了。说明：Kettle已经帮助我们执行了SQL脚本。

![image-20211117104547775](E:\黑马培训\MySQL笔记\assets\image-20211117104547775.png)

### 6. 小结

- 执行SQL脚本组件，可以让Kettle执行一段SQL脚本。我们可以利用它来自动执行某些操作。

## 3.7 Kettle 设置转换参数

**示例需求**：

* 使用Kettle执行SQL脚本删除 tb_user1 表中指定省份的用户，启动时可以指定具体省份

**关键要点**：

* 配置 Kettle 转换参数
* 执行SQL脚本组件
  * 配置执行的 SQL 语句，使用转换参数

### 设置转换参数

### 1. 需求

让Kettle帮助我们删除指定省份的用户，具体删除哪个省份不确定，由执行Kettle时指定。

首先我们可以使用【执行SQL脚本】组件来删除某个省份的用户，类似下图：

![image-20211117105411506](E:\黑马培训\MySQL笔记\assets\image-20211117105411506.png)

```sql
DELETE FROM t_user1 WHERE province = '北京市';
```

但如果把北京市写在执行SQL脚本组件中，那么每次删除的都是北京市的用户。而此处的需求是，要执行Kettle转换时，我们需要==手动来指定要删除哪个省份的用户==。

此时就需要用到Kettle中的参数了。

### 2. 参数的使用方法

双击Kettle转换的空白处，会弹出转换属性窗口。我们可以在属性窗口中找到命名参数选项卡，在里面配置Kettle转换所需要的参数。

例如：下图设置了一个叫做`province`的参数

![image-20211117105630112](E:\黑马培训\MySQL笔记\assets\image-20211117105630112.png)

### 3. 在SQL脚本组件中使用Kettle转换参数

```sql
DELETE FROM t_user1 WHERE province = '${province}’;
```

通过 ${province} 可以应用Kettle配置的转换参数，而且一定要勾选上：「变量转换」

![image-20211117105805628](E:\黑马培训\MySQL笔记\assets\image-20211117105805628.png)

### 4. 运行带参数的转换

在运行转换时，可以指定参数的值，例如：此处指定要删除的城市为北京市。

![image-20211117105834286](E:\黑马培训\MySQL笔记\assets\image-20211117105834286.png)

运行完后，我们发现：北京市的用户已经被删除了。

![image-20211117105850835](E:\黑马培训\MySQL笔记\assets\image-20211117105850835.png)

再来测试下删除河南省的用户:

![image-20211117105919829](E:\黑马培训\MySQL笔记\assets\image-20211117105919829.png)

同样，我们发现当执行完Kettle转换后，河南省的用户也被删除了。

![image-20211117105932731](E:\黑马培训\MySQL笔记\assets\image-20211117105932731.png)

### 5.小结

- 我们可以在属性窗口中找到命名参数选项卡，在里面配置Kettle转换所需要的参数。
- 我们在执行转换时，可以根据参数不同，发挥不同的转换效果。

## 3.8 Kettle 作业(job)的设置

**示例需求**：

* 每5秒钟执行一次Kettle转换，将 user.xls 中的数据抽取转换加载到 tb_user 表中

**关键要点**：

* 新建 job
  * 开始组件：设置定时
  * 转换组件：选择配置转换
  * 成功组件

### 作业（Job）开发

### 1. 需求

前几天帮助程序员小姐姐小花解决了使用Kettle从Excel中抽取数据到MySQL问题，小姐姐特别高兴，请你吃了一顿饭，好一顿魂牵梦绕。小姐姐好几天都没有联系了，今天小姐姐又抱着电脑来找你了。你知道，又有新的问题来了。

项目经理要求小姐姐小花能够每5秒钟执行一次Kettle转换，也就是每5秒钟将Excel中的数据抽取并装载到MySQL中。怎么实现呢？

要实现这个需求，我们需要学习Kettle的JOB，也就是**作业**。

**Kettle中的作业（job）定义了转换应该如何执行，可以配置转换来进行定时执行。**

**提炼需求：**

每5秒钟执行一次Kettle转换，也就是每5秒钟将Excel中的数据抽取并装载到MySQL中。

**效果图如下：**

![image-20211117110138747](E:\黑马培训\MySQL笔记\assets\image-20211117110138747.png)

### 2. 创建作业

点击文件，选择新建/作业按钮，就可以创建作业了。

![image-20211117110237111](E:\黑马培训\MySQL笔记\assets\image-20211117110237111.png)

### 3. 构建Kettle数据流图

1）将核心对象中，通用文件夹下的 Start 组件拖拽到中间的空白区域。

![image-20211117110317865](E:\黑马培训\MySQL笔记\assets\image-20211117110317865.png)

2）将 通用文件夹的「转换」组件拖拽到中间的空白区域。

![image-20211117110343601](E:\黑马培训\MySQL笔记\assets\image-20211117110343601.png)

3）将通用文件夹中的「成功」组件拖拽到中间的空白区域。

![image-20211117110358004](E:\黑马培训\MySQL笔记\assets\image-20211117110358004.png)

4）同样使用Shift键，将组件都连接起来。

![image-20211117110420844](E:\黑马培训\MySQL笔记\assets\image-20211117110420844.png)

### 4. 配置作业流图组件

#### 4.1 配置转换组件

1）配置转换这里选择作业中要执行的转换，此处选择之前开发好的excel_to_mysql.ktr即可。

![image-20211117110525712](E:\黑马培训\MySQL笔记\assets\image-20211117110525712.png)

**注意：**此处要先保存作业，然后再配置转换。

![image-20211117110550731](E:\黑马培训\MySQL笔记\assets\image-20211117110550731.png)

#### 4.2 配置Start启动组件

![image-20211117110825566](E:\黑马培训\MySQL笔记\assets\image-20211117110825566.png)

### 5. 启动运行作业

点击播放箭头启动作业，并观察数据库中的数据是否会5秒钟增加一次。

![image-20211117110849477](E:\黑马培训\MySQL笔记\assets\image-20211117110849477.png)

我们看到数据每隔5秒钟就会增加一次，作业执行完成。

### 6. 小结

- Kettle中的作业(job）定义了转换应该如何执行，可以配置转换来进行定时执行。
- 配置作业前，要先保存作业，然后再配置转换。

## 3.9 Kettle案例-销售数据处理

**示例需求**：

* 将 sales.xlsx 销售数据，每天 00:00 同步更新/插入到 sales_data 数据表中

**关键要点**：

* 新建转换
  * Excel 输入组件
    * 配置输入的 sales.xlsx 文件
  * 表插入/更新组件
    * 配置输出的 sales_data 数据表
* 新建 job
  * 开始组件：设置定时
  * 转换组件：选择上一步新建的转换
  * 成功组件

![image-20220829174949162](E:\黑马培训\MySQL笔记\assets\image-20220829174949162.png)

### 某公司销售数据案例

### 1. 需求

现有零售门店销售数据一份，需要定期同步到总公司的服务器中。

由于该门店数据都是记录在excel中，所以我们需要定期将数据进行写入数据库，且该门店可能会对前一天的数据进行校验修改，所以，我们同步数据的准则就是：

- 如果该订单已存在，就修改其订单内容。
- 如果该订单不存在，则新增订单。

经与公司协商，决定同步周期设置为一天同步一次，同步时间设置在晚上12点，不影响公司正常销售。

![image-20211117231647107](E:\黑马培训\MySQL笔记\assets\image-20211117231647107.png)

**根据上述情况我们总结如下需求：**

- 将Excel中的数据同步到MySQL数据库中
- 定期同步数据
- 同步数据时如果订单存在就更新，如果订单不存在就新增

**使用到的知识：**

1）创建转换

- 输入：Excel输入组件
- 输出：插入/更新组件

![image-20211117231547176](E:\黑马培训\MySQL笔记\assets\image-20211117231547176.png)

2）创建作业

- 定时循环启动
- 添加转换组件
- 添加成功组件

![image-20211117231506360](E:\黑马培训\MySQL笔记\assets\image-20211117231506360.png)

### 2. 创建转换

#### 2.1 新建转换

想要进行数据转换，我们首先要做的事情就是新建一个转换文件，我们点击窗口上的**文件 → 新建 → 转换**。

![image-20211116095029732](E:\黑马培训\MySQL笔记\assets\image-20211116095029732-1662026252763142.png)

#### 2.2 构建 Kettle 数据流图

1）首先将Excel输入组件和插入/更新组件拖拽到流程图中。

![image-20211117232119807](E:\黑马培训\MySQL笔记\assets\image-20211117232119807.png)

2）按住shift键，将两个组件进行关联。

![image-20211117232155882](E:\黑马培训\MySQL笔记\assets\image-20211117232155882.png)

#### 2.3 配置Kettle数据流图中的组件

**Excel输入组件配置**：

1）先修改表格类型，选择`Excel 2007 XLSX(Apache POI)`，然后点击浏览，获取目标Excel。

![image-20211117233235783](E:\黑马培训\MySQL笔记\assets\image-20211117233235783.png)

2）点击增加，将刚才选择的路径添加到下方的选中的文件中。

![image-20211117233453726](E:\黑马培训\MySQL笔记\assets\image-20211117233453726.png)

3）在弹出菜单中，点击「Sheet1」工作簿，并点击 「>」 按钮移动到右边。

![image-20211117234646896](E:\黑马培训\MySQL笔记\assets\image-20211117234646896.png)

4）选择字段标签，点击获取来自头部数据的字段，点击确定，Excel输入组件配置完成。

![img](E:\黑马培训\MySQL笔记\assets\image-20211202103927833.png)

**插入/更新组件配置**：

1）双击插入/更新组件，进入组件配置界面。

- 点击新建进行MySQL连接操作：
- 连接名称：mysql_kettle_demo
- 连接类型为：Mysql
- 连接方式为：JNDI
- JNDI名称为：MYSQL_DB

配置完成后点击确定，回到上一级菜单。

![image-20211117234124788](E:\黑马培训\MySQL笔记\assets\image-20211117234124788.png)

3）在用来查询的关键字中，设置查询规则为表字段中的`单据编码` = 流里的字段`单据编码`，然后点击获取和更新字段，最后点击确定，完成`插入/更新`组件的配置。

![image-20211118002055453](E:\黑马培训\MySQL笔记\assets\image-20211118002055453.png)

**注意：**

- 如果此时没有sales_data表，则点击下方的SQL按钮，在弹出窗口中点击执行，即可以帮我们自动创建表。
- 为了方便检索，建议将单据编码改为char类型数据

![img](E:\黑马培训\MySQL笔记\assets\image-20211202104011202.png)

#### 2.4 保存转换

此处我们保存为`Excel_to_mysql_sales.ktr`文件。

![image-20211118003851172](E:\黑马培训\MySQL笔记\assets\image-20211118003851172.png)

### 3. 创建作业（Job）

#### 3.1 新建作业

点击文件，选择新建/作业按钮，就可以创建作业了。

![image-20211117110237111](E:\黑马培训\MySQL笔记\assets\image-20211117110237111-1662026252765143.png)

#### 3.2 构建Kettle数据流图

1）将核心对象中，通用文件夹下的 Start 组件拖拽到中间的空白区域。

![image-20211117110317865](E:\黑马培训\MySQL笔记\assets\image-20211117110317865-1662026252765144.png)

2）将 通用文件夹的「转换」组件拖拽到中间的空白区域。

![image-20211117110343601](E:\黑马培训\MySQL笔记\assets\image-20211117110343601-1662026252765145.png)

3）将通用文件夹中的「成功」组件拖拽到中间的空白区域。

![image-20211117110358004](E:\黑马培训\MySQL笔记\assets\image-20211117110358004-1662026252765147.png)

4）同样使用Shift键，将组件都连接起来。

![image-20211117110420844](E:\黑马培训\MySQL笔记\assets\image-20211117110420844-1662026252765146.png)

#### 3.3 配置作业流图组件

**配置Start启动组件**：

1）双击Start组件，勾选重复，定时类型为天，时间为每天的00：00。

![image-20211118004111067](E:\黑马培训\MySQL笔记\assets\image-20211118004111067.png)

**配置转换组件**：

1）配置转换这里选择作业中要执行的转换，此处选择之前开发好的`Excel_to_mysql_sales.ktr`即可。

![image-20211118004443487](E:\黑马培训\MySQL笔记\assets\image-20211118004443487.png)

2）点击确定完成转换配置。

![image-20211118004531466](E:\黑马培训\MySQL笔记\assets\image-20211118004531466.png)

3）全部配置完成后建议保存作业。

![image-20211118004706071](E:\黑马培训\MySQL笔记\assets\image-20211118004706071.png)

### 5. 启动运行作业

点击播放箭头启动作业，可以适当调小重复周期，查看效果。

![image-20211118005021650](E:\黑马培训\MySQL笔记\assets\image-20211118005021650.png)

> 注意：插入/更新时速度较慢，且该Excel数据量较大，执行作业时请耐心等待，或者减少数据量，先查看效果。

到现在为止，我们已经完成了所有需求。

