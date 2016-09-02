package cctt.grad.examgenerator.Presenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.Random;
import java.util.Vector;

import cctt.grad.examgenerator.Model.Choice;
import cctt.grad.examgenerator.Model.Class;
import cctt.grad.examgenerator.Model.Course;
import cctt.grad.examgenerator.Model.Exam;
import cctt.grad.examgenerator.Model.Question;
import cctt.grad.examgenerator.Model.Teacher;

/**
 * Created by Hussam Nasar on 02/05/2016.
 */
public class ExamDBHandler extends SQLiteOpenHelper {

    //Database Version and Name...
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ExamGenerator.db";

    //Teacher Table and Column names...
    public final String TABLE_TEACHER = "TEACHER";
    public final String COLUMN_TEACHER_ID = "_id";
    public final String COLUMN_TEACHER_NAME = "name";
    public final String COLUMN_TEACHER_USERNAME = "username";
    public final String COLUMN_TEACHER_PASSWORD = "password";
    public final String COLUMN_TEACHER_STATE = "state";
    public final String COLUMN_TEACHER_TYPE = "type";

    //Course Table and Column names...
    public final String TABLE_COURSE = "COURSE";
    public final String COLUMN_COURSE_ID = "_id";
    public final String COLUMN_COURSE_NAME = "name";
    public final String COLUMN_COURSE_TEACHER = "teacher_id";
    public final String COLUMN_COURSE_CLASS = "class_id";

    //Question Table and Column names...
    public final String TABLE_QUESTION = "QUESTION";
    public final String COLUMN_QUESTION_ID = "_id";
    public final String COLUMN_QUESTION_TEXT = "question_text";
    public final String COLUMN_QUESTION_PRACTICAL_OR_THEORY = "practical_or_theory";
    public final String COLUMN_QUESTION_MCQ_OR_REGULAR = "mcq_or_regular";
    public final String COLUMN_QUESTION_DIFFICULTY = "difficulty";
    public final String COLUMN_QUESTION_TIME = "time";
    public final String COLUMN_QUESTION_COURSE = "course";

    //Attachment Table and Column names...
    /*public final String TABLE_ATTACHMENT = "ATTACHMENT";
    public final String COLUMN_ATTACHMENT_ID = "_id";
    public final String COLUMN_ATTACHMENT_QUESTION = "question_id";
    public final String COLUMN_ATTACHMENT_BODY = "attachment_location";*/

    //Class Table and Column names...
    public final String TABLE_CLASS = "CLASS";
    public final String COLUMN_CLASS_ID = "_id";
    public final String COLUMN_CLASS_YEAR = "year";
    public final String COLUMN_CLASS_TERM = "term";

    //Choice Table and Column names...
    public final String TABLE_CHOICE = "CHOICE";
    public final String COLUMN_CHOICE_ID = "_id";
    public final String COLUMN_CHOICE_TEXT = "choice_text";
    public final String COLUMN_CHOICE_QUESTION = "question_id";

    //Shared Preference for session details...
    public SessionManager sessionManager = null;

    //Keys for Courselist bundle...
    public final String KEY_COURSE_ID = "Course ID";
    public final String KEY_COURSE_NAME = "Course Name";
    public final String KEY_COURSE_YEAR = "Course Year";
    public final String KEY_COURSE_TERM = "Course Term";

    //Keys for Question List bundle...
    public final String KEY_QUESTION_ID = "Question Id";
    public final String KEY_QUESTION_TEXT = "Question Text";
    public final String KEY_QUESTION_MCQ_OR_ESSAY = "Mcq or Essay";
    public final String KEY_QUESTION_PRACTICAL_OR_THEORY = "Practical or Theory";
    public final String KEY_QUESTION_TIME = "Time";
    public final String KEY_QUESTION_DIFFICULTY = "Difficulty";

    //Keys for Teacher List bundle...
    public final String KEY_TEACHER_ID = "Teacher Id";
    public final String KEY_TEACHER_NAME = "Teacher Name";
    public final String KEY_TEACHER_USERNAME = "Username";
    public final String KEY_TEACHER_PASSWORD = "Password";
    public final String KEY_TEACHER_STATE = "State";
    public final String KEY_TEACHER_TYPE = "Type";

    //Keys for Choice List Bundle
    public final String KEY_CHOICE_ID = "Choice Id";
    public final String KEY_CHOICE_TEXT = "Choice Text";
    public final String KEY_CHOICE_QUESTION = "Choice Question";

    //MIN and MAX time values for various exam types...
    public final float MIN_EASY = 10;
    public final float MAX_EASY = 45;
    public final float MIN_MODERATE = 45;
    public final float MAX_MODERATE = 60;
    public final float MIN_DIFFICULT = 60;
    public final float MAX_DIFFICULT = 90;
    public final float MIN_EXTREME = 90;
    public final float MAX_EXTREME = 120;

    private final float MINIMUM_EXAM_TIME = 10;
    private final float MAXIMUM_EXAM_TIME = 120;

    public final float MIN_PER_DIFFPOINTS = 2;
    public final float THEORY_MCQ_DIFF_POINTS = 0.5f;
    public final float PRACTICAL_MCQ_DIFF_POINTS = 1;
    public final float THEORY_ESSAY_DIFF_POINTS = 2;
    public final float PRACTICAL_ESSAY_DIFF_POINTS = 3;


