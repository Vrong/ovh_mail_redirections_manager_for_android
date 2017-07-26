package org.vrong.ovhmailredirections.misc;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by uvrong on 26/12/16.
 */

public class PropertyFile {

    private HashMap<String, String> values;
    private Context context;
    private String filename = null;


    public PropertyFile(Context context, String file) throws IOException {
        this.context = context;
        this.filename = file;

        reload();
        /*
        if(!reload())
            throw new IOException();*/
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public boolean hasKey(String key) {
        return values.containsKey(key);
    }

    public void putValue(String key, String value) {
        values.put(key, value);
    }

    public boolean matchValue(String key, String value) {
        if (values.containsKey(key)) {
            return values.get(key).equals(value);
        } else if (value == null) {
            return true;
        }

        return false;
    }


    public void removeKey(String key) {
        values.remove(key);
    }

    public boolean reload() {
        values = new HashMap<>();
        File dir = context.getFilesDir();
        try {
            InputStream is = new FileInputStream(new File(dir, filename));
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line = r.readLine();

            while (line != null) {
                if (line.length() == 0)
                    continue;
                String tab[] = line.split(" ");
                switch (tab.length) {
                    case 1:
                        values.put(tab[0], "");
                        break;
                    case 2:
                        values.put(tab[0], tab[1]);
                        break;
                }
                line = r.readLine();
            }
            r.close();
            is.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean save() {
        File dir = context.getFilesDir();
        try {
            OutputStream os = new FileOutputStream(new File(dir, filename));
            PrintWriter w = new PrintWriter(new OutputStreamWriter(os));

            for (Map.Entry<String, String> pair : values.entrySet()) {
                w.println(pair.getKey() + " " + pair.getValue());
            }

            w.close();
            os.close();

        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public String dumpContent() {
        StringBuilder sb = new StringBuilder();
        for (String key : values.keySet()) {
            sb.append(key)
                    .append(" -> ")
                    .append(values.get(key))
                    .append("\n");
        }
        return sb.toString();
    }
}
