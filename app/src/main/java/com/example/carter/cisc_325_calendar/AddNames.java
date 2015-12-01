package com.example.carter.cisc_325_calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddNames extends AppCompatActivity {
    private EditText emailInpt;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialization stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_names);

        //get references to objects created in content_add_names.xml
        emailInpt = (EditText) findViewById(R.id.emailInpt);
        ListView emailList = (ListView) findViewById(R.id.emailList);

        //TODO: set onSubmit here

        adapter = new MyListAdapter(this, R.layout.list_item, list);
        emailList.setAdapter(adapter);

        Button button = (Button) findViewById((R.id.button));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add("");
            }
        });

        //when enter is pressed
        /*
        emailInpt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String input = emailInpt.getText().toString();
                if (list.size() == 0 && input.length() > 0) {
                    adapter.add(v); //should hold something more substantial
                }
                return false;
            }
        });
        */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //stuff for handling the pink mail icon in the bottom-right corner
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                Intent myIntent = new Intent(view.getContext(), GenerateAvailability.class);
                startActivity(myIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_names, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This allows the buttons in the list to be clickable
    private class MyListAdapter extends ArrayAdapter<String> {
        private  int layout;
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        //called when an object from the list comes on screen (I think)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if object hasn't been initialized
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                EditText inpt = (EditText) convertView.findViewById(R.id.emailInpt);
                inpt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        String input = v.getText().toString();
                        if (input.equals("")) {
                            adapter.remove(""); //remove this element from the array
                        } else if (list.indexOf("") == list.size()-1) {
                            adapter.add(""); //FIX THIS
                        }
                        return false;
                    }
                });
                Button minus = (Button) convertView.findViewById(R.id.minus);
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        list.remove(""); //remove this element from the array
                        notifyDataSetChanged();
                    }
                });
            }
            return convertView;
        }
    }
}
