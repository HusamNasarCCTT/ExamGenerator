package cctt.grad.examgenerator.View;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import cctt.grad.examgenerator.Presenter.ExamDBHandler;
import cctt.grad.examgenerator.Presenter.SessionManager;
import cctt.grad.examgenerator.Model.Choice;
import cctt.grad.examgenerator.Model.Exam;
import cctt.grad.examgenerator.Model.Question;
import cctt.grad.examgenerator.Utilities.ShakeDetector;
import cctt.grad.examgenerator.R;

public class DisplayExam extends AppCompatActivity {


    private ExamDBHandler examDBHandler = null;
    private Intent intent = null;
    private int courseId;
    private Bundle examParameters = null;
    private ListView questionListView = null;
    private int mcqOrEssay, theoryOrPractical, difficulty, noOfQuestions, teacherId;
    private float time;
    private SessionManager sessionManager = null;
    private Exam exam = null;
    private int examGenerationMethod = 0;
    private FloatingActionButton examPrinter = null,
            regenerateButton = null,
            floatingActionOptions = null,
            displayExamPopupAssistor = null;

    private final int ENGLISH = 1, ARABIC = 2;
    private int reportLanguage = ENGLISH;
    private String courseName, teacherName = null;

    //Animation variables...
    Animation show_popupAssistor;
    Animation hide_popupAssistor;
    Animation show_displayExamPrinter;
    Animation hide_displayExamPrinter;
    Animation show_regenerateButton;
    Animation hide_regenerateButton;

