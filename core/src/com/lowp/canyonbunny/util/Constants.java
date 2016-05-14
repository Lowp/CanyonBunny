package com.lowp.canyonbunny.util;

/**
 * ������
 * 
 * @author lowp
 *
 */
public class Constants {
	// ��Ϸ����ӿڿ�5��
	public static final float VIEWPORT_WIDTH = 5.0f;
	// ��Ϸ����ӿڸ�5��
	public static final float VIEWPORT_HEIGHT = 5.0f;

	// GUI����ӿڿ�
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI����ӿڸ�
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

	// ��Ϸ���������ж��������·��
	public static final String TEXTURE_ATLAS_OBJECTS = "images/canyonbunny.atlas";

	// ��Ϸ��ͼ��Ϣ�ļ�
	public static final String LEVEL_01 = "levels/level-01.png";

	// ��ҵ���������
	public static final int LIVES_START = 3;

	// ���ǻ����ë���ڿ��п�ͣ��ʱ��(��λ:��)
	public static final float ITEM_FEATHER_POWERUP_DURATION = 9;

	// ��Ϸ�����������¿�ʼ��Ϸ֮����3s���ʱ��
	public static final float TIME_DELAY_GAME_OVER = 3;
	// �˵���������������ͼ��
	public static final String TEXTURE_ATLAS_UI = "images/canyonbunny-ui.atlas";
	// �˵���������������ͼ����Ӧ��skin�ļ�
	public static final String SKIN_CANYONBUNNY_UI = "images/canyonbunny-ui.json";
	// ���ÿ�ڸ����������ͼ��
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	// ���ÿ�ڸ����������ͼ����Ӧ��skin�ļ�
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	// ��Ϸ�����ļ�
	public static final String PREFERENCES = "canyonbunny.prefs";
	// ���ܲ��������
	public static final int CARROTS_SPAWN_MAX = 100;
	// ���ܲ��ĸ��Ƿ�Χ
	public static final float CARROTS_SPAWN_RADIUS = 3.5f;
	// ��Ϸ��������ӳ�ʱ��
	public static final float TIME_DELAY_GAME_FINISHED = 6;
	// ��ɫ��
	public static final String shaderMonochromeVertex = "shaders/monochrome.vs";
	public static final String shaderMonochromeFragment = "shaders/monochrome.fs";
	// ��С��б�Ƕ�(С�ڸýǶȽ����¼�����)
	public static final float ACCEL_ANGLE_DEAD_ZONE = 5.0f;
	// ���Ƕȶ�Ӧ������ٶ�
	public static final float ACCEL_MAX_ANGLE_MAX_MOVEMENT = 20.0f;
}
