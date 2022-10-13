# python爬虫  

# selenium自动化爬虫

# 1. 什么是Selenium?

- 官网: Selenium是一个用于Web应用程序测试的工具
- 真实:大量用于网络爬虫,相比于requests爬虫,完全模拟真人使用浏览器的流程,对于动态JS加载的网页更加容易爬取

# 2. Selenium的功能

- 框架低层使用JavaScript模拟真实用户对浏览器进行操作.测试脚本操作时,浏览器自动按照脚本1代码做出点击,输入,打开,验证等操作,就像真实用户所做的一样,从终端用户的角度测试应用程序
- 可以用于较难的爬虫:  动态JS加载,登录验证,表单提交等
- 使用简单可以使用Python,Java等多种语言编写用例脚本

![image-20220825101436159](E:\黑马培训\assets\image-20220825101436159.png)

![image-20220825101527410](E:\黑马培训\assets\image-20220825101527410.png)

**实战案例:**

**from selenium.webdriver import Chrome**  意思是把Chrome这个浏览器的driver导入chrome中去

```python
from selenium.webdriver import Chrome
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By

# 创建一个浏览器对象,有了这个对象以后我们就可以请求一个网页了
driver = Chrome()
# 获取一个网页打开
driver.get("https://www.python.org/")

WebDriverWait(driver, 10).until(lambda d: "Python" in d.title)
print(driver.find_element(By.XPATH, '//*[@id="touchnav-wrapper"]/header/div/div[3]').text)
# print(driver.current_url)
```

# 3. Selenium语法总结

## 3.1 访问url和导航

```python
# 访问一个网页
driver.get("https://selenium.dev")

# 获取当前URL
driver.current_url

# 返回上一页
driver.back()

# 前往下一页
driver.forward()

# 刷新页面
driver.refresh()

# 获取标题
driver.title

# 最大化窗口
driver.maximize_window()

# 全屏窗口
driver.fullscreen_window()

# 屏幕截图
driver.save_screenshot('./image.png')

# 元素截图
element.screenshout("./image.png")

# 关闭driver
driver.close()
```

## 3.2 查找和定位页面元素:

```python
# 定位单个元素
driver.find_element(By.NAME, "q")
```

意思是:定位q这个标签的name

**前面一个参数是定位元素的方式** : 有以下这些

- class name
- css selector
- id
- name
- link text
- partial link text
- tag name
- xpath

```python
# 定位多个元素
driver.find_elements(By.TAG_NAME,'p')
```

```python
# 获取元素的子元素
search_form.find_element(By.NAME, "q")
```

```python
# 获取元素的多个子元素
element.find_elements(By.TAG_NAME, "P")
```

```python
# 获取标签名
driver.find_element(By.CSS_SELECTOR, "h1").tag_name #标签名
```

```python
# 获取文本
driver.find_element(By.CSS_SELECTOR,"h1").txt
```

```python
# 获取属性
active_element.get_attribute("title")
```

## 3.3 等待页面加载完毕/元素加载完毕

含义: 有些时候Python加载太快了,页面还没加载完毕就去取数据就会报错

可以等待满足条件后,再去取数据

**格式:**

timeout超时时间  这个时间超时就会报错,到时间了页面还没加载出来,就去取元素了

WebDriverWait(driver, timeout=3).util(some_condition)

**代码示例:**

```python
from selenium.webdriver.support.ui import WebDriverWait
WebDriverWait(driver).util(lambda d: d.find_element_by_tag_name("p"))
```

**lambda 匿名函数 后面表达式的结果就是函数的返回值,函数也可以没有返回值**

wait是用来判断这个页面真的是否加载完成了

## 3.4 执行键盘操作

**例子:**

```python
# 发送内容,然后回车
driver.find_element(By.Name, "q").send_keys("webdriver"+keys.ENTER)

# 输入内容,然后清理
SearchInput = driver.find_element(By.NAME,"q")
SearchInput.send_keys("selenium")
# Clears the entered text
SearchInput.clear()
```

**代码分析:**

