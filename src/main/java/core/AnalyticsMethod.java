package core;

import core.exceptions.AnalyticsMethodInitializationException;
import de.rwthaachen.openlap.dataset.*;
import de.rwthaachen.openlap.dynamicparam.OpenLAPDynamicParams;

import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;

/**
 * Analytics Method abstract class.
 *
 * To make an implementation the developer must:
 * <ol>
 *     <li>Use the {@code OpenLAPDataColumnFactory} to set both inputs an outputs</li>
 *     <li>Override the methods {@code hasPMML()} and {@code getPMMLFile()}, which allow service consumer
 *     classes to access the PMML file if it exists and to inform themselves about the existence of such file.</li>
 *     <li>Override the main algorithm of the class: {@code implementationExecution()} which MUST write its
 *     contents on the{@code output} property, since service consumer classes will use the {@code execute()} method,
 *     which returns {@code output} after executing {@code implementationExecution()}</li>
 * </ol>
 *
 * Initial data setup is done by the {@code initialize(OpenLAPDataSet data, OpenLAPPortConfig configuration)}
 * method of this class and uses the given configuration to populate the {@code input}.
 * @author  Oscar Barrios
 */

public abstract class AnalyticsMethod {

    private OpenLAPDataSet input;
    private OpenLAPDataSet output;
    private OpenLAPDynamicParams params;

    /**
     * Main execution for the Analytics Method. Will run {@code implementationExecution(output)}, which should be set
     * internally. This forces the implementation to use the output.
     * @return {@code OpenLAPDataSet output} property of the Analytics Method.
     */
    public OpenLAPDataSet execute(){
        implementationExecution();
        return output;
    }

    /**
     * Method to be overriden by the implementing Analytics Method. Uses the property {@code output} in order
     * to be modified so the consumer who runs {@code execute()} can obtain the output.
     */
    protected abstract void implementationExecution();

    /**
     * Used to determine wether the method uses or not a PMML file
     * @return {@code true} if the Method uses PMML, {@code false} otherwise
     */
    public abstract Boolean hasPMML();

    /**
     * A File handler to the PMML XML
     * @return a {@code File} handler of the PMML XML. Should be null whenever {@code hasPMML() == true}
     */
    public abstract InputStream getPMMLInputStream();

    /**
     * Gets the {@code OpenLAPColumnConfigData} for the {@code input} OpenLAPDataSet.
     * Note that the implementor MUST set the {@code input} property
     * @return an array of the  {@code OpenLAPColumnConfigData} for the {@code input}  OpenLAPDataSet
     */
    public List<OpenLAPColumnConfigData> getInputPorts(){
        return input.getColumnsConfigurationData();
    }
    /**
     * Gets the {@code OpenLAPColumnConfigData} for the {@code output} OpenLAPDataSet.
     * Note that the implementor MUST set the {@code output} property
     * @return an array of the  {@code OpenLAPColumnConfigData} for the {@code output} OpenLAPDataSet
     */
    public List<OpenLAPColumnConfigData> getOutputPorts(){
        return output.getColumnsConfigurationData();
    }

    /**
     * Uses a given {@code OpenLAPPortConfig} to initialize the data of the {@code input} property fot the Method.
     * Since the service consumer will use this method before running {@code execute()}, is important that the
     * {@code OpenLAPDataSet input} is properly set by the implementation.
     * @param data {@code OpenLAPDataSet} containing the incoming data
     * @param configuration {@code OpenLAPPortConfig} that specifies how to map {@code data} to the {@code input}
     *                                                   property of the Analytics method.
     * @throws AnalyticsMethodInitializationException Initialization exception
     */
    public void initialize(OpenLAPDataSet data, OpenLAPPortConfig configuration)
            throws AnalyticsMethodInitializationException {
        OpenLAPDataSetConfigValidationResult validationResult;
        // Check that the configuration
        validationResult =input.validateConfiguration(configuration);
        if (!validationResult.isValid())
        {
            // throw exception if not
            throw new AnalyticsMethodInitializationException(validationResult.getValidationMessage());
        }
        // for each configuration element of the configuration
        for (OpenLAPPortMapping mappingEntry: configuration.getMapping()) {
            // map the data of the column c.id==element.id to the input
            input.getColumns().get(mappingEntry.getInputPort().getId()).setData(
                    data.getColumns().get(mappingEntry.getOutputPort().getId()).getData()
            );
        }

        //adding the default values of the parameters in the OpenLAPDynamicParam object for this method
        for (String paramId: getParams().getParams().keySet())
            params.getParams().get(paramId).setValue(getParams().getParams().get(paramId).getDefaultValue());
    }

    /**
     *
     * @param data
     * @param configuration
     * @param additionalParams
     * @throws AnalyticsMethodInitializationException
     */
    public void initialize(OpenLAPDataSet data, OpenLAPPortConfig configuration, Map<String, String> additionalParams)
            throws AnalyticsMethodInitializationException {
        OpenLAPDataSetConfigValidationResult validationResult;
        // Check that the configuration
        validationResult =input.validateConfiguration(configuration);
        if (!validationResult.isValid())
        {
            // throw exception if not
            throw new AnalyticsMethodInitializationException(validationResult.getValidationMessage());
        }
        // for each configuration element of the configuration
        for (OpenLAPPortMapping mappingEntry: configuration.getMapping()) {
            // map the data of the column c.id==element.id to the input
            input.getColumns().get(mappingEntry.getInputPort().getId()).setData(
                    data.getColumns().get(mappingEntry.getOutputPort().getId()).getData()
            );
        }

        //adding the values of the parameters in the OpenLAPDynamicParam object
        if(getParams() != null) {
            for (String paramId : getParams().getParams().keySet()) {
                if (additionalParams.containsKey(paramId)) {
                    switch (getParams().getParams().get(paramId).getDataType()) {
                        case STRING:
                            params.getParams().get(paramId).setValue(additionalParams.get(paramId));
                        case INTEGER:
                            params.getParams().get(paramId).setValue(Integer.parseInt(additionalParams.get(paramId)));
                        case FLOAT:
                            params.getParams().get(paramId).setValue(Float.parseFloat(additionalParams.get(paramId)));
                    }
                } else {
                    getParams().getParams().get(paramId).setValue(getParams().getParams().get(paramId).getDefaultValue());
                }
            }
        }
    }

    /**
     * Getter accessor for the {@code input} of the Analytics Method
     * @return the {@code input} of the Analytics Method
     */
    public OpenLAPDataSet getInput() {
        return input;
    }

    /**
     * Getter accessor for the {@code output} of the Analytics Method
     * @return the {@code output} of the Analytics Method
     */
    public OpenLAPDataSet getOutput() {
        return output;
    }

    /**
     * Setter accessor for the {@code input} of the Analytics Method
     * @param input to be set
     */
    public void setInput(OpenLAPDataSet input) {
        this.input = input;
    }

    /**
     * Setter accessor for the {@code output} of the Analytics Method
     * @param output to be set
     */
    public void setOutput(OpenLAPDataSet output) {
        this.output = output;
    }


    public OpenLAPDynamicParams getParams() {
        return params;
    }

    public void setParams(OpenLAPDynamicParams params) {
        this.params = params;
    }
}