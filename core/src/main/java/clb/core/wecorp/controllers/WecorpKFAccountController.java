package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.service.IWxService;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zyc on 2017/7/21.
 */
@Controller
public class WecorpKFAccountController extends ClbBaseController {

    @Autowired
    IWxService wxService;

    @RequestMapping(value = {"/api/public/wxkf/uploadImg"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseData uploadImg(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file, @RequestParam String appId, @RequestParam String kfAccount) throws Exception {
        return wxService.uploadheadimg(appId, kfAccount, file.getOriginalFilename(), file);
    }

    @RequestMapping(value = {"/api/public/wxkf/add"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseData add(HttpServletRequest request, @RequestParam String appId,@RequestParam String kfAccount, @RequestParam String nickname, @RequestParam String password) throws Exception {
            return wxService.addCustom(appId, kfAccount, nickname, password);
    }

    @RequestMapping(value = {"/api/public/wxkf/getkflist"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getkflist(HttpServletRequest request, @RequestParam String appId) throws Exception {
        return wxService.getkflist(appId);
    }

    @RequestMapping(value = {"/api/public/wxkf/delete"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestParam String appId,@RequestParam String kfAccount) throws Exception {
        return wxService.delCustom(appId, kfAccount);
    }

    @RequestMapping(value = {"/api/public/wxkf/getOnlinekflist"}, method = RequestMethod.POST)
    @ResponseBody
    public ResponseData getOnlinekflist(HttpServletRequest request, @RequestParam String appId) throws Exception {
        return wxService.getOnlinekflist(appId);
    }

}
