package enhanced.portals.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.manual.ManualParser;
import enhanced.base.manual.PageManual;
import enhanced.base.utilities.Localisation;
import enhanced.portals.client.gui.elements.ElementManualCraftingGrid;
import enhanced.portals.client.gui.elements.ElementManualTextButton;
import enhanced.portals.inventory.ContainerManual;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.utility.Reference.EPMod;

public class GuiManual extends BaseGui {
    public static final int CONTAINER_SIZE = 180, CONTAINER_WIDTH = 279;
    static HashMap<String, PageManual> manualPages;

    static ResourceLocation textureB = new ResourceLocation(EPMod.ID, "textures/gui/manualB.png");
    ElementManualCraftingGrid craftingGrid;
    ArrayList<ElementManualTextButton> text_buttons = new ArrayList<ElementManualTextButton>();
    // Pages assigned to be triggered on next or prev.
    String NEXT_PAGE;
    String PREV_PAGE;
    // The page to go to by default (localization line).
    String START_PAGE = EPMod.ID + ".manual.subject";
    // Text Format.
    int RED = 0xFF0000;
    int DARK_GREY = 0x222222;
    int LIGHT_GREY = 0xBBBBBB;
    int GREY = 0x444444;
    int BLACK = 0x000000;
    // Char dimensions of a page.
    int CHARS_PER_ROW = 21;
    int ROWS_PER_PAGE = 12;
    int HEADER_ROWS = 2;
    // Page margins.
    int PAGE_MARGIN = 130; // The amt of space a page spans.
    int CONTENT_MARGIN = 100; // The amt of space the content can take up.
    int LINE_HEIGHT = 12; // The height of each line.
    // String constants.
    String HR = "_____________________";
    String LOC_MANUAL_STRING = "manual.chapter";

    String activePage = "intro";
    String activeSecondPage = "toc";

    public GuiManual(EntityPlayer p) {
        super(new ContainerManual(p.inventory), CONTAINER_SIZE);
        xSize = CONTAINER_WIDTH;
        setHidePlayerInventory();
        texture = new ResourceLocation(EPMod.ID, "textures/gui/manualA.png");

        manualPages = null;
        if (manualPages == null)
            manualPages = ManualParser.loadManual(EPMod.ID);
    }

    @Override
    // Draws the next and prev page icons, and "back" icon.
    protected void drawBackgroundTexture() {
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 140, ySize);
        mc.renderEngine.bindTexture(textureB);
        drawTexturedModalRect(guiLeft + 140, guiTop, 0, 0, 139, ySize);

        //boolean mouseHeight = guiTop + mouseY >= guiTop + CONTAINER_SIZE + 3 && guiTop + mouseY < guiTop + CONTAINER_SIZE + 13;
        //boolean rightButton = mouseHeight && guiLeft + mouseX >= guiLeft + CONTAINER_WIDTH - 23 && guiLeft + mouseX < guiLeft + CONTAINER_WIDTH - 5;
        //boolean leftButton = mouseHeight && guiLeft + mouseX >= guiLeft + 5 && guiLeft + mouseX < guiLeft + 23;
        //boolean middleButton = mouseHeight && guiLeft + mouseX >= guiLeft + xSize / 2 - 10 && guiLeft + mouseX < guiLeft + xSize / 2 - 10 + 21;

