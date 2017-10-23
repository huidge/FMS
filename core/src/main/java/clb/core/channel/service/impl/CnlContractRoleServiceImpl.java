package clb.core.channel.service.impl;

import clb.core.channel.mapper.CnlContractRoleMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlContractRole;
import clb.core.channel.service.ICnlContractRoleService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CnlContractRoleServiceImpl extends BaseServiceImpl<CnlContractRole> implements ICnlContractRoleService{

    @Autowired
    private CnlContractRoleMapper cnlContractRoleMapper;

    /**
     * 查询
     * @param request
     * @param cnlContractRole
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlContractRole> queryCnlContractRole(IRequest request, CnlContractRole cnlContractRole, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlContractRoleMapper.queryCnlContractRole(cnlContractRole);
    }
}