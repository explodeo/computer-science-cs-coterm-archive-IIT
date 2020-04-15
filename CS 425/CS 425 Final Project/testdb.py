###	Christopher Morcom		###
###	CS 425 Final Project	###
###	Spring 2018				###
###	Boris Glavic 			###

import mysql.connector as mariadb
import os
import getpass
import re
import sys
from termcolor import colored, cprint
import msvcrt as m

### GLOBAL STRUCT TO STORE USER LOCALLY TO SAVE QUERING TIME ###
class Admin:
	reg = 0
class Customer:
	cid = ""
	cust_type = 0
	cust_region = 0

### THESE ARE SYSTEM CALL FUNCTIONS ### 
def wait():
	m.getch()
	#pass
def clearcmdline(): 
	clear = lambda: os.system('"cls')
	clear()
def sysexit():
	mariadb_connection.commit()
	cursor.close()
	mariadb_connection.close()
	sys.exit()

###### INIT USER FUNCTIONS ######
def initialize():
	clearcmdline()
	done = False
	cprint("CS 425 FINAL PROJECT (Glavic Spring 2018)\nby CHRISTOPHER MORCOM\n", 'yellow', attrs = ['bold', 'underline'], end ="")
	cprint("""
	This project simulates an online distribution center by 
	using python 3.5 to connect to a MySql Server and executes 
	queries by reading user inputs. 

	To begin, please register with the project or log in by using
	one of the options below:

	Enter "1" to log in.
	Enter "2" to register.
	Enter q to quit.\n""", 'green', attrs=['bold'])
	while not done:
		tmp = input(">>> ")
		if tmp is "1": 
			done = True
			ret = login()
			if ret == "retry": return "retry"
			else: return ret
		elif tmp is "2": 
			done = True
			ret = registerCust()
			ret = login(True)
			return ret
		elif tmp == "q" or tmp == "quit" or tmp == "exit":
			return 0
		else:
			clearcmdline()
			cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
			print("""Bad input. Try again.

		Enter "1" to log in.
		Enter "2" to register.
		Enter q to quit\n""")

def login(isnew = False):
	clearcmdline()
	retry = True
	while retry:
		cprint("[LOGIN]\n", 'cyan')
		username = input("Please enter your username: ")
		password = getpass.getpass("\nPlease enter your password: ")
		query = "SELECT customer_id, customer_username, customer_password, customer_type, customer_region FROM customers WHERE customer_username = %s AND customer_password = %s"
		cursor.execute(query, (username, password))
		data = cursor.fetchall()	
		if not data:
			clearcmdline()
			cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
			print("Username or password incorrect.")
			done = False
			while not done:
				tmp = input("\nEnter 1 to try again, 2 to go back.\n\n>>> ")
				if tmp is "1":
					done = True
					retry = True
				elif tmp is "2": 
					done = True
					retry = False
					return "retry"
		else:
			Customer.cid = data[0][0]
			Customer.cust_type = data[0][3]	## need to def a table for this and permissions
			Customer.cust_region = data[0][4]
			if isnew:
				createAddress()
			return Customer.cust_type

