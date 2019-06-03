package com.teni.myappquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.teni.myappquiz.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="MyQuiz.db";
    private static final int DATABASE_VERSION=9;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteDatabase db;
    Context context;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context, "onCreate called", Toast.LENGTH_SHORT).show();
        this.db=db;

        final String SQL_CREATE_QUESTIONS_TABLE="CREATE TABLE "+
                QuestionsTable.TABLE_NAME +"( "+
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                QuestionsTable.COLUMN_QUESTION + " TEXT, "+
                QuestionsTable.COLUMN_OPTION1 + " TEXT, "+
                QuestionsTable.COLUMN_OPTION2 + " TEXT, "+
                QuestionsTable.COLUMN_OPTION3 + " TEXT, "+
                QuestionsTable.COLUMN_ANSWER + " INTEGER"+
                " );";
Log.d("QuizHelper","ttttt "+SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "onUpgrade called", Toast.LENGTH_SHORT).show();
        final String SQL_DROP_QUESTIONS_TABLE=" DROP TABLE IF EXISTS "+QuestionsTable.TABLE_NAME+ ";";
        db.execSQL(SQL_DROP_QUESTIONS_TABLE);
        onCreate(db);
    }

    private void fillQuestionsTable() {
        Questions q1=new Questions("What are Database in Android","MySql","SqlLite","MsAccess",2);
        addQuestions(q1);
        Questions q2=new Questions(" Which of these access specifiers can be used for an interface?","public","private","protected",1);
        addQuestions(q2);
        Questions q3=new Questions(" Which of the following is the correct way of implementing an interface salary by class manager?"," class manager extends salary {}","class manager implements salary {}","class manager imports salary {}",2);
        addQuestions(q3);
        Questions q4=new Questions(" Which of these keywords is used by a class to use an interface defined previously?"," Import","Implements","implements",3);
        addQuestions(q4);
        Questions q5=new Questions("  Which of these can be used to fully abstract a class from its implementation?"," Objects","Interfaces","Packages",2);
        addQuestions(q5);
    }

    private void addQuestions(Questions questions) {
        ContentValues contentValues=new ContentValues();
        contentValues.put(QuestionsTable.COLUMN_QUESTION,questions.getQuestions());
        contentValues.put(QuestionsTable.COLUMN_OPTION1,questions.getOption1());
        contentValues.put(QuestionsTable.COLUMN_OPTION2,questions.getOption2());
        contentValues.put(QuestionsTable.COLUMN_OPTION3,questions.getOption3());
        contentValues.put(QuestionsTable.COLUMN_ANSWER,questions.getAnswerNumber());
        db.insert(QuestionsTable.TABLE_NAME,null,contentValues);
    }

    public List<Questions> getAllQuestions()
    {
        List<Questions> questionsList=new ArrayList<>();
        db=getReadableDatabase();

        Cursor c=db.rawQuery("SELECT * FROM "+QuestionsTable.TABLE_NAME,null);

        if(c.moveToFirst())
        {
            do{
                Questions questions=new Questions();
                questions.setQuestions(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                questions.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                questions.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                questions.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                questions.setAnswerNumber(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER)));
                questionsList.add(questions);
            }
            while (c.moveToNext());
        }
        c.close();
        return questionsList;
    }

}
