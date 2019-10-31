#!/usr/bin/env python
# -*- coding:utf8 -*-

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class CSDNMarkdown(object):
    driver = None
    timeout = None
    album_url = ""
    waiter = None

    def __init__(self, album):
        '''
        初始化
        '''

        # 请求url
        self.album_url = album

        # 初始化webdriver
        self.driver = webdriver.Firefox()
        # 打开页面
        self.driver.get(self.album_url)
        # 设置等待上限5秒
        self.timeout = WebDriverWait(self.driver, 5)
        self.waiter = WebDriverWait(self.driver, 2)

    def turn(self):
        # 获取第一页文章标题及超链接，并组合markdown链接
        self.get_title()

        # 先打开第二页
        p = self.driver.current_url
        # 翻页直到最后一页
        while p is not None:
            p = self.next()
            # 获取每一页文章标题及超链接，并组合markdown链接
            self.get_title()

    def next(self):
        try:
            # 判断是否到了末页
            locator = (By.CSS_SELECTOR, '.js-page-next.js-page-action.ui-pager.ui-pager-disabled')
            self.waiter.until(EC.presence_of_element_located(locator))
            return None
        except:
            # 判断是否有分页
            try:
                # 找到下一页按钮
                locator = (By.CSS_SELECTOR, '.js-page-next.js-page-action.ui-pager')
                nextPage = self.timeout.until(EC.presence_of_element_located(locator))
                # 翻页
                nextPage.click()
                return self.driver.current_url
            except:
                return None

    def get_title(self):
        # 获取专栏的每一项的标题
        titles = self.driver.find_elements(By.XPATH, '//h2[@class="title"]')
        for t in titles:
            # 通过标题获取对应的文章的超链接
            link = t.find_element(By.XPATH, './/..//..')
            # 组合成markdown格式超链
            fmt = "[%s](%s)" % (t.text, link.get_attribute('href'))
            print(fmt)

    def __del__(self):
        self.driver.close()


if __name__ == "__main__":
    album_url = 'https://blog.csdn.net/yageeart/article/category/854202'
    app = CSDNMarkdown(album_url)
    app.turn()
