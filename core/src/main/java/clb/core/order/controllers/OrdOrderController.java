package clb.core.order.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import clb.core.commission.dto.CmnQueryCommission;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.common.utils.CalculateAge;
import clb.core.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.account.dto.User;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdStatusHis;
import clb.core.order.dto.SysOrderCfgField;
import clb.core.order.dto.SysOrderCfgOperation;
import clb.core.order.service.IOrdOrderService;
import clb.core.production.dto.PrdItems;
import clb.core.supplier.dto.SpeSupplierLine;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.dto.ClbUser;

@RequestMapping({"/fms/ord/order"})
@Controller
public class OrdOrderController extends BaseController{

    @Autowired
    private IOrdOrderService service;

    @Autowired
    private ICmnChannelCommissionService channelCommissionService;

    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        //订单汇总页面的冷静期按钮  --->查询冷静期为空  状态为保单生效的订单
        if(dto.getExpectCoolDateColumn() != null && !"".equals(dto.getExpectCoolDateColumn())) {
        	dto.setStatus("POLICY_EFFECTIVE");
        }
        return new ResponseData(service.queryOrder(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/queryAll")
    @ResponseBody
    public ResponseData queryAll(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryWsOrder(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<OrdOrder> dto){
        IRequest requestCtx = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
            responseData = new ResponseData(service.orderBatchUpdate(requestCtx, dto));
        } catch (Exception e) {
            responseData.setMessage("提交失败，请联系管理员!");
            responseData.setSuccess(false);
        }

        return responseData;
    }

    @RequestMapping(value = "/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<OrdOrder> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    @RequestMapping(value = "/queryUser")
    @ResponseBody
    public ResponseData queryUser(ClbUser dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryUser(requestContext,dto));
    }

    @RequestMapping(value = "/queryField")
    @ResponseBody
    public ResponseData queryField(SysOrderCfgField dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryField(requestContext,dto));
    }

    @RequestMapping(value = "/queryOpera")
    @ResponseBody
    public ResponseData queryOpera(SysOrderCfgOperation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOpera(requestContext,dto));
    }

    @RequestMapping(value = "/queryTradeRoute")
    @ResponseBody
    public ResponseData queryTradeRoute(CmnChannelCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryTradeRoute(requestContext, dto));
    }

    @RequestMapping(value = "/queryOrdTradeRoute")
    @ResponseBody
    public ResponseData queryOrdTradeRoute(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrdTradeRoute(requestContext, dto));
    }

    @RequestMapping(value = "/queryOrderStatus")
    @ResponseBody
    public ResponseData queryOrderStatus(ClbCodeValue dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrderStatus(requestContext, dto));
    }

    @RequestMapping(value = "/queryOrderAppStatus")
    @ResponseBody
    public ResponseData queryOrderAppStatus(ClbCodeValue dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryOrderAppStatus(requestContext, dto));
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(true);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @RequestMapping(value = "/query/immigrant")
    @ResponseBody
    public ResponseData queryImmigrantOrder(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryImmigrantOrder(requestContext,dto,page,pageSize));
    }

    @RequestMapping(value = "/query/bond")
    @ResponseBody
    public ResponseData queryBondOrder(OrdOrder dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryBondOrder(requestContext,dto,page,pageSize));
    }

    /**
     * 更新订单状态
     * daiqian.shi@hand-china.com
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/update/status")
    @ResponseBody
    public ResponseData updateOrderStatus(@RequestBody OrdOrder dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        service.updateStatus(requestContext,dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/queryRole")
    @ResponseBody
    public ResponseData queryRole(ClbUser dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryRole(requestContext,dto));
    }

    @RequestMapping(value = "/queryUserRole")
    @ResponseBody
    public ResponseData queryUserRole(ClbUser dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryUserRole(requestContext,dto));
    }

    @RequestMapping(value = "/queryAddress")
    @ResponseBody
    public ResponseData queryRole(SpeSupplierLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryAddress(requestContext,dto));
    }

    @RequestMapping(value = "/queryContactPerson")
    @ResponseBody
    public ResponseData queryContactPerson(SpeSupplierLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContactPerson(requestContext,dto));
    }

    @RequestMapping(value = "/queryContactPhone")
    @ResponseBody
    public ResponseData queryContactPhone(SpeSupplierLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryContactPhone(requestContext,dto));
    }

    @RequestMapping(value = "/validateTradeRoute")
    @ResponseBody
    public ResponseData validateTradeRoute(@RequestBody CmnQueryCommission dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        ResponseData response = new ResponseData(true);
        if (dto.getChannelId() == null) {
            response.setSuccess(false);
            response.setMessage("渠道ID不能为空!");
            return response;
        }

        if (dto.getEffectiveDate() == null) {
            response.setSuccess(false);
            response.setMessage("有效日期不能为空!");
            return response;
        }

        if ("".equals(dto.getCurrency())) {
            response.setSuccess(false);
            response.setMessage("币种不能为空!");
            return response;
        }

        if ("".equals(dto.getPayMethod())) {
            response.setSuccess(false);
            response.setMessage("缴费方式不能为空!");
            return response;
        }

        if ("".equals(dto.getContributionPeriod())) {
            response.setSuccess(false);
            response.setMessage("供款期不能为空!");
            return response;
        }

        if (dto.getItemId() == null) {
            response.setSuccess(false);
            response.setMessage("主险产品不能为空!");
            return response;
        }

        List<CmnChannelCommission> resultList = channelCommissionService.chooseCommission(requestContext,dto);
        return new ResponseData(resultList);
    }

    @RequestMapping(value = "/queryItemByClass")
    @ResponseBody
    public ResponseData queryItemByClass(PrdItems dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryItemByClass(dto));
    }
    
    /**
     * 导出pdf文件
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/templatePDFOut", method = RequestMethod.GET)
    public void pdfStandardTemplateOut(HttpServletRequest request,HttpServletResponse response) throws IOException{
    	String orderId = request.getParameter("orderId");
    	IRequest requestContext = createRequestContext(request);
    	try {
			service.exportPDF(request,response,orderId,requestContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
   }

    @RequestMapping(value = "/calculateAge")
    @ResponseBody
    public Long calculateAge(Date birthDate,String type, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        Long age = Long.valueOf(CalculateAge.accessAge(birthDate,type));
        return age;
    }

    /**
     * 导出pdf文件
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value="/aiaContractPDF", method = RequestMethod.GET)
    public void aiaContractPDF(HttpServletRequest request,HttpServletResponse response) throws IOException{
        String orderId = request.getParameter("orderId");
        IRequest requestContext = createRequestContext(request);
        service.aiaContractPDF(request,response,orderId,requestContext);
    }
    /**
     * 添加状态日志  需求变更
     * @param request
     * @param ordStatusHis
     */
    @RequestMapping(value="/addHisStatus2")
    public ResponseData addHisStatus2(HttpServletRequest request,OrdStatusHis ordStatusHis){
    	IRequest requestContext = createRequestContext(request);
    	service.addHisStatus2(requestContext,ordStatusHis);
    	return new ResponseData();
    }
}
