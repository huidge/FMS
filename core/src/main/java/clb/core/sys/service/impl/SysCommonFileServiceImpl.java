package clb.core.sys.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IProfileService;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.utils.GetFoldFileNames;
import clb.core.pln.dto.PlnPlanLibrary;
import clb.core.pln.service.IPlnPlanLibraryService;
import clb.core.production.service.IPrdItemSelfpayService;
import clb.core.production.service.IPrdItemSublineService;
import clb.core.production.service.IPrdItemsService;
import clb.core.supplier.dto.SpeSupplier;
import clb.core.supplier.service.ISpeSupplierService;
import clb.core.sys.dto.SysCommonFile;
import clb.core.sys.mapper.SysCommonFileMapper;
import clb.core.sys.service.ISysCommonFileService;

@Service
@Transactional
public class SysCommonFileServiceImpl extends BaseServiceImpl<SysCommonFile> implements ISysCommonFileService{

	@Autowired
	private SysCommonFileMapper sysCommonFileMapper;
	
	@Autowired
	private IProfileService profileService;
	
	@Autowired
	private IPrdItemSelfpayService prdItemSelfpayService;
	
	@Autowired
	private IPlnPlanLibraryService plnPlanLibraryService;
	
	@Autowired
    private IPrdItemsService prdItemsService;
	
	@Autowired
	private IPrdItemSublineService prdItemSublineService;
	
	@Autowired
	private ISpeSupplierService speSupplierService;
	 
	@Override
	public List<SysCommonFile> selectSysCommonFileInfo(IRequest requestContext, SysCommonFile sysCommonFile, int page,
			int pageSize) {
		PageHelper.startPage(page, pageSize);
		return sysCommonFileMapper.selectSysCommonFileInfo(sysCommonFile);
	}

	@Override
	public void importSysFile(IRequest requestCtx, SysCommonFile dto) throws Exception{
		List<Map<String,String>> sysFile = new ArrayList<Map<String,String>>();
		List<SysCommonFile> sysCommonFileList = new ArrayList<SysCommonFile>();
		sysFile = GetFoldFileNames.getFileInfo(profileService.getProfileValue(requestCtx, "SYS_COMMON_FILE"));
		 for (Map<String, String> map : sysFile) {
				//需要插入的单个资料库
			    SysCommonFile sysCommonFile = new SysCommonFile();
				for (Map.Entry<String,String> mapInfo : map.entrySet()) {
					//产品公司名称
					if(mapInfo.getKey().equals("supplierName")){
						SpeSupplier speSupplier = new SpeSupplier();
						speSupplier.setName(mapInfo.getValue());
						if(speSupplierService.selectByName(speSupplier) != null){
							sysCommonFile.setSupplierId(speSupplierService.selectByName(speSupplier).get(0).getSupplierId());
						}
						else{
							throw new Exception("产品公司不存在！");
						}
					}
					//资料类型
					if(mapInfo.getKey().equals("datumType")){
						if(mapInfo.getValue().equals("服务指引")){
							sysCommonFile.setDatumType("FWZY");
						}
						if(mapInfo.getValue().equals("服务表格")){
							sysCommonFile.setDatumType("FWBG");
						}
						if(mapInfo.getValue().equals("表单下载")){
							sysCommonFile.setDatumType("BDXZ");
						}
					}
					//用途
	                if(mapInfo.getKey().equals("userType")){
	                	if(mapInfo.getValue().equals("保单变更")){
							sysCommonFile.setUseType("BDBG");
						}
	                	if(mapInfo.getValue().equals("支付")){
	                		sysCommonFile.setUseType("ZF");
	                	}
						if(mapInfo.getValue().equals("理赔")){
							sysCommonFile.setUseType("LP");
						}
						if(mapInfo.getValue().equals("其它")){
							sysCommonFile.setUseType("QT");
						}
						if(mapInfo.getValue().equals("核保")){
							sysCommonFile.setUseType("HB");
						}
					}
	                //文件名
	                if(mapInfo.getKey().equals("content")){
	                	sysCommonFile.setContent(mapInfo.getValue());
					}
				}
				sysCommonFileList.add(sysCommonFile);
			}
	        
	        for (SysCommonFile sysCommonFile : sysCommonFileList) {
	        	sysCommonFile.set__status("add");
			}
	        
	        self().batchUpdate(requestCtx, sysCommonFileList);
	}

