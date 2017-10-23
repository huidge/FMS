package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetQuestionLine;
import clb.core.forecast.service.IFetQuestionLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/fet/question/line")
public class FetQuestionLineController extends BaseController{

    @Autowired
    private IFetQuestionLineService service;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetQuestionLine dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
        return new ResponseData(service.getData(iRequest,dto,request));
    }
    
    @RequestMapping(value = "/querybycheckperiod")
    @ResponseBody
    public ResponseData queryByCheckPeriod(FetQuestion dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
    	IRequest iRequest = createRequestContext(request);
    	return new ResponseData(service.queryByCheckPeriod(iRequest,dto,request));
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<FetQuestionLine> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }
}