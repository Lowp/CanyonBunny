package com.lowp.canyonbunny.screen.transitions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * ת����Ч
 * 
 * @author lowp
 *
 */
public interface ScreenTransition {
	/**
	 * 
	 * @return ��Ч����ʱ��
	 */
	public float getDuration();

	/**
	 * 
	 * ת����Ӧ��������������Ļ������(��Ϊ���������ڹ���״̬)����ˣ� �����໥�л�����ĻӦ�ñ�ת��Ϊ���������ĵ�Ԫ�������ͨ����Ⱦ������
	 * Ļ���ڴ��е�����(֡�������)��ʵ�֣��÷�����������Render to Texture��(RTT)
	 * 
	 * @param batch
	 *            ����
	 * @param currScreen
	 *            ��ǰ��ĻͼƬ
	 * @param nextScreen
	 *            ��һ����ĻͼƬ
	 * @param alpha
	 * 			  0~1��Χ�仯
	 */
	public void render(SpriteBatch batch, Texture currScreen,
			Texture nextScreen, float alpha);
	
}
