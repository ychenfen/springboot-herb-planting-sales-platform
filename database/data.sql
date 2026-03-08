-- ============================================
-- 中药材种植与销售服务平台 - 初始化数据
-- Database: herb_platform
-- Author: System
-- Date: 2025-10-29
-- ============================================

USE herb_platform;

-- ============================================
-- 一、权限系统初始化数据
-- ============================================

-- 1.1 初始化角色数据
INSERT INTO sys_role (id, role_name, role_code, description, status) VALUES
(1, '系统管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限', 1),
(2, '种植户', 'ROLE_FARMER', '中药材种植户', 1),
(3, '采购商', 'ROLE_BUYER', '药材采购商', 1);

-- 1.2 初始化权限数据 (菜单权限)
INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order, status) VALUES
-- 一级菜单
(1, '工作台', 'dashboard', 1, 0, '/dashboard', 'Dashboard', 'dashboard', 1, 1),
(2, '种植管理', 'planting', 1, 0, '/planting', 'Layout', 'plant', 2, 1),
(3, '销售对接', 'trading', 1, 0, '/trading', 'Layout', 'shopping', 3, 1),
(4, '质量溯源', 'trace', 1, 0, '/trace', 'Layout', 'qrcode', 4, 1),
(5, '数据分析', 'analysis', 1, 0, '/analysis', 'Layout', 'chart', 5, 1),
(6, '系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 6, 1),

-- 种植管理子菜单
(11, '地块管理', 'planting:field', 1, 2, '/planting/field', 'Planting/Field', 'field', 1, 1),
(12, '作物管理', 'planting:crop', 1, 2, '/planting/crop', 'Planting/Crop', 'crop', 2, 1),
(13, '农事记录', 'planting:record', 1, 2, '/planting/record', 'Planting/Record', 'record', 3, 1),

-- 销售对接子菜单
(21, '供应大厅', 'trading:supply_market', 1, 3, '/trading/supply-market', 'Trading/SupplyMarket', 'supply', 1, 1),
(22, '需求大厅', 'trading:demand_market', 1, 3, '/trading/demand-market', 'Trading/DemandMarket', 'demand', 2, 1),
(23, '我的供应', 'trading:my_supply', 1, 3, '/trading/my-supply', 'Trading/MySupply', 'my-supply', 3, 1),
(24, '我的需求', 'trading:my_demand', 1, 3, '/trading/my-demand', 'Trading/MyDemand', 'my-demand', 4, 1),
(25, '订单管理', 'trading:order', 1, 3, '/trading/order', 'Trading/Order', 'order', 5, 1),
(26, '我的收藏', 'trading:favorite', 1, 3, '/trading/favorite', 'Trading/Favorite', 'favorite', 6, 1),

-- 质量溯源子菜单
(31, '溯源管理', 'trace:manage', 1, 4, '/trace/manage', 'Trace/Manage', 'trace-manage', 1, 1),
(32, '溯源查询', 'trace:query', 1, 4, '/trace/query', 'Trace/Query', 'trace-query', 2, 1),

-- 数据分析子菜单
(41, '产量分析', 'analysis:yield', 1, 5, '/analysis/yield', 'Analysis/Yield', 'yield', 1, 1),
(42, '销售分析', 'analysis:sales', 1, 5, '/analysis/sales', 'Analysis/Sales', 'sales', 2, 1),

-- 系统管理子菜单
(51, '用户管理', 'system:user', 1, 6, '/system/user', 'System/User', 'user', 1, 1),
(52, '角色管理', 'system:role', 1, 6, '/system/role', 'System/Role', 'role', 2, 1),
(53, '权限管理', 'system:permission', 1, 6, '/system/permission', 'System/Permission', 'permission', 3, 1),
(54, '系统日志', 'system:log', 1, 6, '/system/log', 'System/Log', 'log', 4, 1),
(55, '系统配置', 'system:config', 1, 6, '/system/config', 'System/Config', 'config', 5, 1),
(56, '通知管理', 'system:notice', 1, 6, '/system/notice', 'System/Notice', 'notice', 6, 1),

-- 按钮权限 - 地块管理
(111, '新增地块', 'planting:field:add', 2, 11, NULL, NULL, NULL, 1, 1),
(112, '编辑地块', 'planting:field:edit', 2, 11, NULL, NULL, NULL, 2, 1),
(113, '删除地块', 'planting:field:delete', 2, 11, NULL, NULL, NULL, 3, 1),
(114, '查看地块', 'planting:field:view', 2, 11, NULL, NULL, NULL, 4, 1),

-- 按钮权限 - 作物管理
(121, '新增作物', 'planting:crop:add', 2, 12, NULL, NULL, NULL, 1, 1),
(122, '编辑作物', 'planting:crop:edit', 2, 12, NULL, NULL, NULL, 2, 1),
(123, '删除作物', 'planting:crop:delete', 2, 12, NULL, NULL, NULL, 3, 1),
(124, '查看作物', 'planting:crop:view', 2, 12, NULL, NULL, NULL, 4, 1),

-- 按钮权限 - 农事记录
(131, '新增记录', 'planting:record:add', 2, 13, NULL, NULL, NULL, 1, 1),
(132, '编辑记录', 'planting:record:edit', 2, 13, NULL, NULL, NULL, 2, 1),
(133, '删除记录', 'planting:record:delete', 2, 13, NULL, NULL, NULL, 3, 1),

-- 按钮权限 - 供应管理
(231, '发布供应', 'trading:supply:add', 2, 23, NULL, NULL, NULL, 1, 1),
(232, '编辑供应', 'trading:supply:edit', 2, 23, NULL, NULL, NULL, 2, 1),
(233, '下架供应', 'trading:supply:offline', 2, 23, NULL, NULL, NULL, 3, 1),

-- 按钮权限 - 需求管理
(241, '发布需求', 'trading:demand:add', 2, 24, NULL, NULL, NULL, 1, 1),
(242, '编辑需求', 'trading:demand:edit', 2, 24, NULL, NULL, NULL, 2, 1),
(243, '取消需求', 'trading:demand:cancel', 2, 24, NULL, NULL, NULL, 3, 1),

-- 按钮权限 - 订单管理
(251, '创建订单', 'trading:order:create', 2, 25, NULL, NULL, NULL, 1, 1),
(252, '确认订单', 'trading:order:confirm', 2, 25, NULL, NULL, NULL, 2, 1),
(253, '取消订单', 'trading:order:cancel', 2, 25, NULL, NULL, NULL, 3, 1),

-- 按钮权限 - 溯源管理
(311, '创建溯源', 'trace:create', 2, 31, NULL, NULL, NULL, 1, 1),
(312, '编辑溯源', 'trace:edit', 2, 31, NULL, NULL, NULL, 2, 1),
(313, '生成二维码', 'trace:qrcode', 2, 31, NULL, NULL, NULL, 3, 1),

-- 按钮权限 - 用户管理
(511, '新增用户', 'system:user:add', 2, 51, NULL, NULL, NULL, 1, 1),
(512, '编辑用户', 'system:user:edit', 2, 51, NULL, NULL, NULL, 2, 1),
(513, '删除用户', 'system:user:delete', 2, 51, NULL, NULL, NULL, 3, 1),
(514, '分配角色', 'system:user:role', 2, 51, NULL, NULL, NULL, 4, 1);

-- 1.3 角色权限关联 (管理员拥有所有权限)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE deleted = 0;

-- 1.4 角色权限关联 (种植户权限)
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
-- 工作台
(2, 1),
-- 种植管理及其子菜单和按钮
(2, 2), (2, 11), (2, 12), (2, 13),
(2, 111), (2, 112), (2, 113), (2, 114),
(2, 121), (2, 122), (2, 123), (2, 124),
(2, 131), (2, 132), (2, 133),
-- 销售对接部分权限
(2, 3), (2, 21), (2, 23), (2, 25), (2, 26),
(2, 231), (2, 232), (2, 233),
(2, 251), (2, 252), (2, 253),
-- 质量溯源
(2, 4), (2, 31), (2, 32),
(2, 311), (2, 312), (2, 313),
-- 数据分析
(2, 5), (2, 41), (2, 42);

-- 1.5 角色权限关联 (采购商权限)
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
-- 工作台
(3, 1),
-- 销售对接权限
(3, 3), (3, 21), (3, 22), (3, 24), (3, 25), (3, 26),
(3, 241), (3, 242), (3, 243),
(3, 251), (3, 252), (3, 253),
-- 质量溯源查询
(3, 4), (3, 32);

-- 1.6 初始化管理员用户 (密码: admin123，使用BCrypt加密)
INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@herb.com', 3, 1),
(2, 'farmer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13800138001', 'farmer001@herb.com', 1, 1),
(3, 'buyer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13800138002', 'buyer001@herb.com', 2, 1);

-- 1.7 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- 管理员
(2, 2),  -- 种植户
(3, 3);  -- 采购商

-- ============================================
-- 二、数据字典初始化
-- ============================================

INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort_order, status) VALUES
-- 用户类型
('user_type', '种植户', '1', 1, 1),
('user_type', '采购商', '2', 2, 1),
('user_type', '管理员', '3', 3, 1),

