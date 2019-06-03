package com.teni.myappquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_start_quiz;
    private static final int REQUEST_CODE_QUIZ=1;
    private static final String KEY_HIGH_SCORE="key_highscore";
    private static final String SHARED_PREFS="shared_prefs";

    private TextView tv_highscrore;
    private int highscore=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_highscrore=findViewById(R.id.tv_high_score);
        btn_start_quiz=findViewById(R.id.btn_start_quiz);

        loadHighScore();

        btn_start_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void startQuiz() {
        Toast.makeText(this, "strat quiz", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,QuizActivity.class);
        startActivityForResult(intent,REQUEST_CODE_QUIZ);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==REQUEST_CODE_QUIZ) {
            if(resultCode==RESULT_OK){
                int score=data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if(score>highscore)
                {
                    updateHighScore(score);
                }
            }
        }
    }

    private void updateHighScore(int highscoreNew)
    {
        highscore=highscoreNew;
        tv_highscrore.setText("HighestScore :"+highscore);
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(KEY_HIGH_SCORE,highscore);
        editor.apply();
    }

    private void loadHighScore()
    {
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        highscore=sharedPreferences.getInt(KEY_HIGH_SCORE,0);
        tv_highscrore.setText("HighestScore :"+highscore);
    }
}
