# Python-ETL

## 1. 时间戳是什么?

　　时间戳是指格林威治时间1970年01月01日00时00分00秒(北京时间1970年01月01日08时00分00秒)起至现在的总秒数。

## 2. Python写入数据到磁盘

**作业流程图示：**

![image-20220902210500054](E:\黑马培训\assets\image-20220902210500054.png)

程序在写入数据的时候不是把文件全部直接写入磁盘的，是先把数据放在内存的文件内容缓冲区中，最后再写入磁盘中，但是也可以人为

的刷新缓冲区，利用flush函数，刷新缓冲区后，刷新出来的数据会写入磁盘内

## 3. ETL基础知识

### 3.1 数据孤岛

企业中的业务数据，通常会分散到非常多的业务数据库中。如果要对这些数据，进行数据分析，因为数据零散存储，就会造成统一分析的困难。

这个问题，就是数据分析中的：**数据孤岛问题**。

![img](E:\黑马培训\assets\1.png)

### 3.2 数据仓库

为了解决数据孤岛问题，我们需要将数据，集中的存储起来，方便集中进行分析。

这种集中数据进行分析的方案，我们一般称之为：**数据仓库**。

![img](E:\黑马培训\assets\2.png)

### 3.3 ETL的概念

既然数据仓库是集中进行数据存储和分析的地方，那么就会涉及到，**如何将零散的数据，集中的输入到数据仓库中？？？**

**答：这个集中输入数据的工作叫做：ETL**

- E(Extract)：抽取，数据的输入
- T(Transform)：转换，数据的处理
- L(Load)：加载，数据的输出

对于ETL来说，就是简单的将数据：从 A 处理到 B。

![image-20220904222240349](E:\黑马培训\assets\image-20220904222240349.png)

### 3.4 ETL的常见实现方式

ETL的实现方式有很多种，最常见的有2种：

- 使用工具去完成ETL的相关任务，如：Kettle、sqoop、flume、datax
  - 优势：简单、易用，配置方便，无需写代码，鼠标拖拖拽拽就能完成。
  - 劣势：不够灵活，特定的任务需求难以实现。
- 使用编程语言去自行开发ETL系统，比如Python、Java等
  - 优势：非常自由，想怎么处理就怎么处理。
  - 劣势：开发时间长。

> 注：这两种方式在企业中都是广泛存在的。
>
> - 简单的ETL任务，常用工具去实现
> - 复杂的ETL任务，常用代码去实现
>
> 本次课程主要使用：Python语言去完成一个ETL的案例。

### 3.5 常见的数据格式

**1. CSV数据格式**

使用固定的分隔符，将数据分隔成多个列的一种文本文件格式。

分隔符可以是任意字符，一般情况下会常用：`逗号`、`分号`、`制表符`、`空格` 等符号。

**CSV的示例：**

```shell
id,name,age
1,潇潇,11
2,甜甜,11
3,美美,11
```

- 上面的CSV数据，使用逗号作为分隔符。

- **CSV格式是可以有标头（Header）的**，用来说明具体的列的含

  义。上面的CSV数据，就是有Header的CSV，表示数据具有id、

  name、age三列数据，Header不是必须的，可以有，也可以没

  有。

```shell
# 其他分隔符示例
id;name;age
1;潇潇;11
2;甜甜;11
3;美美;11
```

### 3.6 JSON数据格式

JSON格式和Python中的字典本质上一样的。Python中的字典可以和JSON格式，进行无缝的切换。

**JSON的示例：**

```python
{
  "name": "zhangsan", 
  "age": 10, 
  "like": ["football", "music"]
}

{
    "sites": [
        { "name":"菜鸟教程" , "url":"www.runoob.com" }, 
        { "name":"google" , "url":"www.google.com" }, 
        { "name":"微博" , "url":"www.weibo.com" }
    ]
}
```

- 上面是一个典型的JSON，也可以说是一个典型的Python字典数据。
- JSON或者Python字典，本质上都是Key-Value型的数据结构，key一般是字符串，表示key的名字，value是任意类型，可以是字符串、数字、list、字典。

### 3.7 XML数据格式

XML格式和JSON一样，也是Key-Value型的数据记录格式。

**XML的示例：**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<note>
  <to>Tove</to>
  <from>Jani</from>
  <heading>Reminder</heading>
  <body>Don't forget me this weekend!</body>
</note>

# 如上，是一个典型的XML格式，它可以和JSON进行无缝转换
{
  "note": [
    "to": "Tove", 
    "from": "Jani", 
    "heading": "Reminder", 
    "body": "Don't forget me this weekend!"
  ]
}
```

> 注：XML格式一般我们很少去处理它，但是在很多的框架中，XML是常见的配置文件格式。比如Hadoop、Hive等都是使用XML作为配置文件格式。

### 3.8 结构化、半结构化、非结构化数据

#### 3.8.1 结构化数据

**概念：可以用Schema描述的数据，就是结构化的数据**。

Schema：数据的描述，可以描述一份数据的具体属性，比如有几个列，每个列是什么含义，可以简单的认为Schema就是类似数据库的`表结构`。

可以简单认为： 可以转换成 `二维表格`的数据，就是结构化的数据。

**常见的结构化数据：**

- 数据库中的表
  - 可以用Schema描述，也就是被表结构所描述
- CSV
  - 同样可以被Schema描述，有表结构（有几个列，每个列是什么）
- Excel
  - 就是一个二维表格、就是结构化数据

> 注意：JSON不一定是结构化，具体看JSON内的内容
>
> - 如果是简单的Key-Value可以是结构化（可以转换成二维表格）
> - 如果是复杂嵌套JSON，那么就不是结构化了。
>
> JSON就是典型的半结构化数据。

#### 3.8.2 半结构化数据

**概念：可以用Schema描述其部分内容的数据，叫做半结构化数据。**

简单的说：半结构化数据部分内容可以描述成二维表格，但是不一定能完全描述成二维表格。

**常见的半结构化数据：**

- JSON
- XML

**示例1：**

```json
{
    "name":"潇潇",
    "age":11
}
```

是结构化数据吗？

- 是结构化的JSON，因为没有复杂嵌套可以被Schema（转换成二维表）描述

**示例2**：

```python
{
  "name":"潇潇",
  "age":11,
  "info":{
      "addr":"北京",
      "tel":13311113333,
      "hobby": ["football", "baskteball"]
  }
}
```

是结构化数据吗？

- 不是，因为无法用固定Schema去描述它，只能描述部分，不能描述全部。

> 注意：半结构化具体看内容，如果内容简单无嵌套，可以当做结构化数据处理；如果内容复杂有嵌套，一般就不能作为结构化数据处理。

#### 3.8.3 非结构化数据

**概念：完全无法用Schema描述的数据，叫做非结构化数据**。

**常见的非结构化数据：**

- markdown文本
- word文档
- mp3
- avi、mp4

> 以后在开发中，最常见到的是：
>
> - 结构化数据处理
> - 半结构化数据处理
>
> 非结构化数据一般不会去处理。

# 1. ETL综合案例

## 1.1 ETL案例需求

### 1.1.1 案例背景说明

本次案例，**是处理的一个零售公司的业务数据**。这个公司是卖收银机的，收银机分布在各大商超中。用户去买东西，收银机结账后，将用户购买的信息，通过网络发送到公司的后台。

基于这个前提，公司后台就会收集到非常多的用户购买的订单数据。

所以，**我们需要做的是，将这个公司的业务数据，进行ETL任务，采集到MySQL以及文件存储中**。

![img](E:\黑马培训\assets\1-16623037058553.png)

### 1.1.2 哪些数据需要采集

1）**订单数据**（用户购买商品，通过网络发送到后台的订单信息）

- **存放在JSON文件中**

2）**商品库的数据**（存储了商品信息）

- **存放在后台的MySQL数据库中**

3）**后台的日志数据**（记录了后台的被访问信息）

- **存放在后台的日志文件中**

![img](E:\黑马培训\assets\2-16623037058584.png)

### 1.1.3 数据采集后输出到哪里

这三种数据，采集后，都需要完成2件事情：

1）将数据写入到数据仓库（MySQL）中

> 因为我们还没学数仓技术，课程中以MySQL代替

2）将数据写出为CSV文件，作为数据的一个备份

### 1.1.4 需求总结

从：JSON文件（订单数据）、MySQL数据库（商品信息）、后台日志文件（log数据）三个地方采集数据。将它们写出到：

- MySQL数据库
- CSV文件

## 1.2 订单数据采集

### 1. 业务需求说明

被采集的订单数据是JSON文件，订单数据会不定期的在某个文件夹内产生一个新的JSON文件(每次生成的JSON文件大小在1MB左右，内容大约1000条)，**且文件名不会重复**，我们的ETL程序，**会定期执行进行数据的采集（比如5分钟、10分钟）**。

**json数据格式**：

下面是一条示例数据：

```json
{
  "discountRate": 1,
  "storeShopNo": "None",
  "dayOrderSeq": 37,
  "storeDistrict": "芙蓉区",
  "isSigned": 0,
  "storeProvince": "湖南省",
  "origin": 0,
  "storeGPSLongitude": "undefined",
  "discount": 0,
  "storeID": 1766,
  "productCount": 1,
  "operatorName": "OperatorName",
  "operator": "NameStr",
  "storeStatus": "open",
  "storeOwnUserTel": 12345678910,
  "payType": "cash",
  "discountType": 2,
  "storeName": "亿户超市郭一一食品店",
  "storeOwnUserName": "OwnUserNameStr",
  "dateTS": 1542436490000,
  "smallChange": 0,
  "storeGPSName": "None",
  "erase": 0,
  "product": [
    {
      "count": 1,
      "name": "南京特醇",
      "unitID": 8,
      "barcode": "6901028300056",
      "pricePer": 12,
      "retailPrice": 12,
      "tradePrice": 11,
      "categoryID": 1
    }
  ],
  "storeGPSAddress": "None",
  "orderID": "154243648991517662217",
  "moneyBeforeWholeDiscount": 12,
  "storeCategory": "normal",
  "receivable": 12,
  "faceID": "",
  "storeOwnUserId": 1694,
  "paymentChannel": 0,
  "paymentScenarios": "OTHER",
  "storeAddress": "StoreAddress",
  "totalNoDiscount": 12,
  "payedTotal": 12,
  "storeGPSLatitude": "undefined",
  "storeCreateDateTS": 1540793134000,
  "storeCity": "长沙市",
  "memberID": "0"
}
```

字段说明：

```bash
discountRate 折扣率
storeShopNo 店铺店号（无用列）
dayOrderSeq 本单为当日第几单
storeDistrict 店铺所在行政区
isSigned 是否签约店铺（签约第三方支付体系）
storeProvince 店铺所在省份
origin 原始信息（无用）
storeGPSLongitude 店铺GPS经度
discount 折扣金额
storeID 店铺ID
productCount 本单售卖商品数量
operatorName 操作员姓名
operator 操作员ID
storeStatus 店铺状态
storeOwnUserTel 店铺店主电话
payType 支付类型
discountType 折扣类型
storeName 店铺名称
storeOwnUserName 店铺店主名称
dateTS 订单时间
smallChange 找零金额
storeGPSName 店铺GPS名称
erase 是否抹零
storeGPSAddress 店铺GPS地址
orderID 订单ID
moneyBeforeWholeDiscount 折扣前金额
storeCategory 店铺类别
receivable 应收金额
faceID 面部识别ID
storeOwnUserId 店铺店主ID
paymentChannel 付款通道
paymentScenarios 付款情况（无用）
storeAddress 店铺地址
totalNoDiscount 整体价格（无折扣）
payedTotal 已付款金额
storeGPSLatitude 店铺GPS纬度
storeCreateDateTS 店铺创建时间
storeCity 店铺所在城市
memberID 会员ID

# 商品字段
count 本单卖几个
name 名称
unitID 单位ID
barcode 条码
price_per 卖出单价
retail_price 零售价(建议)
trade_price 成本价(建议)
category_id 类别ID
```

我们要做的就是，每一次执行，通过程序读取 JSON 文件处理它们，但要注意的是：**已经处理过的文件不可以重复处理**。

**思考：如何做到已处理文件不重复处理？**

1）已处理文件名记录到一个文件中

2）已处理的文件名记录到数据库的表中

我们选择第二种方式，将已经处理过的文件信息，记录到数据库中，因为：

- 数据库存储这些记录信息，更加安全（文件很容易被误删、磁盘损坏），数据库有专业运维，数据丢失损坏的风险远小于文件
- 数据库存储记录，无论是存储、修改等都更加方便（SQL就搞定），如果是文件的话，一般需要整个文件进行覆盖才能完成修改操作没有数据库方便

**元数据管理**

我们选择将已处理的文件信息，放入数据库中进行保存，这个数据库，我们可以称之为：**元数据库**。

**什么是元数据？**

- 元数据：状态、数据的描述，即描述数据的数据。
- 比如，我们记录的哪些文件被处理了，这个就是程序执行中的状态，这个状态就是**元数据**。
- 我们将元数据通过数据库进行保存，保存元数据的库我们称之为：**元数据库**。

> 元数据英文：metadata

**关于MySQL在项目中的使用**

我们在本次ETL项目中会接触三个数据库：

- **元数据管理的库**
- **提供数据的数据源库**
- **数据处理的目的地库**

这三个数据库，由于我们学习阶段，自己电脑里面只有一个MySQL，所有这三个库，我们都在一个MySQL里面使用。

> **注意：这是一个模拟的行为，要知道，在企业中，这三个库，很可能存在与不同的服务器（电脑）上。**

所以，在自己的MySQL中要创建这三个库

```sql
# 创建元数据管理库
CREATE DATABASE metadata CHARACTER SET utf8;
# 创建数据源库（提供被采集数据的）
CREATE DATABASE source_data CHARACTER SET utf8;
# 创建零售数据库(目的地)
CREATE DATABASE retail CHARACTER SET utf8;
```

### 2. 项目结构搭建

**项目工程创建**

1）使用 PyCharm 创建一个名为 **python-etl** 的工程项目：

![img](E:\黑马培训\assets\1-16623056923157.png)

2）在创建好的工程项目目录下，创建如下 5 个 Package

![img](E:\黑马培训\assets\2-16623056923188.png)

下面对上面 5 个 Package 的作用进行简要的介绍，具体作用随着后续的学习，会有进一步的说明：

- config：保存整个ETL工程的配置信息
- model：保存项目的数据模型文件
- test：保存单元测试的代码文件
- util：保存项目的工具文件
- learn：保存项目开发过程中的一些基础知识讲解练习文件（实际不需要）

### 3. 日志模块使用

**程序日志简介**

**程序日志简介及作用**

问题：

```
1. 什么是程序日志？
2. 日志有什么作用？
3. python中如何记录日志？
```

**什么是程序日志？**

程序的日志是记录程序在运行过程中，一些关键性的信息，比如：程序处理了XX文件，XX用户做了XX操作，服务器出现了XX错误等。一个合格的程序，日志是必须要输出的。

![img](E:\黑马培训\assets\3-166230589689711.png)

**日志有什么作用？**

1）可以很方便的了解程序的运行情况

2）方便开发人员检查bug

3）可以分析用户的操作行为、喜好等信息

**python中如何记录日志？**

python中，可以使用 logging 模块记录日志。

```python
import logging
```

**程序日志的5个等级说明**

同其他信息一样，日志信息也是分重要程度的。

日志按重要程度从低到高，分成如下 5 个等级：

1）DEBUG：程序调试bug时使用，话痨级别，使用这个级别，会输出非常详细的日志内容。

2）INFO：程序正常运行时使用，普通的信息，输出到日志中，比如处理到了多少条数据。

3）WARNING：程序未按预期运行时使用，但并不是错误，有可能有隐患，输出日志供程序员查看。

4）ERROR：程序出错误时使用，但是不一定影响运行，输出到日志中，供程序员分析。

5）CRITICAL：特别严重的问题(灾难级别)，如果输出这个级别的日志，表明程序完蛋了。

> 从低到高的：DEBUG < INFO < WARNING < ERROR < CRITICAL

**logging 模块的使用**

**logging 控制日志输出级别**

logging 模块是Python自带的日志输出工具，可以方便的输出日志信息。在logging模块中，我们可以控制允许哪个级别的日志被输出。

每一个具体的级别都会有一个数字代码：

```shell
CRITICAL = 50
FATAL = CRITICAL
ERROR = 40
WARNING = 30
WARN = WARNING
INFO = 20
DEBUG = 10
NOTSET = 0
```

- 比如：日志级别设置为INFO（20），logging将不会输出DEBUG和NOTSET级别的日志；
- 比如：日志级别设置为WARN（30），logging将不会输出INFO、DEBUG、NOTSET级别的日志；
- 即：设置了日志级别之后，小于这个级别的日志不会被输出，哪怕你在代码中调用了这个级别的输出也没用。
- **注意：logging 模块中的默认日志输出级别是 WARN。**

**logging 模块的基本使用**

```python
"""
目标：学习 logging 模块的基本使用
"""
# 导入 logging 模块
import logging

