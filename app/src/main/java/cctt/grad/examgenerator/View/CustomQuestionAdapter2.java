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
 * Created by Hussam Nasar on 24/05/2016.
 */
public class CustomQuestionAdapter2 extends ArrayAdapter<Bundle> {

    public CustomQuestionAdapter2(Context context, Vector<Bundle> questionList) {
        super(context, R.layout.custom_question_list2, questionList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View customRow = inflater.inflate(R.layout.custom_question_list2, parent, false);

        //Getting references to row elements...
        ImageView questionIcon = (ImageView) customRow.findViewById(R.id.questionIcon2);
        TextView questionText = (TextView) customRow.findViewById(R.id.customQuestionText2);
        TextView questionMcqOrEssay = (TextView) customRow.findViewById(R.id.customMcqOrEssay2);
        TextView questionPracOrTheory = (TextView) customRow.findViewById(R.id.customPracOrTheory2);

        //Acquiring data for row elements...
        String _questionText = getItem(position).getString("Question Text");
        String _mcqOrEssay; int _mcqOrEssayDefiner = getItem(position).getInt("Mcq or Essay");
        if(_mcqOrEssayDefiner == 0){
            _mcqOrEssay = "MCQ";
            //questionIcon.setImageResource(R.drawable.question_list);
        }
        else{
            _mcqOrEssay = "Essay";
            //questionIcon.setImageResource(R.drawable.question_mark_sign);
        }
        String _pracOrTheory; int _pracOrTheoryDefiner = getItem(position).getInt("Practical or Theory");
        if(_pracOrTheoryDefiner == 0){
            _pracOrTheory = "Practical";
        }
        else{
            _pracOrTheory = "Theory";
        }


        //Setting data for display in row elements...
        questionIcon.setImageResource(R.drawable.question_iconnn);
        if(_questionText.length() > 150)
            questionText.setText(_questionText.substring(0, 150) + "...");
        else
            questionText.setText(_questionText);
        questionMcqOrEssay.setText(_mcqOrEssay);
        questionPracOrTheory.setText(_pracOrTheory);

        //Question Id...
        int questionId = getItem(position).getInt("Question Id");
        if(questionId == 0){
            questionMcqOrEssay.setVisibility(View.GONE);
            questionPracOrTheory.setVisibility(View.GONE);
        }

        return customRow;
    }
}
