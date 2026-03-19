USE herb_platform;
SET NAMES utf8mb4;

-- =========================================================
-- Seed data
-- =========================================================

-- =========================================================
-- Roles and permissions
-- =========================================================

INSERT INTO sys_role (id, role_name, role_code, description, status) VALUES
(1, '系统管理员', 'ROLE_ADMIN', '系统管理员，拥有全部权限', 1),
(2, '种植户', 'ROLE_FARMER', '中药材种植户', 1),
(3, '商家', 'ROLE_MERCHANT', '药材商家/采购商', 1),
(4, '普通用户', 'ROLE_USER', '平台普通用户，可浏览知识、下单、查询溯源', 1);

INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order, status) VALUES
(1, '首页', 'dashboard', 1, 0, '/dashboard', 'Dashboard', 'dashboard', 1, 1),
(2, '种植管理', 'planting', 1, 0, '/planting', 'Layout', 'plant', 2, 1),
(3, '销售协同', 'trading', 1, 0, '/trading', 'Layout', 'shopping', 3, 1),
(4, '质量溯源', 'trace', 1, 0, '/trace', 'Layout', 'qrcode', 4, 1),
(5, '数据分析', 'analysis', 1, 0, '/analysis', 'Layout', 'chart', 5, 1),
(6, '系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 6, 1),
(7, '种植知识', 'knowledge', 1, 0, '/knowledge', 'Layout', 'reading', 7, 1),

(11, '地块管理', 'planting:field', 1, 2, '/planting/field', 'Planting/Field', 'field', 1, 1),
(12, '作物管理', 'planting:crop', 1, 2, '/planting/crop', 'Planting/Crop', 'crop', 2, 1),
(13, '农事记录', 'planting:record', 1, 2, '/planting/record', 'Planting/Record', 'record', 3, 1),

(21, '供应大厅', 'trading:supply_market', 1, 3, '/trading/supply-market', 'Trading/SupplyMarket', 'supply', 1, 1),
(22, '采购需求', 'trading:demand_market', 1, 3, '/trading/demand-market', 'Trading/DemandMarket', 'demand', 2, 1),
(23, '我的供应', 'trading:my_supply', 1, 3, '/trading/my-supply', 'Trading/MySupply', 'my-supply', 3, 1),
(24, '我的需求', 'trading:my_demand', 1, 3, '/trading/my-demand', 'Trading/MyDemand', 'my-demand', 4, 1),
(25, '订单管理', 'trading:order', 1, 3, '/trading/order', 'Trading/Order', 'order', 5, 1),
(26, '我的收藏', 'trading:favorite', 1, 3, '/trading/favorite', 'Trading/Favorite', 'favorite', 6, 1),

(31, '溯源管理', 'trace:manage', 1, 4, '/trace/manage', 'Trace/Manage', 'trace-manage', 1, 1),
(32, '溯源查询', 'trace:query', 1, 4, '/trace/query', 'Trace/Query', 'trace-query', 2, 1),

(41, '产量分析', 'analysis:yield', 1, 5, '/analysis/yield', 'Analysis/Yield', 'yield', 1, 1),
(42, '销售分析', 'analysis:sales', 1, 5, '/analysis/sales', 'Analysis/Sales', 'sales', 2, 1),

(51, '用户管理', 'system:user', 1, 6, '/system/user', 'System/User', 'user', 1, 1),
(52, '角色管理', 'system:role', 1, 6, '/system/role', 'System/Role', 'role', 2, 1),
(53, '权限管理', 'system:permission', 1, 6, '/system/permission', 'System/Permission', 'permission', 3, 1),
(54, '系统日志', 'system:log', 1, 6, '/system/log', 'System/Log', 'log', 4, 1),
(55, '系统配置', 'system:config', 1, 6, '/system/config', 'System/Config', 'config', 5, 1),
(56, '通知管理', 'system:notice', 1, 6, '/system/notice', 'System/Notice', 'notice', 6, 1),

(71, '智能百科', 'knowledge:encyclopedia', 1, 7, '/knowledge/encyclopedia', 'Knowledge/Encyclopedia', 'document', 1, 1),
(72, '病害识别', 'knowledge:disease', 1, 7, '/knowledge/disease', 'Knowledge/Disease', 'picture', 2, 1),
(73, '种植日历', 'knowledge:calendar', 1, 7, '/knowledge/calendar', 'Knowledge/Calendar', 'calendar', 3, 1),

(111, '新增地块', 'planting:field:add', 2, 11, NULL, NULL, NULL, 1, 1),
(112, '编辑地块', 'planting:field:edit', 2, 11, NULL, NULL, NULL, 2, 1),
(113, '删除地块', 'planting:field:delete', 2, 11, NULL, NULL, NULL, 3, 1),
(114, '查看地块', 'planting:field:view', 2, 11, NULL, NULL, NULL, 4, 1),

(121, '新增作物', 'planting:crop:add', 2, 12, NULL, NULL, NULL, 1, 1),
(122, '编辑作物', 'planting:crop:edit', 2, 12, NULL, NULL, NULL, 2, 1),
(123, '删除作物', 'planting:crop:delete', 2, 12, NULL, NULL, NULL, 3, 1),
(124, '查看作物', 'planting:crop:view', 2, 12, NULL, NULL, NULL, 4, 1),

(131, '新增记录', 'planting:record:add', 2, 13, NULL, NULL, NULL, 1, 1),
(132, '编辑记录', 'planting:record:edit', 2, 13, NULL, NULL, NULL, 2, 1),
(133, '删除记录', 'planting:record:delete', 2, 13, NULL, NULL, NULL, 3, 1),

(231, '发布供应', 'trading:supply:add', 2, 23, NULL, NULL, NULL, 1, 1),
(232, '编辑供应', 'trading:supply:edit', 2, 23, NULL, NULL, NULL, 2, 1),
(233, '下架供应', 'trading:supply:offline', 2, 23, NULL, NULL, NULL, 3, 1),

(241, '发布需求', 'trading:demand:add', 2, 24, NULL, NULL, NULL, 1, 1),
(242, '编辑需求', 'trading:demand:edit', 2, 24, NULL, NULL, NULL, 2, 1),
(243, '取消需求', 'trading:demand:cancel', 2, 24, NULL, NULL, NULL, 3, 1),

(251, '创建订单', 'trading:order:create', 2, 25, NULL, NULL, NULL, 1, 1),
(252, '确认订单', 'trading:order:confirm', 2, 25, NULL, NULL, NULL, 2, 1),
(253, '取消订单', 'trading:order:cancel', 2, 25, NULL, NULL, NULL, 3, 1),

(311, '创建溯源', 'trace:create', 2, 31, NULL, NULL, NULL, 1, 1),
(312, '编辑溯源', 'trace:edit', 2, 31, NULL, NULL, NULL, 2, 1),
(313, '生成二维码', 'trace:qrcode', 2, 31, NULL, NULL, NULL, 3, 1),

(511, '新增用户', 'system:user:add', 2, 51, NULL, NULL, NULL, 1, 1),
(512, '编辑用户', 'system:user:edit', 2, 51, NULL, NULL, NULL, 2, 1),
(513, '删除用户', 'system:user:delete', 2, 51, NULL, NULL, NULL, 3, 1),
(514, '分配角色', 'system:user:role', 2, 51, NULL, NULL, NULL, 4, 1);

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE deleted = 0;

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 7),
(2, 11), (2, 12), (2, 13),
(2, 21), (2, 23), (2, 25), (2, 26),
(2, 31), (2, 32),
(2, 41), (2, 42),
(2, 71), (2, 72), (2, 73),
(2, 111), (2, 112), (2, 113), (2, 114),
(2, 121), (2, 122), (2, 123), (2, 124),
(2, 131), (2, 132), (2, 133),
(2, 231), (2, 232), (2, 233),
(2, 251), (2, 252), (2, 253),
(2, 311), (2, 312), (2, 313);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 1), (3, 3), (3, 4), (3, 7),
(3, 21), (3, 22), (3, 24), (3, 25), (3, 26),
(3, 32),
(3, 71), (3, 72), (3, 73),
(3, 241), (3, 242), (3, 243),
(3, 251), (3, 253);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 1), (4, 3), (4, 4), (4, 7),
(4, 21), (4, 25), (4, 26),
(4, 32),
(4, 71), (4, 72), (4, 73),
(4, 251), (4, 253);

