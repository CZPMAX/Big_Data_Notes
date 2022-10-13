# RPT报表应用层

> 主要去裁剪拼接数据，让表的数据结构与报表系统表结构一致

## 1. 目标与需求

- 新零售数仓分层图

  ![image-20211017141939771](E:\黑马培训\Hadoop生态圈\assets\image-20211017141939771.png)

- RPT

  - 名称：数据报表层（Report），其实就是我们所讲的数据应用层DA、APP。

  - 功能：==根据报表、专题分析的需求而计算生成的个性化数据==。表结构与报表系统保持一致。

  - 解释

    > 这一层存在的意义在于，如果报表系统需要一些及时高效的展示分析。我们可以在RPT层根据其需求提前把相关的字段、计算、统计做好，支撑报表系统的高效、便捷使用。

- 栗子

  - 比如报表需要展示：门店销售量Top10，展示如下效果

    ![image-20211207134550065](E:\黑马培训\Hadoop生态圈\assets\image-20211207134550065.png)

  - 对于其依赖的数据有两种实现方式

    - 方式1：使用专业的BI报表软件直接读取数据仓库或数据集市的数据，然后自己根据需要展示的效果进行数据抽、转换、拼接动作，
    - 方式2：==大数据开发工程师针对前端Top10展示的需求，提前把数据拼接好，返回前端直接页面渲染展示。==

    ![image-20211207134945383](E:\黑马培训\Hadoop生态圈\assets\image-20211207134945383.png)

- 使用DataGrip在Hive中创建RPT层

  > 注意，==**对于建库建表操作，需直接使用Hive**==，因为Presto只是一个数据分析的引擎，其语法不一定支持直接在Hive中建库建表。

  ```sql
  create database if not exists yp_rpt;
  ```

----

## 2. 销售主题报表

- 需求一：门店月销售单量排行

  > 按月统计，==各个门店==的==月销售单量==。

  - 建表

  - 建表在hive中执行
  
    ```sql
    CREATE TABLE yp_rpt.rpt_sale_store_cnt_month(
        -- 标记哪一天干活的
       date_time string COMMENT '统计日期,不能用来分组统计',
        -- 标记哪个月份
       year_code string COMMENT '年code',
        
        -- 起作用的字段下面的这个year_month
       year_month string COMMENT '年月',
       
       -- 起作用的字段下面的这个city_id
       city_id string COMMENT '城市id',
        
       city_name string COMMENT '城市name',
       trade_area_id string COMMENT '商圈id',
       trade_area_name string COMMENT '商圈名称',
       store_id string COMMENT '店铺的id',
       store_name string COMMENT '店铺名称',
       
       -- 起作用的字段下面的这个order_store_cnt 
       order_store_cnt BIGINT COMMENT '店铺成交单量',
       
       -- 其他字段是为了让这个报表更加灵活并且便于理解才加的
        
       miniapp_order_store_cnt BIGINT COMMENT '小程序端店铺成交单量',
       android_order_store_cnt BIGINT COMMENT '安卓端店铺成交单量',
       ios_order_store_cnt BIGINT COMMENT 'ios端店铺成交单量',
       pcweb_order_store_cnt BIGINT COMMENT 'pc页面端店铺成交单量'
    )
    COMMENT '门店月销售单量排行' 
    ROW format delimited fields terminated BY '\t' 
    stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
    ```
  
  - 实现
  
    > 从销售主题统计宽表中，找出分组为store，并且时间粒度为month的进行排序即可。
  
    ```sql
    --门店月销售单量排行
    insert into yp_rpt.rpt_sale_store_cnt_month
    select 
       date_time,
       year_code,
       year_month,
       city_id,
       city_name,
       trade_area_id,
       trade_area_name,
       store_id,
       store_name,
       order_cnt,
       miniapp_order_cnt,
       android_order_cnt,
       ios_order_cnt,
       pcweb_order_cnt
    from yp_dm.dm_sale 
    where time_type ='month' and group_type='store' and store_id is not null 
    order by order_cnt desc;
    ```


  ![image-20211204164443670](E:\黑马培训\Hadoop生态圈\assets\image-20211204164443670.png)

