# CHRISTOPHER MORCOM
# SQL ASSIGNMENT 1
# CS 425
# 28 Feb 2018

CREATE TABLE categories (
    categories_id INT(11) NOT NULL,
    parent_id INT(11) NOT NULL,
    sort_order INT(11) NOT NULL,
    date_added VARCHAR(255) NOT NULL,
    last_modified VARCHAR(25),
    PRIMARY KEY (categories_id)
);

CREATE TABLE categories_keys (
    categories_keyword_id INT(11) NOT NULL,
    categories_id INT(11) NOT NULL,
    categories_keywords VARCHAR(255),
    PRIMARY KEY (categories_keyword_id),
    FOREIGN KEY (categories_id) REFERENCES categories(categories_id)
);

CREATE TABLE categories_description (
    categories_id INT(11) NOT NULL,
    categories_name VARCHAR(25) NOT NULL,
    PRIMARY KEY (categories_id),
    FOREIGN KEY (categories_id)
        REFERENCES categories (categories_id)
);

CREATE TABLE customers (
    customers_id INT(11) NOT NULL,
    customers_gender VARCHAR(5) DEFAULT NULL,
    customers_firstname VARCHAR(45) DEFAULT NULL,
    customers_dob VARCHAR(45) DEFAULT NULL,
    customers_default_address_id INT(11) NOT NULL,
    customers_newsletter INT(11) DEFAULT NULL,
    PRIMARY KEY (customers_id)
);

CREATE TABLE customers_addresses(
    customers_address_id INT(3) NOT NULL,
    customers_id INT(3) NOT NULL,
    customers_street_address VARCHAR(100) NOT NULL,
    customers_city VARCHAR(30) NOT NULL,
    customers_postcode VARCHAR(15) NOT NULL,
    customers_state VARCHAR(30) NOT NULL,
    customers_country VARCHAR(30) NOT NULL,
    customers_address_format_id INT(1) NOT NULL,
    PRIMARY KEY (customers_address_id),
    FOREIGN KEY (customers_id) REFERENCES customers (customers_id)
);

CREATE TABLE orders_status (
    orders_status_id INT(11) NOT NULL,
    language_id INT(11) NOT NULL,
    orders_status_name VARCHAR(45) NOT NULL,
    public_flag INT(11) NOT NULL,
    downloads_flag INT(11) NOT NULL,
    PRIMARY KEY (orders_status_id)
);

CREATE TABLE products (
    products_id INT(11) NOT NULL,
    products_quantity INT(11) NOT NULL,
    products_model VARCHAR(25) DEFAULT NULL,
    products_price INT(11) NOT NULL,
    products_date_added VARCHAR(255) DEFAULT NULL,
    products_last_modified VARCHAR(255) DEFAULT NULL,
    products_weight DECIMAL(12 , 2 ) DEFAULT NULL,
    products_status INT(11) NOT NULL,
    products_tax_class_id INT(11) NOT NULL,
    manufacturers_id INT(11) NOT NULL,
    products_ordered INT(11) NOT NULL,
    PRIMARY KEY (products_id)
);

CREATE TABLE products_description (
    products_id INT(11) NOT NULL,
    products_name VARCHAR(255) NOT NULL,
    products_viewed INT(11) NOT NULL,
    PRIMARY KEY (products_id),
    FOREIGN KEY (products_id)
        REFERENCES products (products_id)
);

CREATE TABLE products_to_categories (
    products_id INT(11) NOT NULL,
    categories_id INT(11) NOT NULL,
    PRIMARY KEY (products_id , categories_id),
    FOREIGN KEY (products_id)
        REFERENCES products (products_id),
    FOREIGN KEY (categories_id)
        REFERENCES categories (categories_id)
);

CREATE TABLE orders (
    orders_id INT(4) NOT NULL,
    customers_address_id INT(3) NOT NULL,
    customers_id INT(3) NOT NULL,
    delivery_name VARCHAR(30) NOT NULL,
    delivery_street_address VARCHAR(100) NOT NULL,
    delivery_city VARCHAR(30) NOT NULL,
    delivery_postcode VARCHAR(15) NOT NULL,
    delivery_state VARCHAR(30) NOT NULL,
    delivery_country VARCHAR(30) NOT NULL,
    delivery_address_format_id INT(1) NOT NULL,   
    last_modified VARCHAR(40) DEFAULT NULL,
    date_purchased VARCHAR(40) DEFAULT NULL,
    orders_status INT(2) NOT NULL,
    shipping_cost DECIMAL(6 , 3 ) DEFAULT NULL,
    PRIMARY KEY (orders_id),
    FOREIGN KEY (customers_id)
        REFERENCES customers (customers_id),
    FOREIGN KEY (orders_status)
        REFERENCES orders_status (orders_status_id)
);

CREATE TABLE billing_addresses(
    orders_id INT(4) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    billing_street_address VARCHAR(100) NOT NULL,
    billing_city VARCHAR(30) NOT NULL,
    billing_postcode VARCHAR(12) NOT NULL,
    billing_state VARCHAR(30) NOT NULL,
    billing_country VARCHAR(30) NOT NULL,
    billing_address_format_id INT(1) NOT NULL,
    PRIMARY KEY (orders_id),
    FOREIGN Key (orders_id) REFERENCES orders(orders_id)
);

CREATE TABLE orders_products (
    orders_products_id INT(11) NOT NULL,
    orders_id INT(11) NOT NULL,
    products_id INT(11) NOT NULL,
    products_quantity INT(11) NOT NULL,
    PRIMARY KEY (orders_products_id),
    FOREIGN KEY (products_id)
        REFERENCES products (products_id),
    FOREIGN KEY (orders_id)
        REFERENCES orders (orders_id)
);