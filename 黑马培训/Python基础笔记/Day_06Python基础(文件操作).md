## 1. 文件

### 1.1 文件操作

**只能向文件中输入字符串数据**

内存中存放的数据在计算机关机后就会消失，要长久保存数据，要使用硬盘，光盘，U盘等设备。便于管理和检索，引入了文件的概念

程序中变量的存储位置也是在内存中，程序结束后，内存中存储的变量也就释放了

**把数据写入文本文件，数据的类型都是字符串类型，不是字符串类型储存不进去，所以把数据读取出来的时候类型也就是字符串类型的，这时就可以用eval() 函数来处理**

**通过程序对文件进行读取，能大大提高工作效率**

- **概述**

​              文件操作指的是根据路径获取对应的文件对象,然后进行读,写,追加等操作

​              文件对象名 = open('文件路径','模式')

- **模式解释**

​             r: read 只是读

​             rb : read byte 以字节的形式只读

​             w : write 只写的模式打开文本文件，如果已存在则会清空里面的内容。如果不存在，则会创建一个新文件

​             wb : write byte 以字节的形式写入

​             a : append 追加

​             ab : append byte  以字节的方式追加

- **相关的几个单词**

​             file(文件)     read(读)      write(写)     close(关闭)     byte(字节)  line(行)   src(数据源)  dest(目录)

- **关于读的函数解释**

**read(长度)**  如果写长度了,表示一次读取多少,如果不写,表示一次读完,并返回读取到的内容

**readline()** 一次读取一行,采取\n作为结束符

**readlines()** 读取所以的行,行之间是用\n隔开的

**close()** 关闭文件对象,相当于把文件关了,用于:  释放资源

#### 1.1.2 文件操作步骤

1. 打开文件
2. 读写文件
3. 关闭文件

​      f.close()

注意：可以只打开和关闭文件，不进行任何读写

- **open() 打开函数**

在Python中，使用open函数，可以打开一个已经存在的文件，或者创建一个新文件，语法如下：

```python
open(name, mode)
```

name 是要打开的目标文件名的字符串(可以包含文件所在的具体路径)

mode 设置打开文件的模式(访问模式) 只读，写入，追加

![image-20220804205944092](E:\黑马培训\assets\image-20220804205944092.png)

**mode 模式的类型**

![image-20220804210141742](E:\黑马培训\assets\image-20220804210141742.png)

![image-20220805182712513](E:\黑马培训\assets\image-20220805182712513.png)

![image-20220805113153401](E:\黑马培训\assets\image-20220805113153401.png)

**注意**: 在python中只能往文件中写入字符串,其他类型的不能写入

**文件指针的指向, 随着读取在向后移动,所以之后的读取不是在开头起步**

readines()函数返回的是一个列表,列表中的每一个元素,就是文件中的每一行数据

如果已经读到文件末尾后,再使用readlines()函数去读取文件内容,那么这个时候就会返回一个**空列表**

read() 要是读完了就没有任何输出结果

**read() 不指定读取的字符数时，把文件中数据全部读取出来**

**在使用w+ r+ 读写数据时 系统执行写入操作以后，文件指针会移动至末尾，此时再进行读操作，会取不到数据，这时就应使用seek()函数将指针调至开头**

**例子代码:**

```python
with open('D:\\9.txt','r') as f:
    res = f.read(2)
    print(res)

    res = f.readline()
    print(res)
```

**运行结果:**

```python
# ss
# cdd
```

如果已经读到末尾了,**再使用read函数,结果为空字符串**

#### 1.1.3 with打开文件自动关闭

自动进行关闭的关键字： with

语法：

​        with open(文件名字, 访问模式) as 文件变量:

​                #     操作代码.....

​                #     操作代码.....

当with语句块下面的代码执行完毕以后，会自动关闭文件

**代码示例**

```python
with open('1.txt', 'w') as f:
	f.write("hello")
	f.write("world")
	
```

r w a 只用于文本文件的读取

### 1.2 文件编码问题

**语法:** 

- 文件变量 = open('文件路径', '访问模式' , encoding='编码方式')

![image-20220805182057406](E:\黑马培训\assets\image-20220805182057406.png)

![image-20220805182209871](E:\黑马培训\assets\image-20220805182209871.png)

![image-20220805182323835](E:\黑马培训\assets\image-20220805182323835.png)

#### **1.2.1 不同系统下默认的编码方式不同**

window系统

​                    文件变量 = open('操作文件','访问模式',encoding='gbk')

Mac/Linux系统

​                    文件变量 = open('操作文件','访问模式',encoding='utf8')

#### **1.2.2 不同模式下操作文件情况**

