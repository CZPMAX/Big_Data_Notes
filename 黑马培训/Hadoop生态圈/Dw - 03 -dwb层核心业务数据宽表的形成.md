# DWB层的核心业务

我们降维的准则：==维度表里明显不需要的我们去掉，拿捏不住的一起加入，宁滥勿缺==

dwb层宽表的形成不是面向主题的，是面向业务的，业务需要分析什么，我们就要在宽表中加入需要的核心字段

## 1. 什么是降维形成宽表？

**核心思想**

==宽表 = 事实表 + 各个维度表中需要的字段     ==>  形成某个主题方向的详细信息表==

维度的退化：不仅有多个维度表退化到一个大的维度表中，多个维度表退化到一个大的事实表，还有多个事实表退

化到一个大是事实表中...  万变不离其宗，都是有一张核心的表，来接收其他表降维下来的字段

- 新零售数仓分层图

  ![image-20220928231546487](E:\黑马培训\Hadoop生态圈\assets\image-20220928231546487.png)

  - 名称：基础数据层、中间数据层DWM
  - 功能：==退化维度（降维）==形成大宽表

- 退化维度

  - 百科定义

    > 退化维度（Degenerate Dimension,DD），就是那些看起来像是事实表的一个维度关键字，但实际上并没有对应的维度表。
    >
    > ==退化维度技术可以减少维度的数量（降维操作）==，简化维度数据仓库的模式。简单的模式比复杂的更容易理解，也有更好的查询性能。

    ![image-20220928231609316](E:\黑马培训\Hadoop生态圈\assets\image-20220928231609316.png)

  - 常见操作

    > 1、将==各个维度表的核心字段数据汇总到事实表==中；
    >
    > 2、如果一个业务模块有多个事实表，也可以将==多个事实表的核心字段汇总到一个大的事实表==。
    >
    > 3、也可以将多个==维度表的核心字段，汇总成一个大的维度宽表==中。

  - 功能

    - 通过退化维度操作之后，带来的显著效果是
      - 整个数仓中**表的个数减少**了；
      - 业务==相关联的数据==（跟你分析相关的）数据字段聚在一起了，==形成一张宽表==。
      - 分析==查询时的效率显著提高==了：多表查询和==单表查询==的差异。
    - 带来的坏处是
      - 数据大量冗余、宽表的概念已经不符合3范式设计要求了。
      - 但是数仓建模的核心追求是，只要有利于分析，能够加快数据分析，都可以做。



## 2. 宽表与普通join查询的区别

==为什么需要降维度？==

答：查询的信息需要用到多张表中的字段，但是有些字段是用不上的，如果全部进行join把字段放入结果集，会造

成大量的数据冗余，就拿下面的例子来说city这个字段是我们用不到的，数据量大的话20-30万条数据，没有用还

存着，这会对存储空间造成极大的浪费。还有查询的时候，要是多张表连接去写sql，肯定是比直接在一张表的基

础上写sql来的麻烦很多，所以我们提前选出需要的字段，对维度表进行降维，与事实表进行组合，形成一张大的

宽表，这样对往后的数据分析的操作，会提供很大的便利

举例说明

![image-20220928232703875](E:\黑马培训\Hadoop生态圈\assets\image-20220928232703875.png)

按上面的例子来看，我们要查询用户具体买了哪些商品，每种商品的销售总额，销售总量，查询时用到的字段不止

在一个表中，所以说我们需要对两张表进行join的操作，因为我们分析的时用户购买的行为，它可以有买东西，也

可以没有买东西，所以说选择的==表连接方式要以事实表为主==，即事实表的字段我们全部都需要，这时我们需要采用

left join来连接两张表

==代码==

```sql
select
fac.pid,
dim.name,
sum(num),
sum(price)
from
fact_order fac left join dim_product dim
on fac.pid=dim.id
GROUP BY fac.pid,dim.name
```

==两张表操作都要写这么多，更何况很多张表的情况，每一次写都join一堆表，人直接升天==



==这时候为了解决这一问题，宽表的概念就出来了==

接着以上面的例子为主，因为只用到的name，所以我们把维度表中的name字段加入到事实表中，形成一张新的宽表即可，如下图所示

![image-20220928235903579](E:\黑马培训\Hadoop生态圈\assets\image-20220928235903579.png)

同样的代码这时候我们再编写sql语言会发现简单了不少

==代码示例==

```sql
select
pid,
name,
sum(num),
sum(price)
from order_kuan
group by pid,name
-- 把购买同一商品的同一个人分到一组，计算它购买商品的总价，和购买商品的总件数
-- 肉眼可见的代码变整洁，简单了，查询语句写的也不那么的繁琐了
```

==从多表转换成宽表，我们所做的动作为==

事实表的结构保持不变，维度表的核心字段退化到事实表中，也就是加入事实表中

==维度表退化成宽表的图示==

![image-20220929001953350](E:\黑马培训\Hadoop生态圈\assets\image-20220929001953350.png)

==退化降维的核心，要去寻找业务相关的字段==



