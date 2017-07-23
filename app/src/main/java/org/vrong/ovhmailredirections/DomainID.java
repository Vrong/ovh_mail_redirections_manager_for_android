package org.vrong.ovhmailredirections;

/**
 * Created by vrong on 21/07/17.
 */

public class DomainID {
    public DomainID(String applicationKey, String secretApplicationKey, String consumerKey, String domain, String endPoint) {
        this.applicationKey = applicationKey.trim();
        this.secretApplicationKey = secretApplicationKey.trim();
        this.consumerKey = consumerKey.trim();
        this.domain = domain.trim();
        this.endPoint = endPoint.trim();
    }

    private String applicationKey=null;
    private String secretApplicationKey=null;
    private String consumerKey=null;
    private String domain=null;
    private String endPoint=null;

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

    public String buildMail(String mail)
    {
        mail = mail.trim();
        if(!mail.contains("@") && domain != null)
        {
            mail = mail + "@" + domain;
        }
        return mail;
    }
}
