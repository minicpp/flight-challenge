package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

public class GameMain extends ApplicationAdapter {
	
	public OrthographicCamera camera;
	public float width;
	public float height;
	public SpriteBatch batch;
	
	private BitmapFont font;
	private ForegroundMap foregroundMap;
	private boolean gameStart;
	
	private Background background;
	private Player player;
	private int score;
	private int highestScore;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		
		font = new BitmapFont(); 
		font.setColor(0,0,0, 1);
		font.getRegion().getTexture().setFilter(TextureFilter.Linear,
				TextureFilter.Linear);
		
		//set camera
		width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        background = new Background(batch, (int)width, (int)height);
        player = new Player(this);
        foregroundMap = new ForegroundMap(batch, (int)width, (int)height);
        
        System.out.println("width="+width+", height="+height + ", OpenGL version:"
        		+Gdx.graphics.getGLVersion().getMajorVersion());
		camera = new OrthographicCamera(width, height);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
		gameStart = false;
	}
	
	public void update(){
		
		if (!player.isDead()) {
			GridPoint2 point = this.foregroundMap.testCollision(player);

			if (point != null)
				this.player.setToDead(point);
		}
		
		if(!gameStart){
			if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
				gameStart = true;
		}
		
		this.background.update();
		this.player.update();
		if(gameStart){
			this.foregroundMap.update();
			if(foregroundMap.isMissionAccomplished() == false && !player.isDead()){
				score = foregroundMap.getScrolledDistance();
				highestScore = score > highestScore? score: highestScore;
			}
		}
	}
	
	public void drawFPS(){
		int fps = Gdx.graphics.getFramesPerSecond();
		font.getData().setScale(1.0f);
		font.setColor(Color.BLACK);
		font.draw(batch, "FPS="+fps, 10, height-10);
	}
	
	public void drawScore(){
		font.getData().setScale(1.5f);
		font.setColor(Color.FOREST);
		font.draw(batch, "Score: "+this.score, 20, 20);
	}
	


	@Override
	public void render () {
		update();
		this.player.preDrawExplosion();
		Gdx.gl.glClearColor(0, 0, 0, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		this.background.draw();
		this.player.draw();
		this.foregroundMap.draw();
		
		if(player.isGameOver()){
			font.setColor(Color.BLACK);
			font.getData().setScale(5.0f);
			font.draw(batch, "Game Over", this.width/4, this.height/1.5f );
			font.getData().setScale(2.0f);
			font.draw(batch, "Scores: "+this.score+"  Highest Scores:" + this.highestScore, this.width/6f, this.height/2.0f );
			font.draw(batch, "Press 'R' Key to Restart", this.width/3.5f, this.height/4.0f );
		}
		
		if(!gameStart){
			font.getData().setScale(4.0f);
			font.setColor(Color.RED);
			font.draw(batch, "Are you ready?", this.width/4, this.height/1.5f );
			font.setColor(Color.BLACK);
			font.getData().setScale(2.0f);
			font.draw(batch, "Press 'Space' Key to start", this.width/3.5f, this.height/2.0f );
			font.draw(batch, "Press Arrow Keys to control the player", this.width/4.5f, this.height/3.0f );
		}
		
		if(gameStart && !player.isGameOver() && foregroundMap.isMissionAccomplished()){
			font.getData().setScale(5.0f);
			font.setColor(Color.YELLOW);
			font.draw(batch, "Mission Accomplished", this.width/12, this.height/1.5f );
			font.getData().setScale(2.0f);
			font.draw(batch, "Scores: "+this.score+"  Highest Scores:" + this.highestScore, this.width/6f, this.height/2.0f );
			font.draw(batch, "Press 'R' Key to Restart", this.width/3.5f, this.height/4.0f );
		}
		
		drawFPS();
		drawScore();
		batch.end();
		
		if(!player.isDead())
			foregroundMap.drawCollision(camera);
	}
	
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		player.dispose();
		foregroundMap.dispose();
	}
}
