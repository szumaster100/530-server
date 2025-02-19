package content.global.skill.summoning.familiar.npc;

import content.data.consumables.Consumables;
import content.global.skill.cooking.data.CookableItem;
import content.global.skill.gathering.fishing.Fish;
import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.consumable.Consumable;
import core.game.interaction.NodeUsageEvent;
import core.game.interaction.UseWithHandler;
import core.game.node.entity.combat.ImpactHandler;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;

@Initializable
public class BunyipNPC extends content.global.skill.summoning.familiar.Familiar {

    private static final int[] FISH = new int[]{317, 327, 3150, 345, 321, 353, 335, 341, 349, 3379, 331, 5004, 359, 10138, 5001, 377, 363, 371, 2148, 7944, 3142, 383, 395, 389, 401, 405, 407};

    private int lastHeal;

    public BunyipNPC() {
        this(null, 6813);
    }

    public BunyipNPC(Player owner, int id) {
        super(owner, id, 4400, 12029, 3, WeaponInterface.STYLE_ACCURATE);
        setLastHeal();
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new BunyipNPC(owner, id);
    }

    @Override
    public void tick() {
        super.tick();
        if (lastHeal < GameWorld.getTicks()) {
            setLastHeal();
            owner.graphics(Graphics.create(1507), 1);
            owner.getSkills().heal(2);
        }
    }

    @Override
    public boolean isPoisonImmune() {
        return true;
    }

    public void setLastHeal() {
        this.lastHeal = GameWorld.getTicks() + (int) (15 / 0.6);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        Fish fish = Fish.forItem(special.getItem());
        Player player = owner;
        if (fish == null) {
            player.sendMessage("You can't use this special on an object like that.");
            return false;
        }
        Consumable consumable = Consumables.getConsumableById(special.getItem().getId() + 2).getConsumable();
        if (consumable == null) {
            player.sendMessage("Error: Report to admin.");
            return false;
        }
        if (player.getSkills().getLevel(Skills.COOKING) < CookableItem.forId(special.getItem().getId()).level) {
            player.sendMessage("You need a Cooking level of at least " + CookableItem.forId(special.getItem().getId()).level + " in order to do that.");
            return false;
        }
        if (player.getInventory().remove(special.getItem())) {
            animate(Animation.create(7747));
            graphics(Graphics.create(1481));
            final int healthEffectValue = consumable.getHealthEffectValue(player);
            if (healthEffectValue > 0) {
                owner.getSkills().heal(consumable.getHealthEffectValue(player));
            } else {
                owner.getImpactHandler().manualHit(player, healthEffectValue, ImpactHandler.HitsplatType.NORMAL);
            }
        }
        return true;
    }

    @Override
    public void visualizeSpecialMove() {
        owner.visualize(Animation.create(7660), Graphics.create(1316));
    }

    @Override
    protected void handleFamiliarTick() {
    }

    @Override
    protected void configureFamiliar() {
        UseWithHandler.addHandler(6813, UseWithHandler.NPC_TYPE, new UseWithHandler(FISH) {
            @Override
            public Plugin<Object> newInstance(Object arg) throws Throwable {
                addHandler(6814, UseWithHandler.NPC_TYPE, this);
                return this;
            }

            @Override
            public boolean handle(NodeUsageEvent event) {
                Player player = event.getPlayer();
                Fish fish = Fish.forItem(event.getUsedItem());
                Consumable consumable = Consumables.getConsumableById(fish.getItem().getId() + 2).getConsumable();
                if (consumable == null) {
                    return true;
                }
                player.lock(1);
                Item runes = new Item(555, RandomFunction.random(1, consumable.getHealthEffectValue(player)));
                if (player.getInventory().remove(event.getUsedItem())) {
                    player.animate(Animation.create(2779));
                    Projectile.create(player, event.getUsedWith().asNpc(), 1435).send();
                    player.getInventory().add(runes);
                    player.sendMessage("The bunyip transmutes the fish into some water runes.");
                }
                return true;
            }
        });
    }

    @Override
    public int[] getIds() {
        return new int[]{6813, 6814};
    }
}