-- 用户状态
('user_status', '禁用', '0', 1, 1),
('user_status', '正常', '1', 2, 1),

-- 土壤类型
('soil_type', '沙土', 'sandy', 1, 1),
('soil_type', '黏土', 'clay', 2, 1),
('soil_type', '壤土', 'loam', 3, 1),
('soil_type', '砂质壤土', 'sandy_loam', 4, 1),
('soil_type', '黏质壤土', 'clay_loam', 5, 1),

-- 灌溉方式
('irrigation_type', '滴灌', 'drip', 1, 1),
('irrigation_type', '喷灌', 'sprinkler', 2, 1),
('irrigation_type', '漫灌', 'flood', 3, 1),
('irrigation_type', '微灌', 'micro', 4, 1),

-- 地块状态
('field_status', '休耕', '0', 1, 1),
('field_status', '在用', '1', 2, 1),
('field_status', '整改中', '2', 3, 1),

-- 生长阶段
('growth_stage', '播种期', 'seeding', 1, 1),
('growth_stage', '发芽期', 'germination', 2, 1),
('growth_stage', '生长期', 'growing', 3, 1),
('growth_stage', '开花期', 'flowering', 4, 1),
('growth_stage', '成熟期', 'maturity', 5, 1),
('growth_stage', '收获期', 'harvest', 6, 1),

