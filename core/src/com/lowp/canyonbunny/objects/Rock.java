package com.lowp.canyonbunny.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.util.Assets;

/**
 * ��ʯ��
 * 
 * @author lowp
 *
 */
public class Rock extends AbstractGameObject {
	// ��ʯ��Ե����
	private TextureRegion regEdge;
	// ��ʯ�м䲿��
	private TextureRegion regMiddle;
	// ��ʯ�м䲿�ֵĳ���
	private int length;

	// ��ʯ�������ڵ�ʱ��
	private final float FLOAT_CYCLE_TIME = 2.0f;
	// ���
	private final float FLOAT_AMPLITUDE = 0.25f;
	// ÿ���������ڵ�ʣ��ʱ��
	private float floatCycleTimeLeft;
	// �ж���ʯ�Ƿ����¸���
	private boolean floatingDownwards;
	// ������Ŀ��λ��
	private Vector2 floatTargetPosition;

	public Rock() {
		init();
	}

	/**
	 * ��ʼ���������ԣ���λ:m(��)
	 */
	private void init() {

		dimension.set(1, 1.5f);
		regEdge = Assets.instance.rock.edge;
		regMiddle = Assets.instance.rock.middle;
		// ������ʯ����ʼ����
		setLength(1);

		// Ĭ�������¸���
		floatingDownwards = false;
		// �õ�һ������ĸ�������ʱ�䣬����ʯ�ĸ���������Ȼ
		floatCycleTimeLeft = MathUtils.random(0, FLOAT_CYCLE_TIME / 2);

		floatTargetPosition = null;

	}

	// ������ʯ�ĳ��Ⱥ���ײ����
	public void setLength(int length) {
		this.length = length;
		bounds.set(0, 0, dimension.x * length, dimension.y);
	}

	// ������ʯ�ĳ���
	public void increaseLength(int amount) {
		setLength(length + amount);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		floatCycleTimeLeft -= deltaTime;

		if (floatTargetPosition == null) {
			floatTargetPosition = new Vector2(position);
		}

		// ����ʯ���ڸ���
		if (floatCycleTimeLeft <= 0) {
			// ���ø���ʱ��
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			// ������ʯ��������
			floatingDownwards = !floatingDownwards;
			// floatTargetPosition.y += FLOAT_AMPLITUDE
			// * (floatingDownwards ? -1 : 1);

			// box2d���ο����ʯ�ƶ�
			body.setLinearVelocity(0, FLOAT_AMPLITUDE
					* (floatingDownwards ? -1 : 1));

		} else {
			body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));

		}

		// ���Բ�ֵ
		position.lerp(floatTargetPosition, deltaTime);

	}

	// ������ʯ
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;
		// boolean isDraw = batch.isDrawing();

		// public void draw (Texture texture, float x, float y, float originX,
		// float originY, float width, float height, float scaleX,
		// float scaleY, float rotation, int srcX, int srcY, int srcWidth, int
		// srcHeight, boolean flipX, boolean flipY)
		//
		// �÷���������(texture)�г�һ����������(��������Ͻ�Ϊԭ�㣬���ε���ʼ��Ϊ(srcX,srcY),��͸�Ϊ(srcWidth.srcHeight)),
		// ��������Ƶ�ָ����λ��(x,y),����(originX,originY)ָ���˾��ε�ê��,(scaleX,scaleY)ָ���������
		// ê����X�᷽���Y�᷽������ű�,rotationָ���˾������ê�������ת�ĽǶ�,(flipX,flipY)ָ�������ǹ�����X���Y����з�ת180��
		//
		// texture : �����Ƶľ�������
		// x : ������Ƶ�x����
		// y : ������Ƶ�y����
		// originX :��ê���x����
		// originX :��ê���y����
		// scaleX :��X�᷽������ű�(�����ê��)
		// scaleY��: Y�᷽������ű�(�����ê��)
		// rotation : ��ת�ĽǶ�(�����ê��)
		// width : ������ʾ�Ŀ�
		// height : ������ʾ�ĸ�
		// srcX : �ü�����ľ��ε���ʼ��x����
		// srcY : �ü�����ľ��ε���ʼ��y����
		// srcWidth : �ü�����ľ��εĿ�
		// srcHeight : �ü�����ľ��εĸ�
		// flipX : ��X�ᷭת180��
		// flipY : ��Y�ᷭת180��

		// if(!isDraw){
		// batch.begin();
		// }

		/**
		 * �ڻ��Ƶ�ʱ�򣬽���ʯ�м䲿�ֵĵ�һ����������½���Ϊ��ʯ�Ķ�λ�㣬������������ ������ʯ��λ�ã������Ե���ұ�Ե������װ������
		 */
		// �������Ե
		reg = regEdge;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x, origin.y, dimension.x / 4, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

		// �����м䲿��
		relX = 0;
		reg = regMiddle;
		for (int i = 0; i < length; i++) {
			batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
					origin.x, origin.y, dimension.x, dimension.y, scale.x,
					scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}

		// �����ұ�Ե
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x + dimension.x / 8, origin.y, dimension.x / 4,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				true, false);

		// if(!isDraw){
		// batch.end();
		// }

	}
}
