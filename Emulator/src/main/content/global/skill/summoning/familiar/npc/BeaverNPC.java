package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import content.global.skill.summoning.familiar.Forager;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.SkillBonus;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Direction;
import core.game.world.map.path.Pathfinder;
import core.game.world.update.flag.context.Animation;
import core.plugin.Initializable;
import core.tools.RandomFunction;

@Initializable
public class BeaverNPC extends Forager {

    private static final Item[] ITEMS = new Item[]{new Item(1511), new Item(2862), new Item(1521), new Item(1519), new Item(6333), new Item(10810), new Item(1517), new Item(6332), new Item(12581), new Item(960), new Item(8778)};

    private static final String[] TREE_NAMES = new String[]{"Tree", "Oak", "Hollow", "Willow", "Arctic pine", "Eucalyptus", "Maple", "Yew", "Magic", "Cursed magic"};

    private boolean multiChop;

    public BeaverNPC() {
        this(null, 6808);
    }

    public BeaverNPC(Player owner, int id) {
        super(owner, id, 2700, 12021, 6, ITEMS);
        boosts.add(new SkillBonus(Skills.WOODCUTTING, 2));
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new BeaverNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        final Scenery object = (Scenery) special.getNode();
        if (!isTree(object.getName())) {
            owner.getPacketDispatch().sendMessages("This scroll only works on naturally growing, oak, willow, arctic pine", "teak, mahogany, maple, yew, and magic trees.");
            return false;
        }
        if (owner.getInventory().freeSlots() == 0) {
            return false;
        }
        if (object.getLocation().getDistance(getLocation()) > 5) {
            owner.getPacketDispatch().sendMessages("The beaver is a little too far from the tree for the scroll to work - stand", "closer.");
            return false;
        }
        Direction dir = Direction.getLogicalDirection(getLocation(), object.getLocation());
        Pathfinder.find(getLocation(), object.getLocation().transform(dir)).walk(this);
        final int ticks = 2 + (int) Math.floor(owner.getLocation().getDistance(object.getLocation().transform(dir)) * 0.5);
        owner.lock(ticks);
        multiChop = true;
        getPulseManager().clear();
        GameWorld.getPulser().submit(new Pulse(ticks, owner, this) {
            @Override
            public boolean pulse() {
                lock(11);
                owner.lock(11);
                faceLocation(object.getLocation());
                animate(Animation.create(7722));
                GameWorld.getPulser().submit(new Pulse(1, owner, BeaverNPC.this) {
                    int counter;
                    boolean recieved = false;

                    @Override
                    public boolean pulse() {
                        switch (++counter) {
                            default:
                                if (counter > 3) {
                                    if (RandomFunction.random(12) < 4) {
                                        owner.getInventory().add(ITEMS[RandomFunction.random(ITEMS.length)], owner);
                                        recieved = true;
                                    }
                                }
                                break;
                            case 11:
                                if (!recieved) {
                                    owner.getInventory().add(ITEMS[RandomFunction.random(ITEMS.length)], owner);
                                }
                                multiChop = false;
                                return true;
                        }
                        return false;
                    }
                });
                return true;
            }
        });
        return true;
    }

    @Override
    public void startFollowing() {
        if (multiChop) {
            return;
        }
        super.startFollowing();
    }

    private boolean isTree(final String name) {
        for (String s : TREE_NAMES) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{6808};
    }

}
