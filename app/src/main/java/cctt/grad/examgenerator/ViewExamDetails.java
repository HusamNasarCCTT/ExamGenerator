package cctt.grad.examgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ViewExamDetails extends AppCompatActivity {

    private TextView viewExamDifficulty = null,
                     viewExactExamDifficulty = null,
                     viewExamTime = null,
                     viewExamType = null,
                     viewExamMaterial = null,
                     viewExamNoOfQuestions = null,
                     viewExamTeacher = null;

    private Intent previousActivityIntent = null;
    private Bundle examDetails = null;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private GraphView graph = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_exam_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewExamDifficulty = (TextView) findViewById(R.id.viewExamDifficulty);
        viewExactExamDifficulty = (TextView) findViewById(R.id.viewExactExamDifficulty);
        viewExamTime = (TextView) findViewById(R.id.viewExamTime);
        viewExamType = (TextView) findViewById(R.id.viewExamType);
        viewExamMaterial = (TextView) findViewById(R.id.viewExamMaterial);
        viewExamNoOfQuestions = (TextView) findViewById(R.id.viewExamNoOfQuestions);
        viewExamTeacher = (TextView) findViewById(R.id.viewExamTeacher);

        graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{

                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graph.addSeries(series);


        //Initializing Session Manager and DB Handler...
        examDBHandler = new ExamDBHandler(this, null, null, 1);
        sessionManager = new SessionManager(getApplicationContext());


        //Get Exam Details from previous Activity...
        previousActivityIntent = getIntent();
        examDetails = previousActivityIntent.getExtras();

        //Set Exam Course Name onto title...
        setTitle(examDetails.getString("Course Name") + " Exam");

        //Set data onto widgets...
        // 1- Exam Difficulty...
        switch (examDetails.getInt("Visual Difficulty", -1)){
            case 1:
                viewExamDifficulty.setText(viewExamDifficulty.getText() + " Easy");
                break;

            case 2:
                viewExamDifficulty.setText(viewExamDifficulty.getText() + " Moderate");
                break;

            case 3:
                viewExamDifficulty.setText(viewExamDifficulty.getText() + " Difficult");
                break;

            case 4:
                viewExamDifficulty.setText(viewExamDifficulty.getText() + " Extremely Difficult");
                break;

            default:
                viewExamDifficulty.setText(viewExamDifficulty.getText() + " What did you do???");
                break;
        }

        //1.5- Exam Exact (Numerical Difficulty)...
        viewExactExamDifficulty.setText(viewExactExamDifficulty.getText() + String.valueOf(examDetails.getDouble("Exact Difficulty")));


        //2- Exam Time...
        viewExamTime.setText(viewExamTime.getText() + String.valueOf(examDetails.getFloat("Time", -1) + " mins"));

        //3- Exam Type (MCQ, Essay, or both)...
        switch (examDetails.getInt("MCQ or Essay", -1)){

            case 0:
                viewExamType.setText(viewExamType.getText() + "MCQ");
                break;

            case 1:
                viewExamType.setText(viewExamType.getText() + "Essay");
                break;

            case 2:
                viewExamType.setText(viewExamType.getText() + "MCQ and Essay");
                break;

            default:
                viewExamType.setText(viewExamType.getText() + "Unknown");
                break;
        }

        //4- Exam Material (Theory, Practical, or both)...
        switch (examDetails.getInt("Theory or Practical", -1)){

            case 0:
                viewExamMaterial.setText(viewExamMaterial.getText() + "Theory");
                break;

            case 1:
                viewExamMaterial.setText(viewExamMaterial.getText() + "Practical");
                break;

            case 2:
                viewExamMaterial.setText(viewExamMaterial.getText() + "Theory and Practical");
                break;

            default:
                viewExamMaterial.setText(viewExamMaterial.getText() + "Unknown");
                break;
        }

        //5- Exam no of questions...
        viewExamNoOfQuestions.setText(viewExamNoOfQuestions.getText() + String.valueOf(examDetails.getInt("No of Questions", -1)));

        //6- Exam teacher...
        viewExamTeacher.setText(viewExamTeacher.getText()
                + examDBHandler.getTeacherName(sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, -1), this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
