package clb.core.system.service;
/**
 * Created by jiaolong.li on 2017-05-10.
 */
import clb.core.system.dto.ClbUser;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import clb.core.system.dto.ClbEmployee;

import java.util.List;

public interface IClbEmployeeService extends IBaseService<ClbEmployee>, ProxySelf<IClbEmployeeService>{

    public List<ClbEmployee> selectAllFields(IRequest requestContext, ClbEmployee clbEmployee, int page, int pageSize);
    /**
     * 通过关联方id查询员工信息
     * @param requestCtx
     * @param clbEmployee1
     * @return
     */
	public List<ClbEmployee> queryEmployeeByOwnershipId(IRequest requestCtx, ClbEmployee clbEmployee1);
}