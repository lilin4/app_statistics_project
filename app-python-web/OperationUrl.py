# coding=utf-8
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from time import sleep
import threading
import time

play_handle_list = []

class operationUrl(object):
    input_user = None
    input_passwd = None
    counter = 0
    pageNum = None


    # 登录
    def __init__(self, album):
        #设置浏览器后台运行
        #option=webdriver.ChromeOptions()
        #option.add_argument('headless') # 设置option
        print("启动驱动.......")
        #self.driver = webdriver.Chrome(chrome_options=option)# 调用带参数的谷歌浏览器

        self.driver = webdriver.Chrome()
        print("等待加载.......")
        self.driver.get(album)
        while (self.input_user is None or self.input_passwd is None):
            sleep(1)
            self.input_user = self.driver.find_element_by_id("txtUserName2")
            self.input_passwd = self.driver.find_elements_by_id("txtPassword2")[0]
            self.counter += 1
            print("等待登录页面加载......." + str(self.counter))
            # 等待5秒重新刷新页面
            if (self.counter == 6):
                # 刷新
                self.driver.refresh()
                self.counter = 0
                print("登录页面加载超时,正在刷新重试.......")
        # 输入账号密码
        self.input_user.send_keys("linl87e@visionvera.com")
        self.input_passwd.send_keys("linli1993")
        # 操作登录按钮
        input_login = self.driver.find_elements_by_id("btnLogin2")[0]
        input_login.click()
        print("登录成功,等待界面加载.......")
        tilte = None
        while (tilte == None or bool(1 - tilte.__eq__("首页 - 视联动力信息技术股份有限公司"))):
            print("界面正在加载,请稍后.......")
            sleep(1)
            tilte = self.driver.title
        print(tilte)

    # 操作
    def operation(self):
        self.timeout = WebDriverWait(self.driver, 5)
        self.waiter = WebDriverWait(self.driver, 2)
        print("进入找知识.......")
        self.driver.find_elements_by_id("hylIndex0")[2].click()
        print("进入更多.......")
        self.driver.find_elements_by_css_selector("[class ='el-kngmore-more pull-right text-grey']")[1].click()
        print("跳转页面.......")
        #获取所有课程页面
        self.pageNum = self.driver.window_handles[-1]
        self.driver.switch_to.window(self.pageNum)
        self.driver.current_window_handle

        #多线程监控点击按钮
        try:
            threading.Thread(target=course_while, args=(self,)).start()
        except:
            print("多线程操作")

        while True:
            #当前页面所有课程
            course_list = self.driver.find_elements_by_css_selector("[class ='el-placehold-body hand']")
            #更多页面跳转数
            for course in course_list:
                self.timer(course)
            #返回知识页面
            self.driver.switch_to.window(self.pageNum)
            self.driver.current_window_handle
            paging=None
            while paging is None:
                paging= self.driver.find_elements_by_class_name("pagetext")
            if len(paging) < 2:
                print("看完了!.......")
                return
            paging[1].click()

    def timer(self, course):
        global play_handle_list
        #获取当前时间
        while True:
            print("校验播放完成.......")
            #if len(self.driver.window_handle) <= 5:
            if len(play_handle_list) <= 3:
                #进入学习列表页
                self.driver.switch_to.window(self.pageNum)
                self.driver.current_window_handle
                #点击课程图标
                course.click()
                #获取播放视频handle
                window_handle = self.driver.window_handles[-1]
                self.driver.switch_to.window(window_handle)
                self.driver.current_window_handle
                #验证课程包
                try:
                    progress_bar=None
                    while progress_bar is None:
                        progress_bar = self.driver.find_element_by_id("lblStudySchedule").text
                    if (progress_bar.__eq__("100.0")):
                        print("课程包学习度 100% 关闭")
                        self.driver.close()
                    else:
                        #开始学习按钮
                        print("课程包学习度 "+str(progress_bar)+"% ")
                        self.driver.find_element_by_id("btnStartStudy").click()
                        play_handle_list.append(window_handle)
                    return
                except:
                    print("课程包不存在,直接进入学习页面!.......")
                #验证学习进度
                play_amount =None
                while play_amount is None:
                    play_amount = self.driver.find_element_by_id("ScheduleText").text
                #学习进取100%
                if (play_amount.__eq__("100%")):
                    print("课程学习进度 100% ")
                    self.driver.close()
                    return
                #开始学些,将页面信息保存至集合
                print("课程学习进度 "+str(play_amount)+"开始学习!")
                play_handle_list.append(window_handle)
                #网站静音

                #验证学分


                return
            #print (time.strftime("%Y-%m-%d %H:%M:%S", time.localtime()))
            sleep(60*3)

def course_while(self):
    global play_handle_list
    #获取当前时间
    while True:
        print("3分钟校验.......")
        #循环验证10分钟检查按钮
        #for x in self.driver.window_handles: #循环所有窗口
        for x in play_handle_list:
            print("校验handler.......["+x+"]")
            try:
                self.driver.switch_to.window(x)
                self.driver.current_window_handle
                #验证学习进度
                play_amount = self.driver.find_element_by_id("ScheduleText").text
                #学习进取100%
                if (play_amount.__eq__("100%")):
                    print("handler["+x+"]"+"播放完毕")
                    #删除保存的handle,释放页面
                    play_handle_list.remove(x)
                    self.driver.close()
                    continue
                #查找点击按钮确定操作
                try:
                    buttons = self.driver.find_elements_by_class_name("btnok")
                    if len(buttons) > 1:
                        buttons[1].click()
                        print("handler["+x+"]"+":检测到10分钟提醒按钮")
                    else:
                        print("handler["+x+"]"+":没有检测到10分钟提醒按钮")
                    #self.driver.switch_to.alert.click()
                except:
                    print("handler["+x+"]"+":没有检测到10分钟提醒按钮")
            except:
                play_handle_list.remove(x)
                print("handler["+x+"]"+":没有找到!")
        sleep(60*3)




if __name__ == "__main__":
    app = operationUrl("http://exy.yunxuetang.cn/login.htm")
    app.operation()
