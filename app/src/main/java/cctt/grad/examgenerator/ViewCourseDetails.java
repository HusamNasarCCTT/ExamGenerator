package cctt.grad.examgenerator;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.HorizontalScrollView;

public class ViewCourseDetails extends AppCompatActivity {

    int courseId = 0;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private Course courseData = null;

    private HorizontalScrollView courseList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_course_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting course ID from CourseManagement Activity...
        courseId = getIntent().getIntExtra("Course Id", 0);

        //Initializing DB Handler and Session Manager...
        examDBHandler = new ExamDBHandler(this, null, null, 1);
        sessionManager = new SessionManager(getApplicationContext());

        //Getting Course Data via courseID...
        courseData = examDBHandler.getCourseById(courseId);

        courseList = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

    }

}
