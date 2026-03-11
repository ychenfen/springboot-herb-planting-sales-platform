USE herb_platform;

ALTER TABLE herb_supply
    ADD COLUMN wholesale_price DECIMAL(10,2) NULL COMMENT '批发价(元/kg)' AFTER price,
    ADD COLUMN wholesale_min_quantity DECIMAL(10,2) NULL COMMENT '起批量(kg)' AFTER wholesale_price;

CREATE TABLE IF NOT EXISTS herb_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    herb_name VARCHAR(100) NOT NULL,
    herb_alias VARCHAR(200),
    herb_category VARCHAR(100),
    planting_season VARCHAR(50),
    disease_type VARCHAR(100),
    keyword_tags VARCHAR(500),
    summary VARCHAR(500),
    planting_points TEXT,
    disease_prevention TEXT,
    rural_value TEXT,
    content TEXT,
    suitable_region VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_knowledge_name (herb_name),
    INDEX idx_knowledge_category (herb_category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中药材知识百科';

CREATE TABLE IF NOT EXISTS herb_disease_case (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    herb_name VARCHAR(100) NOT NULL,
    disease_name VARCHAR(100) NOT NULL,
    disease_type VARCHAR(100),
    symptom_keywords VARCHAR(500),
    symptom_description TEXT,
    prevention_plan TEXT,
    image_url VARCHAR(255),
    season VARCHAR(50),
    severity_level VARCHAR(50),
    feature_tag VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_disease_name (disease_name),
    INDEX idx_disease_herb (herb_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病虫害图文样本库';

CREATE TABLE IF NOT EXISTS herb_calendar_stage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    herb_name VARCHAR(100) NOT NULL,
    stage_name VARCHAR(100) NOT NULL,
    action_type VARCHAR(100),
    day_offset INT DEFAULT 0,
    duration_days INT DEFAULT 1,
    reminder_days INT DEFAULT 3,
    operation_window VARCHAR(100),
    stage_tips VARCHAR(500),
    sort_order INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_calendar_herb (herb_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='种植日历模板';

UPDATE sys_role
SET role_name = '商家',
    role_code = 'ROLE_MERCHANT',
    description = '药材商家/采购商'
WHERE id = 3;

INSERT INTO sys_role (id, role_name, role_code, description, status)
VALUES (4, '普通用户', 'ROLE_USER', '平台普通用户，可浏览知识、下单、查询溯源', 1)
ON DUPLICATE KEY UPDATE
    role_name = VALUES(role_name),
    role_code = VALUES(role_code),
    description = VALUES(description),
    status = VALUES(status);

INSERT INTO sys_permission (id, permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order, status)
VALUES
    (7, '种植知识', 'knowledge', 1, 0, '/knowledge', 'Layout', 'reading', 7, 1),
    (71, '智能百科', 'knowledge:encyclopedia', 1, 7, '/knowledge/encyclopedia', 'Knowledge/Encyclopedia', 'document', 1, 1),
    (72, '病害识别', 'knowledge:disease', 1, 7, '/knowledge/disease', 'Knowledge/Disease', 'picture', 2, 1),
    (73, '种植日历', 'knowledge:calendar', 1, 7, '/knowledge/calendar', 'Knowledge/Calendar', 'calendar', 3, 1)
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    permission_code = VALUES(permission_code),
    parent_id = VALUES(parent_id),
    path = VALUES(path),
    component = VALUES(component),
    icon = VALUES(icon),
    sort_order = VALUES(sort_order),
    status = VALUES(status);

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
VALUES
    (1, 7), (1, 71), (1, 72), (1, 73),
    (2, 7), (2, 71), (2, 72), (2, 73),
    (3, 7), (3, 71), (3, 72), (3, 73),
    (4, 1), (4, 3), (4, 4), (4, 7),
    (4, 21), (4, 25), (4, 26), (4, 32),
    (4, 71), (4, 72), (4, 73),
    (4, 251), (4, 253);

INSERT INTO sys_dict (dict_type, dict_label, dict_value, sort_order, status)
VALUES
    ('user_type', '普通用户', '4', 4, 1)
ON DUPLICATE KEY UPDATE
    dict_label = VALUES(dict_label),
    sort_order = VALUES(sort_order),
    status = VALUES(status);

UPDATE sys_dict
SET dict_label = '商家'
WHERE dict_type = 'user_type' AND dict_value = '2';

INSERT INTO sys_user (id, username, password, real_name, phone, email, user_type, status)
VALUES
    (4, 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户演示', '13800138003', 'user001@herb.com', 4, 1)
ON DUPLICATE KEY UPDATE
    real_name = VALUES(real_name),
    phone = VALUES(phone),
    email = VALUES(email),
    user_type = VALUES(user_type),
    status = VALUES(status);

INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (4, 4);

UPDATE herb_supply
SET wholesale_price = CASE herb_name
        WHEN '白芍' THEN 32.00
        WHEN '丹参' THEN 25.00
        ELSE 29.00
    END,
    wholesale_min_quantity = CASE herb_name
        WHEN '白芍' THEN 50.00
        WHEN '丹参' THEN 30.00
        ELSE 40.00
    END
WHERE wholesale_price IS NULL;

DELETE FROM herb_knowledge;
INSERT INTO herb_knowledge (
    herb_name, herb_alias, herb_category, planting_season, disease_type, keyword_tags, summary,
    planting_points, disease_prevention, rural_value, content, suitable_region
) VALUES
    ('黄芪', '绵黄芪,北芪', '补气类药材', '春季', '根腐病,叶斑病', '黄芪 病害,黄芪 种植,黄芪 水肥管理', '黄芪适合旱作与半旱作地块，管理重点在排水、防板结和苗期病害预防。',
     '选择疏松砂壤土，亩施腐熟有机肥，播后保持浅湿，苗高10cm后追施氮磷钾复合肥。', '重点防根腐病和叶斑病，雨后及时排水，发病初期可用生物制剂+低毒杀菌剂轮换。', '黄芪产业链长，适合规模化种植与初加工，可带动乡村合作社联农增收。', '黄芪种植要兼顾药效成分和产量，苗期重整齐度，中后期重根系膨大与病害预警。', '甘肃,内蒙古,山西,宁夏'),
    ('当归', '岷归,秦归', '根茎类药材', '秋季', '褐斑病,软腐病', '当归 病害,当归 栽培,当归 采收', '当归喜冷凉、湿润环境，忌高温积水，适合海拔较高地区连片种植。',
     '移栽前深翻整地，保持土壤团粒结构，花薹抽生前控制氮肥，避免旺长。', '高湿环境易发褐斑病和软腐病，应强化通风、起垄排水并及时清理病残体。', '当归是道地药材，适合作为区域特色品牌打造和乡村振兴示范作物。', '当归生长周期长，需兼顾地力恢复与轮作安排，采收期要根据根形和挥发油含量综合判断。', '甘肃,云南,四川'),
    ('白芍', '杭白芍,川白芍', '根类药材', '春季', '白绢病,锈病', '白芍 病虫害,白芍 除草,白芍 采收', '白芍重在根系品质，宜选择土层深厚、排水良好的地块，忌重茬。',
     '播前进行种苗消毒，苗期控制杂草，中期以磷钾肥促根，采收前注意控水保质。', '白绢病和锈病高发期在梅雨季，应加强田间卫生和药剂预防。', '白芍可与加工、电商、溯源销售结合，形成“种植+品牌+订单”增收模式。', '白芍适合做标准化种植示范，便于展示规范化田间管理、批次追踪和品质分级。', '安徽,浙江,四川');

DELETE FROM herb_disease_case;
INSERT INTO herb_disease_case (
    herb_name, disease_name, disease_type, symptom_keywords, symptom_description, prevention_plan,
    image_url, season, severity_level, feature_tag
) VALUES
    ('黄芪', '黄芪叶斑病', '真菌性病害', '黄褐斑,圆形病斑,叶片失绿', '叶面出现黄褐色小斑点，后期扩展成圆形或不规则病斑，边缘颜色较深。', '发病初期清除病叶，降低田间湿度，交替使用代森锰锌和生物杀菌剂，7天一次连防2-3次。', '/api/static/knowledge/disease/huangqi_leaf_spot.png', '夏秋季', '中度', '黄褐色斑点'),
    ('黄芪', '黄芪根腐病', '土传病害', '根部褐变,植株萎蔫,烂根', '根系和根颈部褐变腐烂，地上部叶片发黄萎蔫，严重时整株枯死。', '及时拔除病株，病穴撒施石灰或生物菌剂，雨季强化排水并避免偏施氮肥。', '/api/static/knowledge/disease/huangqi_root_rot.png', '雨季', '重度', '褐黑色根腐'),
    ('当归', '当归褐斑病', '叶部病害', '褐色病斑,边缘紫红,叶片卷曲', '叶片出现褐色病斑，边缘紫红色，湿度大时病斑融合并导致叶缘卷曲。', '合理密植，加强通风透光，发病初期喷施苯醚甲环唑或多抗霉素。', '/api/static/knowledge/disease/danggui_blight.png', '梅雨季', '中度', '褐斑卷叶'),
    ('白芍', '白芍白绢病', '茎基部病害', '白色菌丝,茎基腐烂,倒伏', '植株近地面部位出现白色丝状菌丝，茎基部软腐后整株倒伏。', '清除病株和表土，撒施石灰，采用井冈霉素或枯草芽孢杆菌灌根防治。', '/api/static/knowledge/disease/baishao_southern_blight.png', '高温高湿期', '重度', '白色菌丝'),
    ('白芍', '白芍锈病', '叶部病害', '橙黄色粉疱,叶背锈点,早衰', '叶背形成橙黄色粉状疱斑，叶片失绿后提早衰老，影响根部膨大。', '及时清理病叶，增强钾肥供应，发病初期喷施三唑酮类药剂。', '/api/static/knowledge/disease/baishao_rust.png', '夏季', '轻中度', '橙黄锈点');

DELETE FROM herb_calendar_stage;
INSERT INTO herb_calendar_stage (
    herb_name, stage_name, action_type, day_offset, duration_days, reminder_days, operation_window, stage_tips, sort_order
) VALUES
    ('黄芪', '播种整地', '播种', 0, 7, 3, '播前1周', '完成整地、施基肥和种子消毒，保证土壤疏松透气。', 1),
    ('黄芪', '苗期追肥', '施肥', 30, 5, 3, '出苗后30天', '苗齐苗壮后追施复合肥，避免偏施氮肥。', 2),
    ('黄芪', '田间除草', '除草', 45, 7, 2, '雨后晴天', '结合中耕松土开展人工除草，减少养分竞争。', 3),
    ('黄芪', '病害预防', '病害巡检', 70, 10, 3, '雨季来临前', '重点巡查叶斑和根腐，发现病株及时隔离处理。', 4),
    ('黄芪', '采收准备', '采收', 180, 15, 7, '成熟前2周', '检查根系膨大情况，分批安排采收与晾晒。', 5),
    ('当归', '育苗移栽', '播种', 0, 10, 5, '冷凉天气', '控制苗床湿度，移栽后保持缓苗水。', 1),
    ('当归', '追肥壮苗', '施肥', 40, 5, 3, '缓苗后', '以磷钾肥为主，防止徒长和抽薹。', 2),
    ('当归', '中耕除草', '除草', 55, 7, 2, '雨后2天', '保持垄面干净，改善通风条件。', 3),
    ('当归', '病害巡查', '病害巡检', 80, 10, 3, '梅雨季前', '注意褐斑病和软腐病，及时摘除病叶。', 4),
    ('当归', '成熟采收', '采收', 210, 20, 10, '秋末', '根据根形和香气判定采收窗口，避免雨后采挖。', 5),
    ('白芍', '春季定植', '播种', 0, 7, 3, '地温稳定后', '种苗消毒后定植，注意株行距和排水沟设置。', 1),
    ('白芍', '追肥促根', '施肥', 35, 5, 3, '幼苗成活后', '以磷钾肥促根系生长，控制旺长。', 2),
    ('白芍', '人工除草', '除草', 50, 5, 2, '雨后晴天', '防止杂草抢肥，减少病虫栖息环境。', 3),
    ('白芍', '白绢病防控', '病害巡检', 75, 10, 3, '高温高湿前', '重点查看茎基部菌丝和倒伏情况，必要时灌根。', 4),
    ('白芍', '采收与分级', '采收', 200, 20, 10, '根系充分膨大后', '安排采收、分级、晾晒和入库，便于后续溯源销售。', 5);

UPDATE sys_config
SET config_value = '中药材种植与销售服务平台（知识增强版）'
WHERE config_key = 'system.name';
