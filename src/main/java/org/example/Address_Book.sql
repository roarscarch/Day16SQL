-- UC1 create DB;
CREATE DATABASE IF NOT EXISTS Address_Book;
USE Address_Book;

-- UC2 create table
CREATE TABLE IF NOT EXISTS AddressBook (
    contact_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip VARCHAR(10),
    phone_number VARCHAR(15),
    email VARCHAR(255) UNIQUE
);

-- UC3 Insert Contacts

INSERT INTO AddressBook (first_name, last_name, address, city, state, zip, phone_number, email)
VALUES ('Anurag', 'Dubey', 'Hatia', 'Ranchi', 'Jharkhand', '12345', '9876543210', 'anurag@example.com');

INSERT INTO AddressBook (first_name, last_name, address, city, state, zip, phone_number, email)
VALUES ('Rohan', 'Semwal', 'Another Address', 'City98', 'State69', '54321', '1234567890', 'rohan@example.com');

INSERT INTO AddressBook (first_name, last_name, address, city, state, zip, phone_number, email)
VALUES ('Himanshu', 'Kohli', 'patliputra', 'City1', 'State1', '67890', '9876123450', 'himanshu@example.com');

INSERT INTO AddressBook (first_name, last_name, address, city, state, zip, phone_number, email)
VALUES ('John', 'Doe', '123 Main St', 'takshila', 'Fictional State', '56789', '555-1234', 'john@example.com');

INSERT INTO AddressBook (first_name, last_name, address, city, state, zip, phone_number, email)
VALUES ('Jane', 'Smith', '456 Oak Ave', 'Sometown', 'Imaginary State', '98765', '555-5678', 'jane@example.com');

-- UC4 EDIT contacts -- Doubt
UPDATE AddressBook
SET address = 'New Address', phone_number = '9876543211'
WHERE first_name = 'Anurag' AND last_name = 'Dubey';

-- UC5 Delete contacts using their name. Isme bhi error
DELETE FROM AddressBook
WHERE first_name = 'Himanshu' AND last_name = 'Kohli';

-- UC6 retrieval using city
SELECT * FROM AddressBook
WHERE city = 'Ranchi';

--UC7 group by city and state
SELECT city, state, COUNT(*) as city_state_size
FROM AddressBook
GROUP BY city, state;

--UC8 sort by first and last name
SELECT *
FROM AddressBook
WHERE city = 'Ranchi'
ORDER BY first_name, last_name;

--UC9 Alter table with name and type
ALTER TABLE AddressBook
ADD COLUMN name VARCHAR(255),
ADD COLUMN type VARCHAR(50);

-- Update entries with name and type
UPDATE AddressBook
SET name = 'Family', type = 'Relative'
WHERE first_name = 'Anurag' AND last_name = 'Dubey';

UPDATE AddressBook
SET name = 'Friend', type = 'Close Friend'
WHERE first_name = 'Rohan' AND last_name = 'Semwal';

-- UC10 count by type
SELECT type, COUNT(*) as type_count
FROM AddressBook
GROUP BY type;

-- UC11 add person to both friend and family  error
UPDATE AddressBook
SET type = 'Friend, Family'
WHERE first_name = 'Anurag';

-- UC 12 Normalisation

CREATE TABLE IF NOT EXISTS RelationshipTypes (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(255) NOT NULL
);
-- violates 1nf because of multivalued attributes
INSERT INTO RelationshipTypes (type_name) VALUES ('Friends');
INSERT INTO RelationshipTypes (type_name) VALUES ('Family');
INSERT INTO RelationshipTypes (type_name) VALUES ('Colleagues');
-- Adress is not normalised because state , city is dependent on it
CREATE TABLE IF NOT EXISTS Addresses (
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip VARCHAR(10)
);

-- for people different table
CREATE TABLE IF NOT EXISTS Persons (
    person_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15),
    email VARCHAR(255) UNIQUE,
    type_id INT,
    address_id INT,
    FOREIGN KEY (type_id) REFERENCES RelationshipTypes(type_id),
    FOREIGN KEY (address_id) REFERENCES Addresses(address_id)
);

-- since we distributed the address book table . we can drop it
DROP TABLE IF EXISTS AddressBook;









