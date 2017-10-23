package clb.core.commission.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.commission.dto.CmnExtra;
import clb.core.commission.mapper.CmnExtraMapper;
import clb.core.commission.service.ICmnExtraService;
import clb.core.common.utils.DateUtil;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbEmployeeMapper;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class CmnExtraServiceImpl extends BaseServiceImpl<CmnExtra> implements ICmnExtraService{

    @Autowired
    private CmnExtraMapper cmnExtraMapper;
    @Autowired
    private IClbUserService clbUserService;
    @Autowired
    private ClbUserMapper clbUserMapper;
    @Autowired
    private ClbEmployeeMapper clbEmployeeMapper;
    @Autowired
    private SpeSupplierMapper speSupplierMapper;
    
    @Override
    public List<CmnExtra> queryBasic(IRequest requestContext, CmnExtra dto, int page, int pageSize) {
    	//是否超级管理员
    	boolean isAdmin=clbUserService.isAdmin(requestContext);
    	if(!isAdmin){
    		Long supplierId=clbUserService.getSupplierId(requestContext);
    		if(supplierId==null){
    			return new ArrayList<CmnExtra>();
    		}
    		dto.setSupplierId(supplierId);
    	}
    	PageHelper.startPage(page,pageSize);
        return cmnExtraMapper.queryBasic(dto);
    }

    @Override
    public CmnExtra queryMaxVersion(CmnExtra cmnExtra) {
        CmnExtra maxVersion = cmnExtraMapper.queryMaxVersion(cmnExtra);
        CmnExtra maxVersion1 =new CmnExtra();
        if(maxVersion!=null){
        	maxVersion1.setVersion(maxVersion.getVersion());
        	maxVersion1.setEffectiveStartDate(maxVersion.getEffectiveStartDate());
        }
        return maxVersion1;
    }

    @Override
	public CmnExtra queryYTDate(CmnExtra cmnExtra) {
    	 return cmnExtraMapper.queryYTDate(cmnExtra);
	}
    
    @Override
    public List<CmnExtra> addVersion(IRequest request, CmnExtra dto, Long oldVersion) {
        CmnExtra cmnExtra = new CmnExtra();

        //查询前一版本数据
        cmnExtra.setSupplierId(dto.getSupplierId());
        cmnExtra.setParentExtraId(dto.getParentExtraId());
        cmnExtra.setSuperiorSupplierId(dto.getSuperiorSupplierId());
        cmnExtra.setChannelTypeCode(dto.getChannelTypeCode());
        cmnExtra.setItemId(dto.getItemId());
        cmnExtra.setContributionPeriod(dto.getContributionPeriod());
        cmnExtra.setCurrency(dto.getCurrency());
        cmnExtra.setPayMethod(dto.getPayMethod());
        cmnExtra.setPolicyholdersMinAge(dto.getPolicyholdersMinAge());
        cmnExtra.setPolicyholdersMaxAge(dto.getPolicyholdersMaxAge());
        cmnExtra.setVersion(oldVersion);
        CmnExtra cmnExtraOld = cmnExtraMapper.queryExtra(cmnExtra).get(0);
        //更新前一版本数据(有效期至 有效期自(新)-1)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getEffectiveStartDate());
        calendar.add(Calendar.DATE,-1);
        cmnExtraOld.setEffectiveEndDate(DateUtil.dayEnd(calendar.getTime()));
        cmnExtraMapper.updateByPrimaryKeySelective(cmnExtraOld);

        //创建新版本
        dto.setParentExtraId(cmnExtraOld.getParentExtraId());
//        dto.setParentExtraId(cmnExtraOld.getExtraId());
        dto.setVersion(oldVersion+1);
        dto.setEffectiveEndDate(DateUtil.dayEnd(dto.getEffectiveEndDate()));
        cmnExtraMapper.insertSelective(dto);
        List<CmnExtra> list = new ArrayList<>();
        list.add(cmnExtraOld);
        list.add(dto);
        return list;
    }

	@Override
	public List<CmnExtra> queryExtraBasic(IRequest request, CmnExtra dto, int page, int pageSize) {
		List<CmnExtra> list=new ArrayList<CmnExtra>();
		//是否超级管理员
		boolean isAdmin=clbUserService.isAdmin(request);
		if(!isAdmin){
			ClbUser clbUser= clbUserMapper.selectByPrimaryKey(request.getUserId());
			Long supplierId=null;
			if(clbUser.getUserType().equals("SUPPLIER")){
				supplierId=clbUser.getRelatedPartyId();
			}else if(clbUser.getUserType().equals("OPERATOR")){
				ClbEmployee clbEmployee =clbEmployeeMapper.selectByPrimaryKey(clbUser.getRelatedPartyId());
				if(clbEmployee.getOwnershipType().equals("SUPPLIER")){
					supplierId=clbEmployee.getOwnershipId();
				}
			}else{
				throw new RuntimeException("仅供应商用户/平台用户 可设置Extra ");
			}
			if(supplierId==null){
				throw new RuntimeException("该用户未维护供应商! ");
			}
			SpeSupplier supplier=speSupplierMapper.selectByPrimaryKey(supplierId);
			dto.setSupplierId(supplierId);
			PageHelper.startPage(page,pageSize);
			list= cmnExtraMapper.queryExtraBasic(dto);
			for(CmnExtra ov:list){
				ov.setSupplierId(supplierId);
				ov.setSupplierName(supplier.getName());
			}
		}else {
			PageHelper.startPage(page,pageSize);
			list= cmnExtraMapper.queryExtraBasic(dto);
		}
		return list;
	}

}