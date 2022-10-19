# 拉链表

==前言==

从mysql中导入数据到hive中有几种方式？

答：四种

方式2  的全量数据指的是新增数据的全量

方式1  的全量下面来看是全部数据，每次导入的一张表都是全量日期快照表（它跟历史表一样，一样需要建立时

间分组，方便后续的查询），历史数据信息存在历史日期快照表中

==图示==

![3、sqoop导入数据到ods层几种方式](.\assets\3、sqoop导入数据到ods层几种方式.png)

![image-20220927224133026](.\assets\image-20220927224133026.png)

![image-20220927224147014].\assets\image-20220927224147014.png)

![image-20220928201728205](.\assets\image-20220928201728205.png)

## 1. 数据同步问题

**背景**

发生自数据有缓慢变化的情况下，之前的数据要怎么办，怎么保存的情况

![image-20220927201312782](E:\黑马培训\Hadoop生态圈\assets\image-20220927201312782.png)

**如果已经同步的数据发生变化怎么办？**

![](E:\黑马培训\Hadoop生态圈\assets\image-20220927201514631.png)

![image-20220927201527577](E:\黑马培训\Hadoop生态圈\assets\image-20220927201527577.png)

![image-20220927201630789](E:\黑马培训\Hadoop生态圈\assets\image-20220927201630789.png)

![image-20220927201713045](E:\黑马培训\Hadoop生态圈\assets\image-20220927201713045.png)

![image-20220927201731384](E:\黑马培训\Hadoop生态圈\assets\image-20220927201731384.png)

![image-20220927201846894](E:\黑马培训\Hadoop生态圈\assets\image-20220927201846894.png)

```properties
使用拉链表：1.历史数据需要保留 需要维护   2. 数据尽量减少冗余

新增的数据加载到hive表中，那么之前的数据怎么保存？
--- mysql中改了就是改了，对历史数据不保留

但是hive是数据分析数据的数据库，它要对数据进行分析，那么就要考虑到历史数据的状态

解决方案：


             1.  直接更新 -- 也就是说修改的数据直接覆盖原有的数据

              优点：实现最简单，使用最方便         
              缺点：没有历史状态，如果要查询某条更新数据，之前的状态那么就查不到了

               
             2. 每次数据改变  根据日期构建一份全量快照表 每天就是一张表

             优点：记录了所有数据在不同时间下的状态
             缺点：数据冗余存储了很多没有发生变化的数据，导致存储的数据量过大

             -- 有钱公司才这么干

             3. 构建拉链表   通过时间标记发生变化的数据的每种状态的时间周期
              -- 拉链表在设计的时候，在原有的基础上，要创建两列新字段，开始时间，有效期
              -- 这两个字段就是用来标记 这条数据什么时候生效，什么时候失效
              -- 拉链表里面存储更新的数据，是与旧数据放在一起并列显示的


拉链表专门用于解决在数据仓库中数据发生变化如何实现数据存储的问题
拉链表的设计是为了将更新的数据进行状态记录
在拉链表中有效日期变化了，那么它的数据状态就失效了，就代表它更新了已经
然后下面会新加个更新的数据在下面

拉链表left join更新的只是它的有效时间，这个字段改变后，它告诉用户
这行的数值已经被改变   但是它的历史数据没有变，还保留着
最后union all就是把新增与更新的数据放在表中

拉链表左外连接 左表是拉链表 右表是新增的增量表 连接依据就是user_id,大方面来讲，就是每条数据的一个唯一标识

最后要注意：拉链表的整个过程都在 dw 层里的 dwd层

------------------------------------------------------------------------------------------

拉链表就是一个有两个关于时间的字段，是我们在dwd层创建表时，按照需求设定的

```



## 2. 拉链表设计

![image-20220927202224451](E:\黑马培训\Hadoop生态圈\assets\image-20220927202224451.png)

![image-20220927202358973](E:\黑马培训\Hadoop生态圈\assets\image-20220927202358973.png)

![image-20220927202414405](E:\黑马培训\Hadoop生态圈\assets\image-20220927202414405.png)

