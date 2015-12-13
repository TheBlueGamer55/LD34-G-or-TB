package com.swinestudios.growingtwobuttons;

import java.util.Random;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class Star{

	public final float ySpeed = 1;

	private Random random;
	public float x, y, growTimer;
	private float width, height, growSpeed;
	public boolean isBig, isMoving;

	public Star(float x, float y, boolean isMoving){
		this.x = x;
		this.y = y;
		random = new Random();
		isBig = random.nextBoolean();
		growSpeed = 1;
		this.isMoving = isMoving;
		width = (isBig ? 2 : 1);
		height = (isBig ? 2 : 1);
	}

	public void render(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(x - width / 2, y - height / 2, width, height);
	}

	public void update(float delta){
		if(isMoving){
			if(isBig){
				y += ySpeed * 2;
			}
			else{
				y += ySpeed;
			}
			if(y > Gdx.graphics.getHeight()){
				y = -4;
			}
		}
		else{
			if(isBig){
				growTimer += delta;
				if(growTimer >= 0.1f){
					growTimer = 0;
					width += growSpeed;
					height += growSpeed;
				}
				if(width != 2 || height != 2){
					growSpeed *= -1;
				}
			}
		}
	}

}
