package clb.core.commission.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.mapper.CodeValueMapper;
import com.hand.hap.system.service.ILovService;

import clb.core.commission.dto.CmnExtras;
import clb.core.commission.mapper.CmnExtrasMapper;
import clb.core.commission.service.ICmnExtrasService;
import clb.core.fnd.dto.ImportTemp;
import clb.core.fnd.service.IImportTempService;
import clb.core.fnd.service.impl.AbstractImportExecute;
import clb.core.fnd.utils.Constants;
import clb.core.fnd.utils.ValidationTableException;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.production.dto.PrdItemPaymode;
import clb.core.production.dto.PrdItemSubline;
import clb.core.production.dto.PrdItems;
import clb.core.production.mapper.PrdItemPaymodeMapper;
import clb.core.production.mapper.PrdItemSublineMapper;
import clb.core.production.mapper.PrdItemsMapper;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.mapper.SpeSupplierMapper;
import clb.core.system.service.IClbCodeService;

/**
 * @author tiansheng.ye@hand-china.com
 * @version 1.0
 * @name 
 * @description basic 佣金数据导入
 */
@Component
public class CmnExtrasImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ICmnExtrasService cmnExtrasService;
    @Autowired
    private CmnExtrasMapper cmnExtrasMapper;

    @Autowired
    private CodeValueMapper codeValueMapper;
    @Autowired
    private IClbCodeService clbCodeService;
    @Autowired
    private SpeSupplierMapper speSupplierMapper;
    @Autowired
    private PrdItemsMapper prdItemsMapper;
    @Autowired
    private PrdItemSublineMapper prdItemSublineMapper;
    @Autowired
	private PrdItemPaymodeMapper prdItemPaymodeMapper;
    @Autowired
	private ILovService lovService;
    @Autowired
    private IPlnPlanLibraryService plnPlanLibraryService;
    
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
        List<CmnExtras> cmnExtrass = validateData(importBatchId, request);

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
        	cmnExtrass.forEach(cmnExtras -> {
                List<CmnExtras> cmnCmnExtrasList = cmnExtrasMapper.selectExtras(cmnExtras);
                if (null != cmnCmnExtrasList && cmnCmnExtrasList.size() == 1) {
                	cmnExtras.setExtraId(cmnCmnExtrasList.get(0).getExtraId());
                	cmnExtrasService.updateByPrimaryKeySelective(request, cmnExtras);
                } else if (cmnCmnExtrasList.size() == 0) {
                	cmnExtras.setVersion(1L);
                	cmnExtrasService.insertSelective(request, cmnExtras);
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
    public List<CmnExtras> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {

        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
        List<CmnExtras> cmnExtrass = new ArrayList<CmnExtras>();//将临时表的数据转换成供应商比例维护数据
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
                	CmnExtras cmnExtras = new CmnExtras();

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
                        // 产品公司验证
                        else if (objTitle.trim().equals(messageSource.getMessage("产品公司", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	SpeSupplier speSupplier=new SpeSupplier();
                				speSupplier.setStatusCode("Y");
                				speSupplier.setName(objValue.trim());
                				List<SpeSupplier>list= speSupplierMapper.selectByName(speSupplier);
                				
            					if(CollectionUtils.isEmpty(list)){
            						errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
            					}else{
            						cmnExtras.setSupplierId(list.get(0).getSupplierId());
            					}

                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 产品名称验证
                        else if (objTitle.trim().equals(messageSource.getMessage("产品名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	PrdItems prdItems=new PrdItems();
            					prdItems.setItemName(objValue);
            					prdItems.setSupplierId(cmnExtras.getSupplierId());
            					List<PrdItems> list=prdItemsMapper.selectByItemName(prdItems);
            					if(CollectionUtils.isEmpty(list)){
            						errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
            					}else{
            						cmnExtras.setItemId(list.get(0).getItemId());
            					}

                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 供款期验证
                        else if (objTitle.trim().equals(messageSource.getMessage("供款期", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	PrdItemSubline subline =new PrdItemSubline();
                            	subline.setItemId(cmnExtras.getItemId());
                            	subline.setSublineItemName(objValue.trim());
                    			//是否是否已经存在
                    			List<PrdItemSubline> list=prdItemSublineMapper.select(subline);
                    			if(CollectionUtils.isEmpty(list)){
                    				errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
                    			}else{
                    				cmnExtras.setContributionPeriod(objValue.trim());
                    			}
                            }else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 币种验证
                        else if (objTitle.trim().equals(messageSource.getMessage("币种", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	String valueCode=clbCodeService.getCodeValueByMeaning(request, "PUB.CURRENCY", objValue.trim());
                				if(StringUtils.isNotBlank(valueCode)){
                					PrdItemPaymode mode=new PrdItemPaymode();
                                	mode.setItemId(cmnExtras.getItemId());
                                	mode.setCurrencyCode(valueCode);
                        			//是否是否已经存在
                        			List<PrdItemPaymode> list=prdItemPaymodeMapper.select(mode);
                        			if(CollectionUtils.isEmpty(list)){
                        				errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                                locale)).append(";");
                        			}else{
                        				cmnExtras.setCurrency(valueCode);
                        			}
                				}else{
                					errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
                				}
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }

                        // 缴费方式
                        else if (objTitle.trim().equals(messageSource.getMessage("缴费方式", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	//查询该产品中的所有缴费方式
                            	PlnPlanLibrary dto=new PlnPlanLibrary();
                            	dto.setItemId(cmnExtras.getItemId());
                            	List<PlnPlanLibrary> list=plnPlanLibraryService.queryPaymethodByItem(request,dto);
                    			if(CollectionUtils.isEmpty(list)){
                    				errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
                    			}else{
                    				String valueCode=null;
                    				for(PlnPlanLibrary pl:list){
                    					if(pl.getMeaning().equals(objValue.trim())){
                    						valueCode=pl.getValue();
                    					}
                    				}
                    				if(StringUtils.isNotBlank(valueCode)){
                    					cmnExtras.setPayMethod(valueCode);
                    				}else{
                    					errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                                locale)).append(";");
                    				}
                    			}
                            }else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                     // 投保人年龄自
                        else if (objTitle.trim().equals(messageSource.getMessage("投保人年龄自", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setPolicyholdersMinAge(Long.valueOf(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        //投保人年龄至
                        else if (objTitle.trim().equals(messageSource.getMessage("投保人年龄至", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setPolicyholdersMaxAge(Long.valueOf(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第一年
                        else if (objTitle.trim().equals(messageSource.getMessage("第一年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra1(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第二年
                        else if (objTitle.trim().equals(messageSource.getMessage("第二年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra2(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第三年
                        else if (objTitle.trim().equals(messageSource.getMessage("第三年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra3(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第四年
                        else if (objTitle.trim().equals(messageSource.getMessage("第四年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra4(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第五年
                        else if (objTitle.trim().equals(messageSource.getMessage("第五年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra5(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第六年
                        else if (objTitle.trim().equals(messageSource.getMessage("第六年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra6(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第七年
                        else if (objTitle.trim().equals(messageSource.getMessage("第七年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra7(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第八年
                        else if (objTitle.trim().equals(messageSource.getMessage("第八年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra8(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第九年
                        else if (objTitle.trim().equals(messageSource.getMessage("第九年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra9(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第十年
                        else if (objTitle.trim().equals(messageSource.getMessage("第十年", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnExtras.setExtra10(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        
                        //有效期起
                        else if (objTitle.trim().equals(messageSource.getMessage("有效期起", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            	cmnExtras.setEffectiveStartDate(dateFormat.parse(objValue.trim()+" 00:00:00"));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //有效期至
                        else if (objTitle.trim().equals(messageSource.getMessage("有效期至", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            	cmnExtras.setEffectiveEndDate(dateFormat.parse(objValue.trim()+" 23:59:59"));
                            } else {
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
                        if (uniqueList.contains(cmnExtras.getItemId() + cmnExtras.getContributionPeriod()+cmnExtras.getCurrency()+cmnExtras.getPayMethod()+cmnExtras.getPolicyholdersMinAge()+cmnExtras.getPolicyholdersMaxAge())) {
                            errorMessage.append(messageSource.getMessage("该basic已存在！", null, locale)).append(";");
                        } else {
                            uniqueList.add(cmnExtras.getItemId() + cmnExtras.getContributionPeriod()+cmnExtras.getCurrency()+cmnExtras.getPayMethod()+cmnExtras.getPolicyholdersMinAge()+cmnExtras.getPolicyholdersMaxAge());
                        }

                      //文件中的数据校验年龄是否重叠
                        if(check(cmnExtrass, cmnExtras)){
                        	//与表中的数据校验
                        	if(!cmnExtrasService.checkData(request, cmnExtras)){
                        		errorMessage.append(messageSource.getMessage("年龄不能与原有数据重叠！", null, locale)).append(";");
                        	}
                        }else{
                        	errorMessage.append(messageSource.getMessage("excel中存在 年龄数据重叠！", null, locale)).append(";");
                        }
                        
                        cmnExtras.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                        cmnExtrass.add(cmnExtras);
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

        return cmnExtrass;
    }
    
    public boolean check(List<CmnExtras> cmnExtrass,CmnExtras cmnExtras ){
    	boolean check=true;
		//校验年龄是否重叠
		for(CmnExtras cb:cmnExtrass){
			if(cb.getSupplierId().equals(cmnExtras.getSupplierId()) && cb.getItemId().equals(cmnExtras.getItemId()) &&
					cb.getContributionPeriod().equals(cmnExtras.getContributionPeriod()) && cb.getCurrency().equals(cmnExtras.getCurrency())
					&& cb.getPayMethod().equals(cmnExtras.getPayMethod())){
				if( (cb.getPolicyholdersMinAge()<=cmnExtras.getPolicyholdersMinAge() && cmnExtras.getPolicyholdersMinAge()<=cb.getPolicyholdersMaxAge())
					|| (cb.getPolicyholdersMaxAge()>=cmnExtras.getPolicyholdersMaxAge() && cmnExtras.getPolicyholdersMaxAge()>=cb.getPolicyholdersMinAge()) ){
					check=false;
				}
				if( (cmnExtras.getPolicyholdersMinAge()<=cb.getPolicyholdersMinAge() && cb.getPolicyholdersMinAge()<=cmnExtras.getPolicyholdersMaxAge())
						|| (cmnExtras.getPolicyholdersMaxAge()>=cb.getPolicyholdersMaxAge() && cb.getPolicyholdersMaxAge()>=cmnExtras.getPolicyholdersMinAge()) ){
						check=false;
				}
			}
		}
		return check;
    }
}
