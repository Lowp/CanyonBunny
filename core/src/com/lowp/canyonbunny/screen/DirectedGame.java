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
 * ��Ļ�����࣬������Game��
 * 
 * @author lowp
 *
 */
public abstract class DirectedGame implements ApplicationListener {
	// �ж�֡��������Ƿ񱻳�ʼ��
	private boolean init;
	// ��ǰ��Ļ
	private AbstractGameScreen currScreen;
	// ��һ����Ļ
	private AbstractGameScreen nextScreen;
	// ��ǰ��Ļ��֡�������
	private FrameBuffer currFbo;
	// ��һ����Ļ��֡�������
	private FrameBuffer nextFbo;

	private SpriteBatch batch;
	// ת������ʱ��
	private float t;
	// ת����Ч
	private ScreenTransition screenTransition;

	public void setScreen(AbstractGameScreen screen) {
		setScreen(screen, null);
	}

	public void setScreen(AbstractGameScreen screen,
			ScreenTransition screenTransition) {

		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		// ��ʼ��
		if (!init) {
			currFbo = new FrameBuffer(Format.RGB888, w, h, false);
			nextFbo = new FrameBuffer(Format.RGB888, w, h, false);
			batch = new SpriteBatch();
			init = true;
		}

		// �����µ�װ��
		nextScreen = screen;
		// ������һ����Ļ
		nextScreen.show();
		nextScreen.resize(w, h);
		// ����Ļֻ��Ⱦһ��
		nextScreen.render(0);
		// ʹ��ǰ��Ļ������ͣ״̬
		if (currScreen != null) {
			currScreen.pause();
		}
		// ����һ����Ļ������ͣ״̬
		nextScreen.pause();
		// ���������¼�
		Gdx.input.setInputProcessor(null);

		this.screenTransition = screenTransition;
		t = 0;
	}

	@Override
	public void render() {
		
		// ʹʱ������������Ϊ1/60s���õ�һ���ȶ���ʱ�䲽
		float deltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 60.0f);
		// ��ת��
		if (nextScreen == null) {
			if (currScreen != null) {
				currScreen.render(deltaTime);
			}

		}
		// ת��
		else {
			// ת������ʱ��
			float duration = 0;

			if (screenTransition != null)
				// ��ȡת������ʱ��
				duration = screenTransition.getDuration();

			// ���㵱ǰת������ʱ��
			t = Math.min(t + deltaTime, duration);

			// ��ת����Ч��ת����Ч�����
			if (screenTransition == null || t >= duration) {
				if (currScreen != null) {
					//  ���ص�ǰ��Ļ
					currScreen.hide();
				}
				// �ָ���һ����Ļ
				nextScreen.resume();
				// ������һ����Ļ�������¼�����
				Gdx.input.setInputProcessor(nextScreen.getInputProcessor());

				// �л���Ļ
				currScreen = nextScreen;

				// ���õ�ǰ��Ļ��render()����
				nextScreen = null;
				screenTransition = null;
			} else {
				// ��Ⱦ��Ļ����֡���������

				currFbo.begin();
				if (currScreen != null)
					currScreen.render(deltaTime);
				currFbo.end();

				nextFbo.begin();
				nextScreen.render(deltaTime);
				nextFbo.end();

				// ����ת����Ч

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
