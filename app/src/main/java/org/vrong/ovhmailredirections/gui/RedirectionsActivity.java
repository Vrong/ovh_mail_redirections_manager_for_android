package org.vrong.ovhmailredirections.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import org.vrong.ovhmailredirections.R;
import org.vrong.ovhmailredirections.data.DomainIdLoader;
import org.vrong.ovhmailredirections.data.OvhApiKeys;
import org.vrong.ovhmailredirections.data.Redirection;
import org.vrong.ovhmailredirections.misc.PropertyFile;
import org.vrong.ovhmailredirections.ovh.OvhApiWrapper;

import java.io.IOException;
import java.util.List;

public class RedirectionsActivity extends AppCompatActivity implements RedirectionUpdaterListener {

    ListView redirLv = null;
    RedirectionListAdapter adapter = null;
    FloatingActionButton fab;
    private OvhApiWrapper wrapper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirections);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        redirLv = (ListView) findViewById(R.id.redir_listview);


        // filter
        EditText filter = (EditText) findViewById(R.id.textfilter);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(RedirectionsActivity.this.redirLv != null)
                {
                    if(s.length() > 0) {
                        RedirectionsActivity.this.redirLv.setFilterText(s.toString());
                        RedirectionsActivity.this.redirLv.setTextFilterEnabled(true);
                    }
                    else {
                        RedirectionsActivity.this.redirLv.setTextFilterEnabled(false);
                        RedirectionsActivity.this.redirLv.;
                    }
                }*/
                if(RedirectionsActivity.this.adapter != null)
                {
                    RedirectionsActivity.this.adapter.getFilter().filter(s.toString());
                    RedirectionsActivity.this.adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        // fab actions and alertdialogs
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(RedirectionsActivity.this);
                View promptsView = li.inflate(R.layout.redirection_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        RedirectionsActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText fromEt = promptsView
                        .findViewById(R.id.from);
                final EditText toEt = promptsView
                        .findViewById(R.id.to);
                final Switch localCopyEt = promptsView
                        .findViewById(R.id.local_copy);

                try {
                    PropertyFile prop = new PropertyFile(RedirectionsActivity.this, "config");
                    String last_address = prop.getValue("last_to");
                    if (last_address != null)
                        toEt.setText(last_address);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("New redirection")
                        .setPositiveButton("Create", null)
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (!fromEt.getText().toString().isEmpty() &&
                                        !toEt.getText().toString().isEmpty()) {
                                    OvhApiKeys domid = RedirectionsActivity.this.wrapper.getApi().getId();
                                    Redirection redir = new Redirection(domid, "0",
                                            domid.buildMail(fromEt.getText().toString()),
                                            domid.buildMail(toEt.getText().toString()),
                                            localCopyEt.isActivated());
                                    try {
                                        PropertyFile prop = new PropertyFile(RedirectionsActivity.this, "config");
                                        prop.putValue("last_to", redir.getDestination());
                                        prop.save();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    setLoading(true);
                                    RedirectionUpdater.RedirectionAction action =
                                            new RedirectionUpdater.RedirectionAction(RedirectionUpdater.REDIRECTION_ACTION.CREATION, redir);
                                    new RedirectionUpdater(RedirectionsActivity.this.wrapper, RedirectionsActivity.this, action)
                                            .execute();

                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });

                // show it
                alertDialog.show();

            }
        });

        ViewUtils.changeDrawableColor(Color.WHITE, fab.getDrawable());

    }

    @Override
    public void onResume() {
        super.onResume();

        setLoading(true);
        OvhApiKeys id = DomainIdLoader.loadDomainID(this);
        if (id == null) {
            Intent i = new Intent(this, LoginActivity.class);
            this.startActivity(i);
        } else {
            wrapper = new OvhApiWrapper(id);
            new RedirectionUpdater(wrapper, this).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redirections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, LoginActivity.class);
            this.startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRedirectionLoaded(List<Redirection> redirections, RedirectionUpdater.RedirectionAction action) {

        View.OnClickListener delete = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RedirectionListAdapter.RedirectionViewHolder holder = (RedirectionListAdapter.RedirectionViewHolder) view.getTag();

                // remove redirection
                new AlertDialog.Builder(RedirectionsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove this redirection ?")
                        .setMessage("Are you sure you want to remove this redirection :\nfrom " + holder.redirection.getSource() + " to " + holder.redirection.getDestination() + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                setLoading(true);
                                new RedirectionUpdater(RedirectionsActivity.this.wrapper, RedirectionsActivity.this,
                                        new RedirectionUpdater.RedirectionAction(RedirectionUpdater.REDIRECTION_ACTION.SUPPRESSION, holder.redirection))
                                        .execute();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };

        redirLv.setAdapter(adapter = new RedirectionListAdapter(this, R.layout.redirection_item, redirections, delete));

        String msg = "Action performed !";
        switch (action.action) {
            case SELECTION:
                msg = "Loading success.";
                break;
            case SUPPRESSION:
                msg = action.item.getSource() + " has been removed.";
                break;
            case CREATION:
                msg = action.item.getSource() + " has been created.";
        }

        Snackbar.make(RedirectionsActivity.this.fab, msg, 3000)
                .setAction("Action", null).show();
        setLoading(false);
    }

    @Override
    public void onLoadingFailed(RedirectionUpdater.RedirectionAction action) {


        String msg = "Action performed !";
        switch (action.action) {
            case SELECTION:
                msg = "Failed to authenticate, check whether the keys are correct or you have the right access to the API.";
                break;
            case SUPPRESSION:
                msg = "Error occured while removing " + action.item.getSource() + ".";
                break;
            case CREATION:
                msg = "Error occured while adding " + action.item.getDestination() + ".";
        }

        Snackbar.make(RedirectionsActivity.this.fab, msg, 3000)
                .setAction("Action", null).show();

        if (action.action == RedirectionUpdater.REDIRECTION_ACTION.SELECTION) {
            Intent i = new Intent(RedirectionsActivity.this, LoginActivity.class);
            RedirectionsActivity.this.startActivity(i);
        }

        setLoading(false);

    }


    public void setLoading(boolean loading) {
        if (loading) {
            this.findViewById(R.id.redirection_progress).setVisibility(View.VISIBLE);
            this.findViewById(R.id.fab).setVisibility(View.GONE);
            this.findViewById(R.id.redir_listview).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.redirection_progress).setVisibility(View.GONE);
            this.findViewById(R.id.fab).setVisibility(View.VISIBLE);
            this.findViewById(R.id.redir_listview).setVisibility(View.VISIBLE);
        }
    }
}

