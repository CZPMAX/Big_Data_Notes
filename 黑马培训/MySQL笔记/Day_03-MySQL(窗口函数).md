[TOC]

**Day03-MySQL基础**

**前言总结**

```shell
# 1. 子查询【重点】
	AS 起别名 # 字段起别名、表起别名
	子查询操作 # 子查询是用来辅助主查询：充当条件、充当数据源、充当查询字段
# 2. 窗口函数【重点】
	窗口函数简介 # MySQL8.0，简洁高效
	窗口函数基础用法：OVER关键字 # <window function> OVER(...)
	PARTITION BY分区 # <window function> OVER(PARTITION BY 字段, ...)
	排序函数：产生排名 # <ranking function> OVER(PARITION BY 字段, ... ORDER BY 字段, ...)
	典型使用场景：
		1）计算每个值和整体平均值的查询
		2）计算每个值占整体之和的占比
		3）获取指定排名的数据
# 3. MySQL常用内置函数
	时间日期函数
	字符串函数
	数学函数
# 4. 事务操作
	事务：转账
	使用场景：保证一组非查询sql操作，同时成功或同时失败。
```

### 1. SQL子查询

#### 1.1 SQL查询时使用AS关键字起别名

在SQL查询时，可以使用 AS 给表或者字段起别名

1）给查询字段起别名

```sql
-- 示例1：查询每个分类的商品数量
SELECT
    category_id,
    -- COUNT(*) AS product_count
    COUNT(*) product_count -- 起别名时，AS关键字可以省略
FROM products
GROUP BY category_id;

SELECT
    category_id,
    COUNT(*) `desc` -- 注意：别名为关键字时，别名两边要加``，否则报错
FROM products
GROUP BY category_id;
```

2）给表起别名

```sql
-- 示例2：查询每个分类名称所对应的商品数量(没有商品的分类的也要显示)
SELECT
    c.cname,
    COUNT(*) product_count
FROM category c -- category表的别名叫c
LEFT JOIN products p -- products表的别名叫p
ON c.cid = p.category_id
GROUP BY c.cid, c.cname;
```

#### 1.2 SQL子查询操作

> 在一个 SELECT 语句中，嵌入了另外一个 SELECT 语句，那么被嵌入的 SELECT 语句称之为子查询语句，外部那个SELECT 语句则称为主查询。

**规则:  子查询的结果作为数据源的时候必须给他起别名**      **下图示:**

![image-20220827203756976](E:\黑马培训\MySQL笔记\assets\image-20220827203756976.png)

mysql中使用select 查询一个不存在的字段后, 会在表中自动加入这个字段

![image-20220827202103107](E:\黑马培训\MySQL笔记\assets\image-20220827202103107.png)

**主查询和子查询的关系**：

1）子查询是嵌入到主查询中

2）**子查询是辅助主查询的，要么充当条件，要么充当数据源，要么充当查询字段**

3）子查询是可以独立存在的语句，是一条完整的 SELECT 语句

```sql
-- 示例1：查询当前商品大于平均价格的商品
-- ① 查询商品的平均价格
SELECT AVG(price) FROM products;

-- ② 查询所有商品
SELECT * FROM products;

-- ③ 将第①步的结果作为第②步的查询条件
SELECT
    *
FROM products
WHERE price > (SELECT AVG(price) FROM products);

-- 示例4：查询不同类型商品的平均价格
-- 查询结果字段：
--  category_id(分类id)、cname(分类名称)、avg(分类商品平均价格)

SELECT
    category_id,
    cname,
    avg
FROM (
    SELECT
        category_id,
        AVG(price) AS `avg`
    FROM products
    GROUP BY category_id
) a -- 子查询作用数据源时，必须起别名
JOIN category b
ON a.category_id = b.cid;

-- 示例5：针对 students 表的数据，计算每个同学的Score分数和整体平均分数的差值
SELECT
    *,
    (SELECT AVG(Score) FROM students) AS `avg`,
    Score - (SELECT AVG(Score) FROM students) AS `difference`
FROM students;
```

**子查询**

**1. AS 关键字**

在SQL查询，中可以使用 AS 给表或者字段起别名。

**准备数据**

```sql
# 创建分类表
CREATE TABLE category (
  cid VARCHAR(32) PRIMARY KEY,
  cname VARCHAR(50)
);

# 创建商品表
CREATE TABLE products(
  pid VARCHAR(32) PRIMARY KEY,
  pname VARCHAR(50),
  price INT,
  flag VARCHAR(2), # 是否上架标记为：1表示上架、0表示下架
  category_id VARCHAR(32),
  CONSTRAINT products_fk FOREIGN KEY (category_id) REFERENCES category (cid)
);

# 插入分类数据
INSERT INTO category(cid, cname) VALUES('c001', '家电');
INSERT INTO category(cid, cname) VALUES('c002', '服饰');
INSERT INTO category(cid, cname) VALUES('c003', '化妆品');

# 插入商品数据
INSERT INTO products(pid, pname,price,flag,category_id) 
VALUES
('p001','联想',5000,'1','c001'),
('p002','海尔',3000,'1','c001'),
('p003','雷神',5000,'1','c001'),
('p004','JACK JONES',800,'1','c002'),
('p005','真维斯',200,'1','c002'),
('p006','花花公子',440,'1','c002'),
('p007','劲霸',2000,'1','c002'),
('p008','香奈儿',800,'1','c003'),
('p009','相宜本草',200,'1','c003');
```

简单来讲，子查询就是在一个 SQL 语句中嵌套另外一个SQL语句。

**1.1 给表起别名**

**例1：查询每个分类名称所对应的商品数量**

```sql
SELECT
    c.cname,
    COUNT(*)
FROM products AS p
LEFT JOIN category AS c
ON c.cid = p.category_id
GROUP BY c.cname;
```

![image-20211120175334374](E:\黑马培训\MySQL笔记\assets\image-20211120175334374.png)

使用  `表名 AS 别名`  的形式可以给表起别名，**当给表起别名后，在使用该表时只能使用别名，原名已经失效**

如果此时使用原表名将会报错，例如使用category.cname，则出现错误信息：`known column 'category.cname' in 'field list'`。

在给表起别名时，AS 关键字可以省略，但是初学时不建议省略，省略后可读性较差，写法如下：

```sql
SELECT
    c.cname,
    COUNT(*)
FROM products p
LEFT JOIN category c
ON c.cid = p.category_id
GROUP BY c.cname;
```

**5.2 给字段起别名**

**例2：查询每个分类名称所对应的商品数量，并筛选商品总数大于3的分组**

```sql
SELECT
    # 不建议将字段起中文别名
    c.cname  AS category_name,
    COUNT(*) AS total
FROM products p
LEFT JOIN category c
ON c.cid = p.category_id
GROUP BY category_name
HAVING total >= 3;
```

![image-20211120175357486](E:\黑马培训\MySQL笔记\assets\image-20211120175357486.png)

给字段定义别名时，AS 关键字也可以省略，写法如下：

```sql
SELECT
    # 不建议将字段起中文别名
    c.cname  category_name,
    COUNT(*) total
FROM products p
LEFT JOIN category c
ON c.cid = p.category_id
GROUP BY category_name
HAVING total >= 3;
```

**2. 子查询的介绍**

在一个 SELECT 语句中，嵌入了另外一个 SELECT 语句，那么被嵌入的 SELECT 语句称之为子查询语句，外部那个SELECT 语句则称为主查询。

**主查询和子查询的关系**：

1. 子查询是嵌入到主查询中，是用来辅助主查询的
   - 子查询充当主查询的查询条件
   - 子查询充当主查询的数据源
   - 子查询充当主查询的查询字段
2. 子查询是可以独立存在的语句，是一条完整的 SELECT 语句

2. 子查询的使用

所以子查询中的SQL语句，必须可以单独正确的使用，可以查询出正确内容。

#### 1.3 子查询充当查询条件

**例1：查询当前商品中大于平均价格的商品**

**第一步：**获取平均价格 (得到的是一个值)

```sql
SELECT AVG(price) FROM products;
```

**第二步：**查询所有商品

```sql
SELECT * FROM products;
```

**第三步：**将第一步结果作为第二步的查询条件，这种方式即为子查询

```sql
SELECT 
    *
FROM products
WHERE price > (
    SELECT AVG(price) FROM products
);
```

![image-20211120175013198](E:\黑马培训\MySQL笔记\assets\image-20211120175013198.png)

**例2：获取所有商品中，平均价格大于800的分类的全部商品**

 **第一步：**获取平均价格大于800的分类