## 3. 订单明细宽表构建

### 3.1 思想梳理

前提：这个案例构建宽表时都是以事实表为主，为核心，来left join其他维度表，取出想要的字段数据，事实表的

数据一定都不能少，所以才用到了左外（就是说一个订单都没有出现，你去讲他的评价表，这是不现实的，肯定是

先有订单表，再有其他表的，所以是以订单表为主，用左外），其他情况具体分析用左外，还是内连接

==订单宽表的核心就是 订单表 接着宽表的形成就是把其他表的核心字段放入订单表来==

![image-20220929013040544](E:\黑马培训\Hadoop生态圈\assets\image-20220929013040544.png)

==订单表为核心，其他表就是辅助的表==

其中有个重要的地方：订单组表里面放着多笔订单的记录，记录订单组支付信息的表与订单组表关联，它们之间是

一对一的关系

==图示==

![image-20220929013624408](E:\黑马培训\Hadoop生态圈\assets\image-20220929013624408.png)

```sql
核心表: yp_dwd.fact_shop_order订单主表
		(也就说，其他表将围绕着订单主表拼接成为一张宽表)
退化维度表:
	fact_shop_order_address_detail:  订单副表 
		记录订单额外信息 与订单主表是1对1关系 (id与orderID一一对应) 
	fact_shop_order_group:  订单组表 
		多笔订单构成一个订单组 (含orderID)
	fact_order_pay:    订单组支付表
		记录订单组支付信息，跟订单组是1对1关系 (含group_id)
	fact_refund_order:  订单退款信息表
		记录退款相关信息(含orderID)		
	fact_order_settle:  订单结算表
		记录一笔订单中配送员、圈主、平台、商家的分成 (含orderID)
	fact_shop_order_goods_details:  订单和商品的中间表
		记录订单中商品的相关信息，如商品ID、数量、价格、总价、名称、规格、分类(含orderID)
	fact_goods_evaluation:  订单评价表
		记录订单综合评分,送货速度评分等(含orderID)        
	fact_order_delievery_item:  订单配送表
		记录配送员信息、收货人信息、商品信息(含orderID)
```

