#Christopher Morcom
#Ismael Granillo
#CS 425
#28 February 2018
CREATE TABLE regions(
	region_name VARCHAR(30) NOT NULL,
	region_description VARCHAR(254),
	PRIMARY KEY(region_name));
INSERT INTO regions(region_name) VALUES ('North America'), ('Central America'), ('South America'), ('Africa'), ('Asia'), ('Europe');
CREATE TABLE customers(
	customer_id INT(6) NOT NULL AUTO_INCREMENT,
	customer_type INT(2) NOT NULL,
	customer_first VARCHAR(100) NOT NULL,
	customer_last VARCHAR(100) NOT NULL,
	customer_email VARCHAR(254) NOT NULL,
	customer_username VARCHAR(254) NOT NULL,
	customers_password VARCHAR(30) NOT NULL,
	customers_region VARCHAR(30) NOT NULL,
	customers_img_status INT(1) NOT NULL,
	PRIMARY KEY (customer_id),
	FOREIGN KEY (customers_region) REFERENCES regions(region_name));
CREATE TABLE customers_addresses(
	customer_address VARCHAR(254) NOT NULL,
	customer_id INT(6) NOT NULL,
	FOREIGN KEY(customer_id) REFERENCES customers(customer_id));
CREATE TABLE categories(
	category_name VARCHAR(100) NOT NULL,
	PRIMARY KEY(category_name));
CREATE TABLE warehouses(
	category_name VARCHAR(100) NOT NULL,
	category_description VARCHAR(254),
	region_name VARCHAR(30) NOT NULL,
	warehouse_id INT(6) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY(warehouse_id),
	FOREIGN KEY(region_name) REFERENCES regions(region_name));
CREATE TABLE managers(
	manager_phone VARCHAR(25) NOT NULL,
	manager_first VARCHAR(100) NOT NULL,
	manager_last VARCHAR(100) NOT NULL,
	manager_email VARCHAR(254) NOT NULL,
	manager_address VARCHAR(254) NOT NULL,
	warehouse_id INT(6) NOT NULL,
	FOREIGN KEY(warehouse_id) REFERENCES warehouses(warehouse_id));
CREATE TABLE employees(
	employee_region VARCHAR(30) NOT NULL,
	employee_id INT(6) NOT NULL AUTO_INCREMENT,
	employee_name VARCHAR(100) NOT NULL,
	employee_phone VARCHAR(20) NOT NULL,
	employee_email VARCHAR(254) NOT NULL,
	employee_dob VARCHAR(30) NOT NULL,
	employee_category VARCHAR(100) NOT NULL,
	employee_warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(employee_id),
	FOREIGN KEY(employee_region) REFERENCES regions(region_name),
	FOREIGN KEY(employee_category) REFERENCES categories(category_name),
	FOREIGN KEY(employee_warehouse_id) REFERENCES warehouses(warehouse_id));
CREATE TABLE products(
	product_id INT(6) NOT NULL AUTO_INCREMENT,
	product_name VARCHAR(100) NOT NULL,
	product_description VARCHAR(254),
	product_customer_id INT(6) NOT NULL,
	product_price INT(6) NOT NULL,
	product_quantity INT(9) NOT NULL,	#cost is derived
	product_category VARCHAR(100) NOT NULL,
	product_warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(product_id),
	FOREIGN KEY(product_category) REFERENCES categories(category_name),
	FOREIGN KEY(product_customer_id) REFERENCES customers(customer_id),
	FOREIGN KEY(product_warehouse_id) REFERENCES warehouses(warehouse_id));
CREATE TABLE images(
	image_product_id INT(6) NOT NULL,
	image_link VARCHAR(254) NOT NULL,
	image_error VARCHAR(254) NOT NULL,
	image_size VARCHAR(254) NOT NULL,
	image_name VARCHAR(254) NOT NULL,
	image_temp_name VARCHAR(254) NOT NULL,
	image_type VARCHAR(254) NOT NULL,
	PRIMARY KEY(image_product_id),
	FOREIGN KEY(image_product_id) REFERENCES products(product_id));
CREATE TABLE attributes(
	product_color VARCHAR(90),
	product_weight NUMERIC(6,3),
	product_size VARCHAR(100),
	product_id INT(6) NOT NULL,
	PRIMARY KEY(product_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id));
CREATE TABLE inventory(
	product_id INT(6) NOT NULL,
	warehouse_id INT(6) NOT NULL,
	stock INT(11) NOT NULL,
	max_stock INT(11) NOT NULL,
	status INT(2), #lo = 1,mid = 2,hi = 3
	refill_date VARCHAR(254) NOT NULL,
	refill_point VARCHAR(254) NOT NULL,
	PRIMARY KEY(product_id),
	FOREIGN KEY(product_id) REFERENCES products(product_id),
	FOREIGN KEY(warehouse_id) REFERENCES warehouses(warehouse_id));