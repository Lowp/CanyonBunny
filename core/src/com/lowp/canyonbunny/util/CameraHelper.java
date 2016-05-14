package com.lowp.canyonbunny.util;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.objects.AbstractGameObject;

/**
 * 相机帮助类，主要用来存储相机的位置和焦距，并在合适 的时候可以跟随一个游戏对象（调用setTarget()）
 * 
 * @author lowp
 *
 */
public class CameraHelper {
	// 日志TAG
	private static final String TAG = CameraHelper.class.getName();
	// 最大焦距
	private final float MAX_ZOOM_IN = 0.25f;
	// 最大焦距
	private final float MAX_ZOOM_OUT = 10.0f;
	// 相机位置
	private Vector2 position;
	// 相机默认焦距
	private float zoom;
	// 相机跟随速度
	private final float FOLLOW_SPEED = 4.0f;

	private AbstractGameObject target;

	public CameraHelper() {
		position = new Vector2();
		// 默认焦距
		zoom = 1.0f;
	}

	// 更新相机状态
	public void update(float deltaTime) {
		if (!hasTarget())
			return;
		// 让相机的焦点中心与精灵的中心重合，使精灵始终处于屏幕（视口）的中心位置
//		position.x = target.position.x + target.origin.x;
//		position.y = target.position.y + target.origin.x;
		
		// 线性插值，使相机的运动更加平滑
		position.lerp(target.position, FOLLOW_SPEED * deltaTime);
		// 避免相机向下移动太远
		position.y = Math.max(-1f, position.y);
	}

	// 设置相机位置
	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	// 获取相机位置
	public Vector2 getPosition() {
		return position;
	}

	// 增加焦距
	public void addZoom(float amount) {
		setZoom(zoom + amount);
	}

	// 设置相机焦距
	public void setZoom(float zoom) {
		this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT);
	}

	// 获取相机焦距
	public float getZoom() {
		return zoom;
	}

	// 设置相机跟随的目标对象
	public void setTarget(AbstractGameObject target) {
		this.target = target;
	}

	// 获取相机跟随的目标对象
	public AbstractGameObject getTarget() {
		return target;
	}

	// 判断相机时候已存在跟随的对象
	public boolean hasTarget() {
		return target != null;
	}

	// 判断相机时候已存在跟随指定对象
	public boolean hasTarget(AbstractGameObject target) {
		return hasTarget() && this.target.equals(target);
	}

	// 绑定一个相机实例
	public void applyTo(OrthographicCamera camera) {
		camera.position.x = position.x;
		camera.position.y = position.y;
		camera.zoom = zoom;
		// 更新相机
		camera.update();
	}

}
