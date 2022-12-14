# Python进阶之面向对象 类和对象 魔法方法

## 1. 一些碎碎的知识点

> 继承会提高代码的复用性，就是同一段代码可以被多次使用。

> 继承的意思是把那些共性的内容放在父类里面，子类就不用写了，但是不是所有的东西都可以继承下来的。

> 多态一种类型的不同状态，但是都是同一事物。例子：水有很多不同的形态，如冰，气，雾。但是本质上来说他们都是水。New一个冰的对象，它的类型可以是冰，也可以是水。要是水类型的它干的东西会更多。

> Python是面向函数式的编程语言。

> 面向过程意思就是我不会玩手机，我一步一步的自己去学自己去操作。面向对象则是我不会玩手机，我请一个会玩手机的人来完成我要操作的事情。面向对象时我解决问题还要去考虑哪些对象可以让我解决这些问题，对象的维护成本也不一致。

## 2. 面向对象与面向过程的区别

- **面向过程:** 需要实现一个功能的时候，看重的是开发的步骤和过程，把编程任务划分成一个一个的步骤，然后按照步骤分别去执行
- **面向对象:** 在实现一个功能之前，首先分解出解决问题要涉及到的各个对象，站在各个组成对象配合的角度去完成要实现的功能

**举例说明**：洗衣机里面放有脏衣服，怎么洗干净？

面向过程的解决方法：

1. 执行加洗衣粉方法；
2. 执行加水方法；
3. 执行洗衣服方法；
4. 执行脱水方法；
5. 执行烘干方法；

> 以上就是将解决这个问题的过程拆成一个个方法（是没有对象去调用的），通过一个个方法的执行来解决问题。

面向对象的解决方法：

1. 我先弄出两个对象："洗衣机"对象和"人"对象
2. 针对对象"洗衣机"加入一些属性和方法："洗衣服方法"、"脱水方法"、"烘干方法"
3. 针对对象"人"加入属性和方法："加洗衣粉方法"、"加水方法"
4. 然后执行
   - a. 人.加洗衣粉
   - b. 人.加水
   - c. 洗衣机.洗衣服
   - d. 洗衣机.脱水
   - e. 洗衣机.烘干

> 解决同一个问题 ，面向对象编程就是先抽象出对象，然后用对象执行方法的方式解决问题

总结

- 面向过程
  - 把编程任务划分成一个一个的步骤，然后按照步骤分别去执行，适合开发**中小型项目**
- 面向对象
  - 分解出解决问题要涉及到的各个对象，站在各个组成对象配合的角度去完成编程任务，适合**开发大型项目**

## 3. 类和对象

学习目标：

1. 知道类和对象的关系

------

### 3.1 类

- 很多事物存在 **相同的属性和行为(也叫方法)**，比如人有姓名年龄，能吃饭睡觉等等。
- 描述 **具有共同特征的事物的 抽象**，称为 **类** (class)。
- 类包含两个组成部分：
  - **属性**：比如姓名，年龄，身高，肤色等
  - **方法**：比如吃饭，睡觉，飞行，歌唱等

![21_飞机图纸](E:\黑马培训\assets\21_飞机图纸.png)

### 3.2 对象

- **对象** 是 **类** 的实例，是具体的实体，对象也叫**实例对象**

![22_飞机](E:\黑马培训\assets\22_飞机.png)

### 3.3 类和对象的关系

- 类是对象的模板，对象是类的实例(类创造出的具体实体)
  - 类相当于图纸，对象相当于根据图纸制造的实物。
- 每个对象必须有一个对应的类。

![23_玩具](E:\黑马培训\assets\23_玩具.jpg)

![24_月饼](E:\黑马培训\assets\24_月饼.jpg)

- 类是对象的模板，对象是类的实例(类创造出的具体实体)。
- 类相当于图纸，对象相当于根据图纸制造的实物。

### 3.4 定义类

学习目标：

1. 知道如何定义类和实例方法

定义一个类，格式如下：

```python
# class 类名:
# class 类名():
class 类名(object):
    方法列表（不是真的是列表，只是多个函数的定义）
```

**说明：**

- 定义类时有 2 种形式：经典类和新式类，前两行注释部分则为经典类，推荐写新式类
- object 是 Python 里所有类的最顶级父类
- 类名 的命名规则按照 **大驼峰命名法(单词首字母都大写)**

#### 3.4.1 定义实例方法

类中的实例方法格式为：

```python
class 类名(object):
    def 方法名(self): 
        ...
```

- 实例方法的格式和函数类似，也可以设置参数和返回值，但是 **需要设置第一个参数为** `self`
- Pycharm 编写代码是会自动生成`self` ，后面会讲解 `self` 的作用和具体使用

示例代码：

```python
# 需求：定义一个狗类
class Dog(object):
    # 在类中定义的函数，称为方法，下面的这个带有self形参的方法叫：实例方法
    # 注意：self形参必须有，至于是什么，后面会讲！！！
    def eat(self):
        print('吃东西')

    def drink(self):
        print('喝东西')
```

#### 3.4.2 小结

- 定义类和方法：

  ```python
  class 类名(object):
      def 方法名(self): 
          pass
  ```

