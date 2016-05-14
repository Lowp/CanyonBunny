package com.lowp.canyonbunny.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lowp.canyonbunny.CanyonBunnyMain;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Main {
	// �Ƿ����¹�������ͼ��
	private static boolean rebuildAtlas = false;
	// �Ƿ񸽼ӵ�����
	private static boolean drawDebugOutline = false;

	public static void main(String[] arg) {
		// ͨ��libGDX���õ����������������
		if (rebuildAtlas) {
			packTexture();
		}

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "CanyonBunny";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new CanyonBunnyMain(), config);
	}

	public static void packTexture() {
		// ʵ����һ��Settings������������������������
		Settings settings = new Settings();
		// �������������߶�
		settings.maxWidth = 1024;
		// ���������������
		settings.maxHeight = 1024;
		// �Ƿ��ظ����
		settings.duplicatePadding = false;
		// ���õ�����
		settings.debug = drawDebugOutline;
		// ͨ��ĳ���ļ����µ�����ͼƬ(�ڶ�������),�ŵ�Settings��������������(��һ������),�������ָ�����ļ�����(����������)
		// ��ָ��һ����֮��Ӧ��pack�ļ�(���ĸ�����)
		TexturePacker.process(settings, "assets-raw/images",
				"../android/assets/images", "canyonbunny");

		TexturePacker.process(settings, "assets-raw/images-ui",
				"../android/assets/images", "canyonbunny-ui");
	}
}