    private boolean FAB_Status = false;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    private String pdfFileDirectory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_exam_coordinator);

        //To enable Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        examDBHandler = new ExamDBHandler(this, null, null, 1);

        questionListView = (ListView) findViewById(R.id.examQuestions);
        examPrinter = (FloatingActionButton) findViewById(R.id.displayExamPrinter);
        regenerateButton = (FloatingActionButton) findViewById(R.id.regenerateButton);
        floatingActionOptions = (FloatingActionButton) findViewById(R.id.floatingActionOptions);
        displayExamPopupAssistor = (FloatingActionButton) findViewById(R.id.displayExamPopupAssistor);

        //Get Exam Parameters from user's input in ExamGenerator Activity...
        intent = getIntent();
        courseId = intent.getIntExtra("Course ID", -1);
        examParameters = intent.getExtras();

        //Initializing Session Manager to get TeacherID for use with Exam Object..
        sessionManager = new SessionManager(getApplicationContext());
        teacherId = sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, -1);

        mcqOrEssay = examParameters.getInt("MCQ or Essay");
        theoryOrPractical = examParameters.getInt("Theory or Practical");
        examGenerationMethod = examParameters.getInt("Method");

        //Getting reference to Animator XML Files...
        show_popupAssistor = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_popupAssistor = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_displayExamPrinter = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_displayExamPrinter = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_regenerateButton = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_regenerateButton = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);



        switch (examGenerationMethod){

            case 1:
                difficulty = examParameters.getInt("Difficulty");
                time = examParameters.getFloat("Time", -1);
                generateExamByDifficulty();
                break;

            case 2:
                time = examParameters.getFloat("Time", -1);
                generateByTime();
                break;

            case 3:
                noOfQuestions = examParameters.getInt("No Of Questions", -1);
                time = examParameters.getFloat("Time", -1);
                generateByNoOfQuestions();
                break;
        }



        questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = (Bundle) parent.getItemAtPosition(position);
                int questionId = bundle.getInt("Question Id");
                bundle.putInt("Course ID", courseId);
                int isMCQ = bundle.getInt("Mcq or Essay");
                if(questionId == 0){
                    Toast.makeText(DisplayExam.this, "Please add a question to the course", Toast.LENGTH_SHORT).show();
                }else{
                    intent = new Intent(DisplayExam.this, DisplayQuestion.class);
                    intent.putExtra("Course ID", courseId);
                    intent.putExtra("Question Bundle", bundle);
                    intent.putExtra("Course Name", examDBHandler.getCourseById(courseId).get_name());
                    intent.putExtra("Activity Name", "DisplayExam");
                    intent.putExtra("Exam Parameters", examParameters);
                    startActivity(intent);
                }
            }
        });

        regenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inner switch for defining which method to regenerate with...
                switch (examGenerationMethod){

                    case 1:
                        generateExamByDifficulty();
                        break;

                    case 2:
                        generateByTime();
                        break;

                    case 3:
                        generateByNoOfQuestions();
                        break;

                    default:
                        Toast.makeText(DisplayExam.this, "Something's wrong, Pato;", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setTitle(examDBHandler.getCourseById(courseId).get_name() + " Exam");


        examPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exam.get_questionList() != null){
                    Snackbar.make(findViewById(R.id.displayExamCoordinator), "Hit \"OK\" if you've already chosen the language for your PDF", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    printExam(exam);
                                }
                            }).show();
                }else{
                    Snackbar.make(findViewById(R.id.displayExamCoordinator), "Exam couldn't be generated, therefore, PDF can't be generated", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    printExam(exam);
                                }
                            }).show();
                }
            }
        });


        floatingActionOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        displayExamPopupAssistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getCurrentFocus(), "Hit the \"refresh icon\" to load a new question list", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(getCurrentFocus(), "Tap the \"Save\" icon to export a pdf of the question list", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Snackbar.make(getCurrentFocus(), "Shake your phone to shuffle the question list", Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("OK", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Snackbar.make(getCurrentFocus(), "Tap the Actionbar options menu to View Exam Details", Snackbar.LENGTH_INDEFINITE)
                                                                        .setAction("OK", new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                return;
                                                                            }
                                                                        }).show();
                                                            }
                                                        }).show();
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });


        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {

                //Stub method to use whenever a shake event is registered...
                handleShakeEvent(count);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void generateExamByDifficulty(){

        exam = new Exam();
        exam.set_mcqOrEssay(mcqOrEssay);
        exam.set_theoryOrPractical(theoryOrPractical);
        exam.set_course(courseId);
        exam.set_visualDifficulty(difficulty);
        exam.set_maxTime(time);
        exam.set_teacher(sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, 0));
        //exam.set_noOfQuestions(noOfQuestions);

        //Defining range of question difficulties by exam difficulty...
        int MIN, MAX;
        switch (exam.get_visualDifficulty()){
            case 0:
                MIN = 1; MAX = 3;
                break;
            case 1:
                MIN = 4; MAX = 6;
                break;
            case 2:
                MIN = 7; MAX = 8;
                break;
            case 3:
                MIN = 9; MAX = 10;
                break;
            default:
                MIN = 0; MAX = 0;

        }

        exam = examDBHandler.examQuestionListByDifficulty(exam);

        if(exam.get_questionList() == null){
            examCouldntBeGeneratedToast().show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                addMoreQuestionsWithFixedDifficulties(MIN, MAX).show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);
            examGeneratedSuccessfullyToast().show();

        }
    }

    public void generateByTime(){

        exam = new Exam();
        exam.set_mcqOrEssay(mcqOrEssay);
        exam.set_theoryOrPractical(theoryOrPractical);
        exam.set_course(courseId);
        exam.set_visualDifficulty(difficulty);
        exam.set_maxTime(time);
        exam.set_teacher(sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, 0));

        exam = examDBHandler.examQuestionListByTime(exam);
        if(exam.get_questionList() == null){
            examCouldntBeGeneratedToast().show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                examGeneratedWithAdviceToast().show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);
            examGeneratedSuccessfullyToast().show();

        }

    }

    public void generateByNoOfQuestions(){

        exam = new Exam();
        exam.set_mcqOrEssay(mcqOrEssay);
        exam.set_theoryOrPractical(theoryOrPractical);
        exam.set_course(courseId);
        exam.set_visualDifficulty(difficulty);
        exam.set_noOfQuestions(noOfQuestions);
        exam.set_maxTime(time);
        exam.set_teacher(sessionManager.sharedPreferences.getInt(sessionManager.KEY_ID, 0));

        exam = examDBHandler.examQuestionListByNoOfQuestions(exam);
        if(exam.get_questionList() == null){
            examCouldntBeGeneratedToast().show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                addMoreQuestionsTimeTooLowToast().show();
            }
            if(exam.get_noOfQuestions() < noOfQuestions){
                addMoreQuestionsInsufficientQuestionsToast().show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);
            examGeneratedSuccessfullyToast().show();

        }
    }

    public Vector<Bundle> questionVectorToBundleVector(Vector<Question> questionVector){
        Vector<Bundle> vector = new Vector<>();

        for(Question question : questionVector){
            Bundle bundle = new Bundle();
            bundle.putInt(examDBHandler.KEY_QUESTION_ID, question.get_id());
            bundle.putString(examDBHandler.KEY_QUESTION_TEXT, question.get_text());
            bundle.putInt(examDBHandler.KEY_QUESTION_DIFFICULTY, question.get_difficulty());
            bundle.putFloat(examDBHandler.KEY_QUESTION_TIME, question.get_time());
            bundle.putInt(examDBHandler.KEY_QUESTION_MCQ_OR_ESSAY, question.get_mcqOrRegular());
            bundle.putInt(examDBHandler.KEY_QUESTION_PRACTICAL_OR_THEORY, question.get_pracOrTheory());

            vector.add(bundle);
        }

        return vector;
    }

    public Exam shuffleExamQuestions(Exam _exam){

        if(_exam.get_questionList() != null){
            //Initialize 2 Question vectors to shuffle old question list into the new one...
            Vector <Question> oldQuestionList = _exam.get_questionList();
            Vector <Question> newQuestionList = new Vector<>();

            //Select random questions from old list and put them in a new one...
            Random random = new Random();
            while (! oldQuestionList.isEmpty()){

                int selectedQuestion = random.nextInt(oldQuestionList.size());
                newQuestionList.add(oldQuestionList.get(selectedQuestion));
                oldQuestionList.remove(selectedQuestion);

            }
            _exam.set_questionList(newQuestionList);

            //Update Question ListView with new question list...
            questionListView.setAdapter(new CustomQuestionAdapter2(this, questionVectorToBundleVector(_exam.get_questionList())));
            examShuffledToast().show();
        }else{
            examCouldntBeShuffledToast().show();
        }

        return _exam;
    }

    public void handleShakeEvent(int count){
        shuffleExamQuestions(exam);
    }

    public void printExam(Exam _exam){


        //Constructing Filename generation with DDMMYYYYhhmmss format, e.g: 03051993235312
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");

        courseName = examDBHandler.getCourseById(_exam.get_course()).get_name();
        teacherName = examDBHandler.getTeacher(exam.get_teacher()).get_name();

        String pdfName = courseName + "_EXAM_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

        try {
            //Step 1- Create file in Internal Storage...

            pdfFileDirectory = Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName;
            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS + "/" + teacherName + "/" + courseName), pdfName);
            file.createNewFile();
            file.setReadable(true);
            OutputStream output = new FileOutputStream(file);

            //Step 2- Create instance of Document.getInstance() of PdfWriter...
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, output);

            //Step 3- Add document header attributes...

            document.open();
            prepareDocument(document);
            document.close();

            pdfWriter.close();

            //Open file with PDF Reader if one should exist...
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(file.getAbsolutePath()));
            intent.setType("application/pdf");
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities.size() > 0) {
                startActivity(intent);
            } else {
                // Do something else here. Maybe pop up a Dialog or Toast
                Snackbar.make(getCurrentFocus(), "Sorry, but no suitable PDF reader is installed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(getCurrentFocus(), "Exam has been saved in PDF format in the default directory", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Ok", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Snackbar.make(getCurrentFocus(), "Please install a PDF reader to view exam", Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("Ok", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                return;
                                                            }
                                                        }).show();
                                            }
                                        }).show();
                            }
                        }).show();
            }

            Toast.makeText(DisplayExam.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Document prepareDocument(Document _document){

        _document.addAuthor(teacherName);
        _document.addTitle(courseName);
        _document.addProducer();
        _document.addCreationDate();
        _document.addCreator("Exam Generator");

        switch (reportLanguage){
            case ENGLISH:
                return  englishDocument(_document);

            case ARABIC:
                return arabicDocument(_document);
        }

        return _document;
    }

    public Document arabicDocument(Document _document){

        Font generalFont = new Font();
        Font arabicFont = new Font();
        Font choiceFont = new Font();
        Font mainTitleFont = new Font();
        Font instructionFont = new Font();
        try {
            BaseFont baseFont = BaseFont.createFont("assets/Arial/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            generalFont = new Font(baseFont, 14, Font.NORMAL, BaseColor.BLACK);
            arabicFont = new Font(baseFont, 14, Font.NORMAL, BaseColor.BLACK);
            instructionFont = new Font(baseFont, 16, Font.BOLD, BaseColor.BLACK);
            choiceFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);
            mainTitleFont = new Font(baseFont, 18, Font.BOLD, BaseColor.BLACK);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Table to hold Document Content...
        PdfPTable table = new PdfPTable(1);

        //setWidthPercentage() to give the Table the entire width provided by the Document...
        table.setWidthPercentage(100);

        int examTime = (int) exam.get_time();
        int examTerm = examDBHandler.readClass(examDBHandler.getCourseById(exam.get_course()).get_courseClass()).get_term();
        int examYear = examDBHandler.readClass(examDBHandler.getCourseById(exam.get_course()).get_courseClass()).get_year();

        String teacherName = examDBHandler.getTeacher(exam.get_teacher()).get_name();
        String examTermInString;
        switch (examTerm){
            case 0:
                examTermInString = "ربيع";
                break;
            default:
                examTermInString = "خريف";
                break;
        }

        String examTitle = "إمتحان مادة " +  examDBHandler.getCourseById(exam.get_course()).get_name();

        table.addCell(setArCell(examTermInString + " - " + String.valueOf(examYear), PdfPCell.ALIGN_RIGHT, arabicFont));
        if(isQuestionEnglish(examTitle))
            table.addCell(setEnCell(String.valueOf(examTitle), PdfPCell.ALIGN_CENTER, mainTitleFont));
        else
            table.addCell(setArCell(String.valueOf(examTitle), PdfPCell.ALIGN_CENTER, mainTitleFont));

        table.addCell(setArCell("الزمن: " + String.valueOf(examTime) + " دقيقة", PdfPCell.ALIGN_RIGHT, arabicFont));
        String examInstructions = "أجب على كافة الإسئلة التالية:";

        Vector<Question> listOfMCQs = exam.getMCQs();
        Vector<Question> listOfEssays = exam.getEssays();

        Vector<PdfPCell> listOfMCQsInCells = new Vector<>();
        Vector<PdfPCell> listOfEssaysInCells = new Vector<>();

        int qNum = 1;
        if(! listOfMCQs.isEmpty()){

            String mcqInstruction;
            if(listOfMCQs.size() == 1)
                mcqInstruction = "إختر الإجابة الصحيحة للسؤال التالي:";
            else
                mcqInstruction = "إختر الإجابة الصحيحة لكل من:";
            PdfPCell mcqInstructionCell = setArCell(mcqInstruction, PdfPCell.ALIGN_LEFT, instructionFont);
            table.addCell(mcqInstructionCell);
            table.addCell("");
            for (Question mcq : listOfMCQs){

                //Create Question Cell and add it to Table...
                PdfPCell questionCell;

                if(isQuestionEnglish(mcq.get_text())){
                    questionCell = setEnCell("Q" + String.valueOf(qNum) + "/ " + mcq.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                }else{
                    questionCell = setArCell("س" + String.valueOf(qNum) + "/ " + mcq.get_text(), PdfPCell.ALIGN_LEFT, arabicFont);
                }

                listOfMCQsInCells.add(questionCell);
                qNum++;

                int choiceNum = 1;
                //Create MCQ choice cells and add them to table...
                Vector<Choice> choices = examDBHandler.readQuestionChoices(mcq.get_id());
                for (Choice choice : choices){

                    PdfPCell choiceCell;
                    if(isQuestionEnglish(choice.get_text())){
                        choiceCell = setEnCell( String.valueOf(choiceNum) + "- " + choice.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                    }else{
                        choiceCell = setArCell("    " + String.valueOf(choiceNum) + ") " + choice.get_text(), PdfPCell.ALIGN_LEFT, choiceFont);
                    }

                    listOfMCQsInCells.add(choiceCell);
                    choiceNum++;
                }

            }
            for(PdfPCell cell : listOfMCQsInCells){
                table.addCell(cell);
            }
        }

        if(! listOfEssays.isEmpty()){

            String mcqInstruction;
            if(listOfEssays.size() == 1)
                mcqInstruction = "أجب على السرال التالي:";
            else
                mcqInstruction = "أجب كل من:";
            PdfPCell mcqInstructionCell = setArCell(mcqInstruction, PdfPCell.ALIGN_LEFT, instructionFont);
            table.addCell(mcqInstructionCell);
            table.addCell("");
            for (Question essay : listOfEssays){


                //Create Question Cell and add it to Table...

                PdfPCell questionCell;
                if(isQuestionEnglish(essay.get_text())){
                    questionCell = setEnCell("Q" + String.valueOf(qNum) + "/ " + essay.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                }else{
                    questionCell = setArCell("س" + String.valueOf(qNum) + "/ " + essay.get_text(), PdfPCell.ALIGN_LEFT, arabicFont);
                }

                listOfEssaysInCells.add(questionCell);
                qNum++;
            }
            for(PdfPCell cell : listOfEssaysInCells){
                table.addCell(cell);
            }

        }

        table.addCell(setArCell("تمنياتي للجميع بالتوفيق", PdfPCell.ALIGN_RIGHT, arabicFont));
        if(isQuestionEnglish(teacherName))
            table.addCell(setEnCell(teacherName, PdfPCell.ALIGN_LEFT, generalFont));
        else
            table.addCell(setArCell(teacherName, PdfPCell.ALIGN_RIGHT, generalFont));

        try {
            _document.add(table);
        }catch (Exception e){
            e.printStackTrace();
        }
        return _document;
    }

    public Document englishDocument(Document _document){

        Font generalFont = new Font();
        Font arabicFont = new Font();
        Font choiceFont = new Font();
        Font mainTitleFont = new Font();
        Font instructionFont = new Font();
        try {
            BaseFont baseFont = BaseFont.createFont("assets/Arial/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            generalFont = new Font(baseFont, 14, Font.NORMAL, BaseColor.BLACK);
            arabicFont = new Font(baseFont, 14, Font.NORMAL, BaseColor.BLACK);
            instructionFont = new Font(baseFont, 16, Font.BOLD, BaseColor.BLACK);
            choiceFont = new Font(baseFont, 10, Font.NORMAL, BaseColor.BLACK);
            mainTitleFont = new Font(baseFont, 18, Font.BOLD, BaseColor.BLACK);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Table to hold Document Content...
        PdfPTable table = new PdfPTable(1);

        //setWidthPercentage() to give the Table the entire width provided by the Document...
        table.setWidthPercentage(100);

        int examTime = (int) exam.get_time();
        int examTerm = examDBHandler.readClass(examDBHandler.getCourseById(exam.get_course()).get_courseClass()).get_term();
        int examYear = examDBHandler.readClass(examDBHandler.getCourseById(exam.get_course()).get_courseClass()).get_year();

        String teacherName = examDBHandler.getTeacher(exam.get_teacher()).get_name();
        String examTermInString;
        switch (examTerm){
            case 0:
                examTermInString = "Spring";
                break;
            default:
                examTermInString = "Fall";
                break;
        }

        String examTitle = examDBHandler.getCourseById(exam.get_course()).get_name() + " Exam";

        table.addCell(setEnCell(examTermInString + " - " + String.valueOf(examYear), PdfPCell.ALIGN_RIGHT, generalFont));
        if(isQuestionEnglish(examTitle))
            table.addCell(setEnCell(String.valueOf(examTitle), PdfPCell.ALIGN_CENTER, mainTitleFont));
        else
            table.addCell(setArCell(String.valueOf(examTitle), PdfPCell.ALIGN_CENTER, mainTitleFont));
        table.addCell(setEnCell(String.valueOf(examTime) + " mins", PdfPCell.ALIGN_LEFT, generalFont));
        String examInstructions = "Answer All the following questions:";

        Vector<Question> listOfMCQs = exam.getMCQs();
        Vector<Question> listOfEssays = exam.getEssays();

        Vector<PdfPCell> listOfMCQsInCells = new Vector<>();
        Vector<PdfPCell> listOfEssaysInCells = new Vector<>();

        int qNum = 1;
        if(! listOfMCQs.isEmpty()){

            String mcqInstruction;
            if(listOfMCQs.size() == 1)
                mcqInstruction = "Choose the correct answer for the following question:";
            else
                mcqInstruction = "Choose the correct answer for each of the following questions:";
            PdfPCell mcqInstructionCell = setEnCell(mcqInstruction, PdfPCell.ALIGN_LEFT, instructionFont);
            table.addCell(mcqInstructionCell);
            table.addCell("");
            for (Question mcq : listOfMCQs){

                //Create Question Cell and add it to Table...
                PdfPCell questionCell;

                if(isQuestionEnglish(mcq.get_text())){
                    questionCell = setEnCell("Q" + String.valueOf(qNum) + "/ " + mcq.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                }else{
                    questionCell = setArCell("س" + String.valueOf(qNum) + "/ " + mcq.get_text(), PdfPCell.ALIGN_LEFT, arabicFont);
                }

                listOfMCQsInCells.add(questionCell);
                qNum++;

                int choiceNum = 1;
                //Create MCQ choice cells and add them to table...
                Vector<Choice> choices = examDBHandler.readQuestionChoices(mcq.get_id());
                for (Choice choice : choices){

                    PdfPCell choiceCell;
                    if(isQuestionEnglish(choice.get_text())){
                        choiceCell = setEnCell( String.valueOf(choiceNum) + "- " + choice.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                    }else{
                        choiceCell = setArCell("    " + String.valueOf(choiceNum) + ") " + choice.get_text(), PdfPCell.ALIGN_LEFT, choiceFont);
                    }

                    listOfMCQsInCells.add(choiceCell);
                    choiceNum++;
                }

            }
            for(PdfPCell cell : listOfMCQsInCells){
                table.addCell(cell);
            }
        }

        if(! listOfEssays.isEmpty()){

            String mcqInstruction;
            if(listOfEssays.size() == 1)
                mcqInstruction = "Answer the following question:";
            else
                mcqInstruction = "Answer the following questions:";
            PdfPCell mcqInstructionCell = setEnCell(mcqInstruction, PdfPCell.ALIGN_LEFT, instructionFont);
            table.addCell(mcqInstructionCell);
            table.addCell("");
            for (Question essay : listOfEssays){

                //Create Question Cell and add it to Table...

                PdfPCell questionCell;
                if(isQuestionEnglish(essay.get_text())){
                    questionCell = setEnCell("Q" + String.valueOf(qNum) + "/ " + essay.get_text(), PdfPCell.ALIGN_LEFT, generalFont);
                }else{
                    questionCell = setArCell("س" + String.valueOf(qNum) + "/ " + essay.get_text(), PdfPCell.ALIGN_LEFT, arabicFont);
                }


                listOfEssaysInCells.add(questionCell);
                qNum++;
            }
            for(PdfPCell cell : listOfEssaysInCells){
                table.addCell(cell);
            }

        }



        table.addCell(setEnCell("Best of luck", PdfPCell.ALIGN_RIGHT, generalFont));
        if(isQuestionEnglish(teacherName))
            table.addCell(setEnCell(teacherName, PdfPCell.ALIGN_RIGHT, generalFont));
        else
            table.addCell(setArCell(teacherName, PdfPCell.ALIGN_LEFT, generalFont));

        try {
            _document.add(table);
        }catch (Exception e){
            e.printStackTrace();
        }
        return _document;
    }

    public PdfPCell setArCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell setEnCell(String text, int alignment, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public boolean isQuestionEnglish(String question){

        char firstCharacter = question.charAt(0);


        if( ( (firstCharacter >=65 ) && (firstCharacter < (65 + 26) ) ) || ( (firstCharacter >=97 ) && (firstCharacter < (97 + 26) ) ) ){
            return true;
        }
        return false;

    }

    public Toast examGeneratedSuccessfullyToast(){
        Toast toast = Toast.makeText(this, "Exam Generated Successfully", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_success, 0, 0, 0);
        }

        return toast;
    }

    public Toast examGeneratedWithAdviceToast(){
        Toast toast = Toast.makeText(this, "We would advise to add more questions" + " as the generated exam time is way too low", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_caution, 0, 0, 0);
        }

        return toast;
    }

    public Toast examCouldntBeGeneratedToast(){
        Toast toast = Toast.makeText(this, "No exam to view, as questions " +
                "with the requested parameters do not exist", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_fail, 0, 0, 0);
        }

        return toast;
    }

    public Toast examShuffledToast(){
        Toast toast = Toast.makeText(this, "Exam Shuffled Successfully", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_success, 0, 0, 0);
        }

        return toast;
    }

    public Toast examCouldntBeShuffledToast(){
        Toast toast = Toast.makeText(this, "Question list is empty, " +
                "therefore, exam can't be shuffled", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_fail, 0, 0, 0);
        }

        return toast;
    }

    public Toast addMoreQuestionsTimeTooLowToast(){
        Toast toast = Toast.makeText(this, "We would advise to add more questions" + " as the generated exam time is way too low", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_caution, 0, 0, 0);
        }

        return toast;
    }

    public Toast addMoreQuestionsInsufficientQuestionsToast(){
        Toast toast = Toast.makeText(this, "I would advise to add more questions" +
                " as the system's available number of questions is insufficient", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_caution, 0, 0, 0);
        }

        return toast;
    }

    public Toast addMoreQuestionsWithFixedDifficulties(int min, int max){
        Toast toast = Toast.makeText(this, "We would advise to add more questions" +
                " with a difficulty of " + String.valueOf(min) + " TO " + String.valueOf(max) + " as the generated exam time is way too low", Toast.LENGTH_SHORT);
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setGravity(Gravity.CENTER);
        if(toastText != null){
            toastText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.exam_caution, 0, 0, 0);
        }

        return toast;
    }



    //Animation Methods...

    private void expandFAB() {

        //Change the drawable on the button...
        floatingActionOptions.setImageResource(android.R.drawable.button_onoff_indicator_on);

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) displayExamPopupAssistor.getLayoutParams();
        layoutParams.rightMargin += (int) (displayExamPopupAssistor.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (displayExamPopupAssistor.getHeight() * 0.25);
        displayExamPopupAssistor.setLayoutParams(layoutParams);
        displayExamPopupAssistor.startAnimation(show_popupAssistor);
        displayExamPopupAssistor.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) examPrinter.getLayoutParams();
        layoutParams2.rightMargin += (int) (examPrinter.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (examPrinter.getHeight() * 1.5);
        examPrinter.setLayoutParams(layoutParams2);
        examPrinter.startAnimation(show_displayExamPrinter);
        examPrinter.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) regenerateButton.getLayoutParams();
        layoutParams3.rightMargin += (int) (regenerateButton.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (regenerateButton.getHeight() * 1.7);
        regenerateButton.setLayoutParams(layoutParams3);
        regenerateButton.startAnimation(show_regenerateButton);
        regenerateButton.setClickable(true);
    }

    private void hideFAB() {

        //Change the drawable on the button...
        floatingActionOptions.setImageResource(android.R.drawable.button_onoff_indicator_off);

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) displayExamPopupAssistor.getLayoutParams();
        layoutParams.rightMargin -= (int) (displayExamPopupAssistor.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (displayExamPopupAssistor.getHeight() * 0.25);
        displayExamPopupAssistor.setLayoutParams(layoutParams);
        displayExamPopupAssistor.startAnimation(hide_popupAssistor);
        displayExamPopupAssistor.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) examPrinter.getLayoutParams();
        layoutParams2.rightMargin -= (int) (examPrinter.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (examPrinter.getHeight() * 1.5);
        examPrinter.setLayoutParams(layoutParams2);
        examPrinter.startAnimation(hide_displayExamPrinter);
        examPrinter.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) regenerateButton.getLayoutParams();
        layoutParams3.rightMargin -= (int) (regenerateButton.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (regenerateButton.getHeight() * 1.7);
        regenerateButton.setLayoutParams(layoutParams3);
        regenerateButton.startAnimation(hide_regenerateButton);
        regenerateButton.setClickable(false);
    }


    //Inflating menu resource by Options Menu...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_details, menu);
        menu.setGroupCheckable(R.id.pdfExportingLanguage, true, true);
        menu.findItem(R.id.englishPdf).setChecked(true);

        return true;
    }


    //What to do with options...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.englishPdf:
                item.setChecked(true);
                reportLanguage = ENGLISH;
                return true;

            case R.id.arabicPdf:
                item.setChecked(true);
                reportLanguage = ARABIC;
                return true;

            case android.R.id.home:
                finish();
                return true;

            case R.id.view_details:
                if(exam.get_questionList() == null){
                    examCouldntBeGeneratedToast().show();
                }else{
                    Intent toViewExamDetails = new Intent(DisplayExam.this, ViewExamDetails.class);
                    Bundle examDetails = new Bundle();
                    examDetails.putInt("Course ID", courseId);
                    examDetails.putString("Course Name", examDBHandler.getCourseById(exam.get_course()).get_name());
                    examDetails.putInt("No of Questions", exam.get_noOfQuestions());
                    examDetails.putInt("Visual Difficulty", exam.get_visualDifficulty());
                    examDetails.putFloat("Time", exam.get_time());
                    // examDetails.putInt("MCQ or Essay", exam.get_mcqOrEssay());
                    //examDetails.putInt("Theory or Practical", exam.get_theoryOrPractical());
                    examDetails.putDouble("Exact Difficulty", exam.get_exactDiffculty());
                    examDetails.putInt("No of MCQs", exam.getMCQs().size());
                    examDetails.putInt("No of Essays", exam.getEssays().size());
                    examDetails.putInt("No of Theories", exam.getTheoryCount());
                    examDetails.putInt("No of Practicals", exam.getPracticalCount());

                    int[] questionDifficultyArray = new int[exam.get_noOfQuestions()];
                    try {
                        for (int i=0; i < questionDifficultyArray.length; i++){

                            questionDifficultyArray[i] = exam.get_questionList().get(i).get_difficulty();

                        }
                        examDetails.putIntArray("Question Difficulties", questionDifficultyArray);
                    }catch (Exception e){
                        Toast.makeText(DisplayExam.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    toViewExamDetails.putExtras(examDetails);
                    startActivity(toViewExamDetails);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

}
