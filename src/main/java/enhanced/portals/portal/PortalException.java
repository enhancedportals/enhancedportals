package enhanced.portals.portal;

import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;

public class PortalException extends Exception {
    private static final long serialVersionUID = 7990987289131589119L;

    public PortalException(String message) {
        super(Localization.getChatError(EnhancedPortals.MOD_ID, message));
    }

    public PortalException(String message, boolean localize) {
        super(localize ? Localization.getChatError(EnhancedPortals.MOD_ID, message) : message);
    }
}
