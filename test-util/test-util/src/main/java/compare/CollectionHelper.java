package compare;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import checker.CheckerException;

public class CollectionHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionHelper.class);

	public static boolean isEqualList(Collection left, Collection right) throws Exception {
		return isEqualList(left, right, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	public static boolean isEqualList(Collection left, Collection right, String[] exclude) throws Exception {
		if (!needCompareEquals(left, right)) {
			return compareRef(left, right);
		}
		Iterator it1 = left.iterator();
		Iterator it2 = right.iterator();
		Object obj1 = null;
		Object obj2 = null;
		while ((it1.hasNext()) && (it2.hasNext())) {
			obj1 = it1.next();
			obj2 = it2.next();
			if (ArrayUtils.isEmpty(exclude)) {
				if (obj1 == null ? obj2 != null : !EqualsBuilder.reflectionEquals(obj1, obj2)) {
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("CollectionHelper,left not equals right!");
					}
					return false;
				}
			} else if (obj1 == null ? obj2 != null : !EqualsBuilder.reflectionEquals(obj1, obj2, exclude)) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("CollectionHelper,left not equals right!");
				}
				return false;
			}
		}
		return (!it1.hasNext()) && (!it2.hasNext());
	}

	public static boolean isEqualListNoOrder(Collection left, Collection right) throws Exception {
		return isEqualListNoOrder(left, right, ArrayUtils.EMPTY_STRING_ARRAY);
	}

	public static boolean isEqualListNoOrder(Collection left, Collection right, final String[] exclude) throws Exception {
		return isEqualListNoOrderTemplate(left, right, new CompareFace() {
			public boolean compare(Object obj1, Object obj2) {
				return EqualsBuilder.reflectionEquals(obj1, obj2, exclude);
			}
		});
	}

	public static boolean isEqualListNoOrder(Collection left, Collection right, ComparatorTwoFace compare) throws Exception {
		if (!needCompareEquals(left, right)) {
			return compareRef(left, right);
		}
		Object obj1;
		for (Iterator i = left.iterator(); i.hasNext();) {
			obj1 = i.next();
			for (Object obj2 : right) {
				if (obj1 == null ? obj2 == null : compare.equals(obj1, obj2)) {
					right.remove(obj2);
					break;
				}
			}
		}
		return right.size() == 0;
	}

	public static boolean isEqualListNoOrder(Collection left, Collection right, ComparatorFace compare) throws Exception {
		if (!needCompareEquals(left, right)) {
			return compareRef(left, right);
		}
		Object obj1;
		for (Iterator i = left.iterator(); i.hasNext();) {
			obj1 = i.next();
			for (Object obj2 : right) {
				if (obj1 == null ? obj2 == null : compare.equals(obj1, obj2)) {
					right.remove(obj2);
					break;
				}
			}
		}

		return right.size() == 0;
	}

	private static boolean isEqualListNoOrderTemplate(Collection left, Collection right, CompareFace face) throws Exception {
		if (!needCompareEquals(left, right)) {
			return compareRef(left, right);
		}
		Object obj1;
		for (Iterator i = left.iterator(); i.hasNext();) {
			obj1 = i.next();
			for (Object obj2 : right) {
				if (obj1 == null ? obj2 == null : face.compare(obj1, obj2)) {
					right.remove(obj2);
					break;
				}
			}
		}
		return right.size() == 0;
	}

	private static boolean needCompareEquals(Collection left, Collection right) throws Exception {
		if (left == right) {
			return false;
		}
		if ((left == null) || (right == null)) {
			throw new CheckerException(new IllegalArgumentException("比对集合中至少一个为空！"));
		}
		if (left.size() != right.size()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("CollectionHelper,left size not equals right size! left size is " + left.size()
						+ " right size is " + right.size());
			}
			throw new Exception("记录条数不匹配!:::::"+left.size()+":::::"+right.size());
		}
		return true;
	}

	private static boolean compareRef(Collection left, Collection right) {
		if (left == right) {
			return true;
		}
		return false;
	}
}