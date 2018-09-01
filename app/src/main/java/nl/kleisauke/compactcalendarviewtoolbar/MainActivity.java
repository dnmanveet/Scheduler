package nl.kleisauke.compactcalendarviewtoolbar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import nl.kleisauke.compactcalendarviewtoolbar.db.Task;
import nl.kleisauke.compactcalendarviewtoolbar.db.TaskHelper;

import static android.widget.Toast.LENGTH_SHORT;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int RC_SIGN_IN = 9001;
    private AppBarLayout appBarLayout;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private final SimpleDateFormat df = new SimpleDateFormat(" HH:mm:ss");
    private CompactCalendarView compactCalendarView;

    private static final int REQ_CODE_SPEECH_INPUT = 300;
    private FloatingActionButton mSpeakBtn;
    private FloatingActionButton mWriteBtn;
    private boolean isExpanded = false;
    private boolean ascending = true;
    private int quantity = 0;
    private String message;
    public String dat;
    private String tim;
    private String task;
    private AlertDialog Taskdialog;
    private String task1;
    private String g, h;
    private EditText TaskTime;
    private String task5;
    private int isStrike;
    private int quanti =0;

    ArrayList<TaskItem> taskItemList = new ArrayList<>();

    ListView list;
    TaskAdapter adapter;

    private TaskHelper mHelper;
    TextView textCartItemCount;
    int mCartItemCount;
    private MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.clear();
        cal.set(2018,8,25,21,51);

        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // cal.add(Calendar.SECOND, 5);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);



        TextView t = (TextView) findViewById(R.id.tt);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        appBarLayout = findViewById(R.id.app_bar_layout);
        compactCalendarView = findViewById(R.id.compactcalendar_view);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setTitle("Scheduler");

        mHelper = new TaskHelper(this);

        mSpeakBtn = (FloatingActionButton) findViewById(R.id.fab);
        mWriteBtn = (FloatingActionButton) findViewById(R.id.fab1);
        TextView ta = (TextView) findViewById(R.id.tv3);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        mWriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText taskEditText = new EditText(MainActivity.this);
                taskEditText.setHint("Type task");
                TaskTime = new EditText(MainActivity.this);
                TaskTime.setHint("date and time");
                TaskTime.setVisibility(View.VISIBLE);
                TaskTime.setText("Click this to add due date");
                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(50, 0, 50, 0);
                layout.addView(taskEditText,params);
                layout.addView(TaskTime,params);

                TaskTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                TaskTime.setVisibility(View.VISIBLE);
                                TaskTime.setTextColor(Color.BLACK);
                                dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                                tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                                task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                                TaskTime.setText(task5);
                                Taskdialog.show();

                            }

                            @Override
                            public void onCancel() {
                                Taskdialog.show();

                            }
                        }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();

                    }
                });



                Taskdialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("New Task").setMessage("Add a new task").setIcon(R.drawable.round_insert).setView(layout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, int which) {
                        taskEditText.requestFocus();
                        task = String.valueOf(taskEditText.getText());
                        if(TaskTime.getText().toString() != null || TaskTime.getText().toString() != ""){
                            TaskTime.setVisibility(View.VISIBLE);

                        }
                        else if(TaskTime.getText().toString() == null || TaskTime.getText().toString() == ""){
                            TaskTime.setVisibility(View.VISIBLE);
                            TaskTime.setText("Click this to add due date");

                        }
                        if (taskItemList.contains(task)) {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Already Added..", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                                    .show();
                        } else if (task == null || task.trim().equals("")) {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Add task..", Snackbar.LENGTH_LONG)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                                    .show();
                        } else {
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            taskItemList.add(new TaskItem(task,TaskTime.getText().toString()));
                            adapter.add(taskItemList.get(taskItemList.size() - 1));
                            TaskNotification();
                            TaskItem item = taskItemList.get(taskItemList.size() - 1); //adapter.getItem(0);
                            adapter.notifyDataSetChanged();
                            mCartItemCount = adapter.getCount();
                            setupBadge();
                            ContentValues values = new ContentValues();
                            if (TaskTime.getText().toString() == null || TaskTime.getText().toString() == "") {
                                TaskTime.setVisibility(View.VISIBLE);
                                TaskTime.setText("Click this to add due date");
                                values.put(Task.TaskEntry.COL_TASK_TITLE, task);
                                values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                            }
                            else {
                                values.put(Task.TaskEntry.COL_TASK_TITLE, task);
                                values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                            }
                            db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                            TaskNotification();
                            db.close();
                            updateUI();
                        }
                    }
                }).setNegativeButton("Cancel", null).setNeutralButton("Add Due Date", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                            @Override
                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                TaskTime.setVisibility(View.VISIBLE);
                                TaskTime.setTextColor(Color.BLACK);
                                dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                                tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                                task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                                TaskTime.setText(task5);
                                TaskTime.setEnabled(false);
                                Taskdialog.show();

                            }

                            @Override
                            public void onCancel() {
                                Taskdialog.show();

                            }
                        }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();
                    }
                }).create();
                Taskdialog.show();
            }
        });


        list = (ListView) findViewById(R.id.list);


        adapter = new TaskAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(R.id.task_title);
                TextView time = view.findViewById(R.id.timer);


                Taskdialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle(text.getText().toString()).setMessage(time.getText().toString()).setIcon(R.drawable.round_insert).setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, int which) {

                        if(isStrike != 0) {
                            text.setBackgroundColor(Color.WHITE);
                            text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            adapter.notifyDataSetChanged();
                            isStrike = 0;
                        }
                        else{

                            final EditText taskEditText = new EditText(MainActivity.this);
                            taskEditText.setText(text.getText().toString());
                            taskEditText.setHint("Type task");
                            TaskTime = new EditText(MainActivity.this);
                            TaskTime.setHint("date and time");
                            TaskTime.setVisibility(View.VISIBLE);
                            TaskTime.setText("Click this to add due date");
                            LinearLayout layout = new LinearLayout(getApplicationContext());
                            layout.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins(50, 0, 50, 0);
                            layout.addView(taskEditText,params);
                            layout.addView(TaskTime,params);

                            Taskdialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("Edit Task").setMessage("Edit as per required").setIcon(R.drawable.pen).setView(layout).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogue, int which) {
                                    taskEditText.requestFocus();
                                    task = String.valueOf(taskEditText.getText());
                                    if(TaskTime.getText().toString() != null || TaskTime.getText().toString() != ""){
                                        TaskTime.setVisibility(View.VISIBLE);

                                    }
                                    else if(TaskTime.getText().toString() == null || TaskTime.getText().toString() == ""){
                                        TaskTime.setVisibility(View.VISIBLE);
                                        TaskTime.setText("Click this to add due date");

                                    }
                                    if (taskItemList.contains(task)) {
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Already Added..", Snackbar.LENGTH_LONG)
                                                .setAction("CLOSE", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                    }
                                                })
                                                .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                                                .show();
                                    } else if (task == null || task.trim().equals("")) {
                                        View parentLayout = findViewById(android.R.id.content);
                                        Snackbar.make(parentLayout, "Add task..", Snackbar.LENGTH_LONG)
                                                .setAction("CLOSE", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {

                                                    }
                                                })
                                                .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                                                .show();
                                    } else {
                                        SQLiteDatabase db = mHelper.getWritableDatabase();
//                                taskItemList.add(new TaskItem(task,TaskTime.getText().toString()));
//                                adapter.add(taskItemList.get(position));
                                        TaskItem taskItem = adapter.getItem(position);
                                        taskItemList.remove(taskItem);
                                        adapter.remove(taskItem);
                                        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{taskItem.getTaskString()});
                                        taskItemList.add(position,new TaskItem(taskEditText.getText().toString(),TaskTime.getText().toString()));
                                        TaskItem item = taskItemList.get(taskItemList.size() - 1); //adapter.getItem(0);
                                        adapter.notifyDataSetChanged();
                                        mCartItemCount = adapter.getCount();
                                        ContentValues values = new ContentValues();
                                        if (TaskTime.getText().toString() == null || TaskTime.getText().toString() == "") {
                                            TaskTime.setVisibility(View.VISIBLE);
                                            TaskTime.setText("Click this to add due date");
                                            values.put(Task.TaskEntry.COL_TASK_TITLE, taskEditText.getText().toString());
                                            values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                                        }
                                        else {
                                            values.put(Task.TaskEntry.COL_TASK_TITLE, taskEditText.getText().toString());
                                            values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                                        }
                                        db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                        mCartItemCount++;
                                        setupBadge();
                                        db.close();
                                        updateUI();
                                    }
                                }
                            }).setNegativeButton("Cancel", null).setNeutralButton("Add Due Date", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                                        @Override
                                        public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                            TaskTime.setVisibility(View.VISIBLE);
                                            TaskTime.setTextColor(Color.BLACK);
                                            dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                                            tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                                            task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                                            TaskTime.setText(task5);
                                            TaskTime.setEnabled(false);
                                            Taskdialog.show();

                                        }

                                        @Override
                                        public void onCancel() {
                                            Taskdialog.show();

                                        }
                                    }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();
                                }
                            }).create();
                            Taskdialog.show();

                        }

                    }

                }).setNegativeButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = "Task : "+ text.getText().toString() + "\n"+"Due : "+ time.getText().toString() ;
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
                        startActivity(Intent.createChooser(sharingIntent, "Share text via"));

                    }
                }).setNeutralButton("Cancel", null).create();
                Taskdialog.show();




            }
        });


        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(list, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    View parent = (View) listView.getParent();
                    TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
                    TaskItem taskItem = adapter.getItem(position);
                    View parentLayout = findViewById(android.R.id.content);
                    mCartItemCount = adapter.getCount();
                    Snackbar.make(parentLayout, "'" + taskItem.getTaskString() + "'" + " Task deleted.. ", Snackbar.LENGTH_SHORT)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                      Toast.makeText(MainActivity.this,"hii",Toast.LENGTH_LONG).show();
                                      quanti = 1;
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                            .show();
                    if(quanti == 1){
                        Toast.makeText(MainActivity.this,"undo Test",Toast.LENGTH_LONG).show();
                        quanti = 0;
                    }
                    else if(quanti == 0) {
                        taskItemList.remove(taskItem);
                        adapter.remove(taskItem);
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{taskItem.getTaskString()});
                        db.close();
                        updateUI();
                    }
                    if (taskItemList.size() == 0 || taskItemList.size() < 0) {
                        ImageView empty = (ImageView) findViewById(R.id.empty1);
                        empty.setVisibility(View.VISIBLE);
                        list.setEmptyView(findViewById(R.id.empty1));
                        EmptyNotification();
                        mCartItemCount = taskItemList.size();;
                        setupBadge();
                    } else {
                        ImageView empty = (ImageView) findViewById(R.id.empty1);
                        empty.setVisibility(View.INVISIBLE);
                        list.setAdapter(adapter);
                        TaskNotification();
                        mCartItemCount = taskItemList.size();;
                        setupBadge();
                    }

                }


            }
        });
        list.setOnTouchListener(touchListener);

        //List view onLongClick listener
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long l) {

                TextView text = view.findViewById(R.id.task_title);
                TextView time = view.findViewById(R.id.timer);
                text.setBackgroundColor(Color.CYAN);
                time.setBackgroundColor(Color.CYAN);
                isStrike = 1;
                text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                time.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                adapter.notifyDataSetChanged();


                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Delete??").setIcon(R.drawable.round_delete).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, int which) {
                        if (quantity == 1) {
                            TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
                            TaskItem taskItem = adapter.getItem(i);
                            taskItemList.remove(taskItem);
                            adapter.remove(taskItem);
                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{taskItem.getTaskString()});
                            db.close();
                            updateUI();

                            Toast.makeText(getApplicationContext(), "Task deleted.. ", Toast.LENGTH_LONG).show();
                            if (adapter.getCount() == 0) {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.VISIBLE);
                                list.setEmptyView(findViewById(R.id.empty1));
                                EmptyNotification();
                            }
                            mCartItemCount = taskItemList.size();
                            View parent = (View) list.getParent();
                            Snackbar.make(parent, "'" + taskItem.getTaskString() + "'" + " Task deleted.. ", Snackbar.LENGTH_SHORT).setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            }).setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright)).show();

                            setupBadge();
                        } else {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.INVISIBLE);
                                list.setAdapter(adapter);
                                TaskNotification();
                                mCartItemCount = taskItemList.size();;
                                setupBadge();
                        }
                        if (quantity == 0) {
                            taskItemList.remove(adapter.getItem(i));
                            TaskItem taskItem = adapter.getItem(i);
                            adapter.remove(adapter.getItem(i));
                            list.setAdapter(adapter);
                            View parent = (View) view.getParent();
                            SQLiteDatabase db = mHelper.getWritableDatabase();
//                            text2.setBackgroundColor(Color.WHITE);
//                            text2.setPaintFlags(text2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_TITLE + " = ?", new String[]{taskItem.getTaskString()});
                            db.close();
                            updateUI();
                            if (adapter.getCount() == 0) {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.VISIBLE);
                                list.setEmptyView(findViewById(R.id.empty1));
                                EmptyNotification();
                                mCartItemCount = taskItemList.size();;
                                setupBadge();
                            } else {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.INVISIBLE);
                                list.setAdapter(adapter);
                                TaskNotification();
                                mCartItemCount = taskItemList.size();;
                                setupBadge();
                            }
                        }
                    }
                })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView text = view.findViewById(R.id.task_title);
                                TextView time = view.findViewById(R.id.timer);
                                text.setBackgroundColor(Color.WHITE);
                                time.setBackgroundColor(Color.WHITE);

                                isStrike = 0;
                                text.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                time.setPaintFlags(text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                                adapter.notifyDataSetChanged();

                            }
                        }).create();
                dialog.show();
                return true;

            }
        });


        setTitle("Scheduler");


        appBarLayout = findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        compactCalendarView = findViewById(R.id.compactcalendar_view);

        // Force English
        compactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        compactCalendarView.setShouldDrawDaysHeader(true);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