        // Draw "next page" button.
        //if (manualPages.get(activePage).hasNextPage())
        //    drawTexturedModalRect(guiLeft + CONTAINER_WIDTH - 23, guiTop + CONTAINER_SIZE + 3, rightButton ? 23 : 0, 233, 18, 10);
        // Draw "prev page" button.
        //if (prevPage(true))
        //    drawTexturedModalRect(guiLeft + 5, guiTop + CONTAINER_SIZE + 3, leftButton ? 23 : 0, 246, 18, 10);
        // Draw "back to" button.
        //if (!(ProxyClient.manualEntry.equals("subject") || ProxyClient.manualEntry.equals("contents")))
        //    drawTexturedModalRect(guiLeft + xSize / 2 - 10, guiTop + CONTAINER_SIZE + 3, middleButton ? 21 : 0, 222, 21, 10);
    }

    protected void writeChapterHeader() {
        writeChapterHeader(ProxyClient.chapterNum);
    }

    protected void writeChapterHeader(int chapter_num) {
        String title = Localisation.get(EPMod.ID, LOC_MANUAL_STRING + "." + chapter_num + ".title").trim().toUpperCase();
        getFontRenderer().drawString(title, 15, 15, RED);
        getFontRenderer().drawSplitString(HR, 10, 17, PAGE_MARGIN, LIGHT_GREY);
    }

    // Use if you want all the pages.
    protected void drawChapterPages(int chapter_num, int chapter_page) {
        ArrayList<ArrayList<String>> pages = getChapterPages(chapter_num);
        int left_margin = (PAGE_MARGIN - CONTENT_MARGIN) / 2;
        // We display 2 pages at a time (called a spread) so we only need to get 1 number. We'll make it the even (left) side.
        if (chapter_page % 2 == 1)
            chapter_page--;
        int top_margin = 15;
        // See if this is our first page.
        if (chapter_page == 0) {
            writeChapterHeader(chapter_num);
            top_margin = 35;
        }
        // Left page.
        for (String line : pages.get(chapter_page)) {
            getFontRenderer().drawString(line.trim(), left_margin, top_margin, DARK_GREY);
            top_margin += LINE_HEIGHT;
        }
        top_margin = 15;
        // Right page. Check if we have one first.
        if (pages.size() - 1 > chapter_page)
            for (String line : pages.get(chapter_page + 1)) {
                getFontRenderer().drawString(line.trim(), PAGE_MARGIN + left_margin, top_margin, DARK_GREY);
                top_margin += LINE_HEIGHT;
            }
    }

    // Use if you want the page count.
    protected int countChapterPages(int chapter_num) {
        return getChapterPages(chapter_num).size();
    }

    protected List<String> parseStringByWidth(String string, int line_char_count) {
        List<String> lines = new ArrayList<String>();
        String sub;
        do {
            // Check that there are at least line_char_count # of chars left in string.
            if (string.length() < line_char_count)
                sub = string;
            // Otherwise, let's cut that amount out and trim it for split words.
            else {
                sub = string.substring(0, line_char_count);
                // Check if we just cut through a word.
                if (string.length() > line_char_count + 1)
                    if (string.charAt(line_char_count - 1) != ' ' && string.charAt(line_char_count) != ' ') // Check if the whole line is one large word.
                        if (!(sub.lastIndexOf(' ') < 0))
                            sub = sub.substring(0, sub.lastIndexOf(' '));
            }
            string = string.substring(sub.length());
            lines.add(sub);
        } while (!string.isEmpty());
        return lines;
    }

    protected ArrayList<ArrayList<String>> getChapterPages(int chapter_num) {
        ArrayList<ArrayList<String>> pages = new ArrayList<ArrayList<String>>();
        ArrayList<String> paragraphs = new ArrayList<String>();
        String loc;
        int iterator = 0;
        // Gather all paragraphs for this Chapter and put them in 'paragraphs'.
        do {
            // Set up some localization things.
            loc = LOC_MANUAL_STRING + "." + chapter_num + ".p.";
            String paragraph = Localisation.get(EPMod.ID, loc + iterator);
            iterator++;
            // Add to our array.
            paragraphs.add(paragraph);
            // Loop if we have another paragraph after this.
        } while (Localisation.exists(EPMod.ID, loc + iterator));
        // Process the paragraphs and turn them into pages.
        while (!paragraphs.isEmpty()) {
            ArrayList<String> page = new ArrayList<String>();
            // Determine the amount of rows we need to grab per page.
            int page_rows = pages.size() == 0 ? ROWS_PER_PAGE - HEADER_ROWS : ROWS_PER_PAGE;
            // Fill up the page array as long as the page array isn't full and we still have paragraphs left.
            while (page.size() < page_rows && !paragraphs.isEmpty()) {
                ArrayList<String> paragraph_lines = new ArrayList<String>();
                paragraph_lines.addAll(parseStringByWidth(paragraphs.get(0), CHARS_PER_ROW));
                // We need more lines to fill this page.
                if (paragraph_lines.size() <= page_rows - page.size()) {
                    page = addListToArrayList(page, paragraph_lines);
                    // Check if we need to include a separator line between paragraphs.
                    if (paragraph_lines.size() != page_rows - page.size())
                        page.add("");
                    paragraphs.remove(0);
                    // Otherwise we are over.
                } else {
                    int remove_amt_of_lines = page_rows - page.size();
                    // Add the lines to 'page' that we need to fill it up.
                    page = addListToArrayList(page, paragraph_lines.subList(0, remove_amt_of_lines));
                    // Then delete them from our paragraph_lines.
                    paragraph_lines.subList(0, remove_amt_of_lines).clear();
                    paragraphs.remove(0);
                    // Add the remaining paragraph back to paragraphs.
                    paragraphs.add(0, mergeToString(paragraph_lines));
                }
            }
            pages.add(page);
        }
        return pages;
    }

    protected String mergeToString(ArrayList<String> a) {
        List<String> l = new ArrayList<String>();
        l.addAll(a);
        return mergeToString(l);
    }

    protected String mergeToString(List<String> list) {
        String string = "";
        for (String i : list)
            string += i;
        return string;
    }

    protected ArrayList<String> addListToArrayList(ArrayList<String> a, List<String> l) {
        for (String i : l)
            a.add(i);
        return a;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        /*hideItemLinks();

        // Check to see where we are in the Manual:
        // Main Title page.
        if (ProxyClient.manualEntry.equals("subject")) {
            hideCraftingTable();
            // drawSplitString(<right>,<down>,<width>,<text>,<color>)
            getFontRenderer().drawString("ENHANCED", 160, 70, RED);
            getFontRenderer().drawString("PORTALS", 210, 70, DARK_GREY);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.subject.sub").toLowerCase(), 160, 80, PAGE_MARGIN, LIGHT_GREY);
            // Table of Contents.
        } else if (ProxyClient.manualEntry.equals("contents")) {
            hideCraftingTable();
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            getFontRenderer().drawSplitString("> " + Localisation.get(EPMod.ID, "manual.table_of_contents.title").toUpperCase() + " <", 15, 20, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(":", 17, 35, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.0.title"), 20, 35, PAGE_MARGIN, GREY);
            getFontRenderer().drawSplitString(":", 17, 50, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.1.title"), 20, 50, PAGE_MARGIN, GREY);
            getFontRenderer().drawSplitString(":", 17, 65, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.2.title"), 20, 65, 120, GREY);
            getFontRenderer().drawSplitString(":", 17, 80, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.3.title"), 20, 80, 120, GREY);
            getFontRenderer().drawSplitString(":", 17, 95, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.4.title"), 20, 95, 120, GREY);
            getFontRenderer().drawSplitString(":", 17, 110, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.5.title"), 20, 110, PAGE_MARGIN, GREY);
            getFontRenderer().drawSplitString(":", 17, 125, PAGE_MARGIN, RED);
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, "manual.chapter.6.title"), 20, 125, PAGE_MARGIN, GREY);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            // Chapters.
        } else if (ProxyClient.manualEntry.equals("chapter")) {
            hideCraftingTable();
            drawChapterPages(ProxyClient.chapterNum, ProxyClient.chapterPage);
            // Item Gallery for each chapter.
        } else if (ProxyClient.manualEntry.equals("gallery")) {
            hideCraftingTable();
            String title = Localisation.get(EPMod.ID, "manual.gallery.title");
            ArrayList<String> items = new ArrayList<String>();
            // Draw the header.
            getFontRenderer().drawString(title.toUpperCase(), 40, 15, LIGHT_GREY);
            getFontRenderer().drawString("==========", 45, 25, DARK_GREY);
            switch (ProxyClient.chapterNum) {
                default:
                case 2:
                    items.add("dbs");
                    items.add("wrench");
                    break;
                case 3:
                    items.add("frame0"); // Portal Frame
                    items.add("dbs");
                    items.add("location_card");
                    items.add("wrench");
                    items.add("frame1"); // Frame Controller
                    items.add("frame3"); // Network Interface
                    break;
                case 4:
                    items.add("frame2"); // Redstone Interface
                    break;
                case 5:
                    items.add("frame4"); // Dialling Device
                    break;
                case 6:
                    items.add("nanobrush");
                    items.add("frame6"); // Module Manipulator
                    items.add("frame7"); // Fuild Transportation Module
                    items.add("frame8"); // Item Transportation Module
                    items.add("frame9"); // Energy Transportation Module
                    break;
            }
            drawItemLinks(items);
            // Otherwise we are viewing an Item page.
        } else {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            ItemStack[] stacks = ProxyClient.getCraftingRecipeForManualEntry();
            craftingGrid.setVisible(stacks != null);
            craftingGrid.setItems(stacks);
            // Setup some variables to output the item info.
            int left_margin = 15;
            int top_margin = 15;
            String loc_entry = "manual." + ProxyClient.manualEntry;
            top_margin += drawSplitString(left_margin, top_margin, CONTENT_MARGIN, Localisation.get(EPMod.ID, loc_entry + ".title").toUpperCase(), RED);
            // Check if subtitles exist.
            if (Localisation.exists(EPMod.ID, loc_entry + ".subtitle"))
                getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, loc_entry + ".subtitle"), left_margin, top_margin, CONTENT_MARGIN, LIGHT_GREY);
            // Right page.
            top_margin = 15;
            getFontRenderer().drawSplitString(Localisation.get(EPMod.ID, loc_entry + ".info"), PAGE_MARGIN + left_margin, top_margin, CONTENT_MARGIN, DARK_GREY);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
        }*/

        manualPages.get(activePage).render(false);

        if (activeSecondPage != null)
            manualPages.get(activeSecondPage).render(true);

        super.drawGuiContainerForegroundLayer(par1, par2);
    }

    protected void hideCraftingTable() {
        if (craftingGrid != null) {
            // Hide the crafting grid and item holders.
            craftingGrid.setVisible(false);
            craftingGrid.setItems(null);
        }
    }

    protected void hideItemLinks() {
        if (!text_buttons.isEmpty())
            for (ElementManualTextButton link : text_buttons)
                link.setVisible(false);
    }

    protected void drawItemLinks(ArrayList<String> items) {
        int top_margin = 40;
        int left_margin = 15;
        for (String item : items) {
            // If it's blank, means we want a spacer.
            if (!item.isEmpty()) {
                ElementManualTextButton link = new ElementManualTextButton(this, left_margin, top_margin, item);
                link.setVisible(true);
                addElement(link);
                text_buttons.add(link);
            }
            top_margin += LINE_HEIGHT;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 4 && activeSecondPage != null && !activeSecondPage.isEmpty() && manualPages.get(activeSecondPage).hasNextPage()) {
            activePage = manualPages.get(activeSecondPage).getNextPage();
            activeSecondPage = manualPages.get(activePage).getNextPage();
            return;
        } else if (mouseButton == 3 && manualPages.get(activePage).hasPrevPage()) {
            String prev = manualPages.get(activePage).getPrevPage();
            PageManual m = manualPages.get(prev);
            
            if (m.hasPrevPage()) {
                activePage = m.getPrevPage();
                activeSecondPage = prev;
            }
            return;
        } else if (mouseY >= guiTop + CONTAINER_SIZE + 3 && mouseY < guiTop + CONTAINER_SIZE + 13)
            if (mouseX >= guiLeft + CONTAINER_WIDTH - 23 && mouseX < guiLeft + CONTAINER_WIDTH - 5 && nextPage(true)) {
                nextPage();
                return;
            } else if (mouseX >= guiLeft + 5 && mouseX < guiLeft + 23 && prevPage(true)) {
                prevPage();
                return;
            } else if (mouseX >= guiLeft + xSize / 2 - 10 && mouseX < guiLeft + xSize / 2 - 10 + 21) {
                // Check if we need to link back to the Table of Contents or the last page we were viewing.
                if (ProxyClient.manualEntry.equals("chapter") || ProxyClient.manualEntry.equals("gallery"))
                    changeEntry("contents");
                else if (!ProxyClient.manualEntry.equals("subject") && !ProxyClient.manualEntry.equals("contents"))
                    changeEntry("gallery");
                return;
            }
    }

    void nextPage() {
        nextPage(false);
    }

    void prevPage() {
        prevPage(false);
    }

    boolean nextPage(boolean is_next) {
        // -> on Subject page.
        if (ProxyClient.manualEntry.equals("subject")) {
            if (is_next)
                return true;

            // Trigger the page change.
            changeEntry("contents");
        } else if (ProxyClient.manualEntry.equals("contents")) {
            if (is_next)
                return true;

            ProxyClient.chapterNum = 0;
            ProxyClient.chapterPage = 0;
            changeEntry("chapter");
        } else if (ProxyClient.manualEntry.equals("chapter")) {
            int total_chapter_pages = countChapterPages(ProxyClient.chapterNum);
            // Check if the current page is not the last page.
            if (total_chapter_pages > ProxyClient.chapterPage + 2) {
                if (is_next)
                    return true;

                ProxyClient.chapterPage += 2;
                changeEntry("chapter");
            } else // Check if this chapter has a term Gallery.
                if (ProxyClient.chapterNum > 1) {
                    if (is_next)
                        return true;

                    changeEntry("gallery");
                } else if (ProxyClient.manualChapterExists(ProxyClient.chapterNum + 1)) {
                    if (is_next)
                        return true;

                    ProxyClient.chapterNum++;
                    ProxyClient.chapterPage = 0;
                    changeEntry("chapter");
                }
        } else if (ProxyClient.manualEntry.equals("gallery")) // Check if there is a next chapter.
            if (ProxyClient.manualChapterExists(ProxyClient.chapterNum + 1)) {
                if (is_next)
                    return true;
                ProxyClient.chapterNum++;
                ProxyClient.chapterPage = 0;
                changeEntry("chapter");
            }
        return false;
    }

    boolean prevPage(boolean is_prev) {
        // <- on Table of Contents.
        if (ProxyClient.manualEntry.equals("contents")) {
            if (is_prev)
                return true;

            changeEntry("subject");
        } else if (ProxyClient.manualEntry.equals("chapter")) {
            // Check if the current page is not the last page.
            if (!(ProxyClient.chapterPage <= 1)) {
                if (is_prev)
                    return true;

                ProxyClient.chapterPage -= 2;
                changeEntry("chapter");
            } else // Check if there is a next chapter.
                if (ProxyClient.manualChapterExists(ProxyClient.chapterNum - 1)) {
                    if (is_prev)
                        return true;
                    ProxyClient.chapterNum--;
                    ProxyClient.chapterPage = countChapterPages(ProxyClient.chapterNum) - 1;
                    // Check if the previous chapter has a Gallery.
                    if (ProxyClient.chapterNum > 1)
                        changeEntry("gallery");
                    else
                        changeEntry("chapter");
                } else if (is_prev)
                    return true;
                else {
                    ProxyClient.chapterNum = 0;
                    ProxyClient.chapterPage = 0;
                    changeEntry("contents");
                }
        } else if (ProxyClient.manualEntry.equals("gallery")) {
            if (is_prev)
                return true;
            ProxyClient.chapterPage = countChapterPages(ProxyClient.chapterNum) - 1;
            changeEntry("chapter");
        }
        return false;
    }

    void changeEntry(String e) {
        ProxyClient.manualChangeEntry(e);
        pageChanged();
    }

    @Override
    public void initGui() {
        super.initGui();

        // Initiate the crafting grid for the item pages.
        craftingGrid = new ElementManualCraftingGrid(this, 70 - 33, 90 - 33, null);

        if (ProxyClient.manualEntry.equals("subject") || ProxyClient.manualEntry.equals("gallery") || ProxyClient.manualEntry.equals("chapter") || ProxyClient.manualEntry.equals("contents"))
            craftingGrid.setVisible(false);

        addElement(craftingGrid);
    }

    public void pageChanged() {

    }
}
