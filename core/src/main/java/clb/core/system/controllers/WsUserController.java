package clb.core.system.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.app.dto.AppUser;
import clb.core.app.service.IAppUserService;
import clb.core.channel.dto.CnlAppChannel;
import clb.core.channel.service.ICnlChannelService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.service.IRole;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.utils.CommonUtil;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import net.sf.json.JSONObject;

@Controller
public class WsUserController extends ClbBaseController {
    private static Logger logger = LoggerFactory.getLogger(WsUserController.class);

    @Autowired
    private IClbUserService clbUserService;
    @Autowired
    private IProfileService profileService;
    @Autowired
    private PasswordManager passwordManager;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ISysSendSMSService sysSendSMSService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ICnlChannelService cnlChannelService;
    @Autowired
    private CnlChannelMapper cnlChannelMapper;

    @Autowired
    private IAppUserService appUserService;

    @Timed
    @HapInbound(apiName = "ClbSendVerifiCode")
    @RequestMapping(value = "/api/public/sendVerifiCode", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public JSONObject sendVerifiCode(HttpServletRequest request, @RequestBody ClbUser user, HttpServletResponse response) {
        String phone = user.getPhone();
        IRequest requestContext = createRequestContext(request);
        String verifiCode = CommonUtil.ramdomCode(6);
        String content = "您好，您的短信验证码为[" + verifiCode + "]";
        logger.info(content);
        List<String> numList = new ArrayList<String>();
        if(StringUtils.isNotBlank(user.getPhoneCode())){
            if(user.getPhoneCode().equals("86") || user.getPhoneCode().equals("+86")){
                numList.add(phone);
            }else{
                numList.add(user.getPhoneCode().replace("+", "")+phone);
            }
        }else{
            numList.add(phone);
        }

        JSONObject json = sysSendSMSService.sendSMSDirect(requestContext, content, numList, null, "YZX");
        if (json.getBoolean("success")) {
            //HttpSession session = request.getSession();
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            json.put("sessionId", key);
            //session保存到redis中
            JSONObject sessionBean = new JSONObject();
            sessionBean.put("verifiCode", verifiCode);
            sessionBean.put("phone", phone);
            saveSessionRedis(phone + "_" + key, sessionBean);
        }
        return json;
    }

    @Timed
    @HapInbound(apiName = "ClbRegest")
    @RequestMapping(value = "/api/public/user/regest", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public JSONObject regest(HttpServletRequest request, HttpServletResponse htttpresponse, @RequestBody CnlChannel channel) {
        ClbUser dto = channel.getUser();
        IRequest requestContext = createRequestContext(request);
        JSONObject response = new JSONObject();

        response.put("success", true);
        String verifiCode = dto.getVerifiCode();
        JSONObject sessionBean = getSessionBean(dto.getPhone() + "_" + request.getParameter("sessionId"));
        if (sessionBean == null) {
            response.put("success", false);
            response.put("message", "请重新获取验证码!");
            return response;
        }
        Object oldCode = sessionBean.get("verifiCode");
        if (oldCode == null) {
            response.put("success", false);
            response.put("message", "请先发送短信验证码!");
            return response;
        } else {
            if (verifiCode == null) {
                response.put("success", false);
                response.put("message", "请输入短信验证码!");
                return response;
            } else {
                if (!verifiCode.equals(oldCode)) {
                    response.put("success", false);
                    response.put("message", "短信验证码错误!");
                    return response;
                }
            }
        }

        if (!dto.getPassword().equals(dto.getRepPassword())) {
            response.put("success", false);
            response.put("message", "密码与确认密码不一致");
            return response;
        }
        dto.setPasswordEncrypted(passwordManager.encode(dto.getPassword()));
        dto.setStatus("ACTV");
        dto.setStartActiveDate(new Date());

        try {
            //保存用户
            ResponseData responseData=clbUserService.channelUserRegest(requestContext, dto, channel,"REGISTER_SUCCESS");
            if(!responseData.isSuccess()){
                response.put("success", false);
                response.put("message", responseData.getMessage());
                return response;
            }
        } catch (Exception e) {
            CommonUtil.printStackTraceToStr(e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }

        ClbUser clbUser = new ClbUser();
        clbUser.setUserId(dto.getUserId());
        List<ClbUser> clbUserList = clbUserService.selectAllFields(createRequestContext(request), clbUser, 1, 10);
        //删除原session
//        deleteSessionRedis(dto.getPhone() + "_" + request.getParameter("sessionId"));
        //session保存到redis中
        JSONObject json = savaUserSession(request.getParameter("sessionId"), clbUserList.get(0));

        response.put("code", request.getParameter("sessionId"));
        response.put("sessionId", request.getParameter("sessionId"));
        response.put("user", json.toString());
        return response;
    }

    @Timed
    @HapInbound(apiName = "ClbAppUserRegister")
    @RequestMapping(value = "/api/public/user/appUserRegister", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public JSONObject appUserRegister(HttpServletRequest request,
                                      HttpServletResponse htttpresponse,
                                      @RequestBody CnlAppChannel appChannel) {
        IRequest requestContext = createRequestContext(request);
        JSONObject response = new JSONObject();

        // 验证
        if (!StringUtils.isNotBlank(appChannel.getPhoneNum()) || !StringUtils.isNotBlank(appChannel.getPhoneCode()) ) {
            response.put("success", false);
            response.put("message", "手机号及区号不能为空");
            return response;
        }
        if (!StringUtils.isNotBlank(appChannel.getNickName()) ) {
            response.put("success", false);
            response.put("message", "昵称不能为空");
            return response;
        }
        if (!StringUtils.isNotBlank(appChannel.getPassword()) ) {
            response.put("success", false);
            response.put("message", "密码不能为空");
            return response;
        }
        if (!StringUtils.isNotBlank(appChannel.getAppUserId()) ) {
            response.put("success", false);
            response.put("message", "APP用户ID不能为空");
            return response;
        }

        CnlChannel channel = new CnlChannel();
        channel.setPhoneCode(appChannel.getPhoneCode());
        channel.setContactPhone(appChannel.getPhoneNum());
        List<CnlChannel> channelList = new ArrayList<CnlChannel>();
        channelList = cnlChannelService.queryChannelSimple(requestContext, channel);
        if (channelList != null && channelList.size() > 0) {
            channel = channelList.get(0);
            // 查询关联的用户id
            ClbUser user = new ClbUser();
            user.setUserType("CHANNEL");
            user.setRelatedPartyId(channel.getChannelId());
            List<ClbUser> userList = new ArrayList<ClbUser>();
            userList = clbUserService.selectAllFields(requestContext, user, 1, 10);
            if (userList != null && userList.size() > 0) {
                response.put("userName", userList.get(0).getUserName());
                response.put("userId", userList.get(0).getUserId());
                response.put("channelName", channel.getChannelName());
                response.put("channelId", channel.getChannelId());
                try {
                    AppUser appUser3 = new AppUser();
                    appUser3.setAppUserId(Long.valueOf(appChannel.getAppUserId()));
                    appUser3.setAppUsername(appChannel.getNickName());
                    appUser3.setAppPassword(appChannel.getPassword());
                    appUser3.setClbUserId(userList.get(0).getUserId());
                    AppUser appUser1 = appUserService.selectAppUser(requestContext,appUser3);
                    if(appUser1 == null){
                        appUserService.saveAppUser(requestContext, Long.valueOf(appChannel.getAppUserId()), userList.get(0).getUserId(), appChannel.getNickName(), appChannel.getPassword());
                    }
                    } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }
        } else {
            // 没有渠道及用户然后创建
            ClbUser clbUser = new ClbUser();
            clbUser.setPasswordEncrypted(passwordManager.encode(appChannel.getPassword()));
            clbUser.setStatus("ACTV");
            clbUser.setStartActiveDate(new Date());
            clbUser.setUserName(appChannel.getPhoneNum());
            clbUser.setPhoneCode(appChannel.getPhoneCode());
            clbUser.setPhone(appChannel.getPhoneNum());
            clbUser.setUserType("CHANNEL");
            clbUser.setAppUserId(appChannel.getAppUserId());
            channel.setChannelName(appChannel.getNickName());
            channel.setChannelSource("APP");
            try {
                //保存用户
                ResponseData responseData = clbUserService.channelUserRegest(requestContext, clbUser,
                        channel, "");
                if(!responseData.isSuccess()){
                    response.put("success", false);
                    response.put("message", responseData.getMessage());
                    return response;
                }
            } catch (Exception e) {
                CommonUtil.printStackTraceToStr(e);
                response.put("success", false);
                response.put("message", e.getMessage());
                return response;
            }
        }

        ClbUser resultUser = new ClbUser();
        resultUser.setUserName(appChannel.getPhoneNum());
        List<ClbUser> clbUserList = clbUserService.selectAllFields(createRequestContext(request), resultUser, 1, 10);
        if (clbUserList != null && clbUserList.size() > 0) {
            resultUser = clbUserList.get(0);
            response.put("userId", resultUser.getUserId());
            response.put("userName", resultUser.getUserName());
            response.put("channelId", channel.getChannelId());
            response.put("channelName", appChannel.getNickName());
            try {
                appUserService.saveAppUser(requestContext, Long.valueOf(appChannel.getAppUserId()),resultUser.getUserId(), appChannel.getNickName(), appChannel.getPassword());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        return response;
    }

    @Timed
    @HapInbound(apiName = "ClbAddChannelUser")
    @RequestMapping(value = "/api/user/addChannelUser", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public JSONObject addChannelUser(HttpServletRequest request, @RequestBody CnlChannel channel) {
        IRequest requestContext = createRequestContext(request);
        ClbUser dto = channel.getUser();
        JSONObject response = new JSONObject();
        response.put("success", true);
        dto.setPasswordEncrypted(passwordManager.encode(dto.getPassword()));
        dto.setStartActiveDate(new Date());
        try {
            if (channel.getChannelId() != null) {
                ResponseData responseData = clbUserService.addChannelContract(requestContext, channel);
                if(!responseData.isSuccess()){
                    response.put("success", false);
                    response.put("message", "注册异常:" + responseData.getMessage());
                }
            } else {
                ResponseData responseData = clbUserService.channelUserRegest(requestContext, dto, channel, "CJZH");
                if(!responseData.isSuccess()){
                    response.put("success", false);
                    response.put("message", "注册异常:" + responseData.getMessage());
                }
            }

        } catch (Exception e) {
            CommonUtil.printStackTraceToStr(e);
            response.put("success", false);
            response.put("message", "系统异常:" + e.getMessage());
            return response;
        }
        return response;
    }

    @Timed
    @HapInbound(apiName = "ClbLogin")
    @RequestMapping(value = "/api/public/user/login", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public JSONObject login(HttpServletRequest request, @RequestBody User user, HttpServletResponse httpresponse) {
        JSONObject response = new JSONObject();
        try {
            response.put("success", true);
            if (user == null || StringUtils.isAnyBlank(user.getUserName(), user.getPassword())) {
                response.put("success", false);
                response.put("message", "用户名/密码不能为空!");
                return response;
            }
            User user1 = userMapper.selectByUserName(user.getUserName());
            if (user1 == null) {
                response.put("success", false);
                response.put("message", "用户 不存在!");
                return response;
            }
            if (User.STATUS_LOCK.equals(user1.getStatus())) {
                response.put("success", false);
                response.put("message", "用户  已被锁!");
                return response;
            }
            if (user1.getStartActiveDate() != null && (user1.getStartActiveDate().getTime()-3600*1000) > System.currentTimeMillis()) {
                response.put("success", false);
                response.put("message", "用户  还没到开始有效期!");
                return response;
            }
            if (user1.getEndActiveDate() != null && user1.getEndActiveDate().getTime() < (System.currentTimeMillis()-3600*1000)) {
                response.put("success", false);
                response.put("message", "用户  已过期!");
                return response;
            }
            if (!passwordManager.matches(user.getPassword(), user1.getPasswordEncrypted())) {
                response.put("success", false);
                response.put("message", "用户  密码错误!");
                return response;
            }

            if(user.getAttribute1()!=null && user.getAttribute1().equals("back")){
                List<IRole> roles = roleService.selectRolesByUser(RequestHelper.createServiceRequest(request),
                        user1);
                if (CollectionUtils.isEmpty(roles)) {
                    response.put("success", false);
                    response.put("message", "用户无该系统角色，请联系管理员");
                    return response;
                } else {
//				    response.put("toUrl", profileService.getProfileValue(createRequestContext(request), "APP_URL"));
//					String message="<form id=\"backForm\" method=\"post\" action=\"http://localhost:8080/core/api/public/user/sso\">"

                    String message="<form id=\"backForm\" method=\"post\" action=\""+profileService.getProfileValue(createRequestContext(request), "APP_URL")+"/login\">"
                            + "<input type=\"hidden\" name=\"username\" value=\""+user.getUserName()+"\">"
                            + "<input type=\"hidden\" name=\"password\" value=\""+user.getPassword()+"\">"
                            + "<input type=\"submit\" value=\"立即登录\" style=\"display:none\" ></form>"
                            + "<script>document.getElementById(\"backForm\").submit();</script>";
                    response.put("message", message);
                }
            }
            ClbUser clbUser = new ClbUser();
            clbUser.setUserId(user1.getUserId());
            List<ClbUser> clbUserList = clbUserService.selectAllFields(createRequestContext(request), clbUser, 1, 10);
            //    	HttpSession session=request.getSession(true);
            //保存session redis
            String key = UUID.randomUUID().toString().replaceAll("-", "");
            if(clbUser.getUserType()!=null && clbUser.getUserType().equals("CHANNEL") && clbUser.getRelatedPartyId()!=null){
                CnlChannel channel=cnlChannelMapper.selectByPrimaryKey(clbUser.getRelatedPartyId());
                if(channel==null){
                    response.put("success", false);
                    response.put("message", "该渠道用户关联的渠道不存在/被删除!");
                    return response;
                }
            }
            JSONObject json = savaUserSession(key, clbUserList.get(0));
            response.put("code", key);
            response.put("sessionId", key);
            response.put("user", json.toString());

        } catch (Exception e) {
            CommonUtil.printStackTraceToStr(e);
            response.put("success", false);
            response.put("message", CommonUtil.retStackTraceToStr(e));
        }
        return response;
    }

    @Timed
    @HapInbound(apiName = "ClbModifyPassword")
    @RequestMapping(value = "/api/user/modifyPassword", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData modifyPassword(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        if (!user.getPassword().equals(user.getRepPassword())) {
            response.setSuccess(false);
            response.setMessage("密码与确认密码不一致!");
            return response;
        }
        User user1 = userMapper.selectByUserName(user.getUserName());
        if (user1 == null) {
            response.setSuccess(false);
            response.setMessage("用户 不存在!");
            return response;
        }
        if(StringUtils.isNotBlank(user.getPrePassword()) && !passwordManager.encode(user.getPrePassword()).equals(user1.getPasswordEncrypted())){
            response.setSuccess(false);
            response.setMessage("原密码错误!");
            return response;
        }
        user1.setPasswordEncrypted(passwordManager.encode(user.getPassword()));
        userMapper.updateByPrimaryKeySelective(user1);
        return response;
    }

    @Timed
    @HapInbound(apiName = "ClbUserQuery")
    @RequestMapping(value = "/api/user/queryUserInfo", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData queryUserInfo(@RequestBody ClbUser dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //return new ResponseData(service.select(requestContext, dto, page, pageSize));
        return new ResponseData(clbUserService.selectAllFields(requestContext, dto, page, pageSize));
    }

    @Timed
    @HapInbound(apiName = "ClbModifyPasswordById")
    @RequestMapping(value = "/api/user/modifyPasswordById", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData modifyPasswordById(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        if (!user.getPassword().equals(user.getRepPassword())) {
            response.setSuccess(false);
            response.setMessage("密码与确认密码不一致!");
            return response;
        }
        User user1 = userMapper.selectByPrimaryKey(user);
        if (user1 == null) {
            response.setSuccess(false);
            response.setMessage("用户 不存在!");
            return response;
        }
        if(StringUtils.isNotBlank(user.getPrePassword()) && !passwordManager.matches(user.getPrePassword(), user1.getPasswordEncrypted())){
            response.setSuccess(false);
            response.setMessage("原密码错误!");
            return response;
        }
        user1.setPasswordEncrypted(passwordManager.encode(user.getPassword()));
        userMapper.updateByPrimaryKeySelective(user1);
        return response;
    }
    @Timed
    @HapInbound(apiName = "忘记密码")
    @RequestMapping(value = "/api/public/user/forgetPassword", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData forgetPassword(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        String verifiCode = user.getVerifiCode();
        JSONObject sessionBean = getSessionBean(user.getPhone() + "_" + request.getParameter("sessionId"));
        if (sessionBean == null) {
            response.setSuccess(false);
            response.setMessage("请重新获取验证码!");
            return response;
        }
        String oldCode = sessionBean.getString("verifiCode");
        if (oldCode == null) {
            response.setSuccess(false);
            response.setMessage("请先发送短信验证码!");
            return response;
        } else {
            if (verifiCode == null) {
                response.setSuccess(false);
                response.setMessage("请输入短信验证码!");
                return response;
            } else {
                if (!verifiCode.equals(oldCode)) {
                    response.setSuccess(false);
                    response.setMessage("短信验证码错误!");
                    return response;
                }
            }
        }
        response=clbUserService.forgetPassword(createRequestContext(request), user);
        return response;
    }
    @Timed
    @HapInbound(apiName = "ClbUserActive")
    @RequestMapping(value = "/api/user/active", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData activeUser(@RequestBody ClbUser dto, HttpServletRequest request) {
        ResponseData response = new ResponseData(true);

        User user1 = userMapper.selectByPrimaryKey(dto);
        if (user1 == null) {
            response.setSuccess(false);
            response.setMessage("用户 不存在!");
            return response;
        }

        user1.setStatus("ACTV");
        user1.setStartActiveDate(new Date());
        userMapper.updateByPrimaryKeySelective(user1);
        return response;
    }


    @HapInbound(apiName = "ClbVerifyPhome")
    @RequestMapping(value = "/api/user/verifyPhome", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData verifyPhome(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        String verifiCode = user.getVerifiCode();
        JSONObject sessionBean = getSessionBean(user.getPhone() + "_" + request.getParameter("sessionId"));
        if (sessionBean == null) {
            response.setSuccess(false);
            response.setMessage("请重新获取验证码!");
            return response;
        }
        String oldCode = sessionBean.getString("verifiCode");
        if (oldCode == null) {
            response.setSuccess(false);
            response.setMessage("请先发送短信验证码!");
            return response;
        } else {
            if (verifiCode == null) {
                response.setSuccess(false);
                response.setMessage("请输入短信验证码!");
                return response;
            } else {
                if (!verifiCode.equals(oldCode)) {
                    response.setSuccess(false);
                    response.setMessage("短信验证码错误!");
                    return response;
                }
            }
        }
        IRequest irequest = createRequestContext(request);
        clbUserService.updateByPrimaryKeySelective(irequest, user);
        return response;
    }

    public JSONObject savaUserSession(String key, ClbUser clbUser) {
        //session保存到redis中
        JSONObject sessionBean = new JSONObject();
        sessionBean.put("relatedPartyId", clbUser.getRelatedPartyId());
        sessionBean.put("phone", clbUser.getPhone());
        sessionBean.put("pahoneCode", clbUser.getPhoneCode());
        sessionBean.put("key", key);
        sessionBean.put("userId", clbUser.getUserId());
        sessionBean.put("userName", clbUser.getUserName());
        sessionBean.put("status", clbUser.getStatus());
        sessionBean.put("phone", clbUser.getPhone());
        sessionBean.put("phoneCode", clbUser.getPhoneCode());
        sessionBean.put("userType", clbUser.getUserType());
        sessionBean.put("email", clbUser.getEmail());
        if(clbUser.getUserType()!=null && clbUser.getUserType().equals("CHANNEL") && clbUser.getRelatedPartyId()!=null){
            CnlChannel channel=cnlChannelMapper.selectByPrimaryKey(clbUser.getRelatedPartyId());
            sessionBean.put("channelStatus", channel.getStatusCode());
        }
        saveSessionRedis(key, sessionBean);
        return sessionBean;
    }

    @Timed
    @HapInbound(apiName = "ClbPersonalSetting")
    @RequestMapping(value = "/api/user/personalSetting", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData personalSetting(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);

        if (user.getUserId() == null) {
            response.setSuccess(false);
            response.setMessage("用户ID不能为空!");
            return response;
        }

        IRequest irequest = createRequestContext(request);
        clbUserService.updateByPrimaryKeySelective(irequest, user);
        return response;
    }


    @HapInbound(apiName = "校验验证码")
    @RequestMapping(value = "/api/user/checkVerifyCode", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData checkVerifyCode(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        String verifiCode = user.getVerifiCode();
        JSONObject sessionBean = getSessionBean(user.getPhone() + "_" + request.getParameter("sessionId"));
        if (sessionBean == null) {
            response.setSuccess(false);
            response.setMessage("请重新获取验证码!");
            return response;
        }
        String oldCode = sessionBean.getString("verifiCode");
        if (oldCode == null) {
            response.setSuccess(false);
            response.setMessage("请先发送短信验证码!");
            return response;
        } else {
            if (verifiCode == null) {
                response.setSuccess(false);
                response.setMessage("请输入短信验证码!");
                return response;
            } else {
                if (!verifiCode.equals(oldCode)) {
                    response.setSuccess(false);
                    response.setMessage("短信验证码错误!");
                    return response;
                }
            }
        }
        return response;
    }

    @Timed
    @HapInbound(apiName = "手机号变更")
    @RequestMapping(value = "/api/user/changePhone", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public ResponseData changePhone(HttpServletRequest request, @RequestBody ClbUser user) {
        ResponseData response = new ResponseData(true);
        String verifiCode = user.getVerifiCode();
        JSONObject sessionBean = getSessionBean(user.getPhone() + "_" + request.getParameter("sessionId"));
        if (sessionBean == null) {
            response.setSuccess(false);
            response.setMessage("请重新获取验证码!");
            return response;
        }
        String oldCode = sessionBean.getString("verifiCode");
        if (oldCode == null) {
            response.setSuccess(false);
            response.setMessage("请先发送短信验证码!");
            return response;
        } else {
            if (verifiCode == null) {
                response.setSuccess(false);
                response.setMessage("请输入短信验证码!");
                return response;
            } else {
                if (!verifiCode.equals(oldCode)) {
                    response.setSuccess(false);
                    response.setMessage("短信验证码错误!");
                    return response;
                }
            }
        }
        IRequest irequest = createRequestContext(request);
        response=clbUserService.changePhone(irequest, user);
        return response;
    }

}
