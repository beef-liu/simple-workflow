package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class Workflow extends MetaBaseData {

    private long _version;

    private String _author;

    private String _engineName;

    private List<State> _states;

    private String _startState = "";

    /**
     * Name of a Java Class
     */
    private String _stateDataType = "";

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

    public String getEngineName() {
        return _engineName;
    }

    public void setEngineName(String engineName) {
        _engineName = engineName;
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

    public String getStateDataType() {
        return _stateDataType;
    }

    public void setStateDataType(String stateDataType) {
        _stateDataType = stateDataType;
    }
}
