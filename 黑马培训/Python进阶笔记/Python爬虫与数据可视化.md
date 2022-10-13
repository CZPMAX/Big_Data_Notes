# Python爬虫与数据可视化

# 1. 正则表达式

**正则匹配到的内容一定得用到group， 即把上面拿去分组， 这个只能用在match，search里面， 还有一个findall匹配到的内容是返回一个列表，**

**使用正则就是在html语言里面,有些html标签不变,具体内容用正则来匹配,因为想得到的内容位置都一样但是内容不一样**

**(预定字符集)o{n,m}(量词)   意思就是去目标字符串中匹配，先从m开始尝试，看看有没有连续的m个o  依次递减的尝试，最少匹配连续n个0，只要匹配成功就不用往下再匹配了**

**学习目标**：

- 熟悉正则表达式的基本语法
- 熟悉re正则模块中常用方法的使用
- 常握使用正则提取指定分组的数据
- 理解正则中的贪婪模式和非贪婪模式

字符数量少的时候就直接写它们进来,供程序来匹配

## 知识点1：正则表达式功能简介【熟悉】

正则表达式(regular expression)描述了一种字符串匹配的模式，可以用来检查一个串是否含有某种子串、将匹配的子串做替换或者从某个串中取出符合某个条件的子串等。

模式：**一种特定的字符串模式，这个模式是通过一些特殊的符号组成的。**

正则表达式的功能：

- 数据验证（表单验证、如手机、邮箱、IP地址）
- **数据检索（数据检索、数据抓取）**
- 数据隐藏（`135****6235` 王先生）
- 数据过滤（论坛敏感关键词过滤）

> 正则表达式并不是Python所特有的，在Java、PHP、Go以及JavaScript等语言中都是支持正则表达式的。

## 知识点2：正则表达式-匹配单个字符【熟悉】

正则表达式在线练习网址：https://tool.oschina.net/regex/

**匹配单个字符：**

| 正则语法 | 描述                                     |
| -------- | ---------------------------------------- |
| .        | 匹配任意1个字符（除了\n）                |
| []       | 匹配[ ]中列举的字符：[a-z]               |
| \d       | 匹配数字，即0-9                          |
| \D       | 匹配非数字，即不是数字                   |
| \s       | 匹配空白，即 空格，tab键                 |
| \S       | 匹配非空白                               |
| \w       | 匹配非特殊字符，即a-z、A-Z、0-9、_、汉字 |
| \W       | 匹配特殊字符，即非字母、非数字、非汉字   |

练习：

- 在字符串 "abcd123" 中匹配 a : `a`
- 在字符串 "abcd123" 中匹配任意一个字符： `.`
- 在字符串 "abcd123" 中匹配 b 或 d ：`[bd]`
- 在字符串 "abcd123" 中匹配数字: `\d`
- 在字符串 "abcd123" 中匹配非数字内容： `\D`
- 在字符串 "abcd 123" 中匹配空白字符串： `\s`
- 在字符串 "abcd 123" 中匹配非空白字符串： `\S`
- 在字符串 "abcd_123" 中匹配非特殊字符： `\w`
- 在字符串 "abcd&%123" 中匹配特殊字符： `\W`

## 知识点3：正则表达式-匹配多个字符【熟悉】

**匹配多个字符：**

连续匹配

| 正则语法 | 描述                                                     |
| -------- | -------------------------------------------------------- |
| *        | 匹配前一个字符出现0次或者无限次，即可有可无：\d*         |
| +        | 匹配前一个字符出现1次或者无限次，即至少有1次：\d+        |
| ?        | 匹配前一个字符出现1次或者0次，即要么有1次，要么没有：\d? |
| {m}      | 匹配前一个字符出现m次：\d{3}                             |
| {m,n}    | 匹配前一个字符出现从m到n次：\d{2,5}                      |

练习：

- 在字符串 "传智播客_python_666" 中匹配非数字内容： `\D*` 返回： `传智播客_python_`
- 在字符串 "传智播客_python_666" 中匹配数字： `\d+` 返回： `666`
- 在字符串 "传智播客_python_666" 中匹配y或py： `[p]?y` 返回： `py`
- 在字符串 "传智播客_python_666" 中匹配2个数字： `\d{2}` 返回： `66`
- 在字符串 "传智播客_python_666" 中匹配英文字母出现1-3次： `[a-z]{1,3}` 返回：`pyt` `hon`

## 知识点4：正则表达式-匹配开头结尾vs其他匹配【熟悉】

**匹配开头和结尾：**

