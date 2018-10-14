package compare2;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;

import checker.CheckerException;
import compare.ReflectHelper;

public class CollectionComparator{

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionComparator.class);
	
	private List<CompareKey> compareKeys;
	
	private boolean flag;//false:数据库值为实际值 ，一般用在更新类的接口；true：数据库值为期望值，一般用在查询类的接口
	
	public CollectionComparator() {
		super();
	}
	
	public CollectionComparator(boolean flag) {
		super();
		this.flag = flag;
	}
	/**
	 * 相互比较的两个集合中的元素类型相同
	 * @param left
	 * @param right
	 * @param compare
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public boolean isEqualList(List left, List right ,List<CompareKey> compareKeys) {
		this.compareKeys = compareKeys;
		//先比较是否为空
		//比较长度是否相等
		try {
			if (!lengthIsEqual(left, right)) {//如果不相等，无需继续比较
				return false;
			}
		} catch (Exception e) {
			String msg = e.getMessage();//列名字:数据库值:bean值
			System.err.println(msg);
			String msgs[] = msg.split(":::::");
			if (this.flag) {//数据库值为期望值
				Assert.assertEquals(msgs[2], msgs[1],msgs[0]);
			}else {
				Assert.assertEquals(msgs[1], msgs[2],msgs[0]);
			}
		}
		//比较元素是否存在
		for (Iterator leftIT = left.iterator(); leftIT.hasNext();) {//遍历集合元素
			Object lftObj = leftIT.next();//左边元素
			for (Object rgtObj : right) {
				if (lftObj==rgtObj) {
					return true;
				}
				try {
					if (!equals(lftObj, rgtObj ,compareKeys)) {//有元素不相等立即中断循环
						break;
					}
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
			}
		}
		return false;
	}
	private void outlog(String dbkey, Object dbvalue, Object objVal) {
		LOGGER.warn("DataMapCompObject cloumn not equals! the db key is " + dbkey + ", the db value is " + dbvalue
				+ ", the object value is " + objVal.toString());
	}
	
	private boolean equals(Object lftObj, Object rgtObj, List<CompareKey> keys) throws Exception{
		if (CollectionUtils.isEmpty(keys)) {
			LOGGER.warn("比较keys为空");
			return false;
		}
		if (isBasicType(lftObj) ) {
			for (CompareKey compareKey : keys) {
				equals(lftObj, rgtObj, compareKey);
			}
			return true;
		}
		return false;
		
	}
	
	public boolean equals(Map left, Map right, String[] keys){
		if (keys == null ||keys.length==0) {
			LOGGER.warn("比较keys为空");
			return false;
		}
		if ((left == null) || (right == null)) {
			throw new CheckerException(new IllegalArgumentException(
					"比对集合中至少一个为空！"));
		}
		if (left.size()==0||right.size()==0) {
			throw new CheckerException(new IllegalArgumentException(
					"比对集合中至少一个长度为0！"));
		}
		if (left.size() != right.size()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("CollectionComparator,集合长度不相等! 左边为： " + left.size() + " 右边为： " + right.size());
			}
//			throw new Exception("记录条数不匹配!:::::" + left.size() + ":::::"
//					+ right.size());
			Assert.assertEquals(left.size(), right.size(),"记录条数不匹配!");
		}
		
		for (String key : keys) {
			Object leftVal = left.get(key);
			Object rightVal = right.get(key);
			if (leftVal == null ? rightVal == null : leftVal.equals(rightVal)) {
				continue;
			}else {
				if (this.flag) {//数据库值为期望值
					Assert.assertEquals(rightVal,leftVal,key+"列值不匹配");
				}else {
					Assert.assertEquals(leftVal,rightVal,key+"列值不匹配");
				}
			}
		}
		
		return true;
		
	}
	/**
	 * 实际比较方法
	 * @param lftObj
	 * @param rgtObj
	 * @param parKey
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void equals(Object lftObj, Object rgtObj, CompareKey compareKey) throws Exception {
		String parKey = compareKey.getParKey();
		Object lftAttrValue = ReflectHelper.getPropValue(lftObj, parKey); //左边某一属性value
		if (lftAttrValue == null) {//如果左边值为空
			if (null!=ReflectHelper.getPropValue(rgtObj, parKey)) {
				throw new Exception(parKey+":::::"+lftAttrValue+":::::"+ReflectHelper.getPropValue(rgtObj, parKey));
				//列名字:数据库值:bean值
			}
		} else if (lftAttrValue instanceof String) {
			Object rgtAttrValue = ReflectHelper.getPropValue(rgtObj, parKey); //右边某一属性value
			if (!rgtAttrValue.equals(lftAttrValue)) {
				outlog(parKey, lftAttrValue, rgtAttrValue);
				throw new Exception(parKey+":::::"+lftAttrValue+":::::"+rgtAttrValue);
				//列名字:数据库值:bean值
			}
			
		} else if (isBasicType(lftAttrValue)) {//基本类型
			boolean com = equalBasicType(rgtObj, lftAttrValue, parKey);
			if (!com) {
				throw new Exception(parKey+":::::"+lftAttrValue+":::::"+ReflectHelper.getPropValue(rgtObj, parKey));
					//列名字:数据库值:bean值
			}
		} else if (lftAttrValue instanceof List) {
			Object rgtAttrValue =  ReflectHelper.getPropValue(rgtObj, parKey);////右边某一属性value
			if (!(rgtAttrValue instanceof List)) {
				throw new Exception(parKey+":::::"+lftAttrValue+":::::"+rgtAttrValue);
				//列名字:数据库值:bean值
			}
			List lftAttrList = (List) lftAttrValue;
			List rgtAttrList = (List) rgtAttrValue;
			
			//判断是否为空和长度不等
			if (!lengthIsEqual(lftAttrList,rgtAttrList)) {
				throw new CheckerException(new IllegalArgumentException("比对集合长度不等"));
			}
			for (int i = 0; i < lftAttrList.size(); i++) {
				Object lftsubAttr = lftAttrList.get(i);
				Object rgtsubAttr = rgtAttrList.get(i);
				for (String subKey : compareKey.getSubKeys()) {
					equals(lftsubAttr,rgtsubAttr,new CompareKey(subKey));
				}
			}
			
			
		}
	}
	
	/**
	 * 判断是否为基本类型
	 * @param rgtObj
	 * @param lftAttrValue
	 * @param key
	 * @return
	 */
	private boolean equalBasicType(Object rgtObj, Object lftAttrValue, String key) {
		if (lftAttrValue instanceof Boolean) {
			boolean rgtAttrVal = Boolean.parseBoolean((String) ReflectHelper.getPropValue(rgtObj, key));
			boolean lftAttrVal = ((Boolean) lftAttrValue).booleanValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Boolean.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Float)) {
			float rgtAttrVal = Float.parseFloat((String) ReflectHelper.getPropValue(rgtObj, key));
			float lftAttrVal = ((Float) lftAttrValue).floatValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Float.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Double)) {
			double rgtAttrVal = Double.parseDouble((String) ReflectHelper.getPropValue(rgtObj, key));
			double lftAttrVal = ((Double) lftAttrValue).doubleValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Double.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Long)) {
			long rgtAttrVal = Long.parseLong((String) ReflectHelper.getPropValue(rgtObj, key)) ;
			long lftAttrVal = ((Long) lftAttrValue).longValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Long.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Integer)) {
			int rgtAttrVal = Integer.parseInt((String) ReflectHelper.getPropValue(rgtObj, key));
			int lftAttrVal = ((Integer) lftAttrValue).intValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Integer.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Short)) {
			int rgtAttrVal = Short.parseShort((String) ReflectHelper.getPropValue(rgtObj, key));
			int lftAttrVal = ((Short) lftAttrValue).shortValue();
			if (rgtAttrVal != lftAttrVal) {
				outlog(key, Integer.valueOf(rgtAttrVal), lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Character)) {
			String rgtAttrVal = (String) ReflectHelper.getPropValue(rgtObj, key);
			String lftAttrVal = lftAttrValue.toString();
			if (!rgtAttrVal.equals(lftAttrVal)) {
				outlog(key, rgtAttrVal, lftAttrValue);
				return false;
			}
			return true;
		}
		if ((lftAttrValue instanceof Byte)) {
			String rgtAttrVal = (String) ReflectHelper.getPropValue(rgtObj, key);
			String lftAttrVal = lftAttrValue.toString();
			if (!rgtAttrVal.equals(lftAttrVal)) {
				outlog(key, rgtAttrVal, lftAttrValue);
				return false;
			}
			return true;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * 判断长度是否相等
	 * @param left
	 * @param right
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private boolean lengthIsEqual(Collection left, Collection right) throws Exception {
		if (left == right) {
			return true;
		}
		if ((left == null) || (right == null)) {
			throw new CheckerException(new IllegalArgumentException(
					"比对集合中至少一个为空！"));
		}
		if (left.size()==0||right.size()==0) {
			throw new CheckerException(new IllegalArgumentException(
					"比对集合中至少一个长度为0！"));
		}
		if (left.size() != right.size()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("CollectionComparator,集合长度不相等! 左边为： " + left.size() + " 右边为： " + right.size());
			}
			throw new Exception("记录条数不匹配!:::::" + left.size() + ":::::"
					+ right.size());
		}
		
		return true;
	}

	/**
	 * 判断是否为基础类型
	 * @param objVal
	 * @return
	 */
	private boolean isBasicType(Object objVal){
		if (objVal instanceof Boolean) {//
			return true;
		}
		if (objVal instanceof Float) {
			return true;
		}
		if (objVal instanceof Double) {
			return true;
		}
		if (objVal instanceof Long) {
			return true;
		}
		if (objVal instanceof Integer) {
			return true;
		}
		if (objVal instanceof Short) {
			return true;
		}	
		if (objVal instanceof Byte) {
			return true;
		}
		if (objVal instanceof Character) {
			return true;
		}
		return false;
	}
	
}