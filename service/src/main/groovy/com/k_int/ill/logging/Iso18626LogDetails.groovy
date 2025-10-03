package com.k_int.ill.logging;

import com.k_int.ill.ProtocolMethod;
import com.k_int.ill.ProtocolType;

/**
 * Records the details of an iso18626 message
 * @author Chas
 *
 */
public class Iso18626LogDetails extends BaseAuditDetails implements IIso18626LogDetails {

    public Iso18626LogDetails() {
        this.protocolType = ProtocolType.ISO18626;
        this.protocolMethod = ProtocolMethod.POST;
    }

    @Override
    public void request(String requestEndpoint, String requestBody) {
        url = requestEndpoint;
        this.requestBody = requestBody;
    }

    @Override
    public void response(String responseStatus, String responseBody) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }
}
