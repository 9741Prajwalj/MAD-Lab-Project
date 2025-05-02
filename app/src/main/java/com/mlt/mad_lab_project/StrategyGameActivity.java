package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class StrategyGameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private int currentPlayer = 1; // 1 for X, 2 for O
    private int[] gameState = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int[][] winningPositions = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rows
            {0,3,6}, {1,4,7}, {2,5,8}, // columns
            {0,4,8}, {2,4,6}           // diagonals
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        gridLayout = findViewById(R.id.game_grid);
        initializeGame();
    }

    private void initializeGame() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView cell = (ImageView) gridLayout.getChildAt(i);
            cell.setImageResource(0);
            int finalI = i;
            cell.setOnClickListener(v -> playMove((ImageView) v, finalI));
        }
        currentPlayer = 1;
        gameState = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    private void playMove(ImageView cell, int position) {
        if (gameState[position] == 0) {
            gameState[position] = currentPlayer;
            cell.setImageResource(currentPlayer == 1 ? R.drawable.ic_x : R.drawable.ic_o);

            if (checkWinner()) {
                String winner = currentPlayer == 1 ? "X" : "O";
                Toast.makeText(this, winner + " wins!", Toast.LENGTH_SHORT).show();
                initializeGame();
            } else if (isBoardFull()) {
                Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
                initializeGame();
            } else {
                currentPlayer = currentPlayer == 1 ? 2 : 1;
            }
        }
    }

    private boolean checkWinner() {
        for (int[] winningPosition : winningPositions) {
            if (gameState[winningPosition[0]] != 0 &&
                    gameState[winningPosition[0]] == gameState[winningPosition[1]] &&
                    gameState[winningPosition[1]] == gameState[winningPosition[2]]) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int state : gameState) {
            if (state == 0) return false;
        }
        return true;
    }
}