~~~sql
- 建表：订单明细宽表 dwb_order_detail

  > 在进行维度退化的时候，需要将各个表的==核心字段==退化到事实表中形成宽表，究竟哪些是核心字段呢？
  >
  > 答案是：明显不需要的可以不退化  另外拿捏不住  “==宁滥勿缺==”。

  ```sql
  CREATE TABLE yp_dwb.dwb_order_detail(
  --订单主表
    order_id string COMMENT '根据一定规则生成的订单编号', 
    order_num string COMMENT '订单序号', 
    buyer_id string COMMENT '买家的userId', 
    store_id string COMMENT '店铺的id', 
    order_from string COMMENT '渠道类型：android、ios、miniapp、pcweb、other', 
    order_state int COMMENT '订单状态:1.已下单\; 2.已付款, 3. 已确认 \;4.配送\; 5.已完成\; 6.退款\;7.已取消', 
    create_date string COMMENT '下单时间', 
    finnshed_time timestamp COMMENT '订单完成时间,当配送员点击确认送达时,进行更新订单完成时间,后期需要根据订单完成时间,进行自动收货以及自动评价', 
    is_settlement tinyint COMMENT '是否结算\;0.待结算订单\; 1.已结算订单\;', 
    is_delete tinyint COMMENT '订单评价的状态:0.未删除\;  1.已删除\;(默认0)', 
    evaluation_state tinyint COMMENT '订单评价的状态:0.未评价\;  1.已评价\;(默认0)', 
    way string COMMENT '取货方式:SELF自提\;SHOP店铺负责配送', 
    is_stock_up int COMMENT '是否需要备货 0：不需要    1：需要    2:平台确认备货  3:已完成备货 4平台已经将货物送至店铺 ', 
  --  订单副表
    order_amount decimal(36,2) COMMENT '订单总金额:购买总金额-优惠金额', 
    discount_amount decimal(36,2) COMMENT '优惠金额', 
    goods_amount decimal(36,2) COMMENT '用户购买的商品的总金额+运费', 
    is_delivery string COMMENT '0.自提；1.配送', 
    buyer_notes string COMMENT '买家备注留言', 
    pay_time string, 
    receive_time string, 
    delivery_begin_time string, 
    arrive_store_time string, 
    arrive_time string COMMENT '订单完成时间,当配送员点击确认送达时,进行更新订单完成时间,后期需要根据订单完成时间,进行自动收货以及自动评价', 
    create_user string, 
    create_time string, 
    update_user string, 
    update_time string, 
    is_valid tinyint COMMENT '是否有效  0: false\; 1: true\;   订单是否有效的标志',
  --  订单组
    group_id string COMMENT '订单分组id', 
    is_pay tinyint COMMENT '订单组是否已支付,0未支付,1已支付', 
  --  订单组支付
    group_pay_amount decimal(36,2) COMMENT '订单总金额\;', 
  --  退款单
    refund_id string COMMENT '退款单号', 
    apply_date string COMMENT '用户申请退款的时间', 
    refund_reason string COMMENT '买家退款原因', 
    refund_amount decimal(36,2) COMMENT '订单退款的金额', 
    refund_state tinyint COMMENT '1.申请退款\;2.拒绝退款\; 3.同意退款,配送员配送\; 4:商家同意退款,用户亲自送货 \;5.退款完成', 
  --  结算 分润
    settle_id string COMMENT '结算单号',
    settlement_amount decimal(36,2) COMMENT '如果发生退款,则结算的金额 = 订单的总金额 - 退款的金额', 
    dispatcher_user_id string COMMENT '配送员id', 
    dispatcher_money decimal(36,2) COMMENT '配送员的配送费(配送员的运费(如果退货方式为1:则买家支付配送费))', 
    circle_master_user_id string COMMENT '圈主id', 
    circle_master_money decimal(36,2) COMMENT '圈主分润的金额', 
    plat_fee decimal(36,2) COMMENT '平台应得的分润', 
    store_money decimal(36,2) COMMENT '商家应得的订单金额', 
    status tinyint COMMENT '0.待结算；1.待审核 \; 2.完成结算；3.拒绝结算', 
    settle_time string COMMENT ' 结算时间', 
  -- 订单评价
    evaluation_id string,
    evaluation_user_id string COMMENT '评论人id',
    geval_scores int COMMENT '综合评分',
    geval_scores_speed int COMMENT '送货速度评分0-5分(配送评分)',
    geval_scores_service int COMMENT '服务评分0-5分',
    geval_isanony tinyint COMMENT '0-匿名评价，1-非匿名',
    evaluation_time string,
  -- 订单配送
    delievery_id string COMMENT '主键id',
    dispatcher_order_state tinyint COMMENT '配送订单状态:0.待接单.1.已接单,2.已到店.3.配送中 4.商家普通提货码完成订单.5.商家万能提货码完成订单。6，买家完成订单',
    delivery_fee decimal(36,2) COMMENT '配送员的运费',
    distance int COMMENT '配送距离',
    dispatcher_code string COMMENT '收货码',
    receiver_name string COMMENT '收货人姓名',
    receiver_phone string COMMENT '收货人电话',
    sender_name string COMMENT '发货人姓名',
    sender_phone string COMMENT '发货人电话',
    delievery_create_time string,
  -- 商品快照
    order_goods_id string COMMENT '--商品快照id', 
    goods_id string COMMENT '购买商品的id', 
    buy_num int COMMENT '购买商品的数量', 
    goods_price decimal(36,2) COMMENT '购买商品的价格', 
    total_price decimal(36,2) COMMENT '购买商品的价格 = 商品的数量 * 商品的单价 ', 
    goods_name string COMMENT '商品的名称', 
    goods_specification string COMMENT '商品规格', 
    goods_type string COMMENT '商品分类     ytgj:进口商品    ytsc:普通商品     hots爆品', 
    goods_brokerage decimal(36,2) COMMENT '商家设置的商品分润的金额',
    is_goods_refund tinyint COMMENT '0.不退款\; 1.退款'  
  )
  COMMENT '订单明细表'
  PARTITIONED BY(dt STRING)
  row format delimited fields terminated by '\t' 
  stored as orc 
  tblproperties ('orc.compress' = 'SNAPPY');
  ```

-----
~~~

==保留维度表的核心字段，哪些又是核心？==

![image-20220929014744749](E:\黑马培训\Hadoop生态圈\assets\image-20220929014744749.png)



==要考虑到宽表，以后要作用到的分析主题有哪些？==

> 来慎重考虑保留哪一些字段，宁可多，也不能少

![image-20220929015236325](E:\黑马培训\Hadoop生态圈\assets\image-20220929015236325.png)

> 粉色表示这个宽表现在分析需求，要用到的字段只有145，但是以后分析的需求还有123，所以保留维度表的12345进入宽表，而6789现在以后都用不到，所以舍去



### 3.2 案例需求实现

> 看到字段的类型实在拿不准就用string，但是不要看到数字类型的就直接用int，万一后面需要用到小数类型的
>
> 那就不好办了

**代码**