### 3.5 创建对象和调用方法

学习目标：

1. 知道如何创建实例对象和调用方法

------

#### 3.5.1 创建对象

类定义好之后，可以根据已经定义的类去创建实例对象。

创建对象的格式为：

```python
对象变量名 = 类名()
```

示例代码：

```python
class Dog(object):
    """狗类"""
    # 定义在类的函数，叫方法
    # 注意：self参数是必填的，具体作用后面会进行讲解
    def eat(self):
        print('吃东西')

    def drink(self):
        print('喝东西')


# 创建一个 Dog 类的实例对象
dog1 = Dog()
```

#### 3.5.2 调用方法

调用方法的格式为：

```python
对象变量名.方法名()
```

- 注意：虽然定义方法时设置第一个参数 `self`，但是 **调用方法时不要传递对应`self`的参数**，python 解释器自动处理

示例代码：

```python
class Dog(object):
    """狗类"""
    # 定义在类的函数，叫方法
    # 注意：self参数是必填的，具体作用后面会进行讲解
    def eat(self):
        print('吃东西')

    def drink(self):
        print('喝东西')


# 创建一个 Dog 类的实例对象
dog1 = Dog()

# 通过 dog1 对象调用类中的实例方法
# 注意：通过实例对象调用实例方法时，第一个self形参是不需要传递的，python解释器会自动传递
dog1.eat()
dog1.drink()
```

#### 3.5.3 创建多个对象

- 类作为对象的模具，根据类可以创建多个对象

```python
# 对象1 = 类名()
# 对象2 = 类名()
class Dog(object):
    """狗类"""
    # 定义在类的函数，叫方法
    # 注意：self参数是必填的，具体作用后面会进行讲解
    def eat(self):
        print('吃东西')

    def drink(self):
        print('喝东西')


# 创建一个 Dog 类的实例对象
dog1 = Dog()

# 通过 dog1 对象调用类中的实例方法
# 注意：通过实例对象调用实例方法时，第一个self形参是不需要传递的，python解释器会自动传递
dog1.eat()
dog1.drink()

print('========== 华丽分割线 ==========')

# 再创建一个 Dog 类的实例对象
dog2 = Dog()

# 通过 dog2 对象调用类中的实例方法
dog2.eat()
dog2.drink()
```

#### 3.5.4 小结

- 创建对象：`对象变量名 = 类名()`
- 调用方法：`对象变量名.方法名()`

### 3.6 添加和使用属性

学习目标：

1. 知道如何给对象添加属性和使用属性

------

#### 3.6.1 添加和使用属性

**对象既有实例方法，也有自己的属性**。

对象定义/添加属性格式：

```python
对象变量名.属性名 = 数据
```

- 属性和变量类似，**首次赋值时会定义属性**，再次赋值改变属性

获取对象属性值的格式：

```python
对象变量名.属性名
```

示例代码：

```python
# 需求：定义一个狗类
class Dog(object):
    """狗类"""
    # 定义在类的函数，叫方法
    # 注意：self参数是必填的，具体作用后面会进行讲解
    def eat(self):
        print('吃东西')

    def drink(self):
        print('喝东西')


# 创建一个 Dog 类对象
dog1 = Dog()

# 给 dog1 对象添加一个 name 属性，值为`小黑`
dog1.name = '小黑'
# 给 dog1 对象添加一个 age 属性，值为 2
dog1.age = 2

# 获取 dog1 对象的 name 和 age 属性的值
print(dog1.name)
print(dog1.age)

# 修改 dog1 对象的 age 属性的值
dog1.age = 3
print(dog1.age)

print('========== 华丽分割线 ==========')

# 再创建一个 Dog 类的实例对象
dog2 = Dog()
dog2.name = '旺财'
dog2.age = 1
print(dog2.name)
print(dog2.age)
```

**注意点：一个类可以创建多个实例对象，不同的实例对象有各自的内存存储的空间，他们是相互独立的，互不影响！！！**

![img](E:\黑马培训\assets\对象添加属性.png)

#### 3.6.2 小结

定义/添加属性格式：

```
对象变量名.属性名 = 数据
```

- **首次赋值时会定义属性**，再次赋值改变属性

### 3.7 self的作用

学习目标：

1. 知道方法中 self 参数的作用

------

#### 3.7.1 self是什么

- 在Python类中规定，实例方法的第一个参数是实例对象本身，并且约定俗成，把其名字写为self(自己)。

- 某个对象调用其方法时，Python解释器会

  自动把这个对象

  作为第一个参数传递给方法

  - 通俗理解：**通过哪个对象调用实例方法，那个实例方法的self形参就是哪个对象！！！**

示例代码：

```python
class Dog(object):
    """狗类"""
    def show_info(self):
        print('测试self：', id(self))


# 创建一个 Dog 类对象
dog1 = Dog()

print('调用之前：', id(dog1))
# 底层原理：dog1.show_info() 等价于 show_info(dog1)
# python 解释器将 dog1 实例对象自动传递了 self 形参
dog1.show_info()
print('调用之后：', id(dog1))

print('========== 华丽分割线 ==========')

# 再创建一个 Dog 类的对象
dog2 = Dog()

print('调用之前：', id(dog2))
# 底层原理：dog2.show_info() 等价于 show_info(dog2)
# python解释器自动将 dog2 实例对象传递给了 self 形参
dog2.show_info()
print('调用之后：', id(dog2))
```

