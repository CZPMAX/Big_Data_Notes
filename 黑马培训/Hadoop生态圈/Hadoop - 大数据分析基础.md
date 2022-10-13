# Hadoop -- 黄大象

**数据分析基本步骤与流程**

![image-20220913010609901](E:\黑马培训\Hadoop生态圈\assets\image-20220913010609901.png)

**分布式与集群的区别**

![image-20220913010704232](E:\黑马培训\Hadoop生态圈\assets\image-20220913010704232.png)

```python
一：主从架构
	主角色、从角色  一个大哥带领多个小弟  各司其职


二：单点故障  主备架构  负载均衡  故障转移  容错
  
	原因：单点故障
		在分布式环境中 某个节点某个服务出现故障导致整体故障
	
	解决方案：主备架构
		针对单点故障点 设置备份  形成主备架构  主出现故障 备份顶上。
		常见的是一主一备 也可以一主多备。
	
	过程：故障转移	
		
		
	最终效果：容错 failover
		容忍错误的发送
		容错能力指的分布式系统针对单点故障容忍能力。
	
三：负载均衡
	load balance 一个人干不了所有的话 多个人大家一起干活  分担任务。
	

四：伸缩性 
	动态扩容缩容 
	形容分布式集群规模可以灵活的调整
    
    
------------------------------------------------------------------------------------------------------------

1、zk
	watch监听机制。
		谁监听谁?  客户端监听服务端
		监听什么事？监听zk目录树znode的变化
		
		监听步骤。 设置  执行  触发通知
		zk监听特点
			先注册再触发
			一次性
			异步通知
2、zk的典型应用

	数据发布、订阅
		核心：集中配置管理 支持实时动态更新
		
	为其他软件选举服务
		核心：找出唯一  找出新的唯一
	
	kafka hbase hadoop——ha 都利用了zk的这些功能。
	
3、认识hadoop 搭建hadoop集群
	hadoop狭义 广义之分
	hadoop起源
		Nutch 全网搜索  面临：海量数据存储（网页）、海量数据的计算（倒排索引）
		Google3篇论文
	hadoop优点
		通用性---- 精准区分了技术和业务问题  hadoop实现了技术 用户负责实现业务
		
	hadoop发行版本、架构
		官方社区版
			apache社区
		商业发行版
			Cloudera---CDH
			阿里、腾讯等封装的版本
		1.x  2.x  3.x	
	
	hadoop核心组件
		hdfs	
			解决了海量数据存储问题  分布式存储系统
		mapreduce
			解决了海量数据的计算问题  分布式计算框架
		yarn
			解决了分布式环境资源的管理 分配 调度问题
			
		
	hadoop集群,都是标准的主从架构集群
		hdfs
			namenode(NN) datanode(DN) secondarynamenode(SNN)
		yarn
			resourcemanager(RM)  nodemanager(NM)
			
	hadoop搭建  仔细！！！！！！！
		为什么要进行源码编译？
			没有提供源码
			你想自己修改源码
			适配操作系统环境
			
		为什么要进行集群规划？
			合理分配软件和硬件之间关系
			
	hadoop namenod format 初始化
		一次 首次启动之前
		在node1 namenode所在的机器执行
		
		
	hadoop web UI页面
		hdfs:	namenode所在机器的ip:9870
		yarn:	resourcemanager所在机器的ip:8088
		
	辅助功能
		jobhistory服务
			历史作业信息保存 日志聚集管理
			本服务需要自己单独手动启动 关闭
		hdfs垃圾桶
			类似windows回收站

------------------------------------------------------------------------------------------------------------

Hadoop HDFS

1、HDFS
   首先是文件系统  存储文件数据的
   其次才是分布式特性 意味着不是单机存储
   
   HDFS是整个大数据最底层的框架 
   实际中操作：提供数据存储服务 给其他计算引擎提供读写的路径。

2、HDFS核心属性
	主从架构
		NN 管理元数据
		DN 管理数据（数据块block）
		SNN 辅助NN管理元数据
		
	分块存储
		block size=128M(134217728)   hadoop 2.0 64M
		不足128 实际多大这个块就多大
		
	副本机制
		最终3副本机制  1+2=3
		默认3副本策略
			1优先本地 否则随机
			2不同于第一个副本所在机架其他机架
			3和第二个相同机架但是不同节点
		运维：机架感知
			
		不满足条件：策略降级	
			1优先本地 否则随机
			其他副本随机
	
	元数据记录
		描述数据的数据
		文件系统：
			文件自身属性
			文件数据块的位置信息
	
	namespace
		命名空间 名称空间
		描述了一种结构：层次感的结构。  目录树有没有层次感？
	
	一次写入多次读取模型
		设计目标 延迟高 不支持修改操作 
	
3、HDFS shell操作
	命令行可以操作不同的文件系统  具体操作的谁取决于什么？
		写地址协议 如果写协议 以协议为准
		如果没有写 以配置参数值为准
	在上传 下载中涉及一个概念：本地文件系统？这个本地怎么理解。
		客户端的本地的linux系统
		客户端又在哪台机器上？
			你作为用户 在哪台机器敲命令 写代码 这台机器就是客户端机器。
			
			
4、HDFS工作机制【重要！！！！！！！！】			
	NN DN角色职责
		data metadata的区别
	
	【作业：根据课堂画图，将图片版的流程转换成为文字版的流程 能够顺利的叙述出来】
	上传文件流程（写流程）
		pipeline管道上传
		ack应答响应机制
		3副本策略
		数据是以packet包发送的
		
		上传的时候是一个block 一个block上传
		
	下载文件流程（读流程）
		分批获取副本的位置信息
		副本位置信息是排序好的
			近的靠前【近是什么近？ 空间距离远近？网络拓扑中的远近？】
			健康状态好的靠前
	
	NN DN之间的通信机制
		心跳机制
			3s 报活
		
		数据块汇报机制
			6小时 持有数据块情况

5、面试题目：遇到小文件多的场景，如何处理？	
	从存储角度来分析
		问题：
			小文件的元数据信息占内存  磁盘压力不大 内存压力大
		解决方案：合并
			上传之前合并
				使用Python java等编程语言合并
			上传之中合并
				appendToFile追加合并
			上传之后合并
				archive归档合并
			
	从计算的角度来分析

6、安全模式
	特点：
		文件系统保护状态 只读不能写
	
	自动进入离开的条件
		hdfs启动之后就会立即进入
		数据块汇报比例 0.999
		持续30s
		
	手动进入离开的用处
		集群升级维护
	
	启示	
		hdfs启动之后 必须等待安全模式结束 文件系统服务才是正常且可用的

7、HDFS的元数据管理
	
	元数据分类？
		文件自身属性
		文件数据块位置
		
	谁来管理元数据？
		nameNode
		
	元数据存储在哪里？
		内存中
			最新最全的  不安全
		磁盘上
			持久化文件 fsimage 镜像文件
			编辑日志  edits log 

```



# Hadoop -- 大数据入门基石

## 1. Hadoop介绍

狭义上Hadoop指的是Apache的一款开源软件。

用java语言实现开源软件框架允许使用简单的编程模型跨计算机集群对大型数据集进行分布式处理

### 1. Hadoop核心组件

-   Hadoop HDFS（分布式文件存储系统）：解决海量数据存储
-   Hadoop YARN（集群资源管理和任务调度框架）：解决资源任务调度
-   Hadoop MapReduce（分布式计算框架）：解决海量数据计算



### 2. hadoop生态圈

![image-20220913150648974](E:\黑马培训\Hadoop生态圈\assets\image-20220913150648974.png)



### 3. Hadoop历史版本

1.x版本系列：hadoop版本当中的第二代开源版本，主要修复0.x版本的一些bug等 

2.x版本系列：架构产生重大变化，引入了yarn平台等许多新特性 

3.x版本系列: 加入多namenoode新特性



### 4. Hadoop发展简史 

- **Hadoop之父：**Doug Cutting

- **Hadoop起源于Apache Lucene子项目：Nutch**

   Nutch的设计目标是构建一个大型的全网搜索引擎。遇到瓶颈：如何解决数十亿网页的存储和索引问题

- **Google三篇论文**

  《The Google file system》：谷歌分布式文件系统GFS

  《MapReduce: Simpliﬁed Data Processing on Large Clusters》：谷歌分布式计算框架MapReduce

  《Bigtable: A Distributed Storage System for Structured Data》：谷歌结构化数据存储系统



### 5. Hadoop版本变迁

**图示**

![image-20220913151542396](E:\黑马培训\Hadoop生态圈\assets\image-20220913151542396.png)

**集群角色划分准则**

![image-20220913151729093](E:\黑马培训\Hadoop生态圈\assets\image-20220913151729093.png)



## 2. Hadoop jobhistoty服务

**概述**

- 当MR程序在YARN上运行，YARN不会永久记录MR作业的日志信息，在YARN集群重启之后日志信息会消失；

- MR程序分布式执行也不利于日志的集中查看，**开启jobhistory服务再配合YARN日志聚集功能可以实现集中查询日志**

- jobhistory用来记录已经完成的MapReduce程序运行日志，日志信息存放于HDFS目录中;

- jobhistory默认情况下是没有开启，需要在mapred-site.xml中配置，并手动启动服务。



## 3. HDFS 的垃圾桶机制

**1． 垃圾桶机制解析**

每一个文件系统都会有垃圾桶机制，便于将删除的数据回收到垃圾桶里面去，避免某些误操作删除一些重要文件。回收到垃圾桶里里面的

资料数据，都可以进行恢复。

**2． 垃圾桶机制配置**

HDFS的垃圾回收的默认配置属性为 0，也就是说，如果你不小心误删除了某样东西，那么这个操作是不可恢复的。

修改core-site.xml ：

那么可以按照生产上的需求设置回收站的保存时间，这个时间以分钟为单位，例如1440=24h=1天。

```xml
 <property>

   <name>fs.trash.interval</name>

   <value>1440</value>

  </property>
```

然后重启hdfs集群

**3． 垃圾桶机制验证**

如果启用垃圾箱配置，dfs命令删除的文件不会立即从HDFS中删除。相反，HDFS将其移动到垃圾目录（每个用户

在/user/<username>/.Trash下都有自己的垃圾目录）。只要文件保留在垃圾箱中，文件可以快速恢复。

使用skipTrash选项删除文件，该选项不会将文件发送到垃圾箱。它将从HDFS中完全删除。



## 4. HDFS -- 分布式文件管理系统

### 1. 基础知识

#### 1. HDFS介绍

**DFS是Hadoop Distribute File System 的简称**，意为：Hadoop分布式文件系统。是Hadoop核心组件之一，作为最底层的分布式存储

服务而存在。

**分布式文件系统解决的问题就是大数据存储。**它们是横跨在多台计算机上的存储系统。分布式文件系统在大数据时代有着广泛的应用前

景，它们为存储和处理超大规模数据提供所需的扩展能力。

![image-20220913171235290](E:\黑马培训\Hadoop生态圈\assets\image-20220913171235290.png)

- HDFS使用多台计算机存储文件, 并且提供统一的访问接口(namenode), 像是访问一个普通文件系统一样使用分布式文件系统

**图示**

![image-20220913171519341](E:\黑马培训\Hadoop生态圈\assets\image-20220913171519341.png)

#### 2. HDFS设计目标

1. **硬件故障是常态**， HDFS将有成百上千的服务器组成，每一个组成部分都有可能出现故障。因此故障的检测和自动快速恢复是HDFS的

   核心架构目标。

2. HDFS上的应用与一般的应用不同，它们主要是**以流式读取数据**。**HDFS被设计成适合批量处理**，而不是用户交互式的。**相较于数据访**

   **问的反应时间，更注重数据访问的高吞吐量。**

3. 典型的HDFS文件大小是GB到TB的级别。所以，HDFS被调整成支持大文件。它应该提供很高的聚合数据带宽，**一个集群中支持数百**

   **个节点，一个集群中还应该支持千万级别的文件。**

4. 大部分HDFS应用对文件要求的是**write-one-read-many访问模型**。一个文件一旦创建、写入、关闭之后就不需要修改了。这一假设

   简化了数据一致性问题，使高吞吐量的数据访问成为可能。

5. 移动计算的代价比之移动数据的代价低。一个应用请求的计算，离它操作的数据越近就越高效，这在数据达到海量级别的时候更是如

   此。将计算移动到数据附近，比之将数据移动到应用所在显然更好。

6. 在异构的硬件和软件平台上的可移植性。这将推动需要大数据集的应用更广泛地采用HDFS作为平台。



#### 3. HDFS重要特性

首先，它是一个文件系统，用于存储文件，通过统一的命名空间目录树来定位文件；

其次，它是分布式的，由很多服务器联合起来实现其功能，集群中的服务器有各自的角色。

![image-20220913162429535](E:\黑马培训\Hadoop生态圈\assets\image-20220913162429535.png)



#### 4. master/slave架构

HDFS采用master/slave架构。一般一个HDFS集群是有一个Namenode和一定数目的Datanode组成。

**Namenode是HDFS集群主节点，Datanode是HDFS集群从节点**，两种角色各司其职，共同协调完成分布式的文件存储服务。

![1、HDFS官方架构图解读](E:\黑马培训\Hadoop生态圈\assets\1、HDFS官方架构图解读.png)

#### 5. 分块存储

HDFS中的文件在**物理上**是分块存储（block）的，块的大小可以通过配置参数来规定，默认大小在hadoop2.x版本中是128M,在

hadoop1.x中默认64M。

**图示**

![image-20220913171139508](E:\黑马培训\Hadoop生态圈\assets\image-20220913171139508.png)



**分块存储解析**

![2、如何正确理解HDFS分块存储](E:\黑马培训\Hadoop生态圈\assets\2、如何正确理解HDFS分块存储.png)

![image-20220913173132925](E:\黑马培训\Hadoop生态圈\assets\image-20220913173132925.png)

#### 6. 名字空间（NameSpace）

HDFS支持**传统的层次型文件组织结构**。用户或者应用程序可以创建目录，然后将文件保存在这些目录里。文件系统名字空间的层次结构

和大多数现有的文件系统类似：用户可以创建、删除、移动或重命名文件。

**Namenode负责维护文件系统的名字空间，任何对文件系统名字空间或属性的修改都将被Namenode记录下来。**

