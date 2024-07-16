package entity.utils;

import main.Game;

public enum AttributeTypes {

    ATTACK_DAMAGE(5.0),
    ATTACK_RANGE(Game.BLOCK_SIZE * 2.5),
    ATTACK_RESISTANCE(0.0),
    ATTACK_SPEED(1.0),
    FRICTION(Game.BLOCK_SIZE * 32),
    HEALTH(10.0),
    MAX_HEALTH(10.0),
    MOVEMENT_SPEED(Game.BLOCK_SIZE * 11),
    PROJECTILE_DAMAGE(3.0);

    private double value;

    AttributeTypes(double value) {
        this.value = value;
    }

    public double getDefaultValue() {
        return value;
    }
}
