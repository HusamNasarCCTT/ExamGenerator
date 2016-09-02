package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.R;

public class ExamGenerator extends AppCompatActivity {

    //Widget Declaration (GUI)...
    private RadioGroup mcqOrEssay, testDefiner = null;
    RadioButton mcqGen, essayGen, byTime = null;
    private CheckBox theoryGen, practicalGen = null;
    private Spinner examDifficulty = null;
    private EditText examTime, examNoOFQuestions = null;
    private Button generateExam = null;
    private FloatingActionButton examGeneratorAssistButton = null;
    private SeekBar timeSeeker, noOfQuestionsSeeker = null;
    private TextView timeSeekerValue, noOfQuestionsSeekerValue = null;
    private Intent intent = null;
    private Bundle courseIntentData = null;
    private int courseId = 0, examGenerationMethod;

    //Key Strings for Exam Parameter bundle to be passed to DisplayExam Activity...
    private final String KEY_MCQ_OR_ESSAY = "MCQ or Essay";
    private final String KEY_THEORY_OR_PRACTICAL = "Theory or Practical";
    private final String KEY_DIFFICULTY = "Difficulty";
    private final String KEY_TIME = "Time";
    private final String KEY_NO_OF_QUESTIONS = "No Of Questions";
    private final String KEY_EXAM_GENERATION_METHOD = "Method";

    //Key Minimum and Maximum values for Seekbars (Minimum and Maximum EXAMTIME and NO OF QUESTIONS)...
    private final int MINIMUM_EXAM_TIME = 10;
    private final int MAXIMUM_EXAM_TIME = 120;
    private final int MAXIMUM_NO_OF_ESSAYS = 10;
    private final int MAXIMUM_NO_OF_MCQS = 100;
    private final int MINIMUM_NO_OF_ESSAYS = 1;
    private final int MINIMUM_NO_OF_MCQS = 1;

    private int userInputTime = 0;
    private int userInputNoOfQuestions = 0;