- 需求二：日销售曲线

  > 按==天==统计，==总销售金额==和==销售单量==。

  - 建表

    ```sql
    --日销售曲线
    DROP TABLE IF EXISTS yp_rpt.rpt_sale_day;
    CREATE TABLE yp_rpt.rpt_sale_day(
       date_time string COMMENT '统计日期,不能用来分组统计',
       year_code string COMMENT '年code',
       month_code string COMMENT '月份编码', 
       day_month_num string COMMENT '一月第几天', 
       dim_date_id string COMMENT '日期',
    
       sale_amt DECIMAL(38,2) COMMENT '销售收入',
       order_cnt BIGINT COMMENT '成交单量'
    )
    COMMENT '日销售曲线' 
    ROW format delimited fields terminated BY '\t' 
    stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
    ```

  - 实现

    ```sql
    --日销售曲线
    insert into yp_rpt.rpt_sale_day
    select 
       date_time,
       year_code,
       month_code,
       day_month_num,
       dim_date_id,
       sale_amt,
       order_cnt
    from yp_dm.dm_sale 
    where time_type ='date' and group_type='all'
    --按照日期排序显示曲线
    order by dim_date_id;
    ```

  ![image-20211207141004223](E:\黑马培训\Hadoop生态圈\assets\image-20211207141004223.png)

