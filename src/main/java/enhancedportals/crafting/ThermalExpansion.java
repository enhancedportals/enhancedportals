package enhancedportals.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import enhancedportals.EnhancedPortals;
import enhancedportals.block.BlockFrame;
import enhancedportals.block.BlockStabilizer;
import enhancedportals.block.BlockStabilizerEmpty;
import enhancedportals.item.ItemBlankUpgrade;
import enhancedportals.item.ItemDiamondNugget;
import enhancedportals.item.ItemUpgrade;

public class ThermalExpansion {
    static ItemStack getThermalExpansion(String ID) {
        return GameRegistry.findItemStack(EnhancedPortals.MODID_THERMALEXPANSION, ID, 1);
    }
    
    static ItemStack getThermalExpansion(String ID, int meta) {
        ItemStack stack = GameRegistry.findItemStack(EnhancedPortals.MODID_THERMALEXPANSION, ID, 1);
        
        if (stack != null)
            stack.setItemDamage(meta);
        
        return stack;
    }

    public static void registerMachineRecipes() {
        // Redstone Interface
        ThermalExpansionHelper.addTransposerFill(10000, new ItemStack(BlockFrame.instance, 1, 0), new ItemStack(BlockFrame.instance, 1, BlockFrame.REDSTONE_INTERFACE), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
        // Redstone Interface Upgrade
        ThermalExpansionHelper.addTransposerFill(10000, new ItemStack(ItemBlankUpgrade.instance, 1, 0), new ItemStack(ItemUpgrade.instance, 1, 0), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
        // Network Interface
        ThermalExpansionHelper.addTransposerFill(15000, new ItemStack(BlockFrame.instance, 1, 0), new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
        // Network Interface Upgrade
        ThermalExpansionHelper.addTransposerFill(15000, new ItemStack(ItemBlankUpgrade.instance, 1, 1), new ItemStack(ItemUpgrade.instance, 1, 1), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
        // DBS
        ThermalExpansionHelper.addTransposerFill(15000, new ItemStack(BlockStabilizerEmpty.instance, 1, 0), new ItemStack(BlockStabilizer.instance, 1, 0), new FluidStack(FluidRegistry.getFluidID("ender"), 125), false);
    }

    public static void registerRecipes() {
        // OreDict items
        String diamondNugget = "nuggetDiamond";
        
        // TE/TF items
        ItemStack machineFrameBasic = getThermalExpansion("Frame"),
                  machineFrameHardened = getThermalExpansion("Frame", 1),
                  powerCoilGold = getThermalExpansion("powerCoilGold");

        // Frame
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 4, 0), "SQS", "QFQ", "SQS", 'S', Blocks.stone, 'Q', Items.quartz, 'F', machineFrameBasic));
        
        // DBS (Empty)
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockStabilizerEmpty.instance, 3, 0), "INI", "NFN", "ICI", 'F', machineFrameHardened, 'C', powerCoilGold, 'I', Items.iron_ingot, 'N', diamondNugget));
    
        // Energy Module
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ENERGY), BlockFrame.instance, Items.ender_pearl, Items.diamond, powerCoilGold));
        
        // Energy Upgrade
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 7), ItemBlankUpgrade.instance, Items.ender_pearl, Items.diamond, powerCoilGold));
    }
    
    public static void registerItems() {
        GameRegistry.registerItem(new ItemDiamondNugget("diamondNugget"), "nuggetDiamond");
        OreDictionary.registerOre("nuggetDiamond", ItemDiamondNugget.instance);
        GameRegistry.addShapelessRecipe(new ItemStack(ItemDiamondNugget.instance, 9), Items.diamond);
        GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond, 1), ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance);
    }
}
