package org.vrong.ovhmailredirections;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by vrong on 21/07/17.
 */

public class RedirectionListAdapter extends ArrayAdapter<Redirection> {

    private List<Redirection> redirsList = null;
    private View.OnClickListener deleteClickListener = null;

    public RedirectionListAdapter(Context context, int resource, List<Redirection> objects, View.OnClickListener delete) {
        super(context, resource, objects);
        deleteClickListener = delete;
        redirsList = new ArrayList<>(objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Redirection redir = redirsList.get(position);
        RedirectionViewHolder holder = null;

        if (convertView == null) {
            //Inflate our XML view
            LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.redirection_item, null);

            holder = new RedirectionViewHolder();
            holder.deleteFab = (FloatingActionButton) convertView.findViewById(R.id.deleteFab);
            holder.destinationTv = (TextView) convertView.findViewById(R.id.destination);
            holder.sourceTv = (TextView) convertView.findViewById(R.id.source);
            holder.itemV = convertView;
            holder.deleteFab.setTag(holder);
            holder.destinationTv.setTag(holder);
            holder.sourceTv.setTag(holder);
            holder.itemV.setTag(holder);

            //set listener on the delete fab
            holder.deleteFab.setOnClickListener(deleteClickListener);
        }
        else
        {
            holder = (RedirectionViewHolder) convertView.getTag();
        }
        holder.redirection = redir;

        //set view content
        holder.sourceTv.setText(redir.getSource());
        holder.destinationTv.setText(redir.getDestination());

        return convertView;
    }

    public class RedirectionViewHolder
    {
        public Redirection redirection;
        public View itemV;
        public FloatingActionButton deleteFab;
        public TextView sourceTv, destinationTv;
    }
}
