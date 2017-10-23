package clb.core.production.service;

import clb.core.production.dto.PrdImageText;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;

import java.util.List;

/**
 * Created by Rex.Hua on 17/4/16.
 */
public abstract interface IPrdImageTextService extends IBaseService<PrdImageText>,ProxySelf<IPrdImageTextService> {

    List<PrdImageText> query(PrdImageText dto);

    List<PrdImageText> batchUpdate(IRequest request, List<PrdImageText> dtoList);
}