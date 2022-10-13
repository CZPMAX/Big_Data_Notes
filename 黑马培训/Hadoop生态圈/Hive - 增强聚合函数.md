# 增强聚合函数

## 1. grouping sets

**前言**

在没有使用增强聚合函数 grouping sets时，我们如果想要在一个select查询中，得到多种分组查询的结果时，我们得使用union函数对需要的每个分组查询的结果，进行union拼接起来，但是这里会发现它们的字段个数不同，我们需要做的就是补齐这些字段 用Null填充它们的值

**需求**

分别按照==月==（month）、==天==（day）、==月和天==（month,day）统计**来访用户cookieid个数**，

**根据天分组**

![image-20221003231836710](E:\黑马培训\Hadoop生态圈\assets\image-20221003231836710.png)

**根据月分组**

![image-20221003231853194](E:\黑马培训\Hadoop生态圈\assets\image-20221003231853194.png)

**根据月和日分组**

![image-20221003231907491](E:\黑马培训\Hadoop生态圈\assets\image-20221003231907491.png)

**最后使用union拼接这些结果得到最终的结果**

![image-20221003232657804](E:\黑马培训\Hadoop生态圈\assets\image-20221003232657804.png)

==具体代码和解释==

```sql
 select
    month,
    day,
    count(cookieid) as cnt_nums
from test.t_cookie
-- 根据月和天分组 算出某个月内 具体天日期的访客量
-- 如果某个月内,天的日期不同的话 输出时会输出不止一个它对应的月
-- 即2015年的4月分内  的13号有3个访客 其实这个与根据day分组结果一样
-- 但是它多了，天数所对应月份的字段 可以看的更加直观
group by month,day
union all
select
    month,
    null as day,
    count(cookieid) as cnt_nums
from test.t_cookie
-- 根据月份统计，算出每个具体月份内总的访客人数
-- 即2015年的3月份有5个访客
group by month
union all
select
    null as month,
    day,
    count(cookieid) as cnt_nums
from test.t_cookie
-- 根据具体的天数日期分组  算出每天内有多少访客
-- 例 即2015年的4月16日 有2个访客
group by day;

-- 上述sql执行效率极低 原因是同一张表，因为有三种分组情况 所以同一张表被扫描三次 也就是三个mapperreduce 加上一个union的mapperreduce
```

> 增强聚合函数，本质上还是分组聚合

- > 1、使用grouping sets，==执行结果==与使用多个分组查询union合并结果集==一样==；
  >
  > 2、grouping sets==查询速度吊打==分组查询结果union all。大家可以使用explain执行计划查看两条sql的执行逻辑差异。
  >
  > 3、使用grouping sets只==会对表进行一次扫描==。

- hive中grouping sets语法

  > https://cwiki.apache.org/confluence/display/Hive/Enhanced+Aggregation%2C+Cube%2C+Grouping+and+Rollup#EnhancedAggregation,Cube,GroupingandRollup-GROUPINGSETSclause

  ![image-20211012222852943](E:/大数据黑马培训资料/新零售项目课件/新零售数仓_Day06_20220929/1、笔记、总结/Day06_DWS层建设实战-1.assets/image-20211012222852943.png)

  > 特别注意：==**Presto的grouping sets语法和hive略有差异**==。

  ```sql
  ---下面这个是Hive SQL语法支持
  select 
      month,day,count(cookieid) 
  from test.t_cookie 
      group by month,day 
  grouping sets (month,day,(month,day));
  -- hive中grouping sets后面出现了几个字段，那么group by里面就要写几个字段
  
  ----下面这个是Presto SQL语法支持
  select 
      month,day,count(cookieid) 
  from test.t_cookie 
      group by
  grouping sets (month,day,(month,day));
  
  --区别就是在presto的语法中，group by后面不要再加上字段了。
  ```

----

> persto语法要选择标准sql语法

## 2. cube

