package com.dfire.test.util.assertutil;

import java.util.List;

import org.testng.Assert;

public class AssertUtil extends Assert{

	public static void contains(String actualValue, String expectValue) {
			try {
				Assert.assertTrue(actualValue.contains(expectValue), String
						.format("期待'%s'包含'%s'，实际为不包含", actualValue,
								expectValue));
			} catch (AssertionError e) {
				// TODO: handle exception
				String detailMsg = e.getMessage();
				int index = detailMsg.indexOf("expected");//去掉 expected 后面的字符串
				detailMsg = detailMsg.substring(0, index);
				AssertionError assertionError = new AssertionError(detailMsg);
				throw assertionError;
			}
		
	}
	
	public static void notContains(String actualValue, String expectValue) {
		try {
			Assert.assertFalse(actualValue.contains(expectValue), String
					.format("期待'%s'不包含'%s'，实际为包含.", actualValue, expectValue));
		} catch (Exception e) {
			String detailMsg = e.getMessage();
			int index = detailMsg.indexOf("expected");//去掉 expected 后面的字符串
			detailMsg = detailMsg.substring(0, index);
			AssertionError assertionError = new AssertionError(detailMsg);
			throw assertionError;
		}
	}
	
	public static void assertEquals(Object actualValue, Object expectValue,String message) {
		if (expectValue!=null&&String.valueOf(expectValue).equalsIgnoreCase("N/A")) {
			return ;
		}
		Assert.assertEquals(actualValue,  expectValue, message);
	}
	
	
	public static void assertEquals(String actualValue, String expectValue,String message) {
		if (expectValue!=null&&expectValue.equalsIgnoreCase("N/A")) {
			return ;
		}
		Assert.assertEquals(actualValue,  expectValue, message);
	}
	
	public static void assertEquals(Double actualValue, String expectValue,String message) {
		if (expectValue!=null&&expectValue.equalsIgnoreCase("N/A")) {
			return ;
		}
		Assert.assertEquals(actualValue, Double.valueOf(expectValue), message);
	}

	@SuppressWarnings("rawtypes")
	public static  void assertEmpty(List model, String message) {
		if (model!=null) {
			Assert.assertEquals(model.size(),0,message);
		}
		Assert.assertNull(model,message);
		
	}

	@SuppressWarnings("rawtypes")
	public static void assertNotEmpty(List model, String message) {
		Assert.assertNotNull(model,message);
		Assert.assertNotEquals(model.size(),0,message);
	}
		
	
}