```python
import time
# 意思是把Chrome这个浏览器的driver导入chrome中去
from selenium.webdriver import Chrome
# 通过By这个方式来取数据
from selenium.webdriver.common.by import By
# 导入这个模块可以实现浏览器驱动等待
from selenium.webdriver.support.ui import WebDriverWait
# 从下面这个模块中导入这个title_contains函数
from selenium.webdriver.support.expected_conditions import title_contains
# 创建浏览器驱动driver对象
driver = Chrome()
# driver对象获取网页即打开网页
driver.get("https://www.baidu.com")

# 通过xpath这个方式来定位到网页中的特定位置,实现指定操作
driver.find_element(By.XPATH, '//*[@id="kw"]').send_keys("python")
driver.find_element(By.XPATH, '//*[@id="su"]').click()

# 浏览器驱动等待10s,等待浏览器加载出内容,判断页面中是否含有python这一关键字,如果满足条件则继续向下执行操作,不满足条件也抛出错误
# 如果10s内没有加载出页面,则抛出异常
WebDriverWait(driver, 10).until(title_contains(("python")))

while True:
    # 按照CSS标签选择器的方式定位页面元素接着取数据
    h3_list = driver.find_elements(By.CSS_SELECTOR, 'h3.t')
    for h3 in h3_list:
        print(h3.text)
    # last()页面最后一个满足条件的标签
    driver.find_element(By.XPATH, '//*[@id="page"]/div/a[last()]').click()
    time.sleep(3)
```

## 3.5 异常报错信息出现的问题

**程序报错提示:**

![image-20220818095507011](E:\黑马培训\assets\image-20220818095507011.png)

爬虫爬取页面的时候,为了防止程序运行过快,页面还没有加载出来的时候,就爬取数据会报错,而上面这种情况则是定义的lambda匿名函数,后面的表达式返回了False,意思为python这个数据在这个网页的标签中不存在,程序在这里等了10s,都没有取到对应的数据,所以抛出错误了.

# 4. Selenium元素定位详细总结

**事先提示: ** 前面一个空是声明查找的方式,后面一个空写出具体路径

## **4.1 ID元素定位**

- 基于元素属性中的id的值来进行定位，id是一个标签的唯一属性值，可以通过id属性来唯一定位一个元素，是首选的元素定位方式，动态ID不做考虑。

```text
driver.find_element_by_id('id')
driver.find_element(By.ID, 'id')
```

## **4.2 name元素定位**

- 基于元素属性中的name的值来进行定位，但name并不是唯一的，很可能会出现重名。

```text
driver.find_element_by_name('name')
driver.find_element(By.NAME,'name')
```

## **4.3 class_name元素定位**

- 基于元素class样式来定位，非常容易遇到重复的，这个方法的参数只能是一个class值，列如：class属性有空格隔开两个class的值时，只能选取其中一个进行定位。

```text
driver.find_element_by_class_name('class_name')
driver.find_element(By.CLASS_NAME,'class_name')
```

## **4.4 tag_name元素定位**

- 通过元素的标签名来定位元素，如：input标签、span标签；标签名来进行定位元素，重复度最高，只有在需要定位后进行二次筛选的情况下使用。

```text
driver.find_element_by_tag_name('tag_name')
driver.find_element(By.TAG_NAME, 'tag_name')
```

## **4.5 css_selector元素定位**

- css可以通过元素的id、class、属性、子元素、后代元素、index、兄弟元素等多种方式进行元素定位.

### **4.5.1通过ID定位，如：input id="kw"**

```text
driver.find_element_by_css_selector('div#kw')
driver.find_element(By.CSS_SELECTOR, 'div#kw')
```

### **4.5.2 通过class定位，如：input class="is_put"**

```text
driver.find_element_by_css_selector('input.is_put')
driver.find_element(By.CSS_SELECTOR, 'input.is_put')
```

### **4.5.3 通过属性定位，如：input name="kw"**

```text
driver.find_element_by_css_selector('input[name=kw]')
driver.find_element(By.CSS_SELECTOR, 'input[name=kw]')
```

### **4.5.4 通过子元素定位，与xpath不同，css中用">"右箭头代表子元素，而xpath中用的"/"单斜杠表示**

```text
driver.find_element_by_css_selector('div#kw>a')
driver.find_element(By.CSS_SELECTOR, 'div#kw>a')
```

### **4.5.5 通过后代元素定位**

```text
driver.find_element_by_css_selector('div#kw a')
driver.find_element(By.CSS_SELECTOR, 'div#kw a')
```

### **4.5.6 通过元素的index值定位元素**

```text
driver.find_element_by_css_selector('div#kw>a:nth-child(1)')
driver.find_element(By.CSS_SELECTOR, 'div#kw>a:nth-child(1)')
```

### **4.5.7 通过元素兄弟元素定位元素**

- css的定位到的是兄弟元素下面的兄弟元素，而且只是下面的第1个元素（如：span元素id为"kw"有很多input兄弟元素，它只定位到它下面的第1个兄弟元素）；而xpath不同的是可以定位到所有兄弟元素；

```text
driver.find_element_by_css_selector('span#kw+input)')
driver.find_element(By.CSS_SELECTOR, 'span#kw+input')
```

## **4.6 link_text元素定位**

- 主要用于超链接进行定位，全部匹配文本值，使用在链接位置处，例如：a标签

