package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

public class GameMain extends ApplicationAdapter {
	
	private OrthographicCamera camera;
	public float width;
	public float height;
	public SpriteBatch batch;
	private BitmapFont font;
	private ForegroundMap foregroundMap;
	private GridPoint2 point = null;
	
	
	Background background;
	Player player;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		
		font = new BitmapFont(); 
		font.setColor(0,0,0, 1);
		
		
		//set camera
		width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        background = new Background(batch, (int)width, (int)height);
        player = new Player(batch, (int)width, (int)height);
        foregroundMap = new ForegroundMap(batch, (int)width, (int)height);
        
        System.out.println("width="+width+", height="+height + ", OpenGL version:"
        		+Gdx.graphics.getGLVersion().getMajorVersion());
		camera = new OrthographicCamera(width, height);
		camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
		camera.update();
	}
	
	public void update(){
		
		point = this.foregroundMap.testCollision(player);
		
		this.background.update();
		this.player.update();
		this.foregroundMap.update();
	}
	
	public void drawFPS(){
		int fps = Gdx.graphics.getFramesPerSecond();
		font.draw(batch, "FPS="+fps, 10, height-10);
	}

	@Override
	public void render () {
		update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		this.background.draw();
		this.player.draw();
		this.foregroundMap.draw();

		drawFPS();
		batch.end();
		
		foregroundMap.drawCollision(camera);
		
	}
	
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		player.dispose();
	}
}