HDFS会给客户端提供一个统一的抽象目录树，客户端通过路径来访问文件，形如：hdfs://namenode:port/dir-a/dir-b/dir-c/file.data。

![image-20220913174332175](E:\黑马培训\Hadoop生态圈\assets\image-20220913174332175.png)



#### 7. Namenode元数据管理

我们把**目录结构**及**文件分块位置信息**叫做**元数据**。Namenode负责维护整个hdfs文件系统的目录树结构，以及**每一个文件所对应的block**

**块信息（block的id，及所在的datanode服务器）。**

**图示**

![image-20220913171210425](E:\黑马培训\Hadoop生态圈\assets\image-20220913171210425.png)

![image-20220913174143562](E:\黑马培训\Hadoop生态圈\assets\image-20220913174143562.png)

**元数据的分类**

1) 内存元数据：基于内存存储元数据，元数据比较完整

2) fsimage文件：磁盘元数据镜像文件，在NameNode工作目录中，**它不包含block所在的Datanode 信息**

3) edits文件：数据操作日志文件，用于衔接内存元数据和fsimage之间的操作日志，可通过日志运算出元数据

- fsimage + edits = 内存元数据

> 注意：当客户端对hdfs中的文件进行新增或修改时，操作记录首先被记入edit日志文件，当客户端操作成功后，相应的元数据会更新
>
> 到内存元数据中

可以通过hdfs的一个工具来查看edits中的信息

```
bin/hdfs oev -i edits -o edits.xml
```

查看

```
fsimagebin/hdfs oiv -i fsimage_0000000000000000087 -p XML -o fsimage.xml
```



#### 8. Datanode数据存储

文件的各个block的具体存储管理由datanode节点承担。

每一个block都可以在多个datanode上。

Datanode需要定时向Namenode汇报自己持有的block信息。



#### 9. 副本机制

为了容错，文件的所有block都会有副本。每个文件的block大小和副本系数都是可配置的。应用程序可以指定某个文件的副本数目。副本

系数可以在文件创建的时候指定，也可以在之后改变。

副本数量也可以通过参数设置**dfs.replication，默认是3**。

**图示**

![image-20220913171102494](E:\黑马培训\Hadoop生态圈\assets\image-20220913171102494.png)

#### 10. 一次写入，多次读出

HDFS是设计成适应一次写入，多次读出的场景，且不支持文件的修改。

正因为如此，HDFS适合用来做大数据分析的底层存储服务，并不适合用来做.网盘等应用，因为，修改不方便，延迟大，网络开销大，成

本太高。

**小结**

![image-20220913170804675](E:\黑马培训\Hadoop生态圈\assets\image-20220913170804675.png)

### 2. HDFS的基本使用

**shell命令客户端**

Hadoop提供了文件系统的shell命令行客户端，使用方法如下：

```shell
hadoop fs  <args>
```

文件系统shell包括与Hadoop分布式文件系统（HDFS）以及Hadoop支持的其他文件系统（如本地FS，HFTP FS，S3 FS等）直接交互的

各种类似shell的命令。所有FS shell命令都将路径URI作为参数。

URI格式为scheme://authority/path。对于HDFS，该scheme是hdfs，对于本地FS，该scheme是file。scheme和authority是可选的。如

果未指定，则使用配置中指定的默认方案。

对于HDFS,命令示例如下：

```shell
hadoop fs -ls  hdfs://namenode:host/parent/child

hadoop fs -ls  /parent/child  fs.defaultFS中有配置
```

**对于本地文件系统，命令示例如下： 不写前缀默认访问配置文件里面写的文件系统**

```shell
hadoop fs -ls file:///root/ 
```

如果使用的文件系统是HDFS，则使用hdfs dfs也是可以的，此时

```shell
hadoop fs <args> = hdfs dfs <args>
```

**文件系统协议**

![image-20220913175149973](E:\黑马培训\Hadoop生态圈\assets\image-20220913175149973.png)

- shell命令操作

  ```shell
  # 在hdfs的根目录下创建itcast这个目录
  hadoop fs -mkdir /itcast
  
  # 上传文件zookeeper.out到hdfs的itcast目录下
  hadoop fs -put zookeeper.out/itcast
  
  # 查看根目录下有哪些目录或者文件(简言之查看根目录下有多少东西)
  hadoop fs -ls /
  ```

**操作图示**

**1. 查看根目录下的东西**

![image-20220913152340829](E:\黑马培训\Hadoop生态圈\assets\image-20220913152340829.png)

> -----------------------------------------------------------------------------------------------------



**2. 在根目录下创建文件夹**

- **只创建一级的情况:**

![image-20220913152620602](E:\黑马培训\Hadoop生态圈\assets\image-20220913152620602.png)



- **多级创建,且父目录不存在,此时要加上-p  (parents父级的意思)**

![image-20220913153745395](E:\黑马培训\Hadoop生态圈\assets\image-20220913153745395.png)

> ----------------------------------------------------------------------------------------------------



**3. 在根目录下创建文件**

![image-20220913152809626](E:\黑马培训\Hadoop生态圈\assets\image-20220913152809626.png)

> ----------------------------------------------------------------------------------------------------



**3. 删除根目录下的文件**

![image-20220913153114330](E:\黑马培训\Hadoop生态圈\assets\image-20220913153114330.png)

> ----------------------------------------------------------------------------------------------------



**4. 删除根目录下的目录**

- 只使用 -rm 因为要删除的目标是目录删除失败

  ![image-20220913153906556](E:\黑马培训\Hadoop生态圈\assets\image-20220913153906556.png)

- 使用-rm + -r 的组合递归删除

  ![image-20220913154122230](E:\黑马培训\Hadoop生态圈\assets\image-20220913154122230.png)

> ------------------------------------------------------------------------------------------------------

- 删除后的文件或者目录也会像windows系统一样被存放进垃圾桶,前提是垃圾桶机制开启了

  ![image-20220913154942954](E:\黑马培训\Hadoop生态圈\assets\image-20220913154942954.png)

- 跳过垃圾桶直接把数据删除

  ![image-20220913155230523](E:\黑马培训\Hadoop生态圈\assets\image-20220913155230523.png)

> ----------------------------------------------------------------------------------------------------

![image-20220913155648186](E:\黑马培训\Hadoop生态圈\assets\image-20220913155648186.png)

**追加文件**

![image-20220914085718517](E:\黑马培训\Hadoop生态圈\assets\image-20220914085718517.png)

**5. Web UI页面操作**

![image-20220913155351074](E:\黑马培训\Hadoop生态圈\assets\image-20220913155351074.png)



**6. Shell命令选项**

| 选项名称       | 使用格式                                                     | 含义                       |
| -------------- | ------------------------------------------------------------ | -------------------------- |
| -ls            | -ls <路径>                                                   | 查看指定路径的当前目录结构 |
| -lsr           | -lsr <路径>                                                  | 递归查看指定路径的目录结构 |
| -du            | -du <路径>                                                   | 统计目录下个文件大小       |
| -dus           | -dus <路径>                                                  | 汇总统计目录下文件(夹)大小 |
| -count         | -count [-q] <路径>                                           | 统计文件(夹)数量           |
| -mv            | -mv <源路径> <目的路径>                                      | 移动                       |
| -cp            | -cp <源路径> <目的路径>                                      | 复制                       |
| -rm            | -rm [-skipTrash] <路径>                                      | 删除文件/空白文件夹        |
| -rmr           | -rmr [-skipTrash] <路径>                                     | 递归删除                   |
| -put           | -put <多个linux上的文件> <hdfs路径>                          | 上传文件                   |
| -copyFromLocal | -copyFromLocal <多个linux上的文件> <hdfs路径>                | 从本地复制                 |
| -moveFromLocal | -moveFromLocal <多个linux上的文件> <hdfs路径>                | 从本地移动                 |
| -getmerge      | -getmerge <源路径> <linux路径>                               | 合并到本地                 |
| -cat           | -cat <hdfs路径>                                              | 查看文件内容               |
| -text          | -text <hdfs路径>                                             | 查看文件内容               |
| -copyToLocal   | -copyToLocal [-ignoreCrc] [-crc] [hdfs源路径] [linux目的路径] | 从本地复制                 |
| -moveToLocal   | -moveToLocal [-crc] <hdfs源路径> <linux目的路径>             | 从本地移动                 |
| -mkdir         | -mkdir <hdfs路径>                                            | 创建空白文件夹             |
| -setrep        | -setrep [-R] [-w] <副本数> <路径>                            | 修改副本数量               |
| -touchz        | -touchz <文件路径>                                           | 创建空白文件               |
| -stat          | -stat [format] <路径>                                        | 显示文件统计信息           |
| -tail          | -tail [-f] <文件>                                            | 查看文件尾部信息           |
| -chmod         | -chmod [-R] <权限模式> [路径]                                | 修改权限                   |
| -chown         | -chown [-R] [属主][:[属组]] 路径                             | 修改属主                   |
| -chgrp         | -chgrp [-R] 属组名称 路径                                    | 修改属组                   |
| -help          | -help [命令选项]                                             | 帮助                       |



**7. Shell常用命令介绍**

**-ls**

使用方法：hadoop fs -ls [-h] [-R] <args>

功能：显示文件、目录信息。

示例：hadoop fs -ls /user/hadoop/file1

**-mkdir**

使用方法：hadoop fs -mkdir [-p] <paths>

功能：在hdfs上创建目录，-p表示会创建路径中的各级父目录。

示例：hadoop fs -mkdir –p /user/hadoop/dir1

**-put**
 使用方法：hadoop fs -put [-f] [-p] [ -|<localsrc1> .. ]. <dst> 

功能：将单个src或多个srcs从本地文件系统复制到目标文件系统。

-p：保留访问和修改时间，所有权和权限。

-f：覆盖目的地（如果已经存在）

示例：hadoop fs -put -f localfile1 localfile2 /user/hadoop/hadoopdir

**-get**

使用方法：hadoop fs -get [-ignorecrc] [-crc] [-p] [-f] <src> <localdst>

-ignorecrc：跳过对下载文件的CRC检查。

-crc：为下载的文件写CRC校验和。

功能：将文件复制到本地文件系统。

示例：hadoop fs -get hdfs://host:port/user/hadoop/file localfile

**-appendToFile** 

使用方法：hadoop fs -appendToFile <localsrc> ... <dst>

功能：追加一个文件到已经存在的文件末尾

示例：hadoop fs -appendToFile localfile  /hadoop/hadoopfile

**-cat**  

使用方法：hadoop fs -cat [-ignoreCrc] URI [URI ...]

功能：显示文件内容到stdout

示例：hadoop fs -cat /hadoop/hadoopfile

**-tail**

使用方法：hadoop fs -tail [-f] URI 

功能：将文件的最后一千字节内容显示到stdout。

-f选项将在文件增长时输出附加数据。

示例：hadoop fs -tail /hadoop/hadoopfile

**-chgrp** 

使用方法：hadoop fs -chgrp [-R] GROUP URI [URI ...]

功能：更改文件组的关联。用户必须是文件的所有者，否则是超级用户。

-R将使改变在目录结构下递归进行。

示例：hadoop fs -chgrp othergroup /hadoop/hadoopfile

**-chmod**

功能：改变文件的权限。使用-R将使改变在目录结构下递归进行。

示例：hadoop fs -chmod 666 /hadoop/hadoopfile

**-chown**

功能：改变文件的拥有者。使用-R将使改变在目录结构下递归进行。

示例：hadoop fs -chown someuser:somegrp  /hadoop/hadoopfile

**-cp**        

功能：从hdfs的一个路径拷贝hdfs的另一个路径

示例： hadoop fs -cp /aaa/jdk.tar.gz /bbb/jdk.tar.gz.2

**-mv**           

功能：在hdfs目录中移动文件

示例： hadoop fs -mv /aaa/jdk.tar.gz /

**-getmerge**   

功能：合并下载多个文件

示例：比如hdfs的目录 /aaa/下有多个文件:log.1, log.2,log.3,...

hadoop fs -getmerge /aaa/log.*  ./log.sum

**-rm**        

功能：删除指定的文件。只删除非空目录和文件。-r 递归删除。

示例：hadoop fs -rm -r /aaa/bbb/

**-df**        

功能：统计文件系统的可用空间信息

示例：hadoop fs -df -h /

**-du** 

功能：显示目录中所有文件大小，当只指定一个文件时，显示此文件的大小。

示例：hadoop fs -du /user/hadoop/dir1

**-setrep**        

功能：改变一个文件的副本系数。-R选项用于递归改变目录下所有文件的副本系数。

示例：hadoop fs -setrep -w 3 -R /user/hadoop/dir1

### 3. 集群角色

**主角色: namenode**

- NameNode内部通过内存和磁盘文件两种方式管理元数据。

- 其中磁盘上的元数据文件包括Fsimage内存元数据镜像文件和edits log（Journal）**编辑日志**
- **NameNode**是Hadoop分布式文件系统的核心,架构中的主角色
- NameNode维护和管理文件系统元数据,包括名称空间目录树结构,文件和块位置的信息,访问权限等信息
- 基于此,NameNode成为了访问HDFS的唯一入口

 ![image-20220913203726913](E:\黑马培训\Hadoop生态圈\assets\image-20220913203726913.png)



**从角色: datanode**

- DataNode是Hadoop HDFS中的从角色，负责**具体的数据块存储**。

- **DataNode的数量决定了HDFS集群的整体数据存储能力**。通过和NameNode配合维护着数据块

![image-20220913203828394](E:\黑马培训\Hadoop生态圈\assets\image-20220913203828394.png)



**主角色辅助角色:  secondarynamenode**

- 除了DataNode和NameNode之外，还有另一个守护进程，它称为secondary NameNode。充当NameNode的辅助节点，但不能替

  代NameNode。

- 当NameNode启动时，NameNode合并Fsimage和edits log文件以还原当前文件系统名称空间。如果edits log过大不利于加载，

  **Secondary NameNode就辅助NameNode从NameNode下载Fsimage文件和edits log文件进行合并**。

![image-20220913204024396](E:\黑马培训\Hadoop生态圈\assets\image-20220913204024396.png)

![image-20220913211948293](E:\黑马培训\Hadoop生态圈\assets\image-20220913211948293.png)



