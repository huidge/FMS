package clb.core.common.service.impl;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hand.hap.mybatis.util.SqlMapper;

import clb.core.common.service.ICommonService;
import clb.core.common.utils.CommonUtil;

@Service
@Transactional
@SuppressWarnings("rawtypes")
public class CommonServiceImpl implements ICommonService {
	@Autowired
    private SqlSessionFactory sqlSessionFactory;
	
	@Override
	public List<HashMap> selectResultsBySql(String sql, Object obj) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			SqlMapper sqlMapper = new SqlMapper(sqlSession);
			List<HashMap> preResults = sqlMapper.selectList("<script>\n\t" + sql + "</script>", obj, HashMap.class);
			return preResults;
		} catch (Exception e) {
			CommonUtil.printStackTraceToStr(e);
		}
		return null;
	}

}
