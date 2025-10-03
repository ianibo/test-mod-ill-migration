package com.k_int.institution;

import com.k_int.RestResult
import com.k_int.TestBase;
import com.k_int.ill.constants.Institution;

public class InstitutionData {
	static private Object institutionA = null;
	static private Object institutionB = null;
	static private Object institutionC = null;

	static private Object institutionGroupA = null;
	static private Object institutionGroupB = null;
	static private Object institutionGroupC = null;

	static private Object institutionUserA = null;
	static private Object institutionUserB = null;
	static private Object institutionUserC = null;

	static private Object directoryEntryA = null;
	static private Object directoryEntryB = null;
	static private Object directoryEntryC = null;
	
	private final TestBase testBase;
	private final String tenantId;
	
	public InstitutionData(String tenantId, TestBase testBase) {
		this.tenantId = tenantId;
		this.testBase = testBase;
	}

	private Object exists(String path, String fieldName, String value) {
		RestResult restResult = testBase.searchForObjects(
			tenantId,
			path,
			fieldName,
			value
		);
		return((restResult.responseBody.size() > 0) ? restResult.responseBody[0] : null);
	}
		
	private Object createInstitution(String name, String description) {
		Object result = exists(TestBase.PATH_INSTITUTION, "name", name);
		if (result == null) {
		    Map institution = [
	           name : name,
	           description : description
	       ];
	
			// Lets us call the base class to post it
			RestResult restResult = testBase.createNewObject(
				tenantId,
				TestBase.PATH_INSTITUTION,
				institution
			);
			
			result = restResult.responseBody;
		}
		return(result);
	}

	private Object createIndtitutionGroup(String name, String description) {
		Object result = exists(TestBase.PATH_INSTITUTION_GROUP, "name", name);
		if (result == null) {
			Map institutionGroup = [
				name : name,
				description : description
			];
		
			// Lets us call the base class to post it
			RestResult restResult = testBase.createNewObject(
				tenantId,
				TestBase.PATH_INSTITUTION_GROUP,
				institutionGroup
			);
			result = restResult.responseBody;
		}
		return(result);
	}
		
	private Object createIndtitutionUser(String name, String folioUserId) {
		Object result = exists(TestBase.PATH_INSTITUTION_USER, "name", name);
		if (result == null) {
	        Map institutionUser = [
	            name : name,
	            folioUserId : folioUserId,
				institutionManaging : Institution.DEFAULT_INSTITUTION
	        ];
	
			// Lets us call the base class to post it
			RestResult restResult = testBase.createNewObject(
				tenantId,
				TestBase.PATH_INSTITUTION_USER,
				institutionUser
			);
			result = restResult.responseBody;
		}
		return(result);
	}
		
	private Object createDirectoryEntry(String name, String description, String type, String parent) {
		Object result = exists(TestBase.PATH_DIRECTORY_ENTRY, "name", name);
		if (result == null) {
			Map directoryEntry = [
				id: java.util.UUID.randomUUID().toString(),
				name: name,
				slug: name,
				description: description,
				status: 'managed',
				type : type,
				parent : parent
			]
	
			// Lets us call the base class to post it
			RestResult restResult = testBase.createNewObject(
				tenantId,
				TestBase.PATH_DIRECTORY_ENTRY,
				directoryEntry,
				null,
				null,
				null,
				null,
				'[ "directory.admin", "directory.user", "directory.own.read", "directory.any.read" ]'
			);
			result = restResult.responseBody;
		}
		return(result);
	}
		
	public Object getInstitutionA() {
		if (institutionA == null) {
			institutionA = createInstitution("testInstitutionA", "Description for institution A"); 
		}
		return(institutionA);
	}

	public Object getInstitutionB() {
		if (institutionB == null) {
			institutionB = createInstitution("testInstitutionB", "Description for institution B"); 
		}
		return(institutionB);
	}

	public Object getInstitutionC() {
		if (institutionC == null) {
			institutionC = createInstitution("testInstitutionC", "Description for institution C"); 
		}
		return(institutionC);
	}

	public Object getInstitutionGroupA() {
		if (institutionGroupA == null) {
			institutionGroupA = createIndtitutionGroup("testInstitutionGroupA", "Description for institution Group A"); 
		}
		return(institutionGroupA);
	}

	public Object getInstitutionGroupB() {
		if (institutionGroupB == null) {
			institutionGroupB = createIndtitutionGroup("testInstitutionGroupB", "Description for institution Group B"); 
		}
		return(institutionGroupB);
	}

	public Object getInstitutionGroupC() {
		if (institutionGroupC == null) {
			institutionGroupC = createIndtitutionGroup("testInstitutionGroupC", "Description for institution Group C"); 
		}
		return(institutionGroupC);
	}

	public Object getInstitutionUserA() {
		if (institutionUserA == null) {
			institutionUserA = createIndtitutionUser("testInstitutionUserA", "00000000-0000-0000-0000-00000000000a"); 
		}
		return(institutionUserA);
	}

	public Object getInstitutionUserB() {
		if (institutionUserB == null) {
			institutionUserB = createIndtitutionUser("testInstitutionUserB", "00000000-0000-0000-0000-00000000000b"); 
		}
		return(institutionUserB);
	}

	public Object getInstitutionUserC() {
		if (institutionUserC == null) {
			institutionUserC = createIndtitutionUser("testInstitutionUserC", "00000000-0000-0000-0000-00000000000c"); 
		}
		return(institutionUserC);
	}

	public Object getDirectoryEntryA() {
		if (directoryEntryA == null) {
			directoryEntryA = createDirectoryEntry("DirectoryEntryA", "Description for directory entry A", "institution", null); 
		}
		return(directoryEntryA);
	}

	public Object getDirectoryEntryB() {
		if (directoryEntryB == null) {
			directoryEntryB = createDirectoryEntry("DirectoryEntryB", "Description for directory entry B", "institution", null); 
		}
		return(directoryEntryB);
	}

	public Object getDirectoryEntryC() {
		if (directoryEntryC == null) {
			directoryEntryC = createDirectoryEntry("DirectoryEntryC", "Description for directory entry C", "branch", directoryEntryA["id"]); 
		}
		return(directoryEntryC);
	}

	public boolean setupTestData() {
		return((getInstitutionA() != null) &&
			   (getInstitutionB() != null) &&
			   (getInstitutionC() != null) &&
			   (getInstitutionGroupA() != null) &&
			   (getInstitutionGroupB() != null) &&
			   (getInstitutionGroupC() != null) &&
			   (getInstitutionUserA() != null) &&
			   (getInstitutionUserB() != null) &&
			   (getInstitutionUserC() != null) &&
			   (getDirectoryEntryA() != null) &&
			   (getDirectoryEntryB() != null) &&
			   (getDirectoryEntryC() != null)
			);
	}
}
