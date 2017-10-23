package clb.core.common.mapper;

import clb.core.common.dto.Sequence;
import com.hand.hap.mybatis.common.Mapper;

/**
 * @name SequenceMapper
 * @description 序列号规则配置Mapper
 * @author jiaolong.li@hand-china.com
 */
public interface FiredJobMapper {

    public String queryJobFireStatus(String jobGroup, String jobName);
}