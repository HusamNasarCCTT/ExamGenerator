package cctt.grad.examgenerator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by Hussam Nasar on 26/05/2016.
 */
public class CustomTeacherAdapter extends ArrayAdapter<Bundle> {

    public CustomTeacherAdapter(Context context, Vector<Bundle> teacherList) {
        super(context, R.layout.custom_teacher_list, teacherList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customRow = inflater.inflate(R.layout.custom_teacher_list, parent, false);

        //Getting references to row elements...
        ImageView teacherIcon = (ImageView) customRow.findViewById(R.id.teacherIcon);
        TextView teacherName = (TextView) customRow.findViewById(R.id.displayTeacherName);
        TextView userName = (TextView) customRow.findViewById(R.id.displayUserName);
        TextView passWord = (TextView) customRow.findViewById(R.id.displayPassword);
        TextView state = (TextView) customRow.findViewById(R.id.displayState);

        //Acquiring data for row...
        int teacherId = getItem(position).getInt("Teacher Id");
        String _teacherName = getItem(position).getString("Teacher Name");
        String _userName = getItem(position).getString("Username");
        String _passWord = getItem(position).getString("Password");
        String _teacherState;
        if(getItem(position).getInt("State") == 1){
            _teacherState = "Active";
        }else{
            _teacherState = "Inactive";
        }

        //Setting data for display on row...
        teacherIcon.setImageResource(R.drawable.user_iconn);
        teacherName.setText(_teacherName);
        userName.setText(_userName);
        passWord.setText(_passWord);
        state.setText(_teacherState);

        return customRow;
    }
}