```sql
SELECT
    category_id
FROM products
GROUP BY category_id
HAVING AVG(price) > 800;
```

**第二步：**查询所有商品

```sql
SELECT * FROM products;
```

**第三步：**第一步查询出来的分类，作为第二步的条件进行筛选

```sql
SELECT
    *
FROM products
WHERE category_id IN (
    -- 子查询：查询分类商品平均价格大于800的分类
    SELECT
        category_id
    FROM products
    GROUP BY category_id
    HAVING AVG(price) > 800
);
```

![img](E:\黑马培训\MySQL笔记\assets\image-20220510210203921.png)

**示例3：获取所有商品中价格最高的商品(价格最高的商品不只一个)**

```sql
-- ① 查询商品的最高价格
SELECT MAX(price) FROM products;

-- ② 获取所有商品中价格最高的商品(价格最高的商品不只一个)
SELECT
    *
FROM products
WHERE price = (SELECT MAX(price) FROM products);
```

![img](E:\黑马培训\MySQL笔记\assets\Snipaste_2022-05-17_18-32-59.png)

#### 1.4 子查询充当数据源

**例4：查询不同类型商品的平均价格**

```
查询结果字段：
    category_id(分类id)、cname(分类名称)、avg(分类商品平均价格)
```

 **第一步：**查询不同种类商品的平均价格

```sql
SELECT
    category_id,
    AVG(price) AS `avg`
FROM products
GROUP BY category_id;
```

 **第二步：**那上面的结果关联分类表获取分类的名称

```sql
SELECT
    category_id,
    cname,
    avg
FROM category c
JOIN (
    -- 子查询充当数据源，必须起别名
    SELECT
        category_id,
        AVG(price) AS `avg`
    FROM products
    GROUP BY category_id
) a
ON a.category_id = c.cid;
```

![image-20220510210309285](E:\黑马培训\MySQL笔记\assets\image-20220510210309285.png)

#### 1.5 子查询充当查询字段

**例5**：针对 students 表的数据，计算每个同学的Score分数和整体平均分数的差值

```sql
SELECT
    *,
    -- 子查询结果充当主查询的查询字段
    (SELECT AVG(Score) FROM students) AS `Avg_Score`,
    Score - (SELECT AVG(Score) FROM students) AS `differnce`
FROM students;
```

**3. 小结**

- 子查询是一个完整的SQL语句，子查询被嵌入到一对小括号里面
- 子查询是用来辅助主查询的
  - 子查询充当主查询的查询条件
  - 子查询充当主查询的数据源
  - 子查询充当主查询的查询字段

#### 1.6 根据子查询的结果不同,可分类子查询

**子查询外部的语句可以是insert / update / delete / select中的任何一个**

根据子查询的结果不同,分为

- 标量子查询(子查询的结果是单个值): 返回的结果是单个值(数字,日期,字符串)  子查询结果常用在 = < > >= <=等条件下  
- 列子查询(子查询结果为一列)(可以是多行)  通常在 in not in any  some all 条件下产生,这里都是单列多行
- 行子查询(子查询结果为一行)  一行多列     子查询条件为某个字段等于一个值的条件下 才会出现这种情况 可以用在= 等符号的筛选下
- 表子查询(子查询结果为多行多列)

根据子查询位置: 分为where之后,from之后,select之后

##### 1.6.1 行子查询与列子查询详解

**列子查询**

**概念**

列子查询：子查询得到的结果是一列数据(一列多行)

**基本语法**

主查询 where 条件 in (列子查询);

![image-20220904130448687](E:\黑马培训\MySQL笔记\assets\image-20220904130448687.png)

**行子查询**  (标量子查询是它的特殊情况,它只返回了一行一列,相当于单个值)

**概念**

行子查询：子查询返回的结果是一行多列

**行元素**
行元素：字段元素是指一个字段对应的值，行元素对应的就是多个字段，多个字段

合起来作为一个元素参与运算，把这种情况称之为行元素。

**基本语法**

主查询 where 条件 [(构造一个行元素)] = (行子查询);

![image-20220904130840030](E:\黑马培训\MySQL笔记\assets\image-20220904130840030.png)

**子查询中特定关键字的使用**

**in** 
主查询 where 条件 in(列子查询)
**any** 
= any(列子查询);条件在查询结果中有任意一个匹配即可，等价于in
<>any(列子查询);条件在查询结果中不等于任意一个
**some**  
与any完全一样，在国外，some与any的正面含义一致，但是否定就大不相同，not any与not some
**all**  
=all(列子查询)，等于里面所有
<>all(列子查询)，不等于其中所有

**总结：**

常见的三个子查询(标量子查询、列子查询、行子查询)都属于where子查询

**补充:**

```sql
-- 在一个 SELECT 语句中，嵌入了另外一个 SELECT 语句，那么被嵌入的 SELECT 语句称之为子查询语句，外部那个SELECT 语句则称为主查询。

用法:    select * from t1 where column1 = ( select column1 from t2 );
# 主查询和子查询的关系：
# 1）子查询是嵌入到主查询中
# 2）子查询是辅助主查询的，要么充当条件，要么充当数据源，要么充当查询字段
子查询充当查询字段时，只能返回一个值(即这个字段的某一行数据,这个某一行就要看你子查询的时候查询的条件了)。如果想返回多个值则不能够充当查询字段。（但可以充当条件，充当数据源）
# 3）子查询是可以独立存在的语句，是一条完整的 SELECT 语句
注意: 1.子查询的查询结果,充当了主查询的字段(在不分组的情况下)
  2.子查询的结果充当主查询的数据源:必须要给子查询的结果起别名
  3.   坑点：字段名两边不能加单引号
```

**图示:**

![image-20220831230407576](E:\黑马培训\MySQL笔记\assets\image-20220831230407576.png)

### 2. 窗口函数

![image-20220827202744695](E:\黑马培训\MySQL笔记\assets\image-20220827202744695.png)

![image-20220827202954185](E:\黑马培训\MySQL笔记\assets\image-20220827202954185.png)

![image-20220827203048609](E:\黑马培训\MySQL笔记\assets\image-20220827203048609.png)

**总结:**

**OVER() 是限定前面聚合函数值的取值范围, 聚合函数算完的值都赋值给这个范围内的所有行**

#### 2.1 SQL进阶-窗口函数简介

**窗口函数说白了就会添加一个字段,然后那个字段每一行的数据都是前面那个函数的计算结果**

窗口函数是 MySQL8.0 以后加入的功能，之前需要通过定义临时变量和大量的子查询才能完成的工作，使用窗口函数实现起来更加简洁高效。**同时窗口函数也是面试是的高频考点**。

```sql
-- 示例1：针对 students 表的数据，计算每个同学的Score分数和整体平均分数的差值
SELECT
    *,
    AVG(Score) OVER() AS `avg`,
    Score - AVG(Score) OVER() AS `difference`
FROM students;
```

**窗口函数的优点**：

1）**简单**

- 窗口函数更易于使用。在上面的示例中，与使用聚合函数然后合并结果相比，使用窗口函数的 SQL 语句更加简单。

2）**快速**

- 这一点与上一点相关，使用窗口函数比使用替代方法要快得多。当你处理成百上千个千兆字节的数据时，这非常有用。

3）**多功能性**

- 最重要的是，窗口函数具有多种功能，比如：添加移动平均线、添加行号和滞后数据等等。

#### 2.2 窗口函数-基本用法

**OVER()关键字**

```sql
-- 基础语法
<window function> OVER(...)
```

- `<window function>` 表示使用的窗口函数，窗口函数可以使用之前已经学过的聚合函数，比如`COUNT()`、`SUM()`、`AVG()`等，也可以是其他函数，比如 ranking 排序函数等，后面的课程中会介绍
- `OVER(...)`的作用就是设置每行数据关联的窗口数据范围，`OVER()`时，每行关联的数据范围都是整张表的数据。

SQL举例：

```sql
SELECT
    ID,
    Name,
    Gender,
    Score,
    -- OVER()：表示每行关联的窗口数据范围都是整张表的数据
    -- AVG(Score)：表示处理每行数据时，应用 AVG 对每行关联的窗口数据中的 Score 求平均
    AVG(Score) OVER() AS `AVG_Score`
FROM students;
```

![image-20220827203322327](E:\黑马培训\MySQL笔记\assets\image-20220827203322327.png)

![image-20220827203352219](E:\黑马培训\MySQL笔记\assets\image-20220827203352219.png)

