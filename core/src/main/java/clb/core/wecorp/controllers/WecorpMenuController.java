package clb.core.wecorp.controllers;

import clb.core.sys.controllers.ClbBaseController;
import clb.core.wecorp.dto.HmsWxResponseDto;
import clb.core.wecorp.dto.MessageTemplateDTO;
import clb.core.wecorp.dto.WecorpAccount;
import clb.core.wecorp.dto.WecorpAccountMenu;
import clb.core.wecorp.service.IWecorpAccountMenuService;
import clb.core.wecorp.service.IWecorpAccountService;
import clb.core.wecorp.service.IWecorpServerService;
import clb.core.wecorp.service.IWxService;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */
@Controller
public class WecorpMenuController extends ClbBaseController {

    @Autowired
    private IWecorpAccountMenuService wecorpAccountMenuService;
    @Autowired
    private IWecorpServerService wecorpServerService;
    @Autowired
    private IWecorpAccountService wecorpAccountService;

    @Autowired
    IWxService wxService;

    @RequestMapping(value = "/api/public/wecorp/menu/create")
    @ResponseBody
    public HmsWxResponseDto createMenu(HttpServletRequest request,@RequestBody JSONObject jsonObject) throws Exception {
        WecorpAccountMenu wecorpAccountMenu = new WecorpAccountMenu();
        wecorpAccountMenu.setAccountNum(jsonObject.getString("accountNum"));
        wecorpAccountMenu.setContent(jsonObject.getString("content"));
        return wecorpAccountMenuService.createMenuNew(wecorpAccountMenu);

    }

    @RequestMapping(value = "/api/public/testSend")
    @ResponseBody
    public ResponseData testSend(HttpServletRequest request,@RequestBody MessageTemplateDTO messageTemplateDTO,@RequestParam String appId) throws Exception {
        return new ResponseData(Collections.singletonList(wxService.sendTemplate(messageTemplateDTO,appId)));

    }

    @RequestMapping(value = "/wecorp/menu/query")
    @ResponseBody
    public ResponseData query(WecorpAccountMenu wecorpAccountMenu,@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        List<WecorpAccountMenu> lists = wecorpAccountMenuService.select(requestContext,wecorpAccountMenu,page,pageSize);
        return new ResponseData(lists);
    }
}

