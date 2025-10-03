package com.k_int.ill.referenceData;

import com.k_int.ill.NoticePolicy;
import com.k_int.ill.NoticePolicyNotice;
import com.k_int.ill.PredefinedId;
import com.k_int.ill.templating.LocalizedTemplate;
import com.k_int.ill.templating.Template;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.institution.InstitutionService;
import com.k_int.web.toolkit.refdata.RefdataValue;

import grails.util.Holders;
import groovy.util.logging.Slf4j;

/**
 * Class that reads and creates notice templates
 * @author Chas
 *
 */
@Slf4j
public class TemplateData {

    public static final String VOCABULARY_TEMPLATE_RESOLVER = 'TemplateContainer.TemplateResolver';
    public static final String TEMPLATE_RESOLVER_HANDLEBARS = 'Handlebars';

    private InstitutionService institutionService;

    public static void loadAll() {
		(new TemplateData()).load();
    }

    public void load() {
        log.info('Adding predefined templates to the database');
        institutionService = Holders.grailsApplication.mainContext.getBean('institutionService');

        // The newPatronProfile
        loadTemplate('templates/newPatronProfile.json');

        // A new host LMS location
        loadTemplate('templates/newHostLMSLocation.json');

        // A new host LMS shelving location
        loadTemplate('templates/newHostLMSShelvingLocation.json');

        // A new email request
        loadTemplate('templates/emailRequest.json');
    }

    private void loadTemplate(String resourcePath) {
        URL resource = this.class.classLoader.getResource(resourcePath);
        if (resource == null) {
            log.error('Unable to find resource: ' + resourcePath);
        } else {
            InputStream stream = resource.openStream();
            try {
                Map parsedJson = (new groovy.json.JsonSlurper()).parse(stream);

                // We now have a map in our hand that we will map into a template container
                if (parsedJson.template == null) {
                    // Can't do anything without a template
                    log.error('No template supplied for resource: ' + resourcePath);
                } else {
                    // Excellent start let us see if we already have this template
                    String localizedTemplateId = getReferencedId('localized_template', parsedJson.template.predefinedId);
                    LocalizedTemplate localizedTemplate = ((localizedTemplateId == null) ? null : LocalizedTemplate.get(localizedTemplateId));

                    if (localizedTemplate == null) {
                        // didn't previously exist, so we need to create a new template
                        localizedTemplate = new LocalizedTemplate();
                        localizedTemplate.locality = com.k_int.ill.constants.Template.LOCALITY_ENGLISH;
                    }

                    // Ensure the localized template has a template
                    if (localizedTemplate.template == null) {
                        localizedTemplate.template = new Template();
                    }

                    // Now update the template
                    localizedTemplate.template.header = parsedJson.template.header;
                    localizedTemplate.template.templateBody = parsedJson.template.templateBody;

                    // save the template
                    localizedTemplate.template.save(flush:true, failOnError:true);

                    // Now look to see if the container exists
                    String templateContainerId = getReferencedId('template_container', parsedJson.predefinedId);
                    TemplateContainer templateContainer = ((templateContainerId == null) ? null : TemplateContainer.get(templateContainerId));

                    if (templateContainer == null) {
                        // We need to create a new template container
                        templateContainer = new TemplateContainer();
						String context = parsedJson.context;
                        templateContainer.context = (context ? context : com.k_int.ill.constants.Template.CONTEXT_NOTICE);

                        // This appears to have a default, but dosn't seem to kick in before validation
                        templateContainer.templateResolver = RefdataValue.lookupOrCreate(VOCABULARY_TEMPLATE_RESOLVER, TEMPLATE_RESOLVER_HANDLEBARS);
                    }

                    // Set the name and description
                    templateContainer.name = parsedJson.name;
                    templateContainer.description = parsedJson.description;

                    // Save the template container
                    templateContainer.save(flush:true, failOnError:true);

                    // Create the mapping with the predefined id
                    PredefinedId.ensureExists('template_container', parsedJson.predefinedId, templateContainer.id);

                    // Now add the  localized template
                    if (localizedTemplate.owner == null) {
                        localizedTemplate.owner = templateContainer;
                        templateContainer.addToLocalizedTemplates(localizedTemplate);
                    }

                    // save the container and localized template
                    localizedTemplate.save(flush:true, failOnError:true);
                    templateContainer.save(flush:true, failOnError:true);

                    // Create the mapping with the predefined id
                    PredefinedId.ensureExists('localized_template', parsedJson.template.predefinedId, localizedTemplate.id);

					// Do we need to create a notice policy
					if (parsedJson.triggerType != null) {
	                    // Now we have a template we now need to create a notice policy and notice policy notice records but we only do this once
	                    String noticePolicyId = getReferencedId('notice_policy', parsedJson.predefinedId);
	                    if (noticePolicyId != null) {
	                        // Check it exists
	                        if (NoticePolicy.get(noticePolicyId) == null) {
	                            // Dosn't exists, so reset the policyId back to null
	                            noticePolicyId = null;
	                        }
	                    }
	
	                    // Does the notice policy exist
	                    if (noticePolicyId == null) {
	                        NoticePolicy noticePolicy = new NoticePolicy();
	                        noticePolicy.name = parsedJson.name;
	                        noticePolicy.description = parsedJson.description;
	                        noticePolicy.active = parsedJson.active;
	                        noticePolicy.institution = institutionService.getDefaultInstitution();
	                        noticePolicy.save(flush:true, failOnError:true);
	
	                        // Create the mapping with the predefined id
	                        PredefinedId.ensureExists('notice_policy', parsedJson.predefinedId, noticePolicy.id);
	
	                        // Now for the NoticePolicyNotice
	                        NoticePolicyNotice noticePolicyNotice = new NoticePolicyNotice();
	                        noticePolicyNotice.template = templateContainer;
	                        noticePolicyNotice.realTime = true;
	                        noticePolicyNotice.format = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_NOTICE_FORMATS, 'E-mail', 'email');
	                        noticePolicyNotice.trigger = RefdataValue.lookupOrCreate(RefdataValueData.VOCABULARY_NOTICE_TRIGGERS, parsedJson.triggerType);
	                        noticePolicyNotice.noticePolicy = noticePolicy;
	                        noticePolicyNotice.save(flush:true, failOnError:true);
	                        noticePolicy.addToNotices(noticePolicyNotice);
	                        noticePolicy.save(flush:true, failOnError:true);
	                    }
					}
                }
            } catch (Exception e) {
                log.error('Exception thrown while loading template from resource: ' + resourcePath, e);
            } finally {
                // Close all the resources associated with the stream, may not need to do this ...
                stream.close();
            }
        }
    }

    private String getReferencedId(String namespace, String predefinedId) {
        return(PredefinedId.lookupReferenceId(namespace, predefinedId));
    }
}