-- =========================================================
-- Users
-- =========================================================

INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '13800138000', 'admin@herb.com', 3, 1),
(2, 'farmer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13800138001', 'farmer001@herb.com', 1, 1),
(3, 'buyer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', '13800138002', 'buyer001@herb.com', 2, 1),
(4, 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户演示', '13800138003', 'user001@herb.com', 4, 1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

-- =========================================================
-- Dictionary and config
-- =========================================================

INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort_order, status) VALUES
('user_type', '种植户', '1', 1, 1),
('user_type', '商家', '2', 2, 1),
('user_type', '管理员', '3', 3, 1),
('user_type', '普通用户', '4', 4, 1),

('user_status', '禁用', '0', 1, 1),
('user_status', '正常', '1', 2, 1),

('soil_type', '沙土', 'sandy', 1, 1),
('soil_type', '黏土', 'clay', 2, 1),
('soil_type', '壤土', 'loam', 3, 1),
('soil_type', '砂质壤土', 'sandy_loam', 4, 1),
('soil_type', '黏质壤土', 'clay_loam', 5, 1),

('irrigation_type', '滴灌', 'drip', 1, 1),
('irrigation_type', '喷灌', 'sprinkler', 2, 1),
('irrigation_type', '漫灌', 'flood', 3, 1),
('irrigation_type', '微灌', 'micro', 4, 1),