![image-20220927202432296](E:\黑马培训\Hadoop生态圈\assets\image-20220927202432296.png)

> ----------------------------------------------------------------------------------------------------------------------------------------------------



### 2.1 课堂画图理解

==图示==

![7、DWD层数据来源的流程](E:\黑马培训\Hadoop生态圈\assets\7、DWD层数据来源的流程.png)

==自己理解==

```properties
-- ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

-- 拉链的动作是在hive中的dwd层完成的
-- 在项目开始的时候 在hive中的ods层建立分区表 分区字段的值是由我们决定的 --起初分区字段的值为项目的开始日期

-- mysql 导出数据 全量导入hive ods层阶段

-- 把mysql中的数据全量把数据导入到hive之中，我们设置项目启动日期的时间作为分区字段的值
-- 使用的语法是query "" 里面还要加上where语句，筛选条件是，创建日期在我们项目开始日期足够远的时间  到 项目开始的日期当天的23:59，作为源数据导入ods层的表中  注意：如果表的类型是事实表则设置分区 若不是则不设置分区

-- dwd层表的创建

-- 在hive的dwd层创建相对应的拉链表 ==> 最主要的就是新加两个字段 开始时间(拉链开始时间) 与 失效时间(拉链结束时间)  -- 开始时间也是分区的字段

-- 接着采取动态分区插入数据的方式导入数据 主要的语法  insert+select
-- 在我们选择哪些字段的数据导入dwd层的表时，手动确定拉链结束时间的值作为整个字段的值 一般设置为：'9999-99-99' 接着把ods层表里面的分区字段的值作为 dwd层表中的分区字段值
-- 第一次从ods层表中全量导入数据结束

一个小流程的所有表已经都建立完成：

-----------------------------------------------------------------------------------------

接下来mysql中的数据更新.... 产生新的数据和修改的数据
--这时候我们所关注的事实表，需要维护历史数据 那么就得采取拉链表的形式

步骤：
   先创建临时表，表结构与dwd层的最终要覆盖的拉链表的结构一致
   在dwd层  使用insert+select的方式来写 select语句里写  拉链的语句
   
   1. 首先要从ods层的表中，取出今日新增的数据（新增数据包括：新增，修改），因为ods层的表可以说是比dwd层的表少一个字段，所以为了保持字段一致，我们要手动加入拉链结束时间的字段 end_time ,拉链开始的字段虽然说是ods层的表的分区字段，但是我们已经知道了表数据更新的具体日期，所以我们可以把拉链开始的时间字段进行主动赋值。这样我们就得到一张完美的增量表了。
   
   2. 接着我们要完成dwd层中的拉链表与增量数据的表的left join操作，首先从ods层中筛选出更新日期的数据，因为是表连接所以对字段的个数一不一样没有要求，我们只看他与拉链表连的上连不上，拉链表end_time字段的数值取决于两张表是否连接上了，如果连接上了，则代表着增量表的id不为空，这时我们就要修改拉链表的结束日期为 增量表开始时间的前一天，如果为空，则数据还是拉链表的结束时间。
   
   -- 附加：我们还要考虑到一条数据被更新了多次，然后进行left join还能连接上，那么我们怎么确定要修改哪一条数据的情况，但增量表的id不为空时，匹配到了多条数据，那么我们就依靠原拉链表的endtime是否小于9999:99:99来判断，如果小于了，那就不是要修改的数据，而是历史数据。反之等于了就是要修改的数据，所以if的第一个参数要加上两个条件  一个是否为空，一个是否小于结束时间 两者是or的关系，就是满足一个就行
   
   3. 前面两个小步骤完成以后，我们就要进行union all 的操作把新增且修改的数据加到里面来，这样即有了新增的数据，而且修改的数据的历史状态也得以保留，修改的数据也还在。至此整个拉链过程完成
   
   4. 然后就insert overwrite table XXX_XX_tmp partition(前面在dwd层建立拉链表时，所规定的分区字段名) select...from....order by...，把数据写入的临时表，如果数据检查无误，那么就直接，覆盖写入原来的拉链表
   
   5. 这样一次增量导入就完成了

```

