package compare;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.dfire.test.util.DataMap;

public class DataMapCompObject<T> {
	protected static final Logger logger = LoggerFactory.getLogger(DataMapCompObject.class);

	public boolean compareInterval(DataMap dm, T obj, Map<String, String> key, long interval) throws Exception {
		return compare(dm, obj, key, false, interval);
	}

	public boolean compareIgnore(DataMap dm, T obj, Map<String, String> key) throws Exception {
		return compare(dm, obj, key, true, 0L);
	}

	public boolean compare(DataMap dm, T obj, Map<String, String> key) throws Exception {
		return compare(dm, obj, key, false, 0L);
	}

	public boolean compare(DataMap dm, T obj, Map<String, String> key, boolean ignore, long interval) throws Exception{
		if (CollectionUtils.isEmpty(key)) {
			logger.warn("DataMapCompObject map key is empty");
			return false;
		}
		for (Map.Entry<String, String> entry : key.entrySet()) {
			String dbCol = (String) entry.getKey();
			String objProp = (String) entry.getValue();
			System.out.println("key:"+dbCol);
			Object objVal = ReflectHelper.getPropValue(obj, objProp);
			if (objVal == null) {
				if (!StringUtils.isEmpty(dm.getStringValue(dbCol))) {
					throw new Exception(dbCol+":::::"+dm.getStringValue(dbCol)+":::::"+objVal);
					//列名字:数据库值:bean值
				}
			} else if ((objVal instanceof String)) {
				String dbVal = dm.getStringValue(dbCol);
				if (!ignore) {
					if (!objVal.equals(dbVal)) {
						outlog(dbCol, dbVal, objVal);
						throw new Exception(dbCol+":::::"+dm.get(dbCol)+":::::"+objVal);
						//列名字:数据库值:bean值
					}
				} else if (!((String) objVal).equalsIgnoreCase(dbVal)) {
					outlog(dbCol, dbVal, objVal);
					return false;
				}
			} else if ((objVal instanceof Date)) {
				Date dbVal = (Date) dm.get(dbCol);
				long diff = Math.abs(calculateTimeDiff((Date) objVal, dbVal));
				if (diff > interval) {
					outlog(dbCol, dbVal, objVal);
					return false;
				}
			} else {
				try {
				boolean com = compareBasicType(dm, objVal, dbCol);
				if (!com) {
					throw new Exception(dbCol+":::::"+dm.get(dbCol)+":::::"+objVal);
					//列名字:数据库值:bean值
				}
			} catch (IllegalArgumentException e) {
				logger.error("DataMapCompObject Not Supported Type, db column is " + dbCol + ", object property is "
						+ objProp);
			}
				try {
					boolean com = compareBasicType(dm, objVal, dbCol);
					if (!com) {
						throw new Exception(dbCol+":::::"+dm.get(dbCol)+":::::"+objVal);
						//列名字:数据库值:bean值
					}
				} catch (IllegalArgumentException e) {
					logger.error("DataMapCompObject Not Supported Type, db column is " + dbCol + ", object property is "
							+ objProp);
				}
			}
		}
		return true;
	}


	private boolean compareBasicType(DataMap dm, Object objVal, String dbCol) {
		if ((objVal instanceof Boolean)) {
			boolean dbV = Boolean.parseBoolean(dm.getStringValue(dbCol));
			boolean obV = ((Boolean) objVal).booleanValue();
			if (dbV != obV) {
				outlog(dbCol, Boolean.valueOf(dbV), objVal);
				return false;
			}
		}
		if ((objVal instanceof Float)) {
			float dbVal = Float.parseFloat(dm.getStringValue(dbCol));
			float obV = ((Float) objVal).floatValue();
			if (dbVal != obV) {
				outlog(dbCol, Float.valueOf(dbVal), objVal);
				return false;
			}
			return true;
		}
		if ((objVal instanceof Double)) {
			double dbVal = Double.parseDouble(dm.getStringValue(dbCol));
			double obV = ((Double) objVal).doubleValue();
			if (dbVal != obV) {
				outlog(dbCol, Double.valueOf(dbVal), objVal);
				return false;
			}
			return true;
		}
		if ((objVal instanceof Long)) {
			long dbVal = dm.getLongValue(dbCol);
			long obV = ((Long) objVal).longValue();
			if (dbVal != obV) {
				outlog(dbCol, Long.valueOf(dbVal), objVal);
				return false;
			}
			return true;
		}
		if ((objVal instanceof Integer)) {
			int dbVal = dm.getIntValue(dbCol);
			int obV = ((Integer) objVal).intValue();
			if (dbVal != obV) {
				outlog(dbCol, Integer.valueOf(dbVal), objVal);
				return false;
			}
			return true;
		}
		if ((objVal instanceof Short)) {
			int dbVal = Short.parseShort(dm.getStringValue(dbCol));
			int obV = ((Short) objVal).shortValue();
			if (dbVal != obV) {
				outlog(dbCol, Integer.valueOf(dbVal), objVal);
				return false;
			}
			return true;
		}
		if ((objVal instanceof Character)) {
			String dbV = dm.getStringValue(dbCol);
			String obV = objVal.toString();
			if (!dbV.equals(obV)) {
				outlog(dbCol, dbV, objVal);
				return false;
			}
		}
		if ((objVal instanceof Byte)) {
			String dbV = dm.getStringValue(dbCol);
			String obV = objVal.toString();
			if (!dbV.equals(obV)) {
				outlog(dbCol, dbV, objVal);
				return false;
			}
			return true;
		}
		throw new IllegalArgumentException();
	}

	public static long calculateTimeDiff(Date startTime, Date endTime) {
		DateFormat df = DateFormat.getDateTimeInstance();
		long start = -1L;
		long end = -1L;
		try {
			start = df.parse(df.format(startTime)).getTime();
			end = df.parse(df.format(endTime)).getTime();
		} catch (ParseException e) {
			logger.error("哈哈哈哈，抛异常了！！！");
		}
		return Math.abs(end - start);
	}

	private void outlog(String dbkey, Object dbvalue, Object objVal) {
		logger.warn("DataMapCompObject cloumn not equals! the db key is " + dbkey + ", the db value is " + dbvalue
				+ ", the object value is " + objVal.toString());
	}
}
