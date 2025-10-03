package com.k_int.ill.statemodel;

import grails.gorm.MultiTenant;
import services.k_int.tests.ExcludeFromGeneratedCoverageReport;

/**
 *
 */
@ExcludeFromGeneratedCoverageReport
class AvailableAction implements MultiTenant<AvailableAction> {

    /** Query which returns all the states than an action may come from */
    private static final String POSSIBLE_FROM_STATES_QUERY = 'select distinct aa.fromState.code from AvailableAction as aa where aa.model.shortcode = :stateModelCode and aa.actionCode = :action and aa.triggerType = :triggerType';
    private static final String POSSIBLE_ACTIONS_FOR_MODEL_QUERY = 'select distinct aa.actionEvent from AvailableAction as aa where aa.model = :model and aa.actionEvent.code not in :excludeActions and aa.triggerType != :excludeActionType';

    public static final String TRIGGER_TYPE_MANUAL   = "M"; // Available to users
    public static final String TRIGGER_TYPE_PROTOCOL = "P"; // Can occur due to a protocol message
    public static final String TRIGGER_TYPE_SYSTEM   = "S";

    String id;
    StateModel model;
    Status fromState;
    String actionCode;  // To be removed once the data has been added

    /** The action / event that is the source for this available action */
    ActionEvent actionEvent;

    // [S]ystem, [M]anual or [P]rotocol
    String triggerType;

    // [S]ervice / [C]losure / [N]one
    String actionType;

    String actionBody;

    /** The default set of results to use for this action / event */
    ActionEventResultList resultList;

    /** Groovy script that decides if this action is available or not, the request can be referenced in the script as arguments.patronRequest */
    String isAvailableGroovy;

    static belongsTo = [ model: StateModel ];

    static constraints = {
                    model (nullable: false)
                fromState (nullable: false)
              actionEvent (nullable: true, unique: ['model', 'fromState']) // Only temporarily nullable, until the data gets added for it
               actionCode (nullable: false, blank:false) // To be removed once the data has been added
              triggerType (nullable: true, blank:false)
               actionType (nullable: true, blank:false)
               actionBody (nullable: true, blank:false)
               resultList (nullable: true)
        isAvailableGroovy (nullable: true, blank: false)
    }

    static mapping = {
                       id column : 'aa_id', generator: 'uuid2', length:36
                  version column : 'aa_version'
                    model column : 'aa_model'
                fromState column : 'aa_from_state'
               actionCode column : 'aa_action_code'  // To be removed once the data has been added
              actionEvent column : 'aa_action_event'
              triggerType column : 'aa_trigger_type'
               actionType column : 'aa_action_type'
               actionBody column : 'aa_action_body'
               resultList column : 'aa_result_list'
        isAvailableGroovy column : 'aa_is_available_groovy', length: 512
    }

    public static AvailableAction ensure(
        String model,
        String state,
        String action,
        String triggerType,
        String resultListCode = null,
        String isAvailableGroovy = null
    ) {

        AvailableAction result = null;
        StateModel stateModel = StateModel.lookup(model);
        if (stateModel) {
            // Excellent we have found the state model
            result = ensure(stateModel, state, action, triggerType, resultListCode, isAvailableGroovy);
		}
        return(result);
    }

    public static AvailableAction ensure(
        StateModel stateModel,
        String state,
        String action,
        String triggerType,
        String resultListCode = null,
        String isAvailableGroovy = null
    ) {
        AvailableAction result = null;
        if (stateModel) {
            Status status = Status.lookup(state);
            if (status) {
				result = stateModel.availableActions.find { AvailableAction availableAction ->
					return((availableAction.fromState == status) && (availableAction.actionCode == action));
				}
                if (result == null) {
                    // We didn't find it, so create a new one
                    result = new AvailableAction(
						model: stateModel,
                        fromState: status,
                        actionCode: action
                    );

					// Need to add it to the state model, otherwise it will not get saved
					stateModel.addToAvailableActions(result);
                }

                // Update the other fields in case they have changed
                result.actionEvent = ActionEvent.lookup(action);
                result.triggerType = triggerType;
                result.resultList = ActionEventResultList.lookup(resultListCode);
                result.isAvailableGroovy = isAvailableGroovy;
			}
        }
        return(result);
    }

	public static String[] getFromStates(String stateModel, String action, String triggerType = TRIGGER_TYPE_MANUAL) {
		return(executeQuery(POSSIBLE_FROM_STATES_QUERY,[stateModelCode: stateModel, action: action, triggerType: triggerType]));
	}

    public static List<ActionEvent> getUniqueActionsForModel(StateModel model, List<String> excludeActions, Boolean includeProtocolActions, boolean traverseHierarchy) {
        String notIncludeActionType = includeProtocolActions ? "dummy" : TRIGGER_TYPE_PROTOCOL;
        List<ActionEvent> actionEvents = executeQuery(POSSIBLE_ACTIONS_FOR_MODEL_QUERY, [model: model, excludeActions: excludeActions, excludeActionType: notIncludeActionType]);

        // Now do we need to go up the hierarchy
        if (traverseHierarchy && model.inheritedStateModels) {
            // We need to go up the hierarchy
            model.inheritedStateModels.each { inheritedStateModel ->
                actionEvents += getUniqueActionsForModel(inheritedStateModel.inheritedStateModel, excludeActions, includeProtocolActions, traverseHierarchy);
            }

            // Remove any duplicates
            actionEvents.unique(true, { actionEvent1, actionEvent2 -> actionEvent1.id <=> actionEvent2.id });
        }
        return(actionEvents);
    }

    public String toString() {
        return "AvailableAction(${id}) ${actionCode} ${triggerType} ${actionType} ${actionBody?.take(40)}".toString()
    }
}
