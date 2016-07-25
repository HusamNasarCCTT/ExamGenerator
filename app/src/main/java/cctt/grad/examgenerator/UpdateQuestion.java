package cctt.grad.examgenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Vector;

public class UpdateQuestion extends Activity {


    private TextView difficultyText = null;
    private EditText questionText, choice1, choice2, choice3, choice4 = null;
    private RadioButton mcqRBtn, pracRBtn = null;
    private RadioGroup mcqOrEssay, practicalOrTheory = null;
    private RelativeLayout courseAdderLayout = null;
    private RelativeLayout.LayoutParams courseAdderParams, difficultyTextParams = null;
    private Spinner difficulty = null;
    private Button updateQuestion = null;
    private ExamDBHandler examDBHandler = null;
    private int courseId;
    private RelativeLayout updateQuestionLayout = null;
    private Intent fromQuestionManagementActivity = null;
    private Bundle oldQuestionData = null;

    private Question _oldQuestion = null, _newQuestion = null;
    private Vector<Bundle> _oldChoices = null;
    private Vector<Choice> _newChoices = null;
    private Vector<EditText> choiceTextField = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_question);

        //Initializing DB Handler...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Initializing Widgets...
        questionText = (EditText) findViewById(R.id.questionTextEdit);
        mcqOrEssay = (RadioGroup) findViewById(R.id.mcq_or_essayEdit);
        practicalOrTheory = (RadioGroup) findViewById(R.id.practical_or_theoryEdit);
        choice1 = (EditText) findViewById(R.id.choice1Edit);
        choice2 = (EditText) findViewById(R.id.choice2Edit);
        choice3 = (EditText) findViewById(R.id.choice3Edit);
        choice4 = (EditText) findViewById(R.id.choice4Edit);
        difficultyText = (TextView) findViewById(R.id.difficultyTextEdit);
        difficulty = (Spinner) findViewById(R.id.difficultyEdit);
        updateQuestion = (Button) findViewById(R.id.addQuestionEdit);

        //Initializing reference to Layout...
        updateQuestionLayout = (RelativeLayout) findViewById(R.id.updateQuestionLayout);

        //Initializing "Difficulty" text layout parameters for managing widget visibility...
        difficultyTextParams = (RelativeLayout.LayoutParams) difficultyText.getLayoutParams();

        //Initializing Adapter for Difficulty Spinner...
        Vector<String> diffVector = new Vector<>();
        for(int i = 1; i <= 10 ; i++)
            diffVector.add(String.valueOf(i));
        SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, diffVector);
        difficulty.setAdapter(adapter);


        //MCQ Choice visibility control...
        mcqOrEssay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.mcqEdit){
                    choice1.setVisibility(View.VISIBLE);
                    choice2.setVisibility(View.VISIBLE);
                    choice3.setVisibility(View.VISIBLE);
                    choice4.setVisibility(View.VISIBLE);
                    //difficultyTextParams.addRule(RelativeLayout.BELOW, choice4.getId());
                    updateQuestionLayout.requestLayout();
                }else{
                    choice1.setVisibility(View.GONE);
                    choice2.setVisibility(View.GONE);
                    choice3.setVisibility(View.GONE);
                    choice4.setVisibility(View.GONE);
                    //difficultyTextParams.removeRule(RelativeLayout.BELOW);
                    //difficultyTextParams.addRule(RelativeLayout.BELOW, mcqOrEssay.getId());
                    updateQuestionLayout.requestLayout();
                }
            }
        });


        //Getting old question data from previous intent...
        fromQuestionManagementActivity = getIntent();
        oldQuestionData = fromQuestionManagementActivity.getExtras();
        _oldQuestion = new Question();
        _oldQuestion.set_id(oldQuestionData.getInt(examDBHandler.KEY_QUESTION_ID));
        _oldQuestion.set_text(oldQuestionData.getString(examDBHandler.KEY_QUESTION_TEXT));
        _oldQuestion.set_mcqOrRegular(oldQuestionData.getInt(examDBHandler.KEY_QUESTION_MCQ_OR_ESSAY));
        _oldQuestion.set_pracOrTheory(oldQuestionData.getInt(examDBHandler.KEY_QUESTION_PRACTICAL_OR_THEORY));
        _oldQuestion.set_difficulty(oldQuestionData.getInt(examDBHandler.KEY_QUESTION_DIFFICULTY));
        _oldQuestion.set_time(oldQuestionData.getFloat(examDBHandler.KEY_QUESTION_TIME));

        //Setting old question data across widgets...
        questionText.setHint(_oldQuestion.get_text());
        if(_oldQuestion.get_mcqOrRegular() == 0)
            mcqOrEssay.check(R.id.mcqEdit);
        else
            mcqOrEssay.check(R.id.essayEdit);

        if(_oldQuestion.get_pracOrTheory() == 0)
            practicalOrTheory.check(R.id.theoryEdit);
        else
            practicalOrTheory.check(R.id.practicalEdit);

        difficulty.setSelection(_oldQuestion.get_difficulty()-1);

        //Setting up choice EditText widgets with choices...
        choiceTextField = new Vector<>();
        choiceTextField.add(choice1);choiceTextField.add(choice2);choiceTextField.add(choice3);choiceTextField.add(choice4);
        if(_oldQuestion.get_mcqOrRegular() == 0){
            //To run through the choices via EditText widgets...
            _oldChoices = examDBHandler.getChoicesByQuestionId(_oldQuestion.get_id());
            int i=0;
            for (Bundle _oldChoice : _oldChoices){
                choiceTextField.get(i).setHint(_oldChoice.getString(examDBHandler.KEY_CHOICE_TEXT));
                i++;
            }
        }

        updateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputValidator(questionText, choice1, choice2, choice3, choice4)){
                    Toast.makeText(UpdateQuestion.this, "Please enter at least one parameter", Toast.LENGTH_SHORT).show();
                }else{
                    update();
                    questionText.setHint(_newQuestion.get_text());
                    questionText.setText("");
                    Toast.makeText(UpdateQuestion.this, "Question updated successfully", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(UpdateQuestion.this, QuestionManagement.class));
                }
            }
        });

    }

    public boolean inputValidator(EditText _questionText, EditText _choice1, EditText _choice2,
                               EditText _choice3, EditText _choice4){

        if(_questionText.getText().toString().isEmpty() && _choice1.getText().toString().isEmpty()
                && _choice2.getText().toString().isEmpty() && _choice3.getText().toString().isEmpty()
                && _choice4.getText().toString().isEmpty())
            return true;
        else
            return false;

    }

    public void update(){

        _newQuestion = new Question();
        _newQuestion.set_id(_oldQuestion.get_id());

        //1. Question Text...
        if(questionText.getText().toString().isEmpty())
            _newQuestion.set_text(_oldQuestion.get_text());
        else
            _newQuestion.set_text(questionText.getText().toString());


        //2. Question Definer (MCO or Essay)...
        if(mcqOrEssay.getCheckedRadioButtonId() == R.id.mcqEdit)
            _newQuestion.set_mcqOrRegular(0);
        else
            _newQuestion.set_mcqOrRegular(1);

        //3. Question Definer (Theory or Practical)...
        if(practicalOrTheory.getCheckedRadioButtonId() == R.id.theoryEdit)
            _newQuestion.set_pracOrTheory(0);
        else
            _newQuestion.set_pracOrTheory(1);

        //4. Question Difficulty...
        _newQuestion.set_difficulty(difficulty.getSelectedItemPosition()+1);

        if(_newQuestion.get_mcqOrRegular() == 0){
            _newChoices = new Vector<>();
            for(EditText singleChoice : choiceTextField){
                //if(singleChoice.getText().toString().isEmpty())
                if(! singleChoice.getText().toString().isEmpty()){
                    Choice choice = new Choice();
                    choice.set_text(singleChoice.getText().toString());
                    choice.set_questionId(_newQuestion.get_id());
                    _newChoices.add(choice);
                }
            }
            examDBHandler.updateQuestion(_newQuestion, _newChoices);
        }
        else{
            examDBHandler.updateQuestion(_newQuestion, null);
        }



    }


}
