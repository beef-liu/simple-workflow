package simpleworkflow.core.meta;

import java.util.List;

/**
 * @author XingGu_Liu
 */
public class StateRouter extends MetaBaseData {

    private List<StateRouterRule> _routerRules;

    public List<StateRouterRule> getRouterRules() {
        return _routerRules;
    }

    public void setRouterRules(List<StateRouterRule> routerRules) {
        _routerRules = routerRules;
    }
}
