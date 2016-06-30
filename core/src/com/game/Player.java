package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.GridPoint2;

public class Player {
	
	private final float gravitySpeed = 4.0f;
	private final float playerWidth = 140;
	private final float playerHeight = 35;
	
	
	private Animation animation;
	private Texture image;
	private TextureRegion[] frames;
	private Pixmap[] pixmapFrames;
	
	private ParticleEffect explosion;
	private TextureRegion explosionRegion;
	private FrameBuffer fbo;
		
	private float posX;
	private float posY;
	private int deadState;
	private float stateTime;
	
	private GameMain game;

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setToDead(GridPoint2 point) {
		if (deadState > 0)
			return;

		this.explosion.setPosition(point.x, point.y);
		if (this.explosion.isComplete()) {
			this.explosion.start();
			deadState = 1;
		}
	}
	
	
	
	public boolean isDead(){
		return (deadState > 0);
	}
	
	public boolean isGameOver(){
		return deadState == 2;
	}

	public Player(GameMain game) {
		this.game = game;
		this.deadState = 0;
		posX = 100f;
		posY = game.height / 2;
		
		//set chopper animation
		image = new Texture("chopper.png");
		Pixmap pixmapImage = new Pixmap(Gdx.files.internal("chopper.png"));
		this.pixmapFrames = new Pixmap[3];
		TextureRegion[][] tmp = TextureRegion.split(image, 140, 35);
		frames = new TextureRegion[3];
		for (int i = 0; i < 3; ++i) {
			frames[i] = tmp[i][0];
			pixmapFrames[i] = new Pixmap(140, 35, Format.RGBA8888);
			pixmapFrames[i].drawPixmap(pixmapImage, 0, 0, frames[i].getRegionX(), frames[i].getRegionY(),
					frames[i].getRegionWidth(), frames[i].getRegionHeight());
		}
		pixmapImage.dispose();
		animation = new Animation(0.2f, frames);
		animation.setPlayMode(PlayMode.LOOP);
		stateTime = 0;
		
		//set explosion particles
		fbo = new FrameBuffer(Format.RGBA8888, (int) game.width, (int) game.height, false);
		explosion = new ParticleEffect();
		explosion.load(Gdx.files.internal("explosion.p"), Gdx.files.internal(""));
		explosionRegion = new TextureRegion(fbo.getColorBufferTexture());
		explosionRegion.flip(false, true);
		
	}
	
	public void init(){
		if(deadState > 0){
			posX = 100f;
			posY = game.height / 2;
			deadState = 0;
		}
	}

	public void update() {
		stateTime += Gdx.graphics.getDeltaTime();

		if (deadState > 0){
			if(explosion.isComplete() && deadState == 1)
				deadState = 2;
			return;
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			posY += 2.0f;
		} else {
			posY -= this.gravitySpeed;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
			posX -= 2.0f;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
			posX += 2.0f;
		
		//keep player in screen
		if (posY < 0) {
			posY = 0;
		}
		if(posY > game.height - this.playerHeight)
			posY = game.height - this.playerHeight;
		if(posX < 0)
			posX = 0;
		if(posX > game.width - this.playerWidth)
			posX = game.width - this.playerWidth;
		
	}

	public Pixmap getPixmap() {
		int index = this.animation.getKeyFrameIndex(stateTime);
		return this.pixmapFrames[index];
	}

	public void draw() {
		if(deadState == 0){
			TextureRegion currentFrame = animation.getKeyFrame(stateTime);
			game.batch.draw(currentFrame, posX, posY);
		}
		if (!explosion.isComplete() && deadState == 1)
			game.batch.draw(explosionRegion, 0, 0);
	}

	public void preDrawExplosion() {
		if (explosion.isComplete())
			return;
		fbo.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.setProjectionMatrix(game.camera.combined);
		game.batch.begin();
		explosion.draw(game.batch, Gdx.graphics.getDeltaTime());
		game.batch.end();
		fbo.end();
	}

	public void dispose() {
		image.dispose();
		for (Pixmap p : pixmapFrames) {
			p.dispose();
		}
		this.fbo.dispose();
		this.explosion.dispose();
	}
}