def registerCust():
	getting = True
	while getting:
		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		query = "SELECT customer_username FROM customers"
		cursor.execute(query)
		data = cursor.fetchall()
		fname = input("Enter your first name: ")
		lname = input("Enter your last name: ")
		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		while True:
			cemail = input("Enter your email address: ")
			if not re.match(r"[^@]+@[^@]+\.[^@]+", cemail): 
				clearcmdline()
				cprint("[REGISTER]\n", 'cyan')
				cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
				print("Invalid email address.\n\n")
			else: break
		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		while True:
			username = input("Please enter a username: ")
			for x in data:
				if str((username,)) == str(x):
					clearcmdline()
					cprint("[REGISTER]\n", 'cyan')
					cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
					print("Username already taken.\n\n")
					break
			else: break
		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		while True:
			password = getpass.getpass("Please enter your password: ")
			passwordre = getpass.getpass("Please enter your password again: ")
			if password == passwordre: break
			else: 
				cprint("\n\n[ERROR] ", 'red', attrs=['bold', 'blink'])
				print("Passwords don't match.\n\n")
		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		while True:
			print("""Please Specify your region from the list below:
		Enter a number: \n
		  1 = North America
		  2 = Central America
		  3 = South America
		  4 = Africa
		  6 = Asia
		  7 = Europe
		  8 = Australia\n""")
			region = input(">>> ")
			if region.isdigit():
				region = int(region)
				if region < 1 or region > 8:
					clearcmdline()
					cprint("[REGISTER]\n", 'cyan')
					cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
					print("Invalid Region selection.\n\n")
				else: break
			else:
				clearcmdline()
				cprint("\n\n[ERROR] ", 'red', attrs=['bold', 'blink'])
				print("Please enter an integer only\n\n")

		clearcmdline()
		cprint("[REGISTER]\n", 'cyan')
		print("Your User Information is:\n\n\t\t"+"Firstname: '{}'\n\t\tLastname: '{}'\n\t\tEmail: '{}'\n\t\tUsername: '{}'\n\t\tPassword: (hidden)\n\t\tRegion:'{}'\n\n".format(1, fname, lname, cemail, username, password, region))
		cprint("Is this correct? [y/n]", 'cyan', end="")
		yn = (input("\n>>> ")).lower()
		if yn == "yes" or yn == 'y':

			addcustomer = "INSERT INTO customers VALUES (NULL, {}, '{}', '{}', '{}', '{}', '{}', {}, NULL);".format(1, fname, lname, cemail, username, password, region)
			cursor.execute(addcustomer)
			mariadb_connection.commit()
			cprint("Your user has been created.\nPlease log in to continue.\n", 'green', attrs = ['underline'])
			cprint("\n\n[Press any key to continue...]", 'blue', attrs = ['reverse', 'bold', 'concealed'])
			wait()
			getting = False
		else: 
			cprint("Retrying...\n", 'red', attrs=['bold', 'blink'])
			cprint("\n\n[Press any key to continue...]", 'red', attrs = ['reverse', 'bold', 'concealed'])
			wait()

###### DATABASE USER FUNCTIONS ######

