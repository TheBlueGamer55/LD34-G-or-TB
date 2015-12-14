package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class TreeTrunk implements InputProcessor{ 

	public float x, y, initialY;
	public float velX, velY;
	public final float maxVelY = 8.5f; //Max falling speed
	public final float fallingAccelY = 0.05f; //The falling acceleration
	public float rotateSpeed = 20; //Rotating speed used with delta time

	public float netOffset;
	public final float hoverSpeed = 0.2f; //Speed of hovering effect
	public final float hoverRange = 12; //Range of hovering effect

	public boolean isActive;
	public boolean isFalling;

	//Spawn coordinates
	public final float[][] spawnPoints = {{60, 24}, {160, 62}, {250, 40}, {400, 84}, {490, 28}, {587, 51}};

	public TreeProjectile[] acorns;

	public int selection; //Determines which projectile is currently selected
	public Particles selector;
	//Constants for arguments to the selector TODO adjust later
	public final Color selectorColor = Color.WHITE;
	public final int selectorAmount = 8;
	public final float selectorRadius = 24.0f;
	public final float selectorMaxSpeed = 2.0f;

	public Sprite treeTrunk, treeTop;

	public final int maxHealth = 3; //TODO adjust later
	public int health;

	public Rectangle hitbox;
	public Gameplay level;
	public String type;

	public static Sound treeFall = Gdx.audio.newSound(Gdx.files.internal("treeDead.wav"));
	public static Sound dropAcorn = Gdx.audio.newSound(Gdx.files.internal("dropSound.wav"));
	public static Sound explosionHit = Gdx.audio.newSound(Gdx.files.internal("HighDamageHit.wav"));
	public static Sound shatterHit = Gdx.audio.newSound(Gdx.files.internal("Smash.wav"));

	//Controls/key bindings
	public final int LEFT = Keys.LEFT;
	public final int RIGHT = Keys.RIGHT;
	public final int SPACE = Keys.SPACE;

	public TreeTrunk(float x, float y, Gameplay level){
		this.x = x;
		this.y = y;
		initialY = y;
		velX = 0;
		velY = hoverSpeed;
		netOffset = 0;
		selection = 0;
		health = maxHealth;
		isActive = true;
		isFalling = false;
		this.level = level;
		type = "TreeTrunk";

		acorns = new TreeProjectile[spawnPoints.length];
		spawnAcorns();
		selector = new Particles(acorns[selection].hitbox.getCenterX(), acorns[selection].hitbox.getCenterY(), 
				selectorAmount, selectorRadius, selectorMaxSpeed, selectorColor, level);
		acorns[selection].isSelected = true;

		treeTrunk = new Sprite(new Texture(Gdx.files.internal("tree_trunk.png")));
		treeTop = new Sprite(new Texture(Gdx.files.internal("tree_top.png")));
		treeTrunk.setSize(treeTrunk.getWidth() * 2, treeTrunk.getHeight() * 2);
		treeTop.setSize(treeTop.getWidth() * 2, treeTop.getHeight() * 2);

		hitbox = new Rectangle(x, y, 40, 380); //adjust size later based on sprite
	}

	public void render(Graphics g){
		if(isActive){
			g.drawSprite(treeTrunk, x, y); //300, 100
			g.drawSprite(treeTop, 0, y - initialY);

			//Debug - remove later
			g.drawString("" + (int)Math.floor(level.score), x, y);
			g.drawString("" + selection, x, y + 20);
		}
	}

	public void update(float delta){
		if(!isFalling && isActive){
			netOffset += velY;
			moveAcorns();
			y += velY;
			if(y > initialY + hoverRange){
				velY *= -1;
			}
			if(y <= initialY){
				velY *= -1;
			}

			hitbox.setX(this.x);
			hitbox.setY(this.y);

			checkProjectileCollision();
		}
		else if(isFalling){ //Tree died and is falling
			dropAllAcorns();
			velY += fallingAccelY;
			y += velY;
			//Rotate tree as it falls
			treeTrunk.rotate(delta * rotateSpeed);

			if(y - initialY > Gdx.graphics.getHeight()){ //End of falling "animation"
				isFalling = false;
				isActive = false;
				level.gameOver = true;
			}
		}
	}

	/*
	 * Deal damage to trunk and handle game over if out of health
	 */
	public void dealDamage(Projectile p){
		if(!level.isShaking){
			level.isShaking = true;
		}
		health--;
		if(health <= 0){ 
			isFalling = true;
			treeFall.play();
			//Rotational origin and direction depends on where the tree was hit
			float centerX = treeTrunk.getWidth() / 2;
			float centerY = 2*y + treeTrunk.getHeight() - p.y; //Formula for opposite y coordinate within range of height
			//treeTrunk.setOrigin(treeTrunk.getWidth() / 2, p.y); 
			if(p.x > this.x){ //If hit from the right
				if(p.y > this.y + treeTrunk.getHeight() / 2){ //If hit from the bottom half
					//Do nothing, the initial values are fine
				}
				else{ //If hit form the top half
					rotateSpeed *= -1;
				}
			}
			else{ //If hit from the left
				if(p.y > this.y + treeTrunk.getHeight() / 2){ //If hit from the bottom half
					rotateSpeed *= -1;
				}
				else{ //If hit from the top half
					//Do nothing, the initial values are fine
				}
			}
			treeTrunk.setOrigin(centerX, centerY);
		}
		dropAllAcorns();
	}
	
	/*
	 * Plays the appropriate sound based on the projectile that hit
	 * the tree.
	 */
	public void playCorrectNoise(int id){
		//TODO add sounds for each unique projectile type
		switch(id){
			case 0: //Hit by white dwarf/planet
				break;
			case 1: //Hit by asteroid
			case 2:
				shatterHit.play();
				break;
			case 3: //Hit by planet
				explosionHit.play();
			default:
				break;
		}
	}

	/*
	 * Move acorns together with tree
	 */
	public void moveAcorns(){
		for(int i = 0; i < acorns.length; i++){
			if(acorns[i].onTree){
				acorns[i].y = acorns[i].initialY + netOffset;
			}
		}
	}

	/*
	 * Drops all acorns currently on the tree
	 */
	public void dropAllAcorns(){
		for(int i = 0; i < level.treeProjectiles.size(); i++){
			if(level.treeProjectiles.get(i).isActive && level.treeProjectiles.get(i).onTree){
				level.treeProjectiles.get(i).drop();
			}
		}
	}

	/*
	 * Precondition: The currently selected acorn is already growing, 0 <= selection < spawnPoints.length
	 */
	public void dropSelectedAcorn(){
		acorns[selection].drop();
		dropAcorn.play();
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
					//level.projectiles.remove(temp);
					temp.crash();
					dealDamage(temp);
					playCorrectNoise(temp.ID);
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