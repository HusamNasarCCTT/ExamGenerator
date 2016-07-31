package cctt.grad.examgenerator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

public class ViewExamDetails extends AppCompatActivity {

    private TextView viewExactExamDifficulty = null,
                     viewExamTime = null,
                     viewExamType = null,
                     viewExamMaterial = null,
                     viewExamNoOfQuestions = null,
                     viewExamTeacher = null,
                     viewNoOfMCQs,
                     viewNoOfEssays,
                     theoryPercentage,
                     practicalPercentage;
    private int courseId;
    private Button viewExamDifficulty = null;
    private FloatingActionButton viewExamDetailsAssist = null;
    private Intent previousActivityIntent = null;
    private Bundle examDetails = null;
    private SessionManager sessionManager = null;
    private ExamDBHandler examDBHandler = null;
    private GraphView graph = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_exam_details_coordinator);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewExamDifficulty = (Button) findViewById(R.id.viewExamDifficulty);
        viewExamTime = (TextView) findViewById(R.id.viewExamTime);
        viewExamNoOfQuestions = (TextView) findViewById(R.id.viewExamNoOfQuestions);
        viewNoOfMCQs = (TextView) findViewById(R.id.viewExamNoOfMCQs);
        viewNoOfEssays = (TextView) findViewById(R.id.viewExamNoOfEssays);
        theoryPercentage = (TextView) findViewById(R.id.theoryPercentage);
        practicalPercentage = (TextView) findViewById(R.id.practicalPercentage);
        viewExamDetailsAssist = (FloatingActionButton) findViewById(R.id.viewExamDetailsAssist);

        courseId = getIntent().getIntExtra("Course ID", -1);

        //Get Exam Details from previous Activity...
        previousActivityIntent = getIntent();
        examDetails = previousActivityIntent.getExtras();

            int noOfQuestions = 1;
            try {
                noOfQuestions = examDetails.getInt("No of Questions");
            }catch (Exception e){
                Toast.makeText(ViewExamDetails.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            int[] questionDifficulties = examDetails.getIntArray("Question Difficulties");

            graph = (GraphView) findViewById(R.id.graph);
            DataPoint[] dataPoints = new DataPoint[noOfQuestions];
            dataPoints[0] = new DataPoint(0,0);

            for(int i=0; i<questionDifficulties.length; i++){

                dataPoints[i] = new DataPoint(i, questionDifficulties[i]);
            }

            if(noOfQuestions > 10){
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                graph.addSeries(series);
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Snackbar.make(findViewById(R.id.view_exam_details_layout), "Question: " +
                                String.valueOf((int) dataPoint.getX()+1) + "\tDifficulty: " + String.valueOf(dataPoint.getY()), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });

            }else{
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);
                //LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
                series.setSpacing(10);
                graph.addSeries(series);
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Snackbar.make(findViewById(R.id.view_exam_details_layout), "Question: " +
                                String.valueOf((int) dataPoint.getX()+1) + "\tDifficulty: " + String.valueOf(dataPoint.getY()), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });
            }



            //Setting Minimum and Maximum (Range) for GraphView...
            graph.getViewport().setMinX(0);
            graph.getViewport().setMinY(0);
            graph.getViewport().setMaxX(noOfQuestions);
            graph.getViewport().setMaxY(10);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setScrollable(true);
            graph.getViewport().setScalable(true);
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Questions");
            graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graph.getGridLabelRenderer().setVerticalAxisTitle("Difficulty");



            //Initializing Session Manager and DB Handler...
            examDBHandler = new ExamDBHandler(this, null, null, 1);
            sessionManager = new SessionManager(getApplicationContext());


            //Set Exam Course Name onto title...
            setTitle(examDetails.getString("Course Name") + " Exam Dashboard");

            //Set data onto widgets...
            // 1- Exam Difficulty...
            switch (examDetails.getInt("Visual Difficulty", -1)){
                case 1:
                    viewExamDifficulty.setText("Easy");
                    viewExamDifficulty.setTextColor(Color.parseColor("#FF4DB8ED"));
                    break;

                case 2:
                    viewExamDifficulty.setText("Moderate");

                    viewExamDifficulty.setTextColor(Color.parseColor("#FF69F9AE"));
                    viewExamDifficulty.setTextColor(Color.argb(125, 0, 128, 0));
                    break;

                case 3:
                    viewExamDifficulty.setText("Difficult");
                    viewExamDifficulty.setTextColor(Color.YELLOW);
                    break;

                case 4:
                    viewExamDifficulty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                    viewExamDifficulty.setTextColor(Color.RED);
                    viewExamDifficulty.setText("Extremely Difficult");
                    break;

                default:
                    viewExamDifficulty.setText("What did you do???");
                    break;
            }

            //2- Exam Time...
            viewExamTime.setText(String.valueOf(examDetails.getFloat("Time", -1)));

            //3- Exam Type (MCQ, Essay, or both)...
            viewNoOfMCQs.setText(String.valueOf(examDetails.getInt("No of MCQs")));
            viewNoOfEssays.setText(String.valueOf(examDetails.getInt("No of Essays")));

            //5- Exam no of questions...
            viewExamNoOfQuestions.setText(String.valueOf(examDetails.getInt("No of Questions", -1)));

            viewExamDifficulty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ViewExamDetails.this, String.valueOf(examDetails.getDouble("Exact Difficulty") + "/" + "10"), Toast.LENGTH_SHORT).show();
                }
            });

            int numOfQuestions = examDetails.getInt("No of Questions");
            int numOfTheories = examDetails.getInt("No of Theories"),
                    numOfPracticals = examDetails.getInt("No of Practicals");
            if(numOfTheories > 0){
                int theoryPercentageValue = numOfTheories * 100 / numOfQuestions;
                theoryPercentage.setText(String.valueOf(theoryPercentageValue) + "%");
            }else{
                theoryPercentage.setText(String.valueOf(0) + "%");
            }

            if(numOfPracticals > 0){
                int practicalPercentageValue = numOfPracticals * 100 / numOfQuestions;
                practicalPercentage.setText(String.valueOf(practicalPercentageValue) + "%");
            }else{
                practicalPercentage.setText(String.valueOf(0 + "%"));
            }


        viewExamDetailsAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.examDetailsCoordinator), "Tap on graph to view the question number and its exact difficulty", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(findViewById(R.id.examDetailsCoordinator), "Also, you can view the exact exam difficulty (out of 10)", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Snackbar.make(findViewById(R.id.examDetailsCoordinator), "Just tap the difficulty card.", Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("OK", new View.OnClickListener() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                try {
                    finish();
                }catch (Exception e){
                    Toast.makeText(ViewExamDetails.this, e.toString(), Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


}
