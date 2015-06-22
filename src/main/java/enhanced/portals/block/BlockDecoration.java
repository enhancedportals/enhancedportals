package enhanced.portals.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import enhanced.portals.EnhancedPortals;

public class BlockDecoration extends Block {
    protected BlockDecoration(String n) {
        super(Material.rock);
        setBlockName(n);
        setHardness(3);
        setStepSound(soundTypeStone);
        setCreativeTab(EnhancedPortals.instance.creativeTab);
    }
}
