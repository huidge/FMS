package clb.core.order.service.impl;

import clb.core.common.utils.DateUtil;
import clb.core.notification.service.INtnNotificationService;
import clb.core.order.dto.OrdOrder;
import clb.core.order.dto.OrdPending;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.order.mapper.OrdPendingFollowMapper;
import clb.core.order.mapper.OrdPendingMapper;
import clb.core.order.service.IOrdOrderService;
import clb.core.order.service.IOrdPendingService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbCodeService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import clb.core.order.dto.OrdPendingFollow;
import clb.core.order.service.IOrdPendingFollowService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional
public class OrdPendingFollowServiceImpl extends BaseServiceImpl<OrdPendingFollow> implements IOrdPendingFollowService{

    @Autowired
    private OrdPendingFollowMapper ordPendingFollowMapper;

    @Autowired
    private IOrdPendingService ordPendingService;

    @Autowired
    private OrdPendingMapper ordPendingMapper;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private IOrdOrderService ordOrderService;

    @Autowired
    private OrdOrderMapper ordOrderMapper;

    @Autowired
    private INtnNotificationService ntnNotificationService;

    @Autowired
    private ClbUserMapper userMapper;

    @Autowired
    private IClbCodeService clbCodeService;

    /**
     * 查询
     * @param request
     * @param ordPendingFollow
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<OrdPendingFollow> queryOrdPendingFollow(IRequest request, OrdPendingFollow ordPendingFollow, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<OrdPendingFollow> cList = ordPendingFollowMapper.queryOrdPendingFollow(ordPendingFollow);
        for (OrdPendingFollow k : cList) {
            if (k.getFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getFileId());
                if (sysFile != null) {
                    k.set_token(sysFile.get_token());
                }else {
                    k.set_token(null);
                }

            }
        }
        return cList;
    }

    @Override
    public List<OrdPendingFollow> queryWsOrdPendingFollow(IRequest request, OrdPendingFollow ordPendingFollow) {
        List<OrdPendingFollow> cList = ordPendingFollowMapper.queryOrdPendingFollow(ordPendingFollow);
        for (OrdPendingFollow k : cList) {
            if (k.getFileId() != null) {
                SysFile sysFile = sysFileService.selectByPrimaryKey(request, k.getFileId());
                if (sysFile != null) {
                    k.set_token(sysFile.get_token());
                }else {
                    k.set_token(null);
                }

            }
        }
        return cList;
    }

    /****
     * 根据用户类型，以及对应渠道/供应商 去查询用户
     * @param userType
     * @param paramId
     * @return
     */
    public List<ClbUser> queryUserByParam(String userType, Long paramId) {
        ClbUser user = new ClbUser();
        user.setUserType(userType);
        user.setRelatedPartyId(paramId);
        return userMapper.select(user);
    }