```text
driver.find_element_by_link_text('link_text')
driver.find_element(By.LINK_TEXT, 'link_text')
```

## **4.7 partial_link_text元素定位**

- 基于链接的部分文字来定位，link text模糊匹配，模糊查询匹配到多个符合调节的元素，选取第一个，同样也只使用在链接位置处，例如：a标签

```text
driver.find_element_by_partial_link_text('partial_link_text')
driver.find_element(By.PARTIAL_LINK_TEXT, 'partial_link_text')
```

## **4.8 xpath元素定位**

- xpath是一门在xml文档中查找信息的语言。xpath可用来在xml文档中对元素和属性进行遍历。由于html的层次结构与xml的层次结构天然一致，所以使用xpath也能够进行html元素的定位。xpath的功能非常强大，通过xpath的各种方式组合，能够解决selenium自动化测试中界面元素定位的绝大部分问题。

### **4.8.1 xpath通过绝对路径定位元素**

- 将 Xpath 表达式从 html 的最外层节点，逐层填写，最后定位到操作元素，这种方法，一旦路径有变化会导致定位失败，所以不推荐使用该方式。

```text
driver.find_element_by_xpath('/html/body/div[4]/div[1]/a')
driver.find_element(By.XPATH, '/html/body/div[4]/div[1]/a')
```

### **4.8.2 xpath通过相对路径定位元素**

- 绝对路径与相对路径的差别与文件系统中的绝对和相对路径类似，相对路径是只给出元素路径的部分信息，在html的任意层次中寻找符合条件的元素，语句以//开始。

```text
driver.find_element_by_xpath('//form/span')
driver.find_element(By.XPATH, '//form/span')
```

### **4.8.3 xpath通过元素属性定位元素**

- 单属性定位：//input[@name='pwd']表示name属性为pwd的input

```text
driver.find_element_by_xpath("//input[@name='pwd']")
driver.find_element(By.XPATH, "//input[@name='pwd']")
```

- 多属性定位：//a[@title="tutorial" and @rel="follow"]

```text
driver.find_element_by_xpath('//a[@title="tutorial" and @rel="follow"]')
driver.find_element(By.XPATH, '//a[@title="tutorial" and @rel="follow"]')
```

### **4.8.4 xpath通过属性值模糊匹配定位元素**

- xpath模糊匹配的函数有两种：**starts-with和contains**
- **starts-with：//label[starts-with(@class,'btn')]，class属性值以btn开头的label元素**

```text
driver.find_element_by_xpath("//label[starts-with(@class,'btn')]")
driver.find_element(By.XPATH, "//label[starts-with(@class,'btn')]")
```

- **contains：//label[contains(@class,'btn')]，通过属性值包含btn的label元素**

```text
driver.find_element_by_xpath("//label[contains(@class,'btn')]")
driver.find_element(By.XPATH, "//label[contains(@class,'btn')]")
```

### **4.8.5 xpath通过文本定位元素**

- 文本内容的定位是利用html的text字段进行定位的方法，//span[text()='下一步']，由于 “下一步” 这几个字是浏览器界面就可以看到的，  “所见即所得”，这种特征改的可能性非常小，优先推荐使用。**与属性值类似，文本内容也支持starts-with和contains模糊匹配。**

```text
driver.find_element_by_xpath("//span[text()='下一步']")
driver.find_element(By.XPATH, "//span[text()='下一步']")
```

## **4.9 页面常见的元素定位封装**

- 自己封装灵活的元素定位方式，有助于提升代码的可维护性、可读性、可靠性

```python
    def locate_element(self, key):
        """
        通过key获取对象库里面的具体对象
        :param key:
        :return:
        """
        data = self.library.get_value(key)
        locate_method = data.split('>')[0]
        value = data.split('>')[1]
        try:
            if locate_method == 'id':
                return self.get_web_element(By.ID, key, value)
            elif locate_method == 'name':
                return self.get_web_element(By.NAME, key, value)
            elif locate_method == 'className':
                return self.get_web_element(By.CLASS_NAME, key, value)
            elif locate_method == 'cssSelector':
                return self.get_web_element(By.CSS_SELECTOR, key, value)
            elif locate_method == 'linkText':
                return self.get_web_element(By.LINK_TEXT, key, value)
            elif locate_method == 'partialLinkText':
                return self.get_web_element(By.PARTIAL_LINK_TEXT, key, value)
            elif locate_method == 'tagName':
                return self.get_web_element(By.TAG_NAME, key, value)
            else:
                return self.get_web_element(By.XPATH, key, value)
        except Exception as e:
            error_msg = '通过【{}:{}】获取【{}】元素异常，异常信息：{}'.format(locate_method, value, key, e)
            logger.error(error_msg)
            return self.error_info(key, locate_method, value, error_msg)
```

