package org.mahefa.common.enumerator;

public enum ServiceState {
    IDLE,         // The service is not currently running and is waiting to be started
    RUNNING,      // The service is actively performing its tasks
    STARTING,     // The service is in the process of being started
    STOPPING,     // The service is in the process of being stopped
    PAUSED,       // The service has been temporarily paused and can be resumed
    RESUMING,     // The service is in the process of resuming from a paused state
    FAILED,       // The service encountered an error and has stopped functioning correctly
    COMPLETED,    // The service has successfully completed its tasks
    BLOCKED       // The service is currently unable to proceed due to some condition or dependency
}

