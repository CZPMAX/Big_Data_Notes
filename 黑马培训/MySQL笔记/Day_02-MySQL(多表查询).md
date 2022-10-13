[TOC]

**Day02-MySQL基础**

```shell
# 1. DQL数据查询语言【重点】
	排序查询：ORDER BY 列名
	聚合函数：COUNT、MAX、MIN、AVG、SUM
	分组查询：GROUP BY 列名、HAVING
	分页查询：LIMIT M,N
# 2. 多表关联查询【重点】
	表之间的3种关联关系：一对多、一对一、多对多
	外键约束：A和B
	连接查询：内连接、左连接、右连接、全连接
	自连接
```

**各种关系对应的例子:**

![image-20220826211017513](E:\黑马培训\MySQL笔记\assets\image-20220826211017513.png)

#### 1.DQL 排序查询

**多字段同时排序不需要再写ORDER BY 写在一个ORDER BY里 用逗号分割就行**

**SQL排序查询**：

```sql
SELECT 
	*
FROM 表名
# 按照指定的列对查询的结果进行排序，默认排序方式是ASC
# ASC：ascending，表示升序
# DESC：descending，表示降序
ORDER BY 列1 ASC|DESC, 列2 ASC|DESC, ...;
```

#### 2.DQL 聚合函数

**日期函数,文本函数,字符串函数等不是聚合函数,所以可以用在where后面**

成绩表，计算学生成绩平均分？计算学生成绩最大值？计算学生成绩最小值？

聚合函数又叫组函数，通常是对表中的数据进行统计和计算，一般结合分组(GROUP BY)来使用，用于统计和计算分组数据。

**常用的聚合函数**：

- COUNT(col)：表示求指定列的总记录数
- MAX(col)：表示求指定列的最大值
- MIN(col)：表示求指定列的最小值
- SUM(col)：表示求指定列的和
- AVG(col)：表示求指定列的平均值

> 注意：
>
> - **聚合函数的计算会忽略 NULL 值**。
> - 例如：求四个产品的价格平均值，如果有一个价格是NULL，则忽略，求其他三个商品的平均值。

#### 3.DQL 分组查询

成绩表，分别计算男生和女生的平均成绩？计算不同班级的学生成绩的平均分？

**mysql中having后字段要求**                                        

having，用于条件筛选，要与group by一起使用， having并不是只能跟在group by后面写，也能单独写，但是这个字段必须被select选择,他是分组筛选的意思。单独使用的意义不大。。
 与`where`不同的是，`having`作用于分组。
 `where`用于筛选记录，发生在聚集函数作用之前，

having函数体多为聚合函数 。

 与`group by`联用，
 `having`之后的条件判断的字段必须是聚合函数返回的结果。

**having子句中的列名必须出现在 SELECT or GROUP BY的子句中,这是对的**

**分组查询就是将查询结果按照指定字段进行分组，指定字段数据值相同的分为一组。**

```sql
-- group by 后面加的字段是在表中要操作的字段  select 后面的字段是要查的字段 也能用聚合函数
-- having 后面可以加聚合函数 where后面不能加
-- 分组之后     SELECT 后面只能加查询分组字段,和使用聚合函数,其他字段不能查询,其他字段能加的时候,这个字段的值必须唯一,测试版sql查询其他字段,默认返回第一条数据
-- 把分组字段同类型的分为一组  但是这组内有多条数据  先分组再聚合
-- group by category_id 意思是把这个字段数据相同的分为一组
-- 示例1：分组后查看表中有哪些category_id
```

**图示分组拆分:**

![image-20220826205457846](E:\黑马培训\MySQL笔记\assets\image-20220826205457846.png)

上面图示把category_id  相同的分为了一组

**分组查询语法**：

```sql
SELECT
    分组字段...,
    聚合函数(字段)...
FROM 表名
GROUP BY 分组字段1, 分组字段2...
HAVING 条件表达式;
```

- GROUP BY 分组字段：是指按照指定列的值对数据进行分组。
- **分组之后，可以查询每一组的分组字段，或对每组的指定列进行聚合操作。**
- HAVING 条件表达式：用来过滤分组之后的数据。

**HAVING 与 WHERE 的区别：**

- HAVING 是在分组后对数据进行过滤，WHERE 是在分组前对数据进行过滤。
- HAVING 后面可以使用聚合函数(统计函数)，WHERE后面不可以使用聚合函数。