    public ExamDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        sessionManager = new SessionManager(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String teacherQuery = "CREATE TABLE " + TABLE_TEACHER
                + " ( " + COLUMN_TEACHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_TEACHER_NAME + " TEXT NOT NULL, "
                        + COLUMN_TEACHER_USERNAME + " TEXT UNIQUE NOT NULL, "
                        + COLUMN_TEACHER_PASSWORD + " TEXT NOT NULL, "
                        + COLUMN_TEACHER_STATE + " BOOLEAN NOT NULL, "
                        + COLUMN_TEACHER_TYPE + " INTEGER NOT NULL" + ");";

        String classQuery = "CREATE TABLE " + TABLE_CLASS
                + " ( " + COLUMN_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_CLASS_YEAR + " INTEGER NOT NULL, "
                        + COLUMN_CLASS_TERM + " BOOLEAN NOT NULL " + ");";

        String courseQuery = "CREATE TABLE "+ TABLE_COURSE
                + " ( " + COLUMN_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_COURSE_NAME + " TEXT NOT NULL, "
                        + COLUMN_COURSE_TEACHER + " INTEGER NOT NULL, "
                        + COLUMN_COURSE_CLASS + " INTEGER NOT NULL, "
                        + " FOREIGN KEY (" + COLUMN_COURSE_TEACHER + ") REFERENCES " + TABLE_TEACHER + "(" + COLUMN_TEACHER_ID + "), "
                        + " FOREIGN KEY (" + COLUMN_COURSE_CLASS + ") REFERENCES " + TABLE_CLASS + "(" + COLUMN_CLASS_ID + " ));";

        String questionQuery = "CREATE TABLE " + TABLE_QUESTION
                + " ( " + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_QUESTION_TEXT + " TEXT NOT NULL, "
                        + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " BOOLEAN NOT NULL, "
                        + COLUMN_QUESTION_MCQ_OR_REGULAR + " BOOLEAN NOT NULL, "
                        + COLUMN_QUESTION_DIFFICULTY + " TINYINT NOT NULL, "
                //The system design has been upgraded to calculate question time by 3 parameters:
                                                                    //1- Question Difficulty.
                                                                    //2- MCQ or Essay.
                                                                    //3- Practical or Theory.
                        //+ COLUMN_QUESTION_TIME + " TINYINT NOT NULL, "
                        + COLUMN_QUESTION_COURSE + " INTEGER NOT NULL, "
                        + "FOREIGN KEY (" + COLUMN_QUESTION_COURSE + ") REFERENCES " + TABLE_COURSE + "(" + COLUMN_COURSE_ID + " ));";

        String choiceQuery = "CREATE TABLE " + TABLE_CHOICE
                + " ( " + COLUMN_CHOICE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_CHOICE_TEXT + " TEXT NOT NULL, "
                        + COLUMN_CHOICE_QUESTION + " INTEGER NOT NULL, "
                        + "FOREIGN KEY (" + COLUMN_CHOICE_QUESTION + ") REFERENCES " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + " ));";
        /*
        String attachmentQuery = "CREATE TABLE " + TABLE_ATTACHMENT
                + " ( " + COLUMN_ATTACHMENT_ID + " INTEGER NOT NULL, "
                        + COLUMN_ATTACHMENT_QUESTION + " INTEGER NOT NULL, "
                        + COLUMN_ATTACHMENT_BODY + " TEXT NOT NULL, "
                        + "PRIMARY KEY (" + COLUMN_ATTACHMENT_ID + ", " + COLUMN_ATTACHMENT_QUESTION + "), "
                        + "FOREIGN KEY (" + COLUMN_ATTACHMENT_QUESTION + ") REFERENCES " + TABLE_QUESTION + "(" + COLUMN_QUESTION_ID + " );";
        */
        db.execSQL(teacherQuery);
        db.execSQL(classQuery);
        db.execSQL(courseQuery);
        db.execSQL(questionQuery);
        db.execSQL(choiceQuery);

        Teacher teacher = new Teacher();
        teacher.set_name("Administrator");
        teacher.set_username("admin");
        teacher.set_password("admin");
        teacher.set_state(true);
        teacher.set_type(1);

        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_TEACHER_NAME, teacher.get_name());
        adminValues.put(COLUMN_TEACHER_USERNAME, teacher.get_username());
        adminValues.put(COLUMN_TEACHER_PASSWORD, teacher.get_password());
        adminValues.put(COLUMN_TEACHER_STATE, 1);
        adminValues.put(COLUMN_TEACHER_TYPE, 1);

