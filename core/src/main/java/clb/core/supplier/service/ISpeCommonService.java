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
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;

public interface ISpeCommonService{

	List<ClbCodeValue> getCodeValuesByParentId(IRequest iRequest,ClbCode dto,String parentValue);

}