运行结果：

```python
调用之前： 2361046759984
测试self： 2361046759984
调用之后： 2361046759984
========== 华丽分割线 ==========
调用之前： 2361046761328
测试self： 2361046761328
调用之后： 2361046761328
```

#### 3.7.2 self的作用

- 在方法中使用 `self`，可以获取到调用当前方法的对象，进而获取到该对象的属性和方法
- self作用：为了**区分**不同对象的属性和方法

- self 跟java的this关键字很相似

```python
class Dog(object):
    """狗类"""
    def show_info(self):
        # 下面这句代码，无论通过哪个实例对象调用show_info方法，打印出的都是 dog1 的信息
        # print(f'我的名字：{dog1.name}，年龄：{dog1.age}')

        # 通过哪个实例对象调用 show_info 方法，self 形参就是哪个实例对象
        # 通过 self 可以区分不同对象的属性和方法
        print(f'我的名字：{self.name}，年龄：{self.age}')


# 创建 dog1 实例对象
dog1 = Dog()
# 给 dog1 对象添加 name 和 age 两个属性
dog1.name = '小黑'
dog1.age = 2

# 需求：在 Dog 类中实现 show_info 方法，调用时打印：`我的名字：XXX，年龄：XXX`
dog1.show_info()

print('========== 华丽分割线 ==========')

# 再来创建一个 Dog 类的实例对象
dog2 = Dog()
# 给 dog2 对象添加 name 和 age 两个属性
dog2.name = '大黄'
dog2.age = 3
dog2.show_info()
```

![img](E:\黑马培训\assets\self形参的作用.png)

#### 3.7.3 小结

- self是什么
  - 通过哪个对象调用方法，方法中self就是这个对象
- self的作用
  - 为了**区分**不同对象的属性和方法

### 3.8 魔法方法：__ init__()

学习目标：

1. 知道`__init__`方法的作用和特点
2. 知道如何定义不带参数和带参数的`__init__`方法

------

#### 3.8.1 魔法方法

- 在Python中，所有以 `__` **双下划线**包起来的方法，都统称为 **Magic Method** ，中文称 **魔法方法** 
- 魔法方法是系统提供好的方法名字，用户需重新实现它
- 魔法方法一般情况下无需手动调用，在合适时候自动会调用

#### 3.8.2`__init__()`基本使用

- `__init__()`方法叫做 **对象的初始化方法**，在 **创建一个对象时会被自动调用**，不需要手动调用
- `__init__()`方法的作用：给创建的实例对象添加一些初始的属性

示例代码：

```python
class Dog(object):
    """狗类"""
    def __init__(self):
        """
        调用：该方法是在创建每个 Dog 类的实例对象时会被自动调用
        作用：对象的初始化方法，可以在这个方法中给创建对象添加一些初始的属性！！！
        """
        print('======== Dog 类的 __init__ 方法被调用 ========')
        self.name = '旺财'
        self.age = 2

    def show_info(self):
        print(f'我的名字：{self.name}, 年龄：{self.age}')


print('====== 创建对象之前 ======')
# 创建一个 dog1 对象
dog1 = Dog()
print('====== 创建对象之后 ======')

print(dog1.name)
print(dog1.age)

# 通过 dog1 对象调用 show_info 方法
dog1.show_info()
```

运行结果：

```python
====== 创建对象之前 ======
======== Dog 类的 __init__ 方法被调用 ========
====== 创建对象之后 ======
旺财
2
我的名字：旺财, 年龄：2
```

#### 3.8.3`__init__()`自定义参数

- `__init__(self)`除了默认参数`self`，还可以设置任意个数的其他自定义形参，
  - 例如：`__init__(self,x,y,z)`
- `__init__`方法 **设置的自定义形参必须和创建对象时传递的参数保持一致**
  - 例如：`对象变量名 = 类名(x,y,z)`
- 开发者可以 **设置自定义形参**，再创建对象时为初始的默认属性提供 **不同的初始值**

示例代码：

```python
class Dog(object):
    """狗类"""
    def __init__(self, _name, _age):
        """
        调用：该方法是在创建每个 Dog 类的实例对象时会被自动调用
        作用：对象的初始化方法，可以在这个方法中给创建对象添加一些初始的属性！！！
        """
        print('======== Dog 类的 __init__ 方法被调用 ========')
        self.name = _name
        self.age = _age

    def show_info(self):
        print(f'我的名字：{self.name}, 年龄：{self.age}')


# 创建一个 dog1 对象
dog1 = Dog('小黑', 2)
print(dog1.name)
print(dog1.age)
dog1.show_info()

print('========== 华丽分割线 ==========')

# 再创建一个 dog2 对象
dog2 = Dog('旺财', 1)
print(dog2.name)
print(dog2.age)
dog2.show_info()
```

运行结果：

```shell
======== Dog 类的 __init__ 方法被调用 ========
小黑
2
我的名字：小黑, 年龄：2
========== 华丽分割线 ==========
======== Dog 类的 __init__ 方法被调用 ========
旺财
1
我的名字：旺财, 年龄：1
```

