package core;

import OLAPDataSet.*;

/**
 * Created by lechip on 15/11/15.
 */
public abstract class AnalyticsMethod {
    OLAPDataSet input;
    OLAPDataSet output;
    Boolean isPredictive;

    public abstract OLAPDataSet execute();
    public abstract OLAPColumnConfigurationData getInputPorts();
    public abstract OLAPColumnConfigurationData getOutputPorts();
    public abstract void initialize(OLAPDataSet data, OLAPPortConfiguration configuration);
}