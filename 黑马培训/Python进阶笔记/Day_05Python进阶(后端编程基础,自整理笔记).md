# 1. 应用程序架构

**二进制数据都有对应的原来的数据类型：比如字符串，图片等** 解码只能转回原来的数据类型

**在开始学习前先明确的一点：**

1. TCP的通信  我们要创建两种类型的服务器 TCP服务器与TCP客户端

   **TCP服务器的开发流程：**

   ```python
   # 1）创建服务端监听套接字对象，门童迎接客户端发来的信息，带给专属的服务员
   server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
   # 2）绑定端口号
   server_socket.bind(('127.0.0.1', 8080))
   # 3）设置监听  同一时间最多能接收多少个客户端请求
   server_socket.listen(128)
   # 4）等待接受客户端的连接请求
   print('服务端等待接收客户端的连接请求数据...')
   # 门童把接待的客户端数据带来了，给service_client_socket这个socket类的对象，打通了与客户端的连接
   # 它的作用是负责和连接的客户端进行数据的接收和发送
   service_client_socket, ip_port = server_socket.accept()
   
   # 5）接收数据  一次recv最多接收1024个字节 客户端没有发送信息来时会阻塞等待
   recv_msg = service_client_socket.recv(1024)
   print(f'客户端发送的信息为:{recv_msg.decode()}')
   # 6）发送数据 给给客户端发回响应的消息
   send_msg = input('请输入给客户端回应的消息: ') # str
   # 原本就是字符串或者文本数据的经历过二进制编码后，才能接着解码为文本数据，否则不行
   service_client_socket.send(send_msg.encode())
   # 7）关闭套接字 关闭服务端的socket
   service_client_socket.close()
   server_socket.close()
   ```

​       **TCP客户端开发流程：**

```python
# 1.创建一个客户端socket   ()括号里面都是属于socket的方法 第一个参数是使用的ip地址形式，
# 第二个参数是使用的传输协议 在这里分别使用的是ipv4和tcp协议 
client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
# 2.通过客户端的socket连接服务器端程序(connect)
client_socket.connect(('127.0.0.1',8080))
# 3.客户端给服务端发送消息(send)
send_msg = input('请输入给服务端发送的信息:')
client_socket.send(send_msg.encode())
# 4.客户端接收服务端的响应消息(recv)
recv_msg = client_socket.recv(1024)
print(f'接收到服务器端的信息:{recv_msg.decode()})
# 5.关闭客户端的socket
client_socket.close()
```

2. Web服务器的通信 与TCP服务器不同的时是我们不需要去写客户端的程序，因为客户端就是浏览器，Web服务器在本质上就是基于TCP服务器的，但是在这其中添加了HTTP协议，使得Web服务器能与浏览器进行通信，因此我们只需要写出Web服务器端的程序就行

### 1.1 B/S架构与C/S架构：

**B/S软件架构**：Browser/Server

B/S架构软件(即浏览器/服务器模式)，**是C/S架构的一种改进，可以说属于三层C/S架构**。**第一层是浏览器**，即客户端，只有简单的输入输

出功能，处理极少部分的事务逻辑。**第二层是WEB服务器**，扮演着信息传送的角色。**第三层是数据库服务器**，他扮演着重要的角色，因为

它存放着大量的数据。

**C/S软件架构**：Client/Server

C/S架构软件（即客户机/服务器模式）分为**客户机和服务器两层**。第一层是在客户机系统上结合了表示与业务逻辑，第二层是通过网络结

合了数据库服务器。qq,微信，qq音乐等：

# 2. HTTP协议

### 2.1 什么是HTTP协议？

​               HTTP 协议的全称是(HyperText Transfer Protocol)，翻译过来就是**超文本传输协议**，它规定了浏览器和 Web 服务器通信数据的

格式，也就是说浏览器和 Web 服务器通信需要使用HTTP协议。HTTP协议是应用层协议，它规定了数据传输的格式；TCP协议是传输层

协议，负责数据的传输，它不关心数据传输的格式；HTTP依赖于TCP协议进行数据的传输。