- 需求三：渠道销售占比

  > 比如每天不同渠道的==订单量占比==。
  >
  > 也可以延伸为每周、每月、每个城市、每个品牌等等等。

  - 求占比中的坑

  注意事项：涉及到除法操作 要考虑分母是否为0 null
  
  ==presto中小数类型的数据的处理==
  
  ```sql
  -- 小数是经常涉猎的数据类型 各种占比
  
  
  -- 探究1: 小数使用什么数据类型来修饰？ 有什么区别？ 用哪个类型好
  -- float double decimal
  -- todo 区别在于精度问题 是否会出现精度丢失
  -- 如果在银行工作，要求精度极准的场合 使用什么类型比较好?
  
  -- 下面这个5代表保留小数点后5位
  select round(1/3,5); -- 0.33333 看似精度已经保留
  -- 但是再给他进行乘法操作 会发现 todo 出现了精度丢失
  select round(1/3,5) * 1000; -- 按理说应当是333.33 但是实际输出却是333.33000000000004
  
  -- todo double数据精度保存不严谨 会出现丢失的情况
  -- todo 如果遇到精度要求极其严格的场合 强烈推荐使用decimal来修饰
  -- 步骤先使用round函数来保留几位小数 再使用cast函数把double类型的数据转换为decimal类型的数据
  -- todo decimal(x,y) 参数详解
  -- 第一个参数代表着这个数据一共最多能有多少位 总的长度
  -- 第二个参数代表着一个数据有多少位小数位 精度
  select cast(round(1 / 3, 5) as decimal(10, 5)) ;
  
  
  -- decimal中出现的频率最高的是 decimal(5,2) [0.00%  100.00%]
  
  
  -- 探究2: 两个int整型相除 结果是什么
  -- hive中 结果是double hive有默认的隐式转换
  -- 当两个int相除 结果转换成为double类型来保存
  select 1 / 3; -- 0.3333333333
  -- 如果隐式转换的精度不是我们需要的
  -- 那么我们可以利用round（）函数来自己确定数据的精度
  
  -- presto中 不支持类型的隐式转换 todo 要求参与计算的至少一方为double 结果才是double
  select 1 / 3; -- 计算出来的数据，数据类型还是int，因为是小数所以四舍五入了 变成了0
  
  -- 两种方式解决这一类问题
  -- 1. 使用cast函数将其中的至少一个类型转换为double再除
  
  select cast(1 as double) / 3;
  -- 或者
  select 1 / cast(3 as double);
  
  -- 2. 投机取巧 把一个数乘以一个double类型的数据
  -- todo 需要几位小数就乘以1.几位小数
  select 1 * 1.00 / 3; -- 0.33
  select 1 * 1.0000 / 3; -- 0.3333
  
  -- todo 实际中我们采取cast数据类型转换
  
  
  ```
  
  
  
  - 处理思路
  
    ```sql
    --在dm层的dm_sale表中
    	order_cnt 表示总订单量
    		miniapp_order_cnt 表示小程序订单量
    		android_order_cnt 安卓
    		ios_order_cnt ios订单量
    		pcweb_order_cnt  网站订单量
    --所谓的占比就是
    	每个占order_cnt总订单量的比例 也就是进行除法运算
    	
    --最后需要注意的是
    	上述这几个订单量的字段  存储类型是bigint类型。
    	如果想要得出90.25这样的占比率  需要使用cast函数将bigInt转换成为decimal类型。
    ```
  
  - 建表
  
    ```sql
    --渠道销量占比
    DROP TABLE IF EXISTS yp_rpt.rpt_sale_fromtype_ratio;
    CREATE TABLE yp_rpt.rpt_sale_fromtype_ratio(
       date_time string COMMENT '统计日期,不能用来分组统计',
       time_type string COMMENT '统计时间维度：year、month、day',
       year_code string COMMENT '年code',
       year_month string COMMENT '年月',
       dim_date_id string COMMENT '日期',
       
       order_cnt BIGINT COMMENT '成交单量',
       miniapp_order_cnt BIGINT COMMENT '小程序成交单量',
       miniapp_order_ratio DECIMAL(5,2) COMMENT '小程序成交量占比',
       android_order_cnt BIGINT COMMENT '安卓APP订单量',
       android_order_ratio DECIMAL(5,2) COMMENT '安卓APP订单量占比',
       ios_order_cnt BIGINT COMMENT '苹果APP订单量',
       ios_order_ratio DECIMAL(5,2) COMMENT '苹果APP订单量占比',
       pcweb_order_cnt BIGINT COMMENT 'PC商城成交单量',
       pcweb_order_ratio DECIMAL(5,2) COMMENT 'PC商城成交单量占比'
    )
    COMMENT '渠道销量占比' 
    ROW format delimited fields terminated BY '\t' 
    stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
    ```
  
    
  
  - 实现
  
    ```sql
    --渠道销量占比
    insert into yp_rpt.rpt_sale_fromtype_ratio
    select 
       date_time,
       time_type,
       year_code,
       year_month,
       dim_date_id,
       
       order_cnt,
       miniapp_order_cnt,
       cast(
          cast(miniapp_order_cnt as DECIMAL(38,4)) / cast(order_cnt as DECIMAL(38,4))
          * 100
          as DECIMAL(5,2)
       ) miniapp_order_ratio,
       android_order_cnt,
       cast(
          cast(android_order_cnt as DECIMAL(38,4)) / cast(order_cnt as DECIMAL(38,4))
          * 100
          as DECIMAL(5,2)
       ) android_order_ratio,
       ios_order_cnt,
       cast(
          cast(ios_order_cnt as DECIMAL(38,4)) / cast(order_cnt as DECIMAL(38,4))
          * 100
          as DECIMAL(5,2)
       ) ios_order_ratio,
       pcweb_order_cnt,
       cast(
          cast(pcweb_order_cnt as DECIMAL(38,4)) / cast(order_cnt as DECIMAL(38,4))
          * 100
          as DECIMAL(5,2)
       ) pcweb_order_ratio
    from yp_dm.dm_sale
    where group_type = 'all';
    ```


  ![image-20211204164818513](E:\黑马培训\Hadoop生态圈\assets\image-20211204164818513.png)

---

==销售主题从头到尾完整步骤代码==

