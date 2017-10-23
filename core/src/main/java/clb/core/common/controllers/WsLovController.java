package clb.core.common.controllers;

import clb.core.common.utils.CommonUtil;
import clb.core.order.dto.OrdChooseItem;
import clb.core.order.service.IOrdChooseItemService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.Lov;
import com.hand.hap.system.dto.LovItem;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.ILovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @name WsUploadController
 * @description 前端调用后台定义LOV接口api
 * @author xiaoyong.luo@hand-china.com 2017年6月10日21:13:31
 * @version 1.0 
 */
@Controller
public class WsLovController extends ClbBaseController{

	@Autowired
	private ILovService lovService;

	@Autowired
	private IOrdChooseItemService chooseItemService;

	/**
	 * 获取LOV的所有配置信息
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCommonLovConfig")
	@RequestMapping(value = "/api/commons/lov/config", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData lovConfig(@RequestBody Lov lov, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
								 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);
		List<Lov> list = lovService.selectLovs(requestContext, lov, page, pagesize);
		if(list !=null && list.size()> 0){
			for(Lov l : list){
				if(l.getCode().equals(lov.getCode())){
					lov = l;
					break;
				}
			}
			if(lov != null && lov.getLovId() != null){
				LovItem lovItem = new LovItem();
				lovItem.setLovId(lov.getLovId());
				List<LovItem> lovItems =  lovService.selectLovItems(requestContext,lovItem);
				lov.setLovItems(lovItems);
				List<Lov> lovs = new ArrayList<>();
				lovs.add(lov);
				return new ResponseData(lovs);
			}
		}
		return new ResponseData(list);
	}

	/**
	 * 获取LOV的数据
	 */
	@Timed
	@HapInbound(apiName = "ClbWsCommonLovData")
	@RequestMapping(value = "/api/commons/lov/{resource}", method = RequestMethod.POST, produces = {"application/json" })
	@ResponseBody
	public ResponseData getLovDatas(@PathVariable String resource, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
			@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, @RequestBody Map<String, String> params,
			HttpServletRequest request) {
		IRequest requestContext = createRequestContext(request);

		// 当Lov 类型为自定义URL时
		if ("IMMIGRATION_PRD_ITEMS".equals(resource)) {
			OrdChooseItem ordChooseItem =  CommonUtil.map2Bean(params, OrdChooseItem.class);
			ordChooseItem.setBigClass("DC");
			ordChooseItem.setExistFlag("N");
			List<OrdChooseItem> itemList = chooseItemService.queryOrdItemLimit(requestContext, ordChooseItem);
			if (itemList.size() == 0) {
				ordChooseItem.setExistFlag(null);
			}
			itemList = chooseItemService.queryOrdItem(requestContext, ordChooseItem, page, pagesize);
			return new ResponseData(itemList);

		} else if ("BOND_PRD_ITEMS".equals(resource)) {
			OrdChooseItem ordChooseItem =  CommonUtil.map2Bean(params, OrdChooseItem.class);
			ordChooseItem.setBigClass("ZQ");
			ordChooseItem.setExistFlag("N");
			List<OrdChooseItem> itemList = new ArrayList<OrdChooseItem>();
			if (chooseItemService.queryOrdItemLimit(requestContext, ordChooseItem).size() == 0) {
				ordChooseItem.setExistFlag(null);
			}
			itemList = chooseItemService.queryOrdItem(requestContext, ordChooseItem, page, pagesize);

			return new ResponseData(itemList);
		}

		return new ResponseData(lovService.selectDatas(requestContext, resource, params, page, pagesize));
	}


}