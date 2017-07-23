package org.vrong.ovhmailredirections;

import java.util.List;

/**
 * Created by vrong on 23/07/17.
 */
public interface RedirectionListener {

    public void onRedirectionLoaded(List<Redirection> redirs, RedirectionLoading.RedirectionAction action);
    public void onLoadingFailed(RedirectionLoading.RedirectionAction action);
}
