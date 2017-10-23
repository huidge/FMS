package clb.core.commission.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;

import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import clb.core.commission.dto.CmnAdtRiskRelation;
import clb.core.commission.service.ICmnAdtRiskRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CmnAdtRiskRelationController extends BaseController {

	@Autowired
	private ICmnAdtRiskRelationService service;

	@RequestMapping(value = "/fms/cmn/adt/risk/relation/queryAll")
	@ResponseBody
	public ResponseData query(CmnAdtRiskRelation dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		return new ResponseData(service.queryAll(requestContext, dto, page, pageSize));
	}

	@RequestMapping(value = "/fms/cmn/adt/risk/relation/submit")
	@ResponseBody
	public ResponseData update(HttpServletRequest request, @RequestBody List<CmnAdtRiskRelation> dto) {
		IRequest requestCtx = createRequestContext(request);
		//前台每次只会传递一个对象
		CmnAdtRiskRelation cmnAdtRiskRelation = dto.get(0);
		List<CmnAdtRiskRelation> list = new ArrayList<>();
		try {
			if("update".equals(cmnAdtRiskRelation.get__status())) {
				CmnAdtRiskRelation updateByPrimaryKey = service.updateByPrimaryKey(requestCtx, cmnAdtRiskRelation);
				list.add(updateByPrimaryKey);
			}else {
				CmnAdtRiskRelation insertSelective = service.insertSelective(requestCtx, cmnAdtRiskRelation);
				list.add(insertSelective);
			}
		} catch (Exception e) {
			throw new RuntimeException("该附加险数据已存在!");
		}
		return new ResponseData(list);
	}

	@RequestMapping(value = "/fms/cmn/adt/risk/relation/remove")
	@ResponseBody
	public ResponseData delete(HttpServletRequest request, @RequestBody List<CmnAdtRiskRelation> dto) {
		service.batchDelete(dto);
		return new ResponseData();
	}
}