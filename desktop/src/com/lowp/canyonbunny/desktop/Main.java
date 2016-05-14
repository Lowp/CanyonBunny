package com.lowp.canyonbunny.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.lowp.canyonbunny.CanyonBunnyMain;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Main {
	// 是否重新构建纹理图集
	private static boolean rebuildAtlas = false;
	// 是否附加调试线
	private static boolean drawDebugOutline = false;

	public static void main(String[] arg) {
		// 通过libGDX内置的纹理集类进行纹理打包
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
		// 实例化一个Settings对象，用来构建打包纹理的容器
		Settings settings = new Settings();
		// 设置容器的最大高度
		settings.maxWidth = 1024;
		// 设置容器的最大宽度
		settings.maxHeight = 1024;
		// 是否重复填充
		settings.duplicatePadding = false;
		// 设置调试线
		settings.debug = drawDebugOutline;
		// 通过某个文件夹下的所有图片(第二个参数),放到Settings所构建的容器中(第一个参数),并输出到指定的文件夹中(第三个参数)
		// 并指定一个与之对应的pack文件(第四个参数)
		TexturePacker.process(settings, "assets-raw/images",
				"../android/assets/images", "canyonbunny");

		TexturePacker.process(settings, "assets-raw/images-ui",
				"../android/assets/images", "canyonbunny-ui");
	}
}
