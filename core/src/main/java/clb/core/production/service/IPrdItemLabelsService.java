package clb.core.production.service;

import clb.core.production.dto.PrdItemLabelLines;
import clb.core.production.dto.PrdItemLabels;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/16.
 */
public abstract interface IPrdItemLabelsService extends IBaseService<PrdItemLabels>,ProxySelf<IPrdItemLabelsService> {

    String selectLabelId(PrdItemLabels dto);

}