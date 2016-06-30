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
	
	public boolean isMapEnd(){
		
		if(posX + map.getWidth() < 0 )
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
	
	public void init(int screenHeight, float offset, MapItem prevMapItem){
		if(prevMapItem == null){
			this.posX = offset;
		}
		else{
			this.posX = prevMapItem.posX + prevMapItem.map.getWidth();
		}
	}
	
	public static MapItem createMapItem(String filename, int screenHeight, float offset, MapItem prevMapItem) {
		
		FileHandle file = Gdx.files.external(filename);
		if (file == null || file.isDirectory() || !file.exists()) {
			// try to load from internal folder
			file = Gdx.files.internal(filename);
			if (file == null || file.isDirectory() || !file.exists()) {
				System.out.println("The system assumes the file "+filename+" is the end of the map file.");
				return null;
			}
		}

		// load the map file into the memory of the computer
		MapItem item = new MapItem(screenHeight);
		item.createPixmap(file);
		item.init(screenHeight, offset, prevMapItem);
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
		int r,g,b;
		for (int y = 0; y < map.getHeight(); ++y) {
			for (int x = 0; x < map.getWidth(); ++x) {
				int color = map.getPixel(x, y);
				if (color == 0xFFFFFFFF)
					map.drawPixel(x, y);
				else
				{	//detect if the color is very near to the white color.
					r = (color)>>>24;
					g = (color&0x00FF0000)>>>16;
					b = (color&0x0000FF00)>>>8;
					if(r * g * b >= Math.pow(235, 3)){ //0xC5C10000 = 240^4
						map.drawPixel(x, y);
					}
				}
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
