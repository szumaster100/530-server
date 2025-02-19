package content.global.skill.slayer.npc;

import content.global.skill.slayer.Tasks;
import core.game.interaction.NodeUsageEvent;
import core.game.interaction.UseWithHandler;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.ImpactHandler.HitsplatType;
import core.game.node.entity.npc.AbstractNPC;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.plugin.ClassScanner;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Items;

import static core.api.ContentAPIKt.sendMessage;

@Initializable
public class GargoyleNPC extends AbstractNPC {

    public GargoyleNPC(int id, Location location) {
        super(id, location);
    }

    public GargoyleNPC() {
        super(0, null);
    }

    @Override
    public void checkImpact(BattleState state) {
        super.checkImpact(state);
        int lp = getSkills().getLifepoints();
        
        if (state.getEstimatedHit() > -1) {
            if (lp - state.getEstimatedHit() < 1) {
                state.setEstimatedHit(0);
                if (lp > 1) {
                    state.setEstimatedHit(lp - 1);
                }
            }
        }
        
        if (state.getSecondaryHit() > -1) {
            if (lp - state.getSecondaryHit() < 1) {
                state.setSecondaryHit(0);
                if (lp > 1) {
                    state.setSecondaryHit(lp - 1);
                }
            }
        }

        int totalHit = state.getEstimatedHit() + state.getSecondaryHit();
        if (lp - totalHit < 1) {
            state.setEstimatedHit(0);
            state.setSecondaryHit(0);
        }
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ClassScanner.definePlugin(new RockHammerHandler());
        return super.newInstance(arg);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new GargoyleNPC(id, location);
    }

    @Override
    public int[] getIds() {
        return Tasks.GARGOYLES.getNpcs();
    }

    public class RockHammerHandler extends UseWithHandler {

        public RockHammerHandler() {
            super(Items.ROCK_HAMMER_4162);
        }

        @Override
        public Plugin<Object> newInstance(Object arg) {
            for (int id : Tasks.GARGOYLES.getNpcs()) {
                addHandler(id, NPC_TYPE, this);
            }
            return this;
        }

        @Override
        public boolean handle(NodeUsageEvent event) {
            Player player = event.getPlayer();
            NPC npc = (NPC) event.getUsedWith();
            if (npc.getSkills().getLifepoints() > 10) {
                sendMessage(player, "The gargoyle isn't weak enough to be harmed by the hammer.");
            } else {
                sendMessage(player, "The gargoyle cracks apart.");
                npc.getImpactHandler().manualHit(player, npc.getSkills().getLifepoints(), HitsplatType.NORMAL);
            }
            return true;
        }
    }
}
