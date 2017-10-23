package clb.core.supplier.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.ognl.OgnlException;
import org.redisson.api.listener.BaseStatusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.ICodeService;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hap.system.service.impl.CodeServiceImpl;

import clb.core.common.service.ISequenceService;
import clb.core.common.utils.ExceptionUtil;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.supplier.dto.SpeCollectionTerms;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.exceptions.SpeException;
import clb.core.supplier.mapper.SpeCollectionTermsMapper;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.supplier.service.ISpeCollectionTermsService;

/**
 * @name SpeArchivesServiceImpl
 * @description 收款条件业务逻辑层
 * @author bo.wu@hand-china.com 2017年4月24日20:59:14
 * @version 1.0 
 */
@Service
@Transactional
public class SpeCollectionTermsServiceImpl extends BaseServiceImpl<SpeCollectionTerms> implements ISpeCollectionTermsService{

	@Autowired
	private SpeCollectionTermsMapper collectionTermsMapper;
	
	@Autowired
	private PrdItemsMapper prdItemsMapper;
	
	@Autowired
	private ICodeService codeService;
	
	@Autowired
	private ISequenceService sequenceService;
	
	@Override
	public List<SpeCollectionTerms> selectData(SpeCollectionTerms dto,int page,int pagesize) {
		if(pagesize != 0)PageHelper.startPage(page,pagesize);
		return collectionTermsMapper.selectData(dto);
	}

	@Override
	public List<SpeCollectionTerms> selectAllData(SpeCollectionTerms dto) {
		return collectionTermsMapper.selectData(dto);
	}
	
	/**
	 * 获取产品分类 
	 */
	@Override
	public List<CodeValue> getProductDivision(IRequest iRequest) {
		List<PrdItems> items = prdItemsMapper.selectAll();
		String codeName = "PRD.PRODUCT_DIVISION";
		List<CodeValue> itemsCodes = codeService.selectCodeValuesByCodeName(iRequest, codeName);
		List<CodeValue> resData = new ArrayList<>();
		for(PrdItems item:items){
			for(CodeValue c:itemsCodes){
				if(item.getBigClass().equals(c.getValue())){
					if(!resData.contains(c)){
						resData.add(c);
					}
				}
			}
		}
		return resData;
	}
	
	/**
	 * 保存方法，同时校验key值是否重复
	 */
	@Override
	public List<SpeCollectionTerms> collectionTermsBatchUpdate(IRequest iRequest,List<SpeCollectionTerms> data) throws SpeException {
		SpeCollectionTerms param = new SpeCollectionTerms();
		for(SpeCollectionTerms d:data){
			/* ----校验key值是否重复---- */
			if(d.getPaymentCompanyId() == null)d.setPaymentCompanyId(0L);
			//设置参数并查询
			param.setTermType(d.getTermType());
			param.setProductDivision(d.getProductDivision());
			param.setPaymentCompanyType(d.getPaymentCompanyType());
			param.setProductCompanyId(d.getProductCompanyId());
			param.setPaymentCompanyId(d.getPaymentCompanyId());
			param = collectionTermsMapper.selectOne(param);
			if(d.get__status().equals(DTOStatus.ADD)){
				//如果查到值，说明不唯一，报错
				if(param != null){
					throw new SpeException("SPE","创建失败：存在重复值，已有数据的条件编号为"+param.getTermCode(),null);
				}
				String code = sequenceService.getSequence("COLLECTIONTERM");
				d.setTermCode(code);
				try{
					d.setCreatedBy(iRequest.getUserId());
					d.setLastUpdatedBy(iRequest.getUserId());
					d.setLastUpdateLogin(iRequest.getUserId());
					d.setCreationDate(new Date());
					d.setLastUpdateDate(new Date());
					this.insertSelective(iRequest,d);
				}catch(Exception e){
					//防止并发操作
					int type = ExceptionUtil.getExceptionType(e);
					if(type == 1){
						throw new SpeException("SPE","创建失败：存在重复值，已有数据的条件编号为"+code,null);
					}else{
						throw e;
					}
					
				}
				
			}else if(d.get__status().equals(DTOStatus.UPDATE)){
				if(param != null && !param.get_token().equals(d.get_token())){
					throw new SpeException("SPE","更新失败：存在重复值，已有数据的条件编号为"+param.getTermCode(),null);
				}
				d.setLastUpdatedBy(iRequest.getUserId());
				d.setLastUpdateLogin(iRequest.getUserId());
				d.setLastUpdateDate(new Date());
				this.updateByPrimaryKeySelective(iRequest,d);
			}
			param = new SpeCollectionTerms();
		}
		return data;
	}
	
	
}