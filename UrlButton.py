from selenium import webdriver
import time

driver = webdriver.Chrome()

driver.get("http://exy.yunxuetang.cn/kng/kngindex.htm")

if __name__ == "main":
    x = input("x:")
    print(x)

