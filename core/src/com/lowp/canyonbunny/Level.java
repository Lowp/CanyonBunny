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
 * ��Ϸͼ������࣬���ݵ�ͼ���ϵ����ص����ɫ�����ƶ�Ӧ����Ϸ����
 * 
 * @author lowp
 *
 */
public class Level {
	public static final String TAG = Level.class.getName();

	public enum BLOCK_TYPE {
		// RGB��ɫ

		// ��ɫ
		EMPTY(0, 0, 0),
		// ��ɫ
		GOAL(255, 0, 0),
		// ��ɫ
		ROCK(0, 255, 0),
		// ��ɫ
		PLAYER_SPAWNPOINT(255, 255, 255),
		// ��ɫ
		ITEM_FEATHER(255, 0, 255),
		// ��ɫ
		ITEM_GOLD_COIN(255, 255, 0);

		// ��ɫ
		private int color;

		// ��ɫֵ����
		private BLOCK_TYPE(int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}

		// �Ƚ���ɫֵ�Ƿ���ͬ
		public boolean sameColor(int color) {
			return this.color == color;
		}

		// ��ȡ��ɫֵ
		public int getColor() {
			return color;
		}
	}

	// ��ʯ��������
	public Array<Rock> rocks;
	// ��Ҷ�������
	public Array<GoldCoin> goldcoins;
	// ��ë��������
	public Array<Feather> feathers;
	// װ�β����
	public Clouds clouds;
	public Mountains mountains;
	public WaterOverlay waterOverlay;
	public Array<Carrot> carrots;
	public Goal goal;

	// ��Ϸ����
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

		// ����Level���ص�ͼ
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// �洢��ǰ������ѭ����ÿ�ε���������һ����ɫֵ��
		int lastPixel = -1;

		// ɨ�����ص㣬��������Ϸ�����λ��
		// �����Ͻǿ�ʼɨ�������������ص㣬ֱ�����½ǽ���
		for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
			for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {

				AbstractGameObject obj = null;

				float offsetHeight = 0;
				// ��ǰ��ɨ������صĸ߶� = ��Ϸ��ͼ�ĸ߶�-��ǰ���صĸ߶�
				float baseHeight = pixmap.getHeight() - pixelY;
				// ��ȡ��ǰ���ص���ɫֵ(32-bit RGBA)
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x,y)
				// point and create the corresponding game object if there is
				// a match
				// empty space

				// ���ݻ�ȡ����������ɫ��ȷ����֮��Ӧ����Ϸ�����λ��
				if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {

				}
				// ��ʯ����
				else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if (lastPixel != currentPixel) {
						obj = new Rock();
						// �߶�����
						float heightIncreaseFactor = 0.25f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y
								* heightIncreaseFactor + offsetHeight);
						rocks.add((Rock) obj);
					} else {
						// �����ǰ���ص����ɫ�����ϸ���ͬ������ʯ�м䲿�ֵĳ��ȼ�1
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				}
				// ��Ϸ����
				else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
					obj = new BunnyHead();
					offsetHeight = -3.0f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					bunnyHead = (BunnyHead) obj;
				}
				// ��ë
				else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					feathers.add((Feather) obj);
				}
				// ���
				else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight * obj.dimension.y
							+ offsetHeight);
					goldcoins.add((GoldCoin) obj);
				}
				// �����յ�
				else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
					obj = new Goal();
					offsetHeight = -7.0f;
					obj.position.set(pixelX, baseHeight + offsetHeight);
					goal = (Goal) obj;
				}
				// δ֪��Ϸ�������ص���ɫ
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
		// ����װ�β���������ص�ͼ�ϵ�λ�ý��г�ʼ��
		clouds = new Clouds(pixmap.getWidth());
		clouds.position.set(0, 2);

		mountains = new Mountains(pixmap.getWidth());
		mountains.position.set(-1, -1);

		waterOverlay = new WaterOverlay(pixmap.getWidth());
		waterOverlay.position.set(0, -3.75f);

		// �ͷ��ڴ�
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
