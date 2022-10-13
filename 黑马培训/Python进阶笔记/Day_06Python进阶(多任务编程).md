# 1. 多任务编程

**学习目标**：

- 掌握多任务编程中进程和线程的概念

## 知识点1：多任务的基本概念【掌握】

思考：

```
以我们现在所学的知识，能让两个函数同时执行吗？
```

问题：

```
1.什么是多任务？多任务有什么优点？
2.多任务是如何实现的？
3.多任务是如何执行的？
```

**1. 什么是多任务？**

多任务是指多个任务同时执行。比如：百度网盘同时下载多部电影；一边听歌软件听歌，一边写代码；

多任务的最大好处是充分**利用CPU资源，提高程序的执行效率**。

**2. 多任务是如何实现的？**

多任务的**实现方式**有 2 种：**多进程和多线程**。

**3. 多任务是如何执行的？**

多任务的**执行方式**分成 2 种：**并发和并行**。

并发：在一段时间内**快速交替**去执行多个任务

![image-20220822074543654](E:\黑马培训\Python进阶笔记\assets\image-20220822074543654.png)

并行：在一段时间内**真正的同时一起**执行多个任务

![image-20220822074558684](E:\黑马培训\Python进阶笔记\assets\image-20220822074558684.png)

# 2. 多进程编程

**学习目标**：

- 熟悉python中多进程的使用

## 知识点2：进程的基本概念【掌握】

**进程（Process）是资源分配的最小单位**，**它是操作系统进行资源分配的基本单位**，通俗理解：**一个正在运行的程序就是一个进程。例如：正在运行的QQ、微信等他们都是一个进程。**

> **一个程序运行后至少有一个进程**

**多进程可以完成多任务，每个进程就好比一家独立的公司，每个公司都各自在运营，每个进程也各自在运行，执行各自的任务。**

**多进程的作用**：

![image-20220822074630035](E:\黑马培训\Python进阶笔记\assets\image-20220822074630035.png)

## 知识点3：Python中多进程的基本使用【熟悉】

**多进程的创建步骤**：

1）导入进程包

```python
import multiprocessing
```

2）通过进程类创建进程对象 

```python
进程对象 = multiprocessing.Process(target=进程执行的任务函数)

其他参数：
- group：指定进程组，目前只能使用None
- target：执行的目标任务名
- name：进程名字
- args：以元组方式给执行任务传参
- kwargs：以字典方式给执行任务传参
```

3）启动进程执行任务

```python
进程对象.start()
```

**多进程示例代码**：

```python
"""
多进程的基本使用
学习目标：能够使用多进程同时执行两个不同的任务函数
"""
import time
# 导入进程包
import multiprocessing


# 跳舞函数
def dance():
    for i in range(5):
        print('跳舞中...')
        time.sleep(1)


# 唱歌函数
def sing():
    for i in range(5):
        print('唱歌中...')
        time.sleep(1)


if __name__ == '__main__':
    # 创建一个进程，执行 dance 函数
    # 注意：target指定的是函数名或方法名，切忌！！！不要再函数名或方法名后加()
    dance_process = multiprocessing.Process(target=dance) 

    # 再创建一个进程，执行 sing 函数
    sing_process = multiprocessing.Process(target=sing)

    # 启动这两个进程
    dance_process.start()
    sing_process.start()
```

## 知识点4：进程关系-主进程和子进程【常握】

思考题：

```
1. 上面的代码执行时，一共有几个python进程？
一共有3个python进程，一个主进程，两个子进程
```

补充命令：

- tasklist：查看 Windows 系统中整体执行的进程
- tasklist | findstr python：查看 Windows 系统中正在运行的 python 进程

获取进程编号函数：

```python
import os
os.getpid()：获取当前进程的编号
os.getppid()：获取当前进程父进程的编号
```

## 知识点5：进程执行带有参数的任务【熟悉】

Process 类执行任务并给任务传参数有两种方式：

- args 表示以元组的方式给执行任务传参：传参一定要和参数的顺序保持一致
- kwargs 表示以字典方式给执行任务传参：传参字典中的key一定要和参数名保持一致

**示例代码**：

