package com.lowp.canyonbunny.screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.lowp.canyonbunny.screen.transitions.ScreenTransition;

/**
 * 屏幕管理类，类似于Game类
 * 
 * @author lowp
 *
 */
public abstract class DirectedGame implements ApplicationListener {
	// 判断帧缓存对象是否被初始化
	private boolean init;
	// 当前屏幕
	private AbstractGameScreen currScreen;
	// 下一个屏幕
	private AbstractGameScreen nextScreen;
	// 当前屏幕的帧缓存对象
	private FrameBuffer currFbo;
	// 下一个屏幕的帧缓存对象
	private FrameBuffer nextFbo;

	private SpriteBatch batch;
	// 转场持续时间
	private float t;
	// 转场特效
	private ScreenTransition screenTransition;

	public void setScreen(AbstractGameScreen screen) {
		setScreen(screen, null);
	}

	public void setScreen(AbstractGameScreen screen,
			ScreenTransition screenTransition) {

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// 初始化
		if (!init) {
			currFbo = new FrameBuffer(Format.RGB888, w, h, false);
			nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
			batch = new SpriteBatch();
			init = true;
		}

		// 启动新的装场
		nextScreen = screen;
		// 激活下一个屏幕
		nextScreen.show();
		nextScreen.resize(w, h);
		// 让屏幕只渲染一次
		nextScreen.render(0);
		// 使当前屏幕处于暂停状态
		if (currScreen != null) {
			currScreen.pause();
		}
		// 是下一个屏幕处于暂停状态
		nextScreen.pause();
		// 禁用输入事件
		Gdx.input.setInputProcessor(null);

		this.screenTransition = screenTransition;
		t = 0;
	}

	@Override
	public void render() {
		
		// 使时间增量的上限为1/60s，得到一个稳定的时间步
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
		// 不转场
		if (nextScreen == null) {
			if (currScreen != null) {
				currScreen.render(deltaTime);
			}

		}
		// 转场
		else {
			// 转场持续时间
			float duration = 0;

			if (screenTransition != null)
				// 获取转场持续时间
				duration = screenTransition.getDuration();

			// 计算当前转场持续时间
			t = Math.min(t + deltaTime, duration);

			// 无转场特效或转场特效已完成
			if (screenTransition == null || t >= duration) {
				if (currScreen != null) {
					//  隐藏当前屏幕
					currScreen.hide();
				}
				// 恢复下一个屏幕
				nextScreen.resume();
				// 启用下一个屏幕的输入事件监听
				Gdx.input.setInputProcessor(nextScreen.getInputProcessor());

				// 切换屏幕
				currScreen = nextScreen;

				// 调用当前屏幕的render()方法
				nextScreen = null;
				screenTransition = null;
			} else {
				// 渲染屏幕纹理到帧缓存对象中

				currFbo.begin();
				if (currScreen != null)
					currScreen.render(deltaTime);
				currFbo.end();

				nextFbo.begin();
				nextScreen.render(deltaTime);
				nextFbo.end();

				// 绘制转场特效

				// (0~1)
				float alpha = t / duration;

				screenTransition.render(batch, currFbo.getColorBufferTexture(),
						nextFbo.getColorBufferTexture(), alpha);
			}

		}
	}

	@Override
	public void resize(int width, int height) {
		if (currScreen != null)
			currScreen.resize(width, height);
		if (nextScreen != null)
			nextScreen.resize(width, height);
	}

	@Override
	public void pause() {
		if (currScreen != null)
			currScreen.pause();
	}

	@Override
	public void resume() {
		if (currScreen != null)
			currScreen.resume();
	}

	@Override
	public void dispose() {
		if (currScreen != null)
			currScreen.hide();
		if (nextScreen != null)
			nextScreen.hide();
		if (init) {
			currFbo.dispose();
			currScreen = null;
			nextFbo.dispose();
			nextScreen = null;
			batch.dispose();
			init = false;
		}
	}
}