```sql
--订单明细宽表的构建过程
--以订单主表为准 将其他副表、支付表等8个表通过left join将核心字段整合成为订单宽表
    --注意： join的方式  join的字段
    --注意： 如果表示拉链表 注意拉链的状态

--相关参数设置
--分区
SET hive.exec.dynamic.partition=true;
SET hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.max.dynamic.partitions.pernode=10000;
set hive.exec.max.dynamic.partitions=100000;
set hive.exec.max.created.files=150000;
--hive压缩
set hive.exec.compress.intermediate=true;
set hive.exec.compress.output=true;
--写入时压缩生效
set hive.exec.orc.compression.strategy=COMPRESSION;



--step1: 建表  略


--step4 :将降维之后的结果插入到dwb层

insert into table yp_dwb.dwb_order_detail partition(dt)
--step2: 基于订单主表 关联其他8张订单表
select
    --step3: 降维之后抽取保留核心字段  什么叫核心？  明显不需要的不要  拿不住都留下
        --todo 这里返回的字段个数类型顺序 要和dwb层建模时候的订单宽表的字段保持一致
    --订单主表保留的字段
    o.id as order_id,
    o.order_num,
    o.buyer_id,
    o.store_id,
    o.order_from,
    o.order_state,
    o.create_date,
    o.finnshed_time,
    o.is_settlement,
    o.is_delete,
    o.evaluation_state,
    o.way,
    o.is_stock_up,

    --订单副表
    od.order_amount,
    od.discount_amount,
    od.goods_amount,
    od.is_delivery,
    od.buyer_notes,
    od.pay_time,
    od.receive_time,
    od.delivery_begin_time,
    od.arrive_store_time,
    od.arrive_time,
    od.create_user,
    od.create_time,
    od.update_user,
    od.update_time,
    od.is_valid,

    --订单组表
    og.group_id,
    og.is_pay,

    --订单组支付表 注意这个不是订单的金额 是订单组
    op.order_pay_amount as group_pay_amount,

    --退款单
    fro.id as refund_id,
    fro.apply_date,
    fro.refund_reason,
    fro.refund_amount,
    fro.refund_state,

    --结算表
    fos.id  as settle_id,
    fos.settlement_amount,
    fos.dispatcher_user_id,
    fos.dispatcher_money,
    fos.circle_master_user_id,
    fos.circle_master_money,
    fos.plat_fee,
    fos.store_money,
    fos.status,
    fos.settle_time,

    --评价单
    fge.id as evaluation_id,
    fge.user_id as evaluation_user_id,
    fge.geval_scores,
    fge.geval_scores_speed,
    fge.geval_scores_service,
    fge.geval_isanony,
    fge.create_time as evaluation_time,

    --配送单
    fodi.id as delievery_id,
    fodi.dispatcher_order_state,
    fodi.delivery_fee,
    fodi.distance,
    fodi.dispatcher_code,
    fodi.receiver_name,
    fodi.receiver_phone,
    fodi.sender_name,
    fodi.sender_phone,
    fodi.create_time as delievery_create_time,

    --订单商品中间表
    fsogd.id as order_goods_id,
    fsogd.goods_id,
    fsogd.buy_num,
    fsogd.goods_price,
    fsogd.total_price,
    fsogd.goods_name,
    fsogd.goods_specification,
    fsogd.goods_type,
    fsogd.goods_brokerage,
    fsogd.is_refund as is_goods_refund,

    --动态分区字段
        --对于首次全量分析  时间是以分析日期为准 还是以订单具体创建时间为准   和产品经理沟通
--    o.start_date as dt
-- 要是连接的表是拉链表的话，要考虑数据的状态是否为最新，所以要加上判断语句筛选最新数据
     substr(o.create_time,1,10) as dt
from  (select * from yp_dwd.fact_shop_order where end_date='9999-99-99') o --订单主表
left join yp_dwd.fact_shop_order_address_detail od
    on o.id = od.id and od.end_date ='9999-99-99' --订单副表
left join yp_dwd.fact_shop_order_group og
    on og.order_id = o.id and og.end_date ='9999-99-99' --订单组表
left join yp_dwd.fact_order_pay op
    on op.group_id = og.group_id and op.end_date ='9999-99-99' --订单组支付表
left join yp_dwd.fact_refund_order fro
    on fro.order_id = o.id and fro.end_date ='9999-99-99'--订单退款表
left join yp_dwd.fact_order_settle fos
    on fos.order_id = o.id and fos.end_date ='9999-99-99'--订单结算表
left join yp_dwd.fact_shop_order_goods_details fsogd
    on fsogd.order_id = o.id and fsogd.end_date ='9999-99-99'--订单商品中间表
left join yp_dwd.fact_goods_evaluation fge
    on fge.order_id = o.id --订单评价表  注意这个熊孩子 是没有拉链的
left join yp_dwd.fact_order_delievery_item fodi
    on fodi.shop_order_id = o.id; --订单配送表
```



## 4. 店铺明细宽表的构建

### 4.1 思路梳理

**表的梳理**

![image-20220929021408880](E:\黑马培训\Hadoop生态圈\assets\image-20220929021408880.png)

```properties
核心表: yp_dwd.dim_store 店铺表
退化维度表:
	dim_trade_area 商圈表
		记录商圈相关信息，店铺需要归属商圈中(ID主键是店铺表中的外键，trade_area_id)
		意思就是：商圈表中的id 对应着店铺表中的 trade_area_id，它们两是连接条件
	dim_location 地址信息表
		记录了店铺地址
	dim_district 区域字典表
		记录了省市县区域的名称、别名、编码、父级区域ID
```

店铺非常的多，要是使用店铺归属商圈来写的话，一个商圈内将会有非常多的店铺的标记，（意思就是说在商圈表

