package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;

public class MapItem {
	private Pixmap map;
	private Texture texture;
	
	private int screenHeight;
	private float posX;
	
	public boolean isMapEnd(int width){
		
		if(posX + width < 0 )
			return true;
		return false;
	}
	
	public Pixmap getPixmap(){ return map;}
	
	public float getPosX(){return posX;}
	
	public void draw(SpriteBatch batch, int screenWidth){
		if(this.posX + this.map.getWidth() < 0 || this.posX > this.map.getWidth())
			return;
		batch.draw(this.texture, posX, 0);
	}
	
	public MapItem(int screenHeight){
		this.screenHeight = screenHeight;
	}
	
	public void update(float speed){
		posX -= speed;
	}
	
	public static MapItem createMapItem(String filename, int screenHeight, float offset, MapItem prevMapItem) {
		
		FileHandle file = Gdx.files.external(filename);
		if (file == null || file.isDirectory() || !file.exists()) {
			// try to load from internal folder
			file = Gdx.files.internal(filename);
			if (file == null || file.isDirectory() || !file.exists()) {
				System.out.println("Cannot open the file " + filename + ". I assume it is the end of the map file.");
				return null;
			}
		}

		// load the map file into the memory of the computer
		MapItem item = new MapItem(screenHeight);
		item.createPixmap(file);
		if(prevMapItem == null){
			item.posX = offset;
		}
		else{
			item.posX = prevMapItem.posX + prevMapItem.map.getWidth();
		}
		return item;
	}

	private void createPixmap(FileHandle file) {

		Pixmap.setFilter(Filter.NearestNeighbour);
		Pixmap.setBlending(Blending.None);

		Pixmap pixmapSrc = new Pixmap(file);

		double dstWidth = (double) screenHeight / (double) pixmapSrc.getHeight() * (double) pixmapSrc.getWidth();
		map = new Pixmap((int) dstWidth, screenHeight, Format.RGBA8888);
		map.drawPixmap(pixmapSrc, 0, 0, pixmapSrc.getWidth(), pixmapSrc.getHeight(),
				0, 0, (int) dstWidth, screenHeight);
		map.setColor(1.0f, 1.0f, 1.0f, 0.0f);
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				int color = map.getPixel(x, y);
				if (color == 0xFFFFFFFF)
					map.drawPixel(x, y);
			}
		}
		this.texture = new Texture(map);
		pixmapSrc.dispose();
	}
	
	public void dispose(){
		this.map.dispose();
		this.texture.dispose();
	}

}
