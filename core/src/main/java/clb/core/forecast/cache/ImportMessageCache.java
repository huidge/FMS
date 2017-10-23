package clb.core.forecast.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;

import com.hand.hap.cache.impl.HashStringRedisCache;

/**
 * @name ImportMessageCache
 * @description 导入信息缓存处理类
 * @author bo.wu@hand-china.com 2017年6月16日11:12:53
 */
public class ImportMessageCache extends HashStringRedisCache<byte[]> {

	private Logger logger = LoggerFactory.getLogger(ImportMessageCache.class);

	@Override
	public void init() {
		setType(byte[].class);
		strSerializer = getRedisTemplate().getStringSerializer();
	}

	@Override
	public byte[] getValue(String key) {
		return super.getValue(key);
	}

	@Override
	public void setValue(String key, byte[] importMessages) {
		super.setValue(key,importMessages);
	}

	@Override
	public void remove(String key) {
		getRedisTemplate().execute((RedisCallback<Object>) (connection) -> {
			connection.hDel(strSerializer.serialize(getFullKey(key)), strSerializer.serialize(key));
			return null;
		});
	}

	
}
