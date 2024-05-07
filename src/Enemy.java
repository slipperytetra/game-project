public class Enemy {
    private int damage;
    private int health;
    private int speed;
    private String name;

    public Enemy(String name, int damage, int health, int speed) {
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}