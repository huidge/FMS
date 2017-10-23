package clb.core.channel.job;

import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.mapper.CnlChannelContractMapper;
import clb.core.notification.service.INtnNotificationService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.job.AbstractJob;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hand on 2017/7/27.
 */
public class CnlContractRateJob extends AbstractJob {

    public static Log log = LogFactory.getLog(CnlContractRateJob.class);

    @Autowired
    CnlChannelContractMapper cnlChannelContractMapper;

    @Autowired
    ClbUserMapper clbUserMapper;

    @Autowired
    private INtnNotificationService ntnNotificationService;

    private Exception exception = null;
    private Object result;


    @Override
    public void safeExecute(JobExecutionContext jobExecutionContext) throws Exception {
        IRequest iRequest = RequestHelper.getCurrentRequest(true);
            //查询当日上一天新增的数据
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(new Date());
        List<CnlChannelContract> list = cnlChannelContractMapper.queryByDate(dateString);
        for(CnlChannelContract dto:list){
            Map sendNoticeMap=new HashMap();
            sendNoticeMap.put("typeCode", dto.getPartyType());
            sendNoticeMap.put("partyName", dto.getPartyName());
            Long userId = null;
            List<ClbUser> userList = queryUserByParam("CHANNEL", dto.getChannelId());
            if (CollectionUtils.isEmpty(userList)) {
                throw new RuntimeException("查询不到渠道用户,channelId:" + dto.getChannelId());
            } else {
                userId = userList.get(0).getUserId();
            }
            if(dto.getRate1()!=null){
                ntnNotificationService.sendNotification(iRequest,userId,"FL0001" , sendNoticeMap);
            }else if(dto.getRateLevelId()!=null){
                ntnNotificationService.sendNotification(iRequest,userId,"FL0002" , sendNoticeMap);
            }
        }
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
        return clbUserMapper.select(user);
    }

}
