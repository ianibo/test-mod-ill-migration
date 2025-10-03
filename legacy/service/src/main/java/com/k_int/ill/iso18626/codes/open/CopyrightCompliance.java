package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class CopyrightCompliance extends Code {
	// The initial valid copyright compliance codes
	public static final String AU_COPYR_CAT_S183_COMW  = "AU-CopyRCatS183ComW";
	public static final String AU_COPYR_CAT_S183_STATE = "AU-CopyRCatS183State";
	public static final String AU_COPYRIGHT_ACT_S49    = "AU-CopyrightActS49";
	public static final String AU_COPYRIGHT_ACT_S50_1  = "AU-CopyrightActS50-1";
	public static final String AU_COPYRIGHT_ACT_S50_7A = "AU-CopyrightActS50-7a";
	public static final String AU_COPYRIGHT_CLEARED    =  "AU-CopyrightCleared";
	public static final String AU_GENBUS               = "AU-GenBus";
	public static final String NZ_COPYRIGHT_ACT_S54    = "NZ-CopyrightActS54";
	public static final String NZ_COPYRIGHT_ACT_S55    = "NZ-CopyrightActS55";
	public static final String OTHER                   = "Other";
	public static final String UK_COPYR_FEE_PAID       = "UK-CopyRFeePaid";
	public static final String UK_FAIR_DEALING         = "UK-FairDealing";
	public static final String US_CCG                  = "US-CCG";
	public static final String US_CCL                  = "US-CCL";

	static {
		add(AU_COPYR_CAT_S183_COMW);
		add(AU_COPYR_CAT_S183_STATE);
		add(AU_COPYRIGHT_ACT_S49);
		add(AU_COPYRIGHT_ACT_S50_1);
		add(AU_COPYRIGHT_ACT_S50_7A);
		add(AU_COPYRIGHT_CLEARED);
		add(AU_GENBUS);
		add(NZ_COPYRIGHT_ACT_S54);
		add(NZ_COPYRIGHT_ACT_S55);
		add(OTHER);
		add(UK_COPYR_FEE_PAID);
		add(UK_FAIR_DEALING);
		add(US_CCG);
		add(US_CCL);
	}

	public CopyrightCompliance() {
	}

	public CopyrightCompliance(String copyrightCompliance) {
		super(copyrightCompliance);
	}
}
