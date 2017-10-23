package clb.core.wecorp.controllers;

import clb.core.notification.dto.NtnTemplateConcern;
import clb.core.notification.service.INtnTemplateConcernService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.wecorp.service.IWecorpAccountService;
import clb.core.wecorp.service.IWecorpCallBackService;
import clb.core.wecorp.service.IWxService;
import clb.core.wecorp.utils.SHA1Util;
import clb.core.whtcustom.service.IWhtCustomMenuService;
import clb.core.whtcustom.service.IWhtCustomReplyService;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.util.JSONAndMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class WecorpCallBackController extends ClbBaseController {

    @Autowired
    private IWecorpAccountService wecorpAccountService;

    @Autowired
    private IWecorpCallBackService wecorpCallBackService;

    @Autowired
    IWhtCustomReplyService whtCustomReplyService;

    @Autowired
    IWhtCustomMenuService whtCustomMenuService;

    @Autowired
    INtnTemplateConcernService ntnTemplateConcernService;

    @Autowired
    IWxService wxService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/api/public/wecorpcallback" , method = RequestMethod.GET)
    @ResponseBody
    public void wxCallBack(HttpServletRequest request,HttpServletResponse response) throws Exception {
        //配置回调接口地址时进行的验证（在接口地址上拼上appid参数）
        WecorpAccount wecorpAccount = wecorpAccountService.getWoaAccountByAppId(request.getParameter("appId"));
        String token = wecorpAccount.getToken();
        String signature = request.getParameter("signature");
        String echostr = request.getParameter("echostr");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String[] str = {token, timestamp, nonce};
        Arrays.sort(str); // 字典序排序
        String bigStr = str[0] + str[1] + str[2];
        // SHA1加密
        String digest = new SHA1Util().getDigestOfString(bigStr.getBytes()).toLowerCase();
        logger.info("token:{},signature:{},echostr:{},nonce:{},digest:{}",token,signature,echostr,nonce,digest);
        // 确认请求来至微信
        if (digest.equals(signature)) {
            response.setContentType("text/txt");
            OutputStream out = response.getOutputStream();
            out.write(echostr.getBytes());
        } else {
            response.setContentType("text/txt");
            OutputStream out = response.getOutputStream();
            out.write("".getBytes());
        }
    }

    @RequestMapping(value = "/api/public/wecorpcallback" , method = RequestMethod.POST)
    @ResponseBody
    public void wxCallBack(HttpServletRequest request,HttpServletResponse response, @RequestBody(required = false) String reqXml) throws Exception {
        String openId="";
        String appId = request.getParameter("appId");
        try {
            logger.info("============: " + reqXml);
            response.setContentType("text/txt");//先回包
            OutputStream out = response.getOutputStream();
            out.write("".getBytes());
            Map map = JSONAndMap.xml2map(reqXml);
            Map sMsgMap = (Map) map.get("xml");
            String msgType = sMsgMap.get("MsgType").toString();
            String openid = sMsgMap.get("FromUserName").toString();
            openId=openid;
            String toUserName = sMsgMap.get("ToUserName").toString();
            logger.info("msgType:{}", msgType);
            logger.info("openid:{}", openid);
            logger.info("toUserName:{}", toUserName);
            if (msgType.equals("event")) {//用户关注时回复
                String event = sMsgMap.get("Event").toString();
                if (event.equals("subscribe")) {
                    //订阅记录存表
                    if(ntnTemplateConcernService.existOpenIdAndAppId(openid,appId)){
                        ntnTemplateConcernService.subscribe(openid,appId);
                    }else{
                        IRequest requestContext = createRequestContext(request);
                        NtnTemplateConcern ntnTemplateConcern=new NtnTemplateConcern();
                        ntnTemplateConcern.setWechatOpenid(openid);
                        ntnTemplateConcern.setBackgroundAppid(appId);
                        ntnTemplateConcern.setWechatConcernType("Y");
                        ntnTemplateConcernService.insertSelective(requestContext,ntnTemplateConcern);
                    }
                    //订阅回复
                    WecorpResponseDTO dto=null;
                    boolean err=false;
                    try {
                        dto = whtCustomReplyService.subscribeReplyMsg(appId);
                    }catch (Exception e){
                        WecorpResponseDTO default_res=new WecorpResponseDTO();
                        default_res.setContent("subscribeReplyMsg:"+e.getMessage());
                        default_res.setType("text");
                        wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                        err=true;
                    }
                    if(!err) {
                        if (dto != null&&dto.getType()!=null) {
                            logger.info(dto.toString());
                            wecorpCallBackService.sendMessageByType(openid, appId, dto);
                        } else {
                            WecorpResponseDTO default_res = new WecorpResponseDTO();
                            default_res.setContent("感谢您的关注");
                            default_res.setType("text");
                            wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                        }
                    }
                } else if (event.equals("CLICK")) {  //自定义菜单事件
                    /*<xml><ToUserName><![CDATA[gh_2ec955eb1aff]]></ToUserName>
                    <FromUserName><![CDATA[opR6rwIkesKsxXij0PeFZnQnwLkE]]></FromUserName>
                    <CreateTime>1500974650</CreateTime>
                    <MsgType><![CDATA[event]]></MsgType>
                    <Event><![CDATA[CLICK]]></Event>
                    <EventKey><![CDATA[ManualCustomerService]]></EventKey>
                    </xml>*/
                    String event_key = sMsgMap.get("EventKey").toString();
                    if ("ManualCustomerService".equals(event_key)) {
                        //创建客服会话
                        wxService.createCommunite(appId, openid);
                    } else if ("ManualCustomerServiceClose".equals(event_key)) {
                        //手动关闭会话
                        wxService.closeCommunite(appId, openid);
                    } else {
                        //点击key事件
                        WecorpResponseDTO dto=null;
                        boolean err=false;
                        try {
                             dto = whtCustomMenuService.menuClick(appId, event_key);
                        }catch (Exception e){
                            WecorpResponseDTO default_res=new WecorpResponseDTO();
                            default_res.setContent("menuClick:"+e.getMessage());
                            default_res.setType("text");
                            wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                            err=true;
                        }
                        if(!err) {
                            if (dto != null&&dto.getType()!=null) {
                                logger.info(dto.toString());
                                wecorpCallBackService.sendMessageByType(openid, appId, dto);
                            } else {
                                WecorpResponseDTO default_res = new WecorpResponseDTO();
                                default_res.setContent("该栏目尚未配置");
                                default_res.setType("text");
                                wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                            }
                        }
                    }
                }
                //客服接入会话
                else if (event.equalsIgnoreCase("kf_create_session")) {
                    /*
                     <xml>
                         <ToUserName><![CDATA[touser]]></ToUserName>
                         <FromUserName><![CDATA[fromuser]]></FromUserName>
                         <CreateTime>1399197672</CreateTime>
                         <MsgType><![CDATA[event]]></MsgType>
                         <Event><![CDATA[kf_create_session]]></Event>
                         <KfAccount><![CDATA[test1@test]]></KfAccount>
                    </xml>
                    * */
                    String kf = (String) sMsgMap.get("KfAccount");
                    wxService.createSuccess(kf, openid, appId);
                }
                //客服结束会话
                else if (event.equalsIgnoreCase("kf_close_session")) {
                    /*
                    <xml>
                     <ToUserName><![CDATA[touser]]></ToUserName>
                     <FromUserName><![CDATA[fromuser]]></FromUserName>
                     <CreateTime>1399197672</CreateTime>
                     <MsgType><![CDATA[event]]></MsgType>
                     <Event><![CDATA[kf_close_session]]></Event>
                     <KfAccount><![CDATA[test1@test]]></KfAccount>
                    </xml>
                   * */
                    String kf = (String) sMsgMap.get("KfAccount");
                    wxService.endSession(kf, openid, appId);
                }
                //客服转接会话
                else if (event.equalsIgnoreCase("kf_switch_session")) {
                    /*
                    <xml>
                     <ToUserName><![CDATA[touser]]></ToUserName>
                     <FromUserName><![CDATA[fromuser]]></FromUserName>
                     <CreateTime>1399197672</CreateTime>
                     <MsgType><![CDATA[event]]></MsgType>
                     <Event><![CDATA[kf_switch_session]]></Event>
                     <FromKfAccount><![CDATA[test1@test]]></FromKfAccount>
                     <ToKfAccount><![CDATA[test2@test]]></ToKfAccount>
                    </xml>
                   * */
                    String fromkf = (String) sMsgMap.get("FromKfAccount");
                    String tokf = sMsgMap.get("ToKfAccount").toString();
                    wxService.transfer(fromkf, tokf, openid, appId);
                } else if (event.equalsIgnoreCase("unsubscribe")) {
                    //取消订阅更新表
                    ntnTemplateConcernService.unsubscribe(openid,appId);
                } else if (event.equalsIgnoreCase("SCAN")) { //扫描带参数的二维码事件
                    //不做处理
                } else if (event.equalsIgnoreCase("scancode_push")) {//自定义菜单发起扫码事件
                    //不做处理
                } else if (event.equalsIgnoreCase("card_pass_check") || event.equalsIgnoreCase("card_not_pass_check")) {//微信卡券事件  卡券审核
                    //不做处理
                } else if (event.equalsIgnoreCase("user_get_card")) {//卡券领取事件
                    //不做处理
                } else if (event.equalsIgnoreCase("user_del_card")) {//卡券删除事件
                    //不做处理
                } else if (event.equalsIgnoreCase("user_consume_card")) {//卡券核销事件
                    //不做处理
                }
            } else if (msgType.equals("text")) {//用户发送消息时回复
                String content = (String) sMsgMap.get("Content");
                if(!wxService.ifCloseKF(appId, openid,content)) {
                    List<WecorpResponseDTO> wecorpResponseDTOs = Collections.emptyList();
                    boolean err = false;
                    try {
                        wecorpResponseDTOs = whtCustomReplyService.keywordkReplyMsg(appId, content);
                    } catch (Exception e) {
                        WecorpResponseDTO default_res = new WecorpResponseDTO();
                        default_res.setContent("keywordkReplyMsg:" + e.getMessage());
                        default_res.setType("text");
                        wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                        err = true;
                    }
                    if (!err) {
                        if (wecorpResponseDTOs.size() > 0) {
                            for (WecorpResponseDTO dto : wecorpResponseDTOs) {
                                logger.info(dto.toString());
                                if (dto.getType() != null) {
                                    wecorpCallBackService.sendMessageByType(openid, appId, dto);
                                }
                            }
                        } else {
                            WecorpResponseDTO default_res = new WecorpResponseDTO();
                            default_res.setContent("关键字暂无匹配");
                            default_res.setType("text");
                            wecorpCallBackService.sendMessageByType(openid, appId, default_res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            WecorpResponseDTO dto = new WecorpResponseDTO();
            dto.setContent("回调错误："+e.getMessage());
            dto.setType("text");
            wecorpCallBackService.sendMessageByType(openId, appId, dto);
        }
    }

}
