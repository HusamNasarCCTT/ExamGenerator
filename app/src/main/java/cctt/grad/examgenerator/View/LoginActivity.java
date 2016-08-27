package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cctt.grad.examgenerator.Model.Teacher;
import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.R;


public class LoginActivity extends AppCompatActivity {

    private ExamDBHandler examDBHandler = null;
    private EditText loginUserName, loginPassWord = null;
    private ImageView examGeneratorLogo = null;
    private Button loginButton, registerButton = null;
    private TextInputLayout userNameLayout = null, passWordLayout = null;
    private FloatingActionButton loginAssistor = null;
    private int currentThemeInt = 0;
    SessionManager sessionManager = null;

    private Teacher teacher = null;

    @Override
    protected void onCreate(Bundle bnd) {
        super.onCreate(bnd);
        setContentView(R.layout.login_screen_coordinator);

        //To hide System bar and Action bar...
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        //Widget Initialization...
        loginUserName = (EditText) findViewById(R.id.loginUserName);
        loginPassWord = (EditText) findViewById(R.id.loginPassWord);
        loginButton = (Button) findViewById(R.id.loginButton);
        examGeneratorLogo = (ImageView) findViewById(R.id.examGeneratorLogo);
        registerButton = (Button) findViewById(R.id.registerButton);
        userNameLayout = (TextInputLayout) findViewById(R.id.userNameLayout);
        passWordLayout = (TextInputLayout) findViewById(R.id.passWordLayout);
        loginAssistor = (FloatingActionButton) findViewById(R.id.loginScreenAssistButton);


        //DB Handler Initialization...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Session Manager Initialization...

        sessionManager = new SessionManager(getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptLogin(v);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, TeacherManagement.class);
                startActivity(intent);
            }
        });

        loginAssistor.setOnClickListener(new View.OnClickListener() {
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

    public void attemptLogin(View v){

        //Get User inputs...
        String userName, passWord;
        int teacherId, teacherType;
        userName = loginUserName.getEditableText().toString();
        passWord = loginPassWord.getEditableText().toString();

        if(loginInputValidator(userName, passWord)){
            if(examDBHandler.userLogin(userName, passWord)){
                teacherId = examDBHandler.getUserId(userName, passWord);
                teacherType = examDBHandler.getTeacher(teacherId).get_type();
                sessionManager.createLoginSession(teacherId, userName, passWord);
                if(teacherType == 1){
                    startActivity(new Intent(LoginActivity.this, TeacherManagement.class));
                    adminLoginSuccessfulToast().show();
                }else{
                    startActivity(new Intent(LoginActivity.this, CourseManagement.class));
                    finish();
                }
            } else
                //Snackbar.make(v, "Invalid user credentials", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(LoginActivity.this, "Invalid user credentials", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean loginInputValidator(String userName, String passWord){

        userNameLayout.setError(null);
        passWordLayout.setError(null);
        loginUserName.setError(null);
        loginPassWord.setError(null);


        if(userName.isEmpty()){
            userNameLayout.setError("This field is required");
            return false;
        }
        if(passWord.isEmpty()){
            passWordLayout.setError("This field is also required");
            return false;
        }

        return true;
    }

    public Toast adminLoginSuccessfulToast(){
        Toast toast = Toast.makeText(LoginActivity.this, "Admin login successful.\n" +
                "You have Administrative permissions.", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_admin, 0, 0, 0);
        }

        return toast;
    }

}
