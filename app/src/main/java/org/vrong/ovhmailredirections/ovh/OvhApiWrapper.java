package org.vrong.ovhmailredirections.ovh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vrong.ovhmailredirections.data.OvhApiKeys;
import org.vrong.ovhmailredirections.data.Redirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        String path = "/email/domain/" + api.getId().getDomain() + "/redirection";

        try {
            String response = api.get(path, body, true);
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
        List<Redirection> redirs = new ArrayList<>();
        String path = "/email/domain/" + api.getId().getDomain() + "/redirection";

        String response;
        try {
            response = api.get(path, body, true);
        } catch (OvhApiException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println(response);

        try {
            JSONArray ids = new JSONArray(response);
            for (int i = 0; i < ids.length(); i++) {
                String id = ids.getString(i);
                System.out.println(id);

                Redirection rd = getMailRedirection(id);
                if (rd != null)
                    redirs.add(rd);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return sortRedirections(redirs);
    }

    public List<Redirection> sortRedirections(List<Redirection> list) {
        if (list.size() > 0) {
            Collections.sort(list, new Comparator<Redirection>() {
                @Override
                public int compare(final Redirection object1, final Redirection object2) {
                    return object1.getSource().compareTo(object2.getSource());
                }
            });
        }

        return list;
    }


    public Redirection getMailRedirection(String id) {
        String body = "";
        String path = "/email/domain/" + api.getId().getDomain() + "/redirection/" + id;

        String response;
        Redirection redir;
        try {
            response = api.get(path, body, true);
            System.out.println(response);
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
        String path = "/email/domain/" + api.getId().getDomain() + "/redirection/";

        String response;
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
        String path = "/email/domain/" + api.getId().getDomain() + "/redirection/" + redir.getId();

        String response;
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
        String path = "/auth/time";

        int ts = 0;

        String body = "";
        String response;
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