```sql
-- 销售主题报表的需求
--rpt 需要根据报表需求 裁剪拼接数据 支持报表

-- ================================ 店铺月销售单量的统计 ======================================
-- 1. 门店月销售单量排行
-- 维度: store店铺
-- 时间维度: month 月
-- 销售单量: order_cnt

-- 2. 建模 在rpt层建立一张表 将上述查询出来的结果保存起来 便于BI查询展示
create table yp_rpt.rpt_sale_store_month_cnt(

    -- 为了以后能知道，我们是哪一天做的统计报表，这是时候可以加一个字段，作为统计日期
    date_time STRING COMMENT '统计日期，不能用来分组统计',
    year_code STRING COMMENT '年code',

    -- 时间粒度标记
    year_month STRING COMMENT '月code',

    -- todo 维度
    -- 店铺id对应的有非常多 不知道具体它是在哪个位置的，对人们来说不好理解
    -- 所以现在我们要把它大范围的维度加上 具体到哪个城市的哪个商圈
    -- 例子: 比如说店铺叫开发区旗舰店 全国各地有很多会叫这个名字，为了能具体的找到是哪一家店
    -- 所以我们加上这个旗舰店所属的商圈，所属的店铺，便于我们理解
     city_id STRING COMMENT '城市id',
     city_name STRING COMMENT '城市名称',
     trade_area_id STRING COMMENT '商圈id',
     trade_area_name STRING COMMENT '商圈名称',
     store_id STRING COMMENT '店铺id',
     store_name STRING COMMENT '店铺名称',
     -- 时间粒度标记被取出来了 如最上面所示
     -- time_type string COMMENT '时间粒度标记',

     -- todo 指标
     order_store_cnt BIGINT COMMENT '店铺成交量',
     miniapp_order_store_cnt BIGINT COMMENT '小程序成交量',
     android_order_store_cnt BIGINT COMMENT '安卓端店铺成交单量',
     ios_order_store_cnt BIGINT COMMENT 'ios端店铺成交单量',
     pcweb_order_store_cnt BIGINT COMMENT 'pc页面端店铺成交单量'

)
row format delimited fields terminated by '\t'
stored AS ORC tblproperties ('orc.compress'='SNAPPY');

-- 3. sql实现
-- 根据group_type过滤出店铺的数据,再根据time_type过滤出月指标的数据
insert into hive.yp_rpt.rpt_sale_store_month_cnt
SELECT
     -- 提取返回的字段的步骤
     -- 1. 打开我们要插入的目标表，拖出我们需要的所有字段
     -- 2. 因为目标表的字段是我们根据业务需求起的名字，会与数据源表中的字段个别名字不相同
     -- 导致select语句检测不到字段，在数据源表中
     -- 3. 所以我们需要根据数据源表，修改相对应的字段名称
     -- 4. 名称与目标表字段名称是否相同无所谓，但是便于理解还是写成一样的
     -- 标记干活日期，我现在是2022-10-11干的活
     '2022-10-11' AS date_time,
     year_code,
     year_month,
     city_id,
     city_name,
     trade_area_id,
     trade_area_name,
     store_id,
     store_name,
     order_cnt as order_store_cnt,
     miniapp_order_cnt as miniapp_order_store_cnt,
     android_order_cnt as android_order_store_cnt,
     ios_order_cnt as ios_order_store_cnt,
     pcweb_order_cnt as pcweb_order_store_cnt
FROM hive.yp_dm.dm_sale
WHERE group_type='store' AND time_type='month'
AND store_id IS NOT NULL;
-- 因为我们要做的是门店的月销售统计 门店存在我们才能统计，所以我们需要去除脏数据，
-- 把为store_id 为 null的字段过滤掉
-- 由于测试数据的不干净所以我们把店铺为null的过滤掉

-- ================================ 店铺日销售曲线的统计 =======================================
-- 日销售曲线
-- 维度: 日期
-- 粒度: 天
-- 销售: 究竟以谁来表示销售的变化情况 可以根据订单量来表示 也可以根据销售收入来表示
-- todo 具体统计口径 需要和需求方进行对接
--日销售曲线
-- 建表语句
DROP TABLE IF EXISTS yp_rpt.rpt_sale_day;
CREATE TABLE yp_rpt.rpt_sale_day(
   date_time string COMMENT '统计日期,不能用来分组统计',
   year_code string COMMENT '年code',
   month_code string COMMENT '月份编码',
   day_month_num string COMMENT '一月第几天',
   dim_date_id string COMMENT '日期',

   sale_amt DECIMAL(38,2) COMMENT '销售收入',
   order_cnt BIGINT COMMENT '成交单量'
)
COMMENT '日销售曲线'
ROW format delimited fields terminated BY '\t'
stored AS orc tblproperties ('orc.compress' = 'SNAPPY');

-- sql实现
INSERT INTO hive.yp_rpt.rpt_sale_day
SELECT
    '2022-10-11'AS date_time,
    year_code,
    month_code,
    day_month_num,
    dim_date_id,
    sale_amt,
    order_cnt
FROM
hive.yp_dm.dm_sale
WHERE group_type='all' AND time_type='date'

-- 为什么到rpt层以后 sql写的这么简单？
-- 这完全取决于我们前面进行的拉链，降维，增强聚合，维度上卷的一系列操作
-- 在前面我们把一个复杂问题拆分成多个小问题 把这些小问题分散到数仓的各层去解决 这样到最后我们的报表层的工作才这么简单
-- 这就叫把复杂的问题简单化，分步骤去解决问题

-- =========================== 每天不同渠道的订单量占比 ======================
-- 渠道: 小程序 安卓 ios
-- 指标: order_cnt
-- 粒度: 天
-- 建表语句
--渠道销量占比
DROP TABLE IF EXISTS yp_rpt.rpt_sale_fromtype_ratio;
CREATE TABLE yp_rpt.rpt_sale_fromtype_ratio(
   date_time string COMMENT '统计日期,不能用来分组统计',
   time_type string COMMENT '统计时间维度：year、month、day',
   year_code string COMMENT '年code',
   year_month string COMMENT '年月',
   dim_date_id string COMMENT '日期',

   order_cnt BIGINT COMMENT '成交单量',
   miniapp_order_cnt BIGINT COMMENT '小程序成交单量',
   miniapp_order_ratio DECIMAL(5,2) COMMENT '小程序成交量占比',
   android_order_cnt BIGINT COMMENT '安卓APP订单量',
   android_order_ratio DECIMAL(5,2) COMMENT '安卓APP订单量占比',
   ios_order_cnt BIGINT COMMENT '苹果APP订单量',
   ios_order_ratio DECIMAL(5,2) COMMENT '苹果APP订单量占比',
   pcweb_order_cnt BIGINT COMMENT 'PC商城成交单量',
   pcweb_order_ratio DECIMAL(5,2) COMMENT 'PC商城成交单量占比'
)
COMMENT '渠道销量占比'
ROW format delimited fields terminated BY '\t'
stored AS orc tblproperties ('orc.compress' = 'SNAPPY');

-- 将查询出来的数据插入目标表中
insert into hive.yp_rpt.rpt_sale_fromtype_ratio
SELECT
     date_time,
     time_type ,
     year_code,
    year_month,
    dim_date_id,
     -- 各个指标
     order_cnt, -- 总订单量
     -- 算占比用除法
     miniapp_order_cnt,
     cast(cast(miniapp_order_cnt as decimal(34,4)) / cast(order_cnt as decimal(34,4)) * 100 as decimal(5,2)) miniapp_order_ratio,
     android_order_cnt,
     cast(cast(android_order_cnt as decimal(34,4)) / cast(order_cnt as decimal(34,4)) * 100 as decimal(5,2)) android_order_ratio,
     ios_order_cnt,
     cast(cast(ios_order_cnt as decimal(34,4)) / cast(order_cnt as decimal(34,4)) * 100 as decimal(5,2)) ios_order_ratio,
     pcweb_order_cnt,
     cast(cast(pcweb_order_cnt as decimal(34,4)) / cast(order_cnt as decimal(34,4)) * 100 as decimal(5,2)) pcweb_order_ratio
     -- 除了 总订单量外的其他订单量的占比
FROM hive.yp_dm.dm_sale
-- 只要把每天的数据筛选出来即可
-- 分组类型以天为准 但是这个分组里面还有不同时间标记分组聚合的结果
-- 所以要把以天分组聚合的结果取出
WHERE group_type='all' and time_type='date' and order_cnt <> 0 and order_cnt is not null;
```