**总结: **   **从效率的角度来说 id，name，cssselector效率更高，xpath效率最低，但是这种效率来说几乎微乎其微，xpath属于万金流，没有定位不了的元素，配合绝对筛选查找，可读性高，维护性会更好**





# request 爬虫

# 1. Python正则表达式  re模块

**前言:**  

字符串是编写程序时用到最多的数据结构,对字符串的处理也是经常要做到的工作 如判断字符串是不是合法的email地址,电话号码,身份证

号等   虽然可以用字符串处理方法进行判断 但是编写起来非常麻烦 不利于代码的复用   **所以就出现了正则表达式这个工具来匹配字符串**

**图片的数据格式是二进制数据**

**正则表达式的设计思想:** 

用一种描述性语言来给字符串定义一个规则,凡是符合规则的字符串,就认为它匹配了,否则字符串就是不合法的

在不同语言里面正则表达式的语法是一样的

正则的两个参数  第一个你写的正则表达式  第二个你要匹配的字符串  二者缺一不可

**正则的匹配步骤:**

1. 依次拿出表达式和文本中的字符进行比较
2. 如果每一个字符都能匹配,则匹配成功
3. 如果有匹配不成功的字符则匹配失败
4. 注意  整个正则表达式 要全部匹配成功才行  如果里面有一个字符的正则匹配失败 那么匹配会全部失败

## 1.1 正则表达式一些解释

正则表达式是使用字符串表示的,需要了解用**字符**来描述字符串

**疑惑:** 为什么有时候写正则 来匹配html中的标签可以直接写< a  href = ''>< /a >

**解释:** 如果直接给出具体的字符,那就是精确匹配 就如上面的 当然也能在精确匹配中加元字符 来确定具体想要取出的数值

## 1.2 Python支持的正则表达式元字符与语法

\r\n是连用的吗?  

windows下回车换行符连用，\r\n
unix下只用 \n
mac下只用 \r

**数字也被算进单词字符里面了**

#### 1.2.1 元字符

**实际上没有空格 因为这里书写会报错 所以加了空格**

在正则表达式中在什么前面加上  **^**  都是取反的意思 意思就是除了后面写到的字符 它都能匹配 前提是它没有的后面要匹配的字符串里面

也要有

在中括号中没写 "_" 直接把英文字符连着写的 那就是字符集合 包括的就只有里面写的字符  有写的话就是字符范围

一个英文字符加转义符号在正则里有特殊含义以后 让他去反以后 表示的意思也和原来相反了

在正则表达式中 [] 括起来的代表字符集 而{}括起来的则是量词

**这里匹配的操作前提都是以字符串匹配字符串**

- 明确要匹配什么字符    作用相当于定位

  -   .        **描述:** 匹配到除了"\r\n" <== 这个的意思只要字符串中包含\r 或者 \n 或者两个都包含 用点都匹配不到   得使用re.S的模式才能匹配到

  -  \         **描述:** 转义字符  例子: '\ \n' 能匹配 \n      ,       '\n ' 匹配换行符   序列'\ \ '匹配"\ "  而"\ (" 匹配 " ("

  - [xyz]   **描述:** 字符集合 匹配所包含的任意一个字符  例如"[abc]" 能匹配到 "plain" 中的 "a"

  - [^xyz]  **描述:**  负值字符集合  匹配未包含的任意字符 例如 "[ ^abc]" 能匹配到"plain" 中的 "plin"

  - [a-z]    **描述:** 字符范围 匹配指定范围内的任意字符 例如 "[a-z]" 能匹配"a" 到 "z" 范围内的任意小写字母字符

    **注意:**  只有连字符出现在字符组内部 ,并且出现在两个字符之间时,才能表示字符的范围  如果出现在字符的开头 那么就是字符的本身

  - [^a-z]  **描述:** 负值字符范围, 匹配任意一个不在这个字符范围内的字符 例如: "[ ^ a - z]"可以匹配到不在"a" 和 "z" 范围内的任意字符   大小写看具体需要,自己灵活应对还能写数字范围

  - **以下是预定字符集  可以写在字符集[...] 中**

  - \d     **描述:** 匹配一个数字字符  等价于[0-9]  例如"a\dv" 可以匹配到"a2v" 当然也能匹配到"a3v" 只要中间数字式是 0-9就行

  - \D     **描述:** 匹配一个非数字字符 等价于[ ^ 0-9] 例如"a\Dc" 可以匹配到 "abc" 只要不是0-9 的数字均可

  - \s      **描述:** 匹配任何不可见字符 包括空格,制表符,换页符等等  等价于[ \f\n\r\t\v]

  - \S      **描述:** 匹配任何可见字符 等价于[ \f\n\r\t\v]里面字符串的开头加个^

  - \w     **描述:**  匹配包括**下划线**和**任意单词字符**      类似但是不等价于[A-Za-z0-9]  例如: 在字符串 "abcd_123" 中匹配非特殊字符： 用`\w` 后面要是加上数量词就能把整个字符串取出

  - \W     **描述:** 匹配任何非单词字符 等价于‘[ ^A-Za-z0-9]’

  上面的都是去匹配字符串里面需要的字符的 元字符的格式 然而匹配的数量没有指定 下面就要写到指定数量的 数量词 他是作用于字符或者（...)之后

