/*
 * #{copyright}#
 */
package clb.core.core;

/**
 * 常量基类.
 * 
 * @author wanjun.feng
 */
public interface ClbBaseConstants {

    /**
     * 基本常量 - 是 标记.
     */
    String YES = "Y";

    /**
     * 基本常量 - 否 标记.
     */
    String NO = "N";

    String SYSTEM_MAX_DATE = "9999/12/31 23:59:59";

    String SYSTEM_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 缓存ID
     */
    String CACHE_RESOURCE_URL = "resource_url";
    String CACHE_RESOURCE_ID = "resource_id";

    String ROLE_RESOURCE_CACHE = "role_resource";

    /**
     * 默认语言.
     */
    String DEFAULT_LANG = "zh_CN";

    String PREFERENCE_TIME_ZONE = "timeZone";
    
    String PREFERENCE_LOCALE = "locale";
    
    String PREFERENCE_LOCALE2 = "locale2";
    
    String PREFERENCE_THEME = "theme";

    /**
     * SEQUENCE for oracle.<br>
     * JDBC for mysql<br>
     * IDENTITY for config
     */
    String GENERATOR_TYPE = "IDENTITY";

    String LIKE = "LIKE";
    
    /**XMap 中属性值类型  **/
    String XML_DATA_TYPE_FUNCTION = "fn:";
    String XML_DATA_TYPE_BOOLEAN = "boolean:";
    String XML_DATA_TYPE_INTEGER = "integer:";
    String XML_DATA_TYPE_LONG = "long:";
    String XML_DATA_TYPE_FLOAT = "float:";
    String XML_DATA_TYPE_DOUBLE = "double:";
    String XML_DATA_TYPE_DATE = "date:";
}
