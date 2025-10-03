package com.k_int.ill.constants;

/**
 * Class that contains the constants used by directory
 * @author Chas
 *
 */
public class Directory {

    public static final String CATEGORY_DIRECTORY_ENTRY_STATUS    = "DirectoryEntry.Status";
    public static final String CATEGORY_DIRECTORY_ENTRY_TYPE      = "DirectoryEntry.Type";
    public static final String CATEGORY_LOAN_POLICY               = "LoanPolicy";
	public static final String CATEGORY_SERVICE_BUSINESS_FUNCTION = "Service.BusinessFunction";
    public static final String CATEGORY_SERVICE_TYPE              = "Service.Type";
    public static final String CATEGORY_YES_NO                    = "YNO";

    public static final String KEY_LOCAL_INSTITUTION_PATRON_ID = "local_institutionalPatronId";
    public static final String KEY_ILL_POLICY_BORROW_RATIO     = "policy.ill.InstitutionalLoanToBorrowRatio";
    public static final String KEY_ILL_POLICY_LAST_RESORT      = "policy.ill.last_resort";
    public static final String KEY_ILL_POLICY_LOAN             = "policy.ill.loan_policy";
    public static final String KEY_ILL_POLICY_RETURNS          = "policy.ill.returns";

    public static final String LOAN_POLICY_LENDING_ALL           = "lending_all_types";
    public static final String LOAN_POLICY_LENDING_PHYSICAL_ONLY = "lending_physical_only";

    public static final String SERVICE_BUSINESS_FUNCTION_CIRC     = "CIRC";
	public static final String SERVICE_BUSINESS_FUNCTION_HARVEST  = "HARVEST";
	public static final String SERVICE_BUSINESS_FUNCTION_ILL      = "ILL";
	public static final String SERVICE_BUSINESS_FUNCTION_RS_STATS = "RS_STATS";
	public static final String SERVICE_BUSINESS_FUNCTION_RTAC     = "RTAC";
	
    public static final String SERVICE_TYPE_GSM_SMTP      = "GSM.SMTP";
	public static final String SERVICE_TYPE_HTTP          = "HTTP";
	public static final String SERVICE_TYPE_ILL_SMTP      = "ILL.SMTP";
	public static final String SERVICE_TYPE_ISO10161_SMTP = "ISO10161.SMTP";
	public static final String SERVICE_TYPE_ISO10161_TCP  = "ISO10161.TCP";
	public static final String SERVICE_TYPE_ISO18626_2017 = "ISO18626-2017";
	public static final String SERVICE_TYPE_ISO18626_2021 = "ISO18626-2021";
	public static final String SERVICE_TYPE_NCIP          = "NCIP";
	public static final String SERVICE_TYPE_OAI_PMH       = "OAI-PMH";
	public static final String SERVICE_TYPE_RTAC          = "RTAC";
	public static final String SERVICE_TYPE_SRU           = "SRU";
	public static final String SERVICE_TYPE_SRW           = "SRW";
	public static final String SERVICE_TYPE_Z3950         = "Z3950";

    public static final String STATUS_LABEL_MANAGED = "Managed";
    public static final String STATUS_VALUE_MANAGED = STATUS_LABEL_MANAGED.toLowerCase();

    public static final String TAG_PICKUP = "pickup";

    public static final String TYPE_LABEL_CONSORTIUM  = "Consortium";
    public static final String TYPE_LABEL_INSTITUTION = "Institution";
    public static final String TYPE_VALUE_CONSORTIUM  = TYPE_LABEL_CONSORTIUM.toLowerCase();
    public static final String TYPE_VALUE_INSTITUTION = TYPE_LABEL_INSTITUTION.toLowerCase();
}
