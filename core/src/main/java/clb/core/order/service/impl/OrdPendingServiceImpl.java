package clb.core.order.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.service.ISequenceService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdPending;
import clb.core.order.dto.OrdTradeRoute;
import clb.core.order.mapper.OrdPendingMapper;
import clb.core.order.mapper.OrdTradeRouteMapper;
import clb.core.order.service.IOrdPendingService;
import clb.core.order.service.IOrdTradeRouteService;
import clb.core.system.dto.ClbUser;

@Service
@Transactional
public class OrdPendingServiceImpl extends BaseServiceImpl<OrdPending> implements IOrdPendingService{

    @Autowired
    private OrdPendingMapper ordPendingMapper;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private ISequenceService sequenceService;
    
    @Autowired
    private OrdTradeRouteMapper ordTradeRouteMapper;
	
	@Autowired
	private IOrdTradeRouteService ordTradeRouteService;

    /**
     * 查询
     * @param request
     * @param ordPending
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdPending> queryOrdPending(IRequest request, OrdPending ordPending, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);

        ordPending.setCurrentUserId(request.getUserId());
        List<OrdPending> pengdingList = new ArrayList<OrdPending>();
        pengdingList = ordPendingMapper.queryOrdPending(ordPending);

        for (OrdPending pengding : pengdingList) {
            if (request.getUserId().toString().equals(pengding.getDealPerson())) {
                pengding.setMatchUserFlag("Y");
            }
          //对接业务行政
			OrdTradeRoute preUser = new OrdTradeRoute();
			preUser.setOrderId(pengding.getOrderId());
			List<OrdTradeRoute> preUserList = ordTradeRouteMapper.queryPreUserId(preUser);
			if (!CollectionUtils.isEmpty(preUserList)) {
				pengding.setPreName(preUserList.get(0).getPreName());
			}
			//所属一级
			if(pengding.getOrderId() != null) {
				pengding.setStairSell(ordTradeRouteService.queryStairSellByOrderId(request,pengding.getOrderId()));
			}
            
        }
        return pengdingList;
    }

    /**
     * 查询
     * @param request
     * @param ordPending
     * @return
     */
    @Override
    public List<OrdPending> queryWsOrdPending(IRequest request, OrdPending ordPending) {
        return ordPendingMapper.queryWsOrdPending(ordPending);
    }

    /**
     * 查询
     * @param request
     * @param ordPending
     * @return
     */
    @Override
    public List<OrdPending> queryWsPersonalOrdPending(IRequest request, OrdPending ordPending) {
        return ordPendingMapper.queryWsPersonalOrdPending(ordPending);
    }

    /**
     * 查询
     * @param request
     * @param ordPending
     * @return
     */
    @Override
    public Long queryOrdPendingTotle(IRequest request, OrdPending ordPending) {
        return ordPendingMapper.queryOrdPendingTotle(ordPending);
    }

    /**
     * 查询
     * @param request
     * @param ordPending
     * @return
     */
    @Override
    public List<OrdPending> queryWsTeamOrdPending(IRequest request, OrdPending ordPending) {
        return ordPendingMapper.queryWsTeamOrdPending(ordPending);
    }

    /**
     * 订单Pending提交
     * @param request
     * @param ordPendings
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<OrdPending> pendingBatchUpdate(IRequest request, List<OrdPending> ordPendings) {

        for (OrdPending ordPending : ordPendings) {
            if (ordPending.getDealPerson() == null) {
                ordPending.setDealPerson(request.getUserId().toString());
            }
            if (ordPending.getPendingNumber() == null) {
                ordPending.setPendingNumber(sequenceService.getSequence("PENDING"));
            }
        }
        this.batchUpdate(request, ordPendings);
        return ordPendings;
    }

    /**
     * 统计订单下的pending数量
     *
     * @param orderId
     * @return
     */
    @Override
    public Long countPendingByOrderId(Long orderId) {
        return ordPendingMapper.countPendingByOrderId(orderId);
    }

    /**
     * 查询处理人
     * @param ordOrder
     * @return
     */
    public List<ClbUser> queryDealUser(OrdOrder ordOrder){
        return ordPendingMapper.queryDealUser(ordOrder);
    }

    /**
     * 查询
     * @param request
     * @param ordPending
     * @return
     */
    @Override
    public List<OrdPending> queryNotClosed(IRequest request, OrdPending ordPending) {
        return ordPendingMapper.queryNotClosed(ordPending);
    }
}