![image-20220827203530303](E:\黑马培训\MySQL笔记\assets\image-20220827203530303.png)

**典型应用场景**：

1）场景1：**计算每个值和整体平均值的差值**

```sql
-- 需求：计算每个学生的 Score 分数和所有学生整体平均分的差值。
SELECT
    ID,
    Name,
    Gender,
    Score,
    AVG(Score) OVER() AS `AVG_Score`,
    Score - AVG(Score) OVER() AS `difference`
FROM students;
```

2）场景2：**计算每个值占整体之和的占比**

```sql
-- 需求：计算每个学生的Score分数占所有学生分数之和的百分比
SELECT
    ID,
    Name,
    Gender,
    Score,
    SUM(Score) OVER() AS `sum`,
    -- 计算百分比：要把 Score / SUM(Score) OVER() 的结果再乘 100
    Score / SUM(Score) OVER() * 100 AS `ratio`
FROM students;
```

#### 2.3 窗口函数-PARTITION BY分区

**思考题**：

```
1. 如何计算每个学生的 Score 分数和同性别学生平均分的差值？
```

![img](E:\黑马培训\MySQL笔记\assets\1.png)

**PARTITION BY分区**：

```sql
# 基础语法
<window function> OVER(PARTITION BY 列名, ...)
```

- `PARTITION BY 列名, ...`的作用是按照指定的列对整张表的数据进行分区
- 分区之后，在处理每行数据时，`<window function>`是作用在该行数据关联的分区上，不再是整张表上

SQL举例：

```sql
SELECT
    ID,
    Name,
    Gender,
    Score,
    -- PARTITION BY Gender：按照性别对整张表的数据进行分区，此处会分成2个区
    -- AVG(Score)：处理每行数据时，应用 AVG 对该行关联分区数据中的 Score 求平均
    AVG(Score) OVER(PARTITION BY Gender) AS `Avg`
FROM students;
```

![image-20220827203451871](E:\黑马培训\MySQL笔记\assets\image-20220827203451871.png)

![image-20220827203428914](E:\黑马培训\MySQL笔记\assets\image-20220827203428914.png)



**应用示例**：

```sql
-- 需求：计算每个学生的 Score 分数和同性别学生平均分的差值
SELECT
    ID,
    Name,
    Gender,
    Score,
    -- PARTITION BY Gender：按照性别对整张表的数据进行分区，此处会分成2个区
    -- AVG(Score)：处理每行数据时，应用 AVG 对该行关联分区数据中的 Score 求平均
    AVG(Score) OVER(PARTITION BY Gender) AS `Avg`,
    Score - AVG(Score) OVER(PARTITION BY Gender) AS `difference`
FROM students;


-- 需求：计算每人各科分数与对应科目最高分的占比
SELECT
    name,
    course,
    score,
    -- 处理每行数据时，计算相同科目成绩的最高分
    MAX(score) OVER(PARTITION BY course) AS `max`,
    score / MAX(score) OVER(PARTITION BY course) AS `ratio`
FROM tb_score;
```

#### 2.4 窗口函数-排序函数(排名函数)使用

**思考题**：

```bash
1. 如何将 tb_score 中的数据，按照分数从高到低产生一列排名序号？
```

![img](E:\黑马培训\MySQL笔记\assets\2.png)

**排序函数**：

```sql
# 基础语法
<ranking function> OVER (ORDER BY 列名, ...)
```

- OVER() 中可以指定 ORDER BY 按照指定列对每一行关联的分区数据进行排序，然后使用排序函数对分区内的每行数据产生一个排名序号
- 自定义排序

--   ORDER BY  字段名, FIELD(字段, '字段中具体的值1', '字段中具体的值2', .......)

**图示例子:**

![image-20220827205154027](E:\黑马培训\MySQL笔记\assets\image-20220827205154027.png)

![image-20220902211547408](E:\黑马培训\MySQL笔记\assets\image-20220902211547408.png)

**上面的ORDER BY 通过学号排序了, 然后又指定了,接下来按哪个字段,里面指定的值来排序,如上图所示;**

SQL举例：

```sql
SELECT
    name,
    course,
    score,
    -- 此处 OVER() 中没有 PARTITION BY，所以整张表就是一个分区
    -- ORDER BY score DESC：按照 score 对每个分区内的数据降序排序
    -- RANK() 窗口函数的作用是对每个分区内的每一行产生一个排名序号
    RANK() OVER(ORDER BY score DESC) as `rank`
FROM tb_score;
```

> 注意：RANK()产生的排名序号可能会不连续(**当有并列情况时**)

**不同的排序函数**：

- `RANK()`：**产生的排名序号 ，有并列的情况出现时序号不连续**
- `DENSE_RANK()` ：**产生的排序序号是连续的，有并列的情况出现时序号会重复**
- `ROW_NUMBER()` ：**返回连续唯一的行号，排名序号不会重复**

![image-20220827203254441](E:\黑马培训\MySQL笔记\assets\image-20220827203254441.png)

SQL举例：

```sql
SELECT
    name,
    course,
    score,
    -- 可能重复不连续
    RANK() OVER(ORDER BY score DESC) as `rank`,
    -- 一定连续，可能重复
    DENSE_RANK() OVER(ORDER BY score DESC) as `dense_rank`,
    -- 一定连续，且不重复
    ROW_NUMBER() OVER(ORDER BY score DESC) as `row_number`
FROM tb_score;
```

**PARTITION BY和排序函数配合使用**：

```sql
-- 需求：按照不同科目，对学生的分数从高到低进行排名(要求：连续可重复)
SELECT
    name,
    course,
    score,
    -- 对每个分区内的每一行产生排名序号
    DENSE_RANK() OVER(
        -- 将整张表的数据按照科目进行分区
        PARTITION BY course
        -- 对每个分区内的数据按照score降序排列
        ORDER BY score DESC
    ) AS `dense_rank`
FROM tb_score;
```

![image-20220827203153317](E:\黑马培训\MySQL笔记\assets\image-20220827203153317.png)

![image-20220902211654300](E:\黑马培训\MySQL笔记\assets\image-20220902211654300.png)

**并且赋值上序号**

**排序函数典型应用**：

1）场景：**获取指定排名的数据**

```sql
-- 需求：获取每个科目，排名第二的学生信息
SELECT
    name,
    course,
    score
FROM (
    SELECT
        name,
        course,
        score,
        DENSE_RANK() OVER(
            PARTITION BY course
            ORDER BY score DESC
        ) AS `dense_rank`
    FROM tb_score
) s
WHERE `dense_rank` = 2;
```

**CTE公用表表达式**：

CTE(公用表表达式)：Common Table Expresssion，类似于子查询，相当于一张临时表，充当数据源,可以在 CTE 结果的基础上，进行进一步的查询操作。

```sql
WITH some_name AS (
  --- your CTE ---
)
SELECT
  ... 
FROM some_name
```

- 需要给CTE起一个名字（上面的例子中使用了`some_name`)，具体的查询语句写在括号中
- 在括号后面，就可以通过`SELECT` 将CTE的结果当作一张表来使用
- 将CTE称为“内部查询”，其后的部分称为“外部查询”
- 需要先定义CTE，即在外部查询的`SELECT`之前定义CTE

**大白话解释:  把上面---  your CTE --- 代码块的结果,当做一张表,也就是说是数据源,起了一个别名名字叫做some_name,然后把这个当做数据源给下面的select查询数据**

**例子1:**

![image-20220827205908629](E:\黑马培训\MySQL笔记\assets\image-20220827205908629.png)

```sql
-- 例子2
-- 需求：获取每个科目，排名第二的学生信息
WITH ranking AS (
    SELECT
        name,
        course,
        score,
        DENSE_RANK() OVER(
            PARTITION BY course
            ORDER BY score DESC
        ) AS `dense_rank`
    FROM tb_score
)
SELECT
    name,
    course,
    score
FROM ranking
WHERE `dense_rank` = 2;
```



### 3. 内置函数

#### 3.1 MySQL常用内置函数-时间日期函数

**时间类型**

**一、Date（日期类型）：**

3个字节存储，格式：yyyy-mm-dd，初始值为0000-00-00。

**二、Time（时间类型）：**

3个字节存储，格式：hh-mm-ss。

而mysql中的time类型能表示更大的范围（-838:59:59~838:59:59）。

**三、Datetime（日期时间类型）：**

日期时间类型，就是前面的Date和Time结合起来，表示时间，8个字节存储。 

格式：yyyy-mm-dd hh:mm:ss(表示范围为：1000-01-01 00:00:00到9999-12-12 23:59:59)。

