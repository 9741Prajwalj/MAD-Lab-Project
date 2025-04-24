package com.mlt.mad_lab_project;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicActivity extends AppCompatActivity implements SongAdapter.OnItemClickListener {

    private ArrayList<Song> songList;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay, btnNext, btnPrevious;
    private SeekBar seekBar;
    private TextView songTitle, artistName, currentTime, totalTime;
    private Handler handler = new Handler();
    private int currentPosition = 0;
    private boolean isPlaying = false;
    private ImageButton btnToggleList;
    private boolean isListVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Initialize views
        initializeViews();

        // Initialize song list with raw resources
        initializeSongList();

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize media player
        mediaPlayer = new MediaPlayer();

        // Set click listeners
        setClickListeners();

        // Update seek bar periodically
        handler.postDelayed(updateSeekBar, 100);

        btnToggleList = findViewById(R.id.btnToggleList);

        // Set click listener for toggle button
        btnToggleList.setOnClickListener(v -> toggleSongList());
    }

    private void initializeViews() {
        btnPlay = findViewById(R.id.btnPlay);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        seekBar = findViewById(R.id.songProgress);
        songTitle = findViewById(R.id.songTitle);
        artistName = findViewById(R.id.artistName);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        recyclerView = findViewById(R.id.songList);
    }

    private void initializeSongList() {
        songList = new ArrayList<>();

        for (int i = 1; i <= 50; i++) {
            try {
                String songName = "song" + i;
                int resId = getResources().getIdentifier(songName, "raw", getPackageName());

                if (resId != 0) {
                    songList.add(new Song("Song " + i, "Artist " + ((i % 10) + 1), resId));
                } else {
                    Log.e("MusicPlayer", "Resource not found for: " + songName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void toggleSongList() {
        RecyclerView songList = findViewById(R.id.songList);
        TransitionManager.beginDelayedTransition((ViewGroup) songList.getParent());

        if (isListVisible) {
            // Collapse the list
            songList.setVisibility(View.GONE);
            btnToggleList.setImageResource(R.drawable.ic_expand_more);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) songList.getLayoutParams();
            params.height = 0;
            songList.setLayoutParams(params);
        } else {
            // Expand the list
            songList.setVisibility(View.VISIBLE);
            btnToggleList.setImageResource(R.drawable.ic_expand_less);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) songList.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            songList.setLayoutParams(params);
        }
        isListVisible = !isListVisible;
    }
    private void setupRecyclerView() {
        songAdapter = new SongAdapter(songList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songAdapter);
    }

    private void setClickListeners() {
        btnPlay.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            } else {
                playMusic(currentPosition);
            }
        });

        btnNext.setOnClickListener(v -> playNextSong());
        btnPrevious.setOnClickListener(v -> playPreviousSong());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(formatTime(progress));
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void playMusic(int position) {
        try {
            mediaPlayer.reset();
            currentPosition = position;

            // Set data source from raw resource
            mediaPlayer = MediaPlayer.create(this, songList.get(position).getRawResourceId());

            // Set up prepared listener to get actual duration
            mediaPlayer.setOnPreparedListener(mp -> {
                // Update duration UI when media is prepared
                int duration = mp.getDuration();
                totalTime.setText(formatTime(duration));
                seekBar.setMax(duration);

                // Start playback
                mp.start();
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.ic_pause);

                // Update UI with current song info
                Song currentSong = songList.get(position);
                songTitle.setText(currentSong.getTitle());
                artistName.setText(currentSong.getArtist());

                // Highlight current song in list
                songAdapter.setSelectedPosition(position);
                songAdapter.notifyDataSetChanged();
            });

            // When song completes, play next
            mediaPlayer.setOnCompletionListener(mp -> playNextSong());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void playNextSong() {
        currentPosition = (currentPosition + 1) % songList.size();
        playMusic(currentPosition);
    }

    private void playPreviousSong() {
        currentPosition = (currentPosition - 1 + songList.size()) % songList.size();
        playMusic(currentPosition);
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && isPlaying) {
                int currentPos = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(currentPos);
                currentTime.setText(formatTime(currentPos));
            }
            handler.postDelayed(this, 100);
        }
    };

    private String formatTime(int milliseconds) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        );
    }

    @Override
    public void onItemClick(int position) {
        currentPosition = position;
        playMusic(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }

    // Updated Song model class without duration parameter
    public static class Song {
        private String title;
        private String artist;
        private int rawResourceId;

        public Song(String title, String artist, int rawResourceId) {
            this.title = title;
            this.artist = artist;
            this.rawResourceId = rawResourceId;
        }

        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public int getRawResourceId() { return rawResourceId; }
    }
}