    /**
     * 订单Pending提交
     * @param request
     * @param ordPendingFollows
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<OrdPendingFollow> followBatchUpdate(IRequest request, List<OrdPendingFollow> ordPendingFollows) {

        for (OrdPendingFollow ordPendingFollow : ordPendingFollows) {
            ordPendingFollow.setFollowUserId(request.getUserId());
            if (ordPendingFollow.getFollowDate() == null ) {
                ordPendingFollow.setFollowDate(new Date());
            }
            if (ordPendingFollow.getStatus() != null || ordPendingFollow.getDealPerson() != null) {

                OrdPending ordPending = new OrdPending();
                ordPending.setPendingId(ordPendingFollow.getPendingId());
                List<OrdPending> oList = ordPendingMapper.select(ordPending);

                List<OrdOrder> ordOrders = new ArrayList<>();
                for (OrdPending k:oList ) {
                    k.set__status("update");
                    k.setStatus(ordPendingFollow.getStatus());
                    k.setDealPerson(ordPendingFollow.getDealPerson());
                    k.setDealEndDate(ordPendingFollow.getDealEndDate());

                    if (ordPendingFollow.getStatus() != null ) {
                        OrdOrder ordOrder = new OrdOrder();
                        ordOrder.setOrderId(k.getOrderId());
                        ordOrder = ordOrderService.selectByPrimaryKey(request, ordOrder);
                        if ("BOND".equals(ordOrder.getOrderType())) {
                            ordOrder.setStatus(ordPendingFollow.getStatus());
                            ordOrder.set__status("update");
                            ordOrders.add(ordOrder);
                        } else if ("INSURANCE".equals(ordOrder.getOrderType())) {
                            OrdOrder ordOrder1 = new OrdOrder();
                            ordOrder1.setOrderId(k.getOrderId());
                            List<OrdOrder> orderList = ordOrderMapper.queryWsOrder(ordOrder1);
                            ordOrder1 = orderList.get(0);

                            Long userId = null;
                            List<ClbUser> userList = queryUserByParam("CHANNEL", ordOrder1.getChannelId());
                            if (CollectionUtils.isEmpty(userList)) {
                                throw new RuntimeException("查询不到渠道用户,channelId:" + ordOrder1.getChannelId());
                            } else {
                                userId = userList.get(0).getUserId();
                            }

                            Map sendNoticeMap=new HashMap();
                            sendNoticeMap.put("item", ordOrder1.getNoticeItem());
                            sendNoticeMap.put("insurant", ordOrder1.getInsurant());
                            sendNoticeMap.put("applicant", ordOrder1.getApplicant());
                            sendNoticeMap.put("policyNumber", ordOrder1.getPolicyNumber());
                            sendNoticeMap.put("orderNumber", ordOrder1.getOrderNumber());
                            sendNoticeMap.put("reserveDate",DateUtil.getChineseDateString(ordOrder1.getReserveDate()));
                            sendNoticeMap.put("productSupplierName", ordOrder1.getProductSupplierName());
                            sendNoticeMap.put("issueDate",DateUtil.getChineseDateString(ordOrder1.getIssueDate()));
                            sendNoticeMap.put("dividendPeriod", ordOrder1.getDividendPeriod());
                            sendNoticeMap.put("signSupplierName", ordOrder1.getSignSupplierName());
                            sendNoticeMap.put("signPerson", ordOrder1.getSignPerson());

                            if (ordOrder1.getReserveDate() != null) {
                                sendNoticeMap.put("reserveDateStr", DateUtil.getDateStr(ordOrder1.getReserveDate(), "yyyy-MM-dd HH:mm"));
                            }
                            if (ordOrder1.getEffectiveDate() != null) {
                                sendNoticeMap.put("effectiveDate", DateUtil.getDateStr(ordOrder1.getEffectiveDate(), "yyyy-MM-dd"));
                            }
                            if (ordOrder1.getRenewalDueDate() != null) {
                                sendNoticeMap.put("renewalDueDate", DateUtil.getDateStr(ordOrder1.getRenewalDueDate(), "yyyy-MM-dd"));
                                sendNoticeMap.put("renewalDueDay", DateUtil.getDateDiff(ordOrder1.getRenewalDueDate(), new Date()));
                            }
                            sendNoticeMap.put("policyAmount",ordOrder1.getPolicyAmount());
                            sendNoticeMap.put("curDate",DateUtil.getDateStr(new Date(), "yyyy-MM-dd HH:mm:ss"));

                            if ("PENDING_MATERIAL".equals(ordPendingFollow.getStatus())) {
                                ntnNotificationService.sendNotification(request,userId,"BD0012" , sendNoticeMap);
                            } else if ("PENDING_HANDLING".equals(ordPendingFollow.getStatus())) {
                                ntnNotificationService.sendNotification(request,userId,"BD0013" , sendNoticeMap);
                            } else if ("PENDING_APPROVING".equals(ordPendingFollow.getStatus())) {
                                ntnNotificationService.sendNotification(request,userId,"BD0014" , sendNoticeMap);
                            } else if ("PENDING_SUBMITTED".equals(ordPendingFollow.getStatus())) {
                                ntnNotificationService.sendNotification(request,userId,"BD0015" , sendNoticeMap);
                            }
                        }


                    }
                }
                ordPendingService.batchUpdate(request, oList);
                ordOrderService.batchUpdate(request,ordOrders);
            }

        }
        this.batchUpdate(request,ordPendingFollows);
        return ordPendingFollows;
    }
}