**四、Timestamp（时间戳类型）：**

格式：格式yyyy-mm-dd hh-mm-ss。

**五、Year（年类型，一个字节）:**

格式：yyyy。



我们在做数据处理和开发的过程中，经常需要利用SQL在MySQL或HIVE、SPARK中使用一些SQL函数，对时间日

期类型的数据进行操作。

<img src="E:\黑马培训\MySQL笔记\assets\image-20220827204354789.png" alt="image-20220827204354789" style="zoom:80%;" />

1）获取当前时间的函数，比如当前的年月日或时间戳

2）计算时间差的相关函数，比如两个日期之间相差多少天，一个日期90天后是几月几号

3）获取年月日的函数，从一个时间中提取具体的年份、月份

4）时间转换函数，比如将2021-10-05转换为时间戳

**重点函数举例**：

**关于时间的减号是以前,加号是以后**

```sql
-- 获取当前datetime类型的时间
SELECT NOW();

-- 计算指定间隔的时间日期
SELECT DATE_ADD('2007-9-27', INTERVAL 90 DAY);

-- 示例：获取当前时间3个小时以前的时间
SELECT DATE_ADD(NOW(), INTERVAL -3 HOUR);

-- 计算两个时间日期之间的天数差
SELECT DATEDIFF('2021-03-22 09:00:00', '2018-03-20 07:00:00');

-- 提取时间日期中的年月日
-- 获取当前日期中的年份
SELECT YEAR(NOW());

-- 获取2021-10-02 09:00中月份
SELECT MONTH('2021-10-02 09:00');

-- 获取时间日期中的日
SELECT DAY('2021-10-02 09:00');

-- 时间戳转字符串
select FROM_UNIXTIME(1355932800, '%Y-%m-%d %H:%i:%s');
# 起始的时间1970:0:0
select FROM_UNIXTIME(0, '%Y-%m-%d %H:%i:%s');
```

##### 3.1.1 时间日期函数的作用

我们在做数据处理和开发的过程中，经常需要利用SQL在MySQL或HIVE、SPARK中使用一些SQL函数，对时间日期类型的数据进行操作。这些函数有些可用于解析日期值的日期与时间部分，有些函数可用于比较、操纵日期/时间值。

##### 3.1.2 Mysql中时间日期数据类型

| 数据类型  | 具体的样子          |
| --------- | ------------------- |
| year      | 2021                |
| date      | 2021-10-05          |
| time      | 09:00:00            |
| datetime  | 2021-10-05 09:00:00 |
| timestamp | 1625221388          |

##### 3.1.3 常用时间日期函数分类

- 获取当前时间的函数，比如当前的年月日或时间戳
- 计算时间差的相关函数，比如两个日期之间相差多少天，一个日期90天后是几月几号
- 获取年月日的函数，从一个时间中提取具体的年份、月份
- 时间转换函数，比如将`2021-10-05`转换为时间戳

##### 3.1.4 获取当前时间

**获取当前datetime类型的时间**

```sql
SELECT NOW();
-- 返回结果：2021-07-02 17:33:12
```

**获取当前的date类型的时间**

```sql
SELECT CURRENT_DATE();
-- 返回结果：2021-07-02
```

**获取当前的time类型的时间**

```sql
SELECT CURRENT_TIME();
-- 返回结果：18:41:22
```

**获取当前操作系统时间**

```sql
SELECT SYSDATE() AS 系统日期时间;
```

![image-20211120162723200](E:\黑马培训\MySQL笔记\assets\image-20211120162723200.png)

##### 3.1.5 计算时间差

**计算指定间隔的时间日期**

> DATEADD()函数

- DATEADD()函数用于计算日期时间间隔。比如，要得到2007年4月29日起90天后的日期，可以使用下列语句：

  ```sql
  SELECT DATE_ADD('2007-9-27', INTERVAL 90 DAY);
  -- INTERVAL为固定关键词，90 DAY 表示90天
  -- 返回结果为：2007-12-26
  ```

- DATEADD函数的时间间隔参数：可以把下面的值作为时间间隔参数传递给DATEADD()函数

| 时间间隔参数       | 说明      |
| ------------------ | --------- |
| INTERVAL 60 SECOND | 60秒后    |
| INTERVAL 1 MINUTE  | 1分钟后   |
| INTERVAL -1 HOUR   | 1小时前   |
| INTERVAL -1 DAY    | 1天前     |
| INTERVAL -1 MONTH  | 1个月以前 |
| INTERVAL -1 YEAR   | 1年以前   |

- 例：获取当前时间3小时以前的时间

```sql
SELECT DATE_ADD(NOW(), INTERVAL -3 HOUR);
-- 返回结果为：2021-07-02 14:35:48
```

**计算两个时间日期之间的天数差**

> DATEDIFF()函数

- DATEDIFF()函数用于计算两个时间之间的日期差，比如计算`2021-03-22 09:00:00`和`2018-03-20 07:00:00`之间相差多少天，代码如下：

```sql
SELECT DATEDIFF('2021-03-22 09:00:00', '2018-03-20 07:00:00');
-- 返回结果为 1098
```

**计算两个时间日期之间的时间差**

> TIMESTAMPDIFF()函数  -- 这个类型的函数运算时，后面减去前面

- TIMESTAMPDIFF()函数则可以计算两个时间之间的时间差，自定义时间单位，比如比如计算`2021-03-22 09:00:00`和`2018-03-20 07:00:00`之间相差多少月，代码如下：

```sql
SELECT TIMESTAMPDIFF(MONTH, '2021-03-22 09:00:00', '2018-03-20 07:00:00');
-- 返回结果为 -36
```

- 例：使用TIMESTAMPDIFF函数计算当前时间距离1989年3月9日有多少天

```sql
SELECT TIMESTAMPDIFF(DAY, '1989-03-09', NOW());
-- 返回结果为 11803
```

##### 3.1.6 提取时间日期中的年月日

**提取年份**

> YEAR()函数

获取当前时间中的年份

```sql
SELECT YEAR(NOW());
-- 返回结果为 2021
```

**提取月份**

> MONTH()函数

获取`2021-10-02 09:00`中月份

```sql
SELECT MONTH('2021-10-02 09:00');
-- 返回结果为 10
```

**提取时间日期中的日**

> DAY()函数

```sql
SELECT DAY('2021-10-02 09:00');
-- 返回结果为 2
```

**提取时间日期中的时分秒**

> HOUR()函数
>
> MINUTE()函数
>
> SECOND()函数

```sql
-- 分别运行下面的SQL语句
SELECT HOUR('2021-10-02 09:00');
SELECT MINUTE('2021-10-02 09:00');
SELECT SECOND('2021-10-02 09:00');
```

**计算时间日期是星期几**

> WEEKDAY()函数，返回的是星期几的索引：星期一就是0，星期二是1，星期三是2，星期日是6

```sql
SELECT WEEKDAY('2021-07-05');
```

##### 3.1.7 时间日期转换

**时间格式化(时间转字符串)**

```sql
-- 分别运行下面代码
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s');
-- 返回结果为 2021-07-02 18:48:39
SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i');
-- 返回结果为 2021-07-02 18:48
SELECT DATE_FORMAT(NOW(),'%Y年%m月%d日 %H时%i分%s秒');
-- 返回结果为 2021年07月02日 18时48分57秒
```

**字符串转时间**

```sql
-- 分别运行下面代码
SELECT STR_TO_DATE('2021-01-20 16:01:45', '%Y-%m-%d %H:%i:%s');
-- 返回结果为 2021-01-20 16:01:45
SELECT STR_TO_DATE('2021-01-20 16:01:45', '%Y-%m-%d');
-- 返回结果为 2021-01-20
```

**时间或字符串转时间戳**

```sql
-- 分别运行下面代码
SELECT UNIX_TIMESTAMP(NOW());
-- 返回结果为 1625223405
select UNIX_TIMESTAMP('2012-12-20');  
-- 返回结果为 1355932800
```

**时间戳换字符串**

```sql
select FROM_UNIXTIME(1355932800, '%Y-%d');
-- 返回结果为 2012-20
```

**[查表]日期格式**

