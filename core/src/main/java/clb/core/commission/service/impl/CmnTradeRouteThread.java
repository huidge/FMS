package clb.core.commission.service.impl;

import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.dto.CmnTradeRoute;
import clb.core.commission.mapper.CmnChannelCommissionMapper;
import clb.core.commission.mapper.CmnSupplierCommissionMapper;
import clb.core.commission.mapper.CmnTradeRouteMapper;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.commission.service.ICmnSupplierCommissionService;
import clb.core.commission.service.ICmnTradeRouteService;
import clb.core.common.utils.SpringBeanUtil;
import clb.core.common.utils.SpringConfigTool;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @jiaolong.li@hand-china.com
 * @create 2017-08-12 20:17
 **/
public class CmnTradeRouteThread implements Runnable, Serializable {

    private ICmnChannelCommissionService channelCommissionService;

    private ICmnSupplierCommissionService supplierCommissionService;

    private ICmnTradeRouteService routeService;

    private List<CmnChannelCommission> commissionList;

    private Long minLineId;

    private Long batchQty;

    public CmnTradeRouteThread(Long minLineId,
                               Long batchQty,
                               ICmnChannelCommissionService channelCommissionService,
                               ICmnSupplierCommissionService supplierCommissionService,
                               ICmnTradeRouteService routeService){

        this.channelCommissionService = channelCommissionService;
        this.supplierCommissionService = supplierCommissionService;
        this.routeService = routeService;
        this.minLineId = minLineId;
        this.batchQty = batchQty;
    }

    @Override
    public void run() {

        IRequest request = RequestHelper.newEmptyRequest();

        CmnChannelCommission paraCmn = new CmnChannelCommission();
        paraCmn.setStartLineId(this.minLineId);
        paraCmn.setBatchCount(new Long(batchQty));
        this.commissionList = channelCommissionService.selectBatchChannelCmn(paraCmn);

        if (this.commissionList.size() > 0) {
            for (CmnChannelCommission channelCommission : this.commissionList) {

                CmnTradeRoute tradeRoute = new CmnTradeRoute();
                tradeRoute.setChannelCommissionLineId(channelCommission.getLineId());
                tradeRoute.setCompanyCommissionLineId(channelCommission.getLineId());
                tradeRoute.setSeqNum(1L);
                tradeRoute.setCompanyType("CHANNEL");
                tradeRoute.setCompanyId(channelCommission.getChannelId());
                tradeRoute.setChildRouteId(null);

                // who字段
                tradeRoute.setObjectVersionNumber(1L);
                routeService.insertSelective(request, tradeRoute);

                String dealPath = "C" + channelCommission.getChannelId();
                String dealPathName = "C" + channelCommission.getChannelName();

                if (channelCommission.getParentLineId() != null) {
                    // 非顶级渠道 继续找渠道佣金表数据
                    processRoute(channelCommission.getLineId(),
                            2L,
                            tradeRoute.getRouteId(),
                            channelCommission.getParentLineId(),
                            null,
                            dealPath,
                            dealPathName);

                } else {
                    // 顶级渠道 需关联供应商佣金表
                    Long supplierCommissionId = channelCommission.getSupplierCommissionId();

                    // 查询对应的供应商佣金表数据SpeCommissionNum
                    CmnSupplierCommission speCommission = new CmnSupplierCommission();
                    speCommission.setLineId(supplierCommissionId);
                    if (supplierCommissionService.selectByPrimaryKey(request, speCommission) != null) {
                        speCommission = supplierCommissionService.selectByPrimaryKey(request, speCommission);

                        processRoute(channelCommission.getLineId(),
                                2L,
                                tradeRoute.getRouteId(),
                                null,
                                speCommission.getCommissionNum(),
                                dealPath,
                                dealPathName);
                    }

                }

                CmnTradeRoute paraTradeRoute = new CmnTradeRoute();
                paraTradeRoute.setChannelCommissionLineId(channelCommission.getLineId());
                List<CmnTradeRoute> dealPathList = routeService.queryDealPath(paraTradeRoute);
                if (dealPathList != null && dealPathList.size() > 0) {
                    paraTradeRoute = dealPathList.get(0);
                    routeService.updateDealPath(paraTradeRoute);
                    routeService.updateCommissionDealPath(paraTradeRoute);
                }

            }
        }
    }