![image-20220927224028727](E:\黑马培训\Hadoop生态圈\assets\image-20220927224028727.png)

==上课拉链的小例子==

![](E:\黑马培训\Hadoop生态圈\assets\image-20220927224218316.png)



![image-20220927224238508](E:\黑马培训\Hadoop生态圈\assets\image-20220927224238508.png)



<img src="E:\黑马培训\Hadoop生态圈\assets\6、拉链表的实现过程--纯技术角度-16642878113024.png" alt="6、拉链表的实现过程--纯技术角度" style="zoom:25%;" />

```properties
-- 在这里不涉及到什么层什么层的，老师只是单纯的举例子说明
-- 拉链表与增量表在建表时 表的结构相同
-- 数据来源拉链表的数据来自一个文件，增量表的数据来自一个文件 --这些文件课程资料中有提供 load 或者 hadoop fs -put 等方式都能实现数据的上传操作

接下来拉链的操作详解

拉链表（表中有两个标识数据状态的字段）与 数据增量表 先进行left join 操作，从而找出数据发生更新的行，万古不变的一个共识，拉链表的endtime字段的值取决于left join 两张表的字段连上了，还是没有连上。

如果没有连上增量表的userid字段的值会为空值，这时我们根据这个现象来判断，拉链表endtime的值究竟是多少，如果为空，那么就是没接上，这就说明这条数据没有被修改，endtime的值还是原来拉链表的值，如果不为空，则说明连接上了，这条数据被修改。那么我们把它是值修改为增量表开始时间的前一天。完成以上操作以后，left join阶段完成
-- left join 就是为了保存修改的数据的历史状态

 -- 附加：我们还要考虑到一条数据被更新了多次，然后进行left join还能连接上，那么我们怎么确定要修改哪一条数据的情况，但增量表的userid不为空时，匹配到了多条数据，那么我们就依靠原拉链表的endtime是否小于9999:99:99来判断，如果小于了，那就不是要修改的数据，而是历史数据。反之等于了就是要修改的数据，所以if的第一个参数要加上两个条件  一个是否为空，一个是否小于结束时间 两者是or的关系，就是满足一个就行
 
接下来我们就要与增量表进行union all的操作，使新增的数与修改数据一起加入到拉链表中，这样就完成了一次数据的更新

然后先把数据存放进临时表  确认无误以后覆盖原来的拉链表
```

==拉链操作的一般步骤==

![image-20220927223930437](E:\黑马培训\Hadoop生态圈\assets\image-20220927223930437.png)

==增量表如何获取？==

直接使用select语句从ods层的相应表中，筛选出新增与更新的数据即可

主要依靠两个字段：

1. create_time

                                   2. update_time    

==拉链过程完整代码==

