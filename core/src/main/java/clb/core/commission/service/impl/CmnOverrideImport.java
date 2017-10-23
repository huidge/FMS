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

import clb.core.commission.dto.CmnBasic;
import clb.core.commission.dto.CmnOverride;
import clb.core.commission.mapper.CmnBasicMapper;
import clb.core.commission.mapper.CmnOverrideMapper;
import clb.core.commission.service.ICmnOverrideService;
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
public class CmnOverrideImport extends AbstractImportExecute {

    @Autowired
    private IImportTempService importTempService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ICmnOverrideService cmnOverrideService;
    @Autowired
    private CmnOverrideMapper cmnOverrideMapper;
    @Autowired
    private CmnBasicMapper cmnBasicMapper;
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
        List<CmnOverride> cmnOverrides = validateData(importBatchId, request);

        if (importTempService.selectErrorCount(importBatchId) == 0) {//验证没有错误
        	cmnOverrides.forEach(cmnOverride -> {
        		CmnOverride cmnOverride1 = cmnOverrideMapper.queryMaxVersion(cmnOverride);
                if (cmnOverride1!=null && cmnOverride1.getOverrideId()!=null) {
                	cmnOverride.setOverrideId(cmnOverride1.getOverrideId());
                	cmnOverrideService.updateByPrimaryKeySelective(request, cmnOverride);
                } else {
                	cmnOverride.setVersion(1L);
                	cmnOverrideService.insertSelective(request, cmnOverride);
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
    public List<CmnOverride> validateData(Long importBatchId, IRequest request) throws ValidationTableException, Exception {

        String[] locales = request.getLocale().split("_");
        Locale locale = new Locale(locales[0], locales[1]);

        PropertyDescriptor attributeTitle = null;
        PropertyDescriptor attributeValue = null;
        List<ImportTemp> importTemps = new ArrayList<ImportTemp>();//根据流水号查得的所有临时表数据
        List<CmnOverride> cmnOverrides = new ArrayList<CmnOverride>();//将临时表的数据转换成供应商比例维护数据
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
                	CmnOverride cmnOverride = new CmnOverride();
                	
                	//上级供应商是否属于产品公司 ,如果是属于产品公司，则 上级供应商=产品公司。否则该条数据是二级或三级或四级....供应商数据
                	boolean companyFlag=true;
                	Long companySupplierId=null;
                	
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
                        // 供应商名称验证
                        else if (objTitle.trim().equals(messageSource.getMessage("供应商名称", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	SpeSupplier speSupplier=new SpeSupplier();
                				speSupplier.setStatusCode("Y");
                				speSupplier.setName(objValue.trim());
                				List<SpeSupplier>list= speSupplierMapper.selectByName(speSupplier);
                				
            					if(CollectionUtils.isEmpty(list)){
            						errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
            					}else{
            						cmnOverride.setSupplierId(list.get(0).getSupplierId());
            					}

                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        // 上级供应商验证
                        else if (objTitle.trim().equals(messageSource.getMessage("上级供应商", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	SpeSupplier speSupplier=new SpeSupplier();
                				speSupplier.setStatusCode("Y");
                				speSupplier.setName(objValue.trim());
                				List<SpeSupplier>list= speSupplierMapper.selectByName(speSupplier);
                				
            					if(CollectionUtils.isEmpty(list)){
            						errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
            					}else{
            						if(cmnOverride.getSupplierId()!=null &&cmnOverride.getSupplierId().equals(list.get(0).getSupplierId())){
            							errorMessage.append(messageSource.getMessage("供应商与上级供应商不能一样", new Object[]{objValue.trim()},
                                                locale)).append(";");
            						}else{
            							cmnOverride.setSuperiorSupplierId(list.get(0).getSupplierId());
            						}
            					}

                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                     // 渠道分类
                        else if (objTitle.trim().equals(messageSource.getMessage("渠道分类", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	String valueCode=clbCodeService.getCodeValueByMeaning(request, "CNL.CHANNEL_CLASS", objValue.trim());
                				if(StringUtils.isNotBlank(valueCode)){
                					cmnOverride.setChannelTypeCode(valueCode);
                				}else{
                					errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
                				}
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                     // 产品公司
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
            						if(cmnOverride.getSuperiorSupplierId().equals(list.get(0).getSupplierId())){
            							companyFlag=true;
            							companySupplierId=cmnOverride.getSuperiorSupplierId();
            						}else{
            							companyFlag=false;
            							companySupplierId=list.get(0).getSupplierId();
            						}
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
            					prdItems.setSupplierId(companySupplierId);
            					List<PrdItems> list=prdItemsMapper.selectByItemName(prdItems);
            					if(CollectionUtils.isEmpty(list)){
            						errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
            					}else{
            						cmnOverride.setItemId(list.get(0).getItemId());
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
                            	subline.setItemId(cmnOverride.getItemId());
                            	subline.setSublineItemName(objValue.trim());
                    			//是否是否已经存在
                    			List<PrdItemSubline> list=prdItemSublineMapper.select(subline);
                    			if(CollectionUtils.isEmpty(list)){
                    				errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                            locale)).append(";");
                    			}else{
                    				cmnOverride.setContributionPeriod(objValue.trim());
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
                                	mode.setItemId(cmnOverride.getItemId());
                                	mode.setCurrencyCode(valueCode);
                        			//是否是否已经存在
                        			List<PrdItemPaymode> list=prdItemPaymodeMapper.select(mode);
                        			if(CollectionUtils.isEmpty(list)){
                        				errorMessage.append(messageSource.getMessage(Constants.NO_EXIST_ATTR, new Object[]{objValue.trim()},
                                                locale)).append(";");
                        			}else{
                        				cmnOverride.setCurrency(valueCode);
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
                            	dto.setItemId(cmnOverride.getItemId());
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
                    					cmnOverride.setPayMethod(valueCode);
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
                            	cmnOverride.setPolicyholdersMinAge(Long.valueOf(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        //投保人年龄至
                        else if (objTitle.trim().equals(messageSource.getMessage("投保人年龄至", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setPolicyholdersMaxAge(Long.valueOf(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第1年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第1年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride1(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第1年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第1年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride1(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第1年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第1年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment1(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第2年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第2年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride2(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第2年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第2年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride2(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第2年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第2年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment2(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第3年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第3年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride3(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第3年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第3年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride3(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第3年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第3年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment3(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第4年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第4年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride4(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第4年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第4年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride4(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第4年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第4年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment4(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第5年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第5年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride5(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第5年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第5年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride5(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第5年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第5年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment5(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第6年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第6年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride6(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第6年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第6年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride6(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第6年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第6年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment6(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第7年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第7年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride7(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第7年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第7年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride7(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        //第7年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第7年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment7(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        //第8年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第8年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride8(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第8年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第8年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride8(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第8年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第8年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment8(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第9年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第9年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride9(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第9年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第9年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride9(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第9年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第9年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment9(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第10年上级
                        else if (objTitle.trim().equals(messageSource.getMessage("第10年上级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setSuperiorOverride10(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第10年本级
                        else if (objTitle.trim().equals(messageSource.getMessage("第10年本级", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setOverride10(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //第10年调整百分比
                        else if (objTitle.trim().equals(messageSource.getMessage("第10年调整百分比", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	cmnOverride.setAdjustment10(new BigDecimal(objValue.trim()));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                        
                        //有效期起
                        else if (objTitle.trim().equals(messageSource.getMessage("有效期起", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            	cmnOverride.setEffectiveStartDate(dateFormat.parse(objValue.trim()+" 00:00:00"));
                            } else {
                                errorMessage.append(messageSource.getMessage(Constants.NO_UPLOAD_ATTR, new Object[]{objTitle.trim()},
                                        locale)).append(";");
                            }
                        }
                      //有效期至
                        else if (objTitle.trim().equals(messageSource.getMessage("有效期至", null, locale))) {
                            if (null != objValue && !"".equals(objValue.trim())) {
                            	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            	cmnOverride.setEffectiveEndDate(dateFormat.parse(objValue.trim()+" 23:59:59"));
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

                    /*****
                     * 如果上级供应商是产品公司，则需要判断是否存在产品公司以及是否存在源头数据
                     * 否则 ，需要判断是否存在父级 override
                     */
                    if(companyFlag){
                    	//判断是否存在产品公司
                    	CmnBasic cmnBasic=new CmnBasic();
                    	cmnBasic.setItemId(cmnOverride.getItemId());
                    	cmnBasic.setContributionPeriod(cmnOverride.getContributionPeriod());
                    	cmnBasic.setCurrency(cmnOverride.getCurrency());
                    	cmnBasic.setPayMethod(cmnOverride.getPayMethod());
                    	cmnBasic.setPolicyholdersMinAge(cmnOverride.getPolicyholdersMinAge());
                    	cmnBasic.setPolicyholdersMaxAge(cmnOverride.getPolicyholdersMaxAge());
                    	if(CollectionUtils.isEmpty(cmnBasicMapper.selectBasic(cmnBasic))){
                    		errorMessage.append(messageSource.getMessage("不存在上级Basic", null,locale)).append(";");
                    	}
                    	//是否存在源头数据
                    	if(!cmnOverride.getChannelTypeCode().equals("YT") && cmnOverrideMapper.queryYTDate(cmnOverride)==null){
                    		errorMessage.append(messageSource.getMessage("不存在源头数据", null,locale)).append(";");
                    	}
                    }else{
                    	CmnOverride parentOverride=queryParentOverride(request, cmnOverride, companySupplierId);
                    	if(parentOverride==null){
                    		errorMessage.append(messageSource.getMessage("不存在上级override", null,locale)).append(";");
                    	}else{
                    		cmnOverride.setParentOverrideId(parentOverride.getOverrideId());
                    	}
                    }
                    	
                    if ("".equals(errorMessage.toString())) {//通过第二次验证,进行数据转换

                        // 校验唯一性
                        if (uniqueList.contains(cmnOverride.getSupplierId()+cmnOverride.getSuperiorSupplierId()+cmnOverride.getChannelTypeCode()+cmnOverride.getItemId() + cmnOverride.getContributionPeriod()+cmnOverride.getCurrency()+cmnOverride.getPayMethod()+cmnOverride.getPolicyholdersMinAge()+cmnOverride.getPolicyholdersMaxAge())) {
                            errorMessage.append(messageSource.getMessage("该basic已存在！", null, locale)).append(";");
                        } else {
                            uniqueList.add(cmnOverride.getSupplierId()+cmnOverride.getSuperiorSupplierId()+cmnOverride.getChannelTypeCode()+cmnOverride.getItemId() + cmnOverride.getContributionPeriod()+cmnOverride.getCurrency()+cmnOverride.getPayMethod()+cmnOverride.getPolicyholdersMinAge()+cmnOverride.getPolicyholdersMaxAge());
                        }

                        cmnOverride.setAttribute1(importBatchId.toString());   //用Attribute1记录批次号
                        cmnOverrides.add(cmnOverride);
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

        return cmnOverrides;
    }
    
    
    public CmnOverride queryParentOverride(IRequest request,CmnOverride override,Long companySupplierId){
    	CmnOverride parentOverride=new CmnOverride();
    	parentOverride.setSupplierId(override.getSuperiorSupplierId());
    	parentOverride.setSuperiorSupplierId(companySupplierId);
    	//父级必须是供应商类型
    	parentOverride.setChannelTypeCode("GYS");
    	parentOverride.setItemId(override.getItemId());
    	parentOverride.setContributionPeriod(override.getContributionPeriod());
    	parentOverride.setCurrency(override.getCurrency());
    	parentOverride.setPayMethod(override.getPayMethod());
    	parentOverride.setPolicyholdersMaxAge(override.getPolicyholdersMaxAge());
    	parentOverride.setPolicyholdersMinAge(override.getPolicyholdersMinAge());
    	parentOverride.setOverride1(override.getSuperiorOverride1());
    	parentOverride.setOverride2(override.getSuperiorOverride2());
    	parentOverride.setOverride3(override.getSuperiorOverride3());
    	parentOverride.setOverride4(override.getSuperiorOverride4());
    	parentOverride.setOverride5(override.getSuperiorOverride5());
    	parentOverride.setOverride6(override.getSuperiorOverride6());
    	parentOverride.setOverride7(override.getSuperiorOverride7());
    	parentOverride.setOverride8(override.getSuperiorOverride8());
    	parentOverride.setOverride9(override.getSuperiorOverride9());
    	parentOverride.setOverride10(override.getSuperiorOverride10());
    	List<CmnOverride>parentList= cmnOverrideMapper.select(parentOverride);
    	if(CollectionUtils.isEmpty(parentList)){
    		return null;
    	}else{
    		return parentList.get(parentList.size()-1);
    	}
    }
    
}
