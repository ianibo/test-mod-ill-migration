package com.k_int.ModuleDescriptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.k_int.permissions.OkapiApi;
import com.k_int.permissions.OkapiPermission;
import com.k_int.permissions.Permission;
import com.k_int.permissions.PermissionGroup;
import com.k_int.permissions.UrlHandler;
import com.k_int.swagger.SwaggerApiService;

import groovy.json.JsonSlurper
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Generates the module descriptor from the controllers
 */
public class ModuleDescriptorService {

    private static final Map defaultModuleDescriptor = [ : ];

    def grailsApplication;
    SwaggerApiService swaggerApiService;

    public Map generate(String module, boolean validate = false, String groupDisplayName = null, String groupDescription = null) {

        Map moduleDescriptor = defaultModuleDescriptor;

        // Attempt to read the template descriptor from the resoures
        try {
            InputStream stream = this.class.classLoader.getResourceAsStream('moduleDescriptor/ModuleDescriptor-template.json');
            if (stream != null) {
                String moduleDescriptorTemplate = stream.text;
                moduleDescriptor = new JsonSlurper().parseText(moduleDescriptorTemplate);
            }
        } catch(Exception e) {
            log.error("Failed to read or interpret file moduleDescriptor/ModuleDescriptor-template.json from the resources, reverting to default for ill", e);
        }

        // Now modify the moduleDescriptor so that it will hold the url handlers and permissions
        ArrayList urlHandlers = [ ];
        Map permissionSets = [ : ];
        if (moduleDescriptor.provides == null) {
            moduleDescriptor.provides = [ ];
            moduleDescriptor.provides.add(
                [
                    id : "ill",
                    version : '''${info.app.minorVersion}'''
                ]
            );
        }

        // Grab the appropriate section from the provides
        Map moduleProvides =  moduleDescriptor.provides.find{ Map provides -> provides.id == module };

        // If we are validating save the existing handlers
        if (validate) {
            moduleDescriptor.savedUrlHandlers = moduleProvides.handlers;
        }

        // Create the all permission group for the module
        Permission allModulePermissions = createPermissionGroup(
            permissionSets,
            module,
            null,
            PermissionGroup.ALL,
            groupDisplayName,
            groupDescription,
            true
        );

        // Create the system permission group for the module
        Permission systemModulePermissions = createPermissionGroup(
            permissionSets,
            module,
            "system",
            PermissionGroup.ALL,
            groupDisplayName,
            groupDescription,
            true
        );

        // The annotations we will be looking for
        Class annotationSwaggerApiClass = Api.class;
        Class annotationOkapiApiClass = OkapiApi.class;
        Class annotationOkapiPermissionClass = OkapiPermission.class;
        Class annotationSwaggerOperationClass = ApiOperation.class;

        // Loop through all the controller classes
        grailsApplication.controllerClasses.each { controllerArtefact ->
            Class controllerClass = controllerArtefact.getClazz();

            // Does the class have the Okapi Api  and Swagger API annotations, if not we will ignore it
            Api annotationSwaggerApi = controllerClass.getAnnotation(annotationSwaggerApiClass);
            OkapiApi annotationOkapiApi = controllerClass.getAnnotation(annotationOkapiApiClass);
            if (annotationOkapiApi && annotationSwaggerApi) {
                // Determine the all permissions we add it to
                Permission allPermissions = annotationOkapiApi.isSystem() ? systemModulePermissions : allModulePermissions;

                // Grab hold of the api name from the Okapi Api annotation
                String apiName = annotationOkapiApi.name();

                // And the base path from the swagger api annotation
                String apiBasePath = annotationSwaggerApi.value();

                // If we have an api name then we can continue
                if (apiName) {
                    // Create the 3 permission sub grous for this Api
                    Permission allApiPermissions = createPermissionGroup(
                        permissionSets,
                        module,
                        apiName,
                        PermissionGroup.ALL,
                        annotationOkapiApi.displayNameGroupAll(),
                        annotationOkapiApi.descriptionGroupAll(),
                        annotationOkapiApi.isVisibleGroupAll()
                    );

                    // Add this to the all allModulePermissions
                    if (!allPermissions.subPermissions.contains(allApiPermissions.permissionName)) {
                        allPermissions.subPermissions.add(allApiPermissions.permissionName);
                    }

                    // Create the write permission group
                    Permission writeApiPermissions = createPermissionGroup(
                        permissionSets,
                        module,
                        apiName,
                        PermissionGroup.WRITE,
                        annotationOkapiApi.displayNameGroupWrite(),
                        annotationOkapiApi.descriptionGroupWrite(),
                        annotationOkapiApi.isVisibleGroupWrite()
                    );

                    // Add it to the all permission group
                    if (!allApiPermissions.subPermissions.contains(writeApiPermissions.permissionName)) {
                        allApiPermissions.subPermissions.add(writeApiPermissions.permissionName);
                    }

                    // Create the read permission group
                    Permission readApiPermissions = createPermissionGroup(
                        permissionSets,
                        module,
                        apiName,
                        PermissionGroup.READ,
                        annotationOkapiApi.displayNameGroupRead(),
                        annotationOkapiApi.descriptionGroupRead(),
                        annotationOkapiApi.isVisibleGroupRead()
                    );

                    // Add it to the write permission group
                    if (!writeApiPermissions.subPermissions.contains(readApiPermissions.permissionName)) {
                        writeApiPermissions.subPermissions.add(readApiPermissions.permissionName);
                    }

                    // Keep track of the path / method combinations so we do not duplicate
                    ArrayList apiPathMethod = [ ];

                    // Now we have the api details and groups lets us deal with the methods
                    for (Method method : controllerClass.getMethods()) {
                        // Only interested in public methods, which in theory getMethods should give us
                        if (Modifier.isPublic(method.getModifiers())) {
                            // Only interested in methods that have a swagger Operation and Okapi Permission annotations
                            ApiOperation annotationSwaggerOperation = method.getAnnotation(annotationSwaggerOperationClass);
                            OkapiPermission annotationOkapiPermission = method.getAnnotation(annotationOkapiPermissionClass);
                            if (annotationSwaggerOperation && annotationOkapiPermission) {
                                // This is a method we are interested in
                                String path = swaggerApiService.buildPath(apiBasePath, annotationSwaggerOperation.nickname());
                                path = path.replaceFirst(/\{.*?\}/, "{id}");
                                String httpMethod = annotationSwaggerOperation.httpMethod().toUpperCase();
                                String apiPathMethodKey = path + "~" + httpMethod;

                                // Have we already had this combination
                                if (!apiPathMethod.contains(apiPathMethodKey)) {
                                    // We have not, so add the key
                                    apiPathMethod.add(apiPathMethodKey);

                                    // Does this method require a permission we need to create
                                    Permission permission = null;
                                    if (annotationOkapiPermission.name()) {
                                        // If the display name is not set on the permission, set it to what is on the operation annotation
                                        String displayName = annotationOkapiPermission.displayName();
                                        if (!displayName) {
                                            // Not set against the permission, so try and take it from the swagger operation
                                            displayName = annotationSwaggerOperation.value();
                                        }
                                        // If the description is not set on the permission, set it to what is on the operation annotation
                                        String description = annotationOkapiPermission.description();
                                        if (!description) {
                                            // Not set against the permission, so try and take it from the swagger operation
                                            description = annotationSwaggerOperation.value();
                                        }

                                        // Create the permission required to access this end point
                                        permission = createPermission(
                                            permissionSets,
                                            module,
                                            apiName,
                                            annotationOkapiPermission.name(),
                                            annotationSwaggerOperation.httpMethod().toLowerCase(),
                                            annotationOkapiPermission.isVisible(),
                                            displayName,
                                            description,
                                            false
                                        );

                                        // Add it to the appropriate group
                                        Permission permissionGroup = null;
                                        switch (annotationOkapiPermission.permissionGroup()) {
                                            case PermissionGroup.READ:
                                                permissionGroup = readApiPermissions;
                                                break;

                                            case PermissionGroup.WRITE:
                                                permissionGroup = writeApiPermissions;
                                                break;

                                            case PermissionGroup.ALL:
                                                permissionGroup = allApiPermissions;
                                                break;
                                        }

                                        // Add the permission to the group if it dosn't already exist
                                        if (permissionGroup && !permissionGroup.subPermissions.contains(permission.permissionName)) {
                                            permissionGroup.subPermissions.add(permission.permissionName);
                                        }
                                    }

                                    // Now we can create the url handler
                                    urlHandlers.add(
                                        new UrlHandler(
                                            annotationSwaggerOperation.httpMethod().toUpperCase(),
                                            path,
                                            permission
                                        )
                                    );
                                }
                            }
                        }
                    }

                    // Sort the sub permissions
                    readApiPermissions.subPermissions = readApiPermissions.subPermissions.sort();
                    writeApiPermissions.subPermissions = writeApiPermissions.subPermissions.sort();
                    allApiPermissions.subPermissions = allApiPermissions.subPermissions.sort();
                }
            }
        }

        // Are we validating
        if (validate) {
            // We are, so compare the old and new
            moduleDescriptor.validation = compareHandlers(moduleDescriptor.savedUrlHandlers, urlHandlers);
        }

        // Sort the all permissions subpermissions
        allModulePermissions.subPermissions = allModulePermissions.subPermissions.sort();

        // Sort the all permissions subpermissions
        systemModulePermissions.subPermissions = systemModulePermissions.subPermissions.sort();

        // Sort the url handlers
        ArrayList sortedUrlHandlers = urlHandlers.sort{ UrlHandler urlHandler ->
            return(urlHandler.pathPattern + ' ' + urlHandler.methods[0]);
        }

        // Sort the permissions
        ArrayList sortedPermissions = permissionSets.values().sort{ Permission permission ->
            return(permission.permissionName);
        }

        // Set the url handlers on the moduleprovides
        moduleProvides.handlers = sortedUrlHandlers;

        // Set the permissions on the module descriptor
        moduleDescriptor.permissionSets = sortedPermissions;

        // Finally return the module descriptor to the caller
        return(moduleDescriptor);
    }

