package clb.core.system.service.impl;

import clb.core.system.mapper.ClbCodeValueMapper;
import clb.core.system.mapper.SysUserCustomFunctionMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.system.dto.SysUserCustomFunction;
import clb.core.system.service.ISysUserCustomFunctionService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SysUserCustomFunctionServiceImpl extends BaseServiceImpl<SysUserCustomFunction> implements ISysUserCustomFunctionService {

    @Autowired
    private SysUserCustomFunctionMapper sysUserCustomFunctionMapper;

    /**
     * 查询用户已拥有的自定义功能
     * @param sysUserCustomFunction
     * @return
     */
    public List<SysUserCustomFunction> queryOwnFuncs(SysUserCustomFunction sysUserCustomFunction) {
        return sysUserCustomFunctionMapper.queryOwnFuncs(sysUserCustomFunction);
    }

    /**
     * 查询用户可用的自定义功能
     * @param sysUserCustomFunction
     * @return
     */
    public List<SysUserCustomFunction> queryAvailableFuncs(SysUserCustomFunction sysUserCustomFunction) {
        return sysUserCustomFunctionMapper.queryAvailableFuncs(sysUserCustomFunction);
    }

    /**
     * 删除用户下的自定义功能
     * @param sysUserCustomFunction
     */
    public void deleteFuncs(SysUserCustomFunction sysUserCustomFunction) {
        sysUserCustomFunctionMapper.deleteFuncs(sysUserCustomFunction);
    }

    /**
     * 初始化用户的自定义功能
     * @param sysUserCustomFunction
     */
    public void initFuncs(SysUserCustomFunction sysUserCustomFunction, IRequest requestContext) {

        List<SysUserCustomFunction> funcList = new ArrayList<SysUserCustomFunction>();

        funcList = sysUserCustomFunctionMapper.queryAvailableFuncs(sysUserCustomFunction);

        for (SysUserCustomFunction func : funcList) {
            if ("N".equals(func.getSelectFlag())) {
                func.setObjectVersionNumber(1L);
                /*func.setCreationDate(new Date());
                func.setCreatedBy(-1L);
                func.setLastUpdatedBy(-1L);
                func.setLastUpdateDate(new Date());*/
                func.setUserId(sysUserCustomFunction.getUserId());
                self().insert(requestContext, func);
                //sysUserCustomFunctionMapper.insert(func);
            }
        }
    }

}