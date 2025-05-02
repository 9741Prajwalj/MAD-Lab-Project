package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class TriviaGameActivity extends AppCompatActivity {
    private TextView questionText;
    private Button[] optionButtons = new Button[4];
    private String[][] questions = {
            {"What is 2+2?", "4", "3", "5", "6"},
            {"Capital of France?", "Paris", "London", "Berlin", "Madrid"},
            {"Largest planet?", "Jupiter", "Earth", "Mars", "Saturn"}
    };
    private int currentQuestion = 0, score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        questionText = findViewById(R.id.question_text);
        optionButtons[0] = findViewById(R.id.option1);
        optionButtons[1] = findViewById(R.id.option2);
        optionButtons[2] = findViewById(R.id.option3);
        optionButtons[3] = findViewById(R.id.option4);

        loadQuestion();

        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            optionButtons[i].setOnClickListener(v -> checkAnswer(finalI));
        }
    }

    private void loadQuestion() {
        if (currentQuestion < questions.length) {
            questionText.setText(questions[currentQuestion][0]);
            for (int i = 0; i < 4; i++) {
                optionButtons[i].setText(questions[currentQuestion][i+1]);
            }
        } else {
            Toast.makeText(this, "Quiz Over! Score: " + score, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void checkAnswer(int selectedOption) {
        if (questions[currentQuestion][1].equals(optionButtons[selectedOption].getText())) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
        currentQuestion++;
        loadQuestion();
    }
}