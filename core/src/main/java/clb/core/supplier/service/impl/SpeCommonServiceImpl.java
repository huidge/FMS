package clb.core.supplier.service.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;

import clb.core.supplier.service.ISpeCommonService;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.service.IClbCodeService;

/**
 * @name SpeCommonServiceImpl
 * @description 供应商通用业务逻辑层
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
@Service
@Transactional
public class SpeCommonServiceImpl implements ISpeCommonService{


	@Autowired
	private IClbCodeService codeService;


	@Override
	public List<ClbCodeValue> getCodeValuesByParentId(IRequest iRequest,ClbCode dto,String parentValue) {
		//根据代码获取Code
		List<ClbCodeValue> codeValues = codeService.selectCodeValuesByCodeName(iRequest,dto.getCode());
		//剔除parentValue不是parentValue的值
		Iterator<ClbCodeValue> iterator = codeValues.iterator();
		while(iterator.hasNext()){
    		if(!parentValue.equals(iterator.next().getParentValue())){
    			iterator.remove();
    		}
    	}
		return codeValues;
	}
    
    
    

}