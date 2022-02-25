package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.result;

import java.util.HashMap;

public class ProxyResults {
    private String status;
    private String node;
    private String ip;
    private String asn;
    private String provider;
    private String continent;
    private String country;
    private String city;
    private String region;
    private String regionCode;
    private String latitude;
    private String longitude;
    private String isoCode;
    private String proxy;
    private String type;
    private String port;
    private String risk;
    private String total;
    private String vulnerabilityProbing;
    private String loginAttempt;
    private String registrationAttempt;
    private String forumSpam;
    private String lastSeenHuman;
    private String lastSeenUnix;
    private String queryTime;

    public ProxyResults newInstance(String status, String node, String ip, String asn, String provider, String continent, String country, String city, String region, String regionCode, String latitude, String longitude, String isoCode, String proxy, String type, String port, String risk, HashMap<String, String> attackHistory, String total, String vulnerabilityProbing, String loginAttempt, String registrationAttempt, String lastSeenHuman, String lastSeenUnix, String queryTime) {
        ProxyResults results = new ProxyResults();

        this.status = status;
        this.node = node;
        this.ip = ip;
        this.asn = asn;
        this.provider = provider;
        this.continent = continent;
        this.country = country;
        this.city = city;
        this.region = region;
        this.regionCode = regionCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isoCode = isoCode;
        this.proxy = proxy;
        this.type = type;
        this.port = port;
        this.risk = risk;
        this.total = total;
        this.vulnerabilityProbing = vulnerabilityProbing;
        this.loginAttempt = loginAttempt;
        this.registrationAttempt = registrationAttempt;
        this.lastSeenHuman = lastSeenHuman;
        this.lastSeenUnix = lastSeenUnix;
        this.queryTime = queryTime;
        return results;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNode() {
        return this.node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAsn() {
        return this.asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getContinent() {
        return this.continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return this.region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsoCode() {
        return this.isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getProxy() {
        return this.proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getRisk() {
        return this.risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getTotal() {
        return this.total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getVulnerabilityProbing() {
        return this.vulnerabilityProbing;
    }

    public void setVulnerabilityProbing(String vulnerabilityProbing) {
        this.vulnerabilityProbing = vulnerabilityProbing;
    }

    public String getLoginAttempt() {
        return this.loginAttempt;
    }

    public void setLoginAttempt(String loginAttempt) {
        this.loginAttempt = loginAttempt;
    }

    public String getRegistrationAttempt() {
        return this.registrationAttempt;
    }

    public void setRegistrationAttempt(String registrationAttempt) {
        this.registrationAttempt = registrationAttempt;
    }

    public String getLastSeenHuman() {
        return this.lastSeenHuman;
    }

    public void setLastSeenHuman(String lastSeenHuman) {
        this.lastSeenHuman = lastSeenHuman;
    }

    public String getLastSeenUnix() {
        return this.lastSeenUnix;
    }

    public void setLastSeenUnix(String lastSeenUnix) {
        this.lastSeenUnix = lastSeenUnix;
    }

    public String getQueryTime() {
        return this.queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    public String getForumSpam() {
        return this.forumSpam;
    }

    public void setForumSpam(String forumSpam) {
        this.forumSpam = forumSpam;
    }



    public String toString() {
        StringBuffer sb = new StringBuffer("ProxyResults{");
        sb.append("status='").append(this.status).append('\'');
        sb.append(", node='").append(this.node).append('\'');
        sb.append(", ip='").append(this.ip).append('\'');
        sb.append(", asn='").append(this.asn).append('\'');
        sb.append(", provider='").append(this.provider).append('\'');
        sb.append(", continent='").append(this.continent).append('\'');
        sb.append(", country='").append(this.country).append('\'');
        sb.append(", city='").append(this.city).append('\'');
        sb.append(", region='").append(this.region).append('\'');
        sb.append(", regionCode='").append(this.regionCode).append('\'');
        sb.append(", latitude='").append(this.latitude).append('\'');
        sb.append(", longitude='").append(this.longitude).append('\'');
        sb.append(", isoCode='").append(this.isoCode).append('\'');
        sb.append(", proxy='").append(this.proxy).append('\'');
        sb.append(", type='").append(this.type).append('\'');
        sb.append(", port='").append(this.port).append('\'');
        sb.append(", risk='").append(this.risk).append('\'');
        sb.append(", total='").append(this.total).append('\'');
        sb.append(", vulnerabilityProbing='").append(this.vulnerabilityProbing).append('\'');
        sb.append(", loginAttempt='").append(this.loginAttempt).append('\'');
        sb.append(", registrationAttempt='").append(this.registrationAttempt).append('\'');
        sb.append(", forumSpam='").append(this.forumSpam).append('\'');
        sb.append(", lastSeenHuman='").append(this.lastSeenHuman).append('\'');
        sb.append(", lastSeenUnix='").append(this.lastSeenUnix).append('\'');
        sb.append(", queryTime='").append(this.queryTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
