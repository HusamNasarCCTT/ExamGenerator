package cctt.grad.examgenerator.View;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;

import cctt.grad.examgenerator.R;

/**
 * Created by Hussam Nasar on 23/05/2016.
 */
public class CustomCourseAdapter extends ArrayAdapter<Bundle> {

    public CustomCourseAdapter(Context context, Vector<Bundle> courseList) {
        super(context, R.layout.custom_course_list, courseList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customRow = inflater.inflate(R.layout.custom_course_list, parent, false);

        //Getting references to Row Elements
        ImageView courseIcon = (ImageView) customRow.findViewById(R.id.courseIcon);
        TextView courseName = (TextView) customRow.findViewById(R.id.customCourseName);
        TextView courseYear = (TextView) customRow.findViewById(R.id.customCourseYear);
        TextView courseTerm = (TextView) customRow.findViewById(R.id.customCourseTerm);

        //Setting data to String variables for Display on TextViews...

        int singleCourseId = getItem(position).getInt("Course ID");
        String singleCourseName = getItem(position).getString("Course Name");
        int singleCourseYear = getItem(position).getInt("Course Year");
        int singleCourseTerm = getItem(position).getInt("Course Term");

        courseIcon.setImageResource(R.drawable.course_icon);
        courseName.setText(singleCourseName);
        if(singleCourseId > 0){
            courseYear.setText(String.valueOf(singleCourseYear));
            courseTerm.setText(getTermByInt(singleCourseTerm));
        }


        return customRow;
    }


    public String getTermByInt(int _term){

        if(_term == 1){
            return "Spring";
        }else{
            return "Fall";
        }
    }
}



