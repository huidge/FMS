package clb.core.pln.service.impl;

import clb.core.order.dto.OrdOrder;
import clb.core.order.mapper.OrdOrderMapper;
import clb.core.pln.dto.PlnPlanRequest;
import clb.core.pln.dto.PlnPlanSpiderSetting;
import clb.core.pln.mapper.PlnPlanRequestMapper;
import clb.core.pln.mapper.PlnPlanSpiderSettingMapper;
//import clb.core.pln.service.IPlnPlanSpiderService;
import clb.core.pln.service.IPlnPlanSpiderService;
import clb.core.pln.service.IPlnPlanSpiderSettingService;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class PlnPlanSpiderServiceImpl extends BaseServiceImpl<PlnPlanRequest> implements IPlnPlanSpiderService{



    @Autowired
    private PlnPlanRequestMapper plnPlanRequestMapper;

    @Autowired
    private PlnPlanSpiderSettingMapper plnPlanSpiderSettingMapper;

//
//
//    @Override
//    public void exePlnSpider(String requestNumber) {
//
//        Runtime run = Runtime.getRuntime();
//        try {
////            Process process = run.exec("python F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy\\main.py ");
//            Process process = run.exec("python C:\\test.py ");
////            run.exec("cd F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy ");
////            Process process = run.exec("python main.py");
////            Process process = run.exec("cmd.exe /k start F: && cd F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy && scrapy crawl prudentialss");
////            Process process = run.exec("cmd.exe /k start F: && cd F:\\Project\\财联邦\\爬虫\\clb_scrapy\\clb_scrapy && scrapy crawl prudentialss");
//
//            InputStream input = process.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//            String szline;
//            while ((szline  = reader.readLine()) != null) {
//                System.out.println(szline);
//            }
//            reader.close();
//            process.waitFor();
//            process.destroy();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /*@Override
//    public List<PlnPlanRequest> selectPlanRequest(Long requestId, IRequest requestContext) {
//        return plnPlanRequestMapper.selectPlanRequest(plnPlanRequest);
//    }*/


    @Override
    public List<PlnPlanRequest> queryPlanRequest(PlnPlanRequest plnPlanRequest) {
        return plnPlanRequestMapper.selectPlanRequest(plnPlanRequest);
    }

    /*
        更改接口参数
     */
    @Override
    public List<PlnPlanSpiderSetting> querySpaderAuth(PlnPlanSpiderSetting dto){
        List<PlnPlanSpiderSetting> list = new ArrayList();
        PlnPlanSpiderSetting plnPlanSpiderSetting = new PlnPlanSpiderSetting();
        try{
            plnPlanSpiderSetting = plnPlanSpiderSettingMapper.selectUserPass(dto);
            list.add(plnPlanSpiderSetting);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

}