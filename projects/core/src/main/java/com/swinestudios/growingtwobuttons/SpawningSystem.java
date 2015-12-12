package com.swinestudios.growingtwobuttons;

import java.util.Random;

import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class SpawningSystem{

	public boolean isActive;
	
	//TODO adjust constants later
	public final float spawnOffsetX = 64; //How far off screen horizontally a projectile spawns
	public final float spawnOffsetY = 32; //Y spawn coordinate from 0 to screen height - spawnOffsetY
	public final float projectileVelY = 0.2f; //Due to relativity, projectiles move downward
	public float projectileVelX = 0.5f;
	
	public float spawnTimer;
	public float maxSpawnTimer;

	public Gameplay level;
	public String type;
	
	public Random random = new Random();
	
	//Variety of projectile sprites
	public Sprite frisbee = new Sprite(new Texture(Gdx.files.internal("frisbee_temp.png")));
	public Sprite rock = new Sprite(new Texture(Gdx.files.internal("rock_temp.png")));
	public Sprite[] projectileSprites = {frisbee,
										rock};

	public SpawningSystem(Gameplay level){
		isActive = true;
		this.level = level;
		spawnTimer = 0;
		maxSpawnTimer = 2; //TODO adjust initial value later
		type = "SpawningSystem";
	}

	public void render(Graphics g){
		//TODO empty?
	}

	public void update(float delta){
		if(isActive){
			if(spawnTimer < maxSpawnTimer){
				spawnTimer += delta;
				if(spawnTimer > maxSpawnTimer){
					spawnTimer = 0;
					spawnProjectile();
				}
			}
		}
	}
	
	public void spawnProjectile(){
		//Determines which sprite is chosen
		int choice = random.nextInt(projectileSprites.length);
		//Determines which side the projectile spawns on
		boolean side = random.nextBoolean();
		float spawnX;
		float spawnY;
		float velX;
		float velY = projectileVelY;
		
		if(side){ //Left side
			spawnX = -spawnOffsetX;
			velX = projectileVelX;
		}
		else{ //Right side
			spawnX = Gdx.graphics.getWidth() + spawnOffsetX;
			velX = -projectileVelX;
		}
		spawnY = random.nextInt((int) (level.tree.hitbox.height)); //TODO adjust later
		
		Projectile p = new Projectile(spawnX, spawnY, velX, velY, level);
		p.setSprite(projectileSprites[choice]);
		level.projectiles.add(p);
	}

	/*
	 * Sets up any images that this tower may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			if(s != null){
				s[i].setOrigin(0, 0);
				s[i].flip(false, true);
			}
		}
	}

}
