# Python日志器

## 1. 日志器模块介绍

**日志记录，监控，便于定位bug**

![image-20220902195713589](E:\黑马培训\Python额外补充\assets\image-20220902195713589.png)

### 1.2 介绍

**日志级别：**

| 基本     | 中文意义       | 触发情况                                                     |
| -------- | -------------- | ------------------------------------------------------------ |
| DEBUG    | 调试           | 调试时期                                                     |
| INFO     | 提示           | 正常运行时                                                   |
| WARINING | 警告           | 现在可运行，但未来可能发生错误时（例如未来存储空间可能不足） |
| ERROR    | 错误           | 当程序发生错误，无法执行某些功能时                           |
| CRITICAL | 严重的、致命的 | 当程序发生严重错误，无法继续运行时                           |

默认是`WARNING。`

![image](https://img.jbzj.com/file_images/article/202004/2020040911561217.png)

**基本类：**

- Loggers ：日志器，负责开放接口来调用功能，比如它负责添加Handlers和Filters 。有默认的Loggers 对象
- Handlers ：负责日志记录的传输目的地，比如有FileHandler(写入目标为文件)和StreamHandler（写入目标为流，默认为标准输出流）
- Filters ：负责过滤哪些日志是要输出的 。
- Formatters ：负责对日志输出格式的格式化。

## 2. Python 的标准日志模块

​      上面我们说了「日志」是如此的重要，作为无所不能的 Python 当然也有日志相关的功能，Python 标准库中提供了 logging  模块供我们使用。在最简单的使用中，**默认情况下 logging 将日志打印到屏幕终端**，我们可以直接导入 logging 模块，然后调用  debug，info，warn，error 和 critical 等函数来记录日志，**默认日志的级别为warning**，级别比 warning  高的日志才会被显示（critical > error > warning > info >  debug），「级别」是一个逻辑上的概念，用来区分日志的重要程度。

```python
import logging
logging.debug('debug message')
logging.info("info message")
logging.warn('warn message')
logging.error("error message")
logging.critical('critical message')
```

**上述代码的执行结果如下所示：**

![image-20220902200241778](E:\黑马培训\Python额外补充\assets\image-20220902200241778.png)

​      我在上面说过，用 print 的话会产生大量的信息，从而很难从中找到真正有用的信息。而 logging  中将日志分成不同的级别以后，我们在大多数时间只保存级别比较高的日志信息，从而提高了日志的性能和分析速度，这样我们就可以很快速的从一个很大的日志文件里找到错误的信息。

### 2.1 配置日志格式

我们在用 logging 来记录日志之前，先来进行一些简单的配置：

![image-20220902200854769](E:\黑马培训\Python额外补充\assets\image-20220902200854769.png)

上面的例子中，我是用 basicConfig 对日志进行了简单的配置，其实我们还可以进行更为复杂些的配置，在此之前，我们先来了解一下 

logging 中的几个概念：

- **Logger：日志记录器，是应用程序中可以直接使用的接口。**
- **Handler：日志处理器，用以表明将日志保存到什么地方以及保存多久。**
- **Formatter：格式化，用以配置日志的输出格式。**  

上述三者的关系是：**一个 Logger 使用一个 Handler，一个 Handler 使用一个  Formatter**。那么概念我们知道了，该如何去使用它们

呢？我们的 logging 中有很多种方式来配置文件，**简单的就用上面所说的  basicConfig**，对于比较复杂的我们可以将日志的配置保存在一

个配置文件中，然后在主程序中使用 fileConfig 读取配置文件。题目: 日志文件保存所有 debug 及其以上级别的日志，每条日志中要有打

印日志的时间，日志的级别和日志的内容。**在基础配置方法中配置**

![image-20220902201006077](E:\黑马培训\Python额外补充\assets\image-20220902201006077.png)

filename这样写的话,会在当前目录下创建一个test.log日志文件

最下面的是打印信息和加数据的语句,这里面写的数据就是格式化format里面message的数据

上述代码的运行结果为:

![image-20220902202029725](E:\黑马培训\Python额外补充\assets\image-20220902202029725.png)

**在配置文件中写配置，通过导入模块来读取**

**文件格式（配置文件起名为 logging.conf）：**

![image-20220902203525768](E:\黑马培训\Python额外补充\assets\image-20220902203525768.png)

**代码解释：**

在上述的日志配置文件中

- 首先我们在 [loggers] 中声明了一个叫做 root 的日志记录器（logger）
- 在 [handlers]  中声明了一个叫 logfile 的日志处理器（handler）
- 在 [formatters] 中声明了一个名为 generic  的格式化（formatter）
- 之后在 [logger_root] 中定义 root 这个日志处理器（logger） 所使用的日志处理器（handler） 是哪个
- 在 [handler_logfile] 中定义了日志处理器（handler）  输出日志的方式、日志文件的切换时间等
- 最后在 [formatter_generic]  中定义了日志的格式，包括日志的产生时间，级别、文件名以及行号等信息。

**有了上述的配置文件以后，我们就可以在主代码中使用 logging.conf 模块的 fileConfig 函数加载日志配置：**

![image-20220902204056398](E:\黑马培训\Python额外补充\assets\image-20220902204056398.png)

### 2.2 logging模块的基础使用：

- 基础使用就是使用默认logger对象的使用。

**1. 设置logging，调用logging.basicConfig()来配置日志信息。** 【由这个来看就知道，它是“通用型的，只能设置一次的”，如果不同部分想使用不同的日志记录，需要使用logger对象（下面的扩展使用）】  

- 可设置的参数：filename日志文件名，filemode打开文件的方式，format日志的输出格式，datefmt日期输出格式，style设置format的类型，level日志记录的最低级别,stream输出流（不与filename并存，filename优先）,handlers日志处理对象（默认是根处理对象）,
- 一般使用的参数：filename日志文件名，format日志的输出格式，level日志记录的最低级别，stream设置输出流    
  - filename是日志文件名，就是一个普通文件名
  - format是日志的输出格式，设置方法下面讲
  - level的设置值为debug等值，使用方法为logging.DEBUG,logging.INFO,logging.WARNING,logging.ERROR,logging.CRITICAL
  - style影响format的类型，它的值有'%','{','$'，默认是'%',不同的style可以识别以下不同的message标识符：%(message)s、{message}、$message

**2. 输出日志信息：**  

- 调试级别日志信息：logging.debug(信息)
- 提示级别信息：logging.info(信息)
- 警告基本信息：loggin.warning(信息)
- 错误级别信息：logging.error(信息)
- 严重级别信息:logging.critical(信息)

### 2.3 以logger对象的方式配置日志文件

**1.导入模块：**

```python
import logging
```

**2.获取logger对象：**

```python
logger = logging.getLogger("AppName")
```

【这里根据不同的名字定义不同的logger对象,默认为root。】

在模块中使用时，官方文档中有一个这样的代码，有点意思：

```python
logger = logging.getLogger(__name__)
```

**3.设置最低日志输出级别：**

```python
logger.setlevel()
```

例如：

```python
logger.setLevel(logging.INFO)
```

**4.创建并绑定handler：**

handler用于处理日志信息的输出方向,可以添加多个handler，代表同时向多个方向输出信息

- 创建handler：

输出方向为文件，使用FileHandler，例如：

```python
logging.FileHandler("test.log")
```

输出方向为流，使用StreamHandler,例如：

```python
logging.StreamHandler(sys.stdout)
```

- 绑定handler，使用addHandler():

例如：

```
logger.addHandler(handler)
```

- 绑定后如果想解绑handler，使用removeHandler():

例如：

```
logger.removeHandler(handler)
```

**5.定义handler的输出格式formatter并绑定到handler上，formatter的设置方法类似上面基础使用中的format：**

- **创建：**

例如：

```python
formatter = logging.Formatter('%(asctime)s %(levelname)s: %(message)s')
```

- **绑定：**

handler.setFormatter(formatter) 

handler.formatter=formatter

**6.将handle绑定到logger对象上。**

```python
logger.addHandler(file_handler)
logger.addHandler(console_handler)
```

**7.输出日志：**

- 调试级别： logger.debug(信息)
- 提示级别： logger.info(信息)
- 警告级别： logger.warn(信息)
- 错误级别：logger.error(信息)
- logger.exception(信息)【与error不同的是，还附带堆栈信息，一般用在发生异常时】
- 严重级别：logger.fatal(信息) 【fatal是critical的别名】logger.critical(信息)

**如果你不想新建handler和formatter，可以使用basicConfig方式**

可以使用basicConfig来配置所有的logger对象的handler和formatter，basisConfig只能给所有的logger赋一样的输出格式，输出位置等

当要注意混杂风险。【basicConfig和handler必须至少存在一个，因为默认的logger对象也是需要初始化的】

**logging模块中还有一个filter**

## 3. logging.basicConfig函数各参数

- filename：指定日志文件名； 
- filemode：和file函数意义相同，指定日志文件的打开模式，'w'或者'a'；
- format：指定输出的格式和内容，format可以输出很多有用的信息，
- datefmt：指定时间格式，同time.strftime()； 
- level：设置日志级别，默认为logging.WARNNING； 
- stream：指定将日志的输出流，可以指定输出到sys.stderr，sys.stdout或者文件，默认输出到sys.stderr，当stream和filename同时指定时，stream被忽略；　

| 属性名称        | 格式                | 说明                                                        |
| --------------- | ------------------- | ----------------------------------------------------------- |
| name            | %(name)s            | 日志的名称                                                  |
| asctime         | %(asctime)s         | 可读时间，默认格式‘2003-07-08 16:49:45,896'，逗号之后是毫秒 |
| filename        | %(filename)s        | 文件名，pathname的一部分                                    |
| pathname        | %(pathname)s        | 文件的全路径名称                                            |
| funcName        | %(funcName)s        | 调用日志多对应的方法名                                      |
| levelname       | %(levelname)s       | 日志的等级                                                  |
| levelno         | %(levelno)s         | 数字化的日志等级                                            |
| lineno          | %(lineno)d          | 被记录日志在源码中的行数                                    |
| module          | %(module)s          | 模块名                                                      |
| msecs           | %(msecs)d           | 时间中的毫秒部分                                            |
| process         | %(process)d         | 进程的ID                                                    |
| processName     | %(processName)s     | 进程的名称                                                  |
| thread          | %(thread)d          | 线程的ID                                                    |
| threadName      | %(threadName)s      | 线程的名称                                                  |
| relativeCreated | %(relativeCreated)d | 日志被创建的相对时间，以毫秒为单位                          |

## 4. logger对象是什么?

**logger是用log4j作为日志输出组件的,info是日志的级别**

使用了logger,.info后,可以将日志信息写入,例登录成功判断后,调用logger.info("2010年12月17日: 20:25用户XXX成功登录")就能将日志信息写入配置文件中

日志输出级别还有好几个

## 5. logging组件

Logging中除了以上的一些设置之外，logging还提供了几个组件（类）供我们实现一些特殊的功能，它们主要是：

![image-20220903143056493](E:\黑马培训\Python额外补充\assets\image-20220903143056493.png)

这些组件共同完成日志的配置和输出：

- Logger需要通过handler将日志信息输出到目标位置，目标位置可以是sys.stdout和文件等。
- 不同的Handler可以将日志输出到不同的位置(不同的日志文件)。
- Logger可以设置多个handler将同一条日志记录输出到不同的位置。
- 每个Handler都可以设置自己的filter从而实现日志过滤，保留实际项目中需要的日志。
- formatter实现同一条日志以不同的格式输出到不同的地方。

**简单的说就是：日志器作为入口，通过设置处理器的方式将日志输出，处理器再通过过滤器和格式器对日志进行相应的处理操作。**

我们来简单的介绍一下这些组件中一些常用的方法：

**1.Logger类**

![image-20220903143148048](E:\黑马培训\Python额外补充\assets\image-20220903143148048.png)

**2.Handler类**

![image-20220903143215295](E:\黑马培训\Python额外补充\assets\image-20220903143215295.png)

**3.Formater类**

**Formater类**主要负责日志的格式化输出的。

可通过**logging.basicConfig**或**logging.Formatter**函数来配置日志输出内容。

如：formatter = logging.Formatter(fmt=None, datefmt=None)。

如果不指明 fmt，将默认使用 '%(message)s' ，如果不指明 datefmt，将默认使用 ISO8601 日期格式。