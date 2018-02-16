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
	`id` int(11) not NULL AUTO_INCREMENT,
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
