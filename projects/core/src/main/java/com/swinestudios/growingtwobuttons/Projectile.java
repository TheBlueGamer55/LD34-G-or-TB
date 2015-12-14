package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Projectile{

	public float x, y, velX, velY, accelY;
	public final float maxVelY = 9.0f; //Max dropping speed
	public final float crashAccelY = 0.08f; //The falling acceleration

	public boolean isActive;
	public boolean isFalling;

	public Rectangle hitbox;
	public Gameplay level;
	public String type;
	public Sprite projectileSprite;
	
	public Particles effect, rockScrap;
	public int ID;

	public Projectile(float x, float y, float velX, float velY, Gameplay level){
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		accelY = 0;
		ID = -1;
		isActive = true;
		isFalling = false;
		this.level = level;
		type = "Projectile";
		effect = null;
		rockScrap = null;
		//projectileSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(projectileSprite);
		hitbox = new Rectangle(x, y, 16, 16); //Temporary
	}

	public void render(Graphics g){
		if(isActive){
			if(projectileSprite != null){
				if(effect != null){
					effect.render(g);
				}
				/*if(rockScrap != null){
					rockScrap.render(g);
				}*/
				g.drawSprite(projectileSprite, x, y);
			}
			else{ //Temporary shape placeholder
				g.setColor(new Color(128, 0, 0, 1));
				g.fillRect(x, y, hitbox.width, hitbox.height);
			}
		}
	}

	public void update(float delta){
		if(isActive){
			if(velY < maxVelY){
				velY += accelY;
			}
			x += velX;
			y += velY;

			hitbox.setX(x);
			hitbox.setY(y);
			
			if(effect != null){
				effect.x = this.hitbox.getCenterX();
				effect.y = this.hitbox.getCenterY();
				effect.update(delta);
			}
			
			/*if(rockScrap != null){
				rockScrap.update(delta);
			}*/
			
			//Projectiles that have fallen off-screen are removed
			if(y > Gdx.graphics.getHeight()){
				level.projectiles.remove(this);
			}
		}
	}
	
	/*
	 * When a projectile gets hit by a tree's projectile, it crashes
	 * downwards at an angle until it disappears at the bottom.
	 */
	public void crash(){
		//TODO rotate sprite while falling
		if(!isFalling){
			isFalling = true;
			accelY = crashAccelY;
			//TODO adjust debris constants later
			if(ID == 1 || ID == 2){ //Debris only for asteroid projectiles
				createDebris(Color.BROWN, 40, 600f, 4.0f, 3, 4);
			}
		}
	}
	
	public void setEffect(Color c, int amount, float radius, float maxSpeed){
		effect = new Particles(hitbox.getCenterX(), hitbox.getCenterY(), amount, radius, maxSpeed, c, level);
	}
	
	public void setID(int n){
		ID = n;
	}
	
	/*
	 * Creates particles that disappear after a given amount of time and are not
	 * restricted to a given range.
	 */
	public void createDebris(Color c, int amount, float radius, float maxSpeed, float lifetime, int size){
		rockScrap = new Particles(hitbox.getCenterX(), hitbox.getCenterY(), amount, radius, maxSpeed, c, level);
		rockScrap.setTimer(lifetime);
		rockScrap.removeRange();
		rockScrap.setParticleSize(size);
		level.debris.add(rockScrap);
	}
	
	/*
	 * Set the projectile's sprite
	 */
	public void setSprite(Sprite s){
		projectileSprite = new Sprite(s);
		hitbox = new Rectangle(x, y, projectileSprite.getWidth(), projectileSprite.getHeight());
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
