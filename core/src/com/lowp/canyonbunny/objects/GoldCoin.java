package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.lowp.canyonbunny.util.Assets;

public class GoldCoin extends AbstractGameObject {

	private TextureRegion regGoldCoin;
	//��ǵ�ǰ״̬
	public boolean collected;

	public GoldCoin() {
		init();
	}

	private void init() {
		dimension.set(0.5f, 0.5f);
		setAnimation(Assets.instance.goldCoin.animGoldCoin);
		stateTime = MathUtils.random(0.0f, 1.0f);
//		regGoldCoin = Assets.instance.goldCoin.goldCoin;
		// ������ײ���εĴ�С
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	public void render(SpriteBatch batch) {
		//�����ұ��ռ������ٻ��Ƶ�ǰ����
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
