package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lowp.canyonbunny.util.Assets;
/**
 * ˮ ��
 * @author lowp
 *
 */
public class WaterOverlay extends AbstractGameObject {

	private TextureRegion regWaterOverlay;
	private float length;

	public WaterOverlay(float length) {
		this.length = length;
		init();
	}

	private void init() {
		//ˮ�泤(length * 10)m,��3m
		dimension.set(length * 10, 3);
		//��ȡ����
		regWaterOverlay = Assets.instance.levelDecoration.waterOverlay;
		//����ƫ��(-dimension.x/2)m
		origin.x = -dimension.x/2;
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		reg = regWaterOverlay;
		
		batch.draw(reg.getTexture(), position.x + origin.x, position.y
				+ origin.y, origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}

}
