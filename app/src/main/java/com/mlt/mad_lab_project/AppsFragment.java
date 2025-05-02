package com.mlt.mad_lab_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class AppsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        // Calculator Card
        MaterialCardView calculatorCard = view.findViewById(R.id.calculator_card);
        calculatorCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), CalculatorActivity.class)));

        // Location Card
        MaterialCardView locationCard = view.findViewById(R.id.location_card);
        locationCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), LocationActivity.class)));

        // Music Card
        MaterialCardView musicCard = view.findViewById(R.id.music_card);
        musicCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), MusicActivity.class)));

        // Camera Card
        MaterialCardView cameraCard = view.findViewById(R.id.camera_card);
        cameraCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), CameraActivity.class)));

        // Gallery Card
        MaterialCardView galleryCard = view.findViewById(R.id.gallery_card);
        galleryCard.setOnClickListener(v -> startActivity(new Intent(getActivity(), GalleryActivity.class)));

        return view;
    }
}