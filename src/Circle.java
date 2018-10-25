import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Represents a circle that can be rendered and has some properties that can both grow and decay over time.
 *
 * @author Andriy Sheptunov
 * @since September 2018
 */
class Circle {
	// seeded random for reproducibility
	private final static Random random = new Random(123456);

	// stroke for drawing the circle, constant for all circles
	final static Stroke CIRCLE_STROKE = new BasicStroke(Config.CIRCLE_STROKE);
	// circle for rendering
	private Ellipse2D.Double circle;

	// dynamic radius of the circle, grows over time
	private double radius;
	// adjusted randomized decay rate
	private float decay = Config.CIRCLE_DECAY * (1 + random.nextInt(Math.round(Config.CHAOS * 1000)) / 1000f);
	// adjusted randomized growth rate
	private float growth = Config.CIRCLE_GROWTH * (1 + random.nextInt(Math.round(Config.CHAOS * 1000)) / 1000f);

	// color for the circle, decays in opacity over time
	private Color color;

	/**
	 * Creates (spawns) a new circle from the given amplitude in input data. The higher the amplitude, the bigger and
	 * more opaque the circle will be.
	 *
	 * @param amplitude the input amplitude
	 */
	Circle(double amplitude) {
		radius = Config.CIRCLE_RAD_MIN;
		// centered on (0,0) with radius MIN_RADIUS
		genCircle();
		color = new Color(
				Config.COLOR_CIRCLE_R / 255f,
				Config.COLOR_CIRCLE_G / 255f,
				Config.COLOR_CIRCLE_B / 255f,
				opacity(amplitude));
	}

	/**
	 * Calculates and returns the starting opacity for the circle color based on the input amplitude.
	 *
	 * @param amplitude the input amplitude
	 * @return the calculated opacity, in the range smooth(0.0) - smooth(1.0)
	 */
	private float opacity(double amplitude) {
		float output = (float) (amplitude / Config.AMP_ROOF);
		output = (output > 1.0f) ? 1.0f : output;
		return smooth(output);
	}

	/**
	 * Applies a smoothing function to input amplitude, and returns smoothed value. Smoothing amount configured by
	 * Config.SMOOTHING_AMT.
	 *
	 * @param amplitude the input amplitude to smooth
	 * @return the smoothed amplitude
	 */
	private float smooth(double amplitude) {
		double output = amplitude;
		if (Config.USE_SMOOTHING) {
			for (int i = 0; i < Config.SMOOTHING_AMT; i++) {
				output = Math.atan(output); // applies arc tan smoothing (sigmoid)
			}
			output = (output < 0) ? 0 : output; //
		}
		return (float) output;
	}

	/**
	 * Progresses the life of the circle, resulting in color decay and radius growth.
	 */
	void tick() {
		radius *= growth;
		genCircle();
		color = new Color(
				color.getRed() / 255f,
				color.getGreen() / 255f,
				color.getBlue() / 255f,
				color.getAlpha() / 255f * decay); // lowers opacity a tick each time
	}

	/**
	 * Generates a new circle from the current radius, centered in the middle of render output, configured by
	 * Config.DIM_WIDTH and Config.DIM_HEIGHT.
	 */
	private void genCircle() {
		circle = new Ellipse2D.Double(
				-radius + Config.DIM_WIDTH / 2,
				-radius + Config.DIM_HEIGHT / 2,
				radius * 2,
				radius * 2);
	}

	/**
	 * Renders the circle to the given graphics context.
	 *
	 * @param graphics the context to render to
	 */
	void draw(Graphics2D graphics) {
		graphics.setColor(color);
		graphics.draw(circle);
	}

	/**
	 * Returns the width of the circle in pixels.
	 *
	 * @return the width of the circle
	 */
	double width() {
		return circle.width;
	}

	/**
	 * Returns the opacity of the circle in the range 0 - 255.
	 * @return the opacity of the circle
	 */
	int opacity() {
		return color.getAlpha();
	}

}
