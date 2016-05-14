package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lowp.canyonbunny.util.Assets;

/**
 * �Ʋʼ���
 * 
 * @author lowp
 *
 */
public class Clouds extends AbstractGameObject {

	private float length;
	private Array<TextureRegion> regClouds;
	private Array<Cloud> clouds;

	/**
	 * �Ʋ���
	 * 
	 * @author lowp
	 *
	 */
	private class Cloud extends AbstractGameObject {
		private TextureRegion regCloud;

		public Cloud() {
		}

		public void setRegion(TextureRegion region) {
			regCloud = region;
		}

		@Override
		public void render(SpriteBatch batch) {
			TextureRegion reg = regCloud;
			batch.draw(reg.getTexture(), position.x + origin.x, position.y
					+ origin.y, origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, reg.getRegionX(),
					reg.getRegionY(), reg.getRegionWidth(),
					reg.getRegionHeight(), false, false);

			// if(reg == null){
			// Gdx.app.log("scx", "null");
			// }

		}
	}

	public Clouds(float length) {
		this.length = length;
		init();
	}

	private void init() {
		// ����ÿ���ƲʵĴ�СΪ3�׿�1.5�׸�
		dimension.set(3.0f, 1.5f);
		// ��ʼ���Ʋ���Դ����
		regClouds = new Array<TextureRegion>();
		regClouds.add(Assets.instance.levelDecoration.cloud01);
		regClouds.add(Assets.instance.levelDecoration.cloud02);
		regClouds.add(Assets.instance.levelDecoration.cloud03);
		// ÿ���Ƶ���ʼ�����5m
		int distFac = 5;
		// �Ƶ�����
		int numClouds = (int) (length / distFac);
		// ��ʼ�����飬����Ϊ(2 * numClouds)
		clouds = new Array<Cloud>(2 * numClouds);
		// ʵ���������Ʋ�
		for (int i = 0; i < numClouds; i++) {
			Cloud cloud = spawnCloud();
			// �Ʋʽ���ࣨdistFac����
			cloud.position.x = i * distFac;
			clouds.add(cloud);
		}
	}

	private Cloud spawnCloud() {
		Cloud cloud = new Cloud();
		// �����ƲʵĴ�С
		cloud.dimension.set(dimension);
		// ���ѡ���Ƶ�����
		cloud.setRegion(regClouds.random());
		// �����Ʋʵ�λ��
		Vector2 pos = new Vector2();
		// �����Ƶĳ�ʼλ���ڲ��ĩ��
		pos.x = length + 10;
		// λ�õ�y���꽫���������������1���1
		pos.y += 1.75;
		pos.y += MathUtils.random(0.0f, 0.3f)
				* (MathUtils.randomBoolean() ? 1 : -1);

		cloud.position.set(pos);

		/**
		 * �Ʋ������ƶ�
		 */
		// �ٶ�
		Vector2 speed = new Vector2();
		// ��ʼ�ٶ�
		speed.x += 0.5f;
		// ��������ٶ�
		speed.x += MathUtils.random(0.0f, 0.75f);
		// ��������ٶ�
		cloud.terminalVelocity.set(speed);
		// �����ƶ�
		speed.x *= -1;
		cloud.velocity.set(speed);

		return cloud;
	}

	@Override
	public void render(SpriteBatch batch) {
		for (Cloud cloud : clouds)
			cloud.render(batch);
	}

	@Override
	public void update(float deltaTime) {
		/**
		 * ����������
		 */
		// for (int i = 0; i <= clouds.size-1; i++) {
		// Cloud cloud = clouds.get(i);
		// cloud.update(deltaTime);
		// if (cloud.position.x < -5) {
		// // �����Ʈ������Ϸ�����⣬���Ƴ����ƶ䣬�����һ���µ���
		// clouds.removeIndex(i);
		// clouds.add(spawnCloud());
		// }
		// }
		for (int i = clouds.size - 1; i >= 0; i--) {
			Cloud cloud = clouds.get(i);
			cloud.update(deltaTime);
			if (cloud.position.x < -5) {
				// �����Ʈ������Ϸ�����⣬���Ƴ����ƶ䣬�����һ���µ���
				clouds.removeIndex(i);
				clouds.add(spawnCloud());
			}
		}
	}

}
