-- สร้างฐานข้อมูลพร้อม charset/collation
CREATE DATABASE IF NOT EXISTS ecommerce
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- ใช้งานฐานข้อมูล
USE ecommerce;

-- ตรวจสอบ timezone และเวลาปัจจุบัน
SELECT @@global.time_zone, @@session.time_zone, NOW();

-- สร้างตาราง brand
CREATE TABLE IF NOT EXISTS brand (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(30) NOT NULL UNIQUE CHECK (TRIM(name) <> ''),
    websiteUrl VARCHAR(40) CHECK (websiteUrl IS NULL OR LENGTH(TRIM(websiteUrl)) > 0),
    isActive BOOLEAN NOT NULL,
    countryOfOrigin VARCHAR(80) CHECK (countryOfOrigin IS NULL OR LENGTH(TRIM(countryOfOrigin)) > 0),
    createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Trigger: Trim name ก่อน INSERT
DELIMITER //
CREATE TRIGGER trg_brand_before_insert
    BEFORE INSERT ON brand
    FOR EACH ROW
BEGIN
    SET NEW.name = TRIM(NEW.name);
END;
//

-- Trigger: Trim name ก่อน UPDATE
CREATE TRIGGER trg_brand_before_update
    BEFORE UPDATE ON brand
    FOR EACH ROW
BEGIN
    SET NEW.name = TRIM(NEW.name);
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_brand_trim_country_before_insert
    BEFORE INSERT ON brand
    FOR EACH ROW
BEGIN
    SET NEW.countryOfOrigin = TRIM(NEW.countryOfOrigin);
    IF NEW.countryOfOrigin = '' THEN
        SET NEW.countryOfOrigin = NULL;
END IF;
END;
//

CREATE TRIGGER trg_brand_trim_country_before_update
    BEFORE UPDATE ON brand
    FOR EACH ROW
BEGIN
    SET NEW.countryOfOrigin = TRIM(NEW.countryOfOrigin);
    IF NEW.countryOfOrigin = '' THEN
        SET NEW.countryOfOrigin = NULL;
END IF;
END;
//
DELIMITER ;

-- Trigger: Trim websiteUrl ก่อน INSERT
DELIMITER //

CREATE TRIGGER trg_brand_trim_websiteUrl_before_insert
    BEFORE INSERT ON brand
    FOR EACH ROW
BEGIN
    SET NEW.websiteUrl = TRIM(NEW.websiteUrl);
    IF NEW.websiteUrl = '' THEN
        SET NEW.websiteUrl = NULL;
END IF;
END;
//

CREATE TRIGGER trg_brand_trim_websiteUrl_before_update
    BEFORE UPDATE ON brand
    FOR EACH ROW
BEGIN
    SET NEW.websiteUrl = TRIM(NEW.websiteUrl);
    IF NEW.websiteUrl = '' THEN
        SET NEW.websiteUrl = NULL;
END IF;
END;
//

DELIMITER ;


CREATE TABLE IF NOT EXISTS user_account (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            type ENUM('BUYER', 'SELLER') NOT NULL,
    nickname VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    fullname VARCHAR(40) NOT NULL,

    -- เพิ่มเฉพาะ Seller
    mobileNumber VARCHAR(20),
    bankAccountNumber VARCHAR(30),
    bankName VARCHAR(100),
    idCardNumber VARCHAR(20),
    --     nationalIdFrontImage VARCHAR(255),
--     nationalIdBackImage VARCHAR(255),
    isActive BOOLEAN NOT NULL DEFAULT FALSE,

    createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );


DROP TABLE IF EXISTS saleItem;

-- สร้างตาราง saleItem ใหม่พร้อม sellerId
CREATE TABLE IF NOT EXISTS saleItem (
                                        id INT AUTO_INCREMENT PRIMARY KEY,

                                        brandId INT NOT NULL,
                                        sellerId INT NOT NULL,
                                        model VARCHAR(60) NOT NULL CHECK (TRIM(model) <> ''),
    description TEXT NOT NULL CHECK (TRIM(description) <> ''),
    price INT NOT NULL,
    ramGb INT,
    screenSizeInch DECIMAL(4, 2),
    storageGb INT,
    color VARCHAR(100) CHECK (color IS NULL OR TRIM(color) <> ''),
    quantity INT NOT NULL DEFAULT 1,

    createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (brandId) REFERENCES brand(id) ON DELETE CASCADE,
    FOREIGN KEY (sellerId) REFERENCES user_account(id) ON DELETE CASCADE
    );

-- Trigger: Trim model และ description ก่อน INSERT
DELIMITER //
CREATE TRIGGER trg_saleItem_before_insert
    BEFORE INSERT ON saleItem
    FOR EACH ROW
BEGIN
    SET NEW.model = TRIM(NEW.model);
    SET NEW.description = TRIM(NEW.description);
END;
//

-- Trigger: Trim model และ description ก่อน UPDATE
CREATE TRIGGER trg_saleItem_before_update
    BEFORE UPDATE ON saleItem
    FOR EACH ROW
BEGIN
    SET NEW.model = TRIM(NEW.model);
    SET NEW.description = TRIM(NEW.description);
END;
//
DELIMITER ;

DELIMITER //

CREATE TRIGGER trg_check_seller_before_insert
    BEFORE INSERT ON saleItem
    FOR EACH ROW
BEGIN
    DECLARE sellerType ENUM('BUYER', 'SELLER');

    SELECT type INTO sellerType
    FROM user_account
    WHERE id = NEW.sellerId;

    IF sellerType <> 'SELLER' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'sellerId must be a SELLER';
END IF;
END;
//

CREATE TRIGGER trg_check_seller_before_update
    BEFORE UPDATE ON saleItem
    FOR EACH ROW
BEGIN
    DECLARE sellerType ENUM('BUYER', 'SELLER');

    SELECT type INTO sellerType
    FROM user_account
    WHERE id = NEW.sellerId;

    IF sellerType <> 'SELLER' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'sellerId must be a SELLER';
END IF;
END;
//

DELIMITER ;

CREATE TABLE IF NOT EXISTS file_metadata (
                                             id INT AUTO_INCREMENT PRIMARY KEY,

                                             refType VARCHAR(50) NOT NULL,
    refId INT NOT NULL,
    usageType VARCHAR(100) NOT NULL,

    originalFilename VARCHAR(255) NOT NULL,
    storedFilename VARCHAR(255) NOT NULL UNIQUE,
    mimeType VARCHAR(100) NOT NULL,
    fileSize BIGINT NOT NULL,
    filePath VARCHAR(500) NOT NULL,
    displayOrder INT DEFAULT 0,

    createdOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedOn TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );



INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Samsung', 'South Korea', 'https://www.samsung.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Apple', 'United States', 'https://www.apple.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Xiaomi', 'China', 'https://www.mi.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Huawei', 'China', 'https://www.huawei.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('OnePlus', 'China', 'https://www.oneplus.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Sony', 'Japan', 'https://www.sony.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('LG', 'South Korea', 'https://www.lg.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Nokia', 'Finland', 'https://www.nokia.com', 0);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Motorola', 'United States', 'https://www.motorola.com', 0);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('OPPO', 'China', 'https://www.oppo.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Vivo', 'China', 'https://www.vivo.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('ASUS', 'Taiwan', 'https://www.asus.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Google', 'United States', 'https://store.google.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Realme', 'China', 'https://www.realme.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('BlackBerry', 'Canada', 'https://www.blackberry.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('HTC', 'Taiwan', 'https://www.htc.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('ZTE', 'China', 'https://www.zte.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Lenovo', 'China', 'https://www.lenovo.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Honor', 'China', 'https://www.hihonor.com', 1);
INSERT INTO brand (name, countryOfOrigin, websiteUrl, isActive)
VALUES ('Nothing', 'United Kingdom', 'https://nothing.tech', 1);

-- ช่วยให้ query ORDER BY createdOn เร็วขึ้น
CREATE INDEX idx_saleItem_createdOn ON saleItem (createdOn);

-- ช่วยให้ WHERE model LIKE 'xxx%' เร็วขึ้น (เฉพาะ prefix match เท่านั้น)
CREATE INDEX idx_saleItem_model ON saleItem (model);

-- ตาราง brand

-- ช่วยให้ WHERE countryOfOrigin = 'xxx' เร็วขึ้น
CREATE INDEX idx_brand_country ON brand (countryOfOrigin);

CREATE INDEX idx_ref_type ON file_metadata(refType, refId);