package content.global.skill.summoning.familiar;

import content.global.skill.summoning.SummoningPouch;
import content.global.skill.summoning.pet.Pet;
import content.global.skill.summoning.pet.PetDetails;
import content.global.skill.summoning.pet.Pets;
import core.cache.def.impl.ItemDefinition;
import core.game.component.Component;
import core.game.container.Container;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.world.map.Location;
import core.game.world.map.zone.ZoneRestriction;
import core.game.world.update.flag.context.Animation;
import core.tools.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.*;

public final class FamiliarManager {

    private static final Map<Integer, Familiar> FAMILIARS = new HashMap<>();
    private final Map<Integer, PetDetails> petDetails = new HashMap<Integer, PetDetails>();
    private final Player player;
    private Familiar familiar;
    private int summoningCombatLevel;
    private boolean hasPouch;

    public FamiliarManager(Player player) {
        this.player = player;
    }

    public final void parse(JSONObject familiarData) {
        int currentPet = -1;
        if (familiarData.containsKey("currentPet")) {
            currentPet = Integer.parseInt(familiarData.get("currentPet").toString());
        }
        JSONArray petDetails = (JSONArray) familiarData.get("petDetails");
        for (int i = 0; i < petDetails.size(); i++) {
            JSONObject detail = (JSONObject) petDetails.get(i);
            PetDetails details = new PetDetails(0);
            details.updateHunger(Double.parseDouble(detail.get("hunger").toString()));
            details.updateGrowth(Double.parseDouble(detail.get("growth").toString()));
            int itemIdHash = Integer.parseInt(detail.get("petId").toString());
            if (detail.containsKey("stage")) {
                int babyItemId = itemIdHash;
                int itemId = babyItemId;
                int stage = Integer.parseInt(detail.get("stage").toString());
                if (stage > 0) {
                    Pets pets = Pets.forId(babyItemId);
                    itemId = pets.getNextStageItemId(itemId);
                    if (stage > 1) {
                        itemId = pets.getNextStageItemId(itemId);
                    }
                }
                Item item = new Item(itemId);
                item.setCharge(1000);
                itemIdHash = item.getIdHash();
                if (currentPet != -1 && currentPet == babyItemId) {
                    currentPet = itemIdHash;
                }
            }
            this.petDetails.put(itemIdHash, details);
        }

        if (currentPet != -1) {
            PetDetails details = this.petDetails.get(currentPet);
            int itemId = currentPet >> 16 & 0xFFFF;
            Pets pets = Pets.forId(itemId);
            if (details == null) {
                details = new PetDetails(pets.getGrowthRate() == 0.0 ? 100.0 : 0.0);
                this.petDetails.put(currentPet, details);
            }
            familiar = new Pet(player, details, itemId, pets.getNpcId(itemId));
        } else if (familiarData.containsKey("familiar")) {
            JSONObject currentFamiliar = (JSONObject) familiarData.get("familiar");
            int familiarId = Integer.parseInt(currentFamiliar.get("originalId").toString());
            familiar = FAMILIARS.get(familiarId).construct(player, familiarId);
            familiar.ticks = Integer.parseInt(currentFamiliar.get("ticks").toString());
            familiar.specialPoints = Integer.parseInt(currentFamiliar.get("specialPoints").toString());
            JSONArray famInv = (JSONArray) currentFamiliar.get("inventory");
            if (famInv != null) {
                ((BurdenBeast) familiar).container.parse(famInv);
            }
            familiar.setAttribute("hp", Integer.parseInt(currentFamiliar.get("lifepoints").toString()));
        }
    }

    public void login() {
        if (hasFamiliar()) {
            familiar.init();
        }
        player.getFamiliarManager().setConfig(243269632);
    }

