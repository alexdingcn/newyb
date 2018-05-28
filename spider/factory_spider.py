#!/usr/bin/python
# -*- coding: UTF-8 -*-
import scrapy
from scrapy.http import Request
from scrapy.selector import Selector
import MySQLdb


class BlogSpider(scrapy.Spider):
	name = 'blogspider'
	allowed_domains = ['db.yaozh.com']
	url_prefix = 'https://db.yaozh.com/shengchanqiye/'
	headers = {
		"Accept": "*/*",
		"Accept-Encoding": "gzip,deflate",
		"Accept-Language": "en-US,en;q=0.8,zh-TW;q=0.6,zh;q=0.4",
		"Connection": "keep-alive",
		"Content-Type":" application/x-www-form-urlencoded; charset=UTF-8",
		"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36",
		"Referer": "http://db.yaozh.com/"
	}
	cookies = {
		'kztoken':'nJail6zJp6iXaJqWl2tpYmJoZpma',
		'his':'a%3A3%3A%7Bi%3A0%3Bs%3A28%3A%22nJail6zJp6iXaJqWl2toaWhqY5mZ%22%3Bi%3A1%3Bs%3A28%3A%22nJail6zJp6iXaJqWl2toaWhqY5qY%22%3Bi%3A2%3Bs%3A28%3A%22nJail6zJp6iXaJqWl2toaWhqY5qa%22%3B%7D',
		'think_language':'zh-CN',
		'PHPSESSID':'jv9tjk2hcfr7uf5re2i23j56d7',
		'MEIQIA_EXTRA_TRACK_ID':'2ff1eac6cbcf17ed864f46b38c2f684d',
		}
		
		
	def start_requests(self):
		for i in range(6425,7509):
			yield Request(self.url_prefix + str(i) + '.html', method="get", cookies = self.cookies, headers= self.headers)

	def parse(self, response):
		items = []

		companyName = permit = category_code = legal_person = '';
		responsible_person = scope =  city = reg_address = address = issue_date = expired_date = '';
		license = quality_person = phone = fax = email = website = issued_by = issuer = audit = auditor = audit_phone = '';
	
		for title in response.css('tr'):
			key = title.css('th ::text').extract_first().strip();
			val = title.css('span ::text').extract_first().strip()

			if (key == '企业名称'):
				companyName = val;
			elif (key == '药品生产许可证编号'):
				permit = val;
			elif (key == '分类码'):
				category_code = val;
			elif (key == '法定代表人'):
				legal_person = val;
			elif (key == '企业负责人'):
				responsible_person = val;
			elif (key == '生产范围'):
				scope = val;
			elif (key == '省市'):
				city = val;
			elif (key == '注册地址'):
				reg_address = val;
			elif (key == '生产地址'):
				address = val;
			elif (key == '发证日期'):
				issue_date = val;
			elif (key == '有效期截止日'):
				expired_date = val;
			elif (key == '社会信用代码/组织机构代码'):
				license = val;
			elif (key == '质量负责人'):
				quality_person = val;
			elif (key == '公司电话' and val != '暂无权限'):
				phone = val;
			elif (key == '公司传真' and val != '暂无权限'):
				fax = val;
			elif (key == '公司邮箱' and val != '暂无权限'):
				email = val;
			elif (key == '公司网址'):
				website = val;
			elif (key == '发证机关'):
				issued_by = val;
			elif (key == '签发人'):
				issuer = val;
			elif (key == '日常监管机构'):
				audit = val;
			elif (key == '日常监管人员'):
				auditor = val;
			elif (key == '监督举报电话'):
				audit_phone = val;
				
		if companyName:
			print(companyName);
			# 打开数据库连接
			db = MySQLdb.connect("rm-uf60j6333650mx823eo.mysql.rds.aliyuncs.com", "newyb", "12345678", "newyb", charset='utf8' )
			# 使用cursor()方法获取操作游标 
			cursor = db.cursor()
			# SQL 插入语句
			#, 		 
			sql = """INSERT INTO factory_db (company_name,permit, category_code, legal_person,
					 responsible_person, scope, city, reg_address, address, issue_date, expired_date,
					 license, quality_person, phone, fax, email, website, issued_by, issuer, audit, auditor, audit_phone, source)
					 VALUES ('%s', '%s', '%s', '%s','%s','%s', '%s', '%s', '%s','%s','%s', '%s', '%s', '%s','%s','%s', '%s', '%s', '%s','%s','%s','%s','%s')""" % (companyName, permit,category_code, legal_person,
					 responsible_person, scope, city, reg_address, address, issue_date, expired_date,
					 license, quality_person, phone, fax, email, website, issued_by, issuer, audit, auditor, audit_phone, response.url)
			try:
			   # 执行sql语句
			   cursor.execute(sql)
			   # 提交到数据库执行
			   db.commit()
			except:
			   # Rollback in case there is any error
			   db.rollback()
			# 关闭数据库连接
			db.close()
