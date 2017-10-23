package clb.core.sys.controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.IRequestListener;
import com.hand.hap.core.impl.DefaultRequestListener;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;

import net.sf.json.JSONObject;

@RestController
public class ClbBaseController extends BaseController{

	private static Logger logger=LoggerFactory.getLogger(ClbBaseController.class);
	
	private static IRequestListener requestListener = new DefaultRequestListener();

	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
	/**
     * 时间参数转换
     * @param binder
     */
    @InitBinder   
    public void initBinder(WebDataBinder binder) {   
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
        dateFormat.setLenient(true);   
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   
    }
    
	public void saveSessionRedis(String key,JSONObject sessionBean){
		HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
		Map<Object,Object> map = new HashMap<Object,Object>();
        map.put(key, sessionBean.toString());
        hash.putAll("sessionBean", map);
        
	}
	
	public void deleteSessionRedis(HttpServletRequest request){
		String key=request.getParameter("sessionId");
		if(StringUtils.isNotBlank(key)){
			HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
			hash.delete(key);
		}
	}
	
	public void deleteSessionRedis(String key){
		if(StringUtils.isNotBlank(key)){
			HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
			hash.delete(key);
		}
	}
	
	public JSONObject getSessionBean(HttpServletRequest request){
		String key=request.getParameter("sessionId");
		if(StringUtils.isBlank(key)){
			return null;
		}
		HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
		Object json1=hash.entries("sessionBean").get(key);
		logger.info("session信息:key="+key+",json:"+json1);
        return json1==null?null:JSONObject.fromObject(json1);
	}
	
	public JSONObject getSessionBean(String key){
		if(StringUtils.isBlank(key)){
			return null;
		}
		HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
		Object json1=hash.entries("sessionBean").get(key);
		logger.info("session信息:key="+key+",json:"+json1);
        return json1==null?null:JSONObject.fromObject(json1);
	}
	
	protected IRequest createRequestContext(HttpServletRequest request) {
			String sessionId = request.getParameter("sessionId");
			IRequest irequest=null;
			if(StringUtils.isNotBlank(sessionId)){
				irequest= createServiceRequest(request, sessionId);
			}else{
				irequest= RequestHelper.createServiceRequest(request);
			}
			if(irequest!=null&& StringUtils.isBlank(irequest.getLocale())){
				irequest.setLocale("zh_CN");
	        }
			return irequest;
	}
	
	public IRequest createServiceRequest(HttpServletRequest httpServletRequest,String sessionId) {
		JSONObject session = getSessionBean(httpServletRequest);
		IRequest requestContext = requestListener.newInstance();
        if (session != null) {
            requestContext.setUserId(session.getLong("userId"));
            requestContext.setAttribute("userName", session.getString("userName"));
            requestContext.setAttribute("userType", session.getString("userType"));
            requestContext.setAttribute("phone", session.get("phone")==null?null:session.getString("phone"));
            if(session.getString("userType")!=null && session.getString("userType").equals("CHANNEL")){
            	requestContext.setAttribute("channelId", session.get("relatedPartyId")==null?null:Long.valueOf(session.getString("relatedPartyId")));
            }
            requestContext.setAttribute("relatedPartyId", session.get("relatedPartyId")==null?null:Long.valueOf(session.getString("relatedPartyId")));
            Locale locale = RequestContextUtils.getLocale(httpServletRequest);
            if (locale != null) {
                requestContext.setLocale(locale.toString());
            }
        }
        Map<String, String> mdcMap = MDC.getCopyOfContextMap();
        if (mdcMap != null) {
            mdcMap.forEach((k, v) -> requestContext.setAttribute(IRequest.MDC_PREFIX.concat(k), v));
        }
        requestListener.afterInitialize(httpServletRequest, requestContext);
        return requestContext;
    }
	
	/**
	 * 获取访问地址
	 */
	public String getIpAddr(HttpServletRequest request) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			return request.getRemoteAddr();
		}
		byte[] ipAddr = addr.getAddress();
		String ipAddrStr = "";
		for (int i = 0; i < ipAddr.length; i++) {
			if (i > 0) {
				ipAddrStr += ".";
			}
			ipAddrStr += ipAddr[i] & 0xFF;
		}
		return ipAddrStr;
	}
}