![image-20220805182822857](E:\黑马培训\assets\image-20220805182822857.png)

### 1.3 绝对路径与相对路径

##### 1.3.1 简介

![image-20220805153220217](E:\黑马培训\assets\image-20220805153220217.png)

**一个要注意的小问题**

![image-20220805182920500](E:\黑马培训\assets\image-20220805182920500.png)

### 1.4 文件备份案例

**核心思路**:     读取旧文件的内容,写入到新文件中

1. 打开旧文件和创建新文件
2. 读取旧文件内容,写入到新文件中
3. 关闭旧文件和新文件

```python
# 打开旧文件,创建新文件
old_file = open('D:/1.txt', 'r', encoding='gbk')
new_file = open('D:/1[备份].txt', 'w', encoding='gbk')

# 读取旧文件的内容,写入新文件中
content = old_file.read()
new_file.write(content)

#  关闭新旧文件
old_file.close()
new_file.close()
```

#### **1.4.1 用户指定备份文件**

```python
# 提示用户输入要备份的当前目录下的文件名
old_file = input("请输入要备份的文件: ")

# 打开旧文件,创建新文件
old_file = open(old_file, 'r', encoding='gbk')
new_file = open('D:/1[备份].txt', 'w', encoding='gbk')

# 读取旧文件的内容,写入新文件中
content = old_file.read()
new_file.write(content)

#  关闭新旧文件
old_file.close()
new_file.close()
```

#### **1.4.2. 根据旧文件名,生成新的文件名**

![image-20220805165245951](E:\黑马培训\assets\image-20220805165245951.png)

#### **1.4.3. 改造程序能接受所有类型的数据来备份**

**用二进制来**

**改变模式即可:**

![image-20220805165909157](E:\黑马培训\assets\image-20220805165909157.png)

#### **1.4.4. 备份大文件数据**

思路: 边读边写,一点点存储

![image-20220805183057851](E:\黑马培训\assets\image-20220805183057851.png)

以二进制读取的数据,看内容是否为空,必须加写个**b''**

**上面的1024是指的是1024个字节**

![image-20220805205103550](E:\黑马培训\assets\image-20220805205103550.png)

## 2. os

![image-20220805183627675](E:\黑马培训\assets\image-20220805183627675.png)

![image-20220805205045208](E:\黑马培训\assets\image-20220805205045208.png)

**os操作示例**

```python
# 获取python目录下的所有的文件名:os.listdir(路径)
# 对每个文件进行重新命名:os.renamne(旧名称,新名称)
import os
# 1.获取python目录下的所有的文件名:os.listdir(路径)
name_list = os.listdir('./python')
print(name_list)

# 修改当前程序的工作了的目录为python之后, ./ 当前目录下 指的是python
os.chdir('./python')

# 对每个文件进行重新命名  os.rename(旧名称,新名称)
for old_name in name_list:
    os.rename(old_name, '黑马出品-'+old_name)
```

##  3. 异常

![image-20220805210105028](E:\黑马培训\assets\image-20220805210105028.png)

![image-20220805210123914](E:\黑马培训\assets\image-20220805210123914.png)

![image-20220805210908861](E:\黑马培训\assets\image-20220805210908861.png)

**格式**

```python
    try:
        这里写可能会出现的问题代码

    except [异常类型 as 别名]:  # 这里的[]的意思是，可选，即可写可不写,多个异常类型时要写小括号括起来

          这里写的是出现问题后解决问题的方案
    else:
          如果代码没有问题，则执行这里的内容
    finally:
          无论代码是否有问题都会走到这里，一般用于释放资源     
```

**执行顺序:**

1.  先执行try里面的内容,如果出现问题后,无论try里面是否还有代码,都不执行了,直接跳到except中
2.  如果except中写的是出问题后的解决方案,这些解决方案,可以根据你的项目进行自定义
3. 如果是try里面内容没有出现异常的话,则执行else里面的内容
4. 最后,无论try里面是否出错,则都会执行finally里面的内容

**大白话解释**

1. 先执行try,看是否出现问题
2. 出问题走except,不出问题走else
3. 无论是否出问题,都走finally

**快捷键:** crtl + alt + T  代码补齐

**0不能作为除数**

**异常入门格式**

```python
try:
    print(5 / 0)
    f = open('./1.txt', 'r', encoding='utf8')
    # 抓到谁就输出哪个错误
except (FileNotFoundError, ZeroDivisionError) as e:
    # f = open('./1.txt', 'w', encoding='utf8')
    print(f'出问题了{FileNotFoundError}')
    print(f'出问题了{ZeroDivisionError}')
print()

```

