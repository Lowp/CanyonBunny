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
 * 1.通过 CameraHelper类，处理游戏世界中的所有逻辑， 2.继承InputAdapter，对输入事件进行监听
 * 
 * @author lowp
 *
 */
public class WorldController extends InputAdapter implements Disposable {
	// 日志tag
	private static final String TAG = WorldController.class.getName();

	private DirectedGame game;
	
	public WorldRenderer worldRenderer;
	
	public CameraHelper cameraHelper;

	public Level level;
	// 玩家生命数
	public int lives;
	// 玩家得分
	public int score;
	// 记录当前生命数量
	public float livesVisual;
	// 记录当前得分
	public float scoreVisual;
	// 碰撞矩形
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	// 游戏结束后与重新开始游戏之间的间隔时间
	private float timeLeftGameOverDelay;
	// Box2d物理世界
	public World b2world;
	// 是否到达终点
	private boolean goalReached;
	// 判断设备是否具有加速度传感器
	private boolean accelerometerAvailable;

	public WorldController(DirectedGame game) {
		this.game = game;
		init();
	}

	/**
	 * 初始化游戏世界中的所有对象
	 */
	private void init() {
		cameraHelper = new CameraHelper();
		// 判断设备是否具有加速度传感器
		accelerometerAvailable = Gdx.input
				.isPeripheralAvailable(Peripheral.Accelerometer);
		// 初始化玩家生命值
		lives = Constants.LIVES_START;
		livesVisual = lives;
		// 初始化游戏结束后的延迟时间
		timeLeftGameOverDelay = 0;
		// 初始化地图
		initLevel();
	}

	// 初始化游戏所有的层
	private void initLevel() {
		// 初始化分数
		score = 0;
		scoreVisual = score;
		// 初始化地图
		level = new Level(Constants.LEVEL_01);
		// 相机跟随主角
		cameraHelper.setTarget(level.bunnyHead);
		// 初始化物理世界
		initPhysics();
	}

