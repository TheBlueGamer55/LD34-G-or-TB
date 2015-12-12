package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.game.ScreenBasedGame;

public class GrowingTwoButtons extends ScreenBasedGame {
	
	public static final String GAME_IDENTIFIER = "com.swinestudios.growingtwobuttons";
	
	@Override
	public void initialise() {
		this.addScreen(new MainMenu());
		this.addScreen(new Gameplay());
	}	

	@Override
	public int getInitialScreenId() {
		return MainMenu.ID;
	}
	
}