//                Calendar cl = Calendar.getInstance();
//                String year = String.valueOf(cl.get(Calendar.YEAR));
//                String mon = String.valueOf(cl.get(Calendar.MONTH));
//                String da = String.valueOf(cl.get(Calendar.DATE));
//
//                // Create a new intent to open the {@link FamilyActivity}
//                if((String.valueOf(dateClicked.getDate()).equals(da))) {
//
//                    if(String.valueOf(dateClicked.getMonth()).equals(mon)) {
//
//                        if(String.valueOf(dateClicked.getYear()).equals(year)) {
//                            setContentView(R.layout.activity_main);
//                        }
//
//                    }
//                    else {
//                        setContentView(R.layout.activity_update);
//                        Intent newIntent = new Intent(MainActivity.this, update.class);
//                         message = dateFormat.format(dateClicked);
//                        newIntent.putExtra("key", message);
//                        // Start the new activity
//                        startActivity(newIntent);
//                    }
//                }
//                else{
//                    setContentView(R.layout.activity_update);
//                    Intent newIntent = new Intent(MainActivity.this, update.class);
//                     message = dateFormat.format(dateClicked);
//                    newIntent.putExtra("key", message);
//                    // Start the new activity
//                    startActivity(newIntent);
//                }
//
//
            }
