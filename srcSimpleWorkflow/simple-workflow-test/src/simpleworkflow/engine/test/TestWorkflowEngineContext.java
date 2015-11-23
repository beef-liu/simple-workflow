package simpleworkflow.engine.test;

import com.salama.reflect.PreScanClassFinder;
import simpleworkflow.engine.WorkflowEngine;

/**
 * @author XingGu_Liu
 */
public class TestWorkflowEngineContext {

    private PreScanClassFinder _classFinderForWorkflowCoreData;

    private WorkflowEngine _workflowEngine;


    private static TestWorkflowEngineContext _singleton = null;

    public static TestWorkflowEngineContext singleton() {
        if(_singleton == null) {
            _singleton = new TestWorkflowEngineContext();
        }

        return _singleton;
    }

    private TestWorkflowEngineContext() {
    }

    public void init() {

    }
}