- **明确要匹配的指定字符的数量**

  括号里面单个确定匹配次数的符号后面加了个逗号,匹配次数的范围就增大了 代表至少匹配多少  最多没有限制

  ```tex
   * 描述: 匹配前一个字符任意次数 例如 "zo*" 能匹配 "z" 也能匹配"o" 也能匹配"zoo" 或者 "zo" 可以0次也可以无数次
   + 描述: 匹配前一个字符一次或者多次 意思就是说至少匹配一次 例如"zo+"能匹配"zo" 以及"zoo"  但是不能匹配"z" 
   ? 描述: 匹配前一个字符0次或者一次 例如 "do(es)?" 可以匹配"do" 或者 "dose"  ? 等价于{0,1}
  
  这下面要匹配的都是需要是连续的
  
  {n}描述: n是一个非负整数 也包括数字0 匹配括号里确定的n次 次数已经被确定少一个都匹配不到 例如"o{2}"不能 匹配"pop"中的"o"
          但是可以匹配"poop" 中的两个o 这里返回的结果就是"oo"  这里要匹配的需要是连续的
  {n,} 描述: n是一个非负整数 也包括数字0 至少匹配n次 例如"o{2,}" 不能匹配"bob" 中的"o" 但是可以匹配"foooood"里面的所             有"o"  字符串里面连续的o至少要有两个才能匹配成功  超过两个 具体有多少个o都会给他匹配下来返回
            "o{1,}" 等价于"o+"  "o{0,}" 等价于 "0*"
  {n,m} 描述: m和n均是非负整数,其中n<=m 最少匹配n次且最多匹配m次 例如 "o{1,3}" 将去匹配"foooood" 中的前三个"o". "o{0,1}" 等价于"o?" 注意在逗号两个数之间不能有空格   它的匹配规则是 先从最高的次数往下式  直到匹配到位置 匹配的下限就是最低的次数n 如果没有匹配到 那么 则正则拿不到数据 返回none         
  ```

**知识点添加:**   在正则中(....)有什么作用

