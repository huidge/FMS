package clb.core.order.controllers;

import clb.core.order.dto.OrdChooseItem;
import clb.core.order.dto.OrdCustomer;
import clb.core.order.service.IOrdChooseItemService;
import clb.core.order.service.IOrdCustomerService;
import clb.core.sys.controllers.ClbBaseController;
import com.codahale.metrics.annotation.Timed;
import com.hand.hap.core.IRequest;
import com.hand.hap.intergration.annotation.HapInbound;
import com.hand.hap.system.dto.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WsOrderChooseItem extends ClbBaseController {

    @Autowired
    private IOrdChooseItemService chooseItemService;

    /**
     *  投资移民选择产品
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdChooseItemImmigration")
    @RequestMapping(value = "/api/ordChooseItem/immigration", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryImmigration(@RequestBody OrdChooseItem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setBigClass("DC");
        dto.setExistFlag("N");
        List<OrdChooseItem> itemList = chooseItemService.queryOrdItemLimit(requestContext, dto);
        if (itemList.size() == 0) {
            dto.setExistFlag(null);
        }
        itemList = chooseItemService.queryOrdItem(requestContext, dto, page, pageSize);

        return new ResponseData(itemList);
    }

    @RequestMapping(value = "/ordChooseItem/immigration", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryImmigrationLov(OrdChooseItem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setBigClass("DC");
        dto.setExistFlag("N");
        List<OrdChooseItem> itemList = chooseItemService.queryOrdItemLimit(requestContext, dto);
        if (itemList.size() == 0) {
            dto.setExistFlag(null);
        }
        itemList = chooseItemService.queryOrdItem(requestContext, dto, page, pageSize);

        return new ResponseData(itemList);
    }

    /**
     *  债券选择产品
     *
     * @param dto
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @Timed
    @HapInbound(apiName = "ClbWsOrdChooseItemBond")
    @RequestMapping(value = "/api/ordChooseItem/bond", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryBond(@RequestBody OrdChooseItem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setBigClass("ZQ");
        dto.setExistFlag("N");
        List<OrdChooseItem> itemList = new ArrayList<OrdChooseItem>();
        if (chooseItemService.queryOrdItemLimit(requestContext, dto).size() == 0) {
            dto.setExistFlag(null);
        }
        itemList = chooseItemService.queryOrdItem(requestContext, dto, page, pageSize);

        return new ResponseData(itemList);
    }

    @RequestMapping(value = "/ordChooseItem/bond", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseBody
    public ResponseData queryBondLov(OrdChooseItem dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                  @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        dto.setBigClass("ZQ");
        dto.setExistFlag("N");
        List<OrdChooseItem> itemList = new ArrayList<OrdChooseItem>();
        if (chooseItemService.queryOrdItemLimit(requestContext, dto).size() == 0) {
            dto.setExistFlag(null);
        }
        itemList = chooseItemService.queryOrdItem(requestContext, dto, page, pageSize);

        return new ResponseData(itemList);
    }
}
