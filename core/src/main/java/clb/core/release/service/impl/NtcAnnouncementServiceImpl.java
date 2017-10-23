package clb.core.release.service.impl;

import clb.core.common.utils.CommonUtil;
import clb.core.release.dto.NtcAnnouncement;
import clb.core.release.mapper.NtcArticleMapper;
import clb.core.release.service.INtcAnnouncementService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NtcAnnouncementServiceImpl extends BaseServiceImpl<NtcAnnouncement> implements INtcAnnouncementService {

	
	@Autowired
	private NtcArticleMapper mapper;

	// 公告更新状态
	@Override
	public ResponseData updateStatus(IRequest request, NtcAnnouncement dto) {
		ResponseData response=new ResponseData(true);
		try {
			if (dto.getAnnouncementId()==null || dto.getAnnouncementId().equals("")) {
				insertSelective(request,dto);
			} else {
				updateByPrimaryKeySelective(request, dto);
			}
		} catch (Exception e) {
			response.setSuccess(false);
			response.setMessage(e.getMessage());
			CommonUtil.printStackTraceToStr(e);
		}
		return response;
	}

}