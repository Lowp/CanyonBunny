package com.lowp.canyonbunny.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * 本类用来保存游戏菜单的设置
 * 
 * @author lowp
 *
 */
public class GamePreferences {
	public static final String TAG = GamePreferences.class.getName();
	// 单例
	public static final GamePreferences instance = new GamePreferences();

	public boolean soundPlay;
	public boolean musicPlay;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	public boolean useMonochromeShader;
	private Preferences prefs;

	// singleton: prevent instantiation from other classes
	private GamePreferences() {
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	// 载入游戏配置
	public void load() {
		soundPlay = prefs.getBoolean("sound", true);
		musicPlay = prefs.getBoolean("music", true);
		/**
		 * MathUtils.clamp(float a,float b,float c)
		 * a的值必须在b和c之间，如果a<b则返回b,a>c则返回c,否则返回b
		 */
		volSound = MathUtils
				.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		volMusic = MathUtils
				.clamp(prefs.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0), 0, 2);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
		useMonochromeShader = prefs.getBoolean("useMonochromeShader", false);
	}

	// 保存游戏配置
	public void save() {
		prefs.putBoolean("sound", soundPlay);
		prefs.putBoolean("music", musicPlay);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.putBoolean("useMonochromeShader", useMonochromeShader);
		// 将变量的值写入配置文件，确保数据持久化
		prefs.flush();
	}
}
