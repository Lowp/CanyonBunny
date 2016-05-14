package com.lowp.canyonbunny.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.lowp.canyonbunny.screen.transitions.ScreenTransition;
import com.lowp.canyonbunny.util.Assets;
import com.lowp.canyonbunny.util.AudioManager;
import com.lowp.canyonbunny.util.CharacterSkin;
import com.lowp.canyonbunny.util.Constants;
import com.lowp.canyonbunny.util.GamePreferences;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * 菜单界面
 * 
 * @author lowp
 *
 */
public class MenuScreen extends AbstractGameScreen {

	private Stage stage;
	// 菜单屏幕各个层的Skin
	private Skin skinCanyonBunny;
	// 设置宽口各个层的Skin
	private Skin skinLibgdx;
	// 菜单
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	// 设置窗口
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox<CharacterSkin> selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	private CheckBox chkUseMonoChromeShader;
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;

	public MenuScreen(DirectedGame game) {
		super(game);
	}

	@Override
	public void show() {
		// 初始化舞台
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT));
		rebuildStage();
	}

	@Override
	public void render(float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}

		stage.act(deltaTime);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		// 设置舞台视口的大小
		stage.getViewport().update(width, height, true);
	}

	/**
	 * 当屏幕被隐藏时调用
	 */
	@Override
	public void hide() {
		stage.dispose();
		skinCanyonBunny.dispose();
		skinLibgdx.dispose();
	}

	@Override
	public void pause() {

	}

	// 初始化舞台
	private void rebuildStage() {
		// 菜单界面各个层的skin
		skinCanyonBunny = new Skin(
				Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		// 设置窗口各个层的skin
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

		/**
		 * 创建所有的层
		 */
		// 背景层
		Table layerBackground = buildBackgroundLayer();
		// 对象层
		Table layerObjects = buildObjectsLayer();
		// Logo层
		Table layerLogos = buildLogosLayer();
		// 控制层
		Table layerControls = buildControlsLayer();
		// 设置窗口层
		Table layerOptionsWindow = buildOptionsWindowLayer();

		// 将所有的层添加到舞台
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}

	// 创建背景层
	private Table buildBackgroundLayer() {
		Table layer = new Table();
		// 设置背景图片
		imgBackground = new Image(skinCanyonBunny, "background");
		// 将背景图片添加到背景层
		layer.add(imgBackground);
		return layer;
	}

	// 创建对象层
	private Table buildObjectsLayer() {
		Table layer = new Table();
		// 添加“金币”对象
		imgCoins = new Image(skinCanyonBunny, "coins");
		layer.addActor(imgCoins);
		imgCoins.setOrigin(imgCoins.getWidth() / 2, imgCoins.getHeight() / 2);
		imgCoins.addAction(sequence(
				moveTo(135, -20),
				scaleTo(0, 0),
				fadeOut(0),
				delay(2.5f),
				parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut),
						scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
						alpha(1.0f, 0.5f))));

		// 添加“兔子头”对象
		imgBunny = new Image(skinCanyonBunny, "bunny");
		layer.addActor(imgBunny);
		imgBunny.addAction(sequence(moveTo(655, 510), delay(4.0f),
				moveBy(-70, -100, 0.5f, Interpolation.fade),
				moveBy(-100, -50, 0.5f, Interpolation.fade),
				moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
		return layer;
	}

	// 每当调用Table的add()方法，TableLayout将新添加一列，这意味着控件将在
	// 水平方向被添加如果想换行，可以调用row()方法，之后调用expandY()意味着，
	// 空白区域将在竖直方向延伸

	// 创建Logo层
	private Table buildLogosLayer() {
		Table layer = new Table();
		layer.left().top();

		// 添加游戏Logo
		imgLogo = new Image(skinCanyonBunny, "logo");
		layer.add(imgLogo).top();

		// 换行，并在Y轴方向延伸空白区域
		layer.row().expandY();

		// 添加信息Logo
		imgInfo = new Image(skinCanyonBunny, "info");
		layer.add(imgInfo).bottom();

		// 启用调试线
		if (debugEnabled) {
			layer.debug();
		}

		return layer;
	}

	// 在同一方向上最后被调用的定位方法将决定控件的最终定位
	// eg.
	// layer.right().centet().left();最终控件将在屏幕的左边(left()方法生效)

	// 创建控制层
	private Table buildControlsLayer() {
		Table layer = new Table();
		layer.right().bottom();

		// 添加“开始游戏”按钮
		btnMenuPlay = new Button(skinCanyonBunny, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();

			}
		});

		// 换行
		layer.row();

		// 添加设置按钮
		btnMenuOptions = new Button(skinCanyonBunny, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		// 启用调试线
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	// 开始游戏按钮被点击
	private void onPlayClicked() {
		// 设置一个转场特效,持续时间为0.75s
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		// 跳转到游戏界面
		game.setScreen(new GameScreen(game), transition);
	}

	// 设置按钮被点击
	private void onOptionsClicked() {
		// 加载游戏设置
		loadSettings();
		// 隐藏按钮
		showMenuButtons(false);
		// 显示菜单窗口
		showOptionsWindow(true, true);
	}

	/**
	 * 创建设置窗口层
	 */
	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinLibgdx);

		// 添加声音设置项: 音效/音乐的复选框和滑动条
		winOptions.add(buildOptWinAudioSettings()).row();

		// 添加主角颜色设置项: 下拉选择框(白色，灰色，棕色)
		winOptions.add(buildOptWinSkinSelection()).row();

		// 添加Bug选项: 显示FPS
		winOptions.add(buildOptWinDebug()).row();

		// 添加分割线和按钮 (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

		// 设置窗口透明度
		winOptions.setColor(1, 1, 1, 0.8f);

		// 默认隐藏窗口
		showOptionsWindow(false, false);

		if (debugEnabled)
			winOptions.debug();

		// 让TableLayout重新计算部件尺寸和位置
		winOptions.pack();

		// 默认位置为右下角
		winOptions.setPosition(
				Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	// 添加声音设置项
	private Table buildOptWinAudioSettings() {
		Table tbl = new Table();
		// tbl.debug();

		// 添加“Audio”标题
		// 设置外边距(上，左，下，右)
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE))
				.colspan(5);
		// 换行
		tbl.row();
		// 第一列，右边距为10
		tbl.columnDefaults(0).padRight(10);
		// 第一列，右边距为10
		tbl.columnDefaults(1).padRight(10);

		/**
		 * 音效设置选项
		 */
		// 添加复选框
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);

		// 添加"音效"标签
		tbl.add(new Label("Sound", skinLibgdx));

		// 添加音量滑动条
		sldSound = new Slider(0.0f, 1.0f, 0.000001f, false, skinLibgdx);
		tbl.add(sldSound);

		// 换行
		tbl.row();

		/**
		 * 音乐设置选项
		 */
		// 添加复选框
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		// 添加"音乐"标签
		tbl.add(new Label("Music", skinLibgdx));
		// 添加音量滑动条
		sldMusic = new Slider(0.0f, 1.0f, 0.000001f, false, skinLibgdx);
		tbl.add(sldMusic);

		// 换行
		tbl.row();
		return tbl;
	}

	// 创建主角颜色设置项
	private Table buildOptWinSkinSelection() {
		Table tbl = new Table();
		// tbl.debug();

		// 设置边距
		tbl.pad(10, 10, 0, 10);
		// 添加标题
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font",
				Color.ORANGE)).colspan(2);

		// 换行
		tbl.row();

		// 下拉框
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		// 兼容HTML5
		/*
		 * Array<CharacterSkin> items = new Array<CharacterSkin>();
		 * CharacterSkin[] arr = CharacterSkin.values(); for (int i = 0; i <
		 * arr.length; i++) { items.add(arr[i]); }
		 */
		selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCharSkinSelected(((SelectBox<CharacterSkin>) actor)
						.getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);

		// 设置预览图片
		imgCharSkin = new Image(Assets.instance.bunny.head);
		tbl.add(imgCharSkin).width(50).height(50);

		return tbl;
	}

	// 下拉框选项被选中
	private void onCharSkinSelected(int index) {
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}

	// 创建debug设置选项
	private Table buildOptWinDebug() {
		Table tbl = new Table();
		// tbl.debug();

		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED))
				.colspan(3);

		tbl.row();

		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);

		// + Checkbox,
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		// "Show FPS Counter" label
		tbl.add(new Label("Show FPS Counter", skinLibgdx));

		tbl.add(chkShowFpsCounter);

		tbl.row();

		// + Checkbox, "Use Monochrome Shader" label
		chkUseMonoChromeShader = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Use Monochrome Shader", skinLibgdx));
		tbl.add(chkUseMonoChromeShader);
		tbl.row();

		return tbl;
	}

	// 创建设置菜单的按钮和分割线
	private Table buildOptWinButtons() {
		Table tbl = new Table();
		// tbl.debug();

		// 添加分割线
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);

		// 换行
		tbl.row();

		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);

		tbl.row();

		// 添加保存按钮
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).size(60, 30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});

		// 添加取消按钮
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel).size(60, 30);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});

		return tbl;
	}

	// 保存按钮
	private void onSaveClicked() {
		// 保存游戏设置
		saveSettings();
		// 设置窗口
		onCancelClicked();

	}

	// 取消按钮
	private void onCancelClicked() {
		showMenuButtons(true);
		showOptionsWindow(false, true);
		// 设置生效
		AudioManager.instance.onSettingsUpdated();
	}

	// 从配置文件载入游戏设置
	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.soundPlay);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.musicPlay);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
		chkUseMonoChromeShader.setChecked(prefs.useMonochromeShader);
	}

	// 保存游戏设置到配置文件
	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.soundPlay = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.musicPlay = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.useMonochromeShader = chkUseMonoChromeShader.isChecked();
		prefs.save();
	}

	// 显示菜单按钮
	private void showMenuButtons(boolean visible) {
		// 动画持续时间
		float moveDuration = 1.0f;
		// 插值算法
		Interpolation moveEasing = Interpolation.swing;
		// 设置按钮延迟时间
		float delayOptionsButton = 0.25f;

		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton),
				moveBy(moveX, moveY, moveDuration, moveEasing)));

		/**
		 * 等所有动作发生后，恢复按钮事件
		 */
		SequenceAction seq = sequence();
		if (visible) {
			seq.addAction(delay(delayOptionsButton + moveDuration));
		}
		// run方法加载seq动作执行完后执行
		seq.addAction(run(new Runnable() {
			public void run() {
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}

	// 显示设置窗口
	private void showOptionsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 0.5f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled),
				alpha(alphaTo, duration)));
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

}
