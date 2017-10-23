package clb.core.question.service.impl;

import clb.core.customer.dto.CtmCustomer;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.question.dto.QaQuestion;
import clb.core.question.service.IQaQuestionService;
import clb.core.system.dto.ClbCodeValue;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author
 * @version 1.0
 * @name QaQuestionImport
 * @description 问题咨询列表导入
 */
@Component
public class QaQuestionImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IQaQuestionService questionService;

    @Autowired
    private CodeValueMapper codeValueMapper;


    /**
     * @Description: 数据导入
     */
    @Override
    public void execute(Map<String, Object> args) throws ValidationTableException, Exception {
        Long importBatchId = (Long) args.get("importBatchId");
        IRequest request = (IRequest) args.get("request");

        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        int countImportBatchId = 0;
        //校验该批次数据是否已经导入过
        if (null != importBatchId) {
            //countImportBatchId = supplierAllotRatioService.selectCountImportBatchIdByAttrIbute(importBatchId);
        }

        if (countImportBatchId > 0) {
            throw new ValidationTableException(Constants.BATCH_REPEAT, null);
        }
        List<QaQuestion> questions = validateData(importBatchId, request);

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
            questions.forEach(qaQuestion -> {
                List<QaQuestion> qaQuestionList = questionService.query(request, qaQuestion);
                if (null != qaQuestionList && qaQuestionList.size() == 1) {
                    qaQuestionList.get(0).setAttribute1(qaQuestion.getAttribute1());
                    questionService.updateByPrimaryKeySelective(request, qaQuestionList.get(0));
                } else if (qaQuestionList.size() == 0) {
                    questionService.insertSelective(request, qaQuestion);
                }
            });
        }
        StringBuffer errorMsg = new StringBuffer("");
    }

    /**
     * @return
     * @throws IntrospectionException
     * @Description: 数据验证
     * @author
     */
    public List<QaQuestion> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {
        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
        List<QaQuestion> questions = new ArrayList<QaQuestion>();//将临时表的数据转换成供应商比例维护数据
        //将没行数据的供应商id、客户id、联系人id存储，方便匹配是否有重复数据
        Map<Map<Long, Long>, List<Long>> custContactSupMap = new HashMap<Map<Long, Long>, List<Long>>();

        List<String> uniqueList = new ArrayList<String>();   // 问题类型+问题名称

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
                    QaQuestion qaQuestion = new QaQuestion();

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
                        // 问题类型验证
                        else if (objTitle.trim().equals(messageSource.getMessage("问题类型", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {

                                    List<CodeValue> codeList= codeValueMapper.selectCodeValuesByCodeName("QA.QUESTION_TYPE");
                                    String valueCode=null;
                                    for(CodeValue code:codeList){
                                        if(code.getMeaning().equals(objValue)){
                                            valueCode=code.getValue();
                                            break;
                                        }
                                    }
                                    if(valueCode==null){
                                        qaQuestion.setQuestionType("");
                                        errorMessage.append(messageSource.getMessage("问题类型在系统值列表中不存在", new Object[]{
                                                objTitle.trim()}, locale)).append(";");
                                        continue;
                                    }else {
                                        qaQuestion.setQuestionType(valueCode);
                                    }

                                }
                            } else {
                                qaQuestion.setQuestionType("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 问题名称验证
                        else if (objTitle.trim().equals(messageSource.getMessage("问题名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                                qaQuestion.setQuestionName(objValue);

                            } else {
                                qaQuestion.setQuestionName("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 备注验证
                        else if (objTitle.trim().equals(messageSource.getMessage("备注", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                                qaQuestion.setComments(objValue);

                            }
                        }
                        // 解决方案验证
                        else if (objTitle.trim().equals(messageSource.getMessage("解决方案", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {

                                qaQuestion.setSolution(objValue);

                            } else {
                                qaQuestion.setSolution("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
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

                        // 校验‘问题类型+问题名称’唯一性
                        if (uniqueList.contains(qaQuestion.getQuestionType() + qaQuestion.getQuestionName())) {
                            errorMessage.append(messageSource.getMessage("该问题已存在！", null, locale)).append(";");
                        } else {
                            uniqueList.add(qaQuestion.getQuestionType() + qaQuestion.getQuestionName());
                        }

                        qaQuestion.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                        questions.add(qaQuestion);
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

        return questions;
    }
}
