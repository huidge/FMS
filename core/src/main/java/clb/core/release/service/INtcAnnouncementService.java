package clb.core.release.service;

import clb.core.release.dto.NtcAnnouncement;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

public interface INtcAnnouncementService extends IBaseService<NtcAnnouncement>, ProxySelf<INtcAnnouncementService>{

	ResponseData updateStatus(IRequest request, NtcAnnouncement dto);
}