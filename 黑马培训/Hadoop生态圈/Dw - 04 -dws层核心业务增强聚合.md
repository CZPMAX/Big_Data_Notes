# DWS层搭建

# ==DWS层统计的都是日统计宽表==

# ==学习代码==

**full join 动图示例**

![full-join](E:\黑马培训\Hadoop生态圈\assets\full-join.gif)

**升级版示例**

![full-join2](E:\黑马培训\Hadoop生态圈\assets\full-join2.gif)

**其他各种连接的图解**

![image-20221011000113923](E:\黑马培训\Hadoop生态圈\assets\image-20221011000113923.png)

==分组算指标==

数据都是那些数据，但是根据不同的分组情况计算指标，得出的指标结果是不一样的

图示例子：

![image-20221008114323911](E:\黑马培训\Hadoop生态圈\assets\image-20221008114323911.png)

![image-20221008144811216](E:\黑马培训\Hadoop生态圈\assets\image-20221008144811216.png)

**增强聚合练习**

```sql
select * from test.t_cookie;
-- 使用union all对表进行拼接时
-- 每个表的字段个数，顺序，类型都应该一致才能连接上
-- 如果字段不够 缺啥补啥

select
    month,
    day,
    count(cookieid) as cnt_nums
from test.t_cookie
-- 根据月和天分组 算出某个月内 具体天日期的访客量
-- 如果某个月内,天的日期不同的话 输出时会输出不止一个它对应的月
-- 例子：2015年4月12日  5个
--      2015年4月13日  3个
-- 即2015年的4月份内  的13号有3个访客 其实这个与根据day分组结果一样
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
-- 想要提高效率 那么就得扫描一次 算出三种分组算出的结果 即完成多次不同的分组
-- 用增强聚合函数
-- grouping sets
-- grouping是分组的意思 sets是很多个集合的意思
-- 连起来的意思就是根据多个不同的集合 完成多次分组聚合的操作

select
month,
day,
count(cookieid)as cnt_nums
from test.t_cookie
-- group by month, day
group by -- 在这里后面不要写具体的分组字段了
grouping sets ((month),(day),(month,day),()); -- 分组集 列出了所有组合的情况

select
month,
day,
count(cookieid)as cnt_nums
from test.t_cookie
group by
cube(month,day)         -- 这个写法等价于上面的写法

```

**销售收入统计例子**

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



# 1. 销售主题日统计宽表

后续根据这里的日统计数据逐步上卷到年，月，周的累积统计宽表

## 1. 目标与需求

- 新零售数仓分层图

  ![image-20211012233145046](E:/大数据黑马培训资料/新零售项目课件/新零售数仓_Day06_20220929/1、笔记、总结/Day06_DWS层建设实战-1.assets/image-20211012233145046.png)

![image-20221004011427866](E:\黑马培训\Hadoop生态圈\assets\image-20221004011427866.png)

- DWS

  - 名称：==数据服务层==  service
  - 功能：==按主题划分==，形成日统计的宽表，轻度汇总==提前聚合操作==。
  - 解释：轻度提前聚合说的是先聚合出日的指标，后续可以上卷出周、月、年的指标。

  > dws这里，主题终于出现了~~~ 
  >
  > 一个主题所需要的指标、维度，可能往往需要多个DWB层的宽表共同参与计算才能得出。甚至还需要之前层如dwd等参与计算。

  ```
  轻度、重度 ：描述干活的多少  如果都做 叫做重度  只做某一部分叫做轻度
  细粒度、粗粒度：越往下粒度越细
  	举个栗子：1、计算每年的销售额  2、计算每天的销售额
  	上面这个两个都是根据时间维度计算的 哪个粒度细？ 天的粒度细  年粒度粗
  
  时间维度：年 月 日 时 分 秒
  ```


- 使用DataGrip在Hive中创建dws层

  > 注意，==**对于建库建表操作，需直接使用Hive**==，因为Presto只是一个数据分析的引擎，其语法不一定支持直接在Hive中建库建表。

  ```sql
  create database if not exists yp_dws;
  ```

  ![image-20211012234058062](E:\黑马培训\Hadoop生态圈\assets\image-20211012234058062.png)

----

## 2. DWS层搭建--销售主题宽表

==需求与建表==

- 主题需求

  - 指标

    ```properties
    销售收入、平台收入、配送成交额、小程序成交额、安卓APP成交额、苹果APP成交额、PC商城成交额、订单量、参评单量、差评单量、配送单量、退款单量、小程序订单量、安卓APP订单量、苹果APP订单量、PC商城订单量
    
    --共计: 16个指标
    指标一般通过维度算出来的
    ```

  - 维度

    ```properties
    日期、城市、商圈、店铺、品牌、商品大类、商品中类、商品小类
    
    --共计:  8个维度
    --cube所有组合:  2^8=256个
    
    注意，其中日期这个维度很特殊，特殊在我们的表就是根据日期分区的，分区的字段是day天。
    而dws这一层我们需要统计的也是按day统计，日统计宽表嘛
    这也就意味着一个分区就是一天。
    ```

  - 本主题需要维度组合

    > 提示：256个组合都计算还是计算当中的一部分，主动权在于业务、需求，我们做的是技术实现。

    ```properties
    日期 
    日期+城市
    日期+城市+商圈
    日期+城市+商圈+店铺
    -- 上面的维度因为不涉及订单内部信息，所以使用订单总价来计算下单金额
    -- 下面的维度根据商品总价来计算下单金额
    日期+品牌
    日期+大类
    日期+大类+中类
    日期+大类+中类+小类
    
    
    #各个维度之间的关系是什么样的？是并列关系还是递进关系？   维度最终落实sql层面就是分组字段。
    #判断上述8个维度组合中，哪个维度是真正起作用的维度？
    
    #技巧：如何判断是否起作用？在sql中的分组字段 把它去掉 会不会影响分组结果，如果没有影响  不起作用
    
    日期+城市  	     并列关系    城市有没有影响结果吗？  影响大
    -- 按日期分组与按日期+城市分组，区别非常大，完全不是一种概念的东西，所以按上述分组的组合是最简的形式
    
    日期+城市+商圈    递进关系    城市有没有影响结果吗？  没有影响 真正起决定作用的是 商圈
    -- 按日期+城市+商圈来分组与按日期+商圈来分组 ，没有区别，因为主体是看商圈 所以可以简化为按日期加商圈来分组
    
    #对应本项目来说 真正起决定作用的维度是
    	
    日期				   日期
    日期+城市  			 日期+城市
    日期+城市+商圈        日期+商圈     
    日期+城市+商圈+店铺    日期+店铺 
    日期+品牌            日期+品牌    
    日期+大类            日期+大类
    日期+大类+中类        日期+中类
    日期+大类+中类+小类    日期+小类
    
    -- 总结：真正起作用的是精确的小范围
    ```

  - 总计需要计算的指标

    > ==**16*8=128**==
    >
    > 1、如果不加任何限制，只是求出每个指标，那么很简单，128个sql语句进行分组聚合操作。
    >
    > 2、实际中肯定不能那么做，需要==一条sql完成128个指标的计算，并且把结果插入主题统计宽表==中。

- Hive中建表

  ![image-20221005111659954](E:\黑马培训\Hadoop生态圈\assets\image-20221005111659954.png)

  在hive中建表，根据拿到的需求，来定义字段 一般分为三大块 

  1. 维度的字段
  2. 指标的字段    -- 维度与指标的字段都要写全
  3. 如果维度字段超过2个，那么就得增加一个分组标记字段 便于我们识别

  > 既然是把一个主题相关的数据统计数据都存储在一张表中，那么意味着这张统计宽表应该要清晰的记录出维度、指标，有的计算，没有的null补上。

  ```sql
  CREATE TABLE yp_dws.dws_sale_daycount(
      
    --维度 
     create_time string COMMENT '创建时间',
     city_id string COMMENT '城市id',
     city_name string COMMENT '城市name',
     trade_area_id string COMMENT '商圈id',
     trade_area_name string COMMENT '商圈名称',
     store_id string COMMENT '店铺的id',
     store_name string COMMENT '店铺名称',
     brand_id string COMMENT '品牌id',
     brand_name string COMMENT '品牌名称',
     max_class_id string COMMENT '商品大类id',
     max_class_name string COMMENT '大类名称',
     mid_class_id string COMMENT '中类id',
     mid_class_name string COMMENT '中类名称',
     min_class_id string COMMENT '小类id',
     min_class_name string COMMENT '小类名称',
      
     -- 分组标记
     group_type string COMMENT '分组类型：store，trade_area，city，brand，min_class，mid_class，max_class，all',
      
     --   =======日统计======= 
     -- 指标
     --   销售收入
     sale_amt DECIMAL(38,2) COMMENT '销售收入',
     --   平台收入
     plat_amt DECIMAL(38,2) COMMENT '平台收入',
     -- 配送成交额
     deliver_sale_amt DECIMAL(38,2) COMMENT '配送成交额',
     -- 小程序成交额
     mini_app_sale_amt DECIMAL(38,2) COMMENT '小程序成交额',
     -- 安卓APP成交额
     android_sale_amt DECIMAL(38,2) COMMENT '安卓APP成交额',
     --  苹果APP成交额
     ios_sale_amt DECIMAL(38,2) COMMENT '苹果APP成交额',
     -- PC商城成交额
     pcweb_sale_amt DECIMAL(38,2) COMMENT 'PC商城成交额',
     -- 成交单量
     order_cnt BIGINT COMMENT '成交单量',
     -- 参评单量
     eva_order_cnt BIGINT COMMENT '参评单量comment=>cmt',
     -- 差评单量
     bad_eva_order_cnt BIGINT COMMENT '差评单量negtive-comment=>ncmt',
     -- 配送成交单量
     deliver_order_cnt BIGINT COMMENT '配送单量',
     -- 退款单量
     refund_order_cnt BIGINT COMMENT '退款单量',
     -- 小程序成交单量
     miniapp_order_cnt BIGINT COMMENT '小程序成交单量',
     -- 安卓APP订单量
     android_order_cnt BIGINT COMMENT '安卓APP订单量',
     -- 苹果APP订单量
     ios_order_cnt BIGINT COMMENT '苹果APP订单量',
     -- PC商城成交单量
     pcweb_order_cnt BIGINT COMMENT 'PC商城成交单量'
  )
  COMMENT '销售主题日统计宽表'
  PARTITIONED BY(dt STRING)
  ROW format delimited fields terminated BY '\t'
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  
  
  
  
  --主题报表的框架
  1、保存维度字段
  2、保存指标字段
  3、如果维度过多，可以考虑添加分组标记 使用不同的符号或者数字去标记不同的维度，便于后续提取
  这层的所有表没有源表做数据支撑，所以字段名等要自己解决
  要学会给字段 优雅的命名
  ```

- 扩展：如何优雅的给变量起名？

  > 字母数字不行  a b c  1 2 3
  >
  > 汉语不好  有的公司喜欢
  >
  > ==英语_英语==  表示出该变量的含义
  >
  > 参考工具：
  >
  > ​	https://translate.google.cn/  谷歌翻译
  >
  > https://unbug.github.io/codelf/  变量命名神器

-----

## 3. 简易模型分析（分组聚合union）



> 为了更好的实现一条sql计算出所有的指标，这里我们先对数据模型进行简化操作。
>
> 这个模型理解了，销售主题宽表的计算就可以实现了。

- 需求

  - 订单宽表t_order_detail

    ```sql
    --建表（在hive中创建）
    create table test.t_order_detail(
        oid string comment '订单ID',
        goods_id string comment '商品ID',
        o_price int comment '订单总金额',
        g_num int comment '商品数量',
        g_price int comment '商品单价',
        brand_id string comment '品牌ID',
        dt string comment '日期'
    ) comment '订单详情宽表_简易模型'
    row format delimited fields terminated by ',';
    
    --加载数据
    o01,g01,100,1,80,b01,2021-08-29
    o01,g02,100,1,20,b02,2021-08-29
    o02,g03,180,1,80,b01,2021-08-29
    o02,g04,180,2,40,b02,2021-08-29
    o02,g07,180,3,60,b01,2021-08-29
    o03,g02,80,1,80,b02,2021-08-30
    o04,g01,300,2,160,b01,2021-08-30
    o04,g02,300,3,60,b02,2021-08-30
    o04,g03,300,4,80,b01,2021-08-30
    ```

    ![image-20221004104817057](E:\黑马培训\Hadoop生态圈\assets\image-20221004104817057.png)

  - 需求

    - 指标：订单量、销售额
    - 维度：日期、日期+品牌

  - 代码

  ```sql
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
       -- 这里为什么对订单id去重,能实现品牌的去重呢？
       -- 一样的概念 把同一天相同品牌id的分为一组，同一个品牌不会出现在同一订单号下并产生多条记录
       -- 例子    -------------------------------
                 -- d01 --  b01 --      --    --
                 -- d01 --  b01 --      --    --
                 --     --      --      --    --
            
       -- 因为在一个订单中，你能买同一品牌的多个商品，但是我们增加的只是商品的数量而已
       --  如果出现上面的这种增量，数据就不对，就是重复数据
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
  ```

  

  - 计算出结果

    ![image-20211013232613817](E:\黑马培训\Hadoop生态圈\assets\image-20211013232613817.png)

- 实现思路（在presto中计算）

  > 立马想到：多条sql分组聚合+union合并结果集，没有的字段使用null进行填补。

  - step1:统计每天的订单量、销售额

    ```sql
    select
      dt,
      -- 为什么要去重count？
      -- 表中数据是 一个商品记录一个订单号
      -- 本质上它们都属于同一订单，所以要去重count，才能算出每日订单量
      count(distinct oid) as "订单量",
      -- sum进行累加的时候，订单金额字段进行累加要去重和商品总价进行累加不去重  在去不去重都能得到结果时，那么我们选择不去重的情况
      sum(g_price) as "销售额"
    from test.t_order_detail
    group by dt;
    
    ---------------------------------------------------------------------------------
    
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
    ```

    ![image-20211013230246919](E:\黑马培训\Hadoop生态圈\assets\image-20211013230246919.png)

  - step2:统计每天每个品牌的订单量、销售额

    ```sql
    select
      dt,
      brand_id,
      count(distinct oid) as "各品牌订单量",
      sum(g_price) as "各品牌销售额"
    from test.t_order_detail
    group by dt,brand_id;
    
    -- 这里为什么还是要对oid进行去重
    -- 因为这里的分组是 把同一天同一个品牌分到了一组 一个订单可以买同一个品牌的多件商品
    -- 比如说：我去苹果专卖店买一台ipad和一台苹果16 ，增加的只会是订单中这个品牌商品的数量，在这里例子显示的，我们增加了品牌的商品，可是没有让他的数量增加，而增加了一个相同的订单来记录这个商品，这个数据就不对，是违背我们常理分析的，所以要把这条数据去除
    -- 因为这是算同一天的每个品牌的订单量，在这同一天，同一个订单号记录的品牌的数据条数必须只有一条，因为你买多件这个品牌的商品，是让商品数增加的，而不是再新增一个相同的订单号来记录这些数据，以要在以日期，品牌号一起分组的情况下，对订单号进行去重就能完成这个去重操作。
    
    -----------------------------------------------------------------------
    
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
    ```

    ![image-20211013230507311](E:\黑马培训\Hadoop生态圈\assets\image-20211013230507311.png)

  - step3:union all合并两个查询结果集

    **老师写的**

    ```sql
    select
      dt as "日期",
      null as "品牌id",
      count(distinct oid) as "订单量",
      null as "各品牌订单量",
      sum(g_price) as "销售额",
      null as "各品牌销售额",
      1 as group_id
    from test.t_order_detail
    group by dt
    union all
    select
      dt as "日期",
      brand_id as "品牌id",
      null as "订单量",
      count(distinct oid) as "各品牌订单量",
      null as "销售额",
      sum(g_price) as "各品牌销售额",
      2 as group_id
    from test.t_order_detail
    group by dt,brand_id;
    ```

    **自理解写**

    ```sql
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
    ```

    

    > 思考：这么做有什么不妥的地方？