	@Override
	public ResponseData insertFileInfo(IRequest requestContext, String fileName, String type) throws Exception{
		ResponseData responseData = new ResponseData();
		//通用资料库的导入程序
		if(type.equals("SYS_FILE")){
			 String[] fieldList = fileName.split("-");
			 SysCommonFile sysCommonFile = new SysCommonFile();
			 for(int j=0; j<fieldList.length;j++){
             	if(j==0){
             	    SpeSupplier speSupplier = new SpeSupplier();
					speSupplier.setName(fieldList[0]);
					if(speSupplierService.selectByName(speSupplier) != null && speSupplierService.selectByName(speSupplier).size()>=1){
						sysCommonFile.setSupplierId(speSupplierService.selectByName(speSupplier).get(0).getSupplierId());
					}
					else{
						throw new Exception("产品公司不存在！");
					}
             	}
             	if(j==1){
             		if(fieldList[1].equals("服务指引")){
						sysCommonFile.setDatumType("FWZY");
					}
					if(fieldList[1].equals("服务表格")){
						sysCommonFile.setDatumType("FWBG");
					}
					if(fieldList[1].equals("表单下载")){
						sysCommonFile.setDatumType("BDXZ");
					}
              	}
             	if(j==2){
             		if(fieldList[2].equals("保单变更")){
						sysCommonFile.setUseType("BDBG");
					}
                	if(fieldList[2].equals("支付")){
                		sysCommonFile.setUseType("ZF");
                	}
					if(fieldList[2].equals("理赔")){
						sysCommonFile.setUseType("LP");
					}
					if(fieldList[2].equals("其它")){
						sysCommonFile.setUseType("QT");
					}
					if(fieldList[2].equals("核保")){
						sysCommonFile.setUseType("HB");
					}
              	}
             	if(j==3){
             		sysCommonFile.setContent(fieldList[3]);
             	}
             	if(j==4){
             		sysCommonFile.setFileId(Long.valueOf(fieldList[4]));
             	}
             }
			 sysCommonFile.setObjectVersionNumber(1L);
			 sysCommonFile.setCreationDate(new Date());
			 sysCommonFile.setCreatedBy(-1L);
			 sysCommonFile.setLastUpdateDate(new Date());
			 sysCommonFile.setDownloadTimes(0L);
			 sysCommonFile.setLastUpdatedBy(-1L);
			 sysCommonFileMapper.insert(sysCommonFile); 
		}
		
		//计划书库类型一：产品名+年期+性别+年龄+吸烟+缴费方式+保费
	    if(type.equals("LIBRARY_FILE_TYPE_ONE")){
	    	 String[] fieldList = fileName.split("-");
	    	 PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
	    	 for(int j=0; j<fieldList.length;j++){
	    	    if(j==0){
				   Long itemId = prdItemsService.selectItemIdByItemName(fieldList[j]);
				   if(itemId == null){
					   throw new Exception("该产品不存在！");
				   }
				   plnPlanLibrary.setItemId(itemId);
			    }
	    	    if(j==1){
					Long sublineId = prdItemSublineService.selectByCondition(fieldList[j], plnPlanLibrary.getItemId());
					if(sublineId == null){
						throw new Exception("该产品下不存在该年期！");
					}
					plnPlanLibrary.setSublineId(sublineId);
	            }
	    	    if(j==2){
	    	    	plnPlanLibrary.setGender(fieldList[j]);
	           	}
	    	    if(j==3){
	    	    	plnPlanLibrary.setAge(Long.valueOf(fieldList[j]));
	            }
	    	    if(j==4){
	    	    	plnPlanLibrary.setSmokeFlag(fieldList[j]);
	            }
	          	if(j==5){
	          		if("年缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("AP");
            		}
                	if("月缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("MP");
            		}
                	if("季缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("QP");
            		}
                	if("半年缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("SAP");
            		}
                	if("预缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("FJ");
            		}
                	if("趸缴".equals(fieldList[j])){
                		plnPlanLibrary.setPayMethod("WP");
            		}
	            }
	          	if(j==6){   	   
	          		plnPlanLibrary.setPremium(new BigDecimal(fieldList[j]));
	            }
	          	if(j==7){   	   
	          		plnPlanLibrary.setFileId(Long.valueOf(fieldList[j]));
	            }
          	 }
	    	plnPlanLibrary.setCurrency("USD");
			plnPlanLibrary.setNationality("China");
			plnPlanLibrary.setResidence("China");
			plnPlanLibrary.setObjectVersionNumber(1L);
			plnPlanLibrary.setCreationDate(new Date());
			plnPlanLibrary.setCreatedBy(-1L);
			plnPlanLibrary.setLastUpdateDate(new Date());
			plnPlanLibrary.setLastUpdatedBy(-1L);
	    	plnPlanLibraryService.insert(requestContext, plnPlanLibrary);
	      }
	    
	    //
	    if(type.equals("LIBRARY_FILE_TYPE_TWO")){
	    	 String[] fieldList = fileName.split("-");
	    	 PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
	    	 for(int j=0; j<fieldList.length;j++){
	    	    if(j==0){
				   Long itemId = prdItemsService.selectItemIdByItemName(fieldList[j]);
				   if(itemId == null){
					   throw new Exception("该产品不存在！");
				   }
				   plnPlanLibrary.setItemId(itemId);
			    }
	    	    if(j==1){
					Long sublineId = prdItemSublineService.selectByCondition(fieldList[j], plnPlanLibrary.getItemId());
					if(sublineId == null){
						throw new Exception("该产品下不存在该年期！");
					}
					plnPlanLibrary.setSublineId(sublineId);
	            }
	    	    if(j==2){
	    	    	plnPlanLibrary.setGender(fieldList[j]);
	           	}
	    	    if(j==3){
	    	    	plnPlanLibrary.setAge(Long.valueOf(fieldList[j]));
	            }
	    	    if(j==4){
	    	    	plnPlanLibrary.setSmokeFlag(fieldList[j]);
	            }
	    	    if(j==5){
	    	    	plnPlanLibrary.setAmount(new BigDecimal(fieldList[j]));
	            }
	          	if(j==6){
	          		if("年缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("AP");
           		}
               	if("月缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("MP");
           		}
               	if("季缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("QP");
           		}
               	if("半年缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("SAP");
           		}
               	if("预缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("FJ");
           		}
               	if("趸缴".equals(fieldList[j])){
               		plnPlanLibrary.setPayMethod("WP");
           		}
	            }
	          	if(j==7){   	   
	          		plnPlanLibrary.setPremium(new BigDecimal(fieldList[j]));
	            }
	          	if(j==8){   	   
	          		plnPlanLibrary.setFileId(Long.valueOf(fieldList[j]));
	            }
         	 }
	    	plnPlanLibrary.setCurrency("USD");
			plnPlanLibrary.setNationality("China");
			plnPlanLibrary.setResidence("China");
			plnPlanLibrary.setObjectVersionNumber(1L);
			plnPlanLibrary.setCreationDate(new Date());
			plnPlanLibrary.setCreatedBy(-1L);
			plnPlanLibrary.setLastUpdateDate(new Date());
			plnPlanLibrary.setLastUpdatedBy(-1L);
	    	plnPlanLibraryService.insert(requestContext, plnPlanLibrary);
	      }
	    
	    //产品名+年期+保障区域+自付选项+性别+年龄+缴费方式+保费
	    if(type.equals("LIBRARY_FILE_TYPE_THREE")){
	    	 String[] fieldList = fileName.split("-");
	    	 PlnPlanLibrary plnPlanLibrary = new PlnPlanLibrary();
	    	 for(int j=0; j<fieldList.length;j++){
	    	    if(j==0){
				   Long itemId = prdItemsService.selectItemIdByItemName(fieldList[j]);
				   if(itemId == null){
					   throw new Exception("该产品不存在！");
				   }
				   plnPlanLibrary.setItemId(itemId);
			    }
	    	    if(j==1){
					Long sublineId = prdItemSublineService.selectByCondition(fieldList[j], plnPlanLibrary.getItemId());
					if(sublineId == null){
						throw new Exception("该产品下不存在该年期！");
					}
					plnPlanLibrary.setSublineId(sublineId);
	            }
	    	    if(j==2){
	    	    	plnPlanLibrary.setSecurityArea(fieldList[j]);
	           	}
	    	    if(j==3){
	    	    	if(fieldList[j].equals("0")){
	    	    		Long selfpayId =  prdItemSelfpayService.querySelfpayId("0港币/澳门币/美元",plnPlanLibrary.getItemId());
	    	    		if(selfpayId == null){
							throw new Exception("该产品下自付选项不存在！");
						}
	    	    		plnPlanLibrary.setSelfpayId(selfpayId);
	    	    	}
                    if(fieldList[j].equals("2000")){
                    	Long selfpayId =  prdItemSelfpayService.querySelfpayId("16,000港币/澳门币/2,000美元",plnPlanLibrary.getItemId());
                    	if(selfpayId == null){
							throw new Exception("该产品下自付选项不存在！");
						}
                    	plnPlanLibrary.setSelfpayId(selfpayId);
	    	    	}
                    if(fieldList[j].equals("3125")){
                    	Long selfpayId =  prdItemSelfpayService.querySelfpayId("25,000港币/澳门币/3,125美元",plnPlanLibrary.getItemId());
                    	if(selfpayId == null){
							throw new Exception("该产品下自付选项不存在！");
						}
                    	plnPlanLibrary.setSelfpayId(selfpayId);
	    	    	}
	           	}
	    	    if(j==4){
	    	    	plnPlanLibrary.setGender(fieldList[j]);
	           	}
	    	    if(j==5){
	    	    	plnPlanLibrary.setAge(Long.valueOf(fieldList[j]));
	            }
	          	if(j==6){
	          		if("年缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("AP");
          		}
              	if("月缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("MP");
          		}
              	if("季缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("QP");
          		}
              	if("半年缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("SAP");
          		}
              	if("预缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("FJ");
          		}
              	if("趸缴".equals(fieldList[j])){
              		plnPlanLibrary.setPayMethod("WP");
          		}
	            }
	          	if(j==7){   	   
	          		plnPlanLibrary.setPremium(new BigDecimal(fieldList[j]));
	            }
	          	if(j==8){   	   
	          		plnPlanLibrary.setFileId(Long.valueOf(fieldList[j]));
	            }
        	 }
	    	plnPlanLibrary.setCurrency("USD");
			plnPlanLibrary.setNationality("China");
			plnPlanLibrary.setResidence("China");
			plnPlanLibrary.setObjectVersionNumber(1L);
			plnPlanLibrary.setCreationDate(new Date());
			plnPlanLibrary.setCreatedBy(-1L);
			plnPlanLibrary.setLastUpdateDate(new Date());
			plnPlanLibrary.setLastUpdatedBy(-1L);
			plnPlanLibrary.setSmokeFlag("N");
	    	plnPlanLibraryService.insert(requestContext, plnPlanLibrary);
	      }
		
		return responseData;
	}

}