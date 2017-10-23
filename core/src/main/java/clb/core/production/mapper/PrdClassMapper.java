package clb.core.production.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hand.hap.mybatis.common.Mapper;

import clb.core.production.dto.PrdClass;
/**
 * Created by wanjun.feng on 17/4/12.
 */
public abstract interface PrdClassMapper extends Mapper<PrdClass> {
    
	/**
	 * 查询第一级目录
	 * @param setId
	 * @return
	 */
	List<PrdClass> queryBySetId(Long setId);
	
	/**
	 * 查询第二级目录
	 * @param setId
	 * @param className1
	 * @return
	 */
	List<PrdClass> queryClassTwoInfo(@Param(value="setId")Long setId, @Param(value="className1")String className1);
	
	/**
	 * 查询第三级目录
	 * @param setId
	 * @param className1
	 * @param className2
	 * @return
	 */
	List<PrdClass> queryClassThreeInfo(@Param(value="setId")Long setId, @Param(value="className1")String className1,@Param(value="className2")String className2);
	
	/**
	 * 查询代码值id
	 * @param value
	 * @return
	 */
	Map<String,String> selectCodeValue(@Param(value="value")String value,@Param(value="code")String code);
	
}