package com.k_int.ill.referenceData.availableActions;

import com.k_int.ill.statemodel.AvailableAction;
import com.k_int.ill.statemodel.StateModel;
import com.k_int.ill.statemodel.StateModelStatus;
import com.k_int.ill.statemodel.Status;
import com.k_int.ill.statemodel.StatusStage;

/**
 * The base class for creating available actions
 * @author Chas
 *
 */
public abstract class BaseAvailableActions {

    public abstract void load(StateModel stateModel);

	private List<Status> getStatesByStages(StateModel model, List<StatusStage> stages) {
		List<Status> states = new ArrayList<Status>();
		model.states.each { StateModelStatus stateModelStatus ->
			if (stages.contains(stateModelStatus.state.stage)) {
				states.add(stateModelStatus.state);
			}
		}
		return(states);
	}
	
    protected void assignToAllStates(StateModel model, String action, String triggerType, String resultList) {
        // The supplied action can be applied to all states
		List<Status> allStates = new ArrayList<Status>();
		model.states.each { StateModelStatus stateModelStatus ->
			allStates.add(stateModelStatus.state);
		}
        assignToStates(allStates, model, action, triggerType, resultList);
    }

    protected void assignToActiveStates(StateModel model, String action, String triggerType, String resultList) {
        // The supplied action can be applied to all active states
        assignToStates(getStatesByStages(model, StateModel.activeStages), model, action, triggerType, resultList);
    }

    protected void assignToNonTerminalStates(StateModel model, String action, String triggerType, String resultList) {
        // The supplied action can be applied to all non terminal states
		List<Status> nonTerminalStates = new ArrayList<Status>();
		model.states.each { StateModelStatus stateModelStatus ->
			if (!stateModelStatus.isTerminal) {
				nonTerminalStates.add(stateModelStatus.state);
			}
		}
		
        assignToStates(nonTerminalStates, model, action, triggerType, resultList);
    }

    protected void assignToStates(List<Status> states, StateModel model, String action, String triggerType, String resultList) {
        // Make the action to all the states in the list
        states.each { status ->
            AvailableAction.ensure(model, status.code, action, triggerType, resultList);
        }
    }
}