```sql
drop table if exists yp_dwd.fact_shop_order;

-- ================== 创建dwd层的拉链表 ==============================
-- 拉链表的创建 最主要就是用来标记某条数据的状态
-- 最主要的两个特征 有效的开始时间  失效的结束时间
-- 因为需要把拉链表创建为一个分区表，方便以后的操作，所以让新增的有效的开始时间为分区字段
-- 这样一来只需要在建表时 新增一个失效时间的字段就行了，因为分区字段会自成一列，也在返回结果中
-- --订单事实表（拉链表）
DROP TABLE if EXISTS yp_dwd.fact_shop_order;
CREATE TABLE yp_dwd.fact_shop_order(
  id string COMMENT '根据一定规则生成的订单编号',
  order_num string COMMENT '订单序号',
  buyer_id string COMMENT '买家的userId',
  store_id string COMMENT '店铺的id',
  order_from string COMMENT '此字段可以转换 1.安卓\; 2.ios\; 3.小程序H5 \; 4.PC',
  order_state int COMMENT '订单状态:1.已下单\; 2.已付款, 3. 已确认 \;4.配送\; 5.已完成\; 6.退款\;7.已取消',
  create_date string COMMENT '下单时间',
  finnshed_time timestamp COMMENT '订单完成时间,当配送员点击确认送达时,进行更新订单完成时间,后期需要根据订单完成时间,进行自动收货以及自动评价',
  is_settlement tinyint COMMENT '是否结算\;0.待结算订单\; 1.已结算订单\;',
  is_delete tinyint COMMENT '订单评价的状态:0.未删除\;  1.已删除\;(默认0)',
  evaluation_state tinyint COMMENT '订单评价的状态:0.未评价\;  1.已评价\;(默认0)',
  way string COMMENT '取货方式:SELF自提\;SHOP店铺负责配送',
  is_stock_up int COMMENT '是否需要备货 0：不需要    1：需要    2:平台确认备货  3:已完成备货 4平台已经将货物送至店铺 ',
  create_user string,
  create_time string,
  update_user string,
  update_time string,
  is_valid tinyint COMMENT '是否有效  0: false\; 1: true\;   订单是否有效的标志',
  end_date string COMMENT '拉链结束日期')
COMMENT '订单表'
partitioned by (start_date string)
row format delimited fields terminated by '\t'
stored as orc
tblproperties ('orc.compress' = 'SNAPPY');

-- ===================== 使用动态分区插入需要开启一些配置 =================================
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

Alter table yp_dwd.fact_shop_order  change column  order_from order_from string;


-- 首次全量导入数据
-- ====================== 使用insert+select语句全量从ods层的数据源导入数据 =====================
-- 第一次全量导入数据不需要限定筛选条件等，直接把ods层的t_shop_order表中的数据全部导入fact_shop_order表即可
-- 因为第一次导入ods层分区字段的值就只有一个，所以直接用字段名即可
-- 注意导入数据: 失效时间需要我们手动指定，不然缺少字段，导入数据会报错
insert overwrite table yp_dwd.fact_shop_order partition (start_time)
select id,
       order_num,
       buyer_id,
       store_id,
       case
       order_from
       when 1 then 'android'
       when 2 then 'ios'
       when 3 then 'miniapp'
       when 4 then 'pcweb'
       else 'other'
       end as  order_from,
       order_state,
       create_date,
       finnshed_time,
       is_settlement,
       is_delete,
       evaluation_state,
       way,
       is_stock_up,
       create_user,
       create_time,
       update_user,
       update_time,
       is_valid,
       '9999-99-99' as end_time,
       dt as start_time -- 分区字段的时间作为开始时间
from yp_ods.t_shop_order a;

--  ===== 更新mysql数据库中相对应表的数据 ====

-- use yipin;
--
-- delete from t_shop_order where id ='dd9999999999999999';
--
-- -- 新增订单
-- INSERT INTO yipin.t_shop_order (id, order_num, buyer_id, store_id, order_from, order_state, create_date, finnshed_time, is_settlement, is_delete, evaluation_state, way, is_stock_up, create_user, create_time, update_user, update_time, is_valid) VALUES ('dd9999999999999999', '251', '2f322c3f55e211e998ec7cd30ad32e2e', 'e438ca06cdf711e998ec7cd30ad32e2e', 3, 2, '2021-11-30 17:52:23', null, 0, 0, 0, 'SELF', 0, '2f322c3f55e211e998ec7cd30ad32e2e', '2021-11-30 17:52:23', '2f322c3f55e211e998ec7cd30ad32e2e', '2021-11-30 18:52:34', 1);
--
--
-- -- 更新订单
-- UPDATE t_shop_order SET order_num=666
-- WHERE id='dd1910223851672f32';
--
--
-- UPDATE t_shop_order SET update_time='2021-11-30 12:12:12'
-- WHERE id='dd1910223851672f32';



-- ================ 利用sqoop工具将相对应的增量数据从mysql中导入hive中的ods层的表中 =================

-- 此时在ods层中的t_shop_order表已经就有了新增与修改的数据了
-- 接着我们为了更新dwd层的拉链表，在保存历史数据的前提下，我们需要在dwd层进行拉链的操作


-- sqoop import "-Dorg.apache.sqoop.splitter.allow_text_splitter=true" \
-- --connect 'jdbc:mysql://192.168.88.80:3306/yipin?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true' \
-- --username root \
-- --password 123456 \
-- --query "select *, '2021-11-30' as dt from t_shop_order where 1=1 and ((create_time between '2021-11-30 00:00:00' and '2021-11-30 23:59:59') or (update_time between '2021-11-30 00:00:00' and '2021-11-30 23:59:59')) and  \$CONDITIONS" \
-- --hcatalog-database yp_ods \
-- --hcatalog-table t_shop_order \
-- -m 1
-- wait

-- =============== 为了更新数据还要保存原数据的历史信息 我们在dwd层进行拉链的操作 ====================

-- 因为使用的是insert+select的插入方式，所以我们需要一个存放数据的表，在保证数据安全的前提下，这张表我们不用到原来的拉链表
-- 使用与拉链表结构一致的临时表存储数据
-- 创建拉链临时表

CREATE TABLE yp_dwd.fact_shop_order_tmp
(
    `id`               string COMMENT '根据一定规则生成的订单编号',
    `order_num`        string COMMENT '订单序号',
    `buyer_id`         string COMMENT '买家的userId',
    `store_id`         string COMMENT '店铺的id',
    `order_from`       TINYINT COMMENT '是来自于app还是小程序,或者pc 1.安卓; 2.ios; 3.小程序H5 ; 4.PC',
    `order_state`      INT COMMENT '订单状态:1.已下单; 2.已付款, 3. 已确认 ;4.配送; 5.已完成; 6.退款;7.已取消',
    `create_date`      string COMMENT '下单时间',
    `finnshed_time`    timestamp COMMENT '订单完成时间,当配送员点击确认送达时,进行更新订单完成时间,后期需要根据订单完成时间,进行自动收货以及自动评价',
    `is_settlement`    TINYINT COMMENT '是否结算;0.待结算订单; 1.已结算订单;',
    `is_delete`        TINYINT COMMENT '订单评价的状态:0.未删除;  1.已删除;(默认0)',
    `evaluation_state` TINYINT COMMENT '订单评价的状态:0.未评价;  1.已评价;(默认0)',
    `way`              string COMMENT '取货方式:SELF自提;SHOP店铺负责配送',
    `is_stock_up`      INT COMMENT '是否需要备货 0：不需要    1：需要    2:平台确认备货  3:已完成备货 4平台已经将货物送至店铺 ',
    `create_user`      string,
    `create_time`      string,
    `update_user`      string,
    `update_time`      string,
    `is_valid`         TINYINT COMMENT '是否有效  0: false; 1: true;   订单是否有效的标志',
     `end_time`        string  comment '拉链结束时间'
)
comment '订单表'
partitioned by (start_time string)
row format delimited fields terminated by '\t'
stored as orc tblproperties ('orc.compress' = 'ZLIB');

-- 开始拉链的操作
insert overwrite table yp_dwd.fact_shop_order_tmp partition (start_time)
select *
from  (
     -- 增量表的数据
     select id,
            order_num,
            buyer_id,
            store_id,
            order_from,
            order_state,
            create_date,
            finnshed_time,
            is_settlement,
            is_delete,
            evaluation_state,
            way,
            is_stock_up,
            create_user,
            create_time,
            update_user,
            update_time,
            is_valid,
            '9999-99-99' as end_time,
            '2021-11-30' as start_time
     from yp_ods.t_shop_order
     where dt='2021-11-30'

     union all

     -- 拉链表与增量表进行left join 操作
     select a.id,
            a.order_num,
            a.buyer_id,
            a.store_id,
            a.order_from,
            a.order_state,
            a.create_date,
            a.finnshed_time,
            a.is_settlement,
            a.is_delete,
            a.evaluation_state,
            a.way,
            a.is_stock_up,
            a.create_user,
            a.create_time,
            a.update_user,
            a.update_time,
            a.is_valid,
            if(b.id is null or a.end_time < '9999-99-99',a.end_time,date_sub(b.dt,1)) end_time,
            a.start_time
     from yp_dwd.fact_shop_order a left join (select * from yp_ods.t_shop_order where dt='2021-11-30') b
     on a.id=b.id









) c
 order by c.id , start_time;

-- 查询拉链是否成功

select *
from yp_dwd.fact_shop_order_tmp where id='dd1910223851672f32';

-- 拉链操作成功 覆盖原来拉链表的数据
INSERT OVERWRITE TABLE yp_dwd.fact_shop_order partition (start_time)
SELECT * from yp_dwd.fact_shop_order_tmp;

select *
from yp_dwd.fact_shop_order where id='dd1910223851672f32';

```



