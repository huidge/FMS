package clb.core.fnd.utils;

/**
 * @name Constants
 * @description 数据源管理模块常量类
 * @author xiang.ding@hand-china.com
 * @version 1.0
 */
public class Constants {

	/**
	 * 导入限制最多字段数量 -- 30
	 */
	public static final int MAX_ATTR = 150;

	/**
	 * int最大值
	 */
	public static final int MAX_NUMBER = Integer.MAX_VALUE;

	/**
	 * 编码 UTF-8
	 */
	public static final String ENC = "UTF-8";

	/**
	 * 字段重复描述维护 {0}重复！
	 */
	public static final String FIELD_UNIQUE = "fnd.import.field_repeat";

	/**
	 * 商品编码公司ID字段重复描述维护 {0}重复！
	 */
	public static final String TRADE_ITEM_COMPANYID = "md_trade_item_company";

	/**
	 * 字段为空描述维护 {0}不能为空！
	 */
	public static final String FIELD_NO_EMPTY = "hap.validation.notempty";

	/**
	 * 导入报错信息描述维护 -- 序列号异常!请检查序列号设置!
	 */
	public static final String SERIAL_NUMBER_ERROR = "fnd.import.serial_number_error";

	/**
	 * 导入报错信息描述维护 -- 上传出错!
	 */
	public static final String UPLOAD_ERROR = "fnd.import.upload_error";

	/**
	 * 导入报错信息描述维护 -- 导入错误，错误个数为:
	 */
	public static final String EXIST_ERROR = "fnd.import.existerror";

	/**
	 * 导入报错信息描述维护 -- 请选择上传文件
	 */
	public static final String NO_FILE = "fnd.import.no_file";

	/**
	 * 导入报错信息描述维护 -- 标题为空或空列中有多余数据
	 */
	public static final String ERROR_TITLE = "fnd.import.error_title";

	/**
	 * 导入字段异常提示 -- {0}错误
	 */
	public static final String ERROR_ATTR = "fnd.import.error_attr";

	/**
	 * 导入字段异常提示 -- {0}不存在
	 */
	public static final String NO_EXIST_ATTR = "fnd.import.no_exist_attr";

	/**
	 * 导入字段异常提示 -- {0}不存在或者超过一条同名称数据
	 */
	public static final String NO_EXIST_ATTR_OR_MORE_THAN_ONE = "fnd.import.no_exist_attr_or_more_than_one";

	/**
	 * 导入字段异常提示 -- {0}未上传
	 */
	public static final String NO_UPLOAD_ATTR = "fnd.import.no_upload_attr";

	/**
	 * 导入标题异常提示 -- 标题{0}重复或异常
	 */
	public static final String TITLE_EXCEPTION = "fnd.import.title_exception";

	/**
	 * 导入标题异常提示 -- 标题{0}错误，此标题为导出错误信息标题
	 */
	public static final String TEMPLATE_EXCEPTION = "fnd.import.template_exception";

	/**
	 * 导入报错信息描述维护 -- 该批次已经导入！
	 */
	public static final String BATCH_REPEAT = "fnd.import.batch_repeat";

	/**
	 * 导入报错信息描述维护 -- 上传文件为空或未上传文件
	 */
	public static final String DATA_EMPTY = "fnd.import.data_empty";

	/**
	 * 导入报错信息描述维护 -- 出现异常，导入失败！
	 */
	public static final String IMPORT_EXCEPTION = "fnd.import.exception";

	/**
	 * 导入字段标题描述维护 -- 错误信息
	 */
	public static final String ERROR_MSG = "fnd.import.error_message";

	/**
	 * 导入使用的流水号编码 IDS -- import_data_sequence
	 */
	public static final String IMPORT_SEQUENCE_CODE = "FND_IMPORT_DATA";

	/**
	 * 快码大类 原纸对应的 VALUE
	 */
	public static final String PAPER = "PAPER";

	/**
	 * 快码中类 平张纸对应的 VALUE
	 */
	public static final String FLAT = "FLAT";

	/**
	 * 快码中类 卷筒纸对应的 VALUE
	 */
	public static final String DRUM = "DRUM";
	
	/**
	 * attribute
	 */
	public static final String ATTRIBUTE = "attribute";
	
	/**
	 * 导入删除标识
	 */
	public static final String DELETE_FLAG = "DEL";
	
	/**
	 * 服务器繁忙
	 */
	public static final String SERVER_BUSY = "fnd.import.server_busy";
	
	/**
	 * 描述维护 -- 客户编码
	 */
	public static final String CUSTOMER_NUMBER = "om.sale_order_header.customer_number";
	
	/**
	 * 描述维护 -- 客户名称
	 */
	public static final String CUSTOMER_NAME = "md.item.customer_name";
	
	/**
	 * 描述维护 -- 地址简称
	 */
	public static final String ADDRESS_CODE = "md.contact.address_code";
	
	/**
	 * 描述维护 -- 供应商名称
	 */
	public static final String SUPPLIER_NAME = "md.sup.supplier_name";
	
	/**
	 * 描述维护 -- 比例%
	 */
	public static final String RATIO = "md.sup.allot.ratio";
	
	/**
	 * 导入报错信息描述维护 -- 比例必须为0-100之间的正整数
	 */
	public static final String RATIO_ERROR = "md.sup.allot.ratio_error";
	
	/**
	 * 导入报错信息描述维护 -- 客户编码客户名称以及联系人未能匹配到符合数据
	 */
	public static final String CUST_DATA_MISMATCH = "md.customer.data_mismatch";
	
	/**
	 * 导入报错信息描述维护 -- 该比例ID已存在
	 */
	public static final String RATIO_EXIST = "md.supplier.ratio_exist";
	
	/**
	 * 导入报错信息描述维护 -- 下供应商比例总和不为100%
	 */
	public static final String NOT_EQ_HUNDRED = "md.supplier.not_eq_hundred";


	/**
	 * 客户快码
	 */

	public static final String SYS_YES_NO = "SYS.YES_NO";									//是否

	public static final String PUB_NATION = "PUB.NATION";									//国家

	public static final String PUB_PROVINCE = "PUB.PROVICE";								//省份

	public static final String PUB_CITY = "PUB.CITY";										//城市

	public static final String HR_EMPLOYEE_GENDER = "HR.EMPLOYEE_GENDER";					//性别

	public static final String CTM_CERTIFICATE_TYPE = "CTM.CERTIFICATE_TYPE";				//其它证件（比如护照）

	public static final String CTM_MARITAL_STATUS = "CTM.MARITAL_STATUS";					//婚姻状况

	public static final String CTM_DIPLOMA_TYPE = "PUB.EDUCATION";						//教育程度




}
