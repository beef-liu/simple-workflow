package simpleworkflow.core;

/**
 * @author XingGu_Liu
 */
public class WorkflowEnums {
    public static enum WorkflowStatus {
        Running,
        Terminated,
        Completed
    };

    public static enum StateTypes {
        Normal,
        Completed,
        Terminated
    }

}
