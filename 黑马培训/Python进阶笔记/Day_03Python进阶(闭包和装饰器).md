# 闭包和装饰器

## 1. 本章节常用单词

- **closure** 英 ['kləʊʒə] 闭包, 使终止
- **decorator** 英 ['dekəreɪtə] 装饰器 ,装饰者；
- **wrapped** 英 [ræpt] 包裹；
- **callable** 英 ['kɔːləb(ə)l] 可被调用的

## 2. 闭包

**学习目标**

- 能够知道闭包的构成条件
- 能够知道定义闭包的语法格式

------

### 2.1 问题引入

假设我们现在有下面一个计算两个数之差的函数：

```python
def sub(a, b):
    """计算两个数之差"""
    return a - b
```

**需求：累计 sub 函数的调用次数，在每次调用 sub 函数时，都打印出是第几次调用 sub 函数**

解决方式：可以利用全局变量对 sub 函数的调用次数进行累计。

```python
# 定义全局变量num，初始值为0，保存sub函数的累计调用次数
num = 0

def sub(a, b):
    # 声明使用全局变量
    global num
    num += 1
    print(f'第{num}次调用sub函数')
    return a - b

# 测试进行函数调用
sub(2, 1)
sub(3, 2)
sub(5, 3)
```

执行结果：

```bash
第1次调用sub函数
第2次调用sub函数
第3次调用sub函数
```

> 注意：虽然通过 num 全局变量能够记录 sub 函数的调用次数，但是这个 num 全局变量和 sub 函数是分离的，其他函数也可以修改这个全局变量，如果其他函数不慎修改了这个 num 全局变量，会造成结果的不正确。
>
> 有没有更好的方式？**闭包函数**

### 2.2 闭包的介绍

**闭包的定义:**

在函数嵌套的前提下，内部函数使用了外部函数的变量，并且外部函数返回了内部函数，我们把这个**使用外部函数变量的内部函数称为闭包**。

通过闭包的定义，我们可以得知闭包的形成条件:

1. **在函数嵌套(函数里面再定义函数)的前提下**
2. **内部函数使用了外部函数的变量(还包括外部函数的参数)**
3. **外部函数返回了内部函数**

**闭包实现函数调用计数**：

```python
# 外部函数定义
def func_outer():
    # 记录函数调用的次数
    num = 0

    # 内部函数定义
    def sub(a, b):
        # nonlocal关键字声明使用外部函数的变量
        nonlocal num
        num += 1
        print(f'该函数调用的第{num}次')
        return a - b

    # 外部函数返回内部函数
    return sub


# 调用外部函数创建闭包
func = func_outer()

# 调用闭包函数
func(2, 1)
func(3, 2)
func(5, 3)
```

**运行结果:**

```bash
该函数调用的第1次
该函数调用的第2次
该函数调用的第3次
```

### 2.3 闭包的作用

- 闭包可以保存外部函数内的变量，不会随着外部函数调用完而销毁
- 闭包的好处是将外层函数的变量和内层函数进行了绑定，防止其他函数对变量的修改

### 2.4 闭包自己的理解

**代码示例**

```python
def addWithCount(count):
	def add(num):
       return count + num
    return add

aAddWithCount = addWithCount(200)
bAddWithCount = addWithCount(20)

print(aAddWithCount(30))
print(bAddWithCount(30))
```

定义了两个变量来接收addWithCount函数返回的值,这个函数返回的值是add函数类型,他们是addWithCount函数与不同的参数200,30组成新的**add函数**    执行这个函数的结果是: 230 50

**大白话解释:**    外部函数里面的变量和内部函数进行了一个绑定,接着传参进行内部函数的调用



### 2.4 小结

1. 当返回的内部函数使用了外部函数的变量就形成了闭包

2. 闭包可以对外部函数的变量进行保存，不会随着外部函数调用完而销毁

3. 实现闭包的标准格式：

   ```python
    # 外部函数定义
    def func_outer():
        # 记录函数调用的次数
        num = 0
   
        # 内部函数定义
        def sub(a, b):
            # nonlocal关键字声明使用外部函数的变量
            nonlocal num
            num += 1
            print(f'该函数调用的第{num}次')
            return a - b
   
        # 外部函数返回内部函数
        return sub
   ```

## 3. 装饰器

**学习目标**

- 能够知道定义装饰器的语法格式

------

### 3.1 问题引入

假设我们现在有如下两个函数：

```python
def func1():
    _sum = 0
    for i in range(1000000):
        _sum += i
    return _sum


def func2():
    _res = 0
    for i in range(1000000):
        _res *= i
    return _res
```

**需求：分别计算 func1 和 func2 函数调用时的执行时间**

解决方式：分别在函数执行之前和执行之后，获取相应的时间戳，计算两个时间戳之差，即可获取函数调用时的执行时间

```python
import time

# time.time()：获取代码执行时的时间戳，单位是秒
s1 = time.time()
func1()
s2 = time.time()
print('执行时间：', s2 - s1)


s3 = time.time()
func2()
s4 = time.time()
print('执行时间：', s4 - s3)
```

执行结果：

```bash
执行时间： 0.03532052040100098
执行时间： 0.05095553398132324
```

**思考**：如果有 10 个函数都要计算执行时间，该如何操作？在这种情况下，上面的操作就会略显麻烦，接下来我们来介绍另外一种实现方式：**装饰器**

### 3.2 装饰器的定义

**装饰器**就是**给已有函数增加额外功能的函数，它本质上就是一个闭包函数**。

**装饰器的功能特点:**

1. 不修改已有函数的源代码
2. 不修改已有函数的调用方式
3. 给已有函数增加额外的功能

示例代码：

```python
import time

# 添加计算函数调用时间的功能
def get_time(func):
    def wrapper():
        s1 = time.time()
        func()
        s2 = time.time()
        print('执行时间：', s2 - s1)

    return wrapper

# 利用装饰器装饰函数
func1 = get_time(func1)
func1()

func2 = get_time(func2)
func2()
```

执行结果：

```bash
执行时间： 0.05128788948059082
执行时间： 0.04070568084716797
```

**装饰器的基本雏形**：

```python
def decorator(fn):
    """fn：目标函数(被装饰函数)"""
    def inner():
        # TODO：执行函数之
        fn() # 执行被装饰的函数
        # TODO：执行函数之后
    return inner
```

代码说明：

- 闭包函数有且只有一个参数，必须是函数类型，这样定义的函数才是装饰器
- 写代码要遵循开放封闭原则，它规定已经实现的功能代码不允许被修改，但可以被扩展

### 3.3 装饰器的语法糖写法

如果有多个函数都要添加计算调用执行时间的功能，每次都需要编写`func = get_time(func)`这样代码对已有函数进行装饰，这种做法还是比较麻烦。

Python给提供了一个装饰函数更加简单的写法，那就是语法糖，语法糖的书写格式是: @装饰器名字，通过语法糖的方式也可以完成对已有函数的装饰

示例代码：

```python
import time

# 添加计算函数调用时间的功能
def get_time(func):
    def wrapper():
        s1 = time.time()
        func()
        s2 = time.time()
        print('执行时间：', s2 - s1)

    return wrapper

# 本质：func1 = get_time(func1)
@get_time
def func1():
    _sum = 0
    for i in range(1000000):
        _sum += i
    return _sum


# 本质：func2 = get_time(func2)
@get_time
def func2():
    _res = 0
    for i in range(1000000):
        _res *= i
    return _res

# 调用func1
func1()

# 调用func2
func2()
```

说明：

- 装饰器的执行时间是加载模块时立即执行

执行结果：

```bash
执行时间： 0.04107022285461426
执行时间： 0.04064607620239258
```

### 3.4 小结

- 装饰器本质上就是一个闭包函数，它可以对已有函数进行额外的功能扩展。

- 装饰器的语法格式:

  ```python
  def decorator(fn):
      """fn：目标函数(被装饰函数)"""
      def inner():
          # TODO：执行函数之
          fn() # 执行被装饰的函数
          # TODO：执行函数之后
      return inner
  ```

- 装饰器的语法糖用法：@装饰器名称，同样可以完成对已有函数的装饰操作

## 4. 装饰器进阶

**学习目标**

- 能够编写装饰器装饰带有参数和返回值的函数

------

### 4.1 装饰带有参数的函数

假设现在有如下的一个函数 `my_sum`：

```python
def my_sum(n):
    """计算1-n之间的数字和并打印"""
    result = 0

    # 遍历计算 1-n 之间的数字和
    for i in range(1, n+1):
        result += 1

    # 打印计算的结果
    print('结果为：', result)
```

**思考：**编写一个装饰器函数 `get_time` 来装饰函数，能够计算函数的执行时间，该如何操作：

```python
import time


# 添加计算函数调用时间的功能
def get_time(func):
    def wrapper(num):
        s1 = time.time()
        func(num)
        s2 = time.time()
        print('执行时间：', s2 - s1)

    return wrapper


@get_time
def my_sum(n):
    """计算1-n之间的数字和"""
    result = 0

    # 遍历计算 1-n 之间的数字和
    for i in range(1, n + 1):
        result += i

    # 打印计算的结果
    print('结果为：', result)


# 调用 my_sum 函数
my_sum(1000000)
```

> 总结：如果被装饰的函数带有参数，则装饰器的内部函数需要设置对应的参数

**运行结果:**

```python
结果为： 500000500000
执行时间： 0.03584623336791992
```

### 4.2 装饰带有返回值的函数

假设现在有如下的一个函数 `my_sum`：

```python
def my_sum(n):
    """计算1-n之间的数字和并返回"""
    result = 0

    # 遍历计算 1-n 之间的数字和
    for i in range(1, n+1):
        result += 1

    # 返回计算的结果
    return result
```

**思考：**编写一个装饰器函数 `get_time` 来装饰函数，能够计算函数的执行时间，该如何操作：

```python
import time


# 添加计算函数调用时间的功能
def get_time(func):
    def wrapper(num):
        s1 = time.time()
        # 调用被装饰的函数，并接收其返回值
        result = func(num)
        s2 = time.time()
        print('执行时间：', s2 - s1)
        # 将被装饰函数的返回值再返回
        return result

    return wrapper


@get_time
def my_sum(n):
    """计算1-n之间的数字和并返回"""
    result = 0

    # 遍历计算 1-n 之间的数字和
    for i in range(1, n + 1):
        result += i

    # 返回计算的结果
    return result


# 调用 my_sum
res = my_sum(1000000)
print('返回值为：', res)
```

> 总结：如果被装饰的函数带有返回值，则装饰器的内部函数需要将被装饰函数的返回值再返回。

**运行结果:**

```bash
执行时间： 0.036936044692993164
返回值为： 500000500000
```

### 4.3 小结

- 装饰带有参数的函数
  - 如果被装饰的函数带有参数，则装饰器的内部函数需要设置对应的参数。
- 装饰带有返回值的函数
  - 如果被装饰的函数带有返回值，则装饰器的内部函数需要将被装饰函数的返回值再返回。

## 5. 带有参数的装饰器

**学习目标**

- 能够写出带有参数的装饰器

------

### 5.1 日志信息记录装饰器

假设现在有如下函数：

```python
def login():
    print("用户登录逻辑")

def register():
    print("用户注册逻辑")
```

**需求**：定义一个装饰器 `log`，装饰上面的两个函数，实现调用上面两个函数时进行日志信息记录的功能，将调用信息记录到 `record.log` 文件中。

示例代码：

```python
def log(func):
    def inner():
        with open('record.log', 'a', encoding='utf8') as f:
            f.write(f'{func.__name__}函数被调用了\n')
        # 调用被装饰函数
        func()

    return inner


@log
def login():
    print("用户登录逻辑")


@log
def register():
    print("用户注册逻辑")


# 调用login
login()

print('========== 华丽分割线 ==========')

# 调用register
register()
```

运行结果：

```bash
用户登录逻辑
========== 华丽分割线 ==========
用户注册逻辑
```

![img](E:\黑马培训\Python进阶笔记\assets\装饰器-01.png)

### 5.2 带有参数的装饰器介绍

**在上面代码的基础上，如果还想实现能够指定日志信息记录到哪个文件里，该怎么办？**

- 比如：调用 login 函数时的信息记录到 login.log 中
- 比如：调用 register 函数时的信息记录到 register.log 中

**答：** 需要使用带有参数的装饰器；

- 带有参数的装饰器就是使用装饰器装饰函数的时候可以传入指定参数，**语法格式: @装饰器(参数,...)**
- **实现：**在装饰器外面再包裹上一个函数，让最外面的函数接收参数，返回的是装饰器，因为@符号后面必须是装饰器实例。

示例代码：

```python
def logger(path):
    def log(func):
        def inner():
            with open(path, 'a', encoding='utf8') as f:
                f.write(f'{func.__name__}函数被调用\n')
            func()

        return inner
    # 返回装饰器
    return log


@logger('login.log')
def login():
    print("用户登录逻辑")


@logger('register.log')
def register():
    print("用户注册逻辑")


# 调用login
login()

print('========== 华丽分割线 ==========')

# 调用register
register()
```

运行结果：

```bash
用户登录逻辑
========== 华丽分割线 ==========
用户注册逻辑
```

![img](E:\黑马培训\Python进阶笔记\assets\装饰器-02.png)

### 5.3 小结

- 使用带有参数的装饰器，其实是在装饰器外面又包裹了一个函数，使用该函数接收参数，返回是装饰器，因为 @ 符号需要配合装饰器实例使用

- **装饰器的固定格式**

  ![image-20220816214535509](E:\黑马培训\Python进阶笔记\assets\image-20220816214535509.png)

内部函数可以用到外部函数里面的变量, 外部函数的变量与内部函数做了一个绑定

## 6. 深拷贝vs浅拷贝

**学习目标**：

- 常握深拷贝和浅拷贝的区别

**知识点1：简单和嵌套容器类型数据的内存存储【常握】**

以列表为例：

```python
# 简单列表
my_li1 = [3, 5, 1]
```

![img](E:\黑马培训\Python进阶笔记\assets\简单列表内存.png)

```python
# 嵌套列表
my_li2 = [[2, 4], 6, 7]
```

![img](E:\黑马培训\Python进阶笔记\assets\嵌套列表内存.png)

**知识点2：浅拷贝-简单容器和嵌套容器类型数据【常握】**

```
import copy
copy.copy()：浅拷贝
copy.deepcopy()：深拷贝
```

**可变容器类型：列表**

> **可变容器类型，进行浅拷贝时，只会对第一层数据重新开辟内存，进行拷贝。**

```python
import copy

# 简单列表
my_li1 = [3, 5, 1]

# 浅拷贝
my_li3 = copy.copy(my_li1)

print('id(my_li1)：', id(my_li1))
print('id(my_li3)：', id(my_li3))

print('id(my_li1[0])：', id(my_li1[0]))
print('id(my_li3[0])：', id(my_li3[0]))
```

![img](E:\黑马培训\Python进阶笔记\assets\简单可变容器浅拷贝.png)

```python
# 嵌套列表
my_li2 = [[2, 4], 6, 7]

# 浅拷贝
my_li4 = copy.copy(my_li2)

print('id(my_li2)：', id(my_li2))
print('id(my_li4)：', id(my_li4))

print('id(my_li2[0])：', id(my_li2[0]))
print('id(my_li4[0])：', id(my_li4[0]))
```

![img](E:\黑马培训\Python进阶笔记\assets\嵌套可变容器浅拷贝.png)

**不可变容器类型：元祖**

> **不可变容器类型，进行浅拷贝时，不会重新开辟内存，等同于=号赋值。**

```python
# 简单元祖
my_tuple1 = (2, 3, 5)

# 浅拷贝
my_tuple3 = copy.copy(my_tuple1)

print('id(my_tuple1)：', id(my_tuple1))
print('id(my_tuple3)：', id(my_tuple3))
```

![img](E:\黑马培训\Python进阶笔记\assets\简单不可变容器浅拷贝.png)

```python
# 嵌套元祖
my_tuple2 = ([3, 5], 2, 1)

# 浅拷贝
my_tuple4 = copy.copy(my_tuple2)

print('id(my_tuple2)：', id(my_tuple2))
print('id(my_tuple4)：', id(my_tuple4))
```

![img](E:\黑马培训\Python进阶笔记\assets\嵌套不可变容器浅拷贝.png)

**知识点3：深拷贝-简单类型和嵌套容器类型数据【常握】**

**可变容器类型：列表**

> **可变容器类型，进行深拷贝时，每一层`可变数据`都会重新开辟内存，进行拷贝。**

```python
# 简单列表
my_li1 = [3, 5, 1]

# 深拷贝
my_li3 = copy.deepcopy(my_li1)

print('id(my_li1)：', id(my_li1))
print('id(my_li3)：', id(my_li3))

print('id(my_li1[0])：', id(my_li1[0]))
print('id(my_li3[0])：', id(my_li3[0]))
```

![img](E:\黑马培训\Python进阶笔记\assets\简单可变容器深拷贝.png)

```python
# 嵌套列表
my_li2 = [[2, 4], 6, 7]

# 深拷贝
my_li4 = copy.deepcopy(my_li2)

print('id(my_li2)：', id(my_li2))
print('id(my_li4)：', id(my_li4))

print('id(my_li2[0])：', id(my_li2[0]))
print('id(my_li4[0])：', id(my_li4[0]))
```

![img](E:\黑马培训\Python进阶笔记\assets\嵌套可变容器深拷贝.png)

**不可变容器类型：元祖**

> **简单不可变容器类型，进行深拷贝时，不会重新开辟内存，等同于=号赋值。**

```python
# 简单元祖
my_tuple1 = (2, 3, 5)

# 深拷贝
my_tuple3 = copy.deepcopy(my_tuple1)

print('id(my_tuple1)：', id(my_tuple1))
print('id(my_tuple3)：', id(my_tuple3))
```

![img](E:\黑马培训\Python进阶笔记\assets\简单不可变容器深拷贝.png)

> **嵌套不可变容器类型，进行深拷贝时，如果内层有可变类型时，则会重新开辟内存空间，进行拷贝**

```python
# 嵌套元祖
my_tuple2 = ([3, 5], 2, 1)

# 深拷贝
my_tuple4 = copy.deepcopy(my_tuple2)

print('id(my_tuple2)：', id(my_tuple2))
print('id(my_tuple4)：', id(my_tuple4))

print('id(my_tuple2[0])：', id(my_tuple2[0]))
print('id(my_tuple4[0])：', id(my_tuple4[0]))
```

![img](E:\黑马培训\Python进阶笔记\assets\嵌套不可变容器深拷贝.png)

**知识点4：深拷贝和浅拷贝总结【常握】**

**引用赋值：**

- =号赋值的本质就是引用赋值

**浅拷贝：**

- 可变容器类型，进行浅拷贝时，只会对`第一层数据重新开辟内存`，进行拷贝。
- 不可变容器类型，进行浅拷贝时，不会重新开辟内存，`等同于=号赋值`。

**深拷贝：**

- 可变容器类型，进行深拷贝时，`每一层可变数据都会重新开辟内存`，进行拷贝。
- 不可变容器类型
  - 简单不可变容器类型，进行深拷贝时，不会重新开辟内存，`等同于=号赋值`。
  - 嵌套不可变容器类型，进行深拷贝时
    - `如果内层有可变类型时，则会重新开辟内存空间`，进行拷贝。
    - `如果内层都是不可变类型时`，不会重新开辟内存，`等同于=号赋值`。

## Python进阶day04-知识点总结

1）闭包【理解】

**目标**：能够分析闭包简单的面试题。

构成的3个条件：

* 函数嵌套
* 外部函数返回内部函数
* 内部函数使用了外部函数的变量【包括外部函数的参数】

```python
# 外部函数
def func_outer():
    # 在外部函数中定义了一个变量
    # 注意：在内部函数中可以使用外部函数的变量（包括参数）
    # 注意：这里 count 不要单纯的理解为是一个局部变量，当外部函数调用完成，
    # 这个 count 不会被销毁，它跟内部函数进行了绑定，内部函数可以一直使用。
    count = 0

    # 内部函数
    def sub(a, b):
        # 如果内部函数中要对外部函数的变量进行重新赋值，需要进行 nonlocal 声明
        nonlocal count
        count += 1
        print(f'第{count}次调用sub函数')
        return a - b

    # 外部函数返回了内部函数
    return sub
```

2）装饰器【理解】

**目标**：能够使用装饰器(使用语法糖`@装饰器`)

* 作用：**在不改变原始函数代码和调用方式的情况下，对原始函数进行功能的扩展。**

* 构成

  * 必须满足闭包构成的3个条件
  * **外部函数必须有且只有一个形参，而且形参接收的实参必须是一个函数名(该函数名其实就是要扩展功能的那个函数)**

  ```python
  # 这就是一个装饰器
  # 外部函数
  def get_time(func):
      # 内部函数
      def inner():
          t1 = time.time()
          # 内部函数调用传递的原始函数
          func()
          t2 = time.time()
          print(f'函数执行了{t2-t1}秒')
  
      # 外部函数返回内部函数
      return inner
  ```

* 使用语法糖

  ```python
  # 本质：func1 = get_time(func1)
  @get_time
  def func1():
      _sum = 0
      for i in range(1000000):
          _sum += i
      return _sum
  
  
  # 本质：func2 = get_time(func2)
  @get_time
  def func2():
      _res = 0
      for i in range(1000000):
          _res *= i
      return _res
  ```

* 装饰带有参数的函数

  * 如果被装饰的函数有参数，那么装饰器内部函数也需要对应数量的参数，而且内部函数调用被装饰函数时，需要把接收的参数传递给被装饰函数。

* 装饰带有返回值的函数

  * 如果被装饰的函数带有返回值，装饰器的内部函数调用被装饰的函数时，需要接收被装饰函数的返回值，并且最终将被装饰函数的返回值作为自己的返回值返回。

* 通用装饰器模板代码

  ```python
  # 定义一个装饰器：通用装饰器
  def get_time(func):
      # 注意：wrapper小括号里面的 *args 和 **kwargs 代表是两种不定长形参
      def wrapper(*args, **kwargs):
          # TODO：扩展功能的代码...
          # 注意：调用 func 时，小括号里面的 *args 和 **kwargs 代表的是拆解传实参
          result = func(*args, **kwargs)
          # TODO：扩展功能的代码...
          return result
  
      return wrapper
  ```

* 带参数的装饰器

  ```python
  # 现在是给原来的装饰器代码又包裹了一层函数，让最外层的这个函数接收参数给里面的装饰器函数进行使用
  def logger(filename):
      # 这一块代码是我们之前的装饰器代码
      def log(func):
          def wrapper(*args, **kwargs):
              result = func(*args, **kwargs)
              # 增加的功能扩展代码
              with open(f'./{filename}', 'a', encoding='utf8') as f:
                  f.write(f'{func.__name__}函数被调用了\n')
              return result
  
          return wrapper
  
      # 最外部这个函数要把装饰器返回
      return log
  
  
  # log = logger('aaa.log')
  @logger('aaa.log')
  def login():
      print("用户登录逻辑")
  
  
  # log = logger('bbb.log')
  @logger('bbb.log')
  def register():
      print("用户注册逻辑")
  ```

3）深拷贝和浅拷贝【理解】

​	**引用赋值：**

- =号赋值的本质就是引用赋值

**浅拷贝：**

- 可变容器类型，进行浅拷贝时，只会对`第一层数据重新开辟内存`，进行拷贝。
- 不可变容器类型，进行浅拷贝时，不会重新开辟内存，`等同于=号赋值`。

**深拷贝：**

- 可变容器类型，进行深拷贝时，`每一层可变数据都会重新开辟内存`，进行拷贝。
- 不可变容器类型
  - 简单不可变容器类型，进行深拷贝时，不会重新开辟内存，`等同于=号赋值`。
  - 嵌套不可变容器类型，进行深拷贝时
    - `如果内层有可变类型时，则会重新开辟内存空间`，进行拷贝。
    - `如果内层都是不可变类型时`，不会重新开辟内存，`等同于=号赋值`。