# 创建一个 logger 日志器对象
logger = logging.getLogger()

# 创建一个 handler 日志处理器对象，用于指定将日志输出到什么地方
# StreamHandler表示将日志输出到控制台
stream_handler = logging.StreamHandler()

# 添加 handler 日志处理器对象
logger.addHandler(stream_handler)

# 设置日志的输出控制级别：默认是 WARNING，如果不做设置，低于WARNING基本的日志不会输出
logger.setLevel(logging.DEBUG)

# 使用 logger 日志器对象记录日志信息
logger.debug('我是debug信息')
logger.info('我的info信息')
logger.warning('我是warning信息')
logger.error('我是error信息')
logger.critical('我是critical信息')
```

**执行结果：**

```bash
我是debug信息
我的info信息
我是warning信息
我是error信息
我是critical信息
```

**设置日志输出的格式**

```python
"""
目标：学习 logging 模块的基本使用
"""
# 导入 logging 模块
import logging

# 创建一个 logger 日志器对象
logger = logging.getLogger()

# 创建一个 handler 日志处理器对象，用于将日志输出到控制台
stream_handler = logging.StreamHandler()
# 创建一个日志格式控制对象，指定日志信息输出的格式
fmt = logging.Formatter('%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')

# 通过 handler 日志处理器对象设置日志输出的格式
stream_handler.setFormatter(fmt)
# 添加 handler 日志处理器对象
logger.addHandler(stream_handler)

# 设置日志的输出控制级别：默认是 WARNING
logger.setLevel(logging.DEBUG)

# 使用 logger 日志器对象记录日志信息
logger.debug('我是debug信息')
logger.info('我的info信息')
logger.warning('我是warning信息')
logger.error('我是error信息')
logger.critical('我是critical信息')
```

**执行结果**：

```bash
2022-07-31 16:51:34,685 - [DEBUG] - 02-设置日志输出的格式.py[24]: 我是debug信息
2022-07-31 16:51:34,685 - [INFO] - 02-设置日志输出的格式.py[25]: 我的info信息
2022-07-31 16:51:34,685 - [WARNING] - 02-设置日志输出的格式.py[26]: 我是warning信息
2022-07-31 16:51:34,685 - [ERROR] - 02-设置日志输出的格式.py[27]: 我是error信息
2022-07-31 16:51:34,685 - [CRITICAL] - 02-设置日志输出的格式.py[28]: 我是critical信息
```

**附录-Formatter参数列表**:

```python
%(name)s            Name of the logger (logging channel)
%(levelno)s         日志的等级数字
%(levelname)s       日志的等级字符串
%(pathname)s        日志的路径
%(filename)s        输出日志的文件名称
%(module)s          Module (name portion of filename)
%(lineno)d          输出日志的代码的行数
%(funcName)s        方法名称
%(created)f         Time when the LogRecord was created (time.time()
                    return value)
%(asctime)s         输出日志的时间
%(msecs)d           Millisecond portion of the creation time
%(relativeCreated)d Time in milliseconds when the LogRecord was created,
                    relative to the time the logging module was loaded
                    (typically at application startup time)
%(thread)d          Thread ID (if available)
%(threadName)s      Thread name (if available)
%(process)d         Process ID (if available)
%(message)s         日志的正文信息
```

**日志信息输出到文件中**

很多时候，需要将程序的日志信息输出到文件中，此时只需要将 StreamHandler 日志处理器替换为 FileHandler 日志处理器就可以了。

**logging 有  2个常用Handler：**

- StreamHandler：`logging.StreamHandler()`

- FileHandler：

  ```
  logging.FileHandler(参数1, 参数2, 参数3)
  ```

  - 参数1：文件路径
  - 参数2：模式，"a"是追加，"w"是覆盖
  - 参数3：编码，一般utf8

**注意：以下代码需要在 python-etl 工程目录下先创建一个 logs 日志目录，否则执行会出错！！！**

```python
"""
目标：学习 logging 模块的基本使用
"""
# 导入 logging 模块
import logging

# 创建一个 logger 日志器对象
logger = logging.getLogger()

# 创建一个 handler 日志处理器对象，用于将日志输出到文件中
file_handler = logging.FileHandler('../logs/test.log', 'a', 'utf8')
# 创建一个日志格式控制对象，指定日志信息输出的格式
fmt = logging.Formatter('%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')

# 通过 handler 日志处理器对象设置日志输出的格式
file_handler.setFormatter(fmt)
# 添加 handler 日志处理器对象
logger.addHandler(file_handler)

# 设置日志的输出控制级别：默认是 WARNING
logger.setLevel(logging.DEBUG)

# 使用 logger 日志器对象记录日志信息
logger.debug('我是debug信息')
logger.info('我的info信息')
logger.warning('我是warning信息')
logger.error('我是error信息')
logger.critical('我是critical信息')
```

**执行结果：**

![img](E:\黑马培训\assets\4.png)

**封装日志记录工具方法**

**项目配置文件的创建**

在 config 中，新建一个 project_config.py 文件，用于记录本项目相关的配置信息。首先我们可以将日志项目的配置信息，如：日志输出的目录、日志文件名记录到文件中，具体如下：

```python
"""
ETL项目中的所有配置相关信息，都记录到本文件中。
"""

import time

# ################## --程序运行日志的配置项-- ###################
# 配置日志输出的目录
log_root_path = "C:/Users/smart/Desktop/pycode/python-etl/logs/"
# 配置日志输出的文件名
log_name = f'pyetl-{time.strftime("%Y%m%d-%H", time.localtime())}.log'
```

常见的时间格式化的格式：

```bash
%Y：4位数字的年份
%m：2位数字的月份
%d：2位数字的日期
%H：24小时制的小时
%M：2位数字的分钟
%S：2位数字的秒
```

**日志工具方法开发**

在 util 包中，新建一个 logging_util.py 文件，准备日志相关的工具方法：

```python
"""
这个python文件的功能，是构建日志输出的模块，方便我们后续快速的在程序中记录日志信息
"""
import logging
from config import project_config as conf


class Logging(object):
    def __init__(self, level=logging.INFO):
        # 创建一个日志器对象
        self.logger = logging.getLogger()
        # 设置日志的输出控制级别
        self.logger.setLevel(level)


# 定义一个函数，通过这个函数可以返回所需的 logger 对象
def init_logger():
    """日志器对象创建方法"""
    # 创建一个日志器对象
    logger = Logging().logger

    # 设置日志信息输出到指定文件中
    file_handler = logging.FileHandler(filename=conf.log_root_path + conf.log_name,
                                       mode='a',
                                       encoding='utf8')

    # 设置日志信息输出的格式
    fmt = logging.Formatter(
        '%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')

    # 将格式设置到文件的 handler 中
    file_handler.setFormatter(fmt)

    # 将文件输出的 handler 设置给 logger 对象
    logger.addHandler(file_handler)

    # 返回日志器对象
    return logger


if __name__ == '__main__':
    logger = init_logger()
    logger.info('我是info信息')
    logger.warning('我是warning信息')
    logger.error('我是error信息')
```

**测试结果**：

![img](E:\黑马培训\assets\5.png)

### 4. 单元测试

**单元测试简介**

**问题：**

```python
1. 什么是单元测试？
2. 如果进行单元测试？
```

**什么是单元测试？**

单元测试（unit testing），是指对软件中的最小可测试单元进行检查和验证。比如我们在 python 中写了一个函数，来测试一个这个函数的功能是否正确，这个过程就叫单元测试。

**如果进行单元测试？**

在 python 中，有一个内置的专门进行单元测试的模块：unittest，可以通过 unittest 来编写单元测试代码。

**unittest 使用简介**

使用 unittest 编写单元测试代码，需要自定义一个单元测试类，该类必须继承 unittest 中的 TestCase 类，然后在类中定义单元测试的方法即可。

> 一个单元测试类，一般针对一个python文件，python文件中的功能方法，可以对应测试类的一个测试方法。

```python
from unittest import TestCase

class 自定义单元测试类(TestCase):
    def setUp(self) -> None:
        """该方法会在每个单元测试方法执行之前自动执行一次，可以做一些单元测试前的初始工作"""
        pass

    # 注意：所有的单元测试方法名必须以 test 开头，否则不能被 unittest 识别
    def test_单元测试方法1(self):
        pass

    def test_单元测试方法2(self):
        pass

    ...
```

**日志工具方法单元测试类**

在 test 包中创建一个 test_logging_util.py 文件，在其中编写日志工具方法的单元测试代码，具体如下：

```python
"""
日志工具方法单元测试类
"""
from logging import RootLogger
from unittest import TestCase
from util import logging_util


class TestLoggingUtil(TestCase):
    """日志工具方法单元测试类"""
    def setUp(self) -> None:
        pass

    def test_get_logger(self):
        logger = logging_util.init_logger()
        self.assertIsInstance(logger, RootLogger)
```

按照如下图示点击执行 test_get_logger 单元测试方法：

![img](E:\黑马培训\Python额外补充\assets\6.png)

**执行结果**：

![img](E:\黑马培训\Python额外补充\assets\7.png)

**单元测试结果验证方法**

编写的单元测试类继承`TestCase`后，类中就有对应的验证方法，常见验证方法如下：

```python
# 验证arg1和arg2是否相等，如果相等测试通过
self.assertEqual(arg1, arg2)
# 验证arg1和arg2是否不相等，不相等测试通过
self.assertNotEqual(arg1, arg2)

# 验证arg1是不是cls类型，是则测试通过
self.assertIsInstance(arg1, cls)
# 验证arg1是否不是cls类型，不是则测试通过
self.assertNotIsInstance(arg1, cls)

# 验证arg1是否是None，是就通过
self.assertIsNone(arg1)
# 验证arg1是否是True，是就通过
self.assertTrue(arg1)

# 验证arg1是否小于arg2，小于就通过
self.assertLess(arg1, arg2)
# 验证arg1是否小于等于arg2，小于等于就通过
self.assertLessEqual(arg1, arg2)
# 验证arg1是否大于arg2，大于就通过
self.assertGreater(arg1, arg2)
# 验证arg1是否大于等于arg2，大于等于就通过
self.assertGreaterEqual(arg1, arg2)
```

### 5. 确定待采集文件

**订单JSON数据ETL思路**

**业务逻辑**：

1. 获取被采集JSON数据文件夹内的文件列表
2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
3. 分别读取待采集的JSON文件，进行ETL操作（读取->写出CSV->写出MySQL）
4. 将本次处理的JSON文件信息，记录到元数据库表中。

**已采集文件元数据库表创建**：

在 metadata 元数据库中，创建一个 json_file_monitor 表，用于记录哪些订单JSON文件被处理过。

```sql
# 使用元数据管理库
USE metadata;
# 创建已采集文件数据表
CREATE TABLE json_file_monitor (
    id INT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL COMMENT '处理文件名称',
    process_lines INT NULL COMMENT '文件处理行数',
    process_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '文件处理时间'
);
# 执行如下SQL，添加一些测试数据，假设已有文件被采集过
INSERT INTO json_file_monitor (file_name, process_lines)
VALUES
    ('D:/etl/json/x00', 1024),
    ('D:/etl/json/x01', 1024),
    ('D:/etl/json/x02', 1024);
```

![img](E:\黑马培训\Python额外补充\assets\8.png)

**文件工具方法的开发**

1）在 util 包目录下，创建 `file_util.py` 文件，实现代码：

```python
"""
文件工具相关方法
"""
import os


def get_dir_files_list(path='./', recursion=False):
    """
    功能：获取指定目录下的所有文件列表
    参数：
    * path：指定的目录，默认为当前目录
    * recursion：是否递归获取子目录下的文件内容，默认为：False
    返回值：
    * 列表，内含指定目录下所有文件的绝对路径
    """
    name_list = os.listdir(path)
    files = []

    for name in name_list:
        join_path = os.path.join(path, name)
        absolute_path = os.path.abspath(join_path)

        if os.path.isfile(absolute_path):
            # 如果是文件，则添加到文件列表中
            files.append(absolute_path)
        else:
            # 如果是文件夹，则判断是否需要递归获取子文件中的内容
            if recursion:
                sub_dir_files = get_dir_files_list(absolute_path, True)
                files += sub_dir_files

    return files


def get_new_by_compare_lists(processed_list, all_list):
    """
    功能：找出 all_list 中在 processed_list 中没有出现过的元素
    """
    new_list = []

    for i in all_list:
        # 注意：将文件路径中的反斜杠替换为正斜杠
        i = i.replace('\\', '/')
        if i not in processed_list:
            new_list.append(i)

    return new_list
```

2）在 test 包目录下，创建 `test_file_util.py` 文件，添加单元测试代码：

```python
"""
文件工具方法单元测试类
"""
from unittest import TestCase
from util import file_util
import os


class TestFileUtil(TestCase):
    """文件工具方法单元测试类"""
    def setUp(self) -> None:
        self.project_root_path = os.path.dirname(os.getcwd())

    def test_get_dir_files_list(self):
        """
        请在工程根目录的test文件夹内建立：
        test_dir/
            inner1/
                3
                4
                inner2/
                    5
            1
            2
        使用 test_dir 目录进行文件工具方法的测试
        递归结果应该为：['1', '2', '3', '4', '5']
        不递归结果应该为：['1', '2']
        """
        test_path = os.path.join(self.project_root_path, 'test\\test_dir')
        # 测试允许递归
        result = file_util.get_dir_files_list(test_path, True)
        names = []
        for i in result:
            names.append(os.path.basename(i))
        # 将列表元素升序排列，确保和结果对上
        names.sort()
        print(names)
        self.assertEqual(['1', '2', '3', '4', '5'], names)

    def test_get_new_by_compare_lists(self):
        a_list = ['a.txt', 'b.txt', 'c.txt']
        b_list = ['a.txt', 'b.txt', 'c.txt', 'd.txt', 'e.txt']
        new_list = file_util.get_new_by_compare_lists(a_list, b_list)
        self.assertEqual(['d.txt', 'e.txt'], new_list)
