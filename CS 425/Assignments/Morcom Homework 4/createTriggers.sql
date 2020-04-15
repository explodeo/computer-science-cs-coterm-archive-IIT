CREATE TRIGGER product_out_of_stock_update
    AFTER UPDATE ON products
    FOR EACH ROW
	UPDATE products
    SET products_status = 0
    WHERE products_quantity <= 2;
    
CREATE TRIGGER product_out_of_stock_insert
    AFTER INSERT ON products
    FOR EACH ROW
	UPDATE products
    SET products_status = 0
    WHERE products_quantity <= 2;