package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;

public class PuzzleGameActivity extends AppCompatActivity {

    private GridLayout puzzleGrid;
    private TextView movesCounter;
    private int emptyPosition = 8; // Last position is empty
    private int moveCount = 0;
    private ArrayList<Integer> tileNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        puzzleGrid = findViewById(R.id.puzzle_grid);
        movesCounter = findViewById(R.id.moves_counter);

        initializeGame();
    }

    private void initializeGame() {
        // Reset counters
        moveCount = 0;
        movesCounter.setText("Moves: 0");

        // Create tiles (1-8 + empty)
        tileNumbers.clear();
        for (int i = 1; i <= 8; i++) {
            tileNumbers.add(i);
        }
        tileNumbers.add(0); // 0 represents empty space

        // Shuffle tiles
        Collections.shuffle(tileNumbers);

        // Update UI
        updateTiles();
    }

    private void updateTiles() {
        puzzleGrid.removeAllViews();

        for (int i = 0; i < tileNumbers.size(); i++) {
            int number = tileNumbers.get(i);

            if (number == 0) {
                emptyPosition = i;
                continue; // Skip empty space
            }

            com.google.android.material.button.MaterialButton tile =
                    new com.google.android.material.button.MaterialButton(this);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels / 3 - 32;
            params.height = params.width;
            params.setMargins(8, 8, 8, 8);

            tile.setLayoutParams(params);
            tile.setText(String.valueOf(number));
            tile.setTextSize(24);
            tile.setCornerRadius(8);

            final int position = i;
            tile.setOnClickListener(v -> moveTile(position));

            puzzleGrid.addView(tile);
        }
    }

    private void moveTile(int position) {
        // Check if tile can move (adjacent to empty space)
        if (isAdjacent(position, emptyPosition)) {
            // Swap tiles
            Collections.swap(tileNumbers, position, emptyPosition);
            emptyPosition = position;
            moveCount++;
            movesCounter.setText("Moves: " + moveCount);
            updateTiles();

            // Check if puzzle is solved
            if (isSolved()) {
                Snackbar.make(puzzleGrid, "Puzzle solved in " + moveCount + " moves!",
                                Snackbar.LENGTH_LONG)
                        .setAction("Play Again", v -> initializeGame())
                        .show();
            }
        }
    }

    private boolean isAdjacent(int a, int b) {
        // Same row and adjacent columns
        if (a/3 == b/3 && Math.abs(a - b) == 1) return true;
        // Same column and adjacent rows
        if (a%3 == b%3 && Math.abs(a - b) == 3) return true;
        return false;
    }

    private boolean isSolved() {
        for (int i = 0; i < tileNumbers.size() - 1; i++) {
            if (tileNumbers.get(i) != i + 1) {
                return false;
            }
        }
        return tileNumbers.get(8) == 0; // Last position should be empty
    }
}