//
//
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));

            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        final ImageView arrow = findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(v -> {
            float rotation = isExpanded ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();

            isExpanded = !isExpanded;
            appBarLayout.setExpanded(isExpanded, true);
        });

//        updateUI();


//        Edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                v.findViewById(R.id.task_title);
//
//                final EditText taskEditText = new EditText(MainActivity.this);
//                taskEditText.setText(v.findViewById(R.id.task_title));
//                taskEditText.setHint("Type task");
//                TaskTime = new EditText(MainActivity.this);
//                TaskTime.setHint("date and time");
//                TaskTime.setVisibility(View.VISIBLE);
//                TaskTime.setText("Click this to add due date");
//                LinearLayout layout = new LinearLayout(getApplicationContext());
//                layout.setOrientation(LinearLayout.VERTICAL);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(50, 0, 50, 0);
//                layout.addView(taskEditText,params);
//                layout.addView(TaskTime,params);
//
//
//
//                Taskdialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("New Task").setMessage("Add a new task").setIcon(R.drawable.round_insert).setView(layout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogue, int which) {
//                        taskEditText.requestFocus();
//                        task = String.valueOf(taskEditText.getText());
//                        if(TaskTime.getText().toString() != null || TaskTime.getText().toString() != ""){
//                            TaskTime.setVisibility(View.VISIBLE);
//
//                        }
//                        else if(TaskTime.getText().toString() == null || TaskTime.getText().toString() == ""){
//                            TaskTime.setVisibility(View.VISIBLE);
//                            TaskTime.setText("Click this to add due date");
//
//                        }
//                        if (taskItemList.contains(task)) {
//                            View parentLayout = findViewById(android.R.id.content);
//                            Snackbar.make(parentLayout, "Already Added..", Snackbar.LENGTH_LONG)
//                                    .setAction("CLOSE", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                        }
//                                    })
//                                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
//                                    .show();
//                        } else if (task == null || task.trim().equals("")) {
//                            View parentLayout = findViewById(android.R.id.content);
//                            Snackbar.make(parentLayout, "Add task..", Snackbar.LENGTH_LONG)
//                                    .setAction("CLOSE", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//
//                                        }
//                                    })
//                                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
//                                    .show();
//                        } else {
//                            SQLiteDatabase db = mHelper.getWritableDatabase();
//                            taskItemList.add(new TaskItem(task,TaskTime.getText().toString()));
//                            adapter.add(taskItemList.get(taskItemList.size() - 1));
//                            TaskItem item = taskItemList.get(taskItemList.size() - 1); //adapter.getItem(0);
//                            adapter.notifyDataSetChanged();
//                            mCartItemCount = adapter.getCount();
//                            setupBadge();
//                            ContentValues values = new ContentValues();
//                            if (TaskTime.getText().toString() == null || TaskTime.getText().toString() == "") {
//                                TaskTime.setVisibility(View.VISIBLE);
//                                TaskTime.setText("Click this to add due date");
//                                values.put(Task.TaskEntry.COL_TASK_TITLE, task);
//                                values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
//                            }
//                            else {
//                                values.put(Task.TaskEntry.COL_TASK_TITLE, task);
//                                values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
//                            }
//                            db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                            db.close();
//                            updateUI();
//                        }
//                    }
//                }).setNegativeButton("Cancel", null).setNeutralButton("Add Due Date", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
//                            @Override
//                            public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
//                                TaskTime.setVisibility(View.VISIBLE);
//                                TaskTime.setTextColor(Color.BLACK);
//                                dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
//                                tim = String.valueOf(hour12) + ":" + String.valueOf(min);
//                                task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
//                                TaskTime.setText(task5);
//                                Taskdialog.show();
//
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                Taskdialog.show();
//
//                            }
//                        }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();
//                    }
//                }).create();
//                Taskdialog.show();
//
//            }
//        });
    }

    private void updateUI() {

        taskItemList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Task.TaskEntry.TABLE, new String[]{Task.TaskEntry.COL_TASK_TITLE, Task.TaskEntry.COL_TASK_DATE}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(Task.TaskEntry.COL_TASK_TITLE);
            String taskTitle = cursor.getString(cursor.getColumnIndex(Task.TaskEntry.COL_TASK_TITLE));
            String dateTitle = cursor.getString(cursor.getColumnIndex(Task.TaskEntry.COL_TASK_DATE));
            taskItemList.add(new TaskItem(taskTitle, dateTitle));

        }
        for(TaskItem taskItem : taskItemList)
            Log.d("DB", taskItem.toString());

        if (adapter == null) {
            adapter = new TaskAdapter(MainActivity.this, R.layout.item_todo, taskItemList);
            list.setAdapter(adapter);

        } else {
            adapter.clear();
            adapter.addAll(taskItemList);
            adapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menuItem = menu.findItem(R.id.action_cart);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }
    private void setupBadge() {
//        if(mCartItemCount == 0){
//            textCartItemCount.setVisibility(View.GONE);
//        }
//        else{
//            textCartItemCount.setVisibility(View.VISIBLE);
//        }

        if (textCartItemCount != null) if (taskItemList.size() == 0 || taskItemList.size() < 0 || adapter.getCount() == 0 || mCartItemCount == 0 ) {
            if (textCartItemCount.getVisibility() != View.GONE) {
                textCartItemCount.setVisibility(View.GONE);
                menuItem.setVisible(false);
            }
        } else {
            textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
            if (textCartItemCount.getVisibility() != View.VISIBLE) {
                textCartItemCount.setVisibility(View.VISIBLE);
                menuItem.setVisible(true);
            }
        }
    }




    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (compactCalendarView != null) compactCalendarView.setCurrentDate(date);
    }


    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) tvTitle.setText(title);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Task");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   String task2 = String.valueOf(result.get(0));
                    final EditText taskEditText = new EditText(MainActivity.this);
                    taskEditText.setText(task2);
                    final EditText TaskTime = new EditText(MainActivity.this);
                    TaskTime.setHint("date");
                    TaskTime.setVisibility(View.VISIBLE);
                    TaskTime.setText("Click this to add due date");
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(50, 0, 50, 0);
                    layout.addView(taskEditText,params);
                    layout.addView(TaskTime,params);

                    TaskTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                                @Override
                                public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                                    TaskTime.setVisibility(View.VISIBLE);
                                    TaskTime.setTextColor(Color.BLACK);
                                    dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                                    tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                                    task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                                    TaskTime.setText(task5);
                                    Taskdialog.show();

                                }

                                @Override
                                public void onCancel() {
                                    Taskdialog.show();

                                }
                            }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();

                        }
                    });


                    Taskdialog = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle)
                            .setTitle("Voice Task")
                            .setMessage("Adding due date is optional")
                            .setIcon(R.drawable.round_mic)
                            .setView(layout)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogue, int which) {
                                    taskEditText.setText(null);
                                    taskEditText.setText(task2);
                                    taskEditText.setSelection(task2.length());
                                    if(taskItemList.contains(task2))
                                        Toast.makeText(getBaseContext(), "Already Added..", Toast.LENGTH_LONG).show();
                                    else if (task2 == null || task2.trim().equals(""))
                                        Toast.makeText(getBaseContext(), "Add task..", Toast.LENGTH_LONG).show();
                                    else {
                                        taskEditText.setText(task2);
                                        taskItemList.add(new TaskItem(task2,TaskTime.getText().toString()));
                                        adapter.add(taskItemList.get(taskItemList.size() - 1));
                                        TaskItem item = taskItemList.get(taskItemList.size() - 1); //adapter.getItem(0);
                                        adapter.notifyDataSetChanged();
                                        SQLiteDatabase db = mHelper.getWritableDatabase();
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                        ContentValues values = new ContentValues();
                                            values.put(Task.TaskEntry.COL_TASK_TITLE, task2);
                                            values.put(Task.TaskEntry.COL_TASK_DATE,TaskTime.getText().toString());
                                            taskEditText.setText(null);
                                        db.insertWithOnConflict(Task.TaskEntry.TABLE,
                                                null,
                                                values,
                                                SQLiteDatabase.CONFLICT_REPLACE);
                                        TaskNotification();
                                        updateUI();
                                        db.close();
                                        mCartItemCount = adapter.getCount();
                                        setupBadge();
                                        taskEditText.setText(null);

                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .setNeutralButton("Add Due Date", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CustomDateTimePicker(MainActivity.this,
                                            new CustomDateTimePicker.ICustomDateTimeListener() {
                                                @Override
                                                public void onSet(Dialog dialog, Calendar calendarSelected,
                                                                  Date dateSelected, int year,
                                                                  String monthFullName,
                                                                  String monthShortName,
                                                                  int monthNumber, int date,
                                                                  String weekDayFullName,
                                                                  String weekDayShortName, int hour24,
                                                                  int hour12,
                                                                  int min, int sec, String AM_PM) {
                                                    TaskTime.setVisibility(View.VISIBLE);
                                                    TaskTime.setTextColor(Color.BLACK);
                                                    dat = String.valueOf(date)+" "+monthShortName+" "+String.valueOf(year);
                                                    tim = String.valueOf(hour12)+":" + String.valueOf(min);
                                                    task1 = "Date : "+dat+" "+"Time : "+tim+ AM_PM;
                                                    TaskTime.setText(task1);
                                                    TaskTime.setEnabled(false);
                                                    Taskdialog.show();

                                                }

                                                @Override
                                                public void onCancel() {
                                                    Taskdialog.show();
                                                }
                                            }).set24HourFormat(true).setDate(Calendar.getInstance())
                                            .showDialog();
                                }
                            })
                            .create();
                    Taskdialog.show();
                }
                break;
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();
//        if (mCartItemCount == 0) {
////            ImageView empty = (ImageView) findViewById(R.id.empty1);
////            EmptyNotification();
////            empty.setVisibility(View.VISIBLE);
//        } else {

