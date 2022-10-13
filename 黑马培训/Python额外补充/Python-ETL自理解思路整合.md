## ETL - 数据的抽取(extract) --> 转换(Transform) --> 加载(loading)

## 1. ETL 简介

​        ETL是将业务系统的数据经过抽取、清洗转换之后加载到数据仓库的过程，目的是将企业中的分散、零乱、标准不统一的数据整合到

一起，为企业的决策提供分析依据， ETL是BI(商业智能)项目重要的一个环节。

**流程示意图:**

![image-20220908141705595](E:\黑马培训\Python额外补充\assets\image-20220908141705595.png)

**Hadoop大数据分析流程示意:**

![image-20220908141805099](E:\黑马培训\Python额外补充\assets\image-20220908141805099.png)

**订单json数据的etl采集思路**

![image-20220910134018874](E:\黑马培训\Python额外补充\assets\image-20220910134018874.png)

## 2. 利用对象存储数据

**etl利用模型对象(model, 类)存储数据重要思想**

在python中保存数据的方式有非常多种,例如保存在字典,列表,元组中等,这样保存数据非常的繁琐复杂,当遇到大批量数据的时候,不利于工

作人员写程序来存储数据,这时候就可以利用面对对象的思想定义一个类,类中的属性就是对应的要存储的数据,当你每实例化一个对象的时

候,就相当于保存了一条数据.

**具体代码书写流程:**

1.定义出来一个具体的类,在类中书写`__init__`方法,在新建这个类的对象的时

候,初始化这个对象,并传入相应的数据

2.在要用到对象保存数据的别的类中,初始化`__init__`方法中 建立一个保存数
据的空列表,我们默认这个空列表保存的数据是一个对象

3.在这个类中要使用到对象保存数据的地方,方法前面照常写传入数据的步骤,

在方法的末尾写相应的保存数据的代码

4.**重点:**  实例化保存的数据类的对象,并给这个对象传入相应的数据

**完成这个步骤后就相当于保存了一条数据,接着把这个对象放入列表中**

## 3. 程序的日志系统

> 一个合格的程序是必须要有日志记录的,它记录了一些在运行过程中的一些关键信息,例如程序处理了XX文件,XX用户做了XX操作

> python中是利用logging模块来记录日志的

**关于程序日志的5个等级的说明 从低到高分析**

- DEBUG(10): 程序调试BUG时使用,话痨级别,使用这个级别,**会输出非常详细的日志内容**
- INFO(20): 程序正常运行时使用,**普通信息**,输出到日志中,**比如处理了多少条数据**

- WARNING(30): 程序未按预期运行时使用,**但并不是错误,有可能有隐患,输出日志供程序员查看**

- ERROR(40): 程序出错误时使用,**但是不一定影响运行,输出到日志中,供程序员分析**

- CRITICAL(50): 特别严重的问题(灾难级别), **如果输出这个级别日志,表示程序完蛋了**

  > 从低到高: DEBUG < INFO < WARNING < ERROR < CRITICAL   设置日志输出级别后只会输出
  >
  > 比这个级别大的日志
  >
  > 默认日志输出级别为: WARNING

  更多关于日志器模块的知识点,可以到笔记Python日志器中查看..........

  日志器对象logger的类型是logging模块下的RootLogger类,默认的日志器的输出日志级别为WARNING ,这个类下面有以下不止这些的方法

  ![image-20220908165627147](E:\黑马培训\Python额外补充\assets\image-20220908165627147.png)

**使用日志功能的步骤**（在python中模块就相当于一个python文件，里面可以存放多个类，包里面可以

存放多个模块，但是包的本质也是模块）(创建一个新的某个功能的对象时都要带一个logging.类名())：

**扩展：**包也可以作为模块导入python文件

- 创建一个日志器对象(要让日志器对象有输出的功能，那么就要利用addHandler方法给它添加日志

  处理器对象)

  ```python
  logger = logging.getLogger()
  ```

