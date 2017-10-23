package clb.core.release.controllers;

import org.springframework.stereotype.Controller;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.release.dto.NtcSlideshow;
import clb.core.release.service.INtcSlideshowService;
import clb.core.supplier.dto.SpeSupplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NtcSlideshowController extends BaseController {

	@Autowired
	private INtcSlideshowService service;
	
	@Autowired
	private ISysFileService sysFileService;

	@RequestMapping(value = "/fms/ntc/slideshow/query")
	@ResponseBody
	public ResponseData query(NtcSlideshow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<NtcSlideshow> queryByCondition = service.queryByCondition(requestContext, dto, page, pageSize);
		for (NtcSlideshow ntcSlideshow : queryByCondition) {
			if(ntcSlideshow.getPcFileId()!=null){
    			ntcSlideshow.setPcSysFile(sysFileService.selectByPrimaryKey(requestContext,ntcSlideshow.getPcFileId()));
	        }
			if(ntcSlideshow.getWechatFileId()!=null){
				ntcSlideshow.setWechatSysFile(sysFileService.selectByPrimaryKey(requestContext,ntcSlideshow.getWechatFileId()));
			}
			if(ntcSlideshow.getAppFileId()!=null){
				ntcSlideshow.setAppSysFile(sysFileService.selectByPrimaryKey(requestContext,ntcSlideshow.getAppFileId()));
			}
		}
		return new ResponseData(queryByCondition);
	}

	@RequestMapping(value = "/fms/ntc/slideshow/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request,@RequestBody List<NtcSlideshow> dto) {
		IRequest requestCtx = createRequestContext(request);
		List<NtcSlideshow> list = new ArrayList<>();
		try {
			NtcSlideshow key = service.updateByPrimaryKeySelective(requestCtx, dto.get(0));
			list.add(key);
			return new ResponseData(list);
		} catch (Exception e) {
			ResponseData responseData = new ResponseData(dto);
			responseData.setSuccess(false);
			return responseData;
		}

	}

	@RequestMapping(value = "/fms/ntc/slideshow/add")
	@ResponseBody
	public ResponseData add(HttpServletRequest request, @RequestBody List<NtcSlideshow> dto) {
		IRequest requestCtx = createRequestContext(request);
		List<NtcSlideshow> list = new ArrayList<>();
		try {
			NtcSlideshow ntcSlideshow = dto.get(0);
			ntcSlideshow.setRank((queryMaxRank() == null?1:queryMaxRank())+1);
			
			NtcSlideshow selective = service.insertSelective(requestCtx, ntcSlideshow);
			list.add(selective);
			return new ResponseData(list);
		} catch (Exception e) {
			ResponseData responseData = new ResponseData(dto);
			responseData.setSuccess(false);
			return responseData;
		}
	}

	@RequestMapping(value = "/fms/ntc/slideshow/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<NtcSlideshow> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
	
	public Long queryMaxRank() {
		return service.queryMaxRank();
	}
	
	@RequestMapping(value = "/fms/ntc/slideshow/upMove")
	@ResponseBody
	public ResponseData upMove(HttpServletRequest request,NtcSlideshow dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		
		NtcSlideshow ntcSlideshow = service.queryUpRank(dto);
		
		if(ntcSlideshow == null){
			responseData.setSuccess(false);
			responseData.setMessage("已经在最上面了!");
			return responseData;
		}else{
			try {
				Long rank = ntcSlideshow.getRank();
				ntcSlideshow.setRank(dto.getRank());
				dto.setRank(rank);
				
				service.updateByPrimaryKeySelective(requestCtx, ntcSlideshow);
				service.updateByPrimaryKeySelective(requestCtx, dto);
				responseData.setMessage("操作成功!");
				return responseData;
			} catch (Exception e) {
				responseData.setSuccess(false);
				responseData.setMessage("操作失败!");
				return responseData;
			}
		}
	}
	@RequestMapping(value = "/fms/ntc/slideshow/downMove")
	@ResponseBody
	public ResponseData downMove(HttpServletRequest request,NtcSlideshow dto) {
		IRequest requestCtx = createRequestContext(request);
		ResponseData responseData = new ResponseData();
		
		NtcSlideshow ntcSlideshow = service.queryDownRank(dto);
		if(ntcSlideshow == null){
			responseData.setSuccess(false);
			responseData.setMessage("已经在最下面了!");
			return responseData;
		}else{
			try {
				Long rank = ntcSlideshow.getRank();
				ntcSlideshow.setRank(dto.getRank());
				dto.setRank(rank);
				service.updateByPrimaryKeySelective(requestCtx, ntcSlideshow);
				service.updateByPrimaryKeySelective(requestCtx, dto);
				
				responseData.setMessage("操作成功!");
				return responseData;
			} catch (Exception e) {
				responseData.setSuccess(false);
				responseData.setMessage("操作失败!");
				return responseData;
			}
		}
	}
	
	
}