![image-20220806151641487](E:\黑马培训\assets\image-20220806151641487.png)

![image-20220806151810823](E:\黑马培训\assets\image-20220806151810823.png)

**完整的异常解释代码**

```python
try:
   # 里面写的是可能出现问题的代码
   print(10 / 0)
except Exception as e:
   # 出问题后的解决方案
    print(f'出问题了,问题原因是: {e}')
else:
    print('try中没有问题输出我')
finally:
    print('最终都会执行的代码')

```

**异常捕获读取文件数据**

```python
try:
    # 可能出问题的代码
    # 1.获取文件对象
    global f
    f = open('1.txt', 'r', encoding='utf8')
except Exception as e:
    print(f"出问题了,原因是{e}")
else:
    # 没有问题,就走出这里
    # 2.读取文件内容
    data = f.read()
    # 3.打印读取出来的内容
    print(data)
    f.close()
finally:
    # 无论如何都会走到这一步,一般用于释放资源
    # 4.释放资源
    print('程序结束了....')
    
    
```

try...except...else  里面的变量对象能通用  而finally里面找不到上面定义的变量

**异常传递性解释**

​        **概述**:

​                所谓的异常传递性指的是 函数会将它的异常传给它的调用者,让调用者来处理,而我们知道最终所有的函数都是在main函数中执行的,所以就意味着所有的异常都会传到main函数中,即我们在main函数中处理了异常,所有的异常也随之都解决了

![image-20220806160632608](E:\黑马培训\assets\image-20220806160632608.png)

![image-20220806160726392](E:\黑马培训\assets\image-20220806160726392.png)

**一些截图资料**

![image-20220807201418714](E:\黑马培训\assets\image-20220807201418714.png)

![image-20220807201436930](E:\黑马培训\assets\image-20220807201436930.png)

![image-20220807201446625](E:\黑马培训\assets\image-20220807201446625.png)

## 4. 包

**包的作用**：

1. 便于对模块(.py)进行分类管理的，
2. 用来区分重名模块(.py)文件的

![image-20220806192919017](E:\黑马培训\assets\image-20220806192919017.png)

![image-20220806192957262](E:\黑马培训\assets\image-20220806192957262.png)

**包的概述**

所谓包就是文件夹，它的本质还是一个模块，因为里面放的是一堆的模块(.py)文件

**自定义包**

就是我们自己创建一个python package即可该包下默认有个_init_.py文件来管理包

**案例**

![image-20220806193633031](E:\黑马培训\assets\image-20220806193633031.png)

**导入包下的模块**

![image-20220806194136165](E:\黑马培训\assets\image-20220806194136165.png)

想使用包下模块里面的函数，得先导入要使用的模块

**测试代码模块**

```python
# import my_package.my_module1 as m
# m.fun1()

#
# import my_package.my_module1
#
# my_package.my_module1.fun1()


# 导入包下的所有模块
from my_package import *

my_module1.fun1()
my_module1.fun2()

print('-'*23)

my_module2.fun2()
my_module2.fun1()

# 上述代码虽然说实现了导入my_package包下所有的模块的功能
# 但是这样写不好，模块少的情况下可以使用，如果模块多了就不行了
# 解决方法： 通过from my_package import *解决，但是要结合__all__一起使用
# 在__init__文件中写
```

**包里的两个模块代码**

my_module1

```python
def fun1():
    print('my_module1 fun1函数')


def fun2():
    print("my_module1 fun2函数")


if __name__ == '__main__':
    fun1()
    fun2()
```

my_module2

```python
def fun1():
    print('my_module2 fun1函数')


def fun2():
    print("my_module2 fun2函数")


if __name__ == '__main__':
    fun1()
    fun2()
```

控制导入模块时选*模式导入，有些模块不需要的解决办法，在_init_文件中添加代码

```python
__all__ = ['my_module1', 'my_module2']
```

**文件创建的工程目录**

![image-20220806201404637](E:\黑马培训\assets\image-20220806201404637.png)

**总结**：

![image-20220806201427792](E:\黑马培训\assets\image-20220806201427792.png)

![image-20220807202148609](E:\黑马培训\assets\image-20220807202148609.png)

![image-20220807202156009](E:\黑马培训\assets\image-20220807202156009.png)

![image-20220807202201975](E:\黑马培训\assets\image-20220807202201975.png)

## 5. json文件

**json数据格式作用:在不同的程序之后进行数据传递**

json数据格式是类似于python中字典或者列表的字符串格式,但是**字符串内的引号必须使用双引号,**且**不能有多余的逗号**,也**不能在里面引用函数**

**json模块**

