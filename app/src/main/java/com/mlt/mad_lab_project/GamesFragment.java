package com.mlt.mad_lab_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class GamesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        // Game 1
        MaterialCardView game1Card = view.findViewById(R.id.game1_card);
        game1Card.setOnClickListener(v -> showToast("Puzzle Game"));

        // Game 2
        MaterialCardView game2Card = view.findViewById(R.id.game2_card);
        game2Card.setOnClickListener(v -> showToast("Memory Game"));

        // Game 3
        MaterialCardView game3Card = view.findViewById(R.id.game3_card);
        game3Card.setOnClickListener(v -> showToast("Trivia Quiz"));

        // Game 4
        MaterialCardView game4Card = view.findViewById(R.id.game4_card);
        game4Card.setOnClickListener(v -> showToast("Word Search"));

        // Game 5
        MaterialCardView game5Card = view.findViewById(R.id.game5_card);
        game5Card.setOnClickListener(v -> showToast("Sudoku"));

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message + " clicked!", Toast.LENGTH_SHORT).show();
    }
}