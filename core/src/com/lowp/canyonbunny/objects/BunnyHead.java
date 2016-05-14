package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lowp.canyonbunny.util.Assets;
import com.lowp.canyonbunny.util.AudioManager;
import com.lowp.canyonbunny.util.Constants;
import com.lowp.canyonbunny.util.GamePreferences;
import com.lowp.canyonbunny.util.CharacterSkin;

public class BunnyHead extends AbstractGameObject {

	// 日志TAG
	public static final String TAG = BunnyHead.class.getName();

	// 尘埃粒子特效
	public ParticleEffect dustParticles = new ParticleEffect();

	// 指定一个“最大跳跃时间”和“最小跳跃时间”用来限制主角跳跃的力度
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;

	// 一个被"缩短的最大跳跃时间"作用于连续跳跃状态，此时跳跃力度将减小
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

	// 运动方向状态
	public enum VIEW_DIRECTION {
		LEFT, RIGHT
	}

	// 跳跃状态
	public enum JUMP_STATE {
		// 站立于地面，(从平台)下落，跳跃，跳跃后下落
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}

	// 资源纹理
	private TextureRegion regHead;
	// 运动方向
	public VIEW_DIRECTION viewDirection;
	// 跳跃状态
	public JUMP_STATE jumpState;
	// 跳跃时间（在每个跳跃前被初始化）
	public float timeJumping;

	// 是否获得羽毛
	public boolean hasFeatherPowerup;
	// 主角获得羽毛后在空中可停留时间(单位:秒)
	public float timeLeftFeatherPowerup;

	/**
	 * 动画
	 */
	// 正常
	private Animation animNormal;
	// 螺旋
	private Animation animCopterTransform;
	// 解螺旋
	private Animation animCopterTransformBack;
	// 旋转
	private Animation animCopterRotate;

	public BunnyHead() {
		init();
	}

	public void init() {
		// 设置主角大小
		dimension.set(1, 1);
		// 设置主角动画
		animNormal = Assets.instance.bunny.animNormal;
		animCopterTransform = Assets.instance.bunny.animCopterTransform;
		animCopterTransformBack = Assets.instance.bunny.animCopterTransformBack;
		animCopterRotate = Assets.instance.bunny.animCopterRotate;
		setAnimation(animNormal);

		// 设置中心在对象中心
		origin.set(dimension.x / 2, dimension.y / 2);
		// 设置碰撞矩形
		bounds.set(0, 0, dimension.x, dimension.y);
		// 设置对象物理特性
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);
		// 默认运动方向
		viewDirection = VIEW_DIRECTION.RIGHT;
		// 默认跳跃状态
		jumpState = JUMP_STATE.GROUNDED;
		// 跳跃时间(在每个跳跃动作执行前被初始化)
		timeJumping = 0;
		// 是否获得羽毛力量
		hasFeatherPowerup = false;
		// 羽毛力量剩余时间
		timeLeftFeatherPowerup = 0;

