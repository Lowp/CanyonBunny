package com.lowp.canyonbunny.screen;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.lowp.canyonbunny.util.Assets;

public abstract class AbstractGameScreen implements Screen {
	// 日志tag
	private static final String TAG = AbstractGameScreen.class.getName();
	protected DirectedGame game;
	public AbstractGameScreen(DirectedGame game) {
		this.game = game;
	}

//	public abstract void render(float deltaTime);
//
//	public abstract void resize(int width, int height);
//
//	public abstract void show();
//
//	public abstract void hide();

	public abstract InputProcessor getInputProcessor();

	/**
	 * 当游戏从暂停状态恢复时被调用
	 */
	public void resume() {
		Assets.instance.init(new AssetManager());
	}
	
	

	/**
	 * 退出游戏释放游戏资源
	 */
	public void dispose() {
		Assets.instance.dispose();
	}

}