```sql
-- 注意：分组聚合操作时，SELECT之后，除了分组字段和聚合函数可以查询，其他的不能查询（会报错）,仅对这个表而言,遇到多表连接查询可以查别的表的字段
SELECT
    category_id,
    pid,
    MAX(price)
FROM product
GROUP BY category_id;
```

如果查询了其他的字段还能查出来,就说明测试模式没有关闭,他会默认返回每一组你查字段的第一条数据

如果出现上面的 sql 语句不报错，执行下面的sql，关闭mysql的测试模式：

```sql
SET @@global.sql_mode = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
```

**去重函数distinct:**

![image-20220826210045302](E:\黑马培训\MySQL笔记\assets\image-20220826210045302.png)

这里要把整个后面当做一个整体 即后面两个字段都相同的会被去除

**group by 与 order by 一起连用时的注意事项:**

```sql
SELECT id, uid, money, datatime FROM reward GROUP BY uid ORDER BY money DESC;
```

得到如下结果：

![img](https://img.jbzj.com/file_images/article/202010/20201028111947762.jpg?202092811208)

没有得到我们需要的结果，**这是因为group by 和 order by 一起使用时，会先使用group by 分组，并取出分组后的第一条数据，所以后面的order by 排序时根据取出来的第一条数据来排序的**，但是第一条数据不一定是分组里面的最大数据。

总之分组多字段的group by直接用,号来分割多个字段就行**多表连接时的group by 分组查询 select只能挑选的数据是分组查询的字段和聚合查询的结果,这个仅对被分组的那个小表来说,在这个分组里的字段我们不能查询,但是在其他小表里面的字段我们是可以查询的**  多表连接只是把三个表连在一起输出了,但是实质上他们还是单独的小表

**补充知识点:**

- group by是对数据库表中的数据进行聚合操作的,相当于多进一出,所以进行排序操作时,只会默认重每个分组中取一条数据出来进行操作,而partition by是进行分区操作操作的,相当于多进多出,聚合的结果会赋值给分区内的所有行.所以排序操作可以在partition by中执行.

**老师解释图示:**

![image-20220903165836426](E:\黑马培训\MySQL笔记\assets\image-20220903165836426.png)

**group by 聚合操作图示:**

![image-20220903170018331](E:\黑马培训\MySQL笔记\assets\image-20220903170018331.png)

**PARTITION BY 聚合操作:**

![image-20220903171719829](E:\黑马培训\MySQL笔记\assets\image-20220903171719829.png)

![image-20220903171651619](E:\黑马培训\MySQL笔记\assets\image-20220903171651619.png)

**聚合操作的结果每行数据都会有**

#### 4. DQL 分页查询

**分页查询语法**：

```sql
SELECT 
	字段列表 
FROM 表名
LIMIT M, N;
```

* M表示开始行索引，默认是0，代表从下标M的位置开始分页
* N表示查询条数，即提取多少条数据

```sql
分页公式：假设每页n条，获取第i页，LIMIT i*(n-1), n
```

**limit没有获取最值的功能 但是与order by 连用可以获取表中的最值**

**示例**：

```sql
-- 示例1：获取 product 表中的第一条记录
SELECT * FROM product LIMIT 0, 1;

-- 示例2：获取 product 表中下标为2记录开始的2条记录
SELECT * FROM product LIMIT 2, 2;

-- 示例4：当分页展示的数据不存在时，不报错，只不过查询不到任何数据
SELECT
    *
FROM product
WHERE category_id = 'c002'
ORDER BY price
LIMIT 25, 2; -- 没有第26行，但是结果不会报错，只是没有查询结果
```

```sql
-- SQL语句的执行顺序：SQL语句的执行顺序往往和我们写sql的顺序是不一样的！！！

SELECT
	分组字段,
	聚合函数(字段),
	...
FROM 表名
WHERE 条件
GROUP BY 分组字段
HAVING 条件
ORDER BY 字段, ...
LIMIT M, N;
```

**小理解图:**

![image-20220826211059324](E:\黑马培训\MySQL笔记\assets\image-20220826211059324.png)

#### 5. 多表关联查询-表之间的3种关联关系

**多表查询时,就要给每个表起别名,选择查询字段时要以表别名.字段名字的方式查询**

实际开发中，一个项目通常需要很多张表才能完成，而这些表之间存在着某些联系。

表之间的关系可以分为如下 3 种：

> 假设有 A 和 B 两张表

* **一对多关系**：A表的一行记录对应B表的多行记录，反过来B表的一行记录只对应A表的一行记录
  * 举例：商品分类和商品表

![img](E:\黑马培训\MySQL笔记\assets\一对多.png)

* **多对多关系**：A表的一行记录对应B表的多行记录，反过来B表的一行记录也对应A表的多行记录

  * 举例：学生表和课程表(选课关系)

  ![img](E:\黑马培训\MySQL笔记\assets\多对多.png)

* **一对一关系**：A表的一行记录只对应B表的一行记录，反过来B表的一行记录也只对应A表的一行记录

  * 举例：员工基础信息表、员工详细信息表

  ![img](E:\黑马培训\MySQL笔记\assets\一对一.png)

**表关系思考题：**

```
部门表和员工表：一对多
用户表和收货地址表：一对多
客户表和产品表【订购关系】：多对多
个人信息表和身份证表：一对一
```

#### 6. 多表关联查询-外键约束

假设有两张表A和B，B表的某列引用了A表的主键列，则表的这一列称为B表的**外键列**(Foreign Key)，其中A表称为**主表**，B表称为**从表**。

> 在一对多关联关系建表时，在从表(多方)创建一个字段，字段作为外键指向主表(一方)的主键

**外键约束语法**：

```sql
CONSTRAINT FOREIGN KEY (外键字段) REFERENCES 主表名(主键)

-- 示例1：新建分类表 category 和 商品表 product
# 创建分类表
CREATE TABLE category (
    cid   VARCHAR(32) PRIMARY KEY, # 分类id
    cname VARCHAR(100) # 分类名称
);

DESC category;

# 商品表
CREATE TABLE products (
    pid         VARCHAR(32) PRIMARY KEY,
    pname       VARCHAR(40),
    price       DOUBLE,
    category_id VARCHAR(32),
    # CONSTRAINT 约束
    # REFERENCES 引用
    CONSTRAINT FOREIGN KEY (category_id) REFERENCES category (cid) # 添加外键约束
);
```

**外键约束的作用**：

* 保证插入数据的准确性：从表中外键的值在主表主键中必须有对应的值

* 保证删除数据的完整性：主表的主键值被从表外键引用之后，主表中对应的记录不能被删除

  **图示:**

  ![image-20220826210700937](E:\黑马培训\MySQL笔记\assets\image-20220826210700937.png)

![image-20220826210834772](E:\黑马培训\MySQL笔记\assets\image-20220826210834772.png)

![image-20220826210809613](E:\黑马培训\MySQL笔记\assets\image-20220826210809613.png)

**外键约束的缺点**：

* 过分强调或者说使用主键／外键会平添开发难度
* 添加外键，也会降低数据增删改的性能
* **注意：实际开发，很少使用外键约束，而是从代码层面保持表之间的关系**



**外键其他操作**：

```sql
-- 查看表的约束
SHOW CREATE TABLE 表名;
-- 删除外键约束
ALTER TABLE 表名 DROP FOREIGN KEY 外键约束名;

-- 示例：
-- 查看表的约束
SHOW CREATE TABLE products;
-- 删除外键约束
ALTER TABLE products DROP FOREIGN KEY products_ibfk_1;
```

#### 7. 多表关联查询-关联查询操作

笛卡尔积：将左表的每一行数据和右表的每一行数据分别进行关联，组成新的数据。

```
SELECT
	*
FROM 表1 --左表
JOIN 表2 --右表;
```

有条件连接：

```
SELECT
	*
FROM 表1 --左表
JOIN 表2 --右表
ON 表1.字段=表2.字段;
```

**关联查询的语法**：

```sql
SELECT
    *
FROM 左表
INNER|LEFT|RIGHT|FULL JOIN 右表
ON 左表.列名 = 右表.列名 OR 左表.列名 = 右表.列名;
```

**关联查询的 4 种分类**：

1）**内连接：INNER JOIN，简写为 JOIN**

* 也称为等值连接，返回两张表都满足条件的部分(交集)

* 左右两表关联时，满足关联条件的数据，才会出现在最终的关联结果中

* 能满足配对的条件的字段,每个字段都有值  不能满足配对条件的 不取

![img](E:\黑马培训\MySQL笔记\assets\内连接.png)

![image-20220830195228676](E:\黑马培训\MySQL笔记\assets\image-20220830195228676.png)

2）**左外连接：LEFT OUTER JOIN，简写为 LEFT JOIN**

* 左侧+交集部分

* 左右两表关联时，除满足关联条件的数据会出现在最终的关联结果中，左表中不能和右边表联的数据也会出现，右表侧自动填充为NULL

* 除了公共的部分,在左边中的行虽然不满足连接条件,但是也会写入最终的结果,但是有右边表的字段值为空

![img](E:\黑马培训\MySQL笔记\assets\左连接.png)

![image-20220830195512522](E:\黑马培训\MySQL笔记\assets\image-20220830195512522.png)

3）**右外连接：RIGHT OUTER JOIN，简写为 RIGHT JOIN**

* 右侧+交集部分

* 左右两表关联时，除满足关联条件的数据会出现在最终的关联结果中，右表中不能和左表关联的数据也会出现，左表侧自动填充为NULL

  

![img](E:\黑马培训\MySQL笔记\assets\右连接.png)

![image-20220830195543020](E:\黑马培训\MySQL笔记\assets\image-20220830195543020.png)

4）**全外连接：FULL OUTER JOIN，简写为 FULL JOIN**

> 注意：MySQL数据库不支持全连接，需要将左连接和右连接的结果利用 UNION 关键字组合实现全连接的效果。

* 左侧 + 交集部分 + 右则

* 左右两表关联时，除满足关联条件的数据会出现在最终的关联结果中，左右两表不能相互关联的数据也都会出现，对应侧自动填充为NULL

![img](E:\黑马培训\MySQL笔记\assets\全连接.png)

![image-20220830195627513](E:\黑马培训\MySQL笔记\assets\image-20220830195627513.png)

**SQL关联面试题：**

表A记录如下：

```python
aID	aName
1	a1
2	a2
3	a3
4	a4
5	a5
```

表B记录如下：

```
bID	bXueHao
1	2018102401
2	2018102402
3	2018102403
4	2018102404
6	2018102405
```

用 aID 和 bID 关联，分别写出 A inner join B、A left join B、A right join B 以及 A full join B 的结果？

```bash
# A inner join B
aID  aName  bID  bXueHao
1	 a1      1	2018102401
2	 a2      2	2018102402
3	 a3      3	2018102403
4	 a4      4	2018102404

# A left join B
aID  aName  bID  bXueHao
1	 a1      1	2018102401
2	 a2      2	2018102402
3	 a3      3	2018102403
4	 a4      4	2018102404
5	 a5     null null

# A right join B
aID  aName  bID  bXueHao
1	 a1      1	2018102401
2	 a2      2	2018102402
3	 a3      3	2018102403
4	 a4      4	2018102404
null null    6	2018102405

# A full join B
aID  aName  bID  bXueHao
1	 a1      1	2018102401
2	 a2      2	2018102402
3	 a3      3	2018102403
4	 a4      4	2018102404
5	 a5     null null
null null    6	2018102405
```

#### 8. 多表关联查询-自关联查询

进行关联时，左表和右表是同一个表，这样的连接叫自关联。

自关联的时候另外的表是自己想象出来的,实际上不存在,要是自连接删除数据时,查的是哪个表也就是删哪个表,归根到底都是删除自己

自连接多出来的表是不存在的,是幻想出来的  只是这样好操作

```sql
-- 创建一个地区表
CREATE TABLE areas(
    id VARCHAR(30) NOT NULL PRIMARY KEY,
    title VARCHAR(30),
    pid VARCHAR(30)
);

-- 示例1：查询'山西省'下的所有市的信息
-- 查询结果字段：
--   市级地区id、市级地区名称、父级地区id、父级地区名称

SELECT
    c.id,
    c.title,
    c.pid,
    p.title
FROM areas c -- 理解为市表
JOIN areas p -- 理解为省表
ON c.pid = p.id
WHERE p.title = '山西省';
```

> 注意：自关联时，需要给表起别名。

**自关联查询的妙用**：

```sql
-- 自关联的妙用
CREATE TABLE sales (
    month INT NOT NULL, -- 月份
    revenue DECIMAL(10, 2) -- 销售额
);

INSERT INTO sales
VALUES
    (1, 1000),
    (2, 800),
    (3, 1200),
    (4, 2000),
    (5, 1800),
    (6, 5000),
    (7, 3000),
    (8, 2500),
    (9, 1600),
    (10, 2200),
    (11, 900),
    (12, 4600);

-- 示例：查询每个月的销售额和前一个月的销售额的差值
-- 查询结果字段：
--  月份、当前月销售额、前一个月销售额，当前月销售额和前一个月销售额的差

SELECT
    cur.month,
    cur.revenue,
    pre.revenue,
    cur.revenue - pre.revenue
FROM sales cur -- 当月数据
JOIN sales pre -- 上个月数据
ON cur.month = pre.month + 1;
```

**自关联中的经典例子中国省市区: **

**表示意:**

![image-20220826211456280](E:\黑马培训\MySQL笔记\assets\image-20220826211456280.png)

**自关联查询基本的思想就是把一张表当做两张表来看:**

人们把省表,市表,区县表,规定好id 与 pid之间的关系 把他们放入地区表中 到时候 在地区表里面 就会有两个关联的条件 市的pid 对应着省的id  区县的pid对应着市的id 当不指定具体的筛选条件时,这两个条件没有事实上的区分,取出来的数据会省对市一种,市对区县一种,都取了出来

**查询整个省市区的sql:**

![image-20220826212142014](E:\黑马培训\MySQL笔记\assets\image-20220826212142014.png)

**查询省市的sql:**

![image-20220826212214998](E:\黑马培训\MySQL笔记\assets\image-20220826212214998.png)

![image-20220826212238736](E:\黑马培训\MySQL笔记\assets\image-20220826212238736.png)

**查询市区的sql:**

![image-20220826212350398](E:\黑马培训\MySQL笔记\assets\image-20220826212350398.png)

**经典的三级分类:**

![image-20220826212309972](E:\黑马培训\MySQL笔记\assets\image-20220826212309972.png)

**自连接查询截止到每月的销售额sql:**

![image-20220826223652566](E:\黑马培训\MySQL笔记\assets\image-20220826223652566.png)

![image-20220826212452814](E:\黑马培训\MySQL笔记\assets\image-20220826212452814.png)



**图解示例:**

![image-20220826224302433](E:\黑马培训\MySQL笔记\assets\image-20220826224302433.png)



![image-20220826224337620](E:\黑马培训\MySQL笔记\assets\image-20220826224337620.png)

这里

====> 1对应1

====> 2对应1 , 2

====> 3对应1, 2 ,3

.......以此类推

就能算出

**最终结果:**

![image-20220826224451483](E:\黑马培训\MySQL笔记\assets\image-20220826224451483.png)

**示例2;**

![image-20220826224112434](E:\黑马培训\MySQL笔记\assets\image-20220826224112434.png)

**图解示例:**

![image-20220826224145719](E:\黑马培训\MySQL笔记\assets\image-20220826224145719.png)

**sql执行顺序解释:**

![image-20220826211151570](E:\黑马培训\MySQL笔记\assets\image-20220826211151570.png)

**多表查询的基础练习题(面试题):**

![image-20220826211307086](E:\黑马培训\MySQL笔记\assets\image-20220826211307086.png)

![image-20220826211312143](E:\黑马培训\MySQL笔记\assets\image-20220826211312143.png)

#### 9. 临时表虚拟表等差别

永久表就是建立之后始终存在的表，用来长期保存数据。

临时表有两种，一种是和永久表一样，建立后始终存在，但只保存临时数据，时候后就删除或不再使用，另一种是只在使用时临时创建，

使用完后就删除该表。

虚拟表就是是视图，本质上就是一条检索SQL文，数据可能来自多张表，可以有过滤条件

数据库CTE指的是公共表表达式(Common Table Expression)

可以把它认为是在单个select，insert,update, delete 或者 create view 语句中定义的临时结果集。

CTE类似派生表，但它不以对象的形式存储在数据库中，只在当前查询语句的执行期间有效。而且CTE可以在同一个语句中被多次引用。

**CTE可以用于：**

1. 建立递归查询。常见的场景有组织架构图、BOM等。具体用法请参考联机丛书。

2. 当不需要常规视图时，代替视图，提高可读性。

3. 允许在派生列上分组。

4. 在同一个语句中多次被引用。