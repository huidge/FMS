package clb.core.forecast.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import clb.core.common.utils.ImportUtil.ImportMessage;
import clb.core.forecast.threads.QueryThread;

public class QueryThreadHelper {
	private static Logger log = LoggerFactory.getLogger(QueryThreadHelper.class);

	
	 public static void startThread(SqlSessionFactory sqlSessionFactory,String sqlId,List<ImportMessage> params,Class clazz,int num) throws InterruptedException, IOException{
	
		 	QueryThread threads[] = new QueryThread[num];
		 	List[] queryDatas = allocateTask(params,num);
			for(int i=0;i<threads.length;i++){
	    		threads[i] = new QueryThread(sqlSessionFactory,sqlId,clazz);
	    		threads[i].setParam(queryDatas[i]);
	    	}
			
	    	ExecutorService service = Executors.newCachedThreadPool();  

		 	long startTime= System.currentTimeMillis();//开始时间  
	    	for(int i=0;i<threads.length;i++){
	    		service.execute(threads[i]);
	    	}
	    	
	    	service.shutdown();
	        while(true){  
	           if(service.isTerminated()){
	                log.info("所有的子线程都结束了！");
	                break;  
	            }  
	            Thread.sleep(1000); 
	        }
	    	long endTime= System.currentTimeMillis();
	    	log.info("读取完毕，"+num+"个线程运行时间为："+(endTime-startTime)/1000+"秒");
	    	params.get(0).setUpdateFlag(false);
	    	for(QueryThread t:threads){
	    		t.getSqlSession().close();
	    		if(t.getIsRepeat()){
	    			params.get(0).setUpdateFlag(true);
	    		}
	    	}
	    }
	    
	 	/**
	 	 * 获取第一个参数能整除第二个参数时的最大整数 
	 	 */
	    public static int findMaxInt(int data,int number){
	    	int res = data;
	    	if(res <= number)return number;
	    	while(res>=number){
	    		if(res%number == 0)return res;
	    		res = res-1;
	    	}
			return res;
	    }
	    
	    /**
	     * 分配任务
	     * @param 参数列表
	     * @param 线程数
	     * @return 每个线程分配的参数个数 
	     */
	    public static List[] allocateTask(List<?> params,int num){
	    	//获取能整除线程数的最大整数
	    	int res = findMaxInt(params.size(),num);
	    	//获取每个线程的任务个数
	    	int index = res/num;
	    	//开始标志
	    	int start = 0;
	    	//用于截取List
	    	List<?> subList = null;
	    	//无法整除的剩余任务个数
	    	int rest = params.size()-res;
	    	//返回值
	    	List resData[] = new List[num];
	    	List<Object> d = null;
	    	for(int i=0;i<num;i++){
	    		if(start == params.size()){
	    			subList = new ArrayList<>();
	    			resData[i] = subList;
	    			continue;
	    		}
	    		else subList = params.subList(start,start+index);
	    		//这里要新建一个List，否则操作会影响主List
	    		d =new ArrayList<>(subList);
	    		if(rest>0){
	    			d.add(params.get(res+i));
	    			rest = rest-1;
	    		}
	    		resData[i] = d;
	    		start = start+index;
	    	}
			return resData;
	    }
	
}