-- 生长状况
('growth_status', '良好', 'good', 1, 1),
('growth_status', '一般', 'normal', 2, 1),
('growth_status', '较差', 'poor', 3, 1),

-- 作物状态
('crop_status', '生长中', '1', 1, 1),
('crop_status', '已收获', '2', 2, 1),
('crop_status', '已销售', '3', 3, 1),
('crop_status', '已废弃', '4', 4, 1),

-- 农事活动类型
('activity_type', '播种', 'seeding', 1, 1),
('activity_type', '施肥', 'fertilizing', 2, 1),
('activity_type', '浇水', 'watering', 3, 1),
('activity_type', '除草', 'weeding', 4, 1),
('activity_type', '施药', 'spraying', 5, 1),
('activity_type', '收获', 'harvesting', 6, 1),
('activity_type', '整地', 'tillage', 7, 1),
('activity_type', '修剪', 'pruning', 8, 1),
('activity_type', '其他', 'other', 9, 1),

-- 质量等级
('quality_grade', '特级', 'premium', 1, 1),
('quality_grade', '一级', 'first', 2, 1),
('quality_grade', '二级', 'second', 3, 1),
('quality_grade', '三级', 'third', 4, 1),

-- 供应状态
('supply_status', '供应中', '1', 1, 1),
('supply_status', '已售罄', '2', 2, 1),
('supply_status', '已下架', '3', 3, 1),

-- 需求状态
('demand_status', '采购中', '1', 1, 1),
('demand_status', '已完成', '2', 2, 1),
('demand_status', '已取消', '3', 3, 1),

-- 订单状态
('order_status', '待确认', '1', 1, 1),
('order_status', '已确认', '2', 2, 1),
('order_status', '配送中', '3', 3, 1),
('order_status', '已完成', '4', 4, 1),
('order_status', '已取消', '5', 5, 1),
('order_status', '已退款', '6', 6, 1),

-- 支付状态
('payment_status', '未支付', '0', 1, 1),
('payment_status', '已支付', '1', 2, 1),
('payment_status', '已退款', '2', 3, 1),

