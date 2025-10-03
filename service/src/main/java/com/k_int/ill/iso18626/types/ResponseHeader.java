package com.k_int.ill.iso18626.types;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.k_int.ill.iso18626.codes.closed.MessageStatus;
import com.k_int.ill.iso18626.complexTypes.AgencyId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseHeader extends Header {

	public String timestampReceived;
	public MessageStatus messageStatus;
	@JacksonXmlElementWrapper(useWrapping=false)
	public List<ErrorData> errorData;

	public ResponseHeader() {
	}
    public ResponseHeader(
        AgencyId supplyingAgencyId,
        AgencyId requestingAgencyId,
        String requestingAgencyRequestId
    ) {
        super(supplyingAgencyId, requestingAgencyId, requestingAgencyRequestId);
        timestampReceived = Instant.now().toString();
    }

    public void result(ErrorData errorData) {
        // Did we have an error
        if (errorData == null) {
            // No we did not
            messageStatus = new MessageStatus(MessageStatus.OK);
        } else {
            // We did
            messageStatus = new MessageStatus(MessageStatus.ERROR);
            addErrorData(errorData);
        }
    }

    public void addErrorData(ErrorData errorData) {
		// Only add it if it is not null or blank
		if (errorData != null) {
			if (this.errorData == null) {
				this.errorData = new ArrayList<ErrorData>();
			}
			this.errorData.add(errorData);
		}
    }
}
