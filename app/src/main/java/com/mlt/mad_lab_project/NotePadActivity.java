package com.mlt.mad_lab_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotePadActivity extends AppCompatActivity {

    private EditText noteEditText;
    private LinearLayout notesContainer;
    private static final String FILE_NAME = "notes.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        noteEditText = findViewById(R.id.noteEditText);
        notesContainer = findViewById(R.id.notesContainer);
        Button saveButton = findViewById(R.id.saveButton);
        Button clearButton = findViewById(R.id.clearButton);

        saveButton.setOnClickListener(v -> saveNote());
        clearButton.setOnClickListener(v -> clearNote());

        loadNotes();
    }

    private void saveNote() {
        String noteText = noteEditText.getText().toString().trim();
        if (!noteText.isEmpty()) {
            try {
                // Save to file
                FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND);
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());
                String entry = timestamp + "|" + noteText + "\n";
                fos.write(entry.getBytes());
                fos.close();

                // Add to UI
                addNoteToView(timestamp, noteText);
                noteEditText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearNote() {
        noteEditText.setText("");
    }

    private void loadNotes() {
        notesContainer.removeAllViews(); // Clear existing views first
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    addNoteToView(parts[0], parts[1]);
                }
            }

            br.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNoteToView(String timestamp, String note) {
        View noteView = getLayoutInflater().inflate(R.layout.note_item, null);

        TextView timestampView = noteView.findViewById(R.id.timestamp);
        TextView noteContentView = noteView.findViewById(R.id.noteContent);
        ImageButton deleteButton = noteView.findViewById(R.id.deleteButton);

        timestampView.setText(timestamp);
        noteContentView.setText(note);

        // Store the full note data as a tag
        noteView.setTag(timestamp + "|" + note);

        deleteButton.setOnClickListener(v -> deleteNote(noteView));

        notesContainer.addView(noteView, 0); // Add at the top
    }

    private void deleteNote(View noteView) {
        String noteData = (String) noteView.getTag();
        if (noteData != null) {
            // Remove from UI
            notesContainer.removeView(noteView);

            // Remove from file
            try {
                // Read all notes
                FileInputStream fis = openFileInput(FILE_NAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                StringBuilder fileContent = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    if (!line.equals(noteData)) {
                        fileContent.append(line).append("\n");
                    }
                }

                br.close();
                isr.close();
                fis.close();

                // Write back all notes except the deleted one
                FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write(fileContent.toString().getBytes());
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error deleting note", Toast.LENGTH_SHORT).show();
            }
        }
    }
}