package com.lowp.canyonbunny;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.utils.Disposable;
import com.lowp.canyonbunny.objects.BunnyHead;
import com.lowp.canyonbunny.objects.BunnyHead.JUMP_STATE;
import com.lowp.canyonbunny.objects.Carrot;
import com.lowp.canyonbunny.objects.Feather;
import com.lowp.canyonbunny.objects.GoldCoin;
import com.lowp.canyonbunny.objects.Rock;
import com.lowp.canyonbunny.screen.DirectedGame;
import com.lowp.canyonbunny.screen.MenuScreen;
import com.lowp.canyonbunny.screen.ScreenTransitionSlide;
import com.lowp.canyonbunny.screen.transitions.ScreenTransition;
import com.lowp.canyonbunny.util.Assets;
import com.lowp.canyonbunny.util.AudioManager;
import com.lowp.canyonbunny.util.CameraHelper;
import com.lowp.canyonbunny.util.Constants;
import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

/**
 * 1.ͨ�� CameraHelper�࣬������Ϸ�����е������߼��� 2.�̳�InputAdapter���������¼����м���
 * 
 * @author lowp
 *
 */
public class WorldController extends InputAdapter implements Disposable {
	// ��־tag
	private static final String TAG = WorldController.class.getName();

	private DirectedGame game;
	
	public WorldRenderer worldRenderer;
	
	public CameraHelper cameraHelper;

	public Level level;
	// ���������
	public int lives;
	// ��ҵ÷�
	public int score;
	// ��¼��ǰ��������
	public float livesVisual;
	// ��¼��ǰ�÷�
	public float scoreVisual;
	// ��ײ����
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	// ��Ϸ�����������¿�ʼ��Ϸ֮��ļ��ʱ��
	private float timeLeftGameOverDelay;
	// Box2d��������
	public World b2world;
	// �Ƿ񵽴��յ�
	private boolean goalReached;
	// �ж��豸�Ƿ���м��ٶȴ�����
	private boolean accelerometerAvailable;

	public WorldController(DirectedGame game) {
		this.game = game;
		init();
	}

