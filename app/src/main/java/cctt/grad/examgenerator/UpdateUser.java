package cctt.grad.examgenerator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateUser extends AppCompatActivity {

    private EditText usernameEdit = null,
                     passwordEdit = null,
                     newUsernameEdit = null,
                     newPasswordEdit = null;
    private Button   updateUserEdit = null;
    private Bundle userDetails = null;
    private ExamDBHandler examDBHandler = null;
    private SessionManager sessionManager = null;
    private int teacherID = -1;
    private int errorCounter = 0;
    private String oldTeacherName, oldUsername, oldPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user);

        setTitle("Update Account Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing Widgets...
        usernameEdit = (EditText) findViewById(R.id.usernameEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        newUsernameEdit = (EditText) findViewById(R.id.newUsernameEdit);
        newPasswordEdit = (EditText) findViewById(R.id.newPasswordEdit);
        updateUserEdit = (Button) findViewById(R.id.updateUserEdit);

        //Getting user details from previous activity intent...
        Intent fromCourseManagement = getIntent();
        userDetails = fromCourseManagement.getExtras();
        teacherID = userDetails.getInt("ID", -1);
        oldUsername = userDetails.getString("Username");
        oldPassword = userDetails.getString("Password");

        //Initializing DB Handler...
        examDBHandler = new ExamDBHandler(this, null, null, 1);

        //Initializing Session Manager...
        sessionManager = new SessionManager(getApplicationContext());

        Toast.makeText(UpdateUser.this, "After changing credentials you will be logged out++", Toast.LENGTH_SHORT).show();

        updateUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputValidator(usernameEdit.getText().toString(), passwordEdit.getText().toString(), newUsernameEdit.getText().toString(), newPasswordEdit.getText().toString())){
                    if(userDataValidator(usernameEdit.getText().toString(), passwordEdit.getText().toString())){

                        Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_LONG)
                                .setAction("Yes", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        update();
                                        teacherUpdatedSuccessfullyToast().show();
                                        sessionManager.logoutUser();
                                    }
                                }).show();

                    }
                }
            }
        });

    }

    public boolean inputValidator(String _userName, String _password, String _newUserName, String _newPassword){

        usernameEdit.setError(null);
        passwordEdit.setError(null);
        newUsernameEdit.setError(null);
        newPasswordEdit.setError(null);
        if(_userName.isEmpty()){
            usernameEdit.setError("Old username is required");
            return false;
        }
        if(_password.isEmpty()){
            passwordEdit.setError("Old password is required");
            return false;
        }
        if(_newUserName.isEmpty() && _newPassword.isEmpty()){
            newUsernameEdit.setError("At least one of these fields is required");
            newPasswordEdit.setError("At least one of these fields is required");
            return false;
        }
            return true;
    }

    public boolean userDataValidator(String _userName, String _password){

        usernameEdit.setError(null);
        String oldUsername = sessionManager.sharedPreferences.getString(sessionManager.KEY_USERNAME, "");
        String oldPassword = sessionManager.sharedPreferences.getString(sessionManager.KEY_PASSWORD, "");
        if(!oldUsername.matches(_userName) || !oldPassword.matches(_password)){
            usernameEdit.setError("Your parameters don't match our records, try again");
            errorCounter++;
            if(errorCounter == 3){
                Toast.makeText(UpdateUser.this, "You've entered wrong user parameters 3 times, you've been logged out" +
                        " as you might be a threat to the system", Toast.LENGTH_LONG).show();
                Toast.makeText(UpdateUser.this, "If this is just a mistake, then try logging in again", Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
            }
            return false;
        }

        return true;
    }

    public void update(){

        String newUserName, newPassword;

        if(newUsernameEdit.getText().toString().isEmpty())
            newUserName = oldUsername;
        else
            newUserName = usernameEdit.getText().toString();

        if(newPasswordEdit.getText().toString().isEmpty())
            newPassword = oldPassword;
        else
            newPassword = passwordEdit.getText().toString();

        Teacher teacher = new Teacher();
        teacher.set_id(teacherID);
        teacher.set_username(newUserName);
        teacher.set_password(newPassword);

        examDBHandler.updateTeacher(teacher);
    }

    public void updateLoginCredentials(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                // app icon in action bar clicked; go home
                /*
                Intent intent = new Intent(this, CourseManagement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public Toast teacherUpdatedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Account Details updated successfully\nYou've been logged out", Toast.LENGTH_LONG);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.user_edited, 0, 0, 0);
        }

        return toast;
    }
}
