# 课外做题补充知识点与做题心得

# 1. 做题心得

1. 当列表中的元素是字典时，需要遍历拿出数据时，在列表上需要使用for循环来遍历取出，列表中的每一个字典，轮到字典时就不用用for遍历了，直接依靠键来取值

代码示例：

```python
        for i in user_list:
            if i['name'] == _name:
                print('用户名已经存在，不允许注册...')
                break
```

上面代码的意思是，遍历user_list 用i来接收这个容器返回来的数据，取数据是一次一次的取，取一次数据以后，与此同时再把这次中取出来的字典，根据键值来取相对应的数据。

2.  else用得位置的小问题

```
        for i in user_list:
            if i['name'] == _name:
                print('用户名已经存在，不允许注册...')
                break
            # else放在这个位置的时候，就会发生比一次没出现重复，就执行这个else了
            # 而后面的数据还有可能存在相等的，你就没有比了
        else:
            _password = input('请输入注册的密码...')
            new_user = {'name': _name, 'pwd': _password}
            user_list.append(new_user)
```

3. 遍历从左到右依次取出容器中的数据

for 变量 in 列表

从列表中取出一个数据给变量，这个变量走一遍for循环里面的语句，然后接下来就是第二个数据给变量，就这样依次执行，直到列表中没有了数据，for循环结束

**老师解释版本：**

执行过程:
首先从被遍历的数据里取出一个元素，并赋值给临时变量
在循环里可以直接使用临时变量的值
当循环里的代码执行结束，会再次回到 for 循环起始的位置，取出一个元素赋值给临时变量，并开始循环
直到循环已经无法再次取出元素，才会结束循环，然后开始执行循环后的代码

图示如下:

![image-20220801171242122](E:\黑马培训\assets\image-20220801171242122.png)

4. 只有有顺序的数据才可以用到索引，像字典，集合都不能用索引！！！！！！！

5. 出现**隐藏内置名称这个警告时**，说明自己定义的变量与python中已经有的函数重名了，这时就要重新定义变量名，像sum在python中已经有了这个函数了，在以这个名称定义就会覆盖原有的sum函数，这是不可行的
6. 在平时到底要用元组，还是列表呢，那要看存储的数据需不需要修改！
7. 好像只有randint(a, b)是左右都包括的，其它大部分都是包左不包
8. 格式化输出：想要小数点就可以这样{height:.2f}  格式：{ 变量 : 控制符号}

9. **需要重复多次执行功能时,可以把while循环定义在整体框架以外,内部使用时不需要再次循环了,在特定语句下break一下就可以了，如果定义在函数内部，且需要跳出多层循环时，break已经不能满足要求，所以直接return就行，结束函数的执行那么其他的语句都不会再运行了**

**代码示例:**

```python
    while True:
        show_menu()
        num = eval(input('请输入功能数字: '))
        if num == 1:
            print('添加学生')
            break
        elif num == 2:
            print('查询所有学生')
        elif num == 3:
            print('查询某个学生')
        elif num == 4:
            print('修改某个学生')
        elif num == 5:
            print('删除某个学生')
        elif num == 6:
            print('退出系统')
        else:
            print('输入有误请重新输入')
```

10. 关于全局变量与局部变量所出现的问题

![image-20220812095526658](E:\黑马培训\assets\image-20220812095526658.png)

python语言与其他语言不同,定义一个变量不用提前声明,python只根据传给变量的内容来确定变量的类型,所以全局变量在函数内部的引用会与java语言不同,python在函数内部只能来使用全局变量,不能对全局变量进行赋值操作等,如需进行赋值操作,必须使用global来对变量声明,告诉python这个变量是全局变量

![image-20220812100302476](E:\黑马培训\assets\image-20220812100302476.png)

11. 如果需要一段代码在某种特定条件下循环执行,那么直接在代码体外部加上While True即可,在满足特定条件下就直接break来终止循环即可,如果在函数体内部,还能用return直接结束函数的执行

12. 魔法方法init不能有返回值,_  str _ 方法必须要有返回值,且值类型为字符串
13. 隐含了把对象打印成字符串的过程

![image-20220812144350450](E:\黑马培训\assets\image-20220812144350450.png)

**print打印出的结果是 return返回的值**

