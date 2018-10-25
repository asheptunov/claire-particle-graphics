import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Main logic and time progression loop that runs a particle simulation of Circles and Pulsers.
 *
 * @author Andriy Sheptunov
 * @since September 2018
 */
class Renderer {
	private Emitter emitter;

	// rectangle that clears the frame
	private static Rectangle2D.Double BACKGROUND;
	private static Color BACKGROUND_COLOR;
	// overlay that goes atop the emitter
	private static Ellipse2D.Double OVERLAY;

	public static void main(String[] args) {
		new Renderer();
	}

	private Renderer() {
		// pull in config
		try {
			new Config("src/properties.json");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		BACKGROUND = new Rectangle2D.Double(0, 0, Config.DIM_WIDTH, Config.DIM_HEIGHT);
		BACKGROUND_COLOR = new Color(Config.COLOR_BG_R, Config.COLOR_BG_G, Config.COLOR_BG_B);
		OVERLAY = new Ellipse2D.Double(
				-Config.CIRCLE_RAD_MIN + Config.DIM_WIDTH / 2,
				-Config.CIRCLE_RAD_MIN + Config.DIM_HEIGHT / 2,
				Config.CIRCLE_RAD_MIN * 2,
				Config.CIRCLE_RAD_MIN * 2);

		emitter = new Emitter();

		Map<Integer, Double> timings = null;
		try {
			timings = readTimings();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			run(timings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<Integer, Double> readTimings() throws FileNotFoundException {
		Map<Integer, Double> timings = new TreeMap<>();
		JSONObject timingsObject = new JSONObject(new JSONTokener(new FileInputStream(new File(Config.DATA_PATH))));
		JSONArray timingsArray = timingsObject.getJSONArray("timecodes");

		for (int i = 0; i < timingsArray.length(); i++) {
			JSONObject timing = timingsArray.getJSONObject(i);
			timings.put((int) timing.getFloat("time"), timing.getDouble("amplitude"));
		}
		return timings;
	}

	private void run(Map<Integer, Double> timings) throws IOException {
		Queue<Integer> timestamps = new PriorityQueue<>(timings.keySet());
		// initial time; time increments by 1 every time (new rendered / ticked frame per loop), but emits only on every
		// recorded timestamp; i.e. if timestamps are 1 and 3, frame 1 will have emission, tick, and render; frame 2
		// will only tick and render, frame 3 will emit, tick, and render, and loop will exit
		ProgressBar progress = new ProgressBar(13, timestamps.size() + Config.FR_LEADING + Config.FR_TRAILING);

		for (int i = 0; i < Config.FR_LEADING; i++) { // leading frames
			emitter.tick();
			writeFile(i - Config.FR_LEADING);
			progress.step();
		}
		int time = 0;
		while (!timestamps.isEmpty()) {
			if (time == timestamps.element()) { // need to re-emit
				emitter.emit(timings.get(timestamps.poll()));
				progress.step();
			}
			emitter.tick(); // tick every time regardless or whether we're re-rendering or not
			writeFile(time);
			time++;
		}
		for (int i = 0; i < Config.FR_TRAILING; i++) { // trailing frames
			emitter.tick();
			writeFile(time + i);
			progress.step();
		}
		progress.finish();
	}

	private boolean firstWrite = true;
	private int fileNumberingPhase;

	private void writeFile(int time) throws IOException {
		if (firstWrite) {
			firstWrite = false;
			fileNumberingPhase = -time;
		}
		File frameFile = new File("renders/frame" + (time + fileNumberingPhase) + ".png");
		frameFile.mkdirs();
		frameFile.delete();
		frameFile.createNewFile();
		BufferedImage frameContent = render();
		ImageIO.write(frameContent, "png", frameFile);
	}

	private BufferedImage render() {
		BufferedImage output = new BufferedImage(Config.DIM_WIDTH, Config.DIM_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = output.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		clear(graphics);
		emitter.render(graphics);
//		overlay(graphics);
		return output;
	}


	/**
	 * Clears the specified graphics context by drawing a blank frame
	 *
	 * @param graphics the graphics context to clear
	 */
	private void clear(Graphics2D graphics) {
		if (Config.USE_BG) {
			graphics.setColor(BACKGROUND_COLOR);
			graphics.fill(BACKGROUND); // fill don't draw retard lmao
		}
	}

//	/**
//	 * Draws the center overlay
//	 *
//	 * @param graphics the graphics context to draw the overlay on
//	 */
//	private void overlay(Graphics2D graphics) {
//		if (Config.USE_OVERLAY) {
//			graphics.setColor(BACKGROUND_COLOR);
//			graphics.fill(OVERLAY);
//		}
//	}
}
