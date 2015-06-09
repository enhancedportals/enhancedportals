package enhancedportals.base;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import enhancedportals.EnhancedPortals;
import enhancedportals.network.ProxyCommon;

public abstract class BlockContainerEP extends BlockContainer {

	protected BlockContainerEP(String name, Material material) {
		super(material);
		setBlockTextureName(EnhancedPortals.MOD_ID + ":" + name);
		setBlockName(name);
		setCreativeTab(ProxyCommon.creativeTab);
	}
}
