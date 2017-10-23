package clb.core.common.controllers;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.security.TokenUtils;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @name CommonProfileController
 * @description 配置文件查询
 * @author jiaolong.li@hand-china.com 2017年7月21日11:19:31
 * @version 1.0 
 */
@Controller
@RequestMapping("/commons/profile")
public class CommonProfileController extends BaseController{

	@Autowired
	private IProfileService profileService;

	/**
	 *  查询配置文件值
	 * @param profileCode
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryProfileValue")
	@ResponseBody
	public String queryProfileValue(String profileCode, HttpServletRequest request) {
		IRequest requestCtx = createRequestContext(request);
		String profileValue = profileService.getProfileValue(requestCtx, profileCode);
		if ("".equals(profileValue)) {
			profileValue = "N";
		}
		return profileValue;
	}
}