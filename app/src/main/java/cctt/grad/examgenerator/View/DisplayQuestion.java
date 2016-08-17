package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.R;

public class DisplayQuestion extends AppCompatActivity {


    private TextView displayQuestionText, displayQuestionType1, displayQuestionType2,
                     displayQuestionDifficulty, displayQuestionTime,
                     displayChoice1, displayChoice2, displayChoice3, displayChoice4 = null;

    private ExamDBHandler examDBHandler = null;
    private int courseId;
    private Intent intent = null;
    private Bundle questionBundle = null,
                   examParameterBundle = null;
    private String courseName, activityName = null;
    private CardView choiceContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_question);

        //To display Back/Home button on Actionbar...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting Intent Data (Bundle) to display question...
        intent = getIntent();
        courseId = intent.getIntExtra("Course ID", -1);
        courseName = intent.getStringExtra("Course Name");
        activityName = intent.getStringExtra("Activity Name");
        if(activityName.equals("QuestionManagement"))
            getSupportActionBar().setTitle(courseName);
        if(activityName.equals("DisplayExam")){
            examParameterBundle = getIntent().getBundleExtra("Exam Parameters");
            setTitle(courseName + " Exam");
        }
        questionBundle = intent.getBundleExtra("Question Bundle");

        //Initializing DB Handler...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Initializing Widgets...
        displayQuestionText = (TextView) findViewById(R.id.displayQuestionText);
        displayQuestionType1 = (TextView) findViewById(R.id.displayQuestionType1);
        displayQuestionType2 = (TextView) findViewById(R.id.displayQuestionType2);
        displayQuestionDifficulty = (TextView) findViewById(R.id.displayQuestionDifficulty);
        displayQuestionTime = (TextView) findViewById(R.id.displayQuestionTime);
        displayChoice1 = (TextView) findViewById(R.id.displayChoice1);
        displayChoice2 = (TextView) findViewById(R.id.displayChoice2);
        displayChoice3 = (TextView) findViewById(R.id.displayChoice3);
        displayChoice4 = (TextView) findViewById(R.id.displayChoice4);

        displayChoice1.setVisibility(View.INVISIBLE);
        displayChoice2.setVisibility(View.INVISIBLE);
        displayChoice3.setVisibility(View.INVISIBLE);
        displayChoice4.setVisibility(View.INVISIBLE);

        //Acquiring question data from bundle...
        int questionId = questionBundle.getInt("Question Id");
        String questionText = questionBundle.getString("Question Text");
        String questionType1;
        String questionType2;
        String questionDifficulty = String.valueOf(questionBundle.getInt("Difficulty"));
        String questionTime = String.valueOf(questionBundle.getFloat("Time"));


        if(questionBundle.getInt("Mcq or Essay") == 0){
            questionType1 = "Mcq";
            Vector<Bundle> choices = examDBHandler.getChoicesByQuestionId(questionId);
            Vector<TextView> displayChoiceObjects = new Vector<TextView>();
            displayChoiceObjects.add(displayChoice1);
            displayChoiceObjects.add(displayChoice2);
            displayChoiceObjects.add(displayChoice3);
            displayChoiceObjects.add(displayChoice4);
            Iterator<Bundle> iterator = choices.iterator();
            int i = 0;
            while(iterator.hasNext()){
                String choiceText = iterator.next().getString("Choice Text");
                displayChoiceObjects.elementAt(i).setText("* " + choiceText + ".");
                displayChoiceObjects.elementAt(i).setVisibility(View.VISIBLE);
                choiceContainer = (CardView) findViewById(R.id.choiceContainer);
                choiceContainer.setVisibility(View.VISIBLE);
                i++;
            }
        }else{
            questionType1 = "Essay";
        }

        if(questionBundle.getInt("Practical or Theory") == 0){
            questionType2 = "Practical";
        }else{
            questionType2 = "Theory";
        }

        displayQuestionText.setText(questionText);
        displayQuestionType1.setText(questionType1);
        displayQuestionType2.setText(questionType2);
        displayQuestionDifficulty.setText(questionDifficulty + "/10");
        displayQuestionTime.setText(questionTime);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                Intent backIntent;
                if(activityName.equals("QuestionManagement")){
                    /*
                        backIntent = new Intent(this, QuestionManagement.class);
                        final int courseId = questionBundle.getInt("Course ID");
                        Bundle backBundle = new Bundle();
                        backBundle.putInt("Course ID", courseId);
                        backIntent.putExtras(backBundle);
                        startActivity(backIntent);*/
                        finish();

                }
                if(activityName.equals("DisplayExam")){
                    /*
                        backIntent = new Intent(this, DisplayExam.class);
                        final int courseId = questionBundle.getInt("Course ID");
                        backIntent.putExtra("Course ID", courseId);
                        backIntent.putExtras(examParameterBundle);
                        startActivity(backIntent);*/
                        finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
