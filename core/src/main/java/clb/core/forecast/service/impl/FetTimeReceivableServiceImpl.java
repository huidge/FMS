package clb.core.forecast.service.impl;

import clb.core.channel.dto.CnlPerformanceParas;
import clb.core.common.exceptions.CommonException;
import clb.core.common.utils.CommonUtil;
import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.SpringConfigTool;
import clb.core.forecast.dto.*;
import clb.core.forecast.mapper.FetTimeReceivableMapper;
import clb.core.forecast.service.IFetSupposeCommonService;
import clb.core.forecast.utils.SupposeCommonUtil;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import clb.core.forecast.service.IFetTimeReceivableService;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

@Service("FetTimeReceivableServiceImpl")
@Transactional
public class FetTimeReceivableServiceImpl extends BaseServiceImpl<FetTimeReceivable> implements IFetTimeReceivableService {

    @Autowired
    private FetTimeReceivableMapper receivableMapper;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insertTimeData(IRequest iRequest, Long channelId) {

        // 删除本月期间数据
        FetPeriod fetPeriod = new FetPeriod();
        fetPeriod.setPartyId(channelId);
        receivableMapper.deleteCurData(fetPeriod);

        FetSupposeUpdateVersion version = new FetSupposeUpdateVersion();
        //version.setPeriodId(periodId);
        version.setReceiveCompanyType("CHANNEL");
        version.setReceiveCompanyId(channelId);

        try {

            IFetSupposeCommonService service = (IFetSupposeCommonService) SpringConfigTool.getBean("FetSupposeCommonServiceImpl");
            List<FetSupposeCommon> data = service.getAllOrderData(version);

            //应收
            List<FetTimeReceivable> receivables = SupposeCommonUtil.commonListToTimeReceivableList(data);

            self().batchUpdate(iRequest, receivables);
        } catch (Exception e) {
            CommonUtil.printStackTraceToStr(e);
        } finally {
            //transactionManager.commit(transactionStatus);
        }
    }

    @Override
    public List<FetTimeReceivable> queryUnconfirmedAmount(CnlPerformanceParas cnlPerformanceParas) {
        return receivableMapper.queryUnconfirmedAmount(cnlPerformanceParas);
    }
}