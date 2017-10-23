package clb.core.commission.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.commission.dto.CmnOverride;
import clb.core.commission.mapper.CmnOverrideMapper;
import clb.core.commission.service.ICmnOverrideService;
import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbEmployeeMapper;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class CmnOverrideServiceImpl extends BaseServiceImpl<CmnOverride> implements ICmnOverrideService{

    @Autowired
    private CmnOverrideMapper cmnOverrideMapper;
    @Autowired
    private ClbUserMapper clbUserMapper;
    @Autowired
    private ClbEmployeeMapper clbEmployeeMapper;
    @Autowired
    private SpeSupplierMapper speSupplierMapper;
    @Autowired
    private IClbUserService clbUserService;
    @Autowired
	private INtnNotificationService ntnNotificationService;
    @Autowired
    private CnlChannelContractMapper cnlChannelContractMapper;
    
    @Override
    public List<CmnOverride> queryBasic(IRequest requestContext, CmnOverride dto, int page, int pageSize) {
    	//是否超级管理员
    	boolean isAdmin=clbUserService.isAdmin(requestContext);
    	
    	if(!isAdmin){
    		if(clbUserService.hasRoleByRoleCode(requestContext, "Override Superuser")){
    			Long supplierId=clbUserService.getSupplierId(requestContext);
    			if(supplierId==null){
    				return new ArrayList<CmnOverride>();
    			}
    			dto.setSupplierId(supplierId);
    		}
    	}
        PageHelper.startPage(page,pageSize);
        return cmnOverrideMapper.queryBasic(dto);
    }

    @Override
    public CmnOverride queryMaxVersion(CmnOverride cmnOverride) {
    	CmnOverride maxVersion = cmnOverrideMapper.queryMaxVersion(cmnOverride);
    	CmnOverride maxVersion1 =new CmnOverride();
    	if(maxVersion!=null){
    		maxVersion1.setVersion(maxVersion.getVersion());
    		maxVersion1.setEffectiveStartDate(maxVersion.getEffectiveStartDate());
    	}
        return maxVersion1;
    }

    @Override
	public CmnOverride queryYTDate(CmnOverride cmnBasic) {
    	return cmnOverrideMapper.queryYTDate(cmnBasic);
	}
    
    @Override
    public List<CmnOverride> addVersion(IRequest request, CmnOverride dto, Long oldVersion) {
        CmnOverride cmnOverride = new CmnOverride();

        //查询前一版本数据
        cmnOverride.setSupplierId(dto.getSupplierId());
        cmnOverride.setParentOverrideId(dto.getParentOverrideId());
        cmnOverride.setChannelTypeCode(dto.getChannelTypeCode());
        cmnOverride.setSuperiorSupplierId(dto.getSuperiorSupplierId());
        cmnOverride.setItemId(dto.getItemId());
        cmnOverride.setContributionPeriod(dto.getContributionPeriod());
        cmnOverride.setCurrency(dto.getCurrency());
        cmnOverride.setPayMethod(dto.getPayMethod());
        cmnOverride.setPolicyholdersMinAge(dto.getPolicyholdersMinAge());
        cmnOverride.setPolicyholdersMaxAge(dto.getPolicyholdersMaxAge());
        cmnOverride.setVersion(oldVersion);
        CmnOverride cmnOverrideOld = cmnOverrideMapper.queryOverride(cmnOverride).get(0);
        //更新前一版本数据(有效期至 有效期自(新)-1)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getEffectiveStartDate());
        calendar.add(Calendar.DATE,-1);
        cmnOverrideOld.setEffectiveEndDate(DateUtil.dayEnd(calendar.getTime()));
        cmnOverrideMapper.updateByPrimaryKeySelective(cmnOverrideOld);

        //创建新版本
        dto.setParentOverrideId(cmnOverrideOld.getParentOverrideId());
//        dto.setParentOverrideId(cmnOverrideOld.getOverrideId());
        dto.setVersion(oldVersion+1);
        dto.setEffectiveEndDate(DateUtil.dayEnd(dto.getEffectiveEndDate()));
        cmnOverrideMapper.insertSelective(dto);
        List<CmnOverride> list = new ArrayList<>();
        list.add(cmnOverrideOld);
        list.add(dto);
        /****
         * 发送佣金率改变通知
         */
        BigDecimal oldOve=cmnOverrideOld.getOverride1();
        BigDecimal newOve=dto.getOverride1();
        if(oldOve.compareTo(newOve)!=0 && !dto.getChannelTypeCode().equals("YT")){
        	//调整比例
        	String override=null;
        	if(oldOve.doubleValue()==0){
        		override=String.format("%.2f", (newOve.doubleValue())*100)+"%";
        	}else{
        		override=String.format("%.2f", ((newOve.doubleValue()-oldOve.doubleValue())/oldOve.doubleValue())*100)+"%";
        	}
        	SpeSupplier speSupplier=speSupplierMapper.selectByPrimaryKey(dto.getSupplierId());
        	Map<String,Object> noticeMap = new HashMap<String,Object>();
        	noticeMap.put("supplierName", speSupplier.getName());
        	noticeMap.put("override", override);
        	if(dto.getChannelTypeCode().equals("GYS")){
        		//如果是供应商，则找下级
        		CmnOverride ove=new CmnOverride();
        		ove.setParentOverrideId(cmnOverrideOld.getOverrideId());
        		List<CmnOverride> sonOverrideList=cmnOverrideMapper.select(ove);
        		for(CmnOverride ov:sonOverrideList){
        			ClbUser user = new ClbUser();
					user.setUserType("SUPPLIER");
					user.setRelatedPartyId(ov.getSupplierId());
					List<ClbUser> userList = clbUserMapper.selectAllFields(user);
					for(ClbUser u:userList){
						ntnNotificationService.sendNotification(request,u.getUserId(), "PRICE_NOTICE", noticeMap);
					}
        		}
        	}else{
        		//如果是渠道，找 合约信息
        		CnlChannelContract cnlChannelContract=new CnlChannelContract();
        		cnlChannelContract.setChannelTypeCode(dto.getChannelTypeCode());
        		cnlChannelContract.setPartyType("SUPPLIER");
        		cnlChannelContract.setPartyId(cmnOverrideOld.getSupplierId());
        		HashSet<Long>channelIds=new HashSet<Long>();
        		for(CnlChannelContract contract:cnlChannelContractMapper.select(cnlChannelContract)){
        			channelIds.add(contract.getChannelId());
        		}
        		for(Long channelId:channelIds){
        			ClbUser user = new ClbUser();
					user.setUserType("CHANNEL");
					user.setRelatedPartyId(channelId);
					List<ClbUser> userList = clbUserMapper.selectAllFields(user);
					for(ClbUser u:userList){
						ntnNotificationService.sendNotification(request,u.getUserId(), "PRICE_NOTICE", noticeMap);
					}
        		}
        	}
        }
        
        return list;
    }
    
    @Override
    public  List<CmnOverride> batchUpdateOverride(IRequest request ,List<CmnOverride>dto){
    	for(CmnOverride ov:dto){
    		if(ov.getOverrideId()==null){
    			CmnOverride bean=new CmnOverride();
    			bean.setChannelTypeCode(ov.getChannelTypeCode());
    			bean.setSupplierId(ov.getSupplierId());
    			bean.setParentOverrideId(ov.getParentOverrideId());
    			bean.setItemId(ov.getItemId());
    			bean.setCurrency(ov.getCurrency());
    			bean.setPayMethod(ov.getPayMethod());
    			bean.setContributionPeriod(ov.getContributionPeriod());
    			if(!CollectionUtils.isEmpty(select(request, bean, 1, 10))){
    				throw new RuntimeException("供应商名称+上级供应商+渠道分类+产品编号+供款期+币种+缴款方式  不能存在重复数据!");
    			}
    		}
    	}
    	return batchUpdate(request, dto);
    }

	@Override
	public List<CmnOverride> queryOverrideBasic(IRequest request, CmnOverride dto, int page, int pageSize) {
		List<CmnOverride> list=new ArrayList<CmnOverride>();
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
				throw new RuntimeException("仅供应商用户/平台用户 可设置override ");
			}
			if(supplierId==null){
				throw new RuntimeException("该用户未维护供应商! ");
			}
			SpeSupplier supplier=speSupplierMapper.selectByPrimaryKey(supplierId);
			dto.setSupplierId(supplierId);
			PageHelper.startPage(page,pageSize);
			list= cmnOverrideMapper.queryOverrideBasic(dto);
			for(CmnOverride ov:list){
				ov.setSupplierId(supplierId);
				ov.setSupplierName(supplier.getName());
			}
		}else {
			PageHelper.startPage(page,pageSize);
			list= cmnOverrideMapper.queryOverrideBasic(dto);
		}
		return list;
	}

}