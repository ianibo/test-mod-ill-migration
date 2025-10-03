package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class BibliographicRecordIdentifierCode extends Code {
	// The initial valid bibliographic record identifier codes
	public static final String AMICUS  = "AMICUS";
	public static final String BL      = "BL";
	public static final String FAUST   = "FAUST";
	public static final String JNB     = "JNB";
	public static final String LA      = "LA";
	public static final String LCCN    = "LCCN";
	public static final String MEDLINE = "Medline";
	public static final String NCID    = "NCID";
	public static final String OCLC    = "OCLC";
	public static final String PMID    = "PMID";
	public static final String TP      = "TP";

	static {
		add(AMICUS);
		add(BL);
		add(FAUST);
		add(JNB);
		add(LA);
		add(LCCN);
		add(MEDLINE);
		add(NCID);
		add(OCLC);
		add(PMID);
		add(TP);
	}

	public BibliographicRecordIdentifierCode() {
	}

	public BibliographicRecordIdentifierCode(String bibliographicRecordIdentifierCode) {
		super(bibliographicRecordIdentifierCode);
	}
}
