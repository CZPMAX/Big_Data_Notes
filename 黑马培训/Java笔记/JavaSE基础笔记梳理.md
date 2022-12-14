# JavaSE基础笔记

## 1. JDK的使用

### 1.1 什么是JDK

jdk是就java的开发环境,是整个java的核心,其中包括：

1. Java编译器
2. java运行工具
3. java文档生成工具
4. java打包工具等

**JRE**是Java的运行环境，给普通用户使用的，用户只要运行事先编写好程序就好，不需要自己动手编写程序，jre里面有运行工具，不包含编译工具，sun公司的**JDK**工具自带了一个**JRE**工具，也就是说开发环境里面包含运行环境

- **bin目录**：用于存放一些可执行的程序

​                   **javac.exe**(java编译器)：是java的编译工具，它可以将编写好的java文件编译成java字节码文件（可执行的java程序）.class文件，原来的文件是.java

​                   **java.exe**(java运行工具)： 会启动一个java虚拟机（JVM）进程，JVM相当于一个虚拟的操作系统，它们专门负责运行由java编译器生成的字节码文件（.class文件）

​                   **jar.exe**(打包工具)

​                   **javadoc.exe**(文档生成工具)等

- **db目录**：是一个小型的数据库

从JDK 6.0开始，java中引入了一个新的成员JavaDB，这是一个纯java实现，开源的数据库管理系统，不仅很轻便，还支持JDBC4.0所有的规范，在学习JDBC时，不需要额外的安装一个数据库软件，直接使用JavaDB即可

- **include目录**:JDK是通过C和C++实现的，因此在启动时就需要引入一些C语言的头文件，该目录就是用来存放这些头文件的
- **jre目录**：java程序运行时环境，此目录是java运行时环境的根目录，它包含java虚拟机，运行时的类包，java应用启动器以及一个bin目录，但不包含开发环境中的开发工具
- **lib目录**：是java类库或库文件，是开发工具使用的归档包文件
- **src.zip文件** src.zip为src文件夹的压缩文件，src中存放的是**JDK核心类的源代码**，通过该文件可以查看java基础类的源代码

## 2. 入门微解小知识

- **class**是java中的一个**关键字**，它用于定义一个类，**在java中类相当于一个小程序，所有的代码都要在类中书写**

- main方法是java程序的执行入口

## 3. java编程基础

### 3.1 java代码基本语法

修饰符 class 类名{

​			程序代码

}

**注意的几个关键点：**

1. java中的程序代码可分为**结构定义语句**和**功能执行语句**

- **结构定义语句**：用于声明一个类或者方法

- **功能执行语句：**用于实现具体功能，每条功能执行语句的最后都用（；）结束，例子：

```java
System.out.print('helloworld');
```

2. java语言是严格区分大小写的，在定义类时，不能将class写成Class否则编译会报错，其次computer和Computer是两个完全不同的符号
3. java没有严格的格式要求，但是我们还是要注意自己书写的代码格式，要美观
4. java中一句连续的字符串不能分开在两行中书写，下列语句会报错

```java
System.out.print("hello
world");
```

**利用+号把两个字符串连接起来就不会报错**

### 3.2 java中的注释

**1. 单行注释**

通常用于对程序中的某一段代码进行解释：用符号//,具体格式如下：

```java
int p = 10;   //定义一个整型变量
```

**2. 多行注释**

解释说明的内容为多行 以符号/*开头，以符号    */结尾

**3. 文档注释**

文档注释是对一段代码的解释说明 以/** 开头  */结尾

### 3.3 java中的标识符

在编程过程中需要在程序中定义一些符号来标记一些名称如包名。类名，方法名，参数名，变量名等，这些符号被称为标识符

java中的标识符可以由任意顺序的大小写字母，数字，下划线（_)和美元符号（￥）组成，但是**不能由数字开头，不能是java中的关键字**

**定义标识符时应该遵循以下规则：**

1. 包名所有字母一律小写，例如cn, itcast, tset
2. 类名和接口名每个单词的首字母都要大写例如：ArrayList, Iterator
3.  变量名和方法名的第一个单词首字母小写，从第二个单词开始每个单词首字母大写

```java
// getLineNumber   lineNumber
```

4. 常量名所有字母都大写,单词之间用下划线连接

```java
// DAY_OF_MONTH
```

