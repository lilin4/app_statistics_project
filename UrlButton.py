from selenium import webdriver
import time

driver = webdriver.Chrome()

driver.get("http://exy.yunxuetang.cn/kng/knowledgecatalogsearch.htm?sf=UploadDate&s=dc&st=null")

if __name__ == "main":
    x = input("x:")
    print(x)

