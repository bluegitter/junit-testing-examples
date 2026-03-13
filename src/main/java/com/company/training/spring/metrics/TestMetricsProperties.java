package com.company.training.spring.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "training.metrics.test")
public class TestMetricsProperties {

    private boolean enabled = true;
    private String surefireDir = "target/surefire-reports";
    private String jacocoXml = "target/site/jacoco/jacoco.xml";
    private long refreshInterval = 30000L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSurefireDir() {
        return surefireDir;
    }

    public void setSurefireDir(String surefireDir) {
        this.surefireDir = surefireDir;
    }

    public String getJacocoXml() {
        return jacocoXml;
    }

    public void setJacocoXml(String jacocoXml) {
        this.jacocoXml = jacocoXml;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
