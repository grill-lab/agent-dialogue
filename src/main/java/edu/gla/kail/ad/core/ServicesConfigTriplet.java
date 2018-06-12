package edu.gla.kail.ad.core;

public class ServicesConfigTriplet {
    private String nameOfTheService;
    private String projectId;
    private String locaitonOfAuthenticationFile;

    public ServicesConfigTriplet(String nameOfTheService, String projectId, String locaitonOfAuthenticationFile) {
        if (nameOfTheService.isEmpty()) {
            throw new Exception("This string is ")
        }
        this.nameOfTheService = checkNotNull(nameOfTheService, "The name of the service is null");
        this.projectId = projectId;
        this.locaitonOfAuthenticationFile = locaitonOfAuthenticationFile;
    }

    public String getNameOfTheService() {
        return nameOfTheService;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getLocaitonOfAuthenticationFile() {
        return locaitonOfAuthenticationFile;
    }
}
