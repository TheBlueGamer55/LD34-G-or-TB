package com.swinestudios.growingtwobuttons;

import java.util.ArrayList;

import org.mini2Dx.core.game.GameContainer;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.screen.GameScreen;
import org.mini2Dx.core.screen.ScreenManager;
import org.mini2Dx.core.screen.Transition;
import org.mini2Dx.core.screen.transition.FadeInTransition;
import org.mini2Dx.core.screen.transition.FadeOutTransition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;

public class Gameplay implements GameScreen{
	
	public static int ID = 2;
	
	public TreeTrunk tree;
	public SpawningSystem spawner;
	
	public ArrayList<Projectile> projectiles;
	
	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		
	}

	@Override
	public void postTransitionIn(Transition t){
		
	}

	@Override
	public void postTransitionOut(Transition t){
		
	}

	@Override
	public void preTransitionIn(Transition t){
		projectiles = new ArrayList<Projectile>();
		tree = new TreeTrunk(300, 100, this); //TODO adjust position later
		spawner = new SpawningSystem(this);
		
		//Test projectiles
		projectiles.add(new Projectile(20, 330, 4, 0, this));
		
		//Input handling
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(tree);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void preTransitionOut(Transition t){
		
	}

	@Override
	public void render(GameContainer gc, Graphics g){
		g.drawString("This is the gameplay screen", 320, 240);
		renderProjectiles(g);
		tree.render(g);
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta) {
		updateProjectiles(delta);
		tree.update(delta);
		spawner.update(delta);
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
		}
	}
	
	public void renderProjectiles(Graphics g){
		for(int i = 0; i < projectiles.size(); i++){
			projectiles.get(i).render(g);
		}
	}

	public void updateProjectiles(float delta){
		for(int i = 0; i < projectiles.size(); i++){
			projectiles.get(i).update(delta);
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
