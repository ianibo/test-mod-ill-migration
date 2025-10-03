package com.k_int.ill.patronRequest;

import org.springframework.web.multipart.MultipartFile;

import com.k_int.GenericResult;
import com.k_int.ill.PatronRequest;
import com.k_int.ill.PatronRequestDocument;
import com.k_int.ill.PatronRequestDocumentAudit;
import com.k_int.ill.constants.ErrorCodes;
import com.k_int.ill.files.FileDefinitionCreateResult;
import com.k_int.ill.files.FileFetchResult;
import com.k_int.ill.files.FileService;
import com.k_int.ill.files.FileType;

/**
 * Deals with all things to do with a document associated with a patron request
 * @author Chas
 */
public class PatronRequestDocumentService {

	PatronRequestCopyrightService patronRequestCopyrightService;

	FileService fileService;

	/**
	 * Associates a document with the patron request
	 * @param patronRequest The request to associate the document with
	 * @param file the file to be associated with the request
	 * @param description a brief description of the file
	 * @param result The result of trying to associate the file with the request
	 */
	public void add(
		PatronRequest patronRequest,
		MultipartFile file,
		String description,
		GenericResult result
	) {
		// Were we supplied a request
		if (patronRequest == null) {
			result.error("No request supplied to add the document to", ErrorCodes.ERROR_NO_PATRON_REQUEST);
		} else {
			// Let us attempt to create the file
			FileDefinitionCreateResult fileDefinitionCreateResult = fileService.create(
				patronRequest.institution,
				FileType.REQUESTED_DOCUMENT,
				description,
				file
			);
	
			// Were we successful
			if (fileDefinitionCreateResult.error == null) {
				// We were, so let us associate it with the request
				PatronRequestDocument patronRequestDocument = new PatronRequestDocument();
				patronRequestDocument.fileDefinition = fileDefinitionCreateResult.fileDefinition;
	
				// Determine the next position
				patronRequestDocument.position = 0;
				if ((patronRequest.documents != null) && !patronRequest.documents.isEmpty()) {
					// We have existing documents so calculate the next position
					patronRequest.documents.each { PatronRequestDocument document ->
						// Is the current documents position is greater than or equal to the new position 
						if (document.position >= patronRequestDocument.position) {
							// It is, so we need to reset the position of our new document
							patronRequestDocument.position = document.position + 1; 
						}
					}
				}
	
				// We can now add the document to the 	request and save it
				patronRequest.addToDocuments(patronRequestDocument);
				patronRequest.save(flush:true, failOnError:true);
	
				// Set the id on the result to the id of the document
				result.id = patronRequestDocument.id;
			} else {
				result.error("Failed to save file, error: " + fileDefinitionCreateResult.error);
			}
		}
	}

	/**
	 * Download the document associate with the patron request
	 * @param patronRequest The patron request the document is associated with 
	 * @param position If there are multiple documents, the specific document to grab
	 * @param result The if we failed to obtain the document this will hold the reason why
	 * @return If we were successful the object containing the document as a stream
	 */
	public FileFetchResult download(
		PatronRequest patronRequest,
		int position,
		GenericResult result
	) {
		FileFetchResult fileFetchResult = null;

		if (patronRequest == null) {
			result.error("No request supplied to add the document to", ErrorCodes.ERROR_NO_PATRON_REQUEST);
		} else {
			// Only carry on if copyright agreed
			if (patronRequestCopyrightService.isCopyrightAgreed(patronRequest)) {
				// Lookup the document associated with the request
				log.debug("Looking up document for patron request "+ patronRequest.id + " with position " + position);
				PatronRequestDocument patronRequestDocument = PatronRequestDocument.findByPatronRequestAndPosition(
					patronRequest,
					position
				);
	
				// Did we find the document
				if (patronRequestDocument == null) {
					// We did not
					result.error(
						"Unable to locate the document associated with the request",
						ErrorCodes.ERROR_DOCUMENT_NOT_FOUND
					);
				} else {
					// We did, Create ourselves an audit recprd
					PatronRequestDocumentAudit patronRequestDocumentAudit = new PatronRequestDocumentAudit();
					patronRequestDocument.addToAudit(patronRequestDocumentAudit);
					
					// Now we can fetch it
					fileFetchResult = fileService.fetch(patronRequest.institution, patronRequestDocument.fileDefinition);
					if (fileFetchResult.inputStream == null) {
						// We had an error, so record that and reset fileFetchResult to null
						result.error(fileFetchResult.error, ErrorCodes.ERROR_DOCUMENT_FETCHING);
						patronRequestDocumentAudit.message = fileFetchResult.error; 
						fileFetchResult = null;
					}
	
					// Save the audit record
					patronRequestDocument.save(flush : true, failOnError:true);
				}
			} else {
				result.error(
					"In order to view the document, you need to agree to the copyright statement",
					ErrorCodes.ERROR_DOCUMENT_COPYRIGHT_NOT_AGREED
				);
			}
		}

		return(fileFetchResult);
	}
}
