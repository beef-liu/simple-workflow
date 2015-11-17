package simpleworkflow.core.persistence.data;

/**
 * @author XingGu_Liu
 */
public class WfStateEventResult {
    private String _event;

    private String _activity;

    private String _destination;

    public String getEvent() {
        return _event;
    }

    public void setEvent(String event) {
        _event = event;
    }

    public String getActivity() {
        return _activity;
    }

    public void setActivity(String activity) {
        _activity = activity;
    }

    public String getDestination() {
        return _destination;
    }

    public void setDestination(String destination) {
        _destination = destination;
    }
}
