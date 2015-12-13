package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;

public class TreeTrunk implements InputProcessor{ 

	public float x, y;
	public float velX, velY;

	public boolean isActive;
	//public boolean isAttacking;
	
	//Spawn coordinates
	public final float[][] spawnPoints = {{60, 0}, {160, 38}, {250, 16}, {400, 60}, {490, 4}, {587, 27}};
	
	public TreeProjectile[] acorns;
	
	public int selection; //Determines which projectile is currently selected
	public Particles selector;
	//Constants for arguments to the selector TODO adjust later
	public final Color selectorColor = Color.YELLOW;
	public final int selectorAmount = 8;
	public final float selectorRadius = 24.0f;
	public final float selectorMaxSpeed = 2.0f;

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
	public final int SPACE = Keys.SPACE;

	public TreeTrunk(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		velX = 0;
		velY = 0;
		selection = 0;
		health = maxHealth;
		isActive = true;
		this.level = level;
		type = "TreeTrunk";
		
		acorns = new TreeProjectile[spawnPoints.length];
		spawnAcorns();
		selector = new Particles(acorns[selection].hitbox.getCenterX(), acorns[selection].hitbox.getCenterY(), 
				selectorAmount, selectorRadius, selectorMaxSpeed, selectorColor, level);
		acorns[selection].isSelected = true;
		
		//trunk = new Sprite(new Texture(Gdx.files.internal("")));
		hitbox = new Rectangle(x, y, 40, 380); //adjust size later based on sprite
	}

	public void render(Graphics g){
		//Debug - remove later
		g.drawString("" + (int)Math.floor(level.score), x, y);
		g.drawString("" + selection, x, y + 20);
		g.setColor(Color.BROWN);
		g.drawRect(x, y, hitbox.width, hitbox.height);
	}

	public void update(float delta){
		hitbox.setX(this.x);
		hitbox.setY(this.y);

		checkProjectileCollision();
	}
	
	/*
	 * Deal damage to trunk and handle game over if out of health
	 */
	public void dealDamage(){
		//TODO deal damage to trunk
		if(!level.isShaking){
			level.isShaking = true;
		}
		health--;
		if(health <= 0){
			level.gameOver = true;
		}
		dropAllAcorns();
	}
	
	/*
	 * Drops all acorns currently on the tree
	 */
	public void dropAllAcorns(){
		for(int i = 0; i < level.treeProjectiles.size(); i++){
			if(level.treeProjectiles.get(i).isActive){
				level.treeProjectiles.get(i).drop();
			}
		}
	}
	
	/*
	 * Precondition: The currently selected acorn is already growing, 0 <= selection < spawnPoints.length
	 */
	public void dropSelectedAcorn(){
		acorns[selection].drop();
	}
	
	/*
	 * Spawns all the acorns at the spawn points on the tree's branches
	 */
	public void spawnAcorns(){
		for(int i = 0; i < acorns.length; i++){
			acorns[i] = new TreeProjectile(spawnPoints[i][0], spawnPoints[i][1], level);
			level.treeProjectiles.add(acorns[i]);
		}
	}
	
	public void renderSelector(Graphics g){
		selector.render(g);
	}
	
	public void updateSelector(float delta){
		//Update selector coordinates
		if(acorns[selection].onTree){
			selector.x = acorns[selection].hitbox.getCenterX();
			selector.y = acorns[selection].hitbox.getCenterY();
		}
		else{
			selector.x = acorns[selection].initialX + 8;
			selector.y = acorns[selection].initialY + 8;
		}
		selector.update(delta);
	}

	public void checkProjectileCollision(){
		for(int i = 0; i < level.projectiles.size(); i++){
			Projectile temp = level.projectiles.get(i);
			if(temp != null && temp.isActive){
				//If there is a collision with an active projectile
				if(!temp.isFalling && isColliding(temp.hitbox, this.x, this.y)){ 
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
			acorns[selection].isSelected = false;
			selection--;
			if(selection < 0){
				selection = spawnPoints.length - 1;
			}
			acorns[selection].isSelected = true;
		}
		if(keycode == RIGHT){
			acorns[selection].isSelected = false;
			selection++;
			if(selection > spawnPoints.length - 1){
				selection = 0;
			}
			acorns[selection].isSelected = true;
		}
		if(keycode == SPACE){
			if(acorns[selection].onTree){
				dropSelectedAcorn();
			}
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