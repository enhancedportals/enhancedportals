package enhanced.portals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.inventory.ContainerRedstoneInterface;
import enhanced.portals.network.packet.PacketGuiData;
import enhanced.portals.tile.TileRedstoneInterface;

public class GuiRedstoneInterface extends BaseGui {
    public static final int CONTAINER_SIZE = 68;
    TileRedstoneInterface redstone;

    public GuiRedstoneInterface(TileRedstoneInterface ri, EntityPlayer p) {
        super(new ContainerRedstoneInterface(ri, p.inventory), CONTAINER_SIZE);
        name = "gui.redstoneInterface";
        redstone = ri;
        setHidePlayerInventory();
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.add(new GuiButton(0, guiLeft + 8, guiTop + 18, xSize - 16, 20, ""));
        buttonList.add(new GuiButton(1, guiLeft + 8, guiTop + 40, xSize - 16, 20, ""));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", button.id);
        EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        String stateText = "";
        boolean flag = redstone.isOutput;

        switch (redstone.state) {
            case 0:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.portalCreated") : Localization.get(EnhancedPortals.MOD_ID, "gui.createPortalOnSignal");
                break;

            case 1:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.portalRemoved") : Localization.get(EnhancedPortals.MOD_ID, "gui.removePortalOnSignal");
                break;

            case 2:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.portalActive") : Localization.get(EnhancedPortals.MOD_ID, "gui.createPortalOnPulse");
                break;

            case 3:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.portalInactive") : Localization.get(EnhancedPortals.MOD_ID, "gui.removePortalOnPulse");
                break;

            case 4:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.entityTeleport") : Localization.get(EnhancedPortals.MOD_ID, "gui.dialStoredIdentifier");
                break;

            case 5:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.playerTeleport") : Localization.get(EnhancedPortals.MOD_ID, "gui.dialStoredIdentifier2");
                break;

            case 6:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.animalTeleport") : Localization.get(EnhancedPortals.MOD_ID, "gui.dialRandomIdentifier");
                break;

            case 7:
                stateText = flag ? Localization.get(EnhancedPortals.MOD_ID, "gui.monsterTeleport") : Localization.get(EnhancedPortals.MOD_ID, "gui.dialRandomIdentifier2");
                break;
        }

        ((GuiButton) buttonList.get(0)).displayString = redstone.isOutput ? Localization.get(EnhancedPortals.MOD_ID, "gui.output") : Localization.get(EnhancedPortals.MOD_ID, "gui.input");
        ((GuiButton) buttonList.get(1)).displayString = stateText;
    }
}
