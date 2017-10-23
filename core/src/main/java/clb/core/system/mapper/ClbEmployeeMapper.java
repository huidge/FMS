package clb.core.system.mapper;
/**
 * Created by jiaolong.li on 2017-05-10.
 */
import clb.core.system.dto.ClbUser;
import com.hand.hap.mybatis.common.Mapper;
import clb.core.system.dto.ClbEmployee;

import java.util.List;

public interface ClbEmployeeMapper extends Mapper<ClbEmployee>{

    public List<ClbEmployee> selectAllFields(ClbEmployee clbEmployee);
    
    public List<ClbEmployee> selectEmployeeByUserType(String userType);
    /**
     *	通过关联方id查询员工id
     * @param clbEmployee1
     * @return
     */
	public List<ClbEmployee> queryEmployeeByOwnershipId(ClbEmployee clbEmployee1);
}