14. 每个类的数据对象都有自己的内存空间,不同的数据对象都是互不相关的

15. if ... else 语句注意点:

```python
        if age <= 0:
            print('年龄输入不合法')
            sys.exit(0)
        else:
            self.__age = age
            self.color = color
            self.food = food
```

**上面的语句,如果if满足了,想要接下来的语句不要执行,那么必须写else,如果没有写else,后面的语句跟if平级了,if运行完以后,后面的语句也会接着运行**

16. super调用扩展单继承

```python
lass A(object):
    def func(self):
        print('A类中的func函数')


class B(A):
    def func(self):
        super().func()
        print('B类中的func函数')


class C(B):
    def func(self):
        super().func()
        print('C类中的func函数')


# 创建一个C这个类的对象
obj = C()
# 思考：代码的执行结果是什么？？？
obj.func()
```

**代码运行结果:**

![image-20220813155314780](E:\黑马培训\assets\image-20220813155314780.png)

**代码运行流程:**

![image-20220813155348181](E:\黑马培训\assets\image-20220813155348181.png)



**super().func() ===> super(C, self).func() 后面的意思是按照继承的顺序去找它的父类调用里面的func()函数,也就是说去B中调用**

- **要看的是起点的继承顺序链**

17. super调用扩展多继承

```python
class A(object):
    def func(self):
        print('A类中的func函数')


class C(A):
    def func(self):
        super().func()
        print('C类中的func函数')


class B(A):
    def func(self):
        super().func()
        print('B类中的func函数')


class D(B, C):
    def func(self):
        super().func()
        print('D类中的func函数')


# 创建一个 D 这个类的对象
obj = D()
# 思考：代码执行的结果是什么？？？
obj.func()
```

**注意:**![image-20220813161215643](E:\黑马培训\assets\image-20220813161215643.png)

**运行结果:**![image-20220813161251024](E:\黑马培训\assets\image-20220813161251024.png)

**运行流程:**

![image-20220813161318129](E:\黑马培训\assets\image-20220813161318129.png)



14. pickle模块的使用

![image-20220815183834040](E:\黑马培训\assets\image-20220815183834040.png)

![image-20220815183757876](E:\黑马培训\assets\image-20220815183757876.png)

![image-20220815183807925](E:\黑马培训\assets\image-20220815183807925.png)









# 2. 补充知识点

### 2.1 Python itertools库

