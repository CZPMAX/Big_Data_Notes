## 从0 ~ 1 写Python

### 1.用函数实现获取需要的列表

```python
def num_list(n, command):
    num1_list = []
    if command == 1:
        for i in range(0, n + 1):
            if i % 2 == 0:
                num1_list.append(i)
        print(f'0-{n}(包含n)以内的偶数组成的列表{num1_list}')
    # elif (command != True) and (command != False) :
    #     chicken(n)
    elif command == 2:
        chicken(n)
    else:
        chicken(n)


def chicken(n):
    num2_list = []
    for i in range(0, n + 1):
        if i % 2 != 0:
            num2_list.append(i)
    print(f'0-{n}(包含n)以内的奇数组成的列表{num2_list}')


m = eval(input('请输入一个数: '))
h = eval(input('请输入指令 1.需要偶数列表 2.需要奇数列表 按其他数值默认返回奇数列表: '))
num_list(m, h)

```

### 2.学生信息管理系统非文件版本

```python
import sys

user_list = [{'name': 'linda', 'age': 18, 'tel': "1231"},
             {'name': 'liming', 'age': 16, 'tel': '1234'}]
             
             
# 系统中的i代表每次从列表中取出的字典的字典名字

def show_menu():
    """定义菜单列表"""
    print('1. 添加学生')
    print('2. 查询所有学生')
    print('3. 查询某个学生')
    print('4. 修改某个学生')
    print('5. 删除某个学生')
    print('6. 退出系统')


def add_student():
    """添加学生信息操作"""
    # 1.定义一个接收同一学生信息的空字典
    student_list = {}
    name = input('请输入学生姓名: ')
    # 2.循环遍历列表，取出列表中的字典元素，每个元素代表一个学生变量
    for stu_dict in user_list:
        if stu_dict['name'] == name:
            print(f'学生{name}已存在，不能录入...')
            break
    # 3.for循环遍历user_list,元素都遍历完成以后,没有发现重名执行以下操作
    else:
        age = eval(input('请输入学生年龄: '))
        tel = input('请输入学生电话: ')
        # 4.修改字典中对应键的值,如果不存在则创建新的键值对
        student_list['name'] = name
        student_list['age'] = age
        student_list['tel'] = tel
        # 5.往用户列表中添加字典元素
        user_list.append(student_list)
        print('添加学生成功,请继续操作..')


# 定义函数 显示所有学生数据
def search_all_student():
    """显示所有学生数据"""
    # 遍历user_list 获取每个元素(字典) ,从字典中取出每个学生的姓名,年龄,电话并打印
    print('序号\t\t姓名\t\t年龄\t\t电话')
    for i, information in enumerate(user_list):
        # i 下标
        # stu_dict:元素(字典)
        print(f'{i}\t\t{information["name"]}\t\t{information["age"]}\t\t{information["tel"]}')


def search_student():
    """查询具体学生的信息"""
    name = input('请输入您要查询学生的名字')
    # 循环遍历用户列表取出元素下标和字典元素
    for m, i in enumerate(user_list):
        # 判断需要查询的姓名在列表字典元素中是否存在,若存在则输出具体信息,并结束循环
        if i['name'] == name:
            print('序号\t\t姓名\t\t年龄\t\t电话')
            print(f'{m}\t\t{i["name"]}\t\t{i["age"]}\t\t{i["tel"]}')
            break
    # 遍历完列表中所有元素还未找出需要的字典信息执行下列操作
    else:
        # 提供5次输姓名无法查询到信息的机会
        n = 1
        while n <= 5:
            print('查无此人,请重新输入')
            name = input('请输入您要查询学生的名字')
            for m, i in enumerate(user_list):
                if i['name'] == name:
                    print('序号\t\t姓名\t\t年龄\t\t电话')
                    print(f'{m}\t\t{i["name"]}\t\t{i["age"]}\t\t{i["tel"]}')
                    break
            n += 1
        #  5次都没有查到,执行下列操作,并结束函数
        else:
            print('查询次数已经用完，请重新选择功能..')
    # information = [i for i in user_list if i['name'] == name]
    # information_student = dict(information)
    #
    # print(f'您查询学生的全部信息为:{information_student}')
    # for i in user_list:
    #     if i['name'] != name:
    #         flag = 1
    #         print('查无此人,请重新输入')


def modify_student():
    """修改具体学生信息"""
    name = input('请输入您要修改的学生姓名: ')
    # information = [i for i in user_list if i['name'] == name]
    # information1 = dict(information)
    # num = eval(input('请输入您要操作的具体步骤: 1.修改'))
    # 循环遍历整个元素列表,对每个字典元素的name键的值进行判断,是否存在此学生
    for i in user_list:
        if i['name'] == name:
            num = eval(input('请输入要执行的操作 1.修改姓名 2.修改年龄 3.修改电话号码 4.修改全部信息 '))
            if num == 1:
                name = input('请输入新的学生姓名: ')
                if i['name'] == name:
                    print('修改后的姓名与已有姓名相同,请重新进行操作: ')
                    # 已经有相同的姓名,重新再次调用修改信息函数
                    modify_student()
                # 这里的break都是成功完成功能,为了不执行for语句的else而设定的
                else:
                    i['name'] = name
                    print('修改姓名成功，请选择要继续操作的功能...')
                    break
            elif num == 2:
                age = eval(input('请输入新的学生年龄: '))
                i['age'] = age
                print('修改年龄成功，请选择要继续操作的功能...')
                break
            elif num == 3:
                tel = input('请输入新的学生电话号码: ')
                i['tel'] = tel
                print('修改电话成功，请选择要继续操作的功能...')
                break
            elif num == 4:
                name = input('请输入新的学生姓名: ')
                if i['name'] == name:
                    print('修改后的姓名与已有姓名相同,请重新进行操作: ')
                    modify_student()
                i['name'] = name
                age = eval(input('请输入新的学生年龄: '))
                tel = input('请输入新的学生电话号码: ')
                i['age'] = age
                i['tel'] = tel
                print('全部信息修改成功,谢谢使用....')
                break
            else:
                print('输入的数字有错误,请重新操作: ')
                modify_student()
    # 输入要修改姓名在列表中的字典元素里不存在
    else:
        # 提示用户输入操作.选择继续哪一项操作
        num = eval(input('您输入的姓名有误:如需继续尝试请输入:1.是,若不 则输入 2.否: '))
        if num == 1:
            modify_student()
        elif num == 2:
            print('没有此人,不继续操作,谢谢使用再见....')

        else:
            print('尝试是否继续输入错误，系统强制退出.....')
            sys.exit(0)


def del_student():
    """删除具体学生的信息"""
    name = input('请输入要删除学生的姓名: ')
    # 遍历user_list列表中的元素,查看每个字典中的name键所对应的值是否与输入的值相同
    for i in user_list:
        if i['name'] == name:
            num = user_list.index(i)
            del user_list[num]
            return user_list
    # 遍历完整个列表没有发现要删除人的姓名,执行这里的操作
    else:
        # 给用户5次输入姓名的机会
        n = 1
        while n < 6:
            name = input('学生不存在，请重新输入要删除学生的姓名: ')
            for i in user_list:
                if i['name'] == name:
                    # 利用index函数找出符合要求的字典元组在列表中的位置下标
                    num = user_list.index(i)
                    del user_list[num]
                    # 删除完元素后,返回用户信息列表
                    return user_list
                # return返回后,删除函数执行就结束了,for循环也不再循环
            else:
                # 遍历完整个列表还是找不到,难么使n+1 继续循环操作
                n += 1
        else:
            # while循环正常结束执行的功能,代表机会用完还是输不对姓名
            print('可用的尝试次数已经用完，请重新选择功能..')


def main():
    """"定义功能菜单列表"""
    while True:
        show_menu()
        num = eval(input('请输入功能数字: '))
        if num == 1:
            add_student()
        elif num == 2:
            search_all_student()
        elif num == 3:
            search_student()
        elif num == 4:
            modify_student()
        elif num == 5:
            new_user_list = del_student()
            # 值是None的话默认是假
            if new_user_list:
                print(new_user_list)
        elif num == 6:
            sys.exit('谢谢使用再见!   欢迎再次使用学生信息管理系统')
        else:
            print('输入有误请重新输入')


# 调用功能菜单主函数
main()

```

