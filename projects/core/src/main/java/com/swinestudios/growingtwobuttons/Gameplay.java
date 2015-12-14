package com.swinestudios.growingtwobuttons;

import java.util.ArrayList;
import java.util.Random;

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
import com.badlogic.gdx.graphics.Color;

public class Gameplay implements GameScreen{

	public static int ID = 2;

	public float score; //Height reached is score
	public static float maxScore = 0;
	public boolean gameOver = false;
	public boolean paused = false;
	
	public boolean isShaking;
	public final int shakeMagnitude = 12; //Screen shake
	public float screenShakeTimer;
	public float maxScreenShakeTimer = 0.5f;
	
	public final int starAmount = 80; //For the moving stars in the background
	public Star[] stars;

	public TreeTrunk tree;
	public SpawningSystem spawner;

	public ArrayList<Projectile> projectiles;
	public ArrayList<TreeProjectile> treeProjectiles;
	public ArrayList<Particles> debris;
	
	public Random random;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		random = new Random();
		stars = new Star[starAmount];
		for(int i = 0; i < stars.length; i++){
			stars[i] = new Star(random.nextInt(gc.getWidth()), random.nextInt(gc.getHeight()), true);
		}
	}

	@Override
	public void postTransitionIn(Transition t){

	}

	@Override
	public void postTransitionOut(Transition t){
		gameOver = false;
		paused = false;
		score = 0;
	}

	@Override
	public void preTransitionIn(Transition t){
		isShaking = false;
		gameOver = false;
		paused = false;
		score = 0;

		treeProjectiles = new ArrayList<TreeProjectile>();
		projectiles = new ArrayList<Projectile>();
		debris = new ArrayList<Particles>();
		tree = new TreeTrunk(300, 100, this); //TODO adjust position later
		spawner = new SpawningSystem(this);

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
		//Screen shake effect
		if(isShaking){
			g.translate(random.nextInt(shakeMagnitude * 2) - shakeMagnitude, random.nextInt(shakeMagnitude * 2) - shakeMagnitude);
		}
		else{
			g.translate(0, 0);
		}
		
		renderStars(g);
		tree.render(g);
		renderTreeProjectiles(g);
		renderDebris(g);
		renderProjectiles(g);
		tree.renderSelector(g);
		//System.out.println(Gdx.input.getX() + ", " + Gdx.input.getY()); //TODO remove later
		
		//TODO adjust UI for each menu
		if(gameOver){
			isShaking = false;
			g.setColor(Color.RED);
			g.drawString("You died! Press Escape to go back to the main menu", 160, 240);
		}
		if(paused){
			g.setColor(Color.RED);
			g.drawString("Are you sure you want to quit? Y or N", 220, 240);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta) {
		if(!paused && !gameOver){
			if(isShaking){
				screenShakeTimer += delta;
				if(screenShakeTimer >= maxScreenShakeTimer){
					screenShakeTimer = 0;
					isShaking = false;
				}
			}
			
			updateStars(delta);
			updateDebris(delta);
			updateProjectiles(delta);
			updateTreeProjectiles(delta);
			tree.update(delta);
			tree.updateSelector(delta);
			spawner.update(delta);
			
			//Update score
			score += delta*100;
			if(score > maxScore){
				maxScore = score;
			}
			
			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
				paused = true;
			}
		}
		else{
			if(gameOver){
				if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
			}
			else if(paused){
				if(Gdx.input.isKeyJustPressed(Keys.Y)){
					sm.enterGameScreen(MainMenu.ID, new FadeOutTransition(), new FadeInTransition());
				}
				if(Gdx.input.isKeyJustPressed(Keys.N)){
					paused = false;
				}
			}
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
	
	public void renderTreeProjectiles(Graphics g){
		for(int i = 0; i < treeProjectiles.size(); i++){
			treeProjectiles.get(i).render(g);
		}
	}

	public void updateTreeProjectiles(float delta){
		for(int i = 0; i < treeProjectiles.size(); i++){
			treeProjectiles.get(i).update(delta);
		}
	}
	
	public void renderDebris(Graphics g){
		for(int i = 0; i < debris.size(); i++){
			debris.get(i).render(g);
		}
	}
	
	public void updateDebris(float delta){
		for(int i = 0; i < debris.size(); i++){
			debris.get(i).update(delta);
		}
	}
	
	public void renderStars(Graphics g){
		for(int i = 0; i < stars.length; i++){ 
			stars[i].render(g);
		}
	}
	
	public void updateStars(float delta){
		for(int i = 0; i < stars.length; i++){
			stars[i].update(delta);
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
