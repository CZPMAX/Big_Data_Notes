# Hive  --  数据仓库

## 1. 前言

- 传统的数据库**mysql,oracle,sqlserver**用来业务逻辑处理的，而**hive,hbase**数据仓库用来进行数据分析的，**mongodb,redis**非关系

  型数据库(抢票,秒杀,验证码),一般使用于高并发场景的

>    高并发数据一般存在内存里面的

- 英文名称为Data Warehouse，可简写为DW或DWH。

- 数据仓库的目的是构建**面向分析的集成化数据环境**，为企业提供决策支持（Decision Support）。 

- 数据仓库是存数据的，企业的各种数据往里面存，主要目的是为了分析有效数据，后续会基于它产出供分析挖掘的数据，或者数据应

  用需要的数据，如企业的分析性报告和各类报表 等。 

可以理解为： **面向分析的存储系统**

- **数据仓库**目的是构建**面向分析**的集成化数据环境，分析结果为企业提供决策支持（Decision Support）

- 数据仓库本身并不“生产”任何数据，其数据来源于不同外部系统；

- 同时数据仓库自身也不需要“消费”任何的数据，其结果开放给各个外部应用使用；

- 这也是为什么叫“仓库”，而不叫“工厂”的原因。

![image-20220917130121424](E:\黑马培训\Hadoop生态圈\assets\image-20220917130121424.png)



## 2. Hive主要特征

### 2.1 面向主题

传统数据库中，最大的特点是面向应用进行数据的组织，各个业务系统可能是相互分离的。而数据仓库则是面向主题的。主题是一个抽象

的概念，是较高层次上企业信息系统中的数据综合、归类并进行分析利用的抽象。在逻辑意义上，它是对应企业中某一宏观分析领域所涉

及的分析对象。

操作型处理（传统数据）对数据的划分并不适用于决策分析。而基于主题组织的数据则不同，它们被划分为各自独立的领域，每个领域有

各自的逻辑内涵但互不交叉，在抽象层次上对数据进行完整、一致和准确的描述。一些主题相关的数据通常分布在多个操作型系统中

![image-20220917152612286](E:\黑马培训\Hadoop生态圈\assets\image-20220917152612286.png)

### 2.2 集成性

通过对分散、独立、异构的数据库数据进行抽取、清理、转换和汇总便得到了数据仓库的数据，这样保证了数据仓库内的数据关于整个企  

业的一致性。

数据仓库中的综合数据不能从原有的数据库系统直接得到。因此在数据进入数据仓库之前，必然要经过统一与综合，这一步是数据仓库建

设中最关键、最复杂的一步，所要完成的工作有：

（1）要统一源数据中所有矛盾之处，如字段的同名异义、异名同义、单位不统一、字长不一致，等等。

（2）进行数据综合和计算。数据仓库中的数据综合工作可以在从原有数据库抽取数据时生成，但许多是在数据仓库内部生成的，即进入

数据仓库以后进行综合生成的。

下图说明一个保险公司综合数据的简单处理过程，其中数据仓库中与“保险”主题有关的数据来自于多个不同的操作型系统。这些系统内部

数据的命名可能不同，数据格式也可能不同。把不同来源的数据存储到数据仓库之前，需要去除这些不一致。

![image-20220917152647420](E:\黑马培训\Hadoop生态圈\assets\image-20220917152647420.png)

### 2.3 非易失性（不可更新性）

操作型数据库主要服务于日常的业务操作，使得数据库需要不断地对数据实时更新，以便迅速获得当前最新数据，不至于影响正常的业务

运作。**在数据仓库中只要保存过去的业务数据，不需要每一笔业务都实时更新数据仓库，而是根据商业需要每隔一段时间把一批较新的数**

**据导入数据仓库**。

**数据仓库的数据反映的是一段相当长的时间内历史数据的内容，是不同时点的数据库快照的集合**，以及基于这些快照进行统计、综合和重

组的导出数据。

数据**非易失性**主要是针对**应用而言**。数据仓库的用户对数据的操作大多是数据查询或比较复杂的挖掘，一旦数据进入数据仓库以后，一般

情况下被较长时间保留。**数据仓库中一般有大量的查询操作，但修改和删除操作很少**。因此，数据经加工和集成进入数据仓库后是极少更

新的，通常只需要定期的加载和更新。



### 2.4 时变性

**概念**

数据仓库包含各种粒度的历史数据。数据仓库中的数据可能与某个特定日期、星期、月份、 季度或者年份有关。数据仓库的目的是通过

分析企业过去一段时间业务的经营状况，挖掘其中隐藏的模式。虽然数据仓库的用户不能修改数据，但并不是说数据仓库的数据是永远

不变 的。分析的结果只能反映过去的情况，当业务变化后，挖掘出的模式会失去时效性。因此数据仓库的数据需要定时更新，以适应决

策的需要。从这个角度讲，数据仓库建设是一个项目，更是一个过程 。数据仓库的数据随时间的变化表现在以下几个方面。

 （1）数据仓库的数据时限一般要远远长于操作型数据的数据时限。

 （2）操作型系统存储的是当前数据，而数据仓库中的数据是历史数据。

 （3）数据仓库中的数据是按照时间顺序追加的，它们都带有时间属性。



## 3. 数据库 数据仓库 数据集市

实际上他们讲的是OLTP 和 OLAP的区别

**操作型处理：**叫联机事物处理OLTP,也称为面向交易的处理系统,它是针对具体业务在数据库联机的日常操作,通常对少数记录进行查询,修

改.用户较为关心操作的响应时间、数据的安全性、完整性和并发支持的用户数等问题。传统的数据库系统作为数据管理的主要手段，主

要用于操作型处理。

![image-20220917154507960](E:\黑马培训\Hadoop生态圈\assets\image-20220917154507960.png)

> 传统的关系型数据库系统（RDBMS）作为数据管理的主要手段，主要用于操作型处理



**分析型处理：**叫联机分析处理 OLAP（On-Line Analytical Processing）一般针对某些主题的历史数据进行分析，支持管理决策。

![image-20220917154526407](E:\黑马培训\Hadoop生态圈\assets\image-20220917154526407.png)

> 数据仓库是OLAP系统的一个典型示例，主要用于数据分析



**数据库与数据仓库两者比较**

![image-20220917154638207](E:\黑马培训\Hadoop生态圈\assets\image-20220917154638207.png)



**区别**

- 数据库与数据仓库的区别实际讲的是OLTP与OLAP的区别。

- OLTP系统的典型应用就是RDBMS,也就是我们俗称的数据库，当然这里要特别强调此数据库表示的是关系型数据库，Nosql数据库并

  不在讨论范围内。

- OLAP系统的典型应用就是DW,也就是我们俗称的数据仓库



**首先要明白，数据仓库的出现，并不是要取代数据库。**

- 数据库是面向事务的设计，数据仓库是面向主题设计的。


- 数据库一般存储业务数据，数据仓库存储的一般是历史数据。


- 数据库设计是尽量避免冗余，一般针对某一业务应用进行设计，比如一张简单的User表，记录用户名、密码等简单数据即可，符合业 

​       务应用，但是不符合分析。数据仓库在设计是有意引入冗余，依照分析需求，分析维度、分析指标进行设计。

- 数据库是为捕获数据而设计，数据仓库是为分析数据而设计。

![image-20220917154811999](E:\黑马培训\Hadoop生态圈\assets\image-20220917154811999.png)



**数据仓库，是在数据库已经大量存在的情况下，为了进一步挖掘数据资源、为了决策需要而产生的，它决不是所谓的“大型数据库”。**

