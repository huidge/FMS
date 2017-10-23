package clb.core.forecast.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clb.core.notification.service.INtnNotificationService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import org.apache.commons.collections.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ISequenceService;
import clb.core.forecast.dto.FetChannelCheck;
import clb.core.forecast.dto.FetQuestion;
import clb.core.forecast.dto.FetQuestionLine;
import clb.core.forecast.mapper.FetChannelCheckMapper;
import clb.core.forecast.mapper.FetQuestionMapper;
import clb.core.forecast.service.IFetQuestionLineService;
import clb.core.forecast.service.IFetQuestionService;

@Service
@Transactional
public class FetQuestionServiceImpl extends BaseServiceImpl<FetQuestion> implements IFetQuestionService{

	@Autowired
	private FetQuestionMapper questionMapper;

	@Autowired
	private FetChannelCheckMapper channelCheckMapper;

	@Autowired
	private ISequenceService sequenceService;

	@Autowired
	private IFetQuestionLineService questionLineService;

	@Autowired
	private ISysFileService fileService;

	@Autowired
	private INtnNotificationService ntnNotificationService;

    @Autowired
    private ClbUserMapper clbUserMapper;

	private static final Long ADMIN_ROLE_ID = 10001L;

	@Override
	public List<FetQuestion> getData(FetQuestion dto,int page,int pagesize) {
		PageHelper.startPage(page,pagesize);
		return questionMapper.getData(dto);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public FetQuestion saveData(IRequest iRequest,FetQuestion dto) throws CommonException {
		//判断渠道对账数据状态
		FetChannelCheck channelCheck = new FetChannelCheck();
		//状态为新建时
		//获取内容
		List<FetQuestionLine> lines = dto.getLines();
		if(DTOStatus.ADD.equals(dto.get__status())){
			channelCheck.setCheckPeriod(dto.getCheckPeriod());
			channelCheck.setPaymentCompanyType(dto.getPaymentCompanyType());
			channelCheck.setPaymentCompanyId(dto.getPaymentCompanyId());
			channelCheck.setReceiveCompanyType("CHANNEL");
			channelCheck.setReceiveCompanyId(dto.getChannelId());
			channelCheck.setVersion(dto.getVersion());
			List<FetChannelCheck> data = channelCheckMapper.select(channelCheck);
			if(CollectionUtils.isNotEmpty(data)){
				channelCheck = data.get(0);
			}
			if("YSX".equals(channelCheck.getStatus()) || "YQR".equals(channelCheck.getStatus())){
				throw new CommonException("FET","该问题的对账数据已失效或已确认，无法提交!",null);
			}
			String questionNumber = sequenceService.getSequence("FEEDBACK");
			dto.setQuestionNumber(questionNumber);
			try{
				self().insertSelective(iRequest,dto);
			}catch(Exception e){
				throw new RuntimeException(new CommonException("FET","数据重复，提交失败!",null));
			}

		}else{
			FetQuestion param = new FetQuestion();
			param.setQuestionId(lines.get(0).getQuestionId());
			param = questionMapper.selectByPrimaryKey(param);
			//已解决，报错
			if("YJJ".equals(param.getStatus())){
				throw new CommonException("FET","该问题已解决，无法提交，请刷新页面!",null);
			}
			channelCheck.setCheckPeriod(param.getCheckPeriod());
			channelCheck.setPaymentCompanyType(param.getPaymentCompanyType());
			channelCheck.setPaymentCompanyId(param.getPaymentCompanyId());
			channelCheck.setReceiveCompanyType("CHANNEL");
			channelCheck.setReceiveCompanyId(param.getChannelId());
			channelCheck.setVersion(param.getVersion());
			List<FetChannelCheck> data = channelCheckMapper.select(channelCheck);
			if(CollectionUtils.isNotEmpty(data)){
				channelCheck = data.get(0);
			}
			if("YSX".equals(channelCheck.getStatus()) || "YQR".equals(channelCheck.getStatus())){
				throw new CommonException("FET","该问题的对账数据已失效或已确认，无法提交!",null);
			}
			//状态是未解决，且是管理员回复
			if("WJJ".equals(param.getStatus()) && ADMIN_ROLE_ID.equals(iRequest.getRoleId())){
				dto.setStatus("JJZ");
			}
			/* 发送问题反馈通知 add by 谈晟 */
			if (ADMIN_ROLE_ID.equals(iRequest.getRoleId())) { // 管理员回复
				// 设置通知参数
                Map<String, String> sendNoticeMap = new HashMap<>();
                FetQuestion fq = questionMapper.selectByPrimaryKey(dto.getLines().get(0).getQuestionId());
                sendNoticeMap.put("questionNumber", fq.getQuestionNumber()); // 问题编号
                ntnNotificationService.sendNotification(iRequest,queryUserByChannel("CHANNEL",fq.getChannelId()),"DZD0002",sendNoticeMap);
			}
			dto.setQuestionId(lines.get(0).getQuestionId());
			self().updateByPrimaryKeySelective(iRequest, dto);
		}
		//获取删除文件
		List<SysFile> files = dto.getDeleteFiles();
		for(SysFile file:files){
			fileService.delete(iRequest, file);
		}
		if(CollectionUtils.isEmpty(lines)){
			throw new CommonException("FET","请输入文字或上传文件",null);
		}
		for(FetQuestionLine l:lines){
			l.setSubmitDate(new Date());
			if(DTOStatus.ADD.equals(dto.get__status())){
				l.setQuestionId(dto.getQuestionId());
			}
			questionLineService.insertSelective(iRequest, l);
		}
		return dto;
	}

    /**
     * 查询渠道用户
     * add by 谈晟
     *
     * @param userType
     * @param channelId
     * @return
     */
    private Long queryUserByChannel(String userType, Long channelId) {
        ClbUser user = new ClbUser();
        user.setUserType(userType);
        user.setRelatedPartyId(channelId);
        List<ClbUser> userList = clbUserMapper.select(user);
        Long userId;
        if (CollectionUtils.isEmpty(userList)) {
            throw new RuntimeException("查询不到渠道用户,channelId:" + channelId);
        } else {
            userId = userList.get(0).getUserId();
            return userId;
        }
    }

}