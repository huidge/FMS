package clb.core.common.service;

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

/**
 * @name ICommonExportService
 * @description 通用导出方法
 * @author bo.wu@hand-china.com 2017年5月15日21:02:45
 * @version 1.0 
 */
public interface ICommonExportService{

	List<?> selectDatas(IRequest request, String sqlId, Object obj);
}