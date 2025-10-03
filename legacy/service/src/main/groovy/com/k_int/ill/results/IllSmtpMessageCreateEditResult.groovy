package com.k_int.ill.results;

import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.web.toolkit.refdata.RefdataValue;

import groovy.transform.CompileStatic;

/**
 * Holds the result details for details required to create or edit an ill smtp message
 */
@CompileStatic
public class IllSmtpMessageCreateEditResult {

    /** List of action events that can be selected from */
    public List<ActionEventLabelValue> actionEvents = new ArrayList<ActionEventLabelValue>();

    /** List of action events that can be selected from */
    public List<TemplateContainerLabelValue> templateContainers = new ArrayList<TemplateContainerLabelValue>();

	/** The list of service types */
	public List<ReferenceDataLabelValue> serviceTypes = new ArrayList<ReferenceDataLabelValue>();

    public IllSmtpMessageCreateEditResult(
		Collection<ActionEvent> actionEvents,
		Collection<TemplateContainer> templateContainers,
		List<RefdataValue> serviceTypes
    ) {
        // Add all the action events that have been supplied
        if (actionEvents) {
            // Sort by description, before we add them to the list
            actionEvents.sort{ActionEvent actionEvent ->
                return(actionEvent.description.toLowerCase());
            }.each { ActionEvent actionEvent ->
                this.actionEvents.add(new ActionEventLabelValue(actionEvent));
            }
        }

        // Add all the template containers that have been supplied
        if (templateContainers) {
            // Sort by description, before we add them to the list
            templateContainers.sort{TemplateContainer templateContainer -> 
                return(templateContainer.name.toLowerCase());
            }.each { TemplateContainer templateContainer ->
                this.templateContainers.add(new TemplateContainerLabelValue(templateContainer));
            }
        }

		// Add all the service types
		if (serviceTypes) {
			// Sort by label, before we add them to the list
			serviceTypes.sort{ RefdataValue refdataValue ->
				return(refdataValue.label.toLowerCase());
			}.each { RefdataValue refdataValue ->
				this.serviceTypes.add(new ReferenceDataLabelValue(refdataValue));
			}
		}
    }
}
