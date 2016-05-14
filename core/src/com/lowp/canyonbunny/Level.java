package com.lowp.canyonbunny;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.lowp.canyonbunny.objects.AbstractGameObject;
import com.lowp.canyonbunny.objects.BunnyHead;
import com.lowp.canyonbunny.objects.Carrot;
import com.lowp.canyonbunny.objects.Clouds;
import com.lowp.canyonbunny.objects.Feather;
import com.lowp.canyonbunny.objects.Goal;
import com.lowp.canyonbunny.objects.GoldCoin;
import com.lowp.canyonbunny.objects.Mountains;
import com.lowp.canyonbunny.objects.Rock;
import com.lowp.canyonbunny.objects.WaterOverlay;

/**
 * 游戏图层加载类，根据地图层上的像素点的颜色，绘制对应的游戏对象
 * 
 * @author lowp
 *
 */
public class Level {
	public static final String TAG = Level.class.getName();

	public enum BLOCK_TYPE {
		// RGB颜色

		// 黑色
		EMPTY(0, 0, 0),
		// 红色
		GOAL(255, 0, 0),
		// 绿色
		ROCK(0, 255, 0),
		// 白色
		PLAYER_SPAWNPOINT(255, 255, 255),
		// 紫色
		ITEM_FEATHER(255, 0, 255),
		// 黄色
		ITEM_GOLD_COIN(255, 255, 0);

		// 颜色
		private int color;

		// 颜色值换算
		private BLOCK_TYPE(int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}

		// 比较颜色值是否相同
		public boolean sameColor(int color) {
			return this.color == color;
		}

		// 获取颜色值
		public int getColor() {
			return color;
		}
	}

	// 岩石对象数组
	public Array<Rock> rocks;
	// 金币对象数组
	public Array<GoldCoin> goldcoins;
	// 羽毛对象数组
	public Array<Feather> feathers;
	// 装饰层对象
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	public Array<Carrot> carrots;
	public Goal goal;

	// 游戏主角
	public BunnyHead bunnyHead;

	public Level(String filename) {
		init(filename);
	}

	private void init(String filename) {

		// player character
		bunnyHead = null;

		rocks = new Array<Rock>();
		goldcoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();
		carrots = new Array<Carrot>();

		// 加载Level像素地图
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// 存储当前像素在循环内每次迭代后的最后一个颜色值。
		int lastPixel = -1;

		// 扫描像素点，并设置游戏对象的位置
		// 从左上角开始扫描所有所有像素点，直到右下角结束
		for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {

				AbstractGameObject obj = null;

				float offsetHeight = 0;
				// 当前被扫描的像素的高度 = 游戏地图的高度-当前像素的高度
				float baseHeight = pixmap.getHeight() - pixelY;
				// 获取当前像素的颜色值(32-bit RGBA)
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x,y)
				// point and create the corresponding game object if there is
				// a match
				// empty space

				// 根据获取到的像素颜色，确定与之对应的游戏对象的位置
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {

				}
				// 岩石对象
				else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						obj = new Rock();
						// 高度增量
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y
								* heightIncreaseFactor + offsetHeight);
						rocks.add((Rock) obj);
					} else {
						// 如果当前像素点的颜色和是上个相同，则将岩石中间部分的长度加1
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				}
				// 游戏主角
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					bunnyHead = (BunnyHead) obj;
				}
				// 羽毛
				else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					feathers.add((Feather) obj);
				}
				// 金币
				else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					goldcoins.add((GoldCoin) obj);
				}
				// 到达终点
				else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
					obj = new Goal();
					offsetHeight = -7.0f;
					obj.position.set(pixelX, baseHeight + offsetHeight);
					goal = (Goal) obj;
				}
				// 未知游戏对象像素的颜色
				else {
					int r = 0xff & (currentPixel >>> 24); // red color channel
					int g = 0xff & (currentPixel >>> 16); // green color channel
					int b = 0xff & (currentPixel >>> 8); // blue color channel
					int a = 0xff & currentPixel; // alpha channel
					Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<"
							+ pixelY + ">: r<" + r + "> g<" + g + "> b<" + b
							+ "> a<" + a + ">");
				}
				
				lastPixel = currentPixel;
			}
		}
		// 根据装饰层对象在像素地图上的位置进行初始化
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0, 2);

		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);

		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);

		// 释放内存
		pixmap.dispose();
		Gdx.app.debug(TAG, "level '" + filename + "' loaded");
	}

	public void update(float deltaTime) {
		bunnyHead.update(deltaTime);
		for (Rock rock : rocks)
			rock.update(deltaTime);
		for (GoldCoin goldCoin : goldcoins)
			goldCoin.update(deltaTime);
		for (Feather feather : feathers)
			feather.update(deltaTime);
		clouds.update(deltaTime);
		for (Carrot carrot : carrots)
			carrot.update(deltaTime);
	}

	public void render(SpriteBatch batch) {
		// Draw Mountains
		mountains.render(batch);
		// Draw Goal
		goal.render(batch);
		// Draw Rocks
		for (Rock rock : rocks)
			rock.render(batch);
		// Draw Gold Coins
		for (GoldCoin goldCoin : goldcoins)
			goldCoin.render(batch);
		// Draw Feathers
		for (Feather feather : feathers)
			feather.render(batch);
		// Draw Carrots
		for (Carrot carrot : carrots)
			carrot.render(batch);
		// Draw Player Character
		bunnyHead.render(batch);
		// Draw Water Overlay
		waterOverlay.render(batch);
		// Draw Clouds
		clouds.render(batch);
	}

}