| 正则语法 | 描述                |
| -------- | ------------------- |
| ^        | 匹配字符串开头：^a  |
| $        | 匹配字符串结尾 ：b$ |

练习：

- 在字符串 "abc_python_666" 中匹配以a开头： `^a` 返回： `a`
- 在字符串 "abc_python_666" 中匹配以数字结尾：`\d$` 返回： `6`

**其他匹配：**

| 正则语法      | 描述                           |
| ------------- | ------------------------------ |
| `[^指定字符]` | 匹配除了指定字符以外的所有字符 |
| \|            | 匹配左右任意一个表达式         |

.*  匹配到的可以没有,也可以有很多,在不确定要匹配多少个时才能用这个

练习：

- 在字符串 "abc_python_666" 中匹配除了数字以外的字符： `[^\d]+` 返回： `abc_python_`
- 在字符串 "abc-python-666" 中匹配数字和特殊字符：`\d+|\W+` 返回：`-` `-` `666`

## 知识点5：re模块-match方法的使用【熟悉】

```python
# regex
import re
```

**函数语法格式：**

```python
match函数：re.match(pattern, string, flags=0)
参数：
    pattern：匹配的正则表达式
    string：要匹配的字符串
    flags：标志位(暂时忽略，后面会介绍)

功能：尝试从字符串起始位置匹配一个正则表达式
    1）如果不能从起始位置匹配成功，则返回None；
    2）如果能从起始位置匹配成功，则返回一个匹配的对象
```

**match表示必须在从头匹配就成功,即字符串第一位开始,否则匹配不成功!!!!**

**示例代码：**

```python
my_str = 'abc_123_DFG_456'

# 匹配字符串bc(注：从头开始)
res = re.match(r'bc', my_str)
print(type(res), res) # 注意：不能从起始位置匹配成功，则返回None


# 匹配字符串abc(注：从头开始)
res = re.match(r'abc', my_str)
print(type(res), res) # 注意：能从起始位置匹配成功，则返回一个匹配的对象，Match类实例对象

# Match对象.group()：获取正则表达式匹配到的内容
print(res.group())
```

## 知识点6：re模块-search方法的使用【熟悉】

**函数语法格式：**

```python
search函数：re.search(pattern, string, flags=0)
参数：
    pattern：匹配的正则表达式
    string：要匹配的字符串
    flags：标志位(暂时忽略，后面会介绍)

功能：根据正则表达式扫描整个字符串，并返回第一个成功的匹配
    1）如果不能匹配成功，则返回None；
    2）如果能匹配成功，则返回一个匹配对象
```

**示例代码：**

```python
my_str = 'abc_123_DFG_456'

# 匹配连续的3位数字
# \d：匹配一位数字
res = re.search(r'\d{3}', my_str)
print(type(res), res) # 注意：匹配成功，则返回一个匹配的对象，Match类实例对象

# 获取正则表达式匹配到的内容
print(res.group())
```

## 知识点7：re模块-findall方法的使用【熟悉】

**函数语法格式：**

```python
findall函数：re.findall(pattern, string, flags=0)
参数：
    pattern：匹配的正则表达式
    string：要匹配的字符串
    flags：标志位(暂时忽略，后面会介绍)

功能：根据正则表达式扫描整个字符串，并返回所有能成功匹配的子串
    1）如果不能匹配成功，则返回一个空列表；
    2）如果能匹配成功，则返回包含所有匹配子串的列表
```

**示例代码：**

```python
my_str = 'abc_123_DFG_456'

# 匹配字符串中的所有连续的3位数字
# raw string：原生字符串
res = re.findall(r'\d{3}', my_str)
print(type(res), res)
```

![image-20220823194238679](E:\黑马培训\Python进阶笔记\assets\image-20220823194238679.png)

## 知识点8：re模块-正则分组操作【常握】

在使用正则表达式进行匹配操作时，可以将正则表达式进行分组，并在匹配之后获取相应分组的匹配数据。

**正则匹配分组语法**：

| 正则语法               | 描述                     |
| ---------------------- | ------------------------ |
| `(正则表达式)`         | 将括号中字符作为一个分组 |
| `(?P<name>正则表达式)` | 分组起别名               |

**示例1：正则匹配分组操作**

> 语法：(正则表达式)

```python
import re

my_str = '13155667788'

# 需求：使用正则提取出手机号的前3位、中间4位以及后 4 位数据

res = re.match(r'(\d{3})(\d{4})(\d{4})', my_str)
print(type(res), res)

# 获取整个正则表达式匹配的内容
print(res.group())

# 获取正则表达式指定分组匹配到的内容
# Match对象.group(组号)
print(res.group(1)) # 131
print(res.group(2)) # 5566
print(res.group(3)) # 7788
```

