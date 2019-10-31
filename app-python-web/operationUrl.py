# coding=utf-8
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from time import sleep


class operationUrl(object):
    input_user = None
    input_passwd = None
    counter = 0
    play_counter = 5

    # 登录
    def __init__(self, album):
        print("启动驱动.......")
        self.driver = webdriver.Chrome()
        print("等待加载.......")
        self.driver.get(album)
        while (self.input_user == None or self.input_passwd == None):
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
        a_url = self.driver.find_elements_by_css_selector("[class ='el-kngmore-more pull-right text-grey']")[1].click()
        print("跳转页面.......")
        self.driver.switch_to.window(self.driver.window_handles[-1])
        self.driver.current_window_handle
        course_list = self.driver.find_elements_by_css_selector("[class ='el-placehold-body hand']")
        pageNum = self.driver.window_handles
        for course in course_list:
            self.timer(course)

    def timer(self, course):
        # 获取打开页面数量
        while (self.driver.window_handles >= 7):
            sleep(60 * 1)
            course.click()
            self.driver.switch_to.window(self.driver.window_handles[-1])
            self.driver.current_window_handle

            #课程包
            try:
                progress_bar = self.driver.find_element_by_id("lblStudySchedule").text
                if (progress_bar.__eq__("100.0")):
                    self.driver.close()
                return
            except:
                print("课程包不存在,直接进入学习页面!.......")

            #学习页面
            play_amount = self.driver.find_element_by_id("ScheduleText").text
            if (play_amount.__eq__("100%")):
                self.driver.close()
                return
            return




if __name__ == "__main__":
    app = operationUrl("http://exy.yunxuetang.cn/login.htm")
    app.operation()
