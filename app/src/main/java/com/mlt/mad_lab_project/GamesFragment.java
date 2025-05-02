package com.mlt.mad_lab_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class GamesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        // Initialize game cards
        MaterialCardView puzzleCard = view.findViewById(R.id.game1_card);
        MaterialCardView memoryCard = view.findViewById(R.id.game2_card);
        MaterialCardView triviaCard = view.findViewById(R.id.game3_card);
        MaterialCardView adventureCard = view.findViewById(R.id.game4_card);
        MaterialCardView strategyCard = view.findViewById(R.id.game5_card);

        // Set click listeners
        puzzleCard.setOnClickListener(v -> launchGame("Puzzle Game"));
        memoryCard.setOnClickListener(v -> launchGame("Memory Game"));
        triviaCard.setOnClickListener(v -> launchGame("Trivia Game"));
        adventureCard.setOnClickListener(v -> launchGame("Adventure Game"));
        strategyCard.setOnClickListener(v -> launchGame("Strategy Game"));

        return view;
    }

    private void launchGame(String gameName) {
        Intent intent;
        switch (gameName) {
            case "Puzzle Game":
                intent = new Intent(getActivity(), PuzzleGameActivity.class);
                break;
            case "Memory Game":
                intent = new Intent(getActivity(), MemoryGameActivity.class);
                break;
            case "Trivia Game":
                intent = new Intent(getActivity(), TriviaGameActivity.class);
                break;
            case "Adventure Game":
                intent = new Intent(getActivity(), AdventureGameActivity.class);
                break;
            case "Strategy Game":
                intent = new Intent(getActivity(), StrategyGameActivity.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}