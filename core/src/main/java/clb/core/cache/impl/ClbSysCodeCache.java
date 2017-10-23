package clb.core.cache.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.impl.ServiceRequest;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;

import com.hand.hap.cache.impl.HashStringRedisCache;
import com.hand.hap.core.ILanguageProvider;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.Language;

import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeMapper;
import clb.core.system.mapper.ClbCodeValueMapper;

/**
 * @author wanjun.feng@hand-china.com
 */
public class ClbSysCodeCache extends HashStringRedisCache<ClbCode> {

    private Logger logger = LoggerFactory.getLogger(ClbSysCodeCache.class);

    private String codeQuerySqlId = ClbCodeMapper.class.getName() + ".select";
    private String codeValueQuerySqlId = ClbCodeValueMapper.class.getName() + ".select";

    @Autowired
    private ILanguageProvider languageProvider;

    @Override
    public void init() {
        setType(ClbCode.class);
        strSerializer = getRedisTemplate().getStringSerializer();
        initLoad();
    }

    /**
     * key 包含 code 和语言两部分,用'.'拼接.
     *
     * @param key
     *            code.lang
     * @return 一个仅包含 code value 的空的 code
     */
    @Override
    public ClbCode getValue(String key) {
        return super.getValue(key);
    }

    /**
     *
     * @param key
     *            code.lang
     * @param code
     *            需要放入缓存的 Code
     */
    @Override
    public void setValue(String key, ClbCode code) {
        super.setValue(key, code);
    }

    /**
     * 
     * @param key
     *            code
     */
    @Override
    public void remove(String key) {
        getRedisTemplate().execute((RedisCallback) (connection) -> {
            for (Language language : languageProvider.getSupportedLanguages()) {
                connection.hDel(strSerializer.serialize(getFullKey(key)),
                        strSerializer.serialize(key + "." + language.getLangCode()));
            }
            return null;
        });
    }

    public void reload(Long codeId) {
        IRequest oldRequest = RequestHelper.getCurrentRequest();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            for (Language language : languageProvider.getSupportedLanguages()) {
                IRequest request = new ServiceRequest();
                request.setLocale(language.getLangCode());
                RequestHelper.setCurrentRequest(request);
                ClbCode para = new ClbCode();
                para.setCodeId(codeId);
                ClbCode code = sqlSession.selectOne(codeQuerySqlId, para);
                ClbCodeValue p2 = new ClbCodeValue();
                p2.setCodeId(codeId);
                p2.setSortname("orderSeq");
                List<ClbCodeValue> codeValues = sqlSession.selectList(codeValueQuerySqlId, p2);
                code.setClbCodeValues(codeValues);
                setValue(code.getCode() + "." + language.getLangCode(), code);
            }
        } finally {
            RequestHelper.setCurrentRequest(oldRequest);
        }
    }

    @Override
    protected void initLoad() {
        Map<Long, ClbCode> tempMap = new HashMap<>();
        try (SqlSession sqlSession = getSqlSessionFactory().openSession()) {
            for (Language language : languageProvider.getSupportedLanguages()) {
                IRequest request = new ServiceRequest();
                request.setLocale(language.getLangCode());
                RequestHelper.setCurrentRequest(request);

                sqlSession.select(codeQuerySqlId,(resultContext) -> {
                    ClbCode code = (ClbCode) resultContext.getResultObject();
                    tempMap.put(code.getCodeId(), code);
                });

                ClbCodeValue cV=new ClbCodeValue();
                cV.setSortname("orderSeq");
                sqlSession.select(codeValueQuerySqlId, cV, (resultContext) -> {
                    ClbCodeValue value = (ClbCodeValue) resultContext.getResultObject();
                    ClbCode code = tempMap.get(value.getCodeId());
                    if (code != null) {
                        List<ClbCodeValue> codeValues = code.getClbCodeValues();
                        if (codeValues == null) {
                            codeValues = new ArrayList<>();
                            code.setClbCodeValues(codeValues);
                        }
                        codeValues.add(value);
                    }
                });
                tempMap.forEach((k, v) -> {
                    setValue(v.getCode() + "." + language.getLangCode(), v);
                });
                tempMap.clear();
            }
            RequestHelper.clearCurrentRequest();
        } catch (Throwable e) {
            if (logger.isErrorEnabled()) {
                logger.error("init syscode exception ", e);
            }
        }
    }
}