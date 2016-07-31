package cctt.grad.examgenerator;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_management_coordinator);

        //To display Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DB Handler initialization...
        examDbHandler = new ExamDBHandler(this, null, null, 1);

        //Session Manager initialization...
        sessionManager = new SessionManager(getApplicationContext());
        teacherId = sessionManager.sharedPreferences.getInt("ID", -1);

        //Get Teacher Name for Action Bar...
        teacherName = examDbHandler.getTeacherName(teacherId, this);
        setTitle(teacherName);

        //Widget Initialization...
        addCourses = (Button) findViewById(R.id.addCourses);
        courseList = (ListView) findViewById(R.id.courseList);
        courseManagementAssistButton = (FloatingActionButton) findViewById(R.id.courseManagementAssistButton);
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
                courseEditorIntent.putExtras(selectedCourse);
                startActivity(courseEditorIntent);
                return true;
            case R.id.context_delete:
                //Note to self: Remember to write code for deleting all questions for the course not just the course itself...
                Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                examDbHandler.removeCourse(courseId);
                                courseList.setAdapter(getCourseListAdapter());
                                Toast.makeText(CourseManagement.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                return true;

            case R.id.viewCourseDetails:
                Intent toCourseDetails = new Intent(this, ViewCourseDetails.class);
                toCourseDetails.putExtra("Course ID", courseId);
                startActivity(toCourseDetails);
                finish();
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
                sessionManager.logoutUser();
                finish();
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
                    Toast.makeText(this, "There are no courses, you IDIOT!!!", Toast.LENGTH_SHORT).show();
                }else{
                    Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    examDbHandler.deleteAllCourses(teacherId);
                                    courseList.setAdapter(getCourseListAdapter());
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
}
