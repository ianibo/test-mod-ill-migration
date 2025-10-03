package com.k_int;

import java.util.HashMap;
import java.util.Map;

public class OperationResult extends ResponseResult {

	private static final String KEY_FAILED       = "failed";
	private static final String KEY_NO_OPERATION = "noOperation";
	private static final String KEY_OPERATION    = "operation";
	private static final String KEY_SUCCESSFUL   = "successful";

	private static final String OPERATION_ADD    = "Add";
	private static final String OPERATION_REMOVE = "Remove";

	public OperationResult() {
	}

	public OperationResult(String id) {
		super(id);
	}

	public void setOperationAdd() {
		put(KEY_OPERATION, OPERATION_ADD);
	}
	
	public void setOperationRemove() {
		put(KEY_OPERATION, OPERATION_REMOVE);
	}
	
	public void setSuccessful(int successful) {
		put(KEY_SUCCESSFUL, Integer.valueOf(successful));
	}
	
	public void setFailed(int failed) {
		put(KEY_FAILED, Integer.valueOf(failed));
	}

	public void setNoOperation(int noOperation) {
		put(KEY_NO_OPERATION, Integer.valueOf(noOperation));
	}
}
