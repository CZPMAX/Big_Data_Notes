# 调优一大宗旨

##  一切脱离实际业务场景的调优都是耍流氓

## 1. Fetch抓取机制

- Hive中对某些情况的查询可以不必使用MapReduce计算，例如SELECT * FROM xxxx

- 在这种情况下，Hive可以简单地直接读取表对应的存储目录下的文件，然后输出查询结果

- select * （全局查找） ， select 字段 （字段查找），limit 查找

- 在执行sql的时候，==能不走====MapReduce程序处理就尽量不走MapReduce程序处理==

  hive自己处理数据

![image-20220924224727969](E:\黑马培训\Hadoop生态圈\assets\image-20220924224727969.png)

> 设置为more好多不会走mr，设置为none全都走mr



## 2. MapperReduce本地模式

> 前言：mr只有把任务提交到yarn上的时候，才会执行分布式计算任务，不提交yarn，就只是在本地执行计算任务

- 功能：如果非要执行==MapReduce程序，能够本地执行的，尽量不提交yarn上执行==。

- 默认是关闭的。意味着只要走MapReduce就提交yarn执行。

- Hive提供了一个参数，自动切换MapReduce程序为本地模式，如果不满足条件，就执行yarn模式。

  ```sql
  set hive.exec.mode.local.auto = true;
  ```

  

![image-20220924225141184](E:\黑马培训\Hadoop生态圈\assets\image-20220924225141184.png)

![image-20220924225240857](E:\黑马培训\Hadoop生态圈\assets\image-20220924225240857.png)

​    --- 3个条件必须都满足 自动切换本地模式

hive做的操作如果不是查询类的话，要走mr程序，但是上面的三个条件有些不满足

的话，就不会提交到yarn上执行



## 3. Join调优

![image-20220924225655079](E:\黑马培训\Hadoop生态圈\assets\image-20220924225655079.png)

**reduce端的join**

![image-20220924231001445](E:\黑马培训\Hadoop生态圈\assets\image-20220924231001445.png)



**Map端的join**

> 减少了大数据量的shuffer
>
> 实现这个的基础是 分布式缓存的技术（将小表数据分布式的发送到各个maptask执行的机器内存中）

1. 读大表数据，读缓存，关联数据，完成join，然后直接输出
2. 在每个mapper机器中，一个个执行这个任务

**图示：**

![image-20220924231703106](E:\黑马培训\Hadoop生态圈\assets\image-20220924231703106.png)

> 因为用到了分布式缓存技术，把表中的数据存放在内存中，但是内存它毕竟有限，所以必须join的一张表是小表才行，这是硬性条件，不然的话不能使用map端join

如果条件满足，就会自动执行map端join，条件不满足则会去执行reduce端join，虽然慢，但是他能准确无误的执行出来



如果两张大表join想走map端join的话，要把一张表变成小表，那么map join就能实现

怎么说呢？

如果一张大表是张分区表，表中的某些小分区，对join没用，那么我们可以先做一些字段裁剪，去除无用的分区，得到小表，那么这样就能走map端join了

-- 这种思想是一定存在的

-- 先过滤再join

-- 但是我们不能保证过滤完以后，一定满足小表，这时可以用分桶join



![image-20220924233519534](E:\黑马培训\Hadoop生态圈\assets\image-20220924233519534.png)

![image-20220924233956293](E:\黑马培训\Hadoop生态圈\assets\image-20220924233956293.png)

桶的个数成倍数或者相等，意思是取余数的时候，余数部分能对应上，这样相同的余数的去到一部分，分桶的效果就很明显了，如果不这样，分桶的效果会不怎么明显

然后通过开启桶表join，这样大表也可以走map join

上面参数要设置，要求尽量满足，这样效果明显



**桶表join优化的升级版，分桶后的同时再排序**

![image-20220924235307287](E:\黑马培训\Hadoop生态圈\assets\image-20220924235307287.png)

**数据排序后，join的更快，因为它不需要一个个去比较了，排好序的数据。往一个方向走，要么大，要么小，这样查找过滤判断，就很好找了**

