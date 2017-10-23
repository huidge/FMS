package clb.core.system.controllers;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hap.account.dto.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.codahale.metrics.annotation.Timed;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.security.PasswordManager;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.common.utils.CommonUtil;
import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.mapper.CtmCustomerMapper;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.notification.service.INtnTemplateConcernService;
import clb.core.sys.controllers.ClbBaseController;
import clb.core.sys.service.ISysSendSMSService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;
import net.sf.json.JSONObject;

@Controller
public class WsWxUserController extends ClbBaseController {

	private static Logger logger = LoggerFactory.getLogger(WsWxUserController.class);

	@Autowired
	private IClbUserService clbUserService;

	@Autowired
	private PasswordManager passwordManager;

	@Autowired
	private ISysSendSMSService sysSendSMSService;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ClbUserMapper clbUserMapper;
	@Autowired
	private CnlChannelMapper cnlChannelMapper;
	@Autowired
	private CtmCustomerMapper ctmCustomerMapper;
	@Autowired
	private ICtmCustomerService ctmCustomerService;
	@Autowired
	private WsUserController wsUserController;
	@Autowired
	private INtnTemplateConcernService ntnTemplateConcernService;

	@Timed
	@HapInbound(apiName = "检测微信号是否存在")
	@RequestMapping(value = "/api/public/checkWxAccount", method = RequestMethod.POST, produces = "application/json;charset=utf8")
	@ResponseBody
	public JSONObject checkWxAccount(HttpServletRequest request, @RequestBody ClbUser user) {
		JSONObject response=new JSONObject();
		response.put("success", true);
		IRequest irequest=createRequestContext(request);
		if(StringUtils.isBlank(user.getUserType()) || StringUtils.isBlank(user.getWechatOpenid())){
			response.put("success", false);
			response.put("message", "传入信息不全!");
			return response;
		}
		if(!user.getUserType().equals("CHANNEL") && !user.getUserType().equals("CUSTOMER")){
			response.put("success", false);
			response.put("message", "不支持其他用户类型!");
			return response;
		}

		ClbUser user1=new ClbUser();
		//如果手机号为空，则是检测openID是否绑定
		if(StringUtils.isBlank(user.getPhone())){
			user1=new ClbUser();
			user1.setUserType(user.getUserType());
			user1.setWechatOpenid(user.getWechatOpenid());
			List<ClbUser>userList= clbUserService.selectAll(irequest, user1);
			if(CollectionUtils.isEmpty(userList)){
				response.put("success", false);
				response.put("message", "该微信号未绑定用户!");
				return response;
			}
			user1=userList.get(0);
		}else{
			//检测手机是否绑定
			if(StringUtils.isBlank(user.getPhone())  || StringUtils.isBlank(user.getPhoneCode())){
				response.put("success", false);
				response.put("message", "传入信息不全!");
				return response;
			}else{
				//校验验证码
				response=checkVerify(request, user.getVerifiCode(), user.getPhone());
				if(!response.getBoolean("success")){
					return response;
				}

				user1=new ClbUser();
				user1.setUserType(user.getUserType());
				user1.setPhone(user.getPhone());
				user1.setPhoneCode(user.getPhoneCode());
				List<ClbUser>userList= clbUserService.selectAll(irequest, user1);
				if(CollectionUtils.isEmpty(userList)){
					response.put("success", false);
					response.put("message", "该手机号未注册!");
					return response;
				}
				user1=userList.get(0);
			}
		}
		if(StringUtils.isBlank(user1.getWechatOpenid())){
			ntnTemplateConcernService.bind(irequest ,user.getBackgroundAppid(), user.getWechatOpenid(), user1.getUserId());
		}else{
			if(!user1.getWechatOpenid().equals(user.getWechatOpenid()) && StringUtils.isBlank(user.getAttribute1())){
				response.put("success", false);
				response.put("message", "您的手机号已绑定其他微信号，是否更换绑定？");
				return response;
			}else{
				//更换微信号/未绑定状态 时进入绑定逻辑
				if(!user1.getWechatOpenid().equals(user.getWechatOpenid()) || user1.getWechatBindType()==null || user1.getWechatBindType().equals("N")){
					ntnTemplateConcernService.bind(irequest,user.getBackgroundAppid(), user.getWechatOpenid(), user1.getUserId());
				}
			}
		}

		//返回用户session
		response=saveUserSession(irequest, user1);

		return response;
	}

	public JSONObject saveUserSession(IRequest irequest,ClbUser user){
		JSONObject response=new JSONObject();
		response.put("success", true);
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		JSONObject json = wsUserController.savaUserSession(key, user);
		response.put("sessionId", key);
		response.put("user", json.toString());
		return response;
	}