#### 3.8.4 小结

1. `__init__`方法的作用和特点

   - 特点：创建对象的时候，实例化对象，自动调用`__init__`方法
   - 作用：为创建的实例对象添加属性

2. 不带参数和带参数的`__init__`方法的使用

   ```python
   # 不带参数
   class 类名(object):
       def __init__(self):
           pass
   
   # 实例化对象
   对象名 = 类名()
   
   # 带参数
   class 类名(object):
       def __init__(self, 形参1, 形参2 ……):
           pass
   
   # 实例化对象
   对象名 = 类名(实参1, 实参2，……)
   ```

### 3.9 魔法方法：__str__()

学习目标：

1. 知道`__str__`方法的作用

------

#### 3.9.1 _str__()方法的使用

**将一个实例对象转换为字符串**：

- ```
  str(对象)
  ```

  - 如果类中未定义`__str__()`方法，会看到转换的结果是对象在内存中的地址
  - 如果类中定义了`__str__()`方法，则转换的结果是该类中`__str__()`方法的返回值
  - 注意：`__str__()`方法的返回值必须是 **字符串类型**

**print函数打印实例对象**：

- ```
  print(对象)
  ```

  - 如果类中未定义`__str__()`方法，会看到打印的结果是对象在内存中的地址
  - 如果类中定义了`__str__()`方法，会看到打印结果是该类中`__str__()`方法的返回值
  - 注意：**`print(对象)`打印对象时，隐含着将对象转换为字符串的过程**，print最终打印的就是转换后的结果

示例代码：

```python
class Dog(object):
    """狗类"""
    def __init__(self, _name, _age):
        self.name = _name
        self.age = _age

    def show_info(self):
        print(f'我的名字：{self.name}, 年龄：{self.age}')

    # __str__方法实现
    def __str__(self):
        """__str__魔法方法必须返回一个字符串数据"""
        # return 'hello world'
        return f'我们名字：{self.name}, 年龄：{self.age}'


# 创建 dog1 对象
dog1 = Dog('小黑', 1)

# Dog类中没有实现 __str__ 魔法方法，str(dog1)结果：'<__main__.Dog object at 0x0000024312127A30>'
# Dog类中实现了 __str__ 魔法方法，str(dog1)结果：'我们名字：小黑, 年龄：1'
# 把一个实例对象转换为字符串时，会自动调用对应类中的 __str__ 魔法方法，转换的结果就是 __str__ 魔法方法的返回值
result = str(dog1)
print(result)

# 当我们通过 print 打印一个对象，其实隐含着将对象转换为字符串的过程，print最终打印的是对象转换字符串的那个结果
print(dog1) # 输出结果：'我们名字：小黑, 年龄：1'
```

运行结果：

```shell
我们名字：小黑, 年龄：1
我们名字：小黑, 年龄：1
```

#### 3.9.2 小结

- ```
  __str__
  ```

  方法的作用

  - 把一个实例对象转换为字符串时，会自动调用对应类中的 `__str__`魔法方法，转换的结果就是 `__str__`魔法方法的返回值
  - `__str__()`方法的返回值必须是 **字符串类型**

## 4. 应用：烤地瓜

学习目标：

1. 知道烤地瓜案例的需求和实现思路

------

### 1. 需求说明

![101_地瓜](E:\黑马培训\assets\101_地瓜.jpg)

烤地瓜规则：

1. 地瓜有自己的状态，默认是生的，地瓜可以进行烧烤
2. 地瓜有自己烧烤的总时间，由每次烧烤的时间累加得出
3. 地瓜烧烤时，需要提供本次烧烤的时间
4. 地瓜烧烤时，地瓜状态随着烧烤总时间的变化而改变：[0, 3) 生的、[3, 6) 半生不熟、[6, 8) 熟了、>=8 烤糊了 
5. 输出地瓜信息时，可以显示地瓜的状态和烧烤的总时间

### 2. 面向对象设计

**类图**

![102_地瓜类图1](E:\黑马培训\assets\102_地瓜类图1.png)

#### 2.1 地瓜类

1. 使用 `SweetPotato` 类可以创建 **地瓜对象**

2. 地瓜有两个属性：

   - **状态 state**：字符串
   - **烧烤总时间 cooked_time**：整数

3. 定义 

   ```
   cook
   ```

    方法, 提供参数 

   ```
   time
   ```

    设置 

   本次烧烤的时间

   - 使用 **本次烧烤时间** 对 **烧烤总时间** 进行 **累加**

   - 根据 

     烧烤总时间

     , 设置地瓜的状态：

     - [0, 3) -> 生的
     - [3, 6) -> 半生不熟
     - [6, 8) -> 熟了
     - 大于等于8 -> 烤糊了

#### 2.2 主程序逻辑

1. 创建 **地瓜对象** 
2. **分多次** **烧烤地瓜**
3. 每烧烤一次，**输出地瓜信息**

#### 2.3 示例代码

##### 2.3.1 步骤流程