    //DB Handler Declaration...
    private ExamDBHandler examDBHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_generator_activity);
        setTitle("Select Exam Parameters");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Getting Course ID...
        courseIntentData = getIntent().getExtras();
        courseId = courseIntentData.getInt("Course ID", -1);

        //Widget Initialization...
        mcqOrEssay = (RadioGroup) findViewById(R.id.mcqOrEssay);
        theoryGen = (CheckBox) findViewById(R.id.theoryGen);
        practicalGen = (CheckBox) findViewById(R.id.practicalGen);
        examDifficulty = (Spinner) findViewById(R.id.examDifficulty);
        generateExam = (Button) findViewById(R.id.generateExam);
        mcqGen = (RadioButton) findViewById(R.id.mcqGen);
        timeSeeker = (SeekBar) findViewById(R.id.timeSeeker);
        noOfQuestionsSeeker = (SeekBar) findViewById(R.id.noOfQuestionsSeeker);
        timeSeekerValue = (TextView) findViewById(R.id.timeSeekerValue);
        noOfQuestionsSeekerValue = (TextView) findViewById(R.id.noOfQuestionsSeekerValue);
        examGeneratorAssistButton = (FloatingActionButton) findViewById(R.id.examGeneratorAssistButton);

        //Initial Selections for Radio buttons and Checkboxes...
        theoryGen.setChecked(true);
        mcqGen.setChecked(true);

        //Initial exam generation method...
        // 1-> By Difficulty, 2-> By Time, 3-> By No of Questions...
        examGenerationMethod = 1;

        //Setting Minimum and Maximum values for Time and No of Question Seekbars...
        timeSeeker.setMax(MAXIMUM_EXAM_TIME - MINIMUM_EXAM_TIME);
        if(mcqOrEssay.getCheckedRadioButtonId() == R.id.mcqGen)
            noOfQuestionsSeeker.setMax(MAXIMUM_NO_OF_MCQS - MINIMUM_NO_OF_MCQS);
        else
            noOfQuestionsSeeker.setMax(MAXIMUM_NO_OF_ESSAYS - MINIMUM_NO_OF_ESSAYS);

        //DB Handler Initialization...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Initializing default seekbar values...
        timeSeekerValue.setText("Time: " + String.valueOf(MINIMUM_EXAM_TIME) + " mins");
        noOfQuestionsSeekerValue.setText("No of Questions: " + String.valueOf(MINIMUM_NO_OF_MCQS));

        timeSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    progress = MINIMUM_EXAM_TIME + progress;
                userInputTime = progress;
                timeSeekerValue.setText("Time: " + String.valueOf(progress) + " mins");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        noOfQuestionsSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                switch (mcqOrEssay.getCheckedRadioButtonId()){
                    case R.id.mcqGen:

                        noOfQuestionsSeeker.setMax(MAXIMUM_NO_OF_MCQS - MINIMUM_NO_OF_MCQS);
                        progress = MINIMUM_NO_OF_MCQS + progress;
                        noOfQuestionsSeekerValue.setText("No of Questions: " + String.valueOf(progress));
                        break;
                    case R.id.essayGen:

                        noOfQuestionsSeeker.setMax(MAXIMUM_NO_OF_ESSAYS - MINIMUM_NO_OF_ESSAYS);
                        progress = MINIMUM_NO_OF_ESSAYS + progress;
                        noOfQuestionsSeekerValue.setText("No of Questions: " + String.valueOf(progress));
                        break;

                    case R.id.bothGen:

                        noOfQuestionsSeeker.setMax(MAXIMUM_NO_OF_MCQS - MINIMUM_NO_OF_MCQS);
                        progress = MINIMUM_NO_OF_MCQS + progress;
                        noOfQuestionsSeekerValue.setText("No of Questions: " + String.valueOf(progress));
                }
                userInputNoOfQuestions = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        generateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean theoryIsChecked = theoryGen.isChecked();
                boolean practicalIsChecked = practicalGen.isChecked();

                if(theoryOrPracticalValidator(theoryIsChecked, practicalIsChecked)){
                    generateAndDisplay();
                }
                else{
                    Toast.makeText(ExamGenerator.this, "Please enter all Exam parameters", Toast.LENGTH_SHORT).show();
                }
            }
        });

        examGeneratorAssistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpAssistingSnackBar();
            }
        });

    }

    public boolean theoryOrPracticalValidator(boolean theoryIsChecked, boolean practicalIsChecked){

        return theoryIsChecked || practicalIsChecked;
    }



    public void generateAndDisplay(){
        //Temporary parameters to define exam parameters...
        int mcqOrEssaySelection, _mcqOrEssay, _theoryOrPractical = 1, _difficulty, _noOfQuestions;
        float _time = 0;

        mcqOrEssaySelection = mcqOrEssay.getCheckedRadioButtonId();
        switch (mcqOrEssaySelection){
            case R.id.mcqGen:
                _mcqOrEssay = 0;
                break;

            case R.id.essayGen:
                _mcqOrEssay = 1;
                break;

            case R.id.bothGen:
                _mcqOrEssay = 2;
                break;

            default:
                _mcqOrEssay = 2;
                break;
        }

        if(practicalGen.isChecked())
            _theoryOrPractical = 0;
        if(theoryGen.isChecked())
            _theoryOrPractical = 1;
        if(practicalGen.isChecked() && theoryGen.isChecked())
            _theoryOrPractical = 2;


        //Preparing bundle with exam parameters to pass into DisplayExam Activity...
        Bundle examParameters = new Bundle();
        examParameters.putInt(KEY_MCQ_OR_ESSAY, _mcqOrEssay);
        examParameters.putInt(KEY_THEORY_OR_PRACTICAL, _theoryOrPractical);
        examParameters.putInt(KEY_EXAM_GENERATION_METHOD, examGenerationMethod);


        //Generating Exam based on chosen method...
        switch (examGenerationMethod){

            //By Difficulty...
            case 1:
                //Defining Difficulty based on Item Position:
                //0 -> Easy. 1 -> Moderate. 2 -> Difficult. 3 -> Extremely Difficult.
                _difficulty = examDifficulty.getSelectedItemPosition() + 1;
                examParameters.putInt(KEY_DIFFICULTY, _difficulty);
                //Defining Exam time by Difficulty...
                switch (_difficulty){
                    case 1:
                        _time = 45;
                        break;
                    case 2:
                        _time = 60;
                        break;
                    case 3:
                        _time = 90;
                        break;
                    case 4:
                        _time = 120;
                        break;
                }
                examParameters.putFloat(KEY_TIME, _time);
                break;

            //By Time...
            case 2:
                examParameters.putFloat(KEY_TIME, userInputTime);
                break;

            //By Number of Questions...
            case 3:
                examParameters.putInt(KEY_NO_OF_QUESTIONS, userInputNoOfQuestions);
                examParameters.putFloat(KEY_TIME, MAXIMUM_EXAM_TIME);
                break;

            default:
                Toast.makeText(ExamGenerator.this, "Something's wrong, pato'", Toast.LENGTH_SHORT).show();
        }


        intent = new Intent(ExamGenerator.this, DisplayExam.class);
        intent.putExtra("Course ID", courseId);
        intent.putExtras(examParameters);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.exam_generation_modes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.byDifficultyMode:
                selectByDifficultyGenerationMode();
                return true;

            case R.id.byTimeMode:
                selectByTimeGenerationMode();
                return true;

            case R.id.byNoOfQuestionsMode:
                selectByNoOfQuestionsGenerationMode();
                return true;

            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void selectByTimeGenerationMode(){

        examGenerationMethod = 2;
        timeSeekerValue.setVisibility(View.VISIBLE);
        timeSeeker.setVisibility(View.VISIBLE);
        examDifficulty.setVisibility(View.GONE);
        noOfQuestionsSeeker.setVisibility(View.GONE);
        noOfQuestionsSeekerValue.setVisibility(View.GONE);
    }

    public void selectByDifficultyGenerationMode(){

        examGenerationMethod = 1;
        timeSeekerValue.setVisibility(View.GONE);
        timeSeeker.setVisibility(View.GONE);
        examDifficulty.setVisibility(View.VISIBLE);
        noOfQuestionsSeeker.setVisibility(View.GONE);
        noOfQuestionsSeekerValue.setVisibility(View.GONE);
    }

    public void selectByNoOfQuestionsGenerationMode(){

        examGenerationMethod = 3;
        timeSeekerValue.setVisibility(View.GONE);
        timeSeeker.setVisibility(View.GONE);
        examDifficulty.setVisibility(View.GONE);
        noOfQuestionsSeeker.setVisibility(View.VISIBLE);
        noOfQuestionsSeekerValue.setVisibility(View.VISIBLE);
    }


    public void popUpAssistingSnackBar(){

        Snackbar.make(findViewById(R.id.exam_generator_layout), "Choose Exam Type: (Type of Questions to include in the exam)", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(findViewById(R.id.exam_generator_layout), "Choose Exam Material (The material to be included in the exam)", Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar.make(findViewById(R.id.exam_generator_layout), "Tap Actionbar Menu to choose from various generation methods", Snackbar.LENGTH_INDEFINITE)
                                                .setAction("OK", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                }).show();
                                    }
                                }).show();
                    }
                }).show();
    }




}