----**json.dumps(列表或者字典): 将python 中的列表或者字典转换为一个json格式的字符串**

----**json.loads(json字符串)**: 将**json字符串**转换成python中的**列表或者字典**

```python

data = [{'name': '老王', 'age': 16}, {'name': '张三', 'age': 20}]
print(type(data), data)

res = json.dump(data)
print(type(res), res)

# 注意,不能使用str把python中的列表或者字典转换为一个json格式的字符串
# 因为转换的结果不可能符合json格式

# json_str 符合json数据格式
json_str = '[{"name": "老王", "age": 16}, {"name": "张三", "age": 20}]'
print(type(json_str), json_str)

res = json.loads(json_str)
print(type(res), res)

# 注意:传递给loads参数必须符合json格式,否则会出错
# error_str 不符合json格式

error_str = "[{'name': '老王', 'age': 16}, {'name': '张三', 'age': 20}]"

res = json.loadserror_str()
print(type(res), res)

```

**字典.keys():获取字典中所有的键**

**字典.values(): 获取字典中所有的值**

# 实际例子全国疫情地图起飞跳板

用zip打包的元素类型是zip,所以你使用的时候需要去转一下类型,为列表

**在文件里面的数据无论看过去是什么样的类型,他们本质上都是字符串,因为写入数据的时候必须字符串的类型,所以读出来就是字符串类型的**

**疫情数据源数据**:存在里面看过去像子典,但是本质上是字符串,需要用**json.loads(json字符串)**: 将**json字符串**转换成python中的**列表或者字典**

![image-20220808190049911](E:\黑马培训\Python基础笔记\assets\image-20220808190049911.png)

数据源文件当用loads解压出来以后就成了字典文件了

**提取json文件的宗旨就是一层一层的剥皮**

1. ```python
   # 1. 因为是字典文件,所以利用键来取需要的值
   # 2. 先取data["areaTree"] 它所对应的是一个列表,可以看出里面就只有一个元素,类型为字典容器类型
   # 3. 接下来与上面一步合并就是data["areaTree"][0],就会取出如下所示的数据
   ```

   ![image-20220808190743336](E:\黑马培训\Python基础笔记\assets\image-20220808190743336.png)

```python
"""4.这里面又是个字典容器数据类型的数据,所以还能用键值的方式来取,合并上面2步为一体取出键"childern"所对应的值 代码为data["areaTree"][0]["childern"]此时取出的数据为列表容器数据类型,里面存放的元素是字典,这时就能使用for循环来遍历取数据,大体思想就这样,万变不离其中
    
 """ 
```

**数据查看**

![image-20220808191502083](E:\黑马培训\Python基础笔记\assets\image-20220808191502083.png)

![image-20220808191527983](E:\黑马培训\Python基础笔记\assets\image-20220808191527983.png)

..............

**具体代码实现:**

```python
import json

# 读取文件数据
with open('疫情.txt', 'r', encoding='utf8') as f:
    content = f.read() # str
    # 将读取的json数据转换为python中的字典
    data = json.loads(content)

provinces_data = data['areaTree'][0]['children']

# 遍历provinces_data 获取每个省的疫情数据,提取出省的名称和对应的确诊人数
# 定义一个空字典
# 字典[键] = 值: 键不存在,就只是添加键值对: 键存在,就是修改键对应的值

data_dict = {}

for province_data in provinces_data:
    # 获取省的名称
    province_name = province_data['name']
    # 获取省的确诊人数
    confirm_count = province_data['total']['confirm']
    # print(province_name, confirm_count)
    # 向字典中添加键值对
    # data_dict['台湾'] = 15880
    data_dict[province_name] = confirm_count
    data = list(zip(data_dict.keys(), data_dict.values()))
print(data)
```

**下一步可视化的实现**

![image-20220808191713762](E:\黑马培训\Python基础笔记\assets\image-20220808191713762.png)

**添加数据**

![image-20220808191923387](E:\黑马培训\Python基础笔记\assets\image-20220808191923387.png)

**第三个参数想绘制哪里的地图就写哪里的英文名称**

**具体代码**

```python
from pyecharts.charts import Map
# 实例化了map这个类的对象,可以使用这个对象的所有方法
virus_map = Map()
# add函数:给地图添加数据
# 参数1: 解释标签
# 参数2: 数据(一定为列表类型吧)
# 参数3: 地图所属位置
virus_map = virus_map.add("疫情地图", data, "china")
# 生成结果,开始绘制地图
virus_map.render()
```

**优化地图**

![image-20220808193910069](E:\黑马培训\Python基础笔记\assets\image-20220808193910069.png)

**代码:**

