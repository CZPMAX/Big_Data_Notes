# Python操作数据库

**技能目标**

- 掌握Python操作Mysql
- 掌握Python操作Redis

Python可以操作很多数据库，如mysql，oracle等，对于不同数据库需要使用不同的数据库操作模块

Python本身提供了标准的数据库操作接口DB-API,DB-API是一套规范，定义了数据库访问所需要的对象和数据存取方式，**使对不同数据库的访问能够提供一致的访问接口**，以便于程序员编写程序

## 1. MySQLdb

### 1.1 DB-API使用流程

**这是python提供的标准数据库操作接口，是一套规范，使对不同数据库的访问能够提供一致的访问接口**

Python的**DB-API**,实现了多种数据库接口，当使用它连接各种数据库后，可以用相同的方式操作各种不同的数据库，使用流程如下：

1. 首先要导入DB-API模块
2. 获取数据库连接
3. 执行SQL语句和存储过程
4. 关闭数据库连接

### 1.2 MySQLdb简介

**MySQLdb**是用于**Python访问Mysql数据库的接口模块**,实现了Python数据库API操作V2.0 基于MySQL C API 上建立的

**注意:**  Python3.x 以上的版本已经不再支持MySQLdb,已经更换为pymysql

## 2. 什么是 PyMySQL？

PyMySQL 是在 Python3.x 版本中用于连接 MySQL 服务器的一个库，Python2中则使用mysqldb。

PyMySQL 遵循 Python 数据库 API v2.0 规范，并包含了 pure-Python MySQL 客户端库。

MySQLdb要快点，原因是这个是C写的，速度快

MySQLdb只支持Python2.x，还不支持3.x
可以用PyMySQL代替。安装方法：pip install PyMySQL
然后在需要的项目中，把 __init__.py中添加两行：

import pymysql
pymysql.install_as_MySQLdb()

**其他的方法与MySQLdb一样**

下面这些操作库名改一下,改为P

### 2.1 pymysql的作用

> PyMSQL的作用就是利用程序操作 MySQL 数据库，进行数据库的增、删、改、查操作。

**注意：**pymysql中利用游标对象，调用方法execute执行sql语句，之后可以有返回值, 这个返回值是一个整数值, 表示受影响的函数, 如果

是查询语句, 得到的就是结果的行数，如果是插入操作，插入几行则返回几行（例如：插入3行，3行数据受到影响，返回3），如果是删除

操作，删除几行则返回几行（例如：删除3行，3行数据受到影响，返回3），如果是修改操作，修改几行则返回几行（例如：修改3行，3

行数据受到影响，返回3）

## 3. Python中操作MySQL步骤

**操作步骤:** 导入模块 → 创建连接 → 创建游标 → 执行SQL → 关闭游标 → 关闭连接

sql语句是用游标来执行的

**图示:**

![image-20220901144107748](E:\黑马培训\assets\image-20220901144107748.png)

**步骤详解:**

### **3.1 引入模块**

```python
# 导入pymysql扩展包
  import pymysql
```

### 3.2 创建链接（创建connection链接对象）

```python
# 作用：用于和数据库建立连接，调用pymysql模块中的connect()方法
```

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

### 3.3 创建游标（通过链接对象获取游标） 

链接对象已经接到数据库里面了，所以可以使用数据库里面的对象游标工具

有了数据库的链接对象之后, 就可以对数据库执行一定的操作了 　

使用代码来链接数据库一般都需要一个中介, 来具体执行SQL语句

 一般是cursor(游标) 　创建游标对象 `cursor = conn.cursor() ` 　　

