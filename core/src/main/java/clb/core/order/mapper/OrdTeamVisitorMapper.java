package clb.core.order.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.order.dto.OrdTeamVisitor;

import java.util.List;

public interface OrdTeamVisitorMapper extends Mapper<OrdTeamVisitor>{
    /**
     * L团签旅客信息
     * @param ordTeamVisitor
     * @return
     */
    public List<OrdTeamVisitor> queryOrdTeamVisitor(OrdTeamVisitor ordTeamVisitor);
}