```python
"""
进程执行带有参数的任务(函数)
学习目录：能够使用多进程执行带有参数的任务
"""

import multiprocessing
import time


# 带有参数的任务(函数)
def task(count):
    for i in range(count):
        print('任务执行中...')
        time.sleep(0.2)
    else:
        print('任务执行完成')


if __name__ == '__main__':
    # 创建一个进程，执行 task 任务函数
    # 通过元祖指定任务函数的参数：args=(5, )，按照参数顺序指定参数的值
    # sub_process = multiprocessing.Process(target=task, args=(5, ))

    # 通过字典指定任务函数的参数：kwargs={'count': 3}，按照参数名称指定参数的值
    sub_process = multiprocessing.Process(target=task, kwargs={'count': 3})

    # 启动进程
    sub_process.start()
```

## 知识点6：进程使用的 2 个注意点【常握】

进程使用的注意点介绍：

1）进程之间不共享全局变量

> 注意：创建子进程时，子进程会把主进程的东西全部复制一份

2）主进程会等待所有的子进程执行结束再结束







**示例代码1**：

```python
# 注意点1：进程之间不共享全局变量
import multiprocessing
import time

# 定义全局变量
g_list = []


# 添加数据的函数
def add_data():
    for i in range(5):
        g_list.append(i)
        print('add：', i)
        time.sleep(0.2)

    print('add_data：', g_list)


# 读取数据的函数
def read_data():
    print('read_data：', g_list)


if __name__ == '__main__':
    # 创建添加数据的子进程
    add_data_process = multiprocessing.Process(target=add_data)
    # 创建读取数据的子进程
    read_data_process = multiprocessing.Process(target=read_data)

    # 启动添加数据子进程
    add_data_process.start()
    # 主进程等待 add_data_process 执行完成，再向下继续执行
    add_data_process.join()
    # 启动读取数据子进程
    read_data_process.start()

    print('main：', g_list)
```

**示例代码2**：

```python
# 注意点2：主进程会等待所有的子进程执行结束再结束
import multiprocessing
import time


# 任务函数
def task():
    for i in range(20):
        print('任务执行中...')
        time.sleep(0.2)


if __name__ == '__main__':
    # 创建子进程并启动
    sub_process = multiprocessing.Process(target=task)
    sub_process.start()

    # 主进程延时 1s
    time.sleep(1)
    print('主进程结束！')
    # 退出程序
    exit()
```

## 知识点7：守护进程和终止子进程【熟悉】

如何让主进程执行结束时，子进程就结束执行？

1）方式1：将子进程设置为守护进程

```python
进程对象.daemon = True # 注意点：设置守护进程必须在子进程启动之前
```

2）方式2：主进程结束时直接终止子进程

```
进程对象.terminate()
```

**示例代码1**：将子进程设置为守护进程

```python
# 任务函数
def task():
    for i in range(10):
        print('任务执行中...')
        time.sleep(0.2)


if __name__ == '__main__':
    # 创建子进程并启动
    sub_process = multiprocessing.Process(target=task)
    # TODO：设置子进程为守护进程
    sub_process.daemon = True # 注意点：设置守护进程必须在子进程启动之前

    sub_process.start()

    # 主进程延时 1s
    time.sleep(1)
    print('主进程结束！')
    # 退出程序
    exit()
```

**示例代码2**：直接终止子进程

```python
import multiprocessing
import time


# 任务函数
def task():
    for i in range(10):
        print('任务执行中...')
        time.sleep(0.2)


if __name__ == '__main__':
    # 创建子进程并启动
    sub_process = multiprocessing.Process(target=task)
    sub_process.start()

    # 主进程延时 1s
    time.sleep(1)
    print('主进程结束！')
    # TODO: 终止子进程
    sub_process.terminate()

    # 退出程序
    exit()
```

# 3. 多线程编程

**学习目标**：

- 熟悉python中多线程的使用

## 知识点8：线程的基本概念【掌握】

线程是进程中执行代码的一个分支，每个执行分支（线程）要想工作执行代码需要 CPU 进行调度 ，也就是说**线程是 CPU 调度的基本单位**，每个进程默认都有一个线程，而这个线程就是我们通常说的主线程。

> 可以把进程理解为公司，线程理解为公司中的员工，一个公司至少有一个员工。公司(进程)负责提供资源，员工(线程)负责实际干活。

![image-20220822074720111](E:\黑马培训\Python进阶笔记\assets\image-20220822074720111.png)

## 知识点9：Python中多线程的基本使用【熟悉】

**多线程的创建步骤**：

1）导入线程模块

```python
import threading
```

2）通过线程类创建线程对象 

