package enhanced.portals.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import enhanced.base.utilities.Localisation;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.Locale;

public class ItemStabilizer extends ItemBlock {
    public ItemStabilizer(Block b) {
        super(b);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add(Localisation.get(EPMod.ID, Locale.BLOCK_MULTIBLOCK_STRUCTURE));
        list.add(EnumChatFormatting.DARK_GRAY + Localisation.get(EPMod.ID, Locale.BLOCK_DBS_SIZE));
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return EPBlocks.dimensionalBridgeStabilizer.getBlockTextureFromSide(0);
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }
}
