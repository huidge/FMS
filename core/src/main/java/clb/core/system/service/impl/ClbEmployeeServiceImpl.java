package clb.core.system.service.impl;
/**
 * Created by jiaolong.li on 2017-05-10.
 */
import clb.core.system.mapper.ClbEmployeeMapper;
import clb.core.system.mapper.ClbUserMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.system.dto.ClbEmployee;
import clb.core.system.service.IClbEmployeeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClbEmployeeServiceImpl extends BaseServiceImpl<ClbEmployee> implements IClbEmployeeService{

    @Autowired
    private ClbEmployeeMapper clbEmployeeMapper;

    public List<ClbEmployee> selectAllFields(IRequest requestContext, ClbEmployee clbEmployee, int page, int pageSize){
        PageHelper.startPage(page, pageSize);
        return clbEmployeeMapper.selectAllFields(clbEmployee);
    }
    /**
     * 通过关联方id查询员工信息
     */
	@Override
	public List<ClbEmployee> queryEmployeeByOwnershipId(IRequest requestCtx, ClbEmployee clbEmployee1) {
		return clbEmployeeMapper.queryEmployeeByOwnershipId(clbEmployee1);
	}
}