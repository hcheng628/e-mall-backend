CREATE TABLE `mmall_cart`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`user_id` int(11) NOT NULL,
	`product_id` int(11) DEFAULT NULL,
	`quantity` int(11) DEFAULT NULL,
	`checked` int(11) DEFAULT NULL COMMENT '1checked 0unchecked',
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`),
	KEY `user_id_index` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mmall_category` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`parent_id` int(11) DEFAULT NULL COMMENT 'If 0 when Category Root',
	`name` varchar(50) DEFAULT NULL,
	`status` tinyint(1) DEFAULT '1',
	`sort_order` int(4) DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mmall_order` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`order_no` bigint(20) DEFAULT NULL,
	`user_id` int(11) DEFAULT NULL,
	`shipping_id` Int(11) DEFAULT NULL,
	`payment` decimal(20,2) DEFAULT NULL,
	`payment_type` int(4) DEFAULT NULL,
	`postage` int(10) DEFAULT NULL,
	`status` int(10) DEFAULT NULL,
	`payment_time` datetime DEFAULT NULL,
	`send_time` datetime DEFAULT NULL,
	`end_time` datetime DEFAULT NULL,
	`close_time` datetime DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `order_no_index` (`order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mmall_order_item` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`user_id` int(11) DEFAULT NULL,
	`order_no` bigint(20) DEFAULT NULL,
	`product_id` int(11) DEFAULT NULL,
	`product_name` VARCHAR(100) DEFAULT NULL,
	`product_image` VARCHAR(500) DEFAULT NULL,
	`current_unit_price` decimal(20,2) DEFAULT NULL,
	`quantity` int(10) DEFAULT NULL,
	`total_price` decimal(20,2) DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`),
	KEY `order_no_index` (`order_no`) USING BTREE,
	KEY `order_no_user_id_index` (`user_id`, `order_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mmall_pay_info` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`user_id` int(11) DEFAULT NULL,
	`order_no` bigint(20) DEFAULT NULL,
	`pay_platform` int(10) DEFAULT NULL,
	`platform_number` VARCHAR(200) DEFAULT NULL,
	`platform_status` VARCHAR(20) DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `mmall_product` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`category_id` int(11) NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	`subtitle` VARCHAR(200) DEFAULT NULL,
	`main_image` VARCHAR(500) DEFAULT NULL,
	`sub_images` text,
	`detail` text,
	`price` decimal(20,2) NOT NULL,
	`stock` int(11) NOT NULL,
	`status` int(6) DEFAULT '1',
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `mmall_shipping` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`user_id` int(11) DEFAULT NULL,
	`receiver_name` VARCHAR(20) DEFAULT NULL,
	`receiver_phone` VARCHAR(20) DEFAULT NULL,
	`receiver_mobile` VARCHAR(20) DEFAULT NULL,
	`receiver_province` VARCHAR(20) DEFAULT NULL,
	`receiver_city` VARCHAR(20) DEFAULT NULL,
	`receiver_district` VARCHAR(20) DEFAULT NULL,
	`receiver_address` VARCHAR(200) DEFAULT NULL,
	`receiver_zip` VARCHAR(6) DEFAULT NULL,
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE  `mmall_user` (
	`id` int(11) NULL AUTO_INCREMENT,
	`username` VARCHAR(50) NOT NULL,
	`password` VARCHAR(50) NOT NULL,
	`email` VARCHAR(50) DEFAULT NULL,
	`phone` VARCHAR(20) DEFAULT NULL,
	`question` VARCHAR(100) DEFAULT NULL,
	`answer` VARCHAR(100) DEFAULT NULL,
	`role` int(4) NOT NULL COMMENT '0=Admin 1=User',
	`create_time` datetime DEFAULT NULL,
	`update_time` datetime DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `user_name_unique` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

