package com.lowp.canyonbunny;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.lowp.canyonbunny.util.Assets;
import com.lowp.canyonbunny.util.Constants;
import com.lowp.canyonbunny.util.GamePreferences;

/**
 * 负责绘制游戏世界中的所有对象
 * 
 * @author lowp
 *
 */
public class WorldRenderer implements Disposable {
	// 游戏世界相机
	private OrthographicCamera camera;
	// GUI相机
	private OrthographicCamera cameraGUI;

	private SpriteBatch batch;
	private WorldController worldController;

	// Box2d调试线
	private static final boolean DEBUG_DRAW_BOX2D_WORLD = false;
	private Box2DDebugRenderer b2debugRenderer;

	// 着色器
	private ShaderProgram shaderMonochrome;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init() {
		b2debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();

		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		// 翻转y轴
		cameraGUI.setToOrtho(true);
		cameraGUI.update();

		// 初始化着色器
		shaderMonochrome = new ShaderProgram(
				Gdx.files.internal(Constants.shaderMonochromeVertex),
				Gdx.files.internal(Constants.shaderMonochromeFragment));
		if (!shaderMonochrome.isCompiled()) {
			String msg = "Could not compile shader program: "
					+ shaderMonochrome.getLog();
			throw new GdxRuntimeException(msg);
		}
	}

	public void render() {
		renderWorld(batch);
		renderGui(batch);
	}

	// 绘制游戏世界
	private void renderWorld(SpriteBatch batch) {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// 着色器
		if (GamePreferences.instance.useMonochromeShader) {
			batch.setShader(shaderMonochrome);
			shaderMonochrome.setUniformf("u_amount", 1.0f);
		}

		// 渲染地图
		worldController.level.render(batch);

		batch.setShader(null);

		batch.end();

		// 绘制Box2d调试线
		if (DEBUG_DRAW_BOX2D_WORLD) {
			b2debugRenderer.render(worldController.b2world, camera.combined);
		}

	}

	// 绘制GUI
	private void renderGui(SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);

		batch.begin();
		// 绘制分数(左上)
		renderGuiScore(batch);
		// 绘制生命数量(右上)
		renderGuiExtraLive(batch);
		// 绘制FPS(右下)
		if (GamePreferences.instance.showFpsCounter)
			renderGuiFpsCounter(batch);
		// 绘制"Game Over!"
		renderGuiGameOverMessage(batch);
		// 绘制羽毛剩余时间
		renderGuiFeatherPowerup(batch);
		batch.end();
	}

	// 绘制分数
	private void renderGuiScore(SpriteBatch batch) {
		float x = -15;
		float y = -15;
		float offsetX = 50;
		float offsetY = 50;

		// 抖动特效
		if (worldController.scoreVisual < worldController.score) {
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
		batch.draw(Assets.instance.goldCoin.goldCoin, x, y, offsetX, offsetY,
				100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch, ""
				+ (int) worldController.scoreVisual, x + 75, y + 37);

	}

	// 绘制生命数量
	private void renderGuiExtraLive(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;

		for (int i = 0; i < Constants.LIVES_START; i++) {
			if (worldController.lives <= i && worldController.lives >= 0) {

				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			}
			batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120,
					100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}

		// 丢失生命时的特效
		if (worldController.lives >= 0
				&& worldController.livesVisual > worldController.lives) {
			int k = worldController.lives;

			float alphaColor = Math.max(0, worldController.livesVisual
					- worldController.lives - 0.5f);
			float alphaScale = 0.35f * (2 + worldController.lives - worldController.livesVisual) * 2;
			float alphaRotate = -45 * alphaColor;
			batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
			batch.draw(Assets.instance.bunny.head, x + k * 50, y, 50, 50, 120,
					100, alphaScale, -alphaScale, alphaRotate);

			batch.setColor(1, 1, 1, 1);
		}
	}

	// 绘制FPS
	private void renderGuiFpsCounter(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	// 绘制“GAME OVER”
	private void renderGuiGameOverMessage(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;

		if (worldController.isGameOver()) {
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver
					.draw(batch, "GAME OVER!", x, y, 0, Align.center, false);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}

	// 绘制羽毛剩余时间
	private void renderGuiFeatherPowerup(SpriteBatch batch) {
		float x = -15;
		float y = 30;
		float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
		if (timeLeftFeatherPowerup > 0) {
			// 小于4秒的时候产生一个闪烁效果，每秒闪烁5次
			if (timeLeftFeatherPowerup < 4) {

				if (((int) (timeLeftFeatherPowerup * 5) % 2) != 0) {
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100,
					0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);

			Assets.instance.fonts.defaultSmall.draw(batch, ""
					+ (int) timeLeftFeatherPowerup, x + 60, y + 57);
		}
	}

	public void resize(int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
		camera.update();

		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height)
				* (float) width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,
				cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();

	}

	@Override
	public void dispose() {
		batch.dispose();
		shaderMonochrome.dispose();
	}

}
