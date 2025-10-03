package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class SentVia extends Code {
	// The initial valid sent via codes
	public static final String ARIEL            = "Ariel";
	public static final String ARTICLE_EXCHANGE = "ArticleExchange";
	public static final String EMAIL            = "Email";
	public static final String FTP              = "FTP";
	public static final String MAIL             = "Mail";
	public static final String ODYSSEY          = "Odyssey";
	public static final String URL              = "URL";

	static {
		add(ARIEL);
		add(ARTICLE_EXCHANGE);
		add(EMAIL);
		add(FTP);
		add(MAIL);
		add(ODYSSEY);
		add(URL);
	}

	public SentVia() {
	};

	public SentVia(String sentVia) {
		super(sentVia);
	};
}
