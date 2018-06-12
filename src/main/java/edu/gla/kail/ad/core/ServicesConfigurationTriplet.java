package edu.gla.kail.ad.core;


import javax.annotation.Nullable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class for storing the data of a used service and its agent/projectID and the authentication File if needed.
 */
public class ServicesConfigurationTriplet {
    private String _nameOfTheService;
    private String _projectId;
    private String _directoryOfAuthenticationFile;

    public ServicesConfigurationTriplet(String nameOfTheService, String projectId, @Nullable String directoryOfAuthenticationFile) throws Exception{
        if (nameOfTheService.isEmpty()) {
            throw new Exception("The provided name of the service is empty!");
        } else {
            _nameOfTheService = checkNotNull(nameOfTheService, "The name of the service is null");
        }
        if (projectId.isEmpty()) {
            throw new Exception("The provided project ID of the service is empty!");
        } else {
            _projectId = checkNotNull(projectId, "The project ID is null");
        }
        if (directoryOfAuthenticationFile != null) {
            _directoryOfAuthenticationFile = directoryOfAuthenticationFile;
        }
    }
    public String getNameOfTheService() {
        return _nameOfTheService;
    }

    public String get_projectId() {
        return _projectId;
    }

    public String getLocaitonOfAuthenticationFile() {
        return _directoryOfAuthenticationFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicesConfigurationTriplet that = (ServicesConfigurationTriplet) o;
        return Objects.equals(_nameOfTheService, that._nameOfTheService) &&
                Objects.equals(_projectId, that._projectId) &&
                Objects.equals(_directoryOfAuthenticationFile, that._directoryOfAuthenticationFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_nameOfTheService, _projectId, _directoryOfAuthenticationFile);
    }

    @Override
    public String toString() {
        return "ServicesConfigurationTriplet{" +
                "_nameOfTheService='" + _nameOfTheService + '\'' +
                ", _projectId='" + _projectId + '\'' +
                ", _directoryOfAuthenticationFile='" + _directoryOfAuthenticationFile + '\'' +
                '}';
    }
}
