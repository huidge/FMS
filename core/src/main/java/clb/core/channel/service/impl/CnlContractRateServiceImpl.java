package clb.core.channel.service.impl;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlContractRateMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.service.ICnlContractRateService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jun.zhao@hand-china.com
 * @version 1.0
 * @name CnlChannelContactServiceImpl
 * @description 渠道合约费率信息 service 接口实现类
 * @date 2017/4/25
 */
@Service
@Transactional
public class CnlContractRateServiceImpl extends BaseServiceImpl<CnlContractRate> implements ICnlContractRateService {

    @Autowired
    private CnlContractRateMapper cnlContractRateMapper;

    /**
     * 费率表汇总查询
     *
     * @param request
     * @param cnlContractRate
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlContractRate> queryRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlContractRateMapper.queryRate(cnlContractRate);
    }
    
    /**
     * 查询渠道费率
     *
     * @param request
     * @param cnlContractRate
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlContractRate> queryChannelRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlContractRateMapper.queryChannelRate(cnlContractRate);
    }
    
    /**
     * 查询供应商费率
     *
     * @param request
     * @param cnlContractRate
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<CnlContractRate> querySupplierRate(IRequest request, CnlContractRate cnlContractRate, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlContractRateMapper.querySupplierRate(cnlContractRate);
    }

    /**
     * 查询团队成员对应的对应的佣金分成（上级渠道查下级）
     *
     * @param request
     * @param
     * @return
     */
    @Override
    public List<CnlContractRate> queryWsChannelCommission(IRequest request, CnlChannelContract contract) {

        return cnlContractRateMapper.queryTeamCommission(contract);
    }

    /**
     * 更新渠道合约佣金分成
     *
     * @param request
     * @param contract
     * @param rateList
     */
    @Override
    public void submitWsCommissionProportion(IRequest request,
                                             CnlChannelContract contract,
                                             List<CnlContractRate> rateList) {

        for (CnlContractRate contractRate : rateList) {

            if (contractRate.getChannelRateId() != null) {

                cnlContractRateMapper.updateByPrimaryKeySelective(contractRate);
            } else {
                contractRate.setChannelContractId(contract.getChannelContractId());
                cnlContractRateMapper.insertSelective(contractRate);
            }
        }
    }
}