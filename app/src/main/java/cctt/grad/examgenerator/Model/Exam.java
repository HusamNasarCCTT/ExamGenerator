package cctt.grad.examgenerator.Model;

import java.util.Vector;

/**
 * Created by Hussam Nasar on 14/07/2016.
 */
public class Exam {

    private int _noOfQuestions,
                _visualDifficulty,
                _mcqOrEssay,
                _theoryOrPractical,
                _teacher,
                _course;

    private float _time, _maxTime;

    private double _exactDiffculty;

    private Vector<Question> _questionList = null;

    public Exam(){

    }

    public Exam(int _noOfQuestions, int _visualDifficulty, double _exactDiffculty, int _mcqOrEssay, int _theoryOrPractical, int _teacher, int _course, float _time, float _maxTime, Vector<Question> _questionList) {
        this._noOfQuestions = _noOfQuestions;
        this._visualDifficulty = _visualDifficulty;
        this._exactDiffculty = _exactDiffculty;
        this._mcqOrEssay = _mcqOrEssay;
        this._theoryOrPractical = _theoryOrPractical;
        this._teacher = _teacher;
        this._course = _course;
        this._time = _time;
        this._maxTime = _maxTime;
        this._questionList = _questionList;
    }

    public Exam(int _noOfQuestions, int _visualDifficulty, double _exactDiffculty, int _mcqOrEssay, int _theoryOrPractical, int _teacher, int _course, float _time, float _maxTime) {
        this._noOfQuestions = _noOfQuestions;
        this._visualDifficulty = _visualDifficulty;
        this._exactDiffculty = _exactDiffculty;
        this._mcqOrEssay = _mcqOrEssay;
        this._theoryOrPractical = _theoryOrPractical;
        this._teacher = _teacher;
        this._course = _course;
        this._time = _time;
        this._maxTime = _maxTime;
    }

    public int get_noOfQuestions() {
        return _noOfQuestions;
    }

    public void set_noOfQuestions(int _noOfQuestions) {
        this._noOfQuestions = _noOfQuestions;
    }

    public int get_visualDifficulty() {
        return _visualDifficulty;
    }

    public void set_visualDifficulty(int _visualDifficulty) {
        this._visualDifficulty = _visualDifficulty;
    }

    public double get_exactDiffculty() {
        return _exactDiffculty;
    }

    public void set_exactDiffculty(double _exactDiffculty) {
        this._exactDiffculty = _exactDiffculty;
    }

    public int get_mcqOrEssay() {
        return _mcqOrEssay;
    }

    public void set_mcqOrEssay(int _mcqOrEssay) {
        this._mcqOrEssay = _mcqOrEssay;
    }

    public int get_theoryOrPractical() {
        return _theoryOrPractical;
    }

    public void set_theoryOrPractical(int _theoryOrPractical) {
        this._theoryOrPractical = _theoryOrPractical;
    }

    public int get_teacher() {
        return _teacher;
    }

    public void set_teacher(int _teacher) {
        this._teacher = _teacher;
    }

    public int get_course() {
        return _course;
    }

    public void set_course(int _course) {
        this._course = _course;
    }

    public float get_time() {
        return _time;
    }

    public void set_time(float _time) {
        this._time = _time;
    }

    public float get_maxTime() {
        return _maxTime;
    }

    public void set_maxTime(float _maxTime) {
        this._maxTime = _maxTime;
    }

    public Vector<Question> get_questionList() {
        return _questionList;
    }

    public void set_questionList(Vector<Question> _questionList) {
        this._questionList = _questionList;
    }


    public Vector<Question> getMCQs(){

        Vector<Question> mcqList = new Vector<>();

        switch (this.get_mcqOrEssay()){

            case 0:
                return this.get_questionList();

            case 2:
                for (Question question : this.get_questionList()){

                    if(question.get_mcqOrRegular() == 0)
                        mcqList.add(question);

                }
                return mcqList;

            default: return mcqList;
        }

    }

    public Vector<Question> getEssays(){

        Vector<Question> essayList = new Vector<>();

        switch (this.get_mcqOrEssay()){

            case 1:
                return this._questionList;
            case 2:
                for (Question question : this.get_questionList()){

                    if(question.get_mcqOrRegular() == 1)
                        essayList.add(question);

                }
                return essayList;

            default: return essayList;
        }

    }

    public int getTheoryCount(){

        int numOfTheories = 0;
        for (Question question : this._questionList){

            if(question.get_pracOrTheory() == 0){
                numOfTheories++;
            }

        }
        return numOfTheories;
    }

    public int getPracticalCount(){

        int numOfPracticals = 0;
        for (Question question : this._questionList){

            if(question.get_pracOrTheory() == 1){
                numOfPracticals++;
            }
        }
        return numOfPracticals;
    }
}
