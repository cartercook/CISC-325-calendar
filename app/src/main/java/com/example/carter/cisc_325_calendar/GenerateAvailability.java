package com.example.carter.cisc_325_calendar;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GenerateAvailability extends AppCompatActivity {
    private class Event {
        public Date begin;
        public Date end;
    }
    private ArrayList<Event> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_availability);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        list = new ArrayList<>();

        //GOD BLESS THIS STACK OVERFLOW DIAMOND
        //from http://stackoverflow.com/questions/26844770/how-to-get-access-to-the-calendars-on-a-android-phone
        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION };
        // 0 = January, 1 = February, ...
        Calendar startTime = Calendar.getInstance(); //defaults to today's date
        Calendar endTime= Calendar.getInstance();
        endTime.add(Calendar.WEEK_OF_YEAR, 1); //one week from now
        // the range is all data from 2014
        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ))";
        Boolean hasPermission = this.checkCallingOrSelfPermission("android.permission.READ_CALENDAR") == PackageManager.PERMISSION_GRANTED;
        Toast.makeText(this, "has permission: "+hasPermission.toString(), Toast.LENGTH_SHORT).show(); //debug message
        if (hasPermission) {
            Cursor cursor = this.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);
            // output the events
            if (cursor.moveToFirst()) {
                do { //while (cursor.moveToNext())
                    //Toast.makeText(this.getApplicationContext(), "Start: " + (new Date(cursor.getLong(3))).toString() + ", End: " + (new Date(cursor.getLong(4))).toString(), Toast.LENGTH_LONG).show();
                    //"Title: " + cursor.getString(1)
                    Event newEvent = new Event();
                    newEvent.begin = new Date(cursor.getLong(3));
                    newEvent.end = new Date(cursor.getLong(4));

                    Event prevEvent = null; //the event that comes just before the one we're inserting
                    int i = 0; //position at which we are inserting newEvent
                    while (i < list.size() && newEvent.begin.before(list.get(i).begin)) { //iterate until we find an event that starts after newEvent
                        prevEvent = list.get(i);
                        i++;
                    }
                    if (prevEvent == null || prevEvent.end.before(newEvent.end)) { //if newEvent is not completely contained inside prevEvent
                        if (prevEvent != null) { //if there is an event before newEvent
                            if (prevEvent.end.after(newEvent.begin)) { //if they overlap, newEvent swallows prevEvent
                                newEvent.begin = prevEvent.begin;
                                list.remove(i); //remove prev
                                i--;
                            }
                        }
                        Event nextEvent;
                        if (i+1 < list.size()) { //if an event exists after newEvent
                            nextEvent = list.get(i+1);
                        }
                        while (i+1 < list.size()) { //if other events exist after newEvent
                            nextEvent = list.get(i+1); //get the next event
                            if (nextEvent.begin.after(newEvent.end)) { //if these events do not overlap
                                break; //we're done here
                            }
                            //swallow all nextEvents that overlap newEvent
                            if (nextEvent.end.before(newEvent.end)) { //nextEvent is completely contained inside newEvent
                                list.remove(i + 1);
                            } else { //newEvent overlaps but does not totally contain nextEvent
                                newEvent.end = nextEvent.end;
                                list.remove(i + 1);
                                break;
                            }
                        }
                        list.add(i, newEvent); // finally, add our new event into list
                    }
                    Toast.makeText(this.getApplicationContext(), "list size: " + list.size(), Toast.LENGTH_LONG).show();
                } while (cursor.moveToNext());
            }
        }

        //we now have a list full of non-overlapping events
        //Find the times between events
        ArrayList<Event> inverseList = new ArrayList<>(); //the space between events
        Event firstEvent = new Event();
        firstEvent.begin = startTime.getTime();
        if (list.size() <= 0) {
            firstEvent.end = endTime.getTime();
            list.add(firstEvent);
        } else {
            firstEvent.end = list.get(0).begin;
            list.add(firstEvent);
            for (int i = 0; i < list.size() - 1; i++) {
                Event newEvent = new Event();
                newEvent.begin = list.get(i).end;
                newEvent.end = list.get(i + 1).begin;
                inverseList.add(newEvent);
            }
            Event lastEvent = new Event();
            lastEvent.begin = list.get(list.size() - 1).end;
            lastEvent.end = endTime.getTime();
            inverseList.add(lastEvent);
        }

        //convert to string
        String displayString = new String();
        for (int i=0; i < inverseList.size(); i++) {
            Event event = inverseList.get(i);
            displayString += "Event "+(i+1)+": "+event.begin+" - "+event.end+"\n";
        }

        TextView text = (TextView) findViewById(R.id.textView2);
        text.setText(displayString);

        //auto generated
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

}
