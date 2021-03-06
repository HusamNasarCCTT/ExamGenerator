package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.R;

public class CourseManagement extends AppCompatActivity {

    private Button addCourses = null;
    private FloatingActionButton courseManagementAssistButton = null;
    private Calendar calendar = null;
    private ListView courseList = null;
    private ListAdapter adapter;
    private ExamDBHandler examDbHandler = null;
    private SessionManager sessionManager = null;
    private int teacherId = 0;
    private String teacherName = null;
    private ProgressBar deleteAllCoursesProgressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_management_coordinator);

        //To display Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.logout);

        //DB Handler initialization...
        examDbHandler = new ExamDBHandler(this, null, null, 1);

        //Session Manager initialization...
        sessionManager = new SessionManager(getApplicationContext());
        teacherId = sessionManager.sharedPreferences.getInt("ID", -1);

        //Get Teacher Name for Action Bar...
        teacherName = examDbHandler.getTeacher(teacherId).get_name();
        setTitle(teacherName);

        //Widget Initialization...
        addCourses = (Button) findViewById(R.id.addCourses);
        courseList = (ListView) findViewById(R.id.courseList);
        courseManagementAssistButton = (FloatingActionButton) findViewById(R.id.courseManagementAssistButton);
        deleteAllCoursesProgressBar = (ProgressBar) findViewById(R.id.deleteAllCoursesProgressBar);
        courseList.setFocusable(true);

        courseList.setAdapter(getCourseListAdapter());
        registerForContextMenu(courseList);
        try{Snackbar.make(getCurrentFocus(), "Welcome " + teacherName, Snackbar.LENGTH_SHORT)
                .show();}catch (Exception e)
        {
            Snackbar.make(courseList, "Welcome " + teacherName, Snackbar.LENGTH_SHORT).show();
        }

        addCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(CourseManagement.this, CourseAdder.class));
            }
        });

        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = (Bundle) parent.getItemAtPosition(position);
                int courseId = bundle.getInt("Course ID");
                if(courseId == 0){
                    Snackbar.make(getCurrentFocus(), "Please add a course", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }else{
                    Intent courseIntent = new Intent(CourseManagement.this, QuestionManagement.class);
                    courseIntent.putExtras((Bundle) parent.getItemAtPosition(position));
                    startActivity(courseIntent);
                }
            }
        });

        courseManagementAssistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAssistSnackBar();
            }
        });
    }

    @Override
    protected void onResume() {

        courseList.setAdapter(getCourseListAdapter());
        super.onResume();
    }

    public ListAdapter getCourseListAdapter(){
        Vector<Bundle> courseList = examDbHandler.readCourse();
        //ListAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courseList);
        ListAdapter listAdapter = new CustomCourseAdapter(this, courseList);

        return listAdapter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_edit_delete_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Selected item index...
        int selectedItem = info.position;
        Bundle selectedCourse = (Bundle) courseList.getItemAtPosition(selectedItem);
        final int courseId = selectedCourse.getInt("Course ID");
        switch (item.getItemId()){
            case R.id.context_edit:
                //Launch CourseEditor Activity...
                Intent courseEditorIntent = new Intent(CourseManagement.this, CourseEditor.class);

                //Getting Old Course Data from selection and adding it to intent...
                selectedCourse.putInt("Course ID", courseId);
                if(courseId > 0){
                    courseEditorIntent.putExtras(selectedCourse);
                    startActivity(courseEditorIntent);
                }else{
                    Snackbar.make(getCurrentFocus(), "Course is empty, no details are available to edit", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }
                return true;
            case R.id.context_delete:
                //Note to self: Remember to write code for deleting all questions for the course not just the course itself...

                if(courseId > 0){
                    Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    examDbHandler.removeCourse(courseId);
                                    courseList.setAdapter(getCourseListAdapter());
                                    courseDeletedSuccessfullyToast().show();
                                }
                            }).show();
                }else{
                    Snackbar.make(getCurrentFocus(), "Course list already empty", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }

                return true;

            case R.id.viewCourseDetails:
                if(courseId > 0){
                    Intent toCourseDetails = new Intent(this, ViewCourseDetails.class);
                    toCourseDetails.putExtra("Course ID", courseId);
                    startActivity(toCourseDetails);
                }else{
                    Snackbar.make(getCurrentFocus(), "Course is empty, no details are available to view", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_user_account_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                Snackbar.make(getCurrentFocus(), "Log out?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sessionManager.logoutUser();
                                finish();
                            }
                        }).show();
                return true;

            case R.id.updateAccountDetails:{
                Bundle userDetails = new Bundle();
                userDetails.putInt("ID", sessionManager.sharedPreferences.getInt("ID", -1));
                userDetails.putString("Username", sessionManager.sharedPreferences.getString("Username", ""));
                userDetails.putString("Password", sessionManager.sharedPreferences.getString("Password", ""));
                Intent toUpdateUserActivity = new Intent(CourseManagement.this, UpdateUser.class);
                toUpdateUserActivity.putExtras(userDetails);
                startActivity(toUpdateUserActivity);
                return true;
            }

            case R.id.delete_all_courses:

                //Making sure there is actual data to be deleted...
                Bundle bundle = (Bundle) courseList.getItemAtPosition(0);
                final int courseId = bundle.getInt("Course ID", -1);
                if(courseId <= 0){
                    Snackbar.make(getCurrentFocus(), "Course list is already empty", Snackbar.LENGTH_SHORT)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }else{
                    Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final Handler h = new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            deleteAllCoursesProgressBar.setVisibility(View.GONE);
                                            courseList.setAdapter(getCourseListAdapter());
                                            courseDeletedSuccessfullyToast().show();
                                        }
                                    };
                                    Runnable deleteRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            examDbHandler.deleteAllCourses(teacherId);
                                            h.sendEmptyMessage(0);
                                        }
                                    };
                                    deleteAllCoursesProgressBar.setVisibility(View.VISIBLE);
                                    Thread deleteThread = new Thread(deleteRunnable);
                                    deleteThread.start();
                                }
                            }).show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void popupAssistSnackBar(){

        Snackbar.make(getCurrentFocus(), "Tap on Course to open it", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(getCurrentFocus(), "Hold on course to edit or delete it", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar.make(getCurrentFocus(), "Tap on Actionbar option button for more options", Snackbar.LENGTH_INDEFINITE)
                                                .setAction("Cool 😎", new View.OnClickListener() {
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

    public Toast courseDeletedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Course Deleted Successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.delete_course, 0, 0, 0);
        }

        return toast;
    }
}
