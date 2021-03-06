package com.swinestudios.growingtwobuttons;

import java.util.Random;

import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class SpawningSystem{

	public boolean isActive;
	
	//TODO adjust constants later
	public final float spawnOffsetX = 128; //How far off screen horizontally a projectile spawns
	public final float minSpawnY = 155f, maxSpawnY = 375f; //Y spawn coordinate from 155 to 375
	public final float projectileVelY = 0.2f; //Due to relativity, projectiles move downward
	public float projectileVelX = 0.8f;
	
	public float spawnTimer;
	public float maxSpawnTimer;
	public final float initialSpawnTimer = 5f; //Starting difficulty
	
	public float difficultyTimer;
	public final float maxDifficultyTimer = 10f; //Duration of current difficulty TODO adjust later
	public final float difficultyRate = 1f; //How much faster each difficulty gets

	public Gameplay level;
	public String type;
	
	public Random random = new Random();
	
	/*
	 * Variety of projectile sprites
	 * 0 = White dwarf
	 * 1 = asteroid 1
	 * 2 = asteroid 2
	 * 3 = planet blue
	 */
	public Sprite whiteDwarf = new Sprite(new Texture(Gdx.files.internal("white_dwarf.png")));
	public Sprite asteroid1 = new Sprite(new Texture(Gdx.files.internal("asteroid1.png")));
	public Sprite asteroid2 = new Sprite(new Texture(Gdx.files.internal("asteroid2.png")));
	public Sprite planet1 = new Sprite(new Texture(Gdx.files.internal("planet1.png")));
	public Sprite[] projectileSprites = {whiteDwarf,
										asteroid1,
										asteroid2,
										planet1};

	public SpawningSystem(Gameplay level){
		isActive = true;
		this.level = level;
		spawnTimer = 0;
		difficultyTimer = 0;
		maxSpawnTimer = initialSpawnTimer; 
		type = "SpawningSystem";
		//Unique scaling for planet1
		planet1.setSize(planet1.getWidth() * 4, planet1.getHeight() * 4);
	}

	public void render(Graphics g){
		//Empty
	}

	public void update(float delta){
		if(isActive){
			if(spawnTimer <= maxSpawnTimer){
				spawnTimer += delta;
				if(spawnTimer > maxSpawnTimer){
					spawnTimer = 0;
					spawnProjectile();
				}
			}
			if(difficultyTimer < maxDifficultyTimer){
				difficultyTimer += delta;
				if(difficultyTimer > maxDifficultyTimer){
					difficultyTimer = 0;
					if(maxSpawnTimer - difficultyRate > spawnTimer){
						maxSpawnTimer -= difficultyRate;
					}
				}
			}
			//System.out.println(spawnTimer + "\t" + difficultyTimer);
		}
	}
	
	public void spawnProjectile(){
		//Determines which sprite is chosen
		int choice = random.nextInt(projectileSprites.length);
		//Remove white dwarf - hardcoded "removal"
		if(choice == 0){
			choice = random.nextInt(projectileSprites.length - 1) + 1;
		}
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
		spawnY = 90 + random.nextInt((int) level.tree.hitbox.height); //TODO adjust later
		spawnY = random.nextInt((int) ((maxSpawnY - minSpawnY) + 1)) + minSpawnY;
		
		Projectile p = new Projectile(spawnX, spawnY, velX, velY, level);
		p.setSprite(projectileSprites[choice]);
		p.setID(choice);
		level.projectiles.add(p);
		if(choice == 0){ //If white dwarf
			//Create particle light effect TODO adjust constants later
			p.setEffect(Color.WHITE, 75, 112, 6.0f);
		}
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
