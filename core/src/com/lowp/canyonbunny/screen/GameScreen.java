package com.lowp.canyonbunny.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.lowp.canyonbunny.WorldController;
import com.lowp.canyonbunny.WorldRenderer;
import com.lowp.canyonbunny.util.GamePreferences;

/**
 * ��Ϸ����
 * 
 * @author lowp
 *
 */
public class GameScreen extends AbstractGameScreen {

	// ��־tag
	private static final String TAG = GameScreen.class.getName();
	// ��Ϸ�߼�������
	private WorldController worldController;
	// ��Ϸ��Ⱦ������
	private WorldRenderer worldRenderer;
	// ��Androidϵͳ�ϣ��ж���Ϸ�Ƿ���ͣ(ͻȻ���硢����Home�������)
	private boolean paused;

	public GameScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {
		// ������Ϸ����
		GamePreferences.instance.load();
		// ��ʼ����Ϸ�߼�������
		worldController = new WorldController(game);
		// ��ʼ����Ϸ��Ⱦ������
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
		paused = false;
	}

	@Override
	public void render(float deltaTime) {
		// �����Ϸû�б���ͣ���������Ϸ���磨�ȸ����߼����ٽ�����Ⱦ��
		if (!paused) {
			// ͨ��֡���ʸ�����Ϸ�����еĶ���
			worldController.update(Gdx.graphics.getDeltaTime());
		}
		// ������ɫΪ��ɫ
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f,
				0xff / 255.0f);
		// ����
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// ��Ⱦ��Ϸ�����е����ж���
		worldRenderer.render();
	}

	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	/**
	 * �ͷ���Ϸ��Դ
	 */
	@Override
	public void hide() {
		worldController.dispose();
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	/**
	 * ����Ϸ��ͣʱ(���硢����Home�������)������
	 */
	@Override
	public void pause() {
		paused = true;
	}

	/**
	 * ����Ϸ����ͣ״̬�ָ�ʱ������
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
