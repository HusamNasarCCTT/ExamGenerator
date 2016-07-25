package cctt.grad.examgenerator;

/**
 * Created by Hussam Nasar on 02/05/2016.
 */
public class Question {

    private int _id;
    private String _text;
    private int _pracOrTheory;
    private int _mcqOrRegular;
    private int _difficulty;
    private float _time;
    private int _course;

    //Just in case...
    public Question() {

    }

    //To use when Inserting a new Question into Database...
    public Question(String _text, int _pracOrTheory, int _mcqOrRegular, int _difficulty, /*float _time,*/ int _course) {
        this._text = _text;
        this._pracOrTheory = _pracOrTheory;
        this._mcqOrRegular = _mcqOrRegular;
        this._difficulty = _difficulty;
        //this._time = _time;
        this._course = _course;
    }

    //To use when generating exam...
    public Question(int _id, String _text, int _pracOrTheory, int _mcqOrRegular, int _difficulty, float _time) {
        this._id = _id;
        this._text = _text;
        this._pracOrTheory = _pracOrTheory;
        this._mcqOrRegular = _mcqOrRegular;
        this._difficulty = _difficulty;
        this._time = _time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {

        this._id = _id;
    }

    public String get_text() {

        return _text;
    }

    public void set_text(String _text) {

        this._text = _text;
    }

    public void set_pracOrTheory(int _pracOrTheory) {

        this._pracOrTheory = _pracOrTheory;
    }

    public int get_pracOrTheory() {
        return _pracOrTheory;
    }

    public int get_mcqOrRegular() {
        return _mcqOrRegular;
    }

    public void set_mcqOrRegular(int _mcqOrRegular) {
        this._mcqOrRegular = _mcqOrRegular;
    }

    public int get_difficulty() {

        return _difficulty;
    }

    public void set_difficulty(int _difficulty) {

        this._difficulty = _difficulty;
    }

    public float get_time() {

        return _time;
    }

    public void set_time(float _time) {

        this._time = _time;
    }

    public int get_course() {

        return _course;
    }

    public void set_course(int _course) {

        this._course = _course;
    }
}
