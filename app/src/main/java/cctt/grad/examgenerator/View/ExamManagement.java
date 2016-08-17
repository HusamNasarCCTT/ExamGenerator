package cctt.grad.examgenerator.View;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.R;

public class ExamManagement extends AppCompatActivity {

    private ListView examListView = null;
    private int teacherId = 0, courseId = 0;
    private String teacherName = null, courseName = null, examDirectory = null;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private Vector<String> examlist = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_management);

        //Display Home/Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Session Manager Initialization...
        sessionManager = new SessionManager(getApplicationContext());
        //Exam DB Handler Initializationg...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Getting Activity essential data...
        teacherId = sessionManager.sharedPreferences.getInt("ID", 0);
        courseId = getIntent().getExtras().getInt("Course ID");

        teacherName = examDBHandler.getTeacherName(teacherId);
        courseName = examDBHandler.getCourseById(courseId).get_name();

        //Setting Actionbar title...
        setTitle(courseName);

        //Getting Exam List Directory...

        examDirectory = getFilesDir().getAbsolutePath() + "/" + Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName;

        //Widget Initialization...
        examListView = (ListView) findViewById(R.id.examListView);

        //ListView Initialization with Exam List if exams exist...
        examlist = listOfExams(examDirectory);
        examListView.setAdapter(examListAdapter(examlist));


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

}
