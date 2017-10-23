package clb.core.sys.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;

import clb.core.common.utils.ShortUrlUtil;
import clb.core.sys.dto.SysUrlGenerate;
import clb.core.sys.mapper.SysUrlGenerateMapper;
import clb.core.sys.service.ISysUrlGenerateService;

@RestController
public class SysUrlGenerateController extends ClbBaseController {

	private static Logger logger = LoggerFactory.getLogger(SysUrlGenerateController.class);

	@Autowired
	private ISysUrlGenerateService sysUrlGenerateService;
	@Autowired
	private SysUrlGenerateMapper sysUrlGenerateMapper;
	@Autowired
	private IProfileService profileService;

	@RequestMapping(value = "/api/public/redirect/{shortUrl}", method = RequestMethod.GET)  
	public void redirectUrl(HttpServletRequest request, @PathVariable("shortUrl") String shortUrl,HttpServletResponse response) throws IOException{
		IRequest iRequest=createRequestContext(request);
		SysUrlGenerate sysUrlGenerate=new SysUrlGenerate();
		sysUrlGenerate.setShortUrl(shortUrl);
		List<SysUrlGenerate>list= sysUrlGenerateMapper.select(sysUrlGenerate);
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		if(!CollectionUtils.isEmpty(list)){
			sysUrlGenerate=list.get(0);
			Long loseTime=Long.valueOf(profileService.getProfileValue(iRequest, "URL_LOSE_TIME"));
			Date createTIme=sysUrlGenerate.getCreationDate();
			if(createTIme.getTime()>(System.currentTimeMillis()-loseTime*60000)){
				out.print("<script>location.href='"+list.get(0).getLongUrl()+"';</script>");
			}else{
				out.print("地址已失效!");
			}
		}
		out.close();
	}

	@RequestMapping(value = "/api/public/generateShortUrl", method = RequestMethod.POST, produces = "application/json;charset=utf8")
	@ResponseBody
	public ResponseData getCommonData(HttpServletRequest request, @RequestBody Map<String, String> param)
			throws JsonProcessingException {
		ResponseData responseData = new ResponseData(true);
		IRequest irequest = createRequestContext(request);
		String shortUlr = ShortUrlUtil.ShortCode(param.get("longUrl"))[0];
		SysUrlGenerate sysUrlGenerate = new SysUrlGenerate();
		sysUrlGenerate.setShortUrl(shortUlr);
		List<SysUrlGenerate> list = sysUrlGenerateMapper.select(sysUrlGenerate);
		if (CollectionUtils.isEmpty(list)) {
			// 如果没有，则插入新记录
			sysUrlGenerate.setLongUrl(param.get("longUrl"));
			sysUrlGenerateService.insertSelective(irequest, sysUrlGenerate);
		}

		String url = profileService.getProfileValue(irequest, "APP_URL");
		url += "/api/public/redirect/" + shortUlr;
		responseData.setMessage(url);
		return responseData;
	}
}
