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


-- สร้างตาราง saleItem
CREATE TABLE IF NOT EXISTS saleItem (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        brandId INT NOT NULL,
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
    FOREIGN KEY (brandId) REFERENCES brand(id) ON DELETE CASCADE
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

-- INSERT statements for saleItem table

-- Apple products (id 1-10)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (1, 2, 'iPhone 14 Pro Max', 'ไอโฟนเรือธงรุ่นล่าสุด มาพร้อม Dynamic Island จอใหญ่สุดในตระกูล กล้องระดับโปร', 5, 42900, 6.7, 6, 512, 'Space Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (2, 2, 'iPhone 14', 'ไอโฟนรุ่นใหม่ล่าสุด รองรับ 5G เร็วแรง ถ่ายภาพสวยทุกสภาพแสง', 8, 29700, 6.1, 6, 256, 'Midnight');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (3, 2, 'iPhone 13 Pro', 'ไอโฟนรุ่นโปร จอ ProMotion 120Hz กล้องระดับมืออาชีพ', 3, 33000, 6.1, 6, 256, 'Sierra Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (4, 2, 'iPhone 13', 'Previous gen base model', 10, 23100, 6.1, 4, 128, 'Pink');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (5, 2, 'iPhone 12 Pro Max', '2020 flagship model', 4, 29700, 6.7, 6, 256, 'Pacific Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (6, 2, 'iPhone 12', '2020 base model', 6, 19800, 6.1, 4, 128, 'Purple');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (7, 2, 'iPhone SE 2022', 'Budget-friendly model', 15, 14190, 4.7, 4, 64, 'Starlight');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (8, 2, 'iPhone 14 Plus', 'iPhone 14 Plus 128GB สี Starlight
เครื่องศูนย์ไทย โมเดล TH
แบต 100% มีกล่องครบ ประกันศูนย์ถึง พ.ย. 68
ส่งฟรี', 7, 29700, 6.7, 6, 256, 'Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (9, 2, 'iPhone 13 mini', 'Compact previous gen', 5, 19800, 5.4, 4, 128, 'Green');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (10, 2, 'iPhone 12 mini', 'Compact 2020 model', 4, 16500, 5.4, 4, 64, 'Red');

-- Samsung products (id 16-25)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (16, 1, 'Galaxy S23 Ultra', 'Samsung Galaxy S23 Ultra 512GB สีดำปีศาจ
สภาพนางฟ้า 99% ไร้รอย แถมเคสแท้
แบตอึดสุดๆ รองรับปากกา S-Pen
อุปกรณ์ครบกล่อง ประกันศูนย์เหลือ 6 เดือน
ส่งฟรี', 6, 39600, 6.8, NULL, 512, NULL);

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (17, 1, 'Galaxy S23+', 'Premium flagship model', 8, 33000, 6.6, 8, 256, 'Cream');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (18, 1, 'Galaxy Z Fold4', 'สมาร์ทโฟนพับได้สุดล้ำ จอใหญ่เท่าแท็บเล็ต ทำงานได้หลากหลาย', 3, 59400, 7.6, 12, 256, 'Phantom Green');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (19, 1, 'Galaxy Z Flip4', 'Compact foldable', 5, 33000, 6.7, 8, 128, 'Bora Purple');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (20, 1, 'Galaxy A53 5G', 'มือถือ 5G สเปคดี กล้องเทพ แบตอึด คุ้มค่าน่าใช้', 12, 14850, 6.5, 6, 128, 'Awesome Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (21, 1, 'Galaxy A33 5G', 'Budget 5G phone', 15, 11550, 6.4, 6, 128, 'Awesome White');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (22, 1, 'Galaxy S22', 'เรือธงตัวท็อปจาก Samsung พร้อม S Pen ในตัว กล้อง 200MP ซูมไกลสุด 100x', 7, 26400, 6.1, 8, 128, 'Pink Gold');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (23, 1, 'Galaxy M53', 'Mid-range performance', 9, 14850, 6.7, 6, 128, 'Green');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (24, 1, 'Galaxy A73 5G', 'Premium mid-range', 6, 16500, 6.7, 8, 256, 'Gray');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (25, 1, 'Galaxy S21 FE', 'Fan Edition model', 8, 19800, 6.4, 6, 128, 'Olive');

-- Xiaomi products (id 31-40)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (31, 3, '13 Pro', 'เรือธงสเปคแรงจาก Xiaomi กล้องไลก้า ชาร์จไว 120W', 8, 33000, 6.73, 12, 256, 'Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (32, 3, '13T Pro', 'Xiaomi 13T Pro 12/512GB สี Meadow Green
ชิป Dimensity 9200+ เร็วแรง
กล้อง Leica ถ่ายรูปสวยขั้นเทพ
มีที่ชาร์จ 120W ครบกล่อง
จัดส่งฟรีทั่วประเทศ', 6, 23100, NULL, 12, NULL, 'Alpine Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (33, 3, 'POCO F5', 'มือถือสเปคเทพ เน้นเล่นเกม จอ 120Hz ราคาคุ้มค่า', 10, 13200, 6.67, 8, 256, 'Carbon Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (34, 3, 'Redmi Note 12 Pro', 'กล้องคมชัด 108MP แบตอึด ชาร์จเร็ว ราคาโดนใจ', 15, 9900, 6.67, 8, 128, 'Sky Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (35, 3, '12T Pro', 'Previous flagship', 5, 21450, 6.67, 8, 256, 'Cosmic Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (36, 3, 'POCO X5 Pro', 'Mid-range performer', 12, 9900, 6.67, 8, 128, 'Yellow');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (37, 3, 'Redmi 12C', 'Budget friendly', 20, 5940, 6.71, 4, 64, 'Ocean Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (38, 3, '12 Lite', 'Slim mid-range', 8, 13200, 6.55, 8, 128, 'Lite Pink');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (39, 3, 'POCO M5', 'Budget gaming', 14, 7590, 6.58, 6, 128, 'Power Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (40, 3, 'Redmi Note 11', 'Previous gen mid-range', 10, 8250, 6.43, 6, 128, 'Star Blue');

-- Huawei products (id 46-55)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (46, 4, 'P60 Pro', 'กล้องเรือธงระดับเทพ เซ็นเซอร์ใหญ่พิเศษ ถ่ายภาพกลางคืนสวยเยี่ยม', 5, 36300, 6.67, 12, 256, 'Rococo Pearl');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (47, 4, 'Mate 50 Pro', 'เรือธงตระกูล Mate จอ OLED คมชัด ดีไซน์พรีเมียม', 4, 42900, 6.74, 8, 256, 'Silver Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (48, 4, 'nova 11 Pro', 'สมาร์ทโฟนดีไซน์สวย กล้องหน้าคู่ เน้นเซลฟี่ ชาร์จไว', 8, 19800, 6.78, 8, 256, 'Green');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (49, 4, 'P50 Pro', 'Previous flagship', 6, 29700, 6.6, 8, 256, 'Cocoa Gold');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (50, 4, 'nova 10', 'Stylish mid-range', 10, 16500, 6.67, 8, 128, 'Starry Silver');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (51, 4, 'Mate X3', 'Premium foldable', 3, 66000, 7.85, 12, 512, 'Feather Gold');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (52, 4, 'nova 9', 'Previous mid-range', 12, 13200, 6.57, 8, 128, 'Starry Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (53, 4, 'P50 Pocket', 'Foldable fashion', 4, 46200, 6.9, 8, 256, 'Premium Gold');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (54, 4, 'nova Y70', 'Budget friendly', 15, 9900, 6.75, 4, 128, 'Crystal Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (55, 4, 'Mate 40 Pro', 'Classic flagship', 5, 26400, 6.76, 8, 256, 'Mystic Silver');

-- ASUS products (id 61-70)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (61, 12, 'ROG Phone 7', 'สมาร์ทโฟนเกมมิ่งสเปคโหด จอ 165Hz เสียงสเตอริโอคู่ แบตอึด', 4, 33000, 6.78, 16, 512, 'Phantom Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (62, 12, 'ROG Phone 6D', 'เกมมิ่งโฟนพลังแรง CPU Dimensity ระบายความร้อนเยี่ยม', 5, 29700, 6.78, 16, 256, 'Space Gray');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (63, 12, 'Zenfone 9', 'มือถือกะทัดรัด สเปคแรง กล้องกันสั่น ใช้ง่ายมือเดียว', 8, 23100, 5.9, 8, 128, 'Midnight Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (64, 12, 'ROG Phone 6', 'Previous gaming flagship', 6, 29700, 6.78, 12, 256, 'Storm White');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (65, 12, 'Zenfone 8', 'Previous compact flagship', 7, 19800, 5.9, 8, 128, 'Obsidian Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (66, 12, 'ROG Phone 5s', 'Gaming performance', 5, 26400, 6.78, 12, 256, 'Phantom Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (67, 12, 'Zenfone 8 Flip', 'Flip camera flagship', 4, 26400, 6.67, 8, 256, 'Galactic Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (68, 12, 'ROG Phone 5', 'Classic gaming phone', 6, 23100, 6.78, 12, 256, 'Storm White');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (69, 12, 'Zenfone 7', 'Flip camera classic', 5, 19800, 6.67, 8, 128, 'Aurora Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (70, 12, 'ROG Phone 3', 'Legacy gaming phone', 3, 16500, 6.59, 12, 256, 'Black Glare');

-- OPPO products (id 76-85)
INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (76, 10, 'Find X6 Pro', 'กล้องเทพระดับมืออาชีพ ชิป Snapdragon 8 Gen 2 ชาร์จไว 100W', 5, 33000, 6.82, 12, 256, 'Cosmos Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (77, 10, 'Reno9 Pro+', 'OPPO Reno9 Pro+ 5G 256GB สี Glossy Purple
สวยสะดุดตา ใช้งานลื่นสุดๆ
แบต 4700 mAh รองรับชาร์จไว
ครบกล่อง + ใบเสร็จศูนย์
ส่งฟรี Flash Express', 8, 23100, 6.7, 12, 256, 'Eternal Gold');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (78, 10, 'Find N2 Flip', 'สมาร์ทโฟนพับได้สุดหรู จอนอกใหญ่พิเศษ กล้องคู่คมชัด', 4, 33000, 6.8, 8, 256, 'Astral Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (79, 10, 'Reno8 Pro', 'ดีไซน์บางเบา กล้องคมชัด ชาร์จเร็วสุด ระบบเสียงดี', 10, 19800, 6.7, 8, 256, 'Glazed Green');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (80, 10, 'Find X5 Pro', 'Previous flagship', 6, 29700, 6.7, 12, 256, 'Ceramic White');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (81, 10, 'A78', 'Mid-range performer', 15, 9900, 6.56, 8, 128, 'Glowing Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (82, 10, 'Reno7', 'Style focused mid-range', 12, 13200, 6.43, 8, 128, 'Startrails Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (83, 10, 'Find X5 Lite', 'Previous gen lite', 8, 14850, 6.43, 8, 128, 'Starry Black');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (84, 10, 'A77', 'Budget friendly', 20, 8250, 6.56, 6, 128, 'Ocean Blue');

INSERT INTO saleItem (id, brandId, model, description, quantity, price, screenSizeInch, ramGb, storageGb, color)
VALUES (85, 10, 'Reno6 Pro', 'Classic premium', 7, 16500, 6.55, 12, 256, 'Arctic Blue');