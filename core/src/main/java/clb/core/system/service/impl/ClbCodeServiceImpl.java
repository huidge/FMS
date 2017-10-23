/*
 * #{copyright}#
 */
package clb.core.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.ILanguageProvider;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.Language;

import clb.core.cache.impl.ClbSysCodeCache;
import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeMapper;
import clb.core.system.mapper.ClbCodeValueMapper;
import clb.core.system.service.IClbCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wanjun.feng@hand-china.com
 */
@Service
@Transactional
public class ClbCodeServiceImpl implements IClbCodeService {
    @Autowired
    private ClbCodeMapper clbCodeMapper;

    @Autowired
    private ClbCodeValueMapper clbCodeValueMapper;

    @Autowired
    private ILanguageProvider languageProvider;

    @Autowired
    private ClbSysCodeCache clbCodeCache;

    /**
     * 批量操作快码行数据.
     *
     * @param code
     *            头行数据
     */
    private void processCodeValues(ClbCode code) {
        for (ClbCodeValue codeValue : code.getClbCodeValues()) {
            if (codeValue.getCodeValueId() == null) {
                codeValue.setCodeId(code.getCodeId()); // 设置头ID跟行ID一致
                clbCodeValueMapper.insertSelective(codeValue);
            } else if (codeValue.getCodeValueId() != null) {
                clbCodeValueMapper.updateByPrimaryKeySelective(codeValue);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ClbCode> selectCodes(IRequest request, ClbCode code, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<ClbCode> codes = clbCodeMapper.selectCode(code);
        return codes;
    }

    @Override
    public List<ClbCodeValue> selectCodeValues(IRequest request, ClbCodeValue value) {
        return clbCodeValueMapper.selectCodeValuesByCodeId(value);
    }

    @Override
    public List<ClbCodeValue> selectCodeValuesByCodeName(IRequest request, String codeName) {
        ClbCode code = clbCodeCache.getValue(codeName + "." + request.getLocale());
        if (code != null) {
            return code.getClbCodeValues();
        } else {
            return clbCodeValueMapper.selectCodeValuesByCodeName(codeName);
        }
    }

    /**
     * 根据代码和值获取CodeValue.
     * <p>
     * 从 cache 直接取值.
     *
     * @param request
     *            请求上下文
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return codeValue 代码值DTO
     * @author frank.li
     */
    @Override
    public ClbCodeValue getCodeValue(IRequest request, String codeName, String value) {
        ClbCode code = clbCodeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getClbCodeValues() == null) {
            return null;
        }
        for (ClbCodeValue v : code.getClbCodeValues()) {
            if (value.equals(v.getValue())) {
                return v;
            }
        }
        return null;
    };

    /**
     * 根据代码和含义获取代码值.
     * <p>
     * 从 cache 直接取值.
     *
     * @param request
     *            请求上下文
     * @param codeName
     *            代码
     * @param meaning
     *            含义
     * @return value 代码值
     * @author frank.li
     */
    @Override
    public String getCodeValueByMeaning(IRequest request, String codeName, String meaning) {
        ClbCode code = clbCodeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getClbCodeValues() == null) {
            return null;
        }
        for (ClbCodeValue v : code.getClbCodeValues()) {
            if (meaning.equals(v.getMeaning())) {
                return v.getValue();
            }
        }
        return null;
    };

    /**
     * 根据代码和值获取含义.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return meaning 含义
     */
    @Override
    public String getCodeMeaningByValue(IRequest request, String codeName, String value) {
        ClbCode code = clbCodeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getClbCodeValues() == null) {
            return null;
        }
        for (ClbCodeValue v : code.getClbCodeValues()) {
            if (value.equals(v.getValue())) {
                return v.getMeaning();
            }
        }
        return null;
    };

    /**
     * 根据代码和值获取描述.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return description 描述
     */
    @Override
    public String getCodeDescByValue(IRequest request, String codeName, String value) {
        ClbCode code = clbCodeCache.getValue(codeName + "." + request.getLocale());
        if (code == null) {
            return null;
        }

        if (code.getClbCodeValues() == null) {
            return null;
        }
        for (ClbCodeValue v : code.getClbCodeValues()) {
            if (value.equals(v.getValue())) {
                return v.getDescription();
            }
        }
        return null;
    };

    @Override
    public ClbCode createCode(ClbCode code) {
        // 插入头行
        clbCodeMapper.insertSelective(code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getClbCodeValues() != null) {
            processCodeValues(code);
        }
        for (Language lang : languageProvider.getSupportedLanguages()) {
            clbCodeCache.setValue(code.getCode() + "." + lang.getLangCode(), code);
        }
        return code;
    }

    @Override
    public boolean batchDelete(IRequest request, List<ClbCode> codes) {
        // 删除头行
        for (ClbCode code : codes) {
            ClbCodeValue codeValue = new ClbCodeValue();
            codeValue.setCodeId(code.getCodeId());
            // 首先删除行的多语言数据
            clbCodeValueMapper.deleteTlByCodeId(codeValue);
            // 然后删除行
            clbCodeValueMapper.deleteByCodeId(codeValue);

            // 最后删除头
            clbCodeMapper.deleteByPrimaryKey(code);
            clbCodeCache.remove(code.getCode());
        }
        return true;
    }

    @Override
    public boolean batchDeleteValues(IRequest request, List<ClbCodeValue> values) {
        Set<Long> codeIdSet = new HashSet<>();
        for (ClbCodeValue value : values) {
            clbCodeValueMapper.deleteByPrimaryKey(value);
            codeIdSet.add(value.getCodeId());
        }
        for (Long codeId : codeIdSet) {
            clbCodeCache.reload(codeId);
        }

        return true;
    }

    @Override
    public ClbCode updateCode(ClbCode code) {
        clbCodeMapper.updateByPrimaryKeySelective(code);
        // 判断如果行不为空，则迭代循环插入
        if (code.getClbCodeValues() != null) {
            processCodeValues(code);
        }
        clbCodeCache.remove(code.getCode());
        clbCodeCache.reload(code.getCodeId());
        return code;
    }

    @Override
    public List<ClbCode> batchUpdate(IRequest request, List<ClbCode> codes) {
        for (ClbCode code : codes) {
            if (code.getCodeId() == null) {
                self().createCode(code);
            } else if (code.getCodeId() != null) {
                self().updateCode(code);
            }
        }
        return codes;
    }

	@Override
	public List<ClbCodeValue> selectCodeValuesByParam(ClbCodeValue code) {
		return clbCodeValueMapper.selectCodeValuesByParam(code);
	}
}