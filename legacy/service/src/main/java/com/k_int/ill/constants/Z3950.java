package com.k_int.ill.constants;

/**
 * Class that contains constants used by Z3950
 * @author Chas
 *
 */
public class Z3950 {

  	public static final String RECORD_SYNTAX_DEFAULT  = null;
  	public static final String RECORD_SYNTAX_MARC_XML = "marcxml";

	// The attribute prefix
	public static final String ATTRIBUTE_PREFIX = "@attr ";

	// The operators
	public static final String OPERATOR_AND = "@and";
	public static final String OPERATOR_NOT = "@or";
	public static final String OPERATOR_OR  = "@not";

	// Attribute values
	public static final int ATTRIBUTE_USE          = 1;
	public static final int ATTRIBUTE_RELATION     = 2;
	public static final int ATTRIBUTE_POSITION     = 3;
	public static final int ATTRIBUTE_STRUCTURE    = 4;
	public static final int ATTRIBUTE_TRUNCATION   = 5;
	public static final int ATTRIBUTE_COMPLETENESS = 6;
	
	// See https://www.loc.gov/z3950/agency/bib1.html for the definition of the attributes
	public static final int USE_ANY               = 1016;
	public static final int USE_AUTHOR            = 1003;
	public static final int USE_IDENTIFIER        = 1007;
	public static final int USE_ISBN              = 7;
	public static final int USE_ISSN              = 8;
	public static final int USE_LOCAL_NUMBER      = 12;
	public static final int USE_TITLE             = 4;

	// specific to horizon	
	public static final int USE_HORIZON_UNIQUE_ID = 100;
	
	public static final int RELATION_LESS_THAN             = 1;
	public static final int RELATION_LESS_THAN_OR_EQUAL    = 2;
	public static final int RELATION_EQUAL                 = 3;
	public static final int RELATION_GREATER_THAN_OR_EQUAL = 4;
	public static final int RELATION_GREATER_THAN          = 5;
	public static final int RELATION_NOT_EQUAL             = 6;
	
	public static final int POSITION_ANY_POSITION_IN_FIELD = 3;
	public static final int POSITION_FIRST_IN_FIELD        = 1;
	public static final int POSITION_FIRST_IN_SUBFIELD     = 2;
	
	public static final int STRUCTURE_DATE      = 5;
	public static final int STRUCTURE_KEY       = 3;
	public static final int STRUCTURE_PHRASE    = 1;
	public static final int STRUCTURE_WORD      = 2;
	public static final int STRUCTURE_WORD_LIST = 6;
	public static final int STRUCTURE_YEAR      = 4;

	public static final int TRUNCATION_LEFT           = 2;
	public static final int TRUNCATION_LEFT_AND_RIGHT = 3;
	public static final int TRUNCATION_RIGHT          = 1;

	public static final int COMPLETENESS_COMPLETE_FIELD      = 3;
	public static final int COMPLETENESS_COMPLETE_SUBFIELD   = 2;
	public static final int COMPLETENESS_INCOMPLETE_SUBFIELD = 1;
}
