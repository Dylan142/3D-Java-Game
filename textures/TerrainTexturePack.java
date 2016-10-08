package textures;

public class TerrainTexturePack {
	
	private TerrainTexture backgroundTexture;
	private TerrainTexture texture;
	
	public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture texture) {
		this.backgroundTexture = backgroundTexture;
		this.texture = texture;
	}

	public TerrainTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public TerrainTexture getTexture() {
		return texture;
	}
	
}
