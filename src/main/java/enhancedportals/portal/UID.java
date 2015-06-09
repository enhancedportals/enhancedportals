package enhancedportals.portal;

public class UID {
	byte[] glyphs = new byte[9];
	
	/** Sets the glyph in the specified position **/
	public void setGlyph(int pos, byte g) {
		if (pos >= 0 && pos < 9)
			glyphs[pos] = g;
	}
	
	/** Gets the glyph in the specified position. Errors out -2 **/
	public byte getGlyph(int pos) {
		if (pos >= 0 && pos < 9)
			return glyphs[pos];
		else
			return -2;
	}
	
	@Override
	public int hashCode() {
		return glyphs.hashCode();
	}
	
	public String toString() {
		String ret = "";
		
		for (int i = 0; i < 9; i++) {
			if (glyphs.length >= i && glyphs[i] != -1) {
				ret = ret + "-" + glyphs[i];
			}
		}
		
		return ret.substring(1);
	}
}
