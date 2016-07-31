package cctt.grad.examgenerator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.FontFactoryImp;
import com.itextpdf.text.List;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.fonts.otf.Language;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;
import com.jjoe64.graphview.GraphView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class DisplayExam extends AppCompatActivity {


    private ExamDBHandler examDBHandler = null;
    private Intent intent = null;
    private int courseId;
    private Bundle examParameters = null;
    private ListView questionListView = null;
    private Button examPrinter = null;
    private int mcqOrEssay, theoryOrPractical, difficulty, noOfQuestions, teacherId;
    private float time;
    private SessionManager sessionManager = null;
    private Exam exam = null;
    private int examGenerationMethod = 0;
    private FloatingActionButton regenerateButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_exam_coordinator);

        //To enable Back/Home button...
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        examDBHandler = new ExamDBHandler(this, null, null, 1);

        questionListView = (ListView) findViewById(R.id.examQuestions);
        examPrinter = (Button) findViewById(R.id.printExam);
        regenerateButton = (FloatingActionButton) findViewById(R.id.regenerateButton);

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
                Snackbar.make(findViewById(R.id.displayExamCoordinator), "Exam Generated Successfully", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        }).show();
            }
        });



        examPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printExam(exam);
            }
        });




    }

    public void generateExamByDifficulty(){

        exam = new Exam();
        exam.set_mcqOrEssay(mcqOrEssay);
        exam.set_theoryOrPractical(theoryOrPractical);
        exam.set_course(courseId);
        exam.set_visualDifficulty(difficulty);
        exam.set_maxTime(time);
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
            Toast.makeText(DisplayExam.this, "No questions meet the entered parameters, please enter more questions with" +
                    " the desired parameters", Toast.LENGTH_SHORT).show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                Toast.makeText(DisplayExam.this, "We would advise to add more questions" +
                        " with a difficulty of " + String.valueOf(MIN) + " TO " + String.valueOf(MAX) + " as the generated exam time is way too low", Toast.LENGTH_LONG).show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);

        }
    }

    public void generateByTime(){

        exam = new Exam();
        exam.set_mcqOrEssay(mcqOrEssay);
        exam.set_theoryOrPractical(theoryOrPractical);
        exam.set_course(courseId);
        exam.set_visualDifficulty(difficulty);
        exam.set_maxTime(time);

        exam = examDBHandler.examQuestionListByTime(exam);
        if(exam.get_questionList() == null){
            Toast.makeText(DisplayExam.this, "No questions meet the entered parameters, please enter more questions with" +
                    " the desired parameters", Toast.LENGTH_SHORT).show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                Toast.makeText(DisplayExam.this, "We would advise to add more questions" + " as the generated exam time is way too low", Toast.LENGTH_LONG).show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);

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

        exam = examDBHandler.examQuestionListByNoOfQuestions(exam);
        if(exam.get_questionList() == null){
            Toast.makeText(DisplayExam.this, "No questions meet the entered parameters, please enter more questions with" +
                    " the desired parameters", Toast.LENGTH_SHORT).show();
        }else{
            if(exam.get_time() < exam.get_maxTime()/2){
                Toast.makeText(DisplayExam.this, "We would advise to add more questions" + " as the generated exam time is way too low", Toast.LENGTH_LONG).show();
            }
            if(exam.get_noOfQuestions() < noOfQuestions){
                Toast.makeText(DisplayExam.this, "I would advise to add more questions" +
                        " as the system's available number of questions is insufficient", Toast.LENGTH_SHORT).show();
            }
            CustomQuestionAdapter2 adapter = new CustomQuestionAdapter2(this, questionVectorToBundleVector(exam.get_questionList()));
            questionListView.setAdapter(adapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                /*
                Intent backIntent = new Intent(this, ExamGenerator.class);
                Bundle courseIntentData = new Bundle();
                courseIntentData.putInt("Course ID", getIntent().getIntExtra("Course ID", 0));
                startActivity(backIntent);*/
                finish();
                return true;
            case R.id.view_details:
                if(exam.get_questionList() == null){
                    Toast.makeText(DisplayExam.this, "No exam to view, as questions " +
                            "with the requested parameters do not exist", Toast.LENGTH_SHORT).show();
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

            case R.id.regenerate_exam:

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
                Snackbar.make(findViewById(R.id.displayExamCoordinator), "Exam Generated Successfully", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }


    public void printExam(Exam _exam){

        //Constructing Filename generation with DDMMYYYYhhmmss format, e.g: 03051993235312
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");

        String courseName = examDBHandler.getCourseById(_exam.get_course()).get_name();

        String pdfName = courseName + "_EXAM_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

        try {
            //Step 1- Create file in Internal Storage...

            File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), pdfName);
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
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Toast.makeText(DisplayExam.this, "File saved and is available", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(DisplayExam.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Document prepareDocument(Document _document){


        //Adding Header Attributes...
        _document.addAuthor(examDBHandler.getTeacherName(teacherId, this));
        _document.addCreationDate();
        _document.addProducer();
        _document.addCreator("Exam Generator");
        _document.addTitle("Dummy test page");
        _document.setPageSize(PageSize.A4);
        // left,right,top,bottom
        _document.setMargins(36, 36, 36, 36);
        _document.setMarginMirroring(true);


        //Adding an Anchor...
        Anchor anchor = new Anchor();
        anchor.setName("First Chapter");
        BaseFont baseFont;

        try {
             baseFont = BaseFont.createFont("assets/FreeSans/FreeSans.ttf", "UTF-8", BaseFont.EMBEDDED);
        }catch (Exception e){
            baseFont = null;
        }
        Font font = new Font(baseFont, 12);
        //Font font = FontFactory.getFont("assets/DroidKufi/DroidKufi-Regular.ttf", "Cp1250", BaseFont.EMBEDDED);
        //Font font = FontFactory.getFont("assets/FreeSans/FreeSans.ttf", "Cp1250", BaseFont.EMBEDDED);
        font.setSize(24);
        font.setColor(BaseColor.BLACK);
        font.setStyle(Font.NORMAL);
        //font.setFamily("Droid Arabic Kufi");


        //Adding a List...
        List list = new List();

        Paragraph p = new Paragraph(exam.get_questionList().firstElement().get_text());
        Paragraph p1 = new Paragraph("Just testing whether or not I'm doing it wrong");
        p.setFont(font);
        p1.setFont(font);



        try {
            _document.add(anchor);
            _document.add(p);
            _document.add(p1);
        }catch (DocumentException d){
            Toast.makeText(DisplayExam.this, d.toString(), Toast.LENGTH_SHORT).show();
        }



        return _document;
    }

}
