/*
 * #{copyright}#
 */

package clb.core.system.service;

import java.util.List;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.Profile;
/**
 * @author tiansheng.ye@hand-china.com
 */
public interface IClbCommonService extends ProxySelf<IClbCommonService> {

	List<Profile> queryProfiles(IRequest request, int page, int pagesize,Profile profile);
}