('field_status', '休耕', '0', 1, 1),
('field_status', '在用', '1', 2, 1),
('field_status', '整改中', '2', 3, 1),

('growth_stage', '播种期', 'seeding', 1, 1),
('growth_stage', '发芽期', 'germination', 2, 1),
('growth_stage', '生长期', 'growing', 3, 1),
('growth_stage', '开花期', 'flowering', 4, 1),
('growth_stage', '成熟期', 'maturity', 5, 1),
('growth_stage', '采收期', 'harvest', 6, 1),

('growth_status', '良好', 'good', 1, 1),
('growth_status', '一般', 'normal', 2, 1),
('growth_status', '较差', 'poor', 3, 1),

('crop_status', '生长中', '1', 1, 1),
('crop_status', '已采收', '2', 2, 1),
('crop_status', '已销售', '3', 3, 1),
('crop_status', '已废弃', '4', 4, 1),

('activity_type', '播种', 'seeding', 1, 1),
('activity_type', '施肥', 'fertilizing', 2, 1),
('activity_type', '浇水', 'watering', 3, 1),
('activity_type', '除草', 'weeding', 4, 1),
('activity_type', '施药', 'spraying', 5, 1),
('activity_type', '采收', 'harvesting', 6, 1),
('activity_type', '整地', 'tillage', 7, 1),
('activity_type', '修剪', 'pruning', 8, 1),
('activity_type', '其他', 'other', 9, 1),

('quality_grade', '特级', 'premium', 1, 1),
('quality_grade', '一级', 'first', 2, 1),
('quality_grade', '二级', 'second', 3, 1),
('quality_grade', '三级', 'third', 4, 1),

('supply_status', '供应中', '1', 1, 1),
('supply_status', '已售罄', '2', 2, 1),
('supply_status', '已下架', '3', 3, 1),

('demand_status', '采购中', '1', 1, 1),
('demand_status', '已完成', '2', 2, 1),
('demand_status', '已取消', '3', 3, 1),

('order_status', '待确认', '1', 1, 1),
('order_status', '待发货', '2', 2, 1),
('order_status', '待收货', '3', 3, 1),
('order_status', '已完成', '4', 4, 1),
('order_status', '已取消', '5', 5, 1),
('order_status', '已退款', '6', 6, 1),

('payment_status', '未支付', '0', 1, 1),
('payment_status', '已支付', '1', 2, 1),
('payment_status', '已退款', '2', 3, 1),

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

('notice_type', '系统通知', '1', 1, 1),
('notice_type', '订单通知', '2', 2, 1),
('notice_type', '评论通知', '3', 3, 1),
('notice_type', '其他通知', '4', 4, 1);

