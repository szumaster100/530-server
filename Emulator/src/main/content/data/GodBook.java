package content.data;

import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import org.rs.consts.Items;
import org.rs.consts.Quests;

import static core.api.quest.QuestAPIKt.hasRequirement;

public enum GodBook {
    HOLY_BOOK(
        "Holy Book of Saradomin",
        new Item(Items.HOLY_BOOK_3840),
        new Item(Items.DAMAGED_BOOK_3839),
        new Item[]{new Item(Items.HOLY_SYMBOL_1718)},
        new Item[]{
            new Item(Items.SARADOMIN_PAGE_1_3827),
            new Item(Items.SARADOMIN_PAGE_2_3828),
            new Item(Items.SARADOMIN_PAGE_3_3829),
            new Item(Items.SARADOMIN_PAGE_4_3830)
        }
    ),
    BOOK_OF_BALANCE(
        "Guthix's Book of Balance",
        new Item(Items.BOOK_OF_BALANCE_3844),
        new Item(Items.DAMAGED_BOOK_3843),
        new Item[]{
            new Item(Items.HOLY_SYMBOL_1718),
            new Item(Items.UNHOLY_SYMBOL_1724)
        },
        new Item[]{
            new Item(Items.GUTHIX_PAGE_1_3835),
            new Item(Items.GUTHIX_PAGE_2_3836),
            new Item(Items.GUTHIX_PAGE_3_3837),
            new Item(Items.GUTHIX_PAGE_4_3838)
        }
    ),
    UNHOLY_BOOK(
        "Unholy Book of Zamorak",
        new Item(Items.UNHOLY_BOOK_3842),
        new Item(Items.DAMAGED_BOOK_3841),
        new Item[]{new Item(Items.UNHOLY_SYMBOL_1724)},
        new Item[]{
            new Item(Items.ZAMORAK_PAGE_1_3831),
            new Item(Items.ZAMORAK_PAGE_2_3832),
            new Item(Items.ZAMORAK_PAGE_3_3833),
            new Item(Items.ZAMORAK_PAGE_4_3834)
        }
    );

    private final String bookName;
    private final Item book;
    private final Item damagedBook;
    private final Item[] blessItem;
    private final Item[] pages;

    GodBook(String bookName, Item book, Item damagedBook, Item[] blessItem, Item[] pages) {
        this.bookName = bookName;
        this.book = book;
        this.damagedBook = damagedBook;
        this.blessItem = blessItem;
        this.pages = pages;
    }

    public String getBookName() {
        return bookName;
    }

    public Item getBook() {
        return book;
    }

    public Item getDamagedBook() {
        return damagedBook;
    }

    public Item[] getBlessItem() {
        return blessItem;
    }

    public Item[] getPages() {
        return pages;
    }

    public boolean hasGodBook(Player player, boolean both) {
        return player.getInventory().containsItems(
            both ? new Item[]{book, damagedBook} : new Item[]{book}
        );
    }

    public void insertPage(Player player, Item book, Item page) {
        if (!hasRequirement(player, Quests.HORROR_FROM_THE_DEEP)) return;

        if (hasPage(player, book, page)) {
            player.sendMessage("The book already has that page.");
            return;
        }

        if (player.getInventory().remove(new Item(page.getId(), 1))) {
            setPageHash(player, book, getPageIndex(page));
            player.sendMessage("You add the page to the book...");
            if (isComplete(player, book)) {
                player.getSavedData().globalData.setGodPages(new boolean[4]);
                player.getSavedData().globalData.setGodBook(-1);
                player.getInventory().replace(this.book, book.getSlot());
                player.getSavedData().globalData.setGodBook(this);
                player.sendMessage("The book is now complete!");
                String message;
                switch (this) {
                    case UNHOLY_BOOK:
                        message = "unholy symbols";
                        break;
                    case HOLY_BOOK:
                        message = "holy symbols";
                        break;
                    default:
                        message = "unblessed holy symbols";
                        break;
                }
                player.sendMessage("You can now use it to bless " + message + "!");
            }
        }
    }

    public boolean isPage(Item asItem) {
        for (Item page : pages) {
            if (page.getId() == asItem.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean isComplete(Player player, Item book) {
        for (int i = 1; i <= 4; i++) {
            if (!hasPage(player, book, i)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPage(Player player, Item book, Item page) {
        return hasPage(player, book, getPageIndex(page));
    }

    public void setPageHash(Player player, Item book, int pageId) {
        player.getSavedData().globalData.getGodPages()[pageId - 1] = true;
    }

    public boolean hasPage(Player player, Item book, int pageId) {
        return player.getSavedData().globalData.getGodPages()[pageId - 1];
    }

    public int getHash(Item book) {
        return book.getCharge() - 1000;
    }

    public int getPageIndex(Item page) {
        for (int i = 0; i < pages.length; i++) {
            if (pages[i].getId() == page.getId()) {
                return i + 1;
            }
        }
        return -1;
    }

    public static GodBook forItem(Item item, boolean damaged) {
        for (GodBook godBook : values()) {
            if ((damaged ? godBook.damagedBook.getId() : godBook.book.getId()) == item.getId()) {
                return godBook;
            }
        }
        return null;
    }

    public static GodBook forPage(Item page) {
        for (GodBook godBook : values()) {
            for (Item bookPage : godBook.pages) {
                if (bookPage.getId() == page.getId()) {
                    return godBook;
                }
            }
        }
        return null;
    }
}