```Python
"""
# SweetPotato 类的设计
    地瓜有两个属性：
        状态 state：字符串
        烧烤总时间 cooked_time：整数

# 1. 定义__init__方法，添加2个属性
    # 1.1 默认状态state是生的
    # 1.2 默认时间cooked_time是0

# 2. 定义__str__方法
    # 2.1 返回地瓜状态，烧烤总时间

# 3. 定义 cook 方法, 提供参数 time 设置 本次烧烤的时间
    # 3.1 使用 本次烧烤时间 对 烧烤总时间 进行 累加
    # 3.2 根据 烧烤总时间, 设置地瓜的状态：
        [0, 3) -> 生的
        [3, 6) -> 半生不熟
        [6, 8) -> 熟了
        大于等于8 -> 烤糊了

# 4. 主逻辑程序
# 4.1 创建 地瓜对象
# 4.2 分多次烧烤地瓜
# 4.3 每烧烤一次，输出地瓜信息
"""
```

##### 2.3.2 定义地瓜类、通过`__init__（）方法`添加属性

```python
class SweetPotato(object):
    """地瓜类"""
    # 1. 定义__init__方法，添加2个属性
    def __init__(self):
        # 1.1 默认状态state是生的
        self.state = '生的'
        # 1.2 默认时间cooked_time是0
        self.cooked_time = 0
```

##### 2.3.3 定义`__str__()`方法

```python
    # 2. 定义__str__方法
    def __str__(self):
        # 2.1 返回地瓜状态，烧烤总时间
        return f'地瓜状态为：{self.state}, 烧烤总时间为：{self.cooked_time} 分钟'
```

##### 2.3.4 定义"烤地瓜"方法

```python
    # 3. 定义 cook 方法, 提供参数 time 设置 本次烧烤的时间
    def cook(self, time):
        # 3.1 使用 本次烧烤时间 对 烧烤总时间 进行 累加
        self.cooked_time += time

        # 3.2 根据 烧烤总时间, 设置地瓜的状态：
        if 0 <= self.cooked_time < 3:
            self.state = '生的'
        elif 3 <= self.cooked_time < 6:
            self.state = '半生不熟'
        elif 6 <= self.cooked_time < 8:
            self.state = '熟了'
        else:
            self.state = '烤糊了'
```

##### 2.3.5 测试代码

把上面 3 块代码合并为一个程序后，在代码的下面添加以下代码进行测试：

```python
# 4.1 创建一个地瓜类的对象
sp1 = SweetPotato()
# 打印地瓜对象，显示信息
print(sp1)

# 地瓜进行第1次烧烤
sp1.cook(2)
print(sp1)

# 地瓜进行第2次烧烤
sp1.cook(2)
print(sp1)

# 地瓜进行第3次烧烤
sp1.cook(2)
print(sp1)

# 地瓜进行第4次烧烤
sp1.cook(2)
print(sp1)
```

运行结果：

```shell
地瓜状态为：生的，烧烤总时间为：0
地瓜状态为：生的，烧烤总时间为：2
地瓜状态为：半生不熟，烧烤总时间为：4
地瓜状态为：熟了，烧烤总时间为：6
地瓜状态为：烤糊了，烧烤总时间为：8
```

### 3. 拓展功能

#### 3.1 烤地瓜需求拓展

1. 地瓜可以添加佐料,如 盐、孜然、辣酱等
2. 输出地瓜信息时，可以显示地瓜的状态、烧烤总时间、以及添加过的所有佐料

#### 3.2 需求分析

- **每个地瓜记录自己的佐料，定义属性来记录**
- 地瓜可以添加多个佐料，应该定义 **容器类型属性** 来记录添加的佐料
- 地瓜类 应该 **定义方法来实现添加佐料的功能**

![103_地瓜类图2](E:\黑马培训\assets\103_地瓜类图2.png)

#### 3.3 示例代码

##### 3.3.1 步骤流程

```python
# 5. 拓展功能
# 5.1 添加属性 condiments， 列表类型，默认为空列表

# 5.2 修改 __str__ 返回信息，返回增加已添加的佐料信息

# 5.3 定义 add_condiments(self, item), item为添加什么佐料的参数
    # 5.3.1 佐料列表追加元素

# 5.4 再次测试代码，添加佐料，重新打印信息
```

##### 3.3.2 添加 `condiments` 属性

```python
    # 1. 定义__init__方法，添加3个属性
    def __init__(self):
        # 1.1 默认状态state是生的
        self.state = '生的'
        # 1.2 默认时间cooked_time是0
        self.cooked_time = 0
        # 5.1 添加属性 condiments， 列表类型，默认为空列表
        self.condiments = []
```

##### 3.3.3 修改`__str__()`方法，在方法中使用`condiments`属性显示已添加的佐料

```python
    # 2. 定义__str__方法
    def __str__(self):
        # 2.1 返回地瓜状态，烧烤总时间
        # 5.2 修改 __str__ 返回信息，返回增加已添加的佐料信息
        return f'地瓜状态为：{self.state}, 烧烤总时间为：{self.cooked_time} 分钟，添加的佐料为：{self.condiments}'
```

##### 3.3.4 定义`add_condiments()`方法

```python
    # 5.3 定义 add_condiments(self, item), item为添加什么佐料的参数
    def add_condiment(self, item):
        # 5.3.1 佐料列表追加元素
        self.condiments.append(item)
```

