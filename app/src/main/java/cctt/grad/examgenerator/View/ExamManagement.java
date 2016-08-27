package cctt.grad.examgenerator.View;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.R;

public class ExamManagement extends AppCompatActivity {

    private ListView examListView = null;
    private FloatingActionButton examManagementHelpButton = null;
    private int teacherId = 0, courseId = 0;
    private String teacherName = null, courseName = null, examDirectory = null;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private Vector<String> examlist = null;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_management_coordinator);

        //Display Home/Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Session Manager Initialization...
        sessionManager = new SessionManager(getApplicationContext());
        //Exam DB Handler Initializationg...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Getting Activity essential data...
        teacherId = sessionManager.sharedPreferences.getInt("ID", 0);
        courseId = getIntent().getExtras().getInt("Course ID");

        teacherName = examDBHandler.getTeacher(teacherId).get_name();
        courseName = examDBHandler.getCourseById(courseId).get_name();

        //Setting Actionbar title...
        setTitle(courseName + " exams");

        //Getting Exam List Directory...


        //examDirectory = getFilesDir().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName;

        examDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName).getAbsolutePath();

        //Widget Initialization...
        examListView = (ListView) findViewById(R.id.examListView);
        examManagementHelpButton = (FloatingActionButton) findViewById(R.id.examManagementHelpButton);

        //Register ListView for Context Menu...
        registerForContextMenu(examListView);

        //ListView Initialization with Exam List if exams exist...
        examlist = listOfExams(examDirectory);
        examListView.setAdapter(examListAdapter(examlist));

        //Help Button onClick Listener...
        examManagementHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getCurrentFocus(), "Above, is a list of this course's exams", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(getCurrentFocus(), "Tap on any exam to open it via your preferred PDF reader", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Snackbar.make(getCurrentFocus(), "Long tap on an exam for more options", Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("Ok", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                return;
                                                            }
                                                        }).show();
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });

        //Exam List onItemClickListener...
        examListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(( (String) parent.getAdapter().getItem(position)).matches("Exam List is Empty")){
                    return;
                }else{
                    String selectedExamDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName)
                            .getAbsolutePath() + (String) parent.getAdapter().getItem(position);

                    openExam(selectedExamDirectory);
                }


            }
        });


    }

    public Vector<String> listOfExams(String examDirectory){

        Vector<String> examList = new Vector<>();
        //AssetManager assetManager = getAssets();

        try{

            //String list[] = assetManager.list(examDirectory);
            File directory = new File(examDirectory);
            String list[] = directory.list();

            if(list.length > 0){
                for (int i=0; i < list.length; i++){

                    examList.add(list[i]);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return  examList;
    }

    public ListAdapter examListAdapter(Vector<String> listOfExams){


        ListAdapter adapter;
        if(listOfExams.isEmpty()){
            listOfExams.add("Exam List is Empty");
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfExams);
        return adapter;
    }

    public void openExam(String examDirectory){

        //Open file with PDF Reader if one should exist...
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(examDirectory));
        intent.setType("application/pdf");
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities.size() > 0) {
            startActivity(intent);
        } else {
            // Do something else here. Maybe pop up a Dialog or Toast
            Snackbar.make(getCurrentFocus(), "Sorry, but no suitable PDF reader is installed", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Snackbar.make(getCurrentFocus(), "Exam has been saved in PDF format in the default directory", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Ok", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Snackbar.make(getCurrentFocus(), "Please install a PDF reader to view exam", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("Ok", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            return;
                                                        }
                                                    }).show();
                                        }
                                    }).show();
                        }
                    }).show();
        }

    }

    public boolean deleteExam(String examDirectory){

        File file = new File(examDirectory);

        return file.delete();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_only_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.deleteOnly:


                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int selectedItem = info.position;
                if(( (String) examListView.getAdapter().getItem(selectedItem)).matches("Exam List is Empty")){
                    Snackbar.make(getCurrentFocus(), "Exam list is already empty, you can't perform the chosen task", Snackbar.LENGTH_SHORT)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }else{
                    final String selectedExamDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName)
                            .getAbsolutePath() + "/" + (String) examListView.getAdapter().getItem(selectedItem);

                    Snackbar.make(getCurrentFocus(), "Delete exam?", Snackbar.LENGTH_LONG)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(deleteExam(selectedExamDirectory)){

                                        Snackbar.make(getCurrentFocus(), "Exam deleted successfully", Snackbar.LENGTH_SHORT)
                                                .setAction("Ok", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        return;
                                                    }
                                                }).show();

                                        examListView.setAdapter(examListAdapter(listOfExams(examDirectory)));
                                    }else{

                                        Snackbar.make(getCurrentFocus(), "An error occured while deleting exam, please try again", Snackbar.LENGTH_SHORT)
                                                .setAction("Ok", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        return;
                                                    }
                                                }).show();

                                    }
                                }
                            }).show();
                }



                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }
}
