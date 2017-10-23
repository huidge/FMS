package clb.core.forecast.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;

import clb.core.common.exceptions.CommonException;
import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.service.IFetQuestionService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/fet/question")
public class FetQuestionController extends BaseController{

    @Autowired
    private IFetQuestionService service;
    
    @Autowired
    private ISysFileService fileService;


    @RequestMapping(value = "/query")
    @ResponseBody
    public ResponseData query(FetQuestion dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        return new ResponseData(service.getData(dto,page,pageSize));
    }
    
    @RequestMapping(value = "/getToken")
    public String getToken(Long fileId,HttpServletRequest request) {
    	SysFile file = new SysFile();
    	file.setFileId(fileId);
        IRequest requestCtx = createRequestContext(request);
        file = fileService.selectByPrimaryKey(requestCtx,file.getFileId());
        if(file == null)return "";
        if(StringUtils.isEmpty(file.get_token())){
        	return "";
        }
        return file.get_token();
    }

    @RequestMapping(value = "/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<FetQuestion> dto){
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.batchUpdate(requestCtx, dto));
    }

    @RequestMapping(value = "/saveData")
    @ResponseBody
    public ResponseData saveData(HttpServletRequest request,@RequestBody FetQuestion dto) throws CommonException{
        IRequest iRequest = createRequestContext(request);
    	service.saveData(iRequest, dto);
        List<FetQuestion> resData = new ArrayList<>();
        resData.add(dto);
        return new ResponseData(resData);
    }
}