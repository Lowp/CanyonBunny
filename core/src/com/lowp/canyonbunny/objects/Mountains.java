package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.util.Assets;

/**
 * 山脉类
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
		// 设置山每部分(共左右两部分)的大小宽10m,高2m
		dimension.set(10, 2);

		regMountainLeft = Assets.instance.levelDecoration.mountainLeft;
		regMountainRight = Assets.instance.levelDecoration.mountainRight;
		// 将山体的起始点向左移动（dimension.x * 2）m
		origin.x = -dimension.x * 2;
		// 山体的延伸长度
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
	 * 山体跟随相机移动，传入一个百分比，来设置不同山跟随的速度
	 * 
	 * @param batch 
	 * @param offsetX X轴方向的偏移量
	 * @param offsetY Y轴方向的偏移量
	 * @param tintColor 画笔颜色
	 * @param parallaxSpeedX 相机速递的速度的百分比
	 */
	private void drawMountain(SpriteBatch batch, float offsetX, float offsetY,
			float tintColor, float parallaxSpeedX) {
		TextureRegion reg = null;
		// 设置画笔的颜色
		batch.setColor(tintColor, tintColor, tintColor, 1);

		// 山体位置x和y偏移量
		float xRel = dimension.x * offsetX;
		float yRel = dimension.y * offsetY;

		// 山体在游戏世界中的整体跨度
		int mountainLength = 0;
		mountainLength += MathUtils.ceil(length / (2 * dimension.x)
				* (1 - parallaxSpeedX));
		mountainLength += MathUtils.ceil(0.5f + offsetX);

		for (int i = 0; i < mountainLength; i++) {
			// 绘制山的左边部分
			reg = regMountainLeft;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, position.y + origin.y + yRel, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;

			// 绘制山的右边部分
			reg = regMountainRight;
			batch.draw(reg.getTexture(), origin.x + xRel + position.x
					* parallaxSpeedX, origin.y + yRel + position.y, origin.x,
					origin.y, dimension.x, dimension.y, scale.x, scale.y,
					rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			xRel += dimension.x;
		}
		// 重置画笔的颜色为白色
		batch.setColor(1, 1, 1, 1);
	}

	// 山体跟随相机滚动效果
	public void updateScrollPosition(Vector2 camPosition) {
		position.set(camPosition.x, position.y);
	}

}