------

## 4. 简易模型分析（grouping sets增强聚合）

> 思路：采用grouping sets增强聚合计算，并且使用==grouping函数判断分组中是否包含字段==。
>
> **==0表示有，1表示没有==**。

- sql实现

  ```sql
  select
  count(distinct oid),
  sum(g_price)
  from test.t_order_detail
  group by grouping sets(dt,(dt,brand_id));
  
  --直接这样写，结果如下显示，不友好，无法区分是哪一个分组聚合的结果
  ```

  ![image-20211013232142281](E:\黑马培训\Hadoop生态圈\assets\image-20211013232142281.png)

- sql最终实现

  > 可以考虑使用grouping函数判断分组中是否包含指定的字段，并且配合case when进行转换。
  >
  > 注意：==**grouping函数有为0，没有是1**==。

  ```sql
  select
      dt as "日期",
      case when grouping(brand_id) =0
          then brand_id
      else null end as "品牌id",  --第二列，如果分组中有品牌id,就显示，没有就null
      -- 这里为什么还是要对oid进行去重
  -- 因为这里的分组是 把同一天同一个品牌分到了一组 一个订单可以买同一个品牌的多件商品
  -- 比如说：我去苹果专卖店买一台ipad和一台苹果16 ，增加的只会是订单中这个品牌商品的数量，在这里例子显示的，我们增加了品牌的商品，可是没有让他的数量增加，而增加了一个相同的订单来记录这个商品，这个数据就不对，是违背我们常理分析的，所以要把这条数据去除
  -- 因为这是算同一天的每个品牌的订单量，在这同一天，同一个订单号记录的品牌的数据条数必须只有一条，因为你买多件这个品牌的商品，是让商品数增加的，而不是再新增一个相同的订单号来记录这些数据，以要在以日期，品牌号一起分组的情况下，对订单号进行去重就能完成这个去重操作。
  
  -- 品牌去重的概念跟商品去重一致
      count(distinct oid) as "订单量",
      sum(g_price)
  from test.t_order_detail
  group by grouping sets(dt,(dt,brand_id));
  
  --执行上面sql，看看效果。
  --下面是最终完整版
  select
      dt as "日期",
      case when grouping(brand_id) =0  --判断是否包含brand_id
          then brand_id
          else null end as "品牌id",
      case when grouping(dt,brand_id) =1 --只包含日期 就是订单量
          then count(distinct oid)
          else null end as "订单量",
      case when grouping(brand_id) =0 --包含品牌 就是各品牌订单量
          then count(distinct oid)
          else null end as "各品牌订单量",
      case when grouping(brand_id) =1 --没有品牌，就是销售额
          then sum(g_price)
          else null end as "销售额",
      case when grouping(brand_id) =0 --包含品牌，就是各个品牌销售额
          then sum(g_price)
          else null end as "各品牌销售额",
      case when grouping(brand_id) = 1 --没有品牌 就是分组1 否则就是2
          then 1
          else 2 end as group_id
  from test.t_order_detail
  group by grouping sets(dt,(dt,brand_id));
  -- 分组聚合返回的只有分组的字段和进行聚合的结果
  -- 所以我们可以在这里根据有无品牌id来判断，是总的订单量还是每日每个品牌的订单量
  ```
  
  ![image-20211013234823518](E:\黑马培训\Hadoop生态圈\assets\image-20211013234823518.png)

-----

## 5. 复杂模型分析（去重）

![image-20221005174237916](E:\黑马培训\Hadoop生态圈\assets\image-20221005174237916.png)

- 项目订单宽表梳理

  > 根据上述的简易模型我们去梳理一下项目中的yp_dwb.dwb_order_detail订单明细宽表。
  >
  > 把属于同一笔订单的所有商品信息提取出来，验证一下数据是否匹配模型。

  ```sql
  --根据订单id分组，找出订单商品数最多的
  select
      order_id,
      count (order_id) as nums
  from yp_dwb.dwb_order_detail
  group by order_id
  order by nums desc limit 10;
  
  --查看订单ID为dd190227318021f41f的信息
  select * from yp_dwb.dwb_order_detail where order_id = 'dd190227318021f41f';
  
  --认真比对，可以发现，此订单数据有问题，大量重复。
  ```

- 问题

  > 上述简易模型中，数据是没有重复的，直接grouping sets 统计没有问题；
  >
  > 假如==数据是重复的又该如何处理==呢？如何进行去重？
  >
  > 或者说不管数据有没有重复，会不会重复，能不能设计一种解决方案，不管重复如何，先过滤重复，保证计算一定是正确的？？

  ```sql
  --建表（在hive中创建）
  create table test.t_order_detail_dup(
      oid string comment '订单ID',
      goods_id string comment '商品ID',
      o_price int comment '订单总金额',
      g_num int comment '商品数量',
      g_price int comment '商品单价',
      brand_id string comment '品牌ID',
      dt string comment '日期'
  ) comment '订单详情宽表_复杂模型'
  row format delimited fields terminated by ',';
  
  --加载数据
  o01,g01,100,1,80,b01,2021-08-29
  o01,g02,100,1,20,b02,2021-08-29
  o01,g01,100,1,80,b01,2021-08-29
  o02,g03,180,1,80,b01,2021-08-29
  o02,g04,180,2,40,b02,2021-08-29
  o02,g04,180,2,40,b02,2021-08-29
  o02,g07,180,3,60,b01,2021-08-29
  o03,g02,80,1,80,b02,2021-08-30
  o04,g01,300,2,160,b01,2021-08-30
  o04,g02,300,3,60,b02,2021-08-30
  o04,g03,300,4,80,b01,2021-08-30
  ```

![image-20211014091841158](E:\黑马培训\Hadoop生态圈\assets\image-20211014091841158.png)

- 实现思路

  > 1、==ROW_NUMBER() OVER(PARTITION BY 需要去重字段 )== ，这样相同的就会分到一组；
  >
  > 2、为分组中指定的去重字段标上行号,如果有重复的,选中行号为1的就可以。
  >
  > 3、这里只需要将oid与goods_id一起分组就可以实现去重的操作
  >
  > 解析：同一个订单oid 买多件一样的商品 正常情况下只需要增加这件商品的g_num即可，所以如果出现
  >
  > 两条记录oid与goods_id相同，那么它们就是重复的数据

  - 比如只以订单oid去重

    ```sql
    select
        oid,
        row_number() over(partition by oid) as rn1
    from test.t_order_detail_dup;
    
    --去重过程
    with tmp as (select
        oid,
        row_number() over(partition by oid) as rn1
    from test.t_order_detail_dup)
    select * from tmp where rn1 = 1;
    ```

    ![image-20211014093802292](E:\黑马培训\Hadoop生态圈\assets\image-20211014093802292.png)

  - 以订单oid+品牌brand_id去重

    ```sql
    select
        oid,
        brand_id,
        row_number() over(partition by oid,brand_id) as rn2
    from test.t_order_detail_dup;
    
    
    with tmp1 as (select
        oid,
        brand_id,
        row_number() over(partition by oid,brand_id) as rn2
    from test.t_order_detail_dup)
    select * from tmp1 where rn2 = 1;
    ```

    ![image-20211014093937149](E:\黑马培训\Hadoop生态圈\assets\image-20211014093937149.png)

  - 再比如以订单oid+品牌brand_id+商品goods_id去重

    ```sql
    select
        oid,
        brand_id,
        goods_id,
        row_number() over(partition by oid,brand_id,goods_id) as rn3
    from test.t_order_detail_dup;
    
    
    with tmp2 as (select
        oid,
        brand_id,
        goods_id,
        row_number() over(partition by oid,brand_id,goods_id) as rn3
    from test.t_order_detail_dup)
    select * from tmp2 where rn3 = 1;
    ```

    ![image-20211014094232129](E:\黑马培训\Hadoop生态圈\assets\image-20211014094232129.png)

  - 整合一起

    > ```sql
    > select
    >  oid,
    >  brand_id,
    >  goods_id,
    >  row_number() over(partition by oid) as rn1,
    >  row_number() over(partition by oid,brand_id) as rn2,
    >  row_number() over(partition by oid,brand_id,goods_id) as rn3
    > from test.t_order_detail_dup;
    > ```

    ![image-20211014094518943](E:\黑马培训\Hadoop生态圈\assets\image-20211014094518943.png)

- 结论

  > 当我们以不同维度进行组合统计的时候，==**为了避免重复数据对最终结果的影响，可以考虑配合使用row_number去重**==。

----

## 6. 开发步骤



### ==step1--字段抽取==

- 表关系

  > 一切的前提是，先了解原始数据的结构和关系。
  >
  > 对于==销售主题宽表==来说，其当中的==指标和维度字段分别来源==于DWB层：**订单明细宽表**、**店铺明细宽表**、**商品明细宽表**。
  >
  > 比如商圈、店铺等维度来自于店铺明细宽表；大中小分类来自于商品明细宽表；而成交额等指标需要依赖订单明细宽表。

  ```sql
  --以订单为准，以goods_id关联商品，以store_id关联店铺
  select *
  from dwb_order_detail o
      left join dwb_goods_detail g on o.goods_id = g.id
      left join dwb_shop_detail s on o.store_id = s.id;
  ```

- 字段抽取

  > 关联之后，字段非常多，但是并不意味着每一个字段都是销售主题宽表统计需要的；
  >
  > 因此需要==根据销售主题宽表的计算指标和维度，把相关的字段抽取出来==

  ```sql
  select
  --维度
          o.dt as create_date,--日期(注意，分区表的粒度就是按天分区)
  		s.city_id,
  		s.city_name, --城市
  		s.trade_area_id,
  		s.trade_area_name,  --商圈
  		s.id as store_id,
  		s.store_name, --店铺
  		g.brand_id,
  		g.brand_name, --品牌
  		g.max_class_id,
  		g.max_class_name, --商品大类
  		g.mid_class_id,
  		g.mid_class_name,-- 商品中类
  		g.min_class_id,
  		g.min_class_name,--商品小类
  --订单量指标
  		o.order_id, --订单id
  		o.goods_id, --商品id
  --金额指标
  		o.order_amount, --订单金额
          o.total_price, --商品金额(商品数量*商品单价)
  		o.plat_fee,   --平台分润
  		o.dispatcher_money, --配送员的运费
  --判断条件
  		o.order_from, --订单来源渠道：安卓、苹果....
  		o.evaluation_id, --评价单id,不为空表示有评价
  		o.geval_scores,  --综合评分，差评的计算
  		o.delievery_id, --配送单ID(如果不为null，表示是配送单，其他还有可能是自提、商家配送)
  		o.refund_id --退款单id,不为空表示有退款
  
  from dwb_order_detail o
      left join dwb_goods_detail g on o.goods_id = g.id
      left join dwb_shop_detail s on o.store_id = s.id;
  ```



### ==step2--row_number去重(可选)==

> 使用row_number分组去重的时候需要注意：
>
> 1、对于城市、商圈、店铺等维度的成交额计算，根据订单order_amount汇总求和即可；
>
> 2、而对于品牌、大类、中类、小类等维度成交额计算，需要根据goods_id计算。

- 以品牌为例

  ![image-20211014142049286](E:\黑马培训\Hadoop生态圈\assets\image-20211014142049286.png)

  ```sql
  --上述表的数据中，如果计算不同品牌的成交额，就不能再根据订单金额相加了
  --而是必须根据每个订单中，这个品牌的金额（商品总价）进行计算
  --因为订单中可以有不同的商品品牌，我们不能以大的范围来计算得出小范围的值。
  ```

- 分组去重

  ```sql
  -- 下面我们对不同的维度进行分组组合去重，我们只是给它们排了序，并没有像distinct那样给他们去重
  -- 只有我们用到序号的时候，把序号取1 这时候才发生了理论上的去重操作
  
  
  -- 这下面的都能进行去重 是全方面的 各个分组情况都考虑了 非常完整
  -- 每一种分组情况的去重标准都不一样，所以要多维度的进行分组组合排序
  row_number() over(partition by order_id) as order_rn,
  row_number() over(partition by order_id,g.brand_id) as brand_rn,
  row_number() over(partition by order_id,g.max_class_name) as maxclass_rn,
  row_number() over(partition by order_id,g.max_class_name,g.mid_class_name) as midclass_rn,
  row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name) as minclass_rn,
  
  
  --下面分组加入goods_id
  row_number() over(partition by order_id,g.brand_id,o.goods_id) as brand_goods_rn,
  row_number() over(partition by order_id,g.max_class_name,o.goods_id) as maxclass_goods_rn,
  row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,o.goods_id) as midclass_goods_rn,
  row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name,o.goods_id) as minclass_goods_rn
  
  -- 对数据进行去重的时候，最好是对全字段进行进行分区，然后用row_number()排序，去重，这样做虽然说最安全，但是浪费资源，执行时间也非常的久，所以不建议这么做
  -- 去重的正确做法是 寻找最少的维度进行组合分组 去重
  ```

-----

![5、不同维度计算选举的指标是不一样的---是否有商品id](E:\黑马培训\Hadoop生态圈\assets\5、不同维度计算选举的指标是不一样的---是否有商品id.png)

> 因为一个订单中不会出现两个相同的商品，如果出现不会导致又新开一个同样的订单id记录它，而只是会出现
>
> 这个订单内的相同的商品数量增加1，由此可以看出，订单id与商品id都重复时，这条数据就是重复数据

右边上面红色的 当计算销售收入时 用订单总价来计算 因为上面的计算是大范围的不涉及到订单内部的详细信息，

就比如说我要求你店铺的销售收入，这是一个总的情况，我管你卖了多少商品，卖了什么牌子的商品，我只要知道

它总的价钱即可



右边下面的  当计算销售总量时 因为我们是根据日期加品牌，日期加类别等分组统计的，它的范围很精确，不像是

上面那样范围那么大，而且一个订单中可以下单不同的商品，不同的品牌，所以我们要具体到每个商品的id，通过

商品的总价格，来计算销售收入

当算销售收入时，涉及到订单内部细节时，就比如说 算a品牌的某日销售总额 那么就只能用商品总价，而不能用

订单总价



### ==step3--grouping sets分组==

- 使用CTE表达式针对上面抽取字段、分组去重的结果进行引导

  ```sql
  with  temp as (
      select 抽取字段、row_number去重)   
  ```

  > 注意，到这一步为止，temp表的数据已经和之前的我们创建的简易模型、复杂模型差不多了。
  >
  > 后面的技术主要就是case when+grouoing判断。