**请求行，请求头，响应行，响应头等代码格式严格按照请求报文和响应报文的格式来书写。**

**B/S程序中，浏览器访问服务器时，需要通过 URL 地址**。

**URL的详细格式：**

![image-20220820202856254](E:\黑马培训\Python进阶笔记\assets\image-20220820202856254.png)

**URL分析例子：**

![image-20220820203159703](E:\黑马培训\Python进阶笔记\assets\image-20220820203159703.png)

![image-20220820203404455](E:\黑马培训\Python进阶笔记\assets\image-20220820203404455.png)

**HTTP协议包含：http协议的请求和http协议的响应**

**一次HTTP操作称为一个事务，其工作过程大概如下**

1. 用户在浏览器中键入需要访问网页的URL或者点击某个网页中的链接。

2. 浏览器根据URL中的域名，通过DNS解析出目标网页的IP地址。

3. 在HTTP开始工作之前，客户端首先会通过TCP/IP协议来和服务端建立连接（TCP三次握手）。

4. 建立连接后，客户端发送一个请求给服务器，请求方式的格式为：统一资源标识符（URL）、协议版本号，后边是MIME信息包括请

   求修饰符、客户端信息和内容。

5. 服务器接收到请求后，给予相应的响应信息，其格式为一个状态行，包括信息的协议版本号、一个成功或错误的代码，后面是MIME

   信息包括服务器信息、实体信息和可能的内容。

6. 一般情况下，一旦Web服务器向浏览器发送了请求数据，他就要关闭TCP连接，然后如果浏览器或者服务器在其信息头加入这行代

   码：Connection:keep-alive,TCP 连接在发送后将仍然保持打开状态，于是浏览器可以继续通过相同的连接发送请求。保持连接节省

   了为每个请求建立新连接所需的时间，还节约了网络带宽。

**注意事项：**
 1.DNS域名解析采用的是递归查询的方式，过程是，先去找DNS缓存>缓存找不到就去找根域名服务器>根域名又会去找下一级>递归查找之后，找到了，给web浏览器。

2.为什么HTTP协议要基于TCP来实现？ TCP是一个端到端的可靠的面向连接的协议，HTTP基于传输层TCP协议不用担心数据传输的各种问题（当发生错误时，会重传）。

**TCP 三次握手**

为什么建立连接需要三次握手？

首先非常明确的是两次握手是最基本的。第一次握手，客户端发了个连接请求消息到服务器端，服务端收到消息后知道自己与客户端是可以连接成功的，但此时客户端并不知道服务端是否已经接收到了它的请求，所以服务端接收到消息后的应答，客户端得到服务端的反馈才确定自己与服务端是可以连接上的，这是第二次握手。

客户端只有确定了自己能与服务端连接上才能开始发数据，所以两次握手是最基本的。

综上我们思考一下，如果没有第三次握手，而是两次握手之后就认为连接成功了，那么会发生什么呢？第三次握手是为了防止已经失效的连接请求报文突然又传到服务端，因而产生错误。

例如发起请求时发生这样的状况：客户端发出去的第一个连接请求由于某些原因呢在网络节点中滞留了导致延迟，直到连接释放的某个时间点才到达服务端，这是一个失效的报文，但是此时服务端仍然认为这是客户端的建立连接请求的第一次握手，于是服务端回应了客户端，第二次握手。
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20200819085634156.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDU5MzcyMA==,size_16,color_FFFFFF,t_70#pic_center)
 如果只有两次握手，那么到这里，连接就已经建立成功了。但是此时客户端并没有任何数据要发送，而服务器还在傻傻的等候佳音，造成很大的资源浪费。所以需要三次握手，只有客户端再次回应一下，就可以避免这种状况。

**浏览器向 Web 服务器发送请求时，发送的是请求报文**。

请求报文格式如下：

> 四部分组成：请求行、请求头、空行、请求体

![image-20220820202440915](E:\黑马培训\Python进阶笔记\assets\image-20220820202440915.png)

**注意**：

