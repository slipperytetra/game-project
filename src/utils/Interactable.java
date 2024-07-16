package utils;

import main.GameObject;

public interface Interactable {

    void interact();
    void onInteract();
    boolean isInteractable();
    double getRange();

}
