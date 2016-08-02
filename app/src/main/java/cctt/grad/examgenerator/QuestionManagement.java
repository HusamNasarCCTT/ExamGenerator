package cctt.grad.examgenerator;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.Vector;

public class QuestionManagement extends AppCompatActivity {

    private ListView questionList = null;
    private Intent intent = null;
    private ExamDBHandler examDBHandler;
    Intent courseIntent = null;
    private Bundle courseIntentData = null;
    private Course courseData = null;
    private FloatingActionButton popupAnimator = null, popupAssistor = null, addQuestions = null, toExamGeneration = null;;
    private int courseId;

    CoordinatorLayout questionManagementCoordinator = null;
    Animation show_popupAssistor;
    Animation hide_popupAssistor;
    Animation show_addQuestions;
    Animation hide_addQuestions;
    Animation show_toExamGeneration;
    Animation hide_toExamGeneration;

    private static final int MCQ = 0;
    private static final int ESSAY = 1;
    private static final int BOTH = 2;

    //Animator Status in order to expand or hide Action Buttons...
    private boolean FAB_Status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_management_coordinator);

        //To display Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getSupportActionBar().setElevation(0);

        //Widget Initialization......
        questionList = (ListView) findViewById(R.id.questionList);
        addQuestions = (FloatingActionButton) findViewById(R.id.addQuestions);
        toExamGeneration = (FloatingActionButton) findViewById(R.id.toExamGeneration);
        popupAssistor = (FloatingActionButton) findViewById(R.id.questionManagementPopupAssistor);
        popupAnimator = (FloatingActionButton) findViewById(R.id.questionManagementPopupAnimator);

        //Getting reference to Animator XML Files...
        show_popupAssistor = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_popupAssistor = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_addQuestions = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_addQuestions = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_toExamGeneration = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_toExamGeneration = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);

        //DB Handler initialization...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Registering listview for Context menu...
        registerForContextMenu(questionList);


        //Fetch data from course activity...
        courseIntent = getIntent();
        courseIntentData = courseIntent.getExtras();
        courseId = courseIntentData.getInt("Course ID");
        courseData = examDBHandler.getCourseById(courseId);
        if(courseIntentData.isEmpty()){
            return;
        }
        final String courseName = courseData.get_name();
        setTitle(courseName);


        questionList.setAdapter(getQuestionListAdapter(courseId, BOTH));


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
                bundle.putInt("Course ID", courseId);
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
                    //finish();
                    }
            }
        });

        toExamGeneration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionListValidator(questionList)){
                    intent = new Intent(QuestionManagement.this, ExamGenerator.class);
                    intent.putExtras(courseIntentData);
                    startActivity(intent);
                }
                //finish();
                //new ExamGeneratorDialog().show(getSupportFragmentManager(), "Exam Generator");

            }
        });

        popupAssistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.fabFrameLayout), "Tap Actionbar for more options", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        }).show();
            }
        });

        popupAnimator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

    }

    private void expandFAB() {

        //Change the drawable on the button...
        popupAnimator.setImageResource(android.R.drawable.button_onoff_indicator_on);

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupAssistor.getLayoutParams();
        layoutParams.rightMargin += (int) (popupAssistor.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (popupAssistor.getHeight() * 0.25);
        popupAssistor.setLayoutParams(layoutParams);
        popupAssistor.startAnimation(show_popupAssistor);
        popupAssistor.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) addQuestions.getLayoutParams();
        layoutParams2.rightMargin += (int) (addQuestions.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (addQuestions.getHeight() * 1.5);
        addQuestions.setLayoutParams(layoutParams2);
        addQuestions.startAnimation(show_addQuestions);
        addQuestions.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) toExamGeneration.getLayoutParams();
        layoutParams3.rightMargin += (int) (toExamGeneration.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (toExamGeneration.getHeight() * 1.7);
        toExamGeneration.setLayoutParams(layoutParams3);
        toExamGeneration.startAnimation(show_toExamGeneration);
        toExamGeneration.setClickable(true);
    }


    private void hideFAB() {

        //Change the drawable on the button...
        popupAnimator.setImageResource(android.R.drawable.button_onoff_indicator_off);

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) popupAssistor.getLayoutParams();
        layoutParams.rightMargin -= (int) (popupAssistor.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (popupAssistor.getHeight() * 0.25);
        popupAssistor.setLayoutParams(layoutParams);
        popupAssistor.startAnimation(hide_popupAssistor);
        popupAssistor.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) addQuestions.getLayoutParams();
        layoutParams2.rightMargin -= (int) (addQuestions.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (addQuestions.getHeight() * 1.5);
        addQuestions.setLayoutParams(layoutParams2);
        addQuestions.startAnimation(hide_addQuestions);
        addQuestions.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) toExamGeneration.getLayoutParams();
        layoutParams3.rightMargin -= (int) (toExamGeneration.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (toExamGeneration.getHeight() * 1.7);
        toExamGeneration.setLayoutParams(layoutParams3);
        toExamGeneration.startAnimation(hide_toExamGeneration);
        toExamGeneration.setClickable(false);
    }

    public boolean questionListValidator(ListView questionList){

        Bundle firstElement = (Bundle) questionList.getAdapter().getItem(0);
        if(firstElement.getInt("Question Id") == 0){
            Snackbar.make(findViewById(R.id.questionManagementCoordinator), "Question list is empty", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            return;
                        }
                    }).show();

            return false;
        }

        return true;

    }

    @Override
    protected void onStart() {
        questionList.setAdapter(getQuestionListAdapter(courseId, BOTH));
        super.onStart();
    }


    public ListAdapter getQuestionListAdapter(int courseId, int mcqEssayOrBoth){

        Vector<Bundle> questionList = examDBHandler.readQuestion(courseId, mcqEssayOrBoth);

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
                toUpdateQuestionActivity.putExtra("Course ID", courseId);
                startActivity(toUpdateQuestionActivity);
                return true;
            case R.id.context_delete:

                Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_SHORT)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                examDBHandler.removeQuestion(questionId);
                                questionList.setAdapter(getQuestionListAdapter(courseId, BOTH));
                                questionDeletedSuccessfullyToast().show();
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

                /*
                Intent backIntent = new Intent(this, CourseManagement.class);
                startActivity(backIntent);*/
                finish();
                return true;

            case R.id.context_view_all:
                questionList.setAdapter(getQuestionListAdapter(courseId, BOTH));
                return true;

            case R.id.context_view_mcqs:
                questionList.setAdapter(getQuestionListAdapter(courseId, MCQ));
                return true;

            case R.id.context_view_essays:
                questionList.setAdapter(getQuestionListAdapter(courseId, ESSAY));
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
                                    questionList.setAdapter(getQuestionListAdapter(courseId, BOTH));
                                    questionDeletedSuccessfullyToast().show();
                                }
                            }).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Toast questionDeletedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Question deleted successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.question_deleted, 0, 0, 0);
        }

        return toast;
    }
}
