package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;

public class MainMenu implements GameScreen{
	
	public static int ID = 1;
	
	public Sprite background;
	
	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		background = new Sprite(new Texture(Gdx.files.internal("main_menu.png")));
		background.setSize(background.getWidth() * 2, background.getHeight() * 2);
	}

	@Override
	public void postTransitionIn(Transition t){
		
	}

	@Override
	public void postTransitionOut(Transition t){
		
	}

	@Override
	public void preTransitionIn(Transition t){
		
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawSprite(background);
		g.drawString("Highest height reached: " + (int)Gameplay.maxScore + " roots", 225, 274);
		//g.drawString("Highest height reached: " + (int)Gameplay.maxScore + " roots", Gdx.input.getX(), Gdx.input.getY());
		//System.out.println(Gdx.input.getX() + ", " + Gdx.input.getY());
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta) {
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
			sm.enterGameScreen(Gameplay.ID, new FadeOutTransition(), new FadeInTransition());
		}
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
	}
	
	@Override
	public void interpolate(GameContainer gc, float delta){
	}
	
	@Override
	public void onPause() {
	}

	@Override
	public void onResize(int arg0, int arg1) {
	}

	@Override
	public void onResume() {
	}

}
