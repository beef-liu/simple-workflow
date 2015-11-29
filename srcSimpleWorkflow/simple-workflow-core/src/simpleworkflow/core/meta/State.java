package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class State extends MetaBaseData {

    private int _stateType = 0;

    /**
     * This application will be executed before create state instance.
     * The input data is the state data of previous state or inData if it's the start of workflow.
     * Return value should be current state data
     */
    private Application _initApp;

    private List<Transition> _transitions;

    /**
     * Return value should be type of Boolean
     */
    private Application _accessibleCheck;


    public int getStateType() {
        return _stateType;
    }

    public void setStateType(int stateType) {
        _stateType = stateType;
    }

    public Application getInitApp() {
        return _initApp;
    }

    public void setInitApp(Application initApp) {
        _initApp = initApp;
    }

    public List<Transition> getTransitions() {
        return _transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        _transitions = transitions;
    }

    public Application getAccessibleCheck() {
        return _accessibleCheck;
    }

    public void setAccessibleCheck(Application accessibleCheck) {
        _accessibleCheck = accessibleCheck;
    }
}