		// 初始化粒子效果
		dustParticles.load(Gdx.files.internal("particle/dust.pfx"),
				Gdx.files.internal("particle"));
	}

	// 更新主角状态
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		// 更新主角运动方向
		if (velocity.x != 0) {
			// 通过速度方向判断主角运动方向
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT
					: VIEW_DIRECTION.RIGHT;
		}

		// 获得羽毛
		if (timeLeftFeatherPowerup > 0) {
			if (animation == animCopterTransformBack) {
				// Restart "Transform" animation if another feather power-up
				// was picked up during "TransformBack" animation. Otherwise,
				// the "TransformBack" animation would be stuck while the
				// power-up is still active.
				setAnimation(animCopterTransform);
			}

			timeLeftFeatherPowerup -= deltaTime;

			// 移除羽毛的力量
			if (timeLeftFeatherPowerup < 0) {
				timeLeftFeatherPowerup = 0;
				setFeatherPowerup(false);
				setAnimation(animCopterTransformBack);
			}
		}

		// 更新粒子效果
		dustParticles.update(deltaTime);

		// 改变动画状态
		if (hasFeatherPowerup) {
			if (animation == animNormal) {
				setAnimation(animCopterTransform);
			} else if (animation == animCopterTransform) {
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animCopterRotate);
			}
		} else {
			if (animation == animCopterRotate) {
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animCopterTransformBack);
			} else if (animation == animCopterTransformBack) {
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animNormal);
			}
		}

	}

	// 绘制主角
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;

		// 绘制粒子
		dustParticles.draw(batch);

		// 应用Skin颜色
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin]
				.getColor());

		float dimCorrectionX = 0;
		float dimCorrectionY = 0;
		if (animation != animNormal) {
			dimCorrectionX = 0.05f;
			dimCorrectionY = 0.2f;
		}

		reg = animation.getKeyFrame(stateTime, true);
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
				origin.y, dimension.x + dimCorrectionX, dimension.y
						+ dimCorrectionY, scale.x, scale.y, rotation,
				reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT,
				false);

		// 重置画笔颜色为白色
		batch.setColor(1, 1, 1, 1);
	}

	// 竖直方向上的运动
	@Override
	protected void updateMotionY(float deltaTime) {
		switch (jumpState) {
		// 站立于地面
		case GROUNDED:
			/**
			 * 让主角的状态是从由GROUNDED变为FALLING，这是因为当主角掉下平台的时，会继续往下掉
			 */
			jumpState = JUMP_STATE.FALLING;

			// 当主角移动时产生粒子效果
			if (velocity.x != 0) {
				dustParticles.setPosition(position.x + dimension.x / 2,
						position.y);
				dustParticles.start();
			}

			break;
		// 跳跃
		case JUMP_RISING:
			// 计算跳跃时间
			timeJumping += deltaTime;
			// 判断主角跳跃时间是否超过最大跳跃时间
			if (timeJumping <= JUMP_TIME_MAX) {
				// 跳跃力度
				velocity.y = terminalVelocity.y;
			}
			break;
		// 下落
		case FALLING:
			break;
		// 跳跃后下落
		case JUMP_FALLING:
			// 计算跳跃时间
			timeJumping += deltaTime;
			// 短按跳跃键跳跃
			if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
				// 跳跃力度
				velocity.y = terminalVelocity.y;
			}
		}

		if (jumpState != JUMP_STATE.GROUNDED) {
			// 停止播放粒子效果
			dustParticles.allowCompletion();

			super.updateMotionY(deltaTime);
		}

	}

	// 通过判断按键的状态，更新主角的跳跃状态，
	// 该方法用来触发一个新的跳跃动作,中断一个正在进行的跳跃,或允许连续跳跃
	public void setJumping(boolean jumpKeyPressed) {
		switch (jumpState) {
		// 主角在地面
		case GROUNDED:
			if (jumpKeyPressed) {
				AudioManager.instance.play(Assets.instance.sounds.jump);
				// 每次从地面跳跃时初始化跳跃时间
				timeJumping = 0;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		// 主角在空中上升
		case JUMP_RISING:
			if (!jumpKeyPressed)
				jumpState = JUMP_STATE.JUMP_FALLING;
			break;
		// 从平台下落
		case FALLING:

			// 跳跃后下落
		case JUMP_FALLING:
			if (jumpKeyPressed && hasFeatherPowerup) {
				AudioManager.instance.play(
						Assets.instance.sounds.jumpWithFeather, 1,
						MathUtils.random(1.0f, 1.1f));
				// 最大跳跃时间被提前（缩短）
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				jumpState = JUMP_STATE.JUMP_RISING;
			}

			break;
		}
	}

	// 设置获得羽毛后的效果
	public void setFeatherPowerup(boolean pickedUp) {
		hasFeatherPowerup = pickedUp;

		if (pickedUp) {
			timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
		}

	}

	// 判断当前是否仍然具有羽毛的力量
	public boolean hasFeatherPowerup() {
		return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
	}
}
