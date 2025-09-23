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

-- Insert queries for user_account table

INSERT INTO user_account (
    type, nickname, email, password, fullname,
    mobileNumber, bankAccountNumber, bankName, idCardNumber, isActive
) VALUES
-- td-1: BUYER Somchai
('BUYER', 'Somchai', 'itbkk.somchai@ad.sit.kmutt.ac.th', '$argon2id$v=19$m=16384,t=2,p=1$us5dqFXqx2afE0MpFjubfg$aby89PUUvL9DFpdxMt4DlMMjCVQ3RdAoxvB8tWQznfQ', 'Somchai Jaidee',
 NULL, NULL, NULL, NULL, 1),

-- td-2: BUYER Somkiat
('BUYER', 'Somkiat', 'itbkk.somkiat@ad.sit.kmutt.ac.th', '$argon2id$v=19$m=16384,t=2,p=1$us5dqFXqx2afE0MpFjubfg$aby89PUUvL9DFpdxMt4DlMMjCVQ3RdAoxvB8tWQznfQ', 'Somkiat Luckchart',
 NULL, NULL, NULL, NULL, 1),

-- td-3: SELLER Somsuan
('SELLER', 'Somsuan', 'itbkk.somsuan@ad.sit.kmutt.ac.th', '$argon2id$v=19$m=16384,t=2,p=1$us5dqFXqx2afE0MpFjubfg$aby89PUUvL9DFpdxMt4DlMMjCVQ3RdAoxvB8tWQznfQ', 'Somsuan Hundee',
 '083-456-7890', '0371234567', 'Bangkok Bank', '1000111100222', 1),

-- td-4: SELLER Somsuk
('SELLER', 'Somsuk', 'itbkk.somsuk@ad.sit.kmutt.ac.th', '$argon2id$v=19$m=16384,t=2,p=1$us5dqFXqx2afE0MpFjubfg$aby89PUUvL9DFpdxMt4DlMMjCVQ3RdAoxvB8tWQznfQ', 'Somsuk Fundee',
 '084-567-8901', '2371234567', 'Siam Commercial Bank', '1000111100333', 1),

-- td-5: SELLER Somsak
('SELLER', 'Somsak', 'itbkk.somsak@ad.sit.kmutt.ac.th', '$argon2id$v=19$m=16384,t=2,p=1$us5dqFXqx2afE0MpFjubfg$aby89PUUvL9DFpdxMt4DlMMjCVQ3RdAoxvB8tWQznfQ', 'Somsak Saksit',
 '085-678-9012', '0373456789', 'Bangkok Bank', '1000111100444', 1);

