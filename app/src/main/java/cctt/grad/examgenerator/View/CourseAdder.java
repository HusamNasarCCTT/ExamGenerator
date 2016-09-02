package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cctt.grad.examgenerator.Model.Question;
import cctt.grad.examgenerator.Model.Teacher;
import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Model.Class;
import cctt.grad.examgenerator.Model.Course;
import cctt.grad.examgenerator.R;

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
        Teacher newTeacher = new Teacher();


        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _courseName, _courseYearString;
                int _courseYear, _teacherId;
                int semester;
                _courseName = courseName.getEditableText().toString();
                _courseYearString = courseYear.getText().toString();

                if(inputValidator(_courseName, _courseYearString)){


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


                }
            }
        });
    }

    public boolean inputValidator(String courseName, String courseYear){

        this.courseName.setError(null);
        this.courseYear.setError(null);

        if(courseName.isEmpty()){
            this.courseName.setError("This field is required");
            return false;
        }

        if(courseYear.isEmpty()){
            this.courseYear.setError("This field is required");
            return false;
        }

        if(! yearInputValidator(courseYear)){
            this.courseYear.setError("Please enter a recent date");
            return false;
        }

        return true;

    }

    public boolean yearInputValidator(String courseYear){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String currentYear = simpleDateFormat.format(new Date());
        int currentYearInt = Integer.valueOf(currentYear);
        int courseYearInt = Integer.valueOf(courseYear);
        if(courseYearInt >= currentYearInt)
            return true;
        return false;

        /*if(courseYear.startsWith("20") && courseYear.length() == 4)
            return true;
        return false;*/
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
