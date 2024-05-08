import java.awt.*;

public class NPCS extends GameEngine {
    String type;

    private Image plantMonster;

    @Override
    public Image loadImage(String filename) {
        //filename = "resources/images/plantMonster.png";
        return super.loadImage(filename);
    }

    public NPCS(String type){
        this.type = type;

    }



    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {

    }


}