```python
线程对象 = threading.Thread(target=进程执行的任务函数)

其他参数：
- group: 线程组，目前只能使用None
- target: 执行的目标任务名
- args: 以元组的方式给执行任务传参
- kwargs: 以字典方式给执行任务传参
- name: 线程名，一般不用设置
```

3）启动线程执行任务

```python
线程对象.start()
```

**多线程示例代码**：

```python
"""
多线程的基本使用
学习目标：能够使用多线程同时执行两个不同的函数
"""

import time
# 导入模块
import threading


# 跳舞任务函数
def dance():
    for i in range(5):
        print('正在跳舞...%d' % i)
        time.sleep(1)


# 唱歌任务函数
def sing():
    for i in range(5):
        print('正在唱歌...%d' % i)
        time.sleep(1)


if __name__ == '__main__':
    # 创建一个线程，执行 dance 任务函数
    dance_thread = threading.Thread(target=dance)

    # 再创建一个线程，执行 sing 任务函数
    sing_thread = threading.Thread(target=sing)

    # 启动这两个线程
    dance_thread.start()
    sing_thread.start()
```

## 知识点10：线程执行带有参数的任务【熟悉】

Thread 类执行任务并给任务传参数有两种方式：

- args 表示以元组的方式给执行任务传参：传参一定要和参数的顺序保持一致
- kwargs 表示以字典方式给执行任务传参：传参字典中的key一定要和参数名保持一致

**示例代码**：

```python
"""
线程执行带有参数的任务(函数)
学习目录：能够使用多线程执行带有参数的任务
"""
# 导入线程模块
import threading
import time


# 带有参数的任务(函数)
def task(count):
    for i in range(count):
        print('任务执行中...')
        time.sleep(0.2)
    else:
        print('任务执行完成')


if __name__ == '__main__':
    # 创建一个线程，执行 task 任务函数
    # sub_thread = threading.Thread(target=task, args=(3, ))
    sub_thread = threading.Thread(target=task, kwargs={'count': 5})
    # 启动线程
    sub_thread.start()
```

## 知识点11：线程使用的 3 个注意点【常握】

线程使用的注意点介绍：

1）线程之间执行是无序的

2）主线程会等待所有的子线程执行结束再结束

3）线程之间共享全局变量

**示例代码1**：线程之间执行是无序的

```python
import threading
import time


def task():
    time.sleep(1)
    print(f'当前线程：{threading.current_thread().name}')


if __name__ == '__main__':
    for i in range(5):
        sub_thread = threading.Thread(target=task)
        sub_thread.start()
```

**示例代码2**：主线程会等待所有的子线程执行结束再结束

```python
import threading
import time


def task():
    for i in range(5):
        print('任务执行中...')
        time.sleep(0.5)


if __name__ == '__main__':
    # 创建子线程
    sub_thread = threading.Thread(target=task)
    sub_thread.start()

    # 主进程延时 1s
    time.sleep(1)
    print('主线程结束！')
```

**示例代码3**：线程之间共享全局变量

```python
import threading
import time

# 定义全局变量
g_list = []


# 添加数据的函数
def add_data():
    for i in range(5):
        g_list.append(i)
        print('add：', i)
        time.sleep(0.2)

    print('add_data：', g_list)


# 读取数据的函数
def read_data():
    print('read_data：', g_list)


if __name__ == '__main__':
    # 创建添加数据的子线程
    add_data_thread = threading.Thread(target=add_data)
    # 创建读取数据的子线程
    read_data_thread = threading.Thread(target=read_data)

    # 启动添加数据子线程
    add_data_thread.start()
    # 主线程等待 add_data_thread 执行完成，再向下继续执行
    add_data_thread.join()
    # 启动读取数据子线程
    read_data_thread.start()

    print('main：', g_list)
```

## 知识点12：守护线程设置【熟悉】

如何让主线程执行结束时，子线程就结束执行？

答：将子线程设置为守护线程

```python
方式1：子线程对象.daemon = True
方式2：子线程对象.setDaemon(True)
```

**示例代码**：

```python
import threading
import time


# 任务函数
def task():
    for i in range(10):
        print('任务执行中...')
        time.sleep(0.2)


if __name__ == '__main__':
    # 创建子线程并启动
    sub_thread = threading.Thread(target=task)
    # TODO：设置子线程为守护线程
    # sub_thread.daemon = True
    sub_thread.setDaemon(True)

    sub_thread.start()

    # 主线程延时 1s
    time.sleep(1)
    print('主线程结束！')
```

## 知识点13：线程的资源共享问题【常握】

