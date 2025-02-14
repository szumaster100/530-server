package core.game.node.entity.player.link.appearance;

public enum Gender {

    MALE(new BodyPart[]{new BodyPart(0), new BodyPart(10), new BodyPart(18), new BodyPart(26), new BodyPart(33), new BodyPart(36), new BodyPart(42)}),

    FEMALE(new BodyPart[]{new BodyPart(45), new BodyPart(1000), new BodyPart(56), new BodyPart(61), new BodyPart(68), new BodyPart(70), new BodyPart(79)});

    private final BodyPart[] appearanceCache;

    Gender(final BodyPart[] appearanceCache) {
        this.appearanceCache = appearanceCache;
    }

    public BodyPart[] generateCache() {
        final BodyPart[] cache = new BodyPart[appearanceCache.length];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new BodyPart(appearanceCache[i].getLook());
        }
        return cache;
    }

    public BodyPart[] getAppearanceCache() {
        return appearanceCache;
    }

    public byte toByte() {
        return (byte) (this == MALE ? 0 : 1);
    }

    public Gender asByte(byte value) {
        return value == 0 ? MALE : FEMALE;
    }

}