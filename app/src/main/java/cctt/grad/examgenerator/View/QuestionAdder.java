package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Model.Choice;
import cctt.grad.examgenerator.Model.Question;
import cctt.grad.examgenerator.R;

public class QuestionAdder extends AppCompatActivity {

    private TextView difficultyText = null;
    private EditText questionText, choice1, choice2, choice3, choice4 = null;
    private RadioButton mcqRBtn, pracRBtn = null;
    private RadioGroup mcqOrEssay, practicalOrTheory = null;
    private LinearLayout courseAdderLayout = null;
    private LinearLayout.LayoutParams courseAdderParams, difficultyTextParams = null;
    private Spinner difficulty = null;
    private Button addQuestion = null;
    private ExamDBHandler examDBHandler = null;
    private int courseId;
    private Bundle courseIntentData = null;
    private Handler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_adder);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Exam Handler Initialization...
        examDBHandler = new ExamDBHandler(this, null, null, 1);


        //Fetch data from course activity...
        Intent courseIntent = getIntent();
        courseIntentData = courseIntent.getExtras();
        courseId = courseIntentData.getInt("Course ID");
        final String courseName = courseIntentData.getString("Course Name");
        //setTitle(courseName);
        setTitle("Add a New Question");
        if(courseIntentData.isEmpty()){
            return;
        }

        //Widget Initialization...
        difficultyText = (TextView) findViewById(R.id.difficultyText);
        questionText = (EditText) findViewById(R.id.questionText);

        mcqOrEssay = (RadioGroup) findViewById(R.id.mcq_or_essay);
        practicalOrTheory = (RadioGroup) findViewById(R.id.practical_or_theory);
        difficulty = (Spinner) findViewById(R.id.difficulty);
        addQuestion = (Button) findViewById(R.id.addQuestion);
        mcqRBtn = (RadioButton) findViewById(R.id.mcq);
        pracRBtn = (RadioButton) findViewById(R.id.practical);

        //Setting default choices for question types...
        mcqRBtn.setChecked(true);
        pracRBtn.setChecked(true);

        choice1 = (EditText) findViewById(R.id.choice1);
        choice2 = (EditText) findViewById(R.id.choice2);
        choice3 = (EditText) findViewById(R.id.choice3);
        choice4 = (EditText) findViewById(R.id.choice4);

        //Layout and Params initialization...
        courseAdderLayout = (LinearLayout) findViewById(R.id.courseAdderLayout);
        difficultyTextParams = (LinearLayout.LayoutParams) difficultyText.getLayoutParams();


        //Spinner Adapter Initialization
        Vector<String> diffVector = new Vector<>();
        for(int i=1; i<=10; i++)
            diffVector.add(String.valueOf(i));
        SpinnerAdapter difficultyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, diffVector);
        difficulty.setAdapter(difficultyAdapter);

        mcqOrEssay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.mcq){
                    choice1.setVisibility(View.VISIBLE);
                    choice2.setVisibility(View.VISIBLE);
                    choice3.setVisibility(View.VISIBLE);
                    choice4.setVisibility(View.VISIBLE);
                    //difficultyTextParams.addRule(RelativeLayout.BELOW, choice4.getId());
                    courseAdderLayout.requestLayout();
                }else{
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    choice4.setVisibility(View.GONE);
                    //difficultyTextParams.removeRule(RelativeLayout.BELOW);
                    //difficultyTextParams.addRule(RelativeLayout.BELOW, mcqOrEssay.getId());
                    courseAdderLayout.requestLayout();
                }
            }
        });

        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionText.setError(null);
                choice1.setError(null);
                if(questionText.getText().toString().isEmpty())
                    questionText.setError("This field is required");
                if(mcqOrEssay.getCheckedRadioButtonId() == R.id.mcq){
                    if(choice1.getText().toString().isEmpty() && choice2.getText().toString().isEmpty()
                            && choice3.getText().toString().isEmpty() && choice4.getText().toString().isEmpty()){
                        choice1.setError("At least two choices are required");
                    }
                    else
                        try{
                            add();
                        }catch (Exception e){
                            errorAddingQuestionToast().show();
                        }
                }else try{
                    add();
                }catch (Exception e){
                    errorAddingQuestionToast().show();
                    e.printStackTrace();
                }

            }
        });
    }
    public void add(){
        //Question Data variable declaration...
        String _questionText;
        int _mcqOrEssay, _practicalOrTheory, _difficulty, _time;

        //Variable initialization for question parameters...
        _questionText = questionText.getText().toString();

        //Acquiring binary value of question type via selected radio button
        if(mcqOrEssay.getCheckedRadioButtonId() == R.id.mcq)
            _mcqOrEssay = 0;
        else
            _mcqOrEssay = 1;

        if(practicalOrTheory.getCheckedRadioButtonId() == R.id.practical)
            _practicalOrTheory = 0;
        else
            _practicalOrTheory = 1;

        _difficulty = Integer.valueOf(difficulty.getSelectedItem().toString());

        Question question = new Question(_questionText, _practicalOrTheory, _mcqOrEssay, _difficulty, courseId);

        examDBHandler.addQuestion(question);
        if(_mcqOrEssay == 0){
            Vector<Choice> choices = new Vector<Choice>();
            int questionId = examDBHandler.getQuestionIdByText(question.get_text());
            String choice1Text = choice1.getText().toString();
            String choice2Text = choice2.getText().toString();
            String choice3Text = choice3.getText().toString();
            String choice4Text = choice4.getText().toString();
            Choice choice;
            if(! choice1Text.equals("")){
                choice = new Choice(choice1Text, questionId);
                choices.add(choice);
            }
            if(! choice2Text.equals("")){
                choice = new Choice(choice2Text, questionId);
                choices.add(choice);
            }
            if(! choice3Text.equals("")){
                choice = new Choice(choice3Text, questionId);
                choices.add(choice);
            }
            if(! choice4Text.equals("")){
                choice = new Choice(choice4Text, questionId);
                choices.add(choice);
            }

            examDBHandler.addChoices(choices);
        }

        questionAddedSuccessfullyToast().show();
        questionText.setText("");
        choice1.setText("");
        choice2.setText("");
        choice3.setText("");
        choice4.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                /*
                Intent backIntent = new Intent(this, QuestionManagement.class);
                backIntent.putExtras(courseIntentData);
                startActivity(backIntent);*/
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Toast questionAddedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Question added successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.question_added, 0, 0, 0);
        }

        return toast;
    }

    public Toast errorAddingQuestionToast(){

        Toast toast = Toast.makeText(this, "An error occurred while adding question\nTry again, please", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.question_deleted, 0, 0, 0);
        }

        return toast;

    }

    public void stubMethod(){

        for(int i=1; i<=10; i++){

            for(int j=1; j<=50; j++){

                Question question = new Question();
                question.set_text("Question " + j + " Difficulty " + i);
                question.set_difficulty(i);
                question.set_pracOrTheory(0);
                question.set_mcqOrRegular(0);
                question.set_course(courseId);
                examDBHandler.addQuestion(question);
            }

            for(int j=1; j<=50; j++){

                Question question = new Question();
                question.set_text("Question " + j + " Difficulty " + i);
                question.set_difficulty(i);
                question.set_pracOrTheory(0);
                question.set_mcqOrRegular(1);
                question.set_course(courseId);
                examDBHandler.addQuestion(question);
            }

            for(int j=1; j<=50; j++){

                Question question = new Question();
                question.set_text("Question " + j + " Difficulty " + i);
                question.set_difficulty(i);
                question.set_pracOrTheory(1);
                question.set_mcqOrRegular(0);
                question.set_course(courseId);
                examDBHandler.addQuestion(question);
            }

            for(int j=1; j<=50; j++){

                Question question = new Question();
                question.set_text("Question " + j + " Difficulty " + i);
                question.set_difficulty(i);
                question.set_pracOrTheory(1);
                question.set_mcqOrRegular(1);
                question.set_course(courseId);
                examDBHandler.addQuestion(question);
            }

        }
    }
}