```

注意执行单元测试代码时，先在 test 目录下创建如下的测试目录结构：

![img](E:\黑马培训\Python额外补充\assets\9.png)

**数据库工具方法的开发**

1）在 util 包目录下，创建 `mysql_util.py` 文件，实现代码：

```python
"""
MySQL数据库操作工具方法
"""
import pymysql
from util.logging_util import init_logger
from config import project_config as conf

# 创建日志器
logger = init_logger()


"""
数据库操作工具模块
"""
import pymysql
from util.logging_util import init_logger

# 创建一个日志器对象
logger = init_logger()


class MySQLUtil(object):
    """
    数据库操作工具类
    * 创建数据库连接
    * 切换数据库
    * 执行非查询SQL
    * 执行查询SQL
    * 开启事务
    * 提交事务
    * 回滚事务
    * 检查表是否存在
    * 检查表是否存在，不存在则创建
    * 执行一条插入SQL
    * 关闭数据库连接
    """
    def __init__(self, host, user, password, port=3306, charset='utf8', autocommit=False):
        """类的实例对象初始化方法"""
        # 创建一个数据库的连接对象
        self.conn = pymysql.connect(
            host=host, # 数据库服务器的IP
            port=port, # 数据库服务器的端口
            user=user, # 数据库用户
            password=password, # 数据库用户密码,
            charset=charset, # 通信编码
            autocommit=autocommit # 是否自动开启事务提交
        )
        logger.info(f'构建到{host}:{port}数据库的连接成功...')

    def execute(self, sql):
        """执行非查询SQL语句"""
        cursor = self.conn.cursor()
        cursor.execute(sql)

        if not self.conn.get_autocommit():
            # 如果自动提交设置为False，则手动提交
            self.conn.commit()

        cursor.close()
        logger.info(f'非查询SQL语句：{sql}执行完成.')

    def query(self, sql):
        """执行查询SQL语句"""
        cursor = self.conn.cursor()
        cursor.execute(sql)
        result = cursor.fetchall()
        cursor.close()

        logger.info(f'查询SQL语句：{sql}执行完成，查询结果有：{len(result)}条')
        return result

    def select_db(self, db_name):
        """切换数据库"""
        self.conn.select_db(db_name)
        logger.info(f'切换数据库：{db_name}...')

    def query_all(self, db_name, table_name, limit=None):
        """查询指定表的数据"""
        self.select_db(db_name)

        sql = f'select * from {table_name}'
        if limit:
            sql += f' LIMIT {limit}'

        return self.query(sql)

    def begin_transaction(self):
        """开启事务"""
        if self.conn.get_autocommit():
            logger.warning('开启事务冲突，当前连接开启了事务自动提交...')
            logger.warning('关闭当前连接的事务自动提交，用于手动开启事务...')
            # 关闭自动事务提交
            self.conn.autocommit(False)

        # 开启事务
        self.conn.begin()
        logger.info('开启MySQL事务...')

    def commit_transaction(self):
        """提交事务"""
        self.conn.commit()
        logger.info('提交MySQL事务...')

    def rollback_transaction(self):
        self.conn.rollback()
        logger.info('回滚MySQL事务...')

    def check_table_exists(self, db_name, table_name):
        """判断数据表是否存在"""
        self.select_db(db_name)
        result = self.query('show tables')
        return (table_name, ) in result

    def check_table_exists_and_create(self, db_name, table_name, create_cols):
        """判断数据表是否存在，不存在则创建数据表"""
        if not self.check_table_exists(db_name, table_name):
            # 不存在则创建数据表
            self.select_db(db_name)
            create_sql = f'CREATE TABLE {table_name} ({create_cols})'
            self.execute(create_sql)
            logger.info(f'在{db_name}数据库中创建{table_name}表完成.')
        else:
            logger.info(f'在{db_name}数据库中已存在{table_name}表，跳过表创建.')

    def insert_single_sql(self, sql):
        """执行一条插入SQL"""
        try:
            self.execute(sql)
        except Exception as e:
            logger.info(f'执行插入SQL：{sql}出现异常...')
            raise e

        logger.debug(f'执行一次INSERT语句完成，SQL为：{sql}')

    def close(self):
        """关闭数据库连接"""
        if self.conn:
            self.conn.close()

        logger.info(f'关闭到{self.conn.host}:{self.conn.port}的数据库连接...')


def get_mysql_util(host, user, password, port=3306, charset='utf8', autocommit=False):
    """创建数据库工具对象"""
    return MySQLUtil(host, user, password)
```

2）编辑 config 包目录下的 `project_config` 文件，添加元数据库相关配置：

```python
# ################## --元数据库metadata配置项-- ###################
# 元数据库配置
metadata_host = 'localhost'
metadata_port = 3306
metadata_user = 'root'
metadata_password = 'mysql'
metadata_db = 'metadata'

# ################## --订单JSON数据采集配置项-- ###################
# 采集订单JSON数据，元数据表配置项
file_monitor_meta_table_name = "json_file_monitor"
file_monitor_meta_table_create_cols = \
    "id INT PRIMARY KEY AUTO_INCREMENT, " \
    "file_name VARCHAR(255) NOT NULL COMMENT '处理文件名称', " \
    "process_lines INT NULL COMMENT '文件处理行数', " \
    "process_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '文件处理时间'"
```

3）编辑 util 包目录下的 `mysql_util.py` 文件，添加获取已处理订单JSON文件列表函数

```python
def get_processed_files(db_util: MySQLUtil, db_name, table_name, create_cols):
    """
    获取已被处理的订单JSON文件列表
    如果元数据记录表 json_file_monitor 不存在，则创建它
    """
    # 创建订单JSON已处理文件记录元数据表（有则忽略）
    db_util.check_table_exists_and_create(
        db_name,
        table_name,
        create_cols
    )
    # 从元数据库中，获取已处理的订单JSON文件列表
    result = db_util.query_all(db_name, table_name)
    files = []

    for row in result:
        files.append(row[1])
    return files
```

4）在 test 包目录下创建 `test_mysql_util.py` 文件，添加MySQL工具类单元测试代码

```python
"""
MySQL工具单元测试类
"""
from unittest import TestCase
from util import mysql_util
from config import project_config as conf