    public void processRoute(Long commissionLineId,
                             Long seqNum,
                             Long childRouteId,
                             Long parentCommissionLineId,
                             String parentSpeCommissionNum,
                             String dealPath,
                             String dealPathName) {

        IRequest request = RequestHelper.newEmptyRequest();

        if (parentCommissionLineId != null) {
            CmnChannelCommission channelCommission = new CmnChannelCommission();
            channelCommission.setLineId(parentCommissionLineId);
            List<CmnChannelCommission> channelCommissionList = channelCommissionService.selectCommissionById(channelCommission);
            if (channelCommissionList != null && channelCommissionList.size() > 0) {
                channelCommission = channelCommissionList.get(0);

                CmnTradeRoute tradeRoute = new CmnTradeRoute();
                tradeRoute.setChannelCommissionLineId(commissionLineId);
                tradeRoute.setCompanyCommissionLineId(channelCommission.getLineId());
                tradeRoute.setSeqNum(seqNum);
                tradeRoute.setCompanyType("CHANNEL");
                tradeRoute.setCompanyId(channelCommission.getChannelId());
                tradeRoute.setChildRouteId(childRouteId);

                // who字段
                tradeRoute.setObjectVersionNumber(1L);
                routeService.insertSelective(request, tradeRoute);

                dealPath = dealPath + ".C" + channelCommission.getChannelId();
                dealPathName = dealPathName + ".C" + channelCommission.getChannelName();

                if (channelCommission.getParentLineId() != null) {

                    seqNum = seqNum + 1;
                    // 调用自己
                    processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                            channelCommission.getParentLineId(), null,
                            dealPath,
                            dealPathName);
                } else {
                    // 顶级渠道 需关联供应商佣金表
                    Long supplierCommissionId = channelCommission.getSupplierCommissionId();

                    // 查询对应的供应商佣金表数据SpeCommissionNum
                    CmnSupplierCommission speCommission = new CmnSupplierCommission();
                    speCommission.setLineId(supplierCommissionId);
                    if (supplierCommissionService.selectByPrimaryKey(request, speCommission) != null) {
                        speCommission = supplierCommissionService.selectByPrimaryKey(request, speCommission);

                        seqNum = seqNum + 1;
                        processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                                null, speCommission.getCommissionNum(),
                                dealPath,
                                dealPathName);
                    }
                }
            }

        }

        // 供应商佣金表情况
        if (parentSpeCommissionNum != null
                && !"".equals(parentSpeCommissionNum)) {
            CmnSupplierCommission speCommission = new CmnSupplierCommission();
            speCommission.setCommissionNum(parentSpeCommissionNum);
            List<CmnSupplierCommission> speCommissionList = supplierCommissionService.selectByCommissionNum(speCommission);
            if (speCommissionList != null && speCommissionList.size() > 0) {
                speCommission = speCommissionList.get(0);

                if (speCommission.getParentCommissionNum() != null
                        && !"".equals(speCommission.getParentCommissionNum())) {

                    CmnTradeRoute tradeRoute = new CmnTradeRoute();
                    tradeRoute.setChannelCommissionLineId(commissionLineId);

                    // 取本级获取的佣金数据（即上级放出的佣金值）
                    if (speCommissionList.get(0).getParentCommissionNum() != null
                            && !"".equals(speCommissionList.get(0).getParentCommissionNum())) {
                        CmnSupplierCommission paraCommission = new CmnSupplierCommission();
                        paraCommission.setCommissionNum(speCommissionList.get(0).getParentCommissionNum());
                        List<CmnSupplierCommission> incomeCommissionList = supplierCommissionService.selectByCommissionNum(paraCommission);
                        if (incomeCommissionList != null && incomeCommissionList.size() > 0) {
                            tradeRoute.setCompanyCommissionLineId(incomeCommissionList.get(0).getLineId());
                        }
                    } else {
                        Long ytId = supplierCommissionService.selectYtCmnId(speCommissionList.get(0).getLineId());
                        tradeRoute.setCompanyCommissionLineId(ytId);
                    }

                    tradeRoute.setSeqNum(seqNum);
                    tradeRoute.setCompanyType("SUPPLIER");
                    tradeRoute.setCompanyId(speCommission.getSupplierId());
                    tradeRoute.setChildRouteId(childRouteId);
                    // who字段
                    tradeRoute.setObjectVersionNumber(1L);
                    routeService.insertSelective(request, tradeRoute);

                    dealPath = dealPath + ".S" + speCommission.getSupplierId();
                    dealPathName = dealPathName + ".S" + speCommission.getSupplierName();

                    seqNum = seqNum + 1;
                    processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                            null, speCommission.getParentCommissionNum(),
                            dealPath,
                            dealPathName);

                } else {
                    // 顶级供应商
                    CmnSupplierCommission topCommission = new CmnSupplierCommission();
                    topCommission.setLineId(speCommission.getLineId());
                    topCommission = supplierCommissionService.selectAllFields(topCommission).get(0);

                    CmnTradeRoute tradeRoute = new CmnTradeRoute();
                    tradeRoute.setChannelCommissionLineId(commissionLineId);

                    // 通过条件取源头佣金值
                    Long ytId = supplierCommissionService.selectYtCmnId(topCommission.getLineId());
                    tradeRoute.setCompanyCommissionLineId(ytId);
                    //tradeRoute.setCompanyCommissionLineId(topCommission.getLineId());
                    tradeRoute.setSeqNum(seqNum);
                    tradeRoute.setCompanyType("SUPPLIER");
                    tradeRoute.setCompanyId(topCommission.getSupplierId());
                    tradeRoute.setChildRouteId(childRouteId);
                    // who字段
                    tradeRoute.setObjectVersionNumber(1L);
                    routeService.insertSelective(request, tradeRoute);

                    dealPath = dealPath + ".S" + topCommission.getSupplierId();
                    dealPathName = dealPathName + ".S" + topCommission.getSupplierName();

                    seqNum = seqNum + 1;

                    CmnTradeRoute topRoute = new CmnTradeRoute();
                    topRoute.setChannelCommissionLineId(commissionLineId);
                    topRoute.setCompanyCommissionLineId(null);
                    topRoute.setSeqNum(seqNum);
                    topRoute.setCompanyType("SUPPLIER");
                    topRoute.setCompanyId(topCommission.getItemSupplierId());
                    topRoute.setChildRouteId(tradeRoute.getRouteId());
                    // who字段
                    topRoute.setObjectVersionNumber(1L);

                    dealPath = dealPath + ".S" + topCommission.getItemSupplierId();
                    dealPathName = dealPathName + ".S" + topCommission.getItemSupplierName();
                    topRoute.setDealPath(dealPath);
                    topRoute.setDealPathName(dealPathName);
                    routeService.insertSelective(request, topRoute);
                }
            }

        }

    }
}