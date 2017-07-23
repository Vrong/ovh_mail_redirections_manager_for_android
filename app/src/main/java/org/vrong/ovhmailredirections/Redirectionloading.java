package org.vrong.ovhmailredirections;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import java.util.List;

/**
 * Created by vrong on 23/07/17.
 */

class RedirectionLoading extends AsyncTask<Void, Void, Boolean> {

    private final OvhApiWrapper wrapper;
    private final RedirectionAction action;
    private final RedirectionListener listener;
    List<Redirection> redirs;

    RedirectionLoading(OvhApiWrapper wrapper, RedirectionListener listener, RedirectionAction action) {
        this.wrapper = wrapper;
        this.action = action;
        this.listener = listener;
    }

    RedirectionLoading(OvhApiWrapper wrapper, RedirectionListener listener)
    {
        this(wrapper, listener, new RedirectionAction(REDIRECTION_ACTION.SELECTION, null));
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean result = true;
        switch (action.action)
        {
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
        if(result == true)
            redirs = wrapper.getMailRedirections();

        return result;
    }

    @Override
    protected void onPostExecute(final Boolean res) {

        if (redirs != null && res == true) {
            listener.onRedirectionLoaded(redirs, action);
        } else {
            listener.onLoadingFailed(action);
        }
    }

    @Override
    protected void onCancelled() {

    }

    enum REDIRECTION_ACTION {CREATION, MODIFICATION, SUPPRESSION, SELECTION}
    public static class RedirectionAction
    {
        public RedirectionAction(REDIRECTION_ACTION action, Redirection item)
        {
            this.action = action;
            this.item = item;
        }

        public REDIRECTION_ACTION action;
        public Redirection item;
    }

}
