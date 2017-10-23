package clb.core.system.service.impl;

import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.question.dto.QaQuestion;
import clb.core.question.service.IQaQuestionService;
import clb.core.system.dto.ClbUser;
import clb.core.system.mapper.ClbUserMapper;
import clb.core.system.service.IClbUserService;
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
 * @name ClbUserPasswordImport
 * @description 用户密码导入
 */
@Component
public class ClbUserPasswordImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ClbUserMapper usrerMapper;


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
        List<ClbUser> users = validateData(importBatchId, request);

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
            users.forEach(clbUser -> {
                if (!"".equals(clbUser.getPasswordEncrypted())
                        && clbUser.getPasswordEncrypted() != null) {
                    usrerMapper.updatePassword(clbUser);
                }

                if (!"".equals(clbUser.getNewUserName())
                        && clbUser.getNewUserName() != null) {
                    usrerMapper.updateUserName(clbUser);
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
    public List<ClbUser> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {
        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
        List<ClbUser> users = new ArrayList<ClbUser>();//将临时表的数据转换成供应商比例维护数据

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
                    ClbUser clbUser = new ClbUser();

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
                        // 用户名验证
                        else if (objTitle.trim().equals(messageSource.getMessage("用户名", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                clbUser.setUserName(objValue);
                            } else {
                                clbUser.setUserName("");
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 密码验证
                        else if (objTitle.trim().equals(messageSource.getMessage("密码", null, locale))) {
                            clbUser.setPasswordEncrypted(objValue);
                        }
                        // 新用户名验证
                        else if (objTitle.trim().equals(messageSource.getMessage("新用户名", null, locale))) {
                            clbUser.setNewUserName(objValue);
                        }

                        else {//没有对应的标题，添加导入错误信息--只有第一行需要添加，后续行不需要重复添加
                            if (importTemp.getLineNumber() == 2) {
                                errorTitleMessage.append(messageSource.getMessage(Constants.TITLE_EXCEPTION, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                    }

                    if ("".equals(errorMessage.toString())) {//通过第二次验证,进行数据转换

                        users.add(clbUser);
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

        return users;
    }
}