## 3. 商品主题报表

- 需求一：商品销量==topN==

  > 统计出某天销量最多的top10商品

- 需求二：商品收藏==topN==

  > 统计出某天收藏量最多的top10商品

- 需求三：商品加入购物车==topN==

  > 统计出某天，购物车最多的top10商品

- 建表

  ```sql
  --商品销量TOPN
  drop table if exists yp_rpt.rpt_goods_sale_topN;
  create table yp_rpt.rpt_goods_sale_topN(
      `dt` string COMMENT '统计日期',
      `sku_id` string COMMENT '商品ID',
      `payment_num` bigint COMMENT '销量'
  ) COMMENT '商品销量TopN'
  ROW format delimited fields terminated BY '\t'
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  
  --商品收藏TOPN
  drop table if exists yp_rpt.rpt_goods_favor_topN;
  create table yp_rpt.rpt_goods_favor_topN(
      `dt` string COMMENT '统计日期',
      `sku_id` string COMMENT '商品ID',
      `favor_count` bigint COMMENT '收藏量'
  ) COMMENT '商品收藏TopN'
  ROW format delimited fields terminated BY '\t' 
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  
  --商品加入购物车TOPN
  drop table if exists yp_rpt.rpt_goods_cart_topN;
  create table yp_rpt.rpt_goods_cart_topN(
      `dt` string COMMENT '统计日期',
      `sku_id` string COMMENT '商品ID',
      `cart_num` bigint COMMENT '加入购物车数量'
  ) COMMENT '商品加入购物车TopN'
  ROW format delimited fields terminated BY '\t' 
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  
  --商品退款率TOPN
  drop table if exists yp_rpt.rpt_goods_refund_topN;
  create table yp_rpt.rpt_goods_refund_topN(
      `dt` string COMMENT '统计日期',
      `sku_id` string COMMENT '商品ID',
      `refund_ratio` decimal(10,2) COMMENT '退款率'
  ) COMMENT '商品退款率TopN'
  ROW format delimited fields terminated BY '\t' 
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  ```

  ```sql
  --统计出某天销量最多的top10商品
  
  --方式1 ：简单方式
  select sku_id,order_count from yp_dm.dm_sku
  order by order_count desc limit 10;
  
  --方式2  ：复杂方式
  --需求：找出销量最多的前10个  重复的算并列 但是总数只要10个。
  with tmp as (select
      sku_id,order_count,
      rank() over(order by order_count desc) rn1,
      dense_rank() over(order by order_count desc) rn2,
      row_number() over(order by order_count desc) rn3
  from yp_dm.dm_sku)
  
  select * from tmp where rn <11;
  ```

  