//            ImageView empty = (ImageView) findViewById(R.id.empty1);
////          adapter.addAll(taskItemList);
//            empty.setVisibility(View.GONE);
            updateUI();
//            TaskNotification();
            mCartItemCount = adapter.getCount();
            setupBadge();
//        }
        if(adapter.getCount() == 0){
            ImageView empty = (ImageView) findViewById(R.id.empty1);
            list.setEmptyView(empty);
            EmptyNotification();

        }
        else{
            updateUI();
            setupBadge();
            TaskNotification();

        }

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void EmptyNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        isNotiPermissionGranted();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String strDate = sdf.format(new Date());
        String strtime = df.format(new Date());

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);

        // Build notification
        // Actions are just fake

        Notification noti = new Notification.Builder(MainActivity.this).setContentTitle("No tasks today" + "    " + strDate + " " + curTime).setContentText("Enjoy Your day..").setLights(Color.WHITE, 2000, 3000).setSmallIcon(R.mipmap.my).setContentIntent(pIntent).setAutoCancel(true).setSound(alarmSound).addAction(R.drawable.baseline_add, "Add Task", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(0, noti);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void TaskNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String strDate = sdf.format(new Date());
        String strtime = df.format(new Date());

        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        int hours = dt.getHours();
        int minutes = dt.getMinutes();
        int seconds = dt.getSeconds();
        String curTime = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);


        // Build notification
        // Actions are just fake
        int i = taskItemList.size();

        if (i > 0 || i <= 3) {
            g = "Only ";
            h = "Complete fast and Enjoy your day..";
        }
        if (i > 3) {
            g = "";
            h = "Hurry up..";
        }
        Notification noti = new Notification.Builder(MainActivity.this).setContentTitle(g + i + " Tasks Remaining" + "    " + strDate + "    " + curTime).setContentText(h).setLights(Color.WHITE, 2000, 3000).setSmallIcon(R.mipmap.my).setContentIntent(pIntent)