INSERT INTO sys_config (config_key, config_value, config_type, description) VALUES
('system.name', '中药材种植与销售服务平台（知识增强版）', 'system', '系统名称'),
('system.version', '1.1.0', 'system', '系统版本'),
('file.upload.path', '/uploads', 'system', '文件上传路径'),
('file.upload.max_size', '10485760', 'system', '文件上传最大大小(字节): 10MB'),
('file.allowed.image_types', 'jpg,jpeg,png,gif,bmp', 'system', '允许上传的图片类型'),
('jwt.secret', 'HerbPlatformSecretKey2025', 'system', 'JWT 密钥'),
('jwt.expiration', '1800000', 'system', 'JWT 过期时间(毫秒): 30 分钟'),
('page.default_size', '10', 'system', '默认分页大小'),
('page.max_size', '100', 'system', '最大分页大小'),
('cache.user.expire', '3600', 'system', '用户缓存过期时间(秒): 1 小时'),
('cache.permission.expire', '7200', 'system', '权限缓存过期时间(秒): 2 小时');

-- =========================================================
-- Demo business data
-- =========================================================

INSERT INTO herb_field (id, user_id, field_name, field_code, location, province, city, district, area, soil_type, description, status) VALUES
(1, 2, '东山药材基地A区', 'FIELD001', '安徽省亳州市谯城区', '安徽省', '亳州市', '谯城区', 50.00, '壤土', '土层深厚，适合标准化药材种植。', 1),
(2, 2, '东山药材基地B区', 'FIELD002', '安徽省亳州市谯城区', '安徽省', '亳州市', '谯城区', 30.00, '砂质壤土', '靠近水源，适合喜湿药材。', 1);

INSERT INTO herb_crop (id, user_id, field_id, crop_name, crop_variety, plant_date, expected_harvest_date, plant_area, expected_yield, growth_stage, growth_status, status) VALUES
(1, 2, 1, '白芍', '杭白芍', '2024-03-15', '2025-10-15', 20.00, 8000.00, '生长期', '良好', 1),
(2, 2, 1, '丹参', '山东丹参', '2024-04-01', '2025-11-01', 15.00, 6000.00, '生长期', '良好', 1),
(3, 2, 2, '黄芪', '蒙古黄芪', '2024-03-20', '2025-09-20', 25.00, 10000.00, '生长期', '一般', 1);

INSERT INTO herb_farm_record (crop_id, user_id, activity_type, activity_date, activity_detail, materials_used, weather, remark) VALUES
(1, 2, 'sow', '2024-03-15', '进行白芍种苗移栽', '白芍种苗 500 株', '晴天', '定植深度 15cm'),
(1, 2, 'fertilize', '2024-05-10', '施加有机肥', '有机肥 200kg', '多云', '采用沟施方式'),
(1, 2, 'water', '2024-06-20', '夏季灌溉', '清水', '晴天高温', '滴灌保持土壤湿润'),
(2, 2, 'sow', '2024-04-01', '种植丹参', '丹参种苗 300 株', '晴天', '行距 30cm'),
(3, 2, 'sow', '2024-03-20', '种植黄芪', '黄芪种子 5kg', '阴天', '采用条播方式');

INSERT INTO herb_supply (
    id, user_id, crop_id, herb_name, herb_variety, quality_grade, supply_quantity, remaining_quantity,
    price, wholesale_price, wholesale_min_quantity, production_area, harvest_date, description,
    contact_name, contact_phone, status
) VALUES
(1, 2, 1, '白芍', '杭白芍', '一级', 500.00, 500.00, 35.00, 32.00, 50.00, '安徽亳州', '2024-10-15', '自家种植白芍，支持溯源码查看与批发采购。', '张三', '13800138001', 1),
(2, 2, 2, '丹参', '山东丹参', '特级', 300.00, 300.00, 28.00, 25.00, 30.00, '安徽亳州', '2024-11-01', '有机种植丹参，适合药企与批发渠道。', '张三', '13800138001', 1),
(3, 2, 3, '黄芪', '蒙古黄芪', '一级', 420.00, 420.00, 31.00, 29.00, 40.00, '甘肃定西', '2024-09-20', '黄芪片条完整，适合饮片厂与电商直播销售。', '张三', '13800138001', 1);

INSERT INTO herb_demand (id, user_id, herb_name, herb_variety, quality_requirement, demand_quantity, target_price, demand_date, description, contact_name, contact_phone, status) VALUES
(1, 3, '当归', '岷县当归', '要求无硫熏、水分含量不超过 13%，需提供质检报告。', 1000.00, 45.00, '2025-11-15', '长期采购优质当归，支持订单合作。', '李四', '13800138002', 1),
(2, 3, '黄芪', '蒙古黄芪', '要求根条粗壮、无病虫害，支持溯源批次。', 800.00, 32.00, '2025-11-20', '寻找稳定货源，可长期合作。', '李四', '13800138002', 1);

