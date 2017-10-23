/*
 * #{copyright}#
 */

package clb.core.system.service;

import java.util.List;

import clb.core.system.dto.ClbCode;
import clb.core.system.dto.ClbCodeValue;

import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
/**
 * @author wanjun.feng@hand-china.com
 */
public interface IClbCodeService extends ProxySelf<IClbCodeService> {

    List<ClbCode> selectCodes(IRequest request, ClbCode code, int page, int pagesize);

    List<ClbCodeValue> selectCodeValues(IRequest request, ClbCodeValue value);

    ClbCode createCode(ClbCode code);

    boolean batchDelete(IRequest request, List<ClbCode> codes);

    boolean batchDeleteValues(IRequest request, List<ClbCodeValue> values);

    ClbCode updateCode(ClbCode code);

    List<ClbCode> batchUpdate(IRequest request, @StdWho List<ClbCode> codes);
    
    /**
     * 根据code查询所有代码值.
     * 
     * @param request
     * @param code
     * @return 代码值
     */
    List<ClbCodeValue> selectCodeValuesByCodeName(IRequest request, String codeName);
    
    
    /**
     * 根据代码和值获取CodeValue.
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
    ClbCodeValue getCodeValue(IRequest request, String codeName, String value);

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
    String getCodeValueByMeaning(IRequest request, String codeName, String meaning);

    /**
     * 根据代码和值获取Meaning.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return meaning 含义
     */
    String getCodeMeaningByValue(IRequest request, String codeName, String value);

    /**
     * 根据代码和值获取描述.
     *
     * @param codeName
     *            代码
     * @param value
     *            代码值
     * @return description 描述
     */
    String getCodeDescByValue(IRequest request, String codeName, String value);
    
    public List<ClbCodeValue> selectCodeValuesByParam(ClbCodeValue code);
}