中记录数据，会发生这样的情况，1商圈中有店铺a，店铺b，店铺c，...，店铺n）这样会造成本来是一张数据量很

小的表，一下子变成一张大数据量的表，因为原本只需要记录商圈id所对应的商圈名就行，但是现在还有记录店

铺，要想一个问题，一个商圈中店铺的数量绝对不是零零散散的几个，而是由很多。这样一来商圈表中的数据量倍

增，这样是不好的，所以在这里，我们反向归属，让商圈归属店铺，即店铺表里面就有商圈的id，然后我们根据这

个id，就可以去到商圈表中，查到对应的商圈名称

==总结==

谁归属谁的问题，都是理论上的大范围归属在小范围里面，也就是说在商圈表与店铺表这个为举例，就是把对应的

商圈id，在店铺表中新写出一个字段来存放，这样想要找店铺是属于哪个商圈直接去连接商圈表，根据id就能查出



==为什么地址类信息提供了两张表 “dim_location”和"dim_district"?==

**图示**

![image-20220929211253817](E:\黑马培训\Hadoop生态圈\assets\image-20220929211253817.png)

**解释：**

如果不用到地址信息表与行政区域表的话，我们每涉及到一个类型的地址信息就得新创建一张表地址表，其中的数

据是非常完全的，id，名字，省市区，详细地址都有体现，但是我们要清楚一个问题，我们存放是所有的地址，随

着店铺越开越多，同一个地方的字段信息会越来越多，造成数据大量的冗余，就是店铺同在浙江杭州，省，市两个

字段的数据会大量的重复

![image-20220929211956426](E:\黑马培训\Hadoop生态圈\assets\image-20220929211956426.png)

这样方式来建立地址表，地址表会越建越多，查询不同的地址还得查询不同的表，这样明显是复杂而又不方便的

==解决方案：==    就是把详细地址表字段进行拆分，把一张大表拆分成两张小表 ==中国行政区域字典表== 与 ==地址信息表==

> 地址信息表中增加一个字段 来标记不同类型的地址信息  
>
> 比如说增加type字段
>
> 用1标记配送地址信息
>
> 用2标记退货地址信息



![image-20220929213556752](E:\黑马培训\Hadoop生态圈\assets\image-20220929213556752.png)

> 西湖区前面的id是县id

![image-20220929215504338](E:\黑马培训\Hadoop生态圈\assets\image-20220929215504338.png)

行政区域表自己表中就有对应关系（区县对应市，市对应省，省...），这样一来我们在==地址信息表==中只要加上

县的id字段即可,里面存储的是县的id值

这样一来只要根据县的id，去left join==中国行政区域字典表==就可以找到在==中国行政区域字典表==中找到所对应的县的

名字，接着根据县的名字，可以找到它的pid（父id）

根据pid再与==中国行政区域字典表== left join一次就可以找到所对应市的名字，根据市的名字，可以找到它的pid  (父

id),

然后根据pid再与==中国行政区域字典表== left join一次就可以找到所对应省的名字

-- 综上所述三次left join 就可以得到一张完整的详细地址信息表



这样解决了数据的冗余，但是这只是一种类型的地址表，以后还有好的多种类型的地址表，总不能一种类型的地址

表创建一个新表，来与中国行政区域字典表来进行三次left join 这样也是非常麻烦的

**解决方案**

```properties
不管是店铺地址，配送地址，订单地址等等其他地址，它们都是地址，那么我们就可以建立一张地址信息表，在表中添加一
个字段type来标记不同类型的地址
```

**具体设计图示**

![image-20220929220924691](E:\黑马培训\Hadoop生态圈\assets\image-20220929220924691.png)

> 有些字段被他省了，该有还是有

**得到一个类型的详细地址信息表图示**

==获取店铺详细信息地址的SQL语句==

![image-20220929221401734](E:\黑马培训\Hadoop生态圈\assets\image-20220929221401734.png)

这里面的 d1这次的left join 代表着去查县的名称（信息）  相当于此时d1为县表

​                 d2这次的left join代表着去查市的名称（信息）  相当于此时d2为市表

​                 d3这次的left join代表着去查省的名称（信息）  相当于此时d3为省表

==小补充==

这里我们所说的地址信息表里有个字段是标记它的县id的这是对大多数记录来说的，少部分有的是直辖市，他会没

有父id，那么在地址信息表中的标记字段 里面记录的是市的id，它join不到其他记录，那么它返回的就是市的信

息，

==后续如果再加入别的类型地址信息该如何处理？==

就在地址信息表中的type字段，再加入一个数值来描述该类型地址，还是根据县id一层一层向上left join，因为

这里我们是想得到一种类型地址的详细信息，所以地址信息表为主体与中国行政区域字典表进行三次left join就可

以得到一张，指定类型的地址的详细地址信息表



### 4.2 案例需求实现

**代码**

