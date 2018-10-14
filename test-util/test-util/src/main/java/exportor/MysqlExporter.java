package exportor;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dfire.test.util.ColumnInfo;
import com.dfire.test.util.DBHelper;
import com.dfire.test.util.DBUtils;
import com.dfire.test.util.DataMap;

import au.com.bytecode.opencsv.CSVWriter;

public class MysqlExporter {
	private static final Logger logger = LoggerFactory.getLogger(MysqlExporter.class);
	private final String DEFAULT_PATH = "./";
	private final String CSV_SUFFIX = ".csv";

	public static boolean export(String tableName, String filePath, int count) {
		if ((StringUtils.isBlank(tableName)) || (count <= 0)) {
			logger.error("tableName can not be blank and count must no less than 1");
			return false;
		}
		if (StringUtils.isBlank(filePath)) {
			filePath = "./" + tableName + ".csv";
		}
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(filePath), ',', '\000');

			writer.writeAll(getTestData(tableName, count));
			writer.close();
		} catch (IOException e) {
			logger.error("Create file[" + filePath + "] failed.", e);
			return false;
		}
		return true;
	}

	private static List<String[]> getTestData(String tableName, int count) {
		String sql = "SELECT * FROM " + tableName + " limit " + count;

		List<ColumnInfo> columnInfos = DBUtils.getTableColumnInfo(tableName);

		List<DataMap> dataMaps = DBHelper.executeQuery(sql);

		List<String[]> dataList = new ArrayList();

		List<String> columnNames = new ArrayList();
		for (ColumnInfo column : columnInfos) {
			columnNames.add(column.getName());
		}
		dataList.add(columnNames.toArray(new String[columnNames.size()]));
		for (DataMap e : dataMaps) {
			List<String> list = new ArrayList();
			for (ColumnInfo column : columnInfos) {
				String value = e.getStringValue(column.getName());
				list.add(process(column, value));
			}
			dataList.add(list.toArray(new String[list.size()]));
		}
		return dataList;
	}

	private static String process(ColumnInfo info, String value) {
		String newValue = filterControlCharacter(value);
		if (StringUtils.equals(info.getDbType(), "DATETIME")) {
			newValue = "0";
		}
		return newValue;
	}

	private static String filterControlCharacter(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		value = value.replaceAll("\\r", "");
		value = value.replaceAll("\\n", "");
		return value;
	}
}