    private Permission createPermission(
        Map permissions,
        String module,
        String apiName,
        String name,
        String method,
        boolean visibility,
        String displayName,
        String description,
        boolean createSubPermissions
    ) {
        Permission permission = null;
        StringBuilder permissionId = new StringBuilder(module);
        if (apiName) {
            permissionId.append('.');
            permissionId.append(apiName.toLowerCase());
        }
        if (name) {
            permissionId.append('.');
            permissionId.append(name.toLowerCase());
        }
        if (method) {
            permissionId.append('.');
            permissionId.append(method);
        }

        // Does the permission already exist
        String id = permissionId.toString();
        permission = permissions[id];
        if (permission == null) {
            StringBuilder permissionName = new StringBuilder(module.capitalize());
            permissionName.append(" - ");
            if (displayName) {
                permissionName.append(displayName);
            } else {
                if (apiName) {
                    permissionName.append(apiName);
                }
                if (name) {
                    permissionName.append(' ');
                    permissionName.append(name);
                }
            }
            String actualName = permissionName.toString();

            String actualDescription = description;
            if (!description) {
                String prefix = method ? method.toLowerCase() : name;
                StringBuilder permissionDescription = new StringBuilder(prefix.capitalize());
                permissionDescription.append(' ');
                permissionDescription.append(module.capitalize());
                if (apiName) {
                    permissionDescription.append(' ');
                    permissionDescription.append(apiName);
                }
                if (name && method) {
                    permissionDescription.append(' ');
                    permissionDescription.append(name);
                }
                actualDescription = permissionDescription.toString();
            }

            // Create the new permission
            permission = new Permission(
                id,
                actualName,
                actualDescription,
                visibility
            );

            // Do we need to create the sub permissions array
            if (createSubPermissions) {
                // For groups we need to allocate the sub permissions array
                permission.subPermissions = [ ];
            }

            // Add the permission to the permissions
            permissions[id] = permission;
        }

        // Return the permission
        return(permission);
    }

