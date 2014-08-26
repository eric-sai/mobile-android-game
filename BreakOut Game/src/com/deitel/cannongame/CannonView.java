// CannonView.java
// Displays the Cannon Game
package com.deitel.cannongame;

import java.util.HashMap;
import java.util.Map;

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
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class CannonView extends SurfaceView implements SurfaceHolder.Callback {
	private CannonThread cannonThread; // controls the game loop
	//private CollisionDectThread collisionDectThread;
	private Activity activity; // to display Game Over dialog in GUI thread
	private boolean dialogIsDisplayed = false;

	// constants for game play
	public static final int TARGET_PIECES = 7; // sections in the target
	public static final int MISS_PENALTY = 2; // seconds deducted on a miss
	public static final int HIT_REWARD = 3; // seconds added on a hit
	public static final int NUM_TARGET_LINE = 2; // lines of targets

	// variables for the game loop and tracking statistics
	private boolean gameOver; // is the game over?
	private int shotsFired; // the number of shots the user has fired
	private double totalElapsedTime; // the number of seconds elapsed

	private Line[] target = new Line[NUM_TARGET_LINE]; // start and end points
														// of the target
	private int[] targetDistance = new int[NUM_TARGET_LINE]; // target distance
																// from top
	private int[] targetBeginning = new int[NUM_TARGET_LINE]; // target distance
																// from left
	private double pieceLength; // length of a target piece
	private int[] targetEnd = new int[NUM_TARGET_LINE]; // target bottom's
														// distance from left

	// variables for the padder
	private Line pad;
	private int padDistance;
	private int padBeginning;
	private int padEnd;
	// for extension tasks.
	private float padVelocity;

	private int lineWidth; // width of the target and blocker
	private boolean[][] hitStates = new boolean[NUM_TARGET_LINE][TARGET_PIECES]; // is
																					// each
																					// target
																					// piece
																					// hit?
	private int targetPiecesHit; // number of target pieces hit (out of
									// NUJ_TARGET_LINE * TARGET_PIECES)

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

	// public constructor
	public CannonView(Context context, AttributeSet attrs) {
		super(context, attrs); // call super's constructor
		activity = (Activity) context;

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

		// construct Paints for drawing text, cannonball, cannon,
		// blocker and target; these are configured in method onSizeChanged
		textPaint = new Paint(); // Paint for drawing text
		cannonballPaint = new Paint(); // Paint for drawing a cannon ball
		padPaint = new Paint(); // Paint for drawing the pad
		targetPaint = new Paint(); // Paint for drawing the target
		backgroundPaint = new Paint(); // Paint for drawing the target
	} // end CannonView constructor

	// called by surfaceChanged when the size of the SurfaceView changes,
	// such as when it's first added to the View hierarchy
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		screenWidth = w; // store the width
		screenHeight = h; // store the height

		cannonballRadius = w / 120; // cannonball radius 1/36 screen width
		cannonballSpeed = w * 2 / 1200; // cannonball speed multiplier

		lineWidth = w / 24; // target and blocker 1/24 screen width

		// configure instance variables related to the target
		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			targetDistance[i] = ((2 * i) + 1) * lineWidth;
			targetBeginning[i] = w / 8;
			targetEnd[i] = w * 7 / 8;
			target[i].start = new Point(targetBeginning[i], targetDistance[i]);
			target[i].end = new Point(targetEnd[i], targetDistance[i]);
		}

		pieceLength = (targetEnd[0] - targetBeginning[0]) / TARGET_PIECES;

		// configure instance variables related to the pad
		padDistance = 7 * h / 8;
		padBeginning = w / 2;
		padLen = w / 4;
		padEnd = padBeginning + padLen;
		padVelocity = 0;
		pad.start = new Point(padBeginning, padDistance);
		pad.end = new Point(padEnd, padDistance);

		// configure Paint objects for drawing game elements
		textPaint.setTextSize(w / 20); // text size 1/20 of screen width
		textPaint.setAntiAlias(true); // smoothes the text
		padPaint.setStrokeWidth(lineWidth / 2); // set line thickness
		targetPaint.setStrokeWidth(lineWidth); // set line thickness
		backgroundPaint.setColor(Color.WHITE); // set background color

		newGame(); // set up and start a new game
	} // end method onSizeChanged

	// reset all the screen elements and start a new game
	public void newGame() {
		// set every element of hitStates to false--restores target pieces
		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			for (int j = 0; j < TARGET_PIECES; j++)
				hitStates[i][j] = false;
		}

		targetPiecesHit = 0; // no target pieces have been hit

		// blockerVelocity = initialBlockerVelocity; // set initial velocity

		cannonballFired = false; // the cannonball is not on the screen
		shotsFired = 0; // set the initial number of shots fired
		totalElapsedTime = 0.0; // set the time elapsed to zero

		// blocker.start.set(blockerDistance, blockerBeginning);
		// blocker.end.set(blockerDistance, blockerEnd);

		for (int i = 0; i < NUM_TARGET_LINE; i++) {
			target[i].start.set(targetBeginning[i], targetDistance[i]);
			target[i].end.set(targetEnd[i], targetDistance[i]);
		}

		// set the pad
		pad.start.set(padBeginning, padDistance);
		pad.end.set(padEnd, padDistance);

		// configure instance variables related to the ball
		cannonball.x = (padBeginning + padEnd) / 2; // align x-coordinate with
													// cannon
		cannonball.y = padDistance - cannonballRadius - 10; // centers ball
															// vertically

		if (gameOver) {
			gameOver = false; // the game is not over
			cannonThread = new CannonThread(getHolder());
			//collisionDectThread = new CollisionDectThread();
			cannonThread.start();
			//collisionDectThread.start();
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
				soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f); // play pad sound
			} // end if

			// check for collisions with left or right walls
			if (cannonball.x + cannonballRadius >= screenWidth
					|| cannonball.x - cannonballRadius <= 0) {
				cannonballVelocityX *= -1; // reverse cannonball's X direction
			}

			// check for collisions with top wall
			if (cannonball.y - cannonballRadius <= 0)
				cannonballVelocityY *= -1; // rever cannonball's Y direction

			if (cannonball.y + cannonballRadius >= screenHeight) {

				// the ball hits the bottom, game over.
				cannonballVelocityY *= -1;
				// cannonThread.setRunning(false);
				// showGameOverDialog(R.string.lose); // show winning dialog
				// gameOver = true; // the game is over
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
						cannonballVelocityY *= -1; // reverse the cannonball's Y
													// direction

						// play target hit sound
						soundPool.play(soundMap.get(TARGET_SOUND_ID), 1, 1, 1, 0,
								1f);
						targetPiecesHit++;
					}
				}
			}
			if (targetPiecesHit == TARGET_PIECES * NUM_TARGET_LINE) {
				cannonThread.setRunning(false);
				showGameOverDialog(R.string.win); // show winning dialog
				gameOver = true; // the game is over
			}	
		} // end if
	} // end method updatePositions

	private void collisionDect() {
		
		if (cannonballFired){
			// check for collision with pad
			if (cannonball.x + cannonballRadius >= pad.start.x
					&& cannonball.x - cannonballRadius <= pad.end.x
					&& cannonball.y + cannonballRadius >= padDistance
					&& cannonball.y - cannonballRadius <= padDistance) {
				
				cannonballVelocityY *= -1; // reverse cannonball's Y direction
				soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f); // play pad sound
			} // end if

			// check for collisions with left or right walls
			if (cannonball.x + cannonballRadius >= screenWidth
					|| cannonball.x - cannonballRadius <= 0) {
				cannonballVelocityX *= -1; // reverse cannonball's X direction
			}

			// check for collisions with top wall
			if (cannonball.y - cannonballRadius <= 0)
				cannonballVelocityY *= -1; // rever cannonball's Y direction

			if (cannonball.y + cannonballRadius >= screenHeight) {

				// the ball hits the bottom, game over.
				cannonballVelocityY *= -1;
				// cannonThread.setRunning(false);
				// showGameOverDialog(R.string.lose); // show winning dialog
				// gameOver = true; // the game is over
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
						cannonballVelocityY *= -1; // reverse the cannonball's Y
													// direction

						// play target hit sound
						soundPool.play(soundMap.get(TARGET_SOUND_ID), 1, 1, 1, 0,
								1f);
						targetPiecesHit++;
					}
				}
			}
			if (targetPiecesHit == TARGET_PIECES * NUM_TARGET_LINE) {
				cannonThread.setRunning(false);
				showGameOverDialog(R.string.win); // show winning dialog
				gameOver = true; // the game is over
			}			
		}
	}

	// fires a cannonball
	public void fireCannonball(MotionEvent event) {

		if (cannonballFired)
			return;

		cannonballFired = true;

		double angle = alignCannon(event); // get the cannon barrel's angle

		// get the x component of the total velocity
		cannonballVelocityX = (int) (cannonballSpeed * Math.sin(angle));

		// get the y component of the total velocity
		cannonballVelocityY = (int) (cannonballSpeed * Math.cos(angle));

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

		// compute the touch's distance from center of the screen
		// on the x-axis
		double centerMinusX = (screenWidth / 2 - touchPoint.x);

		double angle1 = 0;

		// calculate the angle the barrel makes with the horizontal
		if (centerMinusX != 0) // prevent division by 0
			angle1 = Math.atan((double) (padDistance - touchPoint.y)
					/ centerMinusX);

		// if the touch is on the lower half of the screen
		if (touchPoint.x > screenWidth / 2)
			angle1 += Math.PI; // adjust the angle

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
		canvas.drawText(
				getResources().getString(R.string.time_used_format,
						totalElapsedTime), 30, 50, textPaint);

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
							(int) (currentPoint.x + pieceLength),
							currentPoint.y, targetPaint);
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

		// display number of shots fired and total time elapsed
		dialogBuilder.setMessage(getResources().getString(
				R.string.results_format, shotsFired, totalElapsedTime));
		dialogBuilder.setPositiveButton(R.string.reset_game,
				new DialogInterface.OnClickListener() {
					// called when "Reset Game" Button is pressed
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialogIsDisplayed = false;
						newGame(); // set up and start a new game
					} // end method onClick
				} // end anonymous inner class
				); // end call to setPositiveButton

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
			//collisionDectThread = new CollisionDectThread();
			cannonThread.setRunning(true);
			//collisionDectThread.setRunning(true);
			cannonThread.start(); // start the game loop thread
			//collisionDectThread.start();
		} // end if
	} // end method surfaceCreated

	// called when the surface is destroyed
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// ensure that thread terminates properly
		boolean retry = true;
		cannonThread.setRunning(false);

		while (retry) {
			try {
				cannonThread.join();
				retry = false;
			} // end try
			catch (InterruptedException e) {
			} // end catch
		} // end while
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
						updatePositions(elapsedTimeMS); // update game state
						drawGameElements(canvas); // draw
						previousFrameTime = currentTime; // update previous time
					} // end synchronized block
				} // end try
				finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				} // end finally
			} // end while
		} // end method run
	} // end nested class CannonThread
	/*
	private class CollisionDectThread extends Thread{
		
		private boolean threadIsRunning;
		public CollisionDectThread(){
			
		}
		
		public void setRunning(boolean running){
			threadIsRunning = running;
		}
		
		public void run(){
			while(threadIsRunning){
				try{
					;
				}
				finally{}
			}
		}
	}*/
} // end class CannonView

/*********************************************************************************
 * (C) Copyright 1992-2012 by Deitel & Associates, Inc. and * Pearson Education,
 * * Inc. All Rights Reserved. * * DISCLAIMER: The authors and publisher of this
 * * book have used their * best efforts in preparing the book. These efforts *
 * include the * development, research, and testing of the theories and programs
 * * * to determine their effectiveness. The authors and publisher make * no *
 * warranty of any kind, expressed or implied, with regard to these * programs *
 * or to the documentation contained in these books. The authors * and publisher
 * * shall not be liable in any event for incidental or * consequential damages
 * in * connection with, or arising out of, the * furnishing, performance, or
 * use of * these programs. *
 *********************************************************************************/
