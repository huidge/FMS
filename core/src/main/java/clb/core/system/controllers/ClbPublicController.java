package clb.core.system.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.service.IProfileService;

@RestController
@RequestMapping("/api/public")
public class ClbPublicController extends BaseController {

	@Autowired
	private IProfileService profileService;
	
	@RequestMapping(value = "/getProFile/{resource}")
    public String getProFile(HttpServletRequest request,@PathVariable String resource) throws JsonProcessingException {
		return profileService.getProfileValue(createRequestContext(request), resource);
	}
	
}