**示例2：给正则分组起别名**

> 语法：(?P<分组别名>正则表达式)

```python
my_str = '<div><a href="https://www.itcast.cn" target="_blank">传智播客</a><p>Python</p></div>'

# 需求：使用正则提取出 my_str 字符串中的 `传智播客` 文本

res = re.search(r'<a.*>(?P<text>.*)</a>', my_str)
print(type(res), res)

# 获取整个正则表达式匹配到的内容
print(res.group())

# 获取指定分组匹配到的内容
print(res.group(1)) # 传智播客

# 根据分组的名称，获取指定分组匹配到的内容
# Match对象.group(分组名称)
print(res.group('text')) # 传智播客
```

## 知识点9：re模块-引用正则分组【熟悉】

在正则表达式中，放在圆括号`()`中表示的是一个组，当使用`()`定义了一个正则表达式组后，正则引擎会将匹配到的组按照顺序编号，存入缓存，这样的话我们想在后面对已经匹配过的内容进行引用时，引用分组的方式如下：

| 正则语法    | 描述                                                         |
| ----------- | ------------------------------------------------------------ |
| `\num`      | 引用正则中第 num 个分组匹配到的字符串  例如：`\1`表示第一个分组，`\2`表示第二个分组...... |
| `(?P=name)` | 引用正则中别名为 name 分组匹配到的字符串                     |

**分组引用示例代码：**

```python
# 需求：写一个正则表达式，匹配字符串形如：'xxx xxx xxx'
# 注意：xxx可以是任意多位的数字，但是这三个xxx必须一致，比如：'123 123 123', '6666 6666 6666'

import re

my_str = '123 123 123'

# 根据分组序号引用分组
res = re.match(r'(\d+)\s\1\s\1', my_str)
print(type(res), res)
# 获取整个正则表达式匹配到的内容
print(res.group())

print('=' * 20)

# 根据分组名称引用分组
res = re.match(r'(?P<num>\d+)\s(?P=num)\s(?P=num)', my_str)
print(type(res), res)
# 获取整个正则表达式匹配到的内容
print(res.group())
```

## 知识点10：re模块-正则表示式修饰符【了解】

**正则表达式可以包含一些可选标志修饰符来控制匹配的模式。**

修饰符被指定为一个可选的标志。多个标志可以通过按位 OR(|) 它们来指定。如 re.I | re.M 被设置成 I 和 M 标志：

| 修饰符   | 描述                                                     |
| :------- | :------------------------------------------------------- |
| re.I     | 匹配时不区分大小写                                       |
| re.M     | 多行匹配，影响 ^ 和 $                                    |
| **re.S** | 使 . 匹配包括换行在内的所有字符                          |
| re.L     | 做本地化(locale-aware)匹配                               |
| re.U     | 根据unicode字符集解析字符,这个标志影响\w, \W, \b, \W     |
| re.X     | 该标志通过给予更灵活的格式以便将正则表达式写得更易于理解 |

**示例代码：**

```python
import re

my_str = 'aB'

# re.I：匹配时不区分大小写
res = re.match(r'ab', my_str, flags=re.I)
print(bool(res)) # 非None就True，None就是False


print('=' * 20)
# 开启多行模式
# ^　可以匹配字符串开头（字符串的开始位置），也可以匹配行的开头（即换行符\n之后的位置）
# $　可以匹配字符串结尾（字符串的结束位置）, 也可以匹配行的结尾（即换行符\n之前的位置）

# 关闭多行模式
# ^　只能匹配字符串开头
# $　只能匹配字符串结尾
my_str = 'aabb\nbbcc'

res = re.findall(r'^[a-z]{4}$', my_str, flags=re.M)
print(res)
print(bool(res))
print('=' * 20)

my_str = '\nabc'
# re.S：影响 . 符号，设置之后，.符号就能匹配\n了
res = re.match(r'.', my_str, flags=re.S)
print(bool(res))
```

## 知识点11：贪婪模式和非贪婪模式【常握】

**贪婪模式**：在整个表达式匹配成功的前提下，尽可能多的匹配

**非贪婪模式**：在整个表达式匹配成功的前提下，尽可能少的匹配

> 正则中的量词包括：{m,n}、?、*和+，这些量词默认都是贪婪模式的匹配，可以在这些量词后面加?将其变为非贪婪模式。
>
> 例如：\d{2,5}

