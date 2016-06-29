package com.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.GridPoint2;

public class ForegroundMap {
	private List<MapItem> mapList;
	
	private static int MAX_FILE_SIZE = 65535;
	private int screenHeight;
	private int screenWidth;
	
	private float mapScrollSpeed = 2.0f;
	private float firstMapPosX = 200.0f;
	
	private ShapeRenderer shapeRenderer;
	private List<GridPoint2> pointList;
	
	SpriteBatch spriteBatch;
	
	public ForegroundMap(SpriteBatch spriteBatch, int screenWidth, int screenHeight){
		mapList = new ArrayList<MapItem>();
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.spriteBatch = spriteBatch;
		shapeRenderer = new ShapeRenderer();
		pointList = new ArrayList<GridPoint2>();
		loadMapFile();
	}
	
	private void loadMapFile(){
		MapItem prevItem = null;
		for(int i=0; i<MAX_FILE_SIZE; ++i)
		{
			String filename = "map"+i+".png";
			MapItem item = MapItem.createMapItem(filename, screenHeight, firstMapPosX, prevItem);
			if(item == null)
				break;
			mapList.add(item);
			prevItem = item;
		}
	}
	
	public void update(){
		for(MapItem m:mapList){
			m.update(this.mapScrollSpeed);
		}
	}
	
	
	public void draw(){
		for(MapItem m:mapList){
			m.draw(this.spriteBatch, screenWidth);
		}
	}
	
	public void drawCollision(OrthographicCamera camera){
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(Color.YELLOW);
		
		if(pointList.size() > 0){
			for(GridPoint2 p2: pointList){
				shapeRenderer.rect(p2.x-2, p2.y-2, 5, 5);
			}
			
		}
			
		
		shapeRenderer.end();
	}

	
	public void dispose () {
		for(MapItem m:mapList){
			m.dispose();
		}
		shapeRenderer.dispose();
		mapList = null;
	}
	
	public GridPoint2 testCollision(Player player){
		GridPoint2 result = null;
		this.pointList.clear();
		for(MapItem m:mapList){
			result = ForegroundMap.detectCollision(m.getPixmap(), (int)m.getPosX(), 0,
					player.getPixmap(), (int)player.getPosX(), (int)player.getPosY(), this.pointList);
			if(result != null)
				break;
		}
		return result;
	}
	
	private static GridPoint2 point = new GridPoint2();
	
	public static GridPoint2 detectCollision(Pixmap object1, int posX1,int posY1,
			Pixmap object2, int posX2, int posY2, List<GridPoint2> pointList){
		//detect if the two objects override with each other
		int left1 = posX1;
		int right1 = posX1 + object1.getWidth() - 1;
		int top1 = posY1 + object1.getHeight() - 1;
		int bottom1 = posY1;
		
		int left2 = posX2;
		int right2 = posX2 + object2.getWidth() - 1;
		int top2 = posY2 + object2.getHeight() - 1;
		int bottom2 = posY2;
		
		if(left1 > right2 || right1 < left2 )
			return null;
		if(bottom1 > top2 || top1 < bottom2)
			return null;
		
		int worldPosLeft = Math.max(left1, left2);
		int worldPosRight = Math.min(right1, right2);
		int worldPosTop = Math.min(top1, top2);
		int worldPosBottom = Math.max(bottom1, bottom2);
		
		left1 = worldPosLeft - posX1;
		right1 = worldPosLeft - posX1;
		top1 = worldPosTop - posY1;
		bottom1 = worldPosBottom - posY1;
		
		left2 = worldPosLeft - posX2;
		right2 = worldPosLeft - posX2;
		top2 = worldPosTop - posY2;
		bottom2 = worldPosBottom - posY2;
		
		int rangeX = worldPosRight - worldPosLeft;
		int rangeY = worldPosTop - worldPosBottom;
		int color1 = 0, color2 = 0;
		int color = 0;
		int objY1 = 0, objY2 = 0;
		for(int y=0; y<=rangeY; ++y){
			objY1 = object1.getHeight() - 1 - bottom1-y;
			objY2 = object2.getHeight() - 1 - bottom2-y;
			for(int x=0; x<=rangeX; ++x){
				color1 = object1.getPixel(left1+x, objY1);
				color2 = object2.getPixel(left2+x, objY2);
				color1 &= 0xFF;
				color2 &= 0xFF;
				color = color1 & color2;
				if(color == 0xFF){
					//detected a collision
					int worldX = worldPosLeft + x;
					int worldY = worldPosBottom + y;
					//System.out.println("worldX="+worldX+";worldY="+worldY);
					if(pointList!=null){
						pointList.add(new GridPoint2(worldX, worldY));
					}
					else{
						point.x = worldX;
						point.y = worldY;
						return point;
					}
				}
			}
		}
		
		return null;
	}
}