- sql实现

  > 注意，==这里为了最终展示效果，保证有数据，特意在时间dt上做了特殊处理==。
  >
  > 本来是需要通过dt指定某一天数据的，这里忽略dt过滤 ，直接使用全部数据。
  >
  > ```
  > select * from yp_dws.dws_sku_daycount order by order_count desc;
  > 
  > 
  > select * from yp_dws.dws_sku_daycount where dt ='2021-08-31' order by order_count desc;
  > ```

  ```sql
  --商品销量TOPN
  insert into yp_rpt.rpt_goods_sale_topN
  select
      '2020-08-09' dt,
      sku_id,
      payment_count
  from
      yp_dws.dws_sku_daycount
  -- where
  --     dt='2020-08-09'
  order by payment_count desc
  limit 10;
  
  
  --商品收藏TOPN
  insert into yp_rpt.rpt_goods_favor_topN
  select
      '2020-08-09' dt,
      sku_id,
      favor_count
  from
      yp_dws.dws_sku_daycount 
  -- where
  --     dt='2020-08-09'
  order by favor_count desc
  limit 10;
  
  
  --商品加入购物车TOPN
  insert into yp_rpt.rpt_goods_cart_topN
  select
      '2020-08-09' dt,
      sku_id,
      cart_num
  from
      yp_dws.dws_sku_daycount
  -- where
  --     dt='2021-08-31'
  order by cart_num desc
  limit 10;
  
  --商品退款率TOPN
  insert into yp_rpt.rpt_goods_refund_topN
  select
      '2020-08-09' dt,
      sku_id,
      cast(
        cast(refund_last_30d_count as DECIMAL(38,4)) / cast(payment_last_30d_count as DECIMAL(38,4))
        * 100
        as DECIMAL(5,2)
     ) refund_ratio
  from yp_dm.dm_sku 
  where payment_last_30d_count!=0
  order by refund_ratio desc
  limit 10;
  ```

---

## 4. 用户主题报表

- 需求

  > 活跃会员数、新增会员数、新增消费会员数、总付费会员数、总会员数、会员活跃率等。

- 建表

  ```sql
  --用户数量统计
  drop table if exists yp_rpt.rpt_user_count;
  create table yp_rpt.rpt_user_count(
      dt string COMMENT '统计日期',
      day_users BIGINT COMMENT '活跃会员数',
      day_new_users BIGINT COMMENT '新增会员数',
      day_new_payment_users BIGINT COMMENT '新增消费会员数',
      payment_users BIGINT COMMENT '总付费会员数',
      users BIGINT COMMENT '总会员数',
      day_users2users decimal(38,4) COMMENT '会员活跃率',
      payment_users2users decimal(38,4) COMMENT '总会员付费率',
      day_new_users2users decimal(38,4) COMMENT '会员新鲜度'
  )
  COMMENT '用户数量统计报表'
  ROW format delimited fields terminated BY '\t'
  stored AS orc tblproperties ('orc.compress' = 'SNAPPY');
  ```

