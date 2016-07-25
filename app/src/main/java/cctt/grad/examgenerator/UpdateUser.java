package cctt.grad.examgenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateUser extends AppCompatActivity {

    private EditText usernameEdit = null,
                     passwordEdit = null;
    private Button   updateUserEdit = null;
    private Bundle userDetails = null;
    private ExamDBHandler examDBHandler = null;
    private SessionManager sessionManager = null;
    private int teacherID = -1;
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

        usernameEdit.setHint(oldUsername);
        passwordEdit.setHint(oldPassword);
        Toast.makeText(UpdateUser.this, "After changing credentials you will be logged out++", Toast.LENGTH_SHORT).show();

        updateUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputValidator(usernameEdit.getText().toString(), passwordEdit.getText().toString())){
                    Toast.makeText(UpdateUser.this, "Please enter at least one parameter", Toast.LENGTH_SHORT).show();
                }else{
                    update();
                    Toast.makeText(UpdateUser.this, "User Updated successfully", Toast.LENGTH_SHORT).show();
                    sessionManager.logoutUser();
                }
            }
        });

    }

    public boolean inputValidator(String _userName, String _password){

        if(_userName.isEmpty() && _password.isEmpty())
            return true;
        else
            return false;
    }

    public void update(){

        String newUserName, newPassword;

        if(usernameEdit.getText().toString().isEmpty())
            newUserName = oldUsername;
        else
            newUserName = usernameEdit.getText().toString();

        if(passwordEdit.getText().toString().isEmpty())
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
                Intent intent = new Intent(this, CourseManagement.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
