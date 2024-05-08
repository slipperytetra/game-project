package main;

public enum PlayerAction {

    JUMP(0),
    MOVE_UP(1),
    MOVE_DOWN(-1),
    MOVE_LEFT(-1),
    MOVE_RIGHT(1),
    ATTACK(0);

    public int actionValue;
    PlayerAction(int actionValue) {
        this.actionValue = actionValue;
    }

    public int getActionValue() {
        return actionValue;
    }
}
