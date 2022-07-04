package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.proxycheck;

public class ProxyCheckSettings {
    private String api_key = "";
    private boolean check_vpn = true;
    private boolean check_asn = true;
    private boolean check_node = true;
    private boolean check_time = true;
    private int check_risk = 1;
    private boolean check_port = true;
    private boolean check_seen = true;
    private int max_detection_days = 7;
    private String ver;
    private String tag = "Ultimate-Antibot-ProxyCheck";


    public ProxyCheckSettings(String key){
        this.api_key = key;
    }

    /**
     * Sets the APIKey for use within queries with https://proxycheck.io
     * @return api_key String - APIKey for https://proxycheck.io
     */
    public String getApi_key() {
        return this.api_key;
    }

    /**
     * @param api_key String - APIKey for https://proxycheck.io
     */
    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    /**
     * @return boolean - should VPNChecks run?
     */
    public boolean isCheck_vpn() {
        return this.check_vpn;
    }

    /**
     * When the vpn flag is supplied we will perform a VPN check on the IP Address and present the result to you.
     * @param check_vpn boolean - Should VPNChecks run?
     */
    public void setCheck_vpn(boolean check_vpn) {
        this.check_vpn = check_vpn;
    }

    /**
     * @return boolean - should ASN be queried?
     */
    public boolean isCheck_asn() {
        return this.check_asn;
    }

    /**
     * When the asn flag is supplied we will perform an ASN check on the IP Address and present you with the provider name, ASN, Continent, Country, Country ISOCode, Region, Region ISOCode, City (if it's in a city) and Lat/Long for the IP Address.
     *
     * Provider, Country, Region and City names will be encoded using utf8 for maximum compatibility.
     * @param check_asn boolean - should ASN be queried?
     */
    public void setCheck_asn(boolean check_asn) {
        this.check_asn = check_asn;
    }

    /**
     * @return boolean - should node be queried?
     */
    public boolean isCheck_node() {
        return this.check_node;
    }

    /**
     * When the node flag is supplied we will display which node within our cluster answered your API call.
     * This is only really needed for diagnosing problems with our support staff.
     * @param check_node boolean - Should node be queried?
     */
    public void setCheck_node(boolean check_node) {
        this.check_node = check_node;
    }

    /**
     * Get if the API should tell you the time it took to query the address
     * @return boolean should the time flag be enabled?
     */
    public boolean isCheck_time() {
        return this.check_time;
    }

    /**
     * When the time flag is supplied we will display how long this query took to be answered by our API excluding network overhead.
     * @param check_time boolean - should the time flag be enabled
     */
    public void setCheck_time(boolean check_time) {
        this.check_time = check_time;
    }

    /**
     * Get the risk/strictness level || ACCEPTABLE VALUES [0,1,2]
     * @return the risk/strictness level
     */
    public int getRiskLevel() {
        return this.check_risk;
    }

    /**
     * When the risk flag is set to 1 we will provide you with a risk score for this IP Address ranging from 0 to 100.
     * Scores below 33 can be considered low risk while scores between 34 and 66 can be considered high risk.
     * Addresses with scores above 66 are considered very dangerous.
     *
     *
     * When the risk flag is set to 2 we will still provide you with the above risk score,
     * but you'll also receive individual counts for each type of attack we witnessed this IP performing across our customer
     * network and our own honeypots within the days you specify by the days flag.
     * @param check_risk the risk/strictness level
     */
    public void setRiskLevel(int check_risk) {
        this.check_risk = check_risk;
    }

    /**
     * Get if the API should tell you the active port the proxy is using
     * @return boolean - should the port flag be enabled?
     */
    public boolean isCheck_port() {
        return this.check_port;
    }

    /**
     * When the port flag is supplied we will display to you the port number we saw this IP Address operating a proxy server on.
     * @param check_port boolean - should the port flag be enabled?
     */
    public void setCheck_port(boolean check_port) {
        this.check_port = check_port;
    }

    /**
     * Get if the API should return the last time there was activity on the address
     * @return should the seen flag be enabled?
     */
    public boolean isCheck_seen() {
        return this.check_seen;
    }

    /**
     * When the seen flag is supplied we will display to you the most recent time we saw this IP Address operating as a proxy server.
     * @param check_seen boolean - should the seen flag be enabled?
     */
    public void setCheck_seen(boolean check_seen) {
        this.check_seen = check_seen;
    }


    /**
     * When the days flag is supplied we will restrict our proxy results to between now and the amount of days you specify.
     * For example if you supplied days=2 we would only check our database for Proxies that we saw within the past 48 hours.
     * By default, without this flag supplied we search within the past 7 days.
     * @return int - days to restrict results to?
     */
    public int getMax_detection_days() {
        return this.max_detection_days;
    }

    /**
     * @param max_detection_days int - days to restrict results to?
     */
    public void setMax_detection_days(int max_detection_days) {
        this.max_detection_days = max_detection_days;
    }

    /**
     * @return String - tag to add along queries for easier identification.
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * When the tag flag is supplied we will tag your query with the message you supply. You can supply your tag using the POST method, and we recommend you do so.
     * @param set_tag String - tag to add along queries for easier identification.
     */
    public void setTag(String set_tag) {
        this.tag = set_tag;
    }

    /**
     * Override the API version selection you have set in the customer dashboard with a different version.
     *
     * You can view the version dates supported by ver from within the dashboard.
     * Format examples: ver=16-August-2020 or ver=17-November-2020
     * @param ver apiVersion to use?
     */
    public void setVer(String ver) {
        this.ver = ver;
    }

    /**
     * @return apiVersion to use?
     */
    public String getVer() {
        return this.ver;
    }
}
