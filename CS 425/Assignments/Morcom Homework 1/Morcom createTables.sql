# CHRISTOPHER MORCOM
# SQL ASSIGNMENT 1
# CS 425
# 28 Feb 2018

CREATE TABLE categories_description (
	categories_id INT(4) NOT NULL,
	categories_name VARCHAR(30) NOT NULL,
	PRIMARY KEY (categories_id));

CREATE TABLE categories (
	categories_id INT(4) NOT NULL,
	parent_id INT(4) NOT NULL,
	sort_order INT(4) NOT NULL,
	date_added DATETIME NOT NULL,
	last_modified DATETIME,
	categories_keywords VARCHAR(100),
    PRIMARY KEY (categories_id),
    FOREIGN KEY (categories_id) REFERENCES categories_description(categories_id));

#customer_address can be derived

CREATE TABLE customers (
	customers_id INT(5) NOT NULL,
	customers_gender CHAR(2) NOT NULL,
	customers_firstname VARCHAR(30) NOT NULL,
	customer_dob VARCHAR(25) NOT NULL,
	customers_default_address_id INT(4) NOT NULL,
	customers_newsletter CHAR(4),
	PRIMARY KEY (customers_id));

CREATE TABLE orders_status (
	orders_status_id INT(2) NOT NULL,
	language_id INT(1) NOT NULL,
	orders_status_name CHAR(40) NOT NULL,
	public_flag INT(1) NOT NULL,
	downloads_flag INT(1) NOT NULL,
	PRIMARY KEY (orders_status_id));

CREATE TABLE orders (
	orders_id INT(4) NOT NULL,
	customers_id INT(3) NOT NULL,
	customers_name VARCHAR(30) NOT NULL,
	customers_street_address VARCHAR(100) NOT NULL,
	customers_city VARCHAR(30) NOT NULL,
	customers_postcode VARCHAR(15) NOT NULL,
	customers_state VARCHAR(30) NOT NULL,
	customers_country VARCHAR(30) NOT NULL,
	customers_address_format_id INT(1) NOT NULL,
	delivery_name VARCHAR(30) NOT NULL,
	delivery_street_address VARCHAR(100) NOT NULL,
	delivery_city VARCHAR(30) NOT NULL,
	delivery_postcode VARCHAR(15) NOT NULL,
	delivery_state VARCHAR(30) NOT NULL,
	delivery_country VARCHAR(30) NOT NULL,
	delivery_address_format_id INT(1) NOT NULL,
	billing_street_address VARCHAR(100) NOT NULL,
	billing_city VARCHAR(30) NOT NULL,
	billing_postcode VARCHAR(12) NOT NULL,
	billing_state VARCHAR(30) NOT NULL,
	billing_country VARCHAR(30) NOT NULL,
	billing_address_format_id INT(1) NOT NULL,
	payment_method VARCHAR(50) NOT NULL,
	last_modified DATETIME,
	date_purchased DATETIME,
	orders_status INT(2) NOT NULL,
	shipping_cost NUMERIC(6,3),
	PRIMARY KEY (orders_id),
    FOREIGN KEY (customers_id) REFERENCES customers(customers_id),
    FOREIGN KEY (orders_status) REFERENCES orders_status(orders_status_id));

CREATE TABLE products (
	products_id INT(4) NOT NULL,
	products_quantity INT(3) NOT NULL,
	products_model VARCHAR(15),
	products_price INT(5) NOT NULL,
	products_date_added DATETIME NOT NULL,
	products_last_modified DATETIME,
	products_weight NUMERIC(5,2) NOT NULL,
	products_status INT(1) NOT NULL,
	products_tax_class_id INT(2) NOT NULL,
	manufacturers_id INT(2) NOT NULL,
	products_ordered INT(2) NOT NULL,
	PRIMARY KEY (products_id));

CREATE TABLE products_description (
	products_id INT(4) NOT NULL,
	products_name VARCHAR(75),
	products_viewed INT(6) NOT NULL,
	PRIMARY KEY (products_id),
    FOREIGN KEY (products_id) references products(products_id));

CREATE TABLE orders_products (
	orders_products_id INT(4) NOT NULL,
	orders_id INT(4) NOT NULL,
	products_id INT(4) NOT NULL,
	products_quantity INT(3) NOT NULL,
	PRIMARY KEY (orders_products_id),
    FOREIGN KEY (products_id) REFERENCES products(products_id),
    FOREIGN KEY (orders_id) REFERENCES orders(orders_id));

CREATE TABLE products_to_categories (
	products_id INT(4) NOT NULL,
	categories_id INT(4) NOT NULL,
    FOREIGN KEY (products_id) references products(products_id),
    FOREIGN KEY (categories_id) REFERENCES categories(categories_id));
    