- 能使用grouping sets 进行分组 前提是它计算的维度都是基于同一张表

- 根据业务需求进行维度组合，使用==grouping sets==进行分组。

- grouping sets与cube的区别

- 它们都可以根据给出的字段进行分组 但是grouping sets分组的情况哪些字段能组合在一起都是自己来决定的

  而cube是根据给出的字段，自己进行组合，所有的情况都考虑进去了，比如说给2个字段，那么组合的情况就
  
  有4种，其中包含了0维，实际情况中，我们知道要根据哪些字段进行分组，都是用grouping sets来自己进行
  
  组合
  
  ```properties
  日期
  日期+城市
  日期+城市+商圈
  日期+城市+商圈+店铺
  日期+品牌
  日期+大类
  日期+大类+中类
  日期+大类+中类+小类
  ```
  
  ```sql
  with  temp as (
      select 抽取字段、row_number去重)
  
  select
  	xxxxx
  from temp
  group by
      grouping sets(
          create_date, --日期
          (create_date,city_id,city_name),--日期+城市
          (create_date,city_id,city_name,trade_area_id,trade_area_name),--日期+城市+商圈
          (create_date,city_id,city_name,trade_area_id,trade_area_name,store_id,store_name), --日期+城市+商圈+店铺
          (create_date,brand_id,brand_name),--日期+品牌
          (create_date,max_class_id,max_class_name),--日期+大类
          (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name),--日期+大类+中类
          (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name)--日期+大类+中类+小类
      );
  ```

-----

```sql
-- todo 销售主题的日统计

--step1: 需求分析  理清主题需求中的维度 、指标的业务含义。

--step2: 建模主题表
    --表名  dws_xxxxx主题_daycount
    --字段包含：维度 维度分组标记 指标

--step3: 梳理表关系 抽取字段来支撑本主题的计算
    --哪些表可以来支撑我们这个主题的计算，哪些字段可以支持指标和维度。
    --优先到前一层dwb层去梳理·
--通过梳理发现  dwb层3张宽表都需要参与本主题的计算。
    --以订单宽表为准 去关联的店铺宽表 商品宽表
-- 根据指标与维度，去关联dws的上一层dwb层的三张宽表
with temp as (
select

     -- step4: 字段的抽取
             -- 将能够支撑本主题的指标字段 维度字段抽取出来 精准抽取
     o.dt, -- 日期
     ds.city_id,
     ds.city_name, -- 城市
     ds.trade_area_id,
     ds.trade_area_name, -- 商圈编号与商圈名字
     ds.id as store_id,
     ds.store_name, -- 店铺
     od.brand_id,
     od.brand_name, -- 品牌
     od.max_class_id,
     od.max_class_name,-- 大类
     od.mid_class_id,
     od.mid_class_name, -- 中类
     od.min_class_id,
     od.min_class_name, -- 小类

     -- 订单量
     o.order_id,
     o.goods_id, -- 订单编号 商品编号

     -- 金融相关指标
     o.order_amount, -- 订单总价
     o.total_price, -- 商品总价: 商品单价*商品数量
     o.plat_fee, -- 平台收入
     o.dispatcher_money, -- 配送收入

     -- 判断条件
     o.order_from, -- 订单来源渠道: ios pc miniapp等
     o.evaluation_id, -- 评价单id 如果不为空 表示该订单有评价
     o.geval_scores, -- 订单综合评分 根据业务需求判断好中差
     o.delievery_id, -- 配送单id 如果不为空 表示该订单有配送 其他方式可选（商家配送，用户自提）
     o.refund_id -- 退款单id 如果不为空，表示该订单有退款


from yp_dwb.dwb_order_detail o -- 订单宽表  以订单表为核心来关联其它两张表 所以连接条件中都有它的一个位置
left join yp_dwb.dwb_goods_detail od -- 商品宽表
on o.goods_id=od.id
left join yp_dwb.dwb_shop_detail ds -- 店铺宽表
on o.store_id=ds.id
 )
-- 三张宽表所涉及到的字段有很多，这些字段并不都是我们分析所需要的，所以说我们要对字段进行精准的抽取
-- 这时候我们想要进行字段的抽取，我们就要将主题需求拿过来 上面有维度与指标 按照上面所涉及到的进行字段抽取4
-- 抽完一个打个标记叉掉

-- 一共有三方面的东西
-- 1. 维度
-- 2. 指标
-- 3. 分组标记
select
 -- step7: 查询返回的维度 指标计算的结果
            -- 注意 这里返回的顺序个数类型 要和dws层建表保持一致 因为最终是需要将计算的结果插入到dws目标表中
       -- 维度字段返回
            -- 直接写 分组有就显示 没有就显示null
       dt,
       city_id,
       city_name,
       trade_area_id,
       trade_area_name,
       store_id,
       store_name,
       brand_id,
       brand_name,
       max_class_id,
       max_class_name,
       mid_class_id,
       mid_class_name,
       min_class_id,
       min_class_name,


       --分组标记字段返回   当涉及两个及以上的不同维度计算 最好有意识的在表中增加上标记字段  便于后续过滤取值
       --方式1：通用的方法 也是大家必须掌握的方法
            --todo 将所有出现的字段都放在grouping中判断
            --怎么用呢？ 以119为例 --》转换成为2进制 0111 0111 --->表示dt和brand_id有 其他都没有
       case when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =127
            then 'dt'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =63
            then 'city'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =31
            then 'trade'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =15
            then 'store'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =119
            then 'brand'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =123
            then 'max_class'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =121
            then 'mid_class'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =120
            then 'min_class'
            else 'others' end as group_type
from temp
group by grouping sets(  --step6: 由于八个维度组合都是针对同一份数据的计算  可以使用grouping sets来增强聚合
    (dt), --日期
    (dt,city_id,city_name),--日期+城市
    (dt,city_id,city_name,trade_area_id,trade_area_name),--日期+商圈
    (dt,city_id,city_name,trade_area_id,trade_area_name,store_id,store_name),--日期+店铺
    (dt,brand_id,brand_name), --日期+品牌
    (dt,max_class_id,max_class_name), --日期+大类
    (dt,max_class_id,max_class_name,mid_class_id,mid_class_name), --日期+中类
    (dt,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name) --日期+小类
);
```



### ==step4--维度字段判断==

> 提示：可以根据待插入的目标表yp_dws.dws_sale_daycount的字段顺序，把结果返回。

![image-20211014153211908](E:\黑马培训\Hadoop生态圈\assets\image-20211014153211908.png)

```sql
-- 判断有无都是根据xxx_id来判断的
-- 因为只要有了xxx_id那么下面的名字肯定会有
case when grouping(city_id) = 0   --如果分组中包含city_id 则grouping为0 那么就返回city_id
		then city_id
		else null end as city_id ,
	case when grouping(city_id) = 0
		then city_name
		else null end as city_name ,
	case when grouping(trade_area_id) = 0--商圈
		then trade_area_id
		else null end as trade_area_id ,
	case when grouping(trade_area_id) = 0
		then trade_area_name
		else null end as trade_area_name ,
	case when grouping(store_id) = 0 --店铺
		then store_id
		else null end as store_id ,
	case when grouping(store_id) = 0
		then store_name
		else null end as store_name ,
	case when grouping(brand_id) = 0 --品牌
		then brand_id
		else null end as brand_id ,
	case when grouping(brand_id) = 0
		then brand_name
		else null end as brand_name ,
	case when grouping(max_class_id) = 0 --大类
		then max_class_id
		else null end as max_class_id ,
	case when grouping(max_class_id) = 0
		then max_class_name
		else null end as max_class_name ,
	case when grouping(mid_class_id) = 0 --中类
		then mid_class_id
		else null end as mid_class_id ,
	case when grouping(mid_class_id) = 0
		then mid_class_name
		else null end as mid_class_name ,
	case when grouping(min_class_id) = 0--小类
		then min_class_id
		else null end as min_class_id ,
	case when grouping(min_class_id) = 0
		then min_class_name
		else null end as min_class_name ,
		
-- 不通用 想要使用这个方式 那么必须分组时各个分组字段之间是递进的关系
-- 利用了case when 的语法特性 
-- 从上往下执行 逐个条件匹配 一旦匹配到就返回 不再往后执行
-- 所谓的识别就是找出分组的差异性 有写store_name和没写的效果一样，这里写只是更直观的展示这是根据商圈还是店铺还是
-- 要从小范围往大范围筛选  从多个分组内值包含它一个的情况往大范围推
-- id加名字一起判断，与id单独判断是一个概念  因为没有id那么name肯定是没有的
	case when grouping(store_id,store_name) = 0  --分组类型
		then 'store'
		when grouping(trade_area_id ,trade_area_name) = 0
		then 'trade_area'
		when grouping (city_id,city_name) = 0
		then 'city'
		when grouping (brand_id,brand_name) = 0
		then 'brand'
		when grouping (min_class_id,min_class_name) = 0
		then 'min_class'
		when grouping (mid_class_id,mid_class_name) = 0
		then 'mid_class'
		when grouping (max_class_id,max_class_name) = 0
		then 'max_class'
		when grouping (dt) = 0
		then 'all' -- todo all本身表示的是0维的意思，就是表示不分组，表的全部数据  这里由于dt每个分组都有，把它当作了大家都具备的属性 相当于没有
		           -- all这里表示是根据dt分组的就是day
		else 'other' end as group_type,

--思考：还有什么方式可以精准的识别8个分组？？
--注意：在使用grouping sets的时候 为了可以快速便捷的区分每条数据是根据谁进行的分组计算，
--可以有意识的在表中添加类似分组ID 或者分组type这样的字段
--使用 1 2 3 4 或者具体的字段类标识 数据属于哪一个分组的 

-- 不涉及订单内部细节
(dt),
(dt,city_id,city_name),
(dt,city_id,city_name,area_id,area_name),
(dt,city_id,city_name,area_id,area_name,store_id,store_name),

-- 涉及订单内部细节
(dt,brand_id,brand_name),
(dt,max_class_id,max_class_name),
(dt,max_class_id,max_class_name,mid_class_id,mid_class_name)
   (dt,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name)
   
   
--
grouping(dt,city_id ,area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id)

--十进制转二进制
https://tool.lu/hexconvert/
```



---



### ==step5--销售收入统计==

![1、销售主题--指标计算--细节处理--数据合规性](E:\黑马培训\Hadoop生态圈\assets\1、销售主题--指标计算--细节处理--数据合规性.png)

![image-20221005171703952](E:\黑马培训\Hadoop生态圈\assets\image-20221005171703952.png)

```sql
	--指标计算 注意每个指标都对应着8个分组维度的计算
	--1、销售收入指标 sale_amt
	case when grouping(store_id,store_name) =0  --如果分组中包含店铺,则分组为：日期+城市+商圈+店铺
		then sum(if( order_rn = 1 and store_id is not null ,order_amount,0)) --只有分组中标号为1的(去重)，店铺不为空的才参与计算
		--then sum(if( order_rn = 1 and store_id is not null ,coalesce(order_amount,0),0))  
		--使用coalesce函数更加成熟
		
		--第一个问题：如果业务数据bug 导致null出现 后续sum求和会不会出错？
		-- 要加coalesce进行空值替换
		sum(80+20+80+null)
		sum(80+20+80+0)

		when grouping (trade_area_id ,trade_area_name) = 0 --日期+城市+商圈
		then sum(if( order_rn = 1 and trade_area_id is not null ,order_amount,0))

		when grouping (city_id,city_name) = 0 --日期+城市
		then sum(if( order_rn = 1 and city_id is not null,order_amount,0))

		when grouping (brand_id,brand_name) = 0 --日期+品牌
		then sum(if(brand_goods_rn = 1 and brand_id is not null,total_price,0))

		when grouping (min_class_id,min_class_name) = 0 --日期+大类+中类+小类
		then sum(if(minclass_goods_rn = 1 and min_class_id is not null ,total_price,0))

		when grouping (mid_class_id,mid_class_name) = 0 --日期+大类+中类
		then sum(if(midclass_goods_rn = 1 and mid_class_id is not null,total_price,0))

		when grouping (max_class_id,max_class_name) = 0 ----日期+大类
		then sum(if(maxclass_goods_rn = 1 and max_class_id is not null ,total_price,0))

		when grouping (create_date) = 0 --日期
		then sum(if(order_rn=1 and create_date is not null,order_amount,0))
	else null end  as sale_amt,
		  -- 实际上 有三个地方的脏数据 
		  -- 1. 要进行累加数据的字段值为null
		  -- 2. 数据中有重复的数据
		  -- 3. 数据的主键为null
		  -- 我们进行累加的时候要对字段进行一次处理
		  

--提示
计算 日期  城市  商圈  店铺   使用orderr_maount
计算 品牌  大类  中类  小类  使用total_price
--为什么  详细见课堂画图。

--对于重复的数据 之前使用row_number标记之后 在sum求和之前 使用if进行判断  为1的保留 
```

### ==step6--金额指标统计==

下图中，能实现对具体商品进行抽成的情况，那么必须是里面每件商品的抽成比例我们知道，这样才能进行抽取

![3、销售主题--平台收入处理细节](E:\黑马培训\Hadoop生态圈\assets\3、销售主题--平台收入处理细节.png)

```sql
   
   --2、平台收入 plat_amt
    -- 平台收入意思就是 平台收取就是对商家挣的钱的抽成 平台的抽成不可能说对商家卖的每一个商品都进行抽成，要是真的这样，那这个平台就是黑平台，它进行抽成只可能说对商家卖的每一笔订单，按多少的比例进行抽成，所以说进行平台收入指标的统计，涉及到订单内部详细信息的维度就不能统计它的总金额，我们把他们设置为null即可
     		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null ,plat_fee,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null ,plat_fee,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null,plat_fee,0))
			-- 因为下面的维度区分的太细了，没有具体的数据支撑，所以算不出来，设为null即可
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null,plat_fee,0))
			else null end  as plat_amt ,

	-- 3、配送成交额 deliver_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and delievery_id is not null ,dispatcher_money,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and delievery_id is not null,dispatcher_money,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and delievery_id is not null,dispatcher_money,0))
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and delievery_id is not null ,dispatcher_money,0))
			else null end  as deliver_sale_amt ,

	-- 4、小程序成交额 mini_app_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='miniapp' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='miniapp',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='miniapp',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='miniapp',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='miniapp',order_amount ,0))
			else null end  as mini_app_sale_amt ,

	-- 5、安卓成交额 android_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='android' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='android',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='android',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='android',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='android',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='android',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='android',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='android',order_amount ,0))
			else null end  as android_sale_amt ,

	-- 6、苹果成交额 ios_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='ios' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='ios',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='ios',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='ios',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='ios',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='ios',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='ios',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='ios',order_amount ,0))
			else null end  as ios_sale_amt ,

	-- 7、pc成交额 pcweb_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='pcweb' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='pcweb',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='pcweb',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='pcweb',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='pcweb',order_amount ,0))
			else null end  as pcweb_sale_amt ,

```



### ==step7--订单量指标统计==