    public void summon(Item item, boolean pet, boolean deleteItem) {
        boolean renew = false;
        if (hasFamiliar()) {
            if (familiar.getPouchId() == item.getId()) {
                renew = true;
            } else {
                player.getPacketDispatch().sendMessage("You already have a follower.");
                return;
            }
        }
        if (player.getZoneMonitor().isRestricted(ZoneRestriction.FOLLOWERS) && !player.getLocks().isLocked("enable_summoning")) {
            player.getPacketDispatch().sendMessage("You are standing in a Summoning-free area. You must move out of this area to" + "summon a familiar.");
            return;
        }
        if (pet) {
            summonPet(item, deleteItem);
            return;
        }
        final SummoningPouch pouch = SummoningPouch.get(item.getId());
        if (pouch == null) {
            return;
        }
        if (player.getSkills().getStaticLevel(Skills.SUMMONING) < pouch.getRequiredLevel()) {
            player.getPacketDispatch().sendMessage("You need a Summoning level of " + pouch.getRequiredLevel() + " to summon this familiar.");
            return;
        }
        if (player.getSkills().getLevel(Skills.SUMMONING) < pouch.getSummonCost()) {
            player.getPacketDispatch().sendMessage("You need at least " + pouch.getSummonCost() + " Summoning points to summon this familiar.");
            return;
        }
        final int npcId = pouch.getNpcId();
        Familiar fam = !renew ? FAMILIARS.get(npcId) : familiar;
        if (fam == null) {
            player.getPacketDispatch().sendMessage("Nothing interesting happens.");
            log(this.getClass(), Log.ERR, "Invalid familiar: " + npcId + ".");
            return;
        }
        if (!renew) {
            fam = fam.construct(player, npcId);
            if (fam.getSpawnLocation() == null) {
                player.getPacketDispatch().sendMessage("The spirit in this pouch is too big to summon here. You will need to move to a larger");
                player.getPacketDispatch().sendMessage("area.");
                return;
            }
        }
        if (!player.getInventory().remove(item)) {
            return;
        }
        player.getSkills().updateLevel(Skills.SUMMONING, -pouch.getSummonCost(), 0);
        player.getSkills().addExperience(Skills.SUMMONING, pouch.getSummonExperience());
        if (!renew) {
            familiar = fam;
            spawnFamiliar();
        } else {
            familiar.refreshTimer();
        }
        player.getAppearance().sync();
    }

    public void summon(final Item item, boolean pet) {
        summon(item, pet, true);
    }

    public void morphPet(final Item item, boolean deleteItem, Location location) {
        if (hasFamiliar()) {
            familiar.dismiss();
        }
        summonPet(item, deleteItem, true, location);
    }

    private boolean summonPet(final Item item, boolean deleteItem) {
        return summonPet(item, deleteItem, false, null);
    }

    private boolean summonPet(final Item item, boolean deleteItem, boolean morph, Location location) {
        final int itemId = item.getId();
        int itemIdHash = item.getIdHash();
        if (itemId > 8850 && itemId < 8900) {
            return false;
        }
        Pets pets = Pets.forId(itemId);
        if (pets == null) {
            return false;
        }
        if (player.getSkills().getStaticLevel(Skills.SUMMONING) < pets.getSummoningLevel()) {
            player.getDialogueInterpreter().sendDialogue("You need a summoning level of " + pets.getSummoningLevel() + " to summon this.");
            return false;
        }

        ArrayList<Integer> taken = new ArrayList<Integer>();
        Container[] searchSpace = {player.getInventory(), player.getBankPrimary(), player.getBankSecondary()};
        for (int checkId = pets.getBabyItemId(); checkId != -1; checkId = pets.getNextStageItemId(checkId)) {
            Item check = new Item(checkId, 1);
            for (Container container : searchSpace) {
                for (Item i : container.getAll(check)) {
                    taken.add(i.getCharge());
                }
            }
        }
        PetDetails details = petDetails.get(itemIdHash);
        int individual = item.getCharge();
        if (details != null) {

            details.setIndividual(individual);
            int count = 0;
            for (int i : taken) {
                if (i == individual) {
                    count++;
                }
            }
            if (count > 1) {

                details = null;
            }
        }
        if (details == null) {
            details = new PetDetails(pets.getGrowthRate() == 0.0 ? 100.0 : 0.0);
            for (individual = 0; taken.contains(individual) && individual < 0xFFFF; individual++) {
            }
            details.setIndividual(individual);

            Item newItem = item.copy();
            newItem.setCharge(individual);
            petDetails.put(newItem.getIdHash(), details);
        }
        int npcId = pets.getNpcId(itemId);
        if (npcId > 0) {
            familiar = new Pet(player, details, itemId, npcId);
            if (deleteItem) {
                player.animate(new Animation(827));

                int slot = player.getInventory().getSlotHash(item);

                player.getInventory().remove(item, slot, true);
            }
            if (morph) {
                morphFamiliar(location);
            } else {
                spawnFamiliar();
            }
            return true;
        }
        return true;
    }

