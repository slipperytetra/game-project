package level.item;

import level.Level;
import main.Camera;
import main.TextureBank;

import java.awt.*;

public class Inventory {

    private int width, height;
    private InventoryItemSlot[] items;
    private boolean isOpen;
    private final int slotSize = 40;
    private final float opacity = 0.9f;
    private int selectedSlot;

    public Inventory(int width, int height) {
        this.width = width;
        this.height = height;
        this.items = new InventoryItemSlot[getSize()];
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                items[x + y] = new InventoryItemSlot(this, null, x, y);
            }
        }
        this.selectedSlot = 0;
    }

    public void update(double dt) {

    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean isOpen, Level level) {
        this.isOpen = isOpen;
        if (this.isOpen) {
            level.getOpenInventories().add(this);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return width * height;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= getSize()) {
            return;
        }

        this.selectedSlot = slot;
    }

    public InventoryItemSlot getItemAt(int slot) {
        return items[slot];
    }

    public void setItemAt(InventoryItem item, int slot) {
        if (slot >= getSize()) {
            return;
        }

        this.items[slot].setItem(item);
    }

    public void addItem(ItemType type, int amount) {
        if (containsItem(type)) {
            for (int i = 0; i < getSize(); i++) {
                if (getItemAt(i).getItem() == null) {
                    continue;
                }

                InventoryItem invItem = getItemAt(i).getItem();
                if (invItem.getItemType() == type) {
                    invItem.setAmount(invItem.getAmount() + amount);
                    return;
                }
            }
        }

        for (int i = 0; i < getSize(); i++) {
            if (getItemAt(i).getItem() == null) {
                setItemAt(new InventoryItem(type, amount), i);
                break;
            }
        }
    }

    public void removeItem(InventoryItem item) {
        if (containsItem(item.getItemType())) {
            for (int i = 0; i < getSize(); i++) {
                InventoryItem invItem = getItemAt(i).getItem();
                if (invItem == null) {
                    continue;
                }

                if (invItem.getItemType() == item.getItemType()) {
                    getItemAt(i).setItem(null);
                }
            }
        }
    }

    public boolean containsItem(ItemType type) {
        for (int i = 0; i < getSize(); i++) {
            if (getItemAt(i) == null || getItemAt(i).getItem() == null) {
                continue;
            }

            if (getItemAt(i).getItem().getItemType() == type) {
                return true;
            }
        }

        return false;
    }

    public InventoryItemSlot[] getItems() {
        return items;
    }

    public void render(Camera cam, double locX, double locY) {
        if (!isOpen() || getSize() <= 0) {
            return;
        }

        locX = locX - ((double) (getWidth() * slotSize) / 2) + ((double) slotSize / 2);

        renderFrame(cam, locX, locY);
        TextureBank bank = cam.game.getTextureBank();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                cam.game.drawImage(bank.getTexture("ui_inventory_4").getImage(), cam.toScreenX(locX + (x * slotSize)), cam.toScreenY(locY + (y * slotSize)), slotSize, slotSize, opacity);
                boolean isSelected = getSelectedSlot() == x + y;
                InventoryItem item = getItemAt(x + y).getItem();
                if (item != null) {
                    if (isSelected) {
                        cam.game.drawImage(bank.getTexture("ui_inventory_filled_selected").getImage(), cam.toScreenX(locX + (x * slotSize) - 4), cam.toScreenY(locY + (y * slotSize) - 4), slotSize + 8, slotSize + 8, opacity);
                        cam.game.drawImage(bank.getTexture("item_" + item.getItemType().toString().toLowerCase()).getImage(), cam.toScreenX(locX + (x * slotSize)) + 4, cam.toScreenY(locY + (y * 40)) + 4, 32, 32, opacity);
                    } else {
                        cam.game.drawImage(bank.getTexture("ui_inventory_filled").getImage(), cam.toScreenX(locX + (x * slotSize)), cam.toScreenY(locY + (y * slotSize)), slotSize, slotSize, opacity);
                        cam.game.drawImage(bank.getTexture("item_" + item.getItemType().toString().toLowerCase()).getImage(), cam.toScreenX(locX + (x * slotSize)) + 6, cam.toScreenY(locY + (y * 40)) + 6, 28, 28, opacity);
                    }
                }

                if (isSelected) {
                    cam.game.drawImage(bank.getTexture("ui_inventory_selected").getImage(), cam.toScreenX(locX + (x * slotSize) - 4), cam.toScreenY(locY + (y * slotSize) - 4), slotSize + 8, slotSize + 8);
                }

                if (item != null) {
                    if (item.getAmount() > 1) {
                        double txtX = cam.toScreenX(locX + (x * slotSize) + slotSize - 8);
                        double txtY = cam.toScreenY(locY + (y * slotSize));

                        cam.game.changeColor(Color.BLACK);
                        cam.game.drawBoldText(txtX, txtY, "" + item.getAmount(), 15);
                        cam.game.changeColor(Color.WHITE);
                        cam.game.drawBoldText(txtX, txtY, "" + item.getAmount(), 13);
                    }
                }

                InventoryItemSlot slotItem = getItemAt(x + y);
                slotItem.getCollisionBox().setLocation(locX + (x * slotSize), locY + (y * slotSize));
                if (cam.debugMode) {
                    cam.game.changeColor(Color.GREEN);
                    cam.game.drawRectangle(cam.toScreenX(slotItem.getCollisionBox().getLocation().getX()), cam.toScreenY(slotItem.getCollisionBox().getLocation().getY()), getItemAt(x + y).getCollisionBox().getWidth() , getItemAt(x + y).getCollisionBox().getHeight() );
                }
            }
        }
    }

    private void renderFrame(Camera cam, double locX, double locY) {
        TextureBank bank = cam.game.getTextureBank();

        cam.game.drawImage(bank.getTexture("ui_inventory_0").getImage(), cam.toScreenX(locX - (slotSize)), cam.toScreenY(locY - (slotSize)), slotSize, slotSize, opacity);
        cam.game.drawImage(bank.getTexture("ui_inventory_2").getImage(), cam.toScreenX(locX + (getWidth() * slotSize)), cam.toScreenY(locY - (slotSize)), slotSize, slotSize, opacity);
        cam.game.drawImage(bank.getTexture("ui_inventory_6").getImage(), cam.toScreenX(locX - (slotSize)), cam.toScreenY(locY + (getHeight() * slotSize)), slotSize, slotSize, opacity);
        cam.game.drawImage(bank.getTexture("ui_inventory_8").getImage(), cam.toScreenX(locX + (getWidth() * slotSize)), cam.toScreenY(locY + (getHeight() * slotSize)), slotSize, slotSize, opacity);

        for (int x = 0; x < getWidth(); x++) {
            cam.game.drawImage(bank.getTexture("ui_inventory_1").getImage(), cam.toScreenX(locX + (x * slotSize)), cam.toScreenY(locY - (slotSize)), slotSize, slotSize, opacity);
            cam.game.drawImage(bank.getTexture("ui_inventory_7").getImage(), cam.toScreenX(locX + (x * slotSize)), cam.toScreenY(locY + (getHeight() * slotSize)), slotSize, slotSize, opacity);
        }

        for (int y = 0; y < getHeight(); y++) {
            cam.game.drawImage(bank.getTexture("ui_inventory_3").getImage(), cam.toScreenX(locX - (slotSize)), cam.toScreenY(locY + (y * slotSize)), slotSize, slotSize, opacity);
            cam.game.drawImage(bank.getTexture("ui_inventory_5").getImage(), cam.toScreenX(locX + (getWidth() * slotSize)), cam.toScreenY(locY + (y * slotSize)), slotSize, slotSize, opacity);
        }

        cam.game.drawImage(bank.getTexture("ui_inventory_icon").getImage(), cam.toScreenX(locX + ((double) (getWidth() * slotSize) / 2) - ((double) slotSize)), cam.toScreenY(locY - (slotSize) - ((double) slotSize / 2)), slotSize *2, slotSize *2, opacity);
    }

    public int getSlotSize() {
        return slotSize;
    }

    public void update() {
        for (InventoryItemSlot itemSlot : getItems()) {
            if (itemSlot.getItem() != null) {
                InventoryItem item = itemSlot.getItem();
                if (item.getAmount() <= 0) {
                    removeItem(item);
                }
            }
        }
    }
}
