package edu.gla.kail.ad.core;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Store the type of a agent (e.g. Dialogflow) and additional parameters required for its
 * initialization.
 * The class is parameterized - it uses the generic type: T.
 */
public class ConfigurationTuple<T> {
    // The type of the particular agent.
    private SupportedAgentTypes _agentType;
    // List of configuration objects specific to the type of agent.
    private List<T> _agentSpecificData;

    /**
     * @param supportedAgentTypes - The type of the agent.
     * @param agentSpecificData - A list of generic type objects, that stores the data
     *         required by a particular agent.
     */
    public ConfigurationTuple(SupportedAgentTypes supportedAgentTypes, @Nullable List<T>
            agentSpecificData) {
        _agentType = checkNotNull(supportedAgentTypes, "The name of the service is null");
        _agentSpecificData = agentSpecificData;
    }

    public SupportedAgentTypes get_agentType() {
        return _agentType;
    }

    public List<T> get_agentSpecificData() {
        return _agentSpecificData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationTuple<?> that = (ConfigurationTuple<?>) o;
        return Objects.equals(_agentType, that._agentType) &&
                Objects.equals(_agentSpecificData, that
                        ._agentSpecificData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_agentType, _agentSpecificData);
    }

    @Override
    public String toString() {
        return "ConfigurationTuple{" +
                "_agentType='" + _agentType + '\'' +
                ", _agentSpecificData=" + _agentSpecificData +
                '}';
    }
}