- 创建一个日志处理器对象（控制日志信息是输出到文件中，还是屏幕上）

  **输出信息到屏幕上的日志处理器对象：**

  ```python
  Stream_header = logging.StreamHandler()
  ```

  **输出信息到文件中的日志处理器对象：**

  ```python
  file_hander = logging.FileHandler('../logs/test.log', 'a', 'utf8')
  ```

  logging有两个常用的Handler

  - StreamHeadler:      logging.StreamHandler() ==> 把日志信息记录到屏幕上
  - FileHandler： logging.FileHandler(参数1，参数2，参数3)
    - 参数1    文件路径
    - 参数2    模式 “a”是追加，“w“是覆盖  这里目标文件不存在的话会自动创建
    - 参数3    编码  一般utf8

- logging模块中有一个类Formatter可以设置我们记录的每一条日志的信息格式，所以我们可以创

  建一个日志格式控制器对象

  **常用的日志器格式化的占位符格式：**

  ```python
  # %(asctime)s 日志信息记录的时间
  # %(levelname)s 日志等级
  # %(lineno)d 日志信息是在哪一行记录的
  # %(filename)s 日志信息是在哪一个文件中记录的
  # %(message)s 日志信息的正文
  # 常用格式
  formatter = logging.Formatter('%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')
  
  ```

  **运行结果：** 

  ![image-20220908182603879](E:\黑马培训\Python额外补充\assets\image-20220908182603879.png)

- 在日志处理器对象中有给日志设置格式的方法setFormatter(),接着将formatter当作参数传进来

  ```python
  formatter = logging.Formatter('%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')
  file_handler.setFormatter(formatter)
  ```

- 利用addHandler方法为logger对象添加日志处理器，**前者是后者的方法**

- 选择日志等级，记录日志信息，并会输出（在这之前还可以设置日志的输出等级，NOTSET=0）给

  logger对象具体的方法传入参数，这个参数是输入的信息，方法会将这个信息传递下去，上面的所

  有方法都是为了最后logger对象输出日志信息做铺垫

  ```python
  logger.setLevel(logging.DEBUG)
  ```

- 记录具体日志信息

  ```python
  logger.info('我是info信息..')
  ```

**总结:** 日志器功能的使用  前期入门只用到了三个对象,按照使用的顺序,分别是日志器对象,日志处理器对象,日志格式控制器对象 用到的方法日志处理器的setFormatter()方法,日志器对象的addHandler()方法

## 4. 时间模块简单介绍

```python
"""

time模块的基本使用
* time.sleep(数字): 程序休眠几秒
* time.time(): 获取当前时间戳(以秒为单位)
* time.time_ns: 获取当前的时间戳(以纳秒为单位的)
* time.localtime(): 获取本地时间,结果是一个struct_time类的对象
  loc_time.tm_year
  loc_time.tm_mon
  loc_time.tm_mday
* time.strftime(格式化字符串,日期): 将日期数据(struct_time对象)格式化一个字符串
  例子: time.strftime('%Y-%m-%d %H:%M:%S', loc_time)
* time.strptime(日期字符串,格式化字符串): 将日期字符串转换为一个日期数据(struct_time)对象
  将前面的字符串变成后面的格式
  例子:
      time_str = '2022-08-30 11:49:53'
      time.strptime(time_str,'%Y-%m-%d %H:%M:%S')

* '2022-08-30  11:47:30'
*     %Y 年
*     %m 月
*     %d 日
*     %H 时
*     %M 分
*     %S 秒
"""
```

**time.strptime() 格式化的结果**

![image-20220908195554904](E:\黑马培训\Python额外补充\assets\image-20220908195554904.png)

![image-20220908195719067](E:\黑马培训\Python额外补充\assets\image-20220908195719067.png)

## 5. OS模块简单介绍

**1. 给定一个文件的相对路径 获取文件的绝对路径**

```python
res1 = os.path.abspath("./test_logs.py")
print(res1)
```

**结果:**![image-20220910150019822](E:\黑马培训\Python额外补充\assets\image-20220910150019822.png)

> -----------------------------------------------------------------------------



**2. 返回指定路径的上级路径**

```python
# 返回指定路径的上级路径
res2 = os.path.dirname(res1)
print(res2)
```

**结果:**  ![image-20220910152740300](E:\黑马培训\Python额外补充\assets\image-20220910152740300.png)

