package com.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background{
	Texture bgImage;
	float x;
	float speed;
	int width;
	int height;
	
	SpriteBatch spriteBatch;
	
	public Background(SpriteBatch spriteBatch, int width, int height){
		this.spriteBatch = spriteBatch;
		this.width = width;
		this.height = height;
		
		System.out.println("Running the constructor to create an instance of the Background class");
		bgImage = new Texture("cloud_background.png");
		bgImage.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		speed = 0.5f;
	}
	
	public void update(){
		x += speed;
		while(x > (float)bgImage.getWidth()){
			x -= (float)bgImage.getWidth();
		}
	}
	
	public void draw(){
		spriteBatch.draw(bgImage, 0, 0, (int)x, 0, width, height);
	}
	
	public void dispose () {
		bgImage.dispose();
	}
}
