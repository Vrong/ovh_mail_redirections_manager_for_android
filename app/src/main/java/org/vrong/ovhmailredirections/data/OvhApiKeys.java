package org.vrong.ovhmailredirections.data;

/**
 * Created by vrong on 21/07/17.
 */

public class OvhApiKeys {
    private String applicationKey = null;
    private String secretApplicationKey = null;
    private String consumerKey = null;
    private String domain = null;
    private String endPoint = null;

    public OvhApiKeys(String applicationKey, String secretApplicationKey, String consumerKey, String domain, String endPoint) {
        this.applicationKey = applicationKey.trim();
        this.secretApplicationKey = secretApplicationKey.trim();
        this.consumerKey = consumerKey.trim();
        this.domain = domain.trim();
        this.endPoint = endPoint.trim();
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public String getSecretApplicationKey() {
        return secretApplicationKey;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getDomain() {
        return domain;
    }

    public String getEndPoint() {
        return endPoint;
    }

    /**
     * Add the domain name to the address if needed
     * ex: "test" -> "test@domain.org"
     *
     * @param mail A mail address or whatever mail prefix
     * @return a mail address
     */
    public String buildMail(String mail) {
        mail = mail.trim();
        if (!mail.contains("@") && domain != null) {
            mail = mail + "@" + domain;
        }
        return mail;
    }
}