## 3. 附加知识

==多表连接==

```properties
左表，右表，表的连接join，一个表的字段会全部加在另一个表的后面
有满足连接条件的就有值，不满足连接条件的全部赋值为空

左外连接时  不管满足不满足连接条件，左表的数据都要
右外连接时  不管满足不满足连接条件，右表的数据都要
全连接时     不管满足不满足连接条件，左，右表的数据都要

表连接：连接的字段一定都是自己是自己，当字段名相同时前面不加表名 是只能看一个字段的

        a.id  a.name a.dept     left join     b.id  b.salary    
        
        结果  a.id a.name a.dept b.salary


连接条件是 看id 如果接上了，在连接结果表中，属于b表的字段将会，有值，没接上的话，属于b表
的字段的行，赋值为null

-----------------------------------------------------------------------------------------
union 的表连接是上下的连接
所以说对应的字段类型，名称都要一致这才能接的上
如果两张要union连接的表 字段数量不同的话，且有的字段名不相等
要做到缺啥补啥，什么不一样补什么
```

==拉链表的操作的辅助思想与动态分区插入==

```properties
在hive，mysql中，要进行日期函数运算的字段，字段类型没有限制，是字符串类型的也行是
时间类型类型的也行

事实表：我们所关注的内容，就是事实表，它不仅有新增的动作，也有修改的动作

每天一个增量，每天一个增量，这时候就要根据某些字段来创建分区表
在项目中，根据订单创建的时间，创建分区，每新增的数据，就是一个新的分区内的数据


分区表：  在创建分区表的时候，得指定分区字段  ==> 这个分区字段的名字不能与表中字段重复
                因为我们在查询表的所有数据的时候，这个分区字段也会成为一列返回

                分区表的数据动态插入语法：
                                                         insert into 表名 partition(分区字段) select a.*,  a.原数据表中要作为分区字段的字段 或者 自己指定的分区字段具体数据 as 分区字段名 from 原数据表 a

               -- 自己选定数值的时候，我们需要 在数值后面加上 as 分区字段名
               -- 为了方便查看，那么在数据来源是源字段时，也可以加上 as 分区字段名

               从这里看出分区字段的值，不仅仅可以是来源于原来的原数据表，也可以自己指定

-------------------------------------
小复习：
           静态插入语法：load data [local] inpath '路径' into table 分区表 partition(分区字段='分区字段的值')
-------------------------------------

--------------------------------------------------------
项目背景与划分规则：

全量导入：
根据老师的思维（也是工作中处理这类工程的思想）：

我们的项目哪一天上线，那么就把这天当作起始的日期，也就是说把分区字段中的日期值全部赋值为今日项目上线的日期，把这个日期内的数据全作为 业务数据的旧数据，指定完分区字段的数值后接着根据create_time创建时间来对所有的数据进行筛选，只导入很久以前，到今天23：59之间的数据
                           
为什么不要根据分区字段的今天的数值进制导入数据？
这种思想是极其错误的，根据sql的执行顺序，先from再where ... 再select 给分区字段赋值是在select中进行的，运行到where时，分区字段都没有值，你怎么能使用分区字段来筛选过滤数据？
                            
-- 以上的思想不仅对全量导入适用，而且还对增量导入适用  思想同源，换汤不换药！！！
```

