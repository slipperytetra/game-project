package block;

import level.Level;
import level.item.Inventory;
import level.item.InventoryItem;
import level.item.ItemType;
import utils.Location;

import java.util.Random;

public class BlockChest extends BlockInteractable {

    private Inventory inventory;

    public BlockChest(Level level, Location loc, BlockTypes type) {
        super(level, loc, type);

        inventory = new Inventory(3, 3);

        Random rand = new Random();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
                //if (rand.nextInt(0, 99) < 25) {
                    inventory.setItemAt(new InventoryItem(ItemType.ARROW, slot), slot);
                //}
        }
    }

    @Override
    public void update(double dt) {

        if (!isActive() || !isOnScreen()) {
            return;
        }


        if (isInteractable() && getLevel().getManager().getEngine().keysPressed.contains(70)) {
            onInteract();
            getLevel().getManager().getEngine().keysPressed.remove(70);
        }
    }


    @Override
    public void onInteract() {
        inventory.setOpen(!inventory.isOpen(), getLevel());
    }

    @Override
    public boolean isInteractable() {
        return getLevel().getPlayer().getCollisionBox().collidesWith(getCollisionBox());
    }
}