def mainmenu(userlevel):
	clearcmdline()
	cprint("[MAIN MENU]\n", 'cyan')
	print("Choose an option:")
	if userlevel == 1:
		print("\n\t1 = buy products")
		print("\n\t2 = add account balance")
		cprint("\n\t3 = change user settings",'cyan')
		cprint("\n\t0 = exit", 'red')
		opt = input("\n>>> ")
		if opt == '1': 
			buyproducts()
		elif opt == '2': 
			adduserbalance()
		elif opt == '3': 
			clearcmdline()
			print("\n\t1 = edit addresses (add, modify, delete)")
			print("\n\t2 = change password")
			print("\n\t3 = change email")
			cprint("\n\t0 = go back", 'red')
			opt = input("\n>>> ")
			if opt == '1': 
				clearcmdline()
				print(" 1 = add address\n 2 = modify address\n 3 = delete address\n")
				opt = input(">>> ")
				if opt == '1': 
					createAddress()
				elif opt == '2': 
					modifyAddress()
				elif opt == '3': 
					deleteAddress()
				elif opt == '0': pass
			elif opt == '2': 
				changePW()
			elif opt == '3': 
				changeEmail()
		elif opt == '0': sysexit()
	elif userlevel == 2:
		print("\n\t1 = buy products")
		print("\n\t2 = add/remove products in warehouse")
		print("\n\t3 = view current budget")
		cprint("\n\t4 = change user settings",'cyan')
		cprint("\n\t0 = exit", 'red')
		opt = input("\n>>> ")
		if opt == '1':
			buyproducts()
		elif opt == '2':
			clearcmdline()
			opt = input("\t1 = add product in warehouse\n\t2 = remove product in warehouse\n\n>>> ")
			if opt == '1':
				warehouse_add()
			elif opt == '2': 
				warehouse_remove()
		elif opt == '3':
			viewBudget()
		elif opt == '4': 
			clearcmdline()
			print("\n\t1 = add user account balance")
			print("\n\t2 = edit addresses (add, modify, delete)")
			print("\n\t3 = change password")
			print("\n\t4 = change email")
			cprint("\n\t0 = go back", 'red')
			opt = input("\n>>> ")
			if opt == '1':
				adduserbalance()
			elif opt == '2':
				clearcmdline()
				print(" 1 = add address\n 2 = modify address\n 3 = delete address\n")
				opt = input(">>> ")
				if opt == '1': 
					createAddress()
				elif opt == '2': 
					modifyAddress()
				elif opt == '3': 
					deleteAddress()
				elif opt == '0': pass
			elif opt == '3':
				changePW()
			elif opt == '4':
				changeEmail()
		elif opt == '0': sysexit()
	elif userlevel == 3:
		print("\n\t1 = buy products")
		print("\n\t2 = manage employees in warehouse")
		print("\n\t3 = add/remove products in warehouse")
		print("\n\t4 = view/edit warehouse budget")
		cprint("\n\t5 = change user settings",'cyan')
		cprint("\n\t0 = exit", 'red')
		opt = input("\n>>> ")
		if opt == '1':
			buyproducts()
		elif opt == '2': 
			clearcmdline()
			opt = input("\n\t1 = add employee to warehouse\n\t2 = remove employee from warehouse\n\n>>> ")
			if opt == '1': 
				addEmployee()
			elif opt == '2':
				removeEmployee()
		elif opt == '3':
			clearcmdline()
			opt = input("\t1 = add product in warehouse\n\t2 = remove product in warehouse\n\n>>> ")
			if opt == '1':
				warehouse_add()
			elif opt == '2': 
				warehouse_remove()
		elif opt == '4': 
			clearcmdline()
			viewBudget()
			opt = input("Enter '1' to edit budget, else to go back:\n\n>>> ")
			if opt == '1': 
				editBudget()
		elif opt == '5': 
			print("\n\t1 = add user account balance")
			print("\n\t2 = edit addresses (add, modify, delete)")
			print("\n\t3 = change password")
			print("\n\t4 = change email")
			cprint("\n\t0 = go back", 'red')
			opt = input("\n>>> ")
			if opt == '1':
				adduserbalance()
			elif opt == '2':
				clearcmdline()
				print(" 1 = add address\n 2 = modify address\n 3 = delete address\n")
				opt = input(">>> ")
				if opt == '1': 
					createAddress()
				elif opt == '2': 
					modifyAddress()
				elif opt == '3': 
					deleteAddress()
				elif opt == '0': pass
			elif opt == '3':
				changePW()
			elif opt == '4':
				changeEmail()
		elif opt == '0': sysexit()
	elif userlevel == 4:
		print("\n\t1 = buy products")
		print("\n\t2 = manage region")
		print("\n\t3 = add account balance")
		cprint("\n\t4 = change user settings",'cyan')
		cprint("\n\t0 = exit", 'red')
		opt = input("\n>>> ")
		if opt == '1': 
			buyproducts()
		elif opt == '2': 
			clearcmdline()
			while True:
				print("""Specify the region to edit from the list below:
			Enter a number: \n
			  1 = North America
			  2 = Central America
			  3 = South America
			  4 = Africa
			  6 = Asia
			  7 = Europe
			  8 = Australia\n""")
				region = input(">>> ")
				if region.isdigit():
					region = int(region)
					Admin.reg = region
					if region < 1 or region > 8:
						clearcmdline()
						cprint("[ERROR] ", 'red', attrs=['bold', 'blink'])
						print("Invalid Region selection.\n\n")
					else: break
				else:
					clearcmdline()
					cprint("\n\n[ERROR] ", 'red', attrs=['bold', 'blink'])
					print("Please enter an integer only\n\n")
			clearcmdline()
			print("\n\t1 = add/remove employees in region")
			print("\n\t2 = add/remove managers")
			print("\n\t3 = add/remove products to warehouse")
			cprint("\n\t0 = go back", 'red')
			opt = input("\n>>> ")
			if opt == '1':
				clearcmdline()
				opt = input("\n\t1 = add employee to warehouse\n\t2 = remove employee from warehouse\n\n>>> ")
				if opt == '1': 
					addEmployee()
				elif opt == '2':
					removeEmployee()
			elif opt == '2':
				clearcmdline()
				opt = input("\n\t1 = add manager to warehouse\n\t2 = remove manager from warehouse\n\n>>> ")
				if opt == '1': 
					addManager()
				elif opt == '2':
					removeManager()
			elif opt == '3':
				clearcmdline()
				opt = input("\t1 = add product in warehouse\n\t2 = remove product in warehouse\n\n>>> ")
				if opt == '1':
					warehouse_add()
				elif opt == '2': 
					warehouse_remove()
		elif opt == '3': 
			adduserbalance()
		elif opt == '4': 
			clearcmdline()
			print("\n\t1 = edit addresses (add, modify, delete)")
			print("\n\t2 = change password")
			print("\n\t3 = change email")
			cprint("\n\t0 = go back", 'red')
			opt = input("\n>>> ")
			if opt == '1':
				adduserbalance()
			elif opt == '2':
				clearcmdline()
				print(" 1 = add address\n 2 = modify address\n 3 = delete address\n")
				opt = input(">>> ")
				if opt == '1': 
					createAddress()
				elif opt == '2': 
					modifyAddress()
				elif opt == '3': 
					deleteAddress()
				elif opt == '0': pass
			elif opt == '3':
				changePW()
			elif opt == '4':
				changeEmail()
		elif opt == '0': sysexit()