```sql
    -- 8、订单量 order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null , order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null , order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 , order_id,null))
			else null end  as order_cnt ,

	--9、 参评单量 eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null, order_id,null))
			else null end  as eva_order_cnt ,
	--10、差评单量 bad_eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6 , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			else null end  as bad_eva_order_cnt ,

	--11、配送单量 deliver_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and delievery_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and delievery_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and delievery_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and delievery_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and delievery_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and delievery_id is not null, order_id,null))
			else null end  as deliver_order_cnt ,

	--12、退款单量 refund_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and refund_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and refund_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and refund_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and refund_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and refund_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and refund_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and refund_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and refund_id is not null, order_id,null))
			else null end  as refund_order_cnt ,

	-- 13、小程序订单量 miniapp_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'miniapp', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'miniapp', order_id,null))
			else null end  as miniapp_order_cnt ,

	-- 14、android订单量 android_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'android', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'android', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'android', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'android', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'android', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'android', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'android', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'android', order_id,null))
			else null end  as android_order_cnt ,

	-- 15、ios订单量 ios_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'ios', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'ios', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'ios', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'ios', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'ios', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'ios', order_id,null))
			else null end  as ios_order_cnt ,

	--16、pcweb订单量 pcweb_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'pcweb', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'pcweb', order_id,null))
			else null end  as pcweb_order_cnt ,
```

-----



### ==最终完整版sql实现==

```sql
insert into  hive.yp_dws.dws_sale_daycount
with temp as (select
    --维度抽取
    o.dt as create_date,  --日期
    s.city_id,
    s.city_name,--城市
    s.trade_area_id,
    s.trade_area_name,--商圈
    s.id as store_id,
    s.store_name,--店铺
    g.brand_id,
    g.brand_name, --品牌
    g.max_class_id,
    g.max_class_name,--商品大类
    g.mid_class_id,
    g.mid_class_name,--商品中类
    g.min_class_id,
    g.min_class_name, --商品小类

    --订单量指标
    o.order_id, --订单ID
    o.goods_id, --商品ID


    --金额指标
    o.order_amount,--订单金额
    o.total_price,--商品金额
    o.plat_fee, --平台分润
    o.dispatcher_money,--配送员运费

    --判断条件
    o.order_from,--订单来源：安卓，苹果啥的...
    o.evaluation_id,--评论单ID（如果不为null,表示该订单有评价）
    o.geval_scores, --订单评分（用于计算差评）
    o.delievery_id, --配送单ID(如果不为null，表示是配送单，其他还有可能是自提、商家配送)
    o.refund_id, --退款单ID(如果不为null,表示有退款)

    --分组去重
    row_number() over(partition by order_id) as order_rn,
    row_number() over(partition by order_id,g.brand_id) as brand_rn,
    row_number() over(partition by order_id,g.max_class_name) as maxclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name) as midclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name) as minclass_rn,

    --下面分组加入goods_id
    row_number() over(partition by order_id,g.brand_id,o.goods_id) as brand_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,o.goods_id) as maxclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,o.goods_id) as midclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name,o.goods_id) as minclass_goods_rn

from dwb_order_detail o
    left join dwb_goods_detail g on o.goods_id = g.id
    left join dwb_shop_detail s on o.store_id = s.id)

select
    --查询出来的字段个数、顺序、类型要和待插入表的一致  dws_sale_daycount
case when grouping(city_id) = 0   --如果分组中包含city_id 则grouping为0 那么就返回city_id
		then city_id
		else null end as city_id ,
	case when grouping(city_id) = 0
		then city_name
		else null end as city_name ,
	case when grouping(trade_area_id) = 0--商圈
		then trade_area_id
		else null end as trade_area_id ,
	case when grouping(trade_area_id) = 0
		then trade_area_name
		else null end as trade_area_name ,
	case when grouping(store_id) = 0 --店铺
		then store_id
		else null end as store_id ,
	case when grouping(store_id) = 0
		then store_name
		else null end as store_name ,
	case when grouping(brand_id) = 0 --品牌
		then brand_id
		else null end as brand_id ,
	case when grouping(brand_id) = 0
		then brand_name
		else null end as brand_name ,
	case when grouping(max_class_id) = 0 --大类
		then max_class_id
		else null end as max_class_id ,
	case when grouping(max_class_id) = 0
		then max_class_name
		else null end as max_class_name ,
	case when grouping(mid_class_id) = 0 --中类
		then mid_class_id
		else null end as mid_class_id ,
	case when grouping(mid_class_id) = 0
		then mid_class_name
		else null end as mid_class_name ,
	case when grouping(min_class_id) = 0--小类
		then min_class_id
		else null end as min_class_id ,
	case when grouping(min_class_id) = 0
		then min_class_name
		else null end as min_class_name ,

	case when grouping(store_id,store_name) = 0  --分组类型
		then 'store'
		when grouping(trade_area_id ,trade_area_name) = 0
		then 'trade_area'
		when grouping (city_id,city_name) = 0
		then 'city'
		when grouping (brand_id,brand_name) = 0
		then 'brand'
		when grouping (min_class_id,min_class_name) = 0
		then 'min_class'
		when grouping (mid_class_id,mid_class_name) = 0
		then 'mid_class'
		when grouping (max_class_id,max_class_name) = 0
		then 'max_class'
		when grouping (create_date) = 0
		then 'all'
		else 'other' end as group_type,

	--指标计算 注意每个指标都对应着8个分组维度的计算
	--不能将他们整合到一起进行计算，因为我们需要它们能返回多个字段，如果整合到了一起，那么字段就只剩一个了，这样是不可取的
	
	--1、销售收入指标 sale_amt
	case when grouping(store_id,store_name) =0  --如果分组中包含店铺,则分组为：日期+城市+商圈+店铺
		then sum(if( order_rn = 1 and store_id is not null ,order_amount,0)) --只有分组中标号为1的(去重)，店铺不为空的才参与计算
		--then sum(if( order_rn = 1 and store_id is not null ,coalesce(order_amount,0),0))  --使用coalesce函数更加成熟

		when grouping (trade_area_id ,trade_area_name) = 0 --日期+城市+商圈
		then sum(if( order_rn = 1 and trade_area_id is not null ,order_amount,0))

		when grouping (city_id,city_name) = 0 --日期+城市
		then sum(if( order_rn = 1 and city_id is not null,order_amount,0))

		when grouping (brand_id,brand_name) = 0 --日期+品牌
		then sum(if(brand_goods_rn = 1 and brand_id is not null,total_price,0))

		when grouping (min_class_id,min_class_name) = 0 --日期+大类+中类+小类
		then sum(if(minclass_goods_rn = 1 and min_class_id is not null ,total_price,0))

		when grouping (mid_class_id,mid_class_name) = 0 --日期+大类+中类
		then sum(if(midclass_goods_rn = 1 and mid_class_id is not null,total_price,0))

		when grouping (max_class_id,max_class_name) = 0 ----日期+大类
		then sum(if(maxclass_goods_rn = 1 and max_class_id is not null ,total_price,0))

		when grouping (create_date) = 0 --日期
		then sum(if(order_rn=1 and create_date is not null,order_amount,0))
	else null end  as sale_amt,

    --2、平台收入 plat_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null ,plat_fee,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null ,plat_fee,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null,plat_fee,0))
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null,plat_fee,0))
			else null end  as plat_amt ,

	-- 3、配送成交额 deliver_sale_amt
	-- 要计算配送的成交额，那么得筛选出配送订单id不为null的字段进行计算
	-- 每种分组情况所累加出来的订单总额都不一样
	-- 累加的字段为dispatcher_money
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and delievery_id is not null ,dispatcher_money,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and delievery_id is not null,dispatcher_money,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and delievery_id is not null,dispatcher_money,0))
			-- 订单配送金额求和我们只知道订单的配送成交金额 所以我们只能计算这些大的方面的
			-- 当涉及到订单内部的信息的时候我们依然还是算不出具体数据，那么我们只能设置为null
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and delievery_id is not null ,dispatcher_money,0))
			else null end  as deliver_sale_amt ,

	-- 4、小程序成交额 mini_app_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='miniapp' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='miniapp',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='miniapp',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='miniapp',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='miniapp',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='miniapp',order_amount ,0))
			else null end  as mini_app_sale_amt ,

	-- 5、安卓成交额 android_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='android' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='android',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='android',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='android',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='android',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='android',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='android',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='android',order_amount ,0))
			else null end  as android_sale_amt ,

	-- 6、苹果成交额 ios_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='ios' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='ios',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='ios',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='ios',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='ios',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='ios',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='ios',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='ios',order_amount ,0))
			else null end  as ios_sale_amt ,

	-- 7、pc成交额 pcweb_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='pcweb' ,order_amount,0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='pcweb',order_amount,0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='pcweb',order_amount,0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='pcweb',total_price,0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='pcweb',total_price,0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='pcweb',order_amount ,0))
			else null end  as pcweb_sale_amt ,

    -- 8、订单量 order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null , order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null , order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 , order_id,null))
			else null end  as order_cnt ,

	--9、 参评单量 eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null, order_id,null))
			else null end  as eva_order_cnt ,
	--10、差评单量 bad_eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6 , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			else null end  as bad_eva_order_cnt ,

	--11、配送单量 deliver_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and delievery_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and delievery_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and delievery_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and delievery_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and delievery_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and delievery_id is not null, order_id,null))
			else null end  as deliver_order_cnt ,

	--12、退款单量 refund_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and refund_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and refund_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and refund_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and refund_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and refund_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and refund_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and refund_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and refund_id is not null, order_id,null))
			else null end  as refund_order_cnt ,

	-- 13、小程序订单量 miniapp_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'miniapp', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'miniapp', order_id,null))
			else null end  as miniapp_order_cnt ,

	-- 14、android订单量 android_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'android', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'android', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'android', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'android', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'android', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'android', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'android', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'android', order_id,null))
			else null end  as android_order_cnt ,

	-- 15、ios订单量 ios_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'ios', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'ios', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'ios', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'ios', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'ios', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'ios', order_id,null))
			else null end  as ios_order_cnt ,

	--16、pcweb订单量 pcweb_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'pcweb', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'pcweb', order_id,null))
			else null end  as pcweb_order_cnt ,

        create_date as dt  --日期

from temp
group by
    grouping sets(
        create_date, --日期
        (create_date,city_id,city_name),--日期+城市
        (create_date,city_id,city_name,trade_area_id,trade_area_name),--日期+城市+商圈
        (create_date,city_id,city_name,trade_area_id,trade_area_name,store_id,store_name), --日期+城市+商圈+店铺
        (create_date,brand_id,brand_name),--日期+品牌
        (create_date,max_class_id,max_class_name),--日期+大类
        (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name),--日期+大类+中类
        (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name)--日期+大类+中类+小类
    );
```

==自己跟着老师写的代码==

==指标的计算== 以**销售收入**为基础 后续的指标计算直接以这个基础进行修改即可

![image-20221005175353861](E:\黑马培训\Hadoop生态圈\assets\image-20221005175353861.png)

```sql
--销售主题的日统计

--step1: 需求分析  理清主题需求中的维度 、指标的业务含义。

--step2: 建模主题表
    --表名  dws_xxxxx主题_daycount
    --字段包含：维度 维度分组标记 指标

--step3: 梳理表关系 抽取字段来支撑本主题的计算
    --哪些表可以来支撑我们这个主题的计算，哪些字段可以支持指标和维度。
    --优先到前一层dwb层去梳理·
--通过梳理发现  dwb层3张宽表都需要参与本主题的计算。
    --以订单宽表为准 去关联的店铺宽表 商品宽表
with tmp as (select
    --step4: 字段的抽取
        --将能够支撑本主题的指标字段 维度字段抽取出来  精准抽取
    --维度字段
    o.dt,--日期
    s.city_id,
    s.city_name, --城市
    s.trade_area_id,
    s.trade_area_name, --商圈
    s.id as store_id,
    s.store_name, --店铺
    g.brand_id,
    g.brand_name, --品牌
    g.max_class_id,
    g.max_class_name, --大类
    g.mid_class_id,
    g.mid_class_name,--中类
    g.min_class_id,
    g.min_class_name, --小类

    --订单量
    o.order_id,
    o.goods_id, --订单编号 商品编号

    --金融相关的指标
    o.order_amount, --订单总价
    o.total_price, --商品总价：商品单价*商品数量
    o.plat_fee, --平台收入
    o.dispatcher_money,--配送收入

    --判断条件
    o.order_from, --订单来源渠道：ios pc miniapp等
    o.evaluation_id, --评价单id  如果不为空 表示该订单有评价
    o.geval_scores, --订单综合评分 根据业务需求判断好中差
    o.delievery_id, --配送单id 如果不为空 表示该订单有配送  其他可选方式（商家配送、用户自提）
    o.refund_id,  --退款单id 如果不为空 表示该订单有退款发生

    --step5: (可选)如果想对数据进行去重 可以使用row_number在这里进行判断
    row_number() over(partition by order_id) as order_rn,
    row_number() over(partition by order_id,g.brand_id) as brand_rn,
    row_number() over(partition by order_id,g.max_class_name) as maxclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name) as midclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name) as minclass_rn,

    --下面分组加入goods_id
    row_number() over(partition by order_id,g.brand_id,o.goods_id) as brand_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,o.goods_id) as maxclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,o.goods_id) as midclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name,o.goods_id) as minclass_goods_rn
from hive.yp_dwb.dwb_order_detail o  --订单宽表
    left join hive.yp_dwb.dwb_shop_detail s
        on o.store_id = s.id --店铺宽表
    left join hive.yp_dwb.dwb_goods_detail g
        on o.goods_id =g.id )--商品宽表
select
       --step7: 查询返回的维度 指标计算的结果
            --注意 这里返回的顺序个数类型 要和dws层建表保持一致 因为最终是需要将计算的结果插入到dws目标表中
       --维度字段返回
            --直接写 分组有就显示 没有就显示null
--        city_id,
--        city_name,
--        trade_area_id,
--        trade_area_name,
--        store_id,
--        store_name,
--        brand_id,
--        brand_name,
--        max_class_id,
--        max_class_name,
--        mid_class_id,
--        mid_class_name,
--        min_class_id,
--        min_class_name,

       --分组标记字段返回   当涉及两个及以上的不同维度计算 最好有意识的在表中增加上标记字段  便于后续过滤取值
       --方式1：通用的方法 也是大家必须掌握的方法
            --todo 将所有出现的字段都放在grouping中判断
            --怎么用呢？ 以119为例 --》转换成为2进制 0111 0111 --->表示dt和brand_id有 其他都没有
       case when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =127
            then 'dt'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =63
            then 'city'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =31
            then 'trade'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =15
            then 'store'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =119
            then 'brand'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =123
            then 'max_class'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =121
            then 'mid_class'
            when grouping(dt,city_id,trade_area_id,store_id,brand_id,max_class_id,mid_class_id,min_class_id) =120
            then 'min_class'
            else 'others' end as group_type
			
		-- step8 指标的计算
		-- 指标1： 销售收入
		-- 能够统一采用一种方式计算的前提是  各个维度计算的指标都依赖于同一个字段
		-- 这里分析发现 有的销售收入是根据订单金额计算  有的是根据商品总价计算
		-- 原因就是一个订单中可以买多个商品 多个商品可能属于不同的品牌
		-- 销售金额的计算  具体根据订单金额还是根据商品总价计算 要看这个维度有没有涉及到商品内部细节
		-- 有涉及到就用商品总价计算，没有就用订单金额来计算
		-- COALESCE的作用 第一个参数如果为null，它的值就用第二个参数来代替，如果不为null，它的值就还是它本身
		  case when grouping(store_id,store_name) = 0  --分组类型
		  then SUM(if(order_rn=1 and store_id is not null, COALESCE(order_amount,0),0))
		  when grouping(trade_area_id ,trade_area_name) = 0
		  then SUM(if(order_rn=1 and trade_area_idis not null, COALESCE(order_amount,0),0))
		  when grouping (city_id,city_name) = 0
		  then SUM(if(order_rn=1 and city_id is not null, COALESCE(order_amount,0),0))
		  when grouping (brand_id,brand_name) = 0
		  then SUM(if(brand_goods_rn=1 and brand_id is not null, COALESCE(total_price,0),0))
		  when grouping (min_class_id,min_class_name) = 0
		  then SUM(if(minclass_goods_rn=1 and min_class_id is not null, COALESCE(total_price,0),0))
		  when grouping (mid_class_id,mid_class_name) = 0
		  then SUM(if(midclass_goods_rn=1 and mid_class_id is not null, COALESCE(mid_class_id,0),0))
		  when grouping (max_class_id,max_class_name) = 0
		  then SUM(if(maxclass_goods_rn=1 and max_class_id is not null, COALESCE(mid_class_id,0),0))
		  when grouping (dt) = 0
		  then SUM(if(order_rn=1 and dt is not null, COALESCE(order_amount,0),0))
		  else NULL end as sale_amt,
		  -- 能采取这种方式进行判断计算的，要求我们的数据必须非常的干净
		  -- 如果主键store_id丢失，为非法值null的 ，核心指标要进行累加的字段空值，数据重复
		  -- 就不能这样使用
		  -- 实际的代码要在then后面sum里面作用的字段进程判断
		  -- 实际上 有三个地方的脏数据 
		  -- 1. 要进行累加数据的字段值为null
		  -- 2. 数据中有重复的数据
		  -- 3. 数据的主键为null
		  
		  
		--指标2 平台收入
		-- 一笔订单 分润情况中平台抽取多少钱  设计订单内部的维度不进行求和统计
		ase when grouping(store_id,store_name) = 0  --分组类型
		  then SUM(if(order_rn=1 and store_id is not null, COALESCE(plat_fee,0),0))
		  when grouping(trade_area_id ,trade_area_name) = 0
		  then SUM(if(order_rn=1 and trade_area_idis not null, COALESCE(plat_fee,0),0))
		  when grouping (city_id,city_name) = 0
		  then SUM(if(order_rn=1 and city_id is not null, COALESCE(plat_fee,0),0))
		  when grouping (brand_id,brand_name) = 0
		  then NULL
		  when grouping (min_class_id,min_class_name) = 0
		  then NULL
		  when grouping (mid_class_id,mid_class_name) = 0
		  then NULL
		  when grouping (max_class_id,max_class_name) = 0
		  then NULL
		  when grouping (dt) = 0
		  then SUM(if(order_rn=1 and dt is not null, COALESCE(plat_fee,0),0))
		  else NULL end as group_type,

from tmp
group by grouping sets(  --step6: 由于八个维度组合都是针对同一份数据的计算  可以使用grouping sets来增强聚合
    (dt), --日期
    (dt,city_id,city_name),--日期+城市
    (dt,city_id,city_name,trade_area_id,trade_area_name),--日期+商圈
    (dt,city_id,city_name,trade_area_id,trade_area_name,store_id,store_name),--日期+店铺
    (dt,brand_id,brand_name), --日期+品牌
    (dt,max_class_id,max_class_name), --日期+大类
    (dt,max_class_id,max_class_name,mid_class_id,mid_class_name), --日期+中类
    (dt,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name) --日期+小类
);


```

