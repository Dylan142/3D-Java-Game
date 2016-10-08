package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TexturedModel;
import normalMappingRenderer.NormalMappingRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import particles.Particle;
import shaders.StaticShader;
import shaders.TerrainShader;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;
import terrains.Terrain;
import engineTester.MainGameLoop;
import entities.Camera;
import entities.Entity;
import entities.LightComponent;
import entities.RenderComponent;

public class MasterRenderer {
	
	public static final float FOV = 70;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;
	
	public static final float RED = 0.50f;
	public static final float GREEN = 0.56f;
	public static final float BLUE = 0.66f;
	
	private static final float NIGHT_RED = 0f;
	private static final float NIGHT_GREEN = 0;
	private static final float NIGHT_BLUE = 0;
	
	private static float red = RED;
	private static float green = GREEN;
	private static float blue = BLUE;
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
	
	private NormalMappingRenderer normalMapRenderer;
	
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	//private ParticleRenderer particleRenderer;
	//private ParticleShader particleShader = new ParticleShader();
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<Particle> particles = new ArrayList<Particle>();
	
	public MasterRenderer(Loader loader, Camera camera) {
		enableCulling();
		enableMultisampling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		//particleRenderer = new ParticleRenderer(particleShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		this.shadowMapRenderer = new ShadowMapMasterRenderer(camera);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public static void enableMultisampling() {
		GL11.glEnable(GL13.GL_MULTISAMPLE);
	}
	
	public static void disableMultisampling() {
		GL11.glDisable(GL13.GL_MULTISAMPLE);
	}
	
	public void renderScene(List<Entity> entities, List<Entity> normalEntities, Terrain[][] terrains, List<LightComponent> lights, Camera camera,
			Vector4f clipPlane, ViewFrustum frustum) {
		for(Entity e : entities) {
			processEntity(e);
		}
		
		for(Entity e : normalEntities) {
			processNormalMapEntity(e);
		}
		
		for(int x = 0; x < MainGameLoop.WIDTH; x++) {
			for(int z = 0; z < MainGameLoop.WIDTH; z++) {
				processTerrain(terrains[x][z]);
			}
		}
//				if(!frustum.cubeInFrustum(terrain.getX(), 0, terrain.getZ(), Terrain.SIZE)) {
//					processTerrain(terrain);
//				}				
		
		
		
		
		
//		float yaw = camera.getYaw() % 360;
//		for(Entity entity : entities) {
//			//processEntity(entity);
//			float dx = entity.getPosition().x - player.getPosition().x;
//			float dz = entity.getPosition().z - player.getPosition().z;
//			float dy = entity.getPosition().y - player.getPosition().y;
//			if(yaw < 0) {
//				if(yaw >= -45 || yaw <= -315) {
//					if(dx >= -800 & dx <= 800 && dz >= -1000 && dz < 100 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > -315 && yaw <= -225) {
//					if(dx >= -100 & dx <= 1000 && dz >= -800 && dz < 800 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > -225 && yaw <= -135) {
//					if(dx >= -800 & dx <= 800 && dz >= -100 && dz < 1000 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > -135 && yaw <= -45) {
//					if(dx >= -1000 & dx <= 100 && dz >= -800 && dz < 800 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}
//			}else {
//				if(yaw >= 315 || yaw <= 45) {
//					if(dx >= -800 & dx <= 800 && dz >= -1000 && dz < 100 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > 45 && yaw <= 135) {
//					if(dx >= -100 & dx <= 1000 && dz >= -800 && dz < 800 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > 135 && yaw <= 225) {
//					if(dx >= -800 & dx <= 800 && dz >= -100 && dz < 1000 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}else if(yaw > 225 && yaw < 315){
//					if(dx >= -1000 & dx <= 100 && dz >= -800 && dz < 800 && dy >= - 300 && dy <= 300) {
//						processEntity(entity);
//						continue;
//					}
//				}					
//			}
//		}
		
		render(lights, camera, clipPlane);
	}
	
	public void render(List<LightComponent> lights, Camera camera, Vector4f clipPlane) {
		prepare();
		//colorFog();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(red, green, blue);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
		entities.clear();
		shader.stop();
		normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
		normalMapEntities.clear();
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadSkyColor(red, green, blue);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		terrains.clear();
		skyboxRenderer.render(camera, red, green, blue);
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void processParticle(Particle particle){
		particles.add(particle);
	}
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}
	
	private void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public void processEntity(Entity entity) {
		RenderComponent render = (RenderComponent) entity.getComponent(RenderComponent.class);
		TexturedModel entityModel = render.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processNormalMapEntity(Entity entity) {
		RenderComponent render = (RenderComponent) entity.getComponent(RenderComponent.class);
		TexturedModel entityModel = render.getModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		}else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}
	
	public void renderShadowMap(List<Entity> entityList, List<Entity> normalEntities, LightComponent sun) {
		for(Entity entity : entityList) {
			processEntity(entity);
		}
		for(Entity entity : normalEntities) {
			processNormalMapEntity(entity);
		}
		shadowMapRenderer.render(entities, normalMapEntities, sun);
		entities.clear();
		normalMapEntities.clear();
	}
	
	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}
	
	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		normalMapRenderer.cleanUp();
		//particleShader.cleanUp();
		shadowMapRenderer.cleanUp();
	}
}
