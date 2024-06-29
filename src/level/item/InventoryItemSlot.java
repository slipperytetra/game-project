package level.item;

import utils.CollisionBox;

public class InventoryItemSlot {

    private CollisionBox cBox;
    private InventoryItem item;
    private Inventory inv;
    private int posX, posY;

    public InventoryItemSlot(Inventory inv, InventoryItem item, int posX, int posY) {
        this.inv = inv;
        this.item = item;
        this.posX = posX;
        this.posY = posY;
        cBox = new CollisionBox(posX * inv.getSlotSize(), posY * inv.getSlotSize(), inv.getSlotSize(), inv.getSlotSize());
    }

    public int getSlot() {
        return posX + posY;
    }

    public CollisionBox getCollisionBox() {
        return cBox;
    }

    public Inventory getInventory() {
        return inv;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }
}
