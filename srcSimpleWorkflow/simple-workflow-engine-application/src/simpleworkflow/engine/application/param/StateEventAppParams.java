package simpleworkflow.engine.application.param;

import simpleworkflow.core.interfaces.IWorkflowEngine;
import simpleworkflow.core.meta.State;
import simpleworkflow.core.meta.Workflow;

/**
 * It is the type of parameter of method of application of Event.accessibleCheck and Activity.application
 * @author XingGu_Liu
 */
public class StateEventAppParams extends StateInitAppParams {

    private String _eventName;

    private Object _eventData;

    public StateEventAppParams() {
    }

    public StateEventAppParams(
            IWorkflowEngine engine, String user,
            Workflow workflowMeta, String workflowId,
            State stateMeta, String stateId, Object stateData,
            String eventName, Object eventData) {
        super(engine, user, workflowMeta, workflowId, stateMeta, stateId, stateData);

        _eventName = eventName;
        _eventData = eventData;
    }

    public String getEventName() {
        return _eventName;
    }

    public void setEventName(String eventName) {
        _eventName = eventName;
    }

    public Object getEventData() {
        return _eventData;
    }

    public void setEventData(Object eventData) {
        _eventData = eventData;
    }
}
