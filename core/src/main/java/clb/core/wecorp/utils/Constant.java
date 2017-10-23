package clb.core.wecorp.utils;

/**
 * Created by shanhd on 2016/9/27.
 */
public final class Constant {
    /*验证码类型以及有效时长*/
    public static final String REGISTER_TYPE="register";
    public static final String RESET_TYPE="reset";
    public static final String REPLACE_TYPE="replace";
    public static final String BIND_USER_GAS="bindUserGas";
    public static final int VALID_TIME=10; //单位分钟

    /*用户状态*/
    public static final String USER_ACTIVE="active";
    public static final String USER_INVALID="invalid";

    /*附件类型*/
    public static final String ATTACHE_IMAGE="IMAGE";
    public static final String IMG_TYPE="jpeg,jpg,png,gif,bmp";


    /*未分组*/
    public static final String UN_GROUP_ID="unGroup";
    public static final String UN_GROUP_NAME="未分组";

    /*保存图文操作类型*/
    public static final String ADD_SAVE="add";
    public static final String EDIT_SAVE="edit";

    /*图文消息状态*/
    public static final String ARTICLE_WAIT_SEND="wait_send";  //未发送
    public static final String ARTICLE_SENT="sent"; //已发送

    /*微信素材用途类型*/
    public static final String MATERIAL_NEWS_COVER="NEWS_COVER";          //图文封面
    public static final String MATERIAL_NEWS_CONTENT="NEWS_CONTENT";      //图文正文
    public static final String MATERIAL_MASS_MESSAGE="MASS_MESSAGE";    //群发消息

    /*微信素材类型*/
    public static final String MATERIAL_TYPE_IMAGE="image";          //图片
    public static final String MATERIAL_TYPE_NEWS="news";       //图文

    /*AES密钥*/
    public static final String AES_KEY="0102030405060708";
    public static final String AES_VI="0102030405060708";

    /*燃气表类型*/
    public static final String METER_TYPE_C="C"; //卡表用户
    public static final String METER_TYPE_R="R"; //普表用户


    /*订单状态*/
    public static final String ORDER_STATE_WAIT_PAY="WAIT_PAY";     //'等待付款'
    public static final String ORDER_STATE_PAY_FINISH="PAY_FINISH"; //'已付款'
    public static final String ORDER_STATE_DELIVERED="DELIVERED";   //'已发货'
    public static final String ORDER_STATE_RECEIVING="RECEIVING";   //'已收货'
    public static final String ORDER_STATE_EVALUATE="EVALUATE";     //'已评价'
    public static final String ORDER_STATE_CANCELED="CANCELED";     //已取消
    public static final String ORDER_STATE_RETURNING="RETURNING";   //'退货中'
    public static final String ORDER_STATE_RETURNED="RETURNED";     //'已退货'
    public static final String ORDER_STATE_COLSED="COLSED";         //'已关闭'

    /*商户类型*/
    public static final String MCH_TYPE_COMMERCE="COMMERCE"; //电商
    public static final String MCH_TYPE_RECHARGE="RECHARGE"; //充值

    /*支付类型*/
    public static final String PAY_TYPE_WX="WX"; //微信支付
    public static final String PAY_TYPE_ZFB="ZFB"; //支付宝支付

    /*交易类型*/
    public static final String TRANSACTION_TYPE_PAY="pay"; //付款
    public static final String TRANSACTION_TYPE_REFUND="refund"; //退款

    /*支付业务类型*/
    public static final String BUSINESS_TYPE_COMMODITY="commodity"; //商品支付
    public static final String BUSINESS_TYPE_NFC="nfc"; //NFC充值
    public static final String BUSINESS_TYPE_PAY="pay"; //缴费


    /*售货服务状态*/
    public static final String AFTER_SERVICE_STATE_PROCESSING="processing"; //处理中
    public static final String AFTER_SERVICE_STATE_PROCESSED="processed"; //已处理
    public static final String AFTER_SERVICE_STATE_COLSED="closed"; //已关闭

    /*售后服务类型：退款,退货,换货*/
    public static final String AFTER_SERVICE_TYPE_REFUND="refund"; //退款
    public static final String AFTER_SERVICE_TYPE_RETURN="return"; //退货
    public static final String AFTER_SERVICE_TYPE_BARTER="barter"; //换货

    //发票类型
    public static final String INVOICE_TYPE_COMPANY="company"; //公司
    public static final String INVOICE_TYPE_PERSON="person"; //个人


    public static final String SERIAL_NUMBER="序列号";

    //app主页
    public static final String HOME_PAGE_TYPE_NEWS="news"; //头条新闻
    public static final String HOME_PAGE_TYPE_GAS="gas";   //智慧燃气
    public static final String HOME_PAGE_TYPE_MALL_PROMOTION="mallPromotion";  //商城推广图片
}
