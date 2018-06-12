package edu.gla.kail.ad.core;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * stores the name of the Agent manager and additional parameters required by the Agent manager.
 */
public class ConfigurationTuple<T> {
    private String _nameOfTheAgent;
    private List<T> _parametersRequiredByTheAgent;

    public ConfigurationTuple(String nameOfTheAgent, @Nullable List<T>
            parametersRequiredByTheAgent) throws Exception {
        if (nameOfTheAgent.isEmpty()) {
            throw new Exception("The provided name of the service is empty!");
        } else {
            _nameOfTheAgent = checkNotNull(nameOfTheAgent, "The name of the service is null");
        }

        _parametersRequiredByTheAgent = parametersRequiredByTheAgent;
    }

    public String get_nameOfTheAgent() {
        return _nameOfTheAgent;
    }

    public List<T> get_parametersRequiredByTheAgent() {
        return _parametersRequiredByTheAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationTuple<?> that = (ConfigurationTuple<?>) o;
        return Objects.equals(_nameOfTheAgent, that._nameOfTheAgent) &&
                Objects.equals(_parametersRequiredByTheAgent, that._parametersRequiredByTheAgent);
    }

    @Override
    public int hashCode() {

        return Objects.hash(_nameOfTheAgent, _parametersRequiredByTheAgent);
    }

    @Override
    public String toString() {
        return "ConfigurationTuple{" +
                "_nameOfTheAgent='" + _nameOfTheAgent + '\'' +
                ", _parametersRequiredByTheAgent=" + _parametersRequiredByTheAgent +
                '}';
    }
}