**示例代码：**

```python
import re

my_str = '<div>test1</div><div>test2</div>'

# 贪婪模式：在整个表达式匹配成功的前提下，尽可能多的匹配
re_obj = re.match(r'<div>.*</div>', my_str)
print(re_obj)
print(re_obj.group()) # 获取整个正则表达式匹配的内容

# 非贪婪模式：在整个表达式匹配成功的前提下，尽可能少的匹配
re_obj = re.match(r'<div>.*?</div>', my_str)
print(re_obj)
print(re_obj.group()) # 获取整个正则表达式匹配的内容
```

![image-20220823193710083](E:\黑马培训\Python进阶笔记\assets\image-20220823193710083.png)

![image-20220823193731587](E:\黑马培训\Python进阶笔记\assets\image-20220823193731587.png)

![image-20220823193746440](E:\黑马培训\Python进阶笔记\assets\image-20220823193746440.png)

# 2. 爬虫程序

**学习目标**：

- 熟悉 FastAPI 提取URL地址数据
- 熟悉爬虫的概念和作用
- 掌握爬虫爬取图片数据和GDP数据

## 1. 浏览器的请求过程-渲染【熟悉】

**HTTP单次请求过程：**

![image-20220823233610864](E:\黑马培训\Python进阶笔记\assets\image-20220823233610864.png)

**浏览器请求过程：**

1. 浏览器通过域名解析服务器（DNS）获取IP地址
2. 浏览器先向IP发起请求，并获取响应
3. 在返回的响应内容（html）中，可能会带有css、js、图片等url地址，浏览器按照响应内容中的顺序依次发送其他的请求，并获取相应的响应
4. 浏览器每获取一个响应就对展示出的结果进行添加（加载），js、css等内容可能会修改页面的内容，js也可以重新发送请求，获取响应
5. 从获取第一个响应并在浏览器中展示，直到最终获取全部响应，并在展示的结果中添加内容或修改，**这个过程叫做浏览器的渲染

## 2. FastAPI-返回图片数据【掌握】

![image-20220823193829857](E:\黑马培训\Python进阶笔记\assets\image-20220823193829857.png)

浏览器会向index.html发送请求,请求到页面后,发现页面代码里面还有资源路径,浏览器就会向这些资源路径发请求,要让这些资源路径起作用,得在fastapi里面写对应资源路径的处理函数

![image-20220823144747925](E:\黑马培训\Python进阶笔记\assets\image-20220823144747925.png)

**示例代码**：

```python
# 导入 FastAPI 类
from fastapi import FastAPI
# 导入 uvicorn
import uvicorn
# 导入 Response 响应类
from fastapi import Response

# 创建 FastAPI 对象
app = FastAPI()

# http://127.0.0.1:8080/
# 定义业务处理函数并设置对应的 URL 地址
# get：表示请求方式
# /index.html：表示请求的 URL 地址
@app.get('/index.html')
def index():
    with open('./sources/html/index.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='html')


@app.get('/gdp.html')
def gdp():
    with open('./sources/html/gdp.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='html')


@app.get('/images/0.jpg')
def get_images_0():
    with open('./sources/images/0.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/1.jpg')
def get_images_1():
    with open('./sources/images/1.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/2.jpg')
def get_images_2():
    with open('./sources/images/2.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/3.jpg')
def get_images_3():
    with open('./sources/images/3.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/4.jpg')
def get_images_4():
    with open('./sources/images/4.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/5.jpg')
def get_images_5():
    with open('./sources/images/5.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


@app.get('/images/6.jpg')
def get_images_6():
    with open('./sources/images/6.jpg', 'rb') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='jpg')


if __name__ == '__main__':
    # 启动 Web 服务器
    uvicorn.run(app, host='127.0.0.1', port=8080)
```

## 3. FastAPI-提取URL路径数据【掌握】

问题：

```shell
1. 对于上面的代码，假如我们有1000张图片要返回，该怎么操作？
/images/0.jpg
/images/1.jpg
/images/2.jpg
/images/3.jpg
```

通过观察，我们可以发现图片的资源路径和返回图片的代码都是相似的，只要能从 URL 路径中获得图片的名称，上面的返回图片处理函数就能融合成一个，在函数中只要根据图片名称读取对应的图片内容返回就可以了。

```shell
2. 如何从 URL 地址中提取数据？
```

FastAPI 可以从 URL 地址中提取数据，并将提取的数据传递给对应的处理函数，格式如下：

