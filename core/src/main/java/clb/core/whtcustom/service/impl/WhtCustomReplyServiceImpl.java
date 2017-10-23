package clb.core.whtcustom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.wecorp.dto.WecorpResponseDTO;
import clb.core.whtcustom.dto.WhtCrKeyword;
import clb.core.whtcustom.dto.WhtCrKeywordRule;
import clb.core.whtcustom.dto.WhtCustomReply;
import clb.core.whtcustom.dto.WhtOfficialAccountCfg;
import clb.core.whtcustom.mapper.WhtCrKeywordMapper;
import clb.core.whtcustom.mapper.WhtCrKeywordRuleMapper;
import clb.core.whtcustom.mapper.WhtCustomReplyMapper;
import clb.core.whtcustom.mapper.WhtOfficialAccountCfgMapper;
import clb.core.whtcustom.service.IWhtCustomReplyService;

@Service
@Transactional
public class WhtCustomReplyServiceImpl extends BaseServiceImpl<WhtCustomReply> implements IWhtCustomReplyService{
	@Autowired
    private WhtCustomReplyMapper whtCustomReplyMapper;
	@Autowired
	private WhtCrKeywordMapper WhtCrKeywordMapper;
	@Autowired
	private WhtCrKeywordRuleMapper WhtCrKeywordRuleMapper;
	@Autowired
	private WhtOfficialAccountCfgMapper WhtOfficialAccountCfgMapper;
	
	@Override
	public List<WhtCustomReply> selectAllField(IRequest requestContext, WhtCustomReply whtCustomReply, int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		List<WhtCustomReply> whtCustomReply2 = whtCustomReplyMapper.selectAllField(whtCustomReply);
		return whtCustomReply2;
	}
	
	@Override
	public List<WhtCustomReply> selectAll(IRequest requestContext, WhtCustomReply whtCustomReply) {
		List<WhtCustomReply> whtCustomReply2 = whtCustomReplyMapper.selectAllField(whtCustomReply);
		return whtCustomReply2;
	}

	@Override
	public WecorpResponseDTO subscribeReplyMsg(String appid) throws Exception {
		WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
		WhtOfficialAccountCfg whtOfficialAccountCfg = new WhtOfficialAccountCfg();
		if(!"".equals(appid) && appid!=null){
			whtOfficialAccountCfg.setBackgroundAppid(appid);
			List<WhtOfficialAccountCfg> selectAllField = WhtOfficialAccountCfgMapper.selectAllField(whtOfficialAccountCfg);
			for (WhtOfficialAccountCfg whtOfficialAccountCfg2 : selectAllField) {
				WhtCustomReply subscribeReply = whtCustomReplyMapper.subscribeReply(whtOfficialAccountCfg2.getOriginalId());
				if(!"".equals(subscribeReply.getAddContent())){
					wecorpResponseDTO.setType("text");
					wecorpResponseDTO.setContent(subscribeReply.getAddContent());
					break;
				}else if(!"".equals(subscribeReply.getAddPictureId())){
					wecorpResponseDTO.setType("image");
					wecorpResponseDTO.setMediaId(subscribeReply.getAddPictureId());
					break;
				}else if(!"".equals(subscribeReply.getAddVoiceId())){
					wecorpResponseDTO.setType("voice");
					wecorpResponseDTO.setMediaId(subscribeReply.getAddVoiceId());
					break;
				}else if(!"".equals(subscribeReply.getAddVideoId())){
					wecorpResponseDTO.setType("video");
					wecorpResponseDTO.setMediaId(subscribeReply.getAddVideoId());
					break;
				}
			};
		}else{
			wecorpResponseDTO.setContent("入参不能为空");
			wecorpResponseDTO.setType("text");
		}
		return wecorpResponseDTO;
	}


