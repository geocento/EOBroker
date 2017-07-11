package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by thomas on 13/03/2017.
 */
@Entity
public class ApplicationSettings {

    @Id
    Long id;

    @Column(length = 255)
    String applicationName;

    @Column(length = 255)
    String serverType;

    @Column(length = 1000)
    String dataDirectory;
    @Column(length = 1000)
    String websiteUrl;

    // email settings
    boolean reportByEmail;
    @Column(length = 100)
    String emailFrom;
    @Column(length = 100)
    String emailServer;
    Integer emailPort;
    boolean smtps;
    boolean enableTLS;
    @Column(length = 100)
    String emailAccount;
    @Column(length = 100)
    String emailPassword;

    // API settings
    @Column(length = 1000)
    private String geoserverRESTUri;
    @Column(length = 100)
    private String geoserverUser;
    @Column(length = 100)
    private String geoserverPassword;
    @Column(length = 1000)
    private String geoserverOWS;

    // application parameters
    @Column(length = 1000)
    String helpLink;
    @Column(length = 1000)
    String about;
    @Column(length = 10000)
    String termsAndConditionsURL;
    @Column(length = 10000)
    String supportEmail;
    int maxSampleSizeMB;
    int notificationDelay;
    @Column(length = 10000)
    String supplierWebsiteUrl;
    @Column(length = 10000)
    String adminWebsiteUrl;

    public ApplicationSettings() {
        id = 1L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public boolean isReportByEmail() {
        return reportByEmail;
    }

    public void setReportByEmail(boolean reportByEmail) {
        this.reportByEmail = reportByEmail;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getEmailServer() {
        return emailServer;
    }

    public void setEmailServer(String emailServer) {
        this.emailServer = emailServer;
    }

    public Integer getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public boolean isSmtps() {
        return smtps;
    }

    public void setSmtps(boolean SMTPS) {
        this.smtps = SMTPS;
    }

    public boolean isEnableTLS() {
        return enableTLS;
    }

    public void setEnableTLS(boolean enableTLS) {
        this.enableTLS = enableTLS;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getGeoserverRESTUri() {
        return geoserverRESTUri;
    }

    public void setGeoserverRESTUri(String geoserverRESTUri) {
        this.geoserverRESTUri = geoserverRESTUri;
    }

    public String getGeoserverUser() {
        return geoserverUser;
    }

    public void setGeoserverUser(String geoserverUser) {
        this.geoserverUser = geoserverUser;
    }

    public String getGeoserverPassword() {
        return geoserverPassword;
    }

    public void setGeoserverPassword(String geoserverPassword) {
        this.geoserverPassword = geoserverPassword;
    }

    public String getGeoserverOWS() {
        return geoserverOWS;
    }

    public void setGeoserverOWS(String geoserverOWS) {
        this.geoserverOWS = geoserverOWS;
    }

    public String getHelpLink() {
        return helpLink;
    }

    public void setHelpLink(String helpLink) {
        this.helpLink = helpLink;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTermsAndConditionsURL() {
        return termsAndConditionsURL;
    }

    public void setTermsAndConditionsURL(String termsAndConditionsURL) {
        this.termsAndConditionsURL = termsAndConditionsURL;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public int getMaxSampleSizeMB() {
        return maxSampleSizeMB;
    }

    public void setMaxSampleSizeMB(int maxSampleSizeMB) {
        this.maxSampleSizeMB = maxSampleSizeMB;
    }

    public int getNotificationDelay() {
        return notificationDelay;
    }

    public void setNotificationDelay(int notificationDelay) {
        this.notificationDelay = notificationDelay;
    }

    public String getSupplierWebsiteUrl() {
        return supplierWebsiteUrl;
    }

    public void setSupplierWebsiteUrl(String supplierWebsiteUrl) {
        this.supplierWebsiteUrl = supplierWebsiteUrl;
    }

    public String getAdminWebsiteUrl() {
        return adminWebsiteUrl;
    }

    public void setAdminWebsiteUrl(String adminWebsiteUrl) {
        this.adminWebsiteUrl = adminWebsiteUrl;
    }
}
