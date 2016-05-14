package com.lowp.canyonbunny.util;

/**
 * 常量类
 * 
 * @author lowp
 *
 */
public class Constants {
	// 游戏相机视口宽5米
	public static final float VIEWPORT_WIDTH = 5.0f;
	// 游戏相机视口高5米
	public static final float VIEWPORT_HEIGHT = 5.0f;

	// GUI相机视口宽
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI相机视口高
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

	// 游戏世界中所有对象的纹理集路径
	public static final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.atlas";

	// 游戏地图信息文件
	public static final String LEVEL_01 = "levels/level-01.png";

	// 玩家的生命数量
	public static final int LIVES_START = 3;

	// 主角获得羽毛后在空中可停留时间(单位:秒)
	public static final float ITEM_FEATHER_POWERUP_DURATION = 9;

	// 游戏结束后与重新开始游戏之间有3s间隔时间
	public static final float TIME_DELAY_GAME_OVER = 3;
	// 菜单界面各个层的纹理图集
	public static final String TEXTURE_ATLAS_UI = "images/canyonbunny-ui.atlas";
	// 菜单界面各个层的纹理图集对应的skin文件
	public static final String SKIN_CANYONBUNNY_UI = "images/canyonbunny-ui.json";
	// 设置宽口各个层的纹理图集
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	// 设置宽口各个层的纹理图集对应的skin文件
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	// 游戏配置文件
	public static final String PREFERENCES = "canyonbunny.prefs";
	// 胡萝卜最大数量
	public static final int CARROTS_SPAWN_MAX = 100;
	// 胡萝卜的覆盖范围
	public static final float CARROTS_SPAWN_RADIUS = 3.5f;
	// 游戏结束后的延迟时间
	public static final float TIME_DELAY_GAME_FINISHED = 6;
	// 着色器
	public static final String shaderMonochromeVertex = "shaders/monochrome.vs";
	public static final String shaderMonochromeFragment = "shaders/monochrome.fs";
	// 最小倾斜角度(小于该角度将无事件发生)
	public static final float ACCEL_ANGLE_DEAD_ZONE = 5.0f;
	// 最大角度对应的最大速度
	public static final float ACCEL_MAX_ANGLE_MAX_MOVEMENT = 20.0f;
}
