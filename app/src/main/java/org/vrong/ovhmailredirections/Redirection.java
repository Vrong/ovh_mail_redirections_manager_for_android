package org.vrong.ovhmailredirections;

/**
 * Created by vrong on 21/07/17.
 */

public class Redirection {
    public Redirection(DomainID login, String id, String source, String destination, boolean localCopy)
    {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.login=login;
        this.localCopy=localCopy;
    }

    private boolean localCopy=false;
    private String id=null;
    private String source=null;
    private String destination=null;
    private DomainID login=null;

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getId() {
        return id;
    }

    public DomainID getLogin() {
        return login;
    }

    public boolean isLocalCopy() {
        return localCopy;
    }
}
