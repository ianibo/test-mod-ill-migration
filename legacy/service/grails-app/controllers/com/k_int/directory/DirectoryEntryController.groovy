package com.k_int.directory;

import org.grails.web.json.JSONArray;

import com.k_int.CanEdit;
import com.k_int.OkapiTenantAwareSwaggerController;
import com.k_int.okapi.OkapiHeaders;
import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.PermissionGroup;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.gorm.multitenancy.CurrentTenant;
import groovy.json.JsonOutput;
import groovy.util.logging.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 * Access to InternalContact resources
 */
@Slf4j
@CurrentTenant
@Api(value = "/ill/directory/entry", tags = ["Directory Entry"])
@OkapiApi(name = "directoryEntry")
@ExcludeFromGeneratedCoverageReport
public class DirectoryEntryController extends OkapiTenantAwareSwaggerController<DirectoryEntry>  {

    DirectoryEntryService directoryEntryService;

    public DirectoryEntryController() {
        super(DirectoryEntry)
    }

    @ApiOperation(
        value = "Search with the supplied criteria",
        nickname = "/",
        produces = "application/json",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "term",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The term to be searched for",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "match",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties the match is to be applied to",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "filters",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The filters to be applied",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "sort",
            paramType = "query",
            required = false,
            allowMultiple = true,
            value = "The properties to sort the items by",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "max",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Maximum number of items to return",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "perPage",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Number of items per page",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "offset",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Offset from the becoming of the result set to start returning results",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "page",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "The page you wnat the results being returned from",
            dataType = "integer"
        ),
        @ApiImplicitParam(
            name = "stats",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we return statistics about the search",
            dataType = "boolean"
        ),
        @ApiImplicitParam(
            name = "full",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we want the full details being output",
            defaultValue = "false",
            dataType = "boolean"
        )
    ])
    @OkapiPermission(name = "index", permissionGroup = PermissionGroup.READ)
    public def index(Integer max) {
		setToReturnFullDetails();

        // Just call the base class
        super.index(max);
    }

    @ApiOperation(
        value = "Returns the supplied record",
        nickname = "{id}",
        httpMethod = "GET"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The id of the record to return",
            dataType = "string"
        ),
        @ApiImplicitParam(
            name = "full",
            paramType = "query",
            required = false,
            allowMultiple = false,
            value = "Do we want the full details being output",
            defaultValue = "false",
            dataType = "boolean"
        )
    ])
    @OkapiPermission(name = "show", permissionGroup = PermissionGroup.READ)
    public def show() {
		setToReturnFullDetails();

        // Just call the base class
        super.show();
    }

	@Override
    @ApiOperation(
        value = "Creates a new record with the supplied data",
        nickname = "/",
        httpMethod = "POST"
    )
    @ApiResponses([
        @ApiResponse(code = 201, message = "Created")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The json record that is going to be used for creation",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "item", permissionGroup = PermissionGroup.WRITE)
	public def save() {
		log.debug("Overridden DirectoryEntryController::save() - called when there is a post on a directory entry resource");
		setToReturnFullDetails();

		// Here is one way to get hold of the permissions
		String okapi_permissions_str = request.getHeader(OkapiHeaders.PERMISSIONS) ?: '[]';
		log.debug("Permissions for this request are: ${okapi_permissions_str}");

		// But the superclass should be parsing that and surfacing the permissions so that
		// request.isUserInRole("okapi.directory.entry.item.update")
		// N.B. 1 The okapi. prefix which distinguishes OKAPI permissions from other spring security perms
		// N.B. 2 You can also use the @Secured({"okapi.a.b.c"}) at the method level but the conditional nature of
		// the requirement might mean it's cleaner to do this in the body of the method.

		// Do what the superclass would have done anyway
		super.save()
	}

	@Override
    @ApiOperation(
        value = "Updates the record with the supplied data",
        nickname = "{id}",
        produces = "application/json",
        httpMethod = "PUT"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            name = "id",
            paramType = "path",
            required = true,
            allowMultiple = false,
            value = "The id of the record to be updated",
            dataType = "string"
        ),
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The json record that we are going to update the current record with",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "item", permissionGroup = PermissionGroup.WRITE)
	public def update() {
		log.debug("Overridden DirectoryEntryController::update() - called when there is a put on a directory entry resource");
		setToReturnFullDetails();

		if (request.JSON == null) {
			render(status : 400, contentType: "application/json", text : JsonOutput.toJson([error : 400, message : "No json supplied to updatedirectory entry" ]));
		} else {
			// Check the symbols before we do anything else
			String symbolError = checkSymbols(request.JSON.symbols);
			if (symbolError != null) {
				render(status : 400, contentType: "application/json", text : JsonOutput.toJson([error : 400, message :symbolError]));
			} else {
		        // Check if we can edit this directory entry
		        DirectoryEntry directoryEntry = DirectoryEntry.get(params.id);
		        if (directoryEntryService.canEdit(directoryEntry, getInstitution()) == CanEdit.No) {
		            // Not a directory entry managed by this institution
		            render(status : 400, contentType: "application/json", text : JsonOutput.toJson([error : 400, message : "Cannot edit directory entry: " + directoryEntry.id ]));
		        } else {
					// Check the addresses to avoid duplicate labels for a directory entry
					checkAddresses(params.id);

					// We can edit the directory entry
					request.JSON.pubLastUpdate = System.currentTimeMillis();
		    		super.update();
		        }
			}
		}
	}

	private void checkAddresses(String id) {
		// Do we have any addresses
		if (request.JSON.get("addresses", null) != null) {
			request.JSON.get("addresses").each{ address ->
				// Does the address already have an id
				if (address.get("id", null) == null) {
					// It does not, so look to see if we already have an address with this id
					String addressLabel = address.getAt("addressLabel");
					if (addressLabel != null) {
						// Does the address label already exist
						DirectoryEntry directoryEntry = DirectoryEntry.get(id);
						Address existingAddress = Address.findByAddressLabelAndOwner(addressLabel, directoryEntry);
						
						// Does an address with this label already exist
						if (existingAddress != null) {
							// It does, so assign the id to it
							address.put("id", existingAddress.id);
							
							// We need to map the lines as well if they do not have an id
							if (address.get("lines", null) != null) {
								address.get("lines").each{ addressLine ->
									// If the address line dosn't have an id, then we will need to look to set it
									if (addressLine.get("id", null) == null) {
										// We match them by the type
										if (addressLine.get("type", null) != null) {
											def addressLineType = addressLine.get("type");

											// The value on the type is what we use to compare											
											if (addressLineType.get("value", null) != null) {
												// Now get hold of the value 
												String addressLineTypeValue = addressLineType.get("value");

												// Does this line already exist												
												AddressLine existingAddressLine = existingAddress.lines.find { AddressLine line ->
													return(line.type.value == addressLineTypeValue);
												}

												// It does so we need to set the lines id on the incoming json 												
												if (existingAddressLine != null) {
													addressLine.put("id", existingAddressLine.id);
												}
											}
										}
									}
								}
							}
						}
					}
				} 
			}
		}
	}
	
	private boolean isNotLocalProperty(propName, entry) {
		for(cpValue in entry.customProperties.value) {
			if(!propName.equals(cpValue.definition.name)) {
				log.debug("${propName} not equal to ${cpValue.definition.name}")
				continue;
			}
			log.debug("defaultInternal for ${propName} is ${cpValue.definition.defaultInternal}")
			if(cpValue.definition.defaultInternal == false) {
				return true;
			}
		}
		return false;
	}

	private void setToReturnFullDetails() {
		// We want to return the full details if not set
		if (params.full == null) {
			// This forces the view to return the full details
			params.full = "true";
		}
	}

	private String checkSymbols(def symbols) {

		// A null result means no errors
		String result = null;

		// Loop through all the symbols
		if (symbols != null) {
			// The symbols should be an array
			if (symbols instanceof JSONArray) {
				symbols.each { symbol ->
					Symbol exists = null;

					// Need to lookup the naming authority first in order to use it in findBy
					NamingAuthority authority = NamingAuthority.get(symbol.authority?.id);

					// if we are already have an Id we need to exclude records with the current id
					if (symbol.id) {
						// We have an id, so exclude
						exists = Symbol.findBySymbolAndAuthorityAndIdNotEqual(symbol.symbol, authority, symbol.id);
					} else {
						// No id
						exists = Symbol.findBySymbolAndAuthority(symbol.symbol.toUpperCase(), authority);
					}

					// if we are already have an Id we need to exclude records with the current id
					if (exists != null) {
						// Symbol already exists, so return an appropriate error message
						if (result == null) {
							result = "";
						}

						// Now give a sensible error
						result += "The symbol " + exists.symbol + " for authority " + exists.authority.symbol + " already exists for " + exists.owner.name + ". ";
					}
				}
			} else {
				result = "Symbols not supplied as an array";
			}
		}

		// Return the result to the caller
		return(result);
	}

	/* This action will be used to provide real time validation information
	 * which the client can then act on as it wishes.
     * For example, if the user is attempting to create a root consortium entry
     * when one already exists in the system, this method will return a warning.
     *
     * This could be extended in future to warn when a user is attempting to
     * set up a consortium as a unit, or include logic to detect possible duplications and warn
     * about those, etc.
	 */
    @ApiOperation(
        value = "Validates the supplied record",
        nickname = "validate",
        produces = "application/json",
        httpMethod = "POST"
    )
    @ApiResponses([
        @ApiResponse(code = 200, message = "Success")
    ])
    @ApiImplicitParams([
        @ApiImplicitParam(
            paramType = "body",
            required = true,
            allowMultiple = false,
            value = "The json record that is going to be validated",
            defaultValue = "{}",
            dataType = "string"
        )
    ])
    @OkapiPermission(name = "validate", permissionGroup = PermissionGroup.WRITE)
	public def validate() {
		// Store list of errors/warnings for this directoryEntry
		def returnMap = [
			errors: [],
			warnings: []
		]

		def directoryEntry = getObjectToBind();
		// Translate 'type' from id to human readable value
		def typeString
		if (directoryEntry.type) {
			typeString = RefdataValue.read(directoryEntry.type)?.value
		}

		switch (typeString) {
			case 'consortium':
				def typeCount = DirectoryEntry.executeQuery(
"""
SELECT COUNT(dirEnt) from DirectoryEntry dirEnt
WHERE dirEnt.type.value = 'consortium'
""".toString()
				);
				// If a consortium already exists, warn the user
				if (!directoryEntry.parent && typeCount[0] > 0) {
					returnMap.warnings << "consortiumAlreadyExists"
				}
				break;

			case 'institution':
				def typeCount = DirectoryEntry.executeQuery(
						"""
SELECT COUNT(dirEnt) from DirectoryEntry dirEnt
WHERE dirEnt.status.value = 'managed' AND
      dirEnt.type.value = 'institution'
""".toString()
				);
				// If a managed institution already exists, warn the user
				if (!directoryEntry.parent && typeCount[0] > 0) {
					returnMap.warnings << "managedInstitutionAlreadyExists"
				}
				break;

			case 'branch':
				break;

			default:
				break;
		}

		respond returnMap;
	}


    @Override
    protected void loadedRecord(DirectoryEntry directoryEntry) {
        // We need to set the canEdit property
        directoryEntryService.setCanEdit(directoryEntry, getInstitution());
    }

    @Override
    protected void loadedRecords(List<DirectoryEntry> directoryEntryRecords) {
        // We need to set the canEdit property
        directoryEntryService.setCanEdit(directoryEntryRecords, getInstitution());
    }
}
