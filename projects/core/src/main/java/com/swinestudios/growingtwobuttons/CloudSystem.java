package com.swinestudios.growingtwobuttons;

import java.util.Random;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class CloudSystem{

	public float x, y;
	public final float velX = 2f; //Clouds move right TODO adjust constants later
	public float systemVelY;
	public final float systemAccelY = 0.2f;
	public final float cloudAccelY = 0.5f;
	public final float maxVelY = 16f;
	
	public Sprite cloud1 = new Sprite(new Texture(Gdx.files.internal("cloud1.png")));
	public Sprite cloud2 = new Sprite(new Texture(Gdx.files.internal("cloud2.png")));
	public Sprite cloud3 = new Sprite(new Texture(Gdx.files.internal("cloud3.png")));
	public Sprite[] cloudSprites = {cloud1, cloud2, cloud3};
	public Cloud[] clouds;

	public boolean isActive;
	public boolean isTimed; //If clouds are currently rushing down
	public boolean isTransition; //If the rushing clouds are ending
	public float lifeTimer;
	public float maxLifeTimer = 3f;

	public Gameplay level;
	public String type;

	public Random random = new Random();

	public CloudSystem(float x, float y, int amount, Gameplay level){
		this.x = x;
		this.y = y;
		systemVelY = 0;
		isActive = true;
		isTransition = false;
		this.level = level;
		type = "Particles";
		isTimed = false;
		lifeTimer = 0;
		generateClouds(amount);
		startCloudRush();
	}

	public void render(Graphics g){
		if(isActive){
			drawClouds(g);
		}
	}

	public void update(float delta){
		if(isActive){
			y += systemVelY;
			updateClouds(delta);
			
			if(isTimed && !isTransition){ //If transition to outer space is happening
				lifeTimer += delta;
				if(lifeTimer > maxLifeTimer){
					isTransition = true;
					lifeTimer = 0;
				}
			}
			if(isTransition){ //If the end of the cloud rush is occuring
				systemVelY += systemAccelY;
				if(y > Gdx.graphics.getHeight()){
					isActive = false;
					for(int i = 0; i < clouds.length; i++){
						clouds[i].isActive = false;
					}
					level.cloudsRushing = false;
					level.cloudsTimer = 0;
				}
			}
		}
	}

	public void setTimer(float lifetime){
		maxLifeTimer = lifetime;
		isTimed = true;
	}

	public void generateClouds(int amount){
		//Create clouds
		clouds = new Cloud[amount];
		float spawnX, spawnY;
		int choice;
		for(int i = 0; i < amount; i++){
			spawnX = random.nextInt(Gdx.graphics.getWidth());
			spawnY = random.nextInt(Gdx.graphics.getHeight());
			choice = random.nextInt(cloudSprites.length);
			clouds[i] = new Cloud(spawnX, spawnY, velX, maxVelY, this);
			clouds[i].setSprite(cloudSprites[choice]);
		}
	}
	
	public void startCloudRush(){
		for(int i = 0; i < clouds.length; i++){
			clouds[i].accelY = cloudAccelY;
		}
	}

	public void drawClouds(Graphics g){
		for(int i = 0; i < clouds.length; i++){
			clouds[i].render(g);
		}
	}

	public void updateClouds(float delta){
		for(int i = 0; i < clouds.length; i++){
			clouds[i].update(delta);
		}
	}

	/*
	 * Returns the distance between this and the given target
	 */
	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	public float distanceTo(Circle target){
		return ((float)Math.pow(Math.pow((target.getY() - this.y), 2.0) + Math.pow((target.getX() - this.x), 2.0), 0.5));
	}

}

class Cloud{
	public float x, y, velX, velY, accelY;
	public float maxSpeed;
	public CloudSystem parent;
	public boolean stayInRange;
	public boolean isActive;
	
	public Sprite currentSprite;

	public Cloud(float x, float y, float speed, float maxSpeed, CloudSystem parent){
		this.x = x;
		this.y = y;
		velX = speed;
		velY = 0;
		accelY = 0;
		this.maxSpeed = maxSpeed;
		this.parent = parent;
		stayInRange = true;
		isActive = true;
	}

	public void render(Graphics g){
		if(isActive){
			g.drawSprite(currentSprite, x, y);
		}
	}

	public void update(float delta){
		if(isActive){
			if(velY <= maxSpeed){
				velY += accelY;
			}
			x += velX;
			y += velY; //TODO check if this works
			
			if(x > Gdx.graphics.getWidth()){ //Left to right movement
				x = -currentSprite.getWidth();
			}
			if(y > Gdx.graphics.getHeight()){ //Vertical wrapping
				if(!parent.isTransition){ //During the cloud rush, just respawn at the top
					x = parent.random.nextInt((int) (Gdx.graphics.getWidth() - currentSprite.getWidth()));
					y = parent.y -currentSprite.getHeight();
				}
			}
		}
	}
	
	public void setSprite(Sprite s){
		currentSprite = s;
	}

}