- 请求方法常见的有GET和POST，GET用于获取web服务器数据，POST向web服务器提交数据
- GET请求时，不能携带请求体数据

**Web 服务器给浏览器返回响应时，返回的是响应报文**。

响应报文格式如下：

> 四部分组成：响应行、响应头、空行、响应体

![image-20220820202718907](E:\黑马培训\Python进阶笔记\assets\image-20220820202718907.png)

**具体的通信过程程序：**

![image-20220820203105658](E:\黑马培训\Python进阶笔记\assets\image-20220820203105658.png)

### 2.2 web服务器

**什么是Web服务器？Web服务器的作用是什么？**

Web服务器是可以和浏览器进行HTTP通信的服务器。不能和浏览器进行HTTP通信的服务器统称为非Web服务器。

Web服务器的作用是接收HTTP请求(解析请求报文)，返回HTTP响应(返回响应报文)。

**浏览器能看得懂的语言格式就是响应报文**  没有响应报文的时候则会报错!!!!!!!

**请求报文**: 浏览器发给web服务端,告诉web服务端我要干什么?  **响应报文:**   web服务器接收浏览器的请求返回的相对应的解释内容

**web端与浏览器端通信的原理示意图:**

![sp20220820_100606_011](E:\黑马培训\Python进阶笔记\assets\sp20220820_100606_011.png)

**Web服务器和之前写的TCP服务器有什么区别？**

**之前写的TCP服务器是不能和浏览器进行HTTP通信的**，因为之前写的TCP服务器不能解析浏览器请求时发送的HTTP请求报文，并且不能给浏览器返回HTTP响应报文。

**Web服务器本质也是一个TCP服务器，但是它能解析HTTP请求报文，并且能组织返回响应报文。**

**资源路径告诉浏览器我是干什么的**

> 即 Web 服务器能够知道浏览器请求的资源是什么，并能将对应的资源组织成响应报文的格式进行返回

#### 2.2.3 代码示例练习：

**自己的小理解：**

```tex
这里的web服务端是 服务器
浏览器是 客户端

web服务端需要接收浏览器端发送来的请求报文, 接着我们把请求报文里需要用到的信息拿出来,做完处理后,然后放到响应体中的响应报文中,返回给浏览器

请求报文中,url里面端口号后面的资源路径开头就是/ 告诉web服务器我要干什么 紧接着就是查询条件开头就是?
```

**代码练习1  实现一个简单的web服务器：它的本质就是基于TCP服务器的，不过添加了HTTP协议使浏览器能够解析看的懂，我们要让浏览器做什么**

```python
# 简单Web服务器-返回HelloWorld
# 学习目标：能够实现浏览器和Web服务器简单通信
import socket
# 创建一个服务端监听套接字socket对象
# socket.AF_INET：表示使用 IPV4 地址
# socket.SOCK_STREAM：表示使用 TCP 协议
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # 门迎

# 绑定服务端程序监听的 IP 和 PORT
server.bind(('127.0.0.1', 8080))

# 设置服务器监听，127表示同一时间最多处理127个客户端连接请求
server.listen(127)
print('服务器开始进行监听...')

# 等待接受客户端的连接：如果没有客户端连接时，这句代码会阻塞等待，直到有客户端连接
# server_client：和客户端进行通信的 socket 对象，服务员
# ip_port：客户端的 IP 和 Port

# 这里的while True让浏览器可以重复的向web服务器发送请求
while True:
    # 等待接收客户端的连接，如果没有客户端连接时，这句代码会阻塞等待，直到有客户端连接
    # server_client 和客户端进行具体通信的对象 ，服务于客户端信息的服务员
    # ip_port: 客户端的ip和port
    print('等待客户端发送信息....')
    server_client, ip_port = server.accept()
    print(f'接受到来自客户端{ip_port}的连接请求...')

    # 接受客户端发送的消息，1024表示最多接受1024个字节
    # 如果客户端没有发消息，recv方法会阻塞等待
    recv_msg = server_client.recv(1024) # 返回值是 bytes 类型
    print('客户端发送的消息为：')
    print(recv_msg.decode())

    # TODO：给浏览器返回 Hello World 响应内容
    # 写一个响应报文给浏览器
    # 响应行
    response_line = 'HTTP/1.1 200 OK\r\n'
    # 响应头 这是我们自己创建的web服务器端程序，所以我们可以自己起名字
    response_headers = 'Server: ChenZhiPeng;\r\nContent-Type: text/html;charset=utf-8\r\n'
    # 响应体
    response_body ='<h1 style={font-color=red}>Hello World</h1>'
    # 拼接成一个响应报文
    response_msg = response_line + response_headers + '\r\n' + response_body
    # 将响应报文内容发送给浏览器
    server_client.send(response_msg.encode())

    # 关闭和客户端通信的套接字、监听套接字
    server_client.close()

```

