package clb.core.sys.service.impl;

import com.hand.hap.account.dto.Role;
import com.hand.hap.account.mapper.RoleMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.function.dto.RoleFunction;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.apache.ibatis.ognl.OgnlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.ExceptionUtil;
import clb.core.forecast.dto.FetActualPayment;
import clb.core.supplier.exceptions.SpeException;
import clb.core.sys.dto.SysOperatePermission;
import clb.core.sys.mapper.SysOperatePermissionMapper;
import clb.core.sys.service.ISysOperatePermissionService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SysOperatePermissionServiceImpl extends BaseServiceImpl<SysOperatePermission> implements ISysOperatePermissionService{

	
	@Autowired
	private SysOperatePermissionMapper permissionMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	public List<SysOperatePermission> operatePermissionBatchUpdate(IRequest iRequest,List<SysOperatePermission> data) throws CommonException {
		SysOperatePermission param = new SysOperatePermission();
		for(SysOperatePermission d:data){
			Role role = new Role();
			role.setRoleId(d.getRoleId());
			role = roleMapper.selectByPrimaryKey(role);
			if(role == null){
				throw new CommonException("SYS",d.getRoleId()+"不存在！",null);
			}
			param.setRoleId(d.getRoleId());
			param.setFuncCode(d.getFuncCode());
			param.setFieldName(d.getFieldName());
			param.setFieldValue(d.getFieldValue());
			param.setPermissionType(d.getPermissionType());
			param = permissionMapper.selectOne(param);
			if(d.get__status().equals(DTOStatus.ADD)){
				//如果查到值，说明不唯一，报错
				if(param != null){
					throw new CommonException("SYS","创建失败：存在重复值",null);
				}
				try{
					this.insertSelective(iRequest,d);
				}catch(Exception e){
					int type = ExceptionUtil.getExceptionType(e);
					if(type == 1){
						throw new CommonException("SYS","创建失败：存在重复值",null);
					}else{
						throw e;
					}
				}
				
			}else if(d.get__status().equals(DTOStatus.UPDATE)){
				if(param != null && !param.get_token().equals(d.get_token())){
					throw new CommonException("SYS","更新失败：存在重复值",null);
				}
				this.updateByPrimaryKeySelective(iRequest,d);
			}
			param = new SysOperatePermission();
		}
		return data;
	}
	

}