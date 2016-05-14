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

	// ��־TAG
	public static final String TAG = BunnyHead.class.getName();

	// ����������Ч
	public ParticleEffect dustParticles = new ParticleEffect();

	// ָ��һ���������Ծʱ�䡱�͡���С��Ծʱ�䡱��������������Ծ������
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;

	// һ����"���̵������Ծʱ��"������������Ծ״̬����ʱ��Ծ���Ƚ���С
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

	// �˶�����״̬
	public enum VIEW_DIRECTION {
		LEFT, RIGHT
	}

	// ��Ծ״̬
	public enum JUMP_STATE {
		// վ���ڵ��棬(��ƽ̨)���䣬��Ծ����Ծ������
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}

	// ��Դ����
	private TextureRegion regHead;
	// �˶�����
	public VIEW_DIRECTION viewDirection;
	// ��Ծ״̬
	public JUMP_STATE jumpState;
	// ��Ծʱ�䣨��ÿ����Ծǰ����ʼ����
	public float timeJumping;

	// �Ƿ�����ë
	public boolean hasFeatherPowerup;
	// ���ǻ����ë���ڿ��п�ͣ��ʱ��(��λ:��)
	public float timeLeftFeatherPowerup;

	/**
	 * ����
	 */
	// ����
	private Animation animNormal;
	// ����
	private Animation animCopterTransform;
	// ������
	private Animation animCopterTransformBack;
	// ��ת
	private Animation animCopterRotate;

	public BunnyHead() {
		init();
	}

	public void init() {
		// �������Ǵ�С
		dimension.set(1, 1);
		// �������Ƕ���
		animNormal = Assets.instance.bunny.animNormal;
		animCopterTransform = Assets.instance.bunny.animCopterTransform;
		animCopterTransformBack = Assets.instance.bunny.animCopterTransformBack;
		animCopterRotate = Assets.instance.bunny.animCopterRotate;
		setAnimation(animNormal);

		// ���������ڶ�������
		origin.set(dimension.x / 2, dimension.y / 2);
		// ������ײ����
		bounds.set(0, 0, dimension.x, dimension.y);
		// ���ö�����������
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);
		// Ĭ���˶�����
		viewDirection = VIEW_DIRECTION.RIGHT;
		// Ĭ����Ծ״̬
		jumpState = JUMP_STATE.GROUNDED;
		// ��Ծʱ��(��ÿ����Ծ����ִ��ǰ����ʼ��)
		timeJumping = 0;
		// �Ƿ�����ë����
		hasFeatherPowerup = false;
		// ��ë����ʣ��ʱ��
		timeLeftFeatherPowerup = 0;

		// ��ʼ������Ч��
		dustParticles.load(Gdx.files.internal("particle/dust.pfx"),
				Gdx.files.internal("particle"));
	}

	// ��������״̬
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		// ���������˶�����
		if (velocity.x != 0) {
			// ͨ���ٶȷ����ж������˶�����
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT
					: VIEW_DIRECTION.RIGHT;
		}

		// �����ë
		if (timeLeftFeatherPowerup > 0) {
			if (animation == animCopterTransformBack) {
				// Restart "Transform" animation if another feather power-up
				// was picked up during "TransformBack" animation. Otherwise,
				// the "TransformBack" animation would be stuck while the
				// power-up is still active.
				setAnimation(animCopterTransform);
			}

			timeLeftFeatherPowerup -= deltaTime;

			// �Ƴ���ë������
			if (timeLeftFeatherPowerup < 0) {
				timeLeftFeatherPowerup = 0;
				setFeatherPowerup(false);
				setAnimation(animCopterTransformBack);
			}
		}

		// ��������Ч��
		dustParticles.update(deltaTime);

		// �ı䶯��״̬
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

	// ��������
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;

		// ��������
		dustParticles.draw(batch);

		// Ӧ��Skin��ɫ
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

		// ���û�����ɫΪ��ɫ
		batch.setColor(1, 1, 1, 1);
	}

	// ��ֱ�����ϵ��˶�
	@Override
	protected void updateMotionY(float deltaTime) {
		switch (jumpState) {
		// վ���ڵ���
		case GROUNDED:
			/**
			 * �����ǵ�״̬�Ǵ���GROUNDED��ΪFALLING��������Ϊ�����ǵ���ƽ̨��ʱ����������µ�
			 */
			jumpState = JUMP_STATE.FALLING;

			// �������ƶ�ʱ��������Ч��
			if (velocity.x != 0) {
				dustParticles.setPosition(position.x + dimension.x / 2,
						position.y);
				dustParticles.start();
			}

			break;
		// ��Ծ
		case JUMP_RISING:
			// ������Ծʱ��
			timeJumping += deltaTime;
			// �ж�������Ծʱ���Ƿ񳬹������Ծʱ��
			if (timeJumping <= JUMP_TIME_MAX) {
				// ��Ծ����
				velocity.y = terminalVelocity.y;
			}
			break;
		// ����
		case FALLING:
			break;
		// ��Ծ������
		case JUMP_FALLING:
			// ������Ծʱ��
			timeJumping += deltaTime;
			// �̰���Ծ����Ծ
			if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
				// ��Ծ����
				velocity.y = terminalVelocity.y;
			}
		}

		if (jumpState != JUMP_STATE.GROUNDED) {
			// ֹͣ��������Ч��
			dustParticles.allowCompletion();

			super.updateMotionY(deltaTime);
		}

	}

	// ͨ���жϰ�����״̬���������ǵ���Ծ״̬��
	// �÷�����������һ���µ���Ծ����,�ж�һ�����ڽ��е���Ծ,������������Ծ
	public void setJumping(boolean jumpKeyPressed) {
		switch (jumpState) {
		// �����ڵ���
		case GROUNDED:
			if (jumpKeyPressed) {
				AudioManager.instance.play(Assets.instance.sounds.jump);
				// ÿ�δӵ�����Ծʱ��ʼ����Ծʱ��
				timeJumping = 0;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		// �����ڿ�������
		case JUMP_RISING:
			if (!jumpKeyPressed)
				jumpState = JUMP_STATE.JUMP_FALLING;
			break;
		// ��ƽ̨����
		case FALLING:

			// ��Ծ������
		case JUMP_FALLING:
			if (jumpKeyPressed && hasFeatherPowerup) {
				AudioManager.instance.play(
						Assets.instance.sounds.jumpWithFeather, 1,
						MathUtils.random(1.0f, 1.1f));
				// �����Ծʱ�䱻��ǰ�����̣�
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				jumpState = JUMP_STATE.JUMP_RISING;
			}

			break;
		}
	}

	// ���û����ë���Ч��
	public void setFeatherPowerup(boolean pickedUp) {
		hasFeatherPowerup = pickedUp;

		if (pickedUp) {
			timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
		}

	}

	// �жϵ�ǰ�Ƿ���Ȼ������ë������
	public boolean hasFeatherPowerup() {
		return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
	}
}
