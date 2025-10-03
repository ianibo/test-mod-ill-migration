package com.k_int.ill.protocols.iso18626;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.k_int.ill.iso18626.Iso18626Message;

/**
 * Useful methods for the iso18626 protocol that are independent of the version
 */
public class Iso18626Service {

	Iso18626_2017MessageService iso18626_2017MessageService;
	Iso18626_2021MessageService iso18626_2021MessageService;

	/**
	 * Given an Iso18626Message determine the correct message service to use
	 * @param iso18626Message the message that was supplied
	 * @return the Iso18626Message Service to be used with this message or null if a message was not supplied
	 */
	Iso18626MessageService getMessageService(Iso18626Message iso18626Message) {
		Iso18626MessageService iso18626MessageService = null;

		// Have we been passed an iso18626 message		
		if (iso18626Message != null) {
			// If we are unable to determine the version we default to 2017
			iso18626MessageService = iso18626_2017MessageService;

			// Have we been supplied a version			
			if (iso18626Message.version != null) {
				// So determine the correct message service to pass back
				switch (iso18626Message.version) {
					case Iso18626Message.VERSION_2017_1:
						iso18626MessageService = iso18626_2017MessageService;
						break;
						
					case Iso18626Message.VERSION_2021_1:
					case Iso18626Message.VERSION_2021_2:
						iso18626MessageService = iso18626_2021MessageService;
						break;
				}
			}
		}

		// Return the determined message service to the caller
		return(iso18626MessageService);
	}

	/**
	 * Converts an Iso18626Message to xml returning it as a string
	 * @param iso18626Message the message to be converted
	 * @return The supplied message as a string version of xml
	 */
	public String toXml(Iso18626Message iso18626Message) {
		String xml = null;

		// Do we have a message
		if (iso18626Message != null) {
			// We have an iso18626 so try and create an xml message
			try {
				// We have a message, so we need to generate the xml as a string
				XmlMapper xmlMapper = new XmlMapper();
				xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				xml = xmlMapper.writeValueAsString(iso18626Message);
			} catch (Exception e) {
				log.error("Exception thrown while building iso18626 message", e);
			}
		}

		// Return the xml to the caller
		return(xml);
	}

	/**
	 * Converts an incoming xml string to an Iso18626Message object
	 * @param xml the xml message that needs converting
	 * @return an instance of Iso18626Message or null if there was a problem converting it
	 */
	public Iso18626Message fromXml(String xml) {
		Iso18626Message iso18626Message = null;

		// Do we have a message
		if (xml != null) {
			// We have a xml string representing an iso18626 message so try and get an instance of the class for it
			try {
				// We have a message, so we need to generate the class instance
				XmlMapper xmlMapper = new XmlMapper();
				xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				iso18626Message = xmlMapper.readValue(xml, Iso18626Message.class);
			} catch (Exception e) {
				log.error("Exception thrown while converting xml to an iso18626 instance", e);
			}
		}

		// Return the instant to the caller
		return(iso18626Message);
	}

	/**
	 * Converts an Iso18626Message object to json
	 * @param iso18626Message the instance to be converted
	 * @return the message converted to json as a string or null if there was an error
	 */
	public String toJson(Iso18626Message iso18626Message) {
		String json = null;

		// Do we have a message
		if (iso18626Message != null) {
			// We have an iso18626 so try and create an xml message
			try {
				// We have a message, so we need to generate the json as a string
				JsonMapper jsonMapper = new JsonMapper().enable(SerializationFeature.INDENT_OUTPUT);
				jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				json = jsonMapper.writeValueAsString(iso18626Message);
			} catch (Exception e) {
				log.error("Exception thrown while converting iso18626 message to json", e);
			}
		}

		// Return the json to the caller
		return(json);
	}

	/**
	 * Converts a json representation of an to an Iso18626Message object
	 * @param json the message that needs converting
	 * @return an instance of an Iso18626Message object or null if there was an error 
	 */
	public Iso18626Message fromJson(String json) {
		Iso18626Message iso18626Message = null;

		// Do we have a message
		if (json != null) {
			// We have a json string representing an iso18626 message so try and get an instance of the class for it
			try {
				// We have a message, so we need to generate the class instance
				JsonMapper jsonMapper = new JsonMapper();
				jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				iso18626Message = jsonMapper.readValue(json, Iso18626Message.class);
			} catch (Exception e) {
				log.error("Exception thrown while converting json to an iso18626 instance", e);
			}
		}

		// Return the instant to the caller
		return(iso18626Message);
	}
}
