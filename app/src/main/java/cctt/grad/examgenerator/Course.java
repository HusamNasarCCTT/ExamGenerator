package cctt.grad.examgenerator;

/**
 * Created by Hussam Nasar on 02/05/2016.
 */
public class Course {

    private int _id;
    private String _name;
    private int _courseTeacher;
    private int _courseClass;

    public Course(){

    }

    public Course(String name, int courseTeacher, int courseClass){

        this._name = name;
        this._courseTeacher = courseTeacher;
        this._courseClass = courseClass;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_courseTeacher() {
        return _courseTeacher;
    }

    public void set_courseTeacher(int _courseTeacher) {
        this._courseTeacher = _courseTeacher;
    }

    public int get_courseClass() {
        return _courseClass;
    }

    public void set_courseClass(int _courseClass) {
        this._courseClass = _courseClass;
    }
}
