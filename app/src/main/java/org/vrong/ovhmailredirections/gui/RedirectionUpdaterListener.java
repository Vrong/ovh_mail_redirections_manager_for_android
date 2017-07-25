package org.vrong.ovhmailredirections.gui;

import org.vrong.ovhmailredirections.data.Redirection;

import java.util.List;

/**
 * Created by vrong on 23/07/17.
 */
public interface RedirectionUpdaterListener {

    public void onRedirectionLoaded(List<Redirection> redirs, RedirectionUpdater.RedirectionAction action);
    public void onLoadingFailed(RedirectionUpdater.RedirectionAction action);
}
