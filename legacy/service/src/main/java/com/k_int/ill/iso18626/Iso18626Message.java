package com.k_int.ill.iso18626;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JacksonXmlRootElement(localName = "ISO18626Message")
public class Iso18626Message {

	public static final String VERSION_2014_1 = "1_1_2014";
	public static final String VERSION_2017_1 = "1_2_2017";
	public static final String VERSION_2021_1 = "2021-1";
	public static final String VERSION_2021_2 = "2021-2";

	private static final String XSD_VERSION_2014_1 = "v1_1";
	private static final String XSD_VERSION_2017_1 = "v1_2";
	private static final String XSD_VERSION_2021_1 = "2021-1";
	private static final String XSD_VERSION_2021_2 = "2021-2";

	private static final Map<String, String> versionSchema = new HashMap<String, String>();
	
	private static final String SCHEMA_PREFIX  = "http://illtransactions.org/2013/iso18626 http://illtransactions.org/schemas/ISO-18626-";
	private static final String SCHEMA_POSTFIX = ".xsd";

	static {
		// Populate the version to schema map
		versionSchema.put(VERSION_2014_1, XSD_VERSION_2014_1);
		versionSchema.put(VERSION_2017_1, XSD_VERSION_2017_1);
		versionSchema.put(VERSION_2021_1, XSD_VERSION_2021_1);
		versionSchema.put(VERSION_2021_2, XSD_VERSION_2021_2);
	}
	
	@JacksonXmlProperty(isAttribute = true, localName = "ill:version")
	public String version = "notSet";

	// To get the deserializer for xml to output the version this is what needed to do
	@JsonProperty("version")
	public void setVersion(String version) {
		this.version = version;
	}

	@JacksonXmlProperty(isAttribute = true, localName = "xmlns")
	public String xmlns = "http://illtransactions.org/2013/iso18626";

	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:ill")
	public String xmlnsIll = "http://illtransactions.org/2013/iso18626";

	@JacksonXmlProperty(isAttribute = true, localName = "xmlns:xsi")
	public String xmlnsXsi = "http://www.w3.org/2001/XMLSchema-instance";

	@JacksonXmlProperty(isAttribute = true, localName = "xsi:schemaLocation")
	public String xsiSchemaLocation = "notSet";

	// To get the deserializer for xml to output the schemaLocation this is what needed to do
	@JsonProperty("schemaLocation")
	public void setSchemaLocation(String schemaLocation) {
		this.xsiSchemaLocation = schemaLocation;
	}

    // The messages that can be sent
	public Request request;
	public RequestingAgencyMessage requestingAgencyMessage;
	public SupplyingAgencyMessage supplyingAgencyMessage;

    // The messages confirmations that can be responded with
    public RequestConfirmation requestConfirmation;
    public RequestingAgencyMessageConfirmation requestingAgencyMessageConfirmation;
    public SupplyingAgencyMessageConfirmation supplyingAgencyMessageConfirmation;

	public Iso18626Message() {
	}

	public Iso18626Message(Request request) {
		this.request = request;
	}

	public Iso18626Message(RequestingAgencyMessage requestingAgencyMessage) {
		this.requestingAgencyMessage = requestingAgencyMessage;
	}

	public Iso18626Message(SupplyingAgencyMessage supplyingAgencyMessage) {
		this.supplyingAgencyMessage = supplyingAgencyMessage;
	}

    public Iso18626Message(RequestConfirmation requestConfirmation) {
        this.requestConfirmation = requestConfirmation;
    }

    public Iso18626Message(RequestingAgencyMessageConfirmation requestingAgencyMessageConfirmation) {
        this.requestingAgencyMessageConfirmation = requestingAgencyMessageConfirmation;
    }

    public Iso18626Message(SupplyingAgencyMessageConfirmation supplyingAgencyMessageConfirmation) {
        this.supplyingAgencyMessageConfirmation = supplyingAgencyMessageConfirmation;
    }

	/**
	 * Sets the version and schema to the one we want to use
	 * @param protocolVersion The version of the protocol we want this object to represent
	 */
	public void updateVersionInUse(String protocolVersion) {

		// Set the version correctly
		this.version = protocolVersion;

		// Set it to the correct schema
		this.xsiSchemaLocation = genarateSchemaName(protocolVersion);
	}

    /**
     * Determines the correct name for the schema based on the version of the protocol we are using 
     * @param protocolVersion The version of the protocol we are using
     * @return The name of the schema, if the version is unknown we use the 2017 version of the protocol
     */
    private String genarateSchemaName(String protocolVersion) {
    	String schemaVersion = versionSchema.getOrDefault(protocolVersion, XSD_VERSION_2017_1);
    	return(SCHEMA_PREFIX + schemaVersion + SCHEMA_POSTFIX);
    }
}
