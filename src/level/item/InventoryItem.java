package level.item;

public class InventoryItem {

    private ItemType itemType;
    private int amount;

    public InventoryItem(ItemType type, int amount) {
        this.itemType = type;
        this.amount = amount;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            amount = 0;
        }

        this.amount = amount;
    }

    public void incrementAmount(int amount) {
        setAmount(getAmount() + amount);
    }
}
