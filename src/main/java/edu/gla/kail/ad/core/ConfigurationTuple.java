package edu.gla.kail.ad.core;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Store the name of the Agent manager and additional parameters required by the Agent manager.
 */
public class ConfigurationTuple<T> {
    // The type of the particular Dialog Manager which is used to interact with its Agents.
    private SupportedDialogManagers _dialogManagerType;
    // List of generic type Objects which may be different for every type of the particular
    // Dialog Manager.
    private List<T> _particularDialogManagerSpecificData;

    /**
     * @param dialogManagerType                   The type of the particular Dialog Manager which
     *                                            is used to interact with its Agents.
     * @param particularDialogManagerSpecificData A list of generic type objects, that stores the
     *                                            data required by a particular Dialog Manager
     * @throws Exception It is thrown when the name of the service is not provided.
     */
    public ConfigurationTuple(SupportedDialogManagers dialogManagerType, @Nullable List<T>
            particularDialogManagerSpecificData) throws Exception {
        _dialogManagerType = checkNotNull(dialogManagerType, "The name of the service is null");
        _particularDialogManagerSpecificData = particularDialogManagerSpecificData;
    }

    public SupportedDialogManagers get_dialogManagerType() {
        return _dialogManagerType;
    }

    public List<T> get_particularDialogManagerSpecificData() {
        return _particularDialogManagerSpecificData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationTuple<?> that = (ConfigurationTuple<?>) o;
        return Objects.equals(_dialogManagerType, that._dialogManagerType) &&
                Objects.equals(_particularDialogManagerSpecificData, that
                        ._particularDialogManagerSpecificData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_dialogManagerType, _particularDialogManagerSpecificData);
    }

    @Override
    public String toString() {
        return "ConfigurationTuple{" +
                "_dialogManagerType='" + _dialogManagerType + '\'' +
                ", _particularDialogManagerSpecificData=" + _particularDialogManagerSpecificData +
                '}';
    }
}
