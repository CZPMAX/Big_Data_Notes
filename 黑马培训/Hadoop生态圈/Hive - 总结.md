### 老师笔记总结

```xml
1.数仓基础知识
	数仓是什么？
		是集成的数据分析平台。
	数仓是为何而来？
		分析数据 支持分析 
	数仓有什么特点？
		不生产数据：
			数仓数据来自于各个数据源。比如数据库，日志，爬虫
		不消费数据：
			分析结果给其他数据应用来使用的。比如报表、可视化 挖掘 即席查询
			
		4个特点：
			面向主题
				基于主题开展数据分析 主题是抽象概念 数据综合体。
			集成
				数仓不生产数据 需要经过ETL将数据从数据源集成数仓主题中。
			非易失
				数仓是分析数据的平台 不是创造数据的平台
				数据的规律不可以修改
			时变
				数仓的数据随着时间批次变化
				
	数仓和数据库有什么区别？
		OLTP
			T事务 面向事务 支持事务  典型代表：RDBMS(MySQL ORACLE)
		OLAP
			A分析 面向分析 支持分析  典型代表：DW(Hive)
		一个软件究竟属于TP还是AP,看最终应用场景。
			
	数仓 数据集市？
		数据集市就是小型数据仓库 
		数仓：面向集团整体
		数据集市：面向特定部门
		
	数仓分层架构
		典型经典3层架构
			ODS
				临时存储层  将数据源和数仓解耦合 数据临时存储
			DW
				数据仓库层  分析核心
				实际中会划分更加细致的层次
			DA
				数据应用层
				报表 挖掘
		分层的好处？
			数据结构清晰
			数据血缘追踪
			复杂问题简单化
			减少重复开发
			解耦合
	ETL ELT
		传统数仓
			数据源---->ETL---->数仓-->应用
			
		大数据数仓
			数据源---->EL---->数仓--->TTTT---->应用
			
	数仓最最最最最最最最最最最最最最最最最重要的？
		在离线项目day03  数仓设计
			数仓分层 数仓建模 数仓ETL
			
2.Apache hive
	hive是什么？
		基于Hadoop的开源数仓软件
		和Hadoop有啥关系？
			基于hdfs存储数据的
			基于MapReduce来计算数据的
			
	hive的功能职责？
		首先通过建表将结构化文件映射成为一张表
		然后提供了类似sql语法去分析数据 hive将sql转换成为mr
	
	hive架构？
		用户接口
			提供一种方式让用户可以写sql
				CLI  JDBC  WebUI
		hive driver驱动
			负责sql的校验、解析、编译、优化
		元数据存储
			存储表和文件之间映射关系
		执行引擎及hadoop组件
			默认执行引擎：MR 其他可选引擎：Tez Spark(后期spark重点)
			hdfs yarn
			
	hive和mysql有啥关系？
		除了外表 sql像（模仿mysql语法来设计的 类sql hive sql HQL），没啥关系。
		hive是数仓 mysql是数据库。
		
	hive安装部署
		metadata metastore是？
			metadata 元数据
			metastore 元数据服务
		
		3种部署模式？（都是单机模式 hive不是分布式软件）
			内嵌
				metastore不需要配置 不需要启动
				metadata存储在内置derby数据库中
			本地
				metastore不需要配置 不需要启动
				metadata存储第三方MySQL数据库中
			远程
				metastore需要配置 需要启动
				metadata存储第三方MySQL数据库中
				
				
	hiveserver2服务
	两代客户端
	
		bin/hive------->metastore服务
		bin/beeline------>hiveserver2服务------->metastore服务
		
		启动之后别急 稍等
	
	体验的注意事项
		a.hive不能取代mysql  插入数据延迟太高
		b.hive并不是通过插入获取数据 而是通过映射获取的
			建表时，如何才能保证映射成功？
				字段类型
				字段顺序
				文件在hdfs存储位置
				字段之间分隔符
		c.小数据场景远离hive		
#1、Hive DDL 建表语句  重点之重点
	数据类型
		基础类型 复杂类型
		隐式转换 显示转换  不成功会显示null 
		
	SerDe序列化机制（Hive怎么读写HDFS文件的）
		谁来负责读文件？怎么读的？
		谁类负责反序列化？什么叫做反序列化？
		  对象---字节码、字节流
		  
	分隔符指定（必须会敲）
		row format delimited  使用默认的序列化类
			字段 集合元素 map的kv之间分隔符
		row format serde with serdeproperties(xxxxx) 指定其他序列化类
	默认分隔符
		\001 非打印字符 不可见
	内外部表
		建表区别：关键字external
		删除区别：
			内 元数据、数据都删除  受控表 manage_table
			外 只删除元数据
		如何区分表的是内部还是外部呢？
        	desc formatted xxxx 
        	show create table xxxx
        	
	存储路径location
		默认路径：/user/hive/warehouse
----------------------------------------------------	
	分区表
	分桶表
		分区、分桶是什么
			hive优化表。优化查询速度的。
		解决了什么问题
			全表扫描的问题。
			join效率的问题。
				
		如何创建
		如何加载
		如何使用
		注意事项
#2、Hive DDL Alter
	partition的增加、修改、修复

#3、常见的show
	库  
	表
	分区
	建表语句
	表元数据信息
		describe formatted
		desc formatted
	函数方法
    
#4、hive DML  重点之重点
	load
		local本地是哪里？
		如何理解load是一个纯操作？纯是什么意思？
			加载过程中不会数据进行任何操作。
		什么时候是复制  什么时候是加载

```