### 3. 用for循环倒着输出三角形

```python
# 输出 9*9 乘法口诀表
for i in range(9,0,-1):
    for j in range(i, 0, -1):
        print(f'{j} * {i} = {i * j}', end='  ')
    print()
```

### 4. 综合案例注册登录

```python
# 定义字典存放用户输入的密码与用户名
import sys

user_list = [{'name': 'smart', 'pwd': '123*abc'}, {'name': 'jack', 'pwd': '123456'}]

# 登录注册案例整体框架代码实现
while True:
   # print(user_list)
    # 1.提示用户输入功能数字
    cmd_num = input('请输入操作: 1.用户注册 / 2.用户登录 / 3.退出程序 / 4.删除用户 / 5.查询用户 ')
    # 2.根据用户输入数字，做相应的操作
    if cmd_num == '1':
        # 用户注册
        # 提示用户输入注册的用户名和密码
        _name = input("请输入注册的用户名: ")
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
            print('恭喜您注册成功!!!!!!')
    elif cmd_num == '2':
        # print('用户登录')
        # 提示用户输入登录的用户名和密码
        _name = input('请输入登录的用户名: ')
        _password = input('请输入登录的密码: ')
        # 判断登录的用户名和密码是否正确
        for i in user_list:
            if i['name'] == _name and i['pwd'] == _password:
                print('登录成功')
                break
        else:
            print('用户名或者密码错误,请重新操作......')
    elif cmd_num == '3':
        sys.exit('程序结束，再见了您嘞.....')
    elif cmd_num == '4':
        # print('删除指定用户')
        _name = input('请输入要删除的用户的用户名: ')
        _password = input('请输入要删除的用户的密码: ')
        for i in user_list:
            if _name == i['name'] and _password == i['pwd']:
                # del i['name']
                # del i['pwd']
                del user_list[user_list.index(i)]
                print('删除用户成功')
                print(user_list)
                break
        else:
            print('删除用户验证失败.....')
    elif cmd_num == '5':
        print(user_list)
    else:
        print('输入有误! 请重新输入!......')

```

