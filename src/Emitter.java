import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the spawning point for all particle-type objects. Has the ability to spawn new objects, increment their
 * life over time, and render them.
 *
 * @author Andriy Sheptunov
 * @since September 2018
 */
class Emitter {
	// queue of existing circles
	private Queue<Circle> circles; // will be iterated over in entirety every time
	// singular immortal pulsing circle
	private Pulser pulser; // single pulsing circle

	/**
	 * Creates a new emitter with no Circles and a single Pulser.
	 */
	Emitter() {
		circles = new LinkedList<>();
		pulser = new Pulser();
	}

	/**
	 * Emits particles or motivates behavior similar to emission, based on a specified amplitude data point.
	 *
	 * @param amp the input amplitude
	 */
	void emit(double amp) {
		if (amp >= Config.AMP_FLOOR) { // don't emit anything below the cutoff, essentially a noise gate
			Circle generated = new Circle(amp);
			circles.add(generated);
			pulser.bump(generated);
		}
	}

	/**
	 * Progresses the lifespan of all existing particles, removing unnecessary particles to encourage linear
	 * render times.
	 */
	void tick() {
		Iterator<Circle> i = circles.iterator();
		while (i.hasNext()) {
			Circle c = i.next();
			c.tick();
			if (c.width() > Config.DIM_WIDTH) i.remove(); // removes circles that have expanded past viewable area to conserve memory
		}
		pulser.tick();
	}

	/**
	 * Renders all existing particles to the specified graphics context.
	 *
	 * @param graphics the graphics context to render in
	 */
	void render(Graphics2D graphics) {
		graphics.setStroke(Circle.CIRCLE_STROKE);
		for (Circle c : circles) {
			c.draw(graphics);
		}
		if (Config.USE_OVERLAY){
			pulser.draw(graphics);
		}
	}
}
