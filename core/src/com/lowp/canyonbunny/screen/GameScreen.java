package com.lowp.canyonbunny.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.lowp.canyonbunny.WorldController;
import com.lowp.canyonbunny.WorldRenderer;
import com.lowp.canyonbunny.util.GamePreferences;

/**
 * 游戏界面
 * 
 * @author lowp
 *
 */
public class GameScreen extends AbstractGameScreen {

	// 日志tag
	private static final String TAG = GameScreen.class.getName();
	// 游戏逻辑控制器
	private WorldController worldController;
	// 游戏渲染控制器
	private WorldRenderer worldRenderer;
	// 在Android系统上，判断游戏是否被暂停(突然来电、按下Home键等情况)
	private boolean paused;

	public GameScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {
		// 加载游戏设置
		GamePreferences.instance.load();
		// 初始化游戏逻辑控制器
		worldController = new WorldController(game);
		// 初始化游戏渲染控制器
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
		paused = false;
	}

	@Override
	public void render(float deltaTime) {
		// 如果游戏没有被暂停，则更新游戏世界（先更新逻辑，再进行渲染）
		if (!paused) {
			// 通过帧速率更新游戏世界中的对象
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// 清屏颜色为蓝色
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f,
				0xff / 255.0f);
		// 清屏
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// 渲染游戏世界中的所有对象
		worldRenderer.render();
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	/**
	 * 释放游戏资源
	 */
	@Override
	public void hide() {
		worldController.dispose();
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	/**
	 * 当游戏暂停时(来电、按下Home键等情况)被调用
	 */
	@Override
	public void pause() {
		paused = true;
	}

	/**
	 * 当游戏从暂停状态恢复时被调用
	 */
	@Override
	public void resume() {
		paused = false;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return worldController;
	}

}