### 5. 使用匿名函数

实现调用一个函数处理加减乘除,函数名可以作为实参传给形参

```python
def compute(a, b, command):
    res = command(a, b)
    print(res)


add = lambda x, y: x + y
minus = lambda x, y: x - y
mul = lambda x, y: x * y
div = lambda x, y: x / y

compute(20,30,mul)
```

### 6. 学生信息管理系统文件版本

```python
import sys
import os
user_list = []


def show_menu():
    """定义菜单列表"""
    print('1. 添加学生')
    print('2. 查询所有学生')
    print('3. 查询某个学生')
    print('4. 修改某个学生')
    print('5. 删除某个学生')
    print('6. 退出系统')


def add_student():
    """添加学生信息操作"""
    # 1.定义一个接收同一学生信息的空字典
    student_list = {}
    name = input('请输入学生姓名: ')
    # 2.循环遍历列表，取出列表中的字典元素，每个元素代表一个学生变量
    for stu_dict in user_list:
        if stu_dict['name'] == name:
            print(f'学生{name}已存在，不能录入...')
            break
    # 3.for循环遍历user_list,元素都遍历完成以后,没有发现重名执行以下操作
    else:
        age = eval(input('请输入学生年龄: '))
        tel = input('请输入学生电话: ')
        # 4.修改字典中对应键的值,如果不存在则创建新的键值对
        student_list['name'] = name
        student_list['age'] = age
        student_list['tel'] = tel
        # 5.往用户列表中添加字典元素
        user_list.append(student_list)
        print('添加学生成功,请继续操作..')


# 定义函数 显示所有学生数据
def search_all_student():
    """显示所有学生数据"""
    # 遍历user_list 获取每个元素(字典) ,从字典中取出每个学生的姓名,年龄,电话并打印
    print('序号\t\t姓名\t\t年龄\t\t电话')
    for i, information in enumerate(user_list):
        # i 下标
        # stu_dict:元素(字典)
        print(f'{i}\t\t{information["name"]}\t\t{information["age"]}\t\t{information["tel"]}')


def search_student():
    """查询具体学生的信息"""
    name = input('请输入您要查询学生的名字')
    # 循环遍历用户列表取出元素下标和字典元素
    for m, i in enumerate(user_list):
        # 判断需要查询的姓名在列表字典元素中是否存在,若存在则输出具体信息,并结束循环
        if i['name'] == name:
            print('序号\t\t姓名\t\t年龄\t\t电话')
            print(f'{m}\t\t{i["name"]}\t\t{i["age"]}\t\t{i["tel"]}')
            break
    # 遍历完列表中所有元素还未找出需要的字典信息执行下列操作
    else:
        # 提供5次输姓名无法查询到信息的机会
        n = 1
        while n <= 5:
            print('查无此人,请重新输入')
            name = input('请输入您要查询学生的名字')
            for m, i in enumerate(user_list):
                if i['name'] == name:
                    print('序号\t\t姓名\t\t年龄\t\t电话')
                    print(f'{m}\t\t{i["name"]}\t\t{i["age"]}\t\t{i["tel"]}')
                    break
            n += 1
        #  5次都没有查到,执行下列操作,并结束函数
        else:
            print('查询次数已经用完，请重新选择功能..')
    # information = [i for i in user_list if i['name'] == name]
    # information_student = dict(information)
    #
    # print(f'您查询学生的全部信息为:{information_student}')
    # for i in user_list:
    #     if i['name'] != name:
    #         flag = 1
    #         print('查无此人,请重新输入')


def modify_student():
    """修改具体学生信息"""
    name = input('请输入您要修改的学生姓名: ')
    # information = [i for i in user_list if i['name'] == name]
    # information1 = dict(information)
    # num = eval(input('请输入您要操作的具体步骤: 1.修改'))
    # 循环遍历整个元素列表,对每个字典元素的name键的值进行判断,是否存在此学生
    for i in user_list:
        if i['name'] == name:
            num = eval(input('请输入要执行的操作 1.修改姓名 2.修改年龄 3.修改电话号码 4.修改全部信息 '))
            if num == 1:
                name = input('请输入新的学生姓名: ')
                if i['name'] == name:
                    print('修改后的姓名与已有姓名相同,请重新进行操作: ')
                    # 已经有相同的姓名,重新再次调用修改信息函数
                    modify_student()
                # 这里的break都是成功完成功能,为了不执行for语句的else而设定的
                else:
                    i['name'] = name
                    print('修改姓名成功，请选择要继续操作的功能...')
                    break
            elif num == 2:
                age = eval(input('请输入新的学生年龄: '))
                i['age'] = age
                print('修改年龄成功，请选择要继续操作的功能...')
                break
            elif num == 3:
                tel = input('请输入新的学生电话号码: ')
                i['tel'] = tel
                print('修改电话成功，请选择要继续操作的功能...')
                break
            elif num == 4:
                name = input('请输入新的学生姓名: ')
                if i['name'] == name:
                    print('修改后的姓名与已有姓名相同,请重新进行操作: ')
                    modify_student()
                i['name'] = name
                age = eval(input('请输入新的学生年龄: '))
                tel = input('请输入新的学生电话号码: ')
                i['age'] = age
                i['tel'] = tel
                print('全部信息修改成功,谢谢使用....')
                break
            else:
                print('输入的数字有错误,请重新操作: ')
                modify_student()
    # 输入要修改姓名在列表中的字典元素里不存在
    else:
        # 提示用户输入操作.选择继续哪一项操作
        num = eval(input('您输入的姓名有误:如需继续尝试请输入:1.是,若不 则输入 2.否: '))
        if num == 1:
            modify_student()
        elif num == 2:
            print('没有此人,不继续操作,谢谢使用再见....')

        else:
            print('尝试是否继续输入错误，系统强制退出.....')
            sys.exit(0)


def del_student():
    """删除具体学生的信息"""
    name = input('请输入要删除学生的姓名: ')
    # 遍历user_list列表中的元素,查看每个字典中的name键所对应的值是否与输入的值相同
    for i in user_list:
        if i['name'] == name:
            num = user_list.index(i)
            del user_list[num]
            return user_list
    # 遍历完整个列表没有发现要删除人的姓名,执行这里的操作
    else:
        # 给用户5次输入姓名的机会
        n = 1
        while n < 6:
            name = input('学生不存在，请重新输入要删除学生的姓名: ')
            for i in user_list:
                if i['name'] == name:
                    # 利用index函数找出符合要求的字典元组在列表中的位置下标
                    num = user_list.index(i)
                    del user_list[num]
                    # 删除完元素后,返回用户信息列表
                    return user_list
                # return返回后,删除函数执行就结束了,for循环也不再循环
            else:
                # 遍历完整个列表还是找不到,难么使n+1 继续循环操作
                n += 1
        else:
            # while循环正常结束执行的功能,代表机会用完还是输不对姓名
            print('可用的尝试次数已经用完，请重新选择功能..')


def save_data():
    """将学生数据保存至文件中"""
    with open('stu.txt', 'w', encoding='utf8') as f:
        _user_list = str(user_list)
        f.write(_user_list)


def load_data():
    """加载数据"""
    # 判断stu.txt文件是否存在
    if not os.path.isfile('./stu.txt'):
        print('stu.txt文件不存在')
        return
    with open('./stu.txt', 'r', encoding='utf8') as f:
        content = f.read()
        global user_list
        # eval函数意思就是去掉内容外面的字符串标号
        # 即你看到是什么内容的就是什么样的数据
        # 每次读取数据都是全部数据，所以直接覆盖没问题
        user_list = eval(content)


def main():
    """"定义功能菜单列表"""
    # 程序启动时，从stu.txt中加载数据放在user_list中
    load_data()
    while True:
        show_menu()
        num = eval(input('请输入功能数字: '))
        if num == 1:
            add_student()
        elif num == 2:
            search_all_student()
        elif num == 3:
            search_student()
        elif num == 4:
            modify_student()
        elif num == 5:
            new_user_list = del_student()
            # 值是None的话默认是假
            if new_user_list:
                print(new_user_list)
        elif num == 6:
            save_data()
            sys.exit('谢谢使用再见!   欢迎再次使用学生信息管理系统')
        else:
            print('输入有误请重新输入')


# 调用功能菜单主函数
main()

```

