package clb.core.supplier.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.service.ISpeCollectionTermsService;

/**
 * @name SpeArchivesController
 * @description 收款条件控制器层
 * @author bo.wu@hand-china.com 2017年4月24日20:56:14
 * @version 1.0 
 */
@Controller
@RequestMapping("/supplier/collectionterms")
public class SpeCollectionTermsController extends BaseController{

    
    @Autowired
    private ISpeCollectionTermsService speCollectionTermsService;

    /**
     *@description 基础查询方法 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeCollectionTerms dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
    	List<SpeCollectionTerms> data = speCollectionTermsService.selectData(dto, page, pageSize);
    	return new ResponseData(data);
    }
    
    /**
     *@description 查询所有数据 
     */
    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResponseData getAll() {
        return new ResponseData(speCollectionTermsService.selectData(null,0,0));
    }

    /**
     *@description 查询所有数据
     */
    @RequestMapping(value = "/queryAll")
    @ResponseBody
    public ResponseData queryAll(SpeCollectionTerms dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,HttpServletRequest request) {
        List<SpeCollectionTerms> data = speCollectionTermsService.selectAllData(dto);
        return new ResponseData(data);
    }
    
    /**
     *@description 获取产品分类
     */
    @RequestMapping(value = "/getProductDivision")
    @ResponseBody
    public ResponseData getProductDivision(HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
        return new ResponseData(speCollectionTermsService.getProductDivision(iRequest));
    }

    /**
     *@throws SpeException 
     * @description 提交数据
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SpeCollectionTerms> dtos,BindingResult result) throws SpeException{
    	getValidator().validate(dtos,result);
        if (result.hasErrors()) {
            ResponseData rs = new ResponseData(false);
            rs.setMessage(getErrorMessage(result, request));
            return rs;
        }
    	IRequest requestCtx = createRequestContext(request);
        return new ResponseData(speCollectionTermsService.collectionTermsBatchUpdate(requestCtx, dtos));
    }

    /**
     *@description 删除数据 
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeCollectionTerms> dto){
    	speCollectionTermsService.batchDelete(dto);
        return new ResponseData();
    }
    
    
}