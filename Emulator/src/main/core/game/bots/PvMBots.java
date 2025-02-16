package core.game.bots;

import content.data.consumables.Consumables;
import core.game.consumable.Consumable;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.prayer.PrayerType;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.tools.RandomFunction;

import java.util.ArrayList;
import java.util.List;

public class PvMBots extends AIPlayer {

    private int tick = 0;

    public PvMBots(Location l) {
        super(l);
    }

    public PvMBots(String copyFromFile, Location l) {
        super(copyFromFile, l);
    }

    public List<Entity> FindTargets(Entity entity, int radius) {
        List<Entity> targets = new ArrayList<>(20);
        Object[] localNPCs = RegionManager.getLocalNpcs(entity, radius).toArray();
        int length = localNPCs.length;
        if (length > 5) {
            length = 5;
        }
        for (int i = 0; i < length; i++) {
            NPC npc = (NPC) localNPCs[i];
            {
                if (checkValidTargets(npc))
                    targets.add(npc);
            }
        }
        if (targets.size() == 0)
            return null;
        return targets;
    }

    public boolean checkValidTargets(NPC target) {
        if (!target.isActive()) {
            return false;
        }
        if (!target.getProperties().isMultiZone() && target.inCombat()) {
            return false;
        }
        if (!target.getDefinition().hasAction("attack")) {
            return false;
        }
        return true;
    }

    public boolean AttackNpcsInRadius(int radius) {
        return AttackNpcsInRadius(this, radius);
    }

    public boolean AttackNpcsInRadius(Player bot, int radius) {
        if (bot.inCombat())
            return true;
        List<Entity> creatures = FindTargets(bot, radius);
        if (creatures == null) {
            return false;
        }
        bot.attack(creatures.get(RandomFunction.getRandom((creatures.size() - 1))));
        if (!creatures.isEmpty()) {
            return true;
        } else {
            creatures = FindTargets(bot, radius);
            if (!creatures.isEmpty()) {
                bot.attack(creatures.get(RandomFunction.getRandom((creatures.size() - 1))));
                return true;
            }
            return false;
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.tick++;

        if (this.getSkills().getLifepoints() <= 5) {
            this.getSkills().setLifepoints(20);
        }

        if (this.tick == 100) this.tick = 0;

    }

    public void CheckPrayer(PrayerType type[]) {
        for (int i = 0; i < type.length; i++)
            this.getPrayer().toggle(type[i]);
    }

    public void eat(int foodId) {
        Item foodItem = new Item(foodId);

        if ((this.getSkills().getStaticLevel(Skills.HITPOINTS) >= this.getSkills().getLifepoints() * 3) && this.getInventory().containsItem(foodItem)) {
            this.lock(3);
            Item food = this.getInventory().getItem(foodItem);

            Consumable consumable = Consumables.getConsumableById(food.getId()).getConsumable();

            if (consumable == null) {
                return;
            }

            consumable.consume(food, this);
            this.getProperties().getCombatPulse().delayNextAttack(3);
        }
        if (this.checkVictimIsPlayer() == false)
            if (!(this.getInventory().contains(foodId, 1)))
                this.getInventory().add(new Item(foodId, 5));
    }
}
