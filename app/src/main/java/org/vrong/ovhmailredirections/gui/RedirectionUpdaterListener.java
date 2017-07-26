package org.vrong.ovhmailredirections.gui;

import org.vrong.ovhmailredirections.data.Redirection;

import java.util.List;

/**
 * Created by vrong on 23/07/17.
 */
public interface RedirectionUpdaterListener {

    void onRedirectionLoaded(List<Redirection> redirs, RedirectionUpdater.RedirectionAction action);

    void onLoadingFailed(RedirectionUpdater.RedirectionAction action);
}
