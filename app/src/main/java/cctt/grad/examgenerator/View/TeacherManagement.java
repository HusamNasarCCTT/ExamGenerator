package cctt.grad.examgenerator.View;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.Model.Teacher;
import cctt.grad.examgenerator.R;

public class TeacherManagement extends AppCompatActivity {

    private EditText teacherName = null;
    private EditText userName = null;
    private EditText passWord = null;
    private Button teacherAdder = null;
    private ListView teacherList = null;
    private ProgressBar deleteAllTeachersProgressBar = null;
    private ExamDBHandler examDBHandler = null;
    private SessionManager sessionManager = null;
    private TextInputLayout teacherNameLayout = null, tUsernameLayout = null, tPassWordLayout = null;
    private FloatingActionButton teacherManagementAssistButton = null;
    private int teacherId;
    private Teacher teacher = null;
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_management_coordinator);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Widget Initialization...
        teacherName = (EditText) findViewById(R.id.teacherName);
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);
        teacherAdder = (Button) findViewById(R.id.teacherAdder);
        teacherList = (ListView) findViewById(R.id.teacherList);
        deleteAllTeachersProgressBar = (ProgressBar) findViewById(R.id.deleteTeacherProgressBar);
        teacherNameLayout = (TextInputLayout) findViewById(R.id.teacherNameLayout);
        tUsernameLayout = (TextInputLayout) findViewById(R.id.tUsernameLayout);
        tPassWordLayout = (TextInputLayout) findViewById(R.id.tPassWordLayout);
        teacherManagementAssistButton = (FloatingActionButton) findViewById(R.id.teacherManagementAssistButton);

        examDBHandler = new ExamDBHandler(this, null, null, 1);
        sessionManager = new SessionManager(getApplicationContext());

        //Teacher ID...
        teacherId = sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, -1);
        teacher = examDBHandler.getTeacher(teacherId);

        //Defining user permissions for activity by Username...
        username = sessionManager.sharedPreferences.getString(sessionManager.KEY_USERNAME, "null");

        if(teacher.get_type() != 1){
            setTitle("Create Account");
            teacherList.setVisibility(View.GONE);
            teacherAdder.setText("Register");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            teacherName.setLayoutParams(params);
            userName.setLayoutParams(params);
            passWord.setLayoutParams(params);
            teacherAdder.setLayoutParams(params);
        }else{
            teacherList.setAdapter(getTeacherAdapter());

            getSupportActionBar().setTitle("Admin Dashboard");

            //Registering Teacher list for context menu...
            registerForContextMenu(teacherList);
        }


        //Button Event for adding a teacher to System...
        teacherAdder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tName, uName, pWord = null;
                tName = teacherName.getEditableText().toString();
                uName = userName.getEditableText().toString();
                pWord = passWord.getEditableText().toString();

                if(inputValidator(tName, uName, pWord))
                    add(tName, uName, pWord);
                //else
                    //Toast.makeText(TeacherManagement.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();

            }
        });

        teacherManagementAssistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getCurrentFocus(), "If you are an existing user, tap \"Login\"", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(getCurrentFocus(), "Otherwise, create a new account by tapping \"Register\"", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                return;
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });
    }

    public ListAdapter getTeacherAdapter(){
        Vector<Bundle> teacherList = examDBHandler.readTeacher();
        ListAdapter teacherAdapter = new CustomTeacherAdapter(this, teacherList);

        return teacherAdapter;
    }

    @Override
    protected void onDestroy() {
        sessionManager.logoutUser();
        super.onDestroy();
    }

    public void add(String _tName, String _uName, String _pWord){

        teacherNameLayout.setError(null);
        tUsernameLayout.setError(null);
        tPassWordLayout.setError(null);
        Teacher teacher = new Teacher(_tName, _uName, _pWord, true, 0);
        if(examDBHandler.addTeacher(teacher)){
            teacherAddedSuccessfullyToast().show();
            teacherName.setText("");
            userName.setText("");
            passWord.setText("");
        }else{
            tUsernameLayout.setError("Username already taken");
        }

        if(username.matches("admin")){
            teacherList.setAdapter(getTeacherAdapter());
        }
    }

    public boolean inputValidator(String _tName, String _uName, String _pWord){

        teacherNameLayout.setError(null);
        tUsernameLayout.setError(null);
        tPassWordLayout.setError(null);
        if(_tName.isEmpty()){
            teacherNameLayout.setError("This field is required");
            return false;
        }

        if(_tName.length() > 18){
            teacherNameLayout.setError("Name too long\nMaximum Character set is 18");
            return false;
        }

        if(_uName.isEmpty()){
            tUsernameLayout.setError("This field is required");
            return false;
        }

        if(_pWord.isEmpty()){
            tPassWordLayout.setError("This field is required");
            return false;
        }

        if(_pWord.length() < 6 || _pWord.length() > 32){
            tPassWordLayout.setError("Password must be between 6 and 32 characters in length");
            return false;
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_only_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int selectedItem = info.position;
        Bundle bundle = (Bundle) teacherList.getItemAtPosition(selectedItem);
        final int teacherId = bundle.getInt("Teacher Id");

        switch (item.getItemId()){

            case R.id.deleteOnly:{

                if(teacherId>0){
                    Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_LONG)
                            .setAction("Yes", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final Handler h = new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            deleteAllTeachersProgressBar.setVisibility(View.GONE);
                                            teacherList.setAdapter(getTeacherAdapter());
                                            teacherDeletedSuccessfullyToast().show();
                                        }
                                    };
                                    Runnable deleteRunnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            examDBHandler.removeTeacher(teacherId);
                                            h.sendEmptyMessage(0);
                                        }
                                    };

                                    deleteAllTeachersProgressBar.setVisibility(View.VISIBLE);
                                    Thread deleteThread = new Thread(deleteRunnable);
                                    deleteThread.start();
                                }
                            }).show();
                }else{
                    Snackbar.make(getCurrentFocus(), "Teacher list is empty", Snackbar.LENGTH_LONG)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    return;
                                }
                            }).show();
                }
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                sessionManager.logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Toast teacherAddedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Teacher Added Successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_added, 0, 0, 0);
        }

        return toast;
    }

    public Toast teacherDeletedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Teacher Deleted Successfully", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_deleted, 0, 0, 0);
        }

        return toast;
    }


}