**代码练习2， 返回固定的html内容：**

```python
"""
简单Web服务器-返回固定html内容
学习目标：能够实现Web服务器给浏览器返回固定html内容
"""

import socket

# 创建一个服务端监听套接字socket对象
# socket.AF_INET：表示使用 IPV4 地址
# socket.SOCK_STREAM：表示使用 TCP 协议
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  # 门迎

# 绑定服务端程序监听的 IP 和 PORT
server.bind(('127.0.0.1', 8080))

# 设置服务器监听，127表示同一时间最多处理127个客户端连接请求
server.listen(127)
print('服务器开始进行监听...')

while True:
    # 等待接受客户端的连接：如果没有客户端连接时，这句代码会阻塞等待，直到有客户端连接
    # server_client：和客户端进行通信的 socket 对象，服务员
    # ip_port：客户端的 IP 和 Port
    server_client, ip_port = server.accept()
    print(f'接受到来自客户端{ip_port}的连接请求...')

    # 接受客户端发送的消息，1024表示最多接受1024个字节
    # 如果客户端没有发消息，recv方法会阻塞等待
    recv_msg = server_client.recv(1024)  # 返回值是 bytes 类型
    print('客户端发送的消息为：')
    # 在这里Web服务器有接收到客户端发送来的请求报文，但是没有多做处理，只是打印了
    # 因为我们这个程序不需要利用到请求报文中的数据来做其他的操作，所以只要做写响应报文就好
    # 浏览器只要接收到请求报文，做不做处理无所谓，看具体要求，但是一定要返回一个响应报文
    # 不然会报错
    print(recv_msg.decode())

    # TODO：给浏览器返回 gdp.html 网页的内容
    # 组织一个响应报文，发送给浏览器
    # 响应行
    response_line = 'HTTP/1.1 200 OK\r\n'
    # 响应头
    response_header = 'Server: YYDS;\r\nContent-Type: text/html;charset=utf8\r\n'
    # 响应体
    # 读取 gdb.html文件内容，作为响应报文中的响应体
    with open('./sources/html/gdp.html', 'r', encoding='utf8') as f:
        response_body = f.read()
    # 拼接成一个响应报文
    response_msg = response_line + response_header + '\r\n' +response_body
    server_client.send(response_msg.encode())
    # 关闭和客户端通信的套接字、监听套接字
    server_client.close()

server.close()

```

**代码练习3：返回任意的html内容：**

**思路分析：**Web服务器端返回什么样的内容，取决于浏览器的请求报文中，资源路径需要	web服务器端干什么，所以只要把请求报文中的资源路径提取出来就行，在浏览器端发送请求报文的时候，就要把报文字符串编码成二进制数据进行发送，所以Web服务器端接收的时候，可以把二进制编码解码成字符串，因为请求报文是字符串所以可以进行字符串的操作来获取，**注意：**报文的结尾默认都是\r\n所以我们可以先根据这个来把字符串进行切割，字符串切割后返回的是一个列表，请求报文的格式都是固定不变的：如下图所示，我们可以找到资源路径的具体位置：

![image-20220820230147199](E:\黑马培训\Python进阶笔记\assets\image-20220820230147199.png)

