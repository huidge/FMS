package clb.core.channel.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.channel.dto.CnlContractRateHistory;
import clb.core.channel.mapper.CnlContractRateHistoryMapper;
import clb.core.channel.service.ICnlContractRateHistoryService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wanjun.feng@hand-china.com
 * @date 2017/9/18
 */
@Service
@Transactional
public class CnlContractRateHistoryServiceImpl extends BaseServiceImpl<CnlContractRateHistory> implements ICnlContractRateHistoryService {
    @Autowired
    private CnlContractRateHistoryMapper cnlContractRateHistoryMapper;
    
    @Override
    public List<CnlContractRateHistory> queryRateHis(IRequest request, CnlContractRateHistory cnlContractRateHistory, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        return cnlContractRateHistoryMapper.queryRateHis(cnlContractRateHistory);
    }

}