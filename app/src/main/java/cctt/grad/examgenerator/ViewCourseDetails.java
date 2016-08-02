package cctt.grad.examgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.Vector;

public class ViewCourseDetails extends AppCompatActivity {

    int courseId = 0;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private Course courseData = null;

    private TextView viewCourseDetailsName,
            viewCourseDetailsNumOfEssays,
            viewCourseDetailsNumOfMCQs,
            viewCourseDetailsTheoryPercentage,
            viewCourseDetailsPracticalPercentage,
            viewCourseDetailsTerm,
            viewCourseDetailsYear;

    private String courseName, courseTermString;
    private int numOfEssays, numOfMCQs, numOfQuestions, percentageOfTheories, percentageOfPracticals, courseTerm, courseYear;
    private Vector<Bundle> questionList = null;

    private int MCQ = 0, ESSAY = 1, BOTH = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_course_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting course ID from CourseManagement Activity...
        courseId = getIntent().getIntExtra("Course ID", 0);

        //Initializing DB Handler and Session Manager...
        examDBHandler = new ExamDBHandler(this, null, null, 1);
        sessionManager = new SessionManager(getApplicationContext());
        getSupportActionBar().setIcon(R.drawable.view_course);

        //Initializing Widgets...
        viewCourseDetailsName = (TextView) findViewById(R.id.viewCourseDetailsName);
        viewCourseDetailsNumOfEssays = (TextView) findViewById(R.id.viewCourseDetailsNumOfEssays);
        viewCourseDetailsNumOfMCQs = (TextView) findViewById(R.id.viewCourseDetailsNumOfMCQs);
        viewCourseDetailsTheoryPercentage = (TextView) findViewById(R.id.viewCourseDetailsTheoryPercentage);
        viewCourseDetailsPracticalPercentage = (TextView) findViewById(R.id.viewCourseDetailsPracticalPercentage);
        viewCourseDetailsTerm = (TextView) findViewById(R.id.viewCourseDetailsTerm);
        viewCourseDetailsYear = (TextView) findViewById(R.id.viewCourseDetailsYear);

        //Getting Course Data via courseID...
        courseData = examDBHandler.getCourseById(courseId);

        //Preparing data to display...

        courseName = courseData.get_name();
        setTitle(courseName + " Dashboard");
        Class courseClass = examDBHandler.readClass(courseData.get_courseClass());
        courseTerm = courseClass.get_term();

        if(courseTerm == 0){
            courseTermString = "Fall";
        }else
            courseTermString = "Spring";
        courseYear = courseClass.get_year();

        questionList = examDBHandler.readQuestion(courseId, BOTH);

        numOfQuestions = questionList.size();
        numOfMCQs = getNumOfMCQs(questionList);
        numOfEssays = getNumOfEssays(questionList);
        percentageOfTheories = getPercentageOfTheories(questionList);
        percentageOfPracticals = getPercentageOfPracticals(questionList);


        //Displaying Data...
        viewCourseDetailsName.setText(courseName);
        if(courseName.length() > 7)
            viewCourseDetailsName.setTextSize(48);
        viewCourseDetailsNumOfEssays.setText(String.valueOf(numOfEssays));
        viewCourseDetailsNumOfMCQs.setText(String.valueOf(numOfMCQs));
        viewCourseDetailsTheoryPercentage.setText(String.valueOf(getPercentageOfTheories(questionList)) + "%");
        viewCourseDetailsPracticalPercentage.setText(String.valueOf(getPercentageOfPracticals(questionList)) + "%");
        viewCourseDetailsTerm.setText(courseTermString);
        viewCourseDetailsYear.setText(String.valueOf(courseYear));

    }

    public int getNumOfMCQs(Vector<Bundle> questionList){

        int numOfMCQs = 0;
        for (Bundle question : questionList){

            if(question.getInt("Mcq or Essay") == 0){
                numOfMCQs++;
            }

        }

        return numOfMCQs;
    }

    public int getNumOfEssays(Vector<Bundle> questionList){

        int numOfEssays = 0;
        for (Bundle question : questionList){

            if(question.getInt("Mcq or Essay") == 1){
                numOfEssays++;
            }

        }

        return numOfEssays;
    }

    public int getPercentageOfTheories(Vector<Bundle> questionList){

        int numOfTheories = 0;

        for (Bundle question : questionList){

            if(question.getInt("Practical or Theory") == 0){
                numOfTheories++;
            }

        }

        return (numOfTheories * 100) / questionList.size();
    }

    public int getPercentageOfPracticals(Vector<Bundle> questionList){

        int numOfPracticals = 0;

        for (Bundle question : questionList){

            if(question.getInt("Practical or Theory") == 1){
                numOfPracticals++;
            }

        }

        return (numOfPracticals * 100) / questionList.size();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                /*Intent backIntent = new Intent(this, CourseManagement.class);
                startActivity(backIntent);*/
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
