package clb.core.app.controllers;

import clb.core.app.dto.AppUser;
import clb.core.app.service.IAppUserService;
import clb.core.system.dto.ClbUser;
import clb.core.system.service.IClbUserService;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import org.apache.regexp.RE;
import org.opensaml.xml.signature.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 谈晟 cheng.tan@hand-china.com
 * @description
 * @time 2017/10/16
 */
@Controller
public class AppUserController extends BaseController {

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private IClbUserService clbUserService;

//    @RequestMapping(value = "/api/public/save/AppUser")
//    @ResponseBody
//    public ResponseData saveAppUser(HttpServletRequest request,
//                                    @RequestParam Long appUserId,
//                                    @RequestParam String username,
//                                    @RequestParam String password) {
//        IRequest iRequest = createRequestContext(request);
//        ResponseData responseData = new ResponseData();
//        List<AppUser> appUsers = new ArrayList<>();
//        try {
//            appUsers.add(appUserService.saveAppUser(iRequest, appUserId, username, password));
//        } catch (Exception e) {
//            e.printStackTrace();
//            responseData.setMessage(e.getMessage());
//            responseData.setSuccess(false);
//            return responseData;
//        }
//        responseData.setRows(appUsers);
//        responseData.setSuccess(true);
//        responseData.setMessage("保存app用户成功!");
//        return responseData;
//    }

    @Timed
    @HapInbound(apiName = "ClbWsAppUserPublic")
    @RequestMapping(value = "/api/public/del/AppUser")
    @ResponseBody
    public ResponseData delAppUser(HttpServletRequest request,
                                   @RequestParam Long appUserId) {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
            int delNum = appUserService.delAppUser(iRequest, appUserId);
            if (delNum == 0) { // 删除数量为0
                responseData.setSuccess(false);
                responseData.setMessage("删除app用户失败!");
            } else {
                responseData.setMessage("删除app用户成功!");
                responseData.setSuccess(true);
                responseData.setTotal((long) delNum);
            }
        } catch (Exception e) { // 没有找到对应的app用户
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
        return responseData;
    }

    @Timed
    @HapInbound(apiName = "ClbWsAppUserEditPublic")
    @RequestMapping(value = "/api/public/edit/AppUser")
    @ResponseBody
    public ResponseData editAppUser(HttpServletRequest request, @RequestBody AppUser dto) {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        try {
            int updateNum = appUserService.editAppUser(iRequest, dto.getAppUserId(), dto.getAppUsername(), dto.getAppPassword());
            if (updateNum == 0) { // 更新数量为0
                responseData.setSuccess(false);
                responseData.setMessage("更新app用户失败!");
            } else {
                responseData.setSuccess(true);
                responseData.setMessage("更新app用户成功");
                responseData.setTotal((long) updateNum);
            }
        } catch (Exception e) { // 没有找到对应的app用户
            e.printStackTrace();
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
        return responseData;
    }

    @Timed
    @HapInbound(apiName = "ClbWsAppUserQueryPublic")
    @RequestMapping(value = "/api/public/query/clbUserByClbId")
    @ResponseBody
    public ResponseData queryClbUserByClbId(HttpServletRequest request, @RequestParam Long clbId) {
        IRequest iRequest = createRequestContext(request);
        ResponseData responseData = new ResponseData();
        ClbUser clbUser = new ClbUser();
        clbUser.setUserId(clbId);
        List<ClbUser> list = new ArrayList<>();
        list.add(clbUserService.selectByPrimaryKey(iRequest, clbUser));
        responseData.setRows(list);
        return responseData;
    }



}
