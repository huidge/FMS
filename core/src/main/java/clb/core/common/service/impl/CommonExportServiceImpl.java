package clb.core.common.service.impl;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.attachment.FileInfo;
import com.hand.hap.attachment.UpConstants;
import com.hand.hap.attachment.Uploader;
import com.hand.hap.attachment.UploaderFactory;
import com.hand.hap.attachment.dto.AttachCategory;
import com.hand.hap.attachment.dto.Attachment;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.exception.StoragePathNotExsitException;
import com.hand.hap.attachment.exception.UniqueFileMutiException;
import com.hand.hap.attachment.mapper.AttachCategoryMapper;
import com.hand.hap.attachment.service.IAttachCategoryService;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.util.UploadUtil;
import com.hand.hap.security.TokenUtils;

import clb.core.common.exceptions.CommonException;
import clb.core.common.service.ICommonExportService;
import clb.core.common.service.ICommonUploadService;
import clb.core.supplier.mapper.SpeArchivesMapper;
/**
 * @name CommonUploadServiceImpl
 * @description 通用Excel导出方法业务逻辑层
 * @author bo.wu@hand-china.com 2017年4月21日11:19:31
 * @version 1.0 
 */
@Service
@Transactional
public class CommonExportServiceImpl implements ICommonExportService{


	/**
     * 日志记录.
     **/
    private static Logger log = LoggerFactory.getLogger(CommonExportServiceImpl.class);
    

	@Autowired
    @Qualifier("sqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private BeanFactory beanFactory;
    
    /**
     * 通过mapper查询数据
     * @param  IRequest
     * @param  String Mapper方法的Id
     * @param  Object 参数
     * @return sql查询出来的值
     */
    @Override
	public List<?> selectDatas(IRequest request, String sqlId, Object obj) {
		List<?> data = new ArrayList<>();
		if (!StringUtils.isEmpty(sqlId)) {
            String beanName = StringUtils.uncapitalize(StringUtils.substringBefore(sqlId, "."));
            Object mapperObjectDelegate = beanFactory.getBean(beanName);
            if (mapperObjectDelegate == null) {
                return Collections.emptyList();
            }
            Class<?>[] interfaceClass = mapperObjectDelegate.getClass().getInterfaces();
            for (Class c : interfaceClass) {
                if (c.getSimpleName().equalsIgnoreCase(beanName)) {
                    sqlId = c.getPackage().getName() + "." + StringUtils.capitalize(sqlId);
                    break;
                }
            }
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            	obj = convertMapParamToDtoParam(sqlSession, sqlId, obj);
            	data = sqlSession.selectList(sqlId, obj);
            } catch (Throwable e) {
                if (log.isErrorEnabled()) {
                	log.error(e.getMessage(), e);
                }
            }
        }
		return data;
	}
	
	 /**
     * 将 map 类型的参数 转换为 dto 类型
     * 
     * @param sqlSession mybatis数据库连接
     * @param sqlId Mapper方法的Id
     * @param map 参数
     * @return
     */
    private Object convertMapParamToDtoParam(SqlSession sqlSession, String sqlId, Object map) {
        if (!(map instanceof Map)) {
            log.warn("参数不是map类型:{}", map);
            return map;
        }
        MappedStatement statement = sqlSession.getConfiguration().getMappedStatement(sqlId);
        if (statement == null) {
        	log.warn("no statement found for sqlId:{}", sqlId);
            return map;
        }
        List<ResultMap> resultMaps = statement.getResultMaps();
        if (resultMaps == null || resultMaps.isEmpty()) {
        	log.warn("statement has no specified ResultMap, sqlId:{}", sqlId);
            return map;
        }
        ResultMap resultMap = resultMaps.get(0);
        try {
            Class dtoClass = resultMap.getType();
            Object dto = dtoClass.newInstance();
            ((Map) map).forEach((k, v) -> {
                try {
                    PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(dto, (String) k);
                    if (desc != null) {
                        BeanUtils.setProperty(dto, (String) k, v);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            log.debug("convert query parameter to {}", dto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
	
	
}