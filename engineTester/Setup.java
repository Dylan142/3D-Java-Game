package engineTester;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.Loader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

public class Setup {
	
	private static Loader loader;
	
	public static void init(Loader loader) {
		Setup.loader = loader;
	}
	
	public static TexturedModel createTexturedModel(String obj, String png) {
		ModelData data = OBJFileLoader.loadOBJ(obj);
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture texture = new ModelTexture(loader.loadTexture(png));
		TexturedModel texturedModel = new TexturedModel(model, texture);
		return texturedModel;
	}
	
	public static TexturedModel createTexturedModel(String obj, String png, boolean useFakeLighting) {
		ModelData data = OBJFileLoader.loadOBJ(obj);
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture texture = new ModelTexture(loader.loadTexture(png));
		texture.setUseFakeLighting(useFakeLighting);
		TexturedModel texturedModel = new TexturedModel(model, texture);
		return texturedModel;
	}
	
	public static TexturedModel createTexturedModel(String obj, String png, int textureIndex, int rows, boolean useFakeLighting) {
		ModelData data = OBJFileLoader.loadOBJ(obj);
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture textureAtlas = new ModelTexture(loader.loadTexture(png));
		textureAtlas.setNumberOfRows(rows);
		textureAtlas.setUseFakeLighting(useFakeLighting);
		TexturedModel texturedModel = new TexturedModel(model, textureAtlas);
		return texturedModel;
	}
	
	public static RawModel createRawModel(String obj) {
		ModelData data = OBJFileLoader.loadOBJ(obj);
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		return model;
	}
	
	public static Terrain[][] createTerrains(TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap,
			int size, int heightRange, int heightMinimum) {
		Terrain[][] terrains = new Terrain[size][size];
		for(int z = 0; z < size; z++) {
			for(int x = 0; x < size; x++) {
				terrains[x][z] = new Terrain(x, -z, loader, texturePack, blendMap, heightMap, Maths.getRandomInt(heightRange, heightMinimum));
			}
		}
		return terrains;
	}
}