	@Override
	public List<WecorpResponseDTO> keywordkReplyMsg(String appid, String keyword) throws Exception {
		List<WecorpResponseDTO> WecorpResponseDTOList = new ArrayList<>();
		WecorpResponseDTO wecorpResponseDTO2 = new WecorpResponseDTO();
		WhtCrKeyword whtCrKeywordTemp = null;
		List<WhtCrKeyword> keywordkReplyMsg = new ArrayList<>();
		if(!"".equals(keyword)){
			keywordkReplyMsg = WhtCrKeywordMapper.keywordkReplyMsg(keyword);
		}else{
			wecorpResponseDTO2.setContent("关键字不能为空");
			wecorpResponseDTO2.setType("text");
			WecorpResponseDTOList.add(wecorpResponseDTO2);
			return WecorpResponseDTOList;
			
		}
		if(!CollectionUtils.isEmpty(keywordkReplyMsg)){
			for (WhtCrKeyword whtCrKeyword : keywordkReplyMsg) {
				if("Y".equals(whtCrKeyword.getMatchingFlag())){//全匹配
					if((whtCrKeyword.getKeywordValue()).equals(keyword)){
						whtCrKeywordTemp = whtCrKeyword;
						break;
					}
				}else{//不全匹配
					whtCrKeywordTemp = whtCrKeyword;
					break;
				}
			};
		}
		List<WhtCrKeywordRule> selectAllField = new ArrayList<>();
		if(whtCrKeywordTemp!=null){
			WhtCrKeywordRule whtCrKeywordRule = new WhtCrKeywordRule();
			whtCrKeywordRule.setRuleId(whtCrKeywordTemp.getRuleId());
			selectAllField = WhtCrKeywordRuleMapper.selectAllField(whtCrKeywordRule);
			for (WhtCrKeywordRule whtCrKeywordRule2 : selectAllField) {
				//不全匹配的情况下，回复的优先级：文字>图片>图文>语音>视频
				if("N".equals(whtCrKeywordRule2.getReplyAllFlag())){
					WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
					if(!"".equals(whtCrKeywordRule2.getContent())){
						wecorpResponseDTO.setType("text");
						wecorpResponseDTO.setContent(whtCrKeywordRule2.getContent());
					}else if(!"".equals(whtCrKeywordRule2.getRulePictureId())){
						wecorpResponseDTO.setType("image");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRulePictureId());
					}else if(!"".equals(whtCrKeywordRule2.getRulePictureText())){
						wecorpResponseDTO.setType("news");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRulePictureText());
					}else if(!"".equals(whtCrKeywordRule2.getRuleVoiceId())){
						wecorpResponseDTO.setType("voice");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRuleVoiceId());
					}else if(!"".equals(whtCrKeywordRule2.getRuleVideoId())){
						wecorpResponseDTO.setType("video");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRuleVideoId());
					}
					WecorpResponseDTOList.add(wecorpResponseDTO);
				}else{
					if(!"".equals(whtCrKeywordRule2.getRuleVideoId())){
						WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
						wecorpResponseDTO.setType("video");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRuleVideoId());
						WecorpResponseDTOList.add(wecorpResponseDTO);
					}
					if(!"".equals(whtCrKeywordRule2.getRuleVoiceId())){
						WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
						wecorpResponseDTO.setType("voice");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRuleVoiceId());
						WecorpResponseDTOList.add(wecorpResponseDTO);
					}
					if(!"".equals(whtCrKeywordRule2.getRulePictureText())){
						WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
						wecorpResponseDTO.setType("news");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRulePictureText());
						WecorpResponseDTOList.add(wecorpResponseDTO);
					}
					if(!"".equals(whtCrKeywordRule2.getRulePictureId())){
						WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
						wecorpResponseDTO.setType("image");
						wecorpResponseDTO.setMediaId(whtCrKeywordRule2.getRulePictureId());
						WecorpResponseDTOList.add(wecorpResponseDTO);
					}
					if(!"".equals(whtCrKeywordRule2.getContent())){
						WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
						wecorpResponseDTO.setType("text");
						wecorpResponseDTO.setContent(whtCrKeywordRule2.getContent());
						WecorpResponseDTOList.add(wecorpResponseDTO);
					}
				}
			};
			//没有匹配到关键字，就回复自动回复的内容
		}
		if(WecorpResponseDTOList.size()==0){
			WhtOfficialAccountCfg whtOfficialAccountCfg = new WhtOfficialAccountCfg();
			WecorpResponseDTO wecorpResponseDTO = new WecorpResponseDTO();
			whtOfficialAccountCfg.setBackgroundAppid(appid);
			List<WhtOfficialAccountCfg> selectAll = WhtOfficialAccountCfgMapper.selectAllField(whtOfficialAccountCfg);
			if(selectAll.size()>0){
				WhtCustomReply WhtCustomReply = whtCustomReplyMapper.subscribeReply(selectAll.get(0).getOriginalId());
				//WhtCustomReply WhtCustomReply = whtCustomReplyMapper.subscribeReply("gh_7ccb671d867f");
				if(WhtCustomReply != null){
					if(!"".equals(WhtCustomReply.getAutoContent())){
						wecorpResponseDTO.setType("text");
						wecorpResponseDTO.setContent(WhtCustomReply.getAutoContent());
					}else if(!"".equals(WhtCustomReply.getAutoPictureId())){
						wecorpResponseDTO.setType("image");
						wecorpResponseDTO.setMediaId(WhtCustomReply.getAutoPictureId());
					}else if(!"".equals(WhtCustomReply.getAutoVoiceId())){
						wecorpResponseDTO.setType("voice");
						wecorpResponseDTO.setMediaId(WhtCustomReply.getAutoVoiceId());
					}else if(!"".equals(WhtCustomReply.getAutoVideoId())){
						wecorpResponseDTO.setType("video");
						wecorpResponseDTO.setMediaId(WhtCustomReply.getAutoVideoId());
					}
					WecorpResponseDTOList.add(wecorpResponseDTO);
				}else{
					wecorpResponseDTO2.setContent("暂无配置任何回复信息");
					wecorpResponseDTO2.setType("text");
					WecorpResponseDTOList.add(wecorpResponseDTO2);
					return WecorpResponseDTOList;
				}
			}else{
				wecorpResponseDTO2.setContent("请在财联邦系统配置公众号");
				wecorpResponseDTO2.setType("text");
				WecorpResponseDTOList.add(wecorpResponseDTO2);
				return WecorpResponseDTOList;
			}
		}
		return WecorpResponseDTOList;
	}

	@Override
	public WhtCustomReply subscribeReply(String originalId) {
		WhtCustomReply WhtCustomReply = whtCustomReplyMapper.subscribeReply(originalId);
		return WhtCustomReply;
	}
}