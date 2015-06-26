package enhanced.portals.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;

public class BlockStabilizerEmpty extends Block {
    IIcon dbsEmpty;

    public BlockStabilizerEmpty(String n) {
        super(Material.rock);
        setCreativeTab(EnhancedPortals.instance.creativeTab);
        setHardness(5);
        setResistance(2000);
        setBlockName(n);
        setStepSound(soundTypeStone);
    }

    @Override
    public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);

        if (meta == 0)
            return dbsEmpty;

        return super.getIcon(blockAccess, x, y, z, side);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 0)
            return dbsEmpty;

        return super.getIcon(side, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        dbsEmpty = register.registerIcon(EPMod.ID + ":dbs_empty");
    }
}