5. 在程序中,应该做到用有意义的英文单词来定义标识符,使程序便于阅读例如:userName 表示用户名  passWord表示密码

### 3.4 java中的关键字

- 关键字都是小写的
- 标识符不能与关键字重名

**关键字清单:**

```java
abstract  boolean break byte case catch char const class continue default do double else extends final finally float for goto if implements import instanceof
int interface long native new package private protected public return short static strictfp super swicth this throw throws transient try void volatile
while synchronized
```

### 3.5 java中的常量

常量就是在程序中固定不变的值,是不能改变的数据

在java中常量包括:**整型常量,浮点数常量,布尔常量,字符常量等**

- **整型常量**是数据类型的数据,有二进制,八进制,十进制和十六进制4种表示方法:

1. **二进制**,由0和1组成的数字序列,在Jdk7.0中允许使用字面值来区分二进制数,前面要有0b或者0B开头,目的是为了和十进制作区分如:

0b01101100, 0B10110101

2. **八进制**,以0开头并且后面由0~7范围内(包括0 和 7)的整数组成的数字序列如:0342
3. **十进制**,由0~9范围内(包括0 和 9)的整数组成的数字序列如:198
4. **十六进制**,以0x或者0X开头并且其后由0~9,A~F(包括0和9,A和F)组成的数字序列,如0x25AF

**注意:**

程序中为了标明不同的进制,数据都有特定的标识

八进制必须以0开头,0711 0123

十六进制必须以0x或者0X开头如 0xaf3, 0Xff

整数以十进制表示的时候第一位不能是0,除0本身以外

例如十进制的127

二进制是01111111

八进制是0177

十六进制是0X7F或者0x7F

```jaVA
/*八进制转换成十进制
就是8的零次方乘7加8的一次方乘7加8的平方乘1 = 127*/
```

**十六进制转二进制**

**二进制每4位代表一位的十六进制数**：

**二进制与十六进制数值对应表**

![image-20220810201437690](E:\黑马培训\assets\image-20220810201437690.png)

例子：

将一个二进制数101001010110转为十六进制数，具体步骤为：

1. 每四位分成一段：1010 0101 0110
2. 将每段的数值分别查表替换，结果如下：

​     1010 --> A

​     0101 --> 5

​     0110 --> 6

3. 将替换的结果进行组合，转换结果为0xA56 或 0XA56  十六进制必须以0X 或者 0x 开头 恒定不变
4. 反过来操作一样

**二进制转八进制**

数值对应表：

![image-20220810201950306](E:\黑马培训\assets\image-20220810201950306.png)

例子：将一个二进制数转换为八进制数，二进制的每三位代表八进制的一位，二进制数100101010转八进制， 具体步骤如下：

1. 将每三位分成一段，结果为： 100 101 010
2. 每段的数值分别对应结果如下：

​    100 --> 4

​    101--> 5

​    010 -->2

3. 将替换结果进行组合，转换结果为0452（注意八进制必须以0开头）

**浮点数常量:**就是在数学中用到的小数,分为单精度和双精度,其中单精度后面加f或者F结尾,双精度后面加D或者d结尾,不使用标号结尾,java虚拟机JVM默认数据为双精度浮点数

​       浮点数常量还可以通过指数的方式展现 5.022e+23f

- **字符常量**: 用于表示一个字符,要用单引号括起来,里面可以是英文字母,数字,标点符号,以及转义序列来表示的特殊字符

```java
'a' 'l' 'm' '\n' '\u0000' 'r'
```

- **字符串常量**：用于表示一连串的连续字符，要用双引号括起来

```java
"hello world"
```

- **布尔常量**：只有两个值True和False，用来区分一个事物的真假

- **null常量**：只有一个值null，表示对象的引用为空

   多学一招，在字符常量中反斜杠（  \  ) 是一个特殊字符，被称为转义字符，转义后的字符通常用于表示一个不可见的字符或者具有特殊含义的字符

- \n 换行符 换到下一行的开头
- \r 回车符 把光标定位到当前行的开头，不会跳到下一行
- \t 制表符 将光标移到下一个制表符的位置，就像在文档中使用Tab键一样
- \b 表示退格符号，就像键盘上的Backspace键
- \ ‘ 表示单引号
- \ ” 表示双引号
- \ \ 表示反斜杠\

### 3.6 java中的变量