- ==**cube**==

  - cube翻译过来叫做立方体，data cubes就是数据立方体。

  - cube的功能：==实现多个任意维度的查询==。也可以理解为所有维度组合。

    > 公式：假如说有==**N个维度，那么所有维度的组合的个数：2^N**==
    >
    > 下面这个图，就显示了4个维度所有组合构成的数据立方体。

    ![image-20211012223824098](E:/大数据黑马培训资料/新零售项目课件/新零售数仓_Day06_20220929/1、笔记、总结/Day06_DWS层建设实战-1.assets/image-20211012223824098.png)

  - 语法

  - 0维就是不分组的情况

    ```sql
    select month,day,count(cookieid)
    from test.t_cookie
    group by
    cube (month, day);  --2个维度  2^2=4  分别：【】【month】【day】【month,day】
    
    --上述sql等价于
    select month,day,count(cookieid)
    from test.t_cookie
    group by
    grouping sets ((month,day), month, day, ());
    ```

    ![image-20211012224133056](E:/大数据黑马培训资料/新零售项目课件/新零售数仓_Day06_20220929/1、笔记、总结/Day06_DWS层建设实战-1.assets/image-20211012224133056.png)

  cube可以算出指定维度的所有理论可能分组的情况 还包含0维不分组

## 3. rollup

- ==**rollup**==

  - 语法功能：实现==从右到左递减==多级的统计,显示统计某一层次结构的聚合。

    > 即：rollup((a),(b),(c))等价于grouping sets((a,b,c),(a,b),(a),())。

  ```sql
  select month,day,count(cookieid)
  from test.t_cookie
  group by
  rollup (month,day);
  
  --等价于
  select month,day,count(cookieid)
  from test.t_cookie
  group by
  grouping sets ((month,day), (month), ());
  ```

  ![image-20211012224838605](E:\黑马培训\Hadoop生态圈\assets\image-20211012224838605.png)

----

**使用场景简介**

![image-20221004000017444](E:\黑马培训\Hadoop生态圈\assets\image-20221004000017444.png)

## 4. grouping

- 功能：使用grouping操作来==判断==当前数据==是按照哪个字段来分组的==。

  > 对于给定的分组，如果分组中==包含相应的列，则将位设置为0==，否则将其设置为1。

- 例子

  ```sql
  --为了计算高效 切换至Presto引擎中进行计算
  
  select month,
         day,
         count(cookieid),
         grouping(month)      as m,
         grouping(day)        as d,
         grouping(month, day) as m_d 
         -- 要进行分组的维度字段
         -- 最后有个值的结果可以看出是具体按哪些维度进行分组
         -- month   day  m_d   m_d的0是前面两个0进行组合 从二进制转化为十进制的
         --   0      1    1    得出结果按月份分组
         --   1      0    2    得出结果按天分组
         --   0      0    0    得出结果按月份与日期分组
  from test.t_cookie
  group by
     grouping sets (month, day, (month, day));
  ```

  ![image-20211012232435567](E:\黑马培训\Hadoop生态圈\assets\image-20211012232435567.png)

- 解释说明

  ```properties
  grouping(month)列为0时，可以看到month列都是有值的，为1时则相反，证明当前行是按照month来进行分组统计的；
  
  grouping(day)同理，为0时day列有值，为1时day为空，证明当前行时按照day来进行分组统计的；
  
  grouping(month, day)是grouping(month)、grouping(day)二进制数值组合后转换得到的数字：
  a. 按照month分组，则month=0，day=1，组合后为01，二进制转换为十进制得到数字1；
  b. 按照day分组，则month=1，day=0，组合后为10，二进制转换为十进制得到数字2；
  c. 同时按照month和day分组，则month=0，day=0，组合后为00，二进制转换为十进制得到数字0。
  
  因此可以使用grouping操作来判断当前数据是按照哪个字段来分组的。
  ```

----

#### ![image-20221004003251179](E:\黑马培训\Hadoop生态圈\assets\image-20221004003251179.png)

```properties
如果要精准识别两个或者多个字段时 要写大范围这样才能精确的识别到

例子：要精准识别出只含 C的分组组合 时

 grouping(C)=0 --这样写是不行的，因为含C的分组组合有很多种

正确写法

 grouping(A,B,C)=110 这样写是行的
```



