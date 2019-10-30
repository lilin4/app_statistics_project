from selenium import webdriver
import time

driver = webdriver.Chrome()
driver.get("http://exy.yunxuetang.cn/kng/knowledgecatalogsearch.htm?sf=UploadDate&s=dc&st=null")

if __name__ == "__main__":

    print(driver.title)

