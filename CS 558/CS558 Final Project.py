import mysql.connector as mariadb
import os
import getpass
import re
import sys
from termcolor import colored, cprint
import msvcrt as m

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


###  MAIN METHOD ###
mariadb_connection = mariadb.connect(user='root', password='toor', database='logger', connection_timeout = 60)