```python
from fastapi import FastAPI

app = FastAPI()


# @app.get('/.../{参数名}')
# /items/111
# /items/122
@app.get("/items/{item_id}")
def read_item(item_id): # FastAPI 提取了数据之后，会将提取的数据传递给下面处理函数的对应形参
    pass
```

![image-20220823144932610](E:\黑马培训\Python进阶笔记\assets\image-20220823144932610.png)

想要提取资源路径中的那一块,就把那一块再用{}括起来,在括号内可以起它的别名,下面对应的处理函数里面的形参的名称得与上面起的别名名字一致

**动态返回图片数据示例代码**：

```python
# 导入 FastAPI 类
from fastapi import FastAPI, Path
# 导入 uvicorn
import uvicorn
# 导入 Response 响应类
from fastapi import Response

# 创建 FastAPI 对象
app = FastAPI()


# 定义业务处理函数并设置对应的 URL 地址
# get：表示请求方式
# /index.html：表示请求的 URL 地址
@app.get('/index.html')
def index():
    with open('./sources/html/index.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='html')


@app.get('/gdp.html')
def gdp():
    with open('./sources/html/gdp.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回响应对象
    return Response(content, media_type='html')


# 从 URL 地址中提取图片名称
@app.get('/images/{image_name}')
def get_image(image_name):
    print('image name:', image_name)

    # 拼接图片路径
    file_path = './sources/images/' + image_name

    # 判断图片是否存在
    if not os.path.isfile(file_path):
        return 'Not Found!'

    # 读取图片内容
    with open(file_path, 'rb') as f:
        content = f.read()

    # 返回图片响应
    return Response(content, media_type='jpg')


if __name__ == '__main__':
    # 启动 Web 服务器
    uvicorn.run(app, host='127.0.0.1', port=8080)
```

## 4. 爬虫的概念及工作过程【了解】

问题：

```shell
1. 什么是爬虫？ 爬虫有什么作用？
2. 爬虫工作过程是什么？
```

 **什么是爬虫？爬虫有什么作用？**

网络爬虫（又被称为网页蜘蛛，网络机器人）就是模拟浏览器发送网络请求，接收请求响应，一种按照一定的规则，自动地抓取互联网信息的程序。

- 原则上，只要是浏览器(客户端)能做的事情，爬虫都能做。
- 爬虫只能获取到浏览器(客户端)所展示出来的数据。

爬虫的作用：**数据分析中，进行数据采集的一种方式**。

**爬虫工作过程是什么？**

![image-20220823193645600](E:\黑马培训\Python进阶笔记\assets\image-20220823193645600.png)

1. 向起始的 url 地址发送请求，并获取响应数据
2. 对响应内容进行提取
3. 如果提取 url，则继续发送请求获取响应
4. 如果提取数据，将数据进行保存

## 5. requests 模块的功能使用【掌握】

问题：

```
1. requests模块是什么？有什么作用？
2. 如果使用 requests 模块？
```

**requests模块是什么？有什么作用？**

requests 是用 python 语言编写的一个开源的HTTP库，可以通过 requests 库编写 python 代码发送网络请求，其简单易用，是编写爬虫程序时必知必会的一个模块。

中文文档： https://docs.python-requests.org/zh_CN/latest/index.html

**如果使用 requests 模块？**

1）安装

```bash
pip install requests
或者
pip install requests -i https://pypi.tuna.tsinghua.edu.cn/simple
```

2）示例：请求百度，并获取响应内容

```python
"""
requests模块基本使用
学习目标：能够使用 requests 模块请求URL地址并获取响应内容
"""

# 导入 requests 包
import requests

# 准备 url 地址
url = 'https://www.baidu.com'

# 使用 requests 发送 GET 请求
# 响应对象
response = requests.get(url)

# 获取响应的内容
# response.content：bytes，服务器返回的原始响应内容
# bytes -> str：bytes数据.decode('解码方式')
print(response.content.decode())
```

## 6. 爬虫示例-爬取单张图片数据【掌握】

需求：使用 requests 编写爬虫程序，爬取 http://127.0.0.1:8080/images/0.jpg 图片数据并保存。

**示例代码**：

```python
"""
爬虫示例-爬虫单张图片数据
学习目标：能够使用 requests 爬取单张图片数据并保存
"""

import requests

# 准备请求的 URL 地址
url = 'http://127.0.0.1:8080/images/0.jpg'

# 发送请求
response = requests.get(url)

# 获取响应图片内容
image_content = response.content

# 将响应图片内容保存成本地图片文件
with open('./spider/0.jpg', 'wb') as f:
    f.write(image_content)
```

