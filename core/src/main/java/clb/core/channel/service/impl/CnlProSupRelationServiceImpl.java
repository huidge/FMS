package clb.core.channel.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.channel.dto.CnlProSupRelation;
import clb.core.channel.mapper.CnlProSupRelationMapper;
import clb.core.channel.service.ICnlProSupRelationService;


@Service
@Transactional
public class CnlProSupRelationServiceImpl extends BaseServiceImpl<CnlProSupRelation> implements ICnlProSupRelationService{

	@Autowired
	private CnlProSupRelationMapper cnlProSupRelationMapper;
	
	@Override
	public List<CnlProSupRelation> selectByCondition(IRequest requestContext, CnlProSupRelation cnlProSupRelation,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		return cnlProSupRelationMapper.selectByCondition(cnlProSupRelation);
	}

	@Override
	public void insertAllValue(Workbook wb, IRequest iRequest) throws Exception {
		    
		    //获取第一个sheet页
		    Sheet sheet = wb.getSheetAt(0);
		    
		    //获取sheet页的第一行数
	        Row nRow = sheet.getRow(0);
	        
	        //获取总行数
	        int endRowNo = sheet.getLastRowNum();
	        
	        //获取一行的列数
	        int colNum = nRow.getPhysicalNumberOfCells();
	        
	        List<CnlProSupRelation> cnlProSupRelationList =   new ArrayList<CnlProSupRelation>();
 	        
	        for(int i=1; i<=endRowNo; i++){
	        	
	        	CnlProSupRelation cnlProSupRelation = new CnlProSupRelation();
	        	Row row = sheet.getRow(i);
	             int j = 0;
	             while (j < colNum) {
	                 // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
	                 // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
	                 // str += getStringCellValue(row.getCell((short) j)).trim() +
	                 // "-";
					switch (j) {
					case 0:
						cnlProSupRelation.setSupplierId(cnlProSupRelationMapper.selectSupplierId(this.getCellFormatValue(row.getCell(j))));
						break;
					case 1:
						cnlProSupRelation.setBigClass(this.getCellFormatValue(row.getCell(j)));
						break;
					case 2:
						cnlProSupRelation.setMidClass(this.getCellFormatValue(row.getCell(j)));
						break;
					case 3:
						cnlProSupRelation.setMinClass(this.getCellFormatValue(row.getCell(j)));
						break;
					case 4:
						cnlProSupRelation.setProductId(cnlProSupRelationMapper.selectSupplierId(this.getCellFormatValue(row.getCell(j))));
						break;
					case 5:
						cnlProSupRelation.setProductId(cnlProSupRelationMapper.selectProductId(this.getCellFormatValue(row.getCell(j))));
						break;
					case 6:
						cnlProSupRelation.setContributionPeriod(this.getCellFormatValue(row.getCell(j)));
						break;
					case 7:
						cnlProSupRelation.setChannelClassCode(this.getCellFormatValue(row.getCell(j)));
						break;
					case 8:
						if(cnlProSupRelationMapper.selectChannelId(this.getCellFormatValue(row.getCell(j))) == null || "".equals(cnlProSupRelationMapper.selectChannelId(this.getCellFormatValue(row.getCell(j))))){
							throw new Exception("不存在该渠道名称：" + this.getCellFormatValue(row.getCell(j)));
						}else{
							cnlProSupRelation.setChannelId(cnlProSupRelationMapper.selectChannelId(this.getCellFormatValue(row.getCell(j))));
						}
						break;
					}	 
	                j++;
	             }
	             
	             cnlProSupRelationList.add(cnlProSupRelation);
	        }
	        
	        //循环批量插入数据
	        for (CnlProSupRelation cnlProSupRelation : cnlProSupRelationList) {
	        	cnlProSupRelationMapper.insertFmsCnlProSupRelation(cnlProSupRelation);
			}
	}
	
	
	 /**
     * 根据HSSFCell类型设置数据
     * @param cell
     * @return
     */
    private String getCellFormatValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_FORMULA: {
                // 判断当前的cell是否为Date
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // 如果是Date类型则，转化为Data格式
                    
                    //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                    //cellvalue = cell.getDateCellValue().toLocaleString();
                    
                    //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellvalue = sdf.format(date);
                    
                }
                // 如果是纯数字
                else {
                    // 取得当前Cell的数值
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }

	@Override
	public int selectByAllInfo(CnlProSupRelation cnlProSupRelation) {
		return cnlProSupRelationMapper.selectByAllInfo(cnlProSupRelation);
	}

	@Override
	public List<CnlProSupRelation> selectByConditionNull(IRequest requestContext, CnlProSupRelation cnlProSupRelation,
			int page, int pagesize) {
		PageHelper.startPage(page, pagesize);
		return cnlProSupRelationMapper.selectByConditionNull(cnlProSupRelation);
	}

}
