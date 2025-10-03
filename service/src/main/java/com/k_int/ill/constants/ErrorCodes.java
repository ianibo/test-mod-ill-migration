package com.k_int.ill.constants;

/**
 * Class that contains the constants used by audit logging
 * @author Chas
 *
 */
public class ErrorCodes {

    public static final String PREFIX_SEPARATOR = "_";

    public static final String ID_NOT_SUPPLIED   = "IdNotSupplied";
    public static final String IDS_NOT_SUPPLIED  = "IdsNotSupplied";
    public static final String RECORD_NOT_FOUND  = "RecordNotFound";

    private static final String INSTITUTION_PREFIX = "INSTITUTION" + PREFIX_SEPARATOR;

    public static final String INSTITUTION_INVALID      = INSTITUTION_PREFIX + "Invalid";
    public static final String INSTITUTION_INVALID_USER = INSTITUTION_PREFIX + "InvalidUser";
    public static final String INSTITUTION_NO_GROUP_IDS = INSTITUTION_PREFIX + "NoGroupIds";
    
    public static final String ERROR_DOCUMENT_COPYRIGHT_NOT_AGREED = "DOCUMENT_COPYRIGHT_NOT_AGREED";
    public static final String ERROR_DOCUMENT_NOT_FOUND            = "DOCUMENT_NOT_FOUND";
    public static final String ERROR_DOCUMENT_FETCHING             = "DOCUMENT_FETCHING";
    public static final String ERROR_NO_COPYRIGHT                  = "NO_COPYRIGHT";
    public static final String ERROR_NO_PATRON_REQUEST             = "NO_PATRON_REQUEST";

    private static final String TIMER_PREFIX = "TIMER" + PREFIX_SEPARATOR;

    public static final String TIMER_BEAN_IS_NULL      = TIMER_PREFIX + "BeanIsNull";
    public static final String TIMER_EXCEPTION_IN_BEAN = TIMER_PREFIX + "ExceptionInBean";
    public static final String TIMER_EXCEPTION         = TIMER_PREFIX + "Exception";
    public static final String TIMER_NO_BEAN_FOUND     = TIMER_PREFIX + "NoBeanFound";
    public static final String TIMER_NO_TASK_SET       = TIMER_PREFIX + "NoTaskSet";
    public static final String TIMER_UNABLE_TO_LOCATE  = TIMER_PREFIX + "UnableToLocateTimer";
    public static final String TIMER_UNABLE_TO_LOCK    = TIMER_PREFIX + "UnableToLock";
}
