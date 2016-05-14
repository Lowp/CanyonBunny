package com.lowp.canyonbunny.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.lowp.canyonbunny.util.Assets;

/**
 * 岩石类
 * 
 * @author lowp
 *
 */
public class Rock extends AbstractGameObject {
	// 岩石边缘部分
	private TextureRegion regEdge;
	// 岩石中间部分
	private TextureRegion regMiddle;
	// 岩石中间部分的长度
	private int length;

	// 岩石浮动周期的时间
	private final float FLOAT_CYCLE_TIME = 2.0f;
	// 振幅
	private final float FLOAT_AMPLITUDE = 0.25f;
	// 每个浮动周期的剩余时间
	private float floatCycleTimeLeft;
	// 判断岩石是否向下浮动
	private boolean floatingDownwards;
	// 浮动到目标位置
	private Vector2 floatTargetPosition;

	public Rock() {
		init();
	}

	/**
	 * 初始化对象属性，单位:m(米)
	 */
	private void init() {

		dimension.set(1, 1.5f);
		regEdge = Assets.instance.rock.edge;
		regMiddle = Assets.instance.rock.middle;
		// 设置岩石的起始长度
		setLength(1);

		// 默认先向下浮动
		floatingDownwards = false;
		// 得到一个随机的浮动周期时间，让岩石的浮动更加自然
		floatCycleTimeLeft = MathUtils.random(0, FLOAT_CYCLE_TIME / 2);

		floatTargetPosition = null;

	}

	// 设置岩石的长度和碰撞矩形
	public void setLength(int length) {
		this.length = length;
		bounds.set(0, 0, dimension.x * length, dimension.y);
	}

	// 增加岩石的长度
	public void increaseLength(int amount) {
		setLength(length + amount);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		floatCycleTimeLeft -= deltaTime;

		if (floatTargetPosition == null) {
			floatTargetPosition = new Vector2(position);
		}

		// 让岩石周期浮动
		if (floatCycleTimeLeft <= 0) {
			// 重置浮动时间
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			// 更改岩石浮动方向
			floatingDownwards = !floatingDownwards;
			// floatTargetPosition.y += FLOAT_AMPLITUDE
			// * (floatingDownwards ? -1 : 1);

			// box2d矩形框跟岩石移动
			body.setLinearVelocity(0, FLOAT_AMPLITUDE
					* (floatingDownwards ? -1 : 1));

		} else {
			body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));

		}

		// 线性插值
		position.lerp(floatTargetPosition, deltaTime);

	}

	// 绘制岩石
	@Override
	public void render(SpriteBatch batch) {
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;
		// boolean isDraw = batch.isDrawing();

		// public void draw (Texture texture, float x, float y, float originX,
		// float originY, float width, float height, float scaleX,
		// float scaleY, float rotation, int srcX, int srcY, int srcWidth, int
		// srcHeight, boolean flipX, boolean flipY)
		//
		// 该方法从纹理(texture)切出一个矩形区域(纹理的左上角为原点，矩形的起始点为(srcX,srcY),宽和高为(srcWidth.srcHeight)),
		// 并将其绘制到指定的位置(x,y),其中(originX,originY)指定了矩形的锚点,(scaleX,scaleY)指定矩形相对
		// 锚点在X轴方向或Y轴方向的缩放比,rotation指定了矩形相对锚点进行旋转的角度,(flipX,flipY)指定矩形是够沿着X轴或Y轴进行翻转180度
		//
		// texture : 被绘制的矩形纹理
		// x : 纹理绘制的x坐标
		// y : 纹理绘制的y坐标
		// originX :　锚点的x坐标
		// originX :　锚点的y坐标
		// scaleX :　X轴方向的缩放比(相对于锚点)
		// scaleY　: Y轴方向的缩放比(相对于锚点)
		// rotation : 旋转的角度(相对于锚点)
		// width : 纹理显示的宽
		// height : 纹理显示的高
		// srcX : 裁剪纹理的矩形的起始点x坐标
		// srcY : 裁剪纹理的矩形的起始点y坐标
		// srcWidth : 裁剪纹理的矩形的宽
		// srcHeight : 裁剪纹理的矩形的高
		// flipX : 沿X轴翻转180度
		// flipY : 沿Y轴翻转180度

		// if(!isDraw){
		// batch.begin();
		// }

		/**
		 * 在绘制的时候，将岩石中间部分的第一张纹理的左下角作为岩石的定位点，这样更有利于 处理岩石的位置，而左边缘和右边缘仅仅起装饰作用
		 */
		// 绘制左边缘
		reg = regEdge;
		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x, origin.y, dimension.x / 4, dimension.y, scale.x,
				scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

		// 绘制中间部分
		relX = 0;
		reg = regMiddle;
		for (int i = 0; i < length; i++) {
			batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
					origin.x, origin.y, dimension.x, dimension.y, scale.x,
					scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}

		// 绘制右边缘
		reg = regEdge;
		batch.draw(reg.getTexture(), position.x + relX, position.y + relY,
				origin.x + dimension.x / 8, origin.y, dimension.x / 4,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				true, false);

		// if(!isDraw){
		// batch.end();
		// }

	}
}
