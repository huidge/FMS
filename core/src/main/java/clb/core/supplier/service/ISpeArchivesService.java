package clb.core.supplier.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.system.service.IBaseService;
import clb.core.supplier.dto.SpeArchives;

public interface ISpeArchivesService extends IBaseService<SpeArchives>, ProxySelf<ISpeArchivesService>{

	public List<SpeArchives> getData(IRequest requestContext, SpeArchives dto);
	
	
	public Map<String, Object> Detele(HttpServletRequest request, String fileId, String token,IRequest requestContext)
			throws TokenException;
}