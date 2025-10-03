package com.k_int.ill.iso18626.codes.closed;

import com.k_int.ill.iso18626.codes.Code;

public class RequestSubType extends Code {
	// The valid request sub type codes
	public static final String BOOKING_REQUEST         = "BookingRequest";
	public static final String MULTIPLE_ITEM_REQUEST   = "MultipleItemRequest";
	public static final String PATRON_REQUEST          = "PatronRequest";
	public static final String SUPPLY_LIBRARIES_CHOICE = "SupplyLibrarysChoice";
	public static final String TRANSFER_REQUEST        = "TransferRequest";

	static { 
		add(BOOKING_REQUEST);
		add(MULTIPLE_ITEM_REQUEST);
		add(PATRON_REQUEST);
		add(SUPPLY_LIBRARIES_CHOICE);
		add(TRANSFER_REQUEST);
	};

	public RequestSubType() {
	}

	public RequestSubType(String requestSubType) {
		super(requestSubType);
	}
}