**itertools库**是python中的一个专门用于高效处理迭代问题的[内置函数](https://so.csdn.net/so/search?q=内置函数&spm=1001.2101.3001.7020)库。

itertools中的函数大多是返回各种[迭代器](https://so.csdn.net/so/search?q=迭代器&spm=1001.2101.3001.7020)对象，其中很多函数的作用我们平时要写很多代码才能达到，而在运行效率上反而更低，毕竟人家是系统库。

例如

**itertools.accumulate**

元素累加

![img](https://img-blog.csdnimg.cn/20210818205629525.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2h4eGp4dw==,size_16,color_FFFFFF,t_70)

 

**itertools.combinations**

求列表或生成器中指定数目的元素不重复的所有组合

![img](https://img-blog.csdnimg.cn/20210818210437273.png)

### 2.2 python万物皆是对象

```python
"""
万物皆对象：在 python 中，所有的东西其实都是"对象"
"""
print('=================================== 基础类型 ======================================')
# int、float、bool
# 其实这个 num1 就是一个 int 类的实例对象，int 是 python 一个内置的类
num1 = 10
# <class 'int'>
print(type(num1), num1)

# 其实这个 num2 就是一个 float 类的实例对象，float 是 python 一个内置的类
num2 = 10.5
# <class 'float'>
print(type(num2), num2)

# 其实这个 gender 就是一个 bool 类的实例对象，bool 是 python 一个内置的类
gender = True
# <class 'bool'>
print(type(gender), gender)

print('=================================== 容器类型 ======================================')
# list、dict、tuple、set、str

# 其实这个 my_list 就是一个 list 类的实例对象，list 是 python 一个内置的类
my_list = [1, 2, 3]
# <class 'list'>
print(type(my_list), my_list)

print('=================================== 函数类型 ======================================')


# 注意：函数名其实就是 function 这个类的实例对象
def my_sum(a, b):
    return a + b


# <class 'function'>
print(type(my_sum), my_sum)

print('=================================== 类 ======================================')


# 注意：类名其实一个 type 类的实例对象
class Dog(object):
    """狗类"""
    def __init__(self, _name, _age):
        self.name = _name
        self.age = _age


# <class 'type'>
print(type(Dog), Dog)

# <class '__main__.Dog'>
dog1 = Dog('大黄', 2)
print(type(dog1), dog1)
```

### 2.3 pickle模块的使用

```python
"""
pickle模块的使用：
* pickle.dumps(数据)：将数据转换为可以写入到文件中的一个二进制bytes数据
* pickle.loads(二进制bytes数据)：将二进制bytes数据恢复成原来的数据
"""
# 导入模块
import pickle


class Student(object):
    """学生类"""
    def __init__(self, _name, _age, _tel):
        """学生对象初始化方法，给创建的学生对象添加初始的属性"""
        self.name = _name
        self.age = _age
        self.tel = _tel

    def __str__(self):
        """返回一个字符串，包含学生对象的姓名、年龄、电话"""
        return f'姓名：{self.name}，' \
               f'年龄：{self.age}，' \
               f'电话：{self.tel}'


stu_list = []
stu1 = Student('smart', 18, '138')
stu2 = Student('linda', 16, '135')
stu_list.append(stu1)
stu_list.append(stu2)
print(stu_list)

# 需求1：直接将 stu_list 数据写入到文件
with open('./test.pickle', 'wb') as f:
    content = pickle.dumps(stu_list) # bytes
    f.write(content)

# 需求2：从 test.pickle 中加载数据
with open('./test.pickle', 'rb') as f:
    content = f.read()
    stu_list = pickle.loads(content)
    print(type(stu_list), stu_list)


for stu in stu_list:
    print(stu.name, stu.age, stu.tel)

```

### 2.4 简便方式取列表中的数据

**列表前面加星号作用是将列表解开成两个独立的参数，传入函数**

先看演示代码 

```python
list = [1,2,3,4]
print(*list)
def add(*args): 
print(type(args)) 
print(args)   
for i in args:   
print(i) 
add(list) 
print('------------------------------') 
add(*list) 
` 输出如下 `
1 2 3 4 
<class 'tuple'> 
([1, 2, 3, 4],) 
[1, 2, 3, 4] 
------------------------------ 
<class 'tuple'> 
(1, 2, 3, 4)
1 2 3 4 
在列表前加*会将列表拆解为一个一个的元素 
在函数中使用 * 时，可以按如下理解 
`*args = *list ` ====> 将list直接传递给args，但类型转换为元组 
`*args = list`  ====>   args加上*（拆解）等于list，反过来想，args就等于list再套上一层，即(list,)
```

### 2.5 想要快速打印出列表里面的对象的具体值

这时候可以使用下面这几种方法:

1. ```
   __repr__魔法方法  这个方法只要有对象存放的地方,就会直接把对象的具体数据打印出来,但是外面还是会套着原来装这个对象的容器
   ```

**代码示例:**

```python
class Animal(object):
    def __init__(self, _name):
        self.name = _name

    # 这个只有打印对象时才会使用
    def __str__(self):
        return f"{self.name}"

    # 这个方法无论对象在哪里,只要你要输出的地方有这些对象,那么就会打印出对象的具体的信息
    def __repr__(self):
        return f"{self.name}"


# 示例化一个Animal类型的对象
cat = Animal('张三')
# 定义一个空列表,接收对象
_list = []
# 将对象加入列表中
_list.append(cat)
# 打印列表
print(_list)
```

2.  **利用*号把列表拆分掉,将里面的数据一个个取出来,给print打印**

*list 的作用 :  **会将列表拆解为一个一个的元素** 

![image-20220816213053365](E:\黑马培训\assets\image-20220816213053365.png)

**补充知识点:**

![image-20220816002538383](E:\黑马培训\assets\image-20220816002538383.png)

### 2.6 函数内置属性:  __ name __

格式:函数名.__ name __: 获取函数名的字符串表现形式

![image-20220817114249131](E:\黑马培训\assets\image-20220817114249131.png)

**在模块中的使用中,   被当做模块导入的那个模块,   在他里面调用__ name __ 时它的值是mian   ,而在被导入的模块里他的值是导入模块的名字,我们就这可以使用这一特性,来给测试函数的代码块做封装**

# 3. 闭包与装饰器

### 3.1 一些有用的知识点 --- 闭包

- **python语法规定等式左边的变量是局部变量**,这就是为什么定义了一个全局变量在函数内部进行**赋值操作**时会报错

- 如果在函数内部函数中要对外部函数的变量进行重新赋值操作,需要进行nonlocal 声明
- 闭包的定义

1.    必须有函数嵌套定义
2.    在内部函数中会使用到外部函数中的变量
3.    外部函数的返回值是内部函数名

![image-20220816220438973](E:\黑马培训\assets\image-20220816220438973.png)

**有缺陷的实现:**

![image-20220816220910526](E:\黑马培训\assets\image-20220816220910526.png)

![image-20220816221116754](E:\黑马培训\assets\image-20220816221116754.png)

这样写的话,因为引用的是全局变量,所以这个变量也可以被其他函数使用,如果被其他函数修改的话没那么这个程序运行就会出错,所以这样做不严谨

**利用闭包来实现:**

![image-20220816223219170](E:\黑马培训\assets\image-20220816223219170.png)

**具体代码格式:**

![image-20220816215536854](E:\黑马培训\assets\image-20220816215536854.png)

**闭包的实例:**

![image-20220816223451645](E:\黑马培训\assets\image-20220816223451645.png)

**因为这是给外部函数里面的变量进行追加操作,没有进行赋值操作,所以不用nonlocal来声明变量**

结果是: 10 12 20

### 3.2 通用装饰器代码示例

![image-20220816220659718](E:\黑马培训\assets\image-20220816220659718.png)

**装饰器定义的基本形式:**

![image-20220816222625664](E:\黑马培训\assets\image-20220816222625664.png)



### 3.3 装饰器语法糖的使用

![image-20220816220805776](E:\黑马培训\assets\image-20220816220805776.png)

### 3.4 带有返回值的装饰器

![image-20220816225047669](E:\黑马培训\assets\image-20220816225047669.png)

![image-20220817103356528](E:\黑马培训\assets\image-20220817103356528.png)

**代码示例加注释解释:**

```python
import time


# 装饰器定义
def get_time(func):
    # 被装饰的函数有一个形参，所以装饰器的内部也需要一个形参
    def wrapper(num):
        s1 = time.time()
        # 调用被装饰的原函数时，需要将内部函数接收到的参数再传递给被装饰的原函数
        _sum = func(num)
        s2 = time.time()
        print('执行时间：', s2 - s1)
        # 将被装饰函数的返回值作为内部函数的返回值再返回
        return _sum

    return wrapper


@get_time
def my_sum(n):
    result = 0

    # 遍历计算1-n之间的数字和
    for i in range(1, n + 1):
        result += i

    # print(result)
    return result


# 注意：添加了装饰器以后，my_sum已经变成1装饰器的内部函数wrapper
# 其实这里是在调用装饰器内部的函数wrapper，并且接收它的返回值
res = my_sum(100000)
print(res)

"""

结论: 如果被装饰的函数带有返回值,装饰器内部函数调用被装饰函数时,需要接收被装饰函数的返回值
并且最终将被装饰函数的返回值作为自己的返回值返回



"""

```

### 3.5 装饰带有参数的函数

**代码示例加重点环节解释：**

```python
import time


# 装饰器定义
def get_time(func):
    # 被装饰的函数有一个形参，所以装饰器的内部也需要一个形参
    def wrapper(num):
        s1 = time.time()
        # 调用被装饰的原函数时，需要将内部函数接收到的参数再传递给被装饰的原函数
        func(num)
        s2 = time.time()
        print('执行时间：', s2 - s1)

    return wrapper


@get_time
def my_sum(n):
    result = 0

    # 遍历计算1-n之间的数字和
    for i in range(1, n + 1):
        result += i

    print(result)


my_sum(100000000)


```

**结论:**  如果被装饰的函数有参数，那么装饰器内部函数也需要对应数量的参数,而且内部函数调用被装饰函数时，需要把接收的参数传递给被装饰函数![image-20220816225106074](E:\黑马培训\assets\image-20220816225106074.png)

### 3.6 通用装饰器

**编写通用装饰器代码:**

```python
"""
编写一个通用装饰器,计算函数的执行时间,要求可以装饰任意函数(带任意参数或者返回值)
"""
import time


# 定义一个装饰器
def get_time(func):
    # 这里写的是形参,是将传过来的实参进行组包,变成元组,和字典
    def wrapper(*args, **kwargs):
        t1 = time.time()
        # 这里写的是实参,是将元组或者字典参数进行拆包,传给原来的函数
        res = func(*args, **kwargs)
        t2 = time.time()
        print(f'函数执行的时间: {t2 - t1}')
        return res

    return wrapper


@get_time
def func1(n):  # 把这个函数的名字变成了wrapper 并且把原函数名作为参数传进装饰器了
    _sum = 0
    for i in range(n + 1):
        _sum += i
    return _sum


# 定义一个累乘函数
# 相当于自动做调用 func2 = my_func(func2)
@get_time
def func2(n, m):
    res = 1
    for i in range(n, m):
        res *= i
    return res

res1 = func1(20000)
print(res1)
res2 = func2(1,1000)
print(res2)
```

### 3.7 浅拷贝和深拷贝

![image-20220816225225655](E:\黑马培训\assets\image-20220816225225655.png)

**腾讯的面试题**

![image-20220816225301050](E:\黑马培训\assets\image-20220816225301050.png)

![image-20220816225312370](E:\黑马培训\assets\image-20220816225312370.png)

**代码:**

```python
import copy

a = [[1, 2, 3], [4, 5, 6]]
b = a
c = copy.copy(a) # 浅拷贝
d = copy.deepcopy(a) # 深拷贝

a.append(7)
a[1][2] = 0

print(a)
print(b)
print(c)
print(d)
```

**debug的执行流程:**

注意添加,修改,查询等操作**都是对于变量的地址来说的**  就算一个变量进行赋值的操作已经完成,只要给他赋值的变量

![image-20220816230924637](E:\黑马培训\assets\image-20220816230924637.png)

**代码分析:**

1. 打印a  就是直接把原来的列表打印出来

2. b = a 就是b的指向 指到了a

3. 运行到了浅拷贝这一块,它只会更改这个最外层的可变容器数据类型的地址  这时候 c 变量的值还是原来的,但是最外层列表的地址却已经改变了

4. **可变容器类型，进行深拷贝时，每一层`可变数据`都会重新开辟内存，进行拷贝。**   运行到d  对a进行深拷贝 a中的两个列表的地址都发生了改变,指向的已经不是原来两个列表的地址了,但是数值还是一样,值是不可变数据类型 

5. 给a 这个变量指向的列表之中加数据 a b 都会正常的去添加数据,因为地址没有发说说改变,但是到c, d 这一步因为拷贝的原因,最外层的列表的地址已经发生了改变,而加了值,指向的列表是原来a最外层列表的地址,所以7这个值加不进c, d之中

6. ```
   执行a[1][2] 这个操作是对原来a列表之中的列表的值的引用进行修改,指向了0, 在这里只有深拷贝会把里面列表的地址,重新开辟,所以只有深拷贝这里这个位置的值不会变成0
   ```

   

### 3.8 带参数的装饰器

需要使用带有参数的装饰器；
1. 带有参数的装饰器就是**使用装饰器装饰函数的时候可以传入指定参数**，语法格式: **@装饰器(参数,...)**

**实现**：在装饰器外面再包裹上一个函数，让最外面的函数接收参数，返回的是装饰器，因为@符号后面必须是装饰器实例。
"""

**代码示例:**

```python
"""
带有参数的装饰器
学习目标：能够写出带有参数的装饰器
"""

"""
需求：定义一个装饰器 log，装饰下面的两个函数，实现调用上面两个函数时进行日志信息记录的功能，将调用信息记录到 record.log 文件中。
记录日志信息要求：xxx函数被调用了，比如：login函数被调用了
"""


# 我们现在是给原来的装饰器代码有包装了一层函数,让最外层的这个函数接收参数给里面的装饰器使用
def logger(filename):
    # 这一块代码是我们之前的装饰器代码
    def log(func):
        def wrapper(*args, **kwargs):
            result = func(*args, **kwargs)
            # 增加的功能代码
            with open(f'{filename}', 'a', encoding='utf8') as f:
                f.write(f'{func.__name__}函数被调用了\n')
            return result

        return wrapper

    # 最外部这个函数要把装饰器返回
    return log


# log = logger("aa.log")
@logger("aa.log")
def login():
    print("用户登录逻辑")


# log = logger("bb.log")
# 这一步先是log与filename相绑定了
# 接着就是@log 相当于register = log(register) 这时候register就是wrapper这个函数的函数名
@logger("bb.log")
def register():
    print("用户注册逻辑")


login()
register()
"""
在上面代码的基础上，如果还想实现能够指定日志信息记录到哪个文件里，该怎么办？
比如：调用 login 函数时的信息记录到 login.log 中
比如：调用 register 函数时的信息记录到 register.log 中

答： 需要使用带有参数的装饰器；
1）带有参数的装饰器就是使用装饰器装饰函数的时候可以传入指定参数，语法格式: @装饰器(参数,...)

实现：在装饰器外面再包裹上一个函数，让最外面的函数接收参数，返回的是装饰器，因为@符号后面必须是装饰器实例。
"""

```

### 3.9 装饰器练习

题干：一个函数, 返回一个字符串, 使用装饰器实现对这个字符串添加后缀.txt

**代码与解题思路：**

```python
# 定义一个装饰器给re_str函数添加功能
def my_func(func):
    def wrapper():
        res = func()
        # 函数调用后的扩展功能
        result = res + '.txt'
        print(result)

    return wrapper


# 定义返回字符串的函数
@my_func
def re_str():
    _str = input('请输入需要加后缀的字符串: ')
    return _str

re_str()
```

**练习2:**

题干：假设有如下两个函数：购物车添加函数、订单提交函数，要求编写一个装饰器装饰 cart 和 order 两个函数，在装饰器中模拟登录判断功能，当 username 为 'smart' 且 password 为 '123456abc' 时才调用被装饰函数，否则提示：用户未登录！

```python
# 定义一个装饰器来实现新增功能
def my_func(func):
    def wrapper(*arg, **kwargs):
        _username = arg[0]
        _password = arg[1]
        if _username == 'smart' and _password == '123456abc':
            func()
        else:
            print('用户未登录')

    return wrapper


@my_func
def cart():
    print('购物车添加函数')


@my_func
def order():
    print('订单提交函数')


# 提示用户输入用户名和密码
username = input('请输入用户名：')
password = input('请输入密码：')

cart(username, password)
order(username, password)

```

# 4. 题目练习

### 4.1 密码游戏

**描述**

  牛牛和牛妹一起玩密码游戏，牛牛作为发送方会发送一个4位数的整数给牛妹，牛妹接收后将对密码进行破解。 

  破解方案如下：每位数字都要加上3再除以9的余数代替该位数字，然后将第1位和第3位数字交换，第2位和第4位数字交换。 

  请输出牛妹破解后的密码。 

**输入描述：**

输入一个四位数的整数。

**输出描述：**

输出破解后的密码，以四位数的形式。

**示例1**

输入：

```
1234
```

输出：

```
6745
```

**备注：**

```
输入不会有前置0，但是输出要保持前置0
```

**解答利用数学思维：**

```python
num = eval(input())
g = num % 1000 % 100  % 10
s = num % 1000 % 100 // 10
b = num % 1000 // 100
q = num // 1000
_g = (g + 3) % 9
_s = (s + 3) % 9
_b =  (b + 3) % 9
_q = (q + 3) % 9
_q, _s = _s, _q
_b, _g = _g, _b
a = str(_g)
b = str(_s)
c = str(_b)
d = str(_q)
num_str = d + c + b + a
print(num_str)
```

**极其错误的思想，代码太过于复杂，不是程序员该有的思想！！！**

**解答利用list函数：**

```python
num = input()
# _list = list(num)

list_after = []
for i in num:
    i = int(i)
    i += 3
    i %= 9
    i_str = str(i)
    list_after.append(i_str)
list_after[0], list_after[2] = list_after[2], list_after[0]
list_after[1], list_after[3] = list_after[3], list_after[1]

_str = ''
for i in list_after:
    _str += i
print(_str)
```