//                .setSound(alarmSound)
                .setOngoing(true).addAction(R.drawable.baseline_add, "Add Task", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
//      noti.flags |= Notification.FLAG_AUTO_CANCEL;
//        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;

        notificationManager.notify(0, noti);

    }


    private void setSubtitle(String subtitle) {
        TextView datePickerTextView = findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) datePickerTextView.setText(subtitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        SQLiteDatabase db = mHelper.getWritableDatabase();
        switch (item.getItemId()){
            case  R.id.action_swap_order : {
                sortData(ascending);
                ascending = !ascending;
            }
            case  R.id.action_cart : {
                return true;
            }
            //noinspection SimplifiableIfStatement
            case R.id.action_delete: {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("Clear All ??").setIcon(R.drawable.del).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, int which) {
                        if (adapter.getCount() == 0 || adapter.getCount() < 0) {
                            Toast.makeText(getBaseContext(), "Task list is Empty Presently..", LENGTH_SHORT).show();
                            adapter.clear();
                            mCartItemCount = 0;

                            setupBadge();
                            db.delete(Task.TaskEntry.TABLE, null, null);
                            if (adapter.getCount() == 0 || adapter.getCount() < 0){
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.VISIBLE);
                                list.setEmptyView(findViewById(R.id.empty1));
                                EmptyNotification();
                                mCartItemCount = adapter.getCount();
                            } else {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.INVISIBLE);
                                list.setAdapter(adapter);
                                TaskNotification();
                                mCartItemCount = adapter.getCount();

                            }
                        } else {
                            adapter.clear();
                            db.delete(Task.TaskEntry.TABLE, null, null);
                            mCartItemCount = adapter.getCount();
                            setupBadge();
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Cleared all Items", Snackbar.LENGTH_SHORT)
                                    .setAction("CLOSE", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                                    .show();
                            if (taskItemList.size() == 0 || taskItemList.size() < 0) {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.VISIBLE);
                                list.setEmptyView(findViewById(R.id.empty1));
                                EmptyNotification();
                                setupBadge();
                            } else {
                                ImageView empty = (ImageView) findViewById(R.id.empty1);
                                empty.setVisibility(View.INVISIBLE);
                                list.setAdapter(adapter);
                                TaskNotification();
                                setupBadge();
                            }
                        }
                    }
                }).setNegativeButton("No", null).create();
                dialog.show();
                return true;
            }
            case  R.id.action_exit : {

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("Exit Schedular ??").setIcon(R.drawable.icexit).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, int which) {
                        finish();
                        if(taskItemList.size() == 0 || taskItemList.size() < 0) TaskNotification();
                        else EmptyNotification();
                        System.exit(1);

                        mCartItemCount = taskItemList.size();;
                        if (taskItemList.size() == 0 || taskItemList.size() < 0) {
                            ImageView empty = (ImageView) findViewById(R.id.empty1);
                            empty.setVisibility(View.VISIBLE);
                            list.setEmptyView(findViewById(R.id.empty1));
                            EmptyNotification();
                            mCartItemCount = taskItemList.size();
                        } else {
                            ImageView empty = (ImageView) findViewById(R.id.empty1);
                            empty.setVisibility(View.INVISIBLE);
                            list.setAdapter(adapter);
                            TaskNotification();
                            mCartItemCount = taskItemList.size();
                        }

                    }
                }).setNegativeButton("Cancel", null).create();
                dialog.show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private boolean isNotiPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("MAina","Permission is granted");
                return true;
            } else {

                Log.v("MAina","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Maina","Permission is granted");
            return true;
        }
    }
   public void sortData(boolean asc) {
       updateUI();
       if (adapter.getCount() == 0){
           Toast.makeText(this, "List is Empty ..", Toast.LENGTH_SHORT).show();
       updateUI();
   }
       //SORT ARRAY ASCENDING AND DESCENDING
       else {
//           if (asc) {
//               Collections.sort(TaskItem(adapter);
//           }
//           else {
//               Collections.reverse(adapter.getView());
//           Collections.sort(list);
           }

//           list.setAdapter(new TaskAdapter(MainActivity.this, R.layout.item_todo, taskItemList));

//       }
       updateUI();
   }