- 实现思路

- ![image-20221011222753585](E:\黑马培训\Hadoop生态圈\assets\image-20221011222753585.png)

  > 业务解读

  ```shell
  #首先，确定干活统计的日期，比如2019-05-08
  
  #1、活跃会员数
  	活跃的业务解读：	
  		1、在一个月中累积登录次数大于某个值 叫做活跃  比如累积登录次数 >20
  		2、该用户的末次登陆时间为昨天（今天统计的是昨天的），昨天来过 就表示活跃
  		3、在过去一段时间，连续登陆N天的叫做活跃
  	
  	本项目使用2：用户最后一次登录为统计日期之前一天的  表示活跃
  	
  #2、新增会员数
  	第一次登录为统计日期前一天的  表示新增
  	
  #3、新增消费会员数	
  	首次支付时间为统计日期前一天的
  
  #4、总付费会员数
  	支付次数大于0的
  	
  #5、总会员数
  
  #6、会员活跃率
  	最后一次登录时间为统计日期前一天的表示这个人很活跃  
  	除以总会员数即是会员活跃率
  	
  #7、总会员付费率
  	支付次数payment_count大于0次的表示该用户支付过  不管支付几次
  	除以总会员数即是总会员付费率
  	
  #8、会员新鲜度
  	在统计日期前一天中所有登录用户中，哪些是第一次登录的  占比即是新鲜度
  	login_date_last最后一次登录的时间
  	login_date_first 第一次登录的时间
  ```

  

- sql实现

  ```sql
  --用户数量统计
  insert into yp_rpt.rpt_user_count
  select
      '2019-05-08',
      sum(if(login_date_last='2019-05-07',1,0)), --活跃会员数
      sum(if(login_date_first='2019-05-07',1,0)),--新增会员数
      sum(if(payment_date_first='2019-05-07',1,0)), --新增消费会员数
      sum(if(payment_count>0,1,0)), --总付费会员数
      count(*), --总会员数
      if(
          sum(if(login_date_last = '2019-05-07', 1, 0)) = 0,
          null,
          cast(sum(if(login_date_last = '2019-05-07', 1, 0)) as DECIMAL(38,4))
      )/count(*), --会员活跃率
      if(
          sum(if(payment_count>0,1,0)) = 0,
          null,
          cast(sum(if(payment_count>0,1,0)) as DECIMAL(38,4))
      )/count(*), --总会员付费率
      if(
          sum(if(login_date_first='2019-05-07',1,0)) = 0,
          null,
          cast(sum(if(login_date_first='2019-05-07',1,0)) as DECIMAL(38,4))
      )/sum(if(login_date_last='2019-05-07',1,0)) --会员新鲜度
  from yp_dm.dm_user;
  ```


## 5. RPT层数据至MySQL

- 原理图示

![1、数据导出的方式](E:\黑马培训\Hadoop生态圈\assets\1、数据导出的方式.png)

> 大数据量的时候就是使用sqoop来导出数据，因为sqoop底层使用的mr，而mr喜欢处理大数据量的数据，这
>
> 样一来在分析完成后，不建议使用sqoop来导出数据，所以小数据量的时候使用presto,速度会极其快



==非0即1思想的纠正==

presto不是用来导入导出数据的，它的定位是使用在查询大量级的数据上，专业的还是使用datax，sqoop，

kettle，在这里为什么使用presto来导出数据，就是因为数据量小，我们利用完presto分析查询数据以后，方便把

数据从hive中导出到mysql，又方便速度又快



- 新零售数仓架构图

  > 从数仓架构图上，感受为什么最终需要把拼接的数据导出存储在mysql。
  >
  > 报表系统直接从hive数仓RPT层中读取数据使用可不可以？  可以但是没必要。

  ![image-20211207145330730](E:\黑马培训\Hadoop生态圈\assets\image-20211207145330730.png)

