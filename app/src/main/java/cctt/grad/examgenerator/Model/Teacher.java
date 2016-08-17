package cctt.grad.examgenerator.Model;

/**
 * Created by Hussam Nasar on 01/05/2016.
 */
public class Teacher {

    private int _id;
    private String _name;
    private String _username;
    private String _password;
    private boolean _state;
    private int _type;

    public Teacher(){

    }

    public Teacher(String name, String username, String password, boolean state, int type) {
        this._name = name;
        this._username = username;
        this._password = password;
        this._state = state;
        this._type = type;
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

    public String get_username() {
        return _username;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public boolean is_state() {
        return _state;
    }

    public void set_state(boolean _state) {
        this._state = _state;
    }

    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }
}
