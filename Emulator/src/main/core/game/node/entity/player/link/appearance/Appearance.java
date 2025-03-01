package core.game.node.entity.player.link.appearance;

import core.cache.def.impl.ItemDefinition;
import core.cache.def.impl.NPCDefinition;
import core.game.container.impl.EquipmentContainer;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.game.world.update.flag.context.Animation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class Appearance {
    private final Player player;
    private final int[] animationCache = new int[]{AppearanceCache.STAND_ANIM, AppearanceCache.STAND_TURN_ANIM, AppearanceCache.WALK_ANIM, AppearanceCache.TURN_180_AIM, AppearanceCache.TURN_90_CW, AppearanceCache.TUNR_90_CWW, AppearanceCache.RUN_ANIM};
    private final int[] iconCache = new int[]{-1, -1};
    private final int[] bodyParts = new int[14];
    private BodyPart[] appearanceCache = Gender.MALE.generateCache();
    private Gender gender = Gender.MALE;
    private int npcId = -1;
    private int renderAnimationId = 1426;
    private boolean ridingMinecart;

    public Appearance(final Player player) {
        this.player = player;
    }

    public void parse(JSONObject appearance) {
        gender = gender.asByte(Byte.parseByte(appearance.get("gender").toString()));
        JSONArray appCache = (JSONArray) appearance.get("appearance_cache");
        for (int i = 0; i < appearanceCache.length; i++) {
            (appearanceCache[i]).parse((JSONObject) appCache.get(i));
        }
    }

    public void transformNPC(int id) {
        this.npcId = id;
        setAnimations();
        if (id == -1) {
            player.setSize(1);
            Animation[] anims = WeaponInterface.DEFAULT_ANIMS;
            if (player.getEquipment().get(3) != null) {
                anims = player.getEquipment().get(3).getDefinition().getConfiguration(ItemConfigParser.ATTACK_ANIMS, anims);
            }
            int index = player.getSettings().getAttackStyleIndex();
            if (index < 0 || index >= anims.length) {
                index = 0;
            }
            player.getProperties().setAttackAnimation(anims[index]);
            player.getProperties().setDefenceAnimation(new Animation(404));
            player.getProperties().setDeathAnimation(new Animation(9055, Priority.HIGH));
        } else {
            NPCDefinition def = NPCDefinition.forId(id);
            player.setSize(def.size);
            if (def.getCombatAnimation(0) != null) {
                player.getProperties().setAttackAnimation(def.getCombatAnimation(0));
            }
            if (def.getCombatAnimation(3) != null) {
                player.getProperties().setDefenceAnimation(def.getCombatAnimation(3));
            }
            if (def.getCombatAnimation(4) != null) {
                player.getProperties().setDeathAnimation(def.getCombatAnimation(4));
            }
        }
        sync();
    }

    public void setAnimations() {
        if (npcId == -1) {
            Item weapon = player.getEquipment().get(3);
            if (isRidingMinecart()) {
                this.setRidingMinecart(false);
            }
            if (weapon == null || weapon.getDefinition().getConfiguration(ItemConfigParser.WALK_ANIM, null) == null) {
                setDefaultAnimations();
            } else {
                ItemDefinition def = weapon.getDefinition();
                setStandAnimation(def.getConfiguration(ItemConfigParser.STAND_ANIM, 0x328));
                setStandTurnAnimation(def.getConfiguration(ItemConfigParser.STAND_TURN_ANIM, 0x337));
                setWalkAnimation(def.getConfiguration(ItemConfigParser.WALK_ANIM, 0x333));
                setRunAnimation(def.getConfiguration(ItemConfigParser.RUN_ANIM, 0x338));
                setTurn180(def.getConfiguration(ItemConfigParser.TURN180_ANIM, 0x334));
                setTurn90cw(def.getConfiguration(ItemConfigParser.TURN90CW_ANIM, 0x335));
                setTurn90ccw(def.getConfiguration(ItemConfigParser.TURN90CCW_ANIM, 0x336));
                renderAnimationId = def.getRenderAnimationId();
            }
            if (weapon != null && weapon.getId() == 12842) {
                setStandAnimation(8964);
                setWalkAnimation(8961);
                setRunAnimation(8963);
                setTurn180(8963);
                setTurn90ccw(8963);
                setTurn90cw(8963);
                setStandTurnAnimation(8963);
            }
        } else {
            NPCDefinition def = NPCDefinition.forId(npcId);
            renderAnimationId = def.renderAnimationId;
            setStandAnimation(def.standAnimation);
            int turn = def.getTurnAnimation();
            if (turn < 1) {
                turn = def.walkAnimation;
            }
            setStandTurnAnimation(turn);
            setWalkAnimation(def.walkAnimation);
            setRunAnimation(def.walkAnimation);
            if (def.getTurn180Animation() > 0) {
                setTurn180(def.getTurn180Animation());
            } else {
                setTurn180(turn);
            }
            if (def.getTurnCWAnimation() > 0) {
                setTurn90cw(def.getTurnCWAnimation());
            } else {
                setTurn90cw(turn);
            }
            if (def.getTurnCCWAnimation() > 0) {
                setTurn90ccw(def.getTurnCCWAnimation());
            } else {
                setTurn90ccw(turn);
            }
        }
    }

    public void setAnimations(Animation anim) {
        renderAnimationId = anim.getId();
        sync();
    }

    public void sync() {
        player.updateAppearance();
    }

    public void copy(Appearance appearance) {
        gender = appearance.gender;
        for (int i = 0; i < appearanceCache.length; i++) {
            appearanceCache[i] = appearance.appearanceCache[i];
        }
        for (int i = 0; i < animationCache.length; i++) {
            animationCache[i] = appearance.animationCache[i];
        }
        renderAnimationId = appearance.renderAnimationId;
    }

    public void drawItem(int part, Item item) {
        this.bodyParts[part] = item.getDefinition().getEquipId() + 0x8000;
    }

    public void drawClothes(int part, int clothesId) {
        this.bodyParts[part] = clothesId + 0x100;
    }

    public void clearBodyPart(int part) {
        this.bodyParts[part] = 0;
    }

    public void flagHatClipping() {
        boolean isBald = getHair().getLook() == (isMale() ? 0 : 45);
        int hairLook = isBald ? getHair().getLook() : (isMale() ? 5 : 51);
        this.bodyParts[8] = hairLook + 0x100;
    }

    public void prepareBodyData(Player player) {
        if (player.getRenderInfo().preparedAppearance()) {
            return;
        }
        player.getRenderInfo().setPreparedAppearance(true);
        Item chest = player.getEquipment().get(EquipmentContainer.SLOT_CHEST);
        Item shield = player.getEquipment().get(EquipmentContainer.SLOT_SHIELD);
        Item legs = player.getEquipment().get(EquipmentContainer.SLOT_LEGS);
        Item hat = player.getEquipment().get(EquipmentContainer.SLOT_HAT);
        Item hands = player.getEquipment().get(EquipmentContainer.SLOT_HANDS);
        Item feet = player.getEquipment().get(EquipmentContainer.SLOT_FEET);
        Item cape = player.getEquipment().get(EquipmentContainer.SLOT_CAPE);
        Item amulet = player.getEquipment().get(EquipmentContainer.SLOT_AMULET);
        Item weapon = player.getEquipment().get(EquipmentContainer.SLOT_WEAPON);
        boolean castleWarsHood = cape != null && (cape.getId() == 4041 || cape.getId() == 4042); // Item
        if (hat != null) {
            drawItem(0, hat);
        } else {
            clearBodyPart(0);
        }
        if (cape != null) {
            drawItem(1, cape);
        } else {
            clearBodyPart(1);
        }
        if (amulet != null) {
            drawItem(2, amulet);
        } else {
            clearBodyPart(2);
        }
        if (!ridingMinecart) {
            if (weapon != null) {
                drawItem(3, weapon);
            } else {
                clearBodyPart(3);
            }
            if (shield != null) {
                drawItem(5, shield);
            } else {
                clearBodyPart(5);
            }
        } else {
            clearBodyPart(5);
            drawClothes(3, 82);
        }
        if (chest != null) {
            drawItem(4, chest);
        } else {
            drawClothes(4, getTorso().getLook());
        }
        if (chest != null && chest.getDefinition().getConfiguration(ItemConfigParser.REMOVE_SLEEVES, false)) {
            clearBodyPart(6);
        } else {
            drawClothes(6, getArms().getLook());
        }
        if (legs != null) {
            drawItem(7, legs);
        } else {
            drawClothes(7, getLegs().getLook());
        }
        if ((hat != null && hat.getDefinition().getConfiguration(ItemConfigParser.REMOVE_HEAD, false)) || castleWarsHood) {
            clearBodyPart(8);
        } else {
            drawClothes(8, getHair().getLook());
        }
        if ((hat != null && hat.getDefinition().getConfiguration(ItemConfigParser.IS_HAT, false))) {
            flagHatClipping();
        }
        if (hands != null) {
            drawItem(9, hands);
        } else {
            drawClothes(9, getWrists().getLook());
        }
        if (feet != null) {
            drawItem(10, feet);
        } else {
            drawClothes(10, getFeet().getLook());
        }
        if (hat != null && hat.getDefinition().getConfiguration(ItemConfigParser.REMOVE_BEARD, false)) {
            clearBodyPart(11);
        } else {
            drawClothes(11, getBeard().getLook());
        }

    }

    public void rideCart(boolean ride) {
        if (!ride) {
            setAnimations();
        } else {
            player.getAppearance().setAnimations(Animation.create(211));
        }
        player.getAppearance().setRidingMinecart(ride);
        player.getAppearance().sync();
    }

    public void flyCopter() {
        player.animate(Animation.create(8956));
        player.getEquipment().replace(new Item(12842), 3);
        player.getAppearance().setStandAnimation(8964);
        player.getAppearance().setWalkAnimation(8961);
        player.getAppearance().setRunAnimation(8963);
        player.getAppearance().setTurn180(8963);
        player.getAppearance().setTurn90ccw(8963);
        player.getAppearance().setTurn90cw(8963);
        player.getAppearance().setStandTurnAnimation(8963);
    }

    public Player getPlayer() {
        return player;
    }

    public BodyPart getHair() {
        return appearanceCache[AppearanceCache.HAIR];
    }

    public BodyPart getBeard() {
        return appearanceCache[AppearanceCache.FACIAL_HAIR];
    }

    public BodyPart getTorso() {
        return appearanceCache[AppearanceCache.TORSO];
    }

    public BodyPart getArms() {
        return appearanceCache[AppearanceCache.ARMS];
    }

    public BodyPart getWrists() {
        return appearanceCache[AppearanceCache.WRISTS];
    }

    public BodyPart getSkin() {
        return appearanceCache[AppearanceCache.WRISTS];
    }

    public BodyPart getLegs() {
        return appearanceCache[AppearanceCache.LEGS];
    }

    public BodyPart getFeet() {
        return appearanceCache[AppearanceCache.FEET];
    }

    public int getStandAnimation() {
        return animationCache[0];
    }

    public void setStandAnimation(int animation) {
        animationCache[0] = animation;
    }

    public int getStandTurnAnimation() {
        return animationCache[1];
    }

    public void setStandTurnAnimation(int animation) {
        animationCache[1] = animation;
    }

    public int getWalkAnimation() {
        return animationCache[2];
    }

    public void setWalkAnimation(int animation) {
        animationCache[2] = animation;
    }

    public int getTurn180() {
        return animationCache[3];
    }

    public void setTurn180(int animation) {
        animationCache[3] = animation;
    }

    public int getTurn90cw() {
        return animationCache[4];
    }

    public void setTurn90cw(int animation) {
        animationCache[4] = animation;
    }

    public int getTurn90ccw() {
        return animationCache[5];
    }

    public void setTurn90ccw(int animation) {
        animationCache[5] = animation;
    }

    public int getRunAnimation() {
        return animationCache[6];
    }

    public void setRunAnimation(int animation) {
        animationCache[6] = animation;
    }

    public int getRenderAnimation() {
        if (player.getAttribute("render-anim-override") != null)
            return player.getAttribute("render-anim-override", renderAnimationId);
        return renderAnimationId;
    }

    public void setDefaultAnimations() {
        for (int i = 0; i < animationCache.length; i++) {
            animationCache[i] = AppearanceCache.ANIMATIONS[i];
        }
        renderAnimationId = 1426;
    }

    public int getSkullIcon() {
        return iconCache[0];
    }

    public void setSkullIcon(int icon) {
        iconCache[0] = icon;
    }

    public int getHeadIcon() {
        return iconCache[1];
    }

    public void setHeadIcon(int icon) {
        this.iconCache[1] = icon;
    }

    public Gender getGender() {
        return gender;
    }

    public void changeGender(Gender gender) {
        setGender(gender);
        sync();
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
        this.appearanceCache = gender.generateCache();
    }

    public boolean isMale() {
        return gender == Gender.MALE;
    }

    public int getNpcId() {
        return npcId;
    }

    public boolean isNpc() {
        return npcId != -1;
    }

    public BodyPart[] getAppearanceCache() {
        return appearanceCache;
    }

    public int[] getAnimationCache() {
        return animationCache;
    }

    public int[] getBodyParts() {
        return bodyParts;
    }

    public boolean isRidingMinecart() {
        return ridingMinecart;
    }

    public void setRidingMinecart(boolean ridingMinecart) {
        this.ridingMinecart = ridingMinecart;
    }

    public static class AppearanceCache {

        public static final int[] ANIMATIONS = new int[]{0x328, 0x337, 0x333, 0x334, 0x335, 0x336, 0x338};

        public static final int HAIR = 0;

        public static final int FACIAL_HAIR = 1;

        public static final int TORSO = 2;

        public static final int ARMS = 3;

        public static final int WRISTS = 4;

        public static final int LEGS = 5;

        public static final int FEET = 6;

        public static final int HAIR_COLOR = 0;

        public static final int TORSO_COLOR = 1;

        public static final int LEG_COLOR = 2;

        public static final int FEET_COLOR = 3;

        public static final int SKIN_COLOR = 4;

        private static final int STAND_ANIM = 0x328;

        private static final int STAND_TURN_ANIM = 0x337;

        public static final int WALK_ANIM = 0x333;

        private static final int TURN_180_AIM = 0x334;

        private static final int TURN_90_CW = 0x335;

        private static final int TUNR_90_CWW = 0x336;

        private static final int RUN_ANIM = 0x338;

    }

}