### 7. 字符串格式化写数据的示例代码

**字符串格式化不是只能放在print函数里面**

在外面格式化字符串也行

**示例代码**

```python
user_dict = {'name': 'smart', 'age': 18, 'tel': '13888888888'}

with open('user.txt','w',encoding='utf8')as f:

    for key, value in user_dict.items():
        res =f'{key}{":"}{value}\n'

        f.write(res)
```

### 8. 用pyecharts实现全国疫情地图

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

### 9. lambda函数的使用

![image-20220815183651599](E:\黑马培训\assets\image-20220815183651599.png)

### 10. 面向对象版 -- 学生名片管理系统

```python
"""
面向对象版-学生名片管理系统
学习目标：常握面向对象版学生名片管理系统的实现
"""
import os.path

"""
面向对象分析：

学生类：Student
    属性：
        name：姓名
        age：年龄
        tel：电话
    方法：
        __init__(self, _name, _age, _tel)：初始化学生对象的属性
        __str__(self)：打印学生对象时，显示学生对象的信息

管理类：SysManager
    属性：
        实例化方法
        user_list：保存所有学生的数据
        要是写成类属性,那么只要在下面方法中调用到这个属性的方法都是类方法
    方法：
        show_menu(self)：显示菜单
        add_stu_info(self)：添加学生数据
        show_all_stu(self)：显示所有学生数据
        show_one_stu(self)：查询指定学生数据
        update_one_stu(self)：修改指定学生数据
        delete_one_stu(self)：删除指定学生数据
        load_info(self)：从文件中加载学生数据
        save_info(self)：将学生数据保存到文件中
        start(self)：学生管理系统主逻辑
        
数据存储分析：
1）存储一个学生数据：学生对象
2）存储所有学生学生：列表嵌套学生对象
"""


class Student(object):
    """学生类"""

    def __init__(self, _name, _age, _tel):
        """学生对象初始化方法,给创建的学生对象添加初始属性"""
        self.name = _name
        self.age = _age
        self.tel = _tel

    def __str__(self):
        return f'姓名:{self.name},年龄:{self.age}, 电话:{self.tel}'

    def stu_todict(self):
        return {'name': self.name, 'age': self.age, 'tel': self.tel}


class SysManager(object):
    """管理系统类"""

    def __init__(self):
        """管理系统的初始化方法"""
        self.stu_list = []

    @staticmethod
    def menu():
        print('1. 添加学生')
        print('2. 查询某个学生')
        print('3. 查询所有学生')
        print('4. 修改某个学生')
        print('5. 删除某个学生')
        print('6. 保存信息')
        print('7. 退出系统')

    def add_stu_info(self):
        """添加学生信息"""
        _name = input('请输入添加学生的名字: ')
        # 循环遍历列表元素
        for stu in self.stu_list:
            if stu.name == _name:
                print('已经存在学生信息,禁止添加...')
                break
        else:
            # 提示用户接着输入具体信息
            _age = input('请输入添加学生的年龄: ')
            _tel = input('请输入添加学生的电话: ')
            # 创建学生对象,保存学生数据
            new_stu = Student(_name, _age, _tel)
            # 将对象放入列表之中
            self.stu_list.append(new_stu)

    def show_all_stu(self):
        """查询所有学生的信息"""
        print('序号\t\t姓名\t\t年龄\t\t电话')
        # 遍历列表获取学生对象
        for i, stu in enumerate(self.stu_list):
            # i 元素下标
            # stu学生对象
            print(f'{i + 1}\t\t{stu.name}\t\t{stu.age}\t\t{stu.tel}')

    def show_one_stu(self):
        """查询某个学生的信息"""
        _name = input('请输入学生的姓名: ')
        # 遍历列表获取学生对象,将获取出来学生对象的姓名与查询学生的姓名作比较
        for i, stu in enumerate(self.stu_list):
            # i 下标
            # stu 学生对象
            if stu.name == _name:
                # 如果存在打印出相应信息
                print('序号\t\t姓名\t\t年龄\t\t电话')
                print(f'{i + 1}\t\t{stu.name}\t\t{stu.age}\t\t{stu.tel}')
                break
        else:
            # 遍历完列表没有找出想要的对象
            print(f'{_name}查无此人...')

    def update_one_stu(self):
        """修改某个学生的信息"""
        _name = input('请输入学生的名字: ')
        # 遍历stu_list列表取出对象
        for stu in self.stu_list:
            # 如果学生已存在，提示输入修改之后的姓名、年龄、电话，然后将对应的学生数据进行修改
            if stu.name == _name:
                new_name = input('请输入修改之后的姓名: ')
                new_age = input('请输入修改后的年龄: ')
                new_tel = input('请输入修改后的电话号码: ')
                # 修改学生对象的name、age和tel这三个属性的值
                stu.name = new_name
                stu.age = new_age
                stu.tel = new_tel
                break
        else:
            print(f'系统内没有此人不能修改{_name}学生信息')

    def delete_one_stu(self):
        """删除某个学生信息"""
        # 输入要删除学生的姓名
        _name = input('请输入要删除学生的姓名: ')
        # 根据输入姓名，遍历查找对应的学生
        for i, stu in enumerate(self.stu_list):
            if stu.name == _name:
                del self.stu_list[i]
                print('删除成功!!!')
                break
        else:
            print('没有此次学生信息，删除操作失败...')

    def save_data(self):
        # 定义一个空列表，用来存储转化成字典的每个学生对象
        temp_list = []
        # 遍历实例化属性列表取出对象
        for stu in self.stu_list:
            # 将对象转换成字典，存入列表中
            stu_dict = stu.stu_todict()
            temp_list.append(stu_dict)
        with open('stu.txt', 'w', encoding='utf8') as f:
            # 将列表转换成字符串类型写入文件中
            content = str(temp_list)
            f.write(content)

    def load_data(self):
        """文件中读取数据"""
        # 判断文件stu.txt是否存在
        # not false ==> true
        # 导入模块快捷键 alt + enter
        if not os.path.isfile('./stu.txt'):
            print('stu.txt文件不存在...')
            return
        with open('stu.txt', 'r', encoding='utf8') as f:
            content = f.read()  # str
            temp_list = eval(content)  # temp_list是一个列表套字典
            # 遍历temp_list中的每一个字典，把每个字典中的学生数据再保存成一个对象
            for stu_dict in temp_list:
                # {'name':'smart', 'age':18, 'tel':'135'}
                _name = stu_dict['name']
                _age = stu_dict['age']
                _tel = stu_dict['tel']
                # 将每个字典数据，再次转换成对象，存入实例化列表中
                stu = Student(_name, _age, _tel)
                self.stu_list.append(stu)

    def start(self):
        """管理系统的主逻辑方法"""
        # 开始程序,从磁盘中加载已有数据
        self.load_data()
        while True:
            print(self.stu_list)
            SysManager.menu()
            num = eval(input('请输入要选择的功能: '))
            if num == 1:
                self.add_stu_info()
            elif num == 2:
                self.show_one_stu()
            elif num == 3:
                self.show_all_stu()
            elif num == 4:
                self.update_one_stu()
            elif num == 5:
                self.delete_one_stu()
            elif num == 6:
                self.save_data()
            elif num == 7:
                self.save_data()
                print('程序结束,感谢使用...')
                break
            else:
                print('功能数字输入有误,请重新输入..')


"""
每实例化一个次SysManager类，实例化列表就清空一次，总而言之每从新执行一次程序，实例化属性列表就是空列表
因为每次程序启动的时候，最先干的事情就是把数据，从磁盘里面取出来，转换成对象，而写数据的时候，要先从实例化属性列表中取对象，然后
转换成字典，再加入自己定义的空列表中，接着转化为字符串，把数据写入文件中，所以不存在数据丢失的情况

"""
stu = SysManager()
stu.start()

```

