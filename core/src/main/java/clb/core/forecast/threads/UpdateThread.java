package clb.core.forecast.threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hand.hap.system.dto.DTOStatus;

import clb.core.common.utils.ExceptionUtil;
import clb.core.common.utils.ImportUtil.ImportMessage;

public class UpdateThread implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(UpdateThread.class);
    
	
    
	public UpdateThread(SqlSessionFactory sqlSessionFactory, String updateSqlId, String insertSqlId) {
		super();
		this.sqlSessionFactory = sqlSessionFactory;
		this.updateSqlId = updateSqlId;
		this.insertSqlId = insertSqlId;
	}



	//数据库连接
	private SqlSessionFactory sqlSessionFactory;
    
	//更新mapper
    private String updateSqlId;
    
    //插入mapper
    private String insertSqlId;

    //更新参数
    private List<ImportMessage> updateParam;
    
    private SqlSession sqlSession;
	
	

	public SqlSession getSqlSession() {
		return sqlSession;
	}


	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}




	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}



	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}



	public String getUpdateSqlId() {
		return updateSqlId;
	}



	public void setUpdateSqlId(String updateSqlId) {
		this.updateSqlId = updateSqlId;
	}



	public String getInsertSqlId() {
		return insertSqlId;
	}



	public void setInsertSqlId(String insertSqlId) {
		this.insertSqlId = insertSqlId;
	}



	public List<ImportMessage> getUpdateParam() {
		return updateParam;
	}



	public void setUpdateParam(List<ImportMessage> updateParam) {
		this.updateParam = updateParam;
	}

	@Override
	public void run() {
		int index=0;
		try{
			sqlSession = sqlSessionFactory.openSession();
        }catch(Exception e){}
		while(true){
			 
				 StringBuffer stringBuffer = new StringBuffer("");
				 if(index < updateParam.size()){
					 ImportMessage message = updateParam.get(index);
					 Object data = message.getData();
					 String flag = message.getOperateFlag();
					 try{
						if(DTOStatus.ADD.equals(flag)){
							 sqlSession.insert(insertSqlId,data); 
						 }else if(DTOStatus.UPDATE.equals(flag)){
							 sqlSession.update(updateSqlId,data);
						 }
					}catch(Exception e){
						 if(ExceptionUtil.getExceptionType(e) == 1){
								stringBuffer.append("数据库中存在重复值;");
							}else{
								 if(DTOStatus.ADD.equals(flag)){
									 stringBuffer.append("插入失败;");
								 }else if(DTOStatus.UPDATE.equals(flag)){
									 stringBuffer.append("更新失败;");
								 }
							}
						 	message.setStatus("false");
						 	message.setErrorMessage(stringBuffer.toString());
					 }
					 
				 }
				 index+=1;
				 if(index >= updateParam.size())break;
		}
		sqlSession.close();
	}

}
