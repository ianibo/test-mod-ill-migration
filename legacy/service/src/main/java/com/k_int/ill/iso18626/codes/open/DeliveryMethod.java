package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class DeliveryMethod extends Code {
	// The initial valid sent via codes
	public static final String ARTICLE_EXCHANGE = "ArticleExchange";
	public static final String COURIER          = "Courier";
	public static final String EMAIL            = "Email";
	public static final String FTP              = "FTP";
	public static final String MAIL             = "Mail";
	public static final String ODYSSEY          = "Odyssey";
	public static final String URL              = "URL";

	static {
		add(ARTICLE_EXCHANGE);
		add(COURIER);
		add(EMAIL);
		add(FTP);
		add(MAIL);
		add(ODYSSEY);
		add(URL);
	}

	public DeliveryMethod() {
	};

	public DeliveryMethod(String deliveryMethod) {
		super(deliveryMethod);
	};
}
