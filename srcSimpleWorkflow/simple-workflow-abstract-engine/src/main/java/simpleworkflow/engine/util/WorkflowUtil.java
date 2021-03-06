package simpleworkflow.engine.util;

import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Transition;
import simpleworkflow.core.meta.Workflow;

/**
 * @author XingGu_Liu
 */
public class WorkflowUtil {

    public static State getMetaState(Workflow workFlowMeta, String stateName) {
        for (State data: workFlowMeta.getStates()) {
            if(data.getName().equals(stateName)) {
                return data;
            }
        }

        return null;
    }

    public static boolean setMetaState(Workflow workFlowMeta, State stateMeta) {
        int i = 0;
        for (State data: workFlowMeta.getStates()) {
            if(data.getName().equals(stateMeta.getName())) {
                workFlowMeta.getStates().set(i, stateMeta);
                return true;
            }

            i++;
        }

        return false;
    }

    public static Transition getTransitionByEvent(State stateMeta, String eventName) {
        for (Transition transition: stateMeta.getTransitions()) {
            if(transition.getEvent().getName().equals(eventName)) {
                return transition;
            }
        }

        return null;
    }

//    public static Activity getActivityByActivityName(State stateMeta, String activityName) {
//        for (Transition transition: stateMeta.getTransitions()) {
//            if(transition.getActivity().getName().equals(activityName)) {
//                return transition.getActivity();
//            }
//        }
//
//        return null;
//    }


}