## 7. 爬虫示例-爬取多张图片数据【掌握】

需求：访问 http://127.0.0.1:8080/index.html 网址，获取页面上的所有图片保存到本地。

**示例代码**：

```python
"""
爬虫示例-爬虫多张图片数据
学习目标：能够使用 requests 爬取多张图片数据并保存
"""

import requests
import re

# 思路
# ① 先请求 http://127.0.0.1:8080/index.html，获取响应内容
# ② 从上一步的响应内容中提取所有图片的地址
# ③ 遍历每一个图片地址，向每个图片地址发送请求，并将响应的内容保存成图片文件


def get_images():
    # ① 先请求 http://127.0.0.1:8080/index.html，获取响应内容
    url = 'http://127.0.0.1:8080/index.html'
    # 发送请求
    response = requests.get(url)

    # 获取响应的内容
    html_str = response.content.decode()
    print(html_str)

    # ② 从上一步的响应内容中提取所有图片的地址
    image_url_list = re.findall(r'<img src="(.*?)"', html_str)
    print(image_url_list)

    # ③ 遍历每一个图片地址，向每个图片地址发送请求，并将响应的内容保存成图片文件
    base_url = 'http://127.0.0.1:8080'

    for i, image_url in enumerate(image_url_list):
        # 拼接完整的图片地址
        image_url = base_url + image_url[1:]
        # 发送请求
        image_response = requests.get(image_url)

        # 将响应内容保存成本地图片
        with open(f'./spider/{i}.jpg', 'wb') as f:
            f.write(image_response.content)

    print('保存图片完毕!!!')


if __name__ == '__main__':
    get_images()
```

## 8. 爬虫示例-爬取GDP数据【掌握】

需求：访问 http://127.0.0.1:8080/gdp.html 网址，提取页面上的国家和GDP数据并保存到本地。

**示例代码**：

```python
"""
爬虫示例-爬取GDP数据
学习目标：能够使用 requests 爬取GDP数据并保存
"""

# 思路
# ① 先请求 http://127.0.0.1:8080/index.html，获取响应内容
# ② 使用正则提取页面上的国家和GDP数据
# ③ 将提取的 GDP 保存到文件 gdp.txt 中

import requests
import re


def get_gdp_data():
    # ① 先请求 http://127.0.0.1:8080/gdp.html，获取响应内容
    url = 'http://127.0.0.1:8080/gdp.html'
    # 发送请求
    response = requests.get(url)

    # 获取响应的内容
    html_str = response.content.decode()
    print(html_str)

    # ② 使用正则提取页面上的国家和GDP数据
    gdp_data = re.findall('<a href=""><font>(.*?)</font></a>.*?<font>￥(.*?)亿元</font>', html_str, flags=re.S)
    print(gdp_data)

    # ③ 将提取的 GDP 保存到文件 gdp.txt 中
    with open('./spider/gdp.txt', 'w', encoding='utf8') as f:
        f.write(str(gdp_data))

    print('保存GDP数据完毕!!!')


if __name__ == '__main__':
    get_gdp_data()
```

![image-20220823193932697](E:\黑马培训\Python进阶笔记\assets\image-20220823193932697.png)

上面红色圈里面会有\n  而.是匹配不到\n的 所以要加一个正则修饰符 flags = re.S  在这里们要尽少的去匹配，即要使用非贪婪模式来  还需要用分组去获取 我们需要的数据

## 9. 爬虫示例-多任务版爬虫【了解】

真正的工作环境中，我们爬取的数据可能非常的多，如果还是使用单任务实现，这时候就会让我们爬取数据的时间很长，那么显然使用多任务可以大大提升我们爬取数据的效率。

需求：使用多线程实现分别爬取图片数据和 GDP 数据。

**示例代码**：

