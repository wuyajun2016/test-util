package compare2;

import java.util.List;

public class CompareKey {

	public CompareKey(String parKey) {
		super();
		setParKey(parKey);
	}
	
	public CompareKey(String string, String[] areavoKeys) {
		setParKey(parKey);
		setSubKeys(areavoKeys);
	}

	private String parKey;
	private String[] subKeys ;
	
	public String getParKey() {
		return parKey;
	}
	public void setParKey(String parKey) {
		this.parKey = parKey;
	}
	
	
	public String[] getSubKeys() {
		return subKeys;
	}

	public void setSubKeys(String[] subKeys) {
		this.subKeys = subKeys;
	}

	public CompareKey getCompareKey() {
		return this;
	}
	
	
}
