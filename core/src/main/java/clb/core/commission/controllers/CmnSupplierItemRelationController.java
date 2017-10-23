package clb.core.commission.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import clb.core.commission.dto.CmnSupplierItemRelation;
import clb.core.commission.service.ICmnSupplierItemRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

    @Controller
    public class CmnSupplierItemRelationController extends BaseController{

    @Autowired
    private ICmnSupplierItemRelationService service;


    @RequestMapping(value = "/fms/cmn/supplier/item/relation/query")
    @ResponseBody
    public ResponseData query(CmnSupplierItemRelation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByCondition(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/fms/cmn/supplier/item/relation/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CmnSupplierItemRelation> dto){
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        List<CmnSupplierItemRelation> list = new ArrayList<CmnSupplierItemRelation>();
        try {
        	list = service.batchUpdate(requestCtx, dto);
		} catch (Exception e) {
			responseData.setSuccess(false);
			responseData.setMessage("数据没有填写完整或所属供应商+产品重复!");
		}
		
        responseData.setRows(list);
        return responseData;
    }

    @RequestMapping(value = "/fms/cmn/supplier/item/relation/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CmnSupplierItemRelation> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    }