package clb.core.order.service.impl;

import clb.core.order.mapper.OrdTemplateMapper;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeValueMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdTemplate;
import clb.core.order.dto.OrdTemplateLine;
import clb.core.order.service.IOrdTemplateService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrdTemplateServiceImpl extends BaseServiceImpl<OrdTemplate> implements IOrdTemplateService{

    @Autowired
    private OrdTemplateMapper ordTemplateMapper;

    @Autowired
    private ClbCodeValueMapper clbCodeValueMapper;

    /**
     * 查询
     * @param request
     * @param ordTemplate
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdTemplate> queryOrdTemplate(IRequest request, OrdTemplate ordTemplate, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return ordTemplateMapper.queryOrdTemplate(ordTemplate);
    }

    /**
     * 查询
     * @param request
     * @param ordTemplate
     * @return
     */
    @Override
    public List<OrdTemplate> queryApplyClass(IRequest request, OrdTemplate ordTemplate) {
        return ordTemplateMapper.queryApplyClass(ordTemplate);
    }

    /**
     * 查询
     * @param request
     * @param clbCodeValue
     * @return
     */
    @Override
    public List<ClbCodeValue> queryCodeValue(IRequest request, ClbCodeValue clbCodeValue) {
        return ordTemplateMapper.queryCodeValue(clbCodeValue);
    }

    /**
     * 查询
     * @param request
     * @param clbCodeValue
     * @return
     */
    @Override
    public List<ClbCodeValue> queryCodeValueUnq(IRequest request, ClbCodeValue clbCodeValue) {
        return ordTemplateMapper.queryCodeValueUnq(clbCodeValue);
    }

    @Override
    public List<ClbCodeValue> codeBatchUpdate(IRequest request, List<ClbCodeValue> clbCodeValues) {

        for (ClbCodeValue clbCodeValue : clbCodeValues)
        if (clbCodeValue.getCodeValueId() == null) {
            clbCodeValueMapper.insertSelective(clbCodeValue);
        } else if (clbCodeValue.getCodeValueId() != null) {
            clbCodeValueMapper.updateByPrimaryKeySelective(clbCodeValue);
        }
        return clbCodeValues;
    }
	/**
	 * 通过模板类型和应用分类  查找对应的应用项目
	 */
	@Override
	public List<OrdTemplate> queryApplyItem(IRequest requestCtx, OrdTemplate dto) {
		return ordTemplateMapper.queryApplyItem(dto);
	}

	@Override
	public List<OrdTemplate> queryApplyClassOnOrdAfter(IRequest requestContext, OrdTemplate dto) {
		return ordTemplateMapper.queryApplyClassOnOrdAfter(dto);
	}

	@Override
	public List<OrdTemplateLine> queryTemplateNameOnOrdAfter(IRequest requestContext, OrdTemplateLine dto) {
		return ordTemplateMapper.queryTemplateNameOnOrdAfter(dto);
	}
}