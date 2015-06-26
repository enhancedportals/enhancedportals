package enhanced.portals.item;

import enhanced.base.item.ItemBase;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.utility.Reference.EPMod;

public class ItemBlankUpgrade extends ItemBase {
    public ItemBlankUpgrade(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setTextureName(EPMod.ID + ":upgrade");
    }
}
