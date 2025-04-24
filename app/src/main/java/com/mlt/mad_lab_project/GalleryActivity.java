package com.mlt.mad_lab_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        RecyclerView recyclerView = findViewById(R.id.galleryRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        List<File> imageFiles = getImageFiles(storageDir);

        if (imageFiles.isEmpty()) {
            Toast.makeText(this, "No photos found", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            recyclerView.setAdapter(new GalleryAdapter(imageFiles));
        }
    }

    private List<File> getImageFiles(File storageDir) {
        List<File> imageFiles = new ArrayList<>();
        if (storageDir.exists()) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().endsWith(".jpg") ||
                            file.getName().toLowerCase().endsWith(".png")) {
                        imageFiles.add(file);
                    }
                }
            }
        }
        return imageFiles;

//        recyclerView.setAdapter(new GalleryAdapter(imageFiles));
    }

    private static class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
        private final List<File> imageFiles;

        public GalleryAdapter(List<File> imageFiles) {
            this.imageFiles = imageFiles;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            File imageFile = imageFiles.get(position);
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            holder.imageView.setImageBitmap(bitmap);

            holder.itemView.setOnClickListener(v -> {
                // Open full screen view
                Intent intent = new Intent(holder.itemView.getContext(), FullScreenImageActivity.class);
                intent.putExtra("image_path", imageFile.getAbsolutePath());
                holder.itemView.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return imageFiles.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageThumbnail);
            }
        }
    }
}