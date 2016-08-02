package cctt.grad.examgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class CourseEditor extends AppCompatActivity {

    private Intent previousActivityIntent = null;
    private Bundle bundleFromPreviousIntent = null;
    private int courseId = -1, courseYear = -1, courseTerm = -1, courseClass = -1;
    private String courseName = null;
    private EditText courseNameField, courseYearField = null;
    private Spinner courseTermSpinner = null;
    private Button editCourse = null;
    private ExamDBHandler examDBHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_editor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting data from previous intent...
        previousActivityIntent = getIntent();
        bundleFromPreviousIntent = previousActivityIntent.getExtras();
        courseId = bundleFromPreviousIntent.getInt("Course ID", -1);
        courseName = bundleFromPreviousIntent.getString("Course Name");
        courseYear = bundleFromPreviousIntent.getInt("Course Year", -1);
        courseTerm = bundleFromPreviousIntent.getInt("Course Term", -1);

        //Initializing Widgets...

        courseNameField = (EditText) findViewById(R.id.courseNameEdit);
        courseYearField = (EditText) findViewById(R.id.courseYearEdit);
        courseTermSpinner = (Spinner) findViewById(R.id.courseSemesterEdit);
        editCourse = (Button) findViewById(R.id.EditCourse);

        //Initializing DB Handler...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Putting Old Course Data in widgets for display...
        courseNameField.setHint(courseName);
        courseYearField.setHint(String.valueOf(courseYear));
        courseTermSpinner.setSelection(courseTerm);

        //Getting Course Class ID...
        courseClass = examDBHandler.getClassIdOrCreateClassId(this, courseYear, courseTerm);


        editCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputValidator(courseNameField.getText().toString(), courseYearField.getText().toString(), courseTermSpinner.getSelectedItem().toString())){
                    Toast.makeText(CourseEditor.this, "Change at least one parameter", Toast.LENGTH_SHORT).show();
                }else{
                    if(courseYearValidator(courseYearField.getText().toString())){
                        updateCourse();
                        courseUpdatedSuccessfullyToast().show();
                        finish();
                    }else{
                        Toast.makeText(CourseEditor.this, "Make sure year is in the 21st century", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });




    }


    public boolean inputValidator(String courseName, String courseYear, String _courseTerm){

        if(courseName.isEmpty() && courseYear.isEmpty() && _courseTerm.matches((String) courseTermSpinner.getItemAtPosition(courseTerm)))
            return true;
        else
            return false;
    }

    public boolean courseYearValidator(String courseYear){
        if(courseYear.startsWith("20") && courseYear.length() == 4)
            return true;
        else
            return false;
    }

    public void updateCourse(){

        String newCourseName;
        int newCourseYear, newCourseTerm, newCourseClass;
        if(courseNameField.getText().toString().isEmpty())
            newCourseName = courseName;
        else
            newCourseName = courseNameField.getText().toString();

        if(courseYearField.getText().toString().isEmpty())
            newCourseYear = courseYear;
        else
            newCourseYear = Integer.valueOf(courseYearField.getText().toString());


        if(Integer.valueOf(courseTermSpinner.getSelectedItemPosition()) == courseTerm)
            newCourseTerm = courseTerm;
        else
            newCourseTerm = Integer.valueOf(courseTermSpinner.getSelectedItemPosition());

        newCourseClass = examDBHandler.getClassIdOrCreateClassId(this, newCourseYear, newCourseTerm);

        Course course = new Course();
        course.set_id(courseId);
        course.set_name(newCourseName);
        course.set_courseClass(newCourseClass);

        examDBHandler.updateCourse(course);
    }

    public Toast courseUpdatedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Course Updated Successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.edit_course, 0, 0, 0);
        }

        return toast;
    }

}