INSERT INTO saleItem
(id, sellerId, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES
    (1, 3, 2, 'iPhone 14 Pro Max', 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900, 6.7, 6, 512, 'Space Black'),
    (2, 4, 2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700, 6.1, 6, 256, 'Midnight'),
    (3, 3, 2, 'iPhone 13 Pro', 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 3, 33000, 6.1, 6, 256, 'Sierra Blue'),
    (4, 4, 2, 'iPhone 13', 'Previous gen base model', 10, 23100, 6.1, 4, 128, 'Pink'),
    (5, 3, 2, 'iPhone 12 Pro Max', '2020 flagship model', 4, 29700, 6.7, 6, 256, 'Pacific Blue'),
    (6, 4, 2, 'iPhone 12', '2020 base model', 6, 19800, 6.1, 4, 128, 'Purple'),
    (7, 3, 2, 'iPhone SE 2022', 'Budget-friendly model', 15, 14190, 4.7, 4, 64, 'Starlight'),
    (8, 4, 2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight เครื่องศูนย์ไทย โมเดล TH แบต 100% มีกล่องครบ ประกันศูนย์ถึง พ.ย. 68 ส่งฟรี', 7, 29700, 6.7, 6, 256, 'Blue'),
    (9, 3, 2, 'iPhone 13 mini', 'Compact previous gen', 5, 19800, 5.4, 4, 128, 'Green'),
    (10, 4, 2, 'iPhone 12 mini', 'Compact 2020 model', 4, 16500, 5.4, 4, 64, 'Red'),
    (11, 3, 2, 'iPhone 11 Pro Max', 'Flagship ปี 2019 กล้อง 3 ตัว ถ่ายกลางคืนเยี่ยม', 3, 20900, 6.5, 4, 256, 'Midnight Green'),
    (12, 4, 2, 'iPhone 11', 'สมาร์ทโฟนรุ่นยอดนิยม ประสิทธิภาพดี ราคาจับต้องได้', 6, 15700, 6.1, 4, 128, 'Black'),
    (13, 3, 2, 'iPhone XR', 'จอ Liquid Retina สีสันสดใส กล้องเดี่ยวคุณภาพดี', 5, 12900, 6.1, 3, 64, 'Coral'),
    (14, 4, 2, 'iPhone X', 'รุ่นปฏิวัติการออกแบบ จอเต็มขอบ Face ID', 2, 9900, 5.8, 3, 64, 'Silver'),
    (15, 3, 2, 'iPhone 8 Plus', 'มี Touch ID กล้องคู่ ราคาประหยัด', 8, 7900, 5.5, 3, 64, 'Gold'),
    (16, 4, 2, 'iPhone 8', 'รุ่นเล็กสุดของยุคก่อน Face ID', 4, 6900, 4.7, 2, 64, 'Space Gray'),
    (17, 3, 2, 'iPhone 7 Plus', 'จอกว้าง กล้องคู่รุ่นแรก ราคาย่อมเยา', 5, 5900, 5.5, 3, 32, 'Jet Black'),
    (18, 4, 2, 'iPhone 7', 'กันน้ำได้ มีปุ่ม Home สัมผัส', 3, 4900, 4.7, 2, 32, 'Rose Gold'),
    (19, 3, 2, 'iPhone 6s Plus', 'รุ่นสุดท้ายที่มีช่องหูฟัง 3.5mm', 6, 3900, 5.5, 2, 32, 'Silver'),
    (20, 4, 2, 'iPhone 6s', 'Touch ID เจน 2 รองรับ iOS เวอร์ชันล่าสุดบางรุ่น', 4, 3500, 4.7, 2, 32, 'Space Gray'),
    (21, 3, 2, 'iPhone Gen 21', 'Description for iPhone Gen 21', 2, 16100, 5.1, 3, 64, 'White'),
    (22, 4, 2, 'iPhone Gen 22', 'Description for iPhone Gen 22', 3, 16200, 5.5, 4, 96, 'Blue'),
    (23, 3, 2, 'iPhone Gen 23', 'Description for iPhone Gen 23', 4, 16300, 5.9, 5, 128, 'Gold'),
    (24, 4, 2, 'iPhone Gen 24', 'Description for iPhone Gen 24', 5, 16400, 4.7, 6, 32, 'Red'),
    (25, 3, 2, 'iPhone Gen 25', 'Description for iPhone Gen 25', 1, 16500, 5.1, 2, 64, 'Black'),
    (26, 4, 2, 'iPhone Gen 26', 'Description for iPhone Gen 26', 2, 16600, 5.5, 3, 96, 'White'),
    (27, 3, 2, 'iPhone Gen 27', 'Description for iPhone Gen 27', 3, 16700, 5.9, 4, 128, 'Blue'),
    (28, 4, 2, 'iPhone Gen 28', 'Description for iPhone Gen 28', 4, 16800, 4.7, 5, 32, 'Gold'),
    (29, 3, 2, 'iPhone Gen 29', 'Description for iPhone Gen 29', 5, 16900, 5.1, 6, 64, 'Red'),
    (30, 4, 2, 'iPhone Gen 30', 'Description for iPhone Gen 30', 1, 17000, 5.5, 2, 96, 'Black'),
    (31, 3, 2, 'iPhone Gen 31', 'Description for iPhone Gen 31', 2, 17100, 5.9, 3, 128, 'White'),
    (32, 4, 2, 'iPhone Gen 32', 'Description for iPhone Gen 32', 3, 17200, 4.7, 4, 32, 'Blue'),
    (33, 3, 2, 'iPhone Gen 33', 'Description for iPhone Gen 33', 4, 17300, 5.1, 5, 64, 'Gold'),
    (34, 4, 2, 'iPhone Gen 34', 'Description for iPhone Gen 34', 5, 17400, 5.5, 6, 96, 'Red'),
    (35, 3, 2, 'iPhone Gen 35', 'Description for iPhone Gen 35', 1, 17500, 5.9, 2, 128, 'Black'),
    (36, 4, 2, 'iPhone Gen 36', 'Description for iPhone Gen 36', 2, 17600, 4.7, 3, 32, 'White'),
    (37, 3, 2, 'iPhone Gen 37', 'Description for iPhone Gen 37', 3, 17700, 5.1, 4, 64, 'Blue'),
    (38, 4, 2, 'iPhone Gen 38', 'Description for iPhone Gen 38', 4, 17800, 5.5, 5, 96, 'Gold'),
    (39, 3, 2, 'iPhone Gen 39', 'Description for iPhone Gen 39', 5, 17900, 5.9, 6, 128, 'Red'),
    (40, 4, 2, 'iPhone Gen 40', 'Description for iPhone Gen 40', 1, 18000, 4.7, 2, 32, 'Black'),
    (41, 3, 2, 'iPhone Gen 41', 'Description for iPhone Gen 41', 2, 18100, 5.1, 3, 64, 'White'),
    (42, 4, 2, 'iPhone Gen 42', 'Description for iPhone Gen 42', 3, 18200, 5.5, 4, 96, 'Blue'),
    (43, 3, 2, 'iPhone Gen 43', 'Description for iPhone Gen 43', 4, 18300, 5.9, 5, 128, 'Gold'),
    (44, 4, 2, 'iPhone Gen 44', 'Description for iPhone Gen 44', 5, 18400, 4.7, 6, 32, 'Red'),
    (45, 3, 2, 'iPhone Gen 45', 'Description for iPhone Gen 45', 1, 18500, 5.1, 2, 64, 'Black'),
    (46, 4, 2, 'iPhone Gen 46', 'Description for iPhone Gen 46', 2, 18600, 5.5, 3, 96, 'White'),
    (47, 3, 2, 'iPhone Gen 47', 'Description for iPhone Gen 47', 3, 18700, 5.9, 4, 128, 'Blue'),
    (48, 4, 2, 'iPhone Gen 48', 'Description for iPhone Gen 48', 4, 18800, 4.7, 5, 32, 'Gold'),
    (49, 3, 2, 'iPhone Gen 49', 'Description for iPhone Gen 49', 5, 18900, 5.1, 6, 64, 'Red'),
    (50, 4, 2, 'iPhone Gen 50', 'Description for iPhone Gen 50', 1, 19000, 5.5, 2, 96, 'Black'),
    (51, 3, 2, 'iPhone Gen 51', 'Description for iPhone Gen 51', 2, 19100, 5.9, 3, 128, 'White'),
    (52, 4, 2, 'iPhone Gen 52', 'Description for iPhone Gen 52', 3, 19200, 4.7, 4, 32, 'Blue'),
    (53, 3, 2, 'iPhone Gen 53', 'Description for iPhone Gen 53', 4, 19300, 5.1, 5, 64, 'Gold'),
    (54, 4, 2, 'iPhone Gen 54', 'Description for iPhone Gen 54', 5, 19400, 5.5, 6, 96, 'Red'),
    (55, 3, 2, 'iPhone Gen 55', 'Description for iPhone Gen 55', 1, 19500, 5.9, 2, 128, 'Black'),
    (56, 4, 2, 'iPhone Gen 56', 'Description for iPhone Gen 56', 2, 19600, 4.7, 3, 32, 'White'),
    (57, 3, 2, 'iPhone Gen 57', 'Description for iPhone Gen 57', 3, 19700, 5.1, 4, 64, 'Blue'),
    (58, 4, 2, 'iPhone Gen 58', 'Description for iPhone Gen 58', 4, 19800, 5.5, 5, 96, 'Gold'),
    (59, 3, 2, 'iPhone Gen 59', 'Description for iPhone Gen 59', 5, 19900, 5.9, 6, 128, 'Red'),
    (60, 4, 2, 'iPhone Gen 60', 'Description for iPhone Gen 60', 1, 20000, 4.7, 2, 32, 'Black'),
    (61, 3, 2, 'iPhone Gen 61', 'Description for iPhone Gen 61', 2, 20100, 5.1, 3, 64, 'White'),
    (62, 4, 2, 'iPhone Gen 62', 'Description for iPhone Gen 62', 3, 20200, 5.5, 4, 96, 'Blue'),
    (63, 3, 2, 'iPhone Gen 63', 'Description for iPhone Gen 63', 4, 20300, 5.9, 5, 128, 'Gold'),
    (64, 4, 2, 'iPhone Gen 64', 'Description for iPhone Gen 64', 5, 20400, 4.7, 6, 32, 'Red'),
    (65, 3, 2, 'iPhone Gen 65', 'Description for iPhone Gen 65', 1, 20500, 5.1, 2, 64, 'Black'),
    (66, 4, 2, 'iPhone Gen 66', 'Description for iPhone Gen 66', 2, 20600, 5.5, 3, 96, 'White'),
    (67, 3, 2, 'iPhone Gen 67', 'Description for iPhone Gen 67', 3, 20700, 5.9, 4, 128, 'Blue'),
    (68, 4, 2, 'iPhone Gen 68', 'Description for iPhone Gen 68', 4, 20800, 4.7, 5, 32, 'Gold'),
    (69, 3, 2, 'iPhone Gen 69', 'Description for iPhone Gen 69', 5, 20900, 5.1, 6, 64, 'Red'),
    (70, 4, 2, 'iPhone Gen 70', 'Description for iPhone Gen 70', 1, 21000, 5.5, 2, 96, 'Black'),
    (71, 3, 2, 'iPhone Gen 71', 'Description for iPhone Gen 71', 2, 21100, 5.9, 3, 128, 'White'),
    (72, 4, 2, 'iPhone Gen 72', 'Description for iPhone Gen 72', 3, 21200, 4.7, 4, 32, 'Blue'),
    (73, 3, 2, 'iPhone Gen 73', 'Description for iPhone Gen 73', 4, 21300, 5.1, 5, 64, 'Gold'),
    (74, 4, 2, 'iPhone Gen 74', 'Description for iPhone Gen 74', 5, 21400, 5.5, 6, 96, 'Red'),
    (75, 3, 2, 'iPhone Gen 75', 'Description for iPhone Gen 75', 1, 21500, 5.9, 2, 128, 'Black'),
    (76, 4, 2, 'iPhone Gen 76', 'Description for iPhone Gen 76', 2, 21600, 4.7, 3, 32, 'White'),
    (77, 3, 2, 'iPhone Gen 77', 'Description for iPhone Gen 77', 3, 21700, 5.1, 4, 64, 'Blue'),
    (78, 4, 2, 'iPhone Gen 78', 'Description for iPhone Gen 78', 4, 21800, 5.5, 5, 96, 'Gold'),
    (79, 3, 2, 'iPhone Gen 79', 'Description for iPhone Gen 79', 5, 21900, 5.9, 6, 128, 'Red'),
    (80, 4, 2, 'iPhone Gen 80', 'Description for iPhone Gen 80', 1, 22000, 4.7, 2, 32, 'Black'),
    (81, 3, 2, 'iPhone Gen 81', 'Description for iPhone Gen 81', 2, 22100, 5.1, 3, 64, 'White'),
    (82, 4, 2, 'iPhone Gen 82', 'Description for iPhone Gen 82', 3, 22200, 5.5, 4, 96, 'Blue'),
    (83, 3, 2, 'iPhone Gen 83', 'Description for iPhone Gen 83', 4, 22300, 5.9, 5, 128, 'Gold'),
    (84, 4, 2, 'iPhone Gen 84', 'Description for iPhone Gen 84', 5, 22400, 4.7, 6, 32, 'Red'),
    (85, 3, 2, 'iPhone Gen 85', 'Description for iPhone Gen 85', 1, 22500, 5.1, 2, 64, 'Black');

-- ช่วยให้ query ORDER BY createdOn เร็วขึ้น
CREATE INDEX idx_saleItem_createdOn ON saleItem (createdOn);

-- ช่วยให้ WHERE model LIKE 'xxx%' เร็วขึ้น (เฉพาะ prefix match เท่านั้น)
CREATE INDEX idx_saleItem_model ON saleItem (model);

-- ตาราง brand

-- ช่วยให้ WHERE countryOfOrigin = 'xxx' เร็วขึ้น
CREATE INDEX idx_brand_country ON brand (countryOfOrigin);

CREATE INDEX idx_ref_type ON file_metadata(refType, refId);