package simpleworkflow.engine.persistence.sql.data;

import simpleworkflow.core.meta.State;

/**
 * @author XingGu_Liu
 */
public class WfMeta {
    private String _name;

    private long _version;

    private String _description;

    private String _author;

    private String _engineName;

    private String _startState = "";

    private String _stateDataType = "";

    private String _states;


    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public long getVersion() {
        return _version;
    }

    public void setVersion(long version) {
        _version = version;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
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

    public String getStates() {
        return _states;
    }

    public void setStates(String states) {
        _states = states;
    }

}
