import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * A complex particle that pulses (grows) more with increased amplitudes, and decays over time otherwise. Provides
 * tools for incrementing lifespan, as well as rendering.
 *
 * @author Andriy Sheptunov
 * @since September 2018
 */
public class Pulser {
	// underlying circle for rendering
	private Ellipse2D.Double circle;

	// radius of middle circle which decays over time and can increase with bumps
	private double radius;
	// exponential decay factor for reducing radius
	private float rad_decay = Config.OVERLAY_RAD_DECAY;
	// same as previous but for opacity
	private float op_decay = Config.OVERLAY_OP_DECAY;
	// actual peak opacity with smoothing applied
	private double opacity_peak;

	// color of the pulser, can change over time
	private Color color;

	/**
	 * Creates (spawns) a new pulser with radius Config.OL_RAD_MIN, and color as specified by Config.
	 */
	Pulser() {
		radius = Config.OL_RAD_MIN;
		genCircle();
		opacity_peak = 1;
		for (int i = 0; i < Config.SMOOTHING_AMT; i++) {
			opacity_peak = Math.atan(opacity_peak);
		}
		color = new Color(
				Config.COLOR_OL_R / 255f,
				Config.COLOR_OL_G / 255f,
				Config.COLOR_OL_B / 255f,
				Config.OL_OP_FLOOR);
	}

	/**
	 * Bumps / grows the pulser in size, based on a given Circle. The Circle is assumed to be a representation of a
	 * newly-created, largest circle in the simulation. Growth can be configured using Config.OVERLAY_GROWTH.
	 *
	 * @param peak the circle to grow based on; Bigger and more opaque circles will cause more growth
	 */
	void bump(Circle peak) {
		// [METHOD 1] teleport
//		radius = Config.OL_RAD_MAX;

		// [METHOD 2] linearly scaled teleport
//		radius = Config.OL_RAD_MIN + (peak.opacity() / 255f / opacity_peak) * (Config.OL_RAD_MAX - Config.OL_RAD_MIN);

		// [METHOD 3] exponential growth
//		radius *= Config.OVERLAY_GROWTH;

		// [METHOD 4] linearly scaled exponential growth
		float growth = 1f + (peak.opacity() / 255f / (float) opacity_peak) * (Config.OVERLAY_GROWTH - 1f);
		radius *= growth;

		radius = (radius > Config.OL_RAD_MAX) ? Config.OL_RAD_MAX : radius; // cap out the radius

		genCircle();
		color = new Color(
				color.getRed() / 255f,
				color.getGreen() / 255f,
				color.getBlue() / 255f,
				(float) opacity_peak / 10f
		);
	}

	/**
	 * Progresses the lifespan of the pulser, causing decay in its opacity and radius.
	 */
	void tick() {
		radius *= rad_decay;
		radius = (radius < Config.OL_RAD_MIN) ? Config.OL_RAD_MIN : radius; // bottom out the radius
		genCircle();
		float opacity = color.getAlpha() * op_decay / 255f;
		opacity = (opacity < Config.OL_OP_FLOOR) ? Config.OL_OP_FLOOR : opacity;
		color = new Color(
				color.getRed() / 255f,
				color.getGreen() / 255f,
				color.getBlue() / 255f,
				opacity);
	}

	/**
	 * Generates the underlying circle from the current radius, centered in the middle of the screen (configured using
	 * Config.DIM_WIDTH and Config.DIM_HEIGHT).
	 */
	private void genCircle() {
		circle = new Ellipse2D.Double(
				-radius + Config.DIM_WIDTH / 2,
				-radius + Config.DIM_HEIGHT / 2,
				radius * 2,
				radius * 2);
	}

	/**
	 * Renders the pulser to the given graphics context.
	 *
	 * @param graphics the graphics context to render to
	 */
	void draw(Graphics2D graphics) {
		graphics.setColor(color);
		graphics.fill(circle);
	}

}
