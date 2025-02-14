package core.game.node.entity.player.link.appearance;

import org.json.simple.JSONObject;

public final class BodyPart {
    private int look;
    private int color;

    public BodyPart() {

    }

    public BodyPart(final int look, final int color) {
        this.look = look;
        this.color = color;
    }

    public BodyPart(final int look) {
        this(look, 0);
    }

    public void parse(JSONObject part) {
        changeLook(Integer.parseInt(part.get("look").toString()));
        changeColor(Integer.parseInt(part.get("color").toString()));
    }

    public void changeLook(final int look) {
        this.look = look;
    }

    public void changeColor(final int color) {
        this.color = color;
    }

    public int getLook() {
        return look;
    }

    public int getColor() {
        return color;
    }

}
