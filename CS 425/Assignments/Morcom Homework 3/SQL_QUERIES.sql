# CHRISTOPHER MORCOM
# SQL ASSIGNMENT 3
# CS 425
# 31 March 2018
	# 1.	Find all products missing a product model number?
		SELECT * FROM products 
		WHERE products_model IS NULL; 
	# 2.	Find all products with over $500 and less than $699?
		SELECT * FROM products 
		WHERE products_price > 500 AND products_price < 699;
	# 3.	Find all products that need to be ordered?
		SELECT * FROM products p
			JOIN orders_products op ON op.orders_id = p.products_id
			JOIN orders o ON o.orders_id = op.orders_id
            JOIN orders_status os ON o.orders_status = os.orders_status_id
		WHERE orders_status_name LIKE "processing"; 
	# 4.	Find all products in Leather Laptop Cases category?
		SELECT * FROM products p
			LEFT JOIN products_to_categories ptc ON p.products_id = ptc.products_id
			LEFT JOIN categories c ON c.categories_id = ptc.categories_id
			LEFT JOIN categories_description cd ON cd.categories_id = c.categories_id 
		WHERE categories_name LIKE "Leather Laptop Cases"
	# 5.	Find all Categories with the keyword “women” in the text?
		SELECT * FROM categories c, categories_description d 
		WHERE c.categories_keywords LIKE '%women%' OR d.categories_name LIKE '%women%';
	# 6.	Find all categories added in 2014?
		SELECT * FROM categories, categories_description
		WHERE date_added LIKE '%2014%';  
	# 7.	Find all categories added before 2014 and has not been modified?
		SELECT * FROM categories 
		WHERE date_added < '2014-1-1' AND last_modified IS NULL OR last_modified = date_added;
	# 8.	Find all products to all main categories (main category has no parent)?
		SELECT * FROM products p
			JOIN products_to_categories ptc ON p.products_id = ptc.products_id
			JOIN categories c ON c.categories_id = ptc.categories_id;
	# 9.	Find all products that are Tote Bags?
		SELECT * FROM products a,(SELECT products_id, categories_name FROM categories c,products_to_categories,categories_description cd WHERE cd.categories_name= "Tote Bags") AS b
		WHERE a.products_id = b.products_id;
	# 10.	List all product models that does not start with word “BAG”
		SELECT products_model FROM products 
		WHERE products_model NOT LIKE "BAG%";