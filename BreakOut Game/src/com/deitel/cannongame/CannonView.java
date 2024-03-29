// CannonView.java
// Displays the Cannon Game
package com.deitel.cannongame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
	private CannonThread cannonThread; // controls the game loop
	private CollisionDectThread coldThread;
	// private CollisionDectThread collisionDectThread;
	private Activity activity; // to display Game Over dialog in GUI thread
	private boolean dialogIsDisplayed = false;
	public boolean downloaded = false;

	// constants for game play
	public static final int TARGET_PIECES = 10; // sections in the target
	public static final int MISS_PENALTY = 2; // seconds deducted on a miss
	public static final int HIT_REWARD = 3; // seconds added on a hit
	public static final int NUM_TARGET_LINE = 2; // lines of targets

	// variables for the game loop and tracking statistics
	private boolean gameOver; // is the game over?
	private int shotsFired; // the number of shots the user has fired
	private double totalElapsedTime; // the number of seconds elapsed
	public int totalScore;
	public int currtLevel;

	private Line[] target = new Line[NUM_TARGET_LINE];
	private int[] targetDistance = new int[NUM_TARGET_LINE];
	private int[] targetBeginning = new int[NUM_TARGET_LINE];
	private double pieceLength; // length of a target piece
	private int[] targetEnd = new int[NUM_TARGET_LINE];

	// variables for the padder
	private Line pad;
	private int padDistance;
	private int padBeginning;
	private int padEnd;
	// for extension tasks.
	public float padVelocity;

	private int lineWidth; // width of the target and blocker
	private int linegap;
	private boolean[][] hitStates = new boolean[NUM_TARGET_LINE][TARGET_PIECES];
	private int targetPiecesHit;
	private int sumOfBricks;

	// variables for the cannon and cannonball
	private Point cannonball; // cannonball image's upper-left corner
	private int cannonballVelocityX; // cannonball's x velocity
	private int cannonballVelocityY; // cannonball's y velocity
	private boolean cannonballFired; // is the cannonball on the screen
	private int cannonballRadius; // cannonball radius
	private int cannonballSpeed; // cannonball speed

	private int screenWidth; // width of the screen
	private int screenHeight; // height of the screen
	private int padLen;

	// constants and variables for managing sounds
	private static final int TARGET_SOUND_ID = 0;
	private static final int CANNON_SOUND_ID = 1;
	private static final int BLOCKER_SOUND_ID = 2;
	private SoundPool soundPool; // plays sound effects
	private Map<Integer, Integer> soundMap; // maps IDs to SoundPool

	// Paint variables used when drawing each item on the screen
	private Paint textPaint; // Paint used to draw text
	private Paint cannonballPaint; // Paint used to draw the cannonball
	private Paint targetPaint; // Paint used to draw the target
	private Paint backgroundPaint; // Paint used to clear the drawing area
	private Paint padPaint; // Paint used to draw padder
	
	CannonGame father;

	// public constructor
	public CannonView(Context context, AttributeSet attrs) {
		super(context, attrs); // call super's constructor
		activity = (Activity) context;
		this.father = (CannonGame) context;

		// register SurfaceHolder.Callback listener
		getHolder().addCallback(this);

		for (int i = 0; i < NUM_TARGET_LINE; i++)
			target[i] = new Line(); // create the target as a Line

		// initialize the pad
		pad = new Line(); // create the pad as a Line

		cannonball = new Point(); // create the cannonball as a point

		// initialize hitStates as a boolean array
		hitStates = new boolean[NUM_TARGET_LINE][TARGET_PIECES];

		// initialize SoundPool to play the app's three sound effects
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

		// create Map of sounds and pre-load sounds
		soundMap = new HashMap<Integer, Integer>(); // create new HashMap
		soundMap.put(TARGET_SOUND_ID,
				soundPool.load(context, R.raw.target_hit, 1));
		soundMap.put(CANNON_SOUND_ID,
				soundPool.load(context, R.raw.cannon_fire, 1));
		soundMap.put(BLOCKER_SOUND_ID,
				soundPool.load(context, R.raw.blocker_hit, 1));
		totalScore = 0;

		// construct Paints for drawing text, cannonball, cannon,
		// blocker and target; these are configured in method onSizeChanged
		textPaint = new Paint(); // Paint for drawing text
		cannonballPaint = new Paint(); // Paint for drawing a cannon ball
		padPaint = new Paint(); // Paint for drawing the pad
		targetPaint = new Paint(); // Paint for drawing the target
		backgroundPaint = new Paint(); // Paint for drawing the target
	} // end CannonView constructor
	
	public String readGameSettings(String filename){
		String gameSetting = null;
		try {
			InputStream in = getResources().getAssets().open(filename);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			gameSetting = EncodingUtils.getString(buffer,"UTF-8");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gameSetting;
	}

	// called by surfaceChanged when the size of the SurfaceView changes,
	// such as when it's first added to the View hierarchy
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		screenWidth = w; // store the width
		screenHeight = h; // store the height
		currtLevel = 1;
		Log.d("width:",Integer.toString(w));
		Log.d("height:",Integer.toString(h));
		// Fixed configurations of the game:
		cannonballRadius = w / 120; // cannonball radius 1/36 screen width
		lineWidth = h / 24; // target and blocker 1/24 screen width
		linegap = lineWidth /2;
		
		// configure instance variables related to the target
		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			targetDistance[i] = ((2 * i) + 1) * linegap;
			targetBeginning[i] = w / 8;
			targetEnd[i] = w * 7 / 8;
			target[i].start = new Point(targetBeginning[i], targetDistance[i]);
			target[i].end = new Point( targetEnd[i], targetDistance[i]);
		}
		pieceLength = (targetEnd[0] - targetBeginning[0]) / TARGET_PIECES;
		
		// configure instance variables related to the pad
		padDistance = h *7 / 8;
		padBeginning = w / 2;
		padVelocity = 0;

		// configure Paint objects for drawing game elements
		textPaint.setTextSize(w / 20); // text size 1/20 of screen width
		textPaint.setAntiAlias(true); // smoothes the text
		padPaint.setStrokeWidth(lineWidth / 2); // set line thickness
		targetPaint.setStrokeWidth(lineWidth / 2); // set line thickness
		backgroundPaint.setColor(Color.WHITE); // set background color

		newGame("Breakout-level"+Integer.toString(currtLevel)); // set up and start a new game
	} // end method onSizeChanged

	// reset all the screen elements and start a new game
	public void newGame(String level) {
		
		// get game settings.
		String gameSetting = readGameSettings(level);
		Log.d("asset-level1:",gameSetting);	
		String[] settings = gameSetting.split(",");
		
		padLen = Integer.parseInt(settings[0]);
		cannonballSpeed = Integer.parseInt(settings[1]);
		Log.d("pad len:",Integer.toString(padLen));
		Log.d("ball speed:",Integer.toString(cannonballSpeed));
		padEnd = padBeginning + padLen;
		
		pad.start = new Point(padBeginning, padDistance);
		pad.end = new Point(padEnd, padDistance);
		Log.d("taget setting:",settings[2]);
		
		sumOfBricks = 0;
		// set every element of hitStates to false--restores target pieces
		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			for (int j = 0; j < TARGET_PIECES; j++){
				if(settings[i+2].charAt(j)=='1'){
					hitStates[i][j] = false;
					sumOfBricks++;
				}
				else
					hitStates[i][j] = true;
					
			}
		}

		targetPiecesHit = 0; // no target pieces have been hit
		cannonballFired = false; // the cannonball is not on the screen
		shotsFired = 0; // set the initial number of shots fired
		totalElapsedTime = 0.0; // set the time elapsed to zero
		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			target[i].start.set(targetBeginning[i], targetDistance[i]);
			target[i].end.set(targetEnd[i], targetDistance[i]);
		}

		// set the pad
		pad.start.set(padBeginning, padDistance);
		pad.end.set(padEnd, padDistance);

		// configure instance variables related to the ball
		cannonball.y = pad.start.y - cannonballRadius - 10;
		cannonball.x = (pad.start.x + pad.end.x) / 2;
		
		if (gameOver) {
			gameOver = false; // the game is not over
			cannonThread = new CannonThread(getHolder());
			coldThread = new CollisionDectThread();
			// collisionDectThread = new CollisionDectThread();
			cannonThread.start();
			coldThread.start();
			// collisionDectThread.start();
		} // end if
	} // end method newGame

	// called repeatedly by the CannonThread to update game elements
	private void updatePositions(double elapsedTimeMS) {
		double interval = elapsedTimeMS; // convert to seconds

		if (cannonballFired) // if there is currently a shot fired
		{
			// update cannonball position
			cannonball.x += interval * cannonballVelocityX;
			cannonball.y += interval * cannonballVelocityY;
			// check for collision with pad
			if (cannonball.x + cannonballRadius >= pad.start.x
					&& cannonball.x - cannonballRadius <= pad.end.x
					&& cannonball.y + cannonballRadius >= padDistance
					&& cannonball.y - cannonballRadius <= padDistance) {

				cannonballVelocityY *= -1; // reverse cannonball's Y direction
				soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f); 
			} // end if

			// check for collisions with left or right walls
			if (cannonball.x + cannonballRadius >= screenWidth) {
				cannonballVelocityX *= -1; // reverse cannonball's Y direction
				cannonball.x = screenWidth - cannonballRadius;
			}
			if (cannonball.x - cannonballRadius <= 0){
				// the ball hits the bottom, game over.
				//cannonThread.setRunning(false);
				//coldThread.setRunning(false);
				//showGameOverDialog(R.string.lose); 
				//gameOver = true; // the game is over
				cannonballVelocityX *= -1; 
				cannonball.x = cannonballRadius;
			}
			
			// collistions with top or bottom
			if (cannonball.y + cannonballRadius >= screenHeight){
				
				cannonballVelocityY *= -1;
				cannonball.y = screenHeight - cannonballRadius;
			}
			if(cannonball.y - cannonballRadius <= 0 ) {				
				cannonballVelocityY *= -1;
				cannonball.y = cannonballRadius;
			}

			// check for collisions with targets
			for (int i = 0; i < NUM_TARGET_LINE; i++) {
				if (cannonball.x + cannonballRadius >= target[i].start.x
						&& cannonball.x - cannonballRadius <= target[i].end.x
						&& cannonball.y + cannonballRadius >= targetDistance[i]
						&& cannonball.y - cannonballRadius <= targetDistance[i]) {
					// determine target section number (0 is the top)
					int section = (int) ((cannonball.x - target[i].start.x) / pieceLength);

					// check if the piece hasn't been hit yet
					if ((section >= 0 && section < TARGET_PIECES)
							&& !hitStates[i][section]) {
						hitStates[i][section] = true; // section was hit
						cannonballVelocityY *= -1; // reverse the cannonball's x
													// direction
						totalScore += 10;
						this.father.sendMessage(1);
						Log.d("pad speed:",String.valueOf(padVelocity));
						cannonball.x += interval * padVelocity/1000;
						Log.d("value of  score:", Integer.toString(totalScore));
						// play target hit sound
						soundPool.play(soundMap.get(TARGET_SOUND_ID), 1, 1, 1,
								0, 1f);
						targetPiecesHit++;
					}
				}
			}

		} // end if
	} // end method updatePositions
	
	public void writeRecord(String file) {
		try {
			FileOutputStream fos = this.getContext().openFileOutput("BKT-Gamerecord",
					Context.MODE_APPEND);
			fos.write(file.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// fires a cannonball
	public void fireCannonball(MotionEvent event) {

		if (cannonballFired)
			return;
		cannonball.y = pad.start.y - cannonballRadius - 10;
		cannonball.x = (pad.start.x + pad.end.x) / 2;
		cannonballFired = true;

		//double angle = alignCannon(event); // get the cannon barrel's angle
		double angle = 2.43;
		Log.d("angle:",Double.toString(angle));

		// get the x component of the total velocity
		cannonballVelocityX = (int) (cannonballSpeed * Math.sin(angle));

		// get the y component of the total velocity
		cannonballVelocityY = (int) (-cannonballSpeed * Math.cos(angle));

		// play cannon fired sound
		soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);
	} // end method fireCannonball

	// aligns the cannon in response to a user touch
	public double alignCannon(MotionEvent event) {
		// get the location of the touch in this view
		Point touchPoint = new Point((int) event.getX(), (int) event.getY());

		// compute the touch's distance from center of the screen
		// on the y-axis
		double centerMinusY = (screenHeight / 2 - touchPoint.y);

		double angle = 0; // initialize angle to 0

		// calculate the angle the barrel makes with the horizontal
		if (centerMinusY != 0) // prevent division by 0
			angle = Math.atan((double) touchPoint.x / centerMinusY);

		// if the touch is on the lower half of the screen
		if (touchPoint.y > screenHeight / 2)
			angle += Math.PI; // adjust the angle

		return angle; // return the computed angle
	} // end method alignCannon

	public void movePad(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// update the pad position
		double scrollDistance = distanceX;
		
		// if the pad hit the top or bottom, reverse direction
		if (pad.start.x < 0) {
			pad.start.x = 0;
			pad.end.x = pad.start.x + padLen;
		} else if (pad.end.x > screenWidth) {
			pad.end.x = screenWidth;
			pad.start.x = pad.end.x - padLen;
		} else {
			pad.start.x -= scrollDistance;
			pad.end.x -= scrollDistance;
		}
	}

	// draws the game to the given Canvas
	public void drawGameElements(Canvas canvas) {
		// clear the background
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
				backgroundPaint);

		// display time remaining
		textPaint.setColor(Color.BLUE);
		canvas.drawText(
				getResources().getString(R.string.score,
						totalScore), 30, 90, textPaint);
		
		if(!cannonballFired){
			// configure instance variables related to the ball
			cannonball.y = pad.start.y - cannonballRadius - 10;
			cannonball.x = (pad.start.x + pad.end.x) / 2;
		}
		// draw the cannon ball
		canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
				cannonballPaint);

		// draw the pad
		padPaint.setColor(Color.RED);

		canvas.drawLine(pad.start.x, pad.start.y, pad.end.x, pad.end.y,
				padPaint);

		// draw the target lines
		for (int i = 0; i < NUM_TARGET_LINE; i++) {

			Point currentPoint = new Point(); // start of current target section

			// initialize curPoint to the starting point of the target
			currentPoint.x = target[i].start.x;
			currentPoint.y = target[i].start.y;

			for (int j = 0; j < TARGET_PIECES; j++) {
				// if this target piece is not hit, draw it
				if (!hitStates[i][j]) {
					// alternate coloring the pieces yellow and blue
					if (j % 2 == 0)
						targetPaint.setColor(Color.YELLOW);
					else
						targetPaint.setColor(Color.BLUE);

					canvas.drawLine(currentPoint.x, currentPoint.y,
							(int)(currentPoint.x + pieceLength), currentPoint.y, targetPaint);
				} // end if

				// move curPoint to the start of the next piece
				currentPoint.x += pieceLength;
			} // end for
		}
	} // end method drawGameElements

	// display an AlertDialog when the game ends
	private void showGameOverDialog(int messageId) {
		// create a dialog displaying the given String
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				getContext());
		
		dialogBuilder.setTitle(getResources().getString(messageId));
		dialogBuilder.setCancelable(false);
		if (messageId == R.string.lose){
			// display number of shots fired and total time elapsed
			dialogBuilder.setMessage(getResources().getString(
					R.string.results_format, totalScore));
			dialogBuilder.setPositiveButton(R.string.reset_game,
					new DialogInterface.OnClickListener() {
						// called when "Reset Game" Button is pressed
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialogIsDisplayed = false;
							totalScore = 0;
							father.sendMessage(3);
							newGame("Breakout-level1"); // set up and start a new game
						} // end method onClick
					} // end anonymous inner class
					); // end call to setPositiveButton
		}

		else{
			// display number of shots fired and total time elapsed
			dialogBuilder.setMessage(getResources().getString(
					R.string.results_format, totalScore));
			dialogBuilder.setPositiveButton(R.string.reset_game,
					new DialogInterface.OnClickListener() {
						// called when "Reset Game" Button is pressed
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialogIsDisplayed = false;
							writeRecord(Integer.toString(totalScore)+",");
							totalScore = 0;
							father.sendMessage(3);
							newGame("Breakout-level1"); // set up and start a new game
						} // end method onClick
					} // end anonymous inner class
					); // end call to setPositiveButton
			dialogBuilder.setNegativeButton(R.string.next_level,
					new DialogInterface.OnClickListener() {
						// called when "Reset Game" Button is pressed
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialogIsDisplayed = false;
							currtLevel++;
							if(currtLevel > 2 && !downloaded){
								currtLevel = 1;
							}
							father.sendMessage(2);
							newGame("Breakout-level"+Integer.toString(currtLevel));
						}// set up and start a new game
					} // end method onClick
					);
		}
		activity.runOnUiThread(new Runnable() {
			public void run() {
				dialogIsDisplayed = true;
				dialogBuilder.show(); // display the dialog
			} // end method run
		} // end Runnable
		); // end call to runOnUiThread
	} // end method showGameOverDialog

	// stops the game
	public void stopGame() {
		if (cannonThread != null)
			cannonThread.setRunning(false);
		coldThread.setRunning(false);
	} // end method stopGame

	// releases resources; called by CannonGame's onDestroy method
	public void releaseResources() {
		soundPool.release(); // release all resources used by the SoundPool
		soundPool = null;
	} // end method releaseResources

	// called when surface changes size
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	} // end method surfaceChanged

	// called when surface is first created
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!dialogIsDisplayed) {
			cannonThread = new CannonThread(holder);
			coldThread = new CollisionDectThread();
			// collisionDectThread = new CollisionDectThread();
			cannonThread.setRunning(true);
			coldThread.setRunning(true);
			// collisionDectThread.setRunning(true);
			cannonThread.start(); // start the game loop thread
			coldThread.start();
			// collisionDectThread.start();
		} // end if
	} // end method surfaceCreated

	// called when the surface is destroyed
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ensure that thread terminates properly
		boolean retry = true;
		cannonThread.setRunning(false);
		coldThread.setRunning(false);

		while (retry) {
			try {
				cannonThread.join();
				coldThread.join();
				retry = false;
			} // end try
			catch (InterruptedException e) {
			} // end catch
		} // end while
		//this.activity.setContentView(R.layout.welcome);
	} // end method surfaceDestroyed

	// Thread subclass to control the game loop
	private class CannonThread extends Thread {
		private SurfaceHolder surfaceHolder; // for manipulating canvas
		private boolean threadIsRunning = true; // running by default

		// initializes the surface holder
		public CannonThread(SurfaceHolder holder) {
			surfaceHolder = holder;
			setName("CannonThread");
		} // end constructor

		// changes running state
		public void setRunning(boolean running) {
			threadIsRunning = running;
		} // end method setRunning

		// controls the game loop
		@Override
		public void run() {
			Canvas canvas = null; // used for drawing
			long previousFrameTime = System.currentTimeMillis();

			while (threadIsRunning) {
				try {
					canvas = surfaceHolder.lockCanvas(null);

					// lock the surfaceHolder for drawing
					synchronized (surfaceHolder) {
						long currentTime = System.currentTimeMillis();
						double elapsedTimeMS = currentTime - previousFrameTime;
						totalElapsedTime += elapsedTimeMS / 1000.00;
						drawGameElements(canvas); // draw
						
						// check if game over.
						if (targetPiecesHit == sumOfBricks) {
							cannonThread.setRunning(false);
							coldThread.setRunning(false);
							showGameOverDialog(R.string.win); // show winning dialog
							gameOver = true; // the game is over
						}
						previousFrameTime = currentTime; // update previous time
					} // end synchronized block
				} // end try
				finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				} // end finally
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class CollisionDectThread extends Thread {

		private boolean threadIsRunning = true;

		public CollisionDectThread() {

		}

		public void setRunning(boolean running) {
			threadIsRunning = running;
		}

		public void run() {
			long previousFrameTime = System.currentTimeMillis();
			while (threadIsRunning) {
				try {
					long currentTime = System.currentTimeMillis();
					double elapsedTimeMS = currentTime - previousFrameTime;
					updatePositions(elapsedTimeMS); // update game state
					previousFrameTime = currentTime;
				} finally {
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
