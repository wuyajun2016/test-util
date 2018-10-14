package com.dfire.test.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;





import com.alibaba.fastjson.JSONPath;
import com.dfire.test.util.ExcelUtil;
import com.dfire.test.util.FileUtil;
import com.dfire.test.util.StringUtil;

public class TestBase {

	//testcase 数据
	public List<HttpTestCase> testcaseList = new ArrayList<HttpTestCase>();

	//公共参数池
	public Map<String, String> commonParamMap = new HashMap<String, String>();


	 /**
	  * 读取excel中的testcase到公共数据list
	  */
	@SuppressWarnings("unchecked")
	protected void readExcelToList(String excelPath){
		List<HttpTestCase> temArrayList = new ArrayList<HttpTestCase>();
		if (StringUtil.isNotEmpty(excelPath)) {
			temArrayList.clear();
			List<File> files = FileUtil.getFilesByConf(System.getProperty("user.dir"), excelPath);// excel文件数组
			for (File file : files) {
				if (StringUtil.isEmpty(file.getName())) {
					continue;
				}
				temArrayList = ExcelUtil.getExcelUtil().readExcel(HttpTestCase.class,file.getAbsolutePath()); // 根据excel名称读取excel
				testcaseList.addAll(temArrayList); // 将excel数据添加至list
			}
		}
	}
	
	
	/**
	 * 解析和组装URL
	 * @param url
	 * @return 
	 */
	public String buildUrl(String url) {
		if (StringUtil.isEmpty(url)) {
			return "";
		}
		return getCommonParam(url);
	}
	
	
	/**
	 * 组建参数（处理${xxxx}）
	 * 
	 * @return
	 */
	public String buildParam(String param) {
		if (StringUtil.isEmpty(param)) {
			return "";
		}
		return getCommonParam(param);
	}

	

	/**
	 * 将字符串中${xx}替换为正确的数据
	 * 
	 * @param param
	 * @return
	 */
	protected String getCommonParam(String param) {
		//替换符，如果数据中包含“${}”则会被替换成公共参数中存储的数据
		Pattern commonParpattern = Pattern.compile("\\$\\{(.*?)\\}");
		Matcher m = commonParpattern.matcher(param);// 取公共参数正则
		while (m.find()) {
			String replaceKey = m.group(1);
			//从公共参数池中获取值
			String value = commonParamMap.get(replaceKey);
			// 如果公共参数池中未能找到对应的值，该用例失败。
			Assert.assertNotNull(value,String.format("格式化参数失败，公共参数中找不到%s", replaceKey));
			param = param.replace(m.group(), value);
		}
		return param;
	}
	
	
	protected void verifyResult(String sourchData, String verifyStr) {
		if (StringUtil.isEmpty(verifyStr)) {
			return;
		}
		//将用例中的${}替换为公共参数
		verifyStr = getCommonParam(verifyStr);
	    // 通过';'分隔，通过jsonPath进行一一校验
		Pattern pattern = Pattern.compile("([^;]*)=([^;]*)");
		Matcher m = pattern.matcher(verifyStr.trim());
		while (m.find()) {
			//m.group(1) 为 “=”左边括号的内容为 key，通过jsonpath提取出实际值，  m.group(2) 为 “=”右边括号的内容为 期待值  
			//实际值
			String actualValue = String.valueOf(JSONPath.read(sourchData, m.group(1).trim())); 
			//期待值
			String exceptValue = m.group(2);
			Assert.assertEquals(actualValue, exceptValue, "验证预期结果失败！");
		}
				
	}

	/**
	 * 提取json串中的值保存至公共池中
	 * @param srcJson   将被提取的json串。
	 * @param express  所有将被保存的数据：xx=$.jsonpath.xx;oo=$.jsonpath.oo，将$.jsonpath.
	 *            xx提取出来的值存放至公共池的xx中，将$.jsonpath.oo提取出来的值存放至公共池的oo中
	 */
	protected void saveResult(String srcJson, String express) {
		if (StringUtil.isEmpty(srcJson) || StringUtil.isEmpty(express)) {
			return;
		}
		express = getCommonParam(express);
		String[] saves = express.split(";");
		for (String save : saves) {
			String key, value;
			Pattern pattern = Pattern.compile("([^;=]*)=([^;]*)");
			Matcher m = pattern.matcher(save.trim());
			while (m.find()) {
				key = m.group(1);
				value = String.valueOf(JSONPath.read(srcJson, m.group(2).trim()));
				if (null==value) {
					value = "";
				}
				System.out.println(String.format("存储公共参数   %s值为：%s.", key, value));
				commonParamMap.put(key, value);
			}
		}
	}
}
