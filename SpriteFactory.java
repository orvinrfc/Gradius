/**
	A game inspired by Gradius
	@author Orvin Tritama < orvinrfc at hotmail dot com >
	@version 2018-11-19
*/

import java.awt.*;
import java.awt.geom.*;
import java.util.Random;
import java.util.function.Supplier;

public class SpriteFactory {

	private SpriteFactory() {}

	// ****** Helper

	private static float random(float min, float max) {
		if(max > min) { return random(max, min); }
		float range = max - min;
		Random rand = java.util.concurrent.ThreadLocalRandom.current();
		return rand.nextFloat() * range + min;
	}

	// ***** SHIP

	public static Sprite makeShip(Supplier<Rectangle> bounds) {
		int scaleForShip = Config.getInt("shipScale");
		int initXPos = Config.getInt("shipInitX");
		int initYPos = Config.getInt("shipInitY");
		
		int[] xPoints = new int []{initXPos, initXPos + scaleForShip, initXPos};
		int[] yPoints = new int []{initYPos, (initYPos + (initYPos - scaleForShip) )/ 2 , initYPos - scaleForShip};

		int npoints = 3;
		
		Shape polyShip = new Polygon(xPoints, yPoints, npoints);
		Color borderShipColor = Config.getColor("shipColorBorder");
		Color fillShipColor = Config.getColor("shipColorFill");
		
		return new SpriteImpl(polyShip,bounds,true,borderShipColor, fillShipColor);
	}

	// ***** ASTEROID

	private static class AsteroidImpl extends SpriteImpl {
		
		public AsteroidImpl(int x, float y, float vX, float vY, Supplier<Rectangle> bounds) {
			super(makeShape(x,y), bounds, false, Config.getColor("roidColorBorder"), Config.getColor("roidColorFill"));
			setVelocity(vX,vY);
		}
		private static Shape makeShape(int x, float y) {
			float minSize = Config.getFloat("roidSizeMin");
			float maxSize = Config.getFloat("roidSizeMax");
			float randomSize = random(minSize,maxSize);
			
			Ellipse2D ast = new Ellipse2D.Double(x,y,randomSize,randomSize);
			return ast;
		}
	}

	public static Sprite makeAsteroid(Supplier<Rectangle> bounds) {
		int boundX = (int)(bounds.get().getX() + bounds.get().getWidth());
		
		float minBoundY = (float)(Math.random()* bounds.get().getY());
		float maxBoundY = (float)( bounds.get().getHeight());
		float randomBoundY = random(minBoundY, maxBoundY);
		
		float minVelX = Config.getFloat("roidVelXMin");
		float maxVelX = Config.getFloat("roidVelXMax");
		float randomVelX = random(minVelX,maxVelX);
		
		float minVelY = Config.getFloat("roidVelYMin");
		float maxVelY = Config.getFloat("roidVelYMax");
		float randomVelY = random(minVelY,maxVelY);
		
		return new AsteroidImpl(boundX,randomBoundY,randomVelX,randomVelY,bounds);
	}
}