**namenode职责**

- NameNode仅**存储HDFS的元数据**：文件系统中所有文件的目录树，并跟踪整个集群中的文件，不存储实际数据。

- NameNode知道HDFS中任何**给定文件的块列表及其位置**。使用此信息NameNode知道如何从块中构建文件。

- NameNode**不持久化存储每个文件中各个块所在的datanode的位置信息**，这些信息会在系统启动时从DataNode重建。

- NameNode是Hadoop集群中的**单点故障**。

- NameNode所在机器通常会配置有**大量内存（RAM）**

![image-20220913204303327](E:\黑马培训\Hadoop生态圈\assets\image-20220913204303327.png)



**datanode职责**

- DataNode负责最终**数据块block**的存储。是集群的从角色，也称为Slave。

- DataNode启动时，会将自己注册到NameNode**并汇报自己负责持有的块列表**。

- 当某个DataNode关闭时，不会影响数据的可用性。 NameNode将安排由其他DataNode管理的块进行副本复制。

- DataNode所在机器通常配置有大量的硬盘空间，因为实际数据存储在DataNode中

![image-20220913204503668](E:\黑马培训\Hadoop生态圈\assets\image-20220913204503668.png)

> -----------------------------------------------------------------



### 4. HDFS文件上传

> 名称后面写斜杆代表文件下面, 不写斜杠代表文件夹(目录)

数据上传的时候会把数据按128M默认单个block最大的数据大小进行切块(如果不满128M,本身数据多大,那么它的数据块就多大),然后上传

放入datanode之中,上传块中的数据,是通过客户端与datanode节点建立管道通信pipeline,对块中数据进行打包packet(每个包的数据大小

为64k),在管道中一个包.一个包的传输,就好比血管中的血细胞一样,把东西传到指定的位置,也就是把数据传到datanode中,直到一个块的数

据上传完毕

**图解示意**

![image-20220913233237781](E:\黑马培训\Hadoop生态圈\assets\image-20220913233237781.png)

**HDFS写数据流程(java代码实现思路)**

1、HDFS客户端创建FileSystem对象实例DistributedFileSystem， FileSystem封装了与文件系统操作的相关方法。

2、调用DistributedFileSystem对象的create()方法，通过RPC请求NameNode创建文件。NameNode执行各种检查判断：目标文件是否

存在父目录是否存在、客户端是否具有创建该文件的权限。检查通过，NameNode就会为本次请求记下一条记录，返回

FSDataOutputStream输出流对象给客户端用于写数据。

3、客户端通过FSDataOutputStream输出流开始写入数据。

4、客户端写入数据时，将数据分成一个个数据包（**packet默认64k**）,并写入一个内部数据队列（**data queue**）。

有一个内部类做**DataStreamer**，用于请求NameNode挑选出适合存储数据副本的一组DataNode，默认是3副本存储。DataStreamer将

数据包流式传输到**pipeline**的第一个DataNode,该DataNode存储数据包并将它发送到pipeline的第二个DataNode。同样，第二个

DataNode存储数据包并且发送给第三个（也是最后一个）DataNode。

5、OutputStream也维护着一个内部数据包队列来等待DataNode的收到确认回执，称之为确认队列（**ack** **queue**）,收到pipeline中所有DataNode确认信息后，该数据包才会从确认队列删除。

6、客户端完成数据写入后，在FSDataOutputStream输出流上调用close()方法关闭

7、DistributedFileSystem联系NameNode告知其文件写入完成，等待NameNode确认。因为namenode已经知道文件由哪些块组成

（DataStream请求分配数据块），因此仅需等待最小复制块即可成功返回。

最小复制是由参数dfs.namenode.replication.min指定，默认是1.

**上传文件的最终目标与方式的图解**

![4、HDFS上传文件的最终目标及方式](E:\黑马培训\Hadoop生态圈\assets\4、HDFS上传文件的最终目标及方式.png)

**上传文件流程图解**

![hdfs工作机制--上传文件流程--课堂版画图](E:\黑马培训\Hadoop生态圈\assets\hdfs工作机制--上传文件流程--课堂版画图.png)

**写数据(上传文件的流程)文字版**

- 在hdfs客户端敲入命令,上传指定文件到指定目录之下,客户端给NameNode发送请求,请求上传文件,这时候NameNode接收到请求,进

  行各种的校验,判断这个文件是否可以上传(目标目录是否存在,是否有权限能执行此操作),如果不能上传,则返回错误信息,如果可以则告

  诉客户端,现在你可以上传文件了

- 在上传文件的时候,先将待上传文件进行切块,按每块大小最大为128M再上传(为了在datanode中实现数据的分块存储),这时候客户端

  再向Namanode发送请求,这时候请求的信息是  **要上传文件分割后某一块的数据并且在信息中还存在着要在datanode中生成几个副**

  **本的信息**  (默认副本数为3)

- 这时候namenode根据datanode的汇报情况  (汇报情况这个是同2888心跳端口来实现的) 结合副本策略选择三台可以用的datanode

  位置信息返回给客户端,让客户端与这三台datanode创建管道通信连接

- 客户端拿到可以用的三台datanode的位置信息后,与这三台datanode创建管道通信,再在刚在发送指令的块中,打包数据,每个数据包大

  小为64k,通过管道发送到datanode1中存储在指定的块中,这时候datanode1会与客户端进行ack应答响应(客户端发送几个包给

  datanode机器,在检验响应的的时候就会发送多少个应答给datanode),查看数据是否丢失,如有丢失则让客户端补发数据包,如果没有丢

  失,则datanode1知道数据包数据完整,此时它会进行复制,通过管道再把数据给datanode2,流程与上面一致,一直到datanode3这台机器

  上,块数据存储完毕

- 接下来的块数据按照上述的流程继续上传,因为可以的datanode机器的状态会改变,有可能上一个块数据传输完以后,这个机器存储空间

  满了,也有可能datanode机器发生了故障等等,所以后续的块数据的上传,还是得从第三步开始,重新去请求namenode,让namenode返

  回可以用的datanode的位置信息,给客户端创建管道连接,**所以不同块数据的存放位置随时都在变化**

- 最后所有的数据块上传完毕以后,客户端会通知namenode数据已经上传完毕



**为什么采取横着上传呢,而不是竖着?**

> 因为1 --> 2 --> 3是横着它是复制,可以好传,而竖着话就不是复制了,这样比较麻烦不好操作
>
> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**核心概念:  Pipeline管道**

为什么datanode之间采用pipeline线性传输，而不是一次给三个datanode拓扑式传输呢？

因为数据以管道的方式，顺序的沿着一个方向传输，这样能够充分利用每个机器的带宽，避免网络瓶颈和高延迟时的连接，最小化推送所

有数据的延时。

在线性推送模式下，每台机器所有的出口宽带都用于以最快的速度传输数据，而不是在多个接受者之间分配宽带。

![image-20220913214418512](E:\黑马培训\Hadoop生态圈\assets\image-20220913214418512.png)

**核心概念:  ACK应答响应**

