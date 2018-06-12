package edu.gla.kail.ad.core;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Store the name of the Agent manager and additional parameters required by the Agent manager.
 */
public class ConfigurationTuple<T> {
    private SupportedDialogManagers _dialogManagerType;
    private List<T> _parametersRequiredByTheAgent;

    public ConfigurationTuple(SupportedDialogManagers dialogManagerType, @Nullable List<T>
            parametersRequiredByTheAgent) throws Exception {
        _dialogManagerType = checkNotNull(dialogManagerType, "The name of the service is null");
        _parametersRequiredByTheAgent = parametersRequiredByTheAgent;
    }

    public SupportedDialogManagers get_dialogManagerType() {
        return _dialogManagerType;
    }

    public List<T> get_parametersRequiredByTheAgent() {
        return _parametersRequiredByTheAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationTuple<?> that = (ConfigurationTuple<?>) o;
        return Objects.equals(_dialogManagerType, that._dialogManagerType) &&
                Objects.equals(_parametersRequiredByTheAgent, that._parametersRequiredByTheAgent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_dialogManagerType, _parametersRequiredByTheAgent);
    }

    @Override
    public String toString() {
        return "ConfigurationTuple{" +
                "_dialogManagerType='" + _dialogManagerType + '\'' +
                ", _parametersRequiredByTheAgent=" + _parametersRequiredByTheAgent +
                '}';
    }
}