在cursor()函数中可以传入参数来设置, 查询的结果显示的结构, 默认为null, 显示的结构为: 　![img](https://images2015.cnblogs.com/blog/1003577/201706/1003577-20170608192606731-1127214678.png) 　　　

整个是一个元组, 查询得到的一个记录也是一个元组对象 　一般地, 传入参数 `cursor = conn.cursor(pymysql.cursors.DictCursor) ` 　　得到的显示结果为: 　![img](https://images2015.cnblogs.com/blog/1003577/201706/1003577-20170608192803168-444415517.png)

**整个是一个列表, 一个记录是一个字典, 键是列名**

> 游标的作用：**执行sql语句**（使用频度最高的语句为select、insert、update、delete)

```python
# 通过conn对象的cursor方法，获取游标对象
cursor = conn.cursor()
```

### 3.4 执行SQL (这里需要使用游标执行SQL语句)

```python
# 通过游标对象的execute方法，执行sql语句
# 注意：返回值是受影响的行数  是一个整数
num = cursor.execute()
```

创建好游标对象之后, 就可以使用游标对象来执行一些SQL语句 　

首先是生成SQL语句, 一般地, 为了数据库安全, 一般不要自己拼接SQL语句 　

正确的拼接SQL的方式是将传入的参数放到游标执行SQL语句的函数中 

`sql = "select * from userinfo where username=%s and password=%s" cursor.execute(sql,user,pwd) ` 　

注意: 在原有SQL语句中使用%s做占位符 　使用execute()函数来执行SQL语句 　可以将传入的参数封装成列表, 元组, 字典(此时需要在占位符上加上标记如: %(user)s 来作为键) 　　　如果需要执行多行SQL语句可以使用executemany() 

`sql = "insert into userinfo(username,password) values(%s,%s)" cursor.executemany(sql, [('egon', '12'), ('wusir', '123')]) ` 　　　　

执行SQL语句之后可以有返回值, 这个返回值是一个整数值, 表示受影响的函数, 

如果是查询语句, 得到的就是结果的行数 　　　

执行修改数据库的操作, 需要提交事务来生效 `conn.commit()`

### 3.5 执行的结果存放在哪里？

 **游标 <==> 临时表** 

**游标**是从数据表中提取出来的数据，以**临时表**的形式存放在内存中，在游标中有一个**数据指针**，在初始状态下指向的是首记录，利用fetch语句可以移动该指针，从而对游标中的数据进行各种操作，然后将操作结果写回数据表中

### 3.6 Python简单操作数据库示例

游标对象执行execute方法以后，他返回的值，只是执行了这个sql语句在表中会影响多少行数据，返回的也只是行数

```python
# 导入模块
import pymysql

# 创建数据库链接
conn = pymysql.connect(
    host='localhost',
    port=3306,
    database='travel',
    user='root',
    password='123456'
    )

# 获取游标对象
cursor = conn.cursor()

# 写sql语句
sql = 'SELECT * FROM travel'

# 执行sql语句
num = cursor.execute(sql)
# 查出来的结果放在游标缓存区里面，也能理解为存放在游标临时表中
print(num)

# 关闭链接
conn.close()
cursor.close()
```

### 3.7 获取SQL查询的具体结果

cursor游标对象中有三个方法，用于获取 SQL 查询的结果：

- fetchall()：获取查询的所有结果，返回值为tuple元祖类型【嵌套元祖】

- fetchone()：每次获取查询结果中的一条记录，返回值为tuple元祖类型【简单元祖】

- fetchmany(num)：每次获取查询结果中的num条记录，即多条记录放在一个元组内，而本身单条记录查出来也是元组类型，返回值

  为tuple元祖类型【嵌套元祖】

> 注意：调用获取数据的方法时，每次是基于上次的位置继续向后获取，获取不到数据时，返回空元祖

**示例：**

![image-20220901154438734](E:\黑马培训\assets\image-20220901154438734.png)

**游标回滚:**

![image-20220829175011581](E:\黑马培训\assets\image-20220829175011581.png)

![image-20220829175103398](E:\黑马培训\assets\image-20220829175103398.png)

![image-20220829174927717](E:\黑马培训\assets\image-20220829174927717.png)

### 3.8 DML 语句

#### 3.8.1 插入操作

**DML 语句操作前言:**

创建好游标对象之后, 就可以使用游标对象来执行一些SQL语句 

1  首先是生成SQL语句 

一般地, 为了数据库安全, 一般不要自己拼接SQL语句 　正确的拼接SQL的方式是将传入的参数放到游标执行SQL语句的函数中 

`sql = "select * from userinfo where username=%s and password=%s"    cursor.execute(sql,user,pwd) ` 　　

**注意:** 　　　在原有SQL语句中使用%s做占位符 　　　使用execute()函数来执行SQL语句 　　　

可以将传入的参数封装成列表, 元组, 字典(此时需要在占位符上加上标记如: %(user)s 来作为键) 　　

如果需要执行多行SQL语句可以使用executemany() 

`sql = "insert into userinfo(username,password) values(%s,%s)" `

 `cursor.executemany(sql, [('egon', '12'), ('wusir', '123')]) ` 　　　　

**执行SQL语句之后可以有返回值, 这个返回值是一个整数值, 表示受影响的函数, 如果是查询语句, 得到的就是结果的行数** 

执行修改数据库的操作, 需要提交事务来生效 `conn.commit()`

**操作结果图示:**

![image-20220901162232900](E:\黑马培训\assets\image-20220901162232900.png)

**控制台显示:**

![image-20220901162306188](E:\黑马培训\assets\image-20220901162306188.png)

**解释:** 控制台打印出了数字1,表示表中已经有1条数据受到影响,即已经插入一条数据,可是终端连接数据库后,却查询不到这条数据:

![image-20220901162449687](E:\黑马培训\assets\image-20220901162449687.png)

**原因:** 执行完对数据库表中数据进行改动的sql语句后,都要进行事务的提交,才会真正的执行成功,上面就是没有提交事务,才造成数据没有加

入数据库表中

**解决问题:** 添加代码 conn.commit() 即为数据库连接对象提交事务

![image-20220901162839982](E:\黑马培训\assets\image-20220901162839982.png)

**终端结果查询:**

![image-20220901162909040](E:\黑马培训\assets\image-20220901162909040.png)

#### 3.8.2 修改操作

**注意:** 在数据库中对表的数据进行修改,一定要加条件,不然一执行修改sql,整张表的这个字段的值都会被修改

**遇到的一个小问题提示:** 

```python
sql = 'UPDATE travel SET name="%s",level=%s,price=%s WHERE id=2311'
```

在这里如果给占位符带上的双引号,那么写入到数据库表中的数据也会带双引号

**修改操作示意图:**

![image-20220901164250141](E:\黑马培训\assets\image-20220901164250141.png)

**数据库表数据改变示意图:**

![image-20220901164502694](E:\黑马培训\assets\image-20220901164502694.png)

#### 3.8.3 删除操作

**注意:** 删除操作,也要加条件,不然整张的数据都会被删除

**删除操作示意图:**

![image-20220901165636373](E:\黑马培训\assets\image-20220901165636373.png)

**终端执行结果:**

![image-20220901165711603](E:\黑马培训\assets\image-20220901165711603.png)

#### 3.8.4 pymysql中的事务

PyMySQL 在对 MySQL 数据库进行操作时，**当遇到非查询(即：增、删、改)操作时，自动开启一个默认事务**，后续的 SQL 操作都在这个事务中

**事务提交或回滚**

- conn.commit()：事务提交

  **操作完成之后，如果要结果生效，需要手动进行事务的 commit 提交，否则结果被撤销，即自动进行了事务的 rollback 操作**

- conn.rollback()：事务回滚 (只在事务没有提交的时候有效)

  **撤销上面的所有sql语句对数据表的改动,最后数据库表内容与刚开始操作时一致**

### 3.9 获取最后行的id

有的时候, 我们插入数据的时候是不设置该数据的ID的, 但是我们还需要获得对该ID当做数据插入到另一个表的时候 　

如有文章表和文章媒体表 　这是就需要或的最后插入的数据的时候的ID　 

`cursor.execute(sql) print(cursor.lastrowid) `

### 3.10 关闭连接

**注意: ** 关闭连接的时候,必须游标先关闭,再关闭数据库连接

### 3.11 总结

进行python利用pymysql操作数据库取出来的数据进行操作,数据的类型都会是元组,或者是嵌套元组 这是fetchone,fetchall,fetchmany 取

数据的方法决定的,python作者定义就是单挑数据是元组,多条数据是嵌套元组

**测试代码:**

```python
# 导入模块
import pymysql

# 创建数据库链接
conn = pymysql.connect(
    host='localhost',
    port=3306,
    database='travel',
    user='root',
    password='123456'
)

# 获取游标对象
cursor = conn.cursor()

# 写sql语句
sql = 'SELECT * FROM travel'

# 执行sql语句
num = cursor.execute(sql)
# 查出来的结果放在游标缓存区里面，也能理解为存放在游标临时表中
print(num)

res = cursor.fetchmany(3)
print(res)

res1 = cursor.fetchone()
print(res1)

# 修改fetch指针位置
cursor.scroll(0, mode='absolute')

# 插入操作
# sql = 'INSERT INTO travel values (%s, %s, %s, %s, %s, %s, %s)'
# num1 = cursor.execute(sql, (2311, '平潭岛', '[福建-福州]', 'AA', 95, 4.99, 6523))
# print(num1)

# 修改操作
# sql = 'UPDATE travel SET name="%s",level=%s,price=%s WHERE id=2311'
# num2 = cursor.execute(sql, ('舟山岛', "AAA", 100))
# print(num2)

# 删除操作
sql = 'DELETE FROM travel WHERE id=%s'
num3 = cursor.execute(sql, 2311)
print(num3)
# 提交事务
conn.commit()

# 关闭链接
conn.close()
cursor.close()
```