```python
"""
爬虫示例-爬虫多任务版
学习目标：能够使用多线程的方式执行多任务爬虫
"""

import requests
import re
import threading


def get_images():
    # ① 先请求 http://127.0.0.1:8080/index.html，获取响应内容
    url = 'http://127.0.0.1:8080/index.html'
    # 发送请求
    response = requests.get(url)

    # 获取响应的内容
    html_str = response.content.decode()
    # print(html_str)

    # ② 从上一步的响应内容中提取所有图片的地址
    image_url_list = re.findall(r'<img src="(.*?)"', html_str)
    # print(image_url_list)

    # ③ 遍历每一个图片地址，向每个图片地址发送请求，并将响应的内容保存成图片文件
    base_url = 'http://127.0.0.1:8080'

    for i, image_url in enumerate(image_url_list):
        # 拼接完整的图片地址
        image_url = base_url + image_url[1:]
        # 发送请求
        image_response = requests.get(image_url)

        # 将响应内容保存成本地图片
        with open(f'./spider/{i}.jpg', 'wb') as f:
            f.write(image_response.content)

    print('保存图片完毕!!!')


def get_gdp_data():
    # ① 先请求 http://127.0.0.1:8080/gdp.html，获取响应内容
    url = 'http://127.0.0.1:8080/gdp.html'
    # 发送请求
    response = requests.get(url)

    # 获取响应的内容
    html_str = response.content.decode()
    # print(html_str)

    # ② 使用正则提取页面上的国家和GDP数据
    gdp_data = re.findall('<a href=""><font>(.*?)</font></a>.*?<font>￥(.*?)亿元</font>', html_str, flags=re.S)
    # print(gdp_data)

    # ③ 将提取的 GDP 保存到文件 gdp.txt 中
    with open('./spider/gdp.txt', 'w', encoding='utf8') as f:
        f.write(str(gdp_data))

    print('保存GDP数据完毕!!!')


if __name__ == '__main__':
    # 创建两个线程，分别执行爬取图片和爬取GDP数据的任务
    image_thread = threading.Thread(target=get_images)
    gdp_thread = threading.Thread(target=get_gdp_data)

    # 启动线程
    image_thread.start()
    gdp_thread.start()
```

# 3. 数据可视化

**学习目标**：

- 熟悉pyecharts模块绘制饼图

## 10. pyecharts 模块简介和安装【了解】

**问题**：

```python
1. 什么是 pyecharts？有什么作用？
```

**echarts** 是个由百度开源的 javascript  语言实现数据可视化框架，凭借着良好的交互性，精巧的图表设计，得到了众多开发者的认可。pyecharts 是 echarts 的 python  版本的实现，在 python 中，可以使用 pyecharts 进行数据可视化操作。

官网地址：https://pyecharts.org/#/

**特性**：

1. 简洁的API设计，使用如丝滑般流畅，支持链式调用
2. 囊括了**30+**种常见图表，应有尽有
3. 支持主流**Notebook** 环境，**Jupyter** **Notebook** 和**JupyterLab**
4. 可轻松集成至Flask, Django等主流Web框架
5. 高度灵活的配置项，可轻松搭配出精美的图表
6. 详细的文档和示例，帮助开发者更快的上手项目
7. 多达400+地图文件以及原生的百度地图，为地理数据可视化提供强有力的支持

**安装**：

```bash
pip install pyecharts
```

## 11. pyecharts 绘制饼图-GDP数据可视化【掌握】

需求：加载爬虫抓取的 GDP 数据，使用 pyecharts 绘制饼状图显示GDP前十的国家。

**示例代码**：

```python
"""
pyecharts-GDP数据可视化
学习目标：能够使用 pyecharts 绘制饼图
"""

# 思路
# ① 从文件中读取 GDP 数据
# ② 使用 pyecharts 绘制饼状图显示GDP前十的国家

# 导入饼图类
from pyecharts.charts import Pie
# 导入配置选项模块
import pyecharts.options as opts


def data_view_pie():
    # ① 从文件中读取 GDP 数据
    gdp_data = []

    with open('./spider/gdp.txt', 'r', encoding='utf8') as f:
        content = f.read()
        gdp_data = eval(content)

    # ② 使用 pyecharts 绘制饼状图显示GDP前十的国家
    # 获取 GDP 前 10 的国家数据
    gdp_top10 = gdp_data[:10]

    # 创建饼图
    pie = Pie(init_opts=opts.InitOpts(width="1400px", height="800px"))
    # 给饼图添加数据
    pie.add(
        "GDP",
        gdp_top10,
        label_opts=opts.LabelOpts(formatter='{b}:{d}%')
    )
    # 给饼图设置标题
    pie.set_global_opts(title_opts=opts.TitleOpts(title="2020年世界GDP排名", subtitle="美元"))
    # 保存结果，默认保存到 render.html 文件
    pie.render()


if __name__ == '__main__':
    data_view_pie()
```

![image-20220823194305166](E:\黑马培训\Python进阶笔记\assets\image-20220823194305166.png)

![image-20220823194318685](E:\黑马培训\Python进阶笔记\assets\image-20220823194318685.png)

## 12. day08总结

1）正则表达式【常握】