> -----------------------------------------------------------------------------



**3. 返回指定路径的最后一部分**

```python
# 返回指定路径的最后一部分
res3 = os.path.basename(res1)
print(res3)
```

**结果:**  ![image-20220910152800438](E:\黑马培训\Python额外补充\assets\image-20220910152800438.png)

> -----------------------------------------------------------------------------

os.basename()返回的数据是**字符串类型的**



**4. 文件路径拼接**

```python
# 路径拼接 ,这种拼接方式前一个路径的末尾可以不写反斜杠或者斜杠就能拼接进去
# 因为在python中反斜杠有特殊含义,所以要写反斜杠的时候,就要写两个才能区分开
res4 = os.path.join("D:\\etl\\json", "x00")
print(res4)
```

**结果:** ![image-20220910152257229](E:\黑马培训\Python额外补充\assets\image-20220910152257229.png)

> -----------------------------------------------------------------------------



**5. 判断指定路径下是否为文件**

```python
# 判断指定路径下是否为文件
res5 = os.path.isfile("./test_logs.py")
print(res5)
```

**结果:** ![image-20220910152608088](E:\黑马培训\Python额外补充\assets\image-20220910152608088.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**6. 判断指定路径下是否为目录**

```python
# 判断指定路径下是否为目录
res6 = os.path.isdir("./test_logs.py")
print(res6)
```

**结果:**  ![image-20220910153007449](E:\黑马培训\Python额外补充\assets\image-20220910153007449.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**7. 获取当前程序的工作路径**

```python
# 获取当前程序的工作路径
res7 = os.getcwd()
print(res7)
```

**结果:** ![image-20220910153233982](E:\黑马培训\Python额外补充\assets\image-20220910153233982.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**8. 获取文件夹下的文件(返回的是一个列表)**

```python
# 获取文件夹下面有哪些文件,返回的是一个列表
res8 = os.listdir("../util")
print(res8)
```

**结果:**  ![image-20220910175748731](E:\黑马培训\Python额外补充\assets\image-20220910175748731.png)

> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



## 6. ETL 项目config包下模块开发

**配置一:**

**日志系统的文件配置**

用于记录本项目中相关的配置信息 例如: 日志输出的目录,日志文件名记录到文件中,具体如下

```python
# ###############--程序运行日志的配置项--####################
# 配置日志输出的目录
log_root_path="E:/Workspace/bigdata/pythonbasic/python-etl/logs/"
# 配置日志输出的文件名
# 输出的日志文件名会是 pyetl-20220908-23.log
log_name = f'pyetl-{time.strftime("%Y%m%d-%H", time.localtime())}.log'
```

## 7. ETL 项目util包下模块的开发

**util 工具包**

**工具模块一:**   

准备日志相关的工具util方法 **日志工具方法完整代码**

```python
"""

日志工具方法模块:该模块用于构建日志处理对象,方便后续我们在程序中快速获取日志器,记录日志

"""
import logging
from config import project_config as conf


class Logging(object):
    def __init__(self, level=logging.INFO):
        """实例对象的初始化方法"""
        # 创建一个日志器对象
        self.logger = logging.getLogger()
        # 设置日志输出控制级别
        self.logger.setLevel(level)


# 定义一个函数,通过这个函数可以返回所需的logger对象
def init_logger():
    """日志器的对象的初始化方法"""
    # 获取一个日志器对象,并且已经设置日志输出级别
    logger = Logging().logger

    # 设置日志信息输出到指定文件中
    file_handler = logging.FileHandler(filename=conf.log_root_path + conf.log_name,
                                       mode='a',
                                       encoding='utf8')

    # 设置日志信息输出的格式,也就是创建日志格式控制器对象,它作用于日志处理器对象
    fmt = logging.Formatter(
        '%(asctime)s - [%(levelname)s] - %(filename)s[%(lineno)d]: %(message)s')

    # 将格式设置到文件的 handler (日志处理器对象) 中
    file_handler.setFormatter(fmt)

    # 将文件输出的 handler 设置给 logger 对象, 将日志处理器对象添加给日志器对象
    logger.addHandler(file_handler)

    # 返回日志器对象
    return logger


if __name__ == '__main__':
    # 调用方法获取日志器对象
    logger = init_logger()
    logger.info('我是info信息')
    logger.warning('我是warning信息')
    logger.error('我是error信息')

```

**1. 面向对象创建日志器对象的图解:**

![image-20220908234507924](E:\黑马培训\Python额外补充\assets\image-20220908234507924.png)

**2.日志文件对象初始化图解:**

有日志器对象以后,我们要对日志器对象的内容,输出位置进行配置,所以还要在类外面新建一个方法

从config包中找到具体的模块导入  日志器配置项 对日志输出的文件路径进行拼接

![image-20220908235909791](E:\黑马培训\Python额外补充\assets\image-20220908235909791.png)

**获取日志器对象的方法图解:**

![image-20220909000530939](E:\黑马培训\Python额外补充\assets\image-20220909000530939.png)

**在本模块中进行简单的测试:**

测试代码要写在 if` __name__` == `__main__`:中,以免此模块导入其他python文件中,测试代码自己运行

![image-20220909000936678](E:\黑马培训\Python额外补充\assets\image-20220909000936678.png)

**运行结果:**

![image-20220909001120954](E:\黑马培训\Python额外补充\assets\image-20220909001120954.png)

**题外话:** 把一些日志工具方法的配置文件写在配置包下的配置模块中有什么好处?

> 将来要是想要去修改日志文件的输出目录,就不要去日志工具模块下去找,也就是说这里的代码,我们不需要改动,只要去改动配置文件中的信息就可以了.



> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





**工具模块二:**

准备文件处理的工具方法    **文件处理工具方法相关代码**

**注意点:** windows中文件绝对路径用的是反斜杠`\\`而mysql中记录文件路径用的是斜杠`/` 这时候就要来一个转换,不然无法完成匹配

主要思想 

1. 利用os.listdir()获取指定目录下的文件名列表
2. 遍历文件名列表使用os.abspath()获取对应文件名相应的绝对路径列表
3. 接着与元数据库内的文件路径列表作对比,找出没有采集过的json数据

```python
"""
文件操作模块工具
"""
# 定义一个方法获取要采集json数据文件夹下,所有json文件的绝对路径
import os


def get_dir_files_list(path='./', recursion=True):
    # 获取指定目录下的文件名列表
    name_list = os.listdir(path)

    # 创建空列表接收是文件的名字而不是目录的名字
    files = []

    # 遍历文件名列表获取文件名,并且与路径做拼接,紧接着调用abspath方法,获取文件绝对路径
    # 并且判断该文件名是文件还是目录,以便于下一步操作
    for name in name_list:
        # 拼接文件路径
        join_path = os.path.join(path, name)
        # 根据拼接的路径,获取文件的绝对路径(因为相对路径,仅在本文件中会有效,换其他位置会失效)
        abs_path = os.path.abspath(join_path)
        if os.path.isfile(abs_path):
            # 把这个文件的绝对路径加入files列表中
            files.append(abs_path)
        # 如果在这路径下有文件夹,则要开启递归,获取文件
        # 程序运行前,我们就得查看,是否有子文件夹的存在,
        # 传参的时候给他赋值True,或者直接把缺省参数默认值设为True

        else:
            # 这时候recursion的值就是True,不然就算有子文件夹,也不会执行递归操作
            if recursion:
                sub_files_list = get_dir_files_list(abs_path)
                # 上面这个式子接收到的结果是一个列表,为一个列表添加列表元素,就要有相加
                files += sub_files_list
    # 遍历完整个name_list列表后,返回另一个带有全部文件绝对路径的列表
    return files


def get_new_by_compare_lists(processed_list, all_list):
    """
    :param processed_list: 已经采集过的文件绝对路径列表
    :param all_list: 带有全部文件绝对路径的文件列表
    :return: 返回一个新列表
    """
    new_list = []

    # 遍历查询新元素
    for i in all_list:
        # 将路径中的反斜杠转换为正斜杠
        i = i.replace("\\", "/") # 先运行后面赋值给前面
        if i not in processed_list:
            new_list.append(i)
    return new_list


# 测试代码是否能够使用
# if __name__ == "__main__":
#     res = get_dir_files_list(r"E:\Workspace\bigdata\pythonbasic\python-logs\test\testdir")
#     print(res)

# 测试路径斜杠用的不一样时会发生的什么情况
if __name__ == '__main__':
    all_files = get_dir_files_list(r'D:\etl\json')
    processed_files = ['D:/etl/json/x00', 'D:/etl/json/x01']
    new_all_files = []
    # 因为已经处理过的文件的绝对路径的记录斜杠格式与全部的文件绝对路径格式不同
    # 所以识别不出来,就返回了整个全部列表
    for i in all_files:
        # 把路径中的反斜杠换成正斜杠
        im = i.replace("\\", '/')
        new_all_files.append(im)
    new_files = get_new_by_compare_lists(processed_files, new_all_files)
    print(new_files)
```

**1. 获取要采集json文件目录下的所有文件的绝对路径图解示意:**

![image-20220910213204121](E:\黑马培训\Python额外补充\assets\image-20220910213204121.png)

> 全部文件绝对路径的一个列表
>
> ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



**2. 获取要采集的文件绝对路径列表图解示意:**

![image-20220910234210061](E:\黑马培训\Python额外补充\assets\image-20220910234210061.png)

## 8. ETL 项目test包下模块的开发

**单元测试 (unit testing)**     是指对软件中最小可测试单元进行检查和验证,比如我们在python中写了一个函数,来测试这个函数的功能是否正确,这个过程就是单元测试

在python中有一个内置的专门进行单元测试的模块: **unittest** 可以通过unittest来编写单元测试代码

**unittest使用简介**

使用unittest编写单元测试代码,需要自己定义一个单元测试类,该类必须继承unittest中的TestCase类,然后在类中定义单元测试方法即可

> 一个单元测试类,一般针对一个python文件
>
> python文件中定义的功能方法,可以对应测试类的一个测试方法

**图解与模板示例:**

![image-20220909005251489](E:\黑马培训\Python额外补充\assets\image-20220909005251489.png)

**注意: logger日志器对象所属的类是Logging.RootLogger**

测试的时候要先判断这个对象的类型 利用assertIsInstance(要判断的对象,   类型)方法来判断

**实际代码书写示例:**

![image-20220909010043572](E:\黑马培训\Python额外补充\assets\image-20220909010043572.png)

**单元测试类中的两个初始化方法:**

![image-20220909010256148](E:\黑马培训\Python额外补充\assets\image-20220909010256148.png)

**注意: 单元测试类中的判断方法都是assert开头的**

os.basename()返回的数据是**字符串类型的**

**测试例子1   测试获取全部文件绝对路径工具方法:**

```python
import os.path
from unittest import TestCase
from util import file_util


class test_file(TestCase):
    def setUp(self) -> None:
        print("测试方法开始了.....")

    def tearDown(self) -> None:
        print('tearDown被执行..')

    def test_file_util(self):
        # 测试递归获取
        files = file_util.get_dir_files_list(r'E:\Workspace\bigdata\pythonbasic\python-logs\test\testdir')
        # 对files中的文件绝对路径进行排序
        files.sort()
        # 接着遍历files列表获取基础的文件名,然后添加入新的空列表中,供接下来的比对操作
        bas_names = []
        for name in files:
            bas_name = os.path.basename(name)
            bas_names.append(bas_name)
        # 开始测试比对
        self.assertEqual(['1', '2', '3', '4', '5'], bas_names)
```

**测试例子2   测试日志模块是否配置正确:**

```python
import logging
from unittest import TestCase
from util import logs_util as logs


# 编写测试类
class test_logs(TestCase):
    # 这个方法就跟初始化方法一样,程序运行开始时,会先运行它
    def setUp(self) -> None:
        print("测试方法开始了....")

    # 结束时才运行它,一般做收尾工作
    def tearDown(self) -> None:
        print("tearDown方法运行了....")

    # 测试代码的编写方式一定要是test开头+要测试代码的方法名,这样系统才能识别出来
    def test_logs_util(self):

        # 编写测试方法对比
        logger = logs.init_logger()

        self.assertIsInstance(logger, logging.RootLogger)

```