## 5. 自测案例与row_number()函数去重

```sql
select * from test.t_order_detail;

-- 需求
-- 指标：订单量、销售额
-- 维度：日期、日期+品牌

-- 方式1：分别进行分组聚合 然后使用union all合并。
-- 日期+指标
select
    dt as "日期",
    null as as "品牌号",
    count(distinct oid) as "每日销售的订单量",
    -- 这里在进行求销售额的时候
    -- 可以通过订单金额累加得出但是这个得去重
    -- 也可以通过商品总价格的累加得出，能不去重就不去重
    -- 这里的g_price是商品的总价，不是单价
    sum(g_price) as "每日总的销售额"
from test.t_order_detail
group by dt;

-- 新增一个维度品牌
-- 日期+品牌+指标
select
   dt as "日期",
   brand_id as "品牌号",
   -- 根据日期加品牌分组后
   -- 在同一天下同一个品牌可能会出现在同一个订单号下的订单里
   -- 所以还是可以根据订单号去重统计，来得出每日每个品牌的订单量
   count(distinct oid) as "每日每个品牌的订单量",
   sum(g_price) as "每日每个品牌总的销售额"
from test.t_order_detail
group by dt,brand_id;

-- 因为这些字段所代表的含义都不相同
-- 所以说除了日期这个字段不变以外，每张表都给他新增两个字段再进行union all
select
    dt as "日期",
    null as "品牌号",
    count(distinct oid) as "每日销售的订单量",
    null as "每日每个品牌的订单量",
    sum(g_price) as "每日总的销售额",
    null as  "每日每个品牌总的销售额"
from test.t_order_detail
group by dt
union all
select
   dt as "日期",
   brand_id as "品牌号",
   null as "每日销售的订单量",
   count(distinct oid) as "每日每个品牌的订单量",
   null as "每日总的销售额",
   sum(g_price) as "每日每个品牌总的销售额"
from test.t_order_detail
group by dt,brand_id

-- ------------------------------------------------------------------------------------------------------------
-- 使用grouping sets增强聚合来计算
select
    -- 维度
     dt as "日期",
     case when grouping(brand_id)=0 -- 判断是否包含品牌id
          then brand_id
          else null end as "品牌id",

          -- 看下面的grouping sets的组合方式
          -- 分组组合里面包含dt这个维度的组合有两种，所以要精确的得出只包含日期的只能写大范围
          -- 而brand_id就不同了，分组组合情况只有一种，所以只要写一个就能精确判断出来
          -- 是不是只含有
    -- 指标
     case when grouping(dt,brand_id)=1 -- 只包含日期 那么就是总的订单量
          then count(distinct oid)
          else null end as "订单量",

     case when grouping(brand_id)=0 -- 包含品牌，就是各个品牌的订单量
          then count(distinct oid)
          else null end as "各个品牌订单量",

    case when grouping(brand_id)=1 -- 没有品牌id，就是日期内总的销售额
          then sum(g_price)
          else null end as "销售额",

    case when grouping(brand_id)=0 -- 包含品牌id 就是各个品牌在单日内的销售额
         then sum(g_price)
         else null end as "各个品牌的销售额",

    -- 分组标记
    -- 给分组打上分组标记 没有品牌 就是分组1 否则就是分组2
    case when grouping(brand_id)=1
         then 1
         else 2 end as group_id
from test.t_order_detail
group by
grouping sets (dt,(dt,brand_id));
-- 分组聚合返回的只有分组的字段和进行聚合的结果
-- 所以我们可以在这里根据有无品牌id来判断，是总的订单量还是每日每个品牌的订单量

-- ---------------------------------------------------------------------------
-- 复杂模型分析 去除数据中的重复数据 使用row number() 函数实现
select * from test.t_order_detail_dup;

-- 以oid与goods_id一起分组，然后去重
with temp as (
select
*,
row_number() over(partition by oid, goods_id) as rk
from test.t_order_detail_dup

 )
 select * from temp where rk=1;
```

