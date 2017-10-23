package clb.core.system.controllers;

import clb.core.cache.impl.ClbSysCodeCache;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.cache.Cache;
import com.hand.hap.cache.CacheManager;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.ILanguageProvider;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.DefaultTlTableNameProvider;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.message.components.DefaultPromptListener;
import com.hand.hap.mybatis.entity.EntityField;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.DTOClassInfo;
import com.hand.hap.system.dto.Language;
import com.hand.hap.system.dto.Prompt;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.mapper.MultiLanguageMapper;
import com.hand.hap.system.service.ILovService;
import com.hand.hap.system.service.IProfileService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 通用的 Controller,用来获取公共数据.
 *
 * @author wanjun.feng@hand-china.com
 */
@RestController
public class WsClbCommonController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(WsClbCommonController.class);

    @Autowired
    private ClbCommonController clbCommonController;
    
    @Autowired
    private IClbCodeService clbCodeService;

    /**
     * 获取通用数据.
     *
     * @param resource
     *            资源类型
     * @param params
     *            参数
     * @param request
     *            HttpServletRequest
     * @return json值
     * @throws JsonProcessingException
     *             对象转 JSON 可能出现的异常
     */
    @Timed
    @HapInbound(apiName = "ClbCommonResource")
    @RequestMapping(value = "/api/clb/common/{resource}", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public String getCommonData(@PathVariable String resource, @RequestBody Map<String, String> params,
                                HttpServletRequest request) throws JsonProcessingException {

        return clbCommonController.getCommonJson(resource,params,request);
    }
    
    @Timed
    @HapInbound(apiName = "ClbCommonResource")
    @RequestMapping(value = "/api/clb/common/code", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public List<ClbCodeValue> getCommonData(HttpServletRequest request ,@RequestBody ClbCodeValue dto) throws JsonProcessingException {
        return clbCodeService.selectCodeValuesByParam(dto);
    }

    /**
     * 获取通用数据.
     *
     * @param resource
     *            资源类型
     * @param params
     *            参数
     * @param request
     *            HttpServletRequest
     * @return json值
     * @throws JsonProcessingException
     *             对象转 JSON 可能出现的异常
     */
    @Timed
    @HapInbound(apiName = "ClbCommonResource")
    @RequestMapping(value = "/api/public/common/{resource}", method = RequestMethod.POST, produces = "application/json;charset=utf8")
    @ResponseBody
    public String publicCommonData(@PathVariable String resource, @RequestBody Map<String, String> params,
                                HttpServletRequest request) throws JsonProcessingException {

        return clbCommonController.getCommonJson(resource,params,request);
    }
}