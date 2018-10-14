package compare;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectHelper {
	protected static final Logger logger = LoggerFactory.getLogger(ReflectHelper.class);

	public static Object getPropValue(Object obj, String att) {
		if ((null == obj) || (StringUtils.isBlank(att))) {
			logger.error("param check failed!");
			return null;
		}
		for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				Field f = clazz.getDeclaredField(att);
				f.setAccessible(true);
				try {
					return f.get(obj);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e3) {
				}
			} catch (SecurityException e1) {
			} catch (NoSuchFieldException e2) {
			}
		}
		logger.error("can't find the field " + att);

		return null;
	}
}