class MySQLUtilTest(TestCase):
    """MySQL工具单元测试类"""
    def setUp(self) -> None:
        # 创建数据库连接对象
        self.db_util = mysql_util.get_mysql_util(
            conf.metadata_host,
            conf.metadata_user,
            conf.metadata_password
        )
        # 创建测试数据库 test
        self.db_util.execute('CREATE DATABASE IF NOT EXISTS test CHARSET=utf8')
        # 切换数据库
        self.db_util.select_db('test')
        # 创建测试数据表
        self.db_util.execute('CREATE TABLE unit_test(id INT, name VARCHAR(255))')

    def tearDown(self) -> None:
        # 删除测试数据表 unit_test
        self.db_util.execute('DROP TABLE IF EXISTS unit_test')
        # 删除测试数据库 test
        self.db_util.execute('DROP DATABASE IF EXISTS test')
        # 关闭数据库连接
        self.db_util.close()

    def test_insert_query(self):
        self.db_util.insert_single_sql('INSERT INTO unit_test VALUES(1, "哈哈哈")')
        rs = self.db_util.query('SELECT * FROM unit_test')[0]
        self.assertEqual((1, '哈哈哈'), rs)

    def test_query_all(self):
        self.db_util.insert_single_sql('INSERT INTO unit_test VALUES(1, "哈哈哈")')
        rs = self.db_util.query_all('test', 'unit_test')[0]
        self.assertEqual((1, '哈哈哈'), rs)

    def test_check_table_exists_and_create(self):
        table_name = 'unit_test'
        create_cols = 'id INT, name VARCHAR(255)'
        # 删除 unit_test 表
        self.db_util.execute('DROP TABLE IF EXISTS unit_test')
        self.db_util.check_table_exists_and_create('test', table_name, create_cols)
        rs = self.db_util.check_table_exists('test', table_name)
        self.assertTrue(rs)

    def test_get_processed_files(self):
        files = mysql_util.get_processed_files(
            self.db_util,
            conf.metadata_db,
            conf.file_monitor_meta_table_name,
            conf.file_monitor_meta_table_create_cols
        )
        self.assertEqual(['D:/etl/json/x00', 'D:/etl/json/x01', 'D:/etl/json/x02'],
```

**确定待采集文件代码实现**

1）在 config 包目录下的 `project_config.py` 文件中添加配置项，指定订单JSON数据的采集目录

```python
# 待采集 JSON订单数据 目录
json_data_path = 'D:/etl/json'
```

2）在项目工程目录下创建 `json_service.py` 文件，实现获取获取被采集JSON数据文件夹内的文件列表的业务逻辑：

```python
"""
订单JSON数据ETL思路：
1. 获取被采集JSON数据文件夹内的文件列表
2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
3. 分别读取待采集的JSON文件，进行ETL操作（读取->写出CSV->写出MySQL）
4. 将本次处理的JSON文件信息，记录到元数据库表中。
"""
from util.logging_util import init_logger
from util.mysql_util import get_mysql_util
from util import mysql_util
from util import file_util
from config import project_config as conf

# 创建日志器对象
logger = init_logger()

# 1. 获取被采集JSON数据文件夹内的文件列表
logger.info('采集订单JSON数据程序启动...')
# 创建mysql工具类对象
db_util = get_mysql_util(
    conf.metadata_host,
    conf.metadata_user,
    conf.metadata_password
)

# 获取待采集目录下的文件列表
json_files = file_util.get_dir_files_list(conf.json_data_root_path)

# 2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
# 获取元数据库表中已处理的文件列表
processed_files = mysql_util.get_processed_files(db_util, 
                                                 conf.metadata_db, 
                                                 conf.file_monitor_meta_table_name, 
                                                 conf.file_monitor_meta_table_create_cols)
# 对比确定待采集的文件列表
need_process_files = file_util.get_new_by_compare_lists(processed_files, json_files)
logger.info(f"经过对比元数据，发现有：{need_process_files} 这些新文件，即将采集这些文件的数据.")
```

3）修改 util 包目录下的 `logging_util.py` 文件中的 `init_logger` 方法，解决日志重复记录的问题

```python
# 定义一个函数，通过这个函数可以返回所需的 logger 对象
def init_logger():
    """日志器对象创建方法"""
    # 创建一个日志器对象
    logger = Logging().logger

    # 设置日志信息输出到指定文件中
    file_handler = logging.FileHandler(filename=conf.log_root_path + conf.log_name,
                                       mode='a',
                                       encoding='utf8')

    # 设置日志信息输出的格式
    fmt = logging.Formatter(
        '%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')

    # 将格式设置到文件的 handler 中
    file_handler.setFormatter(fmt)

    # 将文件输出的 handler 设置给 logger 对象
    if not logger.handlers:
        logger.addHandler(file_handler)

    # 返回日志器对象
    return logger
```

**日志结果**：

![img](E:\黑马培训\Python额外补充\assets\10.png)

### 6. 构建数据模型

**订单数据模型思考**

**思考：被采集的订单JSON数据有这么多字段，用什么对象来接收它？字典？元组？**

- 元组：不合适，字段太多了，依靠下标索引来确定字段，很容易出错
- 字典：基本合适，不过字段过多，不太方便
- 模型（Model、类）：合适，一次封装多次使用，写起来略麻烦，用起来非常舒服

**订单JSON数据模型解析**：

一条JSON数据包含了一个订单信息。

```json
{
    订单ID: xxx
    订单时间: xxx
    订单金额: xxx
    ...
    ...
    ...
    ...
    ...
    本订单商品信息: [
        {
            商品1信息
            ...
            ...
        }, 
        {
            商品2信息
            ...
            ...
        }, 
        {
            商品N信息    
        }
    ]
}
```

如上伪JSON数据可知，一条订单数据包含多个商品售卖的信息。可以得知，一个订单和一个订单内卖出的商品信息，是 `一对多`的关系。

我们在写出的时候将两者拆分出来，形成两类数据集分别写出CSV和MySQL。

- **订单信息表：每条记录只包含一个订单的时间、金额、店铺等除商品以外的字段**。

| 订单ID | 时间                | 金额 | 其它字段 |
| ------ | ------------------- | ---- | -------- |
| 123    | 2022-01-01 10:00:00 | 11   | ...      |
| 456    | 2022-01-01 10:00:01 | 33   | ...      |

- **订单商品详情表：每条记录只包含订单ID和本订单的某一条商品信息**。

| 订单ID | 商品ID | 金额 | 数量 | 其它字段 |
| ------ | ------ | ---- | ---- | -------- |
| 123    | 999    | 11   | 1    | ...      |
| 123    | 666    | 33   | 3    | ...      |
| 456    | 999    | 11   | 1    |          |
| 456    | 666    | 33   | 1    | ...      |

  所以，最终我们构建模型，本质上是构建2个模型：

- 订单信息模型

- 订单商品详情模型

  一条JSON数据，可以构建出**一个**订单信息模型，及构建出**多个**订单商品详情模型。

**准备工具方法**

**字符串处理工具方法**

1）在 util 包目录内，创建一个 `str_util.py` 文件，用于开发字符串处理的工具方法：

```python
"""
字符串工具方法
"""


def check_null(data):
    """
    功能：检查传入的字符串是否为空、None或其他无意义的内容，True表示空，False表示非空
    """
    if not data:
        return True

    # 转小写
    data = data.lower()
    if data in ('null', 'none', '', 'undefined'):
        return True

    return False


def check_null_and_transform(data):
    """
    功能：检查字符串，如果是空内容，返回空字符串，同时兼具去除字符串两边空字符的功能
    """
    if check_null(str(data)):
        return ''
    elif isinstance(data, str):
        return data.strip()
    else:
        return data


def check_str_null_and_transform_to_sql_null(data):
    """
    功能：检查传入字符串
        如果是空内容，返回'null'字符串，用于sql插入
        否则返回数据本身，并带上""包裹用于sql插入
    """
    if check_null(str(data)):
        return 'null'
    else:
        return f"'{data}'"


def check_number_null_and_transform_to_sql_null(data):
    """
    功能：检查传入的数字或字符串数据是否是空
    如果是空内容，则返回'null'字符串
    否则返回数据本身
    """
    if not data or check_null(str(data)):
        return 'null'
    else:
        return data


def clear_str(data):
    """
    功能：处理字符串中异常字符，如 单引号,双引号,逗号,分号等
    Note：这个API有可能破坏数据本身的内容,慎用
    """
    if check_null(data):
        # 如果是无意义内容,直接返回不处理了.
        return data

    data = data.replace("'", "")
    data = data.replace("\"", "")
    data = data.replace(";", "")
    data = data.replace(",", "")
    data = data.replace("@", "")
    data = data.replace("\\", "")

    return data
```

2）在 test 包目录内创建 `test_str_util.py` 文件，添加单元测试代码：

```python
"""
字符串工具单元测试类
"""
from unittest import TestCase
from util import str_util


class TestStrUtil(TestCase):
    """字符串工具单元测试类"""
    def setUp(self) -> None:
        pass

    def test_check_null(self):
        data = None
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = ''
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = 'None'
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = 'NONE'
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = 'null'
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = 'NULL'
        result = str_util.check_null(data)
        self.assertEqual(True, result)

        data = 'aaa'
        result = str_util.check_null(data)
        self.assertEqual(False, result)

    def test_check_str_null_and_transform_to_sql_null(self):
        data = None
        result = str_util.check_number_null_and_transform_to_sql_null(data)
        self.assertEqual('null', result)

        data = 'None'
        result = str_util.check_number_null_and_transform_to_sql_null(data)
        self.assertEqual('null', result)

        data = '哈哈'
        result = str_util.check_str_null_and_transform_to_sql_null(data)
        self.assertEqual("'哈哈'", result)
```

**时间戳处理功能方法**

1）在 util 包目录中，创建一个 `time_util.py` 文件，用于开发日期时间处理的工具方法：

```python
"""
时间处理工具方法
"""
import time


def ts13_to_ts10(ts):
    """将13位的时间戳规范成10位的时间戳"""
    return ts // 1000


def ts10_to_date_str(ts, format_str='%Y-%m-%d %H:%M:%S'):
    """将10位的时间戳转换为日期字符串"""
    time_array = time.localtime(ts)
    return time.strftime(format_str, time_array)


def ts13_to_date_str(ts, format_str='%Y-%m-%d %H:%M:%S'):
    """将13位时间戳转换为日期字符串"""
    ts = ts13_to_ts10(ts)
    return ts10_to_date_str(ts, format_str)


def date_str_to_ts(date_str):
    """将日期字符串转换为时间戳，要求格式是标准格式，如2022-02-02 22:22:22"""
    time_array = time.strptime(date_str, '%Y-%m-%d %H:%M:%S')
    return int(time.mktime(time_array))


def compare_str_time(left, right):
    """
    比较两个日期字符串大小，left大返回True，否则返回False
    """
    left_ts = date_str_to_ts(left)
    right_ts = date_str_to_ts(right)

    if left_ts > right_ts:
        return True
    else:
        return False
```

2）在 test 包目录内创建 `test_time_util.py` 文件，添加单元测试代码：

```python
"""
时间处理工具单元测试类
"""
from unittest import TestCase
from util import time_util


class TestTimeUtil(TestCase):
    """时间处理工具单元测试类"""
    def setUp(self) -> None:
        pass

    def test_ts13_to_ts10(self):
        ts = 1645539742000
        result = time_util.ts13_to_ts10(ts)
        self.assertEqual(1645539742, result)

    def test_ts13_to_date_str(self):
        ts = 1645539742000
        s = time_util.ts13_to_date_str(ts)
        self.assertEqual("2022-02-22 22:22:22", s)

    def test_ts10_to_date_str(self):
        ts = 1645539742
        s = time_util.ts10_to_date_str(ts)
        self.assertEqual("2022-02-22 22:22:22", s)

    def test_compare_str_time(self):
        left = "2022-02-02 22:22:22"
        right = "2000-01-01 00:00:00"
        result = time_util.compare_str_time(left, right)
        self.assertEqual(True, result)

        left = "2022-02-02 22:22:22"
        right = "2022-02-02 22:22:22"
        result = time_util.compare_str_time(left, right)
        self.assertEqual(False, result)

        left = "2022-02-02 22:22:22"
        right = "2022-02-03 22:22:22"
        result = time_util.compare_str_time(left, right)
        self.assertEqual(False, result)
```

**数据模型构建**

**新增配置文件信息**

由于JSON数据处理后，涉及到写出MySQL和CSV，我们在配置文件中，添加写出到MySQL和CSV的相关配置。

在 config 包的 `project_config.py` 文件中，新增以下配置内容：

```python
# ################## --订单JSON数据采集配置项-- ###################
# 结果写出MySQL的数据库相关配置
target_host = 'localhost'
target_port = 3306
target_user = 'root'
target_password = 'mysql'
target_data_db = "retail"
# 订单表名称
target_orders_table_name = "orders"
# 订单表MySQL数据库建表语句列信息
target_orders_table_create_cols = \
    f"order_id VARCHAR(255) PRIMARY KEY, " \
    f"store_id INT COMMENT '店铺ID', " \
    f"store_name VARCHAR(30) COMMENT '店铺名称', " \
    f"store_status VARCHAR(10) COMMENT '店铺状态(open,close)', " \
    f"store_own_user_id INT COMMENT '店主id', " \
    f"store_own_user_name VARCHAR(50) COMMENT '店主名称', " \
    f"store_own_user_tel VARCHAR(15) COMMENT '店主手机号', " \
    f"store_category VARCHAR(10) COMMENT '店铺类型(normal,test)', " \
    f"store_address VARCHAR(255) COMMENT '店铺地址', " \
    f"store_shop_no VARCHAR(255) COMMENT '店铺第三方支付id号', " \
    f"store_province VARCHAR(10) COMMENT '店铺所在省', " \
    f"store_city VARCHAR(10) COMMENT '店铺所在市', " \
    f"store_district VARCHAR(10) COMMENT '店铺所在行政区', " \
    f"store_gps_name VARCHAR(255) COMMENT '店铺gps名称', " \
    f"store_gps_address VARCHAR(255) COMMENT '店铺gps地址', " \
    f"store_gps_longitude VARCHAR(255) COMMENT '店铺gps经度', " \
    f"store_gps_latitude VARCHAR(255) COMMENT '店铺gps纬度', " \
    f"is_signed TINYINT COMMENT '是否第三方支付签约(0,1)', " \
    f"operator VARCHAR(10) COMMENT '操作员', " \
    f"operator_name VARCHAR(50) COMMENT '操作员名称', " \
    f"face_id VARCHAR(255) COMMENT '顾客面部识别ID', " \
    f"member_id VARCHAR(255) COMMENT '顾客会员ID', " \
    f"store_create_date_ts TIMESTAMP COMMENT '店铺创建时间', " \
    f"origin VARCHAR(255) COMMENT '原始信息(无用)', " \
    f"day_order_seq INT COMMENT '本订单是当日第几单', " \
    f"discount_rate DECIMAL(10, 5) COMMENT '折扣率', " \
    f"discount_type TINYINT COMMENT '折扣类型', " \
    f"discount DECIMAL(10, 5) COMMENT '折扣金额', " \
    f"money_before_whole_discount DECIMAL(10, 5) COMMENT '折扣前总金额', " \
    f"receivable DECIMAL(10, 5) COMMENT '应收金额', " \
    f"erase DECIMAL(10, 5) COMMENT '抹零金额', " \
    f"small_change DECIMAL(10, 5) COMMENT '找零金额', " \
    f"total_no_discount DECIMAL(10, 5) COMMENT '总价格(无折扣)', " \
    f"pay_total DECIMAL(10, 5) COMMENT '付款金额', " \
    f"pay_type VARCHAR(10) COMMENT '付款类型', " \
    f"payment_channel TINYINT COMMENT '付款通道', " \
    f"payment_scenarios VARCHAR(15) COMMENT '付款描述(无用)', " \
    f"product_count INT COMMENT '本单卖出多少商品', " \
    f"date_ts TIMESTAMP COMMENT '订单时间', " \
    f"INDEX (receivable), INDEX (date_ts)"

# 订单详情表名称
target_orders_detail_table_name = "orders_detail"
# 订单详情建表列信息
target_orders_detail_table_create_cols = \
    f"order_id VARCHAR(255) COMMENT '订单ID', " \
    f"barcode VARCHAR(255) COMMENT '商品条码', " \
    f"name VARCHAR(255) COMMENT '商品名称', " \
    f"count INT COMMENT '本单此商品卖出数量', " \
    f"price_per DECIMAL(10, 5) COMMENT '实际售卖单价', " \
    f"retail_price DECIMAL(10, 5) COMMENT '零售建议价', " \
    f"trade_price DECIMAL(10, 5) COMMENT '贸易价格(进货价)', " \
    f"category_id INT COMMENT '商品类别ID', " \
    f"unit_id INT COMMENT '商品单位ID(包、袋、箱、等)', " \
    f"PRIMARY KEY (order_id, barcode)"

# 订单数据写出 csv 的根路径
retail_output_csv_root_path = "D:/etl/output/csv"
# 每一次运行，订单文件写出路径
retail_orders_output_csv_file_name = \
    f"orders-{time.strftime('%Y-%m-%d-%H_%M', time.localtime())}.csv.tmp"
retail_orders_detail_output_csv_file_name = \
    f"orders-detail-{time.strftime('%Y-%m-%d-%H_%M', time.localtime())}.csv.tmp"
```

**构建订单 JSON 数据模型类**

每条订单 JSON 数据，里面包含了一条订单的信息以及本订单所售卖的商品信息。

针对订单 JSON 数据我们设计2个模型：

- 订单数据模型（除商品信息外的信息），起名叫做：`OrdersModel`
- 订单详情数据模型（仅包含商品信息），起名叫做：`OrdersDetailModel`

1）在 model 包目录下，创建一个 `reatail_orders_model.py` 文件

```python
"""
订单业务数据模型：
1）订单数据模型：OrdersModel
2）单个售卖商品数据模型：SingleProductSoldModel
3）订单详情数据模型：OrdersDetailModel
4）原始数据模型（订单数据+订单详情数据组合模型）：RetailOriginJsonModel
"""
import json
from config import project_config as conf
from util import str_util, time_util


class OrdersModel(object):
    """订单数据模型"""
    def __init__(self, data):
        """
        利用传入的订单json数据构建订单数据模型对象
        """
        # 将 json 数据转换为字典
        data = json.loads(data)
        # 初始化订单数据模型对象
        self.discount_rate = data['discountRate']  # 折扣率
        self.store_shop_no = data['storeShopNo']  # 店铺店号（无用列）
        self.day_order_seq = data['dayOrderSeq']  # 本单为当日第几单
        self.store_district = data['storeDistrict']  # 店铺所在行政区
        self.is_signed = data['isSigned']  # 是否签约店铺（签约第三方支付体系）
        self.store_province = data['storeProvince']  # 店铺所在省份
        self.origin = data['origin']  # 原始信息（无用）
        self.store_gps_longitude = data['storeGPSLongitude']  # 店铺GPS经度
        self.discount = data['discount']  # 折扣金额
        self.store_id = data['storeID']  # 店铺ID
        self.product_count = data['productCount']  # 本单售卖商品数量
        self.operator_name = data['operatorName']  # 操作员姓名
        self.operator = data['operator']  # 操作员ID
        self.store_status = data['storeStatus']  # 店铺状态
        self.store_own_user_tel = data['storeOwnUserTel']  # 店铺店主电话
        self.pay_type = data['payType']  # 支付类型
        self.discount_type = data['discountType']  # 折扣类型
        self.store_name = data['storeName']  # 店铺名称
        self.store_own_user_name = data['storeOwnUserName']  # 店铺店主名称
        self.date_ts = data['dateTS']  # 订单时间
        self.small_change = data['smallChange']  # 找零金额
        self.store_gps_name = data['storeGPSName']  # 店铺GPS名称
        self.erase = data['erase']  # 是否抹零
        self.store_gps_address = data['storeGPSAddress']  # 店铺GPS地址
        self.order_id = data['orderID']  # 订单ID
        self.money_before_whole_discount = data['moneyBeforeWholeDiscount']  # 折扣前金额
        self.store_category = data['storeCategory']  # 店铺类别
        self.receivable = data['receivable']  # 应收金额
        self.face_id = data['faceID']  # 面部识别ID
        self.store_own_user_id = data['storeOwnUserId']  # 店铺店主ID
        self.payment_channel = data['paymentChannel']  # 付款通道
        self.payment_scenarios = data['paymentScenarios']  # 付款情况（无用）
        self.store_address = data['storeAddress']  # 店铺地址
        self.total_no_discount = data['totalNoDiscount']  # 整体价格（无折扣）
        self.payed_total = data['payedTotal']  # 已付款金额
        self.store_gps_latitude = data['storeGPSLatitude']  # 店铺GPS纬度
        self.store_create_date_ts = data['storeCreateDateTS']  # 店铺创建时间
        self.store_city = data['storeCity']  # 店铺所在城市
        self.member_id = data['memberID'] # 会员ID

    def generate_insert_sql(self):
        """
        生成添加表数据的SQL语句
        """
        sql = f"INSERT IGNORE INTO {conf.target_orders_table_name}(" \
              f"order_id,store_id,store_name,store_status,store_own_user_id," \
              f"store_own_user_name,store_own_user_tel,store_category," \
              f"store_address,store_shop_no,store_province,store_city," \
              f"store_district,store_gps_name,store_gps_address," \
              f"store_gps_longitude,store_gps_latitude,is_signed," \
              f"operator,operator_name,face_id,member_id,store_create_date_ts," \
              f"origin,day_order_seq,discount_rate,discount_type,discount," \
              f"money_before_whole_discount,receivable,erase,small_change," \
              f"total_no_discount,pay_total,pay_type,payment_channel," \
              f"payment_scenarios,product_count,date_ts" \
              f") VALUES(" \
              f"'{self.order_id}', " \
              f"{self.store_id}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_name)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_status)}, " \
              f"{self.store_own_user_id}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_own_user_name)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_own_user_tel)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_category)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_address)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_shop_no)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_province)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_city)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_district)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_gps_name)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_gps_address)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_gps_longitude)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.store_gps_latitude)}, " \
              f"{self.is_signed}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.operator)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.operator_name)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.face_id)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.member_id)}, " \
              f"'{time_util.ts13_to_date_str(self.store_create_date_ts)}', " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.origin)}, " \
              f"{self.day_order_seq}, " \
              f"{self.discount_rate}, " \
              f"{self.discount_type}, " \
              f"{self.discount}, " \
              f"{self.money_before_whole_discount}, " \
              f"{self.receivable}, " \
              f"{self.erase}, " \
              f"{self.small_change}, " \
              f"{self.total_no_discount}, " \
              f"{self.payed_total}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.pay_type)}, " \
              f"{self.payment_channel}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.payment_scenarios)}, " \
              f"{self.product_count}, " \
              f"'{time_util.ts13_to_date_str(self.date_ts)}')"

        return sql

    @staticmethod
    def get_csv_header(sep=','):
        """
        生成 csv 数据的标头内容
        """
        header = f"order_id{sep}" \
                 f"store_id{sep}" \
                 f"store_name{sep}" \
                 f"store_status{sep}" \
                 f"store_own_user_id{sep}" \
                 f"store_own_user_name{sep}" \
                 f"store_own_user_tel{sep}" \
                 f"store_category{sep}" \
                 f"store_address{sep}" \
                 f"store_shop_no{sep}" \
                 f"store_province{sep}" \
                 f"store_city{sep}" \
                 f"store_district{sep}" \
                 f"store_gps_name{sep}" \
                 f"store_gps_address{sep}" \
                 f"store_gps_longitude{sep}" \
                 f"store_gps_latitude{sep}" \
                 f"is_signed{sep}" \
                 f"operator{sep}" \
                 f"operator_name{sep}" \
                 f"face_id{sep}" \
                 f"member_id{sep}" \
                 f"store_create_date_ts{sep}" \
                 f"origin{sep}" \
                 f"day_order_seq{sep}" \
                 f"discount_rate{sep}" \
                 f"discount_type{sep}" \
                 f"discount{sep}" \
                 f"money_before_whole_discount{sep}" \
                 f"receivable{sep}" \
                 f"erase{sep}" \
                 f"small_change{sep}" \
                 f"total_no_discount{sep}" \
                 f"pay_total{sep}" \
                 f"pay_type{sep}" \
                 f"payment_channel{sep}" \
                 f"payment_scenarios{sep}" \
                 f"product_count{sep}" \
                 f"date_ts\n"

        return header

    def check_and_transform_area(self):
        """
        检查省市区内容，为空就转换为未知
        """
        if str_util.check_null(self.store_province):
            self.store_province = '未知省份'
        if str_util.check_null(self.store_city):
            self.store_city = '未知城市'
        if str_util.check_null(self.store_district):
            self.store_district = '未知行政区'

    def check_and_transform_all_column(self):
        """
        转换全部的列，如果是空内容，就将其设置为空字符串
        """
        self.discount_rate = str_util.check_null_and_transform(self.discount_rate)
        self.store_shop_no = str_util.check_null_and_transform(self.store_shop_no)
        self.day_order_seq = str_util.check_null_and_transform(self.day_order_seq)
        self.store_district = str_util.check_null_and_transform(self.store_district)
        self.is_signed = str_util.check_null_and_transform(self.is_signed)
        self.store_province = str_util.check_null_and_transform(self.store_province)
        self.origin = str_util.check_null_and_transform(self.origin)
        self.store_gps_longitude = str_util.check_null_and_transform(self.store_gps_longitude)
        self.discount = str_util.check_null_and_transform(self.discount)
        self.store_id = str_util.check_null_and_transform(self.store_id)
        self.product_count = str_util.check_null_and_transform(self.product_count)
        self.operator_name = str_util.check_null_and_transform(self.operator_name)
        self.operator = str_util.check_null_and_transform(self.operator)
        self.store_status = str_util.check_null_and_transform(self.store_status)
        self.store_own_user_tel = str_util.check_null_and_transform(self.store_own_user_tel)
        self.pay_type = str_util.check_null_and_transform(self.pay_type)
        self.discount_type = str_util.check_null_and_transform(self.discount_type)
        self.store_name = str_util.check_null_and_transform(self.store_name)
        self.store_own_user_name = str_util.check_null_and_transform(self.store_own_user_name)
        self.date_ts = str_util.check_null_and_transform(self.date_ts)
        self.small_change = str_util.check_null_and_transform(self.small_change)
        self.store_gps_name = str_util.check_null_and_transform(self.store_gps_name)
        self.erase = str_util.check_null_and_transform(self.erase)
        self.store_gps_address = str_util.check_null_and_transform(self.store_gps_address)
        self.order_id = str_util.check_null_and_transform(self.order_id)
        self.money_before_whole_discount = str_util.check_null_and_transform(self.money_before_whole_discount)
        self.store_category = str_util.check_null_and_transform(self.store_category)
        self.receivable = str_util.check_null_and_transform(self.receivable)
        self.face_id = str_util.check_null_and_transform(self.face_id)
        self.store_own_user_id = str_util.check_null_and_transform(self.store_own_user_id)
        self.payment_channel = str_util.check_null_and_transform(self.payment_channel)
        self.payment_scenarios = str_util.check_null_and_transform(self.payment_scenarios)
        self.store_address = str_util.check_null_and_transform(self.store_address)
        self.total_no_discount = str_util.check_null_and_transform(self.total_no_discount)
        self.payed_total = str_util.check_null_and_transform(self.payed_total)
        self.store_gps_latitude = str_util.check_null_and_transform(self.store_gps_latitude)
        self.store_create_date_ts = str_util.check_null_and_transform(self.store_create_date_ts)
        self.store_city = str_util.check_null_and_transform(self.store_city)
        self.member_id = str_util.check_null_and_transform(self.member_id)

    def to_csv(self, sep=','):
        """
        生成 csv 数据，分割符默认为逗号。
        Note: 生成的数据顺序和header是一一对应的，不要混乱了。
        """
        self.check_and_transform_area()
        self.check_and_transform_all_column()

        csv_line = \
            f"{self.order_id}{sep}" \
            f"{self.store_id}{sep}" \
            f"{self.store_name}{sep}" \
            f"{self.store_status}{sep}" \
            f"{self.store_own_user_id}{sep}" \
            f"{self.store_own_user_name}{sep}" \
            f"{self.store_own_user_tel}{sep}" \
            f"{self.store_category}{sep}" \
            f"{self.store_address}{sep}" \
            f"{self.store_shop_no}{sep}" \
            f"{self.store_province}{sep}" \
            f"{self.store_city}{sep}" \
            f"{self.store_district}{sep}" \
            f"{self.store_gps_name}{sep}" \
            f"{self.store_gps_address}{sep}" \
            f"{self.store_gps_longitude}{sep}" \
            f"{self.store_gps_latitude}{sep}" \
            f"{self.is_signed}{sep}" \
            f"{self.operator}{sep}" \
            f"{self.operator_name}{sep}" \
            f"{self.face_id}{sep}" \
            f"{self.member_id}{sep}" \
            f"{time_util.ts13_to_date_str(self.store_create_date_ts)}{sep}" \
            f"{self.origin}{sep}" \
            f"{self.day_order_seq}{sep}" \
            f"{self.discount_rate}{sep}" \
            f"{self.discount_type}{sep}" \
            f"{self.discount}{sep}" \
            f"{self.money_before_whole_discount}{sep}" \
            f"{self.receivable}{sep}" \
            f"{self.erase}{sep}" \
            f"{self.small_change}{sep}" \
            f"{self.total_no_discount}{sep}" \
            f"{self.payed_total}{sep}" \
            f"{self.pay_type}{sep}" \
            f"{self.payment_channel}{sep}" \
            f"{self.payment_scenarios}{sep}" \
            f"{self.product_count}{sep}" \
            f"{time_util.ts13_to_date_str(self.date_ts)}\n"

        return csv_line

    def to_string(self):
        """
        转换为：属性名: 属性值, 属性名: 属性值......的格式字符串返回
        """
        return f"discount_rate: {self.discount_rate}, " \
               f"store_shop_no: {self.store_shop_no}, " \
               f"day_order_seq: {self.day_order_seq}, " \
               f"store_district: {self.store_district}, " \
               f"is_signed: {self.is_signed}, " \
               f"store_province: {self.store_province}, " \
               f"origin: {self.origin}, " \
               f"store_gps_longitude: {self.store_gps_longitude}, " \
               f"discount: {self.discount}, " \
               f"store_id: {self.store_id}, " \
               f"product_count: {self.product_count}, " \
               f"operator_name: {self.operator_name}, " \
               f"operator: {self.operator}, " \
               f"store_status: {self.store_status}, " \
               f"store_own_user_tel: {self.store_own_user_tel}, " \
               f"pay_type: {self.pay_type}, " \
               f"discount_type: {self.discount_type}, " \
               f"store_name: {self.store_name}, " \
               f"store_own_user_name: {self.store_own_user_name}, " \
               f"date_ts: {self.date_ts}, " \
               f"small_change: {self.small_change}, " \
               f"store_gps_name: {self.store_gps_name}, " \
               f"erase: {self.erase}, " \
               f"store_gps_address: {self.store_gps_address}, " \
               f"order_id: {self.order_id}, " \
               f"money_before_whole_discount: {self.money_before_whole_discount}, " \
               f"store_category: {self.store_category}, " \
               f"receivable: {self.receivable}, " \
               f"face_id: {self.face_id}, " \
               f"store_own_user_id: {self.store_own_user_id}, " \
               f"payment_channel: {self.payment_channel}, " \
               f"payment_scenarios: {self.payment_scenarios}, " \
               f"store_address: {self.store_address}, " \
               f"total_no_discount: {self.total_no_discount}, " \
               f"payed_total: {self.payed_total}, " \
               f"store_gps_latitude: {self.store_gps_latitude}, " \
               f"store_create_date_ts: {self.store_create_date_ts}, " \
               f"store_city: {self.store_city}, " \
               f"member_id: {self.member_id}"


