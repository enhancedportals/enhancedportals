package enhanced.portals.portal;

import enhanced.base.utilities.Localisation;
import enhanced.portals.utility.Reference.EPMod;

public class PortalException extends Exception {
    private static final long serialVersionUID = 7990987289131589119L;

    public PortalException(String message) {
        super(Localisation.getChatError(EPMod.ID, message));
    }

    public PortalException(String message, boolean localize) {
        super(localize ? Localisation.getChatError(EPMod.ID, message) : message);
    }
}