##### USER MODIFIERS #####
def createAddress():
	clearcmdline()
	cprint("[CREATE ADDRESS]\n", 'cyan')
	street = input("Enter your street address: ")
	clearcmdline()
	cprint("[CREATE ADDRESS]\n", 'cyan')
	street2 = input("Enter your street (field 2) (press Enter if not applicable): ")
	clearcmdline()
	cprint("[CREATE ADDRESS]\n", 'cyan')
	city = input("Enter your city: ")
	clearcmdline()
	cprint("[CREATE ADDRESS]\n", 'cyan')
	state = input("Enter your state/province (press Enter if not applicable): ")
	while True:
		clearcmdline()
		cprint("[CREATE ADDRESS]\n", 'cyan') 
		zipcode = input("Enter your zipcode: ")
		if re.match(r"^\d{5}(?:[-\s]\d{4})?$", zipcode):
			break
	addAddr = "INSERT INTO customers_addresses VALUES (NULL, {}, '{}', '{}', '{}', '{}', '{}');".format(Customer.cid, street, street2, city, state, zipcode)
	cursor.execute(addAddr)
	mariadb_connection.commit()
	cprint("Your address has been created.\n", 'green', attrs = ['underline'])
	cprint("\n\n[Press any key to continue...]", 'blue', attrs = ['reverse', 'concealed'])
	wait()

def modifyAddress(): 
	query = "SELECT * FROM customers_addresses WHERE customer_id = '{}'".format(Customer.cid)
	cursor.execute(query)
	data = cursor.fetchall()
	clearcmdline()
	done = False
	while not done:
		for x in data:
			cprint(x[0], 'cyan', attrs=['bold', 'underline'], end='')
			print('['+",".join(str(x[1:]))+']')
		cprint("\n\nChoose an address_id to replace from above.\n")
		try:
			repAID = int(input(">>> "))
			for x in data:
				if repAID == x[0]:  
					done = True
					break
		except: 
			clearcmdline()
	clearcmdline()
	cprint("[UPDATE ADDRESS]\n", 'green')
	street = input("Enter your street address: ")
	clearcmdline()
	cprint("[UPDATE ADDRESS]\n", 'green')
	street2 = input("Enter your street (field 2) (press Enter if not applicable): ")
	clearcmdline()
	cprint("[UPDATE ADDRESS]\n", 'green')
	city = input("Enter your city: ")
	clearcmdline()
	cprint("[UPDATE ADDRESS]\n", 'green')
	state = input("Enter your state/province (press Enter if not applicable): ")
	while True:
		clearcmdline()
		cprint("[UPDATE ADDRESS]\n", 'green') 
		zipcode = input("Enter your zipcode: ")
		if re.match(r"^\d{5}(?:[-\s]\d{4})?$", zipcode):
			break
	updateAddr = "UPDATE customers_addresses SET customer_id={}, street='{}', street2='{}', city='{}', state='{}', zipcode='{}' WHERE address_id={});".format(Customer.cid, street, street2, city, state, zipcode, repAID)
	cursor.execute(updateAddr)
	mariadb_connection.commit()
	cprint("Your address has been updated.\n", 'green', attrs = ['underline'])
	cprint("\n\n[Press any key to continue...]", 'blue', attrs = ['reverse', 'concealed'])
	wait()