	/**
	 * 更新游戏世界逻辑 deltaTime:渲染时，当前帧与上一帧的时间差
	 */
	public void update(float deltaTime) {
		// 在更新所有逻辑之前，先进行输入事件监听，以保证用户的输入事件可以最先被处理
		// handleDebugInput(deltaTime);

		// 判断游戏是结束
		if (isGameOver() || goalReached) {
			timeLeftGameOverDelay -= deltaTime;
			if (timeLeftGameOverDelay < 0)
				// 如果游戏结束，返回到菜单界面
				backToMenu();
		} else {
			handleInputGame(deltaTime);
		}

		// 更新游戏地图中所有对象状态
		level.update(deltaTime);
		// 碰撞检测
		testCollisions();
		// 更新物理世界
		b2world.step(deltaTime, 8, 3);
		// 更新相机状态
		cameraHelper.update(deltaTime);
		// 检测玩家生命数量
		if (!isGameOver() && isPlayerInWater()) {
			// 播放音效
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
			lives--;
			// 如果生命数量位0，则游戏结束
			if (isGameOver()) {
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			} else {
				// 如果主角掉入水中且生命数量为0
				initLevel();
			}
		}

		// 山体跟随相机滚动
		level.mountains.updateScrollPosition(cameraHelper.getPosition());

		// 更新生命数量
		if (livesVisual > lives) {
			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
		}

		// 更新分数
		if (scoreVisual < score)
			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);

	}

	// 初始化物理世界
	private void initPhysics() {
		if (b2world != null) {
			b2world.dispose();
		}
		// 构建2维物理世界，重力加速度为-9.81m/s^2
		b2world = new World(new Vector2(0, -9.81f), true);

		// 岩石
		Vector2 origin = new Vector2();
		for (Rock rock : level.rocks) {
			// BodyDef对象具有构建一个刚体所需的所有属性
			BodyDef bodyDef = new BodyDef();
			// 刚体类型
			bodyDef.type = BodyType.KinematicBody;
			// 刚体位置
			bodyDef.position.set(rock.position);
			// 构建刚体
			Body body = b2world.createBody(bodyDef);
			rock.body = body;

			// 构建矩形框
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f,
					rock.bounds.height / 2.0f, origin, 0);

			// 夹具:将形状和刚体绑定到一起
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);

			polygonShape.dispose();
		}

	}

	/**
	 * 返回菜单界面
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
			// 控制相机移动
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

			// 控制相机放缩
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
			// 主角移动
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			} else {
				// 在移动平台上
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					// 判断设备适配具有加速度传感器
					if (accelerometerAvailable) {
						// normalize accelerometer values from [-10, 10] to [-1,
						// 1]
						// which translate to rotations of [-90, 90] degrees
						float amount = Gdx.input.getAccelerometerY() / 10.0f;
						amount *= 90.0f;
						// 是否达到最小角速度？
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
			// 主角跳跃
			if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) {
				level.bunnyHead.setJumping(true);
			} else {
				level.bunnyHead.setJumping(false);
			}
		}

		// 控制相机放缩
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
		// 重置游戏世界
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		} else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null
					: level.bunnyHead);

			Gdx.app.debug(TAG,
					"Camera follow enabled: " + cameraHelper.hasTarget());
		}// 返回主菜单
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}

		return false;
	}

	// 碰撞检测
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

	// 主角和岩石碰撞
	private void onCollisionBunnyHeadWithRock(Rock rock) {
		BunnyHead bunnyHead = level.bunnyHead;

		// Math.abs(float a)返回a的相反数
		// 主角岩石平台的距离
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

	// 主角和金币碰撞
	private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
		goldcoin.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupCoin);
		score += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
	};

	// 主角和羽毛碰撞
	private void onCollisionBunnyWithFeather(Feather feather) {
		feather.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.pickupFeather);
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		Gdx.app.log(TAG, "Feather collected");
	};

	// 主角到达终点
	private void onCollisionBunnyWithGoal() {
		goalReached = true;
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
		Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position);
		centerPosBunnyHead.x += level.bunnyHead.bounds.width;
		spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX,
				Constants.CARROTS_SPAWN_RADIUS);
	}

	// 创建胡萝卜
	private void spawnCarrots(Vector2 pos, int numCarrots, float radius) {
		// 胡萝卜形状缩小1/2
		float carrotShapeScale = 0.5f;

		// create carrots with box2d body and fixture
		for (int i = 0; i < numCarrots; i++) {
			Carrot carrot = new Carrot();

			/**
			 * 随机生成位置，旋转角度和缩放比
			 */
			// 水平位置范围
			float x = MathUtils.random(-radius, radius);
			// 生成位置范围(始终在相机的视口外)
			float y = MathUtils.random(5.0f, 15.0f);
			// 旋转角度范围
			float rotation = MathUtils.random(0.0f, 360.0f)
					* MathUtils.degreesToRadians;
			// 缩放比范围
			float carrotScale = MathUtils.random(0.5f, 1.5f);
			carrot.scale.set(carrotScale, carrotScale);

			/**
			 * 根据胡萝卜位置和旋转角度创建刚体
			 */
			BodyDef bodyDef = new BodyDef();
			// 位置
			bodyDef.position.set(pos);
			// 随机增加x,y
			bodyDef.position.add(x, y);
			// 旋转角度
			bodyDef.angle = rotation;
			Body body = b2world.createBody(bodyDef);
			// 刚体类型
			body.setType(BodyType.DynamicBody);
			carrot.body = body;

			/**
			 * 为胡萝卜刚体构建矩形
			 */
			PolygonShape polygonShape = new PolygonShape();
			float halfWidth = carrot.bounds.width / 2.0f * carrotScale;
			float halfHeight = carrot.bounds.height / 2.0f * carrotScale;
			polygonShape.setAsBox(halfWidth * carrotShapeScale, halfHeight
					* carrotShapeScale);

			// 构建夹具
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			// 密度
			fixtureDef.density = 50;
			// 恢复力
			fixtureDef.restitution = 0.5f;
			// 摩擦力
			fixtureDef.friction = 0.5f;
			// 绑定夹具
			body.createFixture(fixtureDef);

			polygonShape.dispose();

			// 添加胡萝卜到对象列表
			level.carrots.add(carrot);
		}
	}

	// 判断游戏是否结束
	public boolean isGameOver() {
		return lives < 0;
	}

	// 判断主角是否掉入水中
	public boolean isPlayerInWater() {
		/**
		 * 水层的y坐标位于屏幕的底边缘(y=0)
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