```python
from pyecharts import options as opts # 配置模块
# set_global_opts() 函数: 设置全局配置选项
# visualmap_opts = opts.VisualMapOpts(): 视觉映射配置选项
virus_map.set_global_opts(visualmap_opts=opts.VisualMapOpts())

```

**设置全局配置选项**

![image-20220808194755443](E:\黑马培训\Python基础笔记\assets\image-20220808194755443.png)

**添加的代码为**：

```python
is_piecewise=True,
    pieces=[
        {"min": 1, "max": 9, "label": "1-9人", "color": "#CCFFFF"},
        {"min": 10, "max": 99, "label": "10-99人", "color": "#FFFF99"},
        {"min": 100, "max": 499, "label": "99-499人", "color": "#FF9966"},
        {"min": 500, "max": 999, "label": "499-999人", "color": "#FF6666"},
        {"min": 1000, "max": 9999, "label": "1000-9999人", "color": "#CC3333"},
        {"min": 10000, "label": "10000以上", "color": "#990033"}
    ]
```

**完整代码实现：**

```python
"""
学习目标：能够使用 pyecharts 绘制疫情地图
"""

import json

# ① 读取 `疫情.txt` 这个文件中的数据
with open('疫情.txt', 'r', encoding='utf8') as f:
    content = f.read()  # str
    # 将读取的 json 数据转换为 python 中的字典
    data = json.loads(content)

# 字典取值：字典[键]
# print(type(data), data)

# ② 从上面的字典中提取每个省的名称和对应的确诊人数
# print(type(data['areaTree']), data['areaTree'])
# print(type(data['areaTree'][0]), data['areaTree'][0])
# print(type(data['areaTree'][0]['children']), data['areaTree'][0]['children'])
provinces_data = data['areaTree'][0]['children']

# 遍历 provinces_data，获取每个省的疫情数据，提取出省的名称和对应的确诊人数
# 定义一个空字典
# 字典[键] = 值：键不存在，就是添加键值对；键存在，就是修改键对应的值；
data_dict = {}

for province_data in provinces_data:
    # 获取省的名称
    province_name = province_data['name']
    # 获取省的确诊人数
    confirm_count = province_data['total']['confirm']
    # print(province_name, confirm_count)
    # 向字典中添加键值对
    # data_dict['台湾'] = 15880
    data_dict[province_name] = confirm_count

# print(data_dict)

# 字典.keys()：获取字典中所有的键
# 字典.values()：获取字典中所有的值
# print(data_dict.keys())
# print(data_dict.values())

data = list(zip(data_dict.keys(), data_dict.values()))
print(data)

# ③ 利用 pyecharts 来绘制疫情地图
from pyecharts.charts import Map  # 类（暂时理解为一个图纸）
from pyecharts import options as opts  # 配置模块

# 创建一个地图对象（根据图纸造出来的具体的实物）
visual_map = Map()
# 给地图添加数据
visual_map.add('疫情地图', data, 'china')
# 给地图添加视觉映射的配置
# opts.VisualMapOpts()：配置对象
# opts.VisualMapOpts(
#     is_piecewise=True,
#     pieces=[
#         {"min": 1, "max": 9, "label": "1-9人", "color": "#CCFFFF"},
#         {"min": 10, "max": 99, "label": "10-99人", "color": "#FFFF99"},
#         {"min": 100, "max": 499, "label": "99-499人", "color": "#FF9966"},
#         {"min": 500, "max": 999, "label": "499-999人", "color": "#FF6666"},
#         {"min": 1000, "max": 9999, "label": "1000-9999人", "color": "#CC3333"},
#         {"min": 10000, "label": "10000以上", "color": "#990033"}
#     ]
# )
# visual_map.set_global_opts(visualmap_opts=opts.VisualMapOpts())
visual_map.set_global_opts(visualmap_opts=opts.VisualMapOpts(
    is_piecewise=True,
    pieces=[
        {"min": 1, "max": 9, "label": "1-9人", "color": "#CCFFFF"},
        {"min": 10, "max": 99, "label": "10-99人", "color": "#FFFF99"},
        {"min": 100, "max": 499, "label": "99-499人", "color": "#FF9966"},
        {"min": 500, "max": 999, "label": "499-999人", "color": "#FF6666"},
        {"min": 1000, "max": 9999, "label": "1000-9999人", "color": "#CC3333"},
        {"min": 10000, "label": "10000以上", "color": "#990033"}
    ]
))

# 开始绘制地图
visual_map.render()

```

**结果展示：**

![image-20220808195154646](E:\黑马培训\Python基础笔记\assets\image-20220808195154646.png)
