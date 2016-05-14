package com.lowp.canyonbunny.screen.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 转场特效
 * 
 * @author lowp
 *
 */
public interface ScreenTransition {
	/**
	 * 
	 * @return 特效持续时间
	 */
	public float getDuration();

	/**
	 * 
	 * 转场不应该依赖于两个屏幕的内容(因为他们正处于工作状态)，因此， 两个相互切换的屏幕应该被转换为两个独立的单元，这可以通过渲染各个屏
	 * 幕在内存中的纹理(帧缓存对象)来实现，该方法被称作“Render to Texture”(RTT)
	 * 
	 * @param batch
	 *            画笔
	 * @param currScreen
	 *            当前屏幕图片
	 * @param nextScreen
	 *            下一个屏幕图片
	 * @param alpha
	 * 			  0~1范围变化
	 */
	public void render(SpriteBatch batch, Texture currScreen,
			Texture nextScreen, float alpha);
	
}
