package clb.core.common.service.impl;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import clb.core.common.cache.SequenceCache;
import clb.core.common.dto.Sequence;
import clb.core.common.mapper.SequenceMapper;
import clb.core.common.service.ISequenceService;

/**
 * @ClassName: SequenceServiceImpl
 * @description 序列处理类
 * @author xianzhi.chen@hand-china.com
 * @date 2017年2月3日
 */
@Service
@Transactional
public class SequenceServiceImpl extends BaseServiceImpl<Sequence> implements ISequenceService {

	@Autowired
	private SequenceMapper sequenceMapper;

	@Autowired
	private SequenceCache sequenceCache;

	@Override
	public Sequence insertSelective(IRequest request, Sequence sequence) {
		return super.insertSelective(request, sequence);
	}

	@Override
	public Sequence createSequence(Sequence sequence) {
		// 设置Incr编码
		sequence.setIncrCode(sequence.getSequenceCode() + (sequence.getDateFormat() == null ? null
				: DateFormatUtils.format(new Date(), sequence.getDateFormat())));
		sequenceMapper.insertSelective(sequence);
		sequenceCache.setValue(sequence.getSequenceCode(), sequence);
		return sequence;
	}

	@Override
	public Sequence updateSequence(Sequence sequence) {
		sequenceMapper.updateByPrimaryKeySelective(sequence);
		sequenceCache.remove(sequence.getSequenceCode());
		sequenceCache.reload(sequence.getSequenceId());
		return sequence;
	}

	@Override
	public List<Sequence> batchUpdate(IRequest request, List<Sequence> sequences) {
		for (Sequence sequence : sequences) {
			if (sequence.getSequenceId() == null) {
				self().createSequence(sequence);
			} else if (sequence.getSequenceId() != null) {
				self().updateSequence(sequence);
			}
		}
		return sequences;
	};

	@Override
	public int batchDelete(List<Sequence> sequences) {
		for (Sequence sequence : sequences) {
			sequenceMapper.deleteByPrimaryKey(sequence);
			sequenceCache.remove(sequence.getSequenceCode());
		}
		return sequences.size();
	}

	/**
	 * 格式化流水号
	 *
	 * @param num
	 *            流水号
	 * @param length
	 *            长度
	 * @return 格式化后流水号
	 */
	public static String getNumberFormat(Long num, int length) {
		NumberFormat nf = NumberFormat.getInstance();
		// 设置是否使用分组
		nf.setGroupingUsed(false);
		// 设置最大整数位数
		nf.setMinimumIntegerDigits(length);
		return nf.format(num);
	}

	/**
	 * 获取序列号
	 * @param sequenceCode
	 * @return
	 */
	private String createSequenceNum(String sequenceCode) {
		// 获取定义
		Sequence sequence = sequenceCache.getValue(sequenceCode);
		if (null == sequence) {
			return null;
		}
		// 判断是否需要重新排序
		String dateCode = sequence.getDateFormat() == null ? "" : DateFormatUtils.format(new Date(), sequence.getDateFormat());
		String currentIncrCode = sequence.getSequenceCode() + dateCode;
		// 如果当前RedisKey与之前RedisKey不一样，则删除之前RedisKey
		if (!currentIncrCode.equals(sequence.getIncrCode())) {
			// 删除序列
			sequenceCache.decr(sequenceCache.getCategory() + ":" + sequenceCache.getName() + ":" + sequence.getIncrCode());
			sequence.setIncrCode(currentIncrCode);
			this.updateSequence(sequence);
		}
		// 获取流水号
		Long seq = sequenceCache.incr(sequenceCache.getCategory() + ":" + sequenceCache.getName() + ":" + currentIncrCode);
		// 更新当前序列值
		if (seq != null && (seq % SequenceCache.INCR_NUMBER) == 0) {
			sequence.setSerialNumber(seq);
			this.updateSequence(sequence);
		} // 新增序列并组装编码，格式为 前缀 + 时间代码 + 流水号
		String sequenceStr = sequence.getSequencePrefix() + (dateCode == null ? "" : dateCode)
				+ getNumberFormat(seq, Integer.parseInt(sequence.getSeqLength()));
		return sequenceStr;
	}

	/**
	 * 通过序列序列编码获取序列号
	 */
	@Override
	public String getSequence(String sequenceCode) {
		return createSequenceNum(sequenceCode);
	}

	/**
	 * 通过序列序列编码获取序列号,可自定义前缀
	 */
	@Override
	public String getSequence(String sequenceCode,String sequencePrefix) {
		return sequencePrefix + createSequenceNum(sequenceCode);
	}

	/**
	 * 重新加载数据到Redis
	 */
	public void refreshSequenceForRedis(){
		sequenceCache.init();
	}
}