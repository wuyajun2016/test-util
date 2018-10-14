package com.dfire.test.base;

/**
 * 测试用例对应bean
 * @author dh
 *
 */
public class HttpTestCase {

	private String run;  //是否执行
	private String caseScene;  //用例场景
	private String step;//步骤
	private String interfaceDesc; //接口描述
	private String url;        //请求地址
	private String method;  //请求方法
	private String body;    //请求参数
	private String verify;       //验证参数
	private String intrmVar;   //中间变量
	public String getRun() {
		return run;
	}
	public void setRun(String run) {
		this.run = run;
	}
	public String getCaseScene() {
		return caseScene;
	}
	public void setCaseScene(String caseScene) {
		this.caseScene = caseScene;
	}
	public String getInterfaceDesc() {
		return interfaceDesc;
	}
	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getVerify() {
		return verify;
	}
	public void setVerify(String verify) {
		this.verify = verify;
	}
	public String getIntrmVar() {
		return intrmVar;
	}
	public void setIntrmVar(String intrmVar) {
		this.intrmVar = intrmVar;
	}
	
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String toString(){
		return "caseScene: "+getCaseScene()+",step: "+getStep()+",interfaceDesc: "+getInterfaceDesc()+",url: "+getUrl()+",body: "+getBody()+",verify:"+getVerify();
		
	}
	
	
}
