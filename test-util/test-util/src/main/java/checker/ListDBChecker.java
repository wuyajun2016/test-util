package checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;

import com.dfire.test.util.DBHelper;
import com.dfire.test.util.DataMap;

import compare.CollectionHelper;
import compare.ComparatorTwoFace;
import compare.DataMapCompObject;

public class ListDBChecker<T> implements ResultChecker {
	private Object conditions;
	private String tableName;
	private Map<String, String> keys;
	private List<T> resultlist;
	private int time = -1;
	
	//查询数据库返回记录集
	private List<DataMap> dbData;
	
	private boolean flag;//false:数据库值为实际值 ，一般用在更新类的接口；true：数据库值为期望值，一般用在查询类的接口
	
	public ListDBChecker(String tableName, Map<String, String> conditions, List<T> resultlist,
			Map<String, String> keys) {
		if ((StringUtils.isBlank(tableName)) || (CollectionUtils.isEmpty(conditions)) || (CollectionUtils.isEmpty(keys))
				|| (CollectionUtils.isEmpty(resultlist))) {
			throw new CheckerException(new IllegalArgumentException("ListDBChecker"));
		}
		this.tableName = tableName;
		this.conditions = conditions;
		this.resultlist = resultlist;
		this.keys = keys;
		
		this.dbData = getDBData(tableName,conditions);
	}

	public ListDBChecker(String sql, List<T> resultlist,Map<String, String> keys,boolean flag) {
		if ((StringUtils.isBlank(sql)) || (CollectionUtils.isEmpty(keys))
				|| (CollectionUtils.isEmpty(resultlist))) {
			throw new CheckerException(new IllegalArgumentException("ListDBChecker"));
		}
		this.resultlist = resultlist;
		this.keys = keys;
		this.flag = flag;
		this.dbData = getDBData(sql);
	}
	
	public ListDBChecker(List<DataMap> explist,List<T> resultlist,Map<String, String> keys,boolean flag) {
		if (CollectionUtils.isEmpty(explist) || (CollectionUtils.isEmpty(keys))
				|| (CollectionUtils.isEmpty(resultlist))) {
			throw new CheckerException(new IllegalArgumentException("集合数据不能为空！"));
		}
		this.resultlist = resultlist;
		this.keys = keys;
		this.flag = flag;
		this.dbData = explist;
	}

	public List<DataMap> getDBData(String sql) {
		if (null == sql) {
			return null;
		}
		return DBHelper.executeQuery(sql);
	}
	
	public List<DataMap> getDBData(String tableName,Map<String, String> conditions) {
		if (null == this.conditions) {
			return null;
		}
		List<DataMap> ldm = new ArrayList();
		if ((this.conditions instanceof Map)) {
			Map<String, String> con = (Map) this.conditions;
			ldm = DBHelper.query(this.tableName, con);
		}
		return ldm;
	}
	
	
	@SuppressWarnings("rawtypes")
	public void check() throws Exception {
		if ((CollectionUtils.isEmpty(this.resultlist)) && (CollectionUtils.isEmpty(dbData))) {
			return;
		}
		boolean returnflag = true;
		try {
			returnflag = CollectionHelper.isEqualListNoOrder(dbData, this.resultlist, new ComparatorTwoFace() {
				@SuppressWarnings("unchecked")
				public boolean equals(Object left, Object right) throws Exception {
					DataMapCompObject<T> cmp = new DataMapCompObject();
					if (ListDBChecker.this.time <= 0) {
						return cmp.compare((DataMap) left, (T) right, ListDBChecker.this.keys);
					}
					return cmp.compareInterval((DataMap) left, (T) right, ListDBChecker.this.keys, ListDBChecker.this.time);
				}
			});
		} catch(CheckerException e){
			throw new CheckerException(new IllegalArgumentException(e.getMessage()));
		} catch (Exception e) {
			String msg = e.getMessage();//列名字:数据库值:bean值
			System.err.println(msg);
			String msgs[] = msg.split(":::::");
			
			if (this.flag) {//数据库值为期望值
				Assert.assertEquals(msgs[2], msgs[1],msgs[0]+"列值不匹配");
			}else {
				Assert.assertEquals(msgs[1], msgs[2],msgs[0]+"列值不匹配");
			}
		}
		Assert.assertTrue(returnflag);
//		Assert.assertTrue(CollectionHelper.isEqualListNoOrder(dbData, this.resultlist, new ComparatorTwoFace() {
//			public boolean equals(Object left, Object right) throws Exception {
//				DataMapCompObject<T> cmp = new DataMapCompObject();
//				if (ListDBChecker.this.time <= 0) {
//					return cmp.compare((DataMap) left, (T) right, ListDBChecker.this.keys);
//				}
//				return cmp.compareInterval((DataMap) left, (T) right, ListDBChecker.this.keys, ListDBChecker.this.time);
//			}
//		}), "Two list is not equals ");
	}
}