即我们可以取出返回的列表中的第一个数据，即下标为0的元素，再根据空格对字符串进行切割，返回的列表再取下标为1的元素 这样就得到我们想要的**资源路径**了

```python
"""
简单Web服务器-返回任意html内容
学习目标：能够实现Web服务器给浏览器返回任意html内容
"""
import os.path
import socket

# 创建一个服务端监听套接字socket对象
# socket.AF_INET：表示使用 IPV4 地址
# socket.SOCK_STREAM：表示使用 TCP 协议
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # 门迎

# 绑定服务端程序监听的 IP 和 PORT
server.bind(('127.0.0.1', 8080))

# 设置服务器监听，127表示同一时间最多处理127个客户端连接请求
server.listen(127)
print('服务器开始进行监听...')

while True:
    # 等待接受客户端的连接：如果没有客户端连接时，这句代码会阻塞等待，直到有客户端连接
    # server_client：和客户端进行通信的 socket 对象，服务员
    # ip_port：客户端的 IP 和 Port
    server_client, ip_port = server.accept()
    print(f'接受到来自客户端{ip_port}的连接请求...')

    # 接受客户端发送的消息，1024表示最多接受1024个字节
    # 如果客户端没有发消息，recv方法会阻塞等待
    # recv_msg 内容就是请求报文
    recv_msg = server_client.recv(1024) # 返回值是 bytes 类型
    print('客户端发送的消息为：')
    print(recv_msg.decode())

    # TODO：解析请求报文内容，获取浏览器请求的 URL 地址
    # 从请求报文中提取出需要的资源路径
    request_msg = recv_msg.decode() # str
    # 先把请求报文按照\r\n进行分割，分割之后获取请求行的内容
    request_tags = request_msg.split('\r\n') # 元素是字符串的list
    # 获取请求报文中的请求行
    request_line = request_tags[0]
    # 然后将请求行按照空格再对请求行进行分割，之后获取资源路径
    request_line_tags = request_line.split(' ') # list
    request_url = request_line_tags[1]
    print('获取的资源路径为: ', request_url)

    # TODO：组织 HTTP 响应报文并返回，根据浏览器请求的 URL 地址并返回相应的 html 内容
    # 组织响应报文返回给浏览器
    # 响应行
    response_line = b'HTTP/1.1 200 OK\r\n'
    # 响应头
    response_header = b'Server: YYDS;\r\nContent-Type: Text/html;charset=utf8\r\n'
    # 响应体
    # 如果访问的资源路径是 / 默认返回gdp.html的内容
    if request_url == '/':
        file_path = './sources/html/gdp.html'
    else:
        # 拼接完整路径
        file_path = './sources/html' + request_url

    # 先判断文件是否存在，存在才读取，否则将响应体设为Not Found!
    if os.path.isfile(file_path):
        # 文件存在
        with open(file_path, 'rb')as f:
            response_body = f.read()

    else:
        response_body = b'Not Found!'
    # 拼接成一个响应报文
    response_msg = response_line + response_header + b'\r\n' + response_body

    # 将响应报文编码后发送给浏览器
    server_client.send(response_msg)

    # 关闭和客户端通信的套接字、监听套接字
    server_client.close()

server.close()
```

每次开启web客户端程序，在浏览器上请求连接，系统就会自动请求这个网站所对应的图标，要把网站放到资源路径下的一块文件夹里，采取二进制的方式读取html文件内容，**字符串转二进制数据只要在字符串前面加上b即可**，谷歌浏览器能显示网站图标不知为何火狐出不来。。。。

**Web服务器的代码中，socket设置监听、接收客户端请求、解析HTTP请求报文、组织HTTP响应报文这些其实都是固定的套路，动态变化的是客户端每一次请求该如何进行业务处理**, Web开发框架就是把这些功能都封装在了这里面用户只需要写特点的业务逻辑即可

### 2.3 FastAPI框架

FastAPI是一个现成的高性能的Web开发框架

**开发步骤**

步骤 1：导入 FastAPI

from fastapi import FastAPI

```python
# 导入 uvicorn
import uvicorn
```

