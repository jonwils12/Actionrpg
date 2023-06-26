import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SideScrollingRPGActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
    }

    class GameView extends SurfaceView implements Runnable {

        private Thread gameThread;
        private SurfaceHolder surfaceHolder;
        private volatile boolean running;
        private Player player;
        private Bitmap background;
        private Enemy enemy;
        private boolean isGameOver;

        public GameView(Context context) {
            super(context);
            surfaceHolder = getHolder();
            player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.player_image));
            background = BitmapFactory.decodeResource(getResources(), R.drawable.background_image);
            enemy = new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemy_image));
            isGameOver = false;
        }

        @Override
        public void run() {
            while (running) {
                if (surfaceHolder.getSurface().isValid()) {
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawBitmap(background, 0, 0, null);
                    player.draw(canvas);
                    if (!isGameOver) {
                        enemy.update();
                        enemy.draw(canvas);
                        if (checkCollision(player.getBounds(), enemy.getBounds())) {
                            // Handle collision, e.g., game over logic
                            isGameOver = true;
                        }
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Handle player movement based on touch events
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    player.setMoving(true);
                    player.setMovingDirection(Player.DIRECTION_RIGHT);
                    break;
                case MotionEvent.ACTION_UP:
                    player.setMoving(false);
                    break;
            }
            return true;
        }

        private boolean checkCollision(Rect rect1, Rect rect2) {
            return rect1.intersect(rect2);
        }
    }

    class Player {
        // Player class implementation (same as before)
    }

    class Enemy {
        private Bitmap bitmap;
        private int x, y;
        private int movingDirection;
        private Rect bounds;

        public Enemy(Bitmap bitmap) {
            this.bitmap = bitmap;
            x = 500; // Initial x position
            y = 100; // Initial y position
            movingDirection = Player.DIRECTION_LEFT;
            bounds = new Rect(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
        }

        public void update() {
            x += movingDirection * 3; // Adjust movement speed as needed
            bounds.left = x;
            bounds.right = x + bitmap.getWidth();
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(bitmap, x, y, null);
        }

        public Rect getBounds() {
            return bounds;
        }
    }
}