##### 3.3.5 再次测试

```python
# 创建一个地瓜类的对象
sp1 = SweetPotato()
# 打印地瓜对象，显示信息
print(sp1)

# 地瓜进行第1次烧烤
sp1.cook(2)
sp1.add_condiment('盐')
print(sp1)

# 地瓜进行第2次烧烤
sp1.cook(2)
sp1.add_condiment('孜然')
print(sp1)

# 地瓜进行第3次烧烤
sp1.cook(2)
sp1.add_condiment('辣椒')
print(sp1)

# 地瓜进行第4次烧烤
sp1.cook(2)
sp1.add_condiment('玛莎拉')
print(sp1)
```

运行结果：

```python
地瓜状态为：生的，烧烤总时间为：0，添加的佐料为：[]
地瓜状态为：生的，烧烤总时间为：2，添加的佐料为：['盐']
地瓜状态为：半生不熟，烧烤总时间为：4，添加的佐料为：['盐', '孜然']
地瓜状态为：熟了，烧烤总时间为：6，添加的佐料为：['盐', '孜然', '辣椒']
地瓜状态为：烤糊了，烧烤总时间为：8，添加的佐料为：['盐', '孜然', '辣椒', '玛莎拉']
```

#### 4. 小结

- 烤地瓜案例的实现思路：

  ```python
  """
  # SweetPotato 类的设计
      地瓜有两个属性：
          状态 state：字符串
          烧烤总时间 cooked_time：整数
  
  # 1. 定义__init__方法，添加2个属性
      # 1.1 默认状态state是生的
      # 1.2 默认时间cooked_time是0
  
  # 2. 定义__str__方法
      # 2.1 返回地瓜状态，烧烤总时间
  
  # 3. 定义 cook 方法, 提供参数 time 设置 本次烧烤的时间
      # 3.1 使用 本次烧烤时间 对 烧烤总时间 进行 累加
      # 3.2 根据 烧烤总时间, 设置地瓜的状态：
          [0, 3) -> 生的
          [3, 6) -> 半生不熟
          [6, 8) -> 熟了
          大于等于8 -> 烤糊了
  
  # 4. 主逻辑程序
  # 4.1 创建 地瓜对象
  # 4.2 分多次烧烤地瓜
  # 4.3 每烧烤一次，输出地瓜信息
  
  # 5. 拓展功能
  # 5.1 添加属性 condiments， 列表类型，默认为空列表
  # 5.2 修改 __str__ 返回信息，返回增加已添加的佐料信息
  # 5.3 定义 add_condiments(self, item), item为添加什么佐料的参数
      # 5.3.1 佐料列表追加元素
  # 5.4 再次测试代码，添加佐料，重新打印信息
  """
  ```

## 5. 应用: 搬家具

学习目标：

1. 知道搬家具案例的需求和实现思路

------

### 5.1 需求说明

![111_搬家具](E:\黑马培训\assets\111_搬家具.jpg)

搬家具规则：

1. 家具分不同的类型，并占用不同的面积
2. 输出家具信息时，显示家具的类型和家具占用的面积
3. 房子有自己的地址和占用的面积
4. 房子可以添加家具，如果房子的剩余面积可以容纳家具，则提示家具添加成功；否则提示添加失败
5. 输出房子信息时，可以显示房子的地址、占地面积、剩余面积

### 5.2 面向对象设计

**类图**

![112_搬家具类图](E:\黑马培训\assets\112_搬家具类图.png)

#### 5.2.1 家具类

1. 使用 `Item` 类可以创建 **家具对象**

2. 家具有两个属性：

   - **家具类型 type**：字符串
   - **家具面积 area**：整数

3. 实现

   ```
   __str__
   ```

   方法

   - 显示家具的 type 和 area 属性

#### 5.2.2 房子类

1. 使用 `Home` 类可以创建 **房子对象**

2. 房子有三个属性：

   - **地址 address**：字符串
   - **房子面积 area**：整数
   - **房子剩余面积 free_area**：整数，默认为房子的面积

3. 实现

   ```
   __str__
   ```

   方法

   - 显示房子的 address 、area、free_area 属性

4. 实现

   ```
   add_item
   ```

   方法，提供

   ```
   item
   ```

   参数来添加家具 

   - 如果 

     可以容纳家具：

     - 打印添加家具的类型和面积
     - **剩余面积** 减少

   - 如果 **不能容纳家具：** 提示家具添加失败

#### 5.2.3 主程序逻辑

1. 创建 **家具对象**, 输出 家具信息 
2. 创建 **房子对象**, 输出 房子信息 
3. **房子添加家具**, 输出 房子信息

#### 5.2.4 示例代码

