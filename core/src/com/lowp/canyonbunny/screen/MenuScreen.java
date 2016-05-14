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
 * �˵�����
 * 
 * @author lowp
 *
 */
public class MenuScreen extends AbstractGameScreen {

	private Stage stage;
	// �˵���Ļ�������Skin
	private Skin skinCanyonBunny;
	// ���ÿ�ڸ������Skin
	private Skin skinLibgdx;
	// �˵�
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	// ���ô���
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
		// ��ʼ����̨
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
		// ������̨�ӿڵĴ�С
		stage.getViewport().update(width, height, true);
	}

	/**
	 * ����Ļ������ʱ����
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

	// ��ʼ����̨
	private void rebuildStage() {
		// �˵�����������skin
		skinCanyonBunny = new Skin(
				Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		// ���ô��ڸ������skin
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

		/**
		 * �������еĲ�
		 */
		// ������
		Table layerBackground = buildBackgroundLayer();
		// �����
		Table layerObjects = buildObjectsLayer();
		// Logo��
		Table layerLogos = buildLogosLayer();
		// ���Ʋ�
		Table layerControls = buildControlsLayer();
		// ���ô��ڲ�
		Table layerOptionsWindow = buildOptionsWindowLayer();

		// �����еĲ���ӵ���̨
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

	// ����������
	private Table buildBackgroundLayer() {
		Table layer = new Table();
		// ���ñ���ͼƬ
		imgBackground = new Image(skinCanyonBunny, "background");
		// ������ͼƬ��ӵ�������
		layer.add(imgBackground);
		return layer;
	}

	// ���������
	private Table buildObjectsLayer() {
		Table layer = new Table();
		// ��ӡ���ҡ�����
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

		// ��ӡ�����ͷ������
		imgBunny = new Image(skinCanyonBunny, "bunny");
		layer.addActor(imgBunny);
		imgBunny.addAction(sequence(moveTo(655, 510), delay(4.0f),
				moveBy(-70, -100, 0.5f, Interpolation.fade),
				moveBy(-100, -50, 0.5f, Interpolation.fade),
				moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
		return layer;
	}

	// ÿ������Table��add()������TableLayout�������һ�У�����ζ�ſؼ�����
	// ˮƽ�����������뻻�У����Ե���row()������֮�����expandY()��ζ�ţ�
	// �հ���������ֱ��������

	// ����Logo��
	private Table buildLogosLayer() {
		Table layer = new Table();
		layer.left().top();

		// �����ϷLogo
		imgLogo = new Image(skinCanyonBunny, "logo");
		layer.add(imgLogo).top();

		// ���У�����Y�᷽������հ�����
		layer.row().expandY();

		// �����ϢLogo
		imgInfo = new Image(skinCanyonBunny, "info");
		layer.add(imgInfo).bottom();

		// ���õ�����
		if (debugEnabled) {
			layer.debug();
		}

		return layer;
	}

	// ��ͬһ��������󱻵��õĶ�λ�����������ؼ������ն�λ
	// eg.
	// layer.right().centet().left();���տؼ�������Ļ�����(left()������Ч)

	// �������Ʋ�
	private Table buildControlsLayer() {
		Table layer = new Table();
		layer.right().bottom();

		// ��ӡ���ʼ��Ϸ����ť
		btnMenuPlay = new Button(skinCanyonBunny, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();

			}
		});

		// ����
		layer.row();

		// ������ð�ť
		btnMenuOptions = new Button(skinCanyonBunny, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		// ���õ�����
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	// ��ʼ��Ϸ��ť�����
	private void onPlayClicked() {
		// ����һ��ת����Ч,����ʱ��Ϊ0.75s
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		// ��ת����Ϸ����
		game.setScreen(new GameScreen(game), transition);
	}

	// ���ð�ť�����
	private void onOptionsClicked() {
		// ������Ϸ����
		loadSettings();
		// ���ذ�ť
		showMenuButtons(false);
		// ��ʾ�˵�����
		showOptionsWindow(true, true);
	}

	/**
	 * �������ô��ڲ�
	 */
	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", skinLibgdx);

		// �������������: ��Ч/���ֵĸ�ѡ��ͻ�����
		winOptions.add(buildOptWinAudioSettings()).row();

		// ���������ɫ������: ����ѡ���(��ɫ����ɫ����ɫ)
		winOptions.add(buildOptWinSkinSelection()).row();

		// ���Bugѡ��: ��ʾFPS
		winOptions.add(buildOptWinDebug()).row();

		// ��ӷָ��ߺͰ�ť (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

		// ���ô���͸����
		winOptions.setColor(1, 1, 1, 0.8f);

		// Ĭ�����ش���
		showOptionsWindow(false, false);

		if (debugEnabled)
			winOptions.debug();

		// ��TableLayout���¼��㲿���ߴ��λ��
		winOptions.pack();

		// Ĭ��λ��Ϊ���½�
		winOptions.setPosition(
				Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	// �������������
	private Table buildOptWinAudioSettings() {
		Table tbl = new Table();
		// tbl.debug();

		// ��ӡ�Audio������
		// ������߾�(�ϣ����£���)
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE))
				.colspan(5);
		// ����
		tbl.row();
		// ��һ�У��ұ߾�Ϊ10
		tbl.columnDefaults(0).padRight(10);
		// ��һ�У��ұ߾�Ϊ10
		tbl.columnDefaults(1).padRight(10);

		/**
		 * ��Ч����ѡ��
		 */
		// ��Ӹ�ѡ��
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);

		// ���"��Ч"��ǩ
		tbl.add(new Label("Sound", skinLibgdx));

		// �������������
		sldSound = new Slider(0.0f, 1.0f, 0.000001f, false, skinLibgdx);
		tbl.add(sldSound);

		// ����
		tbl.row();

		/**
		 * ��������ѡ��
		 */
		// ��Ӹ�ѡ��
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		// ���"����"��ǩ
		tbl.add(new Label("Music", skinLibgdx));
		// �������������
		sldMusic = new Slider(0.0f, 1.0f, 0.000001f, false, skinLibgdx);
		tbl.add(sldMusic);

		// ����
		tbl.row();
		return tbl;
	}

	// ����������ɫ������
	private Table buildOptWinSkinSelection() {
		Table tbl = new Table();
		// tbl.debug();

		// ���ñ߾�
		tbl.pad(10, 10, 0, 10);
		// ��ӱ���
		tbl.add(new Label("Character Skin", skinLibgdx, "default-font",
				Color.ORANGE)).colspan(2);

		// ����
		tbl.row();

		// ������
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		// ����HTML5
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

		// ����Ԥ��ͼƬ
		imgCharSkin = new Image(Assets.instance.bunny.head);
		tbl.add(imgCharSkin).width(50).height(50);

		return tbl;
	}

	// ������ѡ�ѡ��
	private void onCharSkinSelected(int index) {
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}

	// ����debug����ѡ��
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

	// �������ò˵��İ�ť�ͷָ���
	private Table buildOptWinButtons() {
		Table tbl = new Table();
		// tbl.debug();

		// ��ӷָ���
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);

		// ����
		tbl.row();

		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);

		tbl.row();

		// ��ӱ��水ť
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).size(60, 30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});

		// ���ȡ����ť
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

	// ���水ť
	private void onSaveClicked() {
		// ������Ϸ����
		saveSettings();
		// ���ô���
		onCancelClicked();

	}

	// ȡ����ť
	private void onCancelClicked() {
		showMenuButtons(true);
		showOptionsWindow(false, true);
		// ������Ч
		AudioManager.instance.onSettingsUpdated();
	}

	// �������ļ�������Ϸ����
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

	// ������Ϸ���õ������ļ�
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

	// ��ʾ�˵���ť
	private void showMenuButtons(boolean visible) {
		// ��������ʱ��
		float moveDuration = 1.0f;
		// ��ֵ�㷨
		Interpolation moveEasing = Interpolation.swing;
		// ���ð�ť�ӳ�ʱ��
		float delayOptionsButton = 0.25f;

		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(delay(delayOptionsButton),
				moveBy(moveX, moveY, moveDuration, moveEasing)));

		/**
		 * �����ж��������󣬻ָ���ť�¼�
		 */
		SequenceAction seq = sequence();
		if (visible) {
			seq.addAction(delay(delayOptionsButton + moveDuration));
		}
		// run��������seq����ִ�����ִ��
		seq.addAction(run(new Runnable() {
			public void run() {
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}

	// ��ʾ���ô���
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
