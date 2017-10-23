package clb.core.customer.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import clb.core.customer.dto.CtmCustomer;
import clb.core.customer.service.ICtmCustomerService;
import clb.core.system.service.IClbCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.hand.hap.core.IRequest;

import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;

/**
 * @author xiaoyong.luo@hand-china.com	2017年7月26日
 * @version 1.0
 * @name CtmCustomerImport
 * @description 客户导入
 */
@Component
public class CtmCustomerImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IClbCodeService clbCodeService;

    @Autowired
    private ICtmCustomerService customerService;


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
        List<CtmCustomer> ctmCustomers = validateData(importBatchId, request);
        if (importTempService.selectErrorCount(importBatchId) == 0) {          //验证没有错误
            ctmCustomers.forEach(ctmCustomer -> {
                customerService.createCustomer(request, locale, ctmCustomer);
            });
        }
    }


    /**
     * @return List<SupplierAllotRatio>
     * @throws IntrospectionException
     * @Description: 数据验证
     */
    public List<CtmCustomer> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {
        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<>();       //根据流水号查得的所有临时表数据
        List<CtmCustomer> ctmCustomers = new ArrayList<>();        //将临时表的数据转换成客户数据

        if (null != importBatchId) {
            //查出所有当前批次的数据
            importTemps = importTempService.selectImportData(importBatchId, 1, Constants.MAX_NUMBER, request);
        }
        List<String> sheets = new ArrayList<>();//sheet页操作
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
                } else if (importTemp.getLineNumber() != 1) {           //第一行为标题行
                    CtmCustomer ctmCustomer = new CtmCustomer();

                    //对所有属性循环，转换成供应商比例维护数据
                    for (int i = 1; i <= Constants.MAX_ATTR; i++) {
                        //标题字段
                        attributeTitle = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTempTitle.getClass());
                        Method methodTitle = attributeTitle.getReadMethod();
                        String objTitle = (String) methodTitle.invoke(importTempTitle);
                        //当前数据
                        attributeValue = new PropertyDescriptor(Constants.ATTRIBUTE + (i), importTemp.getClass());
                        Method methodValue = attributeValue.getReadMethod();
                        String objValue = (String) methodValue.invoke(importTemp);

						/*
						* 以下为数据校验
						* 以下按标题做判断对应标题做对应的验证,若没有对应到标题则在最后添加错误信息
						*/
                        if (null == objTitle || "".equals(objTitle.trim())) {//标题为空时
                            if (null == objValue || "".equals(objValue.trim())) {//行内数据也是空，跳出循环，即不允许出现空的列，否则后面数据当无效处理
                                break;

                                //标题为空但行内有值，表示数据多余或标题缺失
                            } else {
                                if (!isAppendErrorTitle) {
                                    isAppendErrorTitle = true;
                                    errorTitleMessage.append(messageSource.getMessage(Constants.ERROR_TITLE, null, locale)).append(";");
                                }
                            }
                        }


                        //中文姓名 -- 必须
                        else if (objTitle.trim().equals(messageSource.getMessage("中文姓名", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setChineseName(objValue);
                            } else {
                                ctmCustomer.setChineseName("");
                                errorMessage.append(messageSource.getMessage("中文姓名不能为空", new Object[]{objTitle.trim()}, locale)).append(";");
                            }
                        }

                        //拼音姓名
                        else if (objTitle.trim().equals(messageSource.getMessage("拼音姓名", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setEnglishName(objValue);
                            } else {
                                ctmCustomer.setEnglishName("");
                            }
                        }


                        //国籍  -- 值列表校验
                        else if (objTitle.trim().equals(messageSource.getMessage("国籍", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_NATION, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setNationality(valueCode);
                                } else {
                                    ctmCustomer.setNationality("");
                                    errorMessage.append(messageSource.getMessage("国籍在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //教育程度  -- 值列表校验
                        else if (objTitle.trim().equals(messageSource.getMessage("教育程度", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.CTM_DIPLOMA_TYPE, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setEducation(valueCode);
                                } else {
                                    ctmCustomer.setEducation("");
                                    errorMessage.append(messageSource.getMessage("教育程度在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //出生日期验证  -- 必须，格式yyyy/mm/dd
                        else if (objTitle.trim().equals(messageSource.getMessage("出生日期", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                Date date = vaTime(objValue);
                                if (date != null) {
                                    ctmCustomer.setBirthDate(date);
                                } else {
                                    errorMessage.append(messageSource.getMessage("出生日期格式错误", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setBirthDate(null);
                                errorMessage.append(messageSource.getMessage("出生日期必输", new Object[]{objTitle.trim()}, locale)).append(";");
                            }
                        }

                        //性别验证  -- 必须，值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("性别", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.HR_EMPLOYEE_GENDER, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setSex(valueCode);
                                } else {
                                    ctmCustomer.setSex("");
                                    errorMessage.append(messageSource.getMessage("性别在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setSex("");
                                errorMessage.append(messageSource.getMessage("性别在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                            }
                        }

                        //身份证号码  -- 必须
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证号码", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setIdentityNumber(objValue);
                                List<CtmCustomer> list = customerService.selectByIdNumber(request, ctmCustomer);
                                if(list != null && list.size() > 0){
                                    errorMessage.append(messageSource.getMessage("身份证号码不能重复", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setIdentityNumber("");
                                errorMessage.append(messageSource.getMessage("身份证号码不能为空", new Object[]{objTitle.trim()}, locale)).append(";");
                            }
                        }

                        //身份证有效期 -- 格式yyyy/mm/dd
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证有效期", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
//                                    Date date = vaTime(objValue);
//                                    if (date != null) {
                                    ctmCustomer.setIdentityEffectiveDate(objValue);
                                } else {
                                    ctmCustomer.setIdentityEffectiveDate(null);
                                    errorMessage.append(messageSource.getMessage("身份证有效期格式错误", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //是否以身份证作为地址证明
                        else if (objTitle.trim().equals(messageSource.getMessage("是否以身份证作为地址证明", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.SYS_YES_NO, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setIdentityFlag(valueCode);
                                } else {
                                    ctmCustomer.setIdentityFlag("N");
                                    errorMessage.append(messageSource.getMessage("是否以身份证作为地址证明在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setIdentityFlag("N");
                            }
                        }

                        //其他证件  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("其他证件", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, "CTM.CERTIFICATE_TYPE", objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setCertificateType(valueCode);
                                } else {
                                    ctmCustomer.setCertificateType("");
                                    errorMessage.append(messageSource.getMessage("其他证件在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //证件号码
                        else if (objTitle.trim().equals(messageSource.getMessage("证件号码", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setCertificateNumber(objValue);
                            }
                        }

                        //其他证件有效期  -- 格式yyyy/mm/dd
                        else if (objTitle.trim().equals(messageSource.getMessage("其他证件有效期", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
//                                Date date = vaTime(objValue);
//                                if (date != null) {
                                ctmCustomer.setCertificateEffectiveDate(objValue);
                            } else {
                                ctmCustomer.setCertificateEffectiveDate(null);
                                errorMessage.append(messageSource.getMessage("其他证件有效期格式错误", new Object[]{objTitle.trim()}, locale)).append(";");
                            }

                        }

                        //公司名称
                        else if (objTitle.trim().equals(messageSource.getMessage("公司名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setCompanyName(objValue);
                            }
                        }

                        //公司业务性质(行业)
                        else if (objTitle.trim().equals(messageSource.getMessage("其他证件", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setIndustry(objValue);
                            }
                        }

                        //职位
                        else if (objTitle.trim().equals(messageSource.getMessage("职位", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setJob(objValue);
                            }
                        }

                        //职务
                        else if (objTitle.trim().equals(messageSource.getMessage("职位", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setPosition(objValue);
                            }
                        }

                        //月收入水平(港币) -- 数值格式
                        else if (objTitle.trim().equals(messageSource.getMessage("月收入水平(港币)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setIncome(Long.parseLong(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("月收入水平格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //联系电话  -- 格式校验
                        else if (objTitle.trim().equals(messageSource.getMessage("联系电话", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                Map<String, String> phoneMap = vaPhone(objValue);
                                if (phoneMap != null) {
                                    ctmCustomer.setPhone(phoneMap.get("phone"));
                                    ctmCustomer.setPhoneCode(phoneMap.get("phoneCode"));
                                } else {
                                    errorMessage.append(messageSource.getMessage("联系电话格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //电子邮件
                        else if (objTitle.trim().equals(messageSource.getMessage("电子邮件", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setEmail(objValue);
                            }
                        }

                        //公司地址-国家  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("公司地址-国家", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_NATION, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setCompanyNation(valueCode);
                                } else {
                                    errorMessage.append(messageSource.getMessage("公司地址-国家 在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //公司地址-省   -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("公司地址-省", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_PROVINCE, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setCompanyProvince(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("公司地址-省 在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //公司地址-市  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("公司地址-市", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_CITY, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setCompanyCity(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("公司地址-市在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //公司详细地址
                        else if (objTitle.trim().equals(messageSource.getMessage("公司详细地址", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setCompanyAddress(objValue);
                            }
                        }

                        //是否为美国公民或税务居民
                        else if (objTitle.trim().equals(messageSource.getMessage("是否为美国公民或税务居民", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.SYS_YES_NO, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setAmericanCitizenFlag("Y");
                                } else {
                                    ctmCustomer.setAmericanCitizenFlag("N");
                                    errorMessage.append(messageSource.getMessage("是否为美国公民或税务居民在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setAmericanCitizenFlag("N");
                            }
                        }

                        //婚姻状况
                        else if (objTitle.trim().equals(messageSource.getMessage("婚姻状况", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.CTM_MARITAL_STATUS, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setAmericanCitizenFlag(valueCode);
                                } else {
                                    ctmCustomer.setAmericanCitizenFlag("");
                                    errorMessage.append(messageSource.getMessage("婚姻状况在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //身高(cm)
                        else if (objTitle.trim().equals(messageSource.getMessage("身高(cm)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setHeight(Long.parseLong(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("身高(cm)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //体重(kg)
                        else if (objTitle.trim().equals(messageSource.getMessage("体重(kg)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setWeight(Long.parseLong(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("体重(kg)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //邮寄通讯地址-国家  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("邮寄通讯地址-国家", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_NATION, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setPostNation(valueCode);
                                } else {
                                    errorMessage.append(messageSource.getMessage("邮寄通讯地址-国家在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //邮寄通讯地址-省   -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("邮寄通讯地址-省", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_PROVINCE, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setPostProvince(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("邮寄通讯地址-省在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //邮寄通讯地址-市  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("邮寄通讯地址-市", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_CITY, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setPostCity(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("邮寄通讯地址-市在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //邮寄通讯详细地址
                        else if (objTitle.trim().equals(messageSource.getMessage("邮寄通讯详细地址", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setPostAddress(objValue);
                            }
                        }


                        //身份证地址-国家  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证地址-国家", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_NATION, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setIdentityNation(valueCode);
                                } else {
                                    errorMessage.append(messageSource.getMessage("身份证地址-国家在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //身份证地址-省   -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证地址-省", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_PROVINCE, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setIdentityProvince(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("身份证地址-省在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //身份证地址-市  -- 值列表验证
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证地址-市", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (null != objValue && !"".equals(objValue.trim())) {
                                    String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.PUB_CITY, objValue);
                                    if (!StringUtils.isEmpty(valueCode)) {
                                        ctmCustomer.setIdentityCity(valueCode);
                                    } else {
                                        errorMessage.append(messageSource.getMessage("身份证地址-市在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                    }
                                }
                            }
                        }

                        //身份证详细地址
                        else if (objTitle.trim().equals(messageSource.getMessage("身份证详细地址", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setIdentityAddress(objValue);
                            }
                        }

                        //保费资金来源
                        else if (objTitle.trim().equals(messageSource.getMessage("保费资金来源", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setPremiumSource(objValue);
                            }
                        }

                        //平均月支出(港币)
                        else if (objTitle.trim().equals(messageSource.getMessage("平均月支出(港币)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setAmountPerMonth(Double.parseDouble(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("平均月支出(港币)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //持有流动资产(港币)
                        else if (objTitle.trim().equals(messageSource.getMessage("持有流动资产(港币)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setCurrentAssets(Double.parseDouble(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("持有流动资产(港币)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //不动资产总值(港币)
                        else if (objTitle.trim().equals(messageSource.getMessage("不动资产总值(港币)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setFixedAssets(Double.parseDouble(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("不动资产总值(港币)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //保费占个人年收入比例
                        else if (objTitle.trim().equals(messageSource.getMessage("保费占个人年收入比例", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setPremiumRate(Double.parseDouble(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("保费占个人年收入比例格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //个人负债(港币)
                        else if (objTitle.trim().equals(messageSource.getMessage("个人负债(港币)", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                if (vaNumber(objValue)) {
                                    ctmCustomer.setLiabilities(Double.parseDouble(objValue));
                                } else {
                                    errorMessage.append(messageSource.getMessage("个人负债(港币)格式不正确", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }

                        //是否被保险公司拒保
                        else if (objTitle.trim().equals(messageSource.getMessage("是否被保险公司拒保", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.SYS_YES_NO, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setBadFlag(valueCode);
                                } else {
                                    ctmCustomer.setBadFlag("N");
                                    errorMessage.append(messageSource.getMessage("是否被保险公司拒保在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setBadFlag("N");
                            }
                        }

                        //是否因伤病健康理由申请保险赔偿
                        else if (objTitle.trim().equals(messageSource.getMessage("是否因伤病健康理由申请保险赔偿", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                String valueCode = clbCodeService.getCodeValueByMeaning(request, Constants.SYS_YES_NO, objValue);
                                if (!StringUtils.isEmpty(valueCode)) {
                                    ctmCustomer.setCompensateFlag(valueCode);
                                } else {
                                    ctmCustomer.setCompensateFlag("N");
                                    errorMessage.append(messageSource.getMessage("是否因伤病健康理由申请保险赔偿在系统值列表中不存在", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            } else {
                                ctmCustomer.setCompensateFlag("N");
                            }
                        }

                        //承包保险公司
                        else if (objTitle.trim().equals(messageSource.getMessage("承包保险公司", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setBadInsuranceName(objValue);
                            }
                        }

                        //保险种类
                        else if (objTitle.trim().equals(messageSource.getMessage("保险种类", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setBadInsuranceType(objValue);
                            }
                        }

                        //保单生效日
                        else if (objTitle.trim().equals(messageSource.getMessage("保单生效日", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                Date date = vaTime(objValue);
                                if (date != null) {
                                    ctmCustomer.setBadEffactiveDate(date);
                                } else {
                                    ctmCustomer.setBadEffactiveDate(null);
                                    errorMessage.append(messageSource.getMessage("保单生效日格式错误", new Object[]{objTitle.trim()}, locale)).append(";");
                                }
                            }
                        }


                        //是否抽烟
                        else if (objTitle.trim().equals(messageSource.getMessage("是否抽烟", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setSmokeDesc(objValue);
                                ctmCustomer.setSmokeFlag("Y");
                            }
                        }

                        //是否喝酒
                        else if (objTitle.trim().equals(messageSource.getMessage("是否喝酒", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDrinkDesc(objValue);
                                ctmCustomer.setDrinkFlag("Y");
                            }
                        }

                        //是否抽烟
                        else if (objTitle.trim().equals(messageSource.getMessage("是否抽烟", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setSmokeDesc(objValue);
                                ctmCustomer.setSmokeFlag("Y");
                            }
                        }

                        //是否药物成瘾
                        else if (objTitle.trim().equals(messageSource.getMessage("是否药物成瘾", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDrugDesc(objValue);
                                ctmCustomer.setDrugFlag("Y");
                            }
                        }

                        //危险性活动
                        else if (objTitle.trim().equals(messageSource.getMessage("危险性活动", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDangerousDesc(objValue);
                                ctmCustomer.setDangerousFlag("Y");
                            }
                        }

                        //居住国外逗留
                        else if (objTitle.trim().equals(messageSource.getMessage("居住国外逗留", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setAbroadDesc(objValue);
                                ctmCustomer.setAbroadFlag("Y");
                            }
                        }

                        //身体残疾
                        else if (objTitle.trim().equals(messageSource.getMessage("身体残疾", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDisabilityDesc(objValue);
                                ctmCustomer.setDisabilityFlag("Y");
                            }
                        }

                        //精神病
                        else if (objTitle.trim().equals(messageSource.getMessage("精神病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setSpiritDesc(objValue);
                                ctmCustomer.setSpiritFlag("Y");
                            }
                        }

                        //内分泌失调
                        else if (objTitle.trim().equals(messageSource.getMessage("内分泌失调", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setEndocrineDesc(objValue);
                                ctmCustomer.setEndocrineFlag("Y");
                            }
                        }

                        //眼耳鼻喉
                        else if (objTitle.trim().equals(messageSource.getMessage("眼耳鼻喉", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setFaceDesc(objValue);
                                ctmCustomer.setFaceFlag("Y");
                            }
                        }

                        //呼吸系统疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("呼吸系统疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setRespirationDesc(objValue);
                                ctmCustomer.setRespirationFlag("Y");
                            }
                        }

                        //三高
                        else if (objTitle.trim().equals(messageSource.getMessage("三高", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setThreeDesc(objValue);
                                ctmCustomer.setThreeFlag("Y");
                            }
                        }

                        //循环系统疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("循环系统疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setCycleDesc(objValue);
                                ctmCustomer.setCycleFlag("Y");
                            }
                        }

                        //消化系统疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("消化系统疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDigestionDesc(objValue);
                                ctmCustomer.setDigestionFlag("Y");
                            }
                        }

                        //肝脏疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("肝脏疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setLiverDesc(objValue);
                                ctmCustomer.setLiverFlag("Y");
                            }
                        }

                        //生殖系统疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("生殖系统疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setReproductionDesc(objValue);
                                ctmCustomer.setReproductionFlag("Y");
                            }
                        }

                        //骨骼疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("骨骼疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setJointDesc(objValue);
                                ctmCustomer.setJointFlag("Y");
                            }
                        }

                        //癌症肿瘤
                        else if (objTitle.trim().equals(messageSource.getMessage("癌症肿瘤", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setTumorDesc(objValue);
                                ctmCustomer.setTumorFlag("Y");
                            }
                        }

                        //血液疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("血液疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setBloodDesc(objValue);
                                ctmCustomer.setBloodFlag("Y");
                            }
                        }

                        //艾滋病辅导或治疗
                        else if (objTitle.trim().equals(messageSource.getMessage("艾滋病辅导或治疗", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setAidsDesc(objValue);
                                ctmCustomer.setAidsFlag("Y");
                            }
                        }

                        //艾滋病体测
                        else if (objTitle.trim().equals(messageSource.getMessage("艾滋病体测", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setAidsTestDesc(objValue);
                                ctmCustomer.setAidsTestFlag("Y");
                            }
                        }

                        //3月内不正常生理状况
                        else if (objTitle.trim().equals(messageSource.getMessage("3月内不正常生理状况", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setSkinDesc(objValue);
                                ctmCustomer.setSkinFlag("Y");
                            }
                        }

                        //未提及的其他疾病
                        else if (objTitle.trim().equals(messageSource.getMessage("未提及的其他疾病", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setOtherDesc(objValue);
                                ctmCustomer.setOtherFlag("Y");
                            }
                        }

                        //正在接受的治疗
                        else if (objTitle.trim().equals(messageSource.getMessage("正在接受的治疗", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setOtherTreatDesc(objValue);
                                ctmCustomer.setOtherTreatFlag("Y");
                            }
                        }

                        //5年内接受过的检查
                        else if (objTitle.trim().equals(messageSource.getMessage("5年内接受过的检查", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setExaminationDesc(objValue);
                                ctmCustomer.setExaminationFlag("Y");
                            }
                        }

                        //5年内接受过的检查
                        else if (objTitle.trim().equals(messageSource.getMessage("5年内接受过的检查", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setExaminationDesc(objValue);
                                ctmCustomer.setExaminationFlag("Y");
                            }
                        }

                        //直系亲属遗传病史
                        else if (objTitle.trim().equals(messageSource.getMessage("直系亲属遗传病史", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setHereditaryDesc(objValue);
                                ctmCustomer.setHereditaryFlag("Y");
                            }
                        }

                        //是否怀孕
                        else if (objTitle.trim().equals(messageSource.getMessage("是否怀孕", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setPregnancyDesc(objValue);
                                ctmCustomer.setPregnancyFlag("Y");
                            }
                        }

                        //唐氏综合症测试
                        else if (objTitle.trim().equals(messageSource.getMessage("唐氏综合症测试", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setDownTestDesc(objValue);
                                ctmCustomer.setDownTestFlag("Y");
                            }
                        }

                        //因妇科问题看医生
                        else if (objTitle.trim().equals(messageSource.getMessage("因妇科问题看医生", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setGynecologyDesc(objValue);
                                ctmCustomer.setGynecologyFlag("Y");
                            }
                        }

                        //10年内的怀孕并发症
                        else if (objTitle.trim().equals(messageSource.getMessage("10年内的怀孕并发症", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setComplicationDesc(objValue);
                                ctmCustomer.setComplicationFlag("Y");
                            }
                        }

                        //是否接受过子宫乳房检查
                        else if (objTitle.trim().equals(messageSource.getMessage("是否接受过子宫乳房检查", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                                ctmCustomer.setGynecologyOthDesc(objValue);
                                ctmCustomer.setGynecologyOthFlag("Y");
                            }
                        }
                    }  //内循环完事

                    //通过第二次验证,进行数据转换
                    if ("".equals(errorMessage.toString())) {
                        ctmCustomer.setAttribute1(importBatchId.toString());        //用Attribute1记录批次号
                        ctmCustomers.add(ctmCustomer);
                    }
                }
                //数据转换之后更新数据状态
                importTemp.setImportMessage(errorMessage.toString());
                importTempService.updateError(importTemp, request);
            }

            //当前页行标题有错误
            if (!"".equals(errorTitleMessage.toString())) {
                importTempTitle.setImportMessage(errorTitleMessage.toString());
                importTempService.updateError(importTempTitle, request);
            }

        } else {
            throw new ValidationTableException(Constants.DATA_EMPTY, null);
        }

        return ctmCustomers;
    }


    /**
     * @param objValue
     * @return
     * @Description: 时间校验
     */
    public Date vaTime(String objValue) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");        //小写的mm表示的是分钟
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");        //小写的mm表示的是分钟
        try {
            Date date = format1.parse(objValue);
            return date;
        } catch (Exception e) {
            try {
                Date date = format2.parse(objValue);
                return date;
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param strNumber
     * @return
     * @Description: 校验是否为数字
     */
    public boolean vaNumber(String strNumber) {
        if (StringUtils.isEmpty(strNumber)) return false;

        String reg = "\\d+(\\.\\d+)?";
        return strNumber.matches(reg);
    }

    /**
     * @param strPhone
     * @return
     * @Description: 校验电话号码
     */
    public Map<String, String> vaPhone(String strPhone) {
        if (StringUtils.isEmpty(strPhone) || strPhone.indexOf("-") < 0) return null;

        Map<String, String> phoneMap = new HashMap<>();
        String[] phone = strPhone.split("-");
        if (phone != null && phone.length >= 2) {
            phoneMap.put("phone", phone[0]);
            phoneMap.put("phoneCode", phone[1]);
            return phoneMap;
        } else {
            return null;
        }
    }
}