- step1：presto配置连接MySQL

  - 配置mysql Connector

    > 在每台Presto服务的==etc/catalog==目录下，新建文件==mysql.properties==，内容如下

    ```properties
    vim /export/server/presto/etc/catalog/mysql.properties
    
    connector.name=mysql
    connection-url=jdbc:mysql://192.168.88.80:3306?enabledTLSProtocols=TLSv1.2&useUnicode=true&characterEncoding=utf8
    connection-user=root
    connection-password=123456
    ```

  - 重启presto集群

    > /export/server/presto/bin/launcher restart

  - Datagrip中验证是否可以连接MySQL

    > 在presto中根据自己的需要选择需要刷新的catalog、schema等。

    ![image-20211017150803387](E:\黑马培训\Hadoop生态圈\assets\image-20211017150803387.png)

- step2：MySQL中建库

  > 在DataGrip中==选中mysql数据源==,按下F4，在控制台中输入以下sql

  ```sql
  -- 建库
  CREATE DATABASE yp_olap DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

  ![image-20211017151233258](E:\黑马培训\Hadoop生态圈\assets\image-20211017151233258.png)

- step3：使用==Presto在MySQL中建表==

  > 建表时使用Presto来操作，create语法和hive大体相同，只需要将hive中的string类型改为varchar。
  >
  > 另外文件格式、压缩格式这些mysql没有的配置去掉就可以了。
  >
  > 注意==presto不能直接删除mysql的表或数据==。

  - 详细sql语句参考课程脚本资料。

  ![image-20211017151441279](E:\黑马培训\Hadoop生态圈\assets\image-20211017151441279.png)

- step4：使用Presto将数据导出到MySQL中

  - 语法格式

    ```sql
    insert into mysql.yp_olap.rpt_sale_store_cnt_month 
    select  * from hive.yp_rpt.rpt_sale_store_cnt_month;
    ```

  - 完整版sql可以参考课程资料

    ![image-20211017151915476](E:\黑马培训\Hadoop生态圈\assets\image-20211017151915476.png)

==导出数据流程代码==

```sql

-- 方式一: 根据presto的ctas语法把hive中的数据导到mysql中
create table mysql.yp_olap.rpt_goods_sale_topn
as select * from hive.yp_rpt.rpt_goods_sale_topn;
-- 1. 指出在mysql中哪个数据库中建表 建立的表名称
-- 2. 然后在hive中选择要导出数据的表 把整个表结构加数据都复制过去
-- 给mysql中的表 这叫ctas语法


-- ================= 删除在mysql中表的数据 ===================
-- presto mysql连接器不支持删除 更新操作
--  todo 所以我们必须切换到mysql下进行操作，工作中往往不是一步到位的，而是灵活转变的
-- 可以使用delete也可以使用truncate table
delete from yp_olap.rpt_goods_sale_topn;
-- 使用truncate table 比较好在mysql中，保留表结构，清空数据
TRUNCATE TABLE yp_olap.rpt_goods_sale_topn;

-- ========================================================



-- 方式二: 如果presto不支持ctas语法 要怎么实现导出呢?
-- 只能自己手动创建表 然后使用insert+select实现

-- todo step1 在mysql中创建表
-- 在hive连接目录下选择数据来源的表 按下crtl+q 复制建表语句
-- 不要在presto连接目录下复制建表语句
-- 这里可以直接在Presto去创建mysql的表
-- 原因是: presto建表语句几乎与mysql一致 它们的sql都差不多是标准sql
-- 但是hive建表还是去hive中建表
-- 避免纠结，要用什么类型的表，就去哪里建表
-- 就是说不想纠结差异性问题 最好方式就是:
-- presto集成哪个数据源 就去哪个数据源创建
create table mysql.yp_olap.rpt_goods_sale_topn
(
    dt          varchar comment '统计日期',
    sku_id      varchar comment '商品ID',
    payment_num bigint comment '销量'
);

-- todo step2 导入数据 insert+select语法
insert into mysql.yp_olap.rpt_goods_sale_topn
select * from hive.yp_rpt.rpt_goods_sale_topn;
```

使用presto来导出数据不仅有速度快的优点，还实现了与数仓的解耦合，意思就是说哪怕我们把mysql中的表给删

除了，我们hive中的数据都还在，还是能很快的为mysql创建相对应的表，来存放我们分析的数据