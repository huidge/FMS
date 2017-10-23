package clb.core.common.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFoldFileNames {

    public static void main(String[] args) {
    	getFileNameTypeOne();
    	getFileNameTypeTwo();
    	getFileNameTypeThree();
    }

    /**
     * 产品名+年期+性别+年龄+吸烟+缴费方式+保费
     * @return
     */
    public static List<Map<String,String>> getFileNameTypeOne() {
        String path = "C:/Users/hand/Desktop/test/type_one"; // 路径
        List<Map<String,String>> listFile = new ArrayList<Map<String,String>>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                System.out.println(fs.getName());
                Map<String,String> fieldMap = new HashMap<String,String>();
                String[] fieldList = fs.getName().split("_");
                for(int j=0; j<fieldList.length;j++){
                	if(j==0){
                	   fieldMap.put("itemName", fieldList[0]);
                	}
                	if(j==1){
                 	   fieldMap.put("sublineName", fieldList[1]);
                 	}
                	if(j==2){
                 	   fieldMap.put("gender", fieldList[2]);
                 	}
                	if(j==3){
                  	   fieldMap.put("age", fieldList[3]);
                  	}
                	if(j==4){
                  	   fieldMap.put("smokeFlag", fieldList[4]);
                  	}
                	if(j==5){
                  	   fieldMap.put("payMethod", fieldList[5]);
                  	}
                	if(j==6){
                   	   fieldMap.put("premium", fieldList[6].substring(0, fieldList[6].length()-4));
                   	}
                }
                listFile.add(fieldMap);
            }
        }
        return listFile;
    }
    
    /**
     * 产品名+年期+性别+年龄+吸烟+保额+缴费方式+保费
     * @return
     */
    public static List<Map<String,String>> getFileNameTypeTwo() {
        String path = "C:/Users/hand/Desktop/test/type_two"; // 路径
        List<Map<String,String>> listFile = new ArrayList<Map<String,String>>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                System.out.println(fs.getName());
                Map<String,String> fieldMap = new HashMap<String,String>();
                String[] fieldList = fs.getName().split("_");
                for(int j=0; j<fieldList.length;j++){
                	if(j==0){
                	   fieldMap.put("itemName", fieldList[0]);
                	}
                	if(j==1){
                 	   fieldMap.put("sublineName", fieldList[1]);
                 	}
                	if(j==2){
                 	   fieldMap.put("gender", fieldList[2]);
                 	}
                	if(j==3){
                  	   fieldMap.put("age", fieldList[3]);
                  	}
                	if(j==4){
                  	   fieldMap.put("smokeFlag", fieldList[4]);
                  	}
                	if(j==5){
                   	   fieldMap.put("amount", fieldList[5]);
                   	}
                	if(j==6){
                  	   fieldMap.put("payMethod", fieldList[6]);
                  	}
                	if(j==7){
                   	   fieldMap.put("premium", fieldList[7].substring(0, fieldList[7].length()-4));
                   	}
                }
                listFile.add(fieldMap);
            }
        }
        return listFile;
    }
    
    /**
     * 产品名+年期+保障地区+自付选项+性别+年龄+保费
     * @return
     */
    public static List<Map<String,String>> getFileNameTypeThree() {
        String path = "C:/Users/hand/Desktop/test/type_three"; // 路径
        List<Map<String,String>> listFile = new ArrayList<Map<String,String>>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                System.out.println(fs.getName());
                Map<String,String> fieldMap = new HashMap<String,String>();
                String[] fieldList = fs.getName().split("_");
                for(int j=0; j<fieldList.length;j++){
                	if(j==0){
                	   fieldMap.put("itemName", fieldList[0]);
                	}
                	if(j==1){
                 	   fieldMap.put("sublineName", fieldList[1]);
                 	}
                	if(j==2){
                 	   fieldMap.put("securityArea", fieldList[2]);
                 	}
                	if(j==3){
                  	   fieldMap.put("age", fieldList[3]);
                  	}
                	if(j==4){
                  	   fieldMap.put("smokeFlag", fieldList[4]);
                  	}
                	if(j==5){
                   	   fieldMap.put("amount", fieldList[5]);
                   	}
                	if(j==6){
                  	   fieldMap.put("payMethod", fieldList[6]);
                  	}
                	if(j==7){
                   	   fieldMap.put("premium", fieldList[7].substring(0, fieldList[7].length()-4));
                   	}
                }
                listFile.add(fieldMap);
            }
        }
        return listFile;
    }
    
    /**
     * 资料库导入文件
     * @return
     */
    public static List<Map<String,String>> getFileInfo(String url) {
        String path = url; // 资料库路径
        List<Map<String,String>> listFile = new ArrayList<Map<String,String>>();
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(path + " not exists");
            return null;
        }
        File fa[] = f.listFiles();
        for (int i = 0; i < fa.length; i++) {
            File fs = fa[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                System.out.println(fs.getName());
                Map<String,String> fieldMap = new HashMap<String,String>();
                String[] fieldList = fs.getName().split("-");
                for(int j=0; j<fieldList.length;j++){
                	if(j==0){
                	   fieldMap.put("supplierName", fieldList[0]);
                	}
                	if(j==1){
                 	   fieldMap.put("datumType", fieldList[1]);
                 	}
                	if(j==2){
                 	   fieldMap.put("userType", fieldList[2]);
                 	}
                	if(j==3){
                   	   fieldMap.put("content", fieldList[3].substring(0, fieldList[3].length()-4));
                   	}
                }
                listFile.add(fieldMap);
            }
        }
        return listFile;
    }
}