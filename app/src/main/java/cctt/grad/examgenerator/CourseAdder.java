package cctt.grad.examgenerator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CourseAdder extends AppCompatActivity {

    private EditText courseName, courseYear= null;
    private Spinner courseSemester = null;
    private Button addCourse = null;
    private ExamDBHandler examDbHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_adder);

        //To display Home/Back Button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Add a Course");

        //Widget Initialization...
        courseName = (EditText) findViewById(R.id.courseName);
        courseYear = (EditText) findViewById(R.id.courseYear);
        courseSemester = (Spinner) findViewById(R.id.courseSemester);
        addCourse = (Button) findViewById(R.id.addCourse);

        //DB Handler Initialization...
        examDbHandler = new ExamDBHandler(this, null, null, 1);

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _courseName, _courseYearString;
                int _courseYear, _teacherId;
                int semester;
                _courseName = courseName.getEditableText().toString();
                _courseYearString = courseYear.getText().toString();

                if(inputValidator(_courseName, _courseYearString)){

                    if(yearInputValidator(_courseYearString)){
                        _courseYear = Integer.parseInt(courseYear.getEditableText().toString());
                        _teacherId = examDbHandler.sessionManager.sharedPreferences.getInt(examDbHandler.sessionManager.KEY_ID, -1);


                        if(courseSemester.getSelectedItem().toString().equals("Spring")){
                            semester = 1;
                        }else{
                            semester = 0;
                        }
                        Class courseClass = new Class(_courseYear, semester);
                        int _courseClass = examDbHandler.getClassIdOrCreateClassId(getApplicationContext(), _courseYear, semester);
                        Course course = new Course(_courseName, _teacherId, _courseClass);
                        examDbHandler.addCourse(course, courseClass);
                        /*Toast.makeText(CourseAdder.this, "New Course:\n"
                                + course.get_name() + "\nadded successfully", Toast.LENGTH_SHORT).show();*/
                        try{
                            courseAddedSuccessfullyToast().show();
                        }catch (Exception e){
                            Toast.makeText(CourseAdder.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                        courseName.setText("");
                        courseYear.setText("");
                    }else
                        Toast.makeText(CourseAdder.this, "Make sure year is in the 21st century", Toast.LENGTH_SHORT).show();

                }else
                    Toast.makeText(CourseAdder.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean inputValidator(String courseName, String courseYear){
        if(courseName.isEmpty() || courseYear.isEmpty())
            return false;
        return true;
    }

    public boolean yearInputValidator(String courseYear){
        if(courseYear.startsWith("20") && courseYear.length() == 4)
            return true;
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                Intent backIntent = new Intent(this, CourseManagement.class);
                startActivity(backIntent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Toast courseAddedSuccessfullyToast(){

        Toast toast = Toast.makeText(this, "Course Added Successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.course_added_successfully, 0, 0, 0);
        }

        return toast;
    }
}
