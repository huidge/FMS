package clb.core.channel.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlContractRole;

import java.util.List;

public interface CnlContractRoleMapper extends Mapper<CnlContractRole>{

    /**
     * 利益角色查询
     * @param cnlContractRole
     * @return
     */
    public List<CnlContractRole> queryCnlContractRole(CnlContractRole cnlContractRole);

    /**
     * 介绍人查询
     * @param cnlContractRole
     * @return
     */
    public List<CnlContractRole> queryIntroducer(CnlContractRole cnlContractRole);

    /**
     * 业务行政查询
     * @param cnlContractRole
     * @return
     */
    public List<CnlContractRole> queryAdmin(CnlContractRole cnlContractRole);

}