INSERT INTO herb_order (
    id, order_no, supply_id, seller_id, buyer_id, herb_name, herb_variety, quantity, unit_price, total_amount,
    order_status, payment_status, delivery_address, delivery_phone, delivery_name, logistics_company, logistics_no,
    confirm_time, delivery_time, complete_time, cancel_time, cancel_reason, remark, create_time
) VALUES
(101, 'ORD202603010101', 1, 2, 3, '白芍', '杭白芍', 100.00, 32.00, 3200.00, 4, 1,
 '浙江省杭州市西湖区某某路88号', '13800138002', '李四', '顺丰速运', 'SF2026030101',
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, NULL, '按批发价成交', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(102, 'ORD202603020102', 2, 2, 4, '丹参', '山东丹参', 50.00, 25.00, 1250.00, 3, 1,
 '上海市浦东新区某某大道100号', '13800138003', '普通用户演示', '京东物流', 'JD2026030201',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, NULL, '已发货待收货', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(103, 'ORD202603030103', 3, 2, 3, '黄芪', '蒙古黄芪', 20.00, 31.00, 620.00, 1, 0,
 '浙江省杭州市西湖区某某路88号', '13800138002', '李四', NULL, NULL,
 NULL, NULL, NULL, NULL, NULL, '待卖家确认', DATE_SUB(NOW(), INTERVAL 12 HOUR));

INSERT INTO herb_favorite (user_id, target_type, target_id, create_time) VALUES
(3, 1, 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 1, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

INSERT INTO herb_trace (
    id, trace_code, crop_id, user_id, herb_name, batch_no, production_area, plant_date,
    harvest_date, quality_standard, quality_report, status
) VALUES
(1, 'TRACE202410150001', 1, 2, '白芍', 'BAT20241015001', '安徽省亳州市谯城区', '2024-03-15', '2024-10-15', '地方标准饮片一级', '白芍批次质检合格', 1),
(2, 'TRACE202411010001', 2, 2, '丹参', 'BAT20241101001', '安徽省亳州市谯城区', '2024-04-01', '2024-11-01', '企业内控标准 A 级', '丹参批次农残合格', 1),
(3, 'TRACE202409200001', 3, 2, '黄芪', 'BAT20240920001', '甘肃省定西市', '2024-03-20', '2024-09-20', '道地药材分级标准', '黄芪批次感官与含量检测合格', 1);

INSERT INTO herb_trace_node (trace_id, node_type, node_name, node_time, operator, location, description, sort_order) VALUES
(1, 'plant', '白芍播种定植', '2024-03-15 08:00:00', '张三', '东山药材基地A区', '使用合格种苗进行定植。', 1),
(1, 'fertilize', '有机肥施用', '2024-05-10 09:00:00', '张三', '东山药材基地A区', '施加腐熟有机肥 200kg。', 2),
(1, 'harvest', '白芍采收', '2024-10-15 07:00:00', '张三', '东山药材基地A区', '人工采挖并初步分级。', 3),
(2, 'plant', '丹参定植', '2024-04-01 08:00:00', '张三', '东山药材基地A区', '按照标准株行距完成定植。', 1),
(2, 'fertilize', '丹参追肥', '2024-06-15 09:00:00', '张三', '东山药材基地A区', '施加复合肥促进根部膨大。', 2),
(3, 'plant', '黄芪播种', '2024-03-20 08:30:00', '张三', '东山药材基地B区', '条播播种并同步完成基肥施用。', 1),
(3, 'testing', '黄芪质量检测', '2024-09-18 11:00:00', '质检员', '甘肃定西', '完成黄芪批次感官、水分、含量检测。', 2);

INSERT INTO sys_notice (user_id, notice_type, title, content, is_read) VALUES
(2, 1, '种植日历已生成', '黄芪、白芍、当归的农事节点模板已可在系统中查看。', 0),
(3, 2, '采购需求已发布', '您的当归采购需求已进入市场大厅。', 0),
(4, 1, '欢迎体验平台', '您可以查看种植知识、溯源信息，并直接下单采购。', 0);

INSERT INTO stat_daily (stat_date, user_register_count, supply_publish_count, demand_publish_count, order_count, order_amount, pv, uv) VALUES
('2024-10-01', 5, 8, 6, 3, 15600.00, 150, 45),
('2024-10-02', 3, 5, 4, 2, 8900.00, 120, 38),
('2024-10-03', 7, 12, 8, 5, 23400.00, 200, 62);

-- =========================================================
-- Knowledge module demo data
-- =========================================================

INSERT INTO herb_knowledge (
    herb_name, herb_alias, herb_category, planting_season, disease_type, keyword_tags, summary,
    planting_points, disease_prevention, rural_value, content, suitable_region
) VALUES
('黄芪', '绵黄芪、北芪', '补气类药材', '春季', '根腐病、叶斑病', '黄芪 病害,黄芪 种植,黄芪 水肥管理', '黄芪适合排水良好、土层深厚地块，重点在根系膨大和雨季病害预防。', '播前整地施足基肥，出苗后保持浅湿，苗高 10cm 后追施复合肥，控制旺长。', '重点防治根腐病和叶斑病，雨后及时排水，发病初期采用生物制剂与低毒杀菌剂轮换。', '黄芪适合规模化种植与初加工联动，可带动合作社订单农业和乡村增收。', '黄芪种植既看产量，也看药效成分沉积，中后期重根系膨大和病害预警。', '甘肃、内蒙古、山西、宁夏'),
('当归', '岷归、秦归', '根茎类药材', '秋季', '褐斑病、软腐病', '当归 病害,当归 栽培,当归 采收', '当归喜冷凉湿润环境，忌高温积水，适合高海拔区域规模化种植。', '移栽前深翻整地，保持土壤团粒结构，抽薹前控制水肥，避免徒长。', '梅雨季重点防褐斑病和软腐病，应强化通风、起垄排水并及时清理病残体。', '当归是典型道地药材，适合作为区域品牌和乡村振兴特色品种打造。', '当归生长周期较长，需兼顾轮作、地力恢复与采收质量控制。', '甘肃、云南、四川'),
('白芍', '杭白芍、川白芍', '根类药材', '春季', '白绢病、锈病', '白芍 病虫害,白芍 除草,白芍 采收', '白芍重在根系品质，宜选择排水良好、土层深厚地块，便于标准化管理。', '播前进行种苗消毒，苗期重视除草，中期以磷钾肥促根，采收前注意控水保质。', '白绢病和锈病在高温高湿季节高发，应加强田间卫生和药剂预防。', '白芍适合与加工、电商、溯源销售结合，形成“种植+品牌+订单”增收模式。', '白芍适合作为规范化种植示范品种，便于展示田间管理、批次追踪和质量分级。', '安徽、浙江、四川');

INSERT INTO herb_disease_case (
    herb_name, disease_name, disease_type, symptom_keywords, symptom_description, prevention_plan,
    image_url, season, severity_level, feature_tag
) VALUES
('黄芪', '黄芪叶斑病', '真菌性病害', '黄褐斑、圆形病斑、叶片失绿', '叶面先出现黄褐色小斑点，后期扩展成圆形或不规则病斑，边缘颜色较深。', '发病初期清除病叶，降低田间湿度，交替使用代森锰锌和生物杀菌剂，7 天一次连防 2-3 次。', '/api/static/knowledge/disease/huangqi_leaf_spot.png', '夏秋季', '中度', '黄褐色斑点'),
('黄芪', '黄芪根腐病', '土传病害', '根部腐变、植株萎蔫、烂根', '根系和根颈部腐烂发黑，地上部叶片发黄萎蔫，严重时整株枯死。', '及时拔除病株，病穴撒施石灰或生物菌剂，雨季强化排水并避免偏施氮肥。', '/api/static/knowledge/disease/huangqi_root_rot.png', '雨季', '重度', '腐黑色根部'),
('当归', '当归褐斑病', '叶部病害', '褐色病斑、边缘紫红、卷叶', '叶片出现褐色病斑，边缘紫红色，湿度大时病斑融合并导致叶片卷曲。', '合理密植，加强通风透光，发病初期喷施苯醚甲环唑或多抗霉素。', '/api/static/knowledge/disease/danggui_blight.png', '梅雨季', '中度', '褐斑卷叶'),
('白芍', '白芍白绢病', '茎基部病害', '白色菌丝、茎基腐烂、倒伏', '植株近地面部位出现白色丝状菌丝，茎基部软腐后整株倒伏。', '清除病株和表土，撒施石灰，采用井冈霉素或枯草芽孢杆菌灌根防治。', '/api/static/knowledge/disease/baishao_southern_blight.png', '高温高湿期', '重度', '白色菌丝'),
('白芍', '白芍锈病', '叶部病害', '橙黄色粉疱、叶背锈点、早衰', '叶背形成橙黄色粉状疱斑，叶片失绿后提早衰老，影响根部膨大。', '及时清理病叶，增强钾肥供应，发病初期喷施三唑酮类药剂。', '/api/static/knowledge/disease/baishao_rust.png', '夏季', '轻中度', '橙黄锈点');

INSERT INTO herb_calendar_stage (
    herb_name, stage_name, action_type, day_offset, duration_days, reminder_days, operation_window, stage_tips, sort_order
) VALUES
('黄芪', '播种整地', '播种', 0, 7, 3, '播前 1 周', '完成整地、施基肥和种子消毒，保证土壤疏松透气。', 1),
('黄芪', '苗期追肥', '施肥', 30, 5, 3, '出苗后 30 天', '苗齐苗壮后追施复合肥，避免偏施氮肥。', 2),
('黄芪', '田间除草', '除草', 45, 7, 2, '雨后晴天', '结合中耕松土开展人工除草，减少养分竞争。', 3),
('黄芪', '病害巡检', '病害巡检', 70, 10, 3, '雨季来临前', '重点巡查叶斑和根腐，发现病株及时隔离处理。', 4),
('黄芪', '采收准备', '采收', 180, 15, 7, '成熟前 1 周', '检查根系膨大情况，分批安排采收与晾晒。', 5),

('当归', '育苗移栽', '播种', 0, 10, 5, '冷凉天气', '控制苗床湿度，移栽后保持缓苗水。', 1),
('当归', '追肥壮苗', '施肥', 40, 5, 3, '缓苗后', '以磷钾肥为主，防止徒长和抽薹。', 2),
('当归', '中耕除草', '除草', 55, 7, 2, '雨后 2 天', '保持垄面干净，改善通风条件。', 3),
('当归', '病害巡查', '病害巡检', 80, 10, 3, '梅雨季前', '注意褐斑病和软腐病，及时摘除病叶。', 4),
('当归', '成熟采收', '采收', 210, 20, 10, '秋末', '根据根形和香气确定采收窗口，避免雨后采挖。', 5),

('白芍', '春季定植', '播种', 0, 7, 3, '地温稳定后', '种苗消毒后定植，注意株行距与排水沟设置。', 1),
('白芍', '追肥促根', '施肥', 35, 5, 3, '幼苗成活后', '以磷钾肥促根系生长，控制旺长。', 2),
('白芍', '人工除草', '除草', 50, 5, 2, '雨后晴天', '减少杂草争肥，降低病虫栖息环境。', 3),
('白芍', '白绢病预警', '病害巡检', 75, 10, 3, '高温高湿前', '重点检查茎基部菌丝与倒伏情况，必要时灌根。', 4),
('白芍', '采收分级', '采收', 200, 20, 10, '根系充分膨大后', '安排采收、分级、晾晒和入库，便于后续溯源销售。', 5);

-- =========================================================
-- Summary
-- =========================================================

SELECT '=== 用户统计 ===' AS '';
SELECT user_type, COUNT(*) AS count FROM sys_user WHERE deleted = 0 GROUP BY user_type;

SELECT '=== 角色统计 ===' AS '';
SELECT role_name, role_code FROM sys_role WHERE deleted = 0;

SELECT '=== 权限统计 ===' AS '';
SELECT permission_type, COUNT(*) AS count FROM sys_permission WHERE deleted = 0 GROUP BY permission_type;

SELECT '=== 知识模块统计 ===' AS '';
SELECT COUNT(*) AS knowledge_count FROM herb_knowledge;
SELECT COUNT(*) AS disease_case_count FROM herb_disease_case;
SELECT COUNT(*) AS calendar_stage_count FROM herb_calendar_stage;

SELECT '初始化完成：管理员 admin / admin123' AS '';
SELECT '初始化完成：种植户 farmer001 / admin123' AS '';
SELECT '初始化完成：商家 buyer001 / admin123' AS '';
SELECT '初始化完成：普通用户 user001 / admin123' AS '';
