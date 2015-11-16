package simpleworkflow.core.meta;

/**
 * @author XingGu_Liu
 */
public class Event extends MetaBaseData {
    private Activity _activity;

    public Activity getActivity() {
        return _activity;
    }

    public void setActivity(Activity activity) {
        _activity = activity;
    }
}