![image-20220917153455538](E:\黑马培训\Hadoop生态圈\assets\image-20220917153455538.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**数据仓库与数据集市的区别**  

> 仓库范围大，集市范围小

- 数据仓库（Data Warehouse）是面向整个集团组织的数据，数据集市( Data Mart ) 是面向单个部门使用的。

- 可以认为数据集市是数据仓库的子集，也有人把数据集市叫做小型数据仓库。数据集市通常只涉及一个主题领域，例如市场营销或销

  售。因为它们较小且更具体，所以它们通常更易于管理和维护，并具有更灵活的结构。

- 下图中，各种操作型系统数据和包括文件在内的等其他数据作为数据源，经过ETL(抽取转换加载)填充到数据仓库中；数据仓库中有不

- 同主题数据，数据集市则根据部门特点面向指定主题，比如Purchasing（采购）、Sales（销售）、Inventory（库存）；

用户可以基于主题数据开展各种应用：**数据分析、数据报表、数据挖掘**

![1、数据仓库、数据集市](E:\黑马培训\Hadoop生态圈\assets\1、数据仓库、数据集市.png)



## 4. 数据仓库分层架构

按照数据流入流出的过程，数据仓库架构可分为三层——**源数据**、**数据仓库**、**数据应用**

![image-20220824143858735](E:\黑马培训\Hadoop生态圈\assets\image-20220824143858735.png)

**阿里数据仓库示意图**

![image-20220917155215083](E:\黑马培训\Hadoop生态圈\assets\image-20220917155215083.png)

> 通过元数据管理和数据质量监控来把控整个数仓中数据的流转过程、血缘依赖关系和生命周期
>
> 阿里数仓是非常经典的3层架构，从下往上依次是：**ODS**、**DW**、**DA**



**ODS层（Operation Data Store）**

- **操作型数据层**，也称之为源数据层、数据引入层、数据暂存层、临时缓存层。

- 此层存放未经过处理的原始数据至数据仓库系统，结构上与源系统保持一致，是数据仓库的数据准备区。

- 主要完成基础数据引入到数仓的职责，和数据源系统进行解耦合，同时记录基础数据的历史变化。



**DW层（Data Warehouse）**

- **数据仓库层**，由ODS层数据加工而成。主要完成数据加工与整合，建立一致性的维度，构建可复用的面向分析和统计的明细事实

  表，以及汇总公共粒度的指标。内部具体划分如下：

- 公共维度层（DIM）：基于维度建模理念思想，建立整个企业一致性维度。

- 公共汇总粒度事实层（DWS、DWB）：以分析的主题对象作为建模驱动，基于上层的应用和产品的指标需求，构建公共粒度的汇总

  指标事实表，以宽表化手段物理化模型

- 明细粒度事实层（DWD）: 将明细事实表的某些重要维度属性字段做适当冗余，即宽表化处理。



**DA层（或ADS层）**

- **数据应用层**，面向最终用户，面向业务定制提供给产品和数据分析使用的数据。

- 包括前端报表、分析图表、KPI、仪表盘、OLAP专题、数据挖掘等分析



**模板示例**

数据仓库的数据来源于不同的源数据，并提供多样的数据应用，数据自下而上流入数据仓库后向上层开放应用，而数据仓库只是中间集成

化数据管理的一个平台

- 源数据层（ODS）：此层数据无任何更改，直接沿用外围系统数据结构和数据，不对外开放；为临时存储层，是接口数据的临时存储

  区域，为后一步的数据处理做准备。

- 数据仓库层（DW）：也称为细节层，DW层的数据应该是一致的、准确的、干净的数据，即对源系统数据进行了清洗（去除了杂质）

  后的数据。

- 数据应用层（DA或APP）：前端应用直接读取的数据源；根据报表、专题分析需求而计算生成的数据。

![image-20220917153650966](E:\黑马培训\Hadoop生态圈\assets\image-20220917153650966.png)



**数据仓库**从各数据源获取数据及在数据仓库内的数据转换和流动都可以认为是ETL（抽取Extra, 转化Transfer, 装载Load）的过程，ETL是

数据仓库的流水线，也可以认为是数据仓库的血液，它维系着数据仓库中数据的新陈代谢，而数据仓库日常的管理和维护工作的大部分精

力就是保持ETL的正常和稳定。



**为什么要对数据仓库分层？**

- **用空间换时间**，通过大量的预处理来提升应用系统的用户体验（效率），因此数据仓库会存在大量冗余的数据；不分层的话，如果源

  业务系统的业务规则发生变化将会影响整个数据清洗过程，工作量巨大。

- **通过数据分层管理可以简化数据清洗的过程**，因为把原来一步的工作分到了多个步骤去完成，相当于把一个复杂的工作拆成了多个简

  单的工作，把一个大的黑盒变成了一个白盒，每一层的处理逻辑都相对简单和容易理解，这样我们比较容易保证每一个步骤的正确

  性，当数据发生错误的时候，往往我们只需要局部调整某个步骤即可

![image-20220917155749361](E:\黑马培训\Hadoop生态圈\assets\image-20220917155749361.png)

![image-20220917155804636](E:\黑马培训\Hadoop生态圈\assets\image-20220917155804636.png)

## 5. ETL 与 ELT

**背景**

- 数据仓库从各数据源获取数据及在数据仓库内的数据转换和流动都可以认为是ETL（抽取Extract, 转化Transform, 装载Load）的过

  程。

- 但是在实际操作中将数据加载到仓库却产生了两种不同做法：ETL和ELT

**概念**

首先从数据源池中提取数据，这些数据源通常是事务性数据库。数据保存在临时暂存数据库中（ODS）。然后执行转换操作，将数据结构

化并转换为适合目标数据仓库系统的形式。然后将结构化数据加载到仓库中，以备分析。

![image-20220917160012463](E:\黑马培训\Hadoop生态圈\assets\image-20220917160012463.png)

使用ELT，数据在从数据源中提取后立即加载。没有专门的临时数据库（ODS），这意味着数据会立即加载到单一的集中存储库中。数据

在数据仓库系统中进行转换，以便与商业智能工具（BI工具）一起使用。**大数据时代数仓这个特点很明显**

![image-20220917155923776](E:\黑马培训\Hadoop生态圈\assets\image-20220917155923776.png)

**两者区别**

![2、ETL和ELT的区别](E:\黑马培训\Hadoop生态圈\assets\2、ETL和ELT的区别.png)

## 6. Hive工作流程

**几种常见的客户端**

![CS模式--几种常见的客户端](E:\黑马培训\Hadoop生态圈\assets\CS模式--几种常见的客户端.png)

### 1. Hive的架构设计

![image-20220920101641039](E:\黑马培训\Hadoop生态圈\assets\image-20220920101641039.png)

**Hive架构设计包括三个部分**

**1. Hive Clients  (驱动协议客户端)** === > 只提供驱动来连接hive客户端的Hive Service2服务

**它为不同类型的应用程序提供不同的驱动**，使得Hive可以通过类似Java、Python等语言连接，同时也提供了JDBC

和ODBC驱动



**2. Hive servives (Hive服务端)**

客户端必须通过服务端与Hive交互，主要包括：



**用户接口组件（CLI，HiveServer，HWI）==> (Hive客户端)**，它们分别以命令行、与web的形式连接Hive。

**详解：**

 1、CLI：命令行接口，是最常用的一种用户接口，CLI启动时会同时启动一个Hive副本。CLI是和Hive交互的

最简单也是最常用方式，只需要在一个具备完整Hive环境下的Shell终端中键入hive即可启动服务。 用户可以

在CLI上输入HQL来执行创建表、更改属性以及查询等操作。不过Hive CLI不适应于高并发的生产环境，仅仅

是Hive管理员的好工具。

 2、JDBC/ODBC： JDBC是java database connection的规范，它定义了一系列java访问各类db的访问接

口，因此hive-jdbc其实本质上是扮演一个协议转换的角色，把jdbc的标准协议转换为访问**HiveServer服务**的

协议。hive-jdbc除了扮演网络协议转化的工作，并不承担他的工作，比如sql的合法性校验和解析，一律忽略 

。ODBC是一组对数据库访问的标准AP ，它的底层实现源码是采用C/C++编写的。JDBC/ODBC都是通过

hiveclient与hiveserver保持通讯的，借助thrfit rpc协议来实现交互。

 3、HWI：HWI是Hive的web方为接口，提供了一种可以可以通过浏览器来访问Hive的服务



**Driver组件**，该组件包含**编译器**、**优化器**和**执行引擎**，它的作用是将hiveSQL语句进行解析、编译优化、生成

执行计划，然后调用底层MR计算框架。

**具体各组件的作用**

- 解释器：将SQL字符串转化为抽象语法树AST；
- 编译器：将AST编译成逻辑执行计划；
- 优化器：对逻辑执行计划进行优化；
- 执行器：将逻辑执行计划转成可执行的物理计划，如MR/Spark



**Metastore组件**，元数据服务组件。Hive数据分为两个部分，**一部分真实数据保存在HDFS中**

**另一部分是真实数据的元数据，一般保存在MySQL中**，元数据保存了真实数据的很多信息，是对真实数

据的描述。



**3、Hive Storage and Computing** ，包括元数据存储数据库和Hadoop集群。Hive元数据存储在RDBMS中，

Hive数据存储在二手域名买卖平台HDFS中，查询由MR完成。



### 2. Hive的实际工作流程

**流程图解**

![image-20220920110255406](E:\黑马培训\Hadoop生态圈\assets\image-20220920110255406.png)

**细化每步操作细节**

| 序号 | 操作                                                         |
| ---- | ------------------------------------------------------------ |
| 1    | 执行查询。Hive接口，如命令行或Web UI发送查询驱动程序（任何数据库驱动程序，如JDBC，ODBC等）来执行  ==> 在这里hive已经连接上去了，只不过后续的数据传输要依靠这些驱动协议的通道传输 |
| 2    | 获取计划。在驱动程序的帮助下查询**编译器**（Compiler），**分析查询检查语法**和**查询计划**或**查询请求** |
| 3    | 获取元数据，编译器发送元数据请求到Metastore(任何数据库中)    |
| 4    | Metastore发送元数据，返回给编译器                            |
| 5    | 编译器检查要求（编译），并重新发送计划给驱动程序。到此为止，查询解析和编译完成 |
| 6    | 驱动程序发送执行计划到执行引擎                               |
| 7    | 在内部，执行作业的过程是一个MapperReduce工作。执行引擎发送作业给ResouceManager，接着RM把可用的资源路径与job的id返回给执行引擎，执行引擎把后续会用到的jar放在hdfs指定路径上，接着执行引擎发送请求给RM,创建ASM,然后把job传入ASM,ASM把job添加进资源的调度器，资源调度器去查看哪些Nodemanager是可用，空闲的，然后把信息返回给ASM,ASM到指定的NM上创建容器，在容器中创建AM，接着AM到HDFS上获取资源，在容器中生产task任务，这是在数据节点，在查询执行mr工作。与此同时，在执行时，执行引擎可用通过metastore执行元数据操作 |
| 8    | 执行引擎接收到来自数据节点的结果                             |
| 9    | 执行引擎发送这些结果值给驱动程序                             |
| 10   | 驱动程序将结果发送给Hive接口，用户就能看见执行结果           |

**精简解释：**

**简而言之，Hive接到命令之后，首先会去元数据库获取元数据，然后把元数据信息和作业计划发送Hadoop集群**

**执行任务，再将最终的结果返回。**



## 7. Hive入门

### 1. Hive简介

- Apache Hive是一款建立在Hadoop之上的开源**数据仓库**系统，可以将存储在Hadoop文件中的结构化、半结

  构化数据文件映射为一张数据库表，基于表提供了一种类似SQL的查询模型，称为Hive查询语言（HQL），用

  于访问和分析存储在Hadoop文件中的大型数据集。但并不提供查询功能，而是将SQL转化为MapReduce任务

  进行运行。同时，Hive本身不存储数据，只是存储数据的路径或者操作信息，真的数据是存储在可靠的文件系

  统当中（如HDFS、Derby等）

- Hive核心是将**HQL转换为MapReduce**程序，然后将程序提交到Hadoop群集执行。

- Hive由Facebook实现并开源。

![image-20220917233954667](E:\黑马培训\Hadoop生态圈\assets\image-20220917233954667.png)



### 2. 使用Hive的原因

- 使用Hadoop MapReduce直接处理数据所面临的问题 

​       人员学习成本太高 需要掌握java语言

​       MapReduce实现复杂查询逻辑开发难度太大 

- 使用Hive处理数据的好处 

​      操作接口采用类SQL语法，提供快速开发的能力（简单、容易上手）

​      避免直接写MapReduce，减少开发人员的学习成本 

​      支持自定义函数，功能扩展很方便

​       背靠Hadoop，擅长存储分析海量数据集



### 3. Hadoop与Hive的关系

- 从功能来说，数据仓库软件，至少需要具备下述两种能力：

​       存储数据的能力、分析数据的能力

- Apache Hive作为一款大数据时代的数据仓库软件，当然也具备上述两种能力。只不过Hive并不是自己实现了上述两种能力，而是借

  助Hadoop。

  **Hive利用HDFS存储数据，利用MapReduce查询分析数据**。

- 这样突然发现Hive没啥用，不过是套壳Hadoop罢了。其实不然，Hive的最大的魅力在于用户专注于编写HQL，Hive帮您转换成为

  MapReduce程序完成对数据的分析。

![image-20220917234318529](E:\黑马培训\Hadoop生态圈\assets\image-20220917234318529.png)



### 4. Hive架构

**架构与组件示意图**

![3、Apache Hive的架构及组件](E:\黑马培训\Hadoop生态圈\assets\3、Apache Hive的架构及组件.png)

**Hive组件**

**用户接口**：包括 CLI、JDBC/ODBC、WebGUI。其中，CLI(command line interface)为shell命令行；JDBC/ODBC是Hive的JAVA实现，与

传统数据库JDBC类似；WebGUI是通过浏览器访问Hive。

**元数据存储**：通常是存储在关系数据库如 mysql/derby中。Hive 将元数据存储在数据库中。Hive 中的元数据包括表的名字，表的列和分

区及其属性，表的属性（是否为外部表等），表的数据所在目录等。

**解释器、编译器、优化器、执行器**:完成 HQL 查询语句从词法分析、语法分析、编译、优化以及查询计划的生成。生成的查询计划存储在 

HDFS 中，并在随后有 MapReduce 调用执行。

**执行引擎**

Hive本身并不直接处理数据文件。而是通过执行引擎处理。当下Hive支持MapReduce、Tez、Spark3种执行引擎。



### 5. Hive数据仓库与传统数据库区别

hive用于海量数据的离线数据分析。

hive具有sql数据库的外表，但应用场景完全不同，hive只适合用来做批量数据统计分析。

更直观的对比请看下面这幅图：

![image-20220917235110149](E:\黑马培训\Hadoop生态圈\assets\image-20220917235110149.png)

**Hive和MySQL对比**

- Hive虽然具有RDBMS数据库的外表，包括数据模型、SQL语法都十分相似，但应用场景却完全不同。

- **Hive只适合用来做海量数据的离线分析。Hive的定位是数据仓库，面向分析的OLAP（联机型非事务分析）系统。**

- 因此时刻告诉自己，**Hive不是大型数据库，也不是要取代MySQL承担业务数据处理**。

![image-20220920145555523](E:\黑马培训\Hadoop生态圈\assets\image-20220920145555523.png)



### 6. 模拟实现Apache Hive的功能

![image-20220917235434175](E:\黑马培训\Hadoop生态圈\assets\image-20220917235434175.png)



**场景目的**

- 重点理解下面两点：

​       Hive能将数据文件映射成为一张表，这个**映射**是指什么？

​       Hive软件本身到底承担了什么**功能职责**？



**映射信息记录**

- **映射**在数学上称之为一种**对应关系**，比如y=x+1，对于每一个x的值都有与之对应的y的值。

- 在hive中能够写sql处理的前提是针对表，而不是针对文件，因此需要将**文件和表之间的对应关系**描述记录清楚。映射信息专业的叫法

- 称之为元数据信息（元数据是指用来描述数据的数据 metadata）。

  - 具体来看，要记录的元数据信息包括：

    - 表对应着哪个文件（位置信息）

    - 表的列对应着文件哪一个字段（顺序信息）

    - 文件字段之间的分隔符是什么

![image-20220917235951759](E:\黑马培训\Hadoop生态圈\assets\image-20220917235951759.png)

**SQL语法解析、编译**

用户写完sql之后，hive需要针对sql进行语法校验，并且根据记录的元数据信息解读sql背后的含义，制定执行计划。并且把执行计划转换

成MapReduce程序来执行，把执行的结果封装返回给用户



**问题解释**

![image-20220918000152629](E:\黑马培训\Hadoop生态圈\assets\image-20220918000152629.png)



**最终效果**

- 基于上述分析，最终要想模拟实现的Hive的功能，大致需要下图所示组件参与其中。

- 从中可以感受一下Hive承担了什么职责，当然，也可以把这个理解为Hive的架构图。

![image-20220918000243422](E:\黑马培训\Hadoop生态圈\assets\image-20220918000243422.png)







### 7. Hive安装部署

Hive安装前需要安装好JDK和Hadoop。配置好环境变量。如果需要使用mysql来存储元数据，则需要mysql也安装好

#### 1. metadata 与 metastore 

**Hive Metadata即Hive的元数据**。

元数据包含用Hive创建的database、table、表的字段等元信息。

元数据存储在关系型数据库中, 如hive内置的Derby、第三方如MySQL等

![image-20220918000835970](E:\黑马培训\Hadoop生态圈\assets\image-20220918000835970.png)



**什么是元数据**

- 元数据（Metadata），又称中介数据、中继数据，为**描述数据的数据**（data about data），主要是描述数据

  属性（property）的信息，用来支持如指示存储位置、历史数据、资源查找、文件记录等功能。

  ![image-20220918002446719](E:\黑马培训\Hadoop生态圈\assets\image-20220918002446719.png)



**Metastore即元数据服务**，

- Metastore即**元数据服务**。Metastore服务的作用是管理metadata元数据，对外暴露服务地址，让各种客户端通过连接metastore服

  务，由metastore再去连接MySQL数据库来存取元数据。

- 有了metastore服务，就可以有多个客户端同时连接，而且这些客户端不需要知道MySQL数据库的用户名和密码，只需要连接

  metastore 服务即可。某种程度上也保证了hive元数据的安全。

![image-20220918001045809](E:\黑马培训\Hadoop生态圈\assets\image-20220918001045809.png)



#### 2. metastore三种配置方式

metastore服务配置有3种模式：**内嵌模式**、**本地模式**、**远程模式**。

区分3种配置方式的关键是弄清楚两个问题：

- Metastore服务是否需要单独配置、单独启动？

- Metadata是存储在内置的derby中，还是第三方RDBMS,比如Mysql。

![image-20220918001234790](E:\黑马培训\Hadoop生态圈\assets\image-20220918001234790.png)

##### 2.1 内嵌模式

- **内嵌模式**（Embedded Metastore）是metastore默认部署模式。

- 此种模式下，元数据存储在内置的Derby数据库，并且Derby数据库和metastore服务都嵌入在主HiveServer

  进程中，当启动HiveServer进程时，Derby和metastore都会启动。不需要额外起Metastore服务。

- 但是一次只能支持一个活动用户，适用于测试体验，不适用于生产环境。

![image-20220918003937797](E:\黑马培训\Hadoop生态圈\assets\image-20220918003937797.png)

##### 2.2 本地模式

- **本地模式**（Local Metastore）下，Metastore服务与主HiveServer进程在同一进程中运行，但是存储元数据

  的数据库在单独的进程中运行，并且可以在单独的主机上。metastore服务将通过JDBC与metastore数据库进

  行通信。

- 本地模式采用外部数据库来存储元数据，推荐使用MySQL。

- hive根据hive.metastore.uris 参数值来判断，如果为空，则为本地模式。

- 缺点是：每启动一次hive服务，都内置启动了一个metastore。

![image-20220918004121581](E:\黑马培训\Hadoop生态圈\assets\image-20220918004121581.png)

##### 2.3 远程模式

- **远程模式**（Remote Metastore）下，Metastore服务在其自己的单独JVM上运行，而不在HiveServer的JVM中

  运行。如果其他进程希望与Metastore服务器通信，则可以使用Thrift Network API进行通信。

- 远程模式下，需要配置hive.metastore.uris 参数来指定metastore服务运行的机器ip和端口，并且需要单独手

  动启动metastore服务。元数据也采用外部数据库来存储元数据，推荐使用MySQL。

- 在生产环境中，建议用远程模式来配置Hive Metastore。在这种情况下，其他依赖hive的软件都可以通过

  Metastore访问hive。由于还可以完全屏蔽数据库层，因此这也带来了更好的可管理性/安全性。

![image-20220918004308423](E:\黑马培训\Hadoop生态圈\assets\image-20220918004308423.png)



#### 3. Hive metastore 远程模式安装部署

在hadoop200第一台机器上安转hive的metastore等服务

**启动hive之前必须保证:**

1. 选择某台机器提前安转mysql,确保具有远程访问的权限
2. 启动hadoop集群 确保集群正常健康

**Hadoop中添加用户代理配置**

![image-20220920084934187](E:\黑马培训\Hadoop生态圈\assets\image-20220920084934187.png)

**上传安装包 并解压到要安转的目录**

![image-20220920085020628](E:\黑马培训\Hadoop生态圈\assets\image-20220920085020628.png)

**修改配置文件hive-env.sh(hive启动相关)**

![image-20220920085121708](E:\黑马培训\Hadoop生态圈\assets\image-20220920085121708.png)

**添加配置文件hive-site.xml**

```xml
<configuration>
<!-- 存储元数据mysql相关配置 -->
<property>
	<name>javax.jdo.option.ConnectionURL</name>
	<value>jdbc:mysql://node1:3306/hive3?createDatabaseIfNotExist=true&amp;useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8</value>
</property>

<property>
	<name>javax.jdo.option.ConnectionDriverName</name>
	<value>com.mysql.jdbc.Driver</value>
</property>

<property>
	<name>javax.jdo.option.ConnectionUserName</name>
	<value>root</value>
</property>

<property>
	<name>javax.jdo.option.ConnectionPassword</name>
	<value>hadoop</value>
</property>

<!-- H2S运行绑定host -->
<property>
    <name>hive.server2.thrift.bind.host</name>
<value>node1</value>
</property>

<!-- 远程模式部署metastore metastore地址 -->
<property>
    <name>hive.metastore.uris</name>
    <value>thrift://node1:9083</value>
</property>

<!-- 关闭元数据存储授权  --> 
<property>
    <name>hive.metastore.event.db.notification.api.auth</name>
    <value>false</value>
</property>
</configuration>
```

**上传MySQL驱动**

![image-20220920085321021](E:\黑马培训\Hadoop生态圈\assets\image-20220920085321021.png)

**初始化元数据**

![image-20220920085356178](E:\黑马培训\Hadoop生态圈\assets\image-20220920085356178.png)

**创建hive存储目录**

![image-20220920085438392](E:\黑马培训\Hadoop生态圈\assets\image-20220920085438392.png)

**远程模式下使用注意事项**

- 如果在远程模式下，直接运行hive服务，在执行操作的时候会报错，错误信息如下：

![image-20220920145826772](E:\黑马培训\Hadoop生态圈\assets\image-20220920145826772.png)

- 在**远程模式下，必须首先启动Hive metastore服务才可以使用hive。**

- **因为metastore服务和hive server是两个单独的进程了**。



#### 4. metastore 的启动方式

```
#前台启动  关闭ctrl+c
/export/servers/apache-hive-3.1.2-bin/bin/hive --service metastore

#前台启动开启debug日志
/export/servers/apache-hive-3.1.2-bin/bin/hive --service metastore --hiveconf hive.root.logger=DEBUG,console  

#后台启动 进程挂起  关闭使用jps+ kill -9
nohup /exports/server/apache-hive-3.1.2-bin/bin/hive --service metastore &

```



#### 5. Hive Client，Beeline Client

##### 1. 第一代客户端Hive Client

在hive安装包的bin目录下，有hive提供的第一代客户端 bin/hive。使用该客户端可以访问hive的metastore服

务。从而达到操作hive的目的。

如果需要在其他机器上通过该客户端访问hive metastore服务，只需要在该机器的hive-site.xml配置中添加

metastore服务地址即可。

**scp安装包到另一个机器上，比如hadoop202：**

```shell
scp -r /export/servers/apache-hive-3.1.2-bin/ hadoop202:/export/servers/
```

vim hive-site.xml （编辑默认使用元数据服务的机器）内容如下：

```xml
<configuration>
<property>
    <name>hive.metastore.uris</name>
    <value>thrift://node1:9083</value>
</property>
</configuration>
```

使用下面的命令启动hive的客户端：

```shell
/export/servers/apache-hive-3.1.2-bin/bin/hive
```

![image-20220920143150964](E:\黑马培训\Hadoop生态圈\assets\image-20220920143150964.png)

> 可以发现官方提示：第一代客户端已经不推荐使用了。



##### 2. 第二代客户端Hive Beeline Client

hive经过发展，推出了第二代客户端beeline，但是beeline客户端不是直接访问metastore服务的，而是**需要单独**

**启动hiveserver2服务**。

在hive运行的服务器上，首先启动metastore服务，然后启动hiveserver2服务。

```
nohup /export/servers/apache-hive-3.1.2-bin/bin/hive --service metastore &
nohup /export/servers/apache-hive-3.1.2-bin/bin/hive --service hiveserver2 &
```

>  nohup 后台挂起命令  让程序在后台跑



在hadoop202上使用beeline客户端进行连接访问。

```shell
/export/servers/apache-hive-3.1.2-bin/bin/beeline
```

beeline> ! connect jdbc:hive2://hadoop200:10000

Enter username for jdbc:hive2://hadoop200:10000: root

Enter password for jdbc:hive2://hadoop200:10000:123456

![image-20220920144234622](E:\黑马培训\Hadoop生态圈\assets\image-20220920144234622.png)

**HiveServer与HiveServer2服务**

- HiveServer、HiveServer2都是Hive自带的两种服务，允许客户端在不启动CLI（命令行）的情况下对Hive中

  的数据进行操作，且两个都允许远程客户端使用多种编程语言如java，python等向hive提交请求，取回结果。

- 但是，HiveServer不能处理多于一个客户端的并发请求。因此在Hive-0.11.0版本中重写了HiveServer代码得到了HiveServer2，进而解决了该问题。HiveServer已经被废弃。

- **HiveServer2支持多客户端的并发和身份认证，旨在为开放API客户端如JDBC、ODBC提供更好的支持**。



#### 6. Hive服务与客户端

**关系梳理**

- HiveServer2通过Metastore服务读写元数据。所以在远程模式下，**启动HiveServer2之前必须先首先启动**

​      **metastore**服务。

- 特别注意：远程模式下，Beeline客户端只能通过HiveServer2服务访问Hive。而bin/hive是通过Metastore服

  务访问的。具体关系如下：

![image-20220920150454392](E:\黑马培训\Hadoop生态圈\assets\image-20220920150454392.png)

**使用**

- 在hive安装包的bin目录下，有hive提供的第一代客户端 bin/hive。该客户端可以访问hive的metastore服务，

  从而达到操作hive的目的。

- 友情提示：如果您是远程模式部署，请手动启动运行metastore服务。如果是内嵌模式和本地模式，直接运

  bin/hive，metastore服务会内嵌一起启动。

- 可以直接在启动Hive metastore服务的机器上使用bin/hive客户端操作，此时不需要进行任何配置。

![image-20220920150644974](E:\黑马培训\Hadoop生态圈\assets\image-20220920150644974.png)



**使用一代客户端**

- 如果需要在其他机器上通过bin/hive访问hive metastore服务，只需要在该机器的hive-site.xml配置中添加

  metastore服务地址即可

  ![image-20220920150925410](E:\黑马培训\Hadoop生态圈\assets\image-20220920150925410.png)

**使用第二代客户端**

- hive经过发展，推出了第二代客户端beeline，但是beeline客户端不是直接访问metastore服务的，而是需要

  单独启动hiveserver2服务。

- 在hive安装的服务器上，首先启动metastore服务，然后启动hiveserver2服务。

![image-20220920151054920](E:\黑马培训\Hadoop生态圈\assets\image-20220920151054920.png)

**接着在其他客户端上访问**

- 在node3上使用beeline客户端进行连接访问。需要注意hiveserver2服务启动之后需要稍等一会才可以对外提

  供服务。

- Beeline是JDBC的客户端，通过JDBC协议和Hiveserver2服务进行通信，协议的地址是：

  jdbc:hive2://hadoop200:10000

![image-20220920151946780](E:\黑马培训\Hadoop生态圈\assets\image-20220920151946780.png)

![image-20220920152004449](E:\黑马培训\Hadoop生态圈\assets\image-20220920152004449.png)

> 注意其他机器想要访问安装hive服务的服务器,就要在本机上配置元数据服务的服务器地址,再根据相对应的驱
>
> 动协议客户端发送连接请求,给hive客户端的hiveserver2服务

**查看hive运行**

![image-20220920152108402](E:\黑马培训\Hadoop生态圈\assets\image-20220920152108402.png)

**结论**

- Hive SQL语法和标准SQL很类似,使得学习成本降低不少。

- Hive底层是通过MapReduce执行的数据插入动作,所以速度慢。

- 如果大数据集这么一条一条插入的话是非常不现实的，成本极高。

- Hive应该具有自己特有的数据插入表方式，结构化文件映射成为表。



### 8. Apache Hive数据模型

- 数据模型：用来描述数据、组织数据和对数据进行操作，是对现实世界数据特征的描述。

- Hive的数据模型类似于RDBMS库表结构，此外还有自己特有模型。

- Hive中的数据可以在粒度级别上分为三类：

Table 表

Partition 分区

Bucket 分桶

![image-20220920144450960](E:\黑马培训\Hadoop生态圈\assets\image-20220920144450960.png)

**Databases数据库**

- Hive作为一个数据仓库，在结构上积极向传统数据库看齐，也分数据库（Schema），每个数据库下面有各自

  的表组成。默认数据库default。

- Hive的数据都是**存储在HDFS上**的，默认有一个根目录，在hive-site.xml中，由参数

  hive.metastore.warehouse.dir指定。默认值为/user/hive/warehouse。

- 因此，Hive中的数据库在HDFS上的存储路径为：

```xml
${hive.metastore.warehouse.dir}/databasename.db
```

- 比如，名为itcast的数据库存储路径为：

```shell
/user/hive/warehouse/itcast.db
```

**Tables表**

- Hive表与关系数据库中的表相同。Hive中的表所对应的数据通常是存储在HDFS中，而表相关的元数据是存储在RDBMS中。

- Hive中的表的数据在HDFS上的存储路径为：

```shell
${hive.metastore.warehouse.dir}/databasename.db/tablename
```

- 比如,itcast的数据库下t_user表存储路径为：

```shell
/user/hive/warehouse/itcast.db/t_user
```

![image-20220920144745644](E:\黑马培训\Hadoop生态圈\assets\image-20220920144745644.png)

**Partitions分区**

- Partition分区是hive的一种优化手段表。分区是指**根据分区列（例如“日期day”）的值将表划分为不同分区**。

  这样可以更快地对指定分区数据进行查询。

- 分区在存储层面上的表现是:table表目录下以子文件夹形式存在。

- 一个文件夹表示一个分区。子文件命名标准：分区列=分区值

- Hive还支持分区下继续创建分区，所谓的多重分区。关于分区表的使用和详细介绍，后面模块会单独展开。

![image-20220920144909631](E:\黑马培训\Hadoop生态圈\assets\image-20220920144909631.png)

**Buckets分桶**

- Bucket分桶表是hive的一种优化手段表。分桶是指**根据表中字段（例如“编号ID）的值,经过hash计算规则将**

  **数据文件划分成指定的若干个小文件**。

- 分桶规则：hashfunc(字段) % 桶个数，余数相同的分到同一个文件。

![image-20220920145117458](E:\黑马培训\Hadoop生态圈\assets\image-20220920145117458.png)

- 分桶的好处是可以优化join查询和方便抽样查询。

- Bucket分桶表在HDFS中表现为同一个表目录下数据根据hash散列之后变成多个文件。关于桶表以及分桶操

  作，后面模块会单独展开详细讲解。

![image-20220920145216090](E:\黑马培训\Hadoop生态圈\assets\image-20220920145216090.png)



### 9. Hive如何将结构化数据映射成表

**过程**

- 在HDFS根目录下创建一个结构化数据文件user.txt，里面内容如下：

![image-20220920152442005](E:\黑马培训\Hadoop生态圈\assets\image-20220920152442005.png)

- 在hive中创建一张表t_user。注意：**字段的类型顺序要和文件中字段保持一致**。

![image-20220920152546299](E:\黑马培训\Hadoop生态圈\assets\image-20220920152546299.png)

> 注意: 在这里没有指定分隔符,导致hive识别不出来数据

**验证**

- 执行数据查询操作，发现表中并没有数据。

- **猜想1**：难道数据文件要放置在表对应的HDFS路径下才可以成功？

​    使用HDFS命令将数据移动到表对应的路径下。

![image-20220920152724087](E:\黑马培训\Hadoop生态圈\assets\image-20220920152724087.png)

- 再次执行查询操作，显示如下，都是null

​    好像表感知到结构化文件的存在，但是并没有正确识别文件中的数据。

- **猜想2**：还需要指定文件中字段之间的分隔符？

![image-20220920152923799](E:\黑马培训\Hadoop生态圈\assets\image-20220920152923799.png)

- 重建张新表，指定分隔符。

![image-20220920153004103](E:\黑马培训\Hadoop生态圈\assets\image-20220920153004103.png)

> 总结:想要hive能成功映射出结构化数据,要完成以下的操作
>
> 1.将结构化数据文件放在hive表的对应的文件夹下,作为它的数据
>
> 2.建hive表字段个数,类型要相对应,分隔符也要相对应,这样才能映射出表来

**错误操作使用示范**

- 此时再创建一张表，保存分隔符语法，但是故意使得字段类型和文件中不一致。

- 此时发现，有的列显示null，有的列显示正常。

​    name字段本身是字符串，但是建表的时候指定int，类型转换不成功；age是数值类型，建表指定字符串类型，    

​     可以转换成功。说明hive中具有自带的类型转换功能，但是不一定保证转换成功。

![image-20220920155044231](E:\黑马培训\Hadoop生态圈\assets\image-20220920155044231.png)

**结论**

- 要想在hive中创建表跟结构化文件映射成功，需要注意以下几个方面问题：

**创建表时，字段顺序、字段类型要和文件中保持一致**。如果类型不一致，hive会尝试转换，但是不保证转换成功。

不成功显示null。文件好像要放置在Hive表对应的HDFS目录下，其他路径可以吗？ 值得探讨。建表的时候好像要

根据文件内容指定分隔符，不指定可以吗？值得探讨。

- 其实上面这些问题都是Hive的各个知识点，有待于我们接下去不断深入学习

### 10. 使用Hive进行小数据分析如何

![image-20220920155537908](E:\黑马培训\Hadoop生态圈\assets\image-20220920155537908.png)

**配置小结**

**hive数据仓库保存数据的位置在哪里,存放在hdfs上,具体位置自己指定**

hive表存放位置模式是由hive-site.xml 当中的属性指定的

```xml
<name>hive.metastore.warehouse.dir</name>
<value>**/user/hive/warehouse**</value>
```

**查看hiveserver2**

ps -aux| grep hiveserver2

kill -9 进程号

## 8. Hive的使用

### 1. 常见的开发模式

 **Hive  CLI、Beeline CLI**

- Hive自带的命令行客户端

优点：不需要额外安装

缺点：编写SQL环境恶劣，无有效提示，无语法高亮，误操作几率高

- Hive CLI ==> 是第一代客户端 | Beeline CLI ==> 是JDBC客户端

**文本编辑器**

- Sublime、Emacs 、EditPlus、UltraEdit、Visual Studio Code等

  有些不支持作为客户端连接Hive服务，但是支持SQL语法环境，那就再编辑器中开发SQL,复制到Hive CLI执行；

  有些支持安装插件作为客户端直连Hive服务；

![image-20220920160745928](E:\黑马培训\Hadoop生态圈\assets\image-20220920160745928.png)

**Hive可视化工具**

- **IntelliJ** **IDEA**、DataGrip、Dbeaver、SQuirrel SQL Client等

可以在Windows、MAC平台中通过JDBC连接HiveServer2的图形界面工具；

这类工具往往专门针对SQL类软件进行开发优化、页面美观大方，操作简洁，更重要的是SQL编辑环境优雅；

SQL语法智能提示补全、关键字高亮、查询结果智能显示、按钮操作大于命令操作；

![image-20220920160820743](E:\黑马培训\Hadoop生态圈\assets\image-20220920160820743.png)

**Hive可视化工具IntelliJ IDEA**

![image-20220920160926506](E:\黑马培训\Hadoop生态圈\assets\image-20220920160926506.png)

![image-20220920161112275](E:\黑马培训\Hadoop生态圈\assets\image-20220920161112275.png)

![image-20220920161140080](E:\黑马培训\Hadoop生态圈\assets\image-20220920161140080.png)

![image-20220920161200188](E:\黑马培训\Hadoop生态圈\assets\image-20220920161200188.png)

![image-20220920161219145](E:\黑马培训\Hadoop生态圈\assets\image-20220920161219145.png)



### 2. Hive SQL DDL

#### 2.1 前言

**SQL中DDL语法的作用**

- 数据定义语言 (Data Definition Language, DDL)，是SQL语言集中对数据库内部的对象结构进行创建，删除，

  修改等的操作语言，这些数据库对象包括database（schema）、table、view、index等。

- DDL核心语法由**CREATE、ALTER与DROP**三个所组成。**DDL并不涉及表内部数据的操作**。

- 在某些上下文中，该术语也称为数据描述语言，因为它描述了数据库表中的字段和记录。

![image-20220920161545644](E:\黑马培训\Hadoop生态圈\assets\image-20220920161545644.png)

**Hive中DDL语法的使用**

- Hive SQL（HQL）与标准SQL的语法大同小异，基本相通，注意差异即可；

- 基于Hive的设计、使用特点，**HQL中create语法（尤其create table）将是学习掌握Hive DDL语法的重中**

  **之重**。建表是否成功直接影响数据文件是否映射成功，进而影响后续是否可以基于SQL分析数据。通俗点

  说，没有表，表没有数据，你用Hive分析什么呢？

- **选择正确的方向,往往比盲目努力重要**。



#### 2.2 Hive建表完整语法树

![image-20220920163252679](E:\黑马培训\Hadoop生态圈\assets\image-20220920163252679.png)

**注意事项**

- **蓝色字体**是建表语法的关键字，用于指定某些功能。

- **[ ]**中括号的语法表示可选。

- **|**表示使用的时候，左右语法二选一。

- 建表语句中的语法顺序要和语法树中顺序保持一致.

![image-20220920163358240](E:\黑马培训\Hadoop生态圈\assets\image-20220920163358240.png)

#### 2.3 建表语句 -- 表存在时忽略异常

> 不报错,没有if not exists时抛出错误,表已经存在

==IF NOT EXISTS==

```hive
--第一次创建表
0: jdbc:hive2://node1:10000> create table t_1(id int,name string,age int);
--再次执行
0: jdbc:hive2://node1:10000> create table t_1(id int,name string,age int);
Error: Error while processing statement: FAILED: Execution Error, return code 1 from org.apache.hadoop.hive.ql.exec.DDLTask. AlreadyExistsException(message:Table t_1 already exists) (state=08S01,code=1)
--Error while processing statement 执行期间的错误 往往就是逻辑错误

--加上if not exists忽略异常
0: jdbc:hive2://node1:10000> create table if not exists t_1(id int,name string,age int);

0: jdbc:hive2://node1:10000> creatf table t_1(id int,name string,age int);
Error: Error while compiling statement: FAILED: ParseException line 1:0 cannot recognize input near 'creatf' 'table' 't_1' (state=42000,code=40000)
0: jdbc:hive2://node1:10000> 
--Error while compiling statement 编译期间的错误  SQL语法问题


--hive的执行过程如下：

hivesql -->编译SQL成mr程序--->mr提交yarn执行
```

#### 2.4 Hive数据类型详解

**整体概述**

- Hive数据类型指的是表中列的字段类型；

- 整体分为两类：原生数据类型（primitive data type）和复杂数据类型（complex data type）。

  原生数据类型包括：数值类型、时间日期类型、字符串类型、杂项数据类型；

  复杂数据类型包括：array数组、map映射、struct结构、union联合体。

![image-20220920164522620](E:\黑马培训\Hadoop生态圈\assets\image-20220920164522620.png)

![image-20220920175908733](E:\黑马培训\Hadoop生态圈\assets\image-20220920175908733.png)

关于Hive的数据类型，需要注意：

- 英文字母大小写不敏感；

- 除SQL数据类型外，还支持Java数据类型，比如：string；

- int和string是使用最多的，大多数函数都支持；

- 复杂数据类型的使用通常需要和分隔符指定语法配合使用。

- 如果定义的数据类型和文件不一致，hive会尝试隐式转换，但是不保证成功。

**原生数据类型**

![image-20220920164810164](E:\黑马培训\Hadoop生态圈\assets\image-20220920164810164.png)



**复杂数据类型**

![image-20220920165007331](E:\黑马培训\Hadoop生态圈\assets\image-20220920165007331.png)

**注意事项**

- Hive SQL中，数据类型英文字母大小写不敏感；

- 除SQL数据类型外，还支持Java数据类型，比如字符串string；

- 复杂数据类型的使用通常需要和分隔符指定语法配合使用；

- 如果定义的数据类型和文件不一致，Hive会尝试隐式转换，但是不保证成功。

![image-20220920170037108](E:\黑马培训\Hadoop生态圈\assets\image-20220920170037108.png)

**隐式转换**

- 与标准SQL类似，HQL支持隐式和显式类型转换。 

- 原生类型从窄类型到宽类型的转换称为隐式转换，反之，则不允许。 

- 下表描述了类型之间允许的隐式转换：

![hive支持的隐式转换](E:\黑马培训\Hadoop生态圈\assets\hive支持的隐式转换.png)

**显示转换**

- 显示式类型转换使用CAST函数。

例如，CAST（'100'as INT）会将100字符串转换为100整数值。 

如果强制转换失败，例如CAST（‘Allen'as INT），该函数返回NULL。

![image-20220920170235068](E:\黑马培训\Hadoop生态圈\assets\image-20220920170235068.png)

- Hive除了支持SQL类型之外，==还支持java数据类型==；

- Hive除了支持基础数据类型之外，还==支持复合类型==（array数组(相同类型的数据)  map映射）；

  - 针对复合类型的数据 要想直接从文件中解析成功 还必须配合分隔符指定的语法。
  - ![image-20220920171253001](E:\黑马培训\Hadoop生态圈\assets\image-20220920171253001.png)

- Hive中大小写不敏感；

- 在建表的时候，最好==表的字段类型要和文件中的类型保持一致==，

  - 如果不一致，Hive会尝试进行==类型隐式转换==，不保证转换成功，如果不成功，显示null;

- 栗子

```hive
--创建数据库并切换使用
create database if not exists itheima;
use itheima;

--建表
create table t_archer(
      id int comment "ID",
      name string comment "英雄名称",
      hp_max int comment "最大生命",
      mp_max int comment "最大法力",
      attack_max int comment "最高物攻",
      defense_max int comment "最大物防",
      attack_range string comment "攻击范围",
      role_main string comment "主要定位",
      role_assist string comment "次要定位"
) comment "王者荣耀射手信息"
row format delimited
fields terminated by "\t";

--查看表数据 查看表的元数据信息
select * from t_archer;
desc formatted t_archer;

--上传文件到表对应的HDFS目录下  t_archer是表的文件夹
[root@node1 hivedata]# hadoop fs -put archer.txt /user/hive/warehouse/itheima.db/t_archer

[root@node1 hiedata]# pwd
/root/hivedata
```

#### 2.5 Hive读写文件机制

**SerDe是什么**

- SerDe是Serializer、Deserializer的简称，目的是用于序列化和反序列化。

- 序列化是对象转化为字节码的过程；而反序列化是字节码转换为对象的过程。

- Hive使用SerDe（包括FileFormat）读取和写入表行对象。需要注意的是，“key”部分在读取时会被忽略，而在

  写入时key始终是常数。基本上**行对象存储在“value”中**。

**Hive读写文件流程**

- Hive读取文件机制：首先调用InputFormat（默认TextInputFormat），返回一条一条kv键值对记录（默认是

  一行对应一条键值对）。然后调用SerDe（默认LazySimpleSerDe）的Deserializer，将一条记录中的value根

  据分隔符切分为各个字段。

- Hive写文件机制：将Row写入文件时，首先调用SerDe（默认LazySimpleSerDe）的Serializer将对象转换成

  字节序列，然后调用OutputFormat将数据写入HDFS文件中。

**流程图示**

![image-20220920172216177](E:\黑马培训\Hadoop生态圈\assets\image-20220920172216177.png)

**序列化与反序列化流程示意图**

![Serialization vs Deserialization](E:\黑马培训\Hadoop生态圈\assets\Serialization vs Deserialization.jpg)

- 可以通过desc formatted tablename查看表的相SerDe信息。默认如下：

![image-20220920173156554](E:\黑马培训\Hadoop生态圈\assets\image-20220920173156554.png)

**SerDe相关语法**

- ROW FORMAT这一行所代表的是跟读写文件、序列化SerDe相关的语法，功能有二：

- 使用哪个SerDe类进行序列化；

- 如何指定分隔符。

![image-20220920173458467](E:\黑马培训\Hadoop生态圈\assets\image-20220920173458467.png)

- 其中ROW FORMAT是语法关键字，DELIMITED和SERDE二选其一。

- 如果**使用delimited表示使用默认的LazySimpleSerDe类来处理数据**。

- 如果数据文件格式比较特殊可以使用ROW FORMAT SERDE serde_name指定其他的Serde类来处理数据,甚至

  支持用户自定义SerDe类。

![1、hive建表--序列化类指定](E:\黑马培训\Hadoop生态圈\assets\1、hive建表--序列化类指定.png)

**LazySimpleSerDe分隔符指定**

- LazySimpleSerDe是Hive默认的序列化类，包含4种子语法，分别用于指定字段之间、集合元素之间、map映

  射 kv之间、换行的分隔符号。

- 在建表的时候可以根据数据的特点灵活搭配使用。

![image-20220920175401171](E:\黑马培训\Hadoop生态圈\assets\image-20220920175401171.png)

**Hive默认分隔符**

- Hive建表时如果没有row format语法指定分隔符，则采用默认分隔符；

- **默认的分割符是'\001'**，是一种特殊的字符，使用的是ASCII编码的值，键盘是打不出来的。

![image-20220920175541744](E:\黑马培训\Hadoop生态圈\assets\image-20220920175541744.png)

- 在vim编辑器中，连续按下Ctrl+v/Ctrl+a即可输入'\001' ，显示^A
- 在一些文本编辑器中将以**SOH**的形式显示

![image-20220920175629820](E:\黑马培训\Hadoop生态圈\assets\image-20220920175629820.png)



#### 2.6 Hive数据存储路径

**默认存储路径**

Hive表默认存储路径是由${HIVE_HOME}/conf/hive-site.xml配置文件的hive.metastore.warehouse.dir属性指

定。默认值是：/user/hive/warehouse。

![image-20220920194545430](E:\黑马培训\Hadoop生态圈\assets\image-20220920194545430.png)

在该路径下，文件将根据所属的库、表，有规律的存储在对应的文件夹下

![image-20220920195333246](E:\黑马培训\Hadoop生态圈\assets\image-20220920195333246.png)

**指定存储路径**

Hive建表的时候，可以通过**location语法来更改数据在HDFS上的存储路径**，使得建表加载数据更加灵活方便。

语法：`LOCATION '<hdfs_location>'``。```

对于已经生成好的数据文件，使用location指定路径将会很方便。

#### 2.7 实体案例操作

**原生数据类型案例**

文件archer.txt中记录了手游《王者荣耀》射手的相关信息，内容如下所示，其中字段之间分隔符为制表符\t,要求

在Hive中建表映射成功该文件

```xml
1	后羿	5986	1784	396	336	remotely	archer
2	马可波罗	5584	200	362	344	remotely	archer
3	鲁班七号	5989	1756	400	323	remotely	archer
4	李元芳	5725	1770	396	340	remotely	archer
5	孙尚香	6014	1756	411	346	remotely	archer
6	黄忠	5898	1784	403	319	remotely	archer
7	狄仁杰	5710	1770	376	338	remotely	archer
8	虞姬	5669	1770	407	329	remotely	archer
9	成吉思汗	5799	1742	394	329	remotely	archer
10	百里守约	5611	1784	410	329	remotely	archer	assassin

```

**要想成功映射此文件需要按以下步骤执行**

1.查看数据文件,确定**每个字段的类型**和**字段的之间的分割符**,来创建hive数据表

**建表语句:**

```hive
create table if not exists t_archer(
id int comment "ID",
hero_name string,
hp_max int,
mp_max int,
attack_max int,
defense_max int,
attack_range string,
role_main string,
role_assist string
)
row format delimited
fields terminated by "\t"
```



2.登录yarn集群的web服务端查看对应表下的路径

![image-20220920212301619](E:\黑马培训\Hadoop生态圈\assets\image-20220920212301619.png)

> 路径末尾加/与不加/都是进入这个文件夹下 如下图所示



3.把查看的路径复制进虚拟机等待使用命令上传文件

> 在本机三台机器默认使用的hdfs系统都是hadoop200上的hdfs系统

**上传指令图示**

![image-20220920212925791](E:\黑马培训\Hadoop生态圈\assets\image-20220920212925791.png)

4.所有步骤准备完成,可以使用命令查看表是否映射成功

![image-20220920213025641](E:\黑马培训\Hadoop生态圈\assets\image-20220920213025641.png)



**复杂数据类型操作案例**

文件hot_hero_skin_price.txt中记录了手游《王者荣耀》热门英雄的相关皮肤价格信息，内容如下,要求在Hive中

建表映射成功该文件。

![image-20220920213319452](E:\黑马培训\Hadoop生态圈\assets\image-20220920213319452.png)

**描述**

字段：id、name（英雄名称）、win_rate（胜率）、skin_price（皮肤及价格）

分析一下：前3个字段原生数据类型、最后一个字段复杂类型map。需要指定字段之间分隔符、集合元素之间分隔符、map kv之间分隔符。

**建表语句**：

```hive
create table if not exists hero_skin(
id int comment "ID",
hero_name string,
victory_rate int,
skin_price map<string,int>
)
row format delimited
fields terminated by ","
collection items terminated by "-"
map keys terminated by ":"
```

2.web ui 界面查看数据表的路径

![image-20220920220323497](E:\黑马培训\Hadoop生态圈\assets\image-20220920220323497.png)

3.利用hadoop fs的命令传输文件进入hdfs ==>  建表成功后，把hot_hero_skin_price.txt文件上传到对应的表文件

夹下。

![image-20220920220207660](E:\黑马培训\Hadoop生态圈\assets\image-20220920220207660.png)

**查看映射结果**

![image-20220920220431352](E:\黑马培训\Hadoop生态圈\assets\image-20220920220431352.png)

![image-20220920220537393](E:\黑马培训\Hadoop生态圈\assets\image-20220920220537393.png)



**默认分割符案例**

文件team_ace_player.txt中记录了手游《王者荣耀》主要战队内最受欢迎的王牌选手信息，内容如下,要求在Hive

中建表映射成功该文件。

![image-20220920221002606](E:\黑马培训\Hadoop生态圈\assets\image-20220920221002606.png)

字段：id、team_name（战队名称）、ace_player_name（王牌选手名字）

分析一下：数据都是原生数据类型，且字段之间分隔符是\001，因此在建表的时候可以省去row format语句，因

为hive默认的分隔符就是\001。

**建表语句**：

```hive
create table if not exists player_list(
id int,
player_club string,
player_name string
)
row format delimited
```

建表以后去,web端查看相应的表内的路径,然后将相应的文件上传到对应的表文件夹下

上传数据指令

![image-20220920224858318](E:\黑马培训\Hadoop生态圈\assets\image-20220920224858318.png)

上传数据前对应的表内数据文件为空

![image-20220920224748366](E:\黑马培训\Hadoop生态圈\assets\image-20220920224748366.png)

上传后

![image-20220920224813117](E:\黑马培训\Hadoop生态圈\assets\image-20220920224813117.png)

查看映射结果

![image-20220920224937611](E:\黑马培训\Hadoop生态圈\assets\image-20220920224937611.png)

启发:![image-20220920225152312](E:\黑马培训\Hadoop生态圈\assets\image-20220920225152312.png)

![image-20220920225224454](E:\黑马培训\Hadoop生态圈\assets\image-20220920225224454.png)

![image-20220920225238728](E:\黑马培训\Hadoop生态圈\assets\image-20220920225238728.png)

### 3. Hive DDL 建表高阶

#### 1. 内部表与外部表

![image-20220920225330390](E:\黑马培训\Hadoop生态圈\assets\image-20220920225330390.png)

> 建表时不写external,建立的表就是内部表

**内部表**

- **内部表**（Internal table）也称为被Hive拥有和管理的**托管表（Managed table）**。

- 默认情况下创建的表就是内部表，Hive拥有该表的结构和文件。换句话说，Hive完全管理表（元数据和数据）

  的生命周期，类似于RDBMS中的表。

- 当您删除内部表时，它会删除数据以及表的元数据。

![image-20220920225722152](E:\黑马培训\Hadoop生态圈\assets\image-20220920225722152.png)

- 可以使用DESCRIBE FORMATTED tablename,来获取表的元数据描述信息，从中可以看出表的类型。

![image-20220920225747162](E:\黑马培训\Hadoop生态圈\assets\image-20220920225747162.png)



**外部表**

- **外部表**（External table）中的数据不是Hive拥有或管理的，只管理表元数据的生命周期。

- 要创建一个外部表，需要使用**EXTERNAL**语法关键字。

- 删除外部表只会删除元数据，而不会删除实际数据。在Hive外部仍然可以访问实际数据。

- 实际场景中，外部表搭配location语法指定数据的路径，可以让数据更安全。

![image-20220920225943962](E:\黑马培训\Hadoop生态圈\assets\image-20220920225943962.png)

- 可以使用DESCRIBE FORMATTED tablename,来获取表的元数据描述信息，从中可以看出表的类型。



**内外部表的差异**

无论内部表还是外部表，Hive都在Hive Metastore中管理表定义及其分区信息。删除内部表会从Metastore中删除

表元数据，还会从HDFS中删除其所有数据/文件。

**删除外部表，只会从Metastore中删除表的元数据**，并保持HDFS位置中的实际数据不变。

![image-20220920231333748](E:\黑马培训\Hadoop生态圈\assets\image-20220920231333748.png)

![image-20220920231547734](E:\黑马培训\Hadoop生态圈\assets\image-20220920231547734.png)

![image-20220920231610952](E:\黑马培训\Hadoop生态圈\assets\image-20220920231610952.png)



**如何选择内部表,外部表**

当需要通过Hive完全管理控制表的整个生命周期时，请使用内部表。

当文件已经存在或位于远程位置时，请使用外部表，因为即使删除表，文件也会被保留。



**Location关键字的作用**

- 在创建外部表的时候，可以使用location指定存储位置路径，如果不指定会如何？

​     如果不指定location，外部表的默认路径也是位于/user/hive/warehouse，由默认参数控制。

- 创建内部表的时候，是否可以使用location指定？

​    内部表可以使用location指定位置的。

- 是否意味着Hive表的数据在HDFS上的位置不是一定要在/user/hive/warehouse下？

  不一定，Hive中表数据存储位置，不管内部表还是外部表，默认都是在/user/hive/warehouse，当然可以在建

表的时候通过location关键字指定存储位置在HDFS的任意路径。

#### 2. Hive Partitioned Table

> 分区表

##### 1. 分区表的引入、产生背景

现有6份数据文件，分别记录了《王者荣耀》中6种位置的英雄相关信息。现要求通过建立一张表**t_all_hero**，把6

份文件同时映射加载。

```hive
create table t_all_hero(
    id int,
    name string,
    hp_max int,
    mp_max int,
    attack_max int,
    defense_max int,
    attack_range string,
    role_main string,
    role_assist string
)
row format delimited
fields terminated by "\t";
```

加载数据文件到HDFS指定路径下：

![image-20220920235306897](E:\黑马培训\Hadoop生态圈\assets\image-20220920235306897.png)                            

现要求查询role_main主要定位是射手并且hp_max最大生命大于6000的有几个，sql语句如下：

```hive
  select count(*) from t_all_hero where role_main="archer" and hp_max >6000;  
```

**结果示意图**

![image-20220920235600938](E:\黑马培训\Hadoop生态圈\assets\image-20220920235600938.png)

> 因为进行了全表的扫描查询,所以速度非常慢

思考一下：where语句的背后需要进行全表扫描才能过滤出结果，对于hive来说需要扫描表下面的每一个文件。如

果数据文件特别多的话，效率很慢也没必要。本需求中，只需要扫描archer.txt文件即可，如何优化可以加快查

询，减少全表扫描呢？

**提出问题**

![image-20220920235732790](E:\黑马培训\Hadoop生态圈\assets\image-20220920235732790.png)



##### 2. 分区表的概念、创建

当Hive表对应的数据量大、文件多时，为了避免查询时全表扫描数据，Hive支持根据用户指定的字段进行分区，

分区的字段可以是**日期、地域、种类**等具有标识意义的字段。比如把一整年的数据根据月份划分12个月（12个分

区），后续就可以查询指定月份分区的数据，尽可能避免了全表扫描查询。

![image-20220920235913518](E:\黑马培训\Hadoop生态圈\assets\image-20220920235913518.png)

![image-20220920235943743](E:\黑马培训\Hadoop生态圈\assets\image-20220920235943743.png)

**分区表建表语法**：

```hive
CREATE TABLE table_name (
column1 data_type, 
column2 data_type) 
PARTITIONED BY (partition1 data_type, partition2 data_type,….);
```

> 注意partition by 是用在序列化之前的
>
> 注意：分区字段不能是表中已经存在的字段，因为**分区字段**最终也会以**虚拟字段的形式显示在表结构上**



##### 3. 实例操作分区表

针对《王者荣耀》英雄数据，重新创建一张分区表**t_all_hero_part**，以role角色作为分区字段

```hive
create table t_all_hero_part(
       id int,
       name string,
       hp_max int,
       mp_max int,
       attack_max int,
       defense_max int,
       attack_range string,
       role_main string,
       role_assist string
) partitioned by (role string)
row format delimited
fields terminated by "\t";

```

**需要注意：分区字段不能是表中已经存在的字段，因为分区字段最终也会以虚拟字段的形式显示在表结构上**

**图示**

![image-20220921001731449](E:\黑马培训\Hadoop生态圈\assets\image-20220921001731449.png)

###### 1. 分区表数据加载--静态分区

所谓**静态分区**指的是分区的字段值是由用户在加载数据的时候手动指定的。

语法如下：

```hive
load data [local] inpath ' ' into table tablename partition(分区字段='分区值'...);
```

Local表示数据是位于本地文件系统还是HDFS文件系统。关于load语句后续详细展开讲解。

静态加载数据操作如下，文件都位于Hive服务器所在机器本地文件系统上。

> 加载的一个文件就是一个分区字段的值，不可能用load加载一个文件指定多个分区 == >这个想法是错误的

![image-20220921002551292](E:\黑马培训\Hadoop生态圈\assets\image-20220921002551292.png)

**选择字段指定好具体分区,向分区表中加载数据**

```hive
load data local inpath '/root/hivedata/archer.txt' into table t_all_hero_part partition(role='sheshou');
load data local inpath '/root/hivedata/assassin.txt' into table t_all_hero_part partition(role='cike');
load data local inpath '/root/hivedata/mage.txt' into table t_all_hero_part partition(role='fashi');
load data local inpath '/root/hivedata/support.txt' into table t_all_hero_part partition(role='fuzhu');
load data local inpath '/root/hivedata/tank.txt' into table t_all_hero_part partition(role='tanke');
load data local inpath '/root/hivedata/warrior.txt' into table t_all_hero_part partition(role='zhanshi');

```

**这样做的意义何在?**

一个一个的把分区数据导入分区表中,在表中自动形成了分区，在操作时选择分区字段进行筛选,优化了查询速度

**弊端**

在数据量小的时候，尚可这样操作加载分区数据，数据量大的时候，就整个人升天

**如何解决？**

采取动态分区的方式加载数据



###### 2. 分区表数据加载--动态分区

==要注意系统是根据查询字段的位置推断分区名的，而不是字段名称。==

往hive分区表中插入加载数据时，如果需要创建的分区很多，则需要复制粘贴修改很多sql去执行，效率低。因为

hive是批处理系统，所以hive提供了一个动态分区功能，其可以基于查询参数的位置去推断分区的名称，从而建立

分区。

所谓**动态分区**指的是分区的字段值是基于查询结果自动推断出来的。**核心语法就是insert+select**。

启用hive动态分区，需要在hive会话中设置两个参数：

```hive
set hive.exec.dynamic.partition=true;

set hive.exec.dynamic.partition.mode=nonstrict;
```

第一个参数表示开启动态分区功能(hive3 默认已经开启)

第二个参数指定动态分区的模式。分为nonstick非严格模式和strict严格模式。strict严格模式要求至少有一个分区

为静态分区。

创建一张新的分区表**t_all_hero_part_dynamic**

```hive
create table t_all_hero_part_dynamic(
         id int,
         name string,
         hp_max int,
         mp_max int,
         attack_max int,
         defense_max int,
         attack_range string,
         role_main string,
         role_assist string
) partitioned by (role string)
row format delimited
fields terminated by "\t";

```

**执行动态分区插入**

```hive
insert into table t_all_hero_part_dynamic partition(role) select tmp.*,tmp.role_main from t_all_hero tmp;
```

> 动态分区 == >也只是对一个字段的多种类别的值进行分区

动态分区插入时，分区值是**根据查询返回字段位置自动推断的**。

###### 3. 分区表的本质

外表上看起来分区表好像没多大变化，只不过多了一个分区字段。实际上在底层管理数据的方式发生了改变。这里

直接去HDFS查看区别。

非分区表：**t_all_hero**

![image-20220921004501117](E:\黑马培训\Hadoop生态圈\assets\image-20220921004501117.png)

分区表：**t_all_hero_part**

![image-20220921004516193](E:\黑马培训\Hadoop生态圈\assets\image-20220921004516193.png)

![image-20220921004531027](E:\黑马培训\Hadoop生态圈\assets\image-20220921004531027.png)

分区的概念提供了一种将Hive表数据分离为多个文件/目录的方法。

**不同分区对应着不同的文件夹，同一分区的数据存储在同一个文件夹下**。

只需要根据分区值找到对应的文件夹，扫描本分区下的文件即可，避免全表数据扫描。

###### 4. 分区表的使用

区表的使用重点在于：

一、建表时根据业务场景设置合适的分区字段。比如日期、地域、类别等；

二、查询的时候尽量先使用where进行分区过滤，查询指定分区的数据，避免全表扫描。

比如：查询英雄主要定位是射手并且最大生命大于6000的个数。使用分区表查询和使用非分区表进行查询，SQL如下：

```hive
--非分区表 全表扫描过滤查询
select count(*) from t_all_hero where role_main="archer" and hp_max >6000;
--分区表 先基于分区过滤 再查询
select count(*) from t_all_hero_part where role="sheshou" and hp_max >6000;

```

想一想：底层执行性能来说，分区表的优势在哪里？



###### 5. 分区表的注意事项

一、分区表不是建表的必要语法规则，是一种**优化手段**表，可选；

二、分区**字段不能是表中已有的字段**，不能重复；

三、分区字段是**虚拟字段**，其数据并不存储在底层的文件中；

四、分区字段值的确定来自于用户价值数据手动指定（**静态分区**）或者根据查询结果位置自动推断（**动态分区**）

五、Hive**支持多重分区**，也就是说在分区的基础上继续分区，划分更加细粒度

###### 6. 多重分区表

通过建表语句中关于分区的相关语法可以发现，Hive支持多个分区字段：

PARTITIONED BY (partition1 data_type, partition2 data_type,….)。

多重分区下，**分区之间是一种递进关系，可以理解为在前一个分区的基础上继续分区**。

从HDFS的角度来看就是**文件夹下继续划分子文件夹**。

比如：把全国人口数据首先根据省进行分区，然后根据市进行划分，如果你需要甚至可以继续根据区县再划分，此

时就是3分区表。

```hive
--单分区表，按省份分区
create table t_user_province (id int, name string,age int) partitioned by (province string);

--双分区表，按省份和市分区
create table t_user_province_city (id int, name string,age int) partitioned by (province string, city string);

--三分区表，按省份、市、县分区
create table t_user_province_city_county (id int, name string,age int) partitioned by (province string, city string,county string);

```

**多分区表的数据插入和查询使用**

```hive
load data local inpath '文件路径' into table t_user_province partition(province='shanghai');

load data local inpath '文件路径' into table t_user_province_city_county partition(province='zhejiang',city='hangzhou',county='xiaoshan');

select * from t_user_province_city_county where province='zhejiang' and city='hangzhou';

```

**动态插入语法如何？**

多个分区字段时，所有实现动态分区插入数据

```sql
set hive.exec.dynamici.partition=true;

set hive.exec.dynamic.partition.mode=nonstrict;

insert overwrite table ds_parttion

partition(state,ct)

select id ,**country**,**city** from mytest_tmp2_p;
```

注意：字段的个数和顺序不能弄错。

动态分区表的属性

使用动态分区表必须配置的参数 ：

```sql
set hive.exec.dynamic.partition =true（默认false）,表示开启动态分区功能

set hive.exec.dynamic.partition.mode = nonstrict(默认strict),表示容许全部分区都是动态的，不然必

须有静态分区字段

​ 动态分区相关的调优参数：

set hive.exec.max.dynamic.partitions.pernode=100 （默认100，通常能够设置大一点，好比1000）

​ 表示每一个maper或reducer能够容许建立的最大动态分区个数，默认是100，超出则会报错。

set hive.exec.max.dynamic.partitions =1000(默认值)

​ 表示一个动态分区语句能够建立的最大动态分区个数，超出报错

set hive.exec.max.created.files =10000(默认) 全局能够建立的最大文件个数，超出报错。
```



#### 3. Hive Bucketed Tables 

> 分桶表

##### 1. 概念

- **分桶表**也叫做桶表，叫法源自建表语法中bucket单词，是一种用于优化查询而设计的表类型。

- 分桶表对应的数据文件在底层会被分解为若干个部分，通俗来说就是被拆分成若干个独立的小文件。

- 在分桶时，要指定根据哪个字段将数据分为几桶（几个部分）。

![image-20220921010019993](E:\黑马培训\Hadoop生态圈\assets\image-20220921010019993.png)

##### 2. 规则

- 分桶规则如下：桶编号相同的数据会被分到同一个桶当中。

![image-20220921010115122](E:\黑马培训\Hadoop生态圈\assets\image-20220921010115122.png)

- hash_function取决于分桶字段bucketing_column的类型：

1.如果是int类型，hash_function(int) == int;

2.如果是其他比如bigint,string或者复杂数据类型，hash_function比较棘手，将是从该类型派生的某个数字，比如hashcode值。

![image-20220921010202950](E:\黑马培训\Hadoop生态圈\assets\image-20220921010202950.png)

![image-20220921010217273](E:\黑马培训\Hadoop生态圈\assets\image-20220921010217273.png)

##### 3. 语法

- CLUSTERED BY (col_name)表示根据哪个字段进行分；

- INTO N BUCKETS表示分为几桶（也就是几个部分）。

- 需要注意的是，**分桶的字段必须是表中已经存在的字段**。

![image-20220921010312884](E:\黑马培训\Hadoop生态圈\assets\image-20220921010312884.png)

##### 4. 案例分析

- 现有美国2021-1-28号，各个县county的新冠疫情累计案例信息，包括确诊病例和死亡病例，数据格式如下所示：

- 字段含义：count_date（统计日期）,county（县）,state（州）,fips（县编码code）,cases（累计确诊病

  例）,deaths（累计死亡病例）。

![image-20220921010634483](E:\黑马培训\Hadoop生态圈\assets\image-20220921010634483.png)

**创建分桶表**

- 根据state州把数据分为5桶，建表语句如下：

![image-20220921010754572](E:\黑马培训\Hadoop生态圈\assets\image-20220921010754572.png)

- 在创建分桶表时，还可以指定分桶内的数据排序规则：

![image-20220921011010513](E:\黑马培训\Hadoop生态圈\assets\image-20220921011010513.png)

**分桶表的数据加载**

![image-20220921010918499](E:\黑马培训\Hadoop生态圈\assets\image-20220921010918499.png)

- 到HDFS上查看t_usa_covid19_bucket底层数据结构可以发现，数据被分为了5个部分。

- 并且从结果可以发现，分桶字段一样的数据就一定被分到同一个桶中。

![image-20220921011043443](E:\黑马培训\Hadoop生态圈\assets\image-20220921011043443.png)

##### 5. 优点

- （1/3）基于分桶字段查询时，减少全表扫描

![image-20220921011131810](E:\黑马培训\Hadoop生态圈\assets\image-20220921011131810.png)

- （2/3）JOIN时可以提高MR程序效率，减少笛卡尔积数量

**根据join的字段对表进行分桶操作**（比如下图中id是join的字段）

![image-20220921011246424](E:\黑马培训\Hadoop生态圈\assets\image-20220921011246424.png)

- （3/3）分桶表数据进行**高效抽样**

当数据量特别大时，对全体数据进行处理存在困难时，抽样就显得尤其重要了。抽样可以从被抽取的数据中估计和

推断出整体的特性，是科学实验、质量检验、社会调查普遍采用的一种经济有效的工作和研究方法。

### 4. Hive SQL DDL 其他语法

#### 4.1 Database|Schema（数据库）DDL操作

**整体概述**

- 在Hive中，DATABASE的概念和RDBMS中类似，我们称之为数据库，**DATABASE和SCHEMA是可互换的**，都可以使用。

- 默认的数据库叫做default，存储数据位置位于/user/hive/warehouse下。

- 用户自己创建的数据库存储位置是/user/hive/warehouse/database_name.db下。

> 这就可以说明，为什么一进hive时，没有指定使用哪个数据库时，就去建表，表的文件会存放
>
> 在/user/hive/warehouse下，与数据库文件放在一起



##### 1. create database

COMMENT：数据库的注释说明语句

LOCATION：指定数据库在HDFS存储位置，默认/user/hive/warehouse/dbname.db

WITH DBPROPERTIES：用于指定一些数据库的属性配置。

![image-20220921012625106](E:\黑马培训\Hadoop生态圈\assets\image-20220921012625106.png)



##### 2. describe database

- 显示Hive中数据库的名称，注释（如果已设置）及其在文件系统上的位置等信息。

**EXTENDED**关键字用于显示更多信息。可以将关键字describe简写成desc使用。

![image-20220921012802211](E:\黑马培训\Hadoop生态圈\assets\image-20220921012802211.png)

![image-20220921012816821](E:\黑马培训\Hadoop生态圈\assets\image-20220921012816821.png)



##### 3. use database

- **选择特定的数据库**

切换当前会话使用哪一个数据库进行操作

![image-20220921012906972](E:\黑马培训\Hadoop生态圈\assets\image-20220921012906972.png)



##### 4. drop database

- **删除数据库**

默认行为是RESTRICT，这意味着仅在数据库为空时才删除它。

要删除带有表的数据库（不为空的数据库），我们可以使用CASCADE。

![image-20220921012958469](E:\黑马培训\Hadoop生态圈\assets\image-20220921012958469.png)

![image-20220921013006635](E:\黑马培训\Hadoop生态圈\assets\image-20220921013006635.png)



##### 5. alter database

- 更改与Hive中的数据库关联的元数据

![image-20220921013109590](E:\黑马培训\Hadoop生态圈\assets\image-20220921013109590.png)

![image-20220921013118515](E:\黑马培训\Hadoop生态圈\assets\image-20220921013118515.png)



#### 4.2 Table（表）DDL操作

**整体概述**

- Hive中针对表的DDL操作可以说是DDL中的核心操作，包括**建表**、**修改表**、**删除表**、**描述表元数据信息**。

- 其中以建表语句为核心中的核心，详见Hive DDL建表语句。

- 可以说表的定义是否成功直接影响着数据能够成功映射，进而影响是否可以顺利的使用Hive开展数据分析。

- 由于Hive建表之后加载映射数据很快，实际中如果建表有问题，可以不用修改，直接删除重建。



##### 1. describe table

- 显示Hive中表的元数据信息

如果指定了EXTENDED关键字，则它将以Thrift序列化形式显示表的所有元数据。

如果指定了**FORMATTED**关键字，则它将以表格格式显示元数据。

![image-20220921013428970](E:\黑马培训\Hadoop生态圈\assets\image-20220921013428970.png)



##### 2. drop table

- 删除该表的元数据和数据

如果已配置垃圾桶且未指定PURGE，则该表对应的数据实际上将移动到HDFS垃圾桶，Trash/Current目录，而元数据完全丢失。

删除EXTERNAL表时，该表中的数据不会从文件系统中删除，只删除元数据。

如果指定了PURGE，则表数据跳过HDFS垃圾桶直接被删除。因此如果DROP失败，则无法挽回该表数据。

![image-20220921013532427](E:\黑马培训\Hadoop生态圈\assets\image-20220921013532427.png)



##### 3. truncate table

- 从表中删除所有行。

可以简单理解为清空表的所有数据但是保留表的元数据结构。

如果HDFS启用了垃圾桶，数据将被丢进垃圾桶，否则将被删除。

![image-20220921013839052](E:\黑马培训\Hadoop生态圈\assets\image-20220921013839052.png)



##### 4. alter table

![image-20220921013909897](E:\黑马培训\Hadoop生态圈\assets\image-20220921013909897.png)

![image-20220921013918192](E:\黑马培训\Hadoop生态圈\assets\image-20220921013918192.png)



#### 4.3 Hive Partition（分区）DDL操作

**整体概述**

- Hive中针对分区Partition的操作主要包括：增加分区、删除分区、重命名分区、修复分区、修改分区。

##### 1. add partition

- ADD PARTITION会更改表元数据，但不会加载数据。如果分区位置中不存在数据，查询时将不会返回结果。

- 因此需要保证增加的分区位置路径下，数据已经存在，或者增加完分区之后导入分区数据。

![image-20220921014259894](E:\黑马培训\Hadoop生态圈\assets\image-20220921014259894.png)



##### 2. rename partition

![image-20220921014324672](E:\黑马培训\Hadoop生态圈\assets\image-20220921014324672.png)



##### 3. delete partition

- 删除表的分区。这将删除该分区的数据和元数据。

![image-20220921014404130](E:\黑马培训\Hadoop生态圈\assets\image-20220921014404130.png)



##### 4. alter partition

![image-20220921014433664](E:\黑马培训\Hadoop生态圈\assets\image-20220921014433664.png)



##### 5. MSCK partition背景

- Hive将每个表的分区列表信息存储在其metastore中。但是，如果将新分区直接添加到HDFS（例如通过使用hadoop fs -put命令）或从HDFS中直接删除分区文件夹，则除非用户ALTER TABLE table_name ADD/DROP PARTITION在每个新添加的分区上运行命令，否则metastore（也就是Hive）将不会意识到分区信息的这些更改。

- MSCK是metastore check的缩写，表示元数据检查操作，可用于元数据的修复。

![image-20220921014519466](E:\黑马培训\Hadoop生态圈\assets\image-20220921014519466.png)

- MSCK默认行为ADD PARTITIONS，使用此选项，它将把HDFS上存在但元存储中不存在的所有分区添加到metastore。

- DROP PARTITIONS选项将从已经从HDFS中删除的metastore中删除分区信息。

- SYNC PARTITIONS选项等效于调用ADD和DROP PARTITIONS。

- 如果存在大量未跟踪的分区，则可以批量运行MSCK REPAIR TABLE，以避免OOME（内存不足错误）。

![image-20220921014600942](E:\黑马培训\Hadoop生态圈\assets\image-20220921014600942.png)

![image-20220921014633572](E:\黑马培训\Hadoop生态圈\assets\image-20220921014633572.png)

**MSCK ADD PARTITIONS**

![image-20220921014718313](E:\黑马培训\Hadoop生态圈\assets\image-20220921014718313.png)

**MSCK DROP PARTITIONS**

![image-20220921014737566](E:\黑马培训\Hadoop生态圈\assets\image-20220921014737566.png)



##### 6. 常用show语法

![image-20220921014921904](E:\黑马培训\Hadoop生态圈\assets\image-20220921014921904.png)

![image-20220921014944548](E:\黑马培训\Hadoop生态圈\assets\image-20220921014944548.png)



### 5. Hive SQL DML

#### 5.1 load加载数据

load加载数据的时候不做任何转换工作，而是纯粹的把数据文件复制/移动到Hive表的对应地址，也就是说文件里

面什么样子，加载到hive中也是什么样子

**回顾**

- 在Hive中建表成功之后，就会在HDFS上创建一个与之对应的文件夹，且文件夹名字就是表名；

- 文件夹父路径是由参数hive.metastore.warehouse.dir控制，默认值是/user/hive/warehouse；

- 也可以在建表的时候使用location语句指定任意路径。

![image-20220921015155122](E:\黑马培训\Hadoop生态圈\assets\image-20220921015155122.png)

- 不管路径在哪里，只有把数据文件移动到对应的表文件夹下面，Hive才能映射解析成功;

- 最原始暴力的方式就是使用hadoop fs –put|-mv等方式直接将数据移动到表文件夹下；

- 但是，Hive官方推荐使用Load命令将数据加载到表中。

![image-20220921015232544](E:\黑马培训\Hadoop生态圈\assets\image-20220921015232544.png)

**功能**

- Load英文单词的含义为：加载、装载；

- 所谓加载是指：将数据文件移动到与Hive表对应的位置，移动时是纯复制、移动操作。

- 纯复制、移动指在数据load加载到表中时，Hive不会对表中的数据内容进行任何转换，任何操作。

**语法规则**

![image-20220921020220776](E:\黑马培训\Hadoop生态圈\assets\image-20220921020220776.png)

**语义解释**

```shell
1. 使用load加载数据

语法规则:
            1. load data [local] inpath 'filepath' [overwrite] into table tablename [partition(partcol1=val1, partcol2=val2 ...)] [inputformat '上传格式化的格式' serde '序列化规则'] (3.0)后的支持

参数详解: 
           1. 可选参数[local]: 指定local时代表的是本地文件系统,即虚拟机本身的文件系统, 不指定local时则是hadoop200上的hdfs文件系统
  
           2. 可选参数[overwrite]: 覆写的意思,当指定这个参数以后,表中若有数据,则直接会被覆盖掉,即在这个表路径下的数据文件会被覆盖,若不指定,则原表中是数据还有存在,就是在存数据文件的路径下加上我们上传的文件

           3. 可选参数[partition]: 选择这个时候上传数据文件会按指定的字段进行分区上传

```

##### 1. filepath

- **filepath**表示待移动数据的路径。可以指向文件（在这种情况下，Hive将文件移动到表中），也可以指向目录

  （在这种情况下，Hive将把该目录中的所有文件移动到表中）。

- filepath文件路径支持下面三种形式，要结合LOCAL关键字一起考虑：

  1.相对路径，例如：project/data1 

  2.绝对路径，例如：/user/hive/project/data1

  3.具有schema的完整URI，例如：hdfs://namenode:9000/user/hive/project/data1

![image-20220921201345536](E:\黑马培训\Hadoop生态圈\assets\image-20220921201345536.png)

##### 2. local

- **指定LOCAL**， 将在本地文件系统中查找文件路径。

若指定相对路径，将相对于用户的当前工作目录进行解释；

用户也可以为本地文件指定完整的URI-例如：file:///user/hive/project/data1。



- **没有指定LOCAL**关键字。

如果filepath指向的是一个完整的URI，会直接使用这个URI；

如果没有指定schema，Hive会使用在hadoop配置文件中**参数fs.default.name指定的**（不出意外，都是HDFS）



**local本地是哪里?**

- 如果对HiveServer2服务运行此命令

 **本地文件系统**指的是Hiveserver2服务所在机器的本地Linux文件系统，不是Hive客户端所在的本地文件系统。

![image-20220921201826795](E:\黑马培训\Hadoop生态圈\assets\image-20220921201826795.png)



##### 3. overwrite

- 如果使用了**OVERWRITE**关键字，则目标表（或者分区）中的已经存在的数据会被删除，然后再将filepath指

  向的文件/目录中的内容添加到表/分区中。 



##### 4. 案例练习

![image-20220921203001193](E:\黑马培训\Hadoop生态圈\assets\image-20220921203001193.png)

**练习1：从当地文件系统加载数据**

注意：local当地指的是hive服务器所在的虚拟机环境的文件系统，所以要上传的文件要传在这

例子：load data local inpath "/root/students.txt" into table student_local_fs

1. 上传文件到本地文件系统

![image-20220921205032390](E:\黑马培训\Hadoop生态圈\assets\image-20220921205032390.png)

2. 根据文件，建立相对应的hive表

![image-20220921205142855](E:\黑马培训\Hadoop生态圈\assets\image-20220921205142855.png)

3. 指定本地路径加载文件进入表中  实际上是文件复制到表的文件下面

![image-20220921205444694](E:\黑马培训\Hadoop生态圈\assets\image-20220921205444694.png)

4. 查看映射结果

![image-20220921205517263](E:\黑马培训\Hadoop生态圈\assets\image-20220921205517263.png)



**练习2：从hdfs上加载数据**

1. 使用hadoop fs的指令将文件上传至hdfs

![image-20220921210644946](E:\黑马培训\Hadoop生态圈\assets\image-20220921210644946.png)

2. load加载数据，不写local，则是从hdfs文件系统加载数据

![image-20220921210740264](E:\黑马培训\Hadoop生态圈\assets\image-20220921210740264.png)

3. 查看hadoop fs文件系统上数据文件是否还在？

![image-20220921210817779](E:\黑马培训\Hadoop生态圈\assets\image-20220921210817779.png)

> 发现文件在hdfs系统上消失了

总结：使用load加载数据时，加载的数据是本地文件系统的数据时，是纯复制的操作，加载的是hdfs文件系统上的值时是纯移动

4. 查看最终映射结果

![image-20220921211408907](E:\黑马培训\Hadoop生态圈\assets\image-20220921211408907.png)



**案例3：加载数据表类型为分区表(表内的分区都一样，这是静态加载)**

> load 加载一个文件进去以后指定分区后，这个文件就是分区字段的数据文件了

1.上传文件至hdfs文件系统

2.利用load语句加载数据并指定分区字段，把数据放入分区表

![image-20220921225146403](E:\黑马培训\Hadoop生态圈\assets\image-20220921225146403.png)

3.查看web ui端分区表下的数据文件

![image-20220921225320263](E:\黑马培训\Hadoop生态圈\assets\image-20220921225320263.png)

![image-20220921225343726](E:\黑马培训\Hadoop生态圈\assets\image-20220921225343726.png)

具体分区字段的数据内容就是students.txt



**案例3：使用load加载数据文件进入普通表中，接着实现普通表中向分区表的转变**

> 本案例采取动态分区插入，意为使用insert+select来向分区表中导入数据，实现给一个分区字段分多个区

1.创建分区表，指定要分区的字段名称与类型，注意这一步指定的字段不能与表中已存在的字段重名

![image-20220921232056604](E:\黑马培训\Hadoop生态圈\assets\image-20220921232056604.png)

2.使用load加载数据进入普通表中，即数据来源表中

![image-20220921232411473](E:\黑马培训\Hadoop生态圈\assets\image-20220921232411473.png)

3.查看数据导入是否成功

![image-20220921232454875](E:\黑马培训\Hadoop生态圈\assets\image-20220921232454875.png)

4.使用insert+select语句向分区表中动态导入数据

![image-20220921232803486](E:\黑马培训\Hadoop生态圈\assets\image-20220921232803486.png)

5.web ui 查看分区结果

![image-20220921232919802](E:\黑马培训\Hadoop生态圈\assets\image-20220921232919802.png)

6.如图所示动态分区为3个分区

![image-20220921233125225](E:\黑马培训\Hadoop生态圈\assets\image-20220921233125225.png)

##### 5. insert

**注意事项**

![2、insert+select使用注意事项](E:\黑马培训\Hadoop生态圈\assets\2、insert+select使用注意事项.png)

- 在MySQL这样的RDBMS中，通常使用insert+values的方式来向表插入数据，并且执行速度很快。

- 这也是RDBMS中表插入数据的核心方式。

![image-20220921233406684](E:\黑马培训\Hadoop生态圈\assets\image-20220921233406684.png)

- 假如把Hive当成RDBMS，用insert+values的方式插入数据，会如何？

​     执行过程非常非常慢，原因在于底层是使用MapReduce把数据写入Hive表中

- 试想一下，如果在Hive中使用insert+values，对于大数据环境一条条插入数据，用时难以想象。

![image-20220921233508656](E:\黑马培训\Hadoop生态圈\assets\image-20220921233508656.png)



- Hive官方推荐加载数据的方式：

​      清洗数据成为结构化文件，再使用Load语法加载数据到表中。这样的效率更高。

- 但是并不意味insert语法在Hive中没有用武之地。

###### 1. insert+select

- insert+select表示：将后面查询返回的结果作为内容插入到指定表中，注意OVERWRITE将覆盖已有数据。

​    1.需要保证查询结果列的数目和需要插入数据表格的列数目一致。

​    2.如果查询出来的数据类型和插入表格对应的列数据类型不一致，将会进行转换，但是不能保证转换一定成功，

转换失败的数据将会为NULL。

![image-20220921233650487](E:\黑马培训\Hadoop生态圈\assets\image-20220921233650487.png)

![image-20220921233659787](E:\黑马培训\Hadoop生态圈\assets\image-20220921233659787.png)



2. ###### multiple insert

- 翻译为多次插入，多重插入，其核心功能是：一次扫描，多次插入。

- 语法目的就是减少扫描的次数，在一次扫描中。完成多次insert操作。

![image-20220921233821956](E:\黑马培训\Hadoop生态圈\assets\image-20220921233821956.png)

> 多重插入下面的解释是
>
> 从表student中取出num字段的值，插入student_insert1表中，选择name字段的值，插入student_insert2表中



###### 2. Dynamic Partition Inserts

> Dynamic动态

**背景**

- 对于分区表的数据导入加载，最基础的是通过load命令加载数据。

- 在load过程中，分区值是手动指定写死的，叫做静态分区。

![image-20220921234411434](E:\黑马培训\Hadoop生态圈\assets\image-20220921234411434.png)



**概述**

- 动态分区插入指的是：**分区的值是由后续的select查询语句的结果来动态确定的**。

- 根据查询结果自动分区。

| **hive.exec.dynamic.partition**      | **true** | **需要设置true为启用动态分区插入**                           |
| ------------------------------------ | -------- | ------------------------------------------------------------ |
| **hive.exec.dynamic.partition.mode** | strict   | 在strict模式下，用户必须至少指定一个静态分区，以防用户意外覆盖所有分区；在nonstrict模式下，允许所有分区都是动态的 |

![image-20220921234946950](E:\黑马培训\Hadoop生态圈\assets\image-20220921234946950.png)



**练习**

![image-20220921235021160](E:\黑马培训\Hadoop生态圈\assets\image-20220921235021160.png)

![image-20220921235042548](E:\黑马培训\Hadoop生态圈\assets\image-20220921235042548.png)

###### 3. Insert Directory

> 数据导出的文件路径要提前存在否则保持错
>
> 导出文件到指定目录只支持overwrite不支持into

**语法格式**

- Hive支持将select查询的结果导出成文件存放在文件系统中。语法格式如下；

注意：**导出操作是一个OVERWRITE覆盖操作，慎重。**

- 目录可以是完整的URI。如果未指定scheme，则Hive将使用hadoop配置变量fs.default.name来决定导出位

  置；

- 如果使用LOCAL关键字，则Hive会将数据写入本地文件系统上的目录；

- 写入文件系统的数据被序列化为文本，列之间用\001隔开，行之间用换行符隔开。如果列都不是原始数据类

  型，那么这些列将序列化为JSON格式。也可以在导出的时候指定分隔符换行符和文件格式。

![image-20220921235213055](E:\黑马培训\Hadoop生态圈\assets\image-20220921235213055.png)



**实例操作**

![image-20220921235422616](E:\黑马培训\Hadoop生态圈\assets\image-20220921235422616.png)

![image-20220921235452781](E:\黑马培训\Hadoop生态圈\assets\image-20220921235452781.png)

**自己操作示例**

**1. 导出数据表文件到hdfs文件系统中**

1.创建导出数据表数据的目标目录

![image-20220924131342333](E:\黑马培训\Hadoop生态圈\assets\image-20220924131342333.png)

2.编写hive语句导出表中的文件，存放至本地文件系统的目录下

![image-20220924132043728](E:\黑马培训\Hadoop生态圈\assets\image-20220924132043728.png)

3.查看hdfs中导出的数据文件

![image-20220924132122818](E:\黑马培训\Hadoop生态圈\assets\image-20220924132122818.png)

> 没有指定分割符号所以是\001 这里不显示



**1. 导出指定数据表文件的分隔符和存储文件的格式 再到本地文件系统中1**

1. 在本地文件目录下创建存放导出数据的文件目录

![image-20220924142130294](E:\黑马培训\Hadoop生态圈\assets\image-20220924142130294.png)

2. 编写sql语句导出数据到指定目录下

![image-20220924142510528](E:\黑马培训\Hadoop生态圈\assets\image-20220924142510528.png)

3. 查看导出结果

![image-20220924142547325](E:\黑马培训\Hadoop生态圈\assets\image-20220924142547325.png)





### 6. Hive SQL DQL

#### 1. select语法树

```sql
SELECT [ALL | DISTINCT] select_expr, select_expr, ...# 这里的distinct是对后面所有的字段去重，即所有字段有重复才能被去重
FROM table_reference
JOIN table_other ON expr
[WHERE where_condition]
[GROUP BY col_list [HAVING condition]]
[CLUSTER BY col_list| [DISTRIBUTE BY col_list] [SORT BY| ORDER BY col_list]]
[LIMIT number]
```

- 不管是写select语句还是看select语句，==**from关键字**==及后面的表是最重要；
- 其后面的表可能是一张真实物理存在的表，也可能是虚拟的表（查询的结果 或者 视图view）
- select基础语法

  - **==where语句中为什么不能使用聚合函数？==**
  - **==Having和where的区别？==**
  - **==group by语法中的查询字段的限制？为什么要求是分组字段或者是聚合函数应用的字段==**
  - **==梳理执行顺序？==**

#### 2. CLUSTER BY  分桶查询

> 为了探究底层细节，建议使用beeline客户端练习 方面查看查询过程日志。

- 语法

```sql
--CTAS
create table student as select * from student_hdfs; 
--根据查询返回的结果去创建一张表 表的字段个数类型顺序数据都取决于后面的查询。

select * from student;  --普桶查询
select * from student cluster by num; --分桶查询 根据学生编号进行分桶查询

--Q:分为几个部分？ 分的规则是什么？
分为几个部分取决于reducetask个数
分的规则和分桶表的规则一样 hashfunc(字段)  %  reducetask个数
```

- reducetask个数是如何确定的？ reducetask个数就决定了最终数据分为几桶。

```sql
--如果用户没有设置,不指定reduce task个数。则hive根据表输入数据量自己评估
--日志显示：Number of reduce tasks not specified. Estimated from input data size: 1
select * from student cluster by num;

--手动设置reduce task个数
--日志显示：Number of reduce tasks not specified. Defaulting to jobconf value of: 2
set mapreduce.job.reduces =2;
select * from student cluster by num;
----分桶查询的结果真的根据reduce tasks个数分为了两个部分，并且每个部分中还根据了字段进行了排序。

--总结：cluster by xx  分且排序的功能
	  分为几个部分 取决于reducetask个数
	  排序只能是正序 用户无法改变
	   
--需求：把student表数据根据num分为两个部分，每个部分中根据年龄age倒序排序。	
set mapreduce.job.reduces =2;
select  * from student cluster by num order by age desc;
select  * from student cluster by num sort by age desc;
--FAILED: SemanticException 1:50 Cannot have both CLUSTER BY and SORT BY clauses

```

![image-20220924151139530](E:\黑马培训\Hadoop生态圈\assets\image-20220924151139530.png)



#### 3. DISTRIBUTE BY+SORT BY

- 功能：相当于把cluster by的功能一分为二。

  - distribute by只负责分;
  - sort by只负责分之后的每个部分排序。
  - 并且分和排序的字段可以不一样。

```sql
--当后面分和排序的字段是同一个字段 加起来就相等于cluster by
CLUSTER BY(分且排序) = DISTRIBUTE BY（分）+SORT BY（排序） 

--下面两个功能一样的
select  * from student cluster by num;
select  * from student distribute by num sort by num;
--下面这个跟cluster by不等价 原因是排序行为是相反的
select  * from student distribute by num sort by num desc;

--最终实现
select  * from student distribute by num sort by age desc;
```



#### 4. ORDER BY

```sql
--首先我们设置一下reducetask个数，随便设置
--根据之前的探讨，貌似用户设置几个，结果就是几个，但是实际情况如何呢？
set mapreduce.job.reduces =2;
select  * from student order by age desc;

--执行中日志显示
Number of reduce tasks determined at compile time: 1 --不是设置了为2吗 

--原因：order by是全局排序。全局排序意味着数据只能输出在一个文件中。因此也只能有一个reducetask.
--在order by出现的情况下，不管用户设置几个reducetask,在编译执行期间都会变为一个，满足全局。
```

- order by 和sort by
  - order by负责==全局排序==  意味着整个mr作业只有一个reducetask 不管用户设置几个 编译期间hive都会把它设置为1。
  - sort by负责分完之后 局部排序。

![image-20220924151727884](E:\黑马培训\Hadoop生态圈\assets\image-20220924151727884.png)

![image-20220924151822682](E:\黑马培训\Hadoop生态圈\assets\image-20220924151822682.png)

#### 5. Union联合查询

完整版select语法树

```
[WITH CommonTableExpression (, CommonTableExpression)*]
SELECT [ALL | DISTINCT] select_expr, select_expr, ...
FROM table_reference
[WHERE where_condition]
[GROUP BY col_list]
[ORDER BY col_list]
[CLUSTER BY col_list | [DISTRIBUTE BY col_list] [SORT BY col_list]]
[LIMIT [offset,] rows];
```

- union联合查询

> UNION用于将来自==多个SELECT语句的结果合并为一个结果集==。

```sql
--语法规则
select_statement 
	UNION [ALL | DISTINCT] 
select_statement 
	UNION [ALL | DISTINCT] 
select_statement 
	...;

--使用DISTINCT关键字与使用UNION默认值效果一样，都会删除重复行。
select num,name from student_local
UNION
select num,name from student_hdfs;
--和上面一样
select num,name from student_local
UNION DISTINCT
select num,name from student_hdfs;

--使用ALL关键字会保留重复行。
select num,name from student_local
UNION ALL
select num,name from student_hdfs limit 2;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT应用于单个SELECT
--请将上面的放在跟子句一起的括号内
SELECT num,name FROM (select num,name from student_local LIMIT 2)  subq1
UNION
SELECT num,name FROM (select num,name from student_hdfs LIMIT 3) subq2;

--如果要将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT子句应用于整个UNION结果
--请将ORDER BY，SORT BY，CLUSTER BY，DISTRIBUTE BY或LIMIT放在最后一个之后。
select num,name from student_local
UNION
select num,name from student_hdfs
order by num desc;
```

> 注意union默认有去重操作,为了方便我们写全称避免出错

> 排序呀,去重啊等等写在最后就是对整个表而言的



#### 6. CTE表达式

> 通用表表达式（CTE）是一个临时结果集，该结果集是从==WITH子句中指定的简单查询==
>
> ==派生而来的==，该查询紧接在SELECT或INSERT关键字之前。
>
> 通俗解释：sql开始前定义一个SQL片断，该SQL片断可以被后续整个SQL语句所用到，并且可以多次使用。

**通用表表达式（CTE）是一个临时结果集**

```sql
--select语句中的CTE
with q1 as (select num,name,age from student where num = 95002)
select *
from q1;

-- from风格
with q1 as (select num,name,age from student where num = 95002)
from q1
select *;

-- chaining CTEs 链式
with q1 as ( select * from student where num = 95002),
     q2 as ( select num,name,age from q1)
select * from (select num from q2) a;

-- union
with q1 as (select * from student where num = 95002),
     q2 as (select * from student where num = 95004)
select * from q1 union all select * from q2;

-- ctas  
-- creat table as select 创建一张表来自于后面的查询语句  表的字段个数 名字 顺序和数据行数都取决于查询
-- create table t_ctas as select num,name from student limit 2;

create table s2 as
with q1 as ( select * from student where num = 95002)
select * from q1;

-- view
create view v1 as
with q1 as ( select * from student where num = 95002)
select * from q1;

select * from v1;
```



#### 7. Join语法及其使用（侧重inner、left）

**--为什么在SQL类语言中有join语法出现？**
基于sql业务的实际使用，针对不同的业务创建不同的表，数据保存在不同的表中。

有的业务需求基于多份数据共同组合查询才能返回，基于多张表进行查询，所以有了join关联查询。



**语法树**

```sql
join_table:
    table_reference [INNER] JOIN table_factor [join_condition]
  | table_reference {LEFT|RIGHT|FULL} [OUTER] JOIN table_reference join_condition
  | table_reference LEFT SEMI JOIN table_reference join_condition
  | table_reference CROSS JOIN table_reference [join_condition] (as of Hive 0.10)
 
join_condition:
    ON expression
```



**具体6种join方式，重点掌握  ==inner 和left join==**

**left join的简单应用**

![1、left join的简单应用](E:\黑马培训\Hadoop生态圈\assets\1、left join的简单应用.png)

```
--Join语法练习 建表
drop table if exists employee_address;
drop table if exists employee_connection;
drop table if exists employee;

--table1: 员工表
CREATE TABLE employee(
   id int,
   name string,
   deg string,
   salary int,
   dept string
 ) row format delimited
fields terminated by ',';

--table2:员工家庭住址信息表
CREATE TABLE employee_address (
    id int,
    hno string,
    street string,
    city string
) row format delimited
fields terminated by ',';

--table3:员工联系方式信息表
CREATE TABLE employee_connection (
    id int,
    phno string,
    email string
) row format delimited
fields terminated by ',';

--加载数据到表中
load data local inpath '/root/hivedata/employee.txt' into table employee;
load data local inpath '/root/hivedata/employee_address.txt' into table employee_address;
load data local inpath '/root/hivedata/employee_connection.txt' into table employee_connection;

select * from employee;
+--------------+----------------+---------------+------------------+----------------+
| employee.id  | employee.name  | employee.deg  | employee.salary  | employee.dept  |
+--------------+----------------+---------------+------------------+----------------+
| 1201         | gopal          | manager       | 50000            | TP             |
| 1202         | manisha        | cto           | 50000            | TP             |
| 1203         | khalil         | dev           | 30000            | AC             |
| 1204         | prasanth       | dev           | 30000            | AC             |
| 1206         | kranthi        | admin         | 20000            | TP             |
| 1201         | gopal          | manager       | 50000            | TP             |
| 1202         | manisha        | cto           | 50000            | TP             |
| 1203         | khalil         | dev           | 30000            | AC             |
| 1204         | prasanth       | dev           | 30000            | AC             |
| 1206         | kranthi        | admin         | 20000            | TP             |
+--------------+----------------+---------------+------------------+----------------+

select * from employee_address;


select * from employee_connection;
```



```
--1、内连接  inner join == join
  返回左右两边同时满足条件的数据
  
select e.*,e_a.*
from employee e inner join employee_address e_a
on e.id =e_a.id;

--等价于 inner join
select e.*,e_a.*
from employee e join employee_address e_a
on e.id =e_a.id;

--等价于 隐式连接表示法
select e.*,e_a.*
from employee e , employee_address e_a
where e.id =e_a.id;

  
--2、左连接  left join  ==  left OUTER join
  左表为准，左表全部显示，右表与之关联 满足条件的返回，不满足条件显示null
  
select e.*,e_conn.*
from employee e left join employee_connection e_conn
on e.id =e_conn.id;

--等价于 left outer join 左外连接
select e.id,e.*,e_conn.*
from employee e left outer join  employee_connection e_conn
on e.id =e_conn.id;  
  
--3、右连接  right join  ==  right OUTER join 右外连接
  右表为准，右表全部显示，左表与之关联 满足条件的返回，不满足条件显示null
 
select e.id,e.*,e_conn.*
from employee e right join employee_connection e_conn
on e.id =e_conn.id;

--等价于 right outer join
select e.id,e.*,e_conn.*
from employee e right outer join employee_connection e_conn
on e.id =e_conn.id;

--4、外连接 全外连接 full join == full outer join
两表连接的时候使用这个，会自动去重
FULL OUTER JOIN 关键字只要左表（table1）和右表（table2）其中一个表中存在匹配，则返回行.
FULL OUTER JOIN 关键字结合了 LEFT JOIN 和 RIGHT JOIN 的结果。

select e.*,e_a.*
from employee e full outer join employee_address e_a
on e.id =e_a.id;
--等价于
select e.*,e_a.*
from employee e full join employee_address e_a
on e.id =e_a.id;

--5、左半连接 left semi join

select *
from employee e left semi join employee_address e_addr
on e.id =e_addr.id;

--相当于 inner join,但是只返回左表全部数据， 只不过效率高一些
select e.*
from employee e inner join employee_address e_addr
on e.id =e_addr.id;

--6、交叉连接cross join
将会返回被连接的两个表的笛卡尔积，返回结果的行数等于两个表行数的乘积。对于大表来说，cross join慎用。
在SQL标准中定义的cross join就是无条件的inner join。返回两个表的笛卡尔积,无需指
定关联键。
在HiveSQL语法中，cross join 后面可以跟where子句进行过滤，或者on条件过滤。
```

- 允许使用复杂的联接表达式；

- 同一查询中可以连接2个以上的表；

- 如果每个表在联接子句中使用相同的列，则Hive将多个表上的联接转换为单个MR作业

- join时的最后一个表会通过reducer流式传输，并在其中缓冲之前的其他表，

  因此，将大表放置在最后有助减少reducer阶段缓存数据所需要的内存

- 在join的时候，可以通过语法STREAMTABLE提示指定要流式传输的表。如果

  省略STREAMTABLE提示，则Hive将流式传输最右边的表。

- hive中大表使用流式传输是会更快


#### 8. 第一代客户端的功能

> ==批处理==：一次连接，一次交互， 执行结束断开连接
> ==交互式处理==：保持持续连接， 一直交互
>
> 注意：如果说hive的shell客户端 指的是第一代客户端bin/hive
>
> 而第二代客户端bin/beeline属于JDBC客户端 不是shell。

==**bin/hive**==

- 功能1：作为==第一代客户端== 连接访问==metastore服务==，使用Hive。交互式方式

- 功能2：启动hive服务

```shell
/export/server/apache-hive-3.1.2-bin/bin/hive --service metastore 
/export/server/apache-hive-3.1.2-bin/bin/hive --service hiveserver2 
```

- 功能3：批处理执行Hive SQL

```shell
#-e 执行后面的sql语句
/export/server/apache-hive-3.1.2-bin/bin/hive  -e 'select * from itheima.student'

#-f 执行后面的sql文件
vim hive.sql
select * from itheima.student limit 2

/export/servers/apache-hive-3.1.2-bin/bin/hive  -f hive.sql

#sql文件不一定是.sql 要保证文件中是正确的HQL语法。

#-f调用sql文件执行的方式 是企业中hive生产环境主流的调用方式。
```



#### 9. 参数配置方式与优先级

**有哪些参数可以配置？**

```
https://cwiki.apache.org/confluence/display/Hive/Configuration+Properties
```

**配置方式有哪些？  注意配置方式影响范围影响时间是怎样？**

-   方式1：配置文件  con/==hive-site.xml==

```
影响的是基于这个安装包的任何使用方式。
```

- 方式2：配置参数 ==--hiveconf==

```
/export/servers/apache-hive-3.1.2-bin/bin/hive --service metastore  

/export/servers/apache-hive-3.1.2-bin/bin/hive --service hiveserver2  --hiveconf hive.root.logger=DEBUG,console

#影响的是session会话级别的
```

- 方式3：==set命令==

```
session会话级别的 设置完之后将会对后面的sql执行生效。
session结束 set设置的参数将失效。

也是推荐搭建使用的设置参数方式。  谁需要 谁设置 谁生效
```



#### 10. 内置常见运算符

- 官方链接：

https://cwiki.apache.org/confluence/display/Hive/LanguageManual+UDF

- 查看运算符和函数的帮助手册

- 可以使用课程资料中中文版参考手册

```sql
--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;
--使用extended 可以查看更加详细的使用说明
describe function extended +;
```

**具体分类**

- 关系运算符
- 算术运算符 
- 逻辑运算符

```sql
--1、创建表dual
create table dual(id string);
--2、加载一个文件dual.txt到dual表中
--dual.txt只有一行内容：内容为一个空格
load data local inpath '/root/hivedata/dual.txt' into table dual;
--3、在select查询语句中使用dual表完成运算符、函数功能测试
select 1+1 from dual;

select 1+1;

----------------Hive中关系运算符--------------------------
--is null空值判断
select 1 from dual where 'itcast' is null;

--is not null 非空值判断
select 1 from dual where 'itcast' is not null;

--like比较： _表示任意单个字符 %表示任意数量字符
--否定比较： NOT A like B
select 1 from dual where 'itcast' like 'it_';
select 1 from dual where 'itcast' like 'it%';
select 1 from dual where  'itcast' not like 'hadoo_';
select 1 from dual where  not 'itcast' like 'hadoo_';

--rlike：确定字符串是否匹配正则表达式，是REGEXP_LIKE()的同义词。
select 1 from dual where 'itcast' rlike '^i.*t$';
select 1 from dual where '123456' rlike '^\\d+$';  --判断是否全为数字
select 1 from dual where '123456aa' rlike '^\\d+$';

--regexp：功能与rlike相同 用于判断字符串是否匹配正则表达式
select 1 from dual where 'itcast' regexp '^i.*t$';



-------------------Hive中算术运算符---------------------------------
--取整操作: div  给出将A除以B所得的整数部分。例如17 div 3得出5。
select 17 div 3;

--取余操作: %  也叫做取模mod  A除以B所得的余数部分
select 17 % 3;

--位与操作: &  A和B按位进行与操作的结果。 与表示两个都为1则结果为1
select 4 & 8 from dual;  --4转换二进制：0100 8转换二进制：1000
select 6 & 4 from dual;  --4转换二进制：0100 6转换二进制：0110

--位或操作: |  A和B按位进行或操作的结果  或表示有一个为1则结果为1
select 4 | 8 from dual;
select 6 | 4 from dual;

--位异或操作: ^ A和B按位进行异或操作的结果 异或表示两者的值不同,则结果为1
select 4 ^ 8 from dual;
select 6 ^ 4 from dual;


--3、Hive逻辑运算符
--与操作: A AND B   如果A和B均为TRUE，则为TRUE，否则为FALSE。如果A或B为NULL，则为NULL。
select 1 from dual where 3>1 and 2>1;
--或操作: A OR B   如果A或B或两者均为TRUE，则为TRUE，否则为FALSE。
select 1 from dual where 3>1 or 2!=2;
--非操作: NOT A 、!A   如果A为FALSE，则为TRUE；如果A为NULL，则为True。否则为FALSE。
select 1 from dual where not 2>1;
select 1 from dual where !2=1;

--在:A IN (val1, val2, ...)  如果A等于任何值，则为TRUE。
select 1 from dual where 11  in(11,22,33);
--不在:A NOT IN (val1, val2, ...) 如果A不等于任何值，则为TRUE
select 1 from dual where 11 not in(22,33,44);
```



#### 11. 函数及分类标准

内置的函数（==build in func==）

> 所谓的内置指的是hive开发好，可以直接上手使用的；

- 内置函数往往根据函数的应用功能类型来分类
- 日期函数、数字函数、字符串函数、集合函数、条件函数....



用户定义函数（==user-defined function==）

> 用户编程实现函数的逻辑在hive中使用。

- UDF根据函数==输入行数和输出行数==进行分类

- UDF 、UDAF、UDTF

  ```shell
  #1、UDF（User-Defined-Function）普通函数 一进一出  输入一行数据输出一行数据
  
  0: jdbc:hive2://node1:10000> select split("allen woon hadoop"," ");# 根据空格分割返回列表，跟python中split函数用法相同
  +----------------------------+--+
  |            _c0             |
  +----------------------------+--+
  | ["allen","woon","hadoop"]  |
  +----------------------------+--+
  
  #2、UDAF（User-Defined Aggregation Function）聚合函数，多进一出 输入多行输出一行
  
  count sum max  min  avg
  
  #3、UDTF（User-Defined Table-Generating Functions）表生成函数 一进多出 输入一行输出多行
  
  explode 、parse_url_tuple
  ```



==**UDF分类标准的扩大化**==

- 本来，udf/udtf/udaf  3个标准是针对用户自定义函数分类的；
- 但是，现在可以将这个分类标准扩大到==hive中所有的函数，包括内置函数和自定义函数==；
- 不要被UD这两个字母所影响。  Built-in Aggregate Functions (UDAF).



函数相关的常用帮助命令

```sql
--显示所有的函数和运算符
show functions;
--查看运算符或者函数的使用说明
describe function +;
desc function 
--使用extended 可以查看更加详细的使用说明
describe function extended count;
```





#### 12. 字符串函数

> 内置函数

```sql
--字符串截取函数：substr(str, pos[, len]) 或者  substring(str, pos[, len])
select substr("angelababy",-2); --pos是从1开始的索引，如果为负数则倒着数
select substr("angelababy",2,2);

--正则表达式替换函数：regexp_replace(str, regexp, rep)
select regexp_replace('100-200', '(\\d+)', 'num'); --正则分组

--正则表达式解析函数：regexp_extract(str, regexp[, idx]) 提取正则匹配到的指定组内容
函数：regexp_extract(str, regexp[, idx]) ----字符串正则表达式解析函数
参数解释:
其中：
str是被解析的字符串或字段名

regexp 是正则表达式

idx是返回结果 取表达式的哪一部分 默认值为1。

0表示把整个正则表达式对应的结果全部返回

1表示返回正则表达式中第一个() 对应的结果 以此类推

注意点：
要注意的是idx的数字不能大于表达式中()的个数。

select regexp_extract('100-200', '(\\d+)-(\\d+)', 2);

--URL解析函数：parse_url 注意要想一次解析出多个 可以使用parse_url_tuple这个UDTF函数
select parse_url('http://www.itcast.cn/path/p1.php?query=1', 'HOST');

--分割字符串函数: split(str, regex)
select split('apache hive', '\\s+');--匹配一个或者多个空白符

--json解析函数：get_json_object(json_txt, path)
--$表示json对象
select get_json_object('[{"website":"www.itcast.cn","name":"allenwoon"}, {"website":"cloud.itcast.com","name":"carbondata 中文文档"}]', '$.[1].website');

```



#### 13. 时间日期、数值

> 内置函数

~~~sql
Date Functions 日期函数

> 日期和时间戳数字之间的转换 
>
> ==unix_timestamp==  日期转unix时间戳
>
> ==from_unixtime==  unix时间戳转日期
>
> ==date_add== 
>
> ==date_sub==  
>
> ==datediff==

```sql
--获取当前日期: current_date
select current_date();
--获取当前时间戳: current_timestamp
--同一查询中对current_timestamp的所有调用均返回相同的值。
select current_timestamp();
--获取当前UNIX时间戳函数: unix_timestamp
select unix_timestamp();
--日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp("2011-12-07 13:01:03");
--指定格式日期转UNIX时间戳函数: unix_timestamp
select unix_timestamp('20111207 13:01:03','yyyyMMdd HH:mm:ss');
--UNIX时间戳转日期函数: from_unixtime
select from_unixtime(1620723323);
select from_unixtime(0, 'yyyy-MM-dd HH:mm:ss');
--日期比较函数: datediff  日期格式要求'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd'
select datediff('2012-12-08','2012-05-09');
--日期增加函数: date_add
select date_add('2012-02-28',10);
--日期减少函数: date_sub
select date_sub('2012-01-1',10);
```

---

Mathematical Functions 数学函数

> ==round== 取整
>
> ==rand== 取随机值

```sql
--取整函数: round  返回double类型的整数值部分 （遵循四舍五入）
select round(3.1415926);
--指定精度取整函数: round(double a, int d) 返回指定精度d的double类型
select round(3.1415926,4);
--向下取整函数: floor
select floor(3.1415926);
select floor(-3.1415926);
--向上取整函数: ceil
select ceil(3.1415926);
select ceil(-3.1415926);
--取随机数函数: rand 每次执行都不一样 返回一个0到1范围内的随机数
select rand();
--指定种子取随机数函数: rand(int seed) 得到一个稳定的随机数序列
select rand(5);
```

-----
~~~



#### 14. 条件转换、集合、加密

> 内置函数

**最最最最重要的条件函数**

![2、最最最最重要的条件函数](E:\黑马培训\Hadoop生态圈\assets\2、最最最最重要的条件函数.png)

Conditional Functions 条件函数

> ==都重要==。尤其是case when

```sql
--if条件判断: if(boolean testCondition, T valueTrue, T valueFalseOrNull)
select if(1=2,100,200);
select if(sex ='男','M','W') from student limit 3;

--空判断函数: isnull( a )
select isnull("allen",); 只是用来判断tian
select isnull(null);

--非空判断函数: isnotnull ( a )
select isnotnull("allen");
select isnotnull(null);

--空值转换函数: nvl(T value, T default_value)
select nvl("allen","itcast");
select nvl(null,"itcast");

--非空查找函数: COALESCE(T v1, T v2, ...)
--返回参数中的第一个非空值；如果所有值都为NULL，那么返回NULL
select COALESCE(null,11,22,33);
select COALESCE(null,null,null,33);
select COALESCE(null,null,null);

--条件转换函数: CASE a WHEN b THEN c [WHEN d THEN e]* [ELSE f] END
select case 100 when 50 then 'tom' when 100 then 'mary' else 'tim' end;
select case sex when '男' then 'male' else 'female' end from student limit 3;
```

-----

Type Conversion Functions 类型转换函数

- 前置知识：Hive中支持类型的隐式转换  有限制  自动转换  不保证成功  就显示null

- ==cast显示类型转换函数==

  ```sql
  --任意数据类型之间转换:cast
  select cast(12.14 as bigint);
  select cast(12.14 as string);
  select cast("hello" as int);
  +-------+
  |  _c0  |
  +-------+
  | NULL  |
  +-------+
  ```

----

Data Masking Functions 数据脱敏函数  

> mask脱敏 掩码处理
>
> 数据脱敏：==让敏感数据不敏感==   13455667788 --->134****7788

```sql
--mask
--将查询回的数据，大写字母转换为X，小写字母转换为x，数字转换为n。
select mask("abc123DEF");
select mask("abc123DEF",'-','.','^'); --自定义替换的字母

--mask_first_n(string str[, int n]
--对前n个进行脱敏替换
select mask_first_n("abc123DEF",4);

--mask_last_n(string str[, int n])
select mask_last_n("abc123DEF",4);

--mask_show_first_n(string str[, int n])
--除了前n个字符，其余进行掩码处理
select mask_show_first_n("abc123DEF",4);

--mask_show_last_n(string str[, int n])
select mask_show_last_n("abc123DEF",4);

--mask_hash(string|char|varchar str)
--返回字符串的hash编码。
select mask_hash("abc123DEF");

```

-----

Misc. Functions 其他杂项函数、加密函数

```sql
--如果你要调用的java方法所在的jar包不是hive自带的 可以使用add jar添加进来
--hive调用java方法: java_method(class, method[, arg1[, arg2..]])
select java_method("java.lang.Math","max",11,22);

--反射函数: reflect(class, method[, arg1[, arg2..]])
select reflect("java.lang.Math","max",11,22);

--取哈希值函数:hash
select hash("allen");

--current_user()、logged_in_user()、current_database()、version()

--SHA-1加密: sha1(string/binary)
select sha1("allen");

--SHA-2家族算法加密：sha2(string/binary, int)  (SHA-224, SHA-256, SHA-384, SHA-512)
select sha2("allen",224);
select sha2("allen",512);

--crc32加密:
select crc32("allen");

--MD5加密: md5(string/binary)
select md5("allen");
```

**map和array函数**

![image-20220924152144183](E:\黑马培训\Hadoop生态圈\assets\image-20220924152144183.png)



#### 15. explode函数

> UDTF表生成函数

**explode语法功能**

对于UDTF表生成函数，很多人难以理解什么叫做输入一行，输出多行。

为什么叫做表生成？能够产生表吗？下面我们就来学习Hive当做内置的一个非常著名的UDTF函数，名字叫做

**explode函数**，中文戏称之为“爆炸函数”，可以炸开数据。

explode函数接收map或者array类型的数据作为参数，然后把参数中的每个元素炸开变成一行数据。一个元素一

行。这样的效果正好满足于输入一行输出多行。

explode函数在关系型数据库中本身是不该出现的。

因为他的出现本身就是在操作不满足第一范式的数据（每个属性都不可再分）。本身已经违背了数据库的设计原

理，但是在面向分析的数据库或者数据仓库中，这些规范可以发生改变。

![image-20220925094943011](E:\黑马培训\Hadoop生态圈\assets\image-20220925094943011.png)

- explode(array)将array列表里的每个元素生成一行；

- explode(map)将map里的每一对元素作为一行，其中key为一列，value为一列；

>  一般情况下，explode函数可以直接使用即可，也可以根据需要结合lateral view侧视图使用。

![image-20220925095110030](E:\黑马培训\Hadoop生态圈\assets\image-20220925095110030.png)

**explode语法限制原因**

1、 explode函数属于UDTF函数，即表生成函数；

2、 explode函数执行返回的结果可以理解为一张虚拟的表，其数据来源于源表；

3、 在select中只查询源表数据没有问题，只查询explode生成的虚拟表数据也没问题

4、 但是不能在只查询源表的时候，既想返回源表字段又想返回explode生成的虚拟表字段

5、 通俗点讲，有两张表，不能只查询一张表但是返回分别属于两张表的字段；

6、 从SQL层面上来说应该对两张表进行关联查询

7、 Hive专门提供了语法lateral View侧视图，专门用于搭配explode这样的UDTF函数，以满足上述需要。

- ==explode属于UDTF函数==，表生成函数，输入一行数据输出多行数据。

- 功能：

  ```sql
  explode() takes in an array (or a map) as an input and outputs the elements of the array (map) as separate rows.
  
  --explode接收map array类型的参数 把map或者array的元素输出，一行一个元素。
  
  explode(array(11,22,33))         11
  	                             22
  	                             33
  	                             
  	                             
  select explode(`array`(11,22,33,44,55));
  select explode(`map`("id",10086,"name","allen","age",18));	                             
  ```

- 栗子

  > 将NBA总冠军球队数据使用explode进行拆分，并且根据夺冠年份进行倒序排序。

  ```sql
  --step1:建表
  create table the_nba_championship(
             team_name string,
             champion_year array<string>
  ) row format delimited
  fields terminated by ','
  collection items terminated by '|';
  
  --step2:加载数据文件到表中
  load data local inpath '/root/hivedata/The_NBA_Championship.txt' into table the_nba_championship;
  
  --step3:验证
  select * from the_nba_championship;
  
  --step4:使用explode函数对champion_year进行拆分 俗称炸开
  select explode(champion_year) from the_nba_championship;
  
  --想法是正确的 sql执行确实错误的
  select team_name,explode(champion_year) from the_nba_championship;
  --错误信息
  UDTF's are not supported outside the SELECT clause, nor nested in expressions
  UDTF 在 SELECT 子句之外不受支持，也不在表达式中嵌套？？？
  ```

- 如果数据不是map或者array 如何使用explode函数呢？

  > 想法设法使用split subsrt regex_replace等函数组合使用 把数据变成array或者map.

  ```sql
  create table the_nba_championship_str(
             team_name string,
             champion_year string
  ) row format delimited
  fields terminated by ',';
  
  load data local inpath '/root/hivedata/The_NBA_Championship.txt' into table the_nba_championship_str;
  ```

----

![image-20220924151325322](E:\黑马培训\Hadoop生态圈\assets\image-20220924151325322.png)

#### 16. lateral view侧视图

> 侧视图的原理是==将UDTF的结果构建成一个类似于视图的表，然后将原表中的每一行和UDTF函数输出的每一行进行连接，生成一张新的虚拟表==

**侧视图图解**

![image-20221008182222018](E:\黑马培训\Hadoop生态圈\assets\image-20221008182222018.png)

- 概念

**Lateral View**是一种特殊的语法，主要用于**搭配UDTF类型功能的函数一起使用**，用于解决UDTF函数的一些查询

限制的问题。

侧视图的原理是将UDTF的结果构建成一个类似于视图的表，然后将原表中的每一行和UDTF函数输出的每一行进

行连接，生成一张新的虚拟表。这样就避免了UDTF的使用限制问题。使用lateral view时也可以对UDTF产生的记

录设置字段名称，产生的字段可以用于group by、order by 、limit等语句中，不需要再单独嵌套一层子查询。

一般只要使用UDTF，就会固定搭配lateral view使用。

- 背景

  - UDTF函数生成的结果可以当成一张虚拟的表，但是无法和原始表进行组合查询

    ```sql
    select name,explode(location) from test_message;
    --这个sql就是错误的  相当于执行组合查询 
    ```

  - 从理论层面推导，对两份数据进行join就可以了

  - 但是，hive专门推出了lateral view侧视图的语，满足上述需要。

- 功能：==把UDTF函数生成的结果和原始表进行关联，便于用户在select时间组合查询==、  lateral view是UDTf的好基友好搭档，实际中经常配合使用。

- 语法：

  ```sql
  --lateral view侧视图基本语法如下
  select …… from tabelA lateral view UDTF(xxx) 别名 as col1,col2,col3……;
  
  --针对上述NBA冠军球队年份排名案例，使用explode函数+lateral view侧视图，可以完美解决
  select a.team_name ,b.year
  from the_nba_championship a lateral view explode(champion_year) b as year;
  
  --根据年份倒序排序
  select a.team_name ,b.year
  from the_nba_championship a lateral view explode(champion_year) b as year
  order by b.year desc;
  
  --统计每个球队获取总冠军的次数 并且根据倒序排序
  select a.team_name ,count(*) as nums
  from the_nba_championship a lateral view explode(champion_year) b as year
  group by a.team_name
  order by nums desc;
  ```

-----



#### 17. 多行转单列

> （collect_list、concat_ws）

- ==**数据收集函数**==

  ```sql
  collect_set --把多行数据收集为一行  返回set集合  去重无序
  collect_list --把多行数据收集为一行  返回list集合  不去重有序
  ```

- ![image-20220924151220148](E:\黑马培训\Hadoop生态圈\assets\image-20220924151220148.png)

- ![image-20220924151256296](E:\黑马培训\Hadoop生态圈\assets\image-20220924151256296.png)

- 字符串拼接函数

  ```sql
  concat  --直接拼接字符串
  concat_ws --指定分隔符拼接
  
  select concat("it","cast","And","heima");
  select concat("it","cast","And",null);
  
  select concat_ws("-","itcast","And","heima");
  select concat_ws("-","itcast","And",null);
  ```

- 栗子

  ```sql
  --原表
  +----------------+----------------+----------------+--+
  | row2col2.col1  | row2col2.col2  | row2col2.col3  |
  +----------------+----------------+----------------+--+
  | a              | b              | 1              |
  | a              | b              | 2              |
  | a              | b              | 3              |
  | c              | d              | 4              |
  | c              | d              | 5              |
  | c              | d              | 6              |
  +----------------+----------------+----------------+--+
  
  --目标表
  +-------+-------+--------+--+
  | col1  | col2  |  col3  |
  +-------+-------+--------+--+
  | a     | b     | 1-2-3  |
  | c     | d     | 4-5-6  |
  +-------+-------+--------+--+
  
  --建表
  create table row2col2(
                           col1 string,
                           col2 string,
                           col3 int
  )row format delimited fields terminated by '\t';
  
  --加载数据到表中
  load data local inpath '/root/hivedata/r2c2.txt' into table row2col2;
  select * from row2col2;
  
  --最终SQL实现
  select
      col1,
      col2,
      concat_ws(',', collect_list(cast(col3 as string))) as col3
  from
      row2col2
  group by
      col1, col2;
  ```

----



#### 18. 单列转多行

> （explode、lateral view）

- 技术原理： explode+lateral view

- 例子

  ```sql
  --原表
  +-------+-------+--------+--+
  | col1  | col2  |  col3  |
  +-------+-------+--------+--+
  | a     | b     | 1,2,3  |
  | c     | d     | 4,5,6  |
  +-------+-------+--------+--+
  
  --目标表
  +----------------+----------------+----------------+--+
  | row2col2.col1  | row2col2.col2  | row2col2.col3  |
  +----------------+----------------+----------------+--+
  | a              | b              | 1              |
  | a              | b              | 2              |
  | a              | b              | 3              |
  | c              | d              | 4              |
  | c              | d              | 5              |
  | c              | d              | 6              |
  +----------------+----------------+----------------+--+
  
  --创建表
  create table col2row2(
                           col1 string,
                           col2 string,
                           col3 string
  )row format delimited fields terminated by '\t';
  
  --加载数据
  load data local inpath '/root/hivedata/c2r2.txt' into table col2row2;
  
  select * from col2row2;
  
  select explode(split(col3,',')) from col2row2;
  
  --SQL最终实现
  select
      col1,
      col2,
      lv.col3 as col3
  from
      col2row2
          lateral view
              explode(split(col3, ',')) lv as col3;
  ```

----



#### 19. json格式数据处理

- 在hive中，没有json类的存在，一般使==用string类型来修饰==，叫做json字符串，简称==json串==。

- 在hive中，处理json数据的两种方式

  - hive内置了两个用于==解析json的函数==

    ```sql
    json_tuple
    --是UDTF 表生成函数  输入一行，输出多行  一次提取读个值  可以单独使用 也可以配合lateral view侧视图使用
    
    get_json_object
    --是UDF普通函数，输入一行 输出一行 一次只能提取一个值 多次提取多次使用
    ```

  - 使用==JsonSerDe类解析==，在加载json数据到表中的时候完成解析动作

  栗子

  ```sql
  --创建表
  create table tb_json_test1 (
      json string
  );
  
  --加载数据
  load data local inpath '/root/hivedata/device.json' into table tb_json_test1;
  
  select * from tb_json_test1;
  
  -- get_json_object UDF函数 最大弊端是一次只能解析提取一个字段
  select
      --获取设备名称
      get_json_object(json,"$.device") as device,
      --获取设备类型
      get_json_object(json,"$.deviceType") as deviceType,
      --获取设备信号强度
      get_json_object(json,"$.signal") as signal,
      --获取时间
      get_json_object(json,"$.time") as stime
  from tb_json_test1;
  
  --json_tuple 这是一个UDTF函数 可以一次解析提取多个字段
  --单独使用 解析所有字段
  select
      json_tuple(json,"device","deviceType","signal","time") as (device,deviceType,signal,stime)
  from tb_json_test1;
  
  --搭配侧视图使用
  select
      json,device,deviceType,signal,stime
  from tb_json_test1
           lateral view json_tuple(json,"device","deviceType","signal","time") b
           as device,deviceType,signal,stime;
  
  
  --方式2： 使用JsonSerDe类在建表的时候解析数据
  --建表的时候直接使用JsonSerDe解析
  create table tb_json_test2 (
                                 device string,
                                 deviceType string,
                                 signal double,
                                 `time` string
  )
      ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'
      STORED AS TEXTFILE;
  
  load data local inpath '/root/hivedata/device.json' into table tb_json_test2;
  
  select * from tb_json_test2;
  ```

-----

**示例操作**

> 使用JsonSerDe类进行解析json文件，不建议使用，在整张表数据都为json，高频的使用时，使用这个解析
>
> 如果表中json数据使用频率不高，不建议使用这个解析，要用的时候再去解析

1.创建一个json序列化的表

建表语句为：

```sql
create table tb_json_test2 (
device string,
deviceType string,
signal double,
`time` string
)
row format SerDe 'org.apache.hive.hcatalog.data.JsonSerDe'
```

> 当建表时不指定表的数据文件存储的类型时，hive表默认使用textfile类进行文件存储

2.加载数据到表中

```sql
load data local inpath "/root/hivedata/device.json" into table tb_json_test2
```

3.查看表中数据

![image-20220924163400232](E:\黑马培训\Hadoop生态圈\assets\image-20220924163400232.png)

> 很明显json数据文件所有的信息都被解析出来了
>
> 字段名为：键的值 key
>
> 字段中的数值为：值 value



#### 20. 窗口函数

##### 1. 快速理解窗口函数功能

![image-20220924164403140](E:\黑马培训\Hadoop生态圈\assets\image-20220924164403140.png)

- window function 窗口函数、开窗函数、olap分析函数。

- 窗口：可以理解为操作数据的范围，窗口有大有小，本窗口中操作的数据有多有少。
- 可以简单地解释为类似于聚合函数的计算函数，但是通过GROUP BY子句组合的常规聚合会隐藏正在聚合的各个行，最终输出一行；而==窗口函数聚合后还可以访问当中====的各个行，并且可以将这些行中的某些属性添加到结果集中==。

```sql
--建表加载数据
CREATE TABLE employee(
       id int,
       name string,
       deg string,
       salary int,
       dept string
) row format delimited
    fields terminated by ',';

load data local inpath '/root/hivedata/employee.txt' into table employee;

select * from employee;

----sum+group by普通常规聚合操作------------
select dept,sum(salary) as total from employee group by dept;

select id,dept,sum(salary) as total from employee group by dept; --添加id至结果，错误sql

+-------+---------+
| dept  |  total  |
+-------+---------+
| AC    | 60000   |
| TP    | 120000  |
+-------+---------+

----sum+窗口函数聚合操作------------
select id,name,deg,salary,dept,sum(salary) over(partition by dept) as total from employee;

+-------+-----------+----------+---------+-------+---------+
|  id   |   name    |   deg    | salary  | dept  |  total  |
+-------+-----------+----------+---------+-------+---------+
| 1204  | prasanth  | dev      | 30000   | AC    | 60000   |
| 1203  | khalil    | dev      | 30000   | AC    | 60000   |
| 1206  | kranthi   | admin    | 20000   | TP    | 120000  |
| 1202  | manisha   | cto      | 50000   | TP    | 120000  |
| 1201  | gopal     | manager  | 50000   | TP    | 120000  |
+-------+-----------+----------+---------+-------+---------+
```

---



##### 2. 窗口函数语法规则

> ##### 具有==OVER语句==的函数叫做窗口函数。

```sql
Function OVER ([PARTITION BY <...>] [ORDER BY <....>] [<window_expression>])

--1、Function可以是下面分类中的任意一个
	--聚合函数：比如sum、max、avg、max、min等
    --排序函数：比如rank、row_number等
    --分析函数：比如lead、lag、first_value等

--2、OVER 窗口函数语法关键字与标识

--3、PARTITION BY <...>功能类似于group by，用于指定分组，相同的分为一组。如果没有指定PARTITION BY，那么整张表的所有行就是一组；

--4、ORDER BY <....> 用于指定每个分组内的数据排序规则 ，默认是升序ASC，支持ASC、DESC；

--5、window_expression window表达式，也叫window子句，用于指定每个窗口中操作的数据范围
```

- 建表加载数据  后续练习使用

```sql
---建表并且加载数据
create table website_pv_info(
   cookieid string,
   createtime string,   --day
   pv int
) row format delimited
fields terminated by ',';

create table website_url_info (
    cookieid string,
    createtime string,  --访问时间
    url string       --访问页面
) row format delimited
fields terminated by ',';


load data local inpath '/root/hivedata/website_pv_info.txt' into table website_pv_info;
load data local inpath '/root/hivedata/website_url_info.txt' into table website_url_info;

select * from website_pv_info;
select * from website_url_info;
```

> 学习hive的开窗函数几个重要问题：
>
> ==1、partition by有和没有是什么区别？==
>
> ==2、order by有和没有是什么区别？==
>
> ==3、rows 和range划分范围是什么区别？==
>
> ==4、如果用户不指定，默认行为是row还是range？==

----



##### 3. 聚合函数

- 语法

  ```sql
  sum|max|min|avg  OVER ([PARTITION BY <...>] [ORDER BY <....>] [<window_expression>])
  ```

- 重点：==**有PARTITION BY 没有PARTITION BY的区别；有ORDER BY没有ORDER BY的区别**==。

  - 有没有partition by 影响的是全局聚合 还是分组之后 每个组内聚合。
  - 有没有==**order by的区别**==：
    - 没有order by，默认是rows between，首行到最后行，这里的"行"是物理上的行；
    - 有order by，默认是range between，首行到当前行，这里的"行"是逻辑上的行，由字段的值的区间range来划分范围。
    - 因为它没指定window_expression 才会发生这种情况

- hive窗口范围默认值选取

![1、hive的窗口范围默认值选取](E:\黑马培训\Hadoop生态圈\assets\1、hive的窗口范围默认值选取.png)

翻译：

第一行意思是：当order by 被指定 但是window子句没有书写的时候 默认根据rang between来划分窗口范围

第二行意思是：当order by 与 window子句都没有时，窗口范围是默认根据rows between来划分的

- 栗子

- 没有指定

- ![image-20220924182024167](E:\黑马培训\Hadoop生态圈\assets\image-20220924182024167.png)

- 指定的了的

- ![2、窗口聚合函数的应用场景---累加求和场景](E:\黑马培训\Hadoop生态圈\assets\2、窗口聚合函数的应用场景---累加求和场景.png)

  ```sql
  --1、求出每个用户总pv数  sum+group by普通常规聚合操作
  select cookieid,sum(pv) as total_pv from website_pv_info group by cookieid;
  +-----------+-----------+
  | cookieid  | total_pv  |
  +-----------+-----------+
  | cookie1   | 26        |
  | cookie2   | 35        |
  +-----------+-----------+
  
  
  --2、sum+窗口函数 
  需要注意：这里都没有使用window子句指定范围，那么默认值是rows还是range呢？？？
  
  --当没有order by也没有window子句的时候，默认是rows between,从第一行到最后一行，即分组内的所有行聚合。
  --sum(...) over( )
  select cookieid,createtime,pv,
         sum(pv) over() as total_pv
  from website_pv_info;
  
  --sum(...) over( partition by... )
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid) as total_pv 
  from website_pv_info;
  
  
  --当有order by但是缺失window子句的时候，默认是range between，为第一行的值到当前行的值构成的区间
  --sum(...) over( partition by... order by ... )
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime) as current_total_pv
  from website_pv_info;
  
  --上述是根据creattime排序，因此range区间是由createtime的字段值来划分，第一行到当前行。
  
  
  
  --下面是根据pv排序，因此range区间是由pv的字段值来划分，第一行到当前行。
  --下面两个sql等价
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by pv) as current_total_pv
  from website_pv_info; --这属于有order by没有 window子句 默认range
  
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by pv range between unbounded preceding and current row) as current_total_pv
  from website_pv_info;
  
  
  --这是手动指定根据rows来划分范围
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by pv rows between unbounded preceding and current row ) as current_total_pv
  from website_pv_info;
  ```

------

> 当有order by但是缺失window子句的时候，默认是range between



##### 4. window子句

> ##### 直译叫做window表达式 ，通俗叫法称之为window子句。
>
> 当不指定window子句是用哪种形式来划分范围时，hive的原则是不同情况与场景选不同的，就是看情况来选

![image-20220924165653606](E:\黑马培训\Hadoop生态圈\assets\image-20220924165653606.png)

![image-20220924165821493](E:\黑马培训\Hadoop生态圈\assets\image-20220924165821493.png)

```shell
---> 用数值来划分的就用range 例如要一个班上身高175-178的人
-----如果这个用物理来划分的话，就是要身高前六，假设有两个并列178的人，排名在6和7
-----用物理划分的话，就只能取到第6名，而第7取不到，所以不能用

---> 物理上的排名，要班级身高前三的，这时候就可以用到rows了，我只在乎你是不是前三名，并不在乎你身高是否并列，因为我要的就是前三名
```

![image-20220924174259306](E:\黑马培训\Hadoop生态圈\assets\image-20220924174259306.png)

- 功能：控制窗口操作的范围。

- 语法

  ```ini
  unbounded 无边界
  preceding 往前
  following 往后
  unbounded preceding 往前所有行，即初始行
  n preceding 往前n行
  unbounded following 往后所有行，即末尾行
  n following 往后n行
  current row 当前行
   
  语法
  (ROWS | RANGE) BETWEEN (UNBOUNDED | [num]) PRECEDING AND ([num] PRECEDING | CURRENT ROW | (UNBOUNDED | [num]) FOLLOWING)
  
  (ROWS | RANGE) BETWEEN CURRENT ROW AND (CURRENT ROW | (UNBOUNDED | [num]) FOLLOWING)
  (ROWS | RANGE) BETWEEN [num] FOLLOWING AND (UNBOUNDED | [num]) FOLLOWING
  ```

- 栗子

  > 这里以rows between为例来讲解窗口范围的划分，rows表示物理层面上的行，跟字段值没关系。

  ```sql
  --默认从第一行到当前行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime) as pv1  
  from website_pv_info;
  
  --第一行到当前行 等效于rows between不写 默认就是第一行到当前行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding and current row) as pv2
  from website_pv_info;
  
  --向前3行至当前行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and current row) as pv4
  from website_pv_info;
  
  --向前3行 向后1行  当前行为参照行，一共作用范围5行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime rows between 3 preceding and 1 following) as pv5
  from website_pv_info;
  
  --当前行至最后一行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime rows between current row and unbounded following) as pv6
  from website_pv_info;
  
  --第一行到最后一行 也就是分组内的所有行
  select cookieid,createtime,pv,
         sum(pv) over(partition by cookieid order by createtime rows between unbounded preceding  and unbounded following) as pv6
  from website_pv_info;
  ```

----

![2、窗口聚合函数的应用场景---累加求和场景](E:\黑马培训\Hadoop生态圈\assets\2、窗口聚合函数的应用场景---累加求和场景-16640111813871.png)

![image-20220924182645655](E:\黑马培训\Hadoop生态圈\assets\image-20220924182645655.png)

> 我没有指定window子句的窗口范围才有这些问题，所以我们需要哪种范围划分，就手动的去写上，不要让他按默认的来

##### 5. 排序函数

![3、row_number函数和它的兄弟们的区别](E:\黑马培训\Hadoop生态圈\assets\3、row_number函数和它的兄弟们的区别.png)

- 功能：主要对数据分组排序之后，组内顺序标号。

- 核心函数：==**row_number**==、rank、dense_rank 

- 适合场景：==**分组TopN问题**==（注意哦 不是全局topN）

- 栗子

  ```sql
  SELECT
      cookieid,
      createtime,
      pv,
      RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn1,
      DENSE_RANK() OVER(PARTITION BY cookieid ORDER BY pv desc) AS rn2,
      ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS rn3
  FROM website_pv_info;
  
  --需求：找出每个用户访问pv最多的Top3 重复并列的不考虑
  SELECT * from
  (SELECT
      cookieid,
      createtime,
      pv,
      ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY pv DESC) AS seq
  FROM website_pv_info) tmp where tmp.seq <4;
  ```

- ==ntile==函数

  - 功能：将分组排序之后的数据分成指定的若干个部分（若干个桶）

  - 规则：尽量平均分配 ，优先满足最小的桶，彼此最多不相差1个。

  - 栗子

    ```sql
    --把每个分组内的数据分为3桶
    SELECT
        cookieid,
        createtime,
        pv,
        NTILE(3) OVER(PARTITION BY cookieid ORDER BY createtime) AS rn2
    FROM website_pv_info
    ORDER BY cookieid,createtime;
    
    --需求：统计每个用户pv数最多的前3分之1天。
    --理解：将数据根据cookieid分 根据pv倒序排序 排序之后分为3个部分 取第一部分
    SELECT * from
    (SELECT
         cookieid,
         createtime,
         pv,
         NTILE(3) OVER(PARTITION BY cookieid ORDER BY pv DESC) AS rn
     FROM website_pv_info) tmp where rn =1;
    ```

----

![image-20220924182810119](E:\黑马培训\Hadoop生态圈\assets\image-20220924182810119.png)



> 适合去求几分之几部分的数据，比如说：我要这个班，成绩排名是占3分之二部分所有人的数据，这时候就可以用ntile()函数



##### 6. lag、lead函数

![image-20220924204910149](E:\黑马培训\Hadoop生态圈\assets\image-20220924204910149.png)

```sql


--LAG 用于统计窗口内往上第n行值
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LAG(createtime,1,'1970-01-01 00:00:00') OVER(PARTITION BY cookieid ORDER BY createtime) AS last_1_time,
       LAG(createtime,2) OVER(PARTITION BY cookieid ORDER BY createtime) AS last_2_time
FROM website_url_info;


--LEAD 用于统计窗口内往下第n行值
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LEAD(createtime,1,'1970-01-01 00:00:00') OVER(PARTITION BY cookieid ORDER BY createtime) AS next_1_time,
       LEAD(createtime,2) OVER(PARTITION BY cookieid ORDER BY createtime) AS next_2_time
FROM website_url_info;

--FIRST_VALUE 取分组内排序后，截止到当前行，第一个值
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       FIRST_VALUE(url) OVER(PARTITION BY cookieid ORDER BY createtime) AS first1
FROM website_url_info;

--LAST_VALUE  取分组内排序后，截止到当前行，最后一个值
SELECT cookieid,
       createtime,
       url,
       ROW_NUMBER() OVER(PARTITION BY cookieid ORDER BY createtime) AS rn,
       LAST_VALUE(url) OVER(PARTITION BY cookieid ORDER BY createtime) AS last1
FROM website_url_info;
```

![image-20220924211709553](E:\黑马培训\Hadoop生态圈\assets\image-20220924211709553.png)

```shell
上面的解释为错误解释，正确解释为下面文字

蓝色意思是在createtime字段上第一行，往上取一行交给窗口函数的字段第一行，这里取不到所以交给了默认值
然后就是第二行向上取了一行，也就是取到了第一行交给了窗口函数字段的第二行
依此类推

红色意思是在createtime字段上第一行，往上取一行交给窗口函数的字段第一行，没有设默认值，取不到自动为null
直到第三行2018-04-10 10：03：04向上取2行，得到了2018-04-10 10:00:00 所以把取到的这行的值交给了窗口函数的第三行
依此类推

----------------------------------------------------------------------
1.lead向上偏移 （也叫领先函数）领先的是窗口函数求出来的那个字段
-- 窗口函数求出的字段每一行向下取一行，对比原来字段是上移了

-- lead(col,n,default) 用于统计窗口内向下第n行的值

第一个参数为列名
第二个参数为列名这个字段向下取第几行
第三个参数为默认值 当向下n行为null时取默认值，不指定就为null


向下取，把下面的值取上来 ==> 领先

----------------------------------------------------------------------
2.lag向下偏移 （也叫滞后函数）用于统计窗口内向上第n行的值
-- 窗口函数求出的字段每一行向下取一行，对比原来字段是下移了


第一个参数为列名
第二个参数为列名这个字段向上取第几行
第三个参数为默认值 当向上n行为null时取默认值，不指定就为null

向上取，把上面的值取下来 ==>滞后


对比领先与滞后都是跟函数参数里面的一致字段来比较的

总结：
    取值都是在第一个参数字段的基础上，从第一行，向上或向下取第行  把取到的行的值，赋值给对应的窗口函数字段的行，如果取不到，有默认值则设为默认值，没有默认值，则设为null
```

![image-20220924213746634](E:\黑马培训\Hadoop生态圈\assets\image-20220924213746634.png)



##### 7. 文件存储格式

> （text、ORC、parquet）

![4、行式存储、列数存储](E:\黑马培训\Hadoop生态圈\assets\4、行式存储、列数存储.png)

- 列式存储、行式存储

  - 数据最终在文件中底层以什么样的形成保存。

- Hive中表的数据存储格式，不是只支持text文本格式，还支持其他很多格式。

- hive表的文件格式是如何指定的呢？ 建表的时候通过==STORED AS 语法指定。如果没有指定默认都是textfile==。

- Hive中主流的几种文件格式。

  - textfile 文件格式 

  - ==ORC==、Parquet 列式存储格式。

    ```
    都是列式存储格式，底层是以二进制形式存储。数据存储效率极高，对于查询贼方便。
    二进制意味着肉眼无法直接解析，hive可以自解析。
    ```

  - 栗子

    > 分别使用3种不同格式存储数据，去HDFS上查看底层文件存储空间的差异。

    ```sql
    --1、创建表，存储数据格式为TEXTFILE
    create table log_text (
    track_time string,
    url string,
    session_id string,
    referer string,
    ip string,
    end_user_id string,
    city_id string
    )
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
    STORED AS TEXTFILE;  --如果不写stored as textfile 默认就是textfile
    
    --加载数据
    load data local inpath '/root/hivedata/log.txt' into table log_text;
    
    --2、创建表，存储数据格式为ORC
    create table log_orc(
    track_time string,
    url string,
    session_id string,
    referer string,
    ip string,
    end_user_id string,
    city_id string
    )
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
    STORED AS orc ;
    
    --向表中插入数据 思考为什么不能使用load命令加载？ 因为load是纯复制移动操作 不会调整文件格式。
    insert into table log_orc select * from log_text;
    
    --3、创建表，存储数据格式为parquet
    create table log_parquet(
    track_time string,
    url string,
    session_id string,
    referer string,
    ip string,
    end_user_id string,
    city_id string
    )
    ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
    STORED AS PARQUET ;
    
    --向表中插入数据 
    insert into table log_parquet select * from log_text ;
    ```

![image-20220924223049092](E:\黑马培训\Hadoop生态圈\assets\image-20220924223049092.png)

![image-20220924223118854](E:\黑马培训\Hadoop生态圈\assets\image-20220924223118854.png)

![image-20220924223134715](E:\黑马培训\Hadoop生态圈\assets\image-20220924223134715.png)

![image-20220924223154400](E:\黑马培训\Hadoop生态圈\assets\image-20220924223154400.png)

##### 8. 数据压缩和文件格式搭配

> (ORC+snapp)

- Hive的默认执行引擎是MapReduce，因此通常所说的==Hive压缩指的是MapReduce的压缩==。

- 压缩是指通过==算法对数据进行重新编排==，降低存储空间。无损压缩。

- MapReduce可以在两个阶段进行数据压缩

  - map的输出
    - ==减少shuffle的数据量== 提高shuffle时网络IO的效率
  - reduce的输出
    - 减少输出文件的大小 ==降低磁盘的存储空间==

- 压缩的弊端

  - 浪费时间
  - 消耗CPU、内存
  - 某些优秀的压缩算法需要钱

- 压缩的算法（==推荐使用snappy==）

  ```
  Snappy
  org.apache.hadoop.io.compress.SnappyCodec
  ```

- Hive中压缩的设置：注意 本质还是指的是MapReduce的压缩

  ```sql
  --设置Hive的中间压缩 也就是map的输出压缩
  1）开启 hive 中间传输数据压缩功能
  set hive.exec.compress.intermediate=true;
  2）开启 mapreduce 中 map 输出压缩功能
  set mapreduce.map.output.compress=true;
  3）设置 mapreduce 中 map 输出数据的压缩方式
  set mapreduce.map.output.compress.codec = org.apache.hadoop.io.compress.SnappyCodec;
  
  --设置Hive的最终输出压缩，也就是Reduce输出压缩
  1）开启 hive 最终输出数据压缩功能
  set hive.exec.compress.output=true;
  2）开启 mapreduce 最终输出数据压缩
  set mapreduce.output.fileoutputformat.compress=true;
  3）设置 mapreduce 最终数据输出压缩方式
  set mapreduce.output.fileoutputformat.compress.codec =org.apache.hadoop.io.compress.SnappyCodec;
  4）设置 mapreduce 最终数据输出压缩为块压缩  还可以指定RECORD
  set mapreduce.output.fileoutputformat.compress.type=BLOCK;  
  ```

  ```sql
  --设置完毕之后  只有当HiveSQL底层通过MapReduce程序执行 才会涉及压缩。
  --已有普通格式的表
  select * from student_hdfs ;
  
  --ctas语句
  create table student_snappy as select * from student_hdfs ;
  ```

- 在实际开发中，可以根据需求选择不同的文件格式并且搭配不同的压缩算法。可以得到更好的存储效果。

  ```sql
  --不指定压缩格式 代表什么呢？
  --orc 存储文件默认采用ZLIB 压缩。比 snappy 压缩的小
  STORED AS orc;   --2.78M
  STORED AS orc tblproperties ("orc.compress"="ZLIB");
  
  --以ORC格式存储 不压缩
  STORED AS orc tblproperties ("orc.compress"="NONE");  --7.69M
  
  --以ORC格式存储  使用snappy压缩
  STORED AS orc tblproperties ("orc.compress"="SNAPPY"); --3.78M
  
  --ORC文件还支持一些自带的索引  提高查询效率
  
  
  
  --下面这个表就是只使用orc格式存储  但是不设置任何压缩格式
  create table log_orc_no_compress(
  track_time string,
  url string,
  session_id string,
  referer string,
  ip string,
  end_user_id string,
  city_id string
  )
  ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
  STORED AS orc tblproperties ("orc.compress"="NONE");
  
  insert into table log_orc_no_compress select * from log_text;
  ```

##### 9. fetch抓取机制、MR本地模式

Fetch抓取机制

- 功能：在执行sql的时候，==能不走====MapReduce程序处理就尽量不走MapReduce程序处理==。

- 尽量直接去操作数据文件。

- 设置： hive.fetch.task.conversion= more。

  ```sql
  --在下述3种情况下 sql不走mr程序
  
  --全局查找
  select * from student;
  --字段查找
  select num,name from student;
  --limit 查找
  select num,name from student limit 2;
  ```

---

mapreduce本地模式

- 功能：如果非要执行==MapReduce程序，能够本地执行的，尽量不提交yarn上执行==。

- 默认是关闭的。意味着只要走MapReduce就提交yarn执行。

  ```
  mapreduce.framework.name = local 本地模式
  mapreduce.framework.name = yarn 集群模式 
  ```

- Hive提供了一个参数，自动切换MapReduce程序为本地模式，如果不满足条件，就执行yarn模式。

  ```sql
  set hive.exec.mode.local.auto = true;
   
  --3个条件必须都满足 自动切换本地模式
  The total input size of the job is lower than: hive.exec.mode.local.auto.inputbytes.max (128MB by default)  --数据量小于128M
  
  The total number of map-tasks is less than: hive.exec.mode.local.auto.tasks.max (4 by default)  --maptask个数少于4个
  
  The total number of reduce tasks required is 1 or 0.  --reducetask个数是0 或者 1
  ```

- 切换Hive的执行引擎

  ```
  WARNING: Hive-on-MR is deprecated in Hive 2 and may not be available in the future versions. Consider using a different execution engine (i.e. spark, tez) or using Hive 1.X releases.
  
  如果针对Hive的调优依然无法满足你的需求 还是效率低， 尝试使用spark计算引擎 或者Tez.
  ```



##### 10. 通用调优--join优化

> 底层还是MapReduce的join优化

**map端join与reduce端join**

![5、map端join和reduce端join](E:\黑马培训\Hadoop生态圈\assets\5、map端join和reduce端join.png)

- map join

  > 适合于==小表join大表==或者==小表Join小表==

  ![image-20220924232517923](E:\黑马培训\Hadoop生态圈\assets\image-20220924232517923.png)

  多小的表为小表？ ==> 通过参数来控制

  ![image-20220924232605938](E:\黑马培训\Hadoop生态圈\assets\image-20220924232605938.png)

  

  

  

  ```shell
  #是否开启自动转为mapjoin 在满足条件的情况下 默认true
  hive.auto.convert.join=true
  
  Hive老版本
  #如果参与的一个表大小满足条件 转换为map join
  hive.mapjoin.smalltable.filesize=25000000  
  
  
  Hive2.0之后版本
  #是否启用基于输入文件的大小，将reduce join转化为Map join的优化机制。假设参与join的表(或分区)有N个，如果打开这个参数，并且有N-1个表(或分区)的大小总和小于hive.auto.convert.join.noconditionaltask.size参数指定的值，那么会直接将join转为Map join。
  hive.auto.convert.join.noconditionaltask=true 
  hive.auto.convert.join.noconditionaltask.size=10000000 默认10M 
  ```

  

- reduce join

  > 适合于大表Join大表

  ![image-20220924232923382](E:\黑马培训\Hadoop生态圈\assets\image-20220924232923382.png)

- bucket join

  > 适合于大表Join大表

  - 方式1：Bucktet Map Join

    ```properties
    语法: clustered by colName(参与join的字段)
    参数: set hive.optimize.bucketmapjoin = true
    要求: 分桶字段 = Join字段 ，分桶的个数相等或者成倍数，必须是在map join中
    ```

  - 方式2：Sort Merge Bucket Join（SMB）

    ```properties
    基于有序的数据Join
    语法:clustered by colName sorted by (colName)
    参数
    	set hive.optimize.bucketmapjoin = true;
    	set hive.auto.convert.sortmerge.join=true;
        set hive.optimize.bucketmapjoin.sortedmerge = true;
        set hive.auto.convert.sortmerge.join.noconditionaltask=true;
        
    要求: 分桶字段 = Join字段 = 排序字段,分桶的个数相等或者成倍数
    ```



##### 11. 通用调优--数据倾斜优化

**数据倾斜问题**

![6、数据倾斜的相关问题](E:\黑马培训\Hadoop生态圈\assets\6、数据倾斜的相关问题.png)

- group by数据倾斜

  - 方案一：开启Map端聚合

    ```shell
    hive.map.aggr=true;
    #是否在Hive Group By 查询中使用map端聚合。
    #这个设置可以将顶层的部分聚合操作放在Map阶段执行，从而减轻清洗阶段数据传输和Reduce阶段的执行时间，提升总体性能。但是指标不治本。
    ```

  - 方案二：实现随机分区

    ```
    实现随机分区
    select * from table distribute by rand();
    ```

  - 方案三：数据倾斜时==自动负载均衡==

    ```shell
    hive.groupby.skewindata=true;
    
    #开启该参数以后，当前程序会自动通过两个MapReduce来运行
    
    #第一个MapReduce自动进行随机分布到Reducer中，每个Reducer做部分聚合操作，输出结果
    
    #第二个MapReduce将上一步聚合的结果再按照业务（group by key）进行处理，保证相同的分布到一起，最终聚合得到结果
    ```

- join数据倾斜

  - 方案一：提前过滤，将大数据变成小数据，实现Map Join

  - 方案二：使用Bucket Join

  - 方案三：使用Skew Join

    ```shell
    #将Map Join和Reduce Join进行合并，如果某个值出现了数据倾斜，就会将产生数据倾斜的数据单独使用Map Join来实现
    
    #其他没有产生数据倾斜的数据由Reduce Join来实现，这样就避免了Reduce Join中产生数据倾斜的问题
    
    #最终将Map Join的结果和Reduce Join的结果进行Union合并
    
    #开启运行过程中skewjoin
    set hive.optimize.skewjoin=true;
    #如果这个key的出现的次数超过这个范围
    set hive.skewjoin.key=100000;
    #在编译时判断是否会产生数据倾斜
    set hive.optimize.skewjoin.compiletime=true;
    set hive.optimize.union.remove=true;
    #如果Hive的底层走的是MapReduce，必须开启这个属性，才能实现不合并
    set mapreduce.input.fileinputformat.input.dir.recursive=true;
    
    ```

----



##### 12. 通用调优--MR程序task个数调整

- maptask个数

  - 如果是在MapReduce中 maptask是通过==逻辑切片==机制决定的。

  - 但是在hive中，影响的因素很多。比如逻辑切片机制，文件是否压缩、压缩之后是否支持切割。

  - 因此在==Hive中，调整MapTask的个数，直接去HDFS调整文件的大小和个数，效率较高==。

    ```
    如果小文件多，就进行小文件的合并  合并的大小最好=block size
    如果大文件多，就调整blocl size
    ```

- reducetask个数

  - 如果在MapReduce中，通过代码可以直接指定  job.setNumReduceTasks(N)

  - 在Hive中，reducetask个数受以下几个条件控制的

    ```sql
    （1）每个 Reduce 处理的数据量默认是 256MB
    hive.exec.reducers.bytes.per.reducer=256000000
    （2）每个任务最大的 reduce 数，默认为 1009
    hive.exec.reducsers.max=1009
    （3）mapreduce.job.reduces
    该值默认为-1，由 hive 自己根据任务情况进行判断。
    
    
    --如果用户用户不设置 hive将会根据数据量或者sql需求自己评估reducetask个数。
    --用户可以自己通过参数设置reducetask的个数
      set mapreduce.job.reduces = N
    --用户设置的不一定生效，如果用户设置的和sql执行逻辑有冲突，比如order by，在sql编译期间，hive又会将reducetask设置为合理的个数。  
    
    Number of reduce tasks determined at compile time: 1
    ```

----

##### 13. 通用调优--执行计划

- 通过执行计划可以看出==hive接下来是如何打算执行这条sql的==。

- 语法格式：explain + sql语句

- 栗子

  ```sql
  explain select * from student;
  
  +----------------------------------------------------+
  |                      Explain                       |
  +----------------------------------------------------+
  | STAGE DEPENDENCIES:                                |
  |   Stage-0 is a root stage                          |
  |                                                    |
  | STAGE PLANS:                                       |
  |   Stage: Stage-0                                   |
  |     Fetch Operator                                 |
  |       limit: -1                                    |
  |       Processor Tree:                              |
  |         TableScan                                  |
  |           alias: student                           |
  |           Statistics: Num rows: 1 Data size: 5260 Basic stats: COMPLETE Column stats: NONE |
  |           Select Operator                          |
  |             expressions: num (type: int), name (type: string), sex (type: string), age (type: int), dept (type: string) |
  |             outputColumnNames: _col0, _col1, _col2, _col3, _col4 |
  |             Statistics: Num rows: 1 Data size: 5260 Basic stats: COMPLETE Column stats: NONE |
  |             ListSink                               |
  |                                                    |
  +----------------------------------------------------+
  ```

-----

##### 14. 并行机制、推测执行机制

- 并行执行机制

  - 如果hivesql的底层某些stage阶段可以并行执行，就可以提高执行效率。

  - 前提是==stage之间没有依赖==  并行的弊端是瞬时服务器压力变大。

  - 参数

    ```sql
    set hive.exec.parallel=true; --是否并行执行作业。适用于可以并行运行的 MapReduce 作业，例如在多次插入期间移动文件以插入目标
    set hive.exec.parallel.thread.number=16; --最多可以并行执行多少个作业。默认为8。
    ```

- Hive的严格模式

  - 注意。不要和动态分区的严格模式搞混淆。

  - 这里的严格模式指的是开启之后 ==hive会禁止一些用户都影响不到的错误包括效率低下的操作==，不允许运行一些有风险的查询。

  - 设置

    ```sql
    set hive.mapred.mode = strict --默认是严格模式  nonstrict
    ```

  - 解释

    ```
    1、如果是分区表，没有where进行分区裁剪 禁止执行
    2、order by语句必须+limit限制
    ```

- 推测执行机制

  - MapReduce中task的一个机制。
  - 功能：
    - 一个job底层可能有多个task执行，如果某些拖后腿的task执行慢，可能会导致最终job失败。
    - 所谓的==推测执行机制就是通过算法找出拖后腿的task,为其启动备份的task==。
    - 两个task同时处理一份数据，谁先处理完，谁的结果作为最终结果。
  - 推测执行机制默认是开启的，但是在企业生产环境中==建议关闭==。



### 7. 建表时存储数据的文件格式

**1. textfile**

Hive数据库表的默认格式，存储方式 行式存储
可以使用Gzip压缩算法，但压缩后的文件不支持split
在反序列化过程中，必须逐个字符判断是不是分隔符和行结束符，因此反序列化开销会比SequenceFile高几十倍

建表时直接在建表语句最后写上
stored as textfile

**2. sequencefile**

压缩数据文件可以节省磁盘空间
但Hadoop中有些原生压缩文件的缺点之一就是不支持分割。
支持分割的文件可以并行的有多个mapper程序处理大数据文件，大多数文件不支持可分割是因为这些文件只能从头开始读。
Sequence File是可分割的文件格式，支持Hadoop的block级压缩。
Hadoop API提供的一种二进制文件，以key-value的形式序列化到文件中。
存储方式：行存储。
sequencefile支持三种压缩选择：
NONE，RECORD，BLOCK。
Record压缩率低，RECORD是默认选项，通常BLOCK会带来较RECORD更好的压缩性能。
优势是文件和hadoop api中的MapFile是相互兼容的

建表时直接在建表语句最后写上
stored as sequencefile

**3. rcfile**

存储方式：数据按行分块，每块按列存储。
结合了行存储和列存储的优点：
首先，RCFile 保证同一行的数据位于同一节点，因此元组重构的开销很低
其次，像列存储一样，RCFile 能够利用列维度的数据压缩，并且能跳过不必要的列读取
数据追加：RCFile不支持任意方式的数据写操作，仅提供一种追加接口，这是因为底层的 HDFS当前仅仅支持数据追加写文件尾部。
行组大小：行组变大有助于提高数据压缩的效率，但是可能会损害数据的读取性能，因为这样增加了 Lazy 解压性能的消耗。而且行组变大会占用更多的内存，这会影响并发执行的其他MR作业。 
考虑到存储空间和查询效率两个方面，Facebook 选择 4MB 作为默认的行组大小，当然也允许用户自行选择参数进行配置。

建表时直接在建表语句最后写上
stored as rcfile

**4. ORCFile**

存储方式：数据按行分块，每块按照列存储。
压缩快，快速列存取。效率比rcfile高，是rcfile的改良版本

建表时直接在建表语句最后写上
stored as orc

**小结**
TextFile默认格式，加载速度最快，可以采用Gzip进行压缩，压缩后的文件无法split，即并行处理。
SequenceFile压缩率最低，查询速度一般，将数据存放到sequenceFile格式的hive表中，这时数据就会压缩存储。三种压缩格式NONE，RECORD，BLOCK。是可分割的文件格式。
RCfile压缩率最高，查询速度最快，数据加载最慢。
相比TEXTFILE和SEQUENCEFILE，RCFILE由于列式存储方式，数据加载时性能消耗较大，但是具有较好的压缩比和查询响应。数据仓库的特点是一次写入、多次读取，因此，整体来看，RCFILE相比其余两种格式具有较明显的优势。
在hive中使用压缩需要灵活的方式，如果是数据源的话，采用RCFile+bz或RCFile+gz的方式，这样可以很大程度上节省磁盘空间；而在计算的过程中，为了不影响执行的速度，可以浪费一点磁盘空间，建议采用RCFile+snappy的方式，这样可以整体提升hive的执行速度。至于lzo的方式，也可以在计算过程中使用，只不过综合考虑（速度和压缩比）还是考虑snappy适宜。

相比TEXTFILE和SEQUENCEFILE，RCFILE由于列式存储方式，数据加载时性能消耗较大，但是具有较好的压缩比和查询响应。数据仓库的特点是一次写入、多次读取，因此，整体来看，RCFILE相比其余两种格式具有较明显的优势。

**在一般的大数据开发过程中，使用ORC就可以**

**注意**

默认建表时存储的数据类型是textfile,所以这个表只能解析textfile的数据类型文件，想要解析别的数据类型的文

件，建表时就得指定表的存储数据的类型





