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

	public Projectile(float x, float y, float velX, float velY, Gameplay level){
		this.x = x;
		this.y = y;
		this.velX = velX;
		this.velY = velY;
		accelY = 0;
		isActive = true;
		isFalling = false;
		this.level = level;
		type = "Projectile";
		//projectileSprite = new Sprite(new Texture(Gdx.files.internal("______.png")));
		//adjustSprite(projectileSprite);
		hitbox = new Rectangle(x, y, 16, 16); //Temporary
	}

	public void render(Graphics g){
		if(isActive){
			if(projectileSprite != null){
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
		}
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