**上面的是在创建分桶表，可不是在进行分桶查询！！！！！**



不管怎么优化，尽量走map端join

不满足小表条件，过滤操作，过滤操作还不够的话，就采取分桶join，这个还不行的话，就只能走reduce端join，这个除了慢，其他的没有什么缺点，跑多久都能把任务跑完



## 4. 数据倾斜优化

![image-20220925000126267](E:\黑马培训\Hadoop生态圈\assets\image-20220925000126267.png)

> 单机机器处理数据肯定不会出现数据倾斜，数据倾斜只有在分布式环境下处理数据才会出现

map阶段一般不讲数据倾斜，只有reduce阶段才讲数据倾斜

因为map端数据经过shuffer后，传给reducetask的数据会不一样，就是分配问题

发送数据倾斜会出现一个情况，整个mr程序卡在某点迟迟未结束，是因为mr程序中的某个reducetask任务执行还未完成，才会出现的问题



执行时间过长，容易出问题



**如何解决数据倾斜？**

![6、数据倾斜的相关问题](E:\黑马培训\Hadoop生态圈\assets\6、数据倾斜的相关问题-16640356829201.png)

右图上面那个把数据打散，采取分儿治之的思想，最后再汇总结果



**hive中有哪些地方容易出现数据倾斜？**

![image-20220925001309282](E:\黑马培训\Hadoop生态圈\assets\image-20220925001309282.png)

**分组为什么会出现数据倾斜？**

原本数据是不倾斜的，但是我们进行分组后，把相同的放到一组内，这就难免有些组内数据多，有些组内数据少了



**去重统计为什么会出现数据倾斜？**

去重的本质是：不管有几个只返回一个，怎么能返回一个？ 就是把重复的数据放在一起，然后进行操作，这样就能返回一个了，那么问题来了，要是在某一部分这个重复的数据很多，放在一起操作，那么数据倾斜就发生了。



**解决办法：**

![image-20220925001919049](E:\黑马培训\Hadoop生态圈\assets\image-20220925001919049.png)

![image-20220925002027545](E:\黑马培训\Hadoop生态圈\assets\image-20220925002027545.png)

> 方法一和方法二都不满足的话，那么直接开启自动负载均衡的这个参数



![image-20220925002152443](E:\黑马培训\Hadoop生态圈\assets\image-20220925002152443.png)

![image-20220925002302287](E:\黑马培训\Hadoop生态圈\assets\image-20220925002302287.png)

![image-20220925002321259](E:\黑马培训\Hadoop生态圈\assets\image-20220925002321259.png)

![skew join](E:\黑马培训\Hadoop生态圈\assets\skew join.png)

![image-20220925002518480](E:\黑马培训\Hadoop生态圈\assets\image-20220925002518480.png)

如果上面那几个方法都不行，直接开启这个参数



## 5. MapReduce Task并行度调整

![image-20220925002959525](E:\黑马培训\Hadoop生态圈\assets\image-20220925002959525.png)

![image-20220925003222560](E:\黑马培训\Hadoop生态圈\assets\image-20220925003222560.png)

改变读数据的类会把小文件都合并起来，进行map，从计算的角度来看提高了效率，但是从存储的角度来看，没有改变任何，因为存储的文件还都是那些小文件

-- 这就是maptaask并行度的调整



![image-20220925004016457](E:\黑马培训\Hadoop生态圈\assets\image-20220925004016457.png)

为什么这个会不生效，因为全局排序的结果，要输出到一个文件中，所以reducetask只能有一个



## 6.Explain查询计划

![image-20220925004207749](E:\黑马培训\Hadoop生态圈\assets\image-20220925004207749.png)

![image-20220925004259351](E:\黑马培训\Hadoop生态圈\assets\image-20220925004259351.png)

![image-20220925004541821](E:\黑马培训\Hadoop生态圈\assets\image-20220925004541821.png)

![image-20220925004621945](E:\黑马培训\Hadoop生态圈\assets\image-20220925004621945.png)

