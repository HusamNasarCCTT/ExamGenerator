package cctt.grad.examgenerator.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.R;


public class LoginActivity extends AppCompatActivity {

    private ExamDBHandler examDBHandler = null;
    private EditText loginUserName, loginPassWord = null;
    private Button loginButton, registerButton = null;
    SessionManager sessionManager = null;


    @Override
    protected void onCreate(Bundle bnd) {
        super.onCreate(bnd);
        setContentView(R.layout.login_screen);

        getSupportActionBar().hide();

        //Widget Initialization...
        loginUserName = (EditText) findViewById(R.id.loginUserName);
        loginPassWord = (EditText) findViewById(R.id.loginPassWord);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

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
    }

    public void attemptLogin(View v){

        //Get User inputs...
        String userName, passWord;
        int teacherId;
        userName = loginUserName.getEditableText().toString();
        passWord = loginPassWord.getEditableText().toString();

        if(loginInputValidator(userName, passWord)){
            if(examDBHandler.userLogin(userName, passWord)){
                teacherId = examDBHandler.getUserId(userName, passWord);
                sessionManager.createLoginSession(teacherId, userName, passWord);
                if(userName.equals("admin")){
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

        loginUserName.setError(null);
        loginPassWord.setError(null);


        if(userName.isEmpty()){
            loginUserName.setError("This field is required");
            return false;
        }
        if(passWord.isEmpty()){
            loginPassWord.setError("This field is also required");
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
