package com.lowp.canyonbunny.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.objects.AbstractGameObject;

/**
 * ��������࣬��Ҫ�����洢�����λ�úͽ��࣬���ں��� ��ʱ����Ը���һ����Ϸ���󣨵���setTarget()��
 * 
 * @author lowp
 *
 */
public class CameraHelper {
	// ��־TAG
	private static final String TAG = CameraHelper.class.getName();
	// ��󽹾�
	private final float MAX_ZOOM_IN = 0.25f;
	// ��󽹾�
	private final float MAX_ZOOM_OUT = 10.0f;
	// ���λ��
	private Vector2 position;
	// ���Ĭ�Ͻ���
	private float zoom;
	// ��������ٶ�
	private final float FOLLOW_SPEED = 4.0f;

	private AbstractGameObject target;

	public CameraHelper() {
		position = new Vector2();
		// Ĭ�Ͻ���
		zoom = 1.0f;
	}

	// �������״̬
	public void update(float deltaTime) {
		if (!hasTarget())
			return;
		// ������Ľ��������뾫��������غϣ�ʹ����ʼ�մ�����Ļ���ӿڣ�������λ��
//		position.x = target.position.x + target.origin.x;
//		position.y = target.position.y + target.origin.x;
		
		// ���Բ�ֵ��ʹ������˶�����ƽ��
		position.lerp(target.position, FOLLOW_SPEED * deltaTime);
		// ������������ƶ�̫Զ
		position.y = Math.max(-1f, position.y);
	}

	// �������λ��
	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	// ��ȡ���λ��
	public Vector2 getPosition() {
		return position;
	}

	// ���ӽ���
	public void addZoom(float amount) {
		setZoom(zoom + amount);
	}

	// �����������
	public void setZoom(float zoom) {
		this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}

	// ��ȡ�������
	public float getZoom() {
		return zoom;
	}

	// ������������Ŀ�����
	public void setTarget(AbstractGameObject target) {
		this.target = target;
	}

	// ��ȡ��������Ŀ�����
	public AbstractGameObject getTarget() {
		return target;
	}

	// �ж����ʱ���Ѵ��ڸ���Ķ���
	public boolean hasTarget() {
		return target != null;
	}

	// �ж����ʱ���Ѵ��ڸ���ָ������
	public boolean hasTarget(AbstractGameObject target) {
		return hasTarget() && this.target.equals(target);
	}

	// ��һ�����ʵ��
	public void applyTo(OrthographicCamera camera) {
		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.zoom = zoom;
		// �������
		camera.update();
	}

}