```sql
--订单明细宽表的构建过程
--以店铺表为准  left join商圈表   地址表+行政区域表
    --注意： join的方式  join的字段
    --注意： 如果表示拉链表 注意拉链的状态

--相关参数设置
--分区
SET hive.exec.dynamic.partition=true;
SET hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.max.dynamic.partitions.pernode=10000;
set hive.exec.max.dynamic.partitions=100000;
set hive.exec.max.created.files=150000;
--hive压缩
set hive.exec.compress.intermediate=true;
set hive.exec.compress.output=true;
--写入时压缩生效
set hive.exec.orc.compression.strategy=COMPRESSION;



--step1: 建表  略

--step4 :将降维之后的结果插入到dwb层
insert into table yp_dwb.dwb_shop_detail partition(dt)
--step2: 基于店铺表
select
        --返回字段
     --店铺表
     s.id,
     s.address_info,
     s.name as store_name,
     s.is_pay_bond,
     s.trade_area_id,
     s.delivery_method,
     s.store_type,
     s.is_primary,
     s.parent_store_id,

     --商圈表
     a.name as trade_area_name,

     --省市县地址
     d3.id as province_id,
     d2.id as city_id,
     d1.id as area_id,
     d3.name as province_name,
     d2.name as city_name,
     d1.name as area_name,

     --动态分区地址
     substr(s.create_time,1,10) as dt
from (select * from yp_dwd.dim_store where end_date = '9999-99-99') s--店铺表
left join yp_dwd.dim_trade_area a
    on s.trade_area_id = a.id and a.end_date = '9999-99-99' --商圈表
left join yp_dwd.dim_location l
    on l.correlation_id = s.id and l.type =2 and l.end_date  = '9999-99-99' --地址表 todo 注意type为2的才是店铺地址
left join yp_dwd.dim_district d1 on l.adcode = d1.id  --县
left join yp_dwd.dim_district d2 on d1.pid = d2.id --市
left join yp_dwd.dim_district d3 on d2.pid = d3.id; --省
```



## 5. 商品明细宽表的构建

### 5.1 思路梳理

**表梳理**

![image-20220929224327970](E:\黑马培训\Hadoop生态圈\assets\image-20220929224327970.png)

> 一类对应多个商品，一个品牌对应多个商品
>
> 一个商品肯定有一个对应的品牌
>
> 要是在品牌表中，记录商品信息，那么这个品牌表中的数据量将会非常的庞大，毕竟一个品牌不可能只有一
>
> 个商品
>
> 举例说明：
>
> 苹果公司，苹果是一个品牌，它旗下有非常多的商品，但是它的商品肯定就只对应着一个品牌就是苹果，在
>
> 记录商品表信息，顺便添加品牌信息字段，写上它所对应的品牌，这样表中不会多出很多行数据来。这里我
>
> 们就能看出来，在商品表中 记录品牌信息是个明确的选择

==总结==：在想哪张表记录什么信息的时候，要考虑到，这样记录会不会使原来数据量简洁的表，一下子数据量变多起

来，如果出现这种情况，那么归属方向就找错了，要换个方向来记录，一般思想就是，把原来要记录的信息  所在

的表当作一张表来看  反而原来的表被当作了信息 ==>(指的是表里需要被记录的信息) 拿出来 被前面的那个表所记

录，例子：商品明细宽表，店铺明显宽表中都有体现。

```properties
核心表: dim_goods 商品SKU表
		记录了商品相关信息
退化维度表:
	dim_goods_class 商品分类表
		记录了商品所属的分类信息：商品大类、商品中类、商品小类
	dim_brand 品牌信息表	
		记录了品牌信息	
```

==重要提示==

商品的三级分类：**有小类了也就是有大，中类了，依此类推**

如果我们把商品挂在了小类下面，那么我们要找到这个商品，必须是 大类 ==> 中类 ==> 小类 也就是说这么挂我们

就必须得进行三次left join

如果把商品挂在了中类下面，那么我们找这个商品就得是 大类 ==> 中类 也就是说这么挂我们就要进行两次left 

join

如果把商品挂在了大类下面，那么我们找这个商品就得是 大类   也就是说这么挂我们就要进行一次left join

==这里面所有的操作都是要去查询所有商品的全部信息（加上   大，中，小，属于其中哪几个类的信息）==

**老师分析图解**

![image-20220929235459742](E:\黑马培训\Hadoop生态圈\assets\image-20220929235459742.png)

![image-20220929235611136](E:\黑马培训\Hadoop生态圈\assets\image-20220929235611136.png)

>  1	 大类
>  2	 中类
>  3	 小类

商品表用小类id与分类表join一次，得到小类的名称，然后根据小类它爹与分类表join一次，得到中类名称，接着

根据中类它爹与分类表join再一次，得到大类名称  ==> ==前提是商品一定得挂在小类目录下此方案才能成型==。



但是我们发现表中数据很多都是直接挂在一级分类，二级分类下，三级分类下的很少，这样之前的方案就不可行了

**解决方案：**

![image-20220930000924645](E:\黑马培训\Hadoop生态圈\assets\image-20220930000924645.png)

如果不管商品是否都挂在小类上，强行进行三次join，会发生什么情况?

