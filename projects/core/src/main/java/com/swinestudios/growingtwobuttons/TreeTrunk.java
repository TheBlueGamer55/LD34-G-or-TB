package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class TreeTrunk implements InputProcessor{ 

	public float x, y;
	public float velX, velY;

	public boolean isActive;
	//public boolean isAttacking;

	public Sprite trunk;
	
	public final int maxHealth = 3; //TODO adjust later
	public int health;

	public Rectangle hitbox;
	public Gameplay level;
	public String type;

	//public static Sound hurt = Gdx.audio.newSound(Gdx.files.internal(""));

	//Controls/key bindings
	public final int LEFT = Keys.LEFT;
	public final int RIGHT = Keys.RIGHT;

	public TreeTrunk(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		velX = 0;
		velY = 0;
		health = maxHealth;
		isActive = true;
		this.level = level;
		type = "TreeTrunk";
		
		//trunk = new Sprite(new Texture(Gdx.files.internal("")));
		hitbox = new Rectangle(x, y, 40, 380); //adjust size later based on sprite
	}

	public void render(Graphics g){
		//Debug - remove later
		g.drawString("" + (int)Math.floor(level.score), x, y);
		g.drawRect(x, y, hitbox.width, hitbox.height);
	}

	public void update(float delta){
		hitbox.setX(this.x);
		hitbox.setY(this.y);

		checkProjectileCollision();
	}

	public void checkProjectileCollision(){
		for(int i = 0; i < level.projectiles.size(); i++){
			Projectile temp = level.projectiles.get(i);
			if(temp != null && temp.isActive){
				if(isColliding(temp.hitbox, this.x, this.y)){ //If there is a collision
					level.projectiles.remove(temp);
					dealDamage();
				}
			}
		}
	}

	/*
	 * Checks if there is a collision if the player was at the given position.
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
	
	/*
	 * Deal damage to trunk and handle game over if out of health
	 */
	public void dealDamage(){
		//TODO deal damage to trunk
		System.out.println("Tree was hit");
		health--;
		if(health <= 0){
			level.gameOver = true;
		}
	}

	/*
	 * Returns the distance between the player and the given target
	 */
	public float distanceTo(Rectangle target){
		return ((float)Math.pow(Math.pow((target.y - this.y), 2.0) + Math.pow((target.x - this.x), 2.0), 0.5));
	}

	/*
	 * Sets up any images that the player may have. Necessary because images are flipped and have the origin
	 * on the bottom-left by default.
	 */
	public void adjustSprite(Sprite... s){
		for(int i = 0; i < s.length; i++){
			s[i].setOrigin(0, 0);
			s[i].flip(false, true);
		}
	}

	//========================================Input Methods==============================================

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == LEFT){
			//TODO left branch attack
		}
		if(keycode == RIGHT){
			//TODO right branch attack
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		//TODO empty
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}