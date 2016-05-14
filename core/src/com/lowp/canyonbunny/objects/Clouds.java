package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.lowp.canyonbunny.util.Assets;

/**
 * 云彩集类
 * 
 * @author lowp
 *
 */
public class Clouds extends AbstractGameObject {

	private float length;
	private Array<TextureRegion> regClouds;
	private Array<Cloud> clouds;

	/**
	 * 云彩类
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
		// 设置每朵云彩的大小为3米宽，1.5米高
		dimension.set(3.0f, 1.5f);
		// 初始化云彩资源数组
		regClouds = new Array<TextureRegion>();
		regClouds.add(Assets.instance.levelDecoration.cloud01);
		regClouds.add(Assets.instance.levelDecoration.cloud02);
		regClouds.add(Assets.instance.levelDecoration.cloud03);
		// 每朵云的起始点相距5m
		int distFac = 5;
		// 云的数量
		int numClouds = (int) (length / distFac);
		// 初始化数组，长度为(2 * numClouds)
		clouds = new Array<Cloud>(2 * numClouds);
		// 实例化所有云彩
		for (int i = 0; i < numClouds; i++) {
			Cloud cloud = spawnCloud();
			// 云彩将相距（distFac）米
			cloud.position.x = i * distFac;
			clouds.add(cloud);
		}
	}

	private Cloud spawnCloud() {
		Cloud cloud = new Cloud();
		// 设置云彩的大小
		cloud.dimension.set(dimension);
		// 随机选择云的纹理
		cloud.setRegion(regClouds.random());
		// 设置云彩的位置
		Vector2 pos = new Vector2();
		// 设置云的初始位置在层的末端
		pos.x = length + 10;
		// 位置的y坐标将基础坐标上随机加1或减1
		pos.y += 1.75;
		pos.y += MathUtils.random(0.0f, 0.3f)
				* (MathUtils.randomBoolean() ? 1 : -1);

		cloud.position.set(pos);

		/**
		 * 云彩向左移动
		 */
		// 速度
		Vector2 speed = new Vector2();
		// 初始速度
		speed.x += 0.5f;
		// 随机增加速度
		speed.x += MathUtils.random(0.0f, 0.75f);
		// 限制最大速度
		cloud.terminalVelocity.set(speed);
		// 向左移动
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
		 * 更新云容器
		 */
		// for (int i = 0; i <= clouds.size-1; i++) {
		// Cloud cloud = clouds.get(i);
		// cloud.update(deltaTime);
		// if (cloud.position.x < -5) {
		// // 如果云飘到了游戏世界外，则移除该云朵，并添加一朵新的云
		// clouds.removeIndex(i);
		// clouds.add(spawnCloud());
		// }
		// }
		for (int i = clouds.size - 1; i >= 0; i--) {
			Cloud cloud = clouds.get(i);
			cloud.update(deltaTime);
			if (cloud.position.x < -5) {
				// 如果云飘到了游戏世界外，则移除该云朵，并添加一朵新的云
				clouds.removeIndex(i);
				clouds.add(spawnCloud());
			}
		}
	}

}
