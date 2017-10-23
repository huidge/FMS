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

import clb.core.common.exceptions.CommonException;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;

/**
 * @name SpeSupplierController
 * @description 供应商控制器层
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
@Controller
@RequestMapping("/supplier/main")
public class SpeSupplierController extends BaseController{

    @Autowired
    private ISpeSupplierService speSupplierService;

    /**
     *@description 基础查询方法 
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(SpeSupplier dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        List<SpeSupplier> data = speSupplierService.selectData(dto,page,pageSize);
        return new ResponseData(data);
    }
    
    @RequestMapping(value = "/validatequery")
    @ResponseBody
    public ResponseData validatequery(SpeSupplier dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = this.createRequestContext(request);
    	return new ResponseData(speSupplierService.select(iRequest,dto, page,pageSize));
    }
    
    /**
     *@description 查询所有数据 
     */
    @RequestMapping(value = "/getAll")
    @ResponseBody
    public ResponseData query(HttpServletRequest request) {
    	IRequest requestContext = createRequestContext(request);
        return new ResponseData(speSupplierService.selectAll(requestContext));
    }

    /**
     *@throws CommonException 
     * @description 提交数据
     */
    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<SpeSupplier> dtos,BindingResult result) throws CommonException{
    	getValidator().validate(dtos, result);
        if (result.hasErrors()) {
            ResponseData rs = new ResponseData(false);
            rs.setMessage(getErrorMessage(result, request));
            return rs;
        }
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(speSupplierService.supplierBatchUpdate(requestCtx, dtos));
    }

    /**
     *@description 删除数据 
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<SpeSupplier> dto){
    	speSupplierService.batchDelete(dto);
        return new ResponseData();
    }

    /**
     *@description 基础查询方法
     */
    @RequestMapping(value = "/queryByChannel")
    @ResponseBody
    public ResponseData queryByChannel(SpeSupplier dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        List<SpeSupplier> data = speSupplierService.selectDataByChannel(dto);
        return new ResponseData(data);
    }

}