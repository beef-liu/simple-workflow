package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class State extends MetaBaseData {

    private int _stateType = 0;

    /**
     * IStateInitApp if application type is java
     */
    private Activity _initActivity;

    private List<Transition> _transitions;

    /**
     * IStateAuthCheckApp if application type is java
     */
    private Application _accessibleCheck;

    public Activity getInitActivity() {
        return _initActivity;
    }

    public void setInitActivity(Activity initActivity) {
        _initActivity = initActivity;
    }

    public int getStateType() {
        return _stateType;
    }

    public void setStateType(int stateType) {
        _stateType = stateType;
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