![image-20220925004630554](E:\黑马培训\Hadoop生态圈\assets\image-20220925004630554.png)

第一个阶段执行mr，得出运算结果，存放在一个临时目录tmp中

第二个阶段 是一个抓取动作，到临时目录中取出文件，显示到控制台上

如果mr执行失败了，第二个阶段抓取就不用做了



## 7. 并行度机制

![image-20220925005005355](E:\黑马培训\Hadoop生态圈\assets\image-20220925005005355.png)

> 硬件要求高



m1 --> m2 -->m3  任务有依赖只能一个个开启执行 ==> 串行执行



![image-20220925005443601](E:\黑马培训\Hadoop生态圈\assets\image-20220925005443601.png)



优化上图这个任务  前提m1与m2没有依赖关系，这样它们就可以并行执行任务，但是hive默认一次只执行一个阶段，就比如说只执行m1 --> m3 结束后才会执行 m2 -->m3



任务之间没有依赖，任务就能并行执行



所以这个时候为了使任务能够并行执行，所以要设置相关配置参数，开启任务并行，还可以修改一个sql允许最大的并行度，默认的并行度是8，但是你可以修改为其他个数，可是这个对硬件的要求非常高

![image-20220925010049707](E:\黑马培训\Hadoop生态圈\assets\image-20220925010049707.png)

![image-20220925010158417](E:\黑马培训\Hadoop生态圈\assets\image-20220925010158417.png)

> 使用order by 就是为了找出一组数据内的topN的数据 所以加上这个可以避免资源的浪费，省时间

**图示：**

![image-20220925010438874](E:\黑马培训\Hadoop生态圈\assets\image-20220925010438874.png)

成为局部的前三名后，再有资格去主节点争前三名

> 公司一般不开启，全凭程序员的良心

查询的时候都要加limit，避免全局的查询



## 8. 推测执行机制



![image-20220925010955352](E:\黑马培训\Hadoop生态圈\assets\image-20220925010955352.png)

> 默认map,和reduce阶段都会执行这个，但是浪费资源，我们建议关掉它



## 9. presto 分布式查询引擎优化

==我们使用presto就是为了查询数据能更快(越快越好)，规整sql语句的写法==

==它的优化跟hive不同，自成一套==

==presto是分布式的软件它是MS架构的 是个集群有主节点和从节点自分的 worker是负责具体的执行任务的==

worker通过连接器获取数据库的元数据，再从hdfs中获取数据，进行计算

![image-20221002000710891](E:\黑马培训\Hadoop生态圈\assets\image-20221002000710891.png)



### 1. 常规优化

- 数据存储优化

  ```sql
  --1）合理设置分区
  	与Hive类似，Presto会根据元信息读取分区数据，合理的分区能减少Presto数据读取量，提升查询性能。
  
  --2）使用列式存储
  	Presto对ORC文件读取做了特定优化，因此在Hive中创建Presto使用的表时，建议采用ORC格式存储。相对于Parquet，Presto对ORC支持更好。
  	Parquet和ORC一样都支持列式存储，但是Presto对ORC支持更好，而Impala对Parquet支持更好。在数仓设计时，要根据后续可能的查询引擎合理设置数据存储格式。
  
  --3）使用压缩
  	数据压缩可以减少节点间数据传输对IO带宽压力，对于即席查询需要快速解压，建议采用Snappy压缩。
  
  --4）预先排序
  	对于已经排序的数据，在查询的数据过滤阶段，ORC格式支持跳过读取不必要的数据。比如对于经常需要过滤的字段可以预先排序。
  
  INSERT INTO table nation_orc partition(p) SELECT * FROM nation SORT BY n_name;
  如果需要过滤n_name字段，则性能将提升。
  
  SELECT count(*) FROM nation_orc WHERE n_name=’AUSTRALIA’; 
  
  ```

- SQL优化

  - 列裁剪
  - 分区裁剪
  - group by优化
    - 按照数据量大小降序排列
  - ==order by使用limit==
  - ==用regexp_like代替多个like语句==
  - ==join时候大表放置在左边==