class SingleProductSoldModel(object):
    """订单售卖商品数据模型"""
    def __init__(self, order_id, product_detail):
        self.order_id = order_id
        self.count = product_detail['count']
        self.name = product_detail['name']
        self.unit_id = product_detail['unitID']
        self.barcode = product_detail['barcode']
        self.price_per = product_detail['pricePer']
        self.retail_price = product_detail['retailPrice']
        self.trade_price = product_detail['tradePrice']
        self.category_id = product_detail['categoryID']

    def generate_value_segment_for_sql_insert(self):
        """
        生成添加表数据SQL语句的VALUE语句段
        """
        segment = f"(" \
                  f"'{self.order_id}', " \
                  f"{str_util.check_str_null_and_transform_to_sql_null(self.barcode)}, " \
                  f"{str_util.check_str_null_and_transform_to_sql_null(self.name)}, " \
                  f"{self.count}, " \
                  f"{self.price_per}, " \
                  f"{self.retail_price}, " \
                  f"{self.trade_price}, " \
                  f"{self.category_id}, " \
                  f"{self.unit_id}" \
                  f")"

        return segment

    def to_csv(self, sep=","):
        """
        生成一条csv数据，分隔符默认逗号
        """
        csv_line = \
            f"{self.order_id}{sep}" \
            f"{self.barcode}{sep}" \
            f"{self.name}{sep}" \
            f"{self.count}{sep}" \
            f"{self.price_per}{sep}" \
            f"{self.retail_price}{sep}" \
            f"{self.trade_price}{sep}" \
            f"{self.category_id}{sep}" \
            f"{self.unit_id}\n"

        return csv_line

    def to_string(self):
        """
        转换为：属性名: 属性值, 属性名: 属性值......的格式字符串返回
        """
        return f"order_id: {self.order_id}, " \
               f"count: {self.count}, " \
               f"name: {self.name}, " \
               f"unit_id: {self.unit_id}, " \
               f"barcode: {self.barcode}, " \
               f"price_per: {self.price_per}, " \
               f"retail_price: {self.retail_price}, " \
               f"trade_price: {self.trade_price}, " \
               f"category_id: {self.category_id}"


class OrdersDetailModel(object):
    """订单详情数据模型"""
    def __init__(self, data):
        """
        利用传入的订单json数据构建订单详情数据模型对象
        """
        data = json.loads(data)
        order_products_list = data['product']
        self.order_id = data['orderID']
        self.products_detail = [] # 记录当前订单卖出的商品

        for sing_product in order_products_list:
            product = SingleProductSoldModel(self.order_id, sing_product)
            self.products_detail.append(product)

    def generate_insert_sql(self):
        """
        生成添加表数据的SQL语句
        """
        sql = f"INSERT IGNORE INTO {conf.target_orders_detail_table_name}(" \
              f"order_id,barcode,name,count,price_per,retail_price,trade_price,category_id,unit_id" \
              f") VALUES"

        for single_product_sold_model in self.products_detail:
            sql += single_product_sold_model.generate_value_segment_for_sql_insert() + ", "

        # 去除最后的逗号
        sql = sql[:-2]
        return sql

    @staticmethod
    def get_csv_header(sep=','):
        """
        生成 csv 数据的标头内容
        """
        return f"order_id{sep}" \
               f"barcode{sep}" \
               f"name{sep}" \
               f"count{sep}" \
               f"price_per{sep}" \
               f"retail_price{sep}" \
               f"trade_price{sep}" \
               f"category_id{sep}" \
               f"unit_id\n"

    def to_csv(self):
        """生成添加csv的数据行"""
        csv_lines = ''

        for single_product_sold_model in self.products_detail:
            csv_lines += single_product_sold_model.to_csv()

        return csv_lines

    def to_string(self):
        """
        转换为字符串信息
        """
        s = "OrderDetailModel: ["
        for single_product in self.products_detail:
            single_product_str = single_product.to_string()
            s += "{" + single_product_str + "}, "
        s += "]"
        s = s.replace(", ]", "]")
        return s


class RetailOriginJsonModel(object):
    """
    原始订单JSON数据模型
    """
    def __init__(self, data):
        self.order_model = OrdersModel(data)
        self.order_detail_model = OrdersDetailModel(data)

    def get_order_model(self):
        return self.order_model

    def get_order_detail_model(self):
        return self.order_detail_model

    def get_order_id(self):
        return self.order_model.order_id

    def get_products_list(self):
        return self.order_detail_model.products_detail

    def to_string(self):
        s = self.order_model.to_string() + ", " + self.order_detail_model.to_string()
        s = s.replace('OrderDetailModel', 'products')
        return s
```

2）在 test 包目录下，创建一个 `test_retail_model.py` 文件，对数据模型进行单元测试：

```python
"""
数据模型单元测试类
"""
from unittest import TestCase
from model import retail_orders_model


