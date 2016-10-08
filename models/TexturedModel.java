package models;

import textures.ModelTexture;

public class TexturedModel {
	
	private RawModel rawModel;
	private ModelTexture texture;
	
	private boolean canBeCoveredByShadow = true;
	
	public TexturedModel(RawModel rawModel, ModelTexture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}
	
	public boolean getCanBeCoveredByShadow() {
		return canBeCoveredByShadow;
	}
	
	public TexturedModel setCanBeCoveredByShadow(boolean value) {
		this.canBeCoveredByShadow = value;
		return this;
	}
}