- 替换非ORC格式的Hive表

![image-20221001234052704](E:\黑马培训\Hadoop生态圈\assets\image-20221001234052704.png)

----

![image-20221001234258001](E:\黑马培训\Hadoop生态圈\assets\image-20221001234258001.png)

> group by分组的时候  最好的方式是按大范围的先分组 再根据小范围分组 ==>为的是第一次能开启更多的task任务来处理数据

==图示==

![image-20221001235434619](E:\黑马培训\Hadoop生态圈\assets\image-20221001235434619.png)

![image-20221001235443924](E:\黑马培训\Hadoop生态圈\assets\image-20221001235443924.png)

![image-20221001235511899](E:\黑马培训\Hadoop生态圈\assets\image-20221001235511899.png)

> 使用order by 就是为了找出根据某个条件 数据排名前列或者排名靠后的 那何必要返回全表数据呢？直接返回
>
> 需要范围的条数数据就好，所以使用limit来过滤数据，减少返回的条数
>
> order by时加limit 他会在局部先进行排序 多个小局部排序的前列 汇集到一块在进行整体的排序 减少了全表
>
> 扫描

![image-20221002001245749](E:\黑马培训\Hadoop生态圈\assets\image-20221002001245749.png)

> 使用正则匹配 能更高效的查询到数据 向下面那个使用like的查询 每判断一个条件都要进行一次全表扫描 任务
>
> 要处理的数据量非常大
>
> 但是要是使用正则的话扫描一次都对条件进行判断，效率会提升很多

![image-20221002001811459](E:\黑马培训\Hadoop生态圈\assets\image-20221002001811459.png)

图示

![image-20221002003329000](E:\黑马培训\Hadoop生态圈\assets\image-20221002003329000.png)

> 左边的大表被分割成多个小表然后与右边表进行join join一次输出到每个worker里面进行计算



### 2. 内存调优

==presto已经摆脱了yarn==

![image-20221002004655372](E:\黑马培训\Hadoop生态圈\assets\image-20221002004655372.png)

- presto可以自己去申请资源

- 内存管理机制--内存分类

  > Presto管理的内存分为两大类：==user memory==和==system memory==

  - user memory用户内存

    ```
    跟用户数据相关的，比如读取用户输入数据会占据相应的内存，这种内存的占用量跟用户底层数据量大小是强相关的
    ```

  - system memory系统内存

    ```
    执行过程中衍生出的副产品，比如tablescan表扫描，write buffers写入缓冲区，跟查询输入的数据本身不强相关的内存。
    ```

- 内存管理机制--内存池

  > ==内存池中来实现分配user memory和system memory==。
  >
  > 内存池为常规内存池GENERAL_POOL、预留内存池RESERVED_POOL。

  ![image-20211011201539017](E:\黑马培训\Hadoop生态圈\assets\image-20211011201539017.png)

  ```properties
  1、GENERAL_POOL:在一般情况下，一个查询执行所需要的user/system内存都是从general pool中分配的，reserved pool在一般情况下是空闲不用的。
  
  2、RESERVED_POOL:大部分时间里是不参与计算的，但是当集群中某个Worker节点的general pool消耗殆尽之后，coordinator会选择集群中内存占用最多的查询，把这个查询分配到reserved pool，这样这个大查询自己可以继续执行，而腾出来的内存也使得其它的查询可以继续执行，从而避免整个系统阻塞。
  
  注意:
  reserved pool到底多大呢？这个是没有直接的配置可以设置的，他的大小上限就是集群允许的最大的查询的大小(query.total-max-memory-per-node)。
  reserved pool也有缺点，一个是在普通模式下这块内存会被浪费掉了，二是大查询可以用Hive来替代。因此也可以禁用掉reserved pool（experimental.reserved-pool-enabled设置为false），那系统内存耗尽的时候没有reserved pool怎么办呢？它有一个OOM Killer的机制，对于超出内存限制的大查询SQL将会被系统Kill掉，从而避免影响整个presto。
  
  
  OOM （Out	Of MemoryError）内存不足  内存使用量溢出 超出最大范围了
  ```

