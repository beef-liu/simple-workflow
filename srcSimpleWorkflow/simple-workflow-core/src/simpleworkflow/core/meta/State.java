package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class State extends MetaBaseData {

    private int _stateType = 0;

    private Activity _initActivity;

    private List<Transition> _transitions;

    /**
     * Application whose return value is boolean type
     */
    private Application _accessibleJudge;

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

    public Application getAccessibleJudge() {
        return _accessibleJudge;
    }

    public void setAccessibleJudge(Application accessibleJudge) {
        _accessibleJudge = accessibleJudge;
    }
}
