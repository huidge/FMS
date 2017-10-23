package clb.core.order.mapper;

import clb.core.system.dto.ClbCodeValue;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;

import java.util.List;

public interface OrdTemplateMapper extends Mapper<OrdTemplate>{
    /**
     * 话术模板查询
     * @param ordTemplate
     * @return
     */
    public List<OrdTemplate> queryOrdTemplate(OrdTemplate ordTemplate);

    /**
     * 话术模板应用分类查询
     * @param ordTemplate
     * @return
     */
    public List<OrdTemplate> queryApplyClass(OrdTemplate ordTemplate);

    /**
     * 查询快码模糊
     * @param clbCodeValue
     * @return
     */
    public List<ClbCodeValue> queryCodeValue(ClbCodeValue clbCodeValue);

    /**
     * 查询快码精确
     * @param clbCodeValue
     * @return
     */
    public List<ClbCodeValue> queryCodeValueUnq(ClbCodeValue clbCodeValue);
    /**
     * 通过模板类型和应用分类  查找对应的应用项目
     * @param dto
     * @return
     */
	public List<OrdTemplate> queryApplyItem(OrdTemplate dto);
	
	public List<OrdTemplate> queryApplyClassOnOrdAfter(OrdTemplate dto);

	public List<OrdTemplateLine> queryTemplateNameOnOrdAfter(OrdTemplateLine dto);
}