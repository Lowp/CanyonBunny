package com.lowp.canyonbunny.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * ��������������Ϸ�˵�������
 * 
 * @author lowp
 *
 */
public class GamePreferences {
	public static final String TAG = GamePreferences.class.getName();
	// ����
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

	// ������Ϸ����
	public void load() {
		soundPlay = prefs.getBoolean("sound", true);
		musicPlay = prefs.getBoolean("music", true);
		/**
		 * MathUtils.clamp(float a,float b,float c)
		 * a��ֵ������b��c֮�䣬���a<b�򷵻�b,a>c�򷵻�c,���򷵻�b
		 */
		volSound = MathUtils
				.clamp(prefs.getFloat("volSound", 0.5f), 0.0f, 1.0f);
		volMusic = MathUtils
				.clamp(prefs.getFloat("volMusic", 0.5f), 0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0), 0, 2);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
		useMonochromeShader = prefs.getBoolean("useMonochromeShader", false);
	}

	// ������Ϸ����
	public void save() {
		prefs.putBoolean("sound", soundPlay);
		prefs.putBoolean("music", musicPlay);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.putBoolean("useMonochromeShader", useMonochromeShader);
		// ��������ֵд�������ļ���ȷ�����ݳ־û�
		prefs.flush();
	}
}
