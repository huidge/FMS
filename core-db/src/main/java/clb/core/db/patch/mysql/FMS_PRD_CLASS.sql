CREATE TABLE FMS_PRD_CLASS
(
 CLASS_ID BIGINT BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '表ID，主键，供其他表做外键',
 SET_ID BIGINT NOT NULL,
 CLASS_NAME1 VARCHAR(240) DEFAULT NULL COMMENT '分类1',
 CLASS_NAME2 VARCHAR(240) DEFAULT NULL COMMENT '分类2',
 CLASS_NAME3 VARCHAR(240) DEFAULT NULL COMMENT '分类3',
 CLASS_NAME4 VARCHAR(240) DEFAULT NULL COMMENT '分类4',
 CLASS_NAME5 VARCHAR(240) DEFAULT NULL COMMENT '分类5',
 STATUS_CODE VARCHAR(50) NOT NULL DEFAULT 'Y' COMMENT '状态',
 OBJECT_VERSION_NUMBER BIGINT NOT NULL DEFAULT 1 COMMENT '行版本号，用来处理锁',
 PROGRAM_ID BIGINT DEFAULT -1,
 REQUEST_ID BIGINT DEFAULT -1,
 CREATION_DATE  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CREATED_BY INT NOT NULL DEFAULT -1,
 LAST_UPDATED_BY INT NOT NULL DEFAULT -1,
 LAST_UPDATE_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 LAST_UPDATE_LOGIN INT DEFAULT -1,
 ATTRIBUTE_CATEGORY VARCHAR(30),
 ATTRIBUTE1 VARCHAR(150),
 ATTRIBUTE2 VARCHAR(150),
 ATTRIBUTE3 VARCHAR(150),
 ATTRIBUTE4 VARCHAR(150),
 ATTRIBUTE5 VARCHAR(150),
 ATTRIBUTE6 VARCHAR(150),
 ATTRIBUTE7 VARCHAR(150),
 ATTRIBUTE8 VARCHAR(150),
 ATTRIBUTE9 VARCHAR(150),
 ATTRIBUTE10 VARCHAR(150),
 ATTRIBUTE11 VARCHAR(150),
 ATTRIBUTE12 VARCHAR(150),
 ATTRIBUTE13 VARCHAR(150),
 ATTRIBUTE14 VARCHAR(150),
 ATTRIBUTE15 VARCHAR(150)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='产品分类基表';