package com.k_int.ill;

import com.k_int.ill.constants.Category;
import com.k_int.ill.constants.Template;
import com.k_int.ill.protocols.illEmail.IllEmailMessageService;
import com.k_int.ill.results.IllSmtpMessageCreateEditResult;
import com.k_int.ill.statemodel.ActionEvent;
import com.k_int.ill.templating.TemplateContainer;
import com.k_int.ill.templating.TemplateContainerService;
import com.k_int.institution.Institution;
import com.k_int.web.toolkit.refdata.RefdataValue;

/**
 * Provides the necessary methods for interfacing with the ill smtp message domain
 * @author Chas
 *
 */
public class IllSmtpMessageService {

	private static final String SQL_SERVICE_TYPES = '''
select rdv
from RefdataValue rdv
where rdv.owner = (select id from RefdataCategory rdc where rdc.desc = :desc)
''';
	
	
	IllEmailMessageService illEmailMessageService;
	TemplateContainerService templateContainerService;

    /**
     * Retrieves the details required for creating or editing an ill smtp message
     * @return A IllSmtpMessageCreateEditResult object that contains all the details required to create or edit an ill smtp message
     */
    public IllSmtpMessageCreateEditResult detailsForCreateEdit(
		Institution institution
	) {
        Collection<TemplateContainer> templatecontainers = templateContainerService.containersFor(
			institution,
			Template.CONTEXT_ILL_SMTP
		);
        Collection<ActionEvent> actionEvents = illEmailMessageService.validActionEvents();
		List<RefdataValue> serviceTypes = RefdataValue.executeQuery(
			SQL_SERVICE_TYPES,
			[ "desc" : Category.SERVICE_TYPE ]
		);

        return(new IllSmtpMessageCreateEditResult(
			actionEvents,
			templatecontainers,
			serviceTypes
		));
    }
}