![image-20220930002955655](E:\黑马培训\Hadoop生态圈\assets\image-20220930002955655.png)

![image-20221003182820352](E:\黑马培训\Hadoop生态圈\assets\image-20221003182820352.png)

> 这里上面小中大类，代表的都是小中大类的名称

会发生我明明把商品挂在了中类下面，与分类表进行了三次left join以后，居然发现返回了小类的名称和中类的名

称，但是大类的名称为空，这与我们所想要的结果相比，就像错位了一样

**解决办法：**

![image-20220930004455221](E:\黑马培训\Hadoop生态圈\assets\image-20220930004455221.png)

利用case when 把左边分析的扳正，各类别的筛选条件如右图所示，就是认为的在类别字段上加case when在不同的join时，设置level等级，来板正

**解释**

```properties
如果是小类信息 那么join第一次 得到的就是小类信息 join第二次 得到的信息是中类 join第三次得到的信息是 大类
此时 class1.level=1  class2.level=2 class3.level=3

如果是中类信息 那么join第一次就得到中类的信息 第二次就得到大类的信息
此时 class1.level=2  class2.level=3 

如果是大类信息 那么join第一次就得到大类信息
此时 class3.level=3 

-----------------------------------------------------------------------------------------
-- 错位的意思是该挂在中类下的信息，挂在的别的类下面了，所以我们就要把属于这个类，但是却挂在别的类下面的商品
手动的强制板正，让他是属于正常的类

因为商品挂载的位置不同，所以三次join，每次得到的类别信息不同，所以我们手动的根据不同连接分类表的时间手动的来板正
1.一进来就是小类 ，那么就是正常情况 三次join就能获取全部信息 判断等级class1.level=3 然后class1.level=3条件下面的所有属性都属于小类信息

2.一进来就是中类,这是错位的情况，要手动板正它，这时会有两种情况 1是正常第二次join得到中类信息，2是非正常第一次join就得到了中类信息，它们都属于中类信息 所以我们进行case when判断将满足这些条件的信息都归属于中类信息，依靠level进行判断它们是不是满足条件的信息
第一次join得到中类信息的判断条件 class1.level=2 满足这个条件的信息，都属于中类信息
第二次join得到中类信息的判断条件 class2.level=2 满足这个条件的信息，都属于中类信息

                                                         ---- 把上面的两种情况整和就是中类信息
3.一进来就是大类，错位的情况，再加上一进来就是中类的第二次join原本得出的就是大类信息，却挂在了中类下，这些都是错位严重的情况，正常的情况就是第三次join得到了大类信息 所以这里有三个level的判断，来筛选符合条件的数据，挂载到大类下
第一次join得到大类信息的判断条件 class1.level=1 满足这个条件的信息，都属于大类信息
第二次join得到大类信息的判断条件 class2.level=1 满足这个条件的信息，都属于大类信息
第三次join得到大类信息的判断条件 class3.level=1 满足这个条件的信息，都属于大类信息

                                                         ---- 把上面的三种情况整和就是大类信息

-----------------------------------------------------------------------------------------
三次join的解释
第一次join得出小类信息
第二次join再根据小类信息中的父信息join分类表 那么就得到中类信息
第三次join再根据中类信息中的父信息join分类表 那么就得到大类信息

:三次join得出小中大类信息 前提是这个商品本来就是挂在小类上面的
:如果不是那么它原本挂在哪一类下面，则第一次join就会得出哪一类的信息

那么我们可以手动的去控制完成三次join后，对应的类别信息里level的不同取值
当我们要获取小类商品信息时就直接根据不同join顺序的level等级的设置来获取信息 利用case when
case when class1.level=3 then class1.id else null end as min_class_id,
case when class1.level=3 then class1.name else null end as min_class_name

-- 设为null 新加了两个字段小类商品的id与名称 如果不满足条件则这个商品不是这类的商品 当然相应的字段设为null
 
                   就是第一次连接分类表(小类)根据小类的爸爸再连接分类表得到中类依此类推
当我们要获取中类商品信息时就直接根据不同join顺序的level等级的设置来获取信息 利用case when
case when class1.level=2 then class1.id 
     when class2.level=2 then class2.id else null end as mid_class_id,
case when class1.level=2 then class1.name 
     when class2.level=2 then class2.name else null end as mid_class_name
    
当我们要获取大类商品信息时就直接根据不同join顺序的level等级的设置来获取信息 利用case when
case when class1.level=1 then class1.id
     when class2.level=1 then class2,id
     when class3.level=1 then class3.id else null end as max_class_id,
case when class1.level=1 then class1.name
     when class2.level=1 then class2,name
     when class3.level=1 then class3.name else null end as max_class_name
```



![image-20220930004646844](E:\黑马培训\Hadoop生态圈\assets\image-20220930004646844.png)

LEVEL后面加的数字1，2，3  是代表它们是哪一次进行join商品分类表时，所对应的分类表别名

表的连接我们还是照着三次join来连接，虽然说会发生错位，但是错位的结果，我们是能人为板正的

