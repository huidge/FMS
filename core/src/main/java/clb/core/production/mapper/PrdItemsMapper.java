package clb.core.production.mapper;

import clb.core.production.dto.PrdItems;
import com.hand.hap.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/12.
 */
public abstract interface PrdItemsMapper extends Mapper<PrdItems> {

    // 根据条件查询
    public List<PrdItems> selectByParam(PrdItems prdItems);

    // 根据条件查询
    public List<PrdItems> wsSelectByParam(PrdItems prdItems);

    // 获取最大编号
    public Integer getMaxItemsCode(String bigClass);
    /**
     * 根据产品公司的id  查询该公司下的所有产品
     * @param dto
     * @return
     */
	public List<PrdItems> queryitemBySupplierId(PrdItems dto);
	/**
	 * 查询产品的大中小分类
	 * @param dto
	 * @return
	 */
	public List<PrdItems> queryitemClass(PrdItems dto);
	
	
	public PrdItems selectAllInfo(PrdItems dto);

	public List<PrdItems> selectByChannel(PrdItems dto);
	
	public List<PrdItems> selectByItemName(PrdItems dto);
	
	public Long selectItemIdByItemName(String itemName);

	public String selectSupplierAgeTypeByItem(Long itemId);
}