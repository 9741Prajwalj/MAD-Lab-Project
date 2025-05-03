package com.mlt.mad_lab_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGameActivity extends AppCompatActivity {
    private GameView gameView;
    private FrameLayout gameSurfaceContainer;
    private TextView scoreText;
    private RelativeLayout gameOverLayout;
    private TextView finalScoreText;
    private Button restartButton;
    private ImageButton pauseButton;
    private RelativeLayout pauseLayout;
    private ImageButton upButton, downButton, leftButton, rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_game_layout);

        gameSurfaceContainer = findViewById(R.id.gameSurfaceContainer);
        scoreText = findViewById(R.id.scoreText);
        gameOverLayout = findViewById(R.id.gameOverLayout);
        finalScoreText = findViewById(R.id.finalScoreText);
        restartButton = findViewById(R.id.restartButton);
        pauseButton = findViewById(R.id.pauseButton);
        pauseLayout = findViewById(R.id.pauseLayout);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);

        gameView = new GameView(this);
        gameSurfaceContainer.addView(gameView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        upButton.setOnClickListener(v -> gameView.setDirection('U'));
        downButton.setOnClickListener(v -> gameView.setDirection('D'));
        leftButton.setOnClickListener(v -> gameView.setDirection('L'));
        rightButton.setOnClickListener(v -> gameView.setDirection('R'));

        pauseButton.setOnClickListener(v -> {
            gameView.togglePause();
            pauseLayout.setVisibility(gameView.isPaused() ? View.VISIBLE : View.GONE);
            pauseButton.setImageResource(gameView.isPaused() ? R.drawable.ic_play : R.drawable.ic_pause);
        });

        restartButton.setOnClickListener(v -> {
            gameView.resetGame();
            gameOverLayout.setVisibility(View.GONE);
            pauseButton.setImageResource(R.drawable.ic_pause);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    private class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
        private Thread gameThread;
        private volatile boolean running;
        private volatile boolean paused;
        private ArrayList<Point> snake;
        private Point food;
        private char direction;
        private char pendingDirection;
        private int score;
        private int tileSize = 40;
        private int speed = 150;
        private final int initialSpeed = 150;
        private final int speedIncrement = 10;
        private final int maxSpeed = 50;
        private Paint snakePaint;
        private Paint foodPaint;
        private final Random random;
        private boolean foodEaten;
        private final int backgroundColor = Color.BLACK;

        public GameView(Context context) {
            super(context);
            getHolder().addCallback(this);
            setFocusable(true);
            random = new Random();

            snakePaint = new Paint();
            snakePaint.setStyle(Paint.Style.FILL);
            snakePaint.setAntiAlias(true);

            foodPaint = new Paint();
            foodPaint.setStyle(Paint.Style.FILL);
            foodPaint.setAntiAlias(true);
            foodPaint.setColor(Color.RED);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initializeGame();
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void initializeGame() {
            snake = new ArrayList<>();
            direction = 'R';
            pendingDirection = 'R';
            score = 0;
            speed = initialSpeed;
            paused = false;
            foodEaten = false;

            int startX = (getWidth() / tileSize / 2) * tileSize;
            int startY = (getHeight() / tileSize / 2) * tileSize;
            for (int i = 0; i < 3; i++) {
                snake.add(new Point(startX - i * tileSize, startY));
            }

            spawnFood();
            updateScore();
        }

        @Override
        public void run() {
            long lastTime = System.nanoTime();
            double nsPerTick = 1000000000.0 / (1000.0 / speed);
            double delta = 0;

            while (running) {
                if (!paused) {
                    long now = System.nanoTime();
                    delta += (now - lastTime) / nsPerTick;
                    lastTime = now;

                    while (delta >= 1) {
                        gameLoop();
                        delta--;
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void gameLoop() {
            direction = pendingDirection;

            Point newHead = new Point(snake.get(0));
            switch (direction) {
                case 'U': newHead.y -= tileSize; break;
                case 'D': newHead.y += tileSize; break;
                case 'L': newHead.x -= tileSize; break;
                case 'R': newHead.x += tileSize; break;
            }

            // Check if food is eaten before move
            if (newHead.equals(food)) {
                foodEaten = true;
                score++;
                if (score % 3 == 0 && speed > maxSpeed) {
                    speed = Math.max(maxSpeed, speed - speedIncrement);
                }
                spawnFood();
                updateScore();
            } else {
                foodEaten = false;
            }

            move(newHead);
            checkCollision();
            draw();
        }

        private void move(Point newHead) {
            snake.add(0, newHead);
            if (!foodEaten) {
                snake.remove(snake.size() - 1);
            }
        }

        private void checkCollision() {
            Point head = snake.get(0);

            if (head.x < 0 || head.x >= getWidth() || head.y < 0 || head.y >= getHeight()) {
                gameOver();
                return;
            }

            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver();
                    return;
                }
            }
        }

        private void spawnFood() {
            int maxX = getWidth() / tileSize;
            int maxY = getHeight() / tileSize;
            ArrayList<Point> possiblePositions = new ArrayList<>();

            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    Point pos = new Point(x * tileSize, y * tileSize);
                    boolean isSnake = false;
                    for (Point segment : snake) {
                        if (segment.equals(pos)) {
                            isSnake = true;
                            break;
                        }
                    }
                    if (!isSnake) {
                        possiblePositions.add(pos);
                    }
                }
            }

            if (!possiblePositions.isEmpty()) {
                food = possiblePositions.get(random.nextInt(possiblePositions.size()));
            } else {
                gameOver();
            }
        }

        private void draw() {
            Canvas canvas = getHolder().lockCanvas();
            if (canvas == null) return;

            canvas.drawColor(backgroundColor);

            LinearGradient snakeGradient = new LinearGradient(
                    0, 0, tileSize, tileSize,
                    Color.GREEN, Color.rgb(50, 50, 50),
                    Shader.TileMode.CLAMP
            );
            snakePaint.setShader(snakeGradient);

            for (Point segment : snake) {
                canvas.drawRoundRect(
                        segment.x, segment.y,
                        segment.x + tileSize, segment.y + tileSize,
                        10, 10, snakePaint
                );
            }

            canvas.drawCircle(
                    food.x + tileSize / 2f,
                    food.y + tileSize / 2f,
                    tileSize / 2f,
                    foodPaint
            );

            getHolder().unlockCanvasAndPost(canvas);
        }

        private void updateScore() {
            runOnUiThread(() -> {
                scoreText.setText("Score: " + score);
                finalScoreText.setText("Score: " + score);
            });
        }

        private void gameOver() {
            running = false;
            paused = true;
            runOnUiThread(() -> gameOverLayout.setVisibility(View.VISIBLE));
        }

        public void resetGame() {
            running = false;
            if (gameThread != null) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            initializeGame();
            running = true;
            paused = false;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void setDirection(char newDirection) {
            if ((newDirection == 'U' && direction != 'D') ||
                    (newDirection == 'D' && direction != 'U') ||
                    (newDirection == 'L' && direction != 'R') ||
                    (newDirection == 'R' && direction != 'L')) {
                pendingDirection = newDirection;
            }
        }

        public void togglePause() {
            paused = !paused;
        }

        public boolean isPaused() {
            return paused;
        }

        public void pause() {
            if (!paused) {
                togglePause();
                runOnUiThread(() -> {
                    pauseLayout.setVisibility(View.VISIBLE);
                    pauseButton.setImageResource(R.drawable.ic_play);
                });
            }
        }

        public void resume() {
            if (paused && running) {
                togglePause();
                runOnUiThread(() -> {
                    pauseLayout.setVisibility(View.GONE);
                    pauseButton.setImageResource(R.drawable.ic_pause);
                });
            }
        }
    }
}
