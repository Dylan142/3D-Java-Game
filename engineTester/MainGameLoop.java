package engineTester;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import models.TexturedModel;
import normalMappingObjConverter.NormalMappedObjLoader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.Fbo;
import postProcessing.PostProcessing;
import quests.CommandTaskComponent;
import quests.DestinationTaskComponent;
import quests.ExperienceRewardComponent;
import quests.MultipleCommandsTaskComponent;
import quests.Quest;
import quests.QuestSystem;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ViewFrustum;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.BoundingBox;
import toolbox.Maths;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import audio.AudioMaster;
import entities.Camera;
import entities.CollisionComponent;
import entities.CollisionSystem;
import entities.Entity;
import entities.EntitySystem;
import entities.JumpComponent;
import entities.LightComponent;
import entities.MovementComponent;
import entities.MovementSystem;
import entities.Player;
import entities.RenderComponent;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiMaster;

public class MainGameLoop {
	
	/* TODO:
	 * FIX TWO COLLISION BUG
	 * FIGURE OUT WHY PARTICLES ARE WEIRD LOOKING
	 * INVENTORY AND ITEMS
	 * IMPLEMENT DIOLOGUE (CONVERSATIONS)
	 */
	
	private static Random random = new Random();
	private static List<Entity> entities = new ArrayList<Entity>();
	private static List<Entity> normalEntities = new ArrayList<Entity>();
	//private static List<Particle> particles = new ArrayList<Particle>();
	//private static final float BORDER_SIZE = 28;
	public static final int WIDTH = 3;
	private static Terrain[][] terrains;
	
	private static boolean paused = false;
	
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();
		TextMaster.init(loader);
		AudioMaster.init();
		GuiMaster.init(loader);
		
		Setup.init(loader);
		
		FontType font = new FontType(loader.loadFontTexture("verdana"), "verdana");
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grass"));
		TerrainTexture texture = new TerrainTexture(loader.loadTexture("stonePath"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, texture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("simpleBlendMap"));
		
		terrains = new Terrain[WIDTH][WIDTH];
		for(int x = 0; x < WIDTH; x++) {
			for(int z = 0; z < WIDTH; z++) {
				float maxHeight = 40 + (random.nextFloat() * 80);
				terrains[x][z] = new Terrain(x, z, loader, texturePack, blendMap, "heightmap_64", maxHeight);
			}
		}
		
//		terrains[0][0] = new Terrain(0, 0, loader, texturePack, blendMap, "heightmap_64", 60);
//		terrains[1][0] = new Terrain(1, 0, loader, texturePack, blendMap, "heightmap_64", 60);
//		terrains[0][1] = new Terrain(0, 1, loader, texturePack, blendMap, "heightmap_64", 60);
//		terrains[1][1] = new Terrain(1, 1, loader, texturePack, blendMap, "heightmap_64", 60);
		
		float center = Terrain.SIZE * WIDTH / 2;
		Player player = new Player();
		RenderComponent playerRender = new RenderComponent(Setup.createTexturedModel("player", "playerTexture"), 0,
				new Vector3f(center, getTerrainHeight(center, center), center), 2, 6, 2, 0, 0, 0, 1.3f);
		MovementComponent playerMove = new MovementComponent(30, 60, 60);
		BoundingBox playerBox = new BoundingBox(playerRender.getPosition(), new Vector3f(10, 16, 10));
		player.addComponent(playerRender);
		player.addComponent(playerMove);
		player.addComponent(new CollisionComponent(playerBox));
		player.addComponent(new JumpComponent(100));
		addEntity(player);
		Camera camera = new Camera(playerRender, playerMove);
		AudioMaster.setListenerData(playerRender.getPosition().x, playerRender.getPosition().y, playerRender.getPosition().z);
		
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		
		List<LightComponent> lights = new ArrayList<LightComponent>();
		LightComponent sun = new LightComponent(new Vector3f(1000000, 1500000, -1000000), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);
		
		Comparator<LightComponent> lightSorter = new Comparator<LightComponent>() {
			public int compare(LightComponent l0, LightComponent l1) {
				Vector3f l1distance = new Vector3f();
				Vector3f l0distance = new Vector3f();
				Vector3f.sub(l1.getPosition(), playerRender.getPosition(), l1distance);
				Vector3f.sub(l0.getPosition(), playerRender.getPosition(), l0distance);
				if(l1distance.length() > l0distance.length()) return -1;
				if(l1distance.length() < l0distance.length()) return +1;
				return 0;
			}
			
		};
		
		
		TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("obj/barrel", loader),
				new ModelTexture(loader.loadTexture("barrel")));
		barrelModel.getTexture().setShineDamper(10);
		barrelModel.getTexture().setReflectivity(0.5f);
		barrelModel.getTexture().setNormalMap(loader.loadTexture("barrelNormal"));
		
