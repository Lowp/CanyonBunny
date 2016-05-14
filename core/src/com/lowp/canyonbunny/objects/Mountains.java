package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.util.Assets;

/**
 * ɽ����
 * 
 * @author lowp
 *
 */
public class Mountains extends AbstractGameObject {
	private TextureRegion regMountainLeft;
	private TextureRegion regMountainRight;
	private int length;

	public Mountains(int length) {
		this.length = length;
		init();
	}

	private void init() {
		// ����ɽÿ����(������������)�Ĵ�С��10m,��2m
		dimension.set(10, 2);

		regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
		regMountainRight = Assets.instance.levelDecoration.mountainRight;
		// ��ɽ�����ʼ�������ƶ���dimension.x * 2��m
		origin.x = -dimension.x * 2;
		// ɽ������쳤��
		length += dimension.x * 2;
	}

	@Override
	public void render(SpriteBatch batch) {
		// 80% distant mountains (dark gray)
		drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f);
		// 50% distant mountains (gray)
		drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f);
		// 30% distant mountains (light gray)
		drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f);
	}

	/**
	 * ɽ���������ƶ�������һ���ٷֱȣ������ò�ͬɽ������ٶ�
	 * 
	 * @param batch 
	 * @param offsetX X�᷽���ƫ����
	 * @param offsetY Y�᷽���ƫ����
	 * @param tintColor ������ɫ
	 * @param parallaxSpeedX ����ٵݵ��ٶȵİٷֱ�
	 */
	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY,
			float tintColor, float parallaxSpeedX) {
		TextureRegion reg = null;
		// ���û��ʵ���ɫ
		batch.setColor(tintColor, tintColor, tintColor, 1);

		// ɽ��λ��x��yƫ����
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;

		// ɽ������Ϸ�����е�������
		int mountainLength = 0;
		mountainLength += MathUtils.ceil(length / (2 * dimension.x)
				* (1 - parallaxSpeedX));
		mountainLength += MathUtils.ceil(0.5f + offsetX);

		for (int i = 0; i < mountainLength; i++) {
			// ����ɽ����߲���
			reg = regMountainLeft;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, position.y + origin.y + yRel, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;

			// ����ɽ���ұ߲���
			reg = regMountainRight;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, origin.y + yRel + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}
		// ���û��ʵ���ɫΪ��ɫ
		batch.setColor(1, 1, 1, 1);
	}

	// ɽ������������Ч��
	public void updateScrollPosition(Vector2 camPosition) {
		position.set(camPosition.x, position.y);
	}

}