在[正则表达式](https://so.csdn.net/so/search?q=正则表达式&spm=1001.2101.3001.7020)中，小括号是一个特别重要的结构，下面就通过简单的代码实例介绍一下它的作用。

**一.分组的作用:**

这个和算术运算中的小括号是一样的，那就是进行简单的分组。

在小括号中的内容可以被做一个整体进行处理，代码实例如下:

[JavaScript] *纯文本查看* *复制代码*

```javascript
 var reg=/(ab){2}/;

var str="ababantzone";

console.log(reg.test(str))
```

上面的代码就是将"ab"作为一个整体来处理，来验证字符串中是否存在两个连续的"ab"。

如果不加小括号的话，就是验证是否具有两个连续的"b"。

**二.小括号的内容会被存储起来:**

通常我们将小括号包裹形成的代码片段称之为子表达式。

这个子表达式匹配的内容会被存储起来，以便进行相关的操作。

代码实例如下:

[JavaScript] *纯文本查看* *复制代码*

```javascript
 var date='2015-8-7';

var reg=/(\d+)(-)/;

console.log(date.match(reg));
```

生成的数字，第1个元素是匹配的内容，第2个元素是第一个字表达式匹配的内容，依次类推。

当然index和input不包括在内。

- **边界匹配**

**multiline是RegExp对象的只读布尔属性。**  它指定特定正则表达式是否执行多行匹配，即是否使用“m”属性创建

*\r是回车(CR) ,将当前位置移到本行开头*

\n是换行符,通常在输出中用作格式控制

```
^    描述: 匹配输入字符串的开始位置  如果设置了RegExp对象的Multiline属性,^也匹配"\n"或者"\r"
$    描述: 匹配输入字符串的结束位置  如果设置了RegExp对象的Multiline属性,^也匹配"\n"或者"\r"
\b   描述: 匹配一个单词边界,也就是指单词和空格间的位置(即正则表达式的"匹配"有两种概念,一种是匹配字符 一种是匹配位置 这里\b就是           匹配位置的) 例如"er\b" 可以匹配"never"中的"er", 但是不能匹配"verb"中的"er"
\B   描述: 匹配非单词边界. "er\b" 能匹配"verb"中的"er", 但是不能匹配"never"中的"er"
\A   描述: 仅匹配字符串的开头  例如  "\Aabc"可以匹配"abcdef"
\Z   描述: 仅匹配字符串的结尾 例如 "abc\Z" 可以匹配到字符串

```

**\反斜杠作为转义字符,写在正则里面可能出问题,所以就要把字符串转化为原生字符串,即在字符串前面加一个"r"就行**

#### 1.2.2 语法

|           描述: 将两个匹配条件进行逻辑"或"(or)运算  例如正则表达式(him|her)  去匹配 "it belongs to him" 和 "it belongs to her"  的其中                  

​             一个但是它不能去匹配"it belongs to them" 注意这个元字符不是所有软件都支持的

(....)      描述: 将()括号里面的表达式定义成"组" (group) 并将匹配这个表达式的字符保存到一个临时区域(一个正则表达式中最多可以保存9       

​            个) 他们可以用\1 到 \9的符号来引用

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

**re模块-引用正则分组【熟悉】**

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

正则表达式修饰符 ------ 可选标志

多个标志可以通过按位or(|)来指定,就比如说:re.I | re.M

#### 1.2.3函数

```
"""
re 正则模块：match、search、findall
学习目标：能够使用 re 模块中 match、search、findall 三个函数进行字符串的匹配
"""
import re

"""
match函数：re.match(pattern, string, flags=0)
功能：尝试从字符串起始位置匹配一个正则表达式
        1）如果不能从起始位置匹配成功，则返回None；
        2）如果能从起始位置匹配成功，则返回一个匹配的对象
"""

# my_str = 'abc_123_DFG_456'

# 匹配字符串bc(注：从头开始)
# res = re.match('^bc', my_str)
# print(res)

# 匹配字符串abc(注：从头开始)
# res = re.match('^abc', my_str)
# print(res.group())

"""
search函数：re.search(pattern, string, flags=0)
功能：根据正则表达式扫描整个字符串，并返回第一个成功的匹配
        1）如果不能匹配成功，则返回None；
        2）如果能匹配成功，则返回一个匹配对象
"""

# my_str = 'abc_123_DFG_456'

# 匹配连续的3位数字
# res = re.search('\d{3}', my_str)
# print(res.group())

"""
findall函数：re.findall(pattern, string, flags=0)
功能：根据正则表达式扫描整个字符串，并返回所有能成功匹配的子串
        1）如果不能匹配成功，则返回一个空列表；
        2）如果能匹配成功，则返回包含所有匹配子串的列表
        findall 没有group这个属性
"""

my_str = 'abc_123_DFG_456'

# 匹配字符串中的所有连续的3位数字


content = re.findall('\d{3}', my_str)
print(content)
```

##### 1.2.3.1 group(num) 和 groups()方法

**前言:**  除了简单地是否匹配之外, 正则表达式还有提取子串的强大功能

使用的函数就是group(num)  表示的就是要提取的分组

使用group(num) 和 groups()匹配对象方法可以获取**匹配表达式** 也就是说匹配到的字符串

用这个方法的前提是有这个对象 像re模块的match search 这两个函数返回的都是对象 可以使用group方法,但是findall这个函数返回来的数据是一个列表 它是没有这个group属性的,使用会报错

**匹配对象方法:**

**group(num = 0)**  匹配到的整个字符串  ,group() 可以一次性输入多个组号码,在这个情况下他将返回一个包含那些组所对应的元组  (1, 2, 3, .....)

**例子:**

```python
"""

'<div><a.*>(.*)</a><p>(.*)</p></div>' 
这个正则表达式里面
引号里面 代表匹配到的原始字符串的格式 匹配到的原始字符串就会像他一样的格式 group(0)就能查看
数据输出就是: <div><a href="https://www.itcast.cn" target="_blank">传智播客</a><p>Python</p></div>
每个小括号就代表了一个小子串 num 以1开始从左到右依次增加
所以
group(1) 是第一个 对应值是 传智播客
group(2)是第二个  对应值是 Python

"""
# 例子
my_str = '<div><a href="https://www.itcast.cn" target="_blank">传智播客</a><p>Python</p></div>'
# 从头匹配不用起别名,分组可以一行分多个，默认序号从左到右  分别为1，2，3，4， ~ n
res = re.match('<div><a.*>(.*)</a><p>(.*)</p></div>', my_str)
print(res.group(0))
print(res.group(1))
print(res.group(2))
print(res.groups())

```

**运行结果:**

![image-20220825211538106](E:\黑马培训\assets\image-20220825211538106.png)

**groups()** 返回一个包含所有小组字符串的**元组**,从1到所含的小组号

**注意:**  group(0)  恒定不变的就是表示匹配到的一个字符大串 即原始字符串 ,group(1), group(2), ... 表示从1, 2, ...个子串





##### 1.2.3.2 re.sub()方法

替换字符串中的匹配项

**语法:**   re.sub(pattern, repl, string, count=0)

pattern 字符串中需要被替换掉的内容

repl 用谁去替换

count 替换的最大次数  **count=0 的意思是默认全部替换**

**例子:**

```
import re
# 创建一个日期字符串
date = '2022-08-25'
# 输出打印字符串
print('Date: ', date)

# 将字符"-" 用空字符串替换掉
date_after = re.sub('-', '', date)
# 输出打印更改后的字符串
print('Date: ', date_after)
```

**运行结果:**

![image-20220825214713374](E:\黑马培训\assets\image-20220825214713374.png)



##### 1.2.3.3 re.spilt() 方法

**注意:** 正则里面split()函数不与,字符串的split()函数一样是用参数来作为切割的标准,正则里面是用正则表达式分割字符串

**语法:** re.split(pattern, string[,maxsplit=0])





**待补充**

## 1.3 数量词的贪婪模式和非贪婪模式

**匹配字符** 所有的量词都是对前一个单词而言

正则表达式通常用于在文本中查找匹配的字符串，python里数量词默认是贪婪的

**贪婪模式：**总是尝试尽可能多的匹配字符

**非贪婪模式：**总是尝试尽可能少的匹配字符

**例子：**

正则表达式"ab*"如果用于查找"aabbbcc",将找到"abbb", 使用非贪婪则是找到 "a"

**例子: 匹配出数字字符串后面的9**

![image-20220825222111700](E:\黑马培训\assets\image-20220825222111700.png)

以贪婪的模式查询,发现数据都被前面的取走了,而后面的取不到数据

**改进使用非贪婪模式:**

![image-20220825222345895](E:\黑马培训\assets\image-20220825222345895.png)

**解释:**   前面采取非贪婪的查询模式,会看后面的那一组需要什么数据,然后他匹配到这里就不去了,就是尽可能少的去匹配

# 贪婪与非贪婪模式的匹配原理分析

[![img](https://cdn2.jianshu.io/assets/default_avatar/1-04bbeead395d74921af6a4e8214b4f61.jpg)

正则表达式的两个普适性原则：

1、优先选择最左侧的匹配结果

2、标准的匹配量词是匹配优先的

第一条什么意思捏？ 就是拿到一个表达式/bo/ 去匹配一个字符串absdboosd , 首先这个表达式只有一个可能，那就是真的匹配到bo这两个字母，所以直接拿bo 匹配前两个字母，匹配不成功，右移，匹配bs 不成功继续右移，直到成功匹配或者字符串结束。

正则表达式的贪婪模式匹配规则：

首先要知道正则表达式中的量词有：

？【问号】 .【点号】 *【星号】 +【加号】 {min，max} 【集合表达式】

这几种量词都是默认尽可能多的去匹配内容的，即贪婪模式。当然这个还是会遵循第一条左侧优先的原则的， 比如说 /no*/ 去匹配 asnosdfanooooosdf 结果会是左边的no 而不是右边的nooooo

输入字符串： hai，!xiaozhang! can you tell !me! your qq ?

匹配规则： /!.+!/

那么匹配的时候， 第一个匹配规则的！， 那么从字符串的0位开始匹配，匹配到第一个感叹号时达成匹配。

![img](https://upload-images.jianshu.io/upload_images/9047296-721c4918f7a5e04e.jpeg?imageMogr2/auto-orient/strip|imageView2/2/w/617/format/webp)

image.jpeg

然后匹配控制权右移交给点号，于是开始匹配 .（点符号），点符号匹配换行符以外的任何字符，所以会一直匹配到最后的问号。

![img](https://upload-images.jianshu.io/upload_images/9047296-04ee1059a510fe15.jpeg?imageMogr2/auto-orient/strip|imageView2/2/w/604/format/webp)

image.jpeg

点加号匹配就结束了，开始匹配最后一个感叹号，因为已经到了结尾，所以匹配开始回溯，也就是交出一部分已经匹配的字符，以满足匹配到最右面的感叹号，一直匹配到 me后面的感叹号，发现符合规则，于是返回最后的匹配结果：

![img](https://upload-images.jianshu.io/upload_images/9047296-b3bccfe530156d29.jpeg?imageMogr2/auto-orient/strip|imageView2/2/w/631/format/webp)

image.jpeg

------------------------------------------分割线-------------------------------------------------

正则匹配的非贪婪模式的匹配规则： 【忽略优化量词】

非贪婪模式 / 忽略优化量词，即在量词后面跟一个问号，分别有 ?? ， *? ，+? , {m,n}?， {m,}? 这5种。这些词在可匹配可不匹配的情况下只选择不匹配，只有当整个表达式都无法匹配成功时才会尝试匹配这些量词前面的表达式。

以?? 来分析：

正则表达式为　/ab??c/

字符串为： abc

那么匹配的一个原理是： 先来到正则表达式的a

![img](https://upload-images.jianshu.io/upload_images/9047296-a5d22e5c28c40769.jpeg?imageMogr2/auto-orient/strip|imageView2/2/w/334/format/webp)

image.jpeg

ok匹配成功，然后匹配权后移来到b， b后面有两个问号， 所以是非贪婪模式， b?? 应视为一个整体来看。 也就是匹配权交给 b?? ,  这个时候会先忽略匹配，同时记录一个备选状态，匹配权交给c ，这时候规则c匹配到字母b ，也就是整个字符串不符合匹配规则，这个时候进行回溯，  找到备选状态，用b?? 来尝试匹配字母b，发现匹配成功，然后匹配权再次交给c，c去匹配c，ok的，全部匹配成功。结束匹配。

那么用 /ab??c/ 去匹配asdabcac 是什么结果呢？

当然也是只有一个abc 。 因为在不开启循环全局搜索的情况下，会优先匹配最左侧的结果 即普适性原则一 。

那么问题又来了。 用 /e??s/去匹配teset 会得出什么结果呢？

会得到一个es， 从上面的推理就可以得到，e??  首先得到匹配权利，发现可以忽略，于是进入备选状态，匹配权交给s，s去匹配t，匹配不通过，取回备选状态采用e去匹配t  依旧不匹配，于是字符串右移到第二位也就是字母e，这时候重新匹配，e??还是会被优先忽略（同时计入备选状态），匹配权交给s，s无法匹配e，于是取回最近的备选状态。也就是e去匹配e这时候匹配成功，然后匹配权交给s，s去匹配s 成功，整个匹配结束，返回结果es。

**其实这种记录备选状态再取回的方式叫做： 回溯**

（回溯在正则里面有个坑： 灾难性回溯catastrophic backtracking 详情可以了解专题文章： 灾难性回溯）

那么问题又来了，用 /e??se??/ 去匹配 teset 去出现什么结果呢？

我们还是从头分析，首先控制权交给e?? 发现这个是忽略优先，所以放入备选1，然后控制权交给s，  s去匹配t，失败，此时不会后移控制权，以为s是必须匹配项，如果s不匹配后面的都匹配也没有意义。所以取回匹配备选项1，e??  去匹配t，失败，于是字符串后移，e??再次重新拿到控制权，再次忽略，放入备选状态2，控制权交给s，  s去匹配e，失败，取回备选项2，e??去匹配e 成功，控制权交给s，s去匹配s成功，**匹配权交给最后的e??  发现是忽略优先，所以有限选择忽略，控制权右移（就结束了）所以整个匹配过程结束，匹配结果为es。**

# 2. Python爬虫 -- request库

## 2.1 利用request库写爬虫程序框架

**框架还不完善,后期再补充**

```python
# 1.导入request库和re模块
import request
import re

# 2.放入所爬网站的网址url
url = "http:\\www.baidu.com"

# 3.打开对应网址按F12 检查浏览器元素 刷新页面 拿到请求头和cookie 以键值对的形式存入字典中
header = {}

# 4.利用request库的get请求向对应网址发送请求,获取网址返回的响应报文
response = request.get(url,headers=header) # 注意这里的response是一个对象,它里面有个属性content可以获得响应报文中响应# # 体内容
# 5.网站发来的数据都是二进制编码类型的数据 所以要把响应体中的内容解码成字符串 因为它原来就是字符串类型的
html_str = response.content.decode()
"""
完成上面的操作以后我们就获取正则匹配的目标字符串  这时候我们就能去编写相对应的正则表达式去匹配需要的数据

"""
# 6.根据查找的内容所在的位置,从match,search,findall这三个函数中选择一个去操作,前两个返回的是一个对象,可以使用
# group(),groups() | group()返回的数据类型是str groups()返回的数据类型是元组 
# 最后一个函数findall返回的是列表则不能使用group,groups
res = re.match('正则表达式',html_str)

# 接下来就是对res进行操作了



```

