package enhanced.portals.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.Locale;
import enhanced.portals.inventory.ContainerRedstoneInterface;
import enhanced.portals.tile.TileRedstoneInterface;

public class GuiRedstoneInterface extends BaseGui {
    public static final int CONTAINER_SIZE = 68;
    TileRedstoneInterface redstone;

    public GuiRedstoneInterface(TileRedstoneInterface ri, EntityPlayer p) {
        super(new ContainerRedstoneInterface(ri, p.inventory), CONTAINER_SIZE);
        name = Localisation.get(EPMod.ID, Locale.GUI_REDSTONE_INTERFACE);
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
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_PORTAL_CREATED) : Localisation.get(EPMod.ID, Locale.GUI_CREATE_PORTAL_ON_SIGNAL);
                break;

            case 1:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_PORTAL_REMOVED) : Localisation.get(EPMod.ID, Locale.GUI_REMOVE_PORTAL_ON_SIGNAL);
                break;

            case 2:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_PORTAL_ACTIVE) : Localisation.get(EPMod.ID, Locale.GUI_CREATE_PORTAL_ON_PULSE);
                break;

            case 3:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_PORTAL_INACTIVE) : Localisation.get(EPMod.ID, Locale.GUI_REMOVE_PORTAL_ON_PULSE);
                break;

            case 4:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_ENTITY_TELEPORT) : Localisation.get(EPMod.ID, Locale.GUI_DIAL_STORED_IDENTIFIER);
                break;

            case 5:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_PLAYER_TELEPORT) : Localisation.get(EPMod.ID, Locale.GUI_DIAL_STORED_IDENTIFIER2);
                break;

            case 6:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_ANIMAL_TELEPORT) : Localisation.get(EPMod.ID, Locale.GUI_DIAL_RANDOM_IDENTIFIER);
                break;

            case 7:
                stateText = flag ? Localisation.get(EPMod.ID, Locale.GUI_MONSTER_TELEPORT) : Localisation.get(EPMod.ID, Locale.GUI_DIAL_RANDOM_IDENTIFIER2);
                break;
        }

        ((GuiButton) buttonList.get(0)).displayString = redstone.isOutput ? Localisation.get(EPMod.ID, Locale.GUI_OUTPUT) : Localisation.get(EPMod.ID, Locale.GUI_INPUT);
        ((GuiButton) buttonList.get(1)).displayString = stateText;
    }
}
