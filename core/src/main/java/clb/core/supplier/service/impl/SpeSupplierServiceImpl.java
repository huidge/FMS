package clb.core.supplier.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ISequenceService;
import clb.core.common.utils.ExceptionUtil;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.supplier.service.ISpeSupplierService;

/**
 * @name SpeSupplierServiceImpl
 * @description 供应商业务层
 * @author bo.wu@hand-china.com 2017年4月18日19:36:25
 * @version 1.0 
 */
@Service
@Transactional
public class SpeSupplierServiceImpl extends BaseServiceImpl<SpeSupplier> implements ISpeSupplierService{

	@Autowired
	private SpeSupplierMapper speSupplierMapper;
	@Autowired
	private ISequenceService sequenceService;
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<SpeSupplier> supplierBatchUpdate(IRequest request,List<SpeSupplier> data) throws CommonException {
		for(SpeSupplier d:data){
			if(d.get__status().equals(DTOStatus.ADD)){
				String code = sequenceService.getSequence("SUPPLIER");
				d.setSupplierCode(code);
				//STDWhos字段
				d.setCreatedBy(request.getUserId());
				d.setLastUpdatedBy(request.getUserId());
				d.setLastUpdateLogin(request.getUserId());
				d.setCreationDate(new Date());
				d.setLastUpdateDate(new Date());
				try{
					this.insertSelective(request,d);
				}catch(Exception e){
					int type = ExceptionUtil.getExceptionType(e);
					if(type == 1){
						throw new CommonException("SPE","创建失败：存在重复值",null);
					}else{
						throw e;
					}
				}
				
			}else if(d.get__status().equals(DTOStatus.UPDATE)){
				d.setLastUpdatedBy(request.getUserId());
				d.setLastUpdateLogin(request.getUserId());
				d.setLastUpdateDate(new Date());
				this.updateByPrimaryKeySelective(request,d);
			}
		}
		return data;
	}

	@Override
	public List<SpeSupplier> selectData(SpeSupplier dto,int page,int pagesize) {
		PageHelper.startPage(page,pagesize);
		List<SpeSupplier> data = speSupplierMapper.selectData(dto);
		return data;
	}

	@Override
	public List<SpeSupplier> selectAllData(SpeSupplier dto) {
		return speSupplierMapper.selectData(dto);
	}
	
	@Override
    public List<SpeSupplier> selectDataWithItems(SpeSupplier dto) {
        return speSupplierMapper.selectDataWithItems(dto);
    }

	@Override
	public List<SpeSupplier> selectDataByChannel(SpeSupplier dto) {
		return speSupplierMapper.selectDataByChannel(dto);
	}

	@Override
	public List<SpeSupplier> selectByName(SpeSupplier dto) {
		return speSupplierMapper.selectByName(dto);
	}

	@Override
	public List<SpeSupplier> selectByNameAndSort(SpeSupplier dto,int page,int pagesize) {
		PageHelper.startPage(page,pagesize);
		return speSupplierMapper.selectByNameAndSort(dto);
	}
	
	

}