package com.k_int.ill.templating;

import com.k_int.ill.constants.Template;
import com.k_int.ill.protocols.illEmail.IllEmailMessageTokensService;
import com.k_int.ill.results.TemplateContainerCreateEditResult;
import com.k_int.institution.Institution;

import groovy.util.logging.Slf4j;

@Slf4j
public class TemplateContainerService {

	IllEmailMessageTokensService illEmailMessageTokensService;
	
	public List<TemplateContainer> containersFor(
		Institution institution,
		String context
	) {
		return(TemplateContainer.findAllByContext(context));
	}

	/**
	 * Obtains all related data required for creating or editing a template container 
	 * @param institution The institution the data is required for
	 * @param context
	 * @return
	 */
    public TemplateContainerCreateEditResult detailsForCreateEdit(
		Institution institution,
		String context
	) {
		Map<String, List<String>> tokens = null;
		
		// Determine the tokens for this context
		switch (context) {
			case Template.CONTEXT_ILL_SMTP:
				tokens = illEmailMessageTokensService.tokens();
				break;
		}

		// Return the structure to the caller
        return(new TemplateContainerCreateEditResult(
			tokens
		));
    }
}
