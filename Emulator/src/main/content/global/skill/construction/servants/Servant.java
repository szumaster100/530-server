package content.global.skill.construction.servants;

import core.game.node.entity.npc.NPC;
import core.game.node.item.Item;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

public final class Servant extends NPC {

    private final ServantType type;

    private Item item;

    private int uses;

    private boolean greet;

    public Servant(ServantType type) {
        super(type.getNpcId());
        this.type = type;
    }

    public void save(ByteBuffer buffer) {
        buffer.put((byte) type.ordinal());
        buffer.putShort((byte) uses);
        if (item == null) {
            buffer.putShort((short) -1);
        } else {
            buffer.putShort((short) item.getId());
            buffer.putInt(item.getAmount());
        }
        buffer.put((byte) (greet ? 1 : 0));
    }

    public static Servant parse(JSONObject data) {
        int type = Integer.parseInt(data.get("type").toString());
        Servant servant = new Servant(ServantType.values()[type]);
        servant.uses = Integer.parseInt(data.get("uses").toString());
        Object itemRaw = data.get("item");
        if (itemRaw != null) {
            JSONObject item = (JSONObject) itemRaw;
            servant.item = new Item(Integer.parseInt(item.get("id").toString()), Integer.parseInt(item.get("amount").toString()));
        }
        servant.greet = (boolean) data.get("greet");
        return servant;
    }

    public static Servant parse(ByteBuffer buffer) {
        int type = buffer.get();
        if (type == -1) {
            return null;
        }
        Servant servant = new Servant(ServantType.values()[type]);
        servant.uses = buffer.getShort() & 0xFFFF;
        int itemId = buffer.getShort() & 0xFFFF;
        if ((short) itemId != -1) {
            servant.item = new Item(itemId, buffer.getInt());
        }
        servant.greet = buffer.get() == 1;
        return servant;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public boolean isGreet() {
        return greet;
    }

    public void setGreet(boolean greet) {
        this.greet = greet;
    }

    public ServantType getType() {
        return type;
    }
}