### 11. 自己理解版本 -- 学生名片管理系统

```python
# 定义学生类 有三个属性name , age ,tel
import os.path


class Student(object):
    # 实例化方法,初始化学生对象传入三个数据
    def __init__(self, _name, _age, _tel):
        self.name = _name
        self.age = _age
        self.tel = _tel

    def stu_todict(self):
        return {'name': self.name, 'age': self.age, 'tel': self.tel}


# 定义学生信息管理类
class SysManager(object):
    """
    写实例化方法__init__在里面实例化创建一个空列表
    在这里定义空列表可以用实例化,也可以用类属性
    但是用了类属性的话,下面的方法只要用到这个空列表,他们的方法类型就变成了类方法
    程序执行的步骤是先实例化一个SysManager类的对象,然后去调用主程序
    这里面传进来的对象就是SysManager类类型的
    所以你用类方法传进来的实参就是这个类本身,因此现在用实例化方法来创建空列表和用类属性来创建空列表作用都一样
    没有影响,但是还是建议用实例化方法来创建

    在我们写这个程序的时候就已经规定了,在列表中存放的数据是学生类的对象

    每次运行程序,实例化SysManger类,里面的列表就会格式化,变成空列表,当从磁盘中加载数据以后,它里面就会有数据了
    边读边往空列表中加入数据

    """

    def __init__(self):
        self.stu_list = []

    # 当定义一个方法时,这个方法用不到这个类中的任何数据,这个放到其他地方又不合适,放在这个类中很符合常理,此时我们就要将这个方法定义为静态方法
    # 菜单定义到这个地方比较合适
    @staticmethod
    def stu_menu():
        print('1. 添加学生信息')
        print('2. 查询所有学生信息')
        print('3. 查询某个学生信息')
        print('4. 修改某个学生信息')
        print('5. 删除某个学生信息')
        print('6. 保存学生信息')
        print('7. 退出系统')

    def add_stu(self):
        # 提示学生输入名字
        _name = input('请输入要添加学生的名字: ')
        # 遍历学生列表,找出符合条件的学生对象
        for stu in self.stu_list:
            if stu.name == _name:
                print('用户已经存在,不能添加!')
                break
        else:
            # 请输入要添加的学生具体信息
            _age = eval(input('请输入要添加学生的年龄: '))
            _tel = input('请输入要添加学生的电话: ')

            # 实例化学生对象传入数据
            student = Student(_name, _age, _tel)
            # 将对象加入列表之中
            self.stu_list.append(student)

    def show_all_stu(self):
        """展示所以学生的信息"""
        print(f'序号\t\t姓名\t\t年龄\t\t电话')
        for i, stu in enumerate(self.stu_list):
            # i 序号
            # stu 学生对象
            print(f'{i + 1}\t\t{stu.name}\t\t{stu.age}\t\t{stu.tel}')

    def show_one_stu(self):
        """查询某个学生的具体信息"""
        # 提示用户输入要查询学生的名字
        _name = input('请输入要查询学生的姓名: ')
        for i, stu in enumerate(self.stu_list):
            # i 每个对象元素的序号
            # stu 学生对象
            if stu.name == _name:
                print(f'序号\t\t姓名\t\t年龄\t\t电话')
                print(f'{i + 1}\t\t{stu.name}\t\t{stu.age}\t\t{stu.tel}')
                print('查询数据成功!!')
                break
        else:
            print('查无此人,请重新操作....')

    def update_stu(self):
        """修改某个具体学生的数据"""
        _name = input('输入要修改的学生姓名: ')
        for stu in self.stu_list:
            if stu.name == _name:
                new_name = input('请输入修改后的新学生名字: ')
                new_age = eval(input('请输入修改后新的学生年龄: '))
                new_tel = input('请输入新的学生电话号码: ')

                # 将对象的相对应的数据进行修改
                stu.name = new_name
                stu.age = new_age
                stu.tel = new_tel
                break
        else:
            print('没有此人的信息,无法修改学生信息..')

    def del_stu(self):
        """删除具体某个学生的信息"""
        _name = input('请输入删除学生的名字')
        for i, stu in enumerate(self.stu_list):
            if stu.name == _name:
                del self.stu_list[i]
                break
        else:
            print('没有这个人,删除失败..')

    def save_stu(self):
        """保存用户信息"""
        # 定义一个空列表用来接收实例化列表里面对象转换成字典的数据
        _list = []
        for stu in self.stu_list:
            _dict = stu.stu_todict()
            _list.append(_dict)
        with open('stu.txt', 'w', encoding='utf') as f:
            content = str(_list)
            f.write(content)

    def load_stu(self):
        """加载用户全部信息"""
        # 判断是否有stu.txt这个文件
        if not os.path.isfile('stu.txt'):
            print('文件不存在...')
            return
        with open('stu.txt', 'r', encoding='utf') as f:
            content = f.read()
            _content = eval(content)
            for stu in _content:
                _name = stu['name']
                _age = stu['age']
                _tel = stu['tel']
                student = Student(_name, _age, _tel)
                self.stu_list.append(student)

    def start(self):
        """学生信息管理系统主程序"""
        # 开始运行程序从磁盘中加载数据
        self.load_stu()
        while True:
            SysManager.stu_menu()
            num = eval(input('请输入要操作的具体功能: '))
            if num == 1:
                self.add_stu()
            elif num == 2:
                self.show_all_stu()
            elif num == 3:
                self.show_one_stu()
            elif num == 4:
                self.update_stu()
            elif num == 5:
                self.del_stu()
            elif num == 6:
                self.save_stu()
            elif num == 7:
                self.save_stu()
                print('程序退出,谢谢使用..')
                break
            else:
                print('输入数值非法,请重新输入..')

stu = SysManager()
stu.start() ===> start(stu) ==>默认把这个对象当做参数传进来
```

