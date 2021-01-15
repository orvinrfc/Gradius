/**
	A game inspired by Gradius
	@author Orvin Tritama < orvinrfc at hotmail dot com >
	@version 2018-11-19
*/
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.*;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GradiusComp extends JComponent {

	//Final variables
	private final static Font GAME_OVER_FONT =
		new Font(Config.getString("gameOverFontName"), Font.BOLD,
		Config.getInt("gameOverFontSize"));
	
	private final Color GAME_OVER_COLOR = Config.getColor("gameOverFontColor");
	private final String GAME_OVER_MESSAGE = Config.getString("gameOverMsg");
	private final int GAME_FPS = Config.getInt("gameTicksPerSecond"); 
	private final int ASTEROID_FPS = Config.getInt("asteroidMakePerSecond");
	
	private final static Font ENHANCEMENT_FONT = new Font( Font.SERIF, Font.BOLD, 20);
	private final static Color ENHANCEMENT_COLOR = Color.WHITE;
	
	//Instance variables for sprites
	private final Sprite ship;
	private Collection<Sprite> roids;
	
	private final Timer[] timer; //Array of timers
	private long lastUpdate;

	private boolean gameState; //Game state true if the game is still running 
	
	//ENCHANCEMENTS
	protected double fuelPoints;
	private int score;
	
	//Constructor
	public GradiusComp() {
		setPreferredSize(new Dimension(
			Config.getInt("compWidth"), Config.getInt("compHeight")));
		setBackground(Color.BLACK);
		setOpaque(true);
		
		ship = SpriteFactory.makeShip( this::getBounds );
		roids = new HashSet<>();
		
		timer = new Timer[3];
		timer[0] = new Timer(1000/GAME_FPS, this::update);
		timer[1] = new Timer(1000/ASTEROID_FPS, this::makeAsteroid );
		timer[2] = new Timer(1000, this::updateScore);
		
		addKeyListener(new ShipKeyListener());
		
		fuelPoints = 100;
		score = 0;
	}


	public void start() {
		lastUpdate = System.currentTimeMillis();
		for(Timer s : timer){
			s.start();
		}
		gameState = true;
	}
	
	private void update(ActionEvent ae){
		long now = System.currentTimeMillis();
		int dt = (int)( now - lastUpdate);
		this.update(dt);
		lastUpdate = now;
		repaint();
	}
	
	private void update(int millis){
		ship.update(millis);
		roids.stream().forEach(g -> g.update(millis));
		roids.removeIf(a -> a.isOutOfBounds());
		
		boolean hit = roids.stream().anyMatch( i -> i.intersects(ship));
		
		//Stop the timer (i.e: game if any asteroid hit the ship)
		if(hit == true){
			gameOver();
		}
	
	}
	
	private void updateScore(ActionEvent ae){
		score++;
	}
	
	public void paintComponent(Graphics g) {
		requestFocusInWindow();
		g.setColor(getBackground());
		g.fillRect(0,0, getWidth(), getHeight());

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintComponent(g2);
	}
	
	private void paintComponent(Graphics2D g2) {
		ship.draw(g2);
		roids.stream().forEach( s -> s.draw(g2));
		
		g2.setColor(ENHANCEMENT_COLOR);
		g2.setFont(ENHANCEMENT_FONT);
		drawFuel(g2, this.getBounds(), "Fuel: " + fuelPoints + "%");
		drawScore(g2, this.getBounds(), "Score: " + score);
		
		if( gameState == false){
			g2.setColor(GAME_OVER_COLOR);
			g2.setFont(GAME_OVER_FONT);
			drawCentredString(g2,this.getBounds(),GAME_OVER_MESSAGE);
		}
		
	}
	
	//Helper to draw centered string
	//Source : http://www.java2s.com/Tutorial/Java/0261__2D-Graphics/Centertext.htm
	//		via Jeremy's Hilliker Slides 
			
	private void drawCentredString(Graphics2D g2, Rectangle bounds, String s){
		FontMetrics fm = g2.getFontMetrics();
		int x = bounds.x + ((bounds.width - fm.stringWidth(s)) / 2);
		int y = bounds.y + (fm.getAscent() + (bounds.height - (fm.getAscent() + fm.getDescent())) / 2);
		g2.drawString(s,x,y);
	}
	
	//Helper to show healthPoints
	private void drawFuel(Graphics2D g2, Rectangle bounds, String s){
		int x = bounds.x+5;
		int y = bounds.y+20;
		g2.drawString(s,x,y);
	}
	
	//Helper to show score
	private void drawScore(Graphics2D g2, Rectangle bounds,String s){
		int x = (int)(bounds.x + bounds.getWidth()/2);
		int y = bounds.y+20;
		g2.drawString(s,x,y);
	}
	
	private class ShipKeyListener extends KeyAdapter {

		private final float SHIP_VEL_FAST = Config.getFloat("shipVelFast");
		private final float SHIP_VEL_SLOW = Config.getFloat("shipVelSlow");

		private boolean up;
		private boolean down;
		private boolean left;
		private boolean right;
		
		private float velocityX = 0;
		private float velocityY = 0;
		
		@Override
		public void keyPressed(KeyEvent e){
			switch( e.getKeyCode() ){
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_KP_UP:
					up = true;
					if( e.isShiftDown() && isFuelAvailable(fuelPoints)){
						velocityY = -(SHIP_VEL_FAST);
						fuelPoints -= 0.5;
					}else{
						velocityY = -(SHIP_VEL_SLOW);
					}
					break;
				case KeyEvent.VK_S:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
					down = true;
					if( e.isShiftDown() && isFuelAvailable(fuelPoints)){
						velocityY = (SHIP_VEL_FAST);
						fuelPoints -=0.5;
					}else{
						velocityY = (SHIP_VEL_SLOW);
					}
					break;
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_KP_LEFT:
					left = true;
					if( e.isShiftDown() && isFuelAvailable(fuelPoints)){
						velocityX = -(SHIP_VEL_FAST);
						fuelPoints -=0.5;
					}else{
						velocityX = -(SHIP_VEL_SLOW);
					}
					break;
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_KP_RIGHT:
					right = true;
					if( e.isShiftDown() && isFuelAvailable(fuelPoints)){
						velocityX = (SHIP_VEL_FAST);
						fuelPoints -=0.5;
					}else{
						velocityX = (SHIP_VEL_SLOW);
					}
					break;
			}
			
			ship.setVelocity(velocityX,velocityY);
			repaint();
		}
		
		@Override
		public void keyReleased(KeyEvent e){
			switch( e.getKeyCode() ){
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_KP_UP:
					up = false;
					velocityY = 0;
					break;
				case KeyEvent.VK_S:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
					down = false;
					velocityY = 0;
					break;
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_KP_LEFT:
					left = false;
					velocityX = 0;
					break;
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_KP_RIGHT:
					right = false;
					velocityX = 0;
					break;
			}
			
			ship.setVelocity(velocityX,velocityY);
			repaint();
			
		}
		
		//Helper to check if fuel is still available
		private boolean isFuelAvailable(double fuel){
			return fuel > 0;
		}
		
	}
	
	//Helper method to make asteroid
	private void makeAsteroid(ActionEvent e){
		roids.add(SpriteFactory.makeAsteroid(this::getBounds));
	}
	
	//Helper method gameOver to stop the timer
	private void gameOver(){
		for( Timer s : timer){
			s.stop();
		}
		gameState = false;
	}
	
		
}