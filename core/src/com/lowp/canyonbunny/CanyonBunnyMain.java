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
 * 游戏入口类
 * 
 * @author lowp
 *
 */
public class CanyonBunnyMain extends DirectedGame {
	// 日志tag
	private static final String TAG = CanyonBunnyMain.class.getName();

	@Override
	public void create() {
		// 设置libdgx的日志级别为debug
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// 载入游戏配置
		GamePreferences.instance.load();

		// 载入游戏资源
		Assets.instance.init(new AssetManager());

		// 播放背景音乐
		AudioManager.instance.play(Assets.instance.music.song01);

		// 跳转到MenuScreen
		ScreenTransition transition = ScreenTransitionSlice.init(2,
				ScreenTransitionSlice.UP_DOWN, 10, Interpolation.pow5Out);
		setScreen(new MenuScreen(this), transition);

	}

}