**多线程会共享全局变量，当多个线程同时操作同一个共享的全局变量时，可能会造成错误的结果！**

示例代码：

```python
import threading

# 定义全局变量
g_num = 0


def sum_num1():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        g_num += 1

    print('sum1：', g_num)


def sum_num2():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        g_num += 1

    print('sum2：', g_num)


if __name__ == '__main__':
    # 创建两个线程
    first_thread = threading.Thread(target=sum_num1)
    second_thread = threading.Thread(target=sum_num2)

    # 启动两个线程
    first_thread.start()
    second_thread.start()
```

思考题：

```bash
上面的代码执行完成之后，为什么 g_num 的结果不是 200000？
```



## 知识点14：线程资源共享问题解决：线程等待vs互斥锁【熟悉】

思考题：

```
如何解决线程资源共享出现的错误问题？
答：线程同步：保证同一时刻只能有一个线程去操作共享资源(全局变量)

线程同步的方式：
1）线程等待(join)
2）互斥锁
```

**线程等待**：等一个线程完全执行结束之后，再执行另外一个线程。

```python
import threading

# 定义全局变量
g_num = 0


def sum_num1():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        g_num += 1

    print('sum1：', g_num)


def sum_num2():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        g_num += 1

    print('sum2：', g_num)


if __name__ == '__main__':
    # 创建两个线程
    first_thread = threading.Thread(target=sum_num1)
    second_thread = threading.Thread(target=sum_num2)

    # 启动两个线程
    first_thread.start()
    # 线程等待：等待 first_thread 执行完成，主线程的代码再继续向下执行
    first_thread.join()

    second_thread.start()
```

**互斥锁**：操作共享资源时，多个线程去抢同一把"锁"，抢到锁的线程执行，没抢到锁的线程会阻塞等待

```python
import threading

# 定义全局变量
g_num = 0

# 创建一个全局的互斥锁
lock = threading.Lock()


def sum_num1():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        # 加锁：拿到锁的线程代码可以继续向下执行，拿不到锁的线程代码会阻塞等待
        lock.acquire()
        g_num += 1
        # 释放锁
        lock.release()

    print('sum1：', g_num)


def sum_num2():
    global g_num
    # 循环一次给全局变量加1
    for i in range(1000000):
        # 加锁：拿到锁的线程代码可以继续向下执行，拿不到锁的线程代码会阻塞等待
        lock.acquire()
        g_num += 1
        # 释放锁
        lock.release()

    print('sum2：', g_num)


if __name__ == '__main__':
    # 创建两个线程
    first_thread = threading.Thread(target=sum_num1)
    second_thread = threading.Thread(target=sum_num2)

    # 启动两个线程
    first_thread.start()
    second_thread.start()
```

## 知识点15：进程和线程对比【熟悉】

**关系对比**：

1. 线程是依附在进程里面的，没有进程就没有线程
2. 一个进程默认提供一条线程，进程可以创建多个线程

**区别对比**：

**操作系统分配资源是以进程为单位的**

1. **进程是操作系统资源分配的基本单位，线程是CPU调度的基本单位**
2. 线程不能够独立执行，必须依存在进程中
3. 创建进程的资源开销要比创建线程的资源开销要大
4. 进程之间不共享全局变量，线程之间共享全局变量，但是要注意资源竞争的问题
5. 多进程开发比单进程多线程开发稳定性要强

**优缺点对比**：

进程优缺点：

- 优点：可以用多核
- 缺点：资源开销大

线程优缺点:

- 优点：资源开销小
- 缺点：不能使用多核（**仅针对Python语言**）

**扩展知识**：

GIL：全局解释器锁(互斥锁)，python中的任何线程要想执行，必须先拿到GIL锁

在 CPython 中，由于存在 [全局解释器锁](https://docs.python.org/zh-cn/3/glossary.html#term-global-interpreter-lock)，同一时刻只有一个线程可以执行 Python 代码（虽然某些性能导向的库可能会去除此限制）。 如果你想让你的应用更好地利用多核心计算机的计算资源，推荐你使用 [`multiprocessing`](https://docs.python.org/zh-cn/3/library/multiprocessing.html#module-multiprocessing) 或 [`concurrent.futures.ProcessPoolExecutor`](https://docs.python.org/zh-cn/3/library/concurrent.futures.html#concurrent.futures.ProcessPoolExecutor)。 但是，如果你想要同时运行多个 I/O 密集型任务，则多线程仍然是一个合适的模型。