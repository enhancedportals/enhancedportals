package enhanced.portals.client.gui;


public class GuiDimensionalBridgeStabilizer /*extends BaseGui*/ {/*
    public static final int CONTAINER_SIZE = 90;
    TileStabilizerMain stabilizer;
    TabRedstoneFlux rfTab = null;

    public GuiDimensionalBridgeStabilizer(TileStabilizerMain s, EntityPlayer p) {
        super(new ContainerDimensionalBridgeStabilizer(s, p.inventory), CONTAINER_SIZE);
        stabilizer = s;
        name = Localisation.get(EPMod.ID, Locale.GUI_DIMENSIONAL_BRIDGE_STABILIZER);
        setCombinedInventory();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // Triggered when the + and - buttons are pushed for Instability.
        if (button.id == 0 || button.id == 1) {
            String key = (button.id == 0 ? "increase" : "decrease") + "_powerState";
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean(key, false);
            // Sent to ContainerDBS.java.
            EnhancedPortals.instance.packetPipeline.sendToServer(new PacketGuiData(tag));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton add_risk = new GuiButtonSmall(0, guiLeft + 65, guiTop + containerSize - 53, 10, 10, "+");
        GuiButton minus_risk = new GuiButtonSmall(1, guiLeft + 77, guiTop + containerSize - 53, 10, 10, "-");
        buttonList.add(add_risk);
        buttonList.add(minus_risk);
        // addElement(new ElementScrollStabilizer(this, stabilizer, 7, 28));

        if (EPConfiguration.requirePower) {
            addElement(new ElementRedstoneFlux(this, xSize - 23, 18, stabilizer.getEnergyStorage()));
            rfTab = new TabRedstoneFlux(this, stabilizer);
            addTab(rfTab);
        }
    }

    @Override
    protected void drawBackgroundTexture() {
        super.drawBackgroundTexture();

        mc.renderEngine.bindTexture(playerInventoryTexture);
        drawTexturedModalRect(guiLeft + xSize - 25, guiTop + containerSize - 26, 7, 7, 18, 18);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);

        String s1 = "" + stabilizer.intActiveConnections * 2;
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_INFORMATION), 8, 18, 0x404040);
        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_ACTIVE_PORTALS), 12, 28, 0x777777);
        getFontRenderer().drawString(s1, xSize - 27 - getFontRenderer().getStringWidth(s1), 28, 0x404040);

        int instability = stabilizer.powerState == 0 ? stabilizer.instability : stabilizer.powerState == 1 ? 20 : stabilizer.powerState == 2 ? 50 : 70;
        String s2 = instability + "%";

        getFontRenderer().drawString(Localisation.get(EPMod.ID, Locale.GUI_INSTABILITY), 12, 38, 0x777777);
        getFontRenderer().drawString(s2, xSize - 27 - getFontRenderer().getStringWidth(s2), 38, instability == 0 ? 0x00BB00 : instability == 20 ? 0xDD6644 : instability == 50 ? 0xDD4422 : 0xFF0000);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (rfTab != null) {
            int instability = stabilizer.powerState == 0 ? stabilizer.instability : stabilizer.powerState == 1 ? 20 : stabilizer.powerState == 2 ? 50 : 70;
            int powerCost = (int) (stabilizer.intActiveConnections * EPConfiguration.redstoneFluxCost * EPConfiguration.powerUseMultiplier);
            powerCost -= (int) (powerCost * (instability / 100f));
            rfTab.setPowerCost(powerCost / 20);
        }
    }*/
}
