CREATE TABLE regions(
	region_id INT(2) NOT NULL,
	region_name VARCHAR(30) NOT NULL,
	region_description VARCHAR(254),
	PRIMARY KEY(region_id));
CREATE TABLE customers(
	customer_id INT(6) NOT NULL,
	customer_type INT(2) NOT NULL,
	customer_name VARCHAR(100) NOT NULL,
	customer_email VARCHAR(254) NOT NULL,
	customers_region INT(2) NOT NULL,
	PRIMARY KEY (customer_id),
	FOREIGN KEY (customers_region) REFERENCES regions(region_id));
CREATE TABLE user_logins(
	customer_id INT(6) NOT NULL,
	customers_username VARCHAR(30) NOT NULL,
	customers_password VARCHAR(60) NOT NULL,
	customer_email VARCHAR(254) NOT NULL,
	customers_status INT(2) NOT NULL,
	PRIMARY KEY(customers_username),
	FOREIGN KEY(customer_id) REFERENCES customers(customer_id)); #verify with email and status
CREATE TABLE categories(
	category_name VARCHAR(100) NOT NULL,
	category_id INT(6) NOT NULL,
	PRIMARY KEY(category_id));
CREATE TABLE warehouses(
	category_name VARCHAR(100) NOT NULL,
	category_description VARCHAR(254),
	warehouse_region_id INT(2) NOT NULL,
	warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(warehouse_id),
	FOREIGN KEY(warehouse_region_id) REFERENCES regions(region_id));
CREATE TABLE managers(
	manager_phone VARCHAR(25) NOT NULL,
	manager_name VARCHAR(100) NOT NULL,
	manager_email VARCHAR(254) NOT NULL,
	manager_id INT(6) NOT NULL,
	warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(manager_id),
	FOREIGN KEY(warehouse_id) REFERENCES warehouses(warehouse_id));
CREATE TABLE employees(
	employees_region_id INT(2) NOT NULL,
	employee_id INT(6) NOT NULL,
	employee_name VARCHAR(100) NOT NULL,
	employee_phone VARCHAR(20) NOT NULL,
	employee_email VARCHAR(254) NOT NULL,
	employee_dob VARCHAR(30) NOT NULL,
	employee_category INT(6) NOT NULL,
	employee_warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(employee_id),
	FOREIGN KEY(employees_region_id) REFERENCES regions(region_id),
	FOREIGN KEY(employee_category) REFERENCES categories(category_id),
	FOREIGN KEY(employee_warehouse_id) REFERENCES warehouses(warehouse_id));
CREATE TABLE addresses(
	street VARCHAR(254) NOT NULL,
	street2 VARCHAR(254),
	street3 VARCHAR(254),
	city VARCHAR(254) NOT NULL,
	state VARCHAR(254),
	country VARCHAR(254) NOT NULL,
	zipcode VARCHAR(254) NOT NULL,
	customer_id INT(6),
	manager_id INT(6),
	FOREIGN KEY(manager_id) REFERENCES managers(manager_id),
	FOREIGN KEY(customer_id) REFERENCES customers(customer_id));
CREATE TABLE products(
	product_id INT(6) NOT NULL,
	product_name VARCHAR(100) NOT NULL,
	product_description VARCHAR(254),
	product_customer_id INT(6) NOT NULL,
	product_price INT(6) NOT NULL,
	product_quantity INT(9) NOT NULL,	#cost is derived
	product_category INT(6) NOT NULL,
	product_warehouse_id INT(6) NOT NULL,
	PRIMARY KEY(product_id),
	FOREIGN KEY(product_category) REFERENCES categories(category_id),
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
	refill_date DATE NOT NULL,
	refill_point VARCHAR(254) NOT NULL,
	PRIMARY KEY(product_id),
	FOREIGN KEY(product_id) REFERENCES products(product_id),
	FOREIGN KEY(warehouse_id) REFERENCES warehouses(warehouse_id));