    public void morphFamiliar(Location location) {
        familiar.init(location, false);
        player.getInterfaceManager().openTab(new Component(662));
        player.getInterfaceManager().setViewedTab(7);
    }

    public void spawnFamiliar() {
        familiar.init();
        player.getInterfaceManager().openTab(new Component(662));
        player.getInterfaceManager().setViewedTab(7);
    }

    public void eat(int foodId, Pet npc) {
        if (npc != familiar) {
            player.getPacketDispatch().sendMessage("This isn't your pet!");
            return;
        }
        Pet pet = (Pet) familiar;
        Pets pets = Pets.forId(pet.getItemId());
        if (pets == null) {
            return;
        }
        for (int food : pets.getFood()) {
            if (food == foodId) {
                player.getInventory().remove(new Item(foodId));
                player.getPacketDispatch().sendMessage("Your pet happily eats the " + ItemDefinition.forId(food).getName() + ".");
                player.animate(new Animation(827));
                npc.getDetails().updateHunger(-15.0);
                return;
            }
        }
        player.getPacketDispatch().sendMessage("Nothing interesting happens.");
    }

    public void pickup() {
        if (player.getInventory().freeSlots() == 0) {
            player.getPacketDispatch().sendMessage("You don't have enough room in your inventory.");
            return;
        }
        Pet pet = ((Pet) familiar);
        PetDetails details = pet.getDetails();
        Item petItem = new Item(pet.getItemId());
        petItem.setCharge(details.getIndividual());
        if (player.getInventory().add(petItem)) {
            petDetails.put(pet.getItemIdHash(), details);
            player.animate(Animation.create(827));
            player.getFamiliarManager().dismiss();
        }
    }

    public void adjustBattleState(final BattleState state) {
        if (!hasFamiliar()) {
            return;
        }
        familiar.adjustPlayerBattle(state);
    }

    public int getBoost(int skill) {
        if (!hasFamiliar()) {
            return 0;
        }
        return familiar.getBoost(skill);
    }

    public boolean hasFamiliar() {
        return familiar != null;
    }

    public boolean hasPet() {
        return hasFamiliar() && familiar instanceof Pet;
    }

    public void dismiss() {
        if (hasFamiliar()) {
            familiar.dismiss();
        }
    }

    public void removeDetails(int itemIdHash) {
        petDetails.remove(itemIdHash);
    }

    public boolean isOwner(Familiar familiar) {
        if (!hasFamiliar()) {
            return false;
        }
        if (this.familiar != familiar) {
            player.getPacketDispatch().sendMessage("This is not your familiar.");
            return false;
        }
        return true;
    }

    public void setConfig(int value) {
        int current = getVarp(player, 1160);
        int newVal = current + value;
        setVarp(player, 1160, newVal);
    }

    public Familiar getFamiliar() {
        return familiar;
    }

    public void setFamiliar(Familiar familiar) {
        this.familiar = familiar;
    }

    public static Map<Integer, Familiar> getFamiliars() {
        return FAMILIARS;
    }

    public boolean isUsingSummoning() {
        return hasPouch || (hasFamiliar() && !hasPet());
    }

    public boolean isHasPouch() {
        return hasPouch;
    }

    public void setHasPouch(boolean hasPouch) {
        this.hasPouch = hasPouch;
    }

    public int getSummoningCombatLevel() {
        return summoningCombatLevel;
    }

    public void setSummoningCombatLevel(int summoningCombatLevel) {
        this.summoningCombatLevel = summoningCombatLevel;
    }

    public Map<Integer, PetDetails> getPetDetails() {
        return petDetails;
    }
}
