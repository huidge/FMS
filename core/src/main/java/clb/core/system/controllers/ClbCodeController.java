package clb.core.system.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
/**
 * 快速编码Controller.
 * 
 * @author wanjun.feng@hand-china.com
 *
 *         2017年4月17日
 */
@Controller
@RequestMapping("/clb")
public class ClbCodeController extends BaseController {

    @Autowired
    private IClbCodeService clbCodeService;

    /**
     * 获取快速编码对象.
     * 
     * @param code
     *            Code
     * @param page
     *            起始页
     * @param pagesize
     *            分页大小
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/query")
    @ResponseBody
    public ResponseData getCodes(ClbCode code, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(clbCodeService.selectCodes(requestContext, code, page, pagesize));
    }

    /**
     * 查询快速编码值.
     * 
     * @param value
     *            CodeValue
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/codevalue/query")
    @ResponseBody
    public ResponseData getCodeValues(ClbCodeValue value, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(clbCodeService.selectCodeValues(requestContext, value));
    }

    /**
     * 删除快速编码.
     * 
     * @param codes
     *            codes
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/remove", method = RequestMethod.POST)
    public ResponseData removeCodes(@RequestBody List<ClbCode> codes, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        clbCodeService.batchDelete(requestContext, codes);
        return new ResponseData();
    }

    /**
     * 删除快速编码值.
     * 
     * @param values
     *            values
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/codevalue/remove", method = RequestMethod.POST)
    public ResponseData removeValues(@RequestBody List<ClbCodeValue> values, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        clbCodeService.batchDeleteValues(requestContext, values);
        return new ResponseData();
    }

    /**
     * 提交快速编码对象.
     * 
     * @param codes
     *            codes
     * @param result
     *            BindingResult
     * @param request
     *            HttpServletRequest
     * @return ResponseData
     */
    @RequestMapping(value = "/sys/code/submit", method = RequestMethod.POST)
    public ResponseData submitCode(@RequestBody List<ClbCode> codes, BindingResult result, HttpServletRequest request) {
        getValidator().validate(codes, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(clbCodeService.batchUpdate(requestContext, codes));
    }
}
