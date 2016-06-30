package com.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

public class Background{
	private Texture bgImage;
	private float x;
	private final float speed = 0.5f;
	private GameMain game;
	
	public Background(GameMain game){
		this.game = game;
		bgImage = new Texture("cloud_background.png");
		bgImage.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
	}
	
	public void update(){
		x += speed;
		while(x > (float)bgImage.getWidth()){
			x -= (float)bgImage.getWidth();
		}
	}
	
	public void draw(){
		this.game.batch.draw(bgImage, 0, 0, (int)x, 0, (int)game.width, (int)game.height);
	}
	
	public void dispose () {
		bgImage.dispose();
	}
}
