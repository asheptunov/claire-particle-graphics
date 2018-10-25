import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class responsible for parsing config file and setting global rendering parameters.
 *
 * @author Andriy Sheptunov
 * @since September 2018
 */
class Config {
	// dimensions of the rendering frame
	static int DIM_WIDTH, DIM_HEIGHT;
	// non-data frames to add to the render
	static int FR_LEADING, FR_TRAILING;
	// color of emitted circle
	static int COLOR_CIRCLE_R, COLOR_CIRCLE_G, COLOR_CIRCLE_B;
	// color of background
	static int COLOR_BG_R, COLOR_BG_G, COLOR_BG_B;
	// whether to render a background
	static boolean USE_BG;
	// color to use for the overlay
	static int COLOR_OL_R, COLOR_OL_G, COLOR_OL_B;
	// whether to render an overlay
	static boolean USE_OVERLAY;
	// rate of circle property decay upon expansion
	static float CIRCLE_DECAY;
	// base rate of circle expansion
	static float CIRCLE_GROWTH;
	// rate of overlay radius decay over time
	static float OVERLAY_RAD_DECAY;
	// rate of overlay opacity decay over time
	static float OVERLAY_OP_DECAY;
	// rate at which overlay grows in size with pulses
	static float OVERLAY_GROWTH;
	// amount of randomness to apply to decay and growth factors
	static float CHAOS;
	// noise gate bound
	static float AMP_FLOOR;
	// level at which circle properties cap
	static float AMP_ROOF;
	// amount of smoothing to apply
	static int SMOOTHING_AMT;
	// whether to smooth data points for less harsh spikes
	static boolean USE_SMOOTHING;
	// radius at which circles start
	static float CIRCLE_RAD_MIN;
	// radius at which overlay starts
	static float OL_RAD_MAX;
	// min overlay radius decay point
	static float OL_RAD_MIN;
	// min overlay opacity decay point
	static float OL_OP_FLOOR;
	// stroke thickness for circles
	static float CIRCLE_STROKE;
	// file path of data json
	static String DATA_PATH;

	/**
	 * Creates a new config instance from the specified config JSON.
	 *
	 * @param filename the config file to parse
	 * @throws IOException if an i/o error has occurred
	 */
	Config(String filename) throws IOException {
		FileInputStream configStream = new FileInputStream(new File(filename));
		JSONObject configObject = new JSONObject(new JSONTokener(configStream));
		configStream.close();
		DIM_WIDTH = configObject.getInt("render_width");
		DIM_HEIGHT = configObject.getInt("render_height");
		FR_LEADING = configObject.getInt("leading_frames");
		FR_TRAILING = configObject.getInt("trailing_frames");
		String circleColor = configObject.getString("color_circle");
		COLOR_CIRCLE_R = Integer.parseInt(circleColor.substring(0, 2), 16);
		COLOR_CIRCLE_G = Integer.parseInt(circleColor.substring(2, 4), 16);
		COLOR_CIRCLE_B = Integer.parseInt(circleColor.substring(4, 6), 16);
		String bgColor = configObject.getString("color_background");
		COLOR_BG_R = Integer.parseInt(bgColor.substring(0, 2), 16);
		COLOR_BG_G = Integer.parseInt(bgColor.substring(2, 4), 16);
		COLOR_BG_B = Integer.parseInt(bgColor.substring(4, 6), 16);
		USE_BG = configObject.getBoolean("use_background");
		String olColor = configObject.getString("color_overlay");
		COLOR_OL_R = Integer.parseInt(olColor.substring(0, 2), 16);
		COLOR_OL_G = Integer.parseInt(olColor.substring(2, 4), 16);
		COLOR_OL_B = Integer.parseInt(olColor.substring(4, 6), 16);
		USE_OVERLAY = configObject.getBoolean("use_overlay");
		CIRCLE_DECAY = configObject.getFloat("circle_decay_rate");
		CIRCLE_GROWTH = configObject.getFloat("circle_growth_rate");
		OVERLAY_RAD_DECAY = configObject.getFloat("overlay_radius_decay_rate");
		OVERLAY_OP_DECAY = configObject.getFloat("overlay_opacity_decay_rate");
		OVERLAY_GROWTH = configObject.getFloat("overlay_radius_growth_rate");
		CHAOS = configObject.getFloat("circle_chaos");
		AMP_FLOOR = configObject.getFloat("audio_amplitude_floor");
		AMP_ROOF = configObject.getFloat("audio_amplitude_roof");
		SMOOTHING_AMT = configObject.getInt("smoothing_amount");
		USE_SMOOTHING = configObject.getBoolean("use_smoothing");
		CIRCLE_RAD_MIN = configObject.getInt("circle_radius_min");
		OL_RAD_MAX = configObject.getInt("overlay_radius_max");
		OL_RAD_MIN = configObject.getInt("overlay_radius_min");
		OL_OP_FLOOR = configObject.getFloat("overlay_opacity_floor");
		CIRCLE_STROKE = configObject.getFloat("stroke_circle");
		DATA_PATH = configObject.getString("data_path");
	}
}