        db.insert(TABLE_TEACHER, null, adminValues);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Nothing to implement yet...
    }

    public boolean userLogin(String userName, String passWord){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TEACHER_ID + ", "
                                 + COLUMN_TEACHER_USERNAME + ", " + COLUMN_TEACHER_PASSWORD + " FROM " + TABLE_TEACHER
                                 + " WHERE (" + COLUMN_TEACHER_USERNAME + " =\"" + userName + "\""
                                 + " AND " + COLUMN_TEACHER_PASSWORD + " = \"" + passWord + "\");";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if(!cursor.isBeforeFirst() || !cursor.isAfterLast()){
            db.close();
            return true;
        }else{
            db.close();
            return false;
        }

    }

    public int getUserId(String userName, String passWord){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TEACHER_ID + " FROM " + TABLE_TEACHER
                        + " WHERE (" + COLUMN_TEACHER_USERNAME + " =\"" + userName + "\""
                         + " AND " + COLUMN_TEACHER_PASSWORD + " = \"" + passWord + "\");";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        int teacherId;
        teacherId = cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_ID));
        db.close();
        return teacherId;
    }

    public Teacher getTeacher(int teacherId){

        Teacher teacher = new Teacher();
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT " + "*" + " FROM " + TABLE_TEACHER + " WHERE ("
                     + COLUMN_TEACHER_ID + " = " + teacherId + ");";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
            teacher.set_id(teacherId);
            teacher.set_name(cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_NAME)));
            teacher.set_username(cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_USERNAME)));
            teacher.set_password(cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_PASSWORD)));
            teacher.set_type(cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_TYPE)));
        }
        cursor.close();
        db.close();
        return teacher;
    }

    public int getClassIdOrCreateClassId(Context _context, int courseYear, int classSemester){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + "*"/*COLUMN_CLASS_ID*/ + " FROM " + TABLE_CLASS
                       + " WHERE (" + COLUMN_CLASS_YEAR + "=\"" + courseYear + "\""
                       + " AND " + COLUMN_CLASS_TERM + " = \"" + classSemester + "\");";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || !cursor.isAfterLast()){
            db.close();
            return cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_ID));
        }else{
                ContentValues values = new ContentValues();
                values.put(COLUMN_CLASS_YEAR, courseYear);
                values.put(COLUMN_CLASS_TERM, classSemester);
                db.insert(TABLE_CLASS, null, values);
                cursor = db.rawQuery(query, null);
                cursor.moveToFirst();
                int returnValue;
                returnValue = cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_ID));
                db.close();
                return returnValue;
        }
    }

    public Class readClass(int classId){

        String query = "SELECT " + "*" + " FROM " + TABLE_CLASS + " WHERE (" + COLUMN_CLASS_ID + "=" + classId + ");";

        SQLiteDatabase db = getWritableDatabase();
        Class term = new Class();

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if(!cursor.isBeforeFirst() || !cursor.isAfterLast()){

            term.set_id(classId);
            term.set_term(cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_TERM)));
            term.set_year(cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_YEAR)));
            cursor.moveToNext();
        }

        return term;
    }

    public boolean addTeacher(Teacher teacher){

        String userValidationQuery = "SELECT " + COLUMN_TEACHER_USERNAME + " FROM " + TABLE_TEACHER
                                    + " WHERE (" + COLUMN_TEACHER_USERNAME + " = " + "\"" + teacher.get_username() + "\"" + ");";
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, teacher.get_name());
        values.put(COLUMN_TEACHER_USERNAME, teacher.get_username());
        values.put(COLUMN_TEACHER_PASSWORD, teacher.get_password());
        values.put(COLUMN_TEACHER_STATE, teacher.is_state());
        values.put(COLUMN_TEACHER_TYPE, teacher.get_type());
        SQLiteDatabase db = getWritableDatabase();

        //Username record validation...
        Cursor cursor = db.rawQuery(userValidationQuery, null);
        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
            cursor.close();
            db.close();
            return false;
        }

        db.insert(TABLE_TEACHER, null, values);
        db.close();
        return true;

    }

    public void updateTeacher(Teacher teacher){

        try{
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(COLUMN_TEACHER_USERNAME, teacher.get_username());
            values.put(COLUMN_TEACHER_PASSWORD, teacher.get_password());

            String whereClause = COLUMN_TEACHER_ID + "=" + teacher.get_id();
            db.update(TABLE_TEACHER, values, whereClause, null);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void removeTeacher(int teacherId){
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TEACHER + " WHERE (" + COLUMN_TEACHER_ID + " =" + teacherId + ");";
        String tempQuery = "SELECT " + COLUMN_COURSE_ID + " FROM " + TABLE_COURSE + " WHERE (" + COLUMN_COURSE_TEACHER + "=" + teacherId + ");";

        //Making sure to delete any courses associated with the teacher...
        Cursor cursor = db.rawQuery(tempQuery, null);
        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
            while (! cursor.isAfterLast()){
                int courseId = cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID));
                removeCourse(courseId);
                cursor.moveToNext();
            }
            //This initializes a new SQLiteDatabase object in case the old one is closed after finishing the removeCourse method...
            db = getWritableDatabase();
        }
        db.execSQL(query);
        db.close();
    }

    public void addCourse(Course course, Class _class){

        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_NAME, course.get_name());
        values.put(COLUMN_COURSE_TEACHER, course.get_courseTeacher());
        values.put(COLUMN_COURSE_CLASS, course.get_courseClass());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_COURSE, null, values);
        db.close();

    }

    public void removeCourse(int courseId){
        SQLiteDatabase db = getWritableDatabase();
        String tempQuery = "SELECT " + COLUMN_QUESTION_ID + ", " + COLUMN_QUESTION_COURSE + " FROM " + TABLE_QUESTION + " WHERE (" + "\""
                + COLUMN_QUESTION_COURSE + "=" + courseId + "\");";
        Cursor cursor = db.rawQuery(tempQuery, null);
        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
            while(! cursor.isAfterLast()){
                int questionId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID));
                removeQuestion(questionId);
                cursor.moveToNext();
            }
        }
        String query = "DELETE FROM " + TABLE_COURSE + " WHERE (" + COLUMN_COURSE_ID + "= \"" + courseId + "\");";
        db.execSQL(query);
        db.close();
    }

    public void deleteAllCourses(int teacherId){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_COURSE_ID + " FROM " + TABLE_COURSE + " WHERE (" + COLUMN_COURSE_TEACHER + " = " + teacherId + ");";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

            while(! cursor.isAfterLast()){
                deleteCourseQuestions(cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID)));
                cursor.moveToNext();
            }
            db = getWritableDatabase();
            String whereClause = COLUMN_COURSE_TEACHER + "=" + teacherId;
            db.delete(TABLE_COURSE, whereClause, null);
        }
    }

    public void updateCourse(Course course){
        SQLiteDatabase db = getWritableDatabase();
        //String query = "UPDATE " + TABLE_COURSE + " SET " + COLUMN_COURSE_NAME + " = " + "\"" + courseName + "\""
        //                         + " WHERE (" + COLUMN_COURSE_ID + " = " + courseId + ");";
        //Cursor cursor = db.rawQuery(query, null);
        ContentValues values = new ContentValues();
        values.put(COLUMN_COURSE_NAME, course.get_name());
        values.put(COLUMN_COURSE_CLASS, course.get_courseClass());
        String whereClause = COLUMN_COURSE_ID + "=" + course.get_id();
        db.update(TABLE_COURSE, values, whereClause, null);
        db.close();
    }

    public void addQuestion(Question question){

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_TEXT, question.get_text());
        values.put(COLUMN_QUESTION_MCQ_OR_REGULAR, question.get_mcqOrRegular());
        values.put(COLUMN_QUESTION_PRACTICAL_OR_THEORY, question.get_pracOrTheory());
        values.put(COLUMN_QUESTION_DIFFICULTY, question.get_difficulty());
        //values.put(COLUMN_QUESTION_TIME, question.get_time());
        values.put(COLUMN_QUESTION_COURSE, question.get_course());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_QUESTION, null, values);
        db.close();
    }

    public void addChoices(Vector<Choice> choices){
        //Inserting mcq1 Question choices...
        SQLiteDatabase db = getWritableDatabase();

        for (Choice choice : choices){
            ContentValues choiceValues = new ContentValues();
            choiceValues.put(COLUMN_CHOICE_TEXT, choice.get_text());
            choiceValues.put(COLUMN_CHOICE_QUESTION, choice.get_questionId());
            db.insert(TABLE_CHOICE, null, choiceValues);
        }
        db.close();
    }

    public void updateQuestion(Question question, Vector<Choice> choices){

        if(question.get_mcqOrRegular() == 0){
            updateChoices(choices, question.get_id());
        }
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUESTION_TEXT, question.get_text());
            values.put(COLUMN_QUESTION_MCQ_OR_REGULAR, question.get_mcqOrRegular());
            values.put(COLUMN_QUESTION_PRACTICAL_OR_THEORY, question.get_pracOrTheory());
            values.put(COLUMN_QUESTION_DIFFICULTY, question.get_difficulty());

            String whereClause = COLUMN_QUESTION_ID + "=" + question.get_id();
            db.update(TABLE_QUESTION, values, whereClause, null);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void updateChoices(Vector<Choice> choices, int questionId){

        removeChoices(questionId);
        SQLiteDatabase db = getWritableDatabase();
        for (Choice choiceInstance : choices){

            ContentValues values = new ContentValues();
            values.put(COLUMN_CHOICE_TEXT, choiceInstance.get_text());
            values.put(COLUMN_CHOICE_QUESTION, questionId);

            db.insert(TABLE_CHOICE, null, values);
        }
        db.close();
    }

    public void removeChoices(int questionId){

        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CHOICE + " WHERE (" + COLUMN_CHOICE_QUESTION + "=\"" + questionId + "\");";
        db.execSQL(query);
        db.close();
    }

    public void removeQuestion(int questionId){

            try{
                SQLiteDatabase db = getWritableDatabase();
                String tempQuery = "SELECT " + COLUMN_QUESTION_MCQ_OR_REGULAR + " FROM " + TABLE_QUESTION + " WHERE ("
                        + COLUMN_QUESTION_ID + "=" + questionId + ");";
                Cursor cursor = db.rawQuery(tempQuery, null);
                cursor.moveToFirst();
                if(! cursor.isBeforeFirst() || ! cursor.isAfterLast())
                    removeChoices(questionId);
                db = getWritableDatabase();
                String query = "DELETE FROM " + TABLE_QUESTION + " WHERE (" + COLUMN_QUESTION_ID + "=" + questionId +");";
                db.execSQL(query);
                db.close();
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    public void deleteCourseQuestions(int courseId){
        try{
            SQLiteDatabase db = getWritableDatabase();
            String questionRetriever = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE (" + COLUMN_QUESTION_COURSE + " = " + courseId + ");";
            Cursor cursor = db.rawQuery(questionRetriever, null);
            cursor.moveToFirst();

            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

                while(! cursor.isAfterLast()){
                    int questionId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID));
                    int mcqOrEssay = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_MCQ_OR_REGULAR));
                    if(mcqOrEssay == 0){
                        removeChoices(questionId);
                    }
                    cursor.moveToNext();
                }
            }
            db = getWritableDatabase();
            String whereClause = COLUMN_QUESTION_COURSE + "=" + courseId;
            db.delete(TABLE_QUESTION, whereClause, null);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getQuestionIdByText(String _text){
        int questionId = 0;
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT " + COLUMN_QUESTION_ID + " FROM " + TABLE_QUESTION
                    + " WHERE (" + COLUMN_QUESTION_TEXT + " = " + "\"" + _text + "\");";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast())
                questionId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID));
            db.close();
        return questionId;
    }

    public Vector<Bundle> getChoicesByQuestionId(int questionId){

        Vector<Bundle> choices = new Vector<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CHOICE + " WHERE (" + COLUMN_CHOICE_QUESTION + "=\"" + questionId + "\");";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if( ! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
            while(! cursor.isAfterLast()){
                Bundle bundle = new Bundle();
                int choiceId = cursor.getInt(cursor.getColumnIndex(COLUMN_CHOICE_ID));
                String choiceText = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE_TEXT));
                bundle.putInt(KEY_CHOICE_ID, choiceId);
                bundle.putString(KEY_CHOICE_TEXT, choiceText);
                choices.add(bundle);
                cursor.moveToNext();
            }
        }
        db.close();
        return choices;
    }

    public Vector<Choice> readQuestionChoices(int questionId){

        Vector<Choice> choices = new Vector<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_CHOICE + " WHERE (" + COLUMN_CHOICE_QUESTION + "=\"" + questionId + "\");";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if( ! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

            while (! cursor.isAfterLast()){

                Choice choice = new Choice();
                int choiceId = cursor.getInt(cursor.getColumnIndex(COLUMN_CHOICE_ID));
                String choiceText = cursor.getString(cursor.getColumnIndex(COLUMN_CHOICE_TEXT));
                choice.set_id(choiceId);
                choice.set_text(choiceText);
                choices.add(choice);
                cursor.moveToNext();
            }
        }

        db.close();
        return choices;

    }

    public Vector<Bundle> readTeacher(){
            Vector<Bundle> teacherList = new Vector<>();
            SQLiteDatabase db = getWritableDatabase();

            String query = "SELECT * FROM " + TABLE_TEACHER + " WHERE (" + COLUMN_TEACHER_TYPE + " = " + String.valueOf(0) + ");";

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
                while (! cursor.isAfterLast()){
                    int teacherId = cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_ID));
                    String teacherName = cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_NAME));
                    String userName = cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_USERNAME));
                    String passWord = cursor.getString(cursor.getColumnIndex(COLUMN_TEACHER_PASSWORD));
                    int teacherState = cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_STATE));
                    int teacherType = cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_TYPE));
                    Bundle bundle = new Bundle();
                    bundle.putInt(KEY_TEACHER_ID, teacherId);
                    bundle.putString(KEY_TEACHER_NAME, teacherName);
                    bundle.putString(KEY_TEACHER_USERNAME, userName);
                    bundle.putString(KEY_TEACHER_PASSWORD, passWord);
                    bundle.putInt(KEY_TEACHER_STATE, teacherState);
                    bundle.putInt(KEY_TEACHER_TYPE, teacherType);
                    teacherList.add(bundle);
                    cursor.moveToNext();
                }
            }else{
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_TEACHER_ID, -1);
                bundle.putString(KEY_TEACHER_NAME, "There are no teachers");
                bundle.putString(KEY_TEACHER_USERNAME, "");
                bundle.putString(KEY_TEACHER_PASSWORD, "");
                bundle.putInt(KEY_TEACHER_STATE, 0);
                teacherList.add(bundle);
            }
            db.close();

        return teacherList;
    }

    public Vector<Bundle> readCourse(){

        Vector<Bundle> courseVector = new Vector<>();
        SQLiteDatabase db = getWritableDatabase();
        int teacherId = sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, -1);
        String query = "SELECT " + TABLE_COURSE + "." + COLUMN_COURSE_ID + ", " + COLUMN_COURSE_NAME + ", " + COLUMN_CLASS_YEAR
                + ", " + COLUMN_CLASS_TERM + " FROM " + TABLE_COURSE + " INNER JOIN " + TABLE_CLASS
                + " ON " + TABLE_COURSE + "." + COLUMN_COURSE_CLASS + "=" + TABLE_CLASS + "." + COLUMN_CLASS_ID
                + " WHERE (" + COLUMN_COURSE_TEACHER + "=\"" + teacherId + "\");";

            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){
                while(! cursor.isAfterLast()){
                    int courseId = cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID));
                    String courseName = cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME));
                    int courseYear = cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_YEAR));
                    int courseTerm = cursor.getInt(cursor.getColumnIndex(COLUMN_CLASS_TERM));
                    Bundle bundle = new Bundle();
                    bundle.putInt(KEY_COURSE_ID, courseId);
                    bundle.putString(KEY_COURSE_NAME, courseName);
                    bundle.putInt(KEY_COURSE_YEAR, courseYear);
                    bundle.putInt(KEY_COURSE_TERM, courseTerm);
                    courseVector.add(bundle);
                    cursor.moveToNext();
                }
            }else{
                Bundle bundle = new Bundle();
                bundle.putInt(KEY_COURSE_ID, 0);
                bundle.putString(KEY_COURSE_NAME, "Course List Empty");
                bundle.putInt(KEY_COURSE_YEAR, 0);
                bundle.putInt(KEY_COURSE_TERM, 0);
                courseVector.add(bundle);
            }
        db.close();
        return courseVector;
    }

    public Vector<Bundle> readQuestion(int courseId, final int mcqOrEssay){

        final Vector<Bundle> questionVector = new Vector<>();
        final int courseID = courseId;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                String query = "SELECT * FROM " + TABLE_QUESTION + " WHERE (" + COLUMN_QUESTION_COURSE + " = " + courseID + ");";
                String mcqQuery = "SELECT * FROM " + TABLE_QUESTION + " WHERE (" + COLUMN_QUESTION_COURSE + " = " + courseID + " AND "
                                                                                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + mcqOrEssay +  ");";
                Cursor cursor;
                if(mcqOrEssay == 0 || mcqOrEssay == 1)
                    cursor = db.rawQuery(mcqQuery, null);
                else
                    cursor = db.rawQuery(query, null);
                cursor.moveToFirst();
                if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

                    while(! cursor.isAfterLast()){
                        //Setting row data into variables...
                        int questionId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID));
                        String questionText = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT));
                        int mcqOrEssay = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_MCQ_OR_REGULAR));
                        int practicalOrTheory = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_PRACTICAL_OR_THEORY));
                        int difficulty = cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_DIFFICULTY));
                        float time = getQuestionTimeByType(mcqOrEssay, practicalOrTheory,  difficulty);

                        Bundle bundle = new Bundle();
                        bundle.putInt(KEY_QUESTION_ID, questionId);
                        bundle.putString(KEY_QUESTION_TEXT, questionText);
                        bundle.putInt(KEY_QUESTION_MCQ_OR_ESSAY, mcqOrEssay);
                        bundle.putInt(KEY_QUESTION_PRACTICAL_OR_THEORY, practicalOrTheory);
                        bundle.putInt(KEY_QUESTION_DIFFICULTY, difficulty);
                        bundle.putFloat(KEY_QUESTION_TIME, time);
                        questionVector.add(bundle);

                        cursor.moveToNext();
                    }
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt(KEY_QUESTION_ID, 0);
                    bundle.putString(KEY_QUESTION_TEXT, "No questions available for this course");
                    bundle.putInt(KEY_QUESTION_MCQ_OR_ESSAY, -1);
                    bundle.putInt(KEY_QUESTION_PRACTICAL_OR_THEORY, -1);
                    bundle.putInt(KEY_QUESTION_DIFFICULTY, -1);
                    bundle.putFloat(KEY_QUESTION_TIME, -1);
                    questionVector.add(bundle);
                }
                db.close();
            }
        };
        Thread thread = new Thread(r);
        r.run();
        return questionVector;
    }

    public Exam examQuestionListByDifficulty(Exam _exam){

        Vector<Question> _entireQuestionList = new Vector<>();
        Vector<Question> _examQuestionList = new Vector<>();
        Vector<Question> sortedQuestionList = new Vector<>();

        String betweenClause = " BETWEEN ";
        switch (_exam.get_visualDifficulty()){
            case 1:
                betweenClause += "1 AND 3";
                break;

            case 2:
                betweenClause += "4 AND 6";
                break;

            case 3:
                betweenClause += "7 AND 8";
                break;

            case 4:
                betweenClause += "9 AND 10";
                break;

            default:
                break;
        }

        String mcqOrEssayQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + " AND "
                + COLUMN_QUESTION_DIFFICULTY + betweenClause + ");";

        String bothQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + " AND "
                + COLUMN_QUESTION_DIFFICULTY + betweenClause + ");";

        String mcqOrEssayTheoryAndPractical = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + " AND "
                + COLUMN_QUESTION_DIFFICULTY + betweenClause + ");";

        String bothTheoryAndPracticalQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_DIFFICULTY + betweenClause + ");";

        SQLiteDatabase db = getWritableDatabase();

        try {
            Cursor cursor;

            if(_exam.get_mcqOrEssay() == 2){

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(bothTheoryAndPracticalQuery, null);
                }else{
                    cursor = db.rawQuery(bothQuery, null);
                }

            }else{

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(mcqOrEssayTheoryAndPractical, null);
                }else{
                    cursor = db.rawQuery(mcqOrEssayQuery, null);
                }
            }

            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

                while (! cursor.isAfterLast()){
                    Question question = new Question();
                    question.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID)));
                    question.set_text(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)));
                    question.set_difficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_DIFFICULTY)));
                    question.set_mcqOrRegular(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_MCQ_OR_REGULAR)));
                    question.set_pracOrTheory(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_PRACTICAL_OR_THEORY)));
                    question.set_time(getQuestionTimeByType(question.get_mcqOrRegular(), question.get_pracOrTheory(), question.get_difficulty()));

                    //Add question to questionList
                    _entireQuestionList.add(question);
                    cursor.moveToNext();
                }

                //Number of questions with selected difficulty available for selection (to use with randomization)
                Random random = new Random();
                float newQuestionListTime = 0;
                float newExamTime = 0;
                int newQuestionListSumOfDifficulties = 0;
                _exam.set_time(0);

                if(! _entireQuestionList.isEmpty()){
                    while(! _entireQuestionList.isEmpty() && _exam.get_time() <= _exam.get_maxTime()){
                        int selectedPosition = random.nextInt(_entireQuestionList.size());
                        Question selectedQuestion = _entireQuestionList.get(selectedPosition);
                        _examQuestionList.add(selectedQuestion);
                        _entireQuestionList.remove(selectedPosition);
                        newQuestionListSumOfDifficulties += selectedQuestion.get_difficulty();
                        newQuestionListTime += selectedQuestion.get_time();
                        _exam.set_time(_exam.get_time() + selectedQuestion.get_time()) ;
                    }
                }

                if(_exam.get_time() > _exam.get_maxTime()){
                    //Sorting the question list in an ascending fashion in order to remove the question with the lowest time
                    //to bring the total exam time to the closest aggregate time to what the user requires...
                    if(newQuestionListTime > _exam.get_maxTime()){
                        Question minQuestion = null;
                        while (! _examQuestionList.isEmpty()){
                            minQuestion = _examQuestionList.firstElement();
                            for(Question toCompareWith : _examQuestionList){

                                if(minQuestion.get_time() > toCompareWith.get_time()){
                                    minQuestion = toCompareWith;
                                }

                            }
                            sortedQuestionList.add(minQuestion);
                            _examQuestionList.remove(minQuestion);
                        }

                        for (Question question : sortedQuestionList){

                            newExamTime = newQuestionListTime - question.get_time();
                            if(newExamTime <= _exam.get_maxTime()){
                                sortedQuestionList.remove(question);
                                newQuestionListSumOfDifficulties -= question.get_difficulty();
                                break;
                            }
                        }
                        _exam.set_time(newExamTime);
                    }
                }

                double realExamDifficulty = newQuestionListSumOfDifficulties/sortedQuestionList.size();

                if(realExamDifficulty >=1.0 && realExamDifficulty <=3.0)
                    _exam.set_visualDifficulty(1);
                if(realExamDifficulty >=3.0 && realExamDifficulty <=6.0)
                    _exam.set_visualDifficulty(2);
                if(realExamDifficulty >6.0 && realExamDifficulty <=8.0)
                    _exam.set_visualDifficulty(3);
                if(realExamDifficulty >8.0 && realExamDifficulty <=10.0)
                    _exam.set_visualDifficulty(4);


                _exam.set_exactDiffculty(realExamDifficulty);
                _exam.set_questionList(sortedQuestionList);
                _exam.set_noOfQuestions(sortedQuestionList.size());
            }else{
                _exam.set_questionList(null);
                _exam.set_noOfQuestions(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return _exam;
    }

    public Exam examQuestionListByTime(Exam _exam){

        Vector<Question> entireQuestionList = new Vector<>();
        Vector<Question> _examQuestionList = new Vector<>();
        Vector<Question> sortedQuestionList = new Vector<>();
        float newExamTime = 0;

        String mcqOrEssayQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + ");";

        String bothQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + ");";

        String mcqOrEssayTheoryAndPractical = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + ");";

        String bothTheoryAndPracticalQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + ");";

        SQLiteDatabase db = getWritableDatabase();

        try {
            Cursor cursor;

            if(_exam.get_mcqOrEssay() == 2){

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(bothTheoryAndPracticalQuery, null);
                }else{
                    cursor = db.rawQuery(bothQuery, null);
                }

            }else{

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(mcqOrEssayTheoryAndPractical, null);
                }else{
                    cursor = db.rawQuery(mcqOrEssayQuery, null);
                }
            }

            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

                while (! cursor.isAfterLast()){
                    Question question = new Question();
                    question.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID)));
                    question.set_text(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)));
                    question.set_difficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_DIFFICULTY)));
                    question.set_mcqOrRegular(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_MCQ_OR_REGULAR)));
                    question.set_pracOrTheory(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_PRACTICAL_OR_THEORY)));
                    question.set_time(getQuestionTimeByType(question.get_mcqOrRegular(), question.get_pracOrTheory(), question.get_difficulty()));

                    //Add question to questionList
                    if(question.get_time() <= _exam.get_maxTime())
                        entireQuestionList.add(question);
                    cursor.moveToNext();
                }

                //Number of questions with selected parameters available for selection (to use with randomization)...
                Random random = new Random();
                float newQuestionListTime = 0;
                int newQuestionListSumOfDifficulties = 0;
                if(! entireQuestionList.isEmpty()){

                    while(! entireQuestionList.isEmpty() && newQuestionListTime < _exam.get_maxTime()){
                        int selectedPosition = random.nextInt(entireQuestionList.size());
                        Question question = entireQuestionList.get(selectedPosition);
                        _examQuestionList.add(question);
                        newQuestionListTime += question.get_time();
                        newQuestionListSumOfDifficulties += question.get_difficulty();
                        entireQuestionList.remove(selectedPosition);
                    }


                    //Sorting the question list in an ascending fashion in order to remove the question with the lowest time
                    //to bring the total exam time to the closest aggregate time to what the user requires...
                    if(newQuestionListTime > _exam.get_maxTime()){
                        Question minQuestion = null;
                        while (! _examQuestionList.isEmpty()){
                            minQuestion = _examQuestionList.firstElement();
                            for(Question toCompareWith : _examQuestionList){

                                if(minQuestion.get_time() > toCompareWith.get_time()){
                                    minQuestion = toCompareWith;
                                }

                            }
                            sortedQuestionList.add(minQuestion);
                            _examQuestionList.remove(minQuestion);
                        }

                        for (Question question : sortedQuestionList){

                            newExamTime = newQuestionListTime - question.get_time();
                            if(newExamTime <= _exam.get_maxTime()){
                                sortedQuestionList.remove(question);
                                break;
                            }
                        }


                    }

                    int realExamDifficulty = (newQuestionListSumOfDifficulties/sortedQuestionList.size()) -1;
                    int newExamDifficulty = 1;

                    if(realExamDifficulty >=1 && realExamDifficulty <=3)
                        newExamDifficulty = 1;
                    if(realExamDifficulty >3 && realExamDifficulty <=6)
                        newExamDifficulty = 2;
                    if(realExamDifficulty >6 && realExamDifficulty <=8)
                        newExamDifficulty = 3;
                    if(realExamDifficulty >8 && realExamDifficulty <=10)
                        newExamDifficulty = 4;

                    _exam.set_questionList(sortedQuestionList);
                    _exam.set_noOfQuestions(sortedQuestionList.size());
                    _exam.set_maxTime(_exam.get_time());
                    _exam.set_time(newExamTime);
                    _exam.set_exactDiffculty(realExamDifficulty);
                    _exam.set_visualDifficulty(newExamDifficulty);
                }

            }else{
                _exam.set_questionList(null);
                _exam.set_noOfQuestions(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return _exam;
    }

    public Exam examQuestionListByNoOfQuestions(Exam _exam){

        Vector<Question> entireQuestionList = new Vector<>();
        Vector<Question> _examQuestionList = new Vector<>();

        float maxQuestionTime = 0;
        float entireQuestionListTime = 0;

        String mcqOrEssayQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + ");";

        String bothQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_PRACTICAL_OR_THEORY + " = " + _exam.get_theoryOrPractical() + ");";

        String mcqOrEssayTheoryAndPractical = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + " AND "
                + COLUMN_QUESTION_MCQ_OR_REGULAR + " = " + _exam.get_mcqOrEssay() + ");";

        String bothTheoryAndPracticalQuery = "SELECT " + "*" + " FROM " + TABLE_QUESTION + " WHERE ("
                + COLUMN_QUESTION_COURSE + " = " + _exam.get_course() + ");";

        SQLiteDatabase db = getWritableDatabase();

        try {
            Cursor cursor;

            if(_exam.get_mcqOrEssay() == 2){

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(bothTheoryAndPracticalQuery, null);
                }else{
                    cursor = db.rawQuery(bothQuery, null);
                }

            }else{

                if(_exam.get_theoryOrPractical() == 2){
                    cursor = db.rawQuery(mcqOrEssayTheoryAndPractical, null);
                }else{
                    cursor = db.rawQuery(mcqOrEssayQuery, null);
                }
            }

            cursor.moveToFirst();
            if(! cursor.isBeforeFirst() || ! cursor.isAfterLast()){

                while (! cursor.isAfterLast()){
                    Question question = new Question();
                    question.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_ID)));
                    question.set_text(cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_TEXT)));
                    question.set_difficulty(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_DIFFICULTY)));
                    question.set_mcqOrRegular(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_MCQ_OR_REGULAR)));
                    question.set_pracOrTheory(cursor.getInt(cursor.getColumnIndex(COLUMN_QUESTION_PRACTICAL_OR_THEORY)));
                    question.set_time(getQuestionTimeByType(question.get_mcqOrRegular(), question.get_pracOrTheory(), question.get_difficulty()));

                    //In order to get only questions which don't exceed the max question time
                    //So that the time of all questions doesn't exceed the max exam time...
                    maxQuestionTime = _exam.get_maxTime() / _exam.get_noOfQuestions();
                    //Add question to questionList
                    if(question.get_time() <= maxQuestionTime)
                        entireQuestionList.add(question);
                    cursor.moveToNext();
                }

                //Number of questions with selected parameters available for selection (to use with randomization)...
                Random random = new Random();
                float newQuestionListTime = 0;
                int newQuestionListSumOfDifficulties = 0;
                if(! entireQuestionList.isEmpty()){

                    //To get no of questions which match the parameter (NoOfQuestions) which was passed to the exam...
                    while(! entireQuestionList.isEmpty() && _examQuestionList.size() < _exam.get_noOfQuestions()){
                        int selectedPosition = random.nextInt(entireQuestionList.size());
                        Question question = entireQuestionList.get(selectedPosition);
                        _examQuestionList.add(question);
                        newQuestionListTime += question.get_time();
                        newQuestionListSumOfDifficulties += question.get_difficulty();
                        entireQuestionList.remove(selectedPosition);
                    }



                    double realExamDifficulty = (newQuestionListSumOfDifficulties/_examQuestionList.size());
                    int newExamDifficulty = 1;

                    if(realExamDifficulty >=1 && realExamDifficulty <=3)
                        newExamDifficulty = 1;
                    if(realExamDifficulty >3 && realExamDifficulty <=6)
                        newExamDifficulty = 2;
                    if(realExamDifficulty >6 && realExamDifficulty <=8)
                        newExamDifficulty = 3;
                    if(realExamDifficulty >8 && realExamDifficulty <=10)
                        newExamDifficulty = 4;

                    _exam.set_questionList(_examQuestionList);
                    _exam.set_noOfQuestions(_examQuestionList.size());
                    _exam.set_maxTime(_exam.get_time());
                    _exam.set_time(newQuestionListTime);
                    _exam.set_exactDiffculty(realExamDifficulty);
                    _exam.set_visualDifficulty(newExamDifficulty);
                }

            }else{
                _exam.set_questionList(null);
                _exam.set_noOfQuestions(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return _exam;
    }

    public float getQuestionTimeByType(int mcq, int practical, int difficulty){
        float time = MIN_PER_DIFFPOINTS * difficulty;
        //MCQ = 0, Essay = 1.
        //Practical = 0, Theory = 1.
        if(mcq == 0 && practical == 1)
            time *= THEORY_MCQ_DIFF_POINTS;
        if(mcq == 0 && practical == 0)
            time *= PRACTICAL_MCQ_DIFF_POINTS;
        if(mcq == 1 && practical == 1)
            time *= THEORY_ESSAY_DIFF_POINTS;
        if(mcq == 1 && practical == 0)
            time *= PRACTICAL_ESSAY_DIFF_POINTS;
        return time;
    }

    public Course getCourseById(int courseId){

        String query = "SELECT " + "*" + " FROM " + TABLE_COURSE + " WHERE (" + COLUMN_COURSE_ID + " = " + courseId + ");";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        Course course = new Course();
        course.set_id(cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_ID)));
        course.set_name(cursor.getString(cursor.getColumnIndex(COLUMN_COURSE_NAME)));
        course.set_courseClass(cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_CLASS)));
        course.set_courseTeacher(cursor.getInt(cursor.getColumnIndex(COLUMN_COURSE_TEACHER)));
        return course;
    }



}
