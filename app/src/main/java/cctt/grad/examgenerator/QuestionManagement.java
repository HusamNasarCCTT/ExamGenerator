package cctt.grad.examgenerator;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Vector;

public class QuestionManagement extends AppCompatActivity {

    private ListView questionList = null;
    private Button addQuestions, toExamGeneration = null;
    private Intent intent = null;
    private ExamDBHandler examDBHandler;
    Intent courseIntent = null;
    private Bundle courseIntentData = null;
    private int courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_management);

        //To display Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Widget Initialization......
        questionList = (ListView) findViewById(R.id.questionList);
        addQuestions = (Button) findViewById(R.id.addQuestions);
        toExamGeneration = (Button) findViewById(R.id.toExamGeneration);

        //DB Handler initialization...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Registering listview for Context menu...
        registerForContextMenu(questionList);


        //Fetch data from course activity...
        courseIntent = getIntent();
        courseIntentData = courseIntent.getExtras();
        courseId = courseIntentData.getInt("Course ID");
        if(courseIntentData.isEmpty()){
            return;
        }
        final String courseName = courseIntentData.getString("Course Name");
        setTitle(courseName);

        questionList.setAdapter(getQuestionListAdapter(courseId));

        addQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(new Intent(QuestionManagement.this, QuestionAdder.class));
                intent.putExtra("Course ID", courseId);
                intent.putExtra("Course Name", courseName);
                startActivity(intent);
            }
        });


        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = (Bundle) parent.getItemAtPosition(position);
                int questionId = bundle.getInt("Question Id");
                int isMCQ = bundle.getInt("Mcq or Essay");
                if(questionId == 0){
                    Toast.makeText(QuestionManagement.this, "Please add a question to the course", Toast.LENGTH_SHORT).show();
                }else{
                    intent = new Intent(QuestionManagement.this, DisplayQuestion.class);
                    intent.putExtra("Question Bundle", bundle);
                    intent.putExtra("Course Name", courseName);
                    intent.putExtra("Activity Name", "QuestionManagement");
                    intent.putExtra("courseIntentData", courseIntentData);
                    startActivity(intent);
                    }
            }
        });

        toExamGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(QuestionManagement.this, ExamGenerator.class);
                intent.putExtras(courseIntentData);
                startActivity(intent);
                //new ExamGeneratorDialog().show(getSupportFragmentManager(), "Exam Generator");

            }
        });


    }

    @Override
    protected void onStart() {
        questionList.setAdapter(getQuestionListAdapter(courseId));
        super.onStart();
    }


    public ListAdapter getQuestionListAdapter(int courseId){

        Vector<Bundle> questionList = examDBHandler.readQuestion(courseId);

        ListAdapter listAdapter = new CustomQuestionAdapter2(this, questionList);

        return listAdapter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_delete_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //To get a reference to the item clicked to trigger the context menu...
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Selected item index...
        int selectedItem = info.position;

        Bundle bundle = (Bundle) questionList.getItemAtPosition(selectedItem);
        final int questionId = bundle.getInt("Question Id");

        switch (item.getItemId()){

            case R.id.context_edit:
                Intent toUpdateQuestionActivity = new Intent(QuestionManagement.this, UpdateQuestion.class);
                toUpdateQuestionActivity.putExtras(bundle);
                startActivity(toUpdateQuestionActivity);
                return true;
            case R.id.context_delete:

                Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                examDBHandler.removeQuestion(questionId);
                                questionList.setAdapter(getQuestionListAdapter(courseId));
                                Toast.makeText(QuestionManagement.this, "Question removed successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:

                Intent backIntent = new Intent(this, CourseManagement.class);
                startActivity(backIntent);
                finish();
                return true;
            case R.id.context_delete_all:
                //Making sure there is actual data to be deleted...
                Bundle bundle = (Bundle) questionList.getItemAtPosition(0);
                int questionId = bundle.getInt("Question Id", -1);
                if(questionId == 0){
                    Snackbar.make(getCurrentFocus(), "Course is already empty", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Cool 😎", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }else{
                    Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    examDBHandler.deleteCourseQuestions(courseId);
                                    questionList.setAdapter(getQuestionListAdapter(courseId));
                                    Toast.makeText(QuestionManagement.this, "Questions deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