-- 溯源节点类型
('trace_node_type', '种植', 'planting', 1, 1),
('trace_node_type', '施肥', 'fertilizing', 2, 1),
('trace_node_type', '浇水', 'watering', 3, 1),
('trace_node_type', '施药', 'spraying', 4, 1),
('trace_node_type', '采收', 'harvesting', 5, 1),
('trace_node_type', '加工', 'processing', 6, 1),
('trace_node_type', '检测', 'testing', 7, 1),
('trace_node_type', '包装', 'packaging', 8, 1),
('trace_node_type', '运输', 'transportation', 9, 1),
('trace_node_type', '销售', 'sales', 10, 1),

-- 通知类型
('notice_type', '系统通知', '1', 1, 1),
('notice_type', '订单通知', '2', 2, 1),
('notice_type', '评论通知', '3', 3, 1),
('notice_type', '其他通知', '4', 4, 1);

-- ============================================
-- 三、系统配置初始化
-- ============================================

INSERT INTO sys_config (config_key, config_value, config_type, description) VALUES
('system.name', '中药材种植与销售服务平台', 'system', '系统名称'),
('system.version', '1.0.0', 'system', '系统版本'),
('file.upload.path', '/uploads', 'system', '文件上传路径'),
('file.upload.max_size', '10485760', 'system', '文件上传最大大小(字节): 10MB'),
('file.allowed.image_types', 'jpg,jpeg,png,gif,bmp', 'system', '允许上传的图片类型'),
('jwt.secret', 'HerbPlatformSecretKey2025', 'system', 'JWT密钥'),
('jwt.expiration', '1800000', 'system', 'JWT过期时间(毫秒): 30分钟'),
('page.default_size', '10', 'system', '默认分页大小'),
('page.max_size', '100', 'system', '最大分页大小'),
('cache.user.expire', '3600', 'system', '用户缓存过期时间(秒): 1小时'),
('cache.permission.expire', '7200', 'system', '权限缓存过期时间(秒): 2小时');

-- ============================================
-- 四、示例业务数据 (可选)
-- ============================================

-- 4.1 示例地块数据
INSERT INTO herb_field (user_id, field_name, field_code, location, province, city, district, area, soil_type, description, status) VALUES
(2, '东山药材基地A区', 'FIELD001', '安徽省亳州市谯城区', '安徽省', '亳州市', '谯城区', 50.00, '壤土', '位于东山地区，土壤肥沃，适合种植多种中药材', 1),
(2, '东山药材基地B区', 'FIELD002', '安徽省亳州市谯城区', '安徽省', '亳州市', '谯城区', 30.00, '沙质壤土', '靠近水源，适合种植喜湿药材', 1);

-- 4.2 示例作物数据
INSERT INTO herb_crop (user_id, field_id, crop_name, crop_variety, plant_date, expected_harvest_date, plant_area, expected_yield, growth_stage, growth_status, status) VALUES
(2, 1, '白芍', '毫白芍', '2024-03-15', '2025-10-15', 20.00, 8000.00, '生长期', '良好', 1),
(2, 1, '丹参', '山东丹参', '2024-04-01', '2025-11-01', 15.00, 6000.00, '生长期', '良好', 1),
(2, 2, '黄芪', '蒙古黄芪', '2024-03-20', '2025-09-20', 25.00, 10000.00, '生长期', '一般', 1);

-- 4.3 示例农事记录
INSERT INTO herb_farm_record (crop_id, user_id, activity_type, activity_date, activity_detail, materials_used, weather, remark) VALUES
(1, 2, '播种', '2024-03-15', '进行白芍种苗种植', '白芍种苗500株', '晴天', '种植深度15cm'),
(1, 2, '施肥', '2024-05-10', '施加有机肥', '有机肥200kg', '多云', '采用沟施方式'),
(1, 2, '浇水', '2024-06-20', '夏季灌溉', '水', '晴天高温', '滴灌方式，保持土壤湿润'),
(2, 2, '播种', '2024-04-01', '种植丹参', '丹参种苗300株', '晴天', '行距30cm'),
(3, 2, '播种', '2024-03-20', '种植黄芪', '黄芪种子5kg', '阴天', '条播方式');

