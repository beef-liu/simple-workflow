package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Transition extends MetaBaseData {
    private Event _event;

    /**
     * IStateTransitionApp if application type is java
     */
    private Activity _activity;

    private StateRouter _stateRouter;

    public Event getEvent() {
        return _event;
    }

    public void setEvent(Event event) {
        _event = event;
    }

    public Activity getActivity() {
        return _activity;
    }

    public void setActivity(Activity activity) {
        _activity = activity;
    }

    public StateRouter getStateRouter() {
        return _stateRouter;
    }

    public void setStateRouter(StateRouter stateRouter) {
        _stateRouter = stateRouter;
    }
}
