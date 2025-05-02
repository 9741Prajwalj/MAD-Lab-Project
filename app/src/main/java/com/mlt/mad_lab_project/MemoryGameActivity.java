package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView scoreText, timerText;
    private int[] cardImages = {
            R.drawable.ic_card1, R.drawable.ic_card2, R.drawable.ic_card3,
            R.drawable.ic_card4, R.drawable.ic_card5, R.drawable.ic_card6
    };
    private ArrayList<Integer> cards;
    private ImageView firstCard, secondCard;
    private int firstCardPosition, pairsFound = 0, attempts = 0;
    private boolean isBusy = false;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private int seconds = 0;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;
            timerText.setText(String.format("Time: %02d:%02d", seconds/60, seconds%60));
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        gridLayout = findViewById(R.id.memory_grid);
        scoreText = findViewById(R.id.score_text);
        timerText = findViewById(R.id.timer_text);

        initializeGame();
    }

    private void initializeGame() {
        // Reset game state
        pairsFound = 0;
        attempts = 0;
        seconds = 0;
        isBusy = false;
        firstCard = null;
        secondCard = null;

        // Stop previous timer
        timerHandler.removeCallbacks(timerRunnable);

        // Update UI
        scoreText.setText("Matches: 0/6");
        timerText.setText("Time: 00:00");

        // Create pairs of cards
        cards = new ArrayList<>();
        for (int image : cardImages) {
            cards.add(image);
            cards.add(image);
        }
        Collections.shuffle(cards);

        // Setup grid
        gridLayout.removeAllViews();
        int columnCount = 3;
        gridLayout.setColumnCount(columnCount);
        int size = (getResources().getDisplayMetrics().widthPixels - 32) / columnCount;

        for (int i = 0; i < cards.size(); i++) {
            ImageView card = new ImageView(this);
            card.setImageResource(R.drawable.ic_card_back);
            card.setTag(R.id.card_front, cards.get(i)); // Store front image ID
            card.setTag(R.id.card_position, i); // Store position
            card.setScaleType(ImageView.ScaleType.FIT_CENTER);
            card.setAdjustViewBounds(true);
            card.setPadding(8, 8, 8, 8);
            card.setImageTintList(null);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = size;
            params.height = size;
            params.setMargins(4, 4, 4, 4);
            card.setLayoutParams(params);

            card.setOnClickListener(v -> {
                if (!isBusy) {
                    flipCard((ImageView) v);
                }
            });
            gridLayout.addView(card);
        }

        // Start timer
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void flipCard(ImageView card) {
        if (isBusy || card == firstCard || !card.isEnabled()) return;

        // Check if card is showing back
        boolean isBack = card.getTag(R.id.card_state) == null || card.getTag(R.id.card_state).equals("back");
        if (!isBack) return;

        // Flip to front
        card.setTag(R.id.card_state, "front");
        card.animate()
                .rotationY(90)
                .setDuration(150)
                .withEndAction(() -> {
                    card.setImageResource((Integer) card.getTag(R.id.card_front));
                    card.setRotationY(-90);
                    card.animate()
                            .rotationY(0)
                            .setDuration(150)
                            .start();

                    // Handle game logic
                    if (firstCard == null) {
                        firstCard = card;
                        firstCardPosition = (Integer) card.getTag(R.id.card_position);
                    } else {
                        secondCard = card;
                        attempts++;
                        isBusy = true;
                        checkForMatch();
                    }
                })
                .start();
    }

    private void checkForMatch() {
        Integer firstImage = (Integer) firstCard.getTag(R.id.card_front);
        Integer secondImage = (Integer) secondCard.getTag(R.id.card_front);

        if (firstImage.equals(secondImage)) {
            // Match found
            firstCard.setEnabled(false);
            secondCard.setEnabled(false);
            pairsFound++;
            scoreText.setText(String.format("Matches: %d/6", pairsFound));

            if (pairsFound == 6) {
                timerHandler.removeCallbacks(timerRunnable);
                showWinMessage();
            }
            resetTurn();
        } else {
            // Flip back
            timerHandler.postDelayed(() -> {
                firstCard.setTag(R.id.card_state, "back");
                secondCard.setTag(R.id.card_state, "back");

                firstCard.animate()
                        .rotationY(90)
                        .setDuration(150)
                        .withEndAction(() -> {
                            firstCard.setImageResource(R.drawable.ic_card_back);
                            firstCard.setRotationY(-90);
                            firstCard.animate()
                                    .rotationY(0)
                                    .setDuration(150)
                                    .start();
                        })
                        .start();

                secondCard.animate()
                        .rotationY(90)
                        .setDuration(150)
                        .withEndAction(() -> {
                            secondCard.setImageResource(R.drawable.ic_card_back);
                            secondCard.setRotationY(-90);
                            secondCard.animate()
                                    .rotationY(0)
                                    .setDuration(150)
                                    .withEndAction(() -> resetTurn())
                                    .start();
                        })
                        .start();
            }, 500); // Delay to show mismatch
        }
    }

    private void resetTurn() {
        firstCard = null;
        secondCard = null;
        isBusy = false;
    }

    private void showWinMessage() {
        Snackbar.make(gridLayout,
                        String.format("You won in %d attempts and %02d:%02d!", attempts, seconds/60, seconds%60),
                        Snackbar.LENGTH_INDEFINITE)
                .setAction("Play Again", v -> initializeGame())
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }
}