package com.swinestudios.growingtwobuttons;

import java.util.ArrayList;
import java.util.Random;

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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Gameplay implements GameScreen{

	public static int ID = 2;

	public float score; //Height reached is score
	public static float maxScore = 0;
	public boolean gameOver = false;
	public boolean paused = false;
	
	public boolean cloudsRushing;
	public float cloudsTimer;
	public final float maxCloudsTimer = 2f; //How long the intro cloud scene lasts
	public final int cloudAmount = 100; //Total number of clouds
	
	public boolean showingIntro;
	public float introTimer;
	public float maxIntroTimer = 2f;

	public boolean isShaking;
	public final int shakeMagnitude = 12; //Screen shake
	public float screenShakeTimer;
	public float maxScreenShakeTimer = 0.5f;

	public final int starAmount = 80; //For the moving stars in the background
	public Star[] stars;

	public TreeTrunk tree;
	public SpawningSystem spawner;
	public CloudSystem cloudSystem;
	public Color backgroundColor;
	public final float bgRed = 0, bgGreen = 145, bgBlue = 255;

	public ArrayList<Projectile> projectiles;
	public ArrayList<TreeProjectile> treeProjectiles;
	public ArrayList<Particles> debris;
	
	public Sprite introMessage, pauseMessage, gameOverMessage;
	
	//public static Sound theme; //TODO need music

	public Random random;

	@Override
	public int getId(){
		return ID;
	}

	@Override
	public void initialise(GameContainer gc){
		//theme = Gdx.audio.newSound(Gdx.files.internal(""));
		introMessage = new Sprite(new Texture(Gdx.files.internal("intro_text.png")));
		introMessage.scale(1);
		pauseMessage = new Sprite(new Texture(Gdx.files.internal("pause_quit_text.png")));
		gameOverMessage = new Sprite(new Texture(Gdx.files.internal("dead_text.png")));
		random = new Random();
		stars = new Star[starAmount];
		for(int i = 0; i < stars.length; i++){
			stars[i] = new Star(random.nextInt(gc.getWidth()), random.nextInt(gc.getHeight()), true);
		}
	}

	@Override
	public void postTransitionIn(Transition t){
		//theme.loop();
	}

	@Override
	public void postTransitionOut(Transition t){
		gameOver = false;
		paused = false;
		score = 0;
		//theme.stop();
	}

	@Override
	public void preTransitionIn(Transition t){
		backgroundColor = new Color(bgRed / 255f, bgGreen / 255f, bgBlue / 255f, 1);
		isShaking = false;
		gameOver = false;
		paused = false;
		cloudsRushing = true;
		showingIntro = true;
		score = 0;
		introTimer = 0;
		cloudsTimer = 0;

		treeProjectiles = new ArrayList<TreeProjectile>();
		projectiles = new ArrayList<Projectile>();
		debris = new ArrayList<Particles>();
		tree = new TreeTrunk(300, 100, this); 
		spawner = new SpawningSystem(this);
		cloudSystem = new CloudSystem(0, 0, cloudAmount, this);
		cloudSystem.isTimed = true;

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
		//Blue sky transition
		if(cloudSystem.isTransition){
			float ratio = 1 - (cloudSystem.y / Gdx.graphics.getHeight());
			backgroundColor.set(0, bgGreen * ratio / 255f, bgBlue * ratio / 255f, 1);
		}
		g.setBackgroundColor(backgroundColor);
		
		//Screen shake effect
		if(isShaking){
			g.translate(random.nextInt(shakeMagnitude * 2) - shakeMagnitude, random.nextInt(shakeMagnitude * 2) - shakeMagnitude);
		}
		else{
			g.translate(0, 0);
		}

		if(cloudSystem.isTransition || !cloudsRushing){
			renderStars(g); //Don't draw stars if still on blue sky with clouds
		}
		tree.render(g);
		if(cloudsRushing){
			cloudSystem.render(g);
		}
		renderTreeProjectiles(g);
		renderDebris(g);
		renderProjectiles(g);
		tree.renderSelector(g);
		//System.out.println(Gdx.input.getX() + ", " + Gdx.input.getY()); //TODO placement debug

		if(showingIntro){
			g.drawSprite(introMessage, 255, 216);
		}
		if(gameOver){
			isShaking = false;
			g.drawSprite(gameOverMessage, 200, 214);
			//g.setColor(Color.RED);
			//g.drawString("You died! Press Escape to go back to the main menu", 160, 240);
		}
		if(paused){
			g.drawSprite(pauseMessage, 213, 200);
			//g.setColor(Color.RED);
			//g.drawString("Are you sure you want to quit? Y or N", 220, 240);
		}
	}

	@Override
	public void update(GameContainer gc, ScreenManager<? extends GameScreen> sm, float delta) {
		if(!paused && !gameOver){
			if(showingIntro){
				introTimer += delta;
				if(introTimer > maxIntroTimer){
					introTimer = 0;
					showingIntro = false;
				}
			}
			if(cloudsRushing){ //Intro scene
				tree.update(delta);
				tree.updateSelector(delta);
				cloudSystem.update(delta);
				if(cloudSystem.isTransition){
					updateStars(delta);
				}
				cloudsTimer += delta;
				if(cloudsTimer > maxCloudsTimer){
					cloudSystem.isTransition = true;
				}
			}
			else{ //Main gameplay
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
