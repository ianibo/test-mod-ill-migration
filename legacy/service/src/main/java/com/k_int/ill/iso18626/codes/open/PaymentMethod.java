package com.k_int.ill.iso18626.codes.open;

import com.k_int.ill.iso18626.codes.Code;

public class PaymentMethod extends Code {
	// The initial valid payment method codes
	public static final String BANK_TRANSFER = "BankTransfer";
	public static final String CREDIT_CARD = "CreditCard";
	public static final String DEBIT_CARD = "DebitCard";
	public static final String ETFS = "ETFS";
	public static final String IBS = "IBS";
	public static final String IFLA_VOUCHER = "IFLAVoucher";
	public static final String IFM = "IFM";
	public static final String IIBS = "IIBS";
	public static final String LAPS = "LAPS";
	public static final String PAYPAL = "Paypal";

	static {
		add(BANK_TRANSFER);
		add(CREDIT_CARD);
		add(DEBIT_CARD);
		add(ETFS);
		add(IBS);
		add(IFLA_VOUCHER);
		add(IFM);
		add(IIBS);
		add(LAPS);
		add(PAYPAL);
	}

	public PaymentMethod() {
	}

	public PaymentMethod(String paymentMethod) {
		super(paymentMethod);
	}
}
