package cctt.grad.examgenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Hussam Nasar on 19/04/2016.
 */
public class MainActivity extends Activity {

    private Button teacherManagement = null;
    private Button courseManagement = null;
    private Intent intent = null;
    private SessionManager sessionManager = null;

    protected void onCreate(Bundle bnd){

        super.onCreate(bnd);
        setContentView(R.layout.main_layout);
        //Option Initialization...
        teacherManagement = (Button) findViewById(R.id.teacherManagement);
        courseManagement = (Button) findViewById(R.id.courseManagement);

        sessionManager = new SessionManager(getApplicationContext());
        String userName = sessionManager.sharedPreferences.getString(sessionManager.KEY_USERNAME, "null");
        if(userName.equals("admin")){
            intent = new Intent(MainActivity.this, TeacherManagement.class);
            intent.putExtra("ActivityName", "MainActivity");
            startActivity(intent);
        }


        teacherManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, TeacherManagement.class);
                intent.putExtra("ActivityName", "MainActivity");
                startActivity(intent);
            }
        });

        courseManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CourseManagement.class));
            }
        });

    }

}
