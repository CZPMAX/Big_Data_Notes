[TOC]

### 第二天主要内容大纲

- 流程控制语句介绍
- 分支语句之if语句
  - 单分支，双分支，多分支
- if语句的嵌套
- 如何获取随机数
- 综合案例：猜拳游戏

### 1.流程控制语句简介

- 概述：
  - 所谓流程控制语句就是用来控制代码执行的，有些代码是要按照顺序去执行的，有些代码需要满足条件后在执行，有些代码则要

学完这个会收获到的技能：对程序中的分支逻辑，循环逻辑进行实现

满足条件后重复执行代码。

- 分类

  - **顺序结构**
    - 指的是代码会按照从上往下，从左到右依字逐行的执行，默认结构

  ```
  print("=== start ===")
  print("hello 1" + 'aa' + 'bb')
  print('hello 2')
  print('hello 3')
  print('=== end ===')
  ```

  上面的例子就是循序结构，第二句代码就是从左往右执行的例子

  - **分支结构**
    - 指的是代码在某些条件的情况下才会被执行，例如：if语句

  if语句之单分支：

  ​          **应用场景：**
  ​                          适用于一种情况的判断

  ​          **格式：**

  ​                   if  判断条件:

  ​                        判断条件成立后要运行的代码语句

  ​                   ......

  ​                   跟if语句对齐的代码，就跟if没有关系了，判断条件的成立与否，都不会影响这里代码的后续执行

  ​          细节：判断条件无论简单或者复杂，判断过后的结果必须是boolean类型的，即：True或者False

  if语句之双分支：

  ​          应用场景：

  ​                         适用于两种情况的判断

  ​          格式：

  ​                  if 判断条件:

  ​                          语句体1

  ​                      ......

  ​                  else:

  ​                          语句体2

  ​                      ......

  ​          执行流程：

  ​                         1.判断if后面的条件是否成立

  ​                         2.如果条件成立则执行语句体1的内容

  ​                         3.如果条件不成立则执行语句体2的内容

  ​          细节：else默认含有if的反条件

  代码实例：

  ```python
  # 1.定义一个变量来接收用户输入的身高数值，并且提示输入
  height = eval(input('请输入您的身高:'))
  # 2.写if语句判断输入的数据是否满足要求，并且编写满足要求执行的代码块
  if height > 150:
      print('身高不符合要求，请购买您的门票')
  # 3.编写不满足条件时，执行的语句
  else:
      print('身高符合要求，不用购买门票')
  ```

  if语句之多分支：

  ​          应用场景：

  ​                          适用于多种情况的判断
  
  ​                 格式：
  
  ​                          if   判断条件1：
  
  ​                                   语句体1
  
  ​                          elif 判断条件2：
  
  ​                                  语句体2
  
  ​                         elif 判断条件3：
  
  ​                                  语句体3
  
  ​                         ..........
  
  ​                          else:
  
  ​                                   语句体n
  
  ​         执行流程:
  
  1. 先执行判断条件1.看其是否成立
  2. 如果成立，则执行语句体1，然后整个if语句就结束了
  3. 如果不成立，则执行判断条件2，看其是否成立
  4. 如果成立，则执行对应的语句体2，然后整个if语句结束
  5. 如果不成立，则执行判断条件3，依次类推
  6. 如果所有的判断条件都不成立。则执行else对应的 语句体n,然后整个if语句就结束了
  
  就拿成绩比较来说，代码最开始就先过滤无效数据更为严谨
  
  例子：
  
  ```python
  if score < 0 or1 score > 100:
      print('非法值成绩无效')
  elif score >= 90 and score <= 100:
      print('奖励山地自行车一辆')
  elif score >= 80 and score <= 89:
      print('奖励游乐场一日游')
  elif score >= 70 and score <= 79:
      print('放假一天')
  elif score >= 60 and score <= 69:
      print('练习题一套')
  # elif score >= 0 and score <= 59:
  #     print('奖励男女组合拳一套+扫把棒法')
  else:
      print('奖励男女组合拳一套+扫把棒法')
  ```
  
  if  else语句的升级版要考虑到else语句的所带的反条件，这样代码能更加简单
  
  例子：下面代码中 elif score >= 90:的所以下一个起始的elif 默认条件score<90 所以这时候直接写score >= 80就行，具体代码如下：
  
  ```python
  # 升级版，要利用到else语句的反条件
  if score < 0 or score >100:
      print('成绩非法请重新输入:')
  elif score >= 90:
      print('奖励山地自行车一辆')
  elif score >= 80:
      print("奖励游乐场一日游")
  elif score >= 70:
      print('放假一天')
  elif score >= 60:
      print('练习题一套')
  else:
      print('奖励男女组合拳一套+扫把棒法')
  ```
  
  **if语句要考虑到非法值**
  
  if 语句的嵌套使用：
  
  ​                               if 条件1：
  
  ​                                           满足条件1 做的事情1
  
  ​                                           满足条件2 做的事情2
  
  
  
  ​                               if 条件2：
  
  ​                                           满足条件2 做的事情1
  
  ​                                           满足条件2 做的事情2
  
  概述：
  
  ​         所谓的if语句嵌套指的就是 if结构的语句体还是一个if语句，这就叫if语句的嵌套
  
  格式：
  
  ​          if 判断条件1：
  
  ​              语句体1
  
  ​              if 判断条件a：
  ​                  语句体a
  
  ​              else:
  
  ​                  语句体b
  
  ​          else:
  
  ​               语句体2
  
  ​         
  
  ```python
  # 提示用户输入公交卡余额，并且用变量接收
  
  money = eval(input('请输入公交卡余额:'))
  
  # 判断余额是否超过2元，超过就提示上车，如果车上有座位就坐下
  
  if money >= 2:
      print('请上车')
      seat = int(input('座位余量:'))
      if seat >= 1:
          print('可以坐下')
      else:
          print('没有座位，站着')
  else:
      print('余额不足，请下车')
  ```
  
  注释快捷键：CTRL + /
  
  优化：eval()函数的用法，它里面接收的是字符串类型的数据，然后将该数据转换成其对应的数据类型，**即相当于把引号去掉的数据类型。**
  
  ```
  age = eval(input('请输入'))
  print(type(age))
  ```
  
  像上面的代码input原来输入默认的是数据类型是str，现在用eval函数会把字符串的标志''都去掉了。即'123'-->123,所以现在识别到的数据类型是int型，就自动转换成int数据类型
  
  细节：**如果里面的类型是字符串类型且内容不为整型或者浮点型或布尔型，则会报错。因为'abc'-->abc而现实中没有abc数据类型的数据**
  
  ```
  age = input('请输入您的年龄:')
  if int(age) >= 18:
      print('您已经成年了可以来上网了')
  else:
      print('未满18岁请自行离开')
  ```
  
  if语句下面通过4个空格分割的代码是属于if语句的代码块，是一个整体，if语句的条件成立则都执行，不成立则都不执行
  
  - 循环结构
    - 指的是代码在满足某些条件的情况下会被重复执行，例如：for循环，while循环

