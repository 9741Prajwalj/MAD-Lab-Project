package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class AdventureGameActivity extends AppCompatActivity {
    private TextView storyText;
    private MaterialButton choice1, choice2;
    private ImageView storyImage;
    private int storyState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        storyText = findViewById(R.id.story_text);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        storyImage = findViewById(R.id.story_image);

        updateStory();

        choice1.setOnClickListener(v -> {
            if (storyState == 1 || storyState == 2) storyState = 3;
            else if (storyState == 3) storyState = 5;
            updateStory();
        });

        choice2.setOnClickListener(v -> {
            if (storyState == 1) storyState = 2;
            else if (storyState == 2 || storyState == 3) storyState = 4;
            updateStory();
        });
    }

    private void updateStory() {
        switch (storyState) {
            case 1:
                storyImage.setImageResource(R.drawable.ic_adventure1);
                storyText.setText("You're in a forest. Do you go left or right?");
                choice1.setText("Go left");
                choice2.setText("Go right");
                break;
            case 2:
                storyImage.setImageResource(R.drawable.ic_adventure2);
                storyText.setText("You found a cave. Enter or continue?");
                choice1.setText("Enter cave");
                choice2.setText("Continue path");
                break;
            case 3:
                storyImage.setImageResource(R.drawable.ic_adventure3);
                storyText.setText("You found treasure! Take it or leave it?");
                choice1.setText("Take treasure");
                choice2.setText("Leave it");
                break;
            case 4:
                storyImage.setImageResource(R.drawable.ic_adventure4);
                storyText.setText("A bear attacks! Game Over.");
                choice1.setVisibility(View.GONE);
                choice2.setVisibility(View.GONE);
                break;
            case 5:
                storyImage.setImageResource(R.drawable.ic_adventure5);
                storyText.setText("You win! The treasure is yours.");
                choice1.setVisibility(View.GONE);
                choice2.setVisibility(View.GONE);
                break;
        }
    }
}