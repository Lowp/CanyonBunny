package com.lowp.canyonbunny.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * 本类抽象了所有游戏对象的共同特征
 * 
 * @author lowp
 *
 */
public abstract class AbstractGameObject {
	// 位置
	public Vector2 position;
	// 尺寸大小
	public Vector2 dimension;
	// 中心
	public Vector2 origin;
	// 放缩比例系数
	public Vector2 scale;
	// 旋转角度
	public float rotation;

	/**
	 * 物理特性
	 */
	// 游戏对象当前的速度（单位:m/s）
	public Vector2 velocity;
	// 游戏对象正/负最大速度（单位:m/s）
	public Vector2 terminalVelocity;
	// 产生一个作用于当前对象的阻力，直到对象速度为0。该参数代表摩擦力系数，若其为0，当前对象的速度将不会减小
	public Vector2 friction;
	// 加速度（单位:m/s²）
	public Vector2 acceleration;
	// 碰撞矩形
	public Rectangle bounds;

	/**
	 * box2d
	 */
	public Body body;

	// 动画
	public float stateTime;
	public Animation animation;

	// 指定游戏对象默认属性
	public AbstractGameObject() {
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;

		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();
	}

	// 在WorldController.update()中被调用，用于更新游戏对象的状态
	public void update(float deltaTime) {
		stateTime += deltaTime;
		
		if (body == null) {
			updateMotionX(deltaTime);
			updateMotionY(deltaTime);
			// 更新对象位置
			position.x += velocity.x * deltaTime;
			position.y += velocity.y * deltaTime;
		} else {
			position.set(body.getPosition());
			rotation = body.getAngle() * MathUtils.radiansToDegrees;
		}

	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
		stateTime = 0;
	}

	// 水平方向运动
	protected void updateMotionX(float deltaTime) {

		// 模拟阻力
		if (velocity.x != 0) {
			// 如果速度不为0，且速度大于0(正方向移动)，则减速直到对象停止（速度为0）
			if (velocity.x > 0) {
				/**
				 * Math.max(float a,float b) 将a与b进行比较，并返回其中最大的值
				 */
				velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
			}
			// 如果速度小于0(反方向移动)，则加速直到对象停止
			else {
				/**
				 * Math.min(float a,float b) 将a与b进行比较，并返回其中最小的值
				 */
				velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}
		// 匀加速
		velocity.x += acceleration.x * deltaTime;
		// 限制对象的速度不超过正/负方向最大速度
		velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x,
				terminalVelocity.x);
	}

	// 竖直方向运动
	protected void updateMotionY(float deltaTime) {

		if (velocity.y != 0) {
			// 模拟阻力
			if (velocity.y > 0) {
				velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
			} else {
				velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
			}
		}
		// 匀加速
		velocity.y += acceleration.y * deltaTime;
		// Make sure the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y,
				terminalVelocity.y);
	}

	// 在WorldRenderer.render()中被调用，用于绘制游戏对象
	public abstract void render(SpriteBatch batch);
}