app = FastAPI()

FastAPI 是一个为你的 API 提供了所有功能的 Python 类。

    技术细节
    
    FastAPI 是直接从 Starlette 继承的类。
    
    你可以通过 FastAPI 使用所有的 Starlette 的功能。

步骤 2：创建一个 FastAPI「实例」

from fastapi import FastAPI
app = FastAPI()

@app.get("/")
async def root():
return {"message": "Hello World"}

这里的变量 app 会是 FastAPI 类的一个「实例」。

这个实例将是创建你所有 API 的主要交互对象。

这个 app 同样在如下命令中被 uvicorn 所引用：

如果你像下面这样创建应用：

```python
from fastapi import FastAPI
 
my_awesome_api = FastAPI()
 
@my_awesome_api.get("/")
async def root():
    return {"message": "Hello World"}
```

将代码放入 main.py 文件中，然后你可以像下面这样运行 uvicorn：

步骤 3：创建一个路径操作

路径

这里的「路径」指的是 URL 中从第一个 / 起的后半部分。

所以，在一个这样的 URL 中：

https://example.com/items/foo

...路径是：/items/foo

「路径」也通常被称为「端点」或「路由」。

开发 API 时，「路径」是用来分离「关注点」和「资源」的主要手段。

操作

这里的「操作」指的是一种 HTTP「方法」。

下列之一：

    POST
    
    GET
    
    PUT
    
    DELETE

...以及更少见的几种：

    OPTIONS
    
    HEAD
    
    PATCH
    
    TRACE

在 HTTP 协议中，你可以使用以上的其中一种（或多种）「方法」与每个路径进行通信。

在开发 API 时，你通常使用特定的 HTTP 方法去执行特定的行为。

通常使用：

    POST：创建数据。
    
    GET：读取数据。
    
    PUT：更新数据。
    
    DELETE：删除数据。

因此，在 OpenAPI 中，每一个 HTTP 方法都被称为「操作」。

我们也打算称呼它们为「操作」。

定义一个路径操作装饰器

    from fastapi import FastAPI
     
    app = FastAPI()
     
    @app.get("/")
    async def root():
        return {"message": "Hello World"}

@app.get("/") 告诉 FastAPI 在它下方的函数负责处理如下访问请求：

    请求路径为 /
    
    使用 get 操作

你也可以使用其他的操作：

    @app.post()
    
    @app.put()
    
    @app.delete()

以及更少见的：

```python
@app.options()

@app.head()

@app.patch()

@app.trace()
```

步骤 4：定义路径操作函数

这是我们的「路径操作函数」：

```python
路径：是 /。

操作：是 get。

函数：是位于「装饰器」下方的函数（位于 @app.get("/") 下方）。

from fastapi import FastAPI
 
app = FastAPI()
```


​     
```python
@app.get("/")
async def root():
 
    return {"message": "Hello World"}
```

这是一个 Python 函数。

**每当 FastAPI 接收一个使用 GET 方法访问 URL「/」的请求时这个函数会被调用。**

在这个例子中，它是一个 async 函数。

你也可以将其定义为常规函数而不使用 async def:

```python
from fastapi import FastAPI
 
app = FastAPI()
 
@app.get("/")
def root():
    return {"message": "Hello World"}
```

步骤 5：返回内容

```python
from fastapi import FastAPI
 
app = FastAPI()
```


​     
```python
@app.get("/")
async def root():
 
    return {"message": "Hello World"}
```

你可以返回一个 dict、list，也可以像 str、int 一样的单个值，等等。

最后运行调用：

```python
if __name__ == '__main__':
    # 启动 Web 服务器
    # host：指定 Web 服务器监听的IP
    # port：指定 Web 服务器监听的端口
    uvicorn.run(app, host='127.0.0.1', port=8080)
```

**第一句话的意思是在这个模块中运行，uvicorn服务器才能被调用 它的run方法第一个参数是运行哪一个FastAPI对象，第二个是接收浏览器请求的主机地址，第三个参数是接收浏览器请求的主机端口**

