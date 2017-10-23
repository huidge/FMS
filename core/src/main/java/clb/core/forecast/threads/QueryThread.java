package clb.core.forecast.threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.Id;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.drools.command.runtime.GetIdCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.hand.hap.system.dto.DTOStatus;

import clb.core.common.annotations.Title;
import clb.core.common.utils.ImportUtil;
import clb.core.common.utils.ImportUtil.ImportMessage;

public class QueryThread implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(QueryThread.class);
    
    private SqlSessionFactory sqlSessionFactory;
    
	public QueryThread(SqlSessionFactory sqlSessionFactory, String sqlId,Class clazz) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
		this.sqlId = sqlId;
		this.clazz = clazz;
	}

	private SqlSession sqlSession;
	

	public SqlSession getSqlSession() {
		return sqlSession;
	}


	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	//mapper
    private String sqlId;

    //参数
    private List<ImportMessage> param;
    
    //结果
    private List<Object> data;
    
    //判断查询结果是否重复
	private Boolean isRepeat;
	
	//判断查询结果是否重复
	private Class clazz;

    public List<ImportMessage> getParam() {
		return param;
	}


	public void setParam(List<ImportMessage> param) {
		this.param = param;
	}


	public List<Object> getData() {
		return data;
	}
	
	

	public Boolean getIsRepeat() {
		return isRepeat;
	}


	public void setIsRepeat(Boolean isRepeat) {
		this.isRepeat = isRepeat;
	}


	@Override
	public void run() {
		int i=0;
		isRepeat = false;
		data = new ArrayList<>();
		try{
			sqlSession = sqlSessionFactory.openSession();
			while(true){
				 if(i < param.size()){
					 if(!"false".equals(param.get(i).getStatus())){
						 Object d = sqlSession.selectOne(sqlId,param.get(i).getObjectCache());
						 log.info("线程"+Thread.currentThread().getName()+"查询数据为："+d);
						 if(d != null){
							 data.add(d);
							 param.get(i).setOperateFlag(DTOStatus.UPDATE);
							 param.get(i).setId(getId(clazz,d));
							 param.get(i).setObjectCache(d);
							 isRepeat = true;
						 }else{
							 param.get(i).setOperateFlag(DTOStatus.ADD);
							 param.get(i).setObjectCache(null);
						 }
					 }
				 }else{
					 break;
				 }
				 i+=1;
			}
        }catch(Exception e){
        	log.error("查询出错",e);
        }finally{
        	sqlSession.close();
    		log.info("线程"+Thread.currentThread().getName()+"关闭数据库连接");
        }
	}
	
	public static Long getId(Class clazz,Object d){
		Long data = 0L;
		Field[] fields = clazz.getDeclaredFields();
		Method method = null;
		for(int i=0;i<fields.length;i++){
    		Id id = fields[i].getAnnotation(Id.class);
    		if(id != null){
    			method = ImportUtil.getMethod(clazz,fields[i].getName(),"get");
    			break;
    		}
    	}
		if(method != null){
			try{
				data = (Long) method.invoke(d);
			}catch(Exception e){
				log.error("获取Id失败",e);
				data = 0L;
			}
		}
		return data;
		
	}

}
