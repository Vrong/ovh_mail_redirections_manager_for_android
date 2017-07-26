package org.vrong.ovhmailredirections.gui;

import android.os.AsyncTask;

import org.vrong.ovhmailredirections.data.Redirection;
import org.vrong.ovhmailredirections.ovh.OvhApiWrapper;

import java.util.List;

/**
 * Created by vrong on 23/07/17.
 */

class RedirectionUpdater extends AsyncTask<Void, Void, Boolean> {

    private final OvhApiWrapper wrapper;
    private final RedirectionAction action;
    private final RedirectionUpdaterListener listener;
    List<Redirection> redirs;

    RedirectionUpdater(OvhApiWrapper wrapper, RedirectionUpdaterListener listener, RedirectionAction action) {
        this.wrapper = wrapper;
        this.action = action;
        this.listener = listener;
    }

    RedirectionUpdater(OvhApiWrapper wrapper, RedirectionUpdaterListener listener) {
        this(wrapper, listener, new RedirectionAction(REDIRECTION_ACTION.SELECTION, null));
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean result = true;
        switch (action.action) {
            case CREATION:
                result = wrapper.createRedirection(action.item);
                break;
            case MODIFICATION:
                //wrapper.modifyRedirection(action.item);
                break;
            case SUPPRESSION:
                result = wrapper.removeRedirection(action.item);
                break;
        }
        if (result)
            redirs = wrapper.getMailRedirections();

        return result;
    }

    @Override
    protected void onPostExecute(final Boolean res) {

        if (redirs != null && res) {
            listener.onRedirectionLoaded(redirs, action);
        } else {
            listener.onLoadingFailed(action);
        }
    }

    @Override
    protected void onCancelled() {

    }

    enum REDIRECTION_ACTION {CREATION, MODIFICATION, SUPPRESSION, SELECTION}

    public static class RedirectionAction {
        public REDIRECTION_ACTION action;
        public Redirection item;
        public RedirectionAction(REDIRECTION_ACTION action, Redirection item) {
            this.action = action;
            this.item = item;
        }
    }

}
