-- ============================================
-- 中药材种植与销售服务平台 - 数据库表结构
-- Database: herb_platform
-- Author: System
-- Date: 2025-10-29
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS herb_platform
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE herb_platform;

-- 关闭外键检查（避免删除顺序问题，Windows 和 Linux 均适用）
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 一、用户权限模块表
-- ============================================

-- 1.1 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT '/default-avatar.png' COMMENT '头像URL',
    user_type TINYINT DEFAULT 1 COMMENT '用户类型: 1-种植户 2-采购商 3-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除 1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_user_type (user_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1.2 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 1.3 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) UNIQUE NOT NULL COMMENT '权限编码',
    permission_type TINYINT COMMENT '权限类型: 1-菜单 2-按钮 3-接口',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 1.4 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 1.5 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================
-- 二、种植管理模块表
-- ============================================

-- 2.1 地块表
DROP TABLE IF EXISTS herb_field;
CREATE TABLE herb_field (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地块ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    field_name VARCHAR(100) NOT NULL COMMENT '地块名称',
    field_code VARCHAR(50) UNIQUE NOT NULL COMMENT '地块编码',
    location VARCHAR(200) COMMENT '地理位置',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    address VARCHAR(200) COMMENT '详细地址',
    longitude DECIMAL(10,7) COMMENT '经度',
    latitude DECIMAL(10,7) COMMENT '纬度',
    area DECIMAL(10,2) COMMENT '面积(亩)',
    soil_type VARCHAR(50) COMMENT '土壤类型: 沙土/黏土/壤土/其他',
    irrigation_type VARCHAR(50) COMMENT '灌溉方式: 滴灌/喷灌/漫灌',
    description TEXT COMMENT '地块描述',
    images VARCHAR(1000) COMMENT '地块图片(JSON数组)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-休耕 1-在用 2-整改中',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_field_code (field_code),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地块表';

-- 2.2 作物表
DROP TABLE IF EXISTS herb_crop;
CREATE TABLE herb_crop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作物ID',
    user_id BIGINT NOT NULL COMMENT '种植户ID',
    field_id BIGINT NOT NULL COMMENT '地块ID',
    crop_name VARCHAR(100) NOT NULL COMMENT '作物名称',
    crop_variety VARCHAR(100) COMMENT '品种',
    plant_date DATE COMMENT '种植日期',
    expected_harvest_date DATE COMMENT '预计收获日期',
    actual_harvest_date DATE COMMENT '实际收获日期',
    plant_area DECIMAL(10,2) COMMENT '种植面积(亩)',
    expected_yield DECIMAL(10,2) COMMENT '预计产量(kg)',
    actual_yield DECIMAL(10,2) COMMENT '实际产量(kg)',
    growth_stage VARCHAR(50) COMMENT '生长阶段: 播种期/生长期/开花期/成熟期/收获期',
    growth_status VARCHAR(50) COMMENT '生长状况: 良好/一般/较差',
    description TEXT COMMENT '作物描述',
    images VARCHAR(1000) COMMENT '作物图片(JSON数组)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-生长中 2-已收获 3-已销售 4-已废弃',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_field_id (field_id),
    INDEX idx_crop_name (crop_name),
    INDEX idx_status (status),
    INDEX idx_plant_date (plant_date),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (field_id) REFERENCES herb_field(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作物表';

-- 2.3 农事记录表
DROP TABLE IF EXISTS herb_farm_record;
CREATE TABLE herb_farm_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    crop_id BIGINT NOT NULL COMMENT '作物ID',
    user_id BIGINT NOT NULL COMMENT '操作人ID',
    activity_type VARCHAR(50) NOT NULL COMMENT '活动类型: 播种/施肥/浇水/除草/施药/收获/其他',
    activity_date DATE NOT NULL COMMENT '活动日期',
    activity_time TIME COMMENT '活动时间',
    activity_detail TEXT COMMENT '活动详情',
    materials_used VARCHAR(500) COMMENT '使用材料',
    dosage VARCHAR(100) COMMENT '用量',
    cost DECIMAL(10,2) COMMENT '成本(元)',
    weather VARCHAR(50) COMMENT '天气情况',
    temperature VARCHAR(20) COMMENT '温度',
    humidity VARCHAR(20) COMMENT '湿度',
    remark TEXT COMMENT '备注',
    images VARCHAR(1000) COMMENT '图片URLs(JSON数组)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_crop_id (crop_id),
    INDEX idx_user_id (user_id),
    INDEX idx_activity_date (activity_date),
    INDEX idx_activity_type (activity_type),
    FOREIGN KEY (crop_id) REFERENCES herb_crop(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农事记录表';

-- ============================================
-- 三、销售对接模块表
-- ============================================

-- 3.1 供应信息表
DROP TABLE IF EXISTS herb_supply;
CREATE TABLE herb_supply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    crop_id BIGINT COMMENT '关联作物ID',
    herb_name VARCHAR(100) NOT NULL COMMENT '药材名称',
    herb_variety VARCHAR(100) COMMENT '药材品种',
    quality_grade VARCHAR(50) COMMENT '质量等级: 特级/一级/二级/三级',
    supply_quantity DECIMAL(10,2) NOT NULL COMMENT '供应数量(kg)',
    remaining_quantity DECIMAL(10,2) COMMENT '剩余数量(kg)',
    price DECIMAL(10,2) COMMENT '价格(元/kg)',
    price_negotiable TINYINT DEFAULT 1 COMMENT '价格可议: 0-不可议 1-可议',
    production_area VARCHAR(200) COMMENT '产地',
    harvest_date DATE COMMENT '采收日期',
    storage_condition VARCHAR(200) COMMENT '储存条件',
    description TEXT COMMENT '产品描述',
    images VARCHAR(1000) COMMENT '产品图片(JSON数组)',
    certification VARCHAR(500) COMMENT '认证证书(JSON数组)',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_wechat VARCHAR(50) COMMENT '微信号',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-供应中 2-已售罄 3-已下架',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    favorite_count INT DEFAULT 0 COMMENT '收藏次数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_herb_name (herb_name),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_price (price),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (crop_id) REFERENCES herb_crop(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应信息表';

-- 3.2 需求信息表
DROP TABLE IF EXISTS herb_demand;
CREATE TABLE herb_demand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '需求ID',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    herb_name VARCHAR(100) NOT NULL COMMENT '药材名称',
    herb_variety VARCHAR(100) COMMENT '药材品种',
    quality_requirement TEXT COMMENT '质量要求',
    demand_quantity DECIMAL(10,2) NOT NULL COMMENT '需求数量(kg)',
    target_price DECIMAL(10,2) COMMENT '目标价格(元/kg)',
    price_range VARCHAR(50) COMMENT '价格区间',
    demand_date DATE COMMENT '需求日期',
    expiry_date DATE COMMENT '截止日期',
    delivery_address VARCHAR(200) COMMENT '收货地址',
    description TEXT COMMENT '需求描述',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_wechat VARCHAR(50) COMMENT '微信号',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-采购中 2-已完成 3-已取消',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_herb_name (herb_name),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='需求信息表';

-- 3.3 订单表
DROP TABLE IF EXISTS herb_order;
CREATE TABLE herb_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '订单编号',
    supply_id BIGINT NOT NULL COMMENT '供应信息ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    herb_name VARCHAR(100) NOT NULL COMMENT '药材名称',
    herb_variety VARCHAR(100) COMMENT '药材品种',
    quantity DECIMAL(10,2) NOT NULL COMMENT '数量(kg)',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价(元/kg)',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额(元)',
    order_status TINYINT DEFAULT 1 COMMENT '订单状态: 1-待确认 2-已确认 3-配送中 4-已完成 5-已取消 6-已退款',
    payment_status TINYINT DEFAULT 0 COMMENT '支付状态: 0-未支付 1-已支付 2-已退款',
    payment_method VARCHAR(50) COMMENT '支付方式: 线下支付/在线支付',
    delivery_address VARCHAR(500) COMMENT '配送地址',
    delivery_phone VARCHAR(20) COMMENT '收货电话',
    delivery_name VARCHAR(50) COMMENT '收货人',
    logistics_company VARCHAR(100) COMMENT '物流公司',
    logistics_no VARCHAR(100) COMMENT '物流单号',
    confirm_time DATETIME COMMENT '确认时间',
    delivery_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_time DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_order_no (order_no),
    INDEX idx_seller_id (seller_id),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_order_status (order_status),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (supply_id) REFERENCES herb_supply(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (buyer_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 3.4 用户收藏表
DROP TABLE IF EXISTS herb_favorite;
CREATE TABLE herb_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    target_type TINYINT NOT NULL COMMENT '收藏类型: 1-供应信息 2-需求信息',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- ============================================
-- 四、质量溯源模块表
-- ============================================

-- 4.1 溯源信息表
DROP TABLE IF EXISTS herb_trace;
CREATE TABLE herb_trace (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '溯源ID',
    trace_code VARCHAR(100) UNIQUE NOT NULL COMMENT '溯源码',
    crop_id BIGINT NOT NULL COMMENT '作物ID',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    herb_name VARCHAR(100) NOT NULL COMMENT '药材名称',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_area VARCHAR(200) COMMENT '产地',
    plant_date DATE COMMENT '种植日期',
    harvest_date DATE COMMENT '采收日期',
    quality_standard VARCHAR(200) COMMENT '质量标准',
    quality_report VARCHAR(500) COMMENT '质检报告URL',
    certification VARCHAR(500) COMMENT '认证证书(JSON数组)',
    qr_code_url VARCHAR(255) COMMENT '二维码图片URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-有效 0-失效',
    scan_count INT DEFAULT 0 COMMENT '扫码次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_trace_code (trace_code),
    INDEX idx_crop_id (crop_id),
    INDEX idx_user_id (user_id),
    INDEX idx_batch_no (batch_no),
    FOREIGN KEY (crop_id) REFERENCES herb_crop(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源信息表';

-- 4.2 溯源节点表
DROP TABLE IF EXISTS herb_trace_node;
CREATE TABLE herb_trace_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '节点ID',
    trace_id BIGINT NOT NULL COMMENT '溯源ID',
    node_type VARCHAR(50) NOT NULL COMMENT '节点类型: 种植/施肥/浇水/施药/采收/加工/检测/包装/运输/销售',
    node_name VARCHAR(100) NOT NULL COMMENT '节点名称',
    node_time DATETIME NOT NULL COMMENT '节点时间',
    operator VARCHAR(50) COMMENT '操作人',
    operator_id BIGINT COMMENT '操作人ID',
    location VARCHAR(200) COMMENT '地点',
    description TEXT COMMENT '描述',
    images VARCHAR(1000) COMMENT '图片URLs(JSON数组)',
    data_json TEXT COMMENT '扩展数据(JSON)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_trace_id (trace_id),
    INDEX idx_node_type (node_type),
    INDEX idx_node_time (node_time),
    FOREIGN KEY (trace_id) REFERENCES herb_trace(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='溯源节点表';

-- ============================================
-- 五、系统辅助表
-- ============================================

-- 5.1 系统配置表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) UNIQUE NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) COMMENT '配置类型: system/business/display',
    description VARCHAR(200) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_config_key (config_key),
    INDEX idx_config_type (config_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 5.2 操作日志表
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法名',
    params TEXT COMMENT '参数',
    ip VARCHAR(64) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT 'User-Agent',
    result TEXT COMMENT '返回结果',
    execute_time INT COMMENT '执行时长(ms)',
    status TINYINT COMMENT '状态: 0-失败 1-成功',
    error_msg TEXT COMMENT '错误信息',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 5.3 数据字典表
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID',
    dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
    dict_label VARCHAR(100) NOT NULL COMMENT '字典标签',
    dict_value VARCHAR(100) NOT NULL COMMENT '字典值',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dict_type (dict_type),
    UNIQUE KEY uk_type_value (dict_type, dict_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典表';

-- 5.4 消息通知表
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    notice_type TINYINT NOT NULL COMMENT '通知类型: 1-系统通知 2-订单通知 3-评论通知 4-其他',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    link_url VARCHAR(500) COMMENT '链接地址',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 5.5 文件上传记录表
DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    user_id BIGINT COMMENT '上传用户ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_ext VARCHAR(20) COMMENT '文件扩展名',
    module VARCHAR(50) COMMENT '所属模块',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-失效 1-有效',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件上传记录表';

-- ============================================
-- 六、统计分析辅助表
-- ============================================

-- 6.1 每日统计表
DROP TABLE IF EXISTS stat_daily;
CREATE TABLE stat_daily (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    stat_date DATE NOT NULL COMMENT '统计日期',
    user_register_count INT DEFAULT 0 COMMENT '新增用户数',
    supply_publish_count INT DEFAULT 0 COMMENT '新增供应数',
    demand_publish_count INT DEFAULT 0 COMMENT '新增需求数',
    order_count INT DEFAULT 0 COMMENT '订单数',
    order_amount DECIMAL(15,2) DEFAULT 0 COMMENT '交易金额',
    pv INT DEFAULT 0 COMMENT '页面访问量',
    uv INT DEFAULT 0 COMMENT '独立访客数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stat_date (stat_date),
    INDEX idx_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计表';

-- ============================================
-- 七、视图创建
-- ============================================

-- 7.1 用户信息视图(含角色)
CREATE OR REPLACE VIEW v_user_info AS
SELECT
    u.id,
    u.username,
    u.real_name,
    u.phone,
    u.email,
    u.avatar,
    u.user_type,
    u.status,
    u.last_login_time,
    u.create_time,
    GROUP_CONCAT(r.role_name) AS role_names,
    GROUP_CONCAT(r.role_code) AS role_codes
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.id = ur.user_id
LEFT JOIN sys_role r ON ur.role_id = r.id
WHERE u.deleted = 0
GROUP BY u.id;

-- 7.2 作物统计视图
CREATE OR REPLACE VIEW v_crop_stats AS
SELECT
    c.user_id,
    u.real_name AS farmer_name,
    COUNT(c.id) AS total_crops,
    SUM(CASE WHEN c.status = 1 THEN 1 ELSE 0 END) AS growing_crops,
    SUM(CASE WHEN c.status = 2 THEN 1 ELSE 0 END) AS harvested_crops,
    SUM(c.plant_area) AS total_plant_area,
    SUM(c.actual_yield) AS total_yield
FROM herb_crop c
LEFT JOIN sys_user u ON c.user_id = u.id
WHERE c.deleted = 0
GROUP BY c.user_id;

-- 7.3 订单统计视图
CREATE OR REPLACE VIEW v_order_stats AS
SELECT
    DATE(create_time) AS order_date,
    COUNT(id) AS order_count,
    SUM(total_amount) AS total_amount,
    SUM(CASE WHEN order_status = 4 THEN total_amount ELSE 0 END) AS completed_amount,
    AVG(total_amount) AS avg_amount
FROM herb_order
WHERE deleted = 0
GROUP BY DATE(create_time);

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 数据库初始化完成
-- ============================================
