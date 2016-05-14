package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lowp.canyonbunny.util.Assets;

public class GoldCoin extends AbstractGameObject {

	private TextureRegion regGoldCoin;
	//标记当前状态
	public boolean collected;

	public GoldCoin() {
		init();
	}

	private void init() {
		dimension.set(0.5f, 0.5f);
		setAnimation(Assets.instance.goldCoin.animGoldCoin);
		stateTime = MathUtils.random(0.0f, 1.0f);
//		regGoldCoin = Assets.instance.goldCoin.goldCoin;
		// 设置碰撞矩形的大小
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	public void render(SpriteBatch batch) {
		//如果金币被收集，则不再绘制当前对象
		if (collected)
			return;

		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime, true);
		
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
				origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
				reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), false, false);
	}

	public int getScore() {
		return 100;
	}

}
