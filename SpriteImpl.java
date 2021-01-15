/**
	A game inspired by Gradius
	@author Orvin Tritama < orvinrfc at hotmail dot com >
	@version 2018-11-19
*/
import java.awt.*;
import java.awt.geom.*;
import java.util.function.Supplier;

public class SpriteImpl implements Sprite {

	// drawing
	private Shape shape;
	private final Color border;
	private final Color fill;

	// movement
	private float dx, dy;
	private final Supplier<Rectangle> bounds;
	private final boolean isBoundsEnforced;

	protected SpriteImpl(Shape shape, Supplier<Rectangle> bounds, boolean boundsEnforced, Color border, Color fill) {
		this.shape = shape;
		this.bounds = bounds;
		this.isBoundsEnforced = boundsEnforced;
		this.border = border;
		this.fill = fill;
	}

	public Shape getShape() {
		return shape;
	}

	public void setVelocity(float dxPerMilli, float dyPerMilli) {
		dx = dxPerMilli;
		dy = dyPerMilli;
	}

	public void update(int millis) {
		Shape movedShape = AffineTransform.getTranslateInstance(dx*millis,dy*millis).
				createTransformedShape(shape);
		if( this.isInBounds(movedShape) && isBoundsEnforced == true ){
			this.shape = movedShape;
		}
		
		if( isBoundsEnforced == false){
			this.shape = movedShape;
		}
	}

	public boolean isInBounds() {
		return isInBounds(this.getShape() );
	}
	private boolean isInBounds(Shape s) {
		Rectangle toCheckBounds = bounds.get().getBounds();
		Rectangle boundsOfShape = s.getBounds();
		if( toCheckBounds.contains(boundsOfShape) ){
			return true;
		}
		return false;
	}
	
	public boolean isOutOfBounds() {
		return shape.getBounds().getX() < 0 ;
	}

	public void draw(Graphics2D g2) {
		g2.setColor(fill);
		g2.fill(this.getShape());
		g2.setColor(border);
		g2.draw(this.getShape());
	}

	public boolean intersects(Sprite other) {
		return shape.intersects(other.getShape().getBounds2D());
	}
	private boolean intersects(Shape other) {
		Rectangle boundsIntersect = other.getBounds();
		if (this.shape.intersects(boundsIntersect)){
			Area thisShape = new Area(this.getShape());
			Area otherShape = new Area(other);
			return intersects(thisShape,otherShape);
		}
		return false;
	}
	
	private static boolean intersects(Area a, Area b) {
		return a.isEmpty() && b.isEmpty();
	}
}