**采取FastAPI来编写Web程序当一个资源路径我们没有对他做特定的处理时，不会报错，而会自己返回一个Not Found**

**快速开发web应用程序图解：**

![image-20220820203554578](E:\黑马培训\Python进阶笔记\assets\image-20220820203554578.png)

![image-20220820203603217](E:\黑马培训\Python进阶笔记\assets\image-20220820203603217.png)

**代码示例3： 一个普通的FastAPI基本使用 返回hello world**

```python
# 导入FastAPI
from fastapi import FastAPI
# 导入uvicorn
import uvicorn

# 创建一个FastAPI对象
app = FastAPI()


# 定义资源路径的处理与请求方式
@app.get('/index')
# 定义处理函数
def chuli_index():
    # 具体要执行的功能
    return '你好大傻逼'


# 开启web服务器
if __name__ == '__main__':
    # 启动 fastapi 这个程序
    # 注意：uvicorn其实就是 web 服务器
    uvicorn.run(app, host='127.0.0.1', port=8080)

```

代码示例2：

FastAPI 的基本使用 返回html网页内容

```python
"""
FastAPI 基本使用-返回html内容
学习目标：能够使用 FastAPI 定义处理函数返回 html 内容
"""
# 导入 FastAPI 类
from fastapi import FastAPI
# 导入 uvicorn
import uvicorn
# 导入 Response 类
from fastapi import Response

# 创建 FastAPI 对象
app = FastAPI()


# 定义业务处理函数并设置对应的 URL 地址
# get：表示请求方式
# /index：表示请求的 URL 地址
@app.get('/index')
def index():
    # 'Hello World'是响应体的内容
    return 'Hello World'


# TODO：定义处理函数，访问 / 和 /gdp.html 地址时，返回 gdp.html 内容
@app.get('/')
def re_msg():
    return 'Not Found 404'


@app.get('/gdp.html')
def re_html():
    with open('./sources/html/gdp.html', 'rb') as f:
        content = f.read()

    return Response(content, media_type='text/html')


# TODO：定义处理函数，访问 /render.html 地址时，返回 render.html 内容
@app.get('/render.html')
def res_html():
    with open('./sources/html/render.html', 'rb')as f:
        content = f.read()

    return Response(content, media_type='text/html')


if __name__ == '__main__':
    # 启动 Web 服务器
    # uvicorn.run(app, host='127.0.0.1', port=8080)
    # reload=True：当 uvicorn 检测到代码发生改变之后，会自动进行重启
    # 下面是具体格式
    uvicorn.run('05-FastAPI 基本使用-返回html内容:app',
                host='127.0.0.1', port=8080, reload=True)
```

**一些小的补充知识点：**

bytes对应的是文本才能转换为字符串,像图片本身就不是文本,就不能用decode解码,转换成字符串

在字符串前面加上个b 就是转换成二进制 即使 b'   '

uvicorn相当于一个Web服务器，导入uvicorn进入python代码程序以后，虽然说这个程序已经拥有了Web服务器，可以与FastAPI框架做一些数据的传输，但是这个Web服务器此时还是处于待机状态的，我们没有去run把这个服务器开启，这个uvicorn是一点作用都没有的，它是接收不到浏览器发来的请求的，我们得给它传进去对象，指定的访问地址，指定的端口，它才会实现Web服务器该有的功能

在使用fastapi框架是时要返回响应体的内容时我们Response(content, media_type='text/html')  第一个空是响应体的内容，第二个空相当于响应头里面的Content-Type: text/html 指定了响应体内容的类型

# 3. Python进阶day06-知识点总结

1）HTTP协议【理解】

软件程序架构：

* C/S：桌面应用程序（需要下载客户端安装包，安装之后才能使用）
* B/S：浏览器/服务器，就是Web应用程序

HTTP协议：

* **作用：规定了浏览器和Web服务器之间通信的数据格式。**

* url地址组成

  * 协议://域名[:端口]**/资源路径**[?参数名=值&参数名=值]