```sql
%M 月名字(January……December)  
%W 星期名字(Sunday……Saturday)  
%D 有英语前缀的月份的日期(1st, 2nd, 3rd, 等等。）  
%Y 年, 数字, 4 位  
%y 年, 数字, 2 位  
%a 缩写的星期名字(Sun……Sat)  
%d 月份中的天数, 数字(00……31)  
%e 月份中的天数, 数字(0……31)  
%m 月, 数字(01……12)  
%c 月, 数字(1……12)  
%b 缩写的月份名字(Jan……Dec)  
%j 一年中的天数(001……366)  
%H 小时(00……23)  
%k 小时(0……23)  
%h 小时(01……12)  
%I 小时(01……12)  
%l 小时(1……12)  
%i 分钟, 数字(00……59)  
%r 时间,12 小时(hh:mm:ss [AP]M)  
%T 时间,24 小时(hh:mm:ss)  
%S 秒(00……59)  
%s 秒(00……59)  
%p AM或PM  
%w 一个星期中的天数(0=Sunday ……6=Saturday ）  
%U 星期(0……52), 这里星期天是星期的第一天  
%u 星期(0……52), 这里星期一是星期的第一
```

**总结**

- 获取时间的函数
  - NOW：获取当前datetime类型的时间 `2021-10-05 09:00:00`
  - CURRENT_DATE：获取当前date类型的时间 `2021-10-05`
  - CURRENT_TIME：获取当前time类型的时间 `09:00:00`
  - SYSDATE：获取当前操作系统时间 `2021-10-05 09:00:00`
- 计算时间差
  - DATEADD：计算指定间隔的时间日期，比如要得到2007年4月29日起90天后的日期
  - DATEDIFF：计算两个时间日期之间相差的天数
  - TIMESTAMPDIFF：计算两个时间日期之间相差的时间，可以指定时间单位
- 获取年月日
  - YEAR：获取时间日期中的年份
  - MONTH：获取时间日期中的月份
  - DAY：获取时间日期中的日
  - HOUR：获取时间日期中的小时
  - MINUTE：获取时间日期中的分钟
  - SECOND：获取时间日期中的秒钟
  - WEEKDAY：返回的是星期几的索引：星期一就是0，星期二是1，星期三是2，星期日是6
  - EXTRACT：用于包裹并返回日期/时间的单独部分，比如`SELECT * FROM 表 WHERE EXTRACT(MONTH FROM date) = '10'`
- 时间转换
  - DATE_FORMAT：对时间进行格式化，即时间类型数据转换为字符串类型
  - STR_TO_DATE：字符串转换为时间类型
  - UNIX_TIMESTAMP：时间类型或字符串类型转换为时间戳
  - FROM_UNIXTIME：时间戳转

#### 3.2 MySQL常用内置函数-文本函数:

我们在做数据处理和开发的过程中，经常需要利用SQL在MySQL或HIVE、SPARK中使用一些SQL函数；和时间函数一样，也经常需要对字符串类型的数据进行处理操作。

##### **3.2.1 字符串函数的作用**

我们在做数据处理和开发的过程中，经常需要利用SQL在MySQL或HIVE、SPARK中使用一些SQL函数；和时间函数一样，也经常需要对字符串类型的数据进行处理操作。这些函数有些可用于修改、替换字符串，或用于获取字符串中特定的子串。

对于略复杂的需求，因为平台（数据库）的差异，SQL不太能够胜任，可以选择使用python等编程语言来处理；比如在字符串中抽取电话号码等操作。

##### **3.2.2 常用的字符串函数功能**

- 大小写转换、反转，以及小数的位数处理
- 对字符串进行拼接、删除前后缀，或做局部替换
- 获取局部的子串，字符串的字符个数以及存储长度

##### **3.2.3 大小写转换**

> LOWER & UPPER 

- 转换为小写

```sql
SELECT LOWER('Hello World!');
-- 返会结果为 hello world!
```

- 转换为大写

```sql
SELECT UPPER('Hello World!');
-- 返会结果为 HELLO WORLD!
```

##### **3.2.4 字符串反转**

> REVERSE

```sql
SELECT REVERSE('123456');
-- 返会结果为 654321
```

##### **3.2.5 小数的位数处理**

> FORMAT 小数虽然不是字符串，但是经常使用，所以也放在这里一起学习

```sql
SELECT FORMAT(1234, 5);
-- 返会结果为 1,234.00000
SELECT FORMAT(1234.000, 1);
-- 返会结果为 1,234.0
```

##### **3.2.6拼接字符串**

**多个相同字符串拼接**

> REPEAT

```sql
SELECT REPEAT('象飞田', 3);
-- 返会结果为 象飞田象飞田象飞田
```

**多个不同字符串拼接**

> CONCAT & CONCAT_WS

- 直接拼接

```sql
SELECT CONCAT('马走日', '象飞田');
-- 返会结果为 马走日象飞田
```

- 使用指定的字符串进行拼接

```sql
SELECT CONCAT_WS('^_^', '马走日', '象飞田');
-- 返会结果为 马走日^_^象飞田
```

##### **3.2.7 对字符串局部进行替换**

> REPLACE

```sql
SELECT REPLACE('赢赢赢士角炮巡河车赢', '赢', '');  (原字符串,替换的目标字符,要去替换的字符)
-- 士角炮巡河车
SELECT REPLACE('赢赢赢士角炮巡河车赢', '赢', '输');
-- 输输输士角炮巡河车输
```

##### **3.2.8 根据位置获取字符串局部的子字符串**

**从指定位置截取指定长度的子串**

> SUBSTR(str, n, m)：从 str 字符串的第 n 个字符(注意：n不是下标)往后截取 m 个字符，返回子串；m可省略，表示截取到末尾。
>
> 反着取的时候: 开始字符位置是-1 他们默认步长都是1

```sql
SELECT SUBSTR('五七炮屏风马', 3, 1);
-- 炮
SELECT SUBSTR('五七炮屏风马', 4);
-- 屏风马
SELECT SUBSTRING('五七炮屏风马', 4, 3); -- SUBSTRING与SUBSTR相同
-- 屏风马
```

**从左或右获取指定个数的子串**

> LEFT & RIGHT

```sql
SELECT LEFT('仙人指路，急进中兵', 4);
-- 仙人指路
SELECT RIGHT('仙人指路，急进中兵', 4);
-- 急进中兵
```

##### **3.2.9 获取字符串的长度和存储大小**

> CHAR_LENGTH & LENGTH

- 获取字符串的长度

```sql
SELECT CHAR_LENGTH('仙人指路，急进中兵');
-- 9
```

- 获取字符串的存储大小

```sql
SELECT LENGTH('仙人指路，急进中兵');
-- 27  单位是字节Byte，简写为B（utf8编码格式的一个中文字符占3个字节, 1B=8bit）
```

**总结**

- 大小写转换
  - LOWER：将字符串参数值转换为全小写字母后返回
  - UPPER：将字符串参数值转换为全大写字母后返回
- 字符串反转
  - REVERSE：将字符串反转后返回
- 小数的位数处理
  - FORMAT：返回指定小数位数的数字
- 拼接
  - CONCAT：将多个字符串参数首尾相连后返回
  - CONCAT_WS：将多个字符串参数用给定的字符串首尾相连后返回
  - REPEAT：将字符串重复指定次数拼接后返回
- 替换
  - REPLACE：在原字符串中查找所有的子串（大小写敏感），找到后使用指定的字符串替换它，返回替换后的字符串
- 获取局部子串
  - LEFT：返回最左边指定长度的子串
  - RIGHT：返回最右边指定长度的子串
  - SUBSTR：从原字符串中的指定的位置开始截取一个子串并返回
- 获取字符串相关信息
  - LENGTH：返回字符串的存储大小，单位是B字节
  - CHAR_LENGTH：返回字符串中的字符个数

我们在做数据处理和开发的过程中，经常需要利用SQL在MySQL或HIVE、SPARK中使用一些SQL函数；和时间函数一样，也经常需要对数字类型的数据进行处理操作。

**重点函数举例**：

```sql
-- ROUND(X, n)：对 X 进行四舍五入，保留 n 位小数，默认n为0
SELECT ROUND(1.6);
SELECT ROUND(1.333, 2);
SELECT ROUND(2.689, 2);

-- FORMAT(X, n)：对 X 进行四舍五入，保留 n 位小数，以##,###,###.###格式显示
SELECT FORMAT(1001.6, 2);
# 1,001.60
SELECT FORMAT(123456.333, 2);
# 1,123,456.33
SELECT FORMAT(234567.689, 2);
# 234,567.69

-- FLOOR(x)：向下取整
SELECT FLOOR(-1.5);# -2

-- CEIL(X)：向上取整
SELECT CEIL(2.1); # 3

-- GREATEST(expr1, expr2, expr3, ...)：返回列表中的最大值
SELECT GREATEST(3, 12, 34, 8, 25); #34

-- LEAST(expr1, expr2, expr3, ...)：返回列表中的最小值
SELECT LEAST(3, 12, 34, 8, 25);#3
```