```python
"""
家具类 Item
# 1. 定义__init__方法，添加2个属性，需要2个形参 _type, _area
    # 1.1 家具类型 type
    # 1.2 家具面积 area

# 2. 实现__str__方法
    # 2.1 返回家具类型和家具面积

房子类 Home
# 1. 定义__init__方法，添加3个属性，需要3个形参
    # 1.1 地址 address
    # 1.2 房子面积 area
    # 1.3 房子剩余面积 free_area，默认为房子的面积

# 2. 实现__str__方法
    # 2.1 返回房子地址、面积、剩余面积信息

# 3. 实现add_item方法，提供item参数来添加家具，item是对象
    # 3.1 如果 房间的剩余面积 >= 家具的面积，可以容纳家具：
        # 3.1.1 打印添加家具的类型和面积
        # 3.1.2 剩余面积 减少
    # 3.2 否则 不能容纳家具：提示家具添加失败

主程序逻辑：
# 1. 创建 家具对象, 输出 家具信息
# 2. 创建 房子对象, 输出 房子信息
# 3. 房子添加家具, 输出 房子信息
"""


class Item(object):
    """家具类"""
    def __init__(self, _type, _area):
        """
        作用：家具对象的初始化方法，给家具对象添加初始的 type 和 area 两个属性
        形参：
            * _type：接收家具的类型
            * _area：接收家具占用的面积
        """
        self.type = _type
        self.area = _area

    def __str__(self):
        """
        作用：返回一个字符串，包含家具对象的类型和占用的面积
        """
        return f'家具类型：{self.type}，占用面积：{self.area}'


class Home(object):
    """房子类"""
    def __init__(self, _address, _area):
        """
        作用：房子对象的初始化方法，给创建的房子对象添加初始的 address、area 和 free_area 三个属性
        形参：
            * _address：接收房子的地址
            * _area：接收房子的占地面积
        """
        self.address = _address
        self.area = _area
        self.free_area = _area # 刚创建房子对象时，房子的剩余面积等于占地面积

    def __str__(self):
        """
        作用：返回一个字符串，包含房子的地址、占地面积、剩余面积
        """
        return f'房子的地址：{self.address}，占地面积：{self.area}，剩余面积：{self.free_area}'

    def add_item(self, item):
        """
        作用：向房子中添加家具
        形参：
            * item：接收要添加的【家具对象】
        """
        # 判断房子是否能够容纳家具
        if self.free_area >= item.area:
            print(f'{item.type}家具添加成功')
            # 减少房子的剩余面积
            self.free_area -= item.area
        else:
            print(f'{item.type}家具添加失败')


# 首先创建一个房子对象
home1 = Home('上海浦东', 100)
print(home1)

# 创建一个家具对象
item1 = Item('椅子', 5)
# 向房子中添加家具
home1.add_item(item1)
print(home1)

# 再创建一个家具对象
item2 = Item('桌子', 20)
# 向房子中添加家具
home1.add_item(item2)
print(home1)

# 再创建一个家具对象
item3 = Item('多人床', 75)
home1.add_item(item3)
print(home1)
```

运行结果：

```python
房子的地址：上海浦东，占地面积：100，剩余面积：100
椅子家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：95
桌子家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：75
多人床家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：0
```

### 5.3 拓展功能

**需求：输出房子时，显示包含的所有家具的类型**

- `Home`类中添加 `item_list` 属性(家具类型列表)，用于记录所有家具对象
- `Home`类的 `add_item` 方法中, 将添加成功的 **家具类型** 添加到 `item_list` 中
- `Home`类的 `__str__` 方法中, 打印家具的类型

示例代码：

```python
class Item(object):
    """家具类"""
    def __init__(self, _type, _area):
        """
        作用：家具对象的初始化方法，给家具对象添加初始的 type 和 area 两个属性
        形参：
            * _type：接收家具的类型
            * _area：接收家具占用的面积
        """
        self.type = _type
        self.area = _area

    def __str__(self):
        """
        作用：返回一个字符串，包含家具对象的类型和占用的面积
        """
        return f'家具类型：{self.type}，占用面积：{self.area}'


class Home(object):
    """房子类"""
    def __init__(self, _address, _area):
        """
        作用：房子对象的初始化方法，给创建的房子对象添加初始的 address、area 和 free_area 三个属性
        形参：
            * _address：接收房子的地址
            * _area：接收房子的占地面积
        """
        self.address = _address
        self.area = _area
        self.free_area = _area # 刚创建房子对象时，房子的剩余面积等于占地面积
        self.item_list = [] # 刚创建房子对象时，还没有添加家具，item_list值为[]

    def __str__(self):
        """
        作用：返回一个字符串，包含房子的地址、占地面积、剩余面积
        """
        return f'房子的地址：{self.address}，占地面积：{self.area}，剩余面积：{self.free_area}，家具类型：{self.item_list}'

    def add_item(self, item):
        """
        作用：向房子中添加家具
        形参：
            * item：接收要添加的【家具对象】
        """
        # 判断房子是否能够容纳家具
        if self.free_area >= item.area:
            print(f'{item.type}家具添加成功')
            # 减少房子的剩余面积
            self.free_area -= item.area
            # 保存添加的家具类型
            self.item_list.append(item.type)
        else:
            print(f'{item.type}家具添加失败')


# 首先创建一个房子对象
home1 = Home('上海浦东', 100)
print(home1)

# 创建一个家具对象
item1 = Item('椅子', 5)
# 向房子中添加家具
home1.add_item(item1)
print(home1)

# 再创建一个家具对象
item2 = Item('桌子', 20)
# 向房子中添加家具
home1.add_item(item2)
print(home1)

# 再创建一个家具对象
item3 = Item('多人床', 75)
home1.add_item(item3)
print(home1)
```

