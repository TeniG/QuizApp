package com.teni.myappquiz;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE="extra_score";
    public static final long COUNT_DOWN_IN_MILLIS=30000;
    private TextView tv_score;
    private TextView tv_quest_count;
    private TextView tv_quest_timer;
    private TextView tv_question;
    private TextView tv_question_answer;
    private RadioGroup rv_group;
    private RadioButton rd_btn1;
    private RadioButton rd_btn2;
    private RadioButton rd_btn3;
    private Button btn_confirm;
    private List<Questions> questionsList;

    private Questions currentQuestion;
    private int totalScore;
    private int currentQuestionCount=0;
    private int totalQuestionCount;
    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;
    private Boolean answer;
    private long backpressed;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tv_score = findViewById(R.id.tv_score);
        tv_quest_count = findViewById(R.id.tv_quest_number);
        tv_quest_timer = findViewById(R.id.tv_quest_timer);
        tv_question = findViewById(R.id.tv_question);
        tv_question_answer = findViewById(R.id.tv_quest_answer);
        rv_group = findViewById(R.id.rv_group);
        rd_btn1 = findViewById(R.id.rd_btn1);
        rd_btn2 = findViewById(R.id.rd_btn2);
        rd_btn3 = findViewById(R.id.rd_btn3);
        btn_confirm=findViewById(R.id.btn_confirm);

        final QuizDbHelper quizDbHelper = new QuizDbHelper(this);
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            public void run() {
                questionsList = quizDbHelper.getAllQuestions();
//            }
//        });

//
        totalQuestionCount = questionsList.size();
        Collections.shuffle(questionsList);

        textColorDefaultRb = rd_btn1.getTextColors();
        textColorDefaultCd=tv_quest_timer.getTextColors();

        showQuestion();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!answer) {
                    if (rd_btn1.isChecked() || rd_btn2.isChecked() || rd_btn3.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(QuizActivity.this, "Please select an Answer", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    showQuestion();
                }
            }
        });
    }

    private void showQuestion() {
        tv_question_answer.setText("");
        rd_btn1.setTextColor(textColorDefaultRb);
        rd_btn2.setTextColor(textColorDefaultRb);
        rd_btn3.setTextColor(textColorDefaultRb);
        rv_group.clearCheck();

        if(currentQuestionCount<totalQuestionCount)
        {
            currentQuestion=questionsList.get(currentQuestionCount);
            tv_question.setText(currentQuestion.getQuestions());
            rd_btn1.setText(currentQuestion.getOption1());
            rd_btn2.setText(currentQuestion.getOption2());
            rd_btn3.setText(currentQuestion.getOption3());

            currentQuestionCount++;
            tv_quest_count.setText("Questions "+currentQuestionCount +"/"+totalQuestionCount);
            answer=false;

            btn_confirm.setText("Confirm");

            timeLeftInMillis=COUNT_DOWN_IN_MILLIS;
            startCountDown();
        }
        else {
            finishQuiz();

        }
    }

    private void startCountDown()
    {
        countDownTimer=new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis=millisUntilFinished;
                updateCountDownTimeText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis=0;
                updateCountDownTimeText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownTimeText()
    {
        int minutes=(int)(timeLeftInMillis/1000)/60;
        int seconds=(int)(timeLeftInMillis/1000)%60;
        String currentTime=String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tv_quest_timer.setText(currentTime);
        if(timeLeftInMillis<10000)
        {
            tv_quest_timer.setTextColor(Color.RED);
        }
        else {
            tv_quest_timer.setTextColor(textColorDefaultCd);
        }
    }
    private void checkAnswer()
    {
        answer=true;
        countDownTimer.cancel();
        RadioButton rbSelected=findViewById(rv_group.getCheckedRadioButtonId());
        int answerSelceted=rv_group.indexOfChild(rbSelected)+1;
        if(answerSelceted==currentQuestion.getAnswerNumber())
        {
            totalScore++;
            tv_score.setText("Score :"+String.valueOf(totalScore));
        }

        showSolution();
    }

    private void showSolution() {
        rd_btn1.setTextColor(Color.RED);
        rd_btn2.setTextColor(Color.RED);
        rd_btn3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNumber())
        {
            case 1:
                rd_btn1.setTextColor(Color.GREEN);
                tv_question_answer.setText("Option1 is Correct");
                break;
            case 2:
                rd_btn2.setTextColor(Color.GREEN);
                tv_question_answer.setText("Option2 is Correct");
                break;
            case 3:
                rd_btn3.setTextColor(Color.GREEN);
                tv_question_answer.setText("Option3 is Correct");
                break;
        }

        if(currentQuestionCount<totalQuestionCount)
        {
            btn_confirm.setText("NEXT");
        }
        else {
            btn_confirm.setText("FINISH");
        }
    }

    private void finishQuiz()
    {
        Intent resultIntent=new Intent();
        resultIntent.putExtra(EXTRA_SCORE,totalScore);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(backpressed+2000>System.currentTimeMillis())
        {
            finishQuiz();
        }
        else {
            Toast.makeText(this, "Please click again to finish", Toast.LENGTH_SHORT).show();
        }
        backpressed=System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }
    }
}