==完整创建表与导入数据代码==

```sql
------------------------------ 创建销售统计宽表 -----------------------------------
-- 需求
-- 维度: 日期、城市、商圈、店铺、品牌、商品大类、商品中类、商品小类
-- 指标: 销售收入、平台收入、配送成交额、小程序成交额、安卓APP成交额、苹果APP成交额、
-- PC商城成交额、订单量、参评单量、差评单量、配送单量、退款单量、小程序订单量、安卓APP订单量、
-- 苹果APP订单量、PC商城订单量
-- todo 指标一般是通过维度算出来的
--  例如计算一个大维度分组不涉及订单内部细节时求销售收入，
--  就是对订单金额进行求累加，得出的金额就是指标销售收入，
--  这里根据的维度是三张dwb层的宽表中的某些维度字段，不是指的上面标记的维度

CREATE TABLE IF NOT EXISTS yp_dws.dws_sale_daycount(

    -- 维度
    create_time STRING COMMENT '订单创建时间',
    city_id STRING COMMENT '城市id',
    city_name STRING COMMENT '城市名称',
    trade_area_id STRING COMMENT '商圈id',
    trade_area_name STRING COMMENT '商圈名称',
    store_id STRING COMMENT '店铺id',
    store_name STRING COMMENT '店铺名称',
    brand_id STRING COMMENT '品牌id',
    brand_name STRING COMMENT '品牌名称',
    max_class_id STRING COMMENT '大类id',
    max_class_name STRING COMMENT '大类名称',
    mid_class_id STRING COMMENT '中类id',
    mid_class_name STRING COMMENT '中类名称',
    min_class_id STRING COMMENT '小类id',
    min_class_name STRING COMMENT '小类名称',

    -- 因为涉及到2个维度以上的分组 所以要用分组类型来标记
    group_type varchar COMMENT '分组类型',

    -- 指标
     --   销售收入
   sale_amt DECIMAL(38,2) COMMENT '销售收入',
   --   平台收入
   plat_amt DECIMAL(38,2) COMMENT '平台收入',
   -- 配送成交额
   deliver_sale_amt DECIMAL(38,2) COMMENT '配送成交额',
   -- 小程序成交额
   mini_app_sale_amt DECIMAL(38,2) COMMENT '小程序成交额',
   -- 安卓APP成交额
   android_sale_amt DECIMAL(38,2) COMMENT '安卓APP成交额',
   --  苹果APP成交额
   ios_sale_amt DECIMAL(38,2) COMMENT '苹果APP成交额',
   -- PC商城成交额
   pcweb_sale_amt DECIMAL(38,2) COMMENT 'PC商城成交额',
   -- 成交单量
   order_cnt BIGINT COMMENT '成交单量',
   -- 参评单量
   eva_order_cnt BIGINT COMMENT '参评单量comment=>cmt',
   -- 差评单量
   bad_eva_order_cnt BIGINT COMMENT '差评单量negtive-comment=>ncmt',
   -- 配送成交单量
   deliver_order_cnt BIGINT COMMENT '配送单量',
   -- 退款单量
   refund_order_cnt BIGINT COMMENT '退款单量',
   -- 小程序成交单量
   miniapp_order_cnt BIGINT COMMENT '小程序成交单量',
   -- 安卓APP订单量
   android_order_cnt BIGINT COMMENT '安卓APP订单量',
   -- 苹果APP订单量
   ios_order_cnt BIGINT COMMENT '苹果APP订单量',
   -- PC商城成交单量
   pcweb_order_cnt BIGINT COMMENT 'PC商城成交单量'


)
COMMENT '销售主题日统计宽表'
PARTITIONED BY(dt STRING)
ROW format delimited fields terminated BY '\t'
stored AS orc tblproperties ('orc.compress' = 'SNAPPY');

insert into  hive.yp_dws.dws_sale_daycount
with temp as (select
    --维度抽取
    o.dt as create_date,  --日期
    s.city_id,
    s.city_name,--城市
    s.trade_area_id,
    s.trade_area_name,--商圈
    s.id as store_id,
    s.store_name,--店铺
    g.brand_id,
    g.brand_name, --品牌
    g.max_class_id,
    g.max_class_name,--商品大类
    g.mid_class_id,
    g.mid_class_name,--商品中类
    g.min_class_id,
    g.min_class_name, --商品小类

    --订单量指标
    o.order_id, --订单ID
    o.goods_id, --商品ID


    --金额指标
    o.order_amount,--订单金额
    o.total_price,--商品金额
    o.plat_fee, --平台分润
    o.dispatcher_money,--配送员运费

    --判断条件
    o.order_from,--订单来源：安卓，苹果啥的...
    o.evaluation_id,--评论单ID（如果不为null,表示该订单有评价）
    o.geval_scores, --订单评分（用于计算差评）
    o.delievery_id, --配送单ID(如果不为null，表示是配送单，其他还有可能是自提、商家配送)
    o.refund_id, --退款单ID(如果不为null,表示有退款)

    --分组去重
    row_number() over(partition by order_id) as order_rn,
    row_number() over(partition by order_id,g.brand_id) as brand_rn,
    row_number() over(partition by order_id,g.max_class_name) as maxclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name) as midclass_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name) as minclass_rn,

    --下面分组加入goods_id
    row_number() over(partition by order_id,g.brand_id,o.goods_id) as brand_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,o.goods_id) as maxclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,o.goods_id) as midclass_goods_rn,
    row_number() over(partition by order_id,g.max_class_name,g.mid_class_name,g.min_class_name,o.goods_id) as minclass_goods_rn

from yp_dwb.dwb_order_detail o
    left join yp_dwb.dwb_goods_detail g on o.goods_id = g.id
    left join yp_dwb.dwb_shop_detail s on o.store_id = s.id)

select
    --查询出来的字段个数、顺序、类型要和待插入表的一致  dws_sale_daycount
        create_date,
case when grouping(city_id) = 0   --如果分组中包含city_id 则grouping为0 那么就返回city_id
		then city_id
		else null end as city_id ,
	case when grouping(city_id) = 0
		then city_name
		else null end as city_name ,
	case when grouping(trade_area_id) = 0--商圈
		then trade_area_id
		else null end as trade_area_id ,
	case when grouping(trade_area_id) = 0
		then trade_area_name
		else null end as trade_area_name ,
	case when grouping(store_id) = 0 --店铺
		then store_id
		else null end as store_id ,
	case when grouping(store_id) = 0
		then store_name
		else null end as store_name ,
	case when grouping(brand_id) = 0 --品牌
		then brand_id
		else null end as brand_id ,
	case when grouping(brand_id) = 0
		then brand_name
		else null end as brand_name ,
	case when grouping(max_class_id) = 0 --大类
		then max_class_id
		else null end as max_class_id ,
	case when grouping(max_class_id) = 0
		then max_class_name
		else null end as max_class_name ,
	case when grouping(mid_class_id) = 0 --中类
		then mid_class_id
		else null end as mid_class_id ,
	case when grouping(mid_class_id) = 0
		then mid_class_name
		else null end as mid_class_name ,
	case when grouping(min_class_id) = 0--小类
		then min_class_id
		else null end as min_class_id ,
	case when grouping(min_class_id) = 0
		then min_class_name
		else null end as min_class_name ,

	case when grouping(store_id,store_name) = 0  --分组类型
		then 'store'
		when grouping(trade_area_id ,trade_area_name) = 0
		then 'trade_area'
		when grouping (city_id,city_name) = 0
		then 'city'
		when grouping (brand_id,brand_name) = 0
		then 'brand'
		when grouping (min_class_id,min_class_name) = 0
		then 'min_class'
		when grouping (mid_class_id,mid_class_name) = 0
		then 'mid_class'
		when grouping (max_class_id,max_class_name) = 0
		then 'max_class'
		when grouping (create_date) = 0
		then 'all'
		else 'other' end as group_type,

	--指标计算 注意每个指标都对应着8个分组维度的计算
	--不能将他们整合到一起进行计算，因为我们需要它们能返回多个字段，如果整合到了一起，那么字段就只剩一个了，这样是不可取的

	--1、销售收入指标 sale_amt
	case when grouping(store_id,store_name) =0  --如果分组中包含店铺,则分组为：日期+城市+商圈+店铺
		then sum(if( order_rn = 1 and store_id is not null ,COALESCE(order_amount,0),0)) --只有分组中标号为1的(去重)，店铺不为空的才参与计算
		--then sum(if( order_rn = 1 and store_id is not null ,coalesce(order_amount,0),0))  --使用coalesce函数更加成熟

		when grouping (trade_area_id ,trade_area_name) = 0 --日期+城市+商圈
		then sum(if( order_rn = 1 and trade_area_id is not null ,COALESCE(order_amount,0),0))

		when grouping (city_id,city_name) = 0 --日期+城市
		then sum(if( order_rn = 1 and city_id is not null,COALESCE(order_amount,0),0))

		when grouping (brand_id,brand_name) = 0 --日期+品牌
		then sum(if(brand_goods_rn = 1 and brand_id is not null,COALESCE(total_price,0),0))

		when grouping (min_class_id,min_class_name) = 0 --日期+大类+中类+小类
		then sum(if(minclass_goods_rn = 1 and min_class_id is not null ,COALESCE(total_price,0),0))

		when grouping (mid_class_id,mid_class_name) = 0 --日期+大类+中类
		then sum(if(midclass_goods_rn = 1 and mid_class_id is not null,COALESCE(total_price,0),0))

		when grouping (max_class_id,max_class_name) = 0 ----日期+大类
		then sum(if(maxclass_goods_rn = 1 and max_class_id is not null ,COALESCE(total_price,0),0))

		when grouping (create_date) = 0 --日期
		then sum(if(order_rn=1 and create_date is not null,COALESCE(order_amount,0),0))
	else null end  as sale_amt,

    --2、平台收入 plat_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null ,COALESCE(plat_fee,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null ,COALESCE(plat_fee,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null,COALESCE(plat_fee,0),0))
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null,COALESCE(plat_fee,0),0))
			else null end  as plat_amt ,

	-- 3、配送成交额 deliver_sale_amt
	-- 要计算配送的成交额，那么得筛选出配送订单id不为null的字段进行计算
	-- 每种分组情况所累加出来的订单总额都不一样
	-- 累加的字段为dispatcher_money
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and delievery_id is not null ,COALESCE(dispatcher_money,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and delievery_id is not null,COALESCE(dispatcher_money,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and delievery_id is not null,COALESCE(dispatcher_money,0),0))
			-- 订单配送金额求和我们只知道订单的配送成交金额 所以我们只能计算这些大的方面的
			-- 当涉及到订单内部的信息的时候我们依然还是算不出具体数据，那么我们只能设置为null
			when grouping (brand_id,brand_name) = 0
			then null
			when grouping (min_class_id,min_class_name) = 0
			then null
			when grouping (mid_class_id,mid_class_name) = 0
			then null
			when grouping (max_class_id,max_class_name) = 0
			then null
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and delievery_id is not null ,COALESCE(dispatcher_money,0),0))
			else null end  as deliver_sale_amt ,

	-- 4、小程序成交额 mini_app_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='miniapp' ,COALESCE(order_amount,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='miniapp',COALESCE(order_amount,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='miniapp',COALESCE(order_amount,0),0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='miniapp',COALESCE(total_price,0),0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='miniapp',COALESCE(total_price,0),0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='miniapp',COALESCE(total_price,0),0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='miniapp',COALESCE(total_price,0),0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='miniapp',COALESCE(order_amount,0) ,0))
			else null end  as mini_app_sale_amt ,

	-- 5、安卓成交额 android_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='android' ,COALESCE(order_amount,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='android',COALESCE(order_amount,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='android',COALESCE(order_amount,0),0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='android',COALESCE(total_price,0),0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='android',COALESCE(total_price,0),0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='android',COALESCE(total_price,0),0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='android',COALESCE(total_price,0),0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='android',COALESCE(order_amount,0) ,0))
			else null end  as android_sale_amt ,

	-- 6、苹果成交额 ios_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='ios' ,COALESCE(order_amount,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='ios',COALESCE(order_amount,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='ios',COALESCE(order_amount,0),0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='ios',COALESCE(total_price,0),0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='ios',COALESCE(total_price,0),0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='ios',COALESCE(total_price,0),0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='ios',COALESCE(total_price,0),0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='ios',COALESCE(order_amount,0) ,0))
			else null end  as ios_sale_amt ,

	-- 7、pc成交额 pcweb_sale_amt
		case when grouping(store_id,store_name) =0
			then sum(if( order_rn = 1 and store_id is not null and order_from='pcweb' ,COALESCE(order_amount,0),0))
			when grouping (trade_area_id ,trade_area_name) = 0
			then sum(if( order_rn = 1 and trade_area_id is not null and order_from='pcweb',COALESCE(order_amount,0),0))
			when grouping (city_id,city_name) = 0
			then sum(if( order_rn = 1 and city_id is not null and order_from='pcweb',COALESCE(order_amount,0),0))
			when grouping (brand_id,brand_name) = 0
			then sum(if(brand_goods_rn = 1 and brand_id is not null and order_from='pcweb',COALESCE(total_price,0),0))
			when grouping (min_class_id,min_class_name) = 0
			then sum(if(minclass_goods_rn = 1 and min_class_id is not null and order_from='pcweb',COALESCE(total_price,0),0))
			when grouping (mid_class_id,mid_class_name) = 0
			then sum(if(midclass_goods_rn = 1 and mid_class_id is not null and order_from='pcweb',COALESCE(total_price,0),0))
			when grouping (max_class_id,max_class_name) = 0
			then sum(if(maxclass_goods_rn = 1 and max_class_id is not null and order_from='pcweb',COALESCE(total_price,0),0))
			when grouping (create_date) = 0
			then sum(if(order_rn=1 and create_date is not null and order_from='pcweb',COALESCE(order_amount,0) ,0))
			else null end  as pcweb_sale_amt ,

    -- 8、订单量 order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null , order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null , order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 , order_id,null))
			else null end  as order_cnt ,

	--9、 参评单量 eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null , order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null , order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null , order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null , order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null, order_id,null))
			else null end  as eva_order_cnt ,
	--10、差评单量 bad_eva_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6 , order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null  and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and evaluation_id is not null and coalesce(geval_scores,0) <6, order_id,null))
			else null end  as bad_eva_order_cnt ,

	--11、配送单量 deliver_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and delievery_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and delievery_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and delievery_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and delievery_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and delievery_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and delievery_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and delievery_id is not null, order_id,null))
			else null end  as deliver_order_cnt ,

	--12、退款单量 refund_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and refund_id is not null, order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and refund_id is not null, order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and refund_id is not null, order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and refund_id is not null, order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and refund_id is not null, order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and refund_id is not null, order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and refund_id is not null, order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and refund_id is not null, order_id,null))
			else null end  as refund_order_cnt ,

	-- 13、小程序订单量 miniapp_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'miniapp', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'miniapp', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'miniapp', order_id,null))
			else null end  as miniapp_order_cnt ,

	-- 14、android订单量 android_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'android', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'android', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'android', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'android', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'android', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'android', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'android', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'android', order_id,null))
			else null end  as android_order_cnt ,

	-- 15、ios订单量 ios_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'ios', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'ios', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'ios', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'ios', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'ios', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'ios', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'ios', order_id,null))
			else null end  as ios_order_cnt ,

	--16、pcweb订单量 pcweb_order_cnt
		case when grouping(store_id,store_name) =0
			then count(if(order_rn=1 and store_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (trade_area_id ,trade_area_name) = 0
			then count(if(order_rn=1 and trade_area_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (city_id,city_name) = 0
			then count(if(order_rn=1 and city_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (brand_id,brand_name) = 0
			then count(if(brand_rn=1 and brand_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (min_class_id,min_class_name) = 0
			then count(if(minclass_rn=1 and min_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (mid_class_id,mid_class_name) = 0
			then count(if(midclass_rn=1 and mid_class_id is not null  and order_from = 'pcweb', order_id,null))
			when grouping (max_class_id,max_class_name) = 0
			then count(if(maxclass_rn=1 and max_class_id is not null and order_from = 'pcweb', order_id,null))
			when grouping (create_date) = 0
			then count(if(order_rn=1 and order_from = 'pcweb', order_id,null))
			else null end  as pcweb_order_cnt ,

        create_date as dt  --日期

from temp
group by
    grouping sets(
        create_date, --日期
        (create_date,city_id,city_name),--日期+城市
        (create_date,city_id,city_name,trade_area_id,trade_area_name),--日期+城市+商圈
        (create_date,city_id,city_name,trade_area_id,trade_area_name,store_id,store_name), --日期+城市+商圈+店铺
        (create_date,brand_id,brand_name),--日期+品牌
        (create_date,max_class_id,max_class_name),--日期+大类
        (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name),--日期+大类+中类
        (create_date,max_class_id,max_class_name,mid_class_id,mid_class_name,min_class_id,min_class_name)--日期+大类+中类+小类
    );
```

