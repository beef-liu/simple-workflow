package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class State extends MetaBaseData {

    private int _stateType = 0;

    private List<Transition> _transitions;

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
}
