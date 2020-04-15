# CHRISTOPHER MORCOM
# SQL ASSIGNMENT 1
# CS 425
# 28 Feb 2018

# 1. Find all customers without orders?
	SELECT * FROM customers 
	WHERE customers_id NOT IN (SELECT customers_id FROM orders);
# 2. Find all customers not in the US?
	SELECT * FROM customers c, orders o
	WHERE c.customers_id = o.customers_id AND o.customers_country <> "United States";
# 3. Find the ids of all customers who subscribe to newsletter emails
	SELECT customers_id FROM customers c 
	WHERE customers_newsletter = 1;
# 4. List the names and orders total for female customers?
	SELECT c.customers_firstname, COUNT(*) AS orders_total
	FROM customers c, orders o
	WHERE  c.customers_gender = 'f' AND c.customers_id = o.customers_id
	GROUP BY c.customers_firstname;
# 5. Group orders by category of products ordered and list (category name, order total, products ordered and date of orders) 
	SELECT categories_name, products_ordered, date_purchased, COUNT(*) as order_total
	FROM orders o
		JOIN orders_products op ON op.orders_id = o.orders_id
	    JOIN products p ON p.products_id = op.products_id
	    JOIN products_to_categories ptc ON ptc.products_id = p.products_id
	    JOIN categories c ON c.categories_id = ptc.categories_id
	    JOIN categories_description cd ON c.categories_id = cd.categories_id
	GROUP BY categories_name;
# 6. Find all customers with orders?
	SELECT * FROM customers 
	WHERE customers_id IN (SELECT customers_id FROM orders);
# 7. Find all customer names and addresses for those customers with orders having t a pending status?
	SELECT o.customers_name, o.customers_street_address, o.customers_city, o.customers_state, o.customers_country, o.customers_postcode 
	FROM orders o, orders_status os
	WHERE os.orders_status_name = "Pending";
# 8. Find the total shipping cost for orders that have been shipped to the US or United Kingdom?
	SELECT SUM(shipping_cost) AS total_shipping_costs_in_USA_and_UK
	FROM orders o, orders_status os
	WHERE o.orders_status = os.orders_status_id AND os.orders_status_name = "Shipped";
# 9. Find the total for all orders shipped to international destination?
	SELECT COUNT(*) AS total_orders
	FROM orders, orders_status 
	WHERE orders_status_name = "Shipped" AND customers_country <> "United States";
# 10. Write your own query that span multiple tables? (customers_id and names with orders that pay with paypal)
	SELECT c.customers_id, c.customers_firstname
	FROM customers c, (SELECT customers_id, payment_method FROM orders,orders_products) AS od
	WHERE c.customers_id = od.customers_id AND od.payment_method LIKE "PayPal%"
	GROUP BY c.customers_firstname ORDER BY c.customers_id;
# 11. Generate a mailing list for the companies news letter.
	SELECT delivery_name,delivery_street_address,delivery_city,delivery_postcode,delivery_state,delivery_country
	FROM customers c,orders o 
	WHERE customers_newsletter = 1 AND c.customers_id = o.customers_id;

