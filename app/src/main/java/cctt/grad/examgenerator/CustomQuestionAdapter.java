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
 * Created by Hussam Nasar on 24/05/2016.
 */
public class CustomQuestionAdapter extends ArrayAdapter<Bundle> {

    public CustomQuestionAdapter(Context context, Vector<Bundle> questionList) {
        super(context, R.layout.custom_question_list, questionList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customRow = inflater.inflate(R.layout.custom_question_list, parent, false);

        //Getting references to row elements...
        ImageView questionIcon = (ImageView) customRow.findViewById(R.id.questionIcon);
        TextView questionText = (TextView) customRow.findViewById(R.id.customQuestionText);
        //TextView questionMcqOrEssay = (TextView) customRow.findViewById(R.id.customMcqOrTheory);
        //TextView questionPracOrTheory = (TextView) customRow.findViewById(R.id.customPracOrTheory);

        //Acquiring data for row elements...
        String _questionText = getItem(position).getString("Question Text");
        /*String _mcqOrEssay; int _mcqOrEssayDefiner = getItem(position).getInt("Mcq or Essay");
        if(_mcqOrEssayDefiner == 0)
            _mcqOrEssay = "MCQ";
        else
            _mcqOrEssay = "Essay";
        String _pracOrTheory; int _pracOrTheoryDefiner = getItem(position).getInt("Practical or Theory");
        if(_pracOrTheoryDefiner == 0)
            _pracOrTheory = "Practical";
        else
            _pracOrTheory = "Theory";*/


        //Setting data for display in row elements...
        questionIcon.setImageResource(R.drawable.question_icon);
        if(_questionText.length() > 35)
            questionText.setText(_questionText.substring(0, 34) + "...");
        else
            questionText.setText(_questionText);
        //questionMcqOrEssay.setText(_mcqOrEssay);
        //questionPracOrTheory.setText(_pracOrTheory);

        return customRow;
    }
}
