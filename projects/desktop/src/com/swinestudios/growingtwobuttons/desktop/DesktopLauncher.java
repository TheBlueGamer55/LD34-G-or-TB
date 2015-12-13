package com.swinestudios.growingtwobuttons.desktop;

import org.mini2Dx.desktop.DesktopMini2DxConfig;

import com.badlogic.gdx.backends.lwjgl.DesktopMini2DxGame;

import com.swinestudios.growingtwobuttons.GrowingTwoButtons;

public class DesktopLauncher {
	public static void main (String[] arg) {
		DesktopMini2DxConfig config = new DesktopMini2DxConfig(GrowingTwoButtons.GAME_IDENTIFIER);
		config.vSyncEnabled = true;
		config.width = 640;
		config.height = 480;
		config.vSyncEnabled = true;
		config.resizable = false;
        config.foregroundFPS = 30;
        config.backgroundFPS = 30;
        config.title = "TO BE NAMED LATER";
		new DesktopMini2DxGame(new GrowingTwoButtons(), config);
	}
}
