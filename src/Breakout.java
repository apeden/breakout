//package breakout;
/* *
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Title text */
	public static final String TITLE = "Breakout!";	
	
/** Congratulations */
	public static final String CONGRATULATIONS = "You won! Rock on!";	

/** Commiserations */
	public static final String COMMISERATIONS= "You lost! Bad times!";	

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 10;
	//offset reduced to stop ball 'sticking' to underside of paddle

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** A pause of time in miniseconds */
	private static final int PAUSE = 50;
	
	/*Method: setupTitle()*/
	/**Sets up the the title. */
	private void setupTitle() {
        title = new GLabel(TITLE, WIDTH/3, HEIGHT/8);
        title.setFont("SanSerif-BOLDITALIC-34");
        title.setColor(Color.RED);
        add(title);
	}
	
	/*Method: setupScore()*/
	/**Sets up the the score. */
	private void setupScore() {
		score_label = new GLabel(("Your score = "+score), WIDTH/16, HEIGHT/16);
        score_label.setFont("SanSerif-BOLDITALIC-18");
        add(score_label);
	}
		
	/*Method: setup_frame()*/
	/**Sets up the Breakout game frame. 
	 * I needed this to have a visible border on the applet*/
	private void setup_frame() {
		this.resize(WIDTH,HEIGHT);
        pause(PAUSE);
        frame = new GRect(WIDTH, HEIGHT);
        add(frame);
	}
 
	/*Method: setup_bricks()*/
	/** Sets up the coloured bricks
	 * Later on I will learn how to apply and array
	 * or something in this method to make the implementation less
	 * verbose*/
    private void setup_bricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {		
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {
				/*to go down the wall*/
				int y = BRICK_Y_OFFSET
						+ ((i + 1)* (BRICK_HEIGHT + BRICK_SEP));
				/*to fill bricks left to right*/
				int x = (BRICK_SEP/2)
						+ ((WIDTH - ((NBRICKS_PER_ROW)*(BRICK_WIDTH + BRICK_SEP)))/2) 
						+ (j *(BRICK_WIDTH + BRICK_SEP));			
				rect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
				rect.setFilled(true);
				add(rect);
				if (i<NBRICK_ROWS*1/5) {
					rect.setColor(Color.RED);
				}	
				else if (i<NBRICK_ROWS*2/5) {	
					rect.setColor(Color.ORANGE);
				}
				else if (i<NBRICK_ROWS*3/5) {	
					rect.setColor(Color.YELLOW);
				}
				else if (i<NBRICK_ROWS*4/5) {	
					rect.setColor(Color.GREEN);
				}	
				else if (i<NBRICK_ROWS) {	
					rect.setColor(Color.CYAN);
				}
			}
		}
	}

	/*Method: setup_paddle()*/
    /**Sets up the paddle.
     * I lowered the paddle as an easy way of stopping
     * the ball sticking to its underside*/
    private void setup_paddle() {
    	paddle 
		= new GRect(0, 	HEIGHT-PADDLE_Y_OFFSET,
						PADDLE_WIDTH, 
						PADDLE_HEIGHT);
    	paddle.setColor(Color.BLACK);
    	paddle.setFilled(true);
		add(paddle);
    }
   
    /*Method: setup_ball()*/
    /**Sets up the ball. I preferred red*/
    private void setup_ball() {
    	ball 
		= new GOval((WIDTH/2)-BALL_RADIUS, 
					(HEIGHT/2)-BALL_RADIUS, 
					2*BALL_RADIUS, 
					2*BALL_RADIUS);
    	ball.setColor(Color.RED);
    	ball.setFilled(true);
		add(ball);
    }
    
    /*Method: move_ball()*/
    /**Moves the ball. I chose edgier angles to start off
     * Key USP: the angle the ball bounces off the paddle
     * depends on the point of contact*/
    private void move_ball() {
    	vy = 8.0;
    	vx = reg.nextDouble(5.0, 8.0);
    	if  (reg.nextBoolean(0.5)) {
    		vx = -vx;
    	}
    	while(true) {
    		ball.move(vx, vy); 
    		if (checkForCollision() == false) {
    			break;
    		}
    		pause(PAUSE);
    		GObject collider = getCollidingObject(ball.getX(),ball.getY());
    		println(collider);		
    		if (collider != null) {
    			if (collider == paddle) {
    				vy = -vy;
    				if ((ball.getX()-paddle.getX())>(PADDLE_WIDTH/2)) {
    					vx = -vx; //ball direction post paddle bouch varies according to where paddle hits
    				}ball.move(vx, vy);//clears ball from paddle
    			}else if (collider.getWidth() == BRICK_WIDTH) {
    				remove(collider);
    				vy = -vy;    	
    				score += 1;
    				remove(score_label);
    				setupScore();
    				if (score == NBRICKS_PER_ROW*NBRICK_ROWS){
    					terminate(CONGRATULATIONS);
    					break; 
    				}
    			}
    		}
    	} 
    }
    
    /*Method: checkForCollision()*/
    /**Checks for collisions with edges of frame.
     * I added additional ball moves to clear ball
     *  
     * @return
     */
    private Boolean checkForCollision() {
    	if (ball.getY() > (HEIGHT-2*BALL_RADIUS)) {   		
    		terminate(COMMISERATIONS);
    		return false;
    	}else if (ball.getY() < 0) {   		
    		vy = -vy;
    		ball.move(vx, vy);    		
    	}else if (ball.getX() > WIDTH-2*BALL_RADIUS) {   		
    		vx = -vx;
    		ball.move(vx, vy);
    	}else if (ball.getX() < 0) {   		
    		vx = -vx;
    		ball.move(vx, vy);
    		
    	}
    	return true;
    }
    
    /*Method: getCollidingObject()*/
    /** Gets the colliding object*
     * 
     * @param x
     * @param y
     * @return
     */
    private GObject getCollidingObject(double x, double y) {
    	if (getElementAt(x, y) != frame) {
    		return (getElementAt(x, y));
    	} else if (getElementAt(x+(2*BALL_RADIUS), y) != frame) {
		 	return (getElementAt(x+(2*BALL_RADIUS), y));
    	} else if (getElementAt(x, y+(2*BALL_RADIUS)) != frame) {
    		return (getElementAt(x, y+(2*BALL_RADIUS)));
    	} else if (getElementAt(x+(2*BALL_RADIUS), y+(2*BALL_RADIUS)) != frame) {
    		return (getElementAt(x+(2*BALL_RADIUS), y+(2*BALL_RADIUS)));
    	} else {
    		return null;
    	}
    }   
    
    //init method is called when the programme starts
    /**Adds mouse listeners*/
    public void init() {
    	addMouseListeners();
    }
   
    /*Called on mouse press to record the coordinates of the last click*/
    /**gets the element where the mouse is pressed.*/
    public void mousePressed (MouseEvent e) {
    	last = new GPoint(e.getPoint());
    	gobj = getElementAt(last);
    }
    
    /**This method is called every time the mouse is dragged
     *  (i.e. moved with the left clicker depressed)*/
    
    public void mouseDragged (MouseEvent e) {
    	if (gobj == paddle) {
    		if (gobj.getX()<0) {
    			gobj.setLocation(0, HEIGHT-PADDLE_Y_OFFSET);
    		}
    		else if (gobj.getX()>WIDTH-PADDLE_WIDTH) {
        		gobj.setLocation(WIDTH-PADDLE_WIDTH, HEIGHT-PADDLE_Y_OFFSET);
    		}
    		gobj.move(e.getX()-last.getX(),0);
    		last = new GPoint(e.getPoint());  			
    	}
    }

    
    /*Method: terminate*/
    /** puts up end message, good or bad*/
    private void terminate(String message) {
        win_lose = new GLabel(message, WIDTH/12, HEIGHT/2);
        win_lose.setFont("SanSerif-BOLDITALIC-34");
        add(win_lose);
	}
    
	/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setupTitle();
		setupScore();
		setup_frame();
		setup_bricks();
		setup_paddle();
		setup_ball();
		move_ball();
		addMouseListeners();
	}

	/*Private instance variables*/
	private GLabel title;			//title
	private GLabel score_label;	    //score indicator
	private GLabel win_lose;		//text to indicate whether player has one or lost
	private GRect frame;			//frame upon which game is played.
	private GRect paddle;   		//paddle used by user to hit ball
	private GRect rect;				//a brick
	private GOval ball;				//a ball
	private GObject gobj;   		//an unspecified gobj
	private GPoint last;    		//the last mouse position
	private double vx, vy;  		//the velocity of the ball in the x and y direction
	private double score = 0;      	//score
	private RandomGenerator reg = RandomGenerator.getInstance();

}