@Override
public boolean onNavigationItemSelected( MenuItem item1) {
    // Handle navigation view item clicks here.
    int id = item1.getItemId();

    if (id == R.id.nav_newtask) {
        final EditText taskEditText = new EditText(MainActivity.this);
        taskEditText.setHint("Type task");
        TaskTime = new EditText(MainActivity.this);
        TaskTime.setHint("date and time");
        TaskTime.setVisibility(View.VISIBLE);
        TaskTime.setText("Click this to Add due date");
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(50, 0, 50, 0);
        layout.addView(taskEditText,params);
        layout.addView(TaskTime,params);

        TaskTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                        TaskTime.setVisibility(View.VISIBLE);
                        TaskTime.setTextColor(Color.BLACK);
                        dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                        tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                        task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                        TaskTime.setText(task5);
                        Taskdialog.show();

                    }

                    @Override
                    public void onCancel() {
                        Taskdialog.show();

                    }
                }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();

            }
        });



        Taskdialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("New Task").setMessage("Add a new task").setIcon(R.drawable.round_insert).setView(layout).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogue, int which) {
                taskEditText.requestFocus();
                task = String.valueOf(taskEditText.getText());
                if(TaskTime.getText().toString() != null || TaskTime.getText().toString() != ""){
                    TaskTime.setVisibility(View.VISIBLE);

                }
                else if(TaskTime.getText().toString() == null || TaskTime.getText().toString() == ""){
                    TaskTime.setVisibility(View.VISIBLE);
                    TaskTime.setText("Click this to Add due date");

                }
                if (taskItemList.contains(task)) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Already Added..", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                            .show();
                } else if (task == null || task.trim().equals("")) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add task..", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                            .show();
                } else {
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    taskItemList.add(new TaskItem(task,TaskTime.getText().toString()));
                    adapter.add(taskItemList.get(taskItemList.size() - 1));
                    TaskNotification();
                    TaskItem item = taskItemList.get(taskItemList.size() - 1); //adapter.getItem(0);
                    adapter.notifyDataSetChanged();
                    mCartItemCount = adapter.getCount();
                    setupBadge();
                    ContentValues values = new ContentValues();
                    if (TaskTime.getText().toString() == null || TaskTime.getText().toString() == "") {
                        TaskTime.setVisibility(View.VISIBLE);
                        TaskTime.setText("Click this to Add due date");
                        values.put(Task.TaskEntry.COL_TASK_TITLE, task);
                        values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                    }
                    else {
                        values.put(Task.TaskEntry.COL_TASK_TITLE, task);
                        values.put(Task.TaskEntry.COL_TASK_DATE, TaskTime.getText().toString());
                    }
                    db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    TaskNotification();
                    db.close();
                    updateUI();
                }
            }
        }).setNegativeButton("Cancel", null).setNeutralButton("Add Due Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CustomDateTimePicker(MainActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected, Date dateSelected, int year, String monthFullName, String monthShortName, int monthNumber, int date, String weekDayFullName, String weekDayShortName, int hour24, int hour12, int min, int sec, String AM_PM) {
                        TaskTime.setVisibility(View.VISIBLE);
                        TaskTime.setTextColor(Color.BLACK);
                        dat = String.valueOf(date) + " " + monthShortName + " " + String.valueOf(year);
                        tim = String.valueOf(hour12) + ":" + String.valueOf(min);
                        task5 = "Date : " + dat + " " + "Time : " + tim + AM_PM;
                        TaskTime.setText(task5);
                        TaskTime.setEnabled(false);
                        Taskdialog.show();

                    }

                    @Override
                    public void onCancel() {
                        Taskdialog.show();

                    }
                }).set24HourFormat(true).setDate(Calendar.getInstance()).showDialog();
            }
        }).create();
        Taskdialog.show();

  }
  else if (id == R.id.nav_rate_app) {

    } else if (id == R.id.nav_voicetask) {
        startVoiceInput();

    }
    else if (id == R.id.nav_share) {
        String s = "hii";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
        startActivity(Intent.createChooser(sharingIntent, "Share text via"));

    } else if (id == R.id.nav_version) {

        Intent about = new Intent(MainActivity.this, About.class);
        startActivity(about);


    } else if (id == R.id.nav_clearall) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("Clear All ??").setIcon(R.drawable.del).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogue, int which) {
                if (adapter.getCount() == 0 || adapter.getCount() < 0) {
                    Toast.makeText(getBaseContext(), "Task list is Empty Presently..", LENGTH_SHORT).show();
                    adapter.clear();
                    mCartItemCount = 0;

                    setupBadge();
                    db.delete(Task.TaskEntry.TABLE, null, null);
                    if (adapter.getCount() == 0 || adapter.getCount() < 0){
                        ImageView empty = (ImageView) findViewById(R.id.empty1);
                        empty.setVisibility(View.VISIBLE);
                        list.setEmptyView(findViewById(R.id.empty1));
                        EmptyNotification();
                        mCartItemCount = adapter.getCount();
                    } else {
                        ImageView empty = (ImageView) findViewById(R.id.empty1);
                        empty.setVisibility(View.VISIBLE);
                        list.setEmptyView(findViewById(R.id.empty1));
                        list.setAdapter(adapter);
                        TaskNotification();
                        mCartItemCount = adapter.getCount();
                    }
                } else {
                    adapter.clear();
                    ImageView empty = (ImageView) findViewById(R.id.empty1);
                    empty.setVisibility(View.VISIBLE);
                    list.setEmptyView(findViewById(R.id.empty1));
                    db.delete(Task.TaskEntry.TABLE, null, null);
                    mCartItemCount = adapter.getCount();
                    setupBadge();
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Cleared all Items", Snackbar.LENGTH_SHORT)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_blue_bright))
                            .show();
                    if (taskItemList.size() == 0 || taskItemList.size() < 0) {
                        empty.setVisibility(View.VISIBLE);
                        list.setEmptyView(findViewById(R.id.empty1));
                        EmptyNotification();
                        setupBadge();
                    } else {
                        empty.setVisibility(View.INVISIBLE);
                        list.setAdapter(adapter);
                        TaskNotification();
                        setupBadge();
                    }
                }
            }
        }).setNegativeButton("No", null).create();
        dialog.show();
        return true;

    }
    else if(id == R.id.scratchpad) {
        Intent ip = new Intent(MainActivity.this,Scratch.class);
        startActivity(ip)                                                          ;
    }
    else if (id == R.id.nav_exit) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle).setTitle("Exit Schedular ??").setIcon(R.drawable.icexit).setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogue, int which) {
                finish();
                if(taskItemList.size() == 0 || taskItemList.size() < 0) TaskNotification();
                else EmptyNotification();
                System.exit(1);

                mCartItemCount = taskItemList.size();;
                if (taskItemList.size() == 0 || taskItemList.size() < 0) {
                    ImageView empty = (ImageView) findViewById(R.id.empty1);
                    empty.setVisibility(View.VISIBLE);
                    list.setEmptyView(findViewById(R.id.empty1));
                    EmptyNotification();
                    mCartItemCount = taskItemList.size();
                } else {
                    ImageView empty = (ImageView) findViewById(R.id.empty1);
                    empty.setVisibility(View.INVISIBLE);
                    list.setAdapter(adapter);
                    TaskNotification();
                    mCartItemCount = taskItemList.size();
                }

            }
        }).setNegativeButton("Cancel", null).create();
        dialog.show();
        return true;
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);

    return true;
}


//    public void click(View view) {
//        Intent notifyIntent = new Intent(this,MyReceiver.class);
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast
//                (MainActivity.this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
//                1000 * 60 * 60 * 24, pendingIntent);
//    }
//    public  void edittask(View v){
//        v.findViewById(R.id.task_title);
//        Toast.makeText(MainActivity.this,v.findViewById(R.id.task_title),Toast.LENGTH_LONG).show();
//
//    }





}
