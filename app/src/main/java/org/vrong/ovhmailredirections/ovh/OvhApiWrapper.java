package org.vrong.ovhmailredirections.ovh;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vrong.ovhmailredirections.data.OvhApiKeys;
import org.vrong.ovhmailredirections.data.Redirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by vrong on 23/07/17.
 */

public class OvhApiWrapper {

    private OvhApi api = null;

    public OvhApiWrapper(OvhApiKeys id) {
        api = new OvhApi(id);
    }

    public OvhApi getApi() {
        return api;
    }

    /**
     * Try to fetch redirections to know whether the keys are valid or not
     *
     * @return true if valid, false otherwise
     */
    public boolean checkIds() {
        String body = "";
        String path = "";
        String response = null;
        List<Redirection> redirs = null;

        path = "/email/domain/" + api.getId().getDomain() + "/redirection";

        try {
            response = api.get(path, body, true);
            JSONArray ids = new JSONArray(response);
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                System.out.println(id);
            }

            return true;

        } catch (OvhApiException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Redirection> getMailRedirections() {
        String body = "";
        String path = "";
        String response = null;
        List<Redirection> redirs = new ArrayList<>();

        path = "/email/domain/" + api.getId().getDomain() + "/redirection";

        try {
            response = api.get(path, body, true);
        } catch (OvhApiException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println(response);
        Semaphore sem = new Semaphore(50, true);
        List<Thread> tasks = new ArrayList<>();
        try {
            AsyncGetMailRedirection task = null;
            JSONArray ids = new JSONArray(response);
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                System.out.println("Getting redirection id: " + id);

                task = new AsyncGetMailRedirection(redirs, id, sem);
                tasks.add(task);
                task.start();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        if(!tasks.isEmpty()) {
            try {
                for(Thread task: tasks)
                    task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sortRedirections(redirs);
    }

    class AsyncGetMailRedirection extends Thread
    {
        private List<Redirection> list = null;
        private String id;
        Semaphore sem;

        public AsyncGetMailRedirection(List<Redirection> list, String id, Semaphore sem) {
            this.list = list;
            this.id = id;
            this.sem = sem;
        }

        public void run(){
            try {
                sem.acquire();
                Redirection red = getMailRedirection(id);
                if(red == null)
                    System.out.println("Null redirection read for ID" + id);
                else {
                    synchronized (list) {
                        list.add(red);
                    }
                }

                sem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    public List<Redirection> sortRedirections(List<Redirection> list) {
        synchronized (list) {
            if (list.size() > 0) {
                Collections.sort(list, new Comparator<Redirection>() {
                    @Override
                    public int compare(final Redirection object1, final Redirection object2) {
                        return object1.getSource().compareTo(object2.getSource());
                    }
                });
            }
        }

        return list;
    }


    public Redirection getMailRedirection(String id) {
        String body = "";
        String path = "";
        String response = null;
        Redirection redir = null;

        path = "/email/domain/" + api.getId().getDomain() + "/redirection/" + id;

        try {
            response = api.get(path, body, true);
            System.out.println("OvhApiWrapper::getMailRediretions: " + response);
            JSONObject json = new JSONObject(response);
            redir = new Redirection(api.getId(), json.getString("id"), json.getString("from"), json.getString("to"), false);

        } catch (OvhApiException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return redir;
    }

    public boolean createRedirection(Redirection redir) {
        String body = "";
        String path = "";
        String response = null;

        path = "/email/domain/" + api.getId().getDomain() + "/redirection/";

        try {
            JSONObject param = new JSONObject();
            param.put("localCopy", redir.isLocalCopy());
            param.put("to", redir.getDestination());
            param.put("from", redir.getSource());
            body = param.toString();

            response = api.post(path, body, true);
            System.out.println(response);
            JSONObject json = new JSONObject(response);

            return true;

        } catch (OvhApiException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }


    public boolean removeRedirection(Redirection redir) {

        String body = "";
        String path = "";
        String response = null;

        path = "/email/domain/" + api.getId().getDomain() + "/redirection/" + redir.getId();

        try {

            response = api.delete(path, body, true);
            System.out.println(response);
            JSONObject json = new JSONObject(response);

            return true;

        } catch (OvhApiException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }


    public int getTimestamp() {
        String body = "";
        String path = "/auth/time";
        String response = null;
        int ts = 0;

        try {
            response = api.get(path, body, false);
        } catch (OvhApiException e) {
            e.printStackTrace();
            return 0;
        }

        System.out.println(response);
        ts = Integer.parseInt(response);

        return ts;
    }
}