		for(int i = 0; i < 100; i++) {
			float x = center;
			float y = center;
			float z = center;
			if(i % 3 == 0) {
				x = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				z = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				y = getTerrainHeight(x, z);
				Entity wolf = new Entity();
				wolf.addComponent(new RenderComponent(Setup.createTexturedModel("wolf", "wolfTexture"), 0, new Vector3f(x, y, z),
						1, 2, 1.5f, 0, Maths.getRandomInt(360, 0), 0, 2.7f));
				wolf.addComponent(new MovementComponent(6, 6, 20));
				addEntity(wolf);
				Entity fern = new Entity();
				fern.addComponent(new RenderComponent(Setup.createTexturedModel("fern", "fern", Maths.getRandomInt(4, 0), 2, true),
						Maths.getRandomInt(4, 0), new Vector3f(x, y, z), 1, 1, 1, 0, 0, 0, 1));
				addEntity(fern);
			}
			if(i % 10 == 0) {
				x = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				z = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				y = getTerrainHeight(x, z);
				Entity lamp = new Entity();
				RenderComponent render = new RenderComponent(Setup.createTexturedModel("lamp", "lampTexture", true).setCanBeCoveredByShadow(false), 0, new Vector3f(x, y, z),
						2, 8, 2, 0, Maths.getRandomInt(360, 0), 0, 2.4f);
				lamp.addComponent(render);
				LightComponent light = new LightComponent(new Vector3f(render.getPosition().x, render.getPosition().y + 30, render.getPosition().z),
						new Vector3f(Maths.getRandomFloat(3.5f, 2.5f), Maths.getRandomFloat(3.5f, 2.5f), Maths.getRandomFloat(3.5f, 2.5f)),
						new Vector3f(1, 0.01f, 0.002f));
				lights.add(light);
				lamp.addComponent(light);
				
				addEntity(lamp);
			}
			if(i % 20 == 0) {
				x = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				z = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
				y = getTerrainHeight(x, z) + 1;
				Entity barrel = new Entity();
				barrel.addComponent(new RenderComponent(barrelModel, 0, new Vector3f(x, y, z), 5, 6, 5, 0, 0, 0, 1));
				addNormalEntity(barrel);
			}
			x = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
			z = center + Maths.getRandomFloat(2 * Terrain.SIZE, 0) - Terrain.SIZE;
			y = getTerrainHeight(x, z);
			if(i % 2 == 0) {
				Entity entity = new Entity();
				entity.addComponent(new RenderComponent(Setup.createTexturedModel("tree", "tree"), 0,
						new Vector3f(x, y, z), 4, 10, 4, 0, Maths.getRandomFloat(360, 0), 0, Maths.getRandomFloat(13, 8)));
				addEntity(entity);
			}else {
				Entity entity = new Entity();
				entity.addComponent(new RenderComponent(Setup.createTexturedModel("lowPolyTree", "lowPolyTree"), 0, new Vector3f(x, y, z),
						3, 10, 3, 0, Maths.getRandomFloat(360, 0), 0, 1));
				addEntity(entity);
			}
		}
		
		//guis.add(new GuiTexture(loader.loadTexture("heart"), new Vector2f(-0.9f, 0.9f), new Vector2f(0.04f, 0.05f), 0, 0, 0));
		//guis.add(new GuiTexture(loader.loadTexture("heart"), new Vector2f(-0.9f, 0.9f), new Vector2f(0.04f, 0.05f), 0, 180f, 0));
		
		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);
		
		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(center, center, 2);
		waters.add(water);
		
