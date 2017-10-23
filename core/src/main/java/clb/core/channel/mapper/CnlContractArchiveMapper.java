package clb.core.channel.mapper;

import com.hand.hap.mybatis.common.Mapper;
import clb.core.channel.dto.CnlContractArchive;

import java.util.List;

public interface CnlContractArchiveMapper extends Mapper<CnlContractArchive>{
    /**
     * 合约档案信息查询
     * @param cnlContractArchive
     * @return
     */
    public List<CnlContractArchive> queryArchive(CnlContractArchive cnlContractArchive);
}