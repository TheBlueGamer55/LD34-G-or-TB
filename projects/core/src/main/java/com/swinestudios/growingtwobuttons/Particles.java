package com.swinestudios.growingtwobuttons;

import org.mini2Dx.core.geom.Circle;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;

public class Particles{

	public float x, y;
	public float maxSpeed;
	public float radius; //How far away before particles respawn
	public Particle[] debris;
	public Circle center;
	public Color color;

	public boolean isActive;

	public Gameplay level;
	public String type;

	public Particles(float x, float y, int amount, float distance, float speed, Color color, Gameplay level){
		this.x = x;
		this.y = y;
		isActive = true;
		this.level = level;
		this.color = color;
		type = "Particles";
		radius = distance;
		center = new Circle(x, y, 1);
		//Create particles
		maxSpeed = speed;
		debris = new Particle[amount];
		for(int i = 0; i < amount; i++){
			debris[i] = new Particle(x, y, maxSpeed, color, this);
		}
	}

	public void render(Graphics g){
		if(isActive){
			drawParticles(g);
		}
	}

	public void update(float delta){
		if(isActive){
			center.setX(x);
			center.setY(y);
			updateParticles(delta);
		}
	}
	
	public void drawParticles(Graphics g){
		for(int i = 0; i < debris.length; i++){
			debris[i].render(g);
		}
	}
	
	public void updateParticles(float delta){
		for(int i = 0; i < debris.length; i++){
			debris[i].update(delta);
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

class Particle{
	public float x, y, velX, velY;
	public float maxSpeed;
	public Particles parent;
	public Color color;

	public Particle(float x, float y, float maxSpeed, Color color, Particles parent){
		this.x = x;
		this.y = y;
		this.maxSpeed = maxSpeed;
		this.parent = parent;
		this.color = color;
		setVelocity();
	}

	public void render(Graphics g){
		g.setColor(color);
		g.fillCircle(x, y, 1);
	}

	public void update(float delta){
		x += velX;
		y += velY;
		if(distanceTo(parent.center) > parent.radius){
			x = parent.center.getX();
			y = parent.center.getY();
			setVelocity();
		}
	}

	/*
	 * Sets this particle velocity towards a random direction and a random speed that is
	 * at most maxSpeed
	 */
	public void setVelocity(){
		float angle = (float) Math.toRadians(Math.random() * 360);
		float magnitude = (maxSpeed / 2) + (float)(Math.random() * maxSpeed) / 2;
		velX = (float) (magnitude * Math.cos(angle));
		velY = (float) (magnitude * Math.sin(angle));
	}

	public float distanceTo(Circle target){
		return ((float)Math.pow(Math.pow((target.getY() - this.y), 2.0) + Math.pow((target.getX() - this.x), 2.0), 0.5));
	}
}
