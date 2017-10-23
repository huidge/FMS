package clb.core.production.service;

import clb.core.production.dto.PrdItemLabelLines;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/16.
 */
public abstract interface IPrdItemLabelLinesService extends IBaseService<PrdItemLabelLines>,ProxySelf<IPrdItemLabelLinesService> {

    List<PrdItemLabelLines> selectAlive(PrdItemLabelLines dto);

    List<PrdItemLabelLines> choiceTags(PrdItemLabelLines dto);

    PrdItemLabelLines deleteByItemLabel(PrdItemLabelLines dto);
}
