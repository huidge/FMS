package clb.core.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @name Title
 * @description 用于标注Dto中的字段，值为该Dto导入导出Excel时的标题
 * @author bo.wu@hand-china.com 2017年5月12日14:02:28
 * @version 1.0 
 */
//用于描述字段
@Target(ElementType.FIELD)
//运行时通过反射获取值
@Retention(RetentionPolicy.RUNTIME)
//此注解可以被javaDoc记录
@Documented
public @interface Title {
	
	//标题
	public String title();
	
	//标题顺序，小的靠前
	public int index();

}
