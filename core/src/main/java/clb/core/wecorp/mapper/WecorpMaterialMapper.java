package clb.core.wecorp.mapper;

import clb.core.wecorp.dto.WecorpMaterial;
import com.hand.hap.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by shanhd on 2016/10/24.
 */
public interface WecorpMaterialMapper extends Mapper<WecorpMaterial> {

    WecorpMaterial findMaterial(@Param("attachmentId") String attachmentId, @Param("purpose") String purpose,
                            @Param("accountNum") String accountNum, @Param("enableFlag") String enableFlag);

    WecorpMaterial findMaterialByattachmentId(@Param("attachmentId") String attachmentId);

    WecorpMaterial findByAccountMediaIdAndAccountNum(@Param("accountMediaId")String accountMediaId,@Param("accountNum")String accountNum);

    List<WecorpMaterial> queryMaterial(WecorpMaterial dto);

}