即哪一次join返回的结果，我们能控制它在哪一个类字段上返回

这上面意思就是 商品类别的信息在哪个类别下返回



**重点理解**

**level的数值是代表它是大中小的哪一类，class1,...,class3是代表它是哪一次商品表与商品分类表进行join**

==合起来意思就是，在哪一次join，查出了规定数值下类的所有商品==

### 5.2 案例需求实现

**代码**

```sql
--商品明细宽表的构建过程
--以商品表为准  left join品牌表   商品分类表
    --注意： join的方式  join的字段
    --注意： 如果表示拉链表 注意拉链的状态

--相关参数设置
--分区
SET hive.exec.dynamic.partition=true;
SET hive.exec.dynamic.partition.mode=nonstrict;
set hive.exec.max.dynamic.partitions.pernode=10000;
set hive.exec.max.dynamic.partitions=100000;
set hive.exec.max.created.files=150000;
--hive压缩
set hive.exec.compress.intermediate=true;
set hive.exec.compress.output=true;
--写入时压缩生效
set hive.exec.orc.compression.strategy=COMPRESSION;
----------------------------------------
--分析思路：如果每个商品都是挂在小类目录下  这个实现过程就和省市县一样 join3次分类表即可
    --小类id-->join小类名称---->join中类名称----->join大类名称
--问题是 商品都是挂在小类目录下的吗？
select
       gs.id,
       gs.level --重点注意这个字段  level的数字表示分类级别  1大 2中 3小
from yp_dwd.dim_goods g left join yp_dwd.dim_goods_class gs
    on g.store_class_id = gs.id;

--通过上述sql发现  大多数商品挂在大类 中类目录下  意味着不能在join3次

--针对这种情况 有大 有中 有小 如何join呢？
--todo 方式1：针对不同的分类 分别处理 最后结果集合并
    --通过where针对level进行过滤
         --  level=1  找出大类的商品 join1次 得出大类名称
         --  level=2  找出中类的商品 join2次 先得出中类名称，再得出大类名称
         --  level=3  找出小类的商品 join3次 先得出小类名称，再得出中类名称，最后得出大类名称
    --最后将上述3个结果集合并即可。

--todo 方式2： 如果都join3次 会如何？
     --会错位。如何解决呢？
     --在最终查询返回的时候 通过case when针对level的数字进行判断 决定是否返回。

-----------------------------------------
--step1: 建表  略

--step4 :将降维之后的结果插入到dwb层
insert into table yp_dwb.dwb_goods_detail partition(dt)
--step2: 基于商品表
select
    --查询返回的字段抽取
     --商品表
     g.id,
     g.store_id,
     g.class_id,
     g.store_class_id,
     g.brand_id,
     g.goods_name,
     g.goods_specification,
     g.search_name,
     g.goods_sort,
     g.goods_market_price,
     g.goods_price,
     g.goods_promotion_price,
     g.goods_storage,
     g.goods_limit_num,
     g.goods_unit,
     g.goods_state,
     g.goods_verify,
     g.activity_type,
     g.discount,
     g.seckill_begin_time,
     g.seckill_end_time,
     g.seckill_total_pay_num,
     g.seckill_total_num,
     g.seckill_price,
     g.top_it,
     g.create_user,
     g.create_time,
     g.update_user,
     g.update_time,
     g.is_valid,

    --商品分类信息
     case when class1.level=3
          then class1.id
          else null end  as  min_class_id,
     case when class1.level=3
          then class1.name
          else null end  as  min_class_name,  --小类

     case when class1.level=2
          then class1.id
          when class2.level=2
          then class2.id
          else null end as mid_class_id,
     case when class1.level=2
          then class1.name
          when class2.level=2
          then class2.name
          else null end as mid_class_name,  --中类

     case when class1.level=1
          then class1.id
          when class2.level=1
          then class2.id
          when class3.level=1
          then class3.id
          else null end as max_class_id,
     case when class1.level=1
          then class1.name
          when class2.level=1
          then class2.name
          when class3.level=1
          then class3.name
          else null end as max_class_name,  --大类
     --商品品牌
     b.brand_name,
     --动态分区字段 天
     substr(g.create_time,1,10) as dt
from (select * from yp_dwd.dim_goods where end_date ='9999-99-99') g --商品表
left join yp_dwd.dim_brand b
    on g.brand_id = b.id and b.end_date ='9999-99-99' --品牌表
left join  yp_dwd.dim_goods_class class1
    on g.store_class_id = class1.id and class1.end_date='9999-99-99' ---小类
left join  yp_dwd.dim_goods_class class2
    on class1.parent_id = class2.id and class2.end_date='9999-99-99' ---中类
left join  yp_dwd.dim_goods_class class3
    on class2.parent_id = class3.id and class3.end_date='9999-99-99'; ---大类

```

> 一个商品到底属于哪一类，使用case when判断 符合条件的话这个商品就具体属于这个类，则另外两个类就为null
>
> 即分类信息这块字段的返回，由case when控制