-- 4.4 示例供应信息
INSERT INTO herb_supply (user_id, crop_id, herb_name, herb_variety, quality_grade, supply_quantity, remaining_quantity, price, production_area, harvest_date, description, contact_name, contact_phone, status) VALUES
(2, 1, '白芍', '毫白芍', '一级', 500.00, 500.00, 35.00, '安徽亳州', '2024-10-15', '自家种植白芍，无农药残留，质量保证', '张三', '13800138001', 1),
(2, 2, '丹参', '山东丹参', '特级', 300.00, 300.00, 28.00, '安徽亳州', '2024-11-01', '有机种植丹参，品质优良', '张三', '13800138001', 1);

-- 4.5 示例需求信息
INSERT INTO herb_demand (user_id, herb_name, herb_variety, quality_requirement, demand_quantity, target_price, demand_date, description, contact_name, contact_phone, status) VALUES
(3, '当归', '岷县当归', '要求无硫熏，水分含量不超过13%', 1000.00, 45.00, '2025-11-15', '长期采购优质当归，需要提供产地证明和质检报告', '李四', '13800138002', 1),
(3, '黄芪', '蒙古黄芪', '要求根条粗壮，无病虫害', 800.00, 32.00, '2025-11-20', '寻找稳定货源，可长期合作', '李四', '13800138002', 1);

-- 4.6 示例溯源信息
INSERT INTO herb_trace (trace_code, crop_id, user_id, herb_name, batch_no, production_area, plant_date, harvest_date, status) VALUES
('TRACE202410150001', 1, 2, '白芍', 'BAT20241015001', '安徽省亳州市谯城区', '2024-03-15', '2024-10-15', 1),
('TRACE202411010001', 2, 2, '丹参', 'BAT20241101001', '安徽省亳州市谯城区', '2024-04-01', '2024-11-01', 1);

-- 4.7 示例溯源节点
INSERT INTO herb_trace_node (trace_id, node_type, node_name, node_time, operator, location, description, sort_order) VALUES
(1, '种植', '白芍种植', '2024-03-15 08:00:00', '张三', '东山药材基地A区', '使用优质白芍种苗进行种植', 1),
(1, '施肥', '有机肥施用', '2024-05-10 09:00:00', '张三', '东山药材基地A区', '施加充分腐熟的有机肥200kg', 2),
(1, '浇水', '夏季灌溉', '2024-06-20 06:00:00', '张三', '东山药材基地A区', '采用滴灌方式进行灌溉', 3),
(1, '采收', '白芍采收', '2024-10-15 07:00:00', '张三', '东山药材基地A区', '采用人工挖掘方式收获白芍', 4),
(2, '种植', '丹参种植', '2024-04-01 08:00:00', '张三', '东山药材基地A区', '种植优质丹参种苗', 1),
(2, '施肥', '追肥', '2024-06-15 09:00:00', '张三', '东山药材基地A区', '施加复合肥料', 2);

-- 4.8 示例每日统计数据
INSERT INTO stat_daily (stat_date, user_register_count, supply_publish_count, demand_publish_count, order_count, order_amount, pv, uv) VALUES
('2024-10-01', 5, 8, 6, 3, 15600.00, 150, 45),
('2024-10-02', 3, 5, 4, 2, 8900.00, 120, 38),
('2024-10-03', 7, 12, 8, 5, 23400.00, 200, 62);

-- ============================================
-- 数据初始化完成
-- ============================================

-- 查看初始化结果
SELECT '=== 用户统计 ===' AS '';
SELECT user_type, COUNT(*) AS count FROM sys_user WHERE deleted = 0 GROUP BY user_type;

SELECT '=== 角色统计 ===' AS '';
SELECT role_name, role_code FROM sys_role WHERE deleted = 0;

SELECT '=== 权限统计 ===' AS '';
SELECT permission_type, COUNT(*) AS count FROM sys_permission WHERE deleted = 0 GROUP BY permission_type;

SELECT '=== 数据字典统计 ===' AS '';
SELECT dict_type, COUNT(*) AS count FROM sys_dict GROUP BY dict_type ORDER BY dict_type;

SELECT '初始化完成！默认管理员账号: admin / admin123' AS '';
SELECT '默认种植户账号: farmer001 / admin123' AS '';
SELECT '默认采购商账号: buyer001 / admin123' AS '';