	@Timed
	@HapInbound(apiName = "客户注册")
	@RequestMapping(value = "/api/public/addCustomer", method = RequestMethod.POST, produces = "application/json;charset=utf8")
	@ResponseBody
	public JSONObject addCustomer(HttpServletRequest request, HttpServletResponse htttpresponse, @RequestBody CtmCustomer dto) {
		IRequest requestContext = createRequestContext(request);
		//校验验证码
		JSONObject response=checkVerify(request, dto.getVerifiCode(), dto.getPhone());
		response.put("code", 1);
		if(!response.getBoolean("success")){
			return response;
		}
		Long customerId=null;
		Long userId=null;
		try {
			Locale locale = RequestContextUtils.getLocale(request);

			ClbUser user=dto.getUser();

			//校验身份证号
			CtmCustomer newCtmCustomer=new CtmCustomer();
			newCtmCustomer.setIdentityNumber(dto.getIdentityNumber());
			List<CtmCustomer>customerList= ctmCustomerMapper.query(newCtmCustomer);
			if(!CollectionUtils.isEmpty(customerList)){
				if(!customerList.get(0).getChineseName().equals(dto.getChineseName())){
					response.put("success", false);
					response.put("message", "您输入的姓名与系统中该身份证号的姓名不匹配");
					return response;
				}
				customerId=customerList.get(0).getCustomerId();
				//把客户ID set 进去
				dto.setCustomerId(customerId);

				//根据客户 ID，查询用户信息
				ClbUser userPhone=new ClbUser();
				userPhone.setRelatedPartyId(customerId);
				userPhone.setUserType("CUSTOMER");
				List<ClbUser>userList=clbUserService.selectAll(requestContext, userPhone);
				if(!CollectionUtils.isEmpty(userList)){
					if( StringUtils.isBlank(user.getAttribute1()) && (!dto.getPhone().equals(userList.get(0).getPhone()) || !dto.getPhoneCode().equals(userList.get(0).getPhoneCode())) ){
						response.put("success", false);
						response.put("message", "该身份证已关联其他手机号，是否替换?");
						response.put("code", 0);
						return response;
					}
					userId= userList.get(0).getUserId();
				}
			}else{
				//根据手机号，查询用户信息
				ClbUser userPhone=new ClbUser();
				userPhone.setPhoneCode(user.getPhoneCode());
				userPhone.setPhone(user.getPhone());
				userPhone.setUserType("CUSTOMER");
				List<ClbUser>userList=clbUserService.selectAll(requestContext, userPhone);
				if(!CollectionUtils.isEmpty(userList)){
					if(userList.get(0).getRelatedPartyId()!=customerId){
						response.put("success", false);
						response.put("message", "该手机号已关联其他用户");
						return response;
					}
				}
			}

			if(userId!=null){
				//重绑定
				ntnTemplateConcernService.resetbind(requestContext,user.getBackgroundAppid(),user.getWechatOpenid(), userId);
				//更新手机号
				newCtmCustomer=new CtmCustomer();
				newCtmCustomer.setCustomerId(customerId);
				newCtmCustomer.setPhone(dto.getPhone());
				newCtmCustomer.setPhoneCode(dto.getPhoneCode());
				newCtmCustomer.setEmail(dto.getEmail());
				ctmCustomerService.updateByPrimaryKeySelective(requestContext, newCtmCustomer);
			}else{
				ResponseData responseData=clbUserService.customerUserRegest(requestContext, user, dto, locale);
				userId=user.getUserId();
				if(!responseData.isSuccess()){
					response.put("success", false);
					response.put("message", responseData.getMessage());
					return response;
				}
			}

		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.put("success", false);
			response.put("message", "系统异常:"+e.getMessage());
			return response;
		}
		//把用户信息存的redis中
		ClbUser user2=new ClbUser();
		user2.setUserId(userId);
		user2=clbUserService.selectAll(requestContext, user2).get(0);
		String key = UUID.randomUUID().toString().replaceAll("-", "");
		JSONObject json = wsUserController.savaUserSession(key, user2);
		response.put("sessionId", key);
		response.put("user", json.toString());
		return response;
	}

	public JSONObject checkVerify(HttpServletRequest request,String verifiCode,String phone){
		JSONObject response = new JSONObject();
		response.put("success", true);
		JSONObject sessionBean = getSessionBean(phone + "_" + request.getParameter("sessionId"));
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
		return response;
	}

	@Timed
	@HapInbound(apiName = "取消绑定")
	@RequestMapping(value = "/api/public/wxUnBind", method = RequestMethod.POST, produces = "application/json;charset=utf8")
	@ResponseBody
	public JSONObject wxUnBind(HttpServletRequest request, @RequestBody ClbUser user) {
		IRequest requestContext = createRequestContext(request);
		JSONObject response = new JSONObject();
		response.put("success", true);
		try {
			ntnTemplateConcernService.unbind(requestContext, user.getWechatOpenid());
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
			response.put("message", e.getMessage());
		}
		return response;
	}

	@Timed
	@HapInbound(apiName = "根据appId查询用户")
	@RequestMapping(value = "/api/public/queryOneUserByAppId", method = RequestMethod.POST, produces = "application/json;charset=utf8")
	@ResponseBody
	public ResponseData queryOneUserByAppId(HttpServletRequest request,Long appId) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(clbUserService.queryOneUserByAppId(appId));
	}

}
