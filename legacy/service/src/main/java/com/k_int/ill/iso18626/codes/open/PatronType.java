package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class PatronType extends Code {
	// The initial valid patron type codes
	public static final String ADULT                  = "Adult";
	public static final String CHILD                  = "Child";
	public static final String FACULTY                = "Faculty";
	public static final String GRADUATE_STUDENT       = "GraduateStudent";
	public static final String RESEARCHER             = "Researcher";
	public static final String STAFF                  = "Staff";
	public static final String STUDENT                = "Student";
	public static final String UNDER_GRADUATE_STUDENT = "UnderGraduateStudent";

	static {
		add(ADULT);
		add(CHILD);
		add(FACULTY);
		add(GRADUATE_STUDENT);
		add(RESEARCHER);
		add(STAFF);
		add(STUDENT);
		add(UNDER_GRADUATE_STUDENT);
	}

	public PatronType() {
	}

	public PatronType(String patronType) {
		super(patronType);
	}
}