### 4. 事务

![image-20220829175352403](E:\黑马培训\MySQL笔记\assets\image-20220829175352403.png)

**事务的介绍**

事务就是用户定义的一系列执行SQL语句的操作, 这些操作要么完全地执行，要么完全地都不执行， 它是一个不可分割的工作执行单元。

**事务的使用场景:**

在日常生活中，有时我们需要进行银行转账，这个银行转账操作背后就是需要执行多个SQL语句，假如这些SQL执行到一半突然停电了，那么就会导致这个功能只完成了一半，这种情况是不允许出现，要想解决这个问题就需要通过事务来完成。

**转账的基本流程**：

- 第一步：开启一个事务
- 第二步：减少转出账户的余额：转出 200 元
- 第三步：增加转入账户的余额：增加 200 元
- 第四步：提交事务

#### 4.1 事务的四大特性

- 原子性(Atomicity)
- 一致性(Consistency)
- 隔离性(Isolation)
- 持久性(Durability)

**原子性:**

一个事务必须被视为一个不可分割的最小工作单元，整个事务中的所有操作要么全部提交成功，要么全部失败回滚，对于一个事务来说，不可能只执行其中的一部分操作，这就是事务的原子性

**一致性:**

数据库总是从一个一致性的状态转换到另一个一致性的状态。（在前面的例子中，一致性确保了，即使在转账过程中系统崩溃，支票账户中也不会损失200美元，因为事务最终没有提交，所以事务中所做的修改也不会保存到数据库中。）

**隔离性:**

通常来说，一个事务所做的修改操作在提交事务之前，对于其他事务来说是不可见的。（在前面的例子中，当执行完第三步、第四步还未开始时，此时有另外的一个账户汇总程序开始运行，则其看到转出帐户的余额并没有减少 200 元。）

**持久性:**

一旦事务提交，则其所做的修改会永久保存到数据库。

**说明:**

事务能够保证数据的完整性和一致性，让用户的操作更加安全。

**事务的使用**

#### 4.2 数据库的存储引擎

在使用事务之前，先要确保表的存储引擎是 InnoDB 类型, 只有 InnoDB 引擎才可以使用事务，MySQL数据库中表的存储引擎默认是 InnoDB 类型。

**表的存储引擎说明:**

表的存储引擎就是提供存储数据一种机制，不同表的存储引擎提供不同的存储机制。

**汽车引擎效果图:**

![汽车引擎](E:\黑马培训\MySQL笔记\assets\汽车引擎.png)

**说明:**

- 不同的汽车引擎，提供的汽车动力也是不同的。

**查看MySQL数据库支持的表的存储引擎:**

```sql
-- 查看MySQL数据库支持的表的存储引擎
show engines;
```

![img](E:\黑马培训\MySQL笔记\assets\表的存储引擎.png)

**说明:**

- 常用的表的存储引擎是 InnoDB 和 MyISAM
- InnoDB 是支持事务的
- MyISAM 不支持事务，优势是访问速度快，对事务没有要求或者以select、insert为主的都可以使用该存储引擎来创建表

**查看 students 表的创表语句:**

```sql
-- 选择数据库
use winfunc;
-- 查看students表
show create table students;
```

![img](E:\黑马培训\MySQL笔记\assets\数据库引擎.png)

**执行结果**：

```bash
+----------+----------------------------------------------------------------------------+
| Table    | Create Table                                                               |
+----------+----------------------------------------------------------------------------+
| students | CREATE TABLE `students` (                                                  |
|          |  `ID` int(11) NOT NULL,                                                    |
|          |  `Name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,   |
|          |  `Age` int(11) NOT NULL,                                                   |
|          |  `Gender` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, |
|          |  `GPA` decimal(11,2) NOT NULL,                                             |
|          |  PRIMARY KEY (`ID`) USING BTREE                                            |
|          | ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC                    |
+----------+----------------------------------------------------------------------------+
```

**说明:**

- 通过创表语句可以得知，students表的存储引擎是InnoDB。
- 修改表的存储引擎使用: alter table 表名 engine = 引擎类型;
  - 比如：alter table students engine = 'MyISAM';

#### 4.3 事务的基本使用

**事务使用步骤**：

1）开启事务：`begin` 或 `start transaction`

- **开启事务后执行修改命令，变更数据会保存到 MySQL 服务端的缓存文件中，而不维护到物理表中**
- **MySQL数据库默认采用自动提交(autocommit)模式，如果没有显示的开启一个事务，那么每条sql语句都会被当作一个事务执行提交的操作**

2）事务中的 SQL 操作

3）结束事务：提交事务(`commit`)或回滚事务(`rollback`)

- 提交事务：将本地缓存文件中的数据提交到物理表中，完成数据的更新
- 回滚事务：放弃本地缓存文件中的缓存数据，表示回到开始事务前的状态

**事务演练的SQL语句：**

1）首先打开一个 cmd 终端1，连接 mysql 数据库，执行如下 SQL 操作

```sql
-- 使用 winfunc 数据库
use winfunc;

-- 开启事务
begin;
-- 查看 students 表的数据
select * from students;
-- 在事务中新增一个学生的数据
insert into students values(5, 'Tom', 'Male', 88);
-- 查看 students 表的数据
-- 注意: 在当前事务中可以看到新增的数据，但是数据此时并没有更新到物理表中
-- 如果这里后续没有执行提交事务操作，那么数据是没有真正的更新到物理表中
select * from students;
```

2）再打开一个 cmd 终端2，连接 mysql 数据库，执行如下 SQL 操作

```sql
-- 使用 winfunc 数据库
use winfunc;

-- 查看 students 表的数据
-- 这时没有显示新增的数据，说明之前的事务没有提交，这就是事务的隔离性
-- 一个事务所做的修改操作在提交事务之前，对于其他事务来说是不可见的
select * from students;
```

3）再次在 cmd 终端1中进行操作，提交事务

```sql
-- 提交事务，事务中新增的学生数据会真正插入到物理表中
commit;
```

4）再次在 cmd 终端2中进行操作，查看学生的数据

```sql
-- 因为新增学生数据的事务已经提交了，数据已经真正插入到物理表中，此时会看到新增的学生数据
select * from students;
```

#### 4.4 事务简介和ACID特性

注意什么时候会考虑到用事务?

对表中的数据有操作(影响表中数据)的时候,查询操作就没有必要使用事务

![image-20220827203716605](E:\黑马培训\MySQL笔记\assets\image-20220827203716605.png)

**事务应用场景**：**保证一组SQL操作，同时成功或同时失败**，比如：转账

转账：今天520，你要给女朋友转520块钱。

* 你的卡里面 -520块钱

* 你女朋友的卡里面 +520块钱

  

**转账的基本流程**：

- 第一步：开启一个事务

- 第二步：减少转出账户的余额：转出 520 元

- 第三步：增加转入账户的余额：增加 520 元

- 第四步：提交事务

  

**事务的四大特性**：**ACID**

- 原子性(Atomicity)：事务是一个整体，不会部分成功，部分失败

- 一致性(Consistency)：事务保存数据库从一个一致性状态转移到另一个一致性状态【从一个正确的状态转移到另一个正确的状态】

- 隔离性(Isolation)：一个事务的操作提交之前，对另一个事务是不可见的

  转账：2000元 -1000元

  查帐：2000元

- 持久性(Durability)：事务一旦提交，结果便会被永久保存

  

#### 4.5 数据库表的存储引擎简介

**常见的数据库引擎**：

- InnoDB：默认的数据库表存储引擎，支持事务操作
- MyISAM：不支持事务，优势是访问速度快，对事务没有要求或者以select、insert为主的都可以使用该存储引擎来创建表

**相关命令**：

```sql
SHOW ENGINES; -- 查询数据库支持的存储引擎
SHOW CREATE TABLE 表名; -- 查询表的创建SQL
ALTER TABLE 表名 ENGINE = 引擎类型; -- 修改指定表的存储引擎
```

#### 4.6 事务的使用步骤说明

**事务使用步骤**：

1）开启事务：`begin` 或 `start transaction`

- **开启事务后执行修改命令，变更数据会保存到 MySQL 服务端的缓存文件中，而不维护到物理表中**
- **MySQL数据库默认采用自动提交(autocommit)模式，如果没有显示的开启一个事务，那么每条sql语句都会被当作一个事务执行提交的操作**

2）事务中的 SQL 操作：很多SQL语句

3）结束事务：提交事务(`commit`)或回滚事务(`rollback`)

- 提交事务：将本地缓存文件中的数据提交到物理表中，完成数据的更新
- 回滚事务：放弃本地缓存文件中的缓存数据，表示回到开始事务前的状态

**思考题：如果我们有一组sql需要执行，但是这些sql都是select查询的sql，需要使用事务吗？**

答：不需要使用事务；事务主要是针对一组sql，sql里有很多的增、删、改的操作，保证同时成功或失败才需要使用事务；

#### 4.7 小结

1. 事务的特性
   - 原子性：强调事务中的多个操作时一个整体
   - 一致性：强调数据库中不会保存不一致状态
   - 隔离性：强调数据库中事务之间相互不可见
   - 持久性：强调数据库能永久保存数据，一旦提交就不可撤销
2. **MySQL数据库默认采用自动提交(autocommit)模式**, 也就是说修改数据(insert、update、delete)的操作会自动的触发事务，完成事务的提交或者回滚
3. 事务基本操作
   - 开启事务：使用 begin 或者 start transaction
   - 回滚事务：使用 rollback；

### 5. 扩展

#### 5.1 扩展-CASE WHEN语法简介和基本使用

CASE WHEN可以用于SQL查询时，进行条件判断操作。功能类似于Python中的if...elif...else判断。

**基础语法**：

```mysql
CASE
	WHEN 条件1 THEN 值1
	WHEN 条件2 THEN 值2
	WHEN 条件3 THEN 值3
	...
	ELSE 值n