def deleteAddress():
	query = "SELECT * FROM customers_addresses WHERE customer_id = '{}'".format(Customer.cid)
	cursor.execute(query)
	data = cursor.fetchall()
	clearcmdline()
	done = False
	while not done:
		if len(data) <= 0: 
			cprint("\n\n[ERROR] ", 'red', attrs=['bold', 'blink'], end = ' ')
			print("Must have at least one valid address.")
			return
		for x in data:
			cprint(x[0], 'cyan', attrs=['bold', 'underline'], end='')
			print('['+",".join(str(x[1:]))+']')
		cprint("\n\nChoose an address_id to replace from above.\n")
		try:
			delAID = int(input(">>> "))
			for x in data:
				if delAID == x[0]:  
					done = True
					break
		except: 
			clearcmdline()
		delAddr = "DELETE FROM customers_addresses WHERE address_id={}".format(delAID)
		cursor.execute(delAddr)
		mariadb_connection.commit()
		cprint("Address has been DELETED.\n", 'red', attrs = ['underline'])
		cprint("\n\n[Press any key to continue...]", 'blue', attrs = ['reverse', 'concealed'])
		wait()

def changePW():
	pass

def changeEmail():
	pass

##### PRODUCT MODIFIERS #####
def adduserbalance():
	pass

def buyproducts():
	pass

##### UNIQUE EMPLOYEE FUNCTIONS #####

def warehouse_add():
	p_c_id = Customer.cid
	done = False
	q1 = "SELECT * FROM categories;"
	cursor.execute(q1)
	data1 = cursor.fetchall()
	while not done:
		clearcmdline()
		cprint("[ADD PRODUCT] \n", 'red', attrs=['bold', 'blink'])
		print("cID\tcategory_Name")
		for x in data1:
			cprint(str(x[1])+" ", 'green', end="\t")
			print(x[0])
		cprint("\nChoose a category ID from above for your new product\n\n",'cyan')
		cate = input("\n>>> ")
		try:
			cate=int(cate)
			if cate in list(x[1] for x in data1):
				done = True
		except:
			pass

	q2 = "SELECT warehouse_id FROM warehouses w, categories c WHERE w.category_id = c.category_id AND region_id = {};".format(Customer.cust_region)
	cursor.execute(q2)
	data2 = cursor.fetchall()
	whid = data2[0][0]

	q3 = "INSERT INTO products VALUES (NULL, 'testapp','apptest',{},10,10,{},{})".format(Customer.cid, cate, whid)
	cursor.execute(q3)
	mariadb_connection.commit()
	cprint("Your Product has been created. Returning...\n", 'green', attrs=['bold', 'blink'])
	cprint("\n\n[Press any key to continue...]", 'cyan', attrs = ['reverse', 'bold', 'concealed'])
	wait()
def warehouse_remove():
	pass

def viewBudget():
	pass

##### UNIQUE MANAGER FUNCTIONS #####
def addEmployee():
	pass

def removeEmployee():
	pass

def editBudget():
	pass

##### UNIQUE ADMIN FUNCTIONS #####

def addManager():
	pass

def removeManager():
	pass

###### MAIN METHOD ######
mariadb_connection = mariadb.connect(user='root', password='toor', database='online_distro_center', connection_timeout = 60)
cursor = mariadb_connection.cursor()
Customer.cust_type = "retry"
while Customer.cust_type == "retry":
	if Customer.cust_type == 0: sysexit()
	initialize()
while True:
	opt = mainmenu(Customer.cust_type)