//		GuiTexture refraction = new GuiTexture(buffers.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		GuiTexture reflection = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//		guis.add(reflection);
//		guis.add(refraction);
		
		//GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(shadowMap);
		
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particle textures/cosmic"), 2, false);
		
		ParticleSystem system = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 6);
		system.randomizeRotation();
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setScaleError(0.4f);
		system.setSpeedError(0.8f);
		
		//View Frustum for culling
		ViewFrustum frustum = new ViewFrustum();
		frustum.calculateFrustum();
		
		// FPS Counter variables
		long lastFPS = DisplayManager.getTime();
		int fps = 0;
		
		GUIText counter = new GUIText(fps + " fps", 1, font, new Vector2f(0, 0), 0.5f, false);
		GUIText position = new GUIText(playerRender.getPosition() + "", 1, font, new Vector2f(0, 0.04f), 0.5f, false);
		
		//TEMPORARY QUESTS
		Vector3f destination = new Vector3f(center - 20, getTerrainHeight(center - 20, center + 50), center + 50);
		Entity tallTower = new Entity();
		tallTower.addComponent(new RenderComponent(Setup.createTexturedModel("tall_tower", "tall_tower_texture"), 0, destination,
				4, 20, 4, 0, 0, 0, 5));
		CollisionComponent tallTowerCollision = new CollisionComponent(new Vector3f(destination.x - 10, destination.y, destination.z - 10),
				new Vector3f(20, 100, 20));
		tallTower.addComponent(tallTowerCollision);
		addEntity(tallTower);
		QuestSystem questSystem = new QuestSystem(font);
		Quest quest = new Quest("Learning the basics");
		quest.addRewardComponent(new ExperienceRewardComponent(10, false));
		List<Integer> commands= new ArrayList<Integer>();
		commands.add(Keyboard.KEY_W);
		commands.add(Keyboard.KEY_A);
		commands.add(Keyboard.KEY_S);
		commands.add(Keyboard.KEY_D);
		quest.addTaskComponent(new MultipleCommandsTaskComponent(commands, "Try walking around with W, A, S, D"));
		quest.addTaskComponent(new CommandTaskComponent(Keyboard.KEY_LSHIFT, "Try sprinting with shift"));
		quest.addTaskComponent(new CommandTaskComponent(Keyboard.KEY_ESCAPE, "Try pausing the game with escape"));
		questSystem.addQuest(quest);
		Quest quest2 = new Quest("A new home");
		quest2.addTaskComponent(new DestinationTaskComponent(playerRender, destination, new Vector3f(20, 20, 20), "Go to the grey tall tower"));
		questSystem.addQuest(quest2);
		//GuiMaster.createGui("background", new Vector2f(-0.75f, 0.3f), new Vector2f(0.3f, 0.3f), 0, 0, 0);
		
		//SYSTEMS
		EntitySystem entitySystem = new EntitySystem();
		CollisionSystem collision = new CollisionSystem(entities);
		MovementSystem move = new MovementSystem(entities, terrains, player, collision);
		entitySystem.init(move);
		
		Fbo fbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_RENDER_BUFFER);
		PostProcessing.init(loader);
		
		while(!Display.isCloseRequested()) {
			camera.move();
			position.setTextString(playerRender.getPosition() + "");
			entitySystem.update();
			questSystem.update();
			frustum.calculateFrustum();
			AudioMaster.setListenerData(playerRender.getPosition().x, playerRender.getPosition().y, playerRender.getPosition().z);
			system.generateParticles(playerRender.getPosition());
			ParticleMaster.update(camera);
			
			
			renderer.renderShadowMap(entities, normalEntities, sun);
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			Collections.sort(lights, lightSorter);
			lights.add(0, sun);
			
			
			//Water render
			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f), frustum);
			camera.getPosition().y += distance;
			camera.invertPitch();
			
			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight() + 0.2f), frustum);
			
			
			//Render to screen
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			buffers.unbindCurrentFrameBuffer();
			
//			fbo.bindFrameBuffer();
			renderer.renderScene(entities, normalEntities, terrains, lights, camera, new Vector4f(0, 0, 0, 0), frustum);
			lights.remove(0);
			waterRenderer.render(waters, camera, sun);
			ParticleMaster.renderParticles(camera);
//			fbo.unbindFrameBuffer();
//			PostProcessing.doPostProcessing(fbo.getColorTexture());
			
			GuiMaster.render();
			TextMaster.render();
			
			if(DisplayManager.getTime() - lastFPS > 1000) {
				lastFPS += 1000;
				counter.setTextString(fps + " fps");
				fps = 0;
			}
			fps++;
			DisplayManager.updateDisplay();
		}
		
		PostProcessing.cleanUp();
		fbo.cleanUp();
		questSystem.cleanUp();
		ParticleMaster.cleanUp();
		buffers.cleanUp();
		GuiMaster.cleanUp();
		renderer.cleanUp();
		waterShader.cleanUp();
		loader.cleanUp();
		TextMaster.cleanUp();
		AudioMaster.cleanUp();
		DisplayManager.closeDisplay();
	}
	
	public static boolean isPaused() {
		return paused;
	}
	
	public static void setPaused(boolean paused) {
		MainGameLoop.paused = paused;
	}
	
	
	
	public static float getTerrainHeight(float worldX, float worldZ) {
		int x = (int) (worldX / Terrain.SIZE);
		int z = (int) (worldZ / Terrain.SIZE);
		if(x < WIDTH && x >= 0 && z < WIDTH && z >= 0) return terrains[x][z].getHeightOfTerrain(worldX, worldZ);
		return 0;
	}
	
	public static int getTerrainX(float worldX) {
		return (int) (worldX / Terrain.SIZE);
	}
	
	public static int getTerrainZ(float worldZ) {
		return (int) (worldZ / Terrain.SIZE);
	}
	
	private static Entity addEntity(Entity entity) {
		entities.add(entity);
		return entity;
	}
	
	private static Entity addNormalEntity(Entity entity) {
		normalEntities.add(entity);
		return entity;
	}
}