class TestRetailModel(TestCase):
    """数据模型单元测试类"""

    def setUp(self) -> None:
        self.data = """{"discountRate": 1, 
        "storeShopNo": "None", 
        "dayOrderSeq": 37, 
        "storeDistrict": "芙蓉区", 
        "isSigned": 0, 
        "storeProvince": "湖南省", 
        "origin": 0, 
        "storeGPSLongitude": "undefined", 
        "discount": 0, 
        "storeID": 1766, 
        "productCount": 1, 
        "operatorName": "OperatorName", 
        "operator": "NameStr", 
        "storeStatus": "open", 
        "storeOwnUserTel": 12345678910, 
        "payType": "cash", 
        "discountType": 2, 
        "storeName": "亿户超市郭一一食品店", 
        "storeOwnUserName": "OwnUserNameStr", 
        "dateTS": 1542436490000, 
        "smallChange": 0, 
        "storeGPSName": "None", 
        "erase": 0, 
        "product": [{"count": 1, "name": "南京特醇", "unitID": 8, 
        "barcode": "6901028300056", "pricePer": 12, "retailPrice": 12, 
        "tradePrice": 11, "categoryID": 1}], 
        "storeGPSAddress": "None", 
        "orderID": "154243648991517662217", 
        "moneyBeforeWholeDiscount": 12, 
        "storeCategory": "normal", 
        "receivable": 12, 
        "faceID": "", 
        "storeOwnUserId": 1694, 
        "paymentChannel": 0, 
        "paymentScenarios": "OTHER", 
        "storeAddress": "StoreAddress", 
        "totalNoDiscount": 12, 
        "payedTotal": 12, 
        "storeGPSLatitude": "undefined", 
        "storeCreateDateTS": 1540793134000, 
        "storeCity": "长沙市", 
        "memberID": "0"}"""

    def test_orders_model_init(self):
        model = retail_orders_model.OrdersModel(self.data)
        self.assertIsInstance(model, retail_orders_model.OrdersModel)
        self.assertEqual(model.to_string(),
                         """discount_rate: 1, store_shop_no: None, day_order_seq: 37, store_district: 芙蓉区, is_signed: 0, store_province: 湖南省, origin: 0, store_gps_longitude: undefined, discount: 0, store_id: 1766, product_count: 1, operator_name: OperatorName, operator: NameStr, store_status: open, store_own_user_tel: 12345678910, pay_type: cash, discount_type: 2, store_name: 亿户超市郭一一食品店, store_own_user_name: OwnUserNameStr, date_ts: 1542436490000, small_change: 0, store_gps_name: None, erase: 0, store_gps_address: None, order_id: 154243648991517662217, money_before_whole_discount: 12, store_category: normal, receivable: 12, face_id: , store_own_user_id: 1694, payment_channel: 0, payment_scenarios: OTHER, store_address: StoreAddress, total_no_discount: 12, payed_total: 12, store_gps_latitude: undefined, store_create_date_ts: 1540793134000, store_city: 长沙市, member_id: 0""")

    def test_orders_detail_model_init(self):
        model = retail_orders_model.OrdersDetailModel(self.data)
        self.assertEqual(
            """OrderDetailModel: [{order_id: 154243648991517662217, count: 1, name: 南京特醇, unit_id: 8, barcode: 6901028300056, price_per: 12, retail_price: 12, trade_price: 11, category_id: 1}]""",
            model.to_string())

    def test_single_product_sold_model_init(self):
        model = retail_orders_model.OrdersDetailModel(self.data)
        single_product_sold_model: retail_orders_model.SingleProductSoldModel = model.products_detail[0]
        self.assertEqual(single_product_sold_model.to_string(),
                         """order_id: 154243648991517662217, count: 1, name: 南京特醇, unit_id: 8, barcode: 6901028300056, price_per: 12, retail_price: 12, trade_price: 11, category_id: 1""")

    def test_retail_origin_json_model_init(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        self.assertEqual(model.to_string(),
                         """discount_rate: 1, store_shop_no: None, day_order_seq: 37, store_district: 芙蓉区, is_signed: 0, store_province: 湖南省, origin: 0, store_gps_longitude: undefined, discount: 0, store_id: 1766, product_count: 1, operator_name: OperatorName, operator: NameStr, store_status: open, store_own_user_tel: 12345678910, pay_type: cash, discount_type: 2, store_name: 亿户超市郭一一食品店, store_own_user_name: OwnUserNameStr, date_ts: 1542436490000, small_change: 0, store_gps_name: None, erase: 0, store_gps_address: None, order_id: 154243648991517662217, money_before_whole_discount: 12, store_category: normal, receivable: 12, face_id: , store_own_user_id: 1694, payment_channel: 0, payment_scenarios: OTHER, store_address: StoreAddress, total_no_discount: 12, payed_total: 12, store_gps_latitude: undefined, store_create_date_ts: 1540793134000, store_city: 长沙市, member_id: 0, products: [{order_id: 154243648991517662217, count: 1, name: 南京特醇, unit_id: 8, barcode: 6901028300056, price_per: 12, retail_price: 12, trade_price: 11, category_id: 1}]""")

    def test_orders_model_transform_all_column(self):
        """测试转换全部的列，如果是空内容就替换为空字符串的功能"""
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        model.order_model.check_and_transform_all_column()
        print(model.to_string())

    def test_orders_model_generate_insert_sql(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        sql = model.order_model.generate_insert_sql()
        print(sql)
        self.maxDiff = None
        self.assertEqual(
            """INSERT IGNORE INTO orders(order_id,store_id,store_name,store_status,store_own_user_id,store_own_user_name,store_own_user_tel,store_category,store_address,store_shop_no,store_province,store_city,store_district,store_gps_name,store_gps_address,store_gps_longitude,store_gps_latitude,is_signed,operator,operator_name,face_id,member_id,store_create_date_ts,origin,day_order_seq,discount_rate,discount_type,discount,money_before_whole_discount,receivable,erase,small_change,total_no_discount,pay_total,pay_type,payment_channel,payment_scenarios,product_count,date_ts) VALUES('154243648991517662217', 1766, '亿户超市郭一一食品店', 'open', 1694, 'OwnUserNameStr', '12345678910', 'normal', 'StoreAddress', null, '湖南省', '长沙市', '芙蓉区', null, null, null, null, 0, 'NameStr', 'OperatorName', null, '0', '2018-10-29 14:05:34', '0', 37, 1, 2, 0, 12, 12, 0, 0, 12, 12, 'cash', 0, 'OTHER', 1, '2018-11-17 14:34:50')""",
            sql)

    def test_single_product_sold_model_generate_insert_segment_for_sql(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        segment = model.order_detail_model.products_detail[0].generate_value_segment_for_sql_insert()
        self.assertEqual("""('154243648991517662217', '6901028300056', '南京特醇', 1, 12, 12, 11, 1, 8)""", segment)

    def test_orders_detail_model_generate_insert_sql(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        sql = model.order_detail_model.generate_insert_sql()
        self.assertEqual("""INSERT IGNORE INTO orders_detail(order_id,barcode,name,count,price_per,retail_price,trade_price,category_id,unit_id) VALUES('154243648991517662217', '6901028300056', '南京特醇', 1, 12, 12, 11, 1, 8)""", sql)

    def test_orders_model_to_csv_str(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        r = model.order_model.to_csv()
        self.assertEqual(
            """154243648991517662217,1766,亿户超市郭一一食品店,open,1694,OwnUserNameStr,12345678910,normal,StoreAddress,,湖南省,长沙市,芙蓉区,,,,,0,NameStr,OperatorName,,0,2018-10-29 14:05:34,0,37,1,2,0,12,12,0,0,12,12,cash,0,OTHER,1,2018-11-17 14:34:50\n""",
            r)

    def test_single_product_sold_model_to_csv(self):
        model = retail_orders_model.RetailOriginJsonModel(self.data)
        r = model.order_detail_model.products_detail[0].to_csv()
        self.assertEqual("""154243648991517662217,6901028300056,南京特醇,1,12,12,11,1,8\n""", r)
```

**核心业务逻辑**

**核心业务逻辑代码实现**

1）编辑 `file_util.py` 文件，新增如下代码：

```python
def change_output_csv_suffix(path):
    """将csv的后缀名的.tmp删除"""
    if ".tmp" == path[-4:]:
        dst = path[:-4]
        os.rename(path, dst)
```

2）编辑 `mysql_util.py` 文件，在 `MySQLUtil` 类中增加方法 `insert_single_sql_without_autocommit`，代码如下：

```python
class MySQLUtil(object):
    # ...

    def insert_single_sql_without_autocommit(self, sql):
        """执行一个插入的sql，不自动提交"""
        try:
            cursor = self.conn.cursor()
            cursor.execute(sql)
        except Exception as e:
            logger.info(f'执行插入SQL：{sql}异常...')
            # 继续向外抛出这个异常
            raise e

        logger.info(f'插入SQL：{sql}执行完成.')
```

3）编辑 `json_service.py` 文件，新增核心业务逻辑代码实现：

```python
"""
订单JSON数据ETL思路：
1. 获取被采集JSON数据文件夹内的文件列表
2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
3. 分别读取待采集的JSON文件，进行ETL操作（读取->写出CSV->写出MySQL）
4. 将本次处理的JSON文件信息，记录到元数据库表中。
"""
import os
import time

from model.retail_orders_model import RetailOriginJsonModel, OrdersModel, OrdersDetailModel
from util import file_util
# 到我们导入一个模块时，该模块中的代码会从上到下执行一遍
from util import mysql_util
from util.logging_util import init_logger
from config import project_config as conf

# 创建日志器对象
logger = init_logger()

# 1. 获取被采集JSON数据文件夹内的文件列表
logger.info('订单JSON数据采集程序启动...')
json_files = file_util.get_dir_file_list(conf.json_data_path)

# 2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
# 创建一个数据库工具对象
db_util = mysql_util.get_mysql_util(
    conf.metadata_host, # 元数据库服务的IP
    conf.metadata_user, # 元数据库的用户
    conf.metadata_password, # 元数据库的用户密码
    conf.metadata_port # 元数据库服务的端口
)

# 获取元数据库表中已经被采集的订单JSON文件的列表
processed_files = mysql_util.get_processed_files(
    db_util,
    conf.metadata_db, # 元数据库名称
    conf.file_monitor_meta_table_name, # 记录已采集订单JSON文件名称的元数据表
    conf.file_monitor_meta_table_create_cols
)

# 对比确定待采集文件列表
need_process_files = file_util.get_new_by_compare_lists(processed_files, json_files)

if need_process_files:
    logger.info(f'经过元数据的对比，确定本次需要采集的文件列表为：{need_process_files}')
else:
    logger.info(f'经过元数据的对比，暂时没有需要采集的文件，程序退出...')
    exit(0)

# 3. 分别读取待采集的JSON文件，进行ETL操作（读取->写出CSV->写出MySQL）
orders_csv_write_f = open(os.path.join(conf.retail_output_csv_root_path, conf.retail_orders_output_csv_file_name),
                          'w', encoding='utf8')
orders_detail_csv_write_f = open(os.path.join(conf.retail_output_csv_root_path, conf.retail_orders_detail_output_csv_file_name),
                                 'w', encoding='utf8')

# 写入csv文件的标头内容
orders_csv_write_f.write(OrdersModel.get_csv_header())
orders_detail_csv_write_f.write(OrdersDetailModel.get_csv_header())

# 创建目标数据库的连接
target_db_util = mysql_util.get_mysql_util(
    conf.target_host,
    conf.target_user,
    conf.target_password,
    conf.target_port
)

# 检查目录表是否存在，不存在则创建
target_db_util.check_table_exists_and_create(
    conf.target_data_db,
    conf.target_orders_table_name,
    conf.target_orders_table_create_cols
)
target_db_util.check_table_exists_and_create(
    conf.target_data_db,
    conf.target_orders_detail_table_name,
    conf.target_orders_detail_table_create_cols
)

# 遍历读取每个文件的内容，进行ETL操作
for file in need_process_files:
    # 遍历文件中的每一行数据
    origin_model_list = []

    for line in open(file, 'r', encoding='utf8'):
        # 创建订单原始数据模型对象
        origin_model = RetailOriginJsonModel(line)
        origin_model_list.append(origin_model)

    # 剔除订单金额大于 10000 的数据
    reserved_models = []
    for model in origin_model_list:
        if model.order_model.receivable <= 10000:
            model.order_model.check_and_transform_all_column()
            model.order_model.check_and_transform_area()
            reserved_models.append(model)

    # 记录数据处理的条数，每处理1000条，刷新一下文件缓存区
    json_data_processed_count = 0

    # 手动开启事务
    target_db_util.begin_transaction()

    # 遍历写入数据
    for model in reserved_models:
        order_model = model.get_order_model()
        order_detail_model = model.get_order_detail_model()

        # 向目标表中添加订单数据
        target_db_util.insert_single_sql_without_autocommit(order_model.generate_insert_sql())
        # 向目标表中添加订单详情数据
        target_db_util.insert_single_sql_without_autocommit(order_detail_model.generate_insert_sql())

        # 向目标csv文件中写入订单数据
        orders_csv_write_f.write(order_model.to_csv())
        # 向目标表csv文件中写入订单详情数据
        orders_detail_csv_write_f.write(order_detail_model.to_csv())

        # 计算器+1
        json_data_processed_count += 1

        if json_data_processed_count % 1000 == 0:
            orders_csv_write_f.flush()
            orders_detail_csv_write_f.flush()

    target_db_util.commit_transaction()

    # 记录日志信息
    logger.info(f'文件{file}处理完成，{len(reserved_models)}条数据被处理')

    # 记录元数据，将已处理的文件，写入到元数据库表中记录
    file_name = file.replace('\\', '/')
    sql = f"INSERT INTO {conf.file_monitor_meta_table_name}(file_name, process_lines) VALUES(" \
          f"'{file_name}', {len(reserved_models)})"

    db_util.insert_single_sql(sql)
    logger.info(f'文件{file}处理完成，元数据记录完成')

# 关闭文件对象
orders_csv_write_f.close()
orders_detail_csv_write_f.close()

file_util.change_output_csv_suffix(orders_csv_write_f.name)
file_util.change_output_csv_suffix(orders_detail_csv_write_f.name)

# 关闭数据库连接
db_util.close()
target_db_util.close()
```

**订单数据采集代码运行**

直接运行 `json_service.py` 文件，即可实现订单 JSON 数据的采集；

**运行要求**

请检查：

- 所需的MySQL数据库是否都存在，并启动，且可连接
- 配置文件内，相关信息是否正确配置
  - 关于MySQL数据库配置信息
  - JSON数据文件所在路径
  - 写出CSV的数据路径

确认无误后，就可以从课程资料中，找到一个json文件，放入目录内。启动程序查看运行效果了。

然后再次执行，查看是否因为这个文件被处理过，程序就不处理了。

然后再放入一个新文件，再次运行，看是否检测到这个新的未处理的文件，对其进行采集处理。

## 1.3 商品数据采集

### 1. 业务需求说明

**测试数据的添加**

我们给同学们提供了一份数据文件，在**课程资料**里面有一个`barcode条码库.sql`，里面包含了需要被采集的商品数据信息。

下面我们需要将被采集的商品数据导入到 MySQL 的数据源库中，具体操作如下：

1）通过 datagirp 操作界面，在数据源库 `source_data` 上右键，选择 `Run SQL Script`

![img](E:\黑马培训\Python额外补充\assets\1.png)

2）在弹出的操作界面，选择 `简化版-barcode条码库.sql` 文件，点击 `OK` 开始导入数据

![img](E:\黑马培训\Python额外补充\assets\2.png)

3）导入数据完成之后，`source_data` 数据库中会生成四张数据表，如果：

![img](E:\黑马培训\Python额外补充\assets\3.png)

**业务需求简介**

我们现在要做的，就是进行 MySQL 的商品数据采集，采集 `source_data` 数据库中的 `sys_barcode` 表数据，具体输出有两种要求：

1）将采集的商品数据保存到目标数据库 `retail` 中

- 注意：课程中为了学习，不是真正的环境场景，我们是用一个MySQL 全部完成这个操作。
- 如果是在真实的企业场景下，`source_data`库 和 `retail` 库，很可能不在同一个MySQL内。

2）将采集的商品数据写出到 CSV 文件中

**sys_barcode表的数据结构如下**：

```mysql
CREATE TABLE IF NOT EXISTS sys_barcode (
  `code` varchar(50) PRIMARY KEY COMMENT '商品条码',
  `name` varchar(200) DEFAULT '' COMMENT '商品名称',
  `spec` varchar(200) DEFAULT '' COMMENT '商品规格',
  `trademark` varchar(100) DEFAULT '' COMMENT '商品商标',
  `addr` varchar(200) DEFAULT '' COMMENT '商品产地',
  `units` varchar(50) DEFAULT '' COMMENT '商品单位(个、杯、箱、等)',
  `factory_name` varchar(200) DEFAULT '' COMMENT '生产厂家',
  `trade_price` DECIMAL(50, 5) DEFAULT 0.0 COMMENT '贸易价格(指导进价)',
  `retail_price` DECIMAL(50, 5) DEFAULT 0.0 COMMENT '零售价格(建议卖价)',
  `update_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `wholeunit` varchar(50) DEFAULT NULL COMMENT '大包装单位',
  `wholenum` int(11) DEFAULT NULL COMMENT '大包装内装数量',
  `img` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `src` varchar(20) DEFAULT NULL COMMENT '源信息', 
  INDEX (update_at)
) DEFAULT CHARSET=utf8;
```

**数据采集要求**

在进行商品数据采集时，我们的采集要做到**`增量更新`**采集。

假设当前被采集的表有如下数据：

| barcode | name     | price | unit |
| ------- | -------- | ----- | ---- |
| 6900001 | 娃哈哈   | 1     | 瓶   |
| 6900002 | 农夫山泉 | 2     | 瓶   |
| 6900003 | 可口可乐 | 3     | 瓶   |
| 6900004 | 雪碧     | 1     | 瓶   |

> 这4条数据需要被采集到目标表中。

当这一次采集完成后，被采集的表数据发生了更新，新增了一行以及有一条数据的商品名被修改了：

| barcode | name         | price | unit |
| ------- | ------------ | ----- | ---- |
| 6900001 | 娃哈哈       | 1     | 瓶   |
| 6900002 | 农夫山泉     | 2     | 瓶   |
| 6900003 | 可口可乐     | 3     | 瓶   |
| 6900004 | **无糖雪碧** | 1     | 瓶   |
| 6900005 | **芬达**     | 1     | 瓶   |

> 如上表，第四行name发生了更改，第五行是新增的。

我们下一次执行采集的时候，需要做到 只采集4、5两行数据，覆盖到目标表中即可。第1、2、3行数据不理会即可。

**如何实现增量数据采集？**

需要基于被采集的表中的一个字段：**`update_at`**。

![img](E:\黑马培训\Python额外补充\assets\4.png)

如图，表中这个字段意思是：

- 记录数据被插入数据库的时间（新增）
- 记录数据被修改的时间（更新）

这个字段凡是新数据以及更新的数据，这个字段都会记录当时的时间。

**思路**：

1. 查询（采集）数据的时候，SELECT SQL语句按照`update_at`进行排序，按照`升序`排序
2. 当采集完成后，将当前批次最大的时间记录在：MySQL的`元数据库中`
3. 下一次采集的时候，从MySQL的元数据库中，查询出来`上一次采集的时间`
4. SQL的SELECT语句的WHERE条件设置为： `update_at >= 上一次采集时间` 即可

### 2. 构建数据模型

**数据模型构建**

**新增配置文件信息**

由于barcode商品数据处理后，涉及到写出MySQL和CSV，我们在配置文件中，添加写出到MySQL和CSV的相关配置。

在 config 包的 `project_config.py` 文件中，新增以下配置内容：

```python
# ################## --数据库barcode商品数据采集配置项-- ###################
# 元数据库配置
# barcode业务：update_at字段的监控表的名称
metadata_barcode_table_name = 'barcode_monitor'
# barcode业务：update_at字段的监控表的建表语句的列信息
metadata_barcode_table_create_cols = "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '自增ID', " \
                                     "time_record TIMESTAMP NOT NULL COMMENT '本次采集记录的最大时间', " \
                                     "gather_line_count INT NULL COMMENT '本次采集条数'"
# 数据源库配置
source_host = 'localhost'
source_user = 'root'
source_password = 'mysql'
source_port = 3306
source_data_db = 'source_data'
# 数据源表名称
source_barcode_table_name = 'sys_barcode'
# 条码商品表名称
target_barcode_table_name = 'sys_barcode'
target_barcode_table_create_cols = """
    `code` varchar(50) PRIMARY KEY COMMENT '商品条码',
    `name` varchar(200) DEFAULT '' COMMENT '商品名称',
    `spec` varchar(200) DEFAULT '' COMMENT '商品规格',
    `trademark` varchar(100) DEFAULT '' COMMENT '商品商标',
    `addr` varchar(200) DEFAULT '' COMMENT '商品产地',
    `units` varchar(50) DEFAULT '' COMMENT '商品单位(个、杯、箱、等)',
    `factory_name` varchar(200) DEFAULT '' COMMENT '生产厂家',
    `trade_price` DECIMAL(50, 5) DEFAULT 0.0 COMMENT '贸易价格(指导进价)',
    `retail_price` DECIMAL(50, 5) DEFAULT 0.0 COMMENT '零售价格(建议卖价)',
    `update_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `wholeunit` varchar(50) DEFAULT NULL COMMENT '大包装单位',
    `wholenum` int(11) DEFAULT NULL COMMENT '大包装内装数量',
    `img` varchar(500) DEFAULT NULL COMMENT '商品图片',
    `src` varchar(20) DEFAULT NULL COMMENT '源信息', 
    INDEX (update_at)
"""

# 商品数据写出 csv 根路径
barcode_output_csv_root_path = "D:/etl/output/csv"
# 商品数据写出 csv 文件名
barcode_orders_output_csv_file_name = f'barcode-{time.strftime("%Y%m%d-%H%M%S", time.localtime())}.csv'
```

**构建订单 JSON 数据模型类**

每条 barcode 商品数据，里面包含了一个商品信息。

针对 barcode 商品数据我们设计1个模型：

- barcode 商品数据模型，起名叫做：`BarcodeModel`

1）在 model 包目录下，创建一个 `barcode_model.py` 文件

```python
"""
条码商品信息模型类
"""
from util import str_util
from config import project_config as conf


class BarcodeModel(object):
    """条码商品信息模型类"""
    def __init__(self, code=None, name=None, spec=None, trademark=None,
                 addr=None, units=None, factory_name=None, trade_price=None,
                 retail_price=None, update_at=None, wholeunit=None,
                 wholenum=None, img=None, src=None):
        """条码商品模型对象初始化"""
        self.code = code
        self.name = str_util.clear_str(name)
        self.spec = str_util.clear_str(spec)
        self.trademark = str_util.clear_str(trademark)
        self.addr = str_util.clear_str(addr)
        self.units = str_util.clear_str(units)
        self.factory_name = str_util.clear_str(factory_name)
        self.trade_price = trade_price
        self.retail_price = retail_price
        self.update_at = update_at
        self.wholeunit = str_util.clear_str(wholeunit)
        self.wholenum = wholenum
        self.img = img
        self.src = src

    def generate_insert_sql(self):
        """生成SQL的插入语句"""
        sql = f"REPLACE INTO {conf.target_barcode_table_name}(" \
              f"code,name,spec,trademark,addr,units,factory_name,trade_price," \
              f"retail_price,update_at,wholeunit,wholenum,img,src) VALUES(" \
              f"'{self.code}', " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.name)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.spec)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.trademark)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.addr)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.units)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.factory_name)}, " \
              f"{str_util.check_number_null_and_transform_to_sql_null(self.trade_price)}, " \
              f"{str_util.check_number_null_and_transform_to_sql_null(self.retail_price)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.update_at)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.wholeunit)}, " \
              f"{str_util.check_number_null_and_transform_to_sql_null(self.wholenum)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.img)}, " \
              f"{str_util.check_str_null_and_transform_to_sql_null(self.src)}" \
              f")"

        return sql

    @staticmethod
    def get_csv_header(sep=','):
        """
        生成 csv 数据的标头内容
        """
        return f"code{sep}" \
               f"name{sep}" \
               f"spec{sep}" \
               f"trademark{sep}" \
               f"addr{sep}" \
               f"units{sep}" \
               f"factory_name{sep}" \
               f"trade_price{sep}" \
               f"retail_price{sep}" \
               f"update_at{sep}" \
               f"wholeunit{sep}" \
               f"wholenum{sep}" \
               f"img{sep}" \
               f"src\n"

    def to_csv(self, sep=','):
        """生成csv数据行"""
        csv_line = \
            f"{self.code}{sep}" \
            f"{self.name}{sep}" \
            f"{self.spec}{sep}" \
            f"{self.trademark}{sep}" \
            f"{self.addr}{sep}" \
            f"{self.units}{sep}" \
            f"{self.factory_name}{sep}" \
            f"{self.trade_price}{sep}" \
            f"{self.retail_price}{sep}" \
            f"{self.update_at}{sep}" \
            f"{self.wholeunit}{sep}" \
            f"{self.wholenum}{sep}" \
            f"{self.img}{sep}" \
            f"{self.src}\n"

        return csv_line
```

### 3. 核心业务逻辑

**核心业务逻辑代码实现**

1）在项目工程目录下，创建 `mysql_service.py` 文件，添加barcode商品数据采集代码实现：

```python
"""
条码商品数据ETL思路：
1. 查询元数据库表，获取上批次条码商品数据处理的最大时间
2. 查询数据源库表，获取增量更新的条码商品数据
3. 对增量更新的条码商品数据，进行ETL操作（读取->写出MySQL->写出CSV）
4. 将本批次条码商品数据处理的最大时间记录到元数据库表中
"""
import os
import sys

from model.barcode_model import BarcodeModel
from util.logging_util import init_logger
from config import project_config as conf
from util.mysql_util import get_mysql_util

# 创建日志器对象
logger = init_logger()

# 1. 查询元数据库表，获取上批次条码商品数据处理的最大时间
# 创建元数据库工具类对象
metadata_db_util = get_mysql_util(
    conf.metadata_host,
    conf.metadata_user,
    conf.metadata_password
)
# 创建数据源库工具类对象
source_db_util = get_mysql_util(
    conf.source_host,
    conf.source_user,
    conf.source_password
)
# 创建目标数据库工具类对象
target_db_util = get_mysql_util(
    conf.target_host,
    conf.target_user,
    conf.target_password
)

# 判断数据源表是否存在
if not source_db_util.check_table_exists(conf.source_data_db, conf.source_barcode_table_name):
    logger.error(f"数据源库：{conf.source_data_db}中不存在数据源表：{conf.source_barcode_table_name}，"
                 f"无法采集，程序退出，请开展社交找人要去...")
    sys.exit(1)

# 读出元数据库表中的上批次处理时间数据
metadata_db_util.select_db(conf.metadata_db)
# 定义一个变量，记录上批次的处理时间
last_update_time = None

# 判断条码商品处理元数据表是否存在
if not metadata_db_util.check_table_exists(conf.metadata_db, conf.metadata_barcode_table_name):
    metadata_db_util.check_table_exists_and_create(conf.metadata_db,
                                                   conf.metadata_barcode_table_name,
                                                   conf.metadata_barcode_table_create_cols)
else:
    query_sql = f"SELECT time_record FROM {conf.metadata_barcode_table_name} " \
                f"ORDER BY time_record DESC LIMIT 1"
    result = metadata_db_util.query(query_sql)
    if len(result) != 0:
        last_update_time = str(result[0][0])

# 2. 查询数据源库表，获取增量更新的条码商品数据
# 准备查询的SQL语句
if last_update_time:
    sql = f"SELECT * FROM {conf.source_barcode_table_name} " \
          f"WHERE updateAt > '{last_update_time}' " \
          f"ORDER BY updateAt"
else:
    sql = f"SELECT * FROM {conf.source_barcode_table_name} " \
          f"ORDER BY updateAt"

# 开始执行SQL查询
source_db_util.select_db(conf.source_data_db)
result = source_db_util.query(sql)

# 3. 对增量更新的条码商品数据，进行ETL操作（读取->写出MySQL->写出CSV）
# 构建数据模型
barcode_models = []
for single_line_result in result:
    code = single_line_result[0]
    name = single_line_result[1]
    spec = single_line_result[2]
    trademark = single_line_result[3]
    addr = single_line_result[4]
    units = single_line_result[5]
    factory_name = single_line_result[6]
    trade_price = single_line_result[7]
    retail_price = single_line_result[8]
    # single_line_result[9]是读取的updateAt时间，类型是datetime，转换成字符串
    update_at = str(single_line_result[9])
    wholeunit = single_line_result[10]
    wholenum = single_line_result[11]
    img = single_line_result[12]
    src = single_line_result[13]

    # 构建 BarcodeModel
    model = BarcodeModel(code=code,
                         name=name,
                         spec=spec,
                         trademark=trademark,
                         addr=addr,
                         units=units,
                         factory_name=factory_name,
                         trade_price=trade_price,
                         retail_price=retail_price,
                         update_at=update_at,
                         wholeunit=wholeunit,
                         wholenum=wholenum,
                         img=img,
                         src=src)
    barcode_models.append(model)

# 写入数据
target_db_util.select_db(conf.target_data_db)
# 检查目标表是否存在
target_db_util.check_table_exists_and_create(
    conf.target_data_db,
    conf.target_barcode_table_name,
    conf.target_barcode_table_create_cols
)

max_last_update_time = '2000-01-01 00:00:00'

# 创建目标 csv 文件
barcode_csv_write_f = open(os.path.join(conf.barcode_output_csv_root_path, conf.barcode_orders_output_csv_file_name),
                           'a',
                           encoding='utf8')
# 写入csv的标头
barcode_csv_write_f.write(BarcodeModel.get_csv_header())

# 记录已处理的数据条数
count = 0
# 开启事务
target_db_util.begin_transaction()

for model in barcode_models:
    current_data_time = model.update_at
    if current_data_time > max_last_update_time:
        max_last_update_time = current_data_time

    # 生成插入SQL语句
    insert_sql = model.generate_insert_sql()
    target_db_util.insert_single_sql_without_autocommit(insert_sql)

    # 写出数据到 csv
    csv_line = model.to_csv()
    barcode_csv_write_f.write(csv_line)

    count += 1
    if count % 1000 == 0:
        # 每 1000 条数据，清空缓冲区，写入文件
        barcode_csv_write_f.flush()
        logger.info(f"从数据源：{conf.source_data_db}库，读取表：{conf.source_barcode_table_name}，"
                    f"写入csv：{barcode_csv_write_f.name}，当前写出：{count}行")
        # 每 1000 条数据，提交一次事务
        target_db_util.commit_transaction()
        logger.info(f"从数据源：{conf.source_data_db}库，读取表：{conf.source_barcode_table_name}，"
                    f"写入目标表：{conf.target_barcode_table_name}，当前写出：{count}行")

logger.info(f"从数据源：{conf.source_data_db}库，读取表：{conf.source_barcode_table_name}，"
            f"写入csv：{barcode_csv_write_f.name}完成， 最终写出：{count}行")
# 最后一次提交
target_db_util.commit_transaction()
logger.info(f"从数据源：{conf.source_data_db}库，读取表：{conf.source_barcode_table_name}，"
            f"当前写入目标表：{conf.target_barcode_table_name}完成，最终写出：{count}行")

# 4. 将本批次条码商品数据处理的最大时间记录到元数据库表中
# 准备sql
sql = f"INSERT INTO {conf.metadata_barcode_table_name}(" \
      f"time_record, gather_line_count) VALUES(" \
      f"'{max_last_update_time}', " \
      f"{count}" \
      f")"
# 执行插入
metadata_db_util.insert_single_sql(sql)

# 关闭数据库连接
metadata_db_util.close()
source_db_util.close()
target_db_util.close()
logger.info("读取MySQL商品数据，写入目标MySQL和CSV程序执行完成...")
```

## 1.4 日志数据采集

### 1. 业务需求说明

**数据模拟器(数据来源)**

后台日志数据来源于系统后端程序记录的调用数据，这里我们使用一个 Python 程序来模拟生成需要的数据。

1）在项目工程目录下新建一个 `simulator` 文件夹，在该文件夹中新建一个 `backend_logs_simulator.py` 文件，代码如下：

```python
"""
后端服务写出log日志的模拟数据生成器
"""
import datetime
import random
import time

single_log_lines = 1024  # 一个logs文件生成多少行数据
generate_files = 5  # 一次运行生成多少个文件

output_path = "D:/etl/logs/"
log_level_array = ['WARN', 'WARN', 'WARN', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO',
                   'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO', 'INFO',
                   'ERROR']

backend_files_name = ['barcode_service.py', 'barcode_service.py', 'barcode_service.py',
                      'orders_service.py', 'orders_service.py', 'orders_service.py', 'orders_service.py',
                      'orders_service.py', 'orders_service.py',
                      'shop_manager.py', 'shop_manager.py',
                      'user_manager.py', 'user_manager.py', 'user_manager.py',
                      'goods_manager.py', 'goods_manager.py', 'goods_manager.py', 'goods_manager.py',
                      'goods_manager.py', 'goods_manager.py',
                      'base_network.py', 'base_network.py',
                      'event.py', 'event.py', 'event.py', 'event.py', 'event.py', 'event.py', 'event.py']

visitor_areas = {
    '北京市': ['海淀区', '大兴区', '丰台区', '朝阳区', '昌平区', '海淀区', '怀柔区'],
    '上海市': ['静安区', '黄浦区', '徐汇区', '普陀区', '杨浦区', '宝山区', '浦东新区', '浦东新区'],
    '重庆市': ['万州区', '万州区', '涪陵区', '渝中区', '沙坪坝区', '九龙坡区', '南岸区'],
    '江苏省': ['南京市', '南京市', '南京市', '苏州市', '苏州市', '无锡市', '常州市', '宿迁市', '张家港市'],
    '安徽省': ['阜阳市', '阜阳市', '六安市', '合肥市', '合肥市', '合肥市', '池州市', '铜陵市', '芜湖市'],
    '山东省': ['济南市', '济南市', '青岛市', '青岛市', '青岛市', '菏泽市'],
    '湖北省': ['武汉市', '武汉市', '武汉市', '十堰市', '荆州市', '恩施土家族苗族自治州'],
    '广东省': ['广州市', '广州市', '广州市', '深圳市', '深圳市', '深圳市', '珠海市'],
    '天津市': ['和平区', '河东区', '河西区', '武清区', '宝坻区'],
    '湖南省': ['长沙市', '长沙市', '长沙市', '长沙市', '长沙市', '长沙市', '长沙市', '株洲市', '张家界市', '常德市', '益阳市'],
    '浙江省': ['杭州市', '杭州市', '湖州市', '绍兴市', '舟山市', '金华市', '嘉兴市', '丽水市']
}
visitor_province = ['北京市', '上海市', '重庆市', '江苏省', '安徽省', '山东省', '湖北省', '广东省', '天津市', '湖南省', '浙江省']

response_flag = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0]
response_for_error_flag = [1, 1, 1, 1, 1, 0]

for j in range(0, generate_files):
    write_file_path = f'{output_path}{datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")}.log'
    with open(write_file_path, 'w', encoding="UTF-8") as f:
        for i in range(single_log_lines):
            date_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")
            log_level = log_level_array[random.randint(0, len(log_level_array) - 1)]
            file_name = backend_files_name[random.randint(0, len(backend_files_name) - 1)]
            if not log_level == "ERROR":
                if response_flag[random.randint(0, len(response_flag) - 1)] == 1:
                    response_time = random.randint(0, 1000)
                else:
                    response_time = random.randint(1000, 9999)
            else:
                if response_for_error_flag[random.randint(0, len(response_for_error_flag) - 1)] == 1:
                    response_time = random.randint(0, 1000)
                else:
                    response_time = random.randint(1000, 9999)

            province = visitor_province[random.randint(0, len(visitor_province) - 1)]
            city = visitor_areas[province][random.randint(0, len(visitor_areas[province]) - 1)]

            log_str = f"{date_str}\t[{log_level}]\t{file_name}\t响应时间:{response_time}ms\t{province}\t{city}\t" \
                      f"这里是日志信息......"

            f.write(log_str)
            f.write("\n")
    print(f"本次写出第: {j + 1}个文件完成, 文件为: {write_file_path}, 行数:{single_log_lines}")
    time.sleep(1)
```

> 注意：上面代码的第12行，output_path变量记录生成的日志数据写到哪个文件夹下。**用的时候，先改一下设定为你电脑上的文件夹即可，然后右键直接运行即可**。

2）生成日志数据文件之后，点击查看日志数据的结构如下：

![img](E:\黑马培训\Python额外补充\assets\1-166279174888410.png)

它的结构就是：

| 日志时间 | 日志级别 | 代码模块 | 接口响应时间 | 调用者省份 | 调用者城市 | 日志信息 |
| -------- | -------- | -------- | ------------ | ---------- | ---------- | -------- |
| ...      | ...      | ...      | ...          | ...        | ...        | ...      |

**业务需求说明**

我们现在要做的，就是进行后台日志数据采集，采集后台日志文件中的数据，具体输出有两种要求：

1）将采集的后台日志数据保存到目标数据库 `retail` 中

2）将采集的后台日志数据写出到 CSV 文件中

### 2. 构建数据模型

**新增配置文件信息**

由于后台日志数据处理后，涉及到写出MySQL和CSV，我们在配置文件中，添加写出到MySQL和CSV的相关配置。

在 config 包的 `project_config.py` 文件中，新增以下配置内容：

```python
# ################## --后台日志数据采集配置项-- ###################
# 待采集 日志 文件所在的目录
backend_logs_data_root_path = 'D:/etl/logs'

# 采集后台日志数据，元数据表配置项
logs_monitor_meta_table_name = "backend_logs_monitor"
logs_monitor_meta_table_create_cols = \
    "id INT PRIMARY KEY AUTO_INCREMENT, " \
    "file_name VARCHAR(255) NOT NULL COMMENT '处理文件名称', " \
    "process_lines INT NULL COMMENT '文件处理行数', " \
    "process_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '文件处理时间'"

# 后台日志表名称
target_logs_table_name = "backend_logs"
target_logs_table_create_cols = \
        f"id int PRIMARY KEY AUTO_INCREMENT COMMENT '自增ID', " \
        f"log_time TIMESTAMP(6) COMMENT '日志时间,精确到6位毫秒值', " \
        f"log_level VARCHAR(10) COMMENT '日志级别', " \
        f"log_module VARCHAR(50) COMMENT '输出日志的功能模块名', " \
        f"response_time INT COMMENT '接口响应时间毫秒', " \
        f"province VARCHAR(30) COMMENT '访问者省份', " \
        f"city VARCHAR(30) COMMENT '访问者城市', " \
        f"log_text VARCHAR(255) COMMENT '日志正文', " \
        f"INDEX(log_time)"

# 后台日志数据写出 csv 的根路径
logs_output_csv_root_path = "D:/etl/output/backend_logs"
# 每一次运行，后台日志文件写出路径
logs_output_csv_file_name = \
    f"orders-{time.strftime('%Y-%m-%d-%H_%M', time.localtime())}.csv"
```

**构建后台日志数据模型类**

后台日志文件中的每一行记录，包含了一条日志信息。

针对后台日志数据我们设计1个模型：

- 后台日志数据模型，起名叫做：`BackendLogsModel`

1）在 model 包目录下，创建一个 `backend_logs_model.py` 文件

```python
"""
后台日志数据模型类
"""
from config import project_config as conf


class BackendLogsModel(object):
    """后台日志数据模型类"""
    def __init__(self, data: str, sep="\t"):
        """后台日志数据模型对象初始化"""
        arrs = data.split(sep)
        self.log_time = arrs[0]                                     # 日志时间
        self.log_level = arrs[1].replace("[", "").replace("]", "")  # 日志级别,处理方括号
        self.log_module = arrs[2]                                   # 日志模块
        self.response_time = int(arrs[3][:-2][5:])                  # 响应时间提取
        self.province = arrs[4]                                     # 省份
        self.city = arrs[5]                                         # 城市
        self.log_text = arrs[6]                                     # 日志正文

    def generate_insert_sql(self):
        """生成SQL的插入语句"""
        # 注意，表名自行配置到配置文件中
        return f"INSERT INTO {conf.target_logs_table_name}(" \
               f"log_time, log_level, log_module, response_time, province, city, log_text) VALUES(" \
               f"'{self.log_time}', " \
               f"'{self.log_level}', " \
               f"'{self.log_module}', " \
               f"{self.response_time}, " \
               f"'{self.province}', " \
               f"'{self.city}', " \
               f"'{self.log_text}'" \
               f")"

    @staticmethod
    def get_csv_header(sep=','):
        """
        生成 csv 数据的标头内容
        """
        return f"log_time{sep}" \
               f"log_level{sep}" \
               f"log_module{sep}" \
               f"response_time{sep}" \
               f"province{sep}" \
               f"city{sep}" \
               f"log_text\n"

    def to_csv(self, sep=","):
        """生成csv数据行"""
        return \
            f"{self.log_time}{sep}" \
            f"{self.log_level}{sep}" \
            f"{self.log_module}{sep}" \
            f"{self.response_time}{sep}" \
            f"{self.province}{sep}" \
            f"{self.city}{sep}" \
            f"{self.log_text}\n"
```

### 3. 核心业务逻辑

**核心业务逻辑代码实现**

1）在项目工程目录下，创建 `backend_logs_service.py` 文件，添加后台日志数据采集代码实现：

```python
"""
后台日志数据ETL思路：
1. 获取被采集后台日志数据文件夹内的文件列表
2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
3. 分别读取待采集的后台日志文件，进行ETL操作（读取->写出CSV->写出MySQL）
4. 将本次处理的后台日志文件信息，记录到元数据库表中。
"""
import os

from config import project_config as conf
from model.backend_logs_model import BackendLogsModel
from util import file_util, mysql_util
from util.logging_util import init_logger
from util.mysql_util import get_mysql_util

# 创建日志器对象
logger = init_logger()

# 1. 获取被采集后台日志数据文件夹内的文件列表
logger.info('采集后台日志数据程序启动...')
backend_log_files = file_util.get_dir_file_list(conf.backend_logs_data_root_path)

# 2. 和元数据库表中的已采集文件进行对比，确定待采集文件列表
# 创建元数据库的 db_util 对象
meta_db_util = get_mysql_util(
    conf.metadata_host,
    conf.metadata_user,
    conf.metadata_password
)

# 创建目标数据库的 db_util 对象
target_db_util = get_mysql_util(
    conf.target_host,
    conf.target_user,
    conf.target_password
)
# 检查元数据库表是否存在，不存在则创建
meta_db_util.check_table_exists_and_create(conf.metadata_db,
                                           conf.logs_monitor_meta_table_name,
                                           conf.logs_monitor_meta_table_create_cols)

# 获取元数据库表中已处理的文件列表
processed_files = mysql_util.get_processed_files(meta_db_util, conf.metadata_db, conf.logs_monitor_meta_table_name, conf.logs_monitor_meta_table_create_cols)
# 对比确定待采集的文件列表
need_process_files = file_util.get_new_by_compare_lists(processed_files, backend_log_files)

if need_process_files:
    logger.info(f"经过对比元数据，发现有：{need_process_files} 这些新文件，即将采集这些文件的数据.")
else:
    logger.info(f"经过对比元数据，暂无新文件需要进行处理.")
    # 结束程序执行
    exit(0)

# 3. 分别读取待采集的后台日志文件，进行ETL操作（读取->写出CSV->写出MySQL）
logs_csv_write_f = open(os.path.join(conf.logs_output_csv_root_path, conf.logs_output_csv_file_name), 'a', encoding='utf8')
# 写入 csv 标头内容
logs_csv_write_f.write(BackendLogsModel.get_csv_header())

# 检查目标数据库表是否存在，不存在则创建
target_db_util.check_table_exists_and_create(conf.target_data_db,
                                             conf.target_logs_table_name,
                                             conf.target_logs_table_create_cols)

for file in need_process_files:
    logs_model_list = []

    # 读取文件中的每一行内容进行处理
    for line in open(file, 'r', encoding='utf8'):
        line = line.strip()
        logs_model = BackendLogsModel(line)
        logs_model_list.append(logs_model)

    # 开始写出数据到 CSV 和 MySQL
    # 记录已处理数据的条数
    json_data_processed_count = 0

    # 开启事务
    target_db_util.begin_transaction()

    for model in logs_model_list:
        # 写出日志信息到数据库表
        target_db_util.insert_single_sql_without_autocommit(model.generate_insert_sql())

        # 写出订单信息到csv
        logs_csv_write_f.write(model.to_csv())

        # 计数器加1
        json_data_processed_count += 1

        # 写出1000条，刷新文件缓冲区
        if json_data_processed_count % 1000 == 0:
            logs_csv_write_f.flush()

    # 提交 MySQL 事务
    target_db_util.commit_transaction()

    # 记录日志信息
    logger.info(f'文件{file}处理完成，{len(logs_model_list)}条数据被处理')

    # 记录元数据，将已处理的文件，写入到元数据库表中记录
    file_name = file.replace('\\', '/')
    sql = f"INSERT INTO {conf.logs_monitor_meta_table_name}(file_name, process_lines) VALUES(" \
          f"'{file_name}', {len(logs_model_list)})"

    # 一定要注意切换数据库
    meta_db_util.insert_single_sql(sql)
    logger.info(f'文件{file}处理完成，元数据记录完成')

# 关闭文件对象
logs_csv_write_f.close()

# 关闭数据库连接
meta_db_util.close()
target_db_util.close()
```





订单文件定期会在一个文件夹下更新

1. 把已经采集过的数据的路经放入数据库中 称为元数据

![image-20220831225741985](E:\黑马培训\assets\image-20220831225741985.png)

2. 一个完整的程序都要有日志系统,

![image-20220831225632570](E:\黑马培训\assets\image-20220831225632570.png)

![image-20220831225639807](E:\黑马培训\assets\image-20220831225639807.png)

![image-20220831225645523](E:\黑马培训\assets\image-20220831225645523.png)

![image-20220831225653790](E:\黑马培训\assets\image-20220831225653790.png)

![image-20220831225700669](E:\黑马培训\assets\image-20220831225700669.png)



![image-20220831225748916](E:\黑马培训\assets\image-20220831225748916.png)

![image-20220902211740333](E:\黑马培训\assets\image-20220902211740333.png)

![image-20220902211746639](E:\黑马培训\assets\image-20220902211746639.png)

![image-20220902211757276](E:\黑马培训\assets\image-20220902211757276.png)

![image-20220902211817935](E:\黑马培训\assets\image-20220902211817935.png)

![image-20220902211826276](E:\黑马培训\assets\image-20220902211826276.png)

![image-20220902211835909](E:\黑马培训\assets\image-20220902211835909.png)

![image-20220902211844197](E:\黑马培训\assets\image-20220902211844197.png)