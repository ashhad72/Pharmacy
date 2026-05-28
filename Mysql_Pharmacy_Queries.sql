	create database Pharmacy;
	use Pharmacy;

	#DEFINED TABLES HERE
	CREATE TABLE Users (
		user_id INT AUTO_INCREMENT PRIMARY KEY,
		full_name VARCHAR(100) NOT NULL,
		email VARCHAR(100) UNIQUE,
		password VARCHAR(255) NOT NULL,
		role VARCHAR(50) NOT NULL,
		created_at DATETIME DEFAULT CURRENT_TIMESTAMP
	);	

	CREATE TABLE Suppliers (
		supplier_id INT AUTO_INCREMENT PRIMARY KEY,
		supplier_name VARCHAR(100) NOT NULL,
		contact_number VARCHAR(20),
		email VARCHAR(100),
		address VARCHAR(255)
	);

	CREATE TABLE Categories (
		category_id INT AUTO_INCREMENT PRIMARY KEY,
		category_name VARCHAR(100) UNIQUE
	);

	CREATE TABLE Medicines (
		medicine_id INT AUTO_INCREMENT PRIMARY KEY,
		medicine_name VARCHAR(100) NOT NULL,
		category_id INT,
		supplier_id INT,
		batch_number VARCHAR(50),
		price DECIMAL(10,2) NOT NULL,
		quantity_in_stock INT NOT NULL,
		manufacture_date DATE,
		expiry_date DATE,
		description VARCHAR(255),

		FOREIGN KEY (category_id)
		REFERENCES Categories(category_id),

		FOREIGN KEY (supplier_id)
		REFERENCES Suppliers(supplier_id)
	);

	CREATE TABLE Customers (
		customer_id INT AUTO_INCREMENT PRIMARY KEY,
		customer_name VARCHAR(100) NOT NULL,
		phone_number VARCHAR(20)
	);

	CREATE TABLE Sales (
		sale_id INT AUTO_INCREMENT PRIMARY KEY,
		customer_id INT,
		user_id INT,
		total_amount DECIMAL(10,2) NOT NULL,
		sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,

		FOREIGN KEY (customer_id)
		REFERENCES Customers(customer_id),

		FOREIGN KEY (user_id)
		REFERENCES Users(user_id)
	);

	CREATE TABLE Sale_Items (
		sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
		sale_id INT,
		medicine_id INT,
		quantity INT NOT NULL,
		unit_price DECIMAL(10,2) NOT NULL,
		subtotal DECIMAL(10,2) NOT NULL,

		FOREIGN KEY (sale_id)
		REFERENCES Sales(sale_id),

		FOREIGN KEY (medicine_id)
		REFERENCES Medicines(medicine_id)
	);

	CREATE TABLE Purchases (
		purchase_id INT AUTO_INCREMENT PRIMARY KEY,
		supplier_id INT,
		user_id INT,
		total_amount DECIMAL(10,2),
		purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,

		FOREIGN KEY (supplier_id)
		REFERENCES Suppliers(supplier_id),

		FOREIGN KEY (user_id)
		REFERENCES Users(user_id)
	);

	CREATE TABLE Purchase_Items (
		purchase_item_id INT AUTO_INCREMENT PRIMARY KEY,
		purchase_id INT,
		medicine_id INT,
		quantity INT NOT NULL,
		cost_price DECIMAL(10,2) NOT NULL,
		subtotal DECIMAL(10,2) NOT NULL,

		FOREIGN KEY (purchase_id)
		REFERENCES Purchases(purchase_id),

		FOREIGN KEY (medicine_id)
		REFERENCES Medicines(medicine_id)
	);


	#DUMMY DATA AND DATABASE TESTING HERE
	show tables;
	INSERT INTO Categories (category_name)
	VALUES
	('Tablet'),
	('Syrup'),
	('Injection');

	INSERT INTO Suppliers
	(supplier_name, contact_number, email, address)
	VALUES
	('ABC Pharma', '03001234567', 'abc@gmail.com', 'Karachi'),
	('HealthMed', '03111234567', 'health@gmail.com', 'Lahore');

	INSERT INTO Medicines
	(medicine_name, category_id, supplier_id, batch_number,
	price, quantity_in_stock, manufacture_date, expiry_date, description)

	VALUES
	('Panadol', 1, 1, 'B101', 50.00, 100,
	'2025-01-01', '2027-01-01', 'Fever medicine'),

	('Brufen Syrup', 2, 2, 'B202', 120.00, 50,
	'2025-02-01', '2026-12-01', 'Pain relief syrup');


	#VIEWS HERE
    drop table Sales_Report_View;
	CREATE VIEW Sales_Report_View AS
	SELECT
		s.sale_id,
		c.customer_name,
		s.total_amount,
		s.sale_date
	FROM Sales s
	JOIN Customers c
	ON s.customer_id = c.customer_id;

	CREATE VIEW Medicine_Stock_View AS
	SELECT
		medicine_name,
		quantity_in_stock,
		expiry_date
	FROM Medicines;


	#PROCEDURES HERE
	DELIMITER $$
	CREATE PROCEDURE AddMedicine(
		IN p_medicine_name VARCHAR(100),
		IN p_category_id INT,
		IN p_supplier_id INT,
		IN p_price DECIMAL(10,2),
		IN p_quantity INT,
		IN p_expiry_date DATE
	)
	BEGIN
		INSERT INTO Medicines
		(
			medicine_name,
			category_id,
			supplier_id,
			price,
			quantity_in_stock,
			expiry_date
		)
		VALUES
		(
			p_medicine_name,
			p_category_id,
			p_supplier_id,
			p_price,
			p_quantity,
			p_expiry_date
		);
	END $$
	DELIMITER ;
	#procedure calling for testing within database
	CALL AddMedicine( 
		'Panadol',
		1,
		1,
		50.00,
		100,
		'2027-01-01'
	);


	DELIMITER $$
	CREATE PROCEDURE SearchMedicine(
		IN p_keyword VARCHAR(100)
	)
	BEGIN
		SELECT *
		FROM Medicines
		WHERE medicine_name
		LIKE CONCAT('%', p_keyword, '%');
	END $$
	DELIMITER ;
	#procedure calling for testing within database
	CALL SearchMedicine('Pan');


	DELIMITER $$
	CREATE PROCEDURE LowStockMedicines()
	BEGIN
		SELECT *
		FROM Medicines
		WHERE quantity_in_stock < 20;
	END $$
	DELIMITER ;
	#procedure calling for testing within database
	CALL LowStockMedicines();

	DELIMITER $$
	CREATE PROCEDURE ExpiredMedicines()
	BEGIN
		SELECT *
		FROM Medicines
		WHERE expiry_date < CURDATE();
	END $$
	DELIMITER ;
	#procedure calling for testing within database
	CALL ExpiredMedicines();


	#FUNTIONS HERE
	DELIMITER $$
	CREATE FUNCTION GetTotalStockValue()
	RETURNS DECIMAL(10,2)
	DETERMINISTIC
	BEGIN
		DECLARE total DECIMAL(10,2);
		SELECT
		SUM(price * quantity_in_stock)
		INTO total
		FROM Medicines;
		RETURN total;
	END $$
	DELIMITER ;
	SELECT GetTotalStockValue();

	DELIMITER $$
	CREATE FUNCTION GetMedicineCount()
	RETURNS INT
	DETERMINISTIC
	BEGIN
		DECLARE totalMedicines INT;
		SELECT COUNT(*)
		INTO totalMedicines
		FROM Medicines;
		RETURN totalMedicines;
	END $$
	DELIMITER ;
	SELECT GetMedicineCount();

	DELIMITER $$
	CREATE FUNCTION GetLowStockCount()
	RETURNS INT
	DETERMINISTIC
	BEGIN
		DECLARE lowStock INT;
		SELECT COUNT(*)
		INTO lowStock
		FROM Medicines
		WHERE quantity_in_stock < 20;
		RETURN lowStock;
	END $$
	DELIMITER ;
	SELECT GetLowStockCount();	

	DELIMITER $$
	CREATE FUNCTION GetExpiredMedicineCount()
	RETURNS INT
	DETERMINISTIC
	BEGIN
		DECLARE expiredCount INT;
		SELECT COUNT(*)
		INTO expiredCount
		FROM Medicines
		WHERE expiry_date < CURDATE();
		RETURN expiredCount;
	END $$
	DELIMITER ;
	SELECT GetExpiredMedicineCount();

	#TRIGGERS HERE
	DELIMITER $$
	CREATE TRIGGER ReduceStockAfterSale
	AFTER INSERT
	ON Sale_Items
	FOR EACH ROW
	BEGIN
		UPDATE Medicines
		SET quantity_in_stock =
			quantity_in_stock - NEW.quantity
		WHERE medicine_id = NEW.medicine_id;
	END $$
	DELIMITER ;

	DELIMITER $$
	CREATE TRIGGER IncreaseStockAfterPurchase
	AFTER INSERT
	ON Purchase_Items
	FOR EACH ROW
	BEGIN
		UPDATE Medicines
		SET quantity_in_stock =
			quantity_in_stock + NEW.quantity
		WHERE medicine_id = NEW.medicine_id;
	END $$
	DELIMITER ;

	DELIMITER $$
	CREATE TRIGGER PreventNegativeStock
	BEFORE INSERT
	ON Sale_Items
	FOR EACH ROW
	BEGIN
		DECLARE current_stock INT;
		SELECT quantity_in_stock
		INTO current_stock
		FROM Medicines
		WHERE medicine_id = NEW.medicine_id;
		IF current_stock < NEW.quantity THEN
			SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT =
			'Insufficient stock available';
		END IF;
	END $$
	DELIMITER ;
    
    #HARDCODED SYSTEM USER
    INSERT INTO Users
(full_name, email, password, role)

VALUES
(
 'Admin',
 'admin@gmail.com',
 'admin123',
 'ADMIN'
);

#MKAE CHANGES HERE AND FIRE THE QUERIES TO UPDATE USERNAME
UPDATE Users
SET password = 'newpassword123'
WHERE email = 'admin@gmail.com';

UPDATE Users
SET email = 'newadmin@gmail.com'
WHERE user_id = 1;

UPDATE Users
SET full_name = 'Main Pharmacy Admin'
WHERE user_id = 1;

ALTER TABLE Sales
DROP FOREIGN KEY sales_ibfk_2;

ALTER TABLE Sales
DROP COLUMN user_id;
