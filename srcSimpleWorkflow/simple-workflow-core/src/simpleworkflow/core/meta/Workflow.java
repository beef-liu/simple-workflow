package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class Workflow extends MetaBaseData {

    private long _version;

    private String _author;

    private List<State> _states;

    private String _startState = "";


    public long getVersion() {
        return _version;
    }

    public void setVersion(long version) {
        _version = version;
    }

    public String getAuthor() {
        return _author;
    }

    public void setAuthor(String author) {
        _author = author;
    }

    public List<State> getStates() {
        return _states;
    }

    public void setStates(List<State> states) {
        _states = states;
    }

    public String getStartState() {
        return _startState;
    }

    public void setStartState(String startState) {
        _startState = startState;
    }
}
