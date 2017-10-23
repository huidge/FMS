package clb.core.commission.service.impl;
/**
 * Created by jiaolong.li on 2017-04-24.
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlChannel;
import clb.core.channel.dto.CnlChannelContract;
import clb.core.channel.dto.CnlContractRate;
import clb.core.channel.dto.CnlLevel;
import clb.core.channel.dto.CnlProSupRelation;
import clb.core.channel.mapper.CnlContractRateMapper;
import clb.core.channel.mapper.CnlProSupRelationMapper;
import clb.core.commission.dto.CmnAdtRiskRelation;
import clb.core.commission.dto.CmnChannelCommission;
import clb.core.commission.dto.CmnChannelRatioDetail;
import clb.core.commission.dto.CmnQueryCommission;
import clb.core.commission.dto.CmnSupplierCommission;
import clb.core.commission.dto.CmnSupplierItemRelation;
import clb.core.commission.dto.CmnTradeRoute;
import clb.core.commission.mapper.CmnAdtRiskRelationMapper;
import clb.core.commission.mapper.CmnChannelCommissionMapper;
import clb.core.commission.mapper.CmnChannelCommissionShowMapper;
import clb.core.commission.mapper.CmnSupplierCommissionMapper;
import clb.core.commission.mapper.CmnTradeRouteMapper;
import clb.core.commission.mapper.CmnTradeRouteShowMapper;
import clb.core.commission.service.ICmnChannelCommissionService;
import clb.core.commission.service.ICmnSupplierItemRelationService;
import clb.core.common.utils.CalculateAge;
import clb.core.common.utils.CommonUtil;
import clb.core.production.dto.PrdItems;
import clb.core.production.service.IPrdItemsService;
import clb.core.system.service.IClbUserService;

@Service
@Transactional
public class CmnChannelCommissionServiceImpl extends BaseServiceImpl<CmnChannelCommission> implements ICmnChannelCommissionService {

    @Autowired
    private CmnChannelCommissionMapper cmnChannelCommissionMapper;

    @Autowired
    private CmnSupplierCommissionMapper cmnSupplierCommissionMapper;

    @Autowired
    private CnlContractRateMapper cnlContractRateMapper;

    @Autowired
    private IPrdItemsService itemsService;

    @Autowired
    private CmnAdtRiskRelationMapper cmnAdtRiskRelationMapper;

    @Autowired
    private IClbUserService userService;

    @Autowired
    private CmnTradeRouteMapper routeMapper;

    @Autowired
    private CmnChannelCommissionShowMapper commissionShowMapper;

    @Autowired
    private CmnTradeRouteShowMapper routeShowMapper;
    
    @Autowired
    private CnlProSupRelationMapper cnlProSupRelationMapper;
    
    @Autowired
	private IProfileService profileService;
    
    @Autowired
    private ICmnSupplierItemRelationService cmnSupplierItemRelationService;

    public List<CmnChannelCommission> selectAllFields(IRequest requestContext, CmnChannelCommission cmnChannelCommission, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CmnChannelCommission> cmnList = cmnChannelCommissionMapper.selectAllFields(cmnChannelCommission);
        return cmnList;
    }

    public List<CmnChannelCommission> selectBatchChannelCmn(CmnChannelCommission cmnChannelCommission) {
        return cmnChannelCommissionMapper.selectBatchChannelCmn(cmnChannelCommission);
    }

    public List<CmnChannelCommission> selectShowAllFields(IRequest requestContext, CmnChannelCommission cmnChannelCommission, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CmnChannelCommission> cmnList = cmnChannelCommissionMapper.selectShowAllFields(cmnChannelCommission);
        return cmnList;
    }

    public List<CmnChannelCommission> queryLineQty() {
        return cmnChannelCommissionMapper.queryLineQty();
    }

    public List<CmnChannelCommission> selectAllFields(CmnChannelCommission cmnChannelCommission) {
        List<CmnChannelCommission> cmnList = cmnChannelCommissionMapper.selectAllFields(cmnChannelCommission);
        return cmnList;
    }

    public void deleteCmnData(CmnChannelCommission cmnChannelCommission) {
        cmnChannelCommissionMapper.deleteCmnData(cmnChannelCommission);
    }

    public List<CnlChannelContract> queryContractList(CnlChannelContract cnlChannelContract) {
        return cmnChannelCommissionMapper.queryContractList(cnlChannelContract);
    }

    public List<CnlLevel> queryLevelList(CnlLevel cnlLevel) {
        return cmnChannelCommissionMapper.queryLevelList(cnlLevel);
    }

    public List<CmnSupplierCommission> querySupplierCmnList(CmnSupplierCommission supplierCommission) {
        return cmnChannelCommissionMapper.querySupplierCmnList(supplierCommission);
    }

    public List<CnlContractRate> queryContractRateList(CnlContractRate contractRate) {
        return cmnChannelCommissionMapper.queryContractRateList(contractRate);
    }

    public List<CnlContractRate> queryTopRateList(CnlContractRate contractRate) {
        return cmnChannelCommissionMapper.queryTopRateList(contractRate);
    }

    public List<CmnChannelRatioDetail> queryTopRatioDetailList(CmnChannelRatioDetail channelRatioDetail) {
        return cmnChannelCommissionMapper.queryTopRatioDetailList(channelRatioDetail);
    }

    public List<CnlChannel> queryChildChannelList(CnlChannel cnlChannel) {
        return cmnChannelCommissionMapper.queryChildChannelList(cnlChannel);
    }

    public List<CnlChannelContract> queryContractSpeList(CnlChannelContract cnlChannelContract) {
        return cmnChannelCommissionMapper.queryContractSpeList(cnlChannelContract);
    }

    public List<CnlChannelContract> queryContractCnlList(CnlChannelContract cnlChannelContract){
        return cmnChannelCommissionMapper.queryContractCnlList(cnlChannelContract);
    }

    public List<CmnChannelCommission> queryChannelCmnList(CmnChannelCommission cmnChannelCommission){
        return cmnChannelCommissionMapper.queryChannelCmnList(cmnChannelCommission);
    }

    public List<CmnChannelCommission> queryBondList(CmnChannelCommission cmnChannelCommission) {
        return cmnChannelCommissionMapper.queryBondList(cmnChannelCommission);
    }

    public List<CmnChannelCommission> selectCommissionById(CmnChannelCommission cmnChannelCommission) {
        return cmnChannelCommissionMapper.selectCommissionById(cmnChannelCommission);
    }

    /**
     * 保单预约选择佣金
     * @param request
     * @param dto
     * @return
     */
    public List<CmnChannelCommission> chooseCommission(IRequest request, CmnQueryCommission dto) {

        // 年龄转化
        Long policyholdersAge = null;
        if (dto.getBirthDate() != null) {
            String ageType = itemsService.selectSupplierAgeTypeByItem(dto.getItemId());
            policyholdersAge = new Long(CalculateAge.accessAge(dto.getBirthDate(), ageType));
        }

        if (policyholdersAge == null || "".equals(policyholdersAge.toString())) {
            policyholdersAge = -1L;
        }

        // 如果缴费方式为“预缴”，则默认为“年缴”
        if ("FJ".equals(dto.getPayMethod())) {
            dto.setPayMethod("AP");
        }
        if(!"N".equals(dto.getAttribute1())) {
        	
        }
        //根据选择的  渠道 和 产品大分类 去渠道供应商产品关系表中匹配
        CnlProSupRelation cnlProSupRelation = new CnlProSupRelation();
        cnlProSupRelation.setChannelId(dto.getChannelId());
        cnlProSupRelation.setBigClass("BX");
        List<CnlProSupRelation> cnlProSupRelationList = cnlProSupRelationMapper.selectByCondition(cnlProSupRelation);        
       
        //cnlProSupRelationList集合中  供应商id可能会重复  去重
        Set<Long> supplierIdSet = new HashSet<>();
        //如果匹配到数据
        if(CollectionUtils.isNotEmpty(cnlProSupRelationList)) {
			//循环匹配到数据
        	Iterator<CnlProSupRelation> iterator = cnlProSupRelationList.iterator();
        	while (iterator.hasNext()) {
        		CnlProSupRelation cnlProSupRelation2 = iterator.next();
        		//先从最小的限制条件排查起
        		//判断年期
        		if(StringUtils.isNotBlank(cnlProSupRelation2.getContributionPeriod())) {
        			if(!cnlProSupRelation2.getContributionPeriod().equals(dto.getContributionPeriod())) {
        				iterator.remove();
        				continue;
        			}else {
        				if(cnlProSupRelation2.getProductId().equals(dto.getItemId())){
            				supplierIdSet.add(cnlProSupRelation2.getSupplierId());
        				}else {
        					iterator.remove();
        					continue;
        				}
        			}
        		}
			}
        	
        	//上面没有匹配到数据
        	if(CollectionUtils.isNotEmpty(cnlProSupRelationList) && CollectionUtils.isEmpty(supplierIdSet)) {
        		//循环匹配到数据
        		iterator = cnlProSupRelationList.iterator();
            	while (iterator.hasNext()) {
            		CnlProSupRelation cnlProSupRelation2 = iterator.next();
            		//判断产品
            		if(cnlProSupRelation2.getProductId() != null) {
            			if(!cnlProSupRelation2.getProductId().equals(dto.getItemId())){
            				iterator.remove();
            				continue;
            			}else {
            				supplierIdSet.add(cnlProSupRelation2.getSupplierId());
            			}
            		}
            	}
        	}
        	
        	PrdItems items = new PrdItems();
        	//找到预约的产品信息
        	items.setItemId(dto.getItemId());
        	items = itemsService.selectByPrimaryKey(request, items);
        	//上面没有匹配到数据
        	if(CollectionUtils.isNotEmpty(cnlProSupRelationList) && CollectionUtils.isEmpty(supplierIdSet)) {
        		iterator = cnlProSupRelationList.iterator();
            	while (iterator.hasNext()) {
            		CnlProSupRelation cnlProSupRelation2 = iterator.next();
            		//判断产品公司
            		if(cnlProSupRelation2.getPrdSupplierId()!= null) {
            			if(cnlProSupRelation2.getPrdSupplierId() != dto.getPrdSupplierId()) {
            				iterator.remove();
            				continue;
            			}else {
            				if(StringUtils.isNotBlank(cnlProSupRelation2.getMinClass())) {
            					if(cnlProSupRelation2.getMinClass().equals(items.getMinClass())) {
            						supplierIdSet.add(cnlProSupRelation2.getSupplierId());
            					}else {
            						iterator.remove();
            						continue;
            					}
            				}
            				if(StringUtils.isNotBlank(cnlProSupRelation2.getMidClass())) {
            					if(!cnlProSupRelation2.getMidClass().equals(items.getMidClass())) {
            						iterator.remove();
            						continue;
            					}else {
            						supplierIdSet.add(cnlProSupRelation2.getSupplierId());
            					}
            				}
            			}
            		}
            	}	
        	}
        	//没有匹配到数据
        	if(CollectionUtils.isNotEmpty(cnlProSupRelationList) && CollectionUtils.isEmpty(supplierIdSet)) {
        		iterator = cnlProSupRelationList.iterator();
            	while (iterator.hasNext()) {
            		CnlProSupRelation cnlProSupRelation2 = iterator.next();
            		//判断小分类
            		if(StringUtils.isNotBlank(cnlProSupRelation2.getMinClass())) {
            			if(cnlProSupRelation2.getMinClass().equals(items.getMinClass())) {
            				supplierIdSet.add(cnlProSupRelation2.getSupplierId());
            			}else {
            				iterator.remove();
            				continue;
            			}
            		}
            	}
        	}
        	
        	//没有匹配到数据
        	if(CollectionUtils.isNotEmpty(cnlProSupRelationList) && CollectionUtils.isEmpty(supplierIdSet)) {
        		iterator = cnlProSupRelationList.iterator();
            	while (iterator.hasNext()) {
            		CnlProSupRelation cnlProSupRelation2 = iterator.next();
        			//判断中分类
            		if(StringUtils.isNotBlank(cnlProSupRelation2.getMidClass())) {
            			if(cnlProSupRelation2.getMidClass().equals(items.getMidClass())) {
            				supplierIdSet.add(cnlProSupRelation2.getSupplierId());
            			}else {
            				iterator.remove();
            				continue;
            			}
            		}
        		}
        	}
        	//没有匹配到数据  全部加入
        	if(CollectionUtils.isNotEmpty(cnlProSupRelationList) && CollectionUtils.isEmpty(supplierIdSet)) {
        		iterator = cnlProSupRelationList.iterator();
            	while (iterator.hasNext()) {
            		CnlProSupRelation cnlProSupRelation2 = iterator.next();
        			supplierIdSet.add(cnlProSupRelation2.getSupplierId());
        		}
        	}
        }
        
        CmnChannelCommission channelCommission = new CmnChannelCommission();
        channelCommission.setChannelId(dto.getChannelId());
        channelCommission.setCurrency(dto.getCurrency());
        channelCommission.setContributionPeriod(dto.getContributionPeriod());
        channelCommission.setPolicyholdersAge(policyholdersAge);
        channelCommission.setPayMethod(dto.getPayMethod());
        channelCommission.setEffectiveDate(dto.getEffectiveDate());
        channelCommission.setItemId(dto.getItemId());
        channelCommission.setDealPath(dto.getDealPath());
        
        List<CmnChannelCommission> cmnList = new ArrayList<>();
        //如果渠道供应商产品关系找到数据  就不用找产品默认供应商
        if(CollectionUtils.isNotEmpty(supplierIdSet)) {
        	for (Long supplierId : supplierIdSet) {
        		//查询交易路线上包含这类似供应商和渠道的佣金
        		channelCommission.setDealPathChannel("C"+dto.getChannelId());
        		channelCommission.setDealPathSupplier("S"+supplierId);
        		//根据条件去佣金表匹配
        		List<CmnChannelCommission> commissionList = cmnChannelCommissionMapper.chooseCommission(channelCommission);
        		Iterator<CmnChannelCommission> iterator = commissionList.iterator();
        		while(iterator.hasNext()) {
        			String[] dealPathArray = iterator.next().getDealPath().replace(".", " ").split(" ");
        			List<String> dealPathList = Arrays.asList(dealPathArray);
        			if(!dealPathList.contains("S"+supplierId) || !dealPathList.contains("C"+dto.getChannelId())) {
        				iterator.remove();
        			}
        		}
        		cmnList.addAll(commissionList);
			}
        }
        //如果配置文件中配置的"是"并且渠道供应商产品关系没有匹配到数据  就需要去默认供应商表里面寻找数据
        String flag = profileService.getProfileValue(request, "COMMISSION_ONLY_ONE");//读取配置文件
        if("是".equals(flag) && CollectionUtils.isEmpty(cmnList)) {
        	//根据渠道可选佣金数据，按‘佣金有的所属供应商+本预约单的主险产品’，到‘产品默认供应商’界面查找是否存在对应数据
        	CmnSupplierItemRelation condition = new CmnSupplierItemRelation();
        	condition.setItemId(dto.getItemId());
        	List<CmnChannelCommission> commissionList = cmnChannelCommissionMapper.chooseCommission(channelCommission);
        	for (CmnChannelCommission cmnChannelCommission : commissionList) {
        		condition.setSupplierId(cmnChannelCommission.getSupplierId());
        		List<CmnSupplierItemRelation> list = cmnSupplierItemRelationService.selectByCondition(request, condition , 1, 1);
        		if(CollectionUtils.isNotEmpty(list)) {
        			supplierIdSet.add(list.get(0).getPrdSupplierId());
        		}
        	}
        	//再根据找到的数据去佣金表里面匹配
        	for (Long supplierId : supplierIdSet) {
        		//查询交易路线上包含这类似供应商和渠道的佣金
        		channelCommission.setDealPathChannel("C"+dto.getChannelId());
        		channelCommission.setDealPathSupplier("S"+supplierId);
        		//根据条件去佣金表匹配
        		List<CmnChannelCommission> commissionList1 = cmnChannelCommissionMapper.chooseCommission(channelCommission);
        		Iterator<CmnChannelCommission> iterator = commissionList1.iterator();
        		while(iterator.hasNext()) {
        			String[] dealPathArray = iterator.next().getDealPath().replace(".", " ").split(" ");
        			List<String> dealPathList = Arrays.asList(dealPathArray);
        			if(!dealPathList.contains("S"+supplierId) || !dealPathList.contains("C"+dto.getChannelId())) {
        				iterator.remove();
        			}
        		}
        		cmnList.addAll(commissionList);
			}
        }
        
        if (CollectionUtils.isEmpty(cmnList)) {
            cmnList = cmnChannelCommissionMapper.chooseCommission(channelCommission);
        }
        
        //如果配置文件中配置的"是"   佣金只显示第一年佣金最高的一条
        if("是".equals(flag)) {
        	if(CollectionUtils.isNotEmpty(cmnList)) {
				Collections.sort(cmnList, new Comparator<CmnChannelCommission>() {
					/*  
		             * int compare(Student o1, Student o2) 返回一个基本类型的整型，  
		             * 返回负数表示：o1 小于o2，  
		             * 返回0 表示：o1和o2相等，  
		             * 返回正数表示：o1大于o2。  
		             */ 
					@Override
					public int compare(CmnChannelCommission c1, CmnChannelCommission c2) {
						return c2.getTheFirstYear().compareTo(c1.getTheFirstYear());
					}
				});
				
				//去除经过排序后的佣金最高的一条
				List<CmnChannelCommission> cmnList1 = new ArrayList<>();
				cmnList1.addAll(cmnList);
				cmnList.clear();
				cmnList.add(cmnList1.get(0));
        	}
        }

        if (userService.isDaiBan(request)) {
            for (CmnChannelCommission commission : cmnList) {
                commission.setTheFirstYear(new BigDecimal("-999"));
                commission.setTheSecondYear(new BigDecimal("-999"));
                commission.setTheThirdYear(new BigDecimal("-999"));
            }
        }

        List<CmnChannelCommission> resultList = new ArrayList<CmnChannelCommission>();

        if (CollectionUtils.isNotEmpty(cmnList)) {
            for(CmnChannelCommission cmn : cmnList) {
                List<CmnChannelCommission> addResultList = new ArrayList<CmnChannelCommission>();
                if (dto.getAdditionRiskList() != null) {
                    for (PrdItems item : dto.getAdditionRiskList()) {
                    	//查询附加险佣金关联表
                        CmnAdtRiskRelation cmnAdtRiskRelation = new CmnAdtRiskRelation();
                        //cmnAdtRiskRelation.setItemId(dto.getItemId());
                        cmnAdtRiskRelation.setSubItemId(item.getItemId());
                        List<CmnAdtRiskRelation> cmnAdtRiskRelations = cmnAdtRiskRelationMapper.queryAll(cmnAdtRiskRelation);
                        String dependMainFlag = "N";
                        String subItemName = "";
                        if (CollectionUtils.isNotEmpty(cmnAdtRiskRelations)) {
                        	//先判断当主险产品为空的时候  并且是勾选状态 所有的附加险都是跟随主险的佣金
                        	for (CmnAdtRiskRelation cmnAdtRiskRelation2 : cmnAdtRiskRelations) {
                        		if(cmnAdtRiskRelation2.getItemId() == null) {
									if("Y".equals(cmnAdtRiskRelation2.getDependMainFlag())) {
										dependMainFlag = "Y";
										subItemName = cmnAdtRiskRelation2.getSubItemName();
										break;
									}
								}
							}
                        	//判断当主险产品不为空的时候 附加险只跟随当前主险的佣金  并且不能重复设置
                        	for (CmnAdtRiskRelation cmnAdtRiskRelation2 : cmnAdtRiskRelations) {
								if(cmnAdtRiskRelation2.getItemId() == dto.getItemId()) {
									if("Y".equals(cmnAdtRiskRelation2.getDependMainFlag())) {
										if("Y".equals(dependMainFlag)) {
											throw new RuntimeException("附加险佣金关联设置重复,请联系管理员!");
										}else {
											dependMainFlag = "Y";
											subItemName = cmnAdtRiskRelation2.getSubItemName();
											break;
										}
									}	
								}
							}
                        	/*dependMainFlag = cmnAdtRiskRelations.get(0).getDependMainFlag();
                            subItemName = cmnAdtRiskRelations.get(0).getSubItemName();*/
                        }

                        if ("N".equals(dependMainFlag)) {
                            CmnChannelCommission addCommission = new CmnChannelCommission();
                            addCommission.setChannelId(dto.getChannelId());
                            addCommission.setCurrency(dto.getCurrency());
                            addCommission.setContributionPeriod(item.getSublineItemName());
                            addCommission.setPayMethod(dto.getPayMethod());
                            addCommission.setPolicyholdersAge(policyholdersAge);
                            addCommission.setEffectiveDate(dto.getEffectiveDate());
                            addCommission.setItemId(item.getItemId());
                            addCommission.setDealPath(cmn.getDealPath());
                            addCommission.setSupplierId(cmn.getSupplierId());
                            List<CmnChannelCommission> addList = cmnChannelCommissionMapper.chooseCommission(addCommission);
                            if (addList != null && addList.size() > 0) {
                                if (userService.isDaiBan(request)) {
                                    for (CmnChannelCommission commission : addList) {
                                        commission.setTheFirstYear(new BigDecimal("-999"));
                                        commission.setTheSecondYear(new BigDecimal("-999"));
                                        commission.setTheThirdYear(new BigDecimal("-999"));
                                    }
                                }
                                addResultList.add(addList.get(0));
                            }
                        } else {
                            CmnChannelCommission addCommission = new CmnChannelCommission();

                            addCommission.setLineId(cmn.getLineId());
                            addCommission.setTheFirstYear(cmn.getTheFirstYear());
                            addCommission.setTheSecondYear(cmn.getTheSecondYear());
                            addCommission.setTheThirdYear(cmn.getTheThirdYear());
                            addCommission.setTheFourthYear(cmn.getTheFourthYear());
                            addCommission.setTheFifthYear(cmn.getTheFifthYear());
                            addCommission.setTheSixthYear(cmn.getTheSixthYear());
                            addCommission.setTheSeventhYear(cmn.getTheSeventhYear());
                            addCommission.setTheEightYear(cmn.getTheEightYear());
                            addCommission.setTheNinthYear(cmn.getTheNinthYear());
                            addCommission.setTheTenthYear(cmn.getTheTenthYear()); 
                            addCommission.setDealPath(cmn.getDealPath());
                            addCommission.setDealPathName(cmn.getDealPathName());

                            addCommission.setItemId(item.getItemId());
                            addCommission.setItemName(subItemName);
                            addCommission.setContributionPeriod("");
                            addResultList.add(addCommission);
                        }

                    }

                    if (dto.getAdditionRiskList().size() == addResultList.size()) {
                        resultList.add(cmn);
                        resultList.addAll(addResultList);
                    }
                } else {
                    resultList.add(cmn);
                }
            }
        }

        /*ListSortUtil<CmnChannelCommission> sortList = new ListSortUtil<CmnChannelCommission>();
        sortList.sort(resultList, "theFirstYear", "desc");*/

        return resultList;
    }

    // 处理单个渠道计算佣金值
    public void processSingleChannelCmn(IRequest request, Long channelId, Long jobBatchId) {

        // 判断是否已存在该渠道的佣金数据，存在则直接返回
        CmnChannelCommission paraChannelCommission = new CmnChannelCommission();
        paraChannelCommission.setChannelId(channelId);
        List<CmnChannelCommission> resultList = cmnChannelCommissionMapper.selectAllFields(paraChannelCommission);
        if (resultList != null && resultList.size() > 0) {
            return;
        }

        // 循环该渠道合约信息
        CnlChannelContract paraContract = new CnlChannelContract();
        paraContract.setChannelId(channelId);
        List<CnlChannelContract> contractList = cmnChannelCommissionMapper.queryContractList(paraContract);
        if (contractList != null && contractList.size() > 0) {

            for (CnlChannelContract contract : contractList) {

                // 如果合约主体为供应商，则查询供应商佣金表匹配费率级别
                if ("SUPPLIER".equals(contract.getPartyType())) {

                    // 查询费率级别（只可能有一行）
                    /*CnlContractRate contractRate = new CnlContractRate();
                    contractRate.setChannelContractId(contract.getChannelContractId());
                    List<CnlContractRate> rateList = cmnChannelCommissionMapper.queryContractRateList(contractRate);
                    String levelName = "";
                    if (rateList != null && rateList.size() > 0) {
                        levelName = rateList.get(0).getLevelName();
                    }

                    List<CnlLevel> levelList = null;
                    if (!"".equals(levelName)) {
                        CnlLevel paraLevel = new CnlLevel();
                        paraLevel.setLevelName(levelName);
                        levelList= cmnChannelCommissionMapper.queryLevelList(paraLevel);
                    }*/
                    List<CnlLevel> levelList = null;
                    if (contract.getDefineRateFlag().equals("N") && contract.getRateLevelId() != null) {
                        CnlLevel paraLevel = new CnlLevel();
                        paraLevel.setId(contract.getRateLevelId());
                        levelList = cmnChannelCommissionMapper.queryLevelList(paraLevel);
                    }

                    // 匹配具体费率级别时间
                    if (levelList != null && levelList.size() > 0) {
                        CmnSupplierCommission paraSupplierCmn = new CmnSupplierCommission();
                        paraSupplierCmn.setSupplierId(contract.getPartyId());
                        paraSupplierCmn.setChannelTypeCode(contract.getChannelTypeCode());
                        List<CmnSupplierCommission> supplierCommissionList = cmnSupplierCommissionMapper.
                                selectAllFields(paraSupplierCmn);
                        if (supplierCommissionList != null && supplierCommissionList.size() > 0) {

                            for (CmnSupplierCommission supplierCommission : supplierCommissionList) {

                                for (CnlLevel level : levelList) {

                                    Date contractCmnResultBegin = new Date();
                                    Date contractCmnResultEnd = new Date();
                                    Date resultDateBegin = new Date();
                                    Date resultDateEnd = new Date();

                                    // 合约时间 与  供应商佣金表时间
                                    long lContractBegin = contract.getStartDate().getTime();
                                    long lContractEnd = contract.getEndDate().getTime();
                                    long lCommissionBegin = supplierCommission.getEffectiveStartDate().getTime();
                                    long lCommissionEnd = supplierCommission.getEffectiveEndDate().getTime();

                                    if ((lCommissionBegin >= lContractBegin && lCommissionBegin <= lContractEnd)
                                            || (lCommissionEnd >= lContractBegin && lCommissionEnd <= lContractEnd)) {
                                        // 一定有重叠部分
                                        long resultBegin = lCommissionBegin >= lContractBegin ? lCommissionBegin : lContractBegin;
                                        long resultEnd = lContractEnd >= lCommissionEnd ? lCommissionEnd : lContractEnd;

                                        Date resultBeginDate = new Date(resultBegin);
                                        Date resultEndDate = new Date(resultEnd);
                                        contractCmnResultBegin = resultBeginDate;
                                        contractCmnResultEnd = resultEndDate;

                                    } else if (lCommissionBegin < lContractBegin && lCommissionEnd > lContractEnd) {

                                        Date resultBeginDate = new Date(lContractBegin);
                                        Date resultEndDate = new Date(lContractEnd);
                                        contractCmnResultBegin = resultBeginDate;
                                        contractCmnResultEnd = resultEndDate;
                                    } else {
                                        contractCmnResultBegin = null;
                                        contractCmnResultEnd = null;
                                    }

                                    // 结果   与  费率级别时间
                                    if (contractCmnResultBegin != null && contractCmnResultEnd != null) {

                                        long lContractCmnBegin = contractCmnResultBegin.getTime();
                                        long lContractCmnEnd = contractCmnResultEnd.getTime();
                                        long lRateLevelBegin = level.getEffectiveStartDate().getTime();
                                        long lRateLevelEnd = level.getEffectiveEndDate().getTime();

                                        if ((lContractCmnBegin >= lRateLevelBegin && lContractCmnBegin <= lRateLevelEnd)
                                                || (lContractCmnEnd >= lRateLevelBegin && lContractCmnEnd <= lRateLevelEnd)) {
                                            // 一定有重叠部分
                                            long resultBegin = lContractCmnBegin >= lRateLevelBegin ? lContractCmnBegin : lRateLevelBegin;
                                            long resultEnd = lRateLevelEnd >= lContractCmnEnd ? lContractCmnEnd : lRateLevelEnd;

                                            Date resultBeginDate = new Date(resultBegin);
                                            Date resultEndDate = new Date(resultEnd);
                                            resultDateBegin = resultBeginDate;
                                            resultDateEnd = resultEndDate;

                                        } else if (lContractCmnBegin < lRateLevelBegin && lContractCmnEnd > lRateLevelEnd) {

                                            Date resultBeginDate = new Date(lRateLevelBegin);
                                            Date resultEndDate = new Date(lRateLevelEnd);
                                            resultDateBegin = resultBeginDate;
                                            resultDateEnd = resultEndDate;
                                        } else {
                                            resultDateBegin = null;
                                            resultDateEnd = null;
                                        }

                                        // 计算结果
                                        if (resultDateBegin != null && resultDateEnd != null) {

                                            BigDecimal finalData;
                                            CmnChannelCommission channelCommission = new CmnChannelCommission();
                                            channelCommission.setLineId(null);
                                            channelCommission.setChannelId(contract.getChannelId());
                                            channelCommission.setSupplierId(supplierCommission.getSupplierId());
                                            channelCommission.setSupplierCommissionId(supplierCommission.getLineId());
                                            channelCommission.setChannelTypeCode(supplierCommission.getChannelTypeCode());
                                            channelCommission.setItemId(supplierCommission.getItemId());
                                            channelCommission.setContributionPeriod(supplierCommission.getContributionPeriod());
                                            channelCommission.setCurrency(supplierCommission.getCurrency());
                                            channelCommission.setPayMethod(supplierCommission.getPayMethod());
                                            channelCommission.setPolicyholdersMinAge(supplierCommission.getPolicyholdersMinAge());
                                            channelCommission.setPolicyholdersMaxAge(supplierCommission.getPolicyholdersMaxAge());
                                            channelCommission.setPriceType(supplierCommission.getPriceType());
                                            channelCommission.setEffectiveStartDate(resultDateBegin);
                                            channelCommission.setEffectiveEndDate(resultDateEnd);

                                            finalData = supplierCommission.getTheFirstYear().multiply(level.getTheFirstYear());
                                            channelCommission.setTheFirstYear(finalData);

                                            finalData = supplierCommission.getTheSecondYear().multiply(level.getTheSecondYear());
                                            channelCommission.setTheSecondYear(finalData);

                                            finalData = supplierCommission.getTheThirdYear().multiply(level.getTheThirdTear());
                                            channelCommission.setTheThirdYear(finalData);

                                            finalData = supplierCommission.getTheFourthYear().multiply(level.getTheForthYear());
                                            channelCommission.setTheFourthYear(finalData);

                                            finalData = supplierCommission.getTheFifthYear().multiply(level.getTheFifthYear());
                                            channelCommission.setTheFifthYear(finalData);

                                            finalData = supplierCommission.getTheSixthYear().multiply(level.getTheSixthYear());
                                            channelCommission.setTheSixthYear(finalData);

                                            finalData = supplierCommission.getTheSeventhYear().multiply(level.getTheSeventhYear());
                                            channelCommission.setTheSeventhYear(finalData);

                                            finalData = supplierCommission.getTheEightYear().multiply(level.getTheEightYear());
                                            channelCommission.setTheEightYear(finalData);

                                            finalData = supplierCommission.getTheNinthYear().multiply(level.getTheNinthYear());
                                            channelCommission.setTheNinthYear(finalData);

                                            finalData = supplierCommission.getTheTenthYear().multiply(level.getTheTenthYear());
                                            channelCommission.setTheTenthYear(finalData);

                                            // who字段
                                            channelCommission.setCalculateFlag("N");
                                            channelCommission.setObjectVersionNumber(1L);
                                            channelCommission.setVersion("1");
                                            channelCommission.setBatchId(jobBatchId);

                                            cmnChannelCommissionMapper.insertSelective(channelCommission);
                                        }

                                    }
                                }

                            }

                        }
                    }

                } else if ("CHANNEL".equals(contract.getPartyType())) {
                    // 如果合约主体为渠道，则查询已有渠道佣金表数据匹配通用佣金分成或者特殊费率
                    CmnChannelCommission paraChannelCmn = new CmnChannelCommission();
                    paraChannelCmn.setChannelId(contract.getPartyId());
                    List<CmnChannelCommission> channelCommissionList = cmnChannelCommissionMapper.
                            selectAllFields(paraChannelCmn);
                    if(channelCommissionList != null && channelCommissionList.size() > 0) {
                        for (CmnChannelCommission existCmn : channelCommissionList) {

                            Date contractCmnResultBegin = new Date();
                            Date contractCmnResultEnd = new Date();
                            // 合约时间 与  渠道佣金表时间
                            long lContractBegin = contract.getStartDate().getTime();
                            long lContractEnd = contract.getEndDate().getTime();
                            long lCommissionBegin = existCmn.getEffectiveStartDate().getTime();
                            long lCommissionEnd = existCmn.getEffectiveEndDate().getTime();

                            if ((lCommissionBegin >= lContractBegin && lCommissionBegin <= lContractEnd)
                                    || (lCommissionEnd >= lContractBegin && lCommissionEnd <= lContractEnd)) {
                                // 一定有重叠部分
                                long resultBegin = lCommissionBegin >= lContractBegin ? lCommissionBegin : lContractBegin;
                                long resultEnd = lContractEnd >= lCommissionEnd ? lCommissionEnd : lContractEnd;

                                Date resultBeginDate = new Date(resultBegin);
                                Date resultEndDate = new Date(resultEnd);
                                contractCmnResultBegin = resultBeginDate;
                                contractCmnResultEnd = resultEndDate;

                            } else if (lCommissionBegin < lContractBegin && lCommissionEnd > lContractEnd) {

                                Date resultBeginDate = new Date(lContractBegin);
                                Date resultEndDate = new Date(lContractEnd);
                                contractCmnResultBegin = resultBeginDate;
                                contractCmnResultEnd = resultEndDate;
                            } else {
                                contractCmnResultBegin = null;
                                contractCmnResultEnd = null;
                            }

                            // 有交叉重合的时间
                            if (contractCmnResultBegin != null && contractCmnResultEnd != null) {

                                // 查询佣金分成比例
                                List<CnlContractRate> rateList = new ArrayList<CnlContractRate>();
                                CnlContractRate paraRate = new CnlContractRate();
                                if (contract.getDefineRateFlag().equals("N") && contract.getRateLevelId() != null) {
                                    paraRate.setRateLevelId(contract.getRateLevelId());
                                    rateList = cnlContractRateMapper.queryChannelRate(paraRate);
                                } else {
                                    paraRate.setChannelContractId(contract.getChannelContractId());
                                    // 特殊条件
                                    paraRate.setBigClass(existCmn.getBigClass());
                                    paraRate.setMidClass(existCmn.getMidClass());
                                    paraRate.setMinClass(existCmn.getMinClass());
                                    paraRate.setItemId(existCmn.getItemId());
                                    paraRate.setSublineItemName(existCmn.getContributionPeriod());
                                    rateList = cmnChannelCommissionMapper.queryTopRateList(paraRate);
                                }
                                if (rateList != null && rateList.size() > 0) {

                                    for (CnlContractRate resultRate : rateList) {

                                        BigDecimal finalData;
                                        CmnChannelCommission resultCommission = new CmnChannelCommission();
                                        resultCommission.setLineId(null);
                                        resultCommission.setSupplierId(existCmn.getSupplierId());
                                        resultCommission.setParentLineId(existCmn.getLineId());
                                        resultCommission.setChannelId(contract.getChannelId());
                                        resultCommission.setChannelTypeCode(existCmn.getChannelTypeCode());
                                        resultCommission.setItemId(existCmn.getItemId());
                                        resultCommission.setContributionPeriod(existCmn.getContributionPeriod());
                                        resultCommission.setCurrency(existCmn.getCurrency());
                                        resultCommission.setPayMethod(existCmn.getPayMethod());
                                        resultCommission.setPolicyholdersMinAge(existCmn.getPolicyholdersMinAge());
                                        resultCommission.setPolicyholdersMaxAge(existCmn.getPolicyholdersMaxAge());
                                        resultCommission.setPriceType(existCmn.getPriceType());
                                        resultCommission.setEffectiveStartDate(contractCmnResultBegin);
                                        resultCommission.setEffectiveEndDate(contractCmnResultEnd);

                                        finalData = existCmn.getTheFirstYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate1()));
                                        resultCommission.setTheFirstYear(finalData);

                                        finalData = existCmn.getTheSecondYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate2()));
                                        resultCommission.setTheSecondYear(finalData);

                                        finalData = existCmn.getTheThirdYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate3()));
                                        resultCommission.setTheThirdYear(finalData);

                                        finalData = existCmn.getTheFourthYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate4()));
                                        resultCommission.setTheFourthYear(finalData);

                                        finalData = existCmn.getTheFifthYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate5()));
                                        resultCommission.setTheFifthYear(finalData);

                                        finalData = existCmn.getTheSixthYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate6()));
                                        resultCommission.setTheSixthYear(finalData);

                                        finalData = existCmn.getTheSeventhYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate7()));
                                        resultCommission.setTheSeventhYear(finalData);

                                        finalData = existCmn.getTheEightYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate8()));
                                        resultCommission.setTheEightYear(finalData);

                                        finalData = existCmn.getTheNinthYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate9()));
                                        resultCommission.setTheNinthYear(finalData);

                                        finalData = existCmn.getTheTenthYear().multiply(CommonUtil.getZeroIfNull(
                                                resultRate.getRate10()));
                                        resultCommission.setTheTenthYear(finalData);

                                        // who字段
                                        resultCommission.setCalculateFlag("N");
                                        resultCommission.setObjectVersionNumber(1L);
                                        resultCommission.setVersion("1");
                                        resultCommission.setBatchId(jobBatchId);
                                        cmnChannelCommissionMapper.insertSelective(resultCommission);
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }

        // 交易路线计算，channelId作为批数据标志
        List<CmnChannelCommission> channelCommissionList = new ArrayList<CmnChannelCommission>();
        CmnChannelCommission paraCommission = new CmnChannelCommission();
        paraCommission.setChannelId(channelId);
        channelCommissionList = cmnChannelCommissionMapper.selectAllFields(paraCommission);
        if (channelCommissionList != null && channelCommissionList.size() > 0) {

            for (CmnChannelCommission channelCommission : channelCommissionList) {

                CmnTradeRoute tradeRoute = new CmnTradeRoute();
                tradeRoute.setChannelCommissionLineId(channelCommission.getLineId());
                tradeRoute.setCompanyCommissionLineId(channelCommission.getLineId());
                tradeRoute.setSeqNum(1L);
                tradeRoute.setCompanyType("CHANNEL");
                tradeRoute.setCompanyId(channelCommission.getChannelId());
                tradeRoute.setChildRouteId(null);

                // who字段
                tradeRoute.setObjectVersionNumber(1L);
                tradeRoute.setBatchId(jobBatchId);
                routeMapper.insertSelective(tradeRoute);

                String dealPath = "C" + channelCommission.getChannelId();
                String dealPathName = "C" + channelCommission.getChannelName();

                if (channelCommission.getParentLineId() != null) {
                    // 非顶级渠道 继续找渠道佣金表数据
                    processRoute(channelCommission.getLineId(),
                            2L,
                            tradeRoute.getRouteId(),
                            channelCommission.getParentLineId(),
                            null,
                            dealPath,
                            dealPathName,
                            jobBatchId);

                } else {
                    // 顶级渠道 需关联供应商佣金表
                    Long supplierCommissionId = channelCommission.getSupplierCommissionId();

                    // 查询对应的供应商佣金表数据SpeCommissionNum
                    CmnSupplierCommission speCommission = new CmnSupplierCommission();
                    speCommission.setLineId(supplierCommissionId);
                    if (cmnSupplierCommissionMapper.selectByPrimaryKey(speCommission) != null) {
                        speCommission = cmnSupplierCommissionMapper.selectByPrimaryKey(speCommission);

                        processRoute(channelCommission.getLineId(),
                                2L,
                                tradeRoute.getRouteId(),
                                null,
                                speCommission.getCommissionNum(),
                                dealPath,
                                dealPathName,
                                jobBatchId);
                    }

                }

                CmnTradeRoute paraTradeRoute = new CmnTradeRoute();
                paraTradeRoute.setChannelCommissionLineId(channelCommission.getLineId());
                List<CmnTradeRoute> dealPathList = routeMapper.queryDealPath(paraTradeRoute);
                if (dealPathList != null && dealPathList.size() > 0) {
                    paraTradeRoute = dealPathList.get(0);
                    routeMapper.updateDealPath(paraTradeRoute);
                    routeMapper.updateCommissionDealPath(paraTradeRoute);
                }
            }
        }

        // 转移数据，从tmp表到show表
        /*commissionShowMapper.deleteCmnData();
        commissionShowMapper.insertShowCmnData();
        routeShowMapper.deleteRouteData();
        routeShowMapper.insertShowRouteData();*/
        commissionShowMapper.transferShowCmnData(jobBatchId);
        routeShowMapper.transferShowRouteData(jobBatchId);

    }

    public void processRoute(Long commissionLineId,
                             Long seqNum,
                             Long childRouteId,
                             Long parentCommissionLineId,
                             String parentSpeCommissionNum,
                             String dealPath,
                             String dealPathName,
                             Long jobBatchId) {

        IRequest request = RequestHelper.newEmptyRequest();

        if (parentCommissionLineId != null) {
            CmnChannelCommission channelCommission = new CmnChannelCommission();
            channelCommission.setLineId(parentCommissionLineId);
            List<CmnChannelCommission> channelCommissionList = cmnChannelCommissionMapper.selectCommissionById(channelCommission);
            if (channelCommissionList != null && channelCommissionList.size() > 0) {
                channelCommission = channelCommissionList.get(0);

                CmnTradeRoute tradeRoute = new CmnTradeRoute();
                tradeRoute.setChannelCommissionLineId(commissionLineId);
                tradeRoute.setCompanyCommissionLineId(channelCommission.getLineId());
                tradeRoute.setSeqNum(seqNum);
                tradeRoute.setCompanyType("CHANNEL");
                tradeRoute.setCompanyId(channelCommission.getChannelId());
                tradeRoute.setChildRouteId(childRouteId);

                // who字段
                tradeRoute.setObjectVersionNumber(1L);
                tradeRoute.setBatchId(jobBatchId);
                routeMapper.insertSelective(tradeRoute);

                dealPath = dealPath + ".C" + channelCommission.getChannelId();
                dealPathName = dealPathName + ".C" + channelCommission.getChannelName();

                if (channelCommission.getParentLineId() != null) {

                    seqNum = seqNum + 1;
                    // 调用自己
                    processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                            channelCommission.getParentLineId(), null,
                            dealPath,
                            dealPathName, jobBatchId);
                } else {
                    // 顶级渠道 需关联供应商佣金表
                    Long supplierCommissionId = channelCommission.getSupplierCommissionId();

                    // 查询对应的供应商佣金表数据SpeCommissionNum
                    CmnSupplierCommission speCommission = new CmnSupplierCommission();
                    speCommission.setLineId(supplierCommissionId);
                    if (cmnSupplierCommissionMapper.selectByPrimaryKey(speCommission) != null) {
                        speCommission = cmnSupplierCommissionMapper.selectByPrimaryKey(speCommission);

                        seqNum = seqNum + 1;
                        processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                                null, speCommission.getCommissionNum(),
                                dealPath,
                                dealPathName, jobBatchId);
                    }
                }
            }

        }

        // 供应商佣金表情况
        if (parentSpeCommissionNum != null
                && !"".equals(parentSpeCommissionNum)) {
            CmnSupplierCommission speCommission = new CmnSupplierCommission();
            speCommission.setCommissionNum(parentSpeCommissionNum);
            List<CmnSupplierCommission> speCommissionList = cmnSupplierCommissionMapper.selectByCommissionNum(speCommission);
            if (speCommissionList != null && speCommissionList.size() > 0) {
                speCommission = speCommissionList.get(0);

                if (speCommission.getParentCommissionNum() != null
                        && !"".equals(speCommission.getParentCommissionNum())) {

                    CmnTradeRoute tradeRoute = new CmnTradeRoute();
                    tradeRoute.setChannelCommissionLineId(commissionLineId);

                    // 取本级获取的佣金数据（即上级放出的佣金值）
                    if (speCommissionList.get(0).getParentCommissionNum() != null
                            && !"".equals(speCommissionList.get(0).getParentCommissionNum())) {
                        CmnSupplierCommission paraCommission = new CmnSupplierCommission();
                        paraCommission.setCommissionNum(speCommissionList.get(0).getParentCommissionNum());
                        List<CmnSupplierCommission> incomeCommissionList = cmnSupplierCommissionMapper.selectByCommissionNum(paraCommission);
                        if (incomeCommissionList != null && incomeCommissionList.size() > 0) {
                            tradeRoute.setCompanyCommissionLineId(incomeCommissionList.get(0).getLineId());
                        }
                    } else {
                        Long ytId = cmnSupplierCommissionMapper.selectYtCmnId(speCommissionList.get(0).getLineId());
                        tradeRoute.setCompanyCommissionLineId(ytId);
                    }

                    tradeRoute.setSeqNum(seqNum);
                    tradeRoute.setCompanyType("SUPPLIER");
                    tradeRoute.setCompanyId(speCommission.getSupplierId());
                    tradeRoute.setChildRouteId(childRouteId);
                    // who字段
                    tradeRoute.setObjectVersionNumber(1L);
                    tradeRoute.setBatchId(jobBatchId);
                    routeMapper.insertSelective(tradeRoute);

                    dealPath = dealPath + ".S" + speCommission.getSupplierId();
                    dealPathName = dealPathName + ".S" + speCommission.getSupplierName();

                    seqNum = seqNum + 1;
                    processRoute(commissionLineId, seqNum, tradeRoute.getRouteId(),
                            null, speCommission.getParentCommissionNum(),
                            dealPath,
                            dealPathName, jobBatchId);

                } else {
                    // 顶级供应商
                    CmnSupplierCommission topCommission = new CmnSupplierCommission();
                    topCommission.setLineId(speCommission.getLineId());
                    topCommission = cmnSupplierCommissionMapper.selectAllFields(topCommission).get(0);

                    CmnTradeRoute tradeRoute = new CmnTradeRoute();
                    tradeRoute.setChannelCommissionLineId(commissionLineId);

                    // 通过条件取源头佣金值
                    Long ytId = cmnSupplierCommissionMapper.selectYtCmnId(topCommission.getLineId());
                    tradeRoute.setCompanyCommissionLineId(ytId);
                    //tradeRoute.setCompanyCommissionLineId(topCommission.getLineId());
                    tradeRoute.setSeqNum(seqNum);
                    tradeRoute.setCompanyType("SUPPLIER");
                    tradeRoute.setCompanyId(topCommission.getSupplierId());
                    tradeRoute.setChildRouteId(childRouteId);
                    // who字段
                    tradeRoute.setObjectVersionNumber(1L);
                    tradeRoute.setBatchId(jobBatchId);
                    routeMapper.insertSelective(tradeRoute);

                    dealPath = dealPath + ".S" + topCommission.getSupplierId();
                    dealPathName = dealPathName + ".S" + topCommission.getSupplierName();

                    seqNum = seqNum + 1;

                    CmnTradeRoute topRoute = new CmnTradeRoute();
                    topRoute.setChannelCommissionLineId(commissionLineId);
                    topRoute.setCompanyCommissionLineId(null);
                    topRoute.setSeqNum(seqNum);
                    topRoute.setCompanyType("SUPPLIER");
                    topRoute.setCompanyId(topCommission.getItemSupplierId());
                    topRoute.setChildRouteId(tradeRoute.getRouteId());
                    // who字段
                    topRoute.setObjectVersionNumber(1L);

                    dealPath = dealPath + ".S" + topCommission.getItemSupplierId();
                    dealPathName = dealPathName + ".S" + topCommission.getItemSupplierName();
                    topRoute.setDealPath(dealPath);
                    topRoute.setDealPathName(dealPathName);
                    topRoute.setBatchId(jobBatchId);
                    routeMapper.insertSelective(topRoute);
                }
            }

        }

    }

	@Override
	public List<CmnChannelCommission> queryReferralFee(IRequest requestContext, CmnChannelCommission dto, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return cmnChannelCommissionMapper.queryReferralFee(dto);
	}

}