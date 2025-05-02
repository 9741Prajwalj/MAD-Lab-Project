package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class StrategyGameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private MaterialButton restartButton;
    private int currentPlayer = 1; // 1 for X, 2 for O
    private int[] gameState = {0, 0, 0, 0, 0, 0, 0, 0, 0}; // 0: empty, 1: X, 2: O
    private boolean gameActive = true;
    private final int[][] winningPositions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_game);

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Initialize grid and restart button
        gridLayout = findViewById(R.id.game_grid);
        restartButton = findViewById(R.id.restart_button);

        // Set up click listeners for grid cells
        initializeGame();

        // Set up restart button listener
        restartButton.setOnClickListener(v -> resetGame());
    }

    private void initializeGame() {
        gameActive = true;
        currentPlayer = 1;
        gameState = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            ImageView cell = (ImageView) gridLayout.getChildAt(i);
            cell.setImageResource(0); // Clear image
            cell.setEnabled(true);
            int position = Integer.parseInt(cell.getTag().toString());
            cell.setOnClickListener(v -> playMove((ImageView) v, position));
        }
    }

    private void playMove(ImageView cell, int position) {
        if (!gameActive || gameState[position] != 0) {
            return;
        }

        // Update game state and UI
        gameState[position] = currentPlayer;
        cell.setImageResource(currentPlayer == 1 ? R.drawable.ic_x : R.drawable.ic_o);
        cell.setEnabled(false);

        // Check for winner or draw
        if (checkWinner()) {
            String winner = currentPlayer == 1 ? "X" : "O";
            Toast.makeText(this, winner + " wins!", Toast.LENGTH_LONG).show();
            gameActive = false;
        } else if (isBoardFull()) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show();
            gameActive = false;
        } else {
            // Switch player
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
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
            if (state == 0) {
                return false;
            }
        }
        return true;
    }

    private void resetGame() {
        initializeGame();
        Toast.makeText(this, "Game restarted!", Toast.LENGTH_SHORT).show();
    }
}