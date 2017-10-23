package clb.core.system.mapper;

import java.util.List;

import com.hand.hap.system.dto.Profile;

/**
 * @author tiansheng.ye@hand-china.com
 */
public interface ClbCommonMapper{
    
	List<Profile> queryProfiles(Profile profile);
}