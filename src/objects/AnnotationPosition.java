package objects;

public class AnnotationPosition {
	double positionX;
	double positionY;

	public AnnotationPosition(double x, double y) {
		positionX = x;
		positionY = y;
	}

	public double returnPositionX() {
		return positionX;
	}

	public double returnPositionY() {
		return positionY;
	}
}