### 12. 利用爬虫爬取网站多张图片

```python
# 导入相对应的模块
import requests
import re


# 获取图片资源路径的网址
url = 'https://www.tupianzj.com/meinv/'

# 浏览器请求头,让爬虫伪装成浏览器
header = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36',
     'pragma': 'no-cache',
     'cookie': 't=2f28ba9ce8ef11fd424e8fa40831686f; r=7894; Hm_lvt_f5329ae3e00629a7bb8ad78d0efb7273=1661244239; Hm_lpvt_f5329ae3e00629a7bb8ad78d0efb7273=1661264396'}
# 获取web服务器返回的响应报文

response = requests.get(url, headers=header)

# 获取响应报文中响应体的内容利用response.content 来获取 这时候获取的内容是二进制编码类型
print(type(response.content.decode('gbk')), response.content.decode('gbk'))

# 将解码出来的html文档赋值给html_str 类型是 str
html_str = response.content.decode('gbk') # str
print(html_str)
# 使用正则提取自己所需的资源路径
res = re.findall('<img src=".*?"', html_str) # 上面的标识符也要写完整
print(res)
# 遍历列表截取需要的资源路径
for i,path_str in enumerate(res):
    file_path = path_str[10:len(path_str)-1]
    response = requests.get(file_path)
    with open(f'./sources/mm/{i}.jpg', 'wb') as f:
        # 将文件写入对应的路径下的文件中
        f.write(response.content)
```