END
```

**查询示例**：

```sql
-- 练习1
-- 需求：查询所有学生的成绩信息，并将学生的成绩分成5个等级，查询结果中需要有一个学生的成绩等级列，
-- 成绩等级如下：
-- 优秀：90分及以上
-- 良好：80-90，包含80
-- 中等：70-80，包含70
-- 及格：60-70，包含60
-- 不及格：60分以下

-- 查询结果字段：
-- 	name(姓名)、course(科目)、score(成绩)、grade(成绩等级)
SELECT
    *,
    CASE
        WHEN score >= 90 THEN '优秀'
        WHEN score >= 80 THEN '良好'
        WHEN score >= 70 THEN '中等'
        WHEN score >= 60 THEN '及格'
        ELSE '不及格'
    END AS `grade`
FROM tb_score;
```

![image-20220827201533253](E:\黑马培训\MySQL笔记\assets\image-20220827201533253.png)

**CSAE WHEN 更新表中数据**

![image-20220827201642102](E:\黑马培训\MySQL笔记\assets\image-20220827201642102.png)

#### 5.2 扩展-CASE WHEN配合GROUP BY进行使用

```sql
-- 练习2：统计不同科目中，成绩在90分以上(包含90)和90分以下的人数各有多少

-- 查询结果字段：
-- course(科目)、gte_90(该科目90分以上是学生人数)、lt_90(该科目90分以下的学生人数)

-- 方式1
SELECT
    course,
    COUNT(CASE
        WHEN score >= 90 THEN name
    END) AS `gte_90`,
    COUNT(CASE
        WHEN score < 90 THEN name
    END) AS `lt_90`
FROM tb_score
GROUP BY course;

-- 方式2
SELECT
    course,
    SUM(CASE
        WHEN score >= 90 THEN 1
        ELSE 0
    END) AS `gte_90`,
    SUM(CASE
        WHEN score < 90 THEN 1
        ELSE 0
    END) AS `lt_90`
FROM tb_score
GROUP BY course;
```

![image-20220827203857125](E:\黑马培训\MySQL笔记\assets\image-20220827203857125.png)

#### 5.3 扩展-CASE WHEN条件更新操作面试题

**更新数据使用CASE WHEN**

![6](E:\黑马培训\MySQL笔记\assets\6.png)

**建表语句**：

```sql
CREATE TABLE product (
    Product VARCHAR(100),
    Price DECIMAL(10, 2)
);

INSERT INTO product
VALUES
    ('笔记本', 3050),
    ('手机', 2800),
    ('台式电脑', 2050);
```

**参考答案**：

```mysql
# UPDATE 表名 SET 字段=值;

-- 注意：使用 winfunc 数据库
UPDATE product SET price=CASE
    WHEN price >= 3000 THEN price * 0.9
    ELSE price * 1.2
END;

-- CASE WHEN 是双分支的时候，可以使用 IF 函数替代
-- IF(条件, 值1, 值2)
UPDATE product SET price=IF(price>=3000, price * 0.9, price * 1.2);
```

#### 5.4 扩展-CASE WHEN使用面试题

```sql
-- 创建 score 学生成绩表
CREATE TABLE score (
    `学号` VARCHAR(24),
    `科目` VARCHAR(24),
    `成绩` INT
);

-- 给 score 表添加测试表
INSERT INTO score
VALUES
    ('s001', '语文', 90),
    ('s001', '数学', 100),
    ('s001', '英语', 93),
    ('s002', '语文', 98),
    ('s002', '数学', 99),
    ('s002', '英语', 96);
```



**参考答案**：

```sql
SELECT
    学号,
    -- MIN、MAX、AVG、SUM
    MAX(CASE
        WHEN 科目 = '语文' THEN 成绩
    END) AS `语文`,
    MAX(CASE
        WHEN 科目 = '数学' THEN 成绩
    END) AS `数学`,
    MAX(CASE
        WHEN 科目 = '英语' THEN 成绩
    END) AS `英语`
