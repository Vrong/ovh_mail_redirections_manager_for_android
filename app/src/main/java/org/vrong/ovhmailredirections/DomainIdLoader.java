package org.vrong.ovhmailredirections;

import android.content.Context;

import java.io.IOException;

/**
 * Created by vrong on 21/07/17.
 */

public class DomainIdLoader {
    public static final String DOMAIN_FILE = "domainid";
    public static final String APPLICATION_KEY = "ak";
    public static final String SECRET_APPLICATION_KEY = "as";
    public static final String CONSUMER_KEY = "ck";
    public static final String ENDPOINT = "ep";
    public static final String DOMAIN = "domain";

    public static DomainID loadDomainID(Context context)
    {
        try{
            PropertyFile prop = new PropertyFile(context, DOMAIN_FILE);
            if(prop.hasKey(APPLICATION_KEY)
                    && prop.hasKey(SECRET_APPLICATION_KEY)
                    && prop.hasKey(CONSUMER_KEY)
                    && prop.hasKey(ENDPOINT)
                    && prop.hasKey(DOMAIN))
                return new DomainID(prop.getValue(APPLICATION_KEY),
                        prop.getValue(SECRET_APPLICATION_KEY),
                        prop.getValue(CONSUMER_KEY),
                        prop.getValue(DOMAIN),
                        prop.getValue(ENDPOINT));
            else
                return null;
        }
        catch (IOException io)
        {
            return null;
        }

    }

    public static boolean saveDomainID(Context context, DomainID id)
    {
        try
        {
            PropertyFile prop = new PropertyFile(context, DOMAIN_FILE);
            prop.putValue(APPLICATION_KEY, id.getApplicationKey());
            prop.putValue(SECRET_APPLICATION_KEY, id.getSecretApplicationKey());
            prop.putValue(CONSUMER_KEY, id.getConsumerKey());
            prop.putValue(DOMAIN, id.getDomain());
            prop.putValue(ENDPOINT, id.getEndPoint());
            return prop.save();
        }
        catch (IOException io)
        {
            return false;
        }
    }


}
