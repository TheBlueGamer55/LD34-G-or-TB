package com.swinestudios.growingtwobuttons;

import java.util.Random;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class TreeProjectile{

	public float initialX, initialY, initialWidth, initialHeight;
	public float x, y, velY, accelY;
	public final float maxVelY = 8.5f; //Max dropping speed
	public final float crashAccelY = 0.05f; //The falling acceleration
	//public float fallingSpeed = 2.0f;
	public final float maxGrowthWidth = 64;
	public final float maxGrowthHeight = 64;

	public float growthTimer;
	public final float growthBonus = 0.2f; //A selected acorn will grow faster
	public float currentMaxGrowthTimer = 2.0f; //How long before this projectile can start growing again
	public final float maximumGrowthTime = 3.0f;
	
	public boolean isActive;
	public boolean canGrow;
	public boolean isSelected;
	public boolean onTree;

	public Rectangle hitbox;
	public Gameplay level;
	public String type;
	public Sprite projectileSprite;
	
	public Random random = new Random();

	public TreeProjectile(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		initialX = x;
		initialY = y;
		velY = 0;
		accelY = 0;
		isActive = true;
		canGrow = true;
		isSelected = false;
		onTree = true;
		this.level = level;
		type = "TreeProjectile";
		projectileSprite = new Sprite(new Texture(Gdx.files.internal("acorn_temp.png")));
		projectileSprite.setOrigin(projectileSprite.getWidth() / 2, 2); //TODO test for scaling up from correct origin
		hitbox = new Rectangle(x, y, projectileSprite.getWidth(), projectileSprite.getHeight()); //TODO make dimensions based on sprite
		initialWidth = hitbox.width;
		initialHeight = hitbox.height;
	}

	public void render(Graphics g){
		if(isActive){
			if(projectileSprite != null){
				g.drawSprite(projectileSprite, x, y);
			}
			else{ //Temporary shape placeholder
				g.setColor(new Color(0, 128, 0, 1));
				g.fillRect(x, y, hitbox.width, hitbox.height);
			}
		}
	}

	public void update(float delta){
		if(isActive){
			velY += accelY;
			y += velY;
			
			//Grow until the max dimensions are reached, or if released
			if(canGrow && hitbox.width < maxGrowthWidth && hitbox.height < maxGrowthHeight){
				growSize(delta);
			}
			
			hitbox.setX(x);
			hitbox.setY(y);
			
			//Projectiles that have fallen off-screen are reset
			if(y > Gdx.graphics.getHeight()){
				reset();
			}
			
			checkProjectileCollision();
		}
		else{ //Has fallen off screen
			growthTimer += delta;
			if(growthTimer > currentMaxGrowthTimer){ //Start the growth process
				growthTimer = 0;
				isActive = true;
				canGrow = true;
				onTree = true;
			}
		}
	}
	
	/*
	 * Resets this projectile and starts timer before it can start growing again
	 */
	public void reset(){
		x = initialX;
		y = initialY;
		velY = 0;
		accelY = 0;
		isActive = false;
		canGrow = false;
		projectileSprite.setSize(initialWidth, initialHeight);
		hitbox.setWidth(initialWidth);
		hitbox.setHeight(initialHeight);
		hitbox.setX(initialX);
		hitbox.setY(initialY);
		currentMaxGrowthTimer = random.nextFloat() * maximumGrowthTime;
	}
	
	/*
	 * Makes the projectile fall at a constant speed based on its current size
	 */
	public void drop(){
		//TODO different falling speeds based on size?
		//velY = fallingSpeed;
		accelY = crashAccelY;
		canGrow = false;
		onTree = false;
	}
	
	/*
	 * Grows the sprite and hitbox's sizes
	 */
	public void growSize(float delta){
		float growthFactor = delta * 10 + (isSelected ? growthBonus : 0); //TODO adjust growth rate
		//projectileSprite.scale(growthFactor);
		//projectileSprite.setSize(projectileSprite.getScaleX(), projectileSprite.getScaleY());
		projectileSprite.setSize(projectileSprite.getWidth() + growthFactor, projectileSprite.getHeight() + growthFactor);
		x -= growthFactor / 2;
		
		hitbox.setWidth(projectileSprite.getWidth() + growthFactor);
		hitbox.setHeight(projectileSprite.getHeight() + growthFactor);
	}
	
	/*
	 * Set the projectile's sprite
	 */
	public void setSprite(Sprite s){
		projectileSprite = new Sprite(s);
	}
	
	/*
	 * Checks if there is a collision if the object was at the given position.
	 */
	public boolean isColliding(Rectangle other, float x, float y){
		if(other == this.hitbox){ //Make sure solid isn't stuck on itself
			return false;
		}
		if(x < other.x + other.width && x + hitbox.width > other.x && y < other.y + other.height && y + hitbox.height > other.y){
			return true;
		}
		return false;
	}
	
	public void checkProjectileCollision(){
		for(int i = 0; i < level.projectiles.size(); i++){
			Projectile temp = level.projectiles.get(i);
			if(temp != null && temp.isActive){
				if(isColliding(temp.hitbox, this.x, this.y)){ //If there is a collision
					temp.crash();
					temp.velY = this.velY; //Momentum
					//TODO maybe an explosion of particles for added effect?
				}
			}
		}
	}

	/*
	 * Returns the distance between this and the given target
	 */
	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
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