	/**
	 * ��ʼ����Ϸ�����е����ж���
	 */
	private void init() {
		cameraHelper = new CameraHelper();
		// �ж��豸�Ƿ���м��ٶȴ�����
		accelerometerAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.Accelerometer);
		// ��ʼ���������ֵ
		lives = Constants.LIVES_START;
		livesVisual = lives;
		// ��ʼ����Ϸ��������ӳ�ʱ��
		timeLeftGameOverDelay = 0;
		// ��ʼ����ͼ
		initLevel();
	}

	// ��ʼ����Ϸ���еĲ�
	private void initLevel() {
		// ��ʼ������
		score = 0;
		scoreVisual = score;
		// ��ʼ����ͼ
		level = new Level(Constants.LEVEL_01);
		// �����������
		cameraHelper.setTarget(level.bunnyHead);
		// ��ʼ����������
		initPhysics();
	}

	/**
	 * ������Ϸ�����߼� deltaTime:��Ⱦʱ����ǰ֡����һ֡��ʱ���
	 */
	public void update(float deltaTime) {
		// �ڸ��������߼�֮ǰ���Ƚ��������¼��������Ա�֤�û��������¼��������ȱ�����
		// handleDebugInput(deltaTime);

		// �ж���Ϸ�ǽ���
		if (isGameOver() || goalReached) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
				// �����Ϸ���������ص��˵�����
				backToMenu();
		} else {
			handleInputGame(deltaTime);
		}

		// ������Ϸ��ͼ�����ж���״̬
		level.update(deltaTime);
		// ��ײ���
		testCollisions();
		// ������������
		b2world.step(deltaTime, 8, 3);
		// �������״̬
		cameraHelper.update(deltaTime);
		// ��������������
		if (!isGameOver() && isPlayerInWater()) {
			// ������Ч
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
			lives--;
			// �����������λ0������Ϸ����
			if (isGameOver()) {
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			} else {
				// ������ǵ���ˮ������������Ϊ0
				initLevel();
			}
		}

		// ɽ������������
		level.mountains.updateScrollPosition(cameraHelper.getPosition());

		// ������������
		if (livesVisual > lives) {
			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
		}

		// ���·���
		if (scoreVisual < score)
			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);

	}

	// ��ʼ����������
	private void initPhysics() {
		if (b2world != null) {
			b2world.dispose();
		}
		// ����2ά�������磬�������ٶ�Ϊ-9.81m/s^2
		b2world = new World(new Vector2(0, -9.81f), true);

		// ��ʯ
		Vector2 origin = new Vector2();
		for (Rock rock : level.rocks) {
			// BodyDef������й���һ�������������������
			BodyDef bodyDef = new BodyDef();
			// ��������
			bodyDef.type = BodyType.KinematicBody;
			// ����λ��
			bodyDef.position.set(rock.position);
			// ��������
			Body body = b2world.createBody(bodyDef);
			rock.body = body;

			// �������ο�
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f,
					rock.bounds.height / 2.0f, origin, 0);

			// �о�:����״�͸���󶨵�һ��
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);

			polygonShape.dispose();
		}

	}

	/**
	 * ���ز˵�����
	 */
	private void backToMenu() {

		ScreenTransition transition = ScreenTransitionSlide.init(0.75f,
				ScreenTransitionSlide.DOWN, false, Interpolation.bounceOut);

		game.setScreen(new MenuScreen(game), transition);
	}

	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

		if (!cameraHelper.hasTarget(level.bunnyHead)) {
			// ��������ƶ�
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT))
				moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
				moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);

			// �����������
			float camZoomSpeed = 1 * deltaTime;
			float camZoomSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camZoomSpeed *= camZoomSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.COMMA))
				cameraHelper.addZoom(camZoomSpeed);
			if (Gdx.input.isKeyPressed(Keys.PERIOD))
				cameraHelper.addZoom(-camZoomSpeed);
			if (Gdx.input.isKeyPressed(Keys.SLASH))
				cameraHelper.setZoom(1);
		}

	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	private void handleInputGame(float deltaTime) {
		if (cameraHelper.hasTarget(level.bunnyHead)) {
			// �����ƶ�
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			} else {
				// ���ƶ�ƽ̨��
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					// �ж��豸������м��ٶȴ�����
					if (accelerometerAvailable) {
						// normalize accelerometer values from [-10, 10] to [-1,
						// 1]
						// which translate to rotations of [-90, 90] degrees
						float amount = Gdx.input.getAccelerometerY() / 10.0f;
						amount *= 90.0f;
						// �Ƿ�ﵽ��С���ٶȣ�
						if (Math.abs(amount) < Constants.ACCEL_ANGLE_DEAD_ZONE) {
							amount = 0;
						} else {
							// use the defined max angle of rotation instead of
							// the full 90 degrees for maximum velocity
							amount /= Constants.ACCEL_MAX_ANGLE_MAX_MOVEMENT;
						}
						level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x
								* amount;
					} else {
						level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
					}
				}
			}
			// ������Ծ
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
				level.bunnyHead.setJumping(true);
			} else {
				level.bunnyHead.setJumping(false);
			}
		}

		// �����������
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA))
			cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD))
			cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH))
			cameraHelper.setZoom(1);

	}

	@Override
	public boolean keyUp(int keycode) {
		// ������Ϸ����
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		} else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null
					: level.bunnyHead);

			Gdx.app.debug(TAG,
					"Camera follow enabled: " + cameraHelper.hasTarget());
		}// �������˵�
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}

		return false;
	}

	// ��ײ���
	private void testCollisions() {
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y,
				level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);
		// Test collision: Bunny Head <-> Rocks
		for (Rock rock : level.rocks) {
			r2.set(rock.position.x, rock.position.y, rock.bounds.width,
					rock.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}

		// Test collision: Bunny Head <-> Gold Coins
		for (GoldCoin goldcoin : level.goldcoins) {
			
			if (goldcoin.collected)
				continue;
			r2.set(goldcoin.position.x, goldcoin.position.y,
					goldcoin.bounds.width, goldcoin.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyWithGoldCoin(goldcoin);
			break;
		}
		// Test collision: Bunny Head <-> Feathers
		for (Feather feather : level.feathers) {
			if (feather.collected)
				continue;
			r2.set(feather.position.x, feather.position.y,
					feather.bounds.width, feather.bounds.height);
			if (!r1.overlaps(r2))
				continue;
			onCollisionBunnyWithFeather(feather);
			break;
		}

		// Test collision: Bunny Head <-> Goal
		if (!goalReached) {
			r2.set(level.goal.bounds);
			r2.x += level.goal.position.x;
			r2.y += level.goal.position.y;
			if (r1.overlaps(r2))
				onCollisionBunnyWithGoal();
		}
	}

	// ���Ǻ���ʯ��ײ
	private void onCollisionBunnyHeadWithRock(Rock rock) {
		BunnyHead bunnyHead = level.bunnyHead;

		// Math.abs(float a)����a���෴��
		// ������ʯƽ̨�ľ���
		float heightDifference = Math.abs(bunnyHead.position.y
				- (rock.position.y + rock.bounds.height));

		if (heightDifference > 0.25f) {
			Gdx.app.log("scx", "ok");
			boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
			if (hitRightEdge) {
				bunnyHead.position.x = rock.position.x + rock.bounds.width;
			} else {
				bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
			}

			return;
		}

		switch (bunnyHead.jumpState) {
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height
					+ bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_RISING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height
					+ bunnyHead.origin.y;
			break;
		}
	}

	// ���Ǻͽ����ײ
	private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
		goldcoin.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
		score += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
	};

	// ���Ǻ���ë��ײ
	private void onCollisionBunnyWithFeather(Feather feather) {
		feather.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		Gdx.app.log(TAG, "Feather collected");
	};

	// ���ǵ����յ�
	private void onCollisionBunnyWithGoal() {
		goalReached = true;
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
		Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
		centerPosBunnyHead.x += level.bunnyHead.bounds.width;
		spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX,
				Constants.CARROTS_SPAWN_RADIUS);
	}

	// �������ܲ�
	private void spawnCarrots(Vector2 pos, int numCarrots, float radius) {
		// ���ܲ���״��С1/2
		float carrotShapeScale = 0.5f;

		// create carrots with box2d body and fixture
		for (int i = 0; i < numCarrots; i++) {
			Carrot carrot = new Carrot();

			/**
			 * �������λ�ã���ת�ǶȺ����ű�
			 */
			// ˮƽλ�÷�Χ
			float x = MathUtils.random(-radius, radius);
			// ����λ�÷�Χ(ʼ����������ӿ���)
			float y = MathUtils.random(5.0f, 15.0f);
			// ��ת�Ƕȷ�Χ
			float rotation = MathUtils.random(0.0f, 360.0f)
					* MathUtils.degreesToRadians;
			// ���űȷ�Χ
			float carrotScale = MathUtils.random(0.5f, 1.5f);
			carrot.scale.set(carrotScale, carrotScale);

			/**
			 * ���ݺ��ܲ�λ�ú���ת�Ƕȴ�������
			 */
			BodyDef bodyDef = new BodyDef();
			// λ��
			bodyDef.position.set(pos);
			// �������x,y
			bodyDef.position.add(x, y);
			// ��ת�Ƕ�
			bodyDef.angle = rotation;
			Body body = b2world.createBody(bodyDef);
			// ��������
			body.setType(BodyType.DynamicBody);
			carrot.body = body;

			/**
			 * Ϊ���ܲ����幹������
			 */
			PolygonShape polygonShape = new PolygonShape();
			float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
			float halfHeight = carrot.bounds.height / 2.0f * carrotScale;
			polygonShape.setAsBox(halfWidth * carrotShapeScale, halfHeight
					* carrotShapeScale);

			// �����о�
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			// �ܶ�
			fixtureDef.density = 50;
			// �ָ���
			fixtureDef.restitution = 0.5f;
			// Ħ����
			fixtureDef.friction = 0.5f;
			// �󶨼о�
			body.createFixture(fixtureDef);

			polygonShape.dispose();

			// ��Ӻ��ܲ��������б�
			level.carrots.add(carrot);
		}
	}

	// �ж���Ϸ�Ƿ����
	public boolean isGameOver() {
		return lives < 0;
	}

	// �ж������Ƿ����ˮ��
	public boolean isPlayerInWater() {
		/**
		 * ˮ���y����λ����Ļ�ĵױ�Ե(y=0)
		 */
		return level.bunnyHead.position.y < -5;
	}

	@Override
	public void dispose() {
		if (b2world != null) {
			b2world.dispose();
		}

	}

}
