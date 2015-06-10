package enhancedportals.portal;

import enhancedportals.utility.Localization;

public class PortalException extends Exception {
    private static final long serialVersionUID = 7990987289131589119L;

    public PortalException(String message) {
        super(Localization.getChatError(message));
    }

    public PortalException(String message, boolean localize) {
        super(localize ? Localization.getChatError(message) : message);
    }
}
