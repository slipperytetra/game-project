//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Player {
    private Location playerLoc = new Location(0.0, 0.0);
    private boolean isMoving;
    private boolean isFlipped;
    private boolean isJumping;
    private boolean isAttacking;
    private boolean keyObtained;
    private boolean doorTouched;
    private boolean attackRegistered = false;
    private final double JUMP_VELOCITY = -15.0;

    public Player() {
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public boolean isFlipping() {
        return this.isFlipped;
    }

    public void setFlipping(boolean isFlipped) {
        this.isFlipped = isFlipped;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    public boolean isAttacking() {
        return this.isAttacking;
    }

    public void setAttacking(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }

    public boolean hasObtainedKey() {
        return this.keyObtained;
    }

    public void setKeyObtained(boolean keyObtained) {
        this.keyObtained = keyObtained;
    }

    public boolean isTouchingDoor() {
        return this.doorTouched;
    }

    public void setTouchingDoor(boolean doorTouched) {
        this.doorTouched = doorTouched;
    }

    public boolean hasRegisteredAttack() {
        return this.attackRegistered;
    }

    public void setAttackRegistered(boolean attackRegistered) {
        this.attackRegistered = attackRegistered;
    }

    public void update() {
    }

    public Location getLocation() {
        return this.playerLoc;
    }

    public double getJumpVelocity() {
        return -15.0;
    }
}
