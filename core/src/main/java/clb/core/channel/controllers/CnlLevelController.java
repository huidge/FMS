package clb.core.channel.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import clb.core.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;

import clb.core.channel.dto.CnlLevel;
import clb.core.channel.service.ICnlLevelService;

    @Controller
    public class CnlLevelController extends BaseController{

    @Autowired
    private ICnlLevelService service;


    @RequestMapping(value = "/fms/cnl/level/query")
    @ResponseBody
    public ResponseData query(CnlLevel dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.selectByCondition(dto,requestContext,page,pageSize));
    }

    @RequestMapping(value = "/fms/cnl/level/submit")
    @ResponseBody
    public ResponseData update(HttpServletRequest request,@RequestBody List<CnlLevel> dto){
        IRequest requestCtx = createRequestContext(request);
        //循环传递进来的dto
        for (CnlLevel cnlLevel : dto) {
			List<CnlLevel> cnlLevelList = new ArrayList<CnlLevel>();
            cnlLevel.setEffectiveStartDate(DateUtil.dayStart(cnlLevel.getEffectiveStartDate()));
			cnlLevel.setEffectiveEndDate(this.dayEnd(cnlLevel.getEffectiveEndDate()));
			cnlLevelList = service.selectCountByCondition(cnlLevel);
			//判断插入的记录是否存在
			if(cnlLevelList.get(0).getCount() > 0){
				CnlLevel previousCnlLevel = service.selectPreviousVersion(cnlLevel.getChannelClassCode(), cnlLevel.getLevelName(), cnlLevel.getSupplierId(), cnlLevelList.get(0).getMaxVersion());
				cnlLevel.setVersion(cnlLevelList.get(0).getMaxVersion()+1);
				service.cnlLevelInsert(cnlLevel);
				previousCnlLevel.setEffectiveEndDate(this.dayEnd(this.dayMinusOne(cnlLevel.getEffectiveStartDate())));
				service.updateByPrimaryKey(requestCtx, previousCnlLevel);
			}
			else{
				cnlLevel.setVersion(1L);
				service.cnlLevelInsert(cnlLevel);
			}
		}
        CnlLevel cnlLevelSubmit = new CnlLevel();
        dto = service.selectByCondition(cnlLevelSubmit,requestCtx,1,10);
        return new ResponseData(dto);
    }

    @RequestMapping(value = "/fms/cnl/level/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request,@RequestBody List<CnlLevel> dto){
        service.batchDelete(dto);
        return new ResponseData();
    }
    
    /**
     * 获取指定时间的那天 23:59:59 的时间
     * 
     * @param date
     * @return
     */
    private Date dayEnd(Date date) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR_OF_DAY, 23);
            c.set(Calendar.MINUTE, 59);
            c.set(Calendar.SECOND, 59);
            return c.getTime();
    }
    
    /**
     * 设置时间
     * @param date
     * @return
     */
    private Date dayMinusOne(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        return c.getTime();
     }
   }