FROM score
GROUP BY 学号;
```

**图示拆分:**

![image-20220827202214716](E:\黑马培训\MySQL笔记\assets\image-20220827202214716.png)

**使用union在把表装回去:**

![image-20220827202256305](E:\黑马培训\MySQL笔记\assets\image-20220827202256305.png)

#### 5.5 SQL中执行的顺序

![image-20220827202519431](E:\黑马培训\MySQL笔记\assets\image-20220827202519431.png)

### 6. 条件判断函数

统计型SQL绝对是让人头疼的一类，之所以如此，是因为这种SQL中必然有大量的判读对比。而条件判断函数就是应对这类需求的利器。本文重点总结`CASE WHEN`、`IF`、`IFNULL`三种函数。

#### 1. CASE WHEN

Case when语句能在SQL语句中织入判断逻辑，类似于Java中的if else语句。

CASE WHEN语句分为简单函数和条件表达式。

case when语句还可以作用在where后面

**1、简单函数**

![image-20220830234859118](E:\黑马培训\MySQL笔记\assets\image-20220830234859118.png)

如果字段值等于预期值，则返回结果1，否则返回结果2。

下面通过一个简单的示例来看一下具体用法。

表score：

![image-20220830234959783](E:\黑马培训\MySQL笔记\assets\image-20220830234959783.png)

场景：在score表中，sex为1表示男性，sex=0表示女性，查询时转换成汉字显示。

SQL语句：

![image-20220830235047649](E:\黑马培训\MySQL笔记\assets\image-20220830235047649.png)

**2、条件表达式**

CASE的简单函数使用简便，但无法应对较为复杂的场景，这就需要用到条件表达式了，其语法结构如下：

![image-20220830235243431](E:\黑马培训\MySQL笔记\assets\image-20220830235243431.png)

解释一下，语句中的condition是条件判断，如果该判断结果为true，那么CASE语句将返回result，否则返回result2，如果没有ELSE，则返回null。CASE与END之间可以有多个WHEN…THEN…ELSE语句。END表示CASE语句结束。

场景：score 大于等于90为优秀，80-90为良好，60-80为及格，小于60为不及格，用SQL语句统计出每个学生的成绩级别。

SQL:

![image-20220830235329508](E:\黑马培训\MySQL笔记\assets\image-20220830235329508.png)

**3、综合使用**

CASE WHEN 和 聚合函数综合使用，能实现更加复杂的统计功能。

先看第1个场景

在下表`score`（sex=1为男，sex=0为女）中，统计有多少个男生和女生以及男女生及格的各有多少个。

SQL:

![image-20220830235427809](E:\黑马培训\MySQL笔记\assets\image-20220830235427809.png)

![image-20220830235448867](E:\黑马培训\MySQL笔记\assets\image-20220830235448867.png)

![image-20220830235508978](E:\黑马培训\MySQL笔记\assets\image-20220830235508978.png)

#### 2 IF

IF函数也能通过判断条件来返回特定值，它的语法如下：

![image-20220830235639019](E:\黑马培训\MySQL笔记\assets\image-20220830235639019.png)

expr是一个条件表达式，如果结果为true，则返回result_true，否则返回result_false。

用一个示例演示，还是表score：

![image-20220830235710825](E:\黑马培训\MySQL笔记\assets\image-20220830235710825.png)

使用IF函数：![image-20220830235747470](E:\黑马培训\MySQL笔记\assets\image-20220830235747470.png)

可以看出，在一些场景中，`IF`函数和`CASE WHEN`是有同样效果的，前者相对简单，后者能应对更复杂的判断。

另外，IF函数还可以和聚合函数结合，例如查询班级男生女生分别有多少人：

![image-20220830235828025](E:\黑马培训\MySQL笔记\assets\image-20220830235828025.png)

#### 3 IFNULL

在Java程序中调用sql语句时，如果返回结果是`null`，是非常容易引发一些意外情况的。

![image-20220830235910893](E:\黑马培训\MySQL笔记\assets\image-20220830235910893.png)

因此，我们希望在SQL中做一些处理，如果查询结果是`null`，就转换为特定的值，这就要用到Mysql中`IFNULL`函数。

首先SQL一般写法是这样的：

![image-20220830235956763](E:\黑马培训\MySQL笔记\assets\image-20220830235956763.png)

![image-20220831000023412](E:\黑马培训\MySQL笔记\assets\image-20220831000023412.png)

![image-20220901085527776](E:\黑马培训\MySQL笔记\assets\image-20220901085527776.png)

![image-20220831084045468](E:\黑马培训\MySQL笔记\assets\image-20220831084045468.png)

**case when 放在括号里面,重命名写在括号外面**

### 7. SQL中经典问题

数据库中表的**行转列**与**列转行**

#### 7.1 列转行

**1. 图示拆解:**

![image-20220904005722027](E:\黑马培训\MySQL笔记\assets\image-20220904005722027.png)

重点当我们要使多条数据 ==> 一条数据时,就要使用到分组聚合 因为分组聚合的特点就是**多进一出**

**2.利用分组使多行变一行**

**sql代码**

```sql
SELECT
学号
FROM score
GROUP BY 学号;
```

**运行结果:**

![image-20220904010525930](E:\黑马培训\MySQL笔记\assets\image-20220904010525930.png)

这时候我们就得到了相同学号聚合完的两行数据,接着在此基础上继续操作

**巧用sql中select一个值 命名为一个新字段,会给全表的数据加这个值 例如**

![image-20220904011523429](E:\黑马培训\MySQL笔记\assets\image-20220904011523429.png)

**字段加了引号的情况:**

![image-20220904011649672](E:\黑马培训\MySQL笔记\assets\image-20220904011649672.png)

接下来在上面的基础上继续往下操作

**在python中sql语句 字符串写的时候不加引号的,就代表着是引用整个字段的数据**

**(字符串名与字段名一致),加引号的时候就代表的是固定的值,去查询固定值时就**

**会在每一行加上这个值**

**代码:**

```sql
# 根据分组聚合操作使多行相同学号,变为一行
SELECT
学号
FROM score
GROUP BY 学号;

# 接着尝试利用case when将单科成绩拆分出来并赋值
SELECT
-- MAX AVG SUM MIN 都能用
学号,
-- MAX操作的范围是根据学号分组完后的范围进行操作的
MAX(CASE
    WHEN 科目='语文' THEN 成绩 # 对每个人的语文成绩求最大
-- 相当于这里的CASE WHEN 第一次是对s001这个分组内的数据进行操作
-- 第二次是对s002这个分组内的数据进行操作
-- 当科目等于语文时 然后查出这个分组内语文所对应的成绩
-- 接着我们就取出了科目等于'语文'的所有成绩,可以起别名命名为语文
-- 科目这个字段还是存在的只是不显示了,因为我们没有去选择它
END)AS `语文`,
-- 挑出数学成绩
MAX(CASE
       WHEN 科目='数学' THEN 成绩
       END)AS `数学`,
MAX(CASE
        WHEN 科目='英语' THEN 成绩
        END)AS `英语`

FROM score
GROUP BY 学号;
```

#### 7.2 列转行

**图示:**

![image-20220904095026520](E:\黑马培训\MySQL笔记\assets\image-20220904095026520.png)

1.第一步查询每个人的语文成绩

![image-20220904100608765](E:\黑马培训\MySQL笔记\assets\image-20220904100608765.png)

**2.接着可以顺着查询每个人的数学,英语成绩**

接着用uion把整张表连接在一起

**代码示例:**

```sql
SELECT
学号,
'语文'AS `科目`,
# 查询一个表中没有的字段 在表里会新增出来
# 选择一个固定的值那么每一行都有那个值
语文 AS `成绩`
FROM t_score
UNION
SELECT
学号,
'数学' AS `科目`,
数学 AS `成绩`
FROM t_score
UNION
SELECT
学号,
'英语'AS `科目`,
英语 AS `成绩`
FROM t_score
```

**运行结果示意图:**

![image-20220904104047816](E:\黑马培训\MySQL笔记\assets\image-20220904104047816.png)

在之前我们拆分了多行原表,想得到两行数据的表,采取了分组聚合的方式,多进一出

这时候我们想要把学号重新排序回去就得使用order by,因为排序操作都是在一切

操作的末尾执行的,也就是说最后写一个根据学号排序,能对连接后的整张表的学号

进行了排序

**代码:**

```sql
SELECT
学号,
'语文'AS `科目`,
# 查询一个表中没有的字段 在表里会新增出来
# 选择一个固定的值那么每一行都有那个值
语文 AS `成绩`
FROM t_score
UNION
SELECT
学号,
'数学' AS `科目`,
数学 AS `成绩`
FROM t_score
UNION
SELECT
学号,
'英语'AS `科目`,
英语 AS `成绩`
FROM t_score
ORDER BY 学号,FIELD(科目,'数学','语文','英语');
```

**结果图示:**

![image-20220904104652636](E:\黑马培训\MySQL笔记\assets\image-20220904104652636.png)

**错误思想:**  想对全连接的整张表进行分组聚合操作,让同一学号的分为一组,以上面

的形式显示出来,但是往往达不到预期效果,因为分组聚合操作,是多进一出,多用于

对分组外的字段或者分组的字段进行聚合操作,返回聚合的一个结果或者分组的一

个结果(分组的那个字段),对于一个多条数据的字段它是无法查询的,因为在一个分

组内,多数据的字段都与这个分组名相关(分组名只有一个,可是对应的数据有多条),

在分组的前提下,你取字段,字段的值只能有一个它才能对的上,所以group by操作,

要是对多字段取值,在测试模式开启的时候,往往取值取的是多字段的第一条数据

**根据学号分组图示:** 

​              **学号**                                       **科目**                                           **学号**

![image-20220904110402712](E:\黑马培训\MySQL笔记\assets\image-20220904110402712.png)

本质上来说,在我们脑子里面还是会想着分组前的对应关系(这样便于理解),看逻辑

关系,理解为只是把同一类型的放到一组上的而已,对应关系还是没有改变,但是

mysql中的对应关系却已经改变了,如图分组后对应关系所示,所以这既是为什么分

组聚合后只能查询聚合操作的结果,分组的字段,和字段值只有一个的字段

**在上面对应关系的第一条科目是语文,画图画错**

**错误示例图示:**

![image-20220904111522946](E:\黑马培训\MySQL笔记\assets\image-20220904111522946.png)

**补充知识**  将一个查询结果迅速转化为一个表

**语法:**

```sql
CREATE TABLE 表名 AS SELECT 查询;
```

**例子代码:**

```sql
CREATE TABLE t_score AS
SELECT
-- MAX AVG SUM MIN 都能用
学号,
-- MAX操作的范围是根据学号分组完后的范围进行操作的
MAX(CASE
    WHEN 科目='语文' THEN 成绩 # 对每个人的语文成绩求最大
-- 相当于这里的CASE WHEN 第一次是对s001这个分组内的数据进行操作
-- 第二次是对s002这个分组内的数据进行操作
-- 当科目等于语文时 把这个分组内语文所对应的值赋值给科目
-- 接着我们可以给科目这个字段起别名,命名为语文
-- 科目这个字段还是存在的只是不显示了
END)AS `语文`,
-- 挑出数学成绩
MAX(CASE
       WHEN 科目='数学' THEN 成绩
       END)AS `数学`,
MAX(CASE
        WHEN 科目='英语' THEN 成绩
        END)AS `英语`

FROM score
GROUP BY 学号;
```

