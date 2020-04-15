# CHRISTOPHER MORCOM
# SQL ASSIGNMENT 1
# CS 425
# 28 Feb 2018
#PART 1
	# 1.	Find all products missing a product model number?
		SELECT * FROM products 
		WHERE products_model = "" OR products_model = NULL; 
	# 2.	Find all products with over $500 and less than $699?
		SELECT * FROM products 
		WHERE products_price > 500 AND products_price < 699;
	# 3.	Find all products that need to be ordered?
		SELECT * FROM products, (SELECT products_id,orders_status FROM orders,orders_products) AS a 
		WHERE orders_status = 2;
	# 4.	Find all products in Leather Laptop Cases category?
		SELECT * FROM products a,(SELECT products_id FROM categories c,products_to_categories WHERE c.categories_id = 27) AS b
		WHERE a.products_id = b.products_id;
	# 5.	Find all Categories with the keyword “women” in the text?
		SELECT * FROM categories c, categories_description d 
		WHERE c.categories_keywords LIKE '%women%' OR d.categories_name LIKE '%women%';
	# 6.	Find all categories added in 2014?
		SELECT * FROM categories, categories_description
		WHERE date_added LIKE '%2014%';  
	# 7.	Find all categories added before 2014 and has not been modified?
		SELECT * FROM categories 
		WHERE date_added < '2014-1-1' AND last_modified = "" or last_modified <= date_added;
	# 8.	Find all products to all main categories (main category has no parent)?
		SELECT * 
		FROM products p, 
			(SELECT DISTINCT(ptc.products_id), c.categories_id, cd.categories_name 
		    FROM products_to_categories ptc, categories c, categories_description cd 
		    WHERE c.parent_id = '') AS a
		WHERE p.products_id = a.products_id
		ORDER BY a.categories_name;
	# 9.	Find all products that are Tote Bags?
		SELECT * FROM products a,(SELECT products_id, categories_name FROM categories c,products_to_categories,categories_description cd WHERE cd.categories_name= "Tote Bags") AS b
		WHERE a.products_id = b.products_id;
	# 10.	List all product models that does not start with word “BAG”
		SELECT products_model FROM products 
		WHERE products_model NOT LIKE "BAG%";

# PART 2
	# 11.	Find all customers without orders?
		SELECT * FROM customers c,orders od 
		WHERE c.customers_id <> od.customers_id 
		GROUP BY c.customers_id ORDER BY c.customers_id; 
	# 12.	Find all customers not in the US?
		SELECT * FROM customers, orders 
		WHERE customers_country <> "United States";
	# 13.	Find the ids of all customers who subscribe to newsletter emails
		SELECT customers_id FROM customers 
		WHERE customers_newsletter = 1;
	# 14.	List the names and orders total for female customers?
		SELECT c.customers_firstname, SUM(a.products_ordered) AS total_products_ordered
		FROM customers c, orders o, (SELECT op.orders_id, p.products_ordered FROM products p, orders_products op WHERE p.products_id = op.products_id) AS a
		WHERE c.customers_gender = 'f' AND o.orders_id = a.orders_id
		GROUP BY c.customers_firstname;
	# 15.	Group orders by category of products ordered and list (category name, order total, products ordered and date of orders) >
		SELECT categories_name, products_ordered, date_purchased, SUM(a.products_ordered) AS total_orders
		FROM  orders o, orders_products op,
			(SELECT c.categories_id, cd.categories_name 
		    FROM categories c, categories_description cd WHERE c.categories_id = cd.categories_id) AS c,
			(SELECT DISTINCT(p.products_id), products_ordered, pc.categories_id
		    FROM products AS p CROSS JOIN products_to_categories AS pc) AS a
		WHERE a.categories_id = c.categories_id AND a.products_id = op.products_id
		GROUP BY categories_name;
	# 16.	Find all customers with orders?
		SELECT * FROM customers c,orders od 
		WHERE c.customers_id = od.customers_id 
		GROUP BY c.customers_id ORDER BY c.customers_id;  
	# 17.	Find all customer names and addresses for those customers with orders having a pending status?
		SELECT customers_firstname,customers_street_address,customers_city,customers_state,customers_postcode,customers_country  FROM customers c, orders o
		WHERE o.orders_status = 1;
	# 18.	Find the total shipping cost for orders that have been shipped to the US or United Kingdom?
		SELECT customers_country, SUM(shipping_cost) AS total_shipping_costs
		FROM orders, orders_status 
		WHERE orders_status_name = "Shipped" AND 
			(customers_country = "United States" OR customers_country = "United Kingdom")
		GROUP BY customers_country;
	# 19.	Find the total for all orders shipped to international destination?
		SELECT customers_country, COUNT(orders_id) AS total_orders
		FROM orders, orders_status 
		WHERE orders_status_name = "Shipped" AND customers_country <> "United States"
		GROUP BY customers_country;
	# 20.	Write your own query that span multiple tables? (customers with orders that pay with paypal)
		SELECT c.customers_id, c.customers_firstname, a.payment_method 
		FROM customers c, (SELECT orders_status,payment_method FROM orders,orders_products) AS a 
		WHERE a.orders_status = 2 AND a.payment_method LIKE "PayPal%"
		GROUP BY c.customers_id ORDER BY c.customers_id;

		SELECT c.customers_id, c.customers_firstname, a.payment_method 
		FROM customers c, (SELECT customers_id, payment_method FROM orders,orders_products) AS od
		WHERE c.customers_id = od.customers_id AND a.payment_method LIKE "PayPal%"
		GROUP BY c.customers_id ORDER BY c.customers_id;  
	# 21.	Generate a mailing list for the company’s newsletter.
		SELECT delivery_name,delivery_street_address,delivery_city,delivery_postcode,delivery_state,delivery_country
		FROM customers c,orders o 
		WHERE customers_newsletter = 1 AND c.customers_id = o.customers_id;