- 内存相关参数

  ![image-20211011201802703](E:\黑马培训\Hadoop生态圈\assets\image-20211011201802703.png)

  ```properties
  1、user memory用户内存参数
  query.max-memory-per-node:单个query操作在单个worker上user memory能用的最大值
  query.max-memory:单个query在整个集群中允许占用的最大user memory
  
  2、user+system总内存参数
  query.max-total-memory-per-node:单个query操作可在单个worker上使用的最大(user + system)内存
  query.max-total-memory:单个query在整个集群中允许占用的最大(user + system) memory
  
  --以上四个参数是重点guan
  
  当这些阈值被突破的时候，query会以insufficient memory（内存不足）的错误被终结。
  
  3、协助阻止机制
  在高内存压力下保持系统稳定。当general pool常规内存池已满时，操作会被置为blocked阻塞状态，直到通用池中的内存可用为止。此机制可防止激进的查询填满JVM堆并引起可靠性问题。
  
  4、其他参数
  memory.heap-headroom-per-node:这个内存是JVM堆中预留给第三方库的内存分配，presto无法跟踪统计，默认值是-Xmx * 0.3
  
  5、结论
  GeneralPool = 服务器总内存 - ReservedPool - memory.heap-headroom-per-node - Linux系统内存
  
  常规内存池内存大小=服务器物理总内存-服务器linux操作系统内存-预留内存池大小-预留给第三方库内存
  ```

- 内存优化建议

  - 常见的报错解决

    > total memory= user memory +system

    ```properties
    1、Query exceeded per-node total memory limit of xx
    适当增加query.max-total-memory-per-node。
    
    2、Query exceeded distributed user memory limit of xx
    适当增加query.max-memory。
    
    3、Could not communicate with the remote task. The node may have crashed or be under too much load
    内存不够，导致节点crash，可以查看/var/log/message。
    ```

  - 建议参数设置

    ```properties
    1、query.max-memory-per-node和query.max-total-memory-per-node是query操作使用的主要内存配置，因此这两个配置可以适当加大。
    memory.heap-headroom-per-node是三方库的内存，默认值是JVM-Xmx * 0.3，可以手动改小一些。
    
    1) 各节点JVM内存推荐大小: 当前节点剩余内存*80%
    
    2) 对于heap-headroom-pre-node第三方库的内存配置: 建议jvm内存的%15左右
    
    3) 在配置的时候, 不要正正好好, 建议预留一点点, 以免出现问题
    
    数据量在35TB , presto节点数量大约在30台左右 (128GB内存 + 8核CPU)   
    
    注意：
    1、query.max-memory-per-node小于query.max-total-memory-per-node。
    2、query.max-memory小于query.max-total-memory。
    3、query.max-total-memory-per-node 与memory.heap-headroom-per-node 之和必须小于 jvm max memory，也就是jvm.config 中配置的-Xmx。
    ```

    

------

## 10. 宽表形成的时多表join的优化

- Map 端join

![image-20221003205835425](E:\黑马培训\Hadoop生态圈\assets\image-20221003205835425.png)

```properties
1、什么场景遇到什么问题？

	场景：dwb层降维 多表join操作 订单宽表9张表
	问题：执行过程很慢
	
2、面对问题有什么思路？

   底层使用hive实现降维 
   hive的join如何优化？
   
3、优化的原理
	
	hivesql 底层对于默认引擎MapReduce 它是怎么去执行的？

	map端join比reduce端join（common join）快？ 为什么快？  对比
	
	如何才能让底层走map端join？	 
			限制：分布式缓存 对于大表数据无法缓存。
			
			如果可以的话，先过滤 再join
			
			a.满足条件自动转换
				set hive.auto.convert.join=true;
				set hive.auto.convert.join.noconditionaltask.size=512000000;
				
			b.分桶操作  bucket map join 
				分桶字段=join字段
				create table xxx 
				clustered by 分桶字段 into N Bucktes;
				
			c.SMB：有序的buckt map join
				分桶字段=每个桶内的排序字段=join字段
				clustered by  分桶字段 sort by 分桶字段 into N Bucktes.
	
4、如何验证优化是生效的？
	看优化前后的sql执行计划
	explain
	
	下面的都是执行计划中能看见的什么类型的join
	
	map join operator
	
	join operator
	
```

- Map Side Join 

  ```shell
  set hive.auto.convert.join=true;
  
  #如果参与连接的N个表(或分区)中的N-1个的总大小小于512MB，则直接将join转为Map端join,默认值为20MB
  set hive.auto.convert.join.noconditionaltask.size=512000000;
  ```

- ==Bucket-Map== Join

  ```shell
  1）	set hive.optimize.bucketmapjoin = true;
  
  2） 一个表的bucket数是另一个表bucket数的整数倍
  
  3） bucket分桶字段 == join的字段
  ```

- Sort Merge Bucket Join（==SMB Join==）

  > SMB是针对Bucket Map Join的一种优化。条件类似却有些不一样。

  ```shell
  1）
  	set hive.optimize.bucketmapjoin = true;
  	set hive.auto.convert.sortmerge.join=true;
  	set hive.optimize.bucketmapjoin.sortedmerge = true;
  	set hive.auto.convert.sortmerge.join.noconditionaltask=true;
  	
  2）	
  	Bucket 列 == Join 列 == sort 列
  	
  	#hive并不检查两个join的表是否已经做好bucket且sorted，需要用户自己去保证join的表数据sorted， 否则可能数据不正确。
  	
  3）
  	bucket数相等
  	
  
  #注意：
  	a、可以设置参数hive.enforce.sorting 为true，开启强制排序。插数据到表中会进行强制排序。
  	b、表创建时必须是CLUSTERED BY+SORTED BY
  ```

  **调优测试**

  ```sql
  --针对dwb层 降维中 涉及的多表join 可以考虑hive的join优化
      ---1、开启自动转换为mapjoin的参数  设置小表的条件
      set hive.auto.convert.join=true;
      set hive.auto.convert.join.noconditionaltask.size=512000000;
  
      ---2、如果上述依然不能走map join  考虑针对表进行分桶操作  todo 自己手动创建分桶表
          --可以选择普通的bucket map join  也可以使用SMB join
              --写入数据强制分桶
              set hive.enforce.bucketing=true;
              --写入数据强制排序
              set hive.enforce.sorting=true;
              --开启bucketmapjoin
              set hive.optimize.bucketmapjoin = true;
              --开启SMB Join
              set hive.optimize.bucketmapjoin.sortedmerge=true;
              set hive.auto.convert.sortmerge.join=true;
              set hive.auto.convert.sortmerge.join.noconditionaltask=true;
  
  -----------------------------------------------------------------------------------
  
  --测试1 关闭所有优化的参数  看执行计划
  --开启mapjoin
  set hive.auto.convert.join=false;
  --写入数据强制分桶
  set hive.enforce.bucketing=false;
  --写入数据强制排序
  set hive.enforce.sorting=false;
  --开启bucketmapjoin
  set hive.optimize.bucketmapjoin = false;
  --开启SMB Join
  set hive.optimize.bucketmapjoin.sortedmerge=false;
  set hive.auto.convert.sortmerge.join=false;
  set hive.auto.convert.sortmerge.join.noconditionaltask=false;
  
  
  
  --测试2 开启所有优化的参数  看执行计划
  --开启mapjoin
  set hive.auto.convert.join=true;
  set hive.auto.convert.join.noconditionaltask.size=512000000;
  --写入数据强制分桶
  set hive.enforce.bucketing=true;
  --写入数据强制排序
  set hive.enforce.sorting=true;
  --开启bucketmapjoin
  set hive.optimize.bucketmapjoin = true;
  --开启SMB Join
  set hive.optimize.bucketmapjoin.sortedmerge=true;
  set hive.auto.convert.sortmerge.join=true;
  set hive.auto.convert.sortmerge.join.noconditionaltask=true;
  
  
  
  
  explain select
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
  
  

## 11. 数据仓库元数据管理

- 1、背景

  - 问题：==任何一个不了解当前数据仓库设计的人，是无法根据当前的数据来合理的管理和使用数==
    ==据仓库的==（这何尝不是每个刚入职的程序员的心声）
    - 数据使用者：数据分析师、运营、市场
      - 有哪些业务数据，范围？影响？有效性？能否支撑需求？
    - 数据开发者：大数据工程师
      - 新的开发者不能快速的了解整个数据仓库
      - 如何能从数据仓库的N多张表中找到自己想要的那张表
    - 领导决策层：leader、架构师、CTO
      - 快速的了解公司对哪些数据做了处理，做了哪些应用
  - 解决：==对数据仓库构建元数据，通过元数据来描述数据仓库==
    - 任何一个人，只要知道了数据仓库的元数据，就可以快速的对数据仓库中的设计有一定
      的了解，快速上手

- 2、元数据是什么

  > 用于描述数据的数据、记录数据的数据、解释性数据。
  >
  > 想一想
  >
  > ​	HDFS中NN管理了文件系统的元数据，这里的元数据是什么？
  >
  > ​		文件自身属性信息、文件与block位置信息（内存、fsimage edits log）
  >
  > ​	Hive中启动metastore服务访问metadata，这里的metadata是什么，存储在哪？
  >
  > ​		表和HDFS上结构化文件映射信息（表和文件位置、字段类型  顺序  分隔符）
  >
  > ​		Hive的元数据存储在RDBMS（derby MySQL）

  - 数据仓库的元数据：用于描述整个数据仓库中所有==数据的来源==、==转换==、==关系==、==应用==

    > 比如模型的定义、各层级间的映射关系、监控数据仓库的数据状态及ETL的任务运行状态

- 3、数仓元数据分类

  - ==业务元数据==：提供业务性的元数据支持

    ```
    为管理层和业务分析人员服务，从业务角度描述数据，包括商务术语、数据仓库中有什么数据、数据的位置和数据的可用性等；
    帮助业务人员更好地理解数据仓库中哪些数据是可用的以及如何使用。
    ```

  - ==技术元数据==：数仓的设计、规范和数据的定义

    ```
    为开发和管理数据仓库的 IT 人员使用，它描述了与数据仓库开发、管理和维护相关的数据；
    包括数据源信息、数据转换描述、数据仓库模型、数据清洗与更新规则、数据映射和访问权限等。
    ```

  - ==管理元数据==：所有数据来源，数据相关的应用、人员、应用、权限

    ```
    管理过程元数据指描述管理领域相关的概念、关系和规则的数据；
    主要包括管理流程、人员组织、角色职责等信息。
    ```

- 4、元数据管理在数仓的位置

  ![image-20220110203823591](E:/黑马培训/Hadoop生态圈/assets/image-20220110203823591.png)

- 5、元数据管理架构

  ![image-20220110203933871](E:/黑马培训/Hadoop生态圈/assets/image-20220110203933871.png)

- 6、功能

  - ==血缘分析==：向上追溯元数据对象的数据来源
  - ==影响分析==：向下追溯元数据对象对下游的影响
  - ==同步检查==：检查源表到目标表的数据结构是否发生变更
  - ==指标一致性分析==：定期分析指标定义是否和实际情况一致
  - ==实体关联查询==：事实表与维度表的代理键自动关联

- 7、应用

  - ETL自动化管理：使用元数据信息自动生成物理模型，ETL程序脚本，任务依赖关系和调
    度程序
  - 数据质量管理：使用数据质量规则元数据进行数据质量测量
  - 数据安全管理：使用元数据信息进行报表权限控制
  - 数据标准管理：使用元数据信息生成标准的维度模型
  - 数据接口管理：使用元数据信息进行接口统一管理

- 8、工具软件 ==**Apache Atlas**==

  ![image-20220110204204958](E:/黑马培训/Hadoop生态圈/assets/image-20220110204204958.png)

  ![image-20220110204211963](E:/黑马培训/Hadoop生态圈/assets/image-20220110204211963.png)



## 12. 数据质量管理

- 数据质量分类

  - 数据本身质量：数据的产生的时候，数据包含的内容
  - 数据建设质量：从采集、处理、应用
    - 靠数仓规范、元数据管理、分层设计等来保障

- 判断数据质量的维度

  - 准确性：数据采集值和真实值之间的接近程度
  - 精确性：数据采集的粒度越细，误差越低，越接近事实
  - 真实性：数据是否能代表客观的事实
  - 完整性：数据中的数据是否有缺失
  - 全面性：多维数据反映事实
  - 及时性：有效的时间内得到事实的结果
  - 关联性：数据之间关系的明确与发掘
  - 自定义规则：手机号异常  身份证号异常 时间异常  用户来源（面向长三角地区）

- 数据质量处理

  - ==缺省值==：补全推断【平均值】、给定占位符，标记这条数据【-1，或者flag，做相关统计
    的时候，这条数据不参与统计】、丢弃

    > 逻辑删除 物理删除
    >
    > 物理：真实的
    >
    > 逻辑：虚拟的      0无效  1有效     flag  :true   false    T F

    ```
    通过简单的统计分析，可以得到含有缺失值的属性个数，以及每个属性的未缺失数、缺失数和缺失率。删除含有缺失值的记录、对可能值进行插补和不处理三种情况。
    ```

  - ==异常值==：做标记，或者丢失

  > 关键是如何判断数据异常？
  >
  > 正态分布
  >
  > 标准差
  >
  > 方差
  >
  > 还可以通过数据本身特征判断是否异常？
  >
  > 中国大陆手机号  11位
  >
  > 中国大陆身份证号  18位

    ```
  如果数据是符合正态分布，在原则下，异常值被定义为一组测定值中与平均值的偏差超过3倍标准差的值；
  如果不符合正态分布，也可以用远离、偏离平均值的多少倍标准差来描述。
    ```

  - ==不一致的值==：采集了同一个数据对应的不同时间的状态

    ```
    注意数据抽取的规则，对于业务系统数据变动的控制应该保证数据仓库中数据抽取最新数据
    ```

  - ==重复数据或者含有特殊符号的值==

    ```
    在ETL过程中过滤这一部分数据，特殊数据进行数据转换。
    
    ```

  Q:如何保证数据处理时的不重复 不遗漏？
      大数据允不允许适当的数据误差？  不一定
    《大数据思维》

  

## 13. 数仓如何实现全量与增量导入

[数仓利用sqoop完成数据的全量导入与增量导入csdn网址解释](https://blog.csdn.net/and52696686/article/details/108583960)

```
sqoop import
		全量导入
			注意：默认分隔符  并行度调整
			HDFS
			Hive
				hive-database
				hive-table
					
				hcatalog-database
				hcatalog-table
					ORC等特殊格式使用
			实际中导入hvie的方式：
				1、自己建表
					可以控制表的属性  分隔符 存储格式 是否压缩 什么压缩
				2、使用sqoop导入数据
					
			
		子集导入
			where
			
			query
				支持在sqoop中写查询类的sql语句 把sql执行结果作为数据导入
				注意事项：
					where 1=1  $CONDITIONS
					
		增量导入
			sqoop自带方式
				append
					适合自增的int类型字段
				lastmodified
					适合动态变化的时间类型
					
			query自定义查询方式【重点】
				关键：业务系统表在设计的时候 要有create_time  update_time
				T+1 模式下
					xxxx-xx-xx 00:00:00      xxxx-xx-xx 23:59:59
	
	sqoop export
		注意：不管何种导出  目标表需要自己手动提前创建
		全量导出
		
		增量导出
			updateonly
				仅更新
			allowinsert
				更新+新增
	
```







