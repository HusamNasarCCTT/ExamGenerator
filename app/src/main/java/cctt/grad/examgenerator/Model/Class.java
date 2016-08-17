package cctt.grad.examgenerator.Model;

/**
 * Created by Hussam Nasar on 02/05/2016.
 */
public class Class {

    private int _id;
    private int _year;
    private int _term;

    public Class(){

    }

    public Class(int year, int term){
        this._year = year;
        this._term = term;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_year() {
        return _year;
    }

    public void set_year(int _year) {
        this._year = _year;
    }

    public int get_term() {
        return _term;
    }

    public void set_term(int _term) {
        this._term = _term;
    }
}