ACK (Acknowledge character）即是确认字符，在数据通信中，接收方发给发送方的一种传输类控制字符。表示发来的数据已确认接收

无误。

在HDFS pipeline管道传输数据的过程中，传输的反方向会进行ACK校验，确保数据传输安全。

**注意: ACK应答响应是两两之间的,在一个节点没有接收到数据无误的响应的时候,数据不会继续传输下去**

也就是说客户端会通过pipeline与DN1进行数据传输,DN1接收到数据以后,会向客户端发送ACK应答响应,客户端接收到应答后,检查数据是

否无误,错了的话,会继续通过pipeline向DN1发送数据,如果没错则返回响应给DN1告诉它数据无误,可以继续往下传数据了.

![image-20220913215204690](E:\黑马培训\Hadoop生态圈\assets\image-20220913215204690.png)

**核心概念: 默认3副本存储策略**

- 默认副本存储策略是由BlockPlacementPolicyDefault指定

![image-20220913215317274](E:\黑马培训\Hadoop生态圈\assets\image-20220913215317274.png)

**三副本机制**

- 第一块副本：优先客户端本地，否则随机

- 第二块副本：不同于第一块副本的不同机架。

- 第三块副本：第二块副本相同机架不同机器

![image-20220913215423923](E:\黑马培训\Hadoop生态圈\assets\image-20220913215423923.png)



**我们只有一台电脑  ,也就是说只有一台基架,一台基架上有不同的机器,这里的机器指的就是虚拟机服务器**

简言之: 我们手上有一台电脑,相当于有一台基架,电脑上有三个虚拟机,也就是说这时候我们手上有一台基架,基架上有三台机器

当我们只有一台基架的时候,会发生**策略的降级**   也就是如上图右图所示的单基架

第一个副本的存放规则还是一样,优先存放在我们的本机(本地)

剩下的两个副本,这个时候就是依靠  **随机佛性的原则随便放**



### 5. HDFS文件下载

**HDFS读数据流程(java代码实现思路)**

1、HDFS客户端创建FileSystem对象实例DistributedFileSystem， FileSystem封装了与文件系统操作的相关方法。调用

DistributedFileSystem对象的open()方法来打开希望读取的文件。

2、DistributedFileSystem使用RPC调用namenode来确定**文件中前几个块的块**位置（分批次读取）信息。

对于每个块，namenode返回具有该块所有副本的datanode位置地址列表，并且该地址列表是排序好的，与客户端的网络拓扑距离近的排序靠前。

3、DistributedFileSystem将FSDataInputStream输入流返回到客户端以供其读取数据。

4、客户端在FSDataInputStream输入流上调用read()方法。然后，已存储DataNode地址的InputStream连接到文件中第一个块的最近的

DataNode。数据从DataNode流回客户端，结果客户端可以在流上重复调用read（）。

5、当该块结束时，InputStream将关闭与DataNode的连接，然后寻找下一个块的最佳datanode。这些操作对用户来说是透明的。所以

用户感觉起来它一直在读取一个连续的流。

客户端从流中读取数据时，也会根据需要询问NameNode来检索下一批数据块的DataNode位置信息。

6、一旦客户端完成读取，就对FSDataInputStream调用close()方法。

**HDFS下载文件流程图解示意**

![hdfs工作机制--下载文件流程--课堂版画图](E:\黑马培训\Hadoop生态圈\assets\hdfs工作机制--下载文件流程--课堂版画图.png)

**读取数据就以最近位置来读取, namenode返回位置信息列表的时候会把副本排好序,距离客户端近的在前,状态良好的在前**

在网络中的远近是看之前数据传输的时候经过交换机的个数来决定的,如果两个机器传输数据都不需要经过交换机,那么这两个机器距离就

很近

**文字理解**

- 客户端敲入信息下载文件,向namenode发送请求,下载指定的数据文件,namenode接收到请求,进行判断此文件是否可以下载
- 可以下载的话namenode会分批次返回数据块的位置信息,如果文件数据小的话,就不需要分批次返回位置信息了
- 返回块的位置信息列表是排好序的(距离客户端近的在前,状态良好的在前)
- 接着read读取数据块,把数据块都读取出来
- 客户端接着会在本地将这个文件的所有的块根据编号顺序拼接(追加) 成为文件最终完整的样子

> ------------------------------------------------------------------------------------------

**namenode是单点故障,如果它挂掉了,整个hdfs文件系统都不能运行**

### 6. namenode与datanode之间的通信

**概述**

启动时，DataNode需要向NameNode注册自己并汇报自己持有的数据块信息；

工作时，主从之间有心跳机制，数据块汇报机制；

**DataNode会定期（dfs.heartbeat.interval配置项配置，默认是3秒）向NameNode发送心跳**，如果NameNode长时间没有接受到

DataNode发送的心跳， NameNode就会认为该DataNode失效。

**DataNode会定期向NameNode进行自己持有的数据块信息汇报**，汇报时间间隔取参数dfs.blockreport.intervalMsec,参数未配置的话默

认为6小时。 

![6、nn和dn之间的通信机制--心跳、数据块汇报](E:\黑马培训\Hadoop生态圈\assets\6、nn和dn之间的通信机制--心跳、数据块汇报.png)

### 7. HDFS辅助工具

**1. 跨集群复制数据工具distcp**

- DistCp是Hadoop中的一种工具，在hadoop-tools工程下，作为独立子工程存在。

- 定位用于数据迁移，定期在集群之间和集群内部备份数据。

- 在备份过程中，每次运行DistCp都称为一个备份周期。尽管性能相对较慢，但它的普及程度已经越来越高。

- DistCp底层使用MapReduce在群集之间或并行在同一群集内复制文件。执行复制的MapReduce只有mapper阶段。

![image-20220914010753227](E:\黑马培训\Hadoop生态圈\assets\image-20220914010753227.png)



> 做简单了解即可

**2. 文件归档工具archive**

> 重点了解

- HDFS并不擅长存储小文件，因为每个文件最少一个block，每个block的元数据都会在NameNode占用内存；

- 如果存在大量的小文件，它们会吃掉NameNode节点的大量内存。如下所示，模拟小文件场景：

![image-20220914010953844](E:\黑马培训\Hadoop生态圈\assets\image-20220914010953844.png)

- Hadoop Archives可以有效的处理以上问题，它可以把多个文件归档成为一个文件；

- 归档成一个文件后还可以透明的访问之前的每一个文件。

![image-20220914011227419](E:\黑马培训\Hadoop生态圈\assets\image-20220914011227419.png)

**具体流程**

**1. 创建档案**

```shell
Usage: hadoop archive -archiveName name -p <parent> <src>* <dest>
```

​      -archiveName 指要创建的存档的名称。扩展名应该是*.har。 

​      -p 指定文件档案文件src的相对路径。

比如：-p /foo/bar a/b/c e/f/g，这里的/foo/bar是a/b/c与e/f/g的父路径，所以完整路径为/foo/bar/a/b/c与/foo/bar/e/f/g。

**2. 完整示例**

案例：存档一个目录/smallfile下的所有文件:

```shell
hadoop archive -archiveName test.har -p /smallfile /outputdir
```

这样就会在/outputdir目录下创建一个名为test.har的存档文件。

注意：**Archive归档是通过MapReduce程序完成的，需要启动YARN集群**。

![image-20220914011532257](E:\黑马培训\Hadoop生态圈\assets\image-20220914011532257.png)

**查看归档之后的样子**

```shell
hadoop fs -ls /outputdir/test.har
```

这里可以看到har文件包括：两个索引文件，多个part文件（本例只有一个）以及一个标识成功与否的文件。part文件是多个原文件的集

合， 通过index文件可以去找到原文件。

例如上述的三个小文件1.txt 2.txt 3.txt内容分别为1，2，3。进行archive操作之后，三个小文件就归档到test.har里的part-0一个文件里

![image-20220914011910900](E:\黑马培训\Hadoop生态圈\assets\image-20220914011910900.png)

![image-20220914011925558](E:\黑马培训\Hadoop生态圈\assets\image-20220914011925558.png)



**查看归档之前的样子**

在查看har文件的时候，如果没有指定访问协议，默认使用的就是hdfs://，此时所能看到的就是归档之后的样子。

此外，Archive还提供了自己的har uri访问协议。如果用har uri去访问的话，索引、标识等文件就会隐藏起来，只显示创建档案之前的原文件：

Hadoop Archives的URI是：har://scheme-hostname:port/archivepath/fileinarchive  

scheme-hostname格式为**hdfs-域名端口**

![image-20220914012029007](E:\黑马培训\Hadoop生态圈\assets\image-20220914012029007.png)



**提取档案**

按顺序解压存档（串行）：

```shell
hadoop fs -cp har:///outputdir/test.har/* /smallfile1
```

要并行解压存档，请使用DistCp,对应大的归档文件可以提高效率：

```shell
hadoop distcp har:///outputdir/test.har/* /smallfile2
```

![image-20220914012309949](E:\黑马培训\Hadoop生态圈\assets\image-20220914012309949.png)



**注意事项**

- Hadoop archive是特殊的档案格式。一个Hadoop archive对应一个文件系统目录。archive的扩展名是*.har；
- 创建archives本质是运行一个Map/Reduce任务，所以应该在Hadoop集群上运行创建档案的命令； 
- 创建archive文件要消耗和原文件一样多的硬盘空间；
- archive文件不支持压缩，尽管archive文件看起来像已经被压缩过；
- archive文件一旦创建就无法改变，要修改的话，需要创建新的archive文件。事实上，一般不会再对存档后的文件进行修改，因为它们是定期存档的，比如每周或每日；
- 当创建archive时，源文件不会被更改或删除；

### 8. NameNode元数据管理

**元数据存储图示**

![image-20220914013702572](E:\黑马培训\Hadoop生态圈\assets\image-20220914013702572.png)

内存中的数据是最新的，fsimage持久化镜像中的数据不是最新的，但是编辑日志文件中记录的数据操作是最新的，如果数据丢失，就可以根据编辑日志再进行一遍操作即可，数据就恢复过来了

namenode在哪台机器上运行，数据就存储在哪一台机器，我们这里在

```
/export/data/ 
```

里面有编辑日志的文件，和fsimage持久化镜像文件

**查看编辑日志**

![image-20220914014510685](E:\黑马培训\Hadoop生态圈\assets\image-20220914014510685.png)

使用sz工具将转换后的文件下载下来  会下到C盘的download下

![image-20220914014651663](E:\黑马培训\Hadoop生态圈\assets\image-20220914014651663.png)



**元数据管理相关文件 -- VERSION**

**storageType**

说明这个文件存储的是什么进程的数据结构信息。如果是DataNode节点，storageType=DATA_NODE。

**cTime**

NameNode存储系统创建时间，首次格式化文件系统这个属性是0，当文件系统升级之后，该值会更新到升级之后的时间戳；

**layoutVersion**

HDFS元数据格式的版本。HDFS升级时会进行更新。

![image-20220914212654590](E:\黑马培训\Hadoop生态圈\assets\image-20220914212654590.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**元数据相关文件 -- seen_txid**

包含上一次checkpoint时的最后一个事务ID，这不是NameNode接受的最后一个事务ID。

seen_txid内容不会在每个事务性操作上都更新，只会在checkpoint时更新。

NameNode启动时会检查seen_txid文件，以验证它至少可以加载该数目的事务。如果无法验证加载事务，NameNode将中止启动。

![image-20220914212837262](E:\黑马培训\Hadoop生态圈\assets\image-20220914212837262.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**元数据相关文件 -- fsimage**

元数据镜像文件。每个fsimage文件还有一个对应的.md5文件

其中包含MD5校验和，HDFS使用该文件来防止磁盘损坏文件异常

![image-20220914213119779](E:\黑马培训\Hadoop生态圈\assets\image-20220914213119779.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**元数据相关文件 -- edits log**

已完成且不可修改的编辑日志。这些文件中的每个文件都包含文件名定义的范围内的所有编辑日志事务

在HA高可用性部署中，主备namenode之间可以通过edits log进行数据同步

![image-20220914213251895](E:\黑马培训\Hadoop生态圈\assets\image-20220914213251895.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**fsimage文件内容查看fsimage文件内容查看**

fsimage文件是Hadoop文件系统元数据的一个永久性的检查点，包含Hadoop文件系统中的所有目录和文件idnode的序列化信息；

对于文件来说，包含的信息有修改时间、访问时间、块大小和组成一个文件块信息等；

而对于目录来说，包含的信息主要有修改时间、访问控制权限等信息。

oiv是**offline image viewer**的缩写，可将hdfs fsimage文件的内容转储为人类可读的格式。

常用命令：

```shell
hdfs oiv -i fsimage_0000000000000000050 -p XML -o fsimage.xml
```

转换后的格式:

![image-20220914213453261](E:\黑马培训\Hadoop生态圈\assets\image-20220914213453261.png)



**edits log文件内容查看**

edits log文件存放的是Hadoop文件系统的所有更新操作记录日志。

文件系统客户端执行的所有写操作首先会被记录到edits文件中。

oev是**offline edits viewer**（离线edits查看器）的缩写，该工具不需要hadoop集群处于运行状态。

命令：

```shell
hdfs oev -i edits_0000000000000000011-0000000000000000025 -o edits.xml
```

在输出文件中，每个RECORD记录了一次操作,示例如下：

![image-20220914213600065](E:\黑马培训\Hadoop生态圈\assets\image-20220914213600065.png)

### 9. 详细介绍fsimage与edits

**概念**

1，fsimage文件其实是hadoop文件系统元数据的一个永久性的检查点，**其中包含hadoop文件系统中的所有目录和文件idnode的序列**

**化信息。**

2，edits文件存放的是hadoop文件系统的所有更新操作的路径，文件系统客户端执行的所有写操作首先会被记录到edits文件中。元数据

的介绍：

**元数据的分类**

**按形式分类：**

内存元数据和元数据文件；

它们的存在的位置分别为：内存和磁盘上。

其中

内存元数据主要是hdfs文件目录的管理

元数据文件则用于持久化存储

**按类型分**

元数据主要包括：

1、文件、目录自身的属性信息，例如文件名，目录名，修改信息等。

2、文件记录的信息的存储相关的信息，例如存储块信息，分块情况，副本个数等。

3、记录HDFS的Datanode的信息，用于DataNode的管理。


内存元数据主要fsimage，而通过checkpoint功能备份的也主要是内存的元数据。

fsimage和edits文件都是经过序列化的，在NameNode启动的时候，他会将fsimage文件中的内容加载到内存中，之后再执行edits文件中

的各项操作，使得内存中的元数据和实际的同步，存在内存中的元数据支持客户端的读操作。NameNode起来之后，HDFS的更新操作会

重新写到edits文件中，因为fsimage文件一般都很大（GB级别的很常见），如果所有的更新操作都往fsimage文件中添加，这样会导致系

统运行的十分缓慢。但如果往edits文件里面写就不会这样，因此，客户端对hdfs进行写文件时首先被记录在edits文件中，edits修改时元

数据也会更新，每次hdfs更新时edits先更新后客户端才会看到最新信息。如果一个文件比较大，使得写操作需要向多台机器进行操作，

只有所有的操作都执行完成后，写操作才会返回成功，这样的好处是任何的操作都不会因为机器的故障而导致元数据的不同步。fsimage

包含hadoop文件系统中的所有目录和文件idnode的序列化信息，对于文件来说，包含的信息有修改时间，访问时间，块大小和组成一个

文件块信息等；对于目录来说，包含的信息主要有修改时间，访问控制权限等信息。**fsimage****并不包含DataNode的信息，而是包含

DataNode上块的映射信息**，并放到内存中，当一个新的DataNode加入到集群中，DataNode都会向NameNode提供块的信息，而

NameNode会定期的索取块的信息，以使得NameNode拥有最新的块映射。**因fsimage包括Hadoop文件系统中的所有目录和文件**

**idnode的序列化信息(就是映射)，所以如果fsimage丢失或者损坏了**，那么即使DataNode上有块的数据，但是我们没有文件到块的映射

关系，我们也无法用DataNode上的数据！所以定期及时的备份fsimage和edits文件非常重要！文件系统客户端执行的所有写操作首先会

被记录到edits文件中，长此下去，edits会非常的大，而NameNode在重启的时候需要执行edits文件中的各项操作，由此会导致

NameNode启动的时间会很长，由此，定期地合并fsimage和edits文件的内容，然后清空edits文件是非常重要的。 完成合并的是

secondaryNamenode，会请求namenode停止使用edits，暂时将新写操作放入一个新的文件中（edits.new）。secondarynamenode

将namenode中通过http get获得edits，因为要和fsimage合并，所以也是通过http get的方式把fsimage加载到内存，然后执行具体对文

件系统的操作，与fsimage合并，生成新的fsimage，然后把fsimage发送到namenode，通过http post的方式。namenode从

secondarynamenode获得了fsimage后会把原有的fsimage替换为新的fsimage，把edits.new变成edits。同时会更新fstime。

secondarynamenode在合并edits和fsimage时需要消耗的内存和namenode差不多，所以一般把namenode和secondarynamenode放

在不同的机器上。

```
fs.checkpoint.period:默认是一个小时

fs.checkpoint.size:edits达到一定大小时也会出发合并。
```

————————————————

### 10. SNN的作用

> 是namenode的辅助角色,而不是第二老大,要成为第二老大的前提是老大挂掉,他能马上来接替老大的工作,但是事实上证明这是不可行的

**HDFS集群运行久了以后,namenode会遇到的问题**

![image-20220914215031318](E:\黑马培训\Hadoop生态圈\assets\image-20220914215031318.png)

**这时候就要它的助手secondarynamenode来为它解决这些问题了**



#### 1. SNN职责概述

- 因此为了克服上述问题，需要一个易于管理的机制来帮助我们减小edit logs文件的大小和得到一个最新的fsimage文件，这样也会减

  小在NameNode上的压力。

- SecondaryNameNode就是来帮助解决上述问题的，**它的职责是合并NameNode的edit logs到fsimage文件中**。因此常把

  secondarynamenode称之为主角色的辅助角色，辅助NameNode做一些事。



#### 2. SNN  Checkpoint -- 概述

**Checkpoint核心是把fsimage与edits log合并以生成新的fsimage的过程**

**结果：**fsimage版本不断更新不会太旧、edits log文件不会太大。

Secondary Namenode每隔一段时间会检查Namenode上的fsimage和edits文件是否需要合并，如触发设置的条件就开始下载最新的

fsimage和所有的edits文件到本地，并加载到内存中进行合并，然后将合并之后获得的新的fsimage上传到Namenode。checkpoint操作

的触发条件主要配置参数：

```shell
dfs.namenode.checkpoint.check.period=60 #检查触发条件是否满足的频率，单位秒dfs.namenode.checkpoint.dir=file://${hadoop.tmp.dir}/dfs/namesecondarydfs.namenode.checkpoint.edits.dir=${dfs.namenode.checkpoint.dir}
#以上两个参数做checkpoint操作时，secondary namenode的本地工作目录，主要处理fsimage和edits文件的dfs.namenode.checkpoint.max-retries=3 #最大重试次数dfs.namenode.checkpoint.period=3600 #两次checkpoint之间的时间间隔3600秒dfs.namenode.checkpoint.txns=1000000 #两次checkpoint之间最大的操作记录
```

**配置触发机制在的文件**

core-site.xml

```shell
dfs.namenode.checkpoint.period=3600 //两次连续的checkpoint之间的时间间隔。默认1小时

dfs.namenode.checkpoint.txns=1000000 //最大没有执行checkpoint事务的数量，满足将强制执行紧急checkpoint，即使尚未达到检查点周期。默认100万事务数量。
```

**checkpoint作用**

**1. 加快Namenode**

启动Namenode启动时，会合并磁盘上的fsimage文件和edits文件，得到完整的元数据信息，但如果fsimage和edits文件非常大，这个合

并过程就会非常慢，导致HDFS长时间处于安全模式中而无法正常提供服务。SecondaryNamenode的checkpoint机制可以缓解这一问题

**2. 数据恢复**

Namenode和SecondaryNamenode的工作目录存储结构完全相同，当Namenode故障退出需要重新恢复时，可以从

SecondaryNamenode的工作目录中将fsimage拷贝到Namenode的工作目录，以恢复Namenode的元数据。但是SecondaryNamenode

最后一次合并之后的更新操作的元数据将会丢失，最好Namenode元数据的文件夹放在多个磁盘上面进行冗余，降低数据丢失的可能性。

**注意事项：**

\1. SecondaryNamenode只有在第一次进行元数据合并时需要从Namenode下载fsimage到本地。SecondaryNamenode在第一次元数

据合并完成并上传到Namenode后，**所持有的fsimage已是最新的fsimage，无需再从Namenode处获取，而只需要获取edits文件即**

**可**。

\2. SecondaryNamenode从Namenode上将要合并的edits和fsimage拷贝到自己当前服务器上，然后将fsimage和edits反序列化到

SecondaryNamenode的内存中，进行计算合并。因此一般需要把Namenode和SecondaryNamenode分别部署到不同的机器上面，且

SecondaryNamenode服务器配置要求一般不低于Namenode。

\3. SecondaryNamenode不是充当Namenode的“备服务器”，它的主要作用是进行元数据的checkpoint

> ------------------------------------------------------------------------------------------------------------------------------------------------------



#### 3. NameNode元数据的恢复

**1. NameNode存储多目录**

namenode元数据存储目录由参数：dfs.namenode.name.dir指定。

**dfs.namenode.name.dir属性可以配置多个目录**，各个目录存储的文件结构和内容都完全一样，相当于备份，这样做的好处是当其中一

个目录损坏了，也不会影响到hadoop的元数据，特别是当其中一个目录是NFS（网络文件系统Network File System，NFS）之上，即使

你这台机器损坏了，元数据也得到保存。



**2. 从SecondaryNameNode恢复**

SecondaryNameNode在checkpoint的时候会将fsimage和edits log下载到自己的本机上本地存储目录下。并且在checkpoint之后也不会

进行删除。

如果NameNode中的fsimage真的出问题了，还是可以用SecondaryNamenode中的fsimage替换一下NameNode上的fsimage，虽然已

经不是最新的fsimage，但是我们可以将损失减小到最少！

![image-20220915001507248](E:\黑马培训\Hadoop生态圈\assets\image-20220915001507248.png)







#### 4. secondarynamenode '合并' 元数据文件图解

![1、snn合并元数据文件的过程--checkpoint](E:\黑马培训\Hadoop生态圈\assets\1、snn合并元数据文件的过程--checkpoint.png)

> SNN合并元数据文件的过程用专业话的角度来讲即是SNN Checkpoint
>
> 合并不是单纯的文件追加合并,而是一个镜像文件,基于本身的数据条件,依靠编辑日志的操作信息,去操作一遍,让自身的镜像文件变新

**为什么要去整合edits.log 与 fsimage文件呢?**

假设我们机器是在2022.09.13开启的,操作人员通过客户端使用HDFS文件系统,namenode记录下元数据信息,这时候会生成一个

2022.09.13号的fsimage文件,下一次生成还需要很久,但是edits编辑日志会随着使用时间的推移不断地更新这期间的操作记录,也就是说它

里面的信息是与时俱进的 (2022.09.13 ~ now)  ,数据操作记录越来越新,伴随而来的是edits编辑日志的不断变大,此时我们的fsimage文件会

越来越旧,为了解决这一问题,我们就得把新的编辑日志与旧的fsimage文件进行整合,但是namenode要负责的事情很多,忙不过来,就算是驴

也要休息一下,这时候它就请了一个帮手snn来帮助它干活,这样即提升了工作效率,也不耽误自己的工作,两全其美!!!

**合并的具体步骤**

> 背景:  fsimage文件随着namenode工作时间的推移越来越旧,edits日志文件记录的操作信息越来越多,伴随着文件与时俱进的特征而
>
> 来的是文件大小不断地变大,这时候NN有点被撑到了,性能可能被影响,这时候NN 通知 SNN(健胃消食片)快出来工作了

- namenode关闭edits日志文件,开启一个新的日志文件edits.new **(之前的记录信息不包括,这是一个全新的开始)** 来记录合并文件期间

  的操作信息,保证操作信息记录的工作流畅地进行下去

- secondarynamenode去namenode上拉取 (复制) (HTTP GET 方式)edits日志信息文件与fsimage文件,在secondarynamenode的内存中合并文件,因为在内存

  中**合并运行速度快**

- 将fsimage镜像文件加载到SNN的内存中,这时候镜像中的是数据都恢复过来了,可是这个里面记录的信息是旧的,还不是此刻最新的,这

  时候SNN去读取edits日志文件,查看在这镜像之后所有进行的操作信息,再在SNN上操作一遍,这时候就获得了最新的镜像文件 --- 这也

  就是我们所说的文件合并

- 最后在SNN中对镜像文件进行一个校验,没问题以后再把他推送(复制)给namenode,这时候namenode中的fsimage文件就变成最新的镜像文

  件了

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

> 执行元数据文件合并的前提是触发了checkpoint机制, checkpoint会触发SNN的工作,checkpoint什么时候运行可以设置

元数据的checkpoint

**问题: fsimage中没有记录块所对应的datanode信息为什么?**

在fsimage中，并没有记录每一个block对应到哪几个datanodes的对应表信息(具体信息没有存储,但是记录了block与datanode的映射关

系)，而只是存储了所有的关于namespace的相关信息还有inode的序列化信息。**而真正每个block对应到datanodes列表的信息在**

**hadoop中并没有进行持久化存储**，而是在所有datanode启动时，每个datanode对本地磁盘进行扫描，将本datanode上保存的block信

息汇报给namenode，namenode在接收到每个datanode的块信息汇报后，将接收到的块信息，以及其所在的datanode信息等保存在内

存中。（HDFS就是通过这种块信息汇报的方式来完成 block -> datanodes list的对应表构建。Datanode向namenode汇报块信息的过程

叫做blockReport，而namenode将block -> datanodes list的对应表信息保存在一个叫BlocksMap的数据结构中。）


## 5. MapperReduce -- 分布式计算框架

> 分布式计算框架 -- 主体思想分而治之

MAP -- 映射

Reduce -- 聚合减少

**如何理解MapperReduce是分布式的程序？**

![2、如何理解MapReduce是分布式的程序](E:\黑马培训\Hadoop生态圈\assets\2、如何理解MapReduce是分布式的程序.png)

### 1. 理解MapReduce思想

MapReduce的思想核心是“**先分再合，分而治之**”。

所谓“分而治之”就是把一个复杂的问题，按照一定的“分解”方法分为等价的规模较小的若干部分，然后逐个解决，分别找出各部分的结

果，然后把各部分的结果组成整个问题的最终结果。

这种思想来源于日常生活与工作时的经验。即使是发布过论文实现分布式计算的谷歌也只是实现了这种思想，而不是自己原创

Map负责“拆分”：即把复杂的任务分解为若干个“简单的子任务”来并行处理。可以进行拆分的前提是这些小任务可以并行计算，彼此间几

乎没有依赖关系。

Reduce负责“合并”：即对map阶段的结果进行全局汇总。

这两个阶段合起来正是MapReduce思想的体现。

![image-20220915003938266](E:\黑马培训\Hadoop生态圈\assets\image-20220915003938266.png)



### 2. 设计构思

1.对相互间不具有计算依赖关系的大数据计算任务，实现并行最自然的办法就是采取MapReduce分而治之的策略。

2.首先Map阶段进行拆分，把大数据拆分成若干份小数据，多个程序同时并行计算产生中间结果；然后是Reduce聚合阶段，通过程序对

并行的结果进行最终的汇总计算，得出最终的结果。

3.不可拆分的计算任务或相互间有依赖关系的数据无法进行并行计算！

![image-20220915004214822](E:\黑马培训\Hadoop生态圈\assets\image-20220915004214822.png)

**构建抽象编程模型**

MapReduce借鉴了函数式语言中的思想，用**Map**和**Reduce**两个函数提供了高层的并行编程抽象模型。

**map: 对一组数据元素进行某种重复式的处理**

**reduce: 对Map的中间结果进行某种进一步的结果整理 **

![image-20220915004332885](E:\黑马培训\Hadoop生态圈\assets\image-20220915004332885.png)

MapReduce中定义了如下的Map和Reduce两个抽象的编程接口，由用户去编程实现:

```
map: (k1; v1) → (k2; v2)

reduce: (k2; [v2]) → (k3; v3)
```

通过以上两个编程接口，大家可以看出MapReduce处理的数据类型是**<key,value>键值对**。



### 3. 分布式计算概念

分布式计算是一种计算方法，和集中式计算是相对的。

随着计算技术的发展，有些应用需要非常巨大的计算能力才能完成，如果采用集中式计算，需要耗费相当长的时间来完成。

分布式计算**将该应用分解成许多小的部分，分配给多台计算机进行处理**。这样可以节约整体计算时间，大大提高计算效率。

![image-20220915004559546](E:\黑马培训\Hadoop生态圈\assets\image-20220915004559546.png)



### 4. 产生背景

论文中谷歌把分布式数据处理的过程拆分为Map和Reduce两个操作函数（受到函数式编程语言的启发），随后被Apache Hadoop参考并

作为开源版本提供支持，叫做Hadoop MapReduce。

它的出现解决了人们在最初面临海量数据束手无策的问题，同时它还是**易于使用和高度可扩展**的，使得开发者无需关系分布式系统底层的

复杂性即可很容易的编写分布式数据处理程序，并在成千上万台普通的商用服务器中运行



### 5. MapReduce特点

- **易于编程**

Mapreduce框架提供了用于二次开发的接口；简单地实现一些接口，就可以完成一个分布式程序。任务计算交给计算框架去处理，将分

布式程序部署到hadoop集群上运行，集群节点可以扩展到成百上千个等。

- **良好的扩展性**

当计算机资源不能得到满足的时候，可以通过增加机器来扩展它的计算能力。基于MapReduce的分布式计算得特点可以随节点数目增长

保持近似于线性的增长，这个特点是MapReduce处理海量数据的关键，通过将计算节点增至几百或者几千可以很容易地处理数百TB甚至

PB级别的离线数据。

- **高容错性**

Hadoop集群是分布式搭建和部署得，任何单一机器节点宕机了，它可以把上面的计算任务转移到另一个节点上运行，不影响整个作业任

务得完成，过程完全是由Hadoop内部完成的。

- **适合海量数据的离线处理**

可以处理GB、TB和PB级别得数据量



### 6. MapReduce局限性

MapReduce虽然有很多的优势，也有相对得局限性，局限性不代表不能做，而是在有些场景下实现的效果比较差，并不适合用

MapReduce来处理，主要表现在以下结果方面：

- **实时计算性能差**

MapReduce主要应用于离线作业，无法作到秒级或者是亚秒级得数据响应。

- **不能进行流式计算**

流式计算特点是数据是源源不断得计算，并且数据是动态的；而MapReduce作为一个离线计算框架，主要是针对静态数据集得，数据是

不能动态变化得。



### 7. MapReduce实例进程

一个完整的MapReduce程序在分布式运行时有三类

- MRAppMaster：负责整个程序的过程调度及状态协调
- MapTask：负责map阶段的整个数据处理流程
- ReduceTask：负责reduce阶段的整个数据处理流程



### 8. 阶段组成

- MapReduce编程模型只能包含一个Map阶段和一个Reduce阶段

- 如果用户的业务逻辑非常复杂，那就只能多个MapReduce程序串行运行。

![image-20220915005414481](E:\黑马培训\Hadoop生态圈\assets\image-20220915005414481.png)



- 注意：整个MapReduce程序中，数据都是以kv键值对的形式流转的；

- 在实际编程解决各种业务问题中，需要考虑每个阶段的输入输出kv分别是什么；

- MapReduce内置了很多默认属性，比如排序、分组等，都和数据的k有关，所以说kv的类型数据确定及其重要的。

![image-20220915005532410](E:\黑马培训\Hadoop生态圈\assets\image-20220915005532410.png)

### 9. 执行流程

- 虽然MapReduce从外表看起来就两个阶段Map和Reduce,但是内部却包含了很多默认组件和默认的行为。

- 组件：读取数据组件InputFormat、输出数据组件OutputFormat；

- 行为：排序（key的字典序排序）、分组（reduce阶段key相同的分为一组，一组调用一次reduce处理）等等；

![image-20220915005645945](E:\黑马培训\Hadoop生态圈\assets\image-20220915005645945.png)

### 10. MR编程

**概述**

一个最终完整版本的MR程序需要**用户编写的代码（map中的业务逻辑，reduce后需要的结果）**和**Hadoop自己实现的代码（框**

**架）**整合在一起才可以；

其中用户负责map、reduce两个阶段的业务问题，Hadoop负责底层所有的技术问题；

由于MapReduce计算引擎天生的弊端（慢），当下企业中直接使用率已经日薄西山了，所以在企业中工作很少涉及到MapReduce直接编

程，但是某些软件的背后还依赖MapReduce引擎。

可以通过官方提供的示例来感受MapReduce及其内部执行流程，因为后续的新的计算引擎比如Spark，当中就有MapReduce深深的影子

存在。

**使用示例**

![image-20220915005958323](E:\黑马培训\Hadoop生态圈\assets\image-20220915005958323.png)

![image-20220915010040516](E:\黑马培训\Hadoop生态圈\assets\image-20220915010040516.png)

**使用python编程完成mr的流程**

![image-20220915010225555](E:\黑马培训\Hadoop生态圈\assets\image-20220915010225555.png)

![image-20220915010245550](E:\黑马培训\Hadoop生态圈\assets\image-20220915010245550.png)



### 11. 提交脚本文件到linux上运行 （重点掌握）

**程序执行方式**

方式1：本地测试Python脚本逻辑是否正确。

方式2：使用hadoop streaming提交Python脚本集群运行。

注意：不管哪种方式执行，都需要提前在Centos系统上安装好Python3。

- 方式1：本地测试Python脚本逻辑是否正确。

![image-20220915010743956](E:\黑马培训\Hadoop生态圈\assets\image-20220915010743956.png)



- 方式2：使用hadoop streaming提交Python脚本集群运行。

![image-20220915010812064](E:\黑马培训\Hadoop生态圈\assets\image-20220915010812064.png)



- 方式2：使用hadoop streaming提交Python脚本集群运行。

![image-20220915010928379](E:\黑马培训\Hadoop生态圈\assets\image-20220915010928379.png)

![image-20220915010938642](E:\黑马培训\Hadoop生态圈\assets\image-20220915010938642.png)



### 12. MapperReduce详细工作机制

MapReduce计算模型主要由三个阶段构成：Map、Shuffle、Reduce

Map是映射，负责数据的处理、分发，将原始数据转化为键值对；Reduce是合并，将具有相同key值的value进行

处理后再输出新的键值对作为最终结果。**为了让Reduce可以并行处理Map的结果，必须对Map的输出进行一定**

**的排序与分割，然后再交给对应的Reduce**，而这个将Map输出进一步整理并交给Reduce的过程就是

Shuffle。整个MR的大致过程如下所示。

![image-20220920100557596](E:\黑马培训\Hadoop生态圈\assets\image-20220920100557596.png)

> 一个maptask任务处理一个mapper,多个maptask任务处理的map结果交给一个或多个reducetask任务处理,默认情况都是一个reducetask任务,如果用户有需求,可以指定多个reducetask任务来获得不同的处理结果



**KV键值对**

- MapReduce框架运转在<key,value>键值对上，也就是说，框架把作业的输入看成是一组<key,value>键值对,

- 同样也产生一组<key,value>键值对作为作业的输出，这两组键值对可能是不同的。

![image-20220915012330676](E:\黑马培训\Hadoop生态圈\assets\image-20220915012330676.png)

![image-20220915012339200](E:\黑马培训\Hadoop生态圈\assets\image-20220915012339200.png)



**输入梳理**

- 默认读取数据的组件叫做**Text****InputFormat**。

- 关于输入路径：

\- 如果指向的是一个文件，处理该文件；

\- 如果指向的是一个文件夹（目录），就处理该目录所有的文件，把所有文件当成整体来处理。



**输出梳理**

- 默认输出数据的组件叫做**TextOutputFormat**。

- 输出路径不能提前存在，必须是一个不存在的目录，否则执行报错，因为底层会对输出路径进行检测判断。

- 可以在程序中编写代码进行判断，如果输出路径存在，先删除，再提交执行。

![image-20220915012513000](E:\黑马培训\Hadoop生态圈\assets\image-20220915012513000.png)



**Map阶段执行过程**

![image-20220915012648926](E:\黑马培训\Hadoop生态圈\assets\image-20220915012648926.png)

![image-20220915012706694](E:\黑马培训\Hadoop生态圈\assets\image-20220915012706694.png)

> 分区操作能开启的时候，要计算的结果跟数据的个数，顺序等无关，才能使用这个分区操作
>



**Reduce阶段执行过程**

![image-20220915012745414](E:\黑马培训\Hadoop生态圈\assets\image-20220915012745414.png)



**MapReduce Shuffle机制**

**大数据主流计算引擎的关系**

![image-20220916210807256](E:\黑马培训\Hadoop生态圈\assets\image-20220916210807256.png)

**概念**

![image-20220915012837068](E:\黑马培训\Hadoop生态圈\assets\image-20220915012837068.png)





![image-20220915012900210](E:\黑马培训\Hadoop生态圈\assets\image-20220915012900210.png)



![image-20220915012925136](E:\黑马培训\Hadoop生态圈\assets\image-20220915012925136.png)

**MapperReduce的shuffle阶段详细图解**

![2、MapReduce的shuffle机制](E:\黑马培训\Hadoop生态圈\assets\2、MapReduce的shuffle机制.png)

**partition by 分区详细图解**

![3、MapReduce中partition分区的概念](E:\黑马培训\Hadoop生态圈\assets\3、MapReduce中partition分区的概念.png)

> 任何数对2取余数 余数范围是0,1  任何数对3取余 余数范围是0,1,2 ==> 任何数对分区数取余  余数范围是0 ~ [分区数 - 1 ]
>
> 如果默认分区规则不满足,则可以自己去设置分区规则 比如说男的返回1 女的返回0
>
> 不能保证数据的统一分配,但是能保证map输出相同的key会来到同一个分区

**Combiner归约详细图解**

![4、Combiner的定义概念](E:\黑马培训\Hadoop生态圈\assets\4、Combiner的定义概念.png)

![5、Combiner的功能和注意事项](E:\黑马培训\Hadoop生态圈\assets\5、Combiner的功能和注意事项.png)

**归约现象展示**

![6、MapReduce中Combiner归约的现象](E:\黑马培训\Hadoop生态圈\assets\6、MapReduce中Combiner归约的现象.png)





![image-20220915013003691](E:\黑马培训\Hadoop生态圈\assets\image-20220915013003691.png)



**详细工作机制图解**

<img src="E:\黑马培训\Hadoop生态圈\assets\3、MapReduce执行流程图--课堂画图板.png" alt="3、MapReduce执行流程图--课堂画图板" style="zoom: 200%;" />

**详细流程图**

![image-20220920100319633](E:\黑马培训\Hadoop生态圈\assets\image-20220920100319633.png)

**MapperReduce动图示例**

![20200718161759560](E:\黑马培训\Hadoop生态圈\assets\20200718161759560.gif)

![20200718162126287](E:\黑马培训\Hadoop生态圈\assets\20200718162126287.gif)

**Map阶段详解示意图**

![20200718162254803](E:\黑马培训\Hadoop生态圈\assets\20200718162254803.png)

**Reduce阶段详解示意图**

![20200718162308573](E:\黑马培训\Hadoop生态圈\assets\20200718162308573.png)

**文字理解**

```
MapperReduce程序首先执行的是mapper阶段
mapper阶段运行的任务叫做maptask,maptask任务是并行的


首先思考的是一个mr程序究竟启动几个maptask任务?

不管你怎么分,数据都不能重复,不能遗漏

mr会配合hdfs一起使用,当文件存放在hdfs上以后,就会被分块默认最大为128M

mr内部会对待处理目录下的文件逐个遍历,以切片大小形成规划文件

其中  切片大小 split size == block size == 128M
注意  split size 默认大小 等于 block size 默认大小
         block size 默认大小 等于128M
         这是个间接的过程,而不是一个直接的过程  == > 不能直接说split size         默认大小等于128M
         根据hadoop版本的不同它块的默认大小也不同

---  为什么会叫逻辑切片呢? 因为hdfs已经把数据分块给分好了

例子:

待处理的数据目录下
/wordcount/input
1.txt 200M
2.txt 100M

第一步

根据上面分析的,我们可以推断出两个数据分别的切片个数

split0   1.txt  0  ~ 128M
split1   1.txt  128 ~ 200M
split2   2.txt  0 ~ 100M


最终结论一个block对应着一个逻辑切片启动一个maptask任务

决定一个mr程序中启动多少个maptask因素有

1. 文件大小

2. 文件个数

3. split size = block size  --> block size默认的值可以被修改的 --> 一修改分块的个数也变了,逻辑切片的个数也改变

联系: mapperReduce为什么讨厌小文件 --> 在这里可以看出 -- >就算文件再小他也是个小文件,也要启动一个maptask任务来处理,这样非常浪费资源,处理的也慢

第二步

根据逻辑切片的个数,我们会启动相同个数的maptask任务,并且给这三个任务加上编号,序号从开始, 接下来就是来处理这些数据


三个切片没有区别,都是处理一部分数据,而且还能并行的处理数据


mr程序的灵活性就在这里体现出来了,不管你给他处理的数据是多么大,还是多么少文件数量是多的还是少的,三个因素共同作用来决定开启maptask任务的数量


---- ------------------------------------  任务预处理阶段就结束了

接下来依靠TextInputFormat组件去HDFS上面读取数据,这时候TextInputFormat组件相当于客户端,去HDFS服务端上读取数据

--- 它是默认去读取数据的组件,也可以改成其他的方式去读

--- TextInputFormat组件是一行一行的读取数据   返回的内容是key:value键值对

读一行数据处理一行数据,即读完一行就传到maptask任务里面进行运算,跑maptask

-- 处理数据都是在内存中处理的,存数据在磁盘中

要是读一行数据就进行一次io操作,把数据写入磁盘中,频繁的进行io操作.会把磁盘干蹦了,影响磁盘的性能

为了避免出现这个问题,频繁地io操作把磁盘干蹦,maptask专门在内存中开辟了一块空间Memory Buffer内存缓冲区,用来暂时存放数据,缓冲区默认大小100M ,里面有个阀值内存缓冲区的0.8,在这里是80M,到达80M,内存缓冲区内的数据就开始往磁盘中写入(溢写)   剩下的20M空间继续接收计算完后传入的数据

在往磁盘中写入数据的过程上,还有一个归约的操作,但是默认是不开启的,归约会合并相同键的数据,进行合并,简化数据量,但是能进行这个操作是有要求的,计算结果要与数据的数量,顺序无关才可以使用,

这个内存缓冲区也叫环形的内存缓冲区 ==> 意思是这个缓冲区比如说是从左往右进度条慢慢增加到80,达到80以后就开始把80这个区域内的数据写入磁盘,渐渐释放内存,与此同时20M那个内存空间内会接收数据,往左,写到80M,
然后将数据写入磁盘,就这样来回循环.  鉴于此现象也就有了环形内存缓冲区的名称,它进行了局部的整合,减少了shuffle的数据量,提高了计算的效率


内存到磁盘的过程叫溢出,溢出的时候有个动作,对数据进行排序,根据key的字典序进行排序

将磁盘上多次溢出写入的文件合并成一个最终的文件,放在自己机器的本地临时目录上,静静的等待着reducetask来拉取数据,


map阶段结束了




key : 每行数据的偏移量offset
value: 这一行的文本内容

小知识点:  
例子  hello 这时候光标在hello的字母h前面  这时候只要offset=1 光标向后移动一位,就能读出h

光标要是移动到/r/n后面以后,再移动就到下一个行上了
-- 解析 \r是回车使光标移动到行首,本行的行首
           \n 是换行使光标下移一格,到了下一行的行首

不同的系统换行符号不一样
windows是\r\n
unix和linux都是\n
mac上是\r

一个程序输出多大的文件不仅与输入文件大小有关,还与程序中间对这个文件做了什么有关,   输出多大数据文件跟输入没太大关系,跟程序中间对这个文件做了什么有直接关系,所以溢出只有至少,没有最多

问题: 溢写会发生几次
至少一次  多了有几次不知道

第二个阶段登场的task叫做reducetask



reduceTask将启动几个? == > 问题又名reducetask并行度机制

如果用户不干预,默认下只有一个reducetask,并行度为1 
如果有需求, 用户可以启动多个reducetask

拉取数据有个好处,我

map处理完的数据,不是map发送给reduce的,是reduce自己过来拉取数据,
这个拉取底层就是一个http协议的请求,第一个线程会去逐个问处理完了没,然后记录下来处理完的maptask,接着会启动一个线程fetcch去maptask拉取属于自己的数据,放在reducetask的内存中,等待运算

1. merge合并 拉取过来的数据合并成一个整体

2. 接着根据key的字典序进行排序

3.根据key,把相同的key分为一组,进行分组操作

4.分完组以后,每组会形成新的键值对,去调用reduce阶段的代码进行业务处理 

键还是不变, 值是一个迭代器装着每组以前的value

该求和求和,求最大求最大等

接下来就是输出了 ,调用TextOutPutFormat类进行输出,这个是默认输出组件

它会将数据输出到HDFS指定的目录下,而且要求输出目录不能提前存在

目录下 会有part-r-00000这一类的文件,整个计算过程完成后还有一个
_SUCESS_文件

排序排到前面的数据会先处理,先出来



  

如果是推模式,就是map处理好数据主动发给reducetask,reduce此时要是还没准备好的话,那么数据就处理不了,拉模式有个好处,我按需所得,现在我能处理多少条数据,那么我就去拉取多少数据

fink spark kafaka 几乎都是拉取模式,就是我能处理多少条数据就去拿多少数据

去mapperreduce那里找一下[配置]

```



## 6. YARN -- 另一种资源协调者

> YARN有三大组件 RM NM AM 除了AM以外,它们本身在YARN中一直都存在着,AM组件的创建时接收到了ASM的指令才会创建
>
> 与YARN一样的是,在RM中也有本身就存在的两大组件,分别为 ASM 与 RS (ResourceScheduler)在RM中一直存在着

### 1. 简介

- Apache Hadoop YARN （Yet Another Resource Negotiator，另一种资源协调者）是一种新的Hadoop资源管理器。

- YARN是一个**通用资源管理系统和调度平台**，可为上层应用提供统一的资源管理和调度。

- 它的引入为集群在利用率、资源统一管理和数据共享等方面带来了巨大好处。

- 可以把Hadoop YARN理解为相当于一个**分布式的操作系统平台**，而MapReduce等计算程序则相当于运行于操作系统之上的应用程

  序，YARN为这些程序提供运算所需的资源（内存、CPU等）。

- Hadoop能有今天这个地位，YARN可以说是功不可没。因为有了YARN ，更多计算框架可以接入到 HDFS中，而不单单是 

  MapReduce，正式因为YARN的包容，使得其他计算框架能专注于计算性能的提升。

- HDFS可能不是最优秀的大数据存储系统，但却是应用最广泛的大数据存储系统， YARN功不可没。

**如何理解通用资源管理系统和调度平台？**

- 资源管理系统：集群的硬件资源，和程序运行相关，比如内存、CPU等。

- 调度平台：多个程序同时申请计算资源如何分配，调度的规则（算法）。

- 通用：不仅仅支持MapReduce程序，理论上支持各种计算程序。YARN不关心你干什么，只关心你要资源，在有的情况下给你，用完

  之后还我。

### 2. YARN的集群角色

- Apache Hadoop YARN是一个标准的Master/Slave集群（主从架构）。

- 其中**ResourceManager**（RM） 为Master， **NodeManager**（NM） 为 Slave。

- 常见的是一主多从集群，也可以搭建RM的HA高可用集群。

![7、yarn角色架构](E:\黑马培训\Hadoop生态圈\assets\7、yarn角色架构.png)

#### 1. 三大组件图示

![image-20220917002722799](E:\黑马培训\Hadoop生态圈\assets\image-20220917002722799.png)



#### 2. 组件具体解析

**ResourceManager (RM)**    

- RM是YARN中的主角色，决定系统中所有应用程序之间资源分配的最终权限，即最终仲裁者。

- RM接收用户的作业提交，并通过NodeManager分配、管理各个机器上的计算资源。资源以Container形式给与。

- 此外，RM内部的两大组件   一个是可插拔组件-**scheduler**，和另一个组件**ASM**。

  - Scheduler:这个组件完全是插拔式的，用户可以根据自己的需求实现不同的调度器，目前YARN提供了FIFO、容量以及公平调度

  器。这个组件的**唯一功能**就是**给提交到集群的应用程序分配资源**，**并且对可用的资源和运行的队列进行限制**。Scheduler并不对作业

  进行监控；

  - ApplicationsManager (AsM):  这个组件**用于管理整个集群应用程序的application masters**，**负责接收应用程序的提交**；为

  **application master启动提供资源**；**监控应用程序的运行进度以及在应用程序出现故障时重启它**

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**NodeManager (NM）**

**概述**

> NM是YARN中的从角色，一台机器上一个，负责管理本机器上的计算资源。
>
> NM根据RM命令，启动Container容器、监视容器的资源使用情况。并且向RM主角色汇报资源使用情况。

**详情解释**

NM是YARN中每个节点上的代理，它管理Hadoop集群中单个计算节点，根据相关的设置来启动容器的。NodeManager会定期向

ResourceManager发送心跳信息来更新其健康状态。同时其也会监督Container的生命周期管理，监控每个Container的资源使用

（内存、CPU等）情况，追踪节点健康状况，管理日志和不同应用程序用到的附属服务（auxiliary service）。



**ApplicationMaster  (AM）**

**概述**

> 用户提交的每个应用程序均包含一个AM, 它是应用程序内的“老大”，负责程序内部各阶段的资源申请，监督程序的执行情况。

**详情解释**

ApplicationMaster是应用程序级别的，每个ApplicationMaster管理运行在YARN上的应用程序。YARN 将 ApplicationMaster看做是第三

方组件，ApplicationMaster负责和ResourceManager scheduler协商资源，并且和NodeManager通信来运行相应的task。

ResourceManager 为 ApplicationMaster 分配容器，这些容器将会用来运行task。ApplicationMaster 也会追踪应用程序的状态，监控

容器的运行进度。当容器运行完成， ApplicationMaster 将会向 ResourceManager 注销这个容器；如果是整个作业运行完成，其也会向 

ResourceManager 注销自己，这样这些资源就可以分配给其他的应用程序使用了。

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**NM中的Container**

Container是与特定节点绑定的，其包含了内存、CPU磁盘等逻辑资源。不过在现在的容器实现中，这些资源只包括了内存和CPU。容器

是由 ResourceManager scheduler 服务动态分配的资源构成。容器授予 ApplicationMaster 使用特定主机的特定数量资源的权限。

ApplicationMaster 也是在容器中运行的，其在应用程序分配的第一个容器中运行。

Container 是YARN 中的资源抽象，它封装了某个节点上的多维度资源，如内存、CPU、磁盘、网络等，当 AM 向 RM 申请资源时， RM 

为 AM 返回的资源便是用 Container表示的。YARN 会为每个任务分配一个 Container，且该任务只能使用该 Container 中描述的资源。

需要注意的是， Container 不同于 MRv1 中的 slot（槽位），它是一个动态资源划分单位，是根据应用程序的需求动态生成的。

当下YARN仅支持**CPU**和**内存**两种资源，底层使用了轻量级资源隔离机制Cgroups进行资源隔离 

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

![image-20220917005745763](E:\黑马培训\Hadoop生态圈\assets\image-20220917005745763.png)



### 3. YARN上的应用类型

**短应用程序**：指一定时间内（可能是秒级、分钟级或小时级，尽管天级别或者更长时间的也存在，但非常少）可运行完成并正常退出的应

用程序，比如 MapReduce 作业、 Spark 作业等；

**长应用程序**：指不出意外，永不终止运行的应用程序，通常是一些服务，比如 Storm Service（主要包括 Nimbus 和 Supervisor 两类服

务）， Flink（包括 JobManager和 TaskManager两类服务） 等，而它们本身作为一个框架提供了编程接口供用户使用。

> 尽管这两类应用程序作用不同，一类直接运行数据处理程序，一类用于部署服务（服务之上再运行数据处理程序），但运行在 YARN
>
>  上的流程是相同的。

![image-20220917010200409](E:\黑马培训\Hadoop生态圈\assets\image-20220917010200409.png)



### 4. MR提交YARN的交互流程

**图解示例**

![image-20220917010323381](E:\黑马培训\Hadoop生态圈\assets\image-20220917010323381.png)

**文字理解**

yarn是另一种资源协调者，在集群中负责资源的分配

打个通俗的比方：

在一个部队中，有将军和训练有素的士兵，但是它们只会打仗，在谋略上完全不行，自己来的话就会找不到方向，有勇无谋很容易导致战

役的失败。因此在军队中通常还有一种角色军师，相当于诸葛亮出谋划策，这个yarn就相当于诸葛亮，来出谋划策分配部队的作战方向，

各个小部队的任务

**流程示意**

1. Client客户端提交job，给ResourceManager申请job_id

2. RM接收到Client的请求以后，给Client返回资源的提交路径和job_id

  > 每一个job都有一个唯一的job_id

3. 上传计算所需要的资源（Jar包，Configuration信息， InputSplit(数据分片信息)）到指定位置

4. Client发送完资源到指定位置后，向RM发送执行作业请求，ResourceManager在接收到这个请求以后，把这

   个请求给ApplicationManager

---------- **作业提交阶段工作完成**



1. Applications Manager接收到job请求后, 将job添加到ResourceScheduler中

2. RM维护了一个队列，所有要执行的job都会保存在这个队列中，按照一定的规则等待执行

3. 当轮到我们的job执行的时候，如果调度有资源可以满足要求的时候，RM会通知ASM (Applications 

   Manager),有空闲的NodeManager可以用来执行job

4. ASM (Applications Manager)调用分配给他的NodeManager，开辟一个Container容器，并且在这个容器中

   启动AM (Application Master ) 

5. AM去获取HDFS上的资源文件，生成相对应的Task， Task的种类可以是MapTask，也可以是ReduceTask

----------- **作业初始化阶段完成**



1. AM向RM申请运行Task的任务资源

   

2. RM分派Task任务，给空闲的NM，NM领取任务并去创建用于执行Task的Container容器



----------- **任务分配阶段完成**



1. AM发出信号通知所有接收到Task计算任务的NM,开始启动计算

2. NM接收到信号，启动Task计算

3. 如果这一批Task计算完成以后，还有Task任务需要执行的话，AM则向RM申请资源，创建新的Container来运行新的Task任务

4. 在任务执行阶段，AM会监视所有Task的运行情况，Task运行完成以后会直接将Container释放掉，job执行完

   毕以后，AM向ASM申请注销自己，释放掉Container

---------- **任务运行阶段完成**



1. 框架更新计算的进度和状态

--------- **任务完成阶段结束**



### 5. YARN资源调度器Scheduler

> 在YARN中，负责给应用分配资源的就是**Scheduler**，它是ResourceManager的核心组件之一
>
> Scheduler完全专用于调度作业，它无法跟踪应用程序的状态。
>
> 一般而言，调度是一个难题，并且没有一个“最佳”策略，为此，YARN提供了多种调度器和可配置的策略供选择



理想情况下，我们应用对Yarn资源的请求应该立刻得到满足，但现实情况资源往往是有限的，特别是在一个很繁忙的集群，一个应用资源

的请求经常需要等待一段时间才能的到相应的资源。在**Yarn**中，负责给应用分配资源的就是Scheduler。其实调度本身就是一个难

题，很难找到一个完美的策略可以解决所有的应用场景。为此，Yarn提供了多种调度器和可配置的策略供我们选择。



在Yarn中有三种调度器可以选择：

**FIFO Scheduler（先进先出调度器） ，Capacity Scheduler （容量调度器），Fair Scheduler （公平调度器）**

**Apache版本YARN默认使用Capacity Scheduler**

如果需要使用其他的调度器，可以在yarn-site.xml中的yarn.resourcemanager.scheduler.class进行配置



#### 1. 工作队列

- 工作队列（Queue）是从不同客户端收到的各种任务的集合。

- YARN默认只有一个可用于提交任务的队列，叫做default。当然用户也可以配置队列形成队列树结构。

- Scheduler的本质就是根据何种规则策略去分配资源给队列中的任务。

![image-20220917012719522](E:\黑马培训\Hadoop生态圈\assets\image-20220917012719522.png)

#### 2. 队列树

- 在YARN中，有**层级队列组织**方法，它们构成一个树结构，且根队列叫做root。

- 对于任何一个应用，都可以显式地指定它属于的队列，也可以不指定从而使用default队列。

- 在YARN WebUI界面上可以看到默认的队列组织情况。

![image-20220917012832318](E:\黑马培训\Hadoop生态圈\assets\image-20220917012832318.png)

### 6. 资源调度器介绍

> 注意y轴单词的意思是应用,利用,效用,job作业就是从这个方向被拿出来应用的

#### 1. FIFO Scheduler

**FIFO** Scheduler把应用按提交的顺序排成一个队列，这是一个**先进先出**队列，在进行资源分配的时候，先给队列中最头上的应用进行分配

资源，待最头上的应用需求满足后再给下一个分配，以此类推。

![image-20220917012003451](E:\黑马培训\Hadoop生态圈\assets\image-20220917012003451.png)

FIFO Scheduler是最简单也是最容易理解的调度器，也不需要任何配置，但它并不适用于共享集群。大的应用可能会占用所有集群资源，

这就导致其它应用被阻塞。在共享集群中，更适合采用Capacity Scheduler或Fair Scheduler，这两个调度器都允许大任务和小任务在提

交的同时获得一定的系统资源。

**优势与劣势**

**优势：**

无需配置、先到先得、易于执行

**坏处：**

任务的优先级不会变高，因此高优先级的作业需要等待

不适合共享集群



#### 2. Capacity Scheduler

Capacity 调度器允许多个组织共享整个集群，每个组织可以获得集群的一部分计算能力。通过为每个组织分配专门的队列，然后再为每个

队列分配一定的集群资源，这样整个集群就可以通过设置多个队列的方式给多个组织提供服务了。除此之外，队列内部又可以垂直划分，

这样一个组织内部的多个成员就可以共享这个队列资源了，**在一个队列内部，资源的调度是采用的是先进先出(FIFO)策略**

![image-20220917013637017](E:\黑马培训\Hadoop生态圈\assets\image-20220917013637017.png)

容量调度器 Capacity Scheduler 最初是由 Yahoo 最初开发设计使得 Hadoop 应用能够被多用户使用，且最大化整个集群资源的吞吐量，

现被 IBM BigInsights 和 Hortonworks HDP 所采用。

Capacity Scheduler 被设计为允许应用程序在一个可预见的和简单的方式共享集群资源，即"作业队列"。Capacity Scheduler 是根据租户

的需要和要求把现有的资源分配给运行的应用程序。Capacity Scheduler 同时允许应用程序访问还没有被使用的资源，以确保队列之间共

享其它队列被允许的使用资源。管理员可以控制每个队列的容量，Capacity Scheduler 负责把作业提交到队列中。

**举例说明**

- Capacity Scheduler调度器以队列为单位划分资源。简单通俗点来说，就是一个个队列有独立的资源，队列的结构和资源是可以进行

  配置的。

- default队列占30%资源，analyst和dev分别占40%和30%资源；且analyst和dev各有两个子队列，子队列在父队列的基础上再分配资源。

每个队列里的应用以FIFO方式调度，每个队列可设定一定比例的资源最低保证和使用上限防止滥用；

而当一个队列的资源有剩余时，可暂时将剩余资源共享给其他队列。

![image-20220917103946545](E:\黑马培训\Hadoop生态圈\assets\image-20220917103946545.png)

**优点**

- 层次化的队列设计（Hierarchical Queues）

层次化的管理，可以更容易、更合理分配和限制资源的使用。

- 容量保证（Capacity Guarantees）

每个队列上都可以设置一个资源的占比，保证每个队列都不会占用整个集群的资源。

- 安全（Security）

每个队列有严格的访问控制。用户只能向自己的队列里面提交任务，而且不能修改或者访问其他队列的任务。

- 弹性分配（Elasticity）

空闲的资源可以被分配给任何队列。

当多个队列出现争用的时候，则会按照权重比例进行平衡。

- 多租户租用（Multi-tenancy）

通过队列的容量限制，多个用户就可以共享同一个集群，同时保证每个队列分配到自己的容量，提高利用率

- 操作性（Operability）

Yarn支持动态修改调整队列容量、权限等的分配，可以在运行时直接修改

- 基于用户/组的队列隐射（Queue Mapping based on User or Group）

允许用户基于用户或者组去映射一个作业到特定队列。



由于Hadoop默认调度策略就是Capacity，**因此官方自带默认配置HADOOP_CONF/capacity-scheduler.xml**。

默认配置中显示全局只有一个队列default，占集群整体容量100。

![image-20220917104207842](E:\黑马培训\Hadoop生态圈\assets\image-20220917104207842.png)

**开启调度器**

如果需要使用其他的调度器，**可以在yarn-site.xml中的yarn.resourcemanager.scheduler.class进行配置**

```xml
<property>
   <name>yarn.resourcemanager.scheduler.class</name>
   <value>org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler</value>
 </property>
```

**队列配置**

Capacity的核心就是队列的分配和使用，修改HADOOP_CONF/capacity-scheduler.xml文件可以配置队列。默认有一个预定义的队列

root，所有的队列都是它的子队列。队列的分配支持层次化的配置，同级之间使用，来进行分割。

![image-20220917104627442](E:\黑马培训\Hadoop生态圈\assets\image-20220917104627442.png)

```xml
<property>     
<name>yarn.scheduler.capacity.root.queues</name>     
<value>a,b,c</value> 
</property>
<property>     
<name>yarn.scheduler.capacity.root.a.queues</name>     
<value>a1,a2</value> 
</property> 
<property>     
<name>yarn.scheduler.capacity.root.b.queues</name>     
<value>b1,b2</value>
</property>
```

**案例说明**

![image-20220917105159772](E:\黑马培训\Hadoop生态圈\assets\image-20220917105159772.png)

![image-20220917105221029](E:\黑马培训\Hadoop生态圈\assets\image-20220917105221029.png)

![image-20220917105240300](E:\黑马培训\Hadoop生态圈\assets\image-20220917105240300.png)

![image-20220917105256680](E:\黑马培训\Hadoop生态圈\assets\image-20220917105256680.png)

![image-20220917105314211](E:\黑马培训\Hadoop生态圈\assets\image-20220917105314211.png)

![image-20220917105330301](E:\黑马培训\Hadoop生态圈\assets\image-20220917105330301.png)

![image-20220917105354854](E:\黑马培训\Hadoop生态圈\assets\image-20220917105354854.png)



#### 3. Fair Scheduler

在Fair调度器中，我们不需要预先占用一定的系统资源，Fair调度器会为所有运行的job动态的调整系统资源。如下图所示，当第一个大

job提交时，只有这一个job在运行，此时它获得了所有集群资源；当第二个小任务提交后，Fair调度器会分配一半资源给这个小任务，让

这两个任务公平的共享集群资源。

需要注意的是，在下图Fair调度器中，从第二个任务提交到获得资源会有一定的延迟，因为它需要等待第一个任务释放占用的

Container (**这里的释放不是全部释放,而是部分释放,分一半的资源给他**)。小任务执行完成之后也会释放自己占用的资源，大任务又获得了

全部的系统资源。最终效果就是Fair调度器即得到了高的资源利用率又能保证小任务及时完成。

![image-20220917013844238](E:\黑马培训\Hadoop生态圈\assets\image-20220917013844238.png)

公平调度器 Fair Scheduler 最初是由 Facebook 开发设计使得 Hadoop 应用能够被多用户公平地共享整个集群资源，现被 Cloudera CDH 

所采用。Fair Scheduler 不需要保留集群的资源，因为它会动态在所有正在运行的作业之间平衡资源。

**如何理解公平共享**

- 有两个用户A和B，每个用户都有自己的队列。

- A启动一个作业，由于没有B的需求，它分配了集群所有可用的资源。

- 然后B在A的作业仍在运行时启动了一个作业，经过一段时间，A,B各自作业都使用了一半的资源。

- 现在，如果B用户在其他作业仍在运行时开始第二个作业，它将与B的另一个作业共享其资源，因此B的每个作业将拥有资源的四分之

  一，而A的继续将拥有一半的资源。结果是资源在用户之间公平地共享。

  ![image-20220917105752688](E:\黑马培训\Hadoop生态圈\assets\image-20220917105752688.png)

**队列**

- 默认情况下，所有用户共享一个名为default的队列。

- 可以在提交应用时指定队列，也可以通过配置根据请求中包含的用户名或组来分配队列。

- 在每个队列中，使用调度策略在运行的应用程序之间共享资源。默认设置是基于内存的公平共享。

![image-20220917105853681](E:\黑马培训\Hadoop生态圈\assets\image-20220917105853681.png)

**特性优势**

- **分层队列**：队列可以按层次结构排列以划分资源，并可以配置权重以按特定比例共享集群。

- **基于用户或组的队列映射**：可以根据提交任务的用户名或组来分配队列。如果任务指定了一个队列,则在该队列中提交任务。

- **资源抢占**：根据应用的配置，抢占和分配资源可以是友好的或是强制的。默认不启用资源抢占。

- **保证最小配额**：可以设置队列最小资源，允许将保证的最小份额分配给队列，保证用户可以启动任务。当队列不能满足最小资源

  时,可以从其它队列抢占。当队列资源使用不完时,可以给其它队列使用。这对于确保某些用户、组或生产应用始终获得足够的资源。

- **允许资源共享**：即当一个应用运行时,如果其它队列没有任务执行,则可以使用其它队列,当其它队列有应用需要资源时再将占用的队列

  释放出来。所有的应用都从资源队列中分配资源。

- **默认不限制每个队列和用户可以同时运行应用的数量**。可以配置来限制队列和用户并行执行的应用数量。限制并行执行应用数量不会

  导致任务提交失败,超出的应用会在队列中等待。

  

### 7. 大数据为何讨厌小文件

面试题目：遇到小文件多的场景，如何处理？	

​	从存储角度（HDFS）来分析

​		问题：

​			小文件的元数据信息占内存  磁盘压力不大 内存压力大

​		解决方案：合并

​			上传之前合并

​				使用Python java等编程语言合并

​			上传之中合并

​				appendToFile追加合并

​			上传之后合并

​				archive归档合并
​			

从计算的角度（MapReduce）来分析

​	问题：一个文件哪怕再小 本身也是一个块  一个块对应着一个切片 一个切片就启动一个maptask
​	
​	就会导致多少个小文件就启动多少个maptask来处理 而每个maptask都处理一点点数据 效率不高		
​	
​	因为maptask是进程级别的 创建 启动  销毁都需要时间精力 成本高。
​	
​	解决方案：合并



### 8. Hadoop HA集群

> 高可用集群 == > 可持续使用集群

#### 1. 高可用 (HA) 背景知识

**单点故障**

- 单点故障（英语：single point of failure，缩写**SPOF**）是指系统中某一点一旦失效，就会让整个系统无法运行

- 换句话说，单点故障即会整体故障

![image-20220917115511431](E:\黑马培训\Hadoop生态圈\assets\image-20220917115511431.png)



##### 1.1 如何解决单点故障

- 解决单点故障，实现系统服务高可用的核心并不是让故障永不发生，而是让故障的发生对业务的影响降到最小。因为软硬件故障是难

  以避免的问题。

- 当下企业中成熟的做法就是给单点故障设置备份，形成主备架构。通俗描述就是**当主挂掉，备份顶上**，短暂的中断之后继续提供服

  务。

- 常见的是**一主一备**架构，当然也可以一主多备。备份越多，容错能力越强，与此同时，冗余也越大，浪费资源。

![image-20220917115600548](E:\黑马培训\Hadoop生态圈\assets\image-20220917115600548.png)



##### 1.2 主备集群角色

**Active**：主角色。活跃的角色，代表正在对外提供服务的角色服务。任意时间有且只有一个active对外提供服务。

**Standby**：备份角色。需要和主角色保持数据、状态同步，并且时刻准备切换成主角色（当主角色挂掉或者出现故障时），对外提供服

务，保持服务的可用性。

![image-20220917115811828](E:\黑马培训\Hadoop生态圈\assets\image-20220917115811828.png)



##### 1.3 高可用

- **高可用性**（英语：high availability，缩写为 **HA**），IT术语，指系统无中断地执行其功能的能力，代表系统的可用性程度。是进行系

  统设计时的准则之一。

- 高可用性系统意味着系统服务可以更长时间运行，通常通过提高系统的容错能力来实现。

- 高可用性或者高可靠度的系统不会希望有单点故障造成整体故障的情形。一般可以通过**冗余**的方式增加多个相同机能的部件，只要这

  些部件没有同时失效，系统（或至少部分系统）仍可运作，这会让可靠度提高。



##### 1.4 集群可用性评判标准（x个9）

- 在系统的高可用性里有个衡量其可靠性的标准——X个9，这个X是代表数字3-5。

- X个9表示在系统1年时间的使用过程中，系统可以正常使用时间与总时间（1年）之比。

```xml
3个9：(1-99.9%)*365*24=8.76小时，表示该系统在连续运行1年时间里最多可能的业务中断时间是8.76小时。

4个9：(1-99.99%)*365*24=0.876小时=52.6分钟，表示该系统在连续运行1年时间里最多可能的业务中断时间是52.6分钟。

5个9：(1-99.999%)*365*24*60=5.26分钟，表示该系统在连续运行1年时间里最多可能的业务中断时间是5.26分钟。
```

- 可以看出，9越多，系统的可靠性越强，能够容忍的业务中断时间越少，但是要付出的成本更高。



##### 1.5 HA系统设计核心问题（1）

- **脑裂问题**

  脑裂(split-brain)是指“大脑分裂”,本是医学名词。

  在HA集群中，脑裂指的是当联系主备节点的"心跳线"断开时(即两个节点断开联系时)，本来为一个整体、动作协调的HA系统，就分裂成

为两个独立的节点。由于相互失去了联系，主备节点之间像"裂脑人"一样，使得整个集群处于混乱状态。

- **脑裂的严重后果**

  1）集群无主：都认为对方是状态好的，自己是备份角色，后果是无服务；

  2）集群多主：都认为对方是故障的，自己是主角色。相互争抢共享资源，结果会导致系统混乱，数据损坏。此外对于客户端访问也是一

   头雾水，找谁呢？

  避免脑裂问题的核心是：保持任意时刻系统有且只有一个主角色提供服务。



##### 1.6 HA系统设计核心问题 (2)

- **数据状态同步问题**

 主备切换保证服务持续可用性的前提是主备节点之间的状态、数据是一致的，或者说准一致的。如果说备用的节点和主节点之间的数据

 差距过大，即使完成了主备切换的动作，那也是没有意义的。

 数据同步常见做法是：**通过日志重演操作记录**。

主角色正常提供服务，**发生的事务性操作通过日志记录，备用角色读取日志重演操作**

#### 2. NAMENODE单点故障问题

**概述**

- 在Hadoop 2.0.0之前，NameNode是HDFS集群中的单点故障（SPOF）。

- 每个群集只有一个NameNode，如果NameNode进程不可用，则整个HDFS群集不可用。

**解决**

- **在同一群集中运行两个（从3.0.0起，支持超过两个）冗余NameNode**。形成主备架构。

- 这样可以在机器崩溃的情况下快速故障转移到新的NameNode，或者出于计划维护的目的由管理员发起的正常故障转移。



#### 3. HDFS HA解决方案  --  QJM

**Quorum Journal Manager介绍**

- QJM全称Quorum Journal Manager（仲裁日志管理器），是Hadoop官方推荐的HDFS HA解决方案之一。

- 使用zookeeper中ZKFC来实现主备切换；

- 使用Journal Node（JN）集群实现edits log的共享以达到数据同步的目的。

![image-20220917121019734](E:\黑马培训\Hadoop生态圈\assets\image-20220917121019734.png)



**主备切换、脑裂问题解决 -- ZKFailoverController（zkfc)**

**ZK Failover Controller**（ZKFC）是一个ZooKeeper客户端。主要职责：

- 监视和管理NameNode健康状态

ZKFC通过命令**监视的NameNode节点及机器的健康状态**。

- 维持和ZK集群联系

如果本地NameNode运行状况良好，并且ZKFC看到当前没有其他节点持有锁znode，它将自己尝试获取该锁。如果成功，则表明它“赢得

了**选举**”，并负责运行故障转移以使其本地NameNode处于Active状态。如果已经有其他节点持有锁，zkfc选举失败，则会对该节点注册

监听，等待下次继续选举。

![image-20220917121131888](E:\黑马培训\Hadoop生态圈\assets\image-20220917121131888.png)



**主备切换、脑裂问题解决 -- Fencing（隔离）机制**

- 故障转移过程也就是俗称的主备角色切换的过程，切换过程中最怕的就是脑裂的发生。因此需要Fencing机制来避免，将先前的Active

  节点隔离，然后将Standby转换为Active状态。

- Hadoop公共库中对外提供了两种Fenching实现，分别是sshfence和shellfence（缺省实现）。

- **sshfence**是指通过ssh登陆目标节点上，使用命令fuser将进程杀死（通过tcp端口号定位进程pid，该方法比jps命令更准确）；

- shellfence是指执行一个用户事先定义的shell命令（脚本）完成隔离。



**主备数据状态同步问题解决**

- Journal Node（**JN**）集群是轻量级分布式系统，主要用于高速读写数据、存储数据。

- 通常使用**2N+1**台JournalNode存储共享**Edits Log**（编辑日志）。--底层类似于zk的分布式一致性算法。

- 任何修改操作在 Active NN上执行时，JournalNode进程同时也会记录edits log到**至少半数**以上的JN中，这时 Standby NN 监测到JN 

  里面的同步log发生变化了会读取JN里面的edits log，然后重演操作记录同步到自己的目录镜像树里面。

![image-20220917121349884](E:\黑马培训\Hadoop生态圈\assets\image-20220917121349884.png)



#### 4. YARN HA解决方案

**背景**

- ResourceManager负责资源管理和应用的调度，是YARN的核心组件，集群的主角色。

- 在Hadoop 2.4之前，ResourceManager是YARN群集中的**SPOF**（Single Point of Failure，单点故障）。

- 为了解决RM的单点故障问题，YARN设计了一套**Active/Standby**模式的ResourceManager HA架构。

  ![image-20220917124002440](E:\黑马培训\Hadoop生态圈\assets\image-20220917124002440.png)

**架构**

- Hadoop官方推荐方案：基于Zookeeper集群实现YARN HA

- 实现HA集群的关键是：主备之间状态数据同步、主备之间顺利切换（故障转移机制）

- 针对数据同步问题，可以通过zk来存储共享集群的状态数据。因为zk本质也是一个小文件存储系统。

- 针对主备顺利切换，可以手动，也可以基于zk自动实现。

![image-20220917124224474](E:\黑马培训\Hadoop生态圈\assets\image-20220917124224474.png)



**故障转移机制**

- 第一种：**手动故障转移**

管理员使用命令手动进行状态切换。

- 第二种：**自动故障****转移**

RM可以选择嵌入基于Zookeeper的ActiveStandbyElector来实现自动故障转移。

YARN的自动故障转移不需要像HDFS那样运行单独的ZKFC守护程序，因为ActiveStandbyElector是一个嵌入在RM中充当故障检测器和

Leader选举的线程，而不是单独的ZKFC守护进程。



**故障转移原理（基于zk自动切换）**

- **创建锁节点**：在ZooKeeper上会创建一个叫做ActiveStandbyElectorLock的锁节点，所有的RM在启动的时候，都会去竞争写这个

  **临时**的Lock节点，而ZooKeeper能保证只有一个RM创建成功。创建成功的RM就切换为Active状态，没有成功的RM则切换为

  Standby状态。

- **注册Watcher监听**：Standby状态的RM向ActiveStandbyElectorLock节点注册一个节点变更的Watcher监听，利用临时节点的特性

  （会话结束节点自动消失），能够快速感知到Active状态的RM的运行情况。

- **准备切换**：当Active状态的RM出现故障（如宕机或网络中断），其在ZooKeeper上创建的Lock节点随之被删除，这时其它各个

  Standby状态的RM都会受到ZooKeeper服务端的Watcher事件通知，然后开始竞争写Lock子节点，创建成功的变为Active状态，其他

  的则是Standby状态。

- **Fencing(隔离)**：在分布式环境中，机器经常出现假死的情况（常见的是GC耗时过长、网络中断或CPU负载过高）而导致无法正常对

  外进行及时响应。如果有一个处于Active状态的RM出现假死，其他的RM刚选举出来新的Active状态的RM，这时假死的RM又恢复正

  常，还认为自己是Active状态，这就是分布式系统的脑裂现象，即存在多个处于Active状态的RM，可以使用隔离机制来解决此类问

  题。

- YARN的Fencing机制是借助ZooKeeper数据节点的ACL权限控制来实现不同RM之间的隔离。创建的根ZNode必须携带ZooKeeper的

  ACL信息，目的是为了独占该节点，以防止其他RM对该ZNode进行更新。借助这个机制假死之后的RM会试图去更新ZooKeeper的相

  关信息，但发现没有权限去更新节点数据，就把自己切换为Standby状态





> 
