package cctt.grad.examgenerator;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class TeacherManagement extends AppCompatActivity {

    private EditText teacherName = null;
    private EditText userName = null;
    private EditText passWord = null;
    private Button teacherAdder = null;
    private ListView teacherList = null;
    private ExamDBHandler examDBHandler = null;
    private SessionManager sessionManager = null;
    private int teacherId;
    private String username = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_management);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Widget Initialization...
        teacherName = (EditText) findViewById(R.id.teacherName);
        userName = (EditText) findViewById(R.id.userName);
        passWord = (EditText) findViewById(R.id.passWord);
        teacherAdder = (Button) findViewById(R.id.teacherAdder);
        teacherList = (ListView) findViewById(R.id.teacherList);

        examDBHandler = new ExamDBHandler(this, null, null, 1);
        sessionManager = new SessionManager(getApplicationContext());

        //Teacher ID...
        teacherId = sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, -1);

        //Defining user permissions for activity by Username...
        username = sessionManager.sharedPreferences.getString(sessionManager.KEY_USERNAME, "null");

        if(! username.matches("admin")){
            teacherList.setVisibility(View.GONE);
            teacherAdder.setText("Register");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            teacherName.setLayoutParams(params);
            userName.setLayoutParams(params);
            passWord.setLayoutParams(params);
            teacherAdder.setLayoutParams(params);
        }else{
            teacherList.setAdapter(getTeacherAdapter());

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
    }

    public ListAdapter getTeacherAdapter(){
        Vector<Bundle> teacherList = examDBHandler.readteacher();
        ListAdapter teacherAdapter = new CustomTeacherAdapter(this, teacherList);

        return teacherAdapter;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_delete_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int selectedItem = info.position;
        Bundle bundle = (Bundle) teacherList.getItemAtPosition(selectedItem);
        final int teacherId = bundle.getInt("Teacher Id");

        switch (item.getItemId()){

            case R.id.context_edit:{

                break;
            }

            case R.id.context_delete:{

                Snackbar.make(getCurrentFocus(), "Are you sure?", Snackbar.LENGTH_LONG)
                        .setAction("Yes", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                examDBHandler.removeTeacher(teacherId);
                                teacherList.setAdapter(getTeacherAdapter());
                            }
                        }).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        sessionManager.logoutUser();
        super.onDestroy();
    }

    public void add(String _tName, String _uName, String _pWord){

        Teacher teacher = new Teacher(_tName, _uName, _pWord, true);
        examDBHandler.addTeacher(teacher);
        Toast.makeText(TeacherManagement.this, "Teacher Name: " + teacher.get_name() + "\n"
                        +  "Username: " + teacher.get_username() + "\n"
                        +  "Password: " + teacher.get_password() + "\nAdded"
                , Toast.LENGTH_SHORT).show();
        if(username.matches("admin")){
            teacherList.setAdapter(getTeacherAdapter());
        }
    }

    public boolean inputValidator(String _tName, String _uName, String _pWord){

        if(_tName.isEmpty()){
            teacherName.setError("This field is required");
            return false;
        }

        if(_uName.isEmpty()){
            userName.setError("This field is required");
            return false;
        }

        if(_pWord.isEmpty()){
            passWord.setError("This field is required");
            return false;
        }

        return true;
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
}
