package cctt.grad.examgenerator.Model;

/**
 * Created by Hussam Nasar on 28/05/2016.
 */
public class Choice {

    private int _id;
    private String _text;
    private int _questionId;

    public Choice(){

    }

    public Choice(String _text, int _questionId) {
        this._text = _text;
        this._questionId = _questionId;
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

    public int get_questionId() {
        return _questionId;
    }

    public void set_questionId(int _questionId) {
        this._questionId = _questionId;
    }
}