* **HTTP请求报文**

  * 浏览器发送给Web服务器的数据格式
  * 组成：请求行、请求头、空行、请求体(注意：GET没有请求体)

* **HTTP响应报文**

  * Web服务器返回给浏览器的数据格式
  * 组成：响应行、响应头、空行、响应体(注意：给浏览器返回的数据)

* 请求头和响应头

  ```python
  请求头：
  * User-Agent：告诉web服务器，我们是通过什么东西向他发的请求；很多网站会通过User-Agent识别收到的请求是不是一个爬虫程序发的程序，但是User-Agent是可以伪造的。
  
  响应头：
  * Server：告诉浏览器，Web服务器是什么类型，什么版本；
  * Content-Type：告诉浏览器，返回的响应报文中的响应体是什么内容（网页、图片、css、js）
  	* Content-Type: text/html;charset=utf-8
  	* Content-Type: image/png
  	* Content-Type: application/x-javascript
  	* Content-Type: text/css; charset=utf-8
  ```

2）Web服务器开发【理解】

* **作用：本质上就是一个TCP服务器程序，但是能够和浏览器进行HTTP通信（能够解析请求报文，而且能够返回响应报文）。**

* 示例

  * 改写TCP服务器程序，给浏览器返回一个 Hello World 进行显示。

    * **组织响应报文**

  * 改写程序，给浏览器返回一个 gdp.html 网页

    * **读取 gdp.html 内容作为响应体**

  * 改写程序，根据浏览器请求的资源路径给浏览器返回网页

    * **解析请求报文：提取资源路径**
    * **根据资源路径读取对应的文件内容作为响应体**

    ![image-20220820183116034](G:\AppData\Roaming\Typora\typora-user-images\image-20220820183116034.png)

3）FastAPI 框架【理解】

* 作用：python中的一个 web 开发框架，把 web 程序开发过程中一些固定的套路都进行提前的封装，我们可以直接使用，提高开发效率，只要关注具体的业务逻辑实现就可以了。
* 安装
  * pip install fastapi
  * pip install uvicorn
* 使用

```python
# 导入 FastAPI 这个类
from fastapi import FastAPI
# 导入 uvicorn
import uvicorn

# 创建一个 FastAPI 这个类的实例对象
app = FastAPI()


# 定义资源路径以及对应的处理函数
# 注意：只要浏览器访问了这个服务器 /index 资源路径，index 处理函数就会被调用，函数的返回值就是响应体的内容
# 指定 /index 对应的处理函数是 index，get代表请求方式
@app.get('/index')
def aaa():
    # 函数的返回值就是响应体的内容
    return 'Hello World'


if __name__ == '__main__':
    # 启动 fastapi 这个程序
    # 注意：uvicorn其实就是 web 服务器
    uvicorn.run(app, host='127.0.0.1', port=8080)
```

* 返回网页

```python
from fastapi import Response

@app.get('/')
@app.get('/gdp.html')
def gdp_html():
    # 读取 gdp.html 文件的内容
    with open('./sources/html/gdp.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回一个响应 Response 对象
    return Response(content, media_type='text/html')


# TODO：定义处理函数，访问 /render.html 地址时，返回 render.html 内容
@app.get('/render.html')
def render_html():
    # 读取 render.html 文件的内容
    with open('./sources/html/render.html', 'r', encoding='utf8') as f:
        content = f.read()

    # 返回一个响应 Response 对象
    return Response(content, media_type='text/html')
```

![05-FastAPI请求过程](E:\黑马培训\Python进阶笔记\assets\05-FastAPI请求过程.png)

* uvicorn 自动重启

```python
if __name__ == '__main__':
    # 启动 Web 服务器
    # uvicorn.run(app, host='127.0.0.1', port=8080)
    # reload=True：当 uvicorn 检测到代码发生改变之后，会自动进行重启
    uvicorn.run('06-FastAPI 基本使用-返回html内容:app',
                host='127.0.0.1', port=8080, reload=True)
```

浏览器请求百度的过程:

![image-20220822192515430](E:\黑马培训\Python进阶笔记\assets\image-20220822192515430.png)