运行结果：

```python
房子的地址：上海浦东，占地面积：100，剩余面积：100，家具类型：[]
椅子家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：95，家具类型：['椅子']
桌子家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：75，家具类型：['椅子', '桌子']
多人床家具添加成功
房子的地址：上海浦东，占地面积：100，剩余面积：0，家具类型：['椅子', '桌子', '多人床']
```

### 5.4 小结

- 搬家具案例的实现思路：

  ```python
  """
  家具类 Item
  # 1. 定义__init__方法，添加2个属性，需要2个形参 _type, _area
      # 1.1 家具类型 type
      # 1.2 家具面积 area
  
  # 2. 实现__str__方法
      # 2.1 返回家具类型和家具面积
  
  房子类 Home
  # 1. 定义__init__方法，添加3个属性，需要3个形参
      # 1.1 地址 address
      # 1.2 房子面积 area
      # 1.3 房子剩余面积 free_area，默认为房子的面积
  
  # 2. 实现__str__方法
      # 2.1 返回房子地址、面积、剩余面积信息
  
  # 3. 实现add_item方法，提供item参数来添加家具，item是对象
      # 3.1 如果 房间的剩余面积 >= 家具的面积，可以容纳家具：
          # 3.1.1 打印添加家具的类型和面积
          # 3.1.2 剩余面积 减少
      # 3.2 否则 不能容纳家具：提示家具添加失败
  
  主程序逻辑：
  # 1. 创建 家具对象, 输出 家具信息
  # 2. 创建 房子对象, 输出 房子信息
  # 3. 房子添加家具, 输出 房子信息
  
  输出房子时，显示包含的所有家具的类型
  # a. Home类中添加 item_list 属性(家具类型列表)，用于记录所有家具类型
  # b. Home类的 add_item 方法中, 将添加成功的 家具类型 添加到 item_list 中
  # c. Home类的 __str__ 方法中, 打印家具的类型列表
  """
  ```

## 6. 面向对象day01-内容总结

1）面向过程和面向对象的编程思维【初步理解】

面向过程：看重功能实现的步骤：第一步、第二步、第三步...

* 加洗衣液
* 加水
* 洗衣服
* 脱水
* 烘干

面向对象：分解对象(功能中涉及都哪些对象，每个对象应该干什么)，站在对象配合的角度思考功能的实现

* 分解对象：人和洗衣机
  * 人：加洗衣液、加水
  * 洗衣机：洗衣服、脱水、烘干
* 功能实现
  * 人.加洗衣液
  * 人.加水
  * 洗衣机.洗衣服
  * 洗衣机.脱水
  * 洗衣机.烘干

2）类和对象【初步理解】

* 类是一个抽象的概念，相当于一张图纸
  * 属性：代表的是类创造的对象有哪些数据需要保存
  * 方法：代表的是类创造的对象能够干什么
* 对象一个实体，是根据类造出来的实物
  * 一个类可以创造很多个对象

3）类的定义【重点】

```python
class 类名(object):
    def 方法名(self):
        pass
    
    def 方法名(self):
        pass
```

4）创建对象和对象调用类中的方法【重点】

```python
对象变量名 = 类名()
# 本质：方法名(对象)
对象变量名.方法名()
```

5）对象的属性添加/修改和获取【重点】

```python
# 第一次做是添加属性，再做就是修改属性的值
对象变量名.属性名 = 值

# 获取对象的属性的值
对象变量名.属性名
```

6）self是什么和self的作用【重点】

* self就是实例对象本身：通过哪个对象调用了类中的方法，方法中的self形参就是哪个对象
* self的作用就是区分不同的实例对象，获取不同对象的属性或调用不同对象的方法

7）魔法方法：`__init__` 【重点】

* 什么时候调用？每创建一个实例对象，该类的`__init__`就会被自动调用一次
* 有什么用？给创建的每个实例对象添加一些初始的属性，也叫对象的初始化方法

```python
class 类名(object):
	def __init__(self, 形参1, 形参2, ...):
		self.属性名 =  形参1
		self.属性名 =  形参2
		...
        
对象变量名 = 类名(实参1, 实参2, ...)
```

8）魔法方法：`__str__`【重点】

* 作用：返回一个字符串，包含实例对象的一些属性的信息，方法通过`print(对象)`来查看对象的属性信息
* 注意：`__str__`必须有返回值，而且必须一个字符串

9）烤地瓜案例【重点】

目标：

* 今天面向对象语法的练习
* 对面向对象编程步骤大体认识

```
面向对象编程的步骤：
1）分解对象：根据要解决的问题，分析问题中涉及到哪些对象
    地瓜、烤箱、人
    由于我们的这个案例很简单，咱们这里只会拆解出一个对象：地瓜对象

2）抽象出类：分解出的每个对象抽象成一个类，分析类的对象有哪些属性和方法
    地瓜类：分析地瓜对象的属性（对象需要保存的数据）和方法（对象能够干什么）
    
3）根据第2）的抽象定义类
4）通过类创建对象，实现功能
```

