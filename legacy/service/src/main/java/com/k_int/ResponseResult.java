package com.k_int;

import java.util.HashMap;
import java.util.Map;

public class ResponseResult extends GenericResult {

	/** Generic responses that need to be returned */
	public Map<String, Object> responseResult = new HashMap<String, Object>();

	public ResponseResult() {
	}

	public ResponseResult(String id) {
		super(id);
	}
	
	public void put(String key, Object value) {
		responseResult.put(key,  value);
	}
}
