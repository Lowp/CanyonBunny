package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lowp.canyonbunny.util.Assets;

/**
 * 胡萝卜类
 * 
 * @author lowp
 *
 */
public class Carrot extends AbstractGameObject {

	private TextureRegion regCarrot;

	public Carrot() {
		init();
	}

	private void init() {
		// 大小
		dimension.set(0.25f, 0.5f);
		regCarrot = Assets.instance.levelDecoration.carrot;
		// 边界框
		bounds.set(0, 0, dimension.x, dimension.y);
		// 中心
		origin.set(dimension.x / 2, dimension.y / 2);
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		reg = regCarrot;
		batch.draw(reg.getTexture(), position.x - origin.x, position.y
				- origin.y, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}

}