* re 模块：regex(正则)
  * match：要求从字符串开头必须匹配成功，成功返回 Match 对象，否则返回None
  * search：从字符串开头向后扫描匹配，返回第一个成功的匹配，成功返回 Match 对象，否则返回None
  * **findall：从字符串开头扫描匹配到结尾，返回所有匹配到的内容，返回的是个 list**

* 正则分组（常用）

  * 可以将正则表达式内容分成几组，在整个正则匹配成功之后，可以获取指定分组匹配到的内容
    * 语法：`(正则)` 或 `(?P<别名>正则)`
      * `(\d{3})(\d{4})(\d{4})`
      * `<a.*>(?P<text>.*)</a>`
    * 函数：获取指定分组匹配的内容
      * `Match对象.group()`：获取整个正则匹配的内容
      * `Match对象.group(组序号)` 或 `Match对象.group(组别名)`：获取正则中指定分组匹配的内容
  * 分组引用：正则中可以引用前面分组匹配的内容
    * 语法：`\组序号` 或 `(?P=组别名)`
      * `(\d+)\s\1\s\1`
      * `(?P<num>\d+)\s(?P=num)\s(?P=num)`

* 正则表达式修饰符

  * re.I：匹配时不区分字母大小写
  * re.M：多行匹配，影响 ^ 和 $
  * **re.S：影响 . 符号，设置之后，.符号就能匹配\n了**

* 贪婪模式和非贪婪模式

  * 贪婪模式：在保证整个表达式匹配成功的前提下，尽可能多的匹配
  * 非贪婪模式：在保证整个表达式匹配成功的前提下，尽可能少的匹配

  * 针对的正则表达式中量词：`*`、`+`、`{m,n}`、`?`，这些量词在匹配时，默认都是贪婪模式，如果需要变成非贪婪模式，需要再量词之后再添加一个`?`

2）网络爬虫程序

* 浏览器渲染

  * 浏览器通过域名解析服务器（DNS）获取IP地址
  * 浏览器先向IP发起请求，并获取响应
  * 在返回的响应内容（html）中，可能会带有css、js、图片等url地址，浏览器按照响应内容中的顺序依次发送其他的请求，并获取相应的响应
  * 浏览器每获取一个响应就对展示出的结果进行添加（加载），js、css等内容可能会修改页面的内容，js也可以重新发送请求，获取响应
  * 从获取第一个响应并在浏览器中展示，直到最终获取全部响应，并在展示的结果中添加内容或修改，**这个过程叫做浏览器的渲染**

* FastAPI 返回图片

  ```python
  @app.get('/images/0.jpg')
  def get_image_0():
      with open('./sources/images/0.jpg', 'rb') as f:
          content = f.read()
  
      return Response(content, media_type='image/jpg')
  ```

* FastAPI 提取 url 地址中的参数

  ```python
  # /images/xxx.jpg
  @app.get('/images/{image_name}')
  def get_image(image_name):
      # 1.jpg
      # 2.jpg
      # ...
      print('图片名称：', image_name)
      with open(f'./sources/images/{image_name}', 'rb') as f:
          content = f.read()
  
      return Response(content, media_type='image/jpg')
  ```

* 爬虫程序

  * 作用：模拟浏览器发送HTTP请求，获得响应，爬虫程序是数据采集的一种方式。
  * requests 模块：python中一个HTTP开源库，经常用来编写爬虫程序。
    * pip install requests
  * 爬虫示例
    * 请求百度
    * 爬取一张图片
    * 爬取多张图片
    * 爬取网页数据：GDP数据提取
    * 爬虫多任务
  * 数据可视化
    * pyecharts：代码进行数据可视化操作
    * FineBI：通过软件加载数据，直接进行可视化

3）进阶内容总结

* 面向对象代码：多写，包括 python
* 概念
  * html、css、IP、端口、TCP、socket、HTTP协议、web服务器、多任务、多进程、多线程、爬虫程序

4）MySQL数据库-4天

* SQL基础：表增、删、改、**查**

* 软件安装
  * mysql数据库
    * **解压之后的目录不要带中文**
    * **打开cmd一定以管理员身份打开**
    * **安装mysql时候，不要把cmd关掉，临时密码在cmd界面上**
    * **修改mysql密码的时候，不要设置复杂密码**
    * 万一数据库装坏了，重新来！！！
      * **打开cmd以管理员身份打开**
      * 切换到解压目录下的 bin 目录里
      * `net stop mysql`：停止mysql服务
      * `mysqld -remove`：卸载mysql服务
      * 切换到解压目录下，删除里面的data目录
      * 从管理员打开 cmd 那一步重新开始
  * datagrip软件
    * 跟 pycharm 一样