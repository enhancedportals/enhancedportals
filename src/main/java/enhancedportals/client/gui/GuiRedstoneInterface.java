package enhancedportals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhancedportals.EnhancedPortals;
import enhancedportals.inventory.ContainerRedstoneInterface;
import enhancedportals.network.packet.PacketGuiData;
import enhancedportals.tile.TileRedstoneInterface;
import enhancedportals.utility.Localization;

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
        EnhancedPortals.packetPipeline.sendToServer(new PacketGuiData(tag));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        String stateText = "";
        boolean flag = redstone.isOutput;

        switch (redstone.state) {
            case 0:
                stateText = flag ? Localization.get("gui.portalCreated") : Localization.get("gui.createPortalOnSignal");
                break;

            case 1:
                stateText = flag ? Localization.get("gui.portalRemoved") : Localization.get("gui.removePortalOnSignal");
                break;

            case 2:
                stateText = flag ? Localization.get("gui.portalActive") : Localization.get("gui.createPortalOnPulse");
                break;

            case 3:
                stateText = flag ? Localization.get("gui.portalInactive") : Localization.get("gui.removePortalOnPulse");
                break;

            case 4:
                stateText = flag ? Localization.get("gui.entityTeleport") : Localization.get("gui.dialStoredIdentifier");
                break;

            case 5:
                stateText = flag ? Localization.get("gui.playerTeleport") : Localization.get("gui.dialStoredIdentifier2");
                break;

            case 6:
                stateText = flag ? Localization.get("gui.animalTeleport") : Localization.get("gui.dialRandomIdentifier");
                break;

            case 7:
                stateText = flag ? Localization.get("gui.monsterTeleport") : Localization.get("gui.dialRandomIdentifier2");
                break;
        }

        ((GuiButton) buttonList.get(0)).displayString = redstone.isOutput ? Localization.get("gui.output") : Localization.get("gui.input");
        ((GuiButton) buttonList.get(1)).displayString = stateText;
    }
}
