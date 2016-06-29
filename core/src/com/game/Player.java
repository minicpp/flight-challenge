package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player{
	Animation animation;
	Texture image;
	TextureRegion[] frames;
	Pixmap[] pixmapFrames;
	
	float posX;
	float posY;
	float width;
	float height;
	
	float gravitySpeed = 5.0f;
	
	SpriteBatch spriteBatch;
	float stateTime;
	
	public float getPosX(){
		return posX;
	}
	
	public float getPosY(){
		return posY;
	}
	
	public Player(SpriteBatch spriteBatch, int screenWidth, int screenHeight){
		this.spriteBatch = spriteBatch;
		image = new Texture("chopper.png");
		Pixmap pixmapImage = new Pixmap(Gdx.files.internal("chopper.png"));
		this.pixmapFrames = new Pixmap[3];
		TextureRegion[][] tmp = TextureRegion.split(image, 140, 35);
		frames = new TextureRegion[3];
		for(int i=0; i<3; ++i){
			frames[i] = tmp[i][0];
			pixmapFrames[i] = new Pixmap(140, 35, Format.RGBA8888);
			pixmapFrames[i].drawPixmap(pixmapImage, 0, 0, frames[i].getRegionX(), frames[i].getRegionY(),
					frames[i].getRegionWidth(), frames[i].getRegionHeight());
		}
		animation = new Animation(0.2f, frames);
		animation.setPlayMode(PlayMode.LOOP);
		stateTime = 0;
		
		posX = 100f;
		posY = screenHeight/2;
		width = 140;
		height = 35;
		pixmapImage.dispose();
	}
	
	public void update(){
		stateTime += Gdx.graphics.getDeltaTime();
		
		if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			posY += 2.0f;
		}
		else{
			posY -= this.gravitySpeed;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
			posX -= 2.0f;
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			posX += 2.0f;
		if(posY < 0){
			posY = 0;
		}
	}
	
	public Pixmap getPixmap(){
		int index = this.animation.getKeyFrameIndex(stateTime);
		return this.pixmapFrames[index];
	}
	
	public void draw(){
		TextureRegion currentFrame = animation.getKeyFrame(stateTime);
		spriteBatch.draw(currentFrame, posX, posY);
	}
	
	public void dispose () {
		image.dispose();
		for(Pixmap p:pixmapFrames){
			p.dispose();
		}
	}
}