    private Permission createPermissionGroup(
        Map permissions,
        String module,
        String apiName,
        PermissionGroup permissionGroup,
        String displayName,
        String description,
        boolean visibility
    ) {
        // For groups we have a different default display name if one has not been supplied
        String overrideDisplayName = displayName;
        if (!overrideDisplayName) {
            switch (permissionGroup) {
                case PermissionGroup.ALL:
                    overrideDisplayName = "All";
                    break;

                case PermissionGroup.WRITE:
                    overrideDisplayName = "Create, Update and Read";
                    break;

                case PermissionGroup.READ:
                    overrideDisplayName = "Read";
                    break;
            }

            // Append permissions to the display name
            overrideDisplayName += " permissions";

            // Append the api name is we have one
            if (apiName) {
                overrideDisplayName += " for the " + apiName + " api";
            }
        }

        // Now create the permission
        Permission permission = createPermission(
            permissions,
            module,
            apiName,
            permissionGroup.toString().toLowerCase(),
            null,
            visibility,
            overrideDisplayName,
            description,
            true
        );

        return(permission);
    }

    private Map compareHandlers(ArrayList oldHandlers, ArrayList newHandlers) {
        // Run through the old handlers, determining what the match process will be
        oldHandlers.forEach { Map oldHandler ->
            if (oldHandler.pathPattern.endsWith('/*')) {
                oldHandler.matchPattern = oldHandler.pathPattern.substring(0, oldHandler.pathPattern.length() - 2);
            } else  if (oldHandler.pathPattern.endsWith('*') || oldHandler.pathPattern.endsWith('/')) {
                oldHandler.matchPattern = oldHandler.pathPattern.substring(0, oldHandler.pathPattern.length() - 1);
            }
        }

        Map result = [ newNotFound : [ ], oldNotFound: [ ]];
        // Loop through the new handlers
        newHandlers.forEach { UrlHandler newHandler ->
            String method = newHandler.methods[0];
            String newPathPattern = newHandler.pathPattern;
            Map oldHandler = oldHandlers.find{ Map existingHandler ->
                boolean pathMatch = existingHandler.methods.contains(method);
                if (pathMatch) {
                    if (existingHandler.matchPattern != null) {
                        pathMatch = newPathPattern.startsWith(existingHandler.matchPattern);
                    } else {
                        pathMatch = (existingHandler.pathPattern == newPathPattern);
                    }
                }
                return(pathMatch);
            }

            // Did we find an old handler
            if (oldHandler) {
                // remove the method we have found
                oldHandler.methods -= method;
            } else {
                // Add the new one to the not found list
                result.newNotFound.add(newHandler);
            }
        }

        // Run through the old handlers, and any that still have a method add to the not found list
        oldHandlers.forEach { Map oldHandler ->
             if (oldHandler.methods) {
                result.oldNotFound.add(oldHandler);
             }
        }
        return(result);
    }
}
