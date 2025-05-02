package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Collections;
import java.util.ArrayList;

public class MemoryGameActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private int[] cardImages = {R.drawable.ic_card1, R.drawable.ic_card2, R.drawable.ic_card3,
            R.drawable.ic_card4, R.drawable.ic_card5, R.drawable.ic_card6};
    private ArrayList<Integer> cards;
    private ImageView firstCard, secondCard;
    private int firstCardPosition, pairsFound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        gridLayout = findViewById(R.id.memory_grid);
        initializeGame();
    }

    private void initializeGame() {
        // Create pairs of cards
        cards = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            cards.add(cardImages[i]);
            cards.add(cardImages[i]);
        }
        Collections.shuffle(cards);

        // Setup grid
        gridLayout.removeAllViews();
        for (int i = 0; i < cards.size(); i++) {
            ImageView card = new ImageView(this);
            card.setImageResource(R.drawable.ic_card_back);
            card.setTag(cards.get(i));
            card.setScaleType(ImageView.ScaleType.CENTER_CROP);
            card.setPadding(8, 8, 8, 8);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels / 4;
            params.height = params.width;
            params.setMargins(4, 4, 4, 4);

            card.setLayoutParams(params);
            int finalI = i;
            card.setOnClickListener(v -> flipCard((ImageView) v, finalI));
            gridLayout.addView(card);
        }
    }

    private void flipCard(ImageView card, int position) {
        if (card.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_card_back).getConstantState()) {
            card.setImageResource((Integer) card.getTag());

            if (firstCard == null) {
                firstCard = card;
                firstCardPosition = position;
            } else {
                secondCard = card;
                checkForMatch();
            }
        }
    }

    private void checkForMatch() {
        if (firstCard.getTag().equals(secondCard.getTag())) {
            firstCard.setEnabled(false);
            secondCard.setEnabled(false);
            pairsFound++;

            if (pairsFound == 6) {
                Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
            }
        } else {
            new android.os.Handler().postDelayed(() -> {
                firstCard.setImageResource(R.drawable.ic_card_back);
                secondCard.setImageResource(R.drawable.ic_card_back);
            }, 500);
        }
        firstCard = null;
        secondCard = null;
    }
}