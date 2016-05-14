package com.lowp.canyonbunny;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.lowp.canyonbunny.screen.DirectedGame;
import com.lowp.canyonbunny.screen.MenuScreen;
import com.lowp.canyonbunny.screen.ScreenTransitionSlice;
import com.lowp.canyonbunny.screen.transitions.ScreenTransition;
import com.lowp.canyonbunny.util.Assets;
import com.lowp.canyonbunny.util.AudioManager;
import com.lowp.canyonbunny.util.GamePreferences;

/**
 * ��Ϸ�����
 * 
 * @author lowp
 *
 */
public class CanyonBunnyMain extends DirectedGame {
	// ��־tag
	private static final String TAG = CanyonBunnyMain.class.getName();

	@Override
	public void create() {
		// ����libdgx����־����Ϊdebug
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// ������Ϸ����
		GamePreferences.instance.load();

		// ������Ϸ��Դ
		Assets.instance.init(new AssetManager());

		// ���ű�������
		AudioManager.instance.play(Assets.instance.music.song01);

		// ��ת��MenuScreen
		ScreenTransition transition = ScreenTransitionSlice.init(2,
				ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);

	}

}
