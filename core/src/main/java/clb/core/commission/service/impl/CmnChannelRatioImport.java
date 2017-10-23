package clb.core.commission.service.impl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.mapper.CnlChannelMapper;
import clb.core.commission.dto.CmnChannelRatio;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.mapper.CmnChannelRatioDetailMapper;
import clb.core.commission.mapper.CmnChannelRatioMapper;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.system.dto.ClbCodeValue;
import clb.core.system.mapper.ClbCodeValueMapper;

import com.hand.hap.core.IRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author
 * @version 1.0
 * @name CmnChannelRatioImport
 * @description 渠道分成导入
 */
@Component
public class CmnChannelRatioImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CnlChannelMapper cnlChannelMapper;

    @Autowired
    private CmnChannelRatioMapper cmnChannelRatioMapper;

    @Autowired
    private CmnChannelRatioDetailMapper cmnChannelRatioDetailMapper;

    @Autowired
    private PrdItemsMapper prdItemsMapper;

    @Autowired
    private PrdItemSublineMapper prdItemSublineMapper;

    @Autowired
    private ClbCodeValueMapper clbCodeValueMapper;


    /**
     * @Description: 数据导入
     */
    @Override
    public void execute(Map<String, Object> args) throws ValidationTableException, Exception {
        Long importBatchId = (Long) args.get("importBatchId");
        IRequest request = (IRequest) args.get("request");

        int countImportBatchId = 0;
        //校验该批次数据是否已经导入过
        if (null != importBatchId) {
            //countImportBatchId = supplierAllotRatioService.selectCountImportBatchIdByAttrIbute(importBatchId);
        }

        if (countImportBatchId > 0) {
            throw new ValidationTableException(Constants.BATCH_REPEAT, null);
        }
        List<CmnChannelRatioDetail> cmnChannelRatioDetails = validateData(importBatchId, request);

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
            cmnChannelRatioDetails.forEach(cmnChannelRatioDetail -> {
                if (StringUtils.isNotBlank(cmnChannelRatioDetail.getAttribute2()) && StringUtils.isNotBlank(cmnChannelRatioDetail.getAttribute1())) {
                    CmnChannelRatio cmnChannelRatio = new CmnChannelRatio();
                    cmnChannelRatio.setRatioName(cmnChannelRatioDetail.getAttribute1());
                    cmnChannelRatio.setChannelId(Long.parseLong(cmnChannelRatioDetail.getAttribute2()));
                    //根据渠道名称和级别名称，判断该级别数据库中是否存在
                    List<CmnChannelRatio> cmnChannelRatioList = cmnChannelRatioMapper.selectByChannelIdAndRatioName(cmnChannelRatio);
                    if (cmnChannelRatioList.size() > 0) {
                        cmnChannelRatio.setRatioId(cmnChannelRatioList.get(0).getRatioId());
                        cmnChannelRatioDetail.setRatioId(cmnChannelRatioList.get(0).getRatioId());
                    } else {
                        cmnChannelRatio.setCreatedBy(request.getUserId());
                        cmnChannelRatio.setCreationDate(new Date());
                        cmnChannelRatio.setLastUpdatedBy(request.getUserId());
                        cmnChannelRatio.setLastUpdateLogin(request.getUserId());
                        cmnChannelRatio.setLastUpdateDate(new Date());
                        cmnChannelRatio.setObjectVersionNumber(1L);
                        int ratioId = cmnChannelRatioMapper.insertSelective(cmnChannelRatio);
                        cmnChannelRatioDetail.setRatioId((long) ratioId);
                    }
                }
                cmnChannelRatioDetail.setAttribute1(null);
                cmnChannelRatioDetail.setAttribute2(null);
                cmnChannelRatioDetail.setCreatedBy(request.getUserId());
                cmnChannelRatioDetail.setCreationDate(new Date());
                cmnChannelRatioDetail.setLastUpdatedBy(request.getUserId());
                cmnChannelRatioDetail.setLastUpdateLogin(request.getUserId());
                cmnChannelRatioDetail.setLastUpdateDate(new Date());
                cmnChannelRatioDetail.setObjectVersionNumber(1L);
                cmnChannelRatioDetailMapper.insertSelective(cmnChannelRatioDetail);
            });
        }
    }

    /**
     * @return
     * @throws IntrospectionException
     * @Description: 数据验证
     * @author
     */
    public List<CmnChannelRatioDetail> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {
        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
        List<CmnChannelRatioDetail> cmnChannelRatioDetails = new ArrayList<CmnChannelRatioDetail>();//将临时表的数据转换成供应商比例维护数据

        if (null != importBatchId) {
            //查出所有当前批次的数据
            importTemps = importTempService.selectImportData(importBatchId, 1, Constants.MAX_NUMBER, request);
        }
        List<String> sheets = new ArrayList<String>();//sheet页操作
        ImportTemp importTemp = new ImportTemp();//循环对象
        //对当前批次数据做迭代循环校验
        if (null != importTemps && importTemps.size() > 0) {
            Iterator<ImportTemp> iterator = importTemps.iterator();
            ImportTemp importTempTitle = null;
            StringBuffer errorTitleMessage = new StringBuffer("");//检查标题行是否有错
            //防止标题错误重复添加
            boolean isAppendErrorTitle = false;
            while (iterator.hasNext()) {
                importTemp = iterator.next();
                StringBuffer errorMessage = new StringBuffer("");
                if (!sheets.contains(importTemp.getSheet())) {//标题行
                    sheets.add(importTemp.getSheet());
                    if (!"".equals(errorTitleMessage.toString())) {//行标题有错误
                        importTempTitle.setImportMessage(errorTitleMessage.toString());
                        importTempService.updateError(importTempTitle, request);
                    }
                    errorTitleMessage = new StringBuffer("");
                    isAppendErrorTitle = false;
                    importTempTitle = importTemp;
                    //标题行重复性验证
                    Set<String> titleSet = new HashSet<String>();
                    for (int i = 1; i <= Constants.MAX_ATTR; i++) {
                        //标题字段
                        attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
                        Method methodRead = attributeTitle.getReadMethod();
                        String objTitle = (String) methodRead.invoke(importTempTitle);
                        if (null != objTitle && !"".equals(objTitle.trim())) {
                            if (titleSet.contains(objTitle)) {
                                errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            } else {
                                titleSet.add(objTitle);
                            }
                        }
                    }
                } else if (importTemp.getLineNumber() != 1) {   //第一行为标题行
                    CmnChannelRatio cmnChannelRatio = new CmnChannelRatio();
                    CmnChannelRatioDetail cmnChannelRatioDetail = new CmnChannelRatioDetail();

                    //对所有属性循环
                    for (int i = 1; i <= Constants.MAX_ATTR; i++) {
                        //标题字段
                        attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
                        Method methodTitle = attributeTitle.getReadMethod();
                        String objTitle = (String) methodTitle.invoke(importTempTitle);
                        //当前数据
                        attributeValue = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTemp.getClass());
                        Method methodValue = attributeValue.getReadMethod();
                        String objValue = (String) methodValue.invoke(importTemp);

                        // 以下为数据校验
                        // 以下按标题做判断对应标题做对应的验证,若没有对应到标题则在最后添加错误信息
                        if (null == objTitle || "".equals(objTitle.trim())) {//标题为空时
                            if (null == objValue || "".equals(objValue.trim())) {//行内数据也是空，跳出循环，即不允许出现空的列，否则后面数据当无效处理
                                break;
                            } else {//标题为空但行内有值，表示数据多余或标题缺失
                                if (!isAppendErrorTitle) {
                                    isAppendErrorTitle = true;
                                    errorTitleMessage.append(messageSource.getMessage(Constants.ERROR_TITLE, null, locale)).append(";");
                                }
                            }
                        }
                        // 渠道名称验证
                        else if (objTitle.trim().equals(messageSource.getMessage("渠道名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                //根据渠道名称查找渠道ID
                                CnlChannel cnlChannel = new CnlChannel();
                                cnlChannel.setChannelName(objValue);
                                List<CnlChannel> cnlChannelList = cnlChannelMapper.queryChannelByChannelName(cnlChannel);
                                if (cnlChannelList.size() == 1) {
                                    cmnChannelRatio.setChannelId(cnlChannelList.get(0).getChannelId());
                                } else {
                                    cmnChannelRatio.setChannelName("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            } else {
                                cmnChannelRatio.setChannelName("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 级别名称验证
                        else if (objTitle.trim().equals(messageSource.getMessage("级别名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatio.setRatioName(objValue);
                                //根据渠道名称和级别名称，判断该级别数据库中是否存在
                                List<CmnChannelRatio> cmnChannelRatioList = cmnChannelRatioMapper.selectByChannelIdAndRatioName(cmnChannelRatio);
                                if (cmnChannelRatioList.size() > 0) {
                                    cmnChannelRatio.setRatioId(cmnChannelRatioList.get(0).getRatioId());
                                    cmnChannelRatioDetail.setRatioId(cmnChannelRatioList.get(0).getRatioId());
                                } else {
                                    cmnChannelRatioDetail.setAttribute1(objValue);
                                    cmnChannelRatioDetail.setAttribute2(cmnChannelRatio.getChannelId().toString());
                                }
                            } else {
                                cmnChannelRatio.setRatioName("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 分成头ID、大分类、中分类、小分类、产品名称、供款期唯一性验证
                        else if (objTitle.trim().equals(messageSource.getMessage("大分类", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                List<ClbCodeValue> clbCodeValueList = clbCodeValueMapper.selectCodeValueByMeaning("PRD.PRODUCT_DIVISION", objValue);
                                if (clbCodeValueList.size() == 1) {
                                    cmnChannelRatioDetail.setBigClass(clbCodeValueList.get(0).getValue());
                                } else {
                                    cmnChannelRatioDetail.setBigClass("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            }
                        }
                        else if (objTitle.trim().equals(messageSource.getMessage("中分类", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                List<ClbCodeValue> clbCodeValueList = clbCodeValueMapper.selectCodeValueByMeaning("PRD.PRODUCT_CLASS", objValue);
                                if (clbCodeValueList.size() == 1) {
                                    cmnChannelRatioDetail.setMidClass(clbCodeValueList.get(0).getValue());
                                } else {
                                    cmnChannelRatioDetail.setMidClass("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            }
                        }
                        else if (objTitle.trim().equals(messageSource.getMessage("小分类", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                List<ClbCodeValue> clbCodeValueList = clbCodeValueMapper.selectCodeValueByMeaning("PRD.PRODUCT_CATAGORY", objValue);
                                if (clbCodeValueList.size() == 1) {
                                    cmnChannelRatioDetail.setMinClass(clbCodeValueList.get(0).getValue());
                                } else {
                                    cmnChannelRatioDetail.setMinClass("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            }
                        }
                        else if (objTitle.trim().equals(messageSource.getMessage("产品名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                Long itemId = prdItemsMapper.selectItemIdByItemName(objValue);
                                if (itemId != null) {
                                    cmnChannelRatioDetail.setItemId(itemId);
                                } else {
                                    cmnChannelRatioDetail.setItemName("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            }
                        }
                        //供款期校验
                        else if (objTitle.trim().equals(messageSource.getMessage("供款期", null, locale))) {
                            if (cmnChannelRatioDetail.getItemId() != null && null != objValue && !"".equals(objValue.trim())) {
                                Long prdItemSublineId = prdItemSublineMapper.selectByCondition(objValue, cmnChannelRatioDetail.getItemId());
                                if (prdItemSublineId != null) {
                                    cmnChannelRatioDetail.setSublineItemId(prdItemSublineId);
                                } else {
                                    cmnChannelRatioDetail.setSublineItemName("");
                                    errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                            locale)).append(";");
                                }
                            }
                            //校验重复数据
                            if (cmnChannelRatioDetail.getRatioId() != null) {
                                CmnChannelRatioDetail _cmnChannelRatioDetail = new CmnChannelRatioDetail();
                                _cmnChannelRatioDetail.setRatioId(cmnChannelRatioDetail.getRatioId());
                                _cmnChannelRatioDetail.setBigClass(cmnChannelRatioDetail.getBigClass());
                                _cmnChannelRatioDetail.setMidClass(cmnChannelRatioDetail.getMidClass());
                                _cmnChannelRatioDetail.setMinClass(cmnChannelRatioDetail.getMinClass());
                                _cmnChannelRatioDetail.setItemId(cmnChannelRatioDetail.getItemId());
                                _cmnChannelRatioDetail.setSublineItemId(cmnChannelRatioDetail.getSublineItemId());
                                List<CmnChannelRatioDetail> _cmnChannelRatioDetailList = cmnChannelRatioDetailMapper.selectChannelRatioDetailsByNull(_cmnChannelRatioDetail);
                                if (_cmnChannelRatioDetailList != null && _cmnChannelRatioDetailList.size() > 0) {
                                    errorMessage.append("不能设置相同的自定义级别;");
                                }
                            }
                        }
                        // 第一年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第一年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheFirstYear(new BigDecimal(objValue));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 第二年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第二年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheSecondYear(new BigDecimal(objValue));
                            }
                        }
                        // 第三年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第三年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheThirdYear(new BigDecimal(objValue));
                            }
                        }
                        // 第四年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第四年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheFourthYear(new BigDecimal(objValue));
                            }
                        }
                        // 第五年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第五年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheFifthYear(new BigDecimal(objValue));
                            }
                        }
                        // 第六年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第六年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheSixthYear(new BigDecimal(objValue));
                            }
                        }
                        // 第七年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第七年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheSeventhYear(new BigDecimal(objValue));
                            }
                        }
                        // 第八年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第八年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheEightYear(new BigDecimal(objValue));
                            }
                        }
                        // 第九年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第九年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheNinthYear(new BigDecimal(objValue));
                            }
                        }
                        // 第十年费率验证
                        else if (objTitle.trim().equals(messageSource.getMessage("第十年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setTheTenthYear(new BigDecimal(objValue));
                            }
                        }
                        // 备注验证
                        else if (objTitle.trim().equals(messageSource.getMessage("备注", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                cmnChannelRatioDetail.setComments(objValue);
                            }
                        }
                        else {//没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
                            if (importTemp.getLineNumber() == 2) {
                                errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                    }
                    if ("".equals(errorMessage.toString())) {//通过第二次验证,进行数据转换
                        cmnChannelRatioDetails.add(cmnChannelRatioDetail);
                    }
                }
                //数据转换之后更新数据状态
                importTemp.setImportMessage(errorMessage.toString());//数据转换之后更新错误状态
                importTempService.updateError(importTemp, request);
            }
            if (!"".equals(errorTitleMessage.toString())) {//当前页行标题有错误
                importTempTitle.setImportMessage(errorTitleMessage.toString());
                importTempService.updateError(importTempTitle, request);
            }
        } else {
            throw new ValidationTableException(Constants.DATA_EMPTY, null);
        }

        return cmnChannelRatioDetails;
    }
}