编程是来源于生活的，在生活中都能找到编程的影子

### 2.比较运算符和逻辑运算符

#### 比较运算符：

​         作用：

​                 就是用来做比较的，无论简单还是复杂，结果必然是boolean类型的

​         分类：

​                 < >  >= <= == !=

​         细节：

​                 =号是赋值，==是比较，不要写错了

代码演示：

```python
print(10 > 3)  #True
print(10 < 3)  #False
print(10 >= 3)  #False
print(10 > 10)  #False
print(10 <= 10)  #True
print(10 < 10)  #False
print(10 == 20)  #False
print(10 != 3)  #True
```

#### 逻辑运算符：

​          作用：

​                  用来描述逻辑关系的，例如，并且，或者，非（取反）

​          分类：

​                  and  or  not

> and 表示逻辑与，即，并且的意思，要求所有的条件都要满足，总结：有False则为假

> or 表示逻辑或，即， 或者的意思，只要一个条件满足即可，总结：有True则为真

> not 表示逻辑非，即， 取反的意思，以前是True取反就是False, 反之亦然

![image-20220726221608966](E:\黑马培训\assets\image-20220726221608966.png)

扩展：关于逻辑运算符操作数字

**在实际开发中，有些地方会看到这些代码，技巧就是：把0当False，非0当作True**

对于and的来说有0，整体就是假的  否则取最后一个整数

对于or来说有非0的，整体就是真的   取第一个整数(不包括0)，如果都为0，则整体都为0，即都为假

代码实例：

```python
print(0 and 3) #0
print(2 and 3 and 10) #10
print(0 or  3) #3
print(2 or  0 or 10) #2
```

![image-20220726222726685](E:\黑马培训\assets\image-20220726222726685.png)

![image-20220726222908954](E:\黑马培训\assets\image-20220726222908954.png)

![image-20220726222931677](E:\黑马培训\assets\image-20220726222931677.png)

else默认有if语句的反条件

### 3.三元(目)运算符

概述：

​        简化if....else..语句的操作，将代码优化为一行

格式：

​        值1 if 判断条件 else 值2

if.....else 中都只能有一行代码

执行流程：

1. 先执行判断条件，看其结果是True还是False
2. 如果是True，就返回值1，否则返回值2

代码示例：

方式1：# 获取任意两个整数的差值

```python
c, d, result = 7, 10, 0

# 获取差值
# 方式1: if else版
if c > d:
    result = c - d
else:
    result = d - c
```

方式2：

```python
# 方式2: 三元运算符
result = c - d if c > d else d - c
```

建议：以后做题尽量想多的解题方案，以后遇到问题也能尽快解决

```
card = int(input('请输入银行卡余额：'))
pay = int(input('请输入付款金额：'))

# if card > pay:
#     print('使用银行卡付款')
# else:
#     print('余额不足')

msg = '使用银行卡付款' if card > pay else '余额不足'

print(msg)
```



### 4.Debug断点调试

目的/作用：

1. 可以帮助我们调试程序，方便我们查询程序的每一步执行结果
2. 可以帮助我们排错

步骤：

1. 加断点，

   哪里不会点哪里

   断点一般加在第一行有效代码处，这样可以查看整个程序的执行流程

2. 如何启动Debug断点调试:  在代码编辑区，右键，Debug as ...

3. 启动Debug断点调试以后，看哪里？

​        下左：Frames, 即 表示当前代码执行到哪里了（会具体到行数）

​        下中：Variables, 即 表示变量值的变化的

​        下右：Console, 即控制台，用来展示程序的打印结果的

   4. 如何进行下一步

      F7:    逐过程调试，即一步一步调试，遇到函数后，会进入函数的内部（底层源码）

      F8： 逐行调试，即一行一行的调试，遇到函数后，不会进入到函数内部，而是当做一段代码处理

      F9： 逐断点调试，即 其当前断点调到下一个断点

   5. 如何结束Debug断点调试

​       方式1：一步一步调试完，自动结束

​       方式2：点击红色正方形按钮，结束调试

​       方式3： 如果当前已经是最后一个断点了，再此按下F9，即：结束调试

    6. 如何删除断点

​       方式1： 重新点击断点

​       方式2： 选择 双击图表（break points），移除所有断点即可

细节：用Python来Debug调试代码时，Frames出现Frames are not available不是这个程序出现了什么问题，而是该程序还没有运行到断点处。Debug图表旁边有要执行的程序，需要自己查看，这里的程序是否是将要调试的程序。

![image-20220727184017321](E:\黑马培训\assets\image-20220727184017321.png)

下左：Frames, 即 表示当前代码执行到哪里了（会具体到行数）

下中：Variables, 即 表示变量值的变化的

下右：Console, 即控制台，用来展示程序的打印结果的

### 5.生成随机数random

```python
import random
num = random.randint(1,3)
print(num)
```

randint(1,3)这个包左也包右

python定义变量为空：

```python
i = None
i = null
i = 0 (数值类型)
i = '' （两个单引号） 或 i = "" (这是字符串类型)
i = [] (列表类型)
i = {}（字典类型）
i = () （元祖类型）
```

猜拳不够严谨版本游戏：

```python
import random
# 1.提示用户玩家录入他/她的手势，并接收
playerNum = int(input('请输入您的手势: 剪刀(0) 石头(1) 布(2):  '))

# 2.随机生成电脑的手势
computerNum = random.randint(0,2)
# 3.补充 为了更好的体验，打印电脑的手势
# gesture = '' 这样弄更加复杂了
if computerNum == 0:
   print('电脑出的是: 剪刀')
elif computerNum == 1:
   print('电脑出的是: 石头')
else:
   print('电脑出的是: 布')
# 3.比较他们的手势并输出结果
if playerNum == computerNum:
    print('哎呀。算你走运，平局了')
elif (playerNum == 1 and computerNum == 0) or (playerNum == 0 and computerNum ==2) or (playerNum == 2 and computerNum == 1):
    print('玩家胜利')
else:
    print('电脑胜利')
```

优化过后的代码，输错值时提供三次重新输入机会

```python
import random
# 1.提示用户玩家录入他/她的手势，并接收
import sys

count = 0
while(count<3):
         playerNum = int(input('请输入您的手势: 剪刀(0) 石头(1) 布(2):  '))
         if playerNum >= 0 and playerNum <= 2:
             count = 4
         else:
             count += 1
if count == 3:
    sys.exit(0)
# 2.随机生成电脑的手势
computerNum = random.randint(0,2)
# 3.补充 为了更好的体验，打印电脑的手势
# gesture = '' 这样弄更加复杂了
if computerNum == 0:
   print('电脑出的是: 剪刀')

elif computerNum == 1:
   print('电脑出的是: 石头')
else:
   print('电脑出的是: 布')
# 3.比较他们的手势并输出结果
if playerNum == computerNum:
    print('哎呀。算你走运，平局了')
elif (playerNum == 1 and computerNum == 0) or (playerNum == 0 and computerNum ==2) or (playerNum == 2 and computerNum == 1):
    print('玩家胜利')
else:
    print('电脑胜利')
```