==开发思路步骤==

**![image-20221004192203404](E:\黑马培训\Hadoop生态圈\assets\image-20221004192203404.png)**

==代码==

根据维度与指标精准的抽取字段，完成一个需求的抽取，就叉掉一个

![image-20221004195157228](E:\黑马培训\Hadoop生态圈\assets\image-20221004195157228.png)



# 2. 商品主题日统计宽表

## 1. 目标与需求

- 主题需求

  - 指标

    ```properties
    下单次数、下单件数、下单金额、被支付次数、被支付件数、被支付金额、被退款次数、被退款件数、被退款金额、被加入购物车次数、被加入购物车件数、被收藏次数、好评数、中评数、差评数
    
    --总共15个指标
    ```

  - 维度

  - -- 这里涉及的维度有订单内部细节，所以要用商品的总价来计算

    ```properties
    日期（day）+商品
    ```

- 本主题建表操作

  > 注意：建表操作需要在hive中执行，presto不支持hive的建表语法。

  ```sql
  create table yp_dws.dws_sku_daycount 
  (
      dt STRING,
      sku_id string comment 'sku_id',
      sku_name string comment '商品名称',
      order_count bigint comment '被下单次数',
      order_num bigint comment '被下单件数',
      order_amount decimal(38,2) comment '被下单金额',
      payment_count bigint  comment '被支付次数',
      payment_num bigint comment '被支付件数',
      payment_amount decimal(38,2) comment '被支付金额',
      refund_count bigint  comment '被退款次数',
      refund_num bigint comment '被退款件数',
      refund_amount  decimal(38,2) comment '被退款金额',
      cart_count bigint comment '被加入购物车次数',
      cart_num bigint comment '被加入购物车件数',
      favor_count bigint comment '被收藏次数',
      evaluation_good_count bigint comment '好评数',
      evaluation_mid_count bigint comment '中评数',
      evaluation_bad_count bigint comment '差评数'
  ) COMMENT '每日商品行为'
  --PARTITIONED BY(dt STRING)
  ROW format delimited fields terminated BY '\t'
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  
  ```

- 扩展知识：如何优雅的给变量起名字？

  > https://www.zhihu.com/question/21440067/answer/1277465532
  >
  > https://unbug.github.io/codelf/
  >
  > https://translate.google.cn/  

指标的解释：下单次数时跟这个商品出现在几个订单有关，下单件数跟这个商品的num有关

![image-20221005183355950](E:\黑马培训\Hadoop生态圈\assets\image-20221005183355950.png)



## 2. 思路分析

- 确定指标字段与表的关系

  > 当需求提出指标和维度之后，我们需要做的就是确定通过哪些表能够提供支撑。
  >
  > 思考：是不是意味着==数仓分层之后，上一层的表只能查询下一层的，能不能跨层？==
  >
  > 答案是不一定的。
  >
  > 看公司的规章制度，如果不允许，如何操作？
  >
  > dwd--->dwb--->dws 

```shell
#下单次数、下单件数、下单金额
dwb_order_detail
 	order_id: 下单次数相当于下了多少订单（有多少个包含这个商品的订单）
 	buy_num : 下单件数相当于下了多少商品 
 	total_price  每个商品下单金额指的是订单金额还是商品金额？应该是商品金额(订单中可能会包含其他商品)

#被支付次数、被支付件数、被支付金额
dwb_order_detail
	#支付状态的判断
		order_state: 只要不是1和7  就是已经支付状态的订单
		is_pay: 这个字段也可以 0表示未支付，1表示已支付。#推荐使用这个字段来判断
	#次数 件数 金额
    	order_id
    	buy_num
    	total_price

#被退款次数、被退款件数、被退款金额
dwb_order_detail
	#退款的判断
		refund_id: 退款单号 is not null的就表明有退款
	#次数 件数 金额	
		order_id
    	buy_num
    	total_price

#被加入购物车次数、被加入购物车件数
yp_dwd.fact_shop_cart(能够提供购物车相关信息的只有这张表)
	id: 次数
	buy_num: 件数

#被收藏次数
yp_dwd.fact_goods_collect
	id: 次数

#好评数、中评数、差评数
yp_dwd.fact_goods_evaluation_detail
	geval_scores_goods:商品评分0-10分
	
	#如何判断 好  中  差 （完全业务指定）
		得分: >= 9 	 好
		得分: >6  <9   中
		得分：<= 6  	差

#维度
	时间、商品
```

> 概况起来，计算商品主题宽表，需要参与计算的表有：
>
> ==yp_dwb.dwb_order_detail  订单明细宽表==
>
> ==yp_dwd.fact_shop_cart  购物车表==
>
> ==yp_dwd.fact_goods_collect  商品收藏表==
>
> ==yp_dwd.fact_goods_evaluation_detail  商品评价表==

- 针对本需求的计算，思考能否使用grouping sets计算？？

  > 1、查询的结果是来自同一张表 或者是同一份数据；
  >
  > 2、分组维度组合至少有两个及以上。

  ```sql
  --当只有一个维度组合的  完全没必要为了增强聚合而增强  避免生搬硬套
  select * fom  table
  group by
  grouping sets((dt,goods_id));
  
  select * fom  table
  group by dt,goods_id;
  ```

  

**备注** 之前我们的数据是来源于，三张宽表left join 成一张表  这样一来，就满足了查询的结果是来源于一张表，我

们有一个中心订单表为准，来关联其他表

但是现在4张表我们不知道能以谁为主，举例：我们今天下单了一件商品，从这里就能看出，我们把这个商品加购

物车了，收藏了，评价了吗？显然不能，从这里我们也能看出，这四张表没有明显的关系关联，可以说是毫无关系

这样一来我们就不能采取先把4张表，关联成一张表再进行计算。

==换种思路==

我们先计算出每张表能计算出来的指标，然后将结果进行组合

使用union进行合并的时候 因为指标都不一样，所以要进行补字段，然后将补的字段设置为null值

![4、商品主题的--sql技术实现方案](E:\黑马培训\Hadoop生态圈\assets\4、商品主题的--sql技术实现方案.png)

## 3. 代码实现

==代码==

```sql
--商品主题的日统计

--step1: 需求分析  理清主题需求中的维度 、指标的业务含义。

--step2: 建模主题表
    --表名  dws_xxxxx主题_daycount
    --字段包含：维度 维度分组标记 指标

--step3: 梳理表关系 抽取字段来支撑本主题的计算
    --哪些表可以来支撑我们这个主题的计算，哪些字段可以支持指标和维度。
    --优先到前一层dwb层去梳理·

--指标：
    -- 下单次数、下单件数、下单金额is_delivery
        --表：dwb_order_detail
            --字段：
                -- order_id  下单次数
                -- buy_num   下单件数
                -- total_price 下单金额

    -- 被支付次数、被支付件数、被支付金额
        --表：dwb_order_detail
            --todo 支付状态该如何判断呢？
                --order_state  除了1和7 剩下的都是已经支付的
                --is_pay  1表示已经支付
            --字段：
                -- order_id  被支付次数
                -- buy_num   被支付件数
                -- total_price 被支付金额


    -- 被退款次数、被退款件数、被退款金额
        --表：dwb_order_detail
            --todo 退款状态该如何判断呢？
                --refund_id    不为空表示有退款行为产生
                --refund_state  为5表示退款完成
            --字段：
                -- order_id  被退款次数
                -- buy_num   被退款件数
                -- total_price 被退款金额

    -- 被加入购物车次数、被加入购物车件数
        --表：dwd.fact_shop_cart
            --字段：id次数    buy_num件数
            -- create_time添加购物车的实际  goods_id

    -- 被收藏次数
        --表：dwd.fact_goods_collect
            --字段  id次数

    -- 好评数、中评数、差评数
        --表：fact_goods_evaluation_detail
            --字段：geval_scores_goods 商品评分
            --todo 至于何谓好中差 需要需求指定？
            --比如： >=9 好评  >=6 <9中评   <6 差评
-- 如果允许跨层抽取数据，那么可以直接抽取，如果不允许则把dwd层我们需要的数据提一层到dwb层里，然后再进行计算
--维度：
    --日期（day）+商品
------------------------------------------
--step4  由于下单 支付 退款指标来自于同一张表的数据 订单宽表 而这个表有很多
       --先使用列裁剪抽取核心字段出来  使用cte引导临时结果集 便于后续查询使用
       
-- 多个cte表达式之间用逗号隔开，不加with
with order_tmp as (select
    dt,
    goods_id,
    goods_name,
    order_id,
    buy_num,
    total_price,
    is_pay,
    refund_id,
--     refund_state  --这里由于测试数据原因 就不对退款状态进行判断了 直接根据id是否为空判断
    row_number() over(partition by order_id,goods_id) as rn
from yp_dwb.dwb_order_detail),
--第一组指标：下单次数 下单件数 下单金额
order_count_tmp as (select
       dt,goods_id,goods_name,
       -- 同一个订单下商品不会出现一样的，如果出现也只是加数量，不会加订单号
       -- 所以不需要去重
       count(order_id)  as order_count,
       sum(buy_num) as order_num,
       sum(total_price) as order_amount
from order_tmp
where rn =1 --去重操作
group by dt,goods_id,goods_name),

--第二组指标：被支付次数 被支付件数 被支付金额
payment_count_tmp as (select
       dt,goods_id,goods_name,
       count(order_id)  as payment_count,
       sum(buy_num) as payment_num, -- 后面的别名要与我们新建表的字段名一致
       sum(total_price) as payment_amount
from order_tmp
where rn =1 and is_pay =1 --去重操作 支付判断操作
group by dt,goods_id,goods_name),

--第三组指标：退款次数 退款件数 退款金额
refund_count_tmp as (select
       dt,goods_id,goods_name,
       count(order_id)  as refund_count,
       sum(buy_num) as refund_num,
       sum(total_price) as refund_amount
from order_tmp
where rn =1 and refund_id is not null  --退款状态判断 不是null时就发生了退款，这样才会有退款的数据给我们统计
group by dt,goods_id,goods_name),

--  上面的三组指标都是从dwb层里抽取的数据，那里的拉链已经结束了
-- 所以不需要加数据的有效时间作为筛选条件
--第四组指标：被加入购物车次数、被加入购物车件数
cart_count_tmp as (select
    substring(create_time,1,10) as dt,
    goods_id,
    count(id) as cart_count,
    sum(buy_num) as cart_num
from yp_dwd.fact_shop_cart
where end_date ='9999-99-99'
group by substring(create_time,1,10),goods_id),


--第五组指标：被收藏次数
favor_count_tmp as (select
    substring(create_time,1,10) as dt,
    goods_id,
    count(id) as favor_count
from yp_dwd.fact_goods_collect
where end_date ='9999-99-99'
group by substring(create_time,1,10),goods_id),

--第六组指标：好评、中评、差评
evaluation_count_tmp as (select
    substring(create_time,1,10) as dt,
    goods_id,
    --好评
    sum(if(geval_scores_goods >=9,1,0)) as evaluation_good_count,
    --中评
    sum(if(geval_scores_goods >=6 and geval_scores_goods<9,1,0)) as evaluation_mid_count,
    --差评
    sum(if(geval_scores_goods <6,1,0)) as evaluation_bad_count
from yp_dwd.fact_goods_evaluation_detail
group by substring(create_time,1,10),goods_id)


```

![image-20221005230214473](E:\黑马培训\Hadoop生态圈\assets\image-20221005230214473.png)

**画图理解**

![6、商品主题--计算好中评思路](E:\黑马培训\Hadoop生态圈\assets\6、商品主题--计算好中评思路.png)

 ==完整代码流程==

在后续抽取字段加入表中的时候，要把插入数据的目标表的建表语句放在一旁，时刻参考



```sql
-- -------------------------------------- 创建商品日统计表 ------------------------------------------
CREATE TABLE IF NOT EXISTS yp_dws.dws_sku_daycount(

      -- 维度
      dt STRING,
      sku_id STRING COMMENT '商品id',
      sku_name STRING COMMENT '商品名称', -- 商品

      -- 因为涉及到的维度不超过两个，所以不需要分组标记
      -- 指标
      order_count BIGINT COMMENT '下单次数',
      order_num BIGINT COMMENT '下单件数',
      order_amt DECIMAL(38,2) COMMENT '下单金额',
      pay_count BIGINT COMMENT '被支付次数',
      pay_num BIGINT COMMENT '被支付件数',
      pay_amt DECIMAL(38,2) COMMENT '支付金额',
      refund_count BIGINT COMMENT '被退款次数',
      refund_num BIGINT COMMENT '被退款件数',
      refund_amt DECIMAL(38,2) COMMENT '被退款金额',
      shop_count BIGINT COMMENT '被加入购物车次数',
      shop_num BIGINT COMMENT '被加入购物车件数',
      collect_count BIGINT COMMENT '被收藏次数',
      good_eva_count BIGINT COMMENT '好评数',
      mid_eva_count BIGINT COMMENT '中评数',
      bad_eva_count BIGINT COMMENT '差评数'

)
row format delimited fields terminated by '\t'
stored as ORC tblproperties ('orc.compress'='SNAPPY');

---------------------------------------- 根据指标梳理表关系 -----------------------------------------
-- 因为我们是在dws层进行具体操作 所以我们挑选数据源优先从dwb层寻找数据
-- 梳理表关系寻找数据来源，把每组相关的指标都列出来分析
-- todo #下单次数 #下单件数 #下单金额
-- 与订单相关那么我们就去dwb层的订单明细宽表中寻找数据
-- 数据来源: yp_dwb.dwb_order_detail
-- 抽取相关字段
-- order_id (下了多少订单，就会产生多少不同的订单id，这也就是下单次数)
-- buy_num (下单件数相当于下了多少商品)
-- total_price (下单金额就是这个订单的总价 因为一个订单总包含这不同的商品，所以这里使用到了商品总价)
-- 后续的指标计算涉及到了订单内部的计算，所以用到的是商品总价来计算下单金额
-- 最后使用cte表达式临时存储抽取的数据


-- todo #被支付次数 #被支付件数 # 支付金额
-- 数据来源表 yp_dwb.dwb_order_detail
-- 抽取的字段
--   支付状态判断
--   order_state  这个字段的值只要不是1和7就是已经支付完的订单
--   is_pay  0代表未支付 1代表已经支付  # 推荐使用这个字段进行判断订单的
-- 根据支付状态 筛选数据 筛选出来的数据都是is_pay=1 的数据 所以说order_id就是被支付的次数 buy_num就是被支付的件数
-- total_price商品的总价来计算支付的金额


-- todo #被退款次数 #被退款件数 #被退款的金额
-- 数据来源表 yp_dwb.dwb_order_detail
-- 退款的订单判断 refund_state 只要这个字段不为null则就发生了退款
-- 抽取的字段
-- #  次数 件数 金额
-- 		order_id
--     	buy_num
--     	total_price


-- todo 被加入购物车次数  被加入购物车件数
-- 数据来源表 yp_dwd.fact_shop_cart （能提供购物车相关信息的表就只有这一张）
-- 实际工作中如果公司允许跨层抽取数据，那么这样就可行，如果不允许那么只能把数据向上提一层或者多层
-- 让数据离我们做分析的层近
-- 抽取的字段
-- id 加入一次购物车就会新生成一个关于这个商品的购物车id
-- buy_num 购买商品的数量就等价于被加入购物车的件数


-- todo 被收藏的次数
-- 数据来源 yp_dwd.fact_goods_collect
-- id 每收藏一个id就会加一
-- 也不涉及去重的操作就直接取数据做指标的计算即可


-- todo #好评数、中评数、差评数
-- 数据来源表 yp_dwd.fact_goods_evaluation_detail
-- 涉及字段 geval_scores_goods 综合评分0-10
-- 我们根据给的需求 来基于这个字段进行if的判断给





---------------------------------------- cte创建临时结果集 -----------------------------------------------
-- 在多个cte表达式一起写的时候，表的别名不能起相同的别名
insert into hive.yp_dws.dws_sku_daycount
WITH order_base AS (
     -- 下单的数据不管你有没有支付，只要有下单都算，所以说这里不用再加上支付状态，来筛选数据，只要是有下单就都拿来统计
     SELECT
           dt,
           order_id, --订单id
           goods_id, --商品id
           goods_name, --商品名称
           buy_num, --购买商品的件数
           total_price, --商品的总价
           is_pay, --支付状态（1代表已经支付）
           -- 给临时结果集中的数据进行分区排序 如果后续需要去重可以根据这个序号进行去重
           row_number() over (partition by order_id,goods_id) AS rk
     FROM yp_dwb.dwb_order_detail

 )
 -- 订单指标计算
,order_count AS(

       SELECT
            dt,
            goods_id AS sku_id, --商品id
            goods_name AS sku_name, --商品名称
            COUNT(order_id) AS order_count,
            SUM(buy_num) AS order_num,
            SUM(total_price) AS order_amt
       FROM order_base WHERE rk=1
       GROUP BY dt,goods_id,goods_name

)
-- todo 订单指标与支付指标虽然说它抽取的字段相同，但是本质上不同的地方是关于支付指标计算的数据要加上订单的支付状态的筛选
--  订单指标只要有下订单的是数据都拿来计算，而支付指标要在下订单的数据上做一个筛选，只要已经支付过的订单数据，不要未支付过的订单数据


 -- 因为涉及到支付指标的计算，所以我们需要再加上一个临时结果集，来筛选已经支付的订单，来支持支付指标的计算
 -- 只有支付了订单，才有支付相关的数据，否则一切免谈
,pay_base AS(

       -- 筛选出已经支付的订单，并且进行分区，重复数据会被分在一组，并进行排序
       -- 去重时只要选择序号为1 的数据就能完成去重操作
       SELECT
            *,
            row_number() over (partition by order_id,goods_id) AS rk
       FROM yp_dwb.dwb_order_detail
       WHERE is_pay=1

)
-- 支付指标计算
,payment_count AS (

          SELECT
                dt,goods_id AS sku_id,goods_name AS sku_name,
                COUNT(order_id) AS pay_count,
                SUM(buy_num) AS pay_num,
                SUM(total_price) AS pay_amt
          FROM pay_base
          WHERE rk=1
          GROUP BY dt,goods_id,goods_name

 )
,refund_base AS (

     SELECT
          *,
          row_number() over (partition by order_id,goods_id) AS rk
     FROM yp_dwb.dwb_order_detail
     WHERE refund_state IS NOT NULL

 )
 -- 退款指标计算
 ,refund_count AS (

      SELECT dt,goods_id AS sku_id,goods_name AS sku_name,
             COUNT(order_id) AS refund_count,
             SUM(buy_num) AS refund_num,
             SUM(total_price) AS refund_amt
      FROM refund_base
      WHERE rk=1
      GROUP BY dt,goods_id,goods_name



 )
 -- 加购物车次数，件数
 -- 因为不涉及去重操作，所以不用再建立一个临时表来进行分区排序，为以后的去重提供条件
 -- 所以现在就只要去数据源表抽取我们所需要的数据即可
 -- 为什么不涉及去重操作？
 -- 因为同一商品可以被加入购物车多次，没有说只能加一次的说法，这样就不存在重复的数据的说法
 ,cart_count AS (

     SELECT
          substring(c.create_time,1,10) AS dt,
          c.goods_id AS sku_id,
          count(c.id) AS shop_count,
          sum(c.buy_num) AS shop_num
     FROM yp_dwd.fact_shop_cart c
     -- 因为这个数据来源表是拉链表，所以我们不能提取拉链表中无效过期的数据
     -- 提取的是标记时间为"9999-99-99"的数据
     WHERE end_date='9999-99-99'
     -- 进行分组聚合统计
     -- 同一天下同一商品被加购物车的次数，和买的件数
     GROUP BY substring(c.create_time,1,10), c.goods_id

  )
-- 收藏指标
,favor_count AS (

    SELECT
          substring(create_time,1,10) AS dt,
          goods_id AS sku_id,
          count(id) AS collect_count
    FROM yp_dwd.fact_goods_collect 
    WHERE end_date='9999-99-99'
    GROUP BY substring(create_time,1,10), goods_id

 )
 -- 好中差评的统计
 ,evaluation_count AS (

    SELECT
          substring(e.create_time,1,10) AS dt, e.goods_id AS sku_id,
          COUNT(if(geval_scores_goods >= 9,1,null)) AS good_eva_count,
          COUNT(if(geval_scores_goods > 6 AND geval_scores_goods < 9,1,null)) AS mid_eva_count,
          COUNT(if(geval_scores_goods > 0 AND geval_scores_goods <= 6,1,null)) AS bad_eva_count
    FROM yp_dwd.fact_goods_evaluation_detail e
    GROUP BY substring(e.create_time,1,10), e.goods_id



  )

  -- 以上我们计算完指标以后
  -- 接着 todo 合并这些指标的结果集

,unionall as (
    select
        dt, sku_id, sku_name,
        order_count,
        order_num,
        order_amt,
        0 as pay_count,
        0 as pay_num,
        0 as pay_amt,
        0 as refund_count,
        0 as refund_num,
        0 as refund_amt,
        0 as shop_count,
        0 as shop_num,
        0 as collect_count,
        0 as good_eva_count,
        0 as mid_eva_count,
        0 as bad_eva_count
    from order_count
    union all
    select
        dt, sku_id, sku_name,
        0 as order_count,
        0 as order_num,
        0 as order_amt,
        pay_count,
        pay_num,
        pay_amt,
        0 as refund_count,
        0 as refund_num,
        0 as refund_amt,
        0 as shop_count,
        0 as shop_num,
        0 as collect_count,
        0 as good_eva_count,
        0 as mid_eva_count,
        0 as bad_eva_count
    from payment_count
    union all
    select
        dt, sku_id, sku_name,
        0 as order_count,
        0 as order_num,
        0 as order_amt,
        0 as pay_count,
        0 as pay_num,
        0 as pay_amt,
        refund_count,
        refund_num,
        refund_amt,
        0 as shop_count,
        0 as shop_num,
        0 as collect_count,
        0 as good_eva_count,
        0 as mid_eva_count,
        0 as bad_eva_count
    from refund_count
    union all
    select
         dt, sku_id, null as sku_name,
        0 as order_count,
        0 as order_num,
        0 as order_amt,
        0 as pay_count,
        0 as pay_num,
        0 as pay_amt,
        0 as refund_count,
        0 as refund_num,
        0 as refund_amt,
        shop_count,
        shop_num,
        0 as collect_count,
        0 as good_eva_count,
        0 as mid_eva_count,
        0 as bad_eva_count
    from cart_count
    union all
    select
        dt, sku_id, null as sku_name,
        0 as order_count,
        0 as order_num,
        0 as order_amt,
        0 as pay_count,
        0 as pay_num,
        0 as pay_amt,
        0 as refund_count,
        0 as refund_num,
        0 as refund_amt,
        0 as shop_count,
        0 as shop_num,
        collect_count,
        0 as good_eva_count,
        0 as mid_eva_count,
        0 as bad_eva_count
    from favor_count
    union all
    select
         dt, sku_id, null as sku_name,
        0 as order_count,
        0 as order_num,
        0 as order_amt,
        0 as pay_count,
        0 as pay_num,
        0 as pay_amt,
        0 as refund_count,
        0 as refund_num,
        0 as refund_amt,
        0 as shop_count,
        0 as shop_num,
        0 as collect_count,
        good_eva_count,
        mid_eva_count,
        bad_eva_count
    from evaluation_count
)
-- 因为每个指标都是单独计算，然后union把六组指标整个在一起的，然后就会发现当一个订单在计算第一组指标时，其他五组指标的字段为null，在计算第二组指标时，其他组的指标为null，依此类推，正常来说同一订单的指标，应该是在一行上显示，但是这里却错位成多行，所以我们要把属于一个订单的所有指标都整合到一行上

-- 把指标整合到一行上显示,原理如下图所示
select
    dt, sku_id, max(sku_name),
    sum(order_count),
    sum(order_num),
    sum(order_amt),
    sum(pay_count),
    sum(pay_num),
    sum(pay_amt),
    sum(refund_count),
    sum(refund_num),
    sum(refund_amt),
    sum(shop_count),
    sum(shop_num),
    sum(collect_count),
    sum(good_eva_count),
    sum(mid_eva_count),
    sum(bad_eva_count)
from unionall
group by dt, sku_id
;
```

![2、商品主题指标使用union all合并之后再次分组聚合](E:\黑马培训\Hadoop生态圈\assets\2、商品主题指标使用union all合并之后再次分组聚合.png)

# 3. 用户主题日统计宽表

## 1. 目标与需求

- 主题需求

  - 指标

    ```properties
    登录次数、收藏店铺数、收藏商品数、加入购物车次数、加入购物车金额、下单次数、下单金额、支付次数、支付金额
    ```

  - 维度

    ```properties
    用户、日期
    ```

- 建表操作

  > 注意：建表操作需要在hive中执行，presto不支持hive的建表语法。

  ```sql
  create table yp_dws.dws_user_daycount
  (
      dt STRING,
      user_id string comment '用户 id',
      login_count bigint comment '登录次数',
      store_collect_count bigint comment '店铺收藏数量',
      goods_collect_count bigint comment '商品收藏数量',
      cart_count bigint comment '加入购物车次数',
      cart_amount decimal(38,2) comment '加入购物车金额',
      order_count bigint comment '下单次数',
      order_amount    decimal(38,2)  comment '下单金额',
      payment_count   bigint      comment '支付次数',
      payment_amount  decimal(38,2) comment '支付金额'
  ) COMMENT '每日用户行为'
  --PARTITIONED BY(dt STRING)
  ROW format delimited fields terminated BY '\t'
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  ```

- 确定字段与表关系

  ```shell
  #登录次数
  yp_dwd.fact_user_login
  	id
  	login_user
  
  #收藏店铺数、收藏商品数
  yp_dwd.fact_store_collect
  	id
  	user_id
  	
  #加入购物车次数、加入购物车金额
  yp_dwd.fact_shop_cart
  yp_dwb.dwb_goods_detail
  	#因为购物车中没有金额，因此需要和商品详情表进行关联
  	goods_promotion_price: 商品促销价格(售价)
  
  #下单次数、下单金额
  yp_dwd.fact_shop_order
  yp_dwd.fact_shop_order_address_detail
  	#通过订单主副表可以提供
  
  #支付次数、支付金额
  yp_dwd.fact_trade_record
  ```

- 最终实现合并的方式

  - 使用union all合并
  - 使用full join合并

## 2. 代码示例

```sql
---------------------------- 依照需求建立用户主题日统计宽表 ------------------------------
---
-- STEP1: 建表 ---
CREATE TABLE IF NOT EXISTS yp_dws.dws_user_daycount(
           -- 维度
           dt STRING COMMENT '日期',
           user_id STRING COMMENT '用户',

           -- 分组标记，涉及的维度分组情况过少，不需要分组标记
           -- 指标
           login_count BIGINT COMMENT '登录次数',
           store_collection_num BIGINT COMMENT '收藏店铺数',
           goods_collection_num BIGINT COMMENT '收藏商品数',
           cart_count BIGINT COMMENT '被加入购物车次数',
           cart_amt DECIMAL(28,2) COMMENT '加入购物车的金额',
           order_id STRING COMMENT '下单次数',
           order_amt DECIMAL(28,2) COMMENT '下单金额',
           payment_count BIGINT COMMENT '支付次数',
           payment_amt DECIMAL(28,2) COMMENT '支付金额'

)COMMENT '用户行为表'
row format delimited fields terminated by '\t'
stored as ORC tblproperties ('orc.compress'='SNAPPY');

-- STEP2: 表关系梳理，找出数据来源表 ------------------------------------------
-- todo  下单次数 下单金额 与订单相关的字段，去dwb层订单宽表中找
-- 数据源表 yp_dwb.dwb_order_detail
-- order_id 每下一次订单生产一个订单号
-- 分组的维度不涉及订单内部信息，所以使用订单金额
-- order_amount 使用订单总价来计算订单金额

-- todo 登录次数
-- 数据来源表 yp
-- id
-- login_user

-- todo 收藏店铺数 收藏商品数
-- 数据来源 yp_dwd.fact_goods_collect
-- create_time
-- id
-- user_id


-- 订单指标计算
insert into yp_dws.dws_user_daycount
WITH order_count_amount AS (

     SELECT
         substring(create_time,1,10) AS dt,
         buyer_id AS user_id,
         count(order_id)as order_count,
         sum(order_amount)as order_amt
     FROM yp_dwb.dwb_order_detail
     GROUP BY buyer_id, substring(create_time,1,10)
     -- 同一天下相同用户下的订单金额的总和，因为一天内一个用户下的订单可能会有多个

 )
 -- 登录指标计算
 ,login_count AS (

    SELECT
         dt,
         login_user as user_id,
         count(id) as login_count
    FROM yp_dwd.fact_user_login
    GROUP BY login_user, dt
  )
-- 收藏店铺数
,collection_store_count AS (

        SELECT
              substring(create_time,1,10) AS dt,
              user_id,
              count(id) as store_collection_num
        FROM yp_dwd.fact_goods_collect c
        where end_date='9999-99-99'
        GROUP BY user_id, substring(create_time,1,10)

 )
 -- 收藏商品数
 ,goods_collect_count AS (

        SELECT
              substring(create_time,1,10) AS dt,
              user_id,
              count(id) as goods_collection_num
        FROM yp_dwd.fact_goods_collect
        where end_date='9999-99-99'
        GROUP BY user_id, substring(create_time,1,10)

 )
-- 加入购物车次数和金额
,cart_count_amount as (
   select
      count(cart.id) as cart_count,
      sum(g.goods_promotion_price) as cart_amt,
      buyer_id as user_id, substring(cart.create_time, 1, 10) as dt
   from yp_dwd.fact_shop_cart cart, yp_dwb.dwb_goods_detail g
   where cart.end_date='9999-99-99' and cart.goods_id=g.id
   group by buyer_id, substring(cart.create_time, 1, 10)

)-- 支付次数和金额
, payment_count_amount as (
   select
      count(id) as payment_count,
      sum(trade_true_amount) as payment_amt,
      user_id, substring(create_time, 1, 10) as dt
   from yp_dwd.fact_trade_record
   where is_valid=1 and trade_type in (1,11) and status=1
   group by user_id, substring(create_time, 1, 10)
)
select
        dt,
       user_id,
      -- 登录次数
       sum(login_count) login_count,
       -- 店铺收藏数
       sum(store_collection_num) store_collection_num,
       -- 商品收藏数
       sum(goods_collection_num) goods_collection_num,
       -- 加入购物车次数和金额
       sum(cart_count) cart_count,
       sum(cart_amt) cart_amt,
       -- 下单次数和金额
       sum(order_count) order_count,
       sum(order_amt) order_amt,
       -- 支付次数和金额
       sum(payment_count) payment_count,
       sum(payment_amt) payment_amt

from
(
    select lc.login_count,
           0 store_collection_num,
           0 goods_collection_num,
           0 cart_count, 0 cart_amt,
           0 order_count, 0 order_amt,
           0 payment_count, 0 payment_amt,
           user_id, dt
    from login_count lc
    union all
    select
           0 login_count,
           scc.store_collection_num,
           0 goods_collection_num,
           0 cart_count, 0 cart_amt,
           0 order_count, 0 order_amt,
           0 payment_count, 0 payment_amt,
           user_id, dt
    from collection_store_count scc
    union all
    select
           0 login_count,
           0 store_collection_num,
           gcc.goods_collection_num,
           0 cart_count, 0 cart_amt,
           0 order_count, 0 order_amt,
           0 payment_count, 0 payment_amt,
           user_id, dt
    from goods_collect_count gcc
    union all
    select
           0 login_count,
           0 store_collection_num,
           0 goods_collection_num,
           cca.cart_count, cart_amt,
           0 order_count, 0 order_amt,
           0 payment_count, 0 payment_amt,
           user_id, dt
    from cart_count_amount cca
    union all
    select
           0 login_count,
           0 store_collection_num,
           0 goods_collection_num,
           0 cart_count, 0 cart_amt,
           oca.order_count, order_amt,
           0 payment_count, 0 payment_amt,
           user_id, dt
    from order_count_amount oca
    union all
    select
           0 login_count,
           0 store_collection_num,
           0 goods_collection_num,
           0 cart_count, 0 cart_amt,
           0 order_count, 0 order_amt,
           pca.payment_count, payment_amt,
           user_id, dt
    from payment_count_amount pca
) user_count
group by user_id, dt
;

```

# 4. FULL JOIN实现的方式

## 1. 分析

要想使用全连接来连接两张表或者多张表，首先我们要确定表连接的字段，在这里表连接的字段根据分析主题的不

同，字段也会不同，像商品主题日统计表，我们分析的是商品，那么我们着重点就在于商品所在的指标，如果只根

据商品来进行关联的话，会出现一个问题，昨天的商品也会跟今天的商品关联在一起，因为表中有天的概念，所以

说我们要加上一个条件，把同一天 下的同一个商品关联在一起。

**图示**

![image-20221008152238232](E:\黑马培训\Hadoop生态圈\assets\image-20221008152238232.png)

![image-20221008152154613](E:\黑马培训\Hadoop生态圈\assets\image-20221008152154613.png)

![image-20221008152200526](E:\黑马培训\Hadoop生态圈\assets\image-20221008152200526.png)



==代码示例==

```sql
----注意，如果session无法选中presto数据源----------
----把sql文件语法方言支持调整为Generic sql模式----------

insert into hive.yp_dws.dws_sku_daycount
with order_cnt as (select
	dt ,pay_time ,apply_date ,goods_id , goods_name , order_state ,is_pay ,refund_id ,refund_state ,
	order_id ,buy_num ,total_price ,
	row_number() over(partition by order_id , goods_id) as rn1
from hive.yp_dwb.dwb_order_detail),

goods_order_1  as (select
	dt,
	goods_id,
	goods_name,
	count(order_id) as order_count,
	sum(buy_num) as order_num,
	sum(total_price) as order_amount
from order_cnt where rn1 = 1
group by goods_id,goods_name , dt) ,

goods_pay_2 as (select
	substring(pay_time,1,10) as dt,
	goods_id,
	goods_name,
	count(order_id) as payment_count,
	sum(buy_num) as payment_num,
	sum(total_price) as payment_amount
from order_cnt where rn1 = 1 and  order_state not in(1,7) and is_pay=1
group by goods_id,goods_name, substring(pay_time,1,10)),

goods_refund_3 as(select
	substring(apply_date,1,10) as dt,
	goods_id,
	goods_name,
	count(order_id) as refund_count,
	sum(buy_num) as refund_num,
	sum(total_price) as refund_amount
from order_cnt where rn1 = 1 and refund_id is not null and refund_state = 5
group by  goods_id , goods_name , substring(apply_date,1,10)),

goods_cart_4 as (select
	 substring(c.create_time ,1,10) as  dt,
	 c.goods_id  ,
	 d1.goods_name ,
	 count(c.id) as cart_count,
	 sum(c.buy_num) as cart_num
from hive.yp_dwd.fact_shop_cart c join hive.yp_dwb.dwb_goods_detail  d1
	on c.goods_id  = d1.id
group by c.goods_id  , d1.goods_name , substring(c.create_time ,1,10)),

goods_favor_5 as (select
	 substring(gc.create_time ,1,10) as dt,
	 gc.goods_id ,
	 d2.goods_name ,
	 count(gc.id) as favor_count
from hive.yp_dwd.fact_goods_collect gc join hive.yp_dwb.dwb_goods_detail  d2
	on gc.goods_id  = d2.id
group  by gc.goods_id , d2.goods_name , substring(gc.create_time ,1,10)) ,

goods_eval_6 as (select
	substring(g.create_time ,1,10)  as dt,
	g.goods_id,
	d3.goods_name,
	-- 低于 6分  差评 , 6~8分(包含)  8以上好评
	count(
		if( g.geval_scores_goods is null OR g.geval_scores_goods > 8 , g.id ,null )
		) as evaluation_good_count,
	count(
		if( g.geval_scores_goods is not null and g.geval_scores_goods between 6 and 8 , g.id ,null )
		) as evaluation_mid_count,
	count(
		if( g.geval_scores_goods is not null and g.geval_scores_goods < 6 , g.id ,null )
		) as evaluation_bad_count
from hive.yp_dwd.fact_goods_evaluation_detail  g join hive.yp_dwb.dwb_goods_detail  d3
	on  g.goods_id  = d3.id
group by  g.goods_id,d3.goods_name,substring(g.create_time ,1,10)),

temp as (select
	coalesce(goods_order_1.dt,goods_pay_2.dt,goods_refund_3.dt,goods_cart_4.dt,goods_favor_5.dt,goods_eval_6.dt) as dt,
	coalesce(goods_order_1.goods_id,goods_pay_2.goods_id,goods_refund_3.goods_id,goods_cart_4.goods_id,goods_favor_5.goods_id,goods_eval_6.goods_id) as sku_id,
	coalesce(goods_order_1.goods_name,goods_pay_2.goods_name,goods_refund_3.goods_name,goods_cart_4.goods_name,goods_favor_5.goods_name,goods_eval_6.goods_name) as sku_name,

	coalesce(goods_order_1.order_count,0) as order_count,
	coalesce(goods_order_1.order_num,0) as order_num,
	coalesce(goods_order_1.order_amount,0) as order_amount,

	coalesce(goods_pay_2.payment_count,0) as payment_count,
	coalesce(goods_pay_2.payment_num,0) as payment_num,
	coalesce(goods_pay_2.payment_amount,0) as payment_amount,

	coalesce(goods_refund_3.refund_count,0) as refund_count,
	coalesce(goods_refund_3.refund_num,0) as refund_num,
	coalesce(goods_refund_3.refund_amount,0) as refund_amount,

	coalesce(goods_cart_4.cart_count,0) as cart_count,
	coalesce(goods_cart_4.cart_num,0) as cart_num,

	coalesce(goods_favor_5.favor_count,0) as favor_count,

	coalesce(goods_eval_6.evaluation_good_count,0) as evaluation_good_count,
	coalesce(goods_eval_6.evaluation_mid_count,0) as evaluation_mid_count,
	coalesce(goods_eval_6.evaluation_bad_count,0) as evaluation_bad_count
from  goods_order_1
         -- 连接的条件需要同一天的同一件sahng'p
	full join goods_pay_2 on goods_order_1.goods_id=goods_pay_2.goods_id and goods_order_1.dt = goods_pay_2.dt
	full join goods_refund_3 on goods_order_1.goods_id = goods_refund_3.goods_id and goods_order_1.dt = goods_refund_3.dt
	full join goods_cart_4 on goods_order_1.goods_id = goods_cart_4.goods_id and goods_order_1.dt = goods_cart_4.dt
	full join goods_favor_5 on goods_order_1.goods_id = goods_favor_5.goods_id and goods_order_1.dt = goods_favor_5.dt
	full join goods_eval_6 on goods_order_1.goods_id = goods_eval_6.goods_id and goods_order_1.dt = goods_eval_6.dt)

select
	dt,
	sku_id, sku_name,
	sum(order_count),
	sum(order_num),
	sum(order_amount),
	sum(payment_count),
	sum(payment_num),
	sum(payment_amount),
	sum(refund_count),
	sum(refund_num),
	sum(refund_amount),
	sum(cart_count),
	sum(cart_num),
	sum(favor_count),
	sum(evaluation_good_count),
	sum(evaluation_mid_count),
	sum(evaluation_bad_count)
from  temp
group by sku_id, sku_name,dt;

```

