package clb.core.order.service;

import clb.core.system.dto.ClbCodeValue;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;

import java.util.List;

public interface IOrdTemplateService extends IBaseService<OrdTemplate>, ProxySelf<IOrdTemplateService>{
    List<OrdTemplate> queryOrdTemplate(IRequest request, OrdTemplate ordTemplate, int page, int pagesize);

    List<OrdTemplate> queryApplyClass(IRequest request, OrdTemplate ordTemplate);

    List<ClbCodeValue> queryCodeValue(IRequest request, ClbCodeValue clbCodeValue);

    List<ClbCodeValue> queryCodeValueUnq(IRequest request, ClbCodeValue clbCodeValue);

    List<ClbCodeValue> codeBatchUpdate(IRequest request, List<ClbCodeValue> clbCodeValues);
    /**
     * 通过模板类型和应用分类  查找对应的应用项目
     * @param requestCtx
     * @param dto
     * @return
     */
	List<OrdTemplate> queryApplyItem(IRequest requestCtx, OrdTemplate dto);
	/**
	 * 通过模板类型查找对应应用分类(售后项目)
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<OrdTemplate> queryApplyClassOnOrdAfter(IRequest requestContext, OrdTemplate dto);
	/**
	 * 模板名称和内容
	 * @param requestContext
	 * @param dto
	 * @return
	 */
	List<OrdTemplateLine> queryTemplateNameOnOrdAfter(IRequest requestContext, OrdTemplateLine dto);
}