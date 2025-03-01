package content.region.kandarin.quest.mcannon.handlers;

public enum DMCRevolution {

    NORTH(515) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY > 0 && Math.abs(offsetX) <= offsetY;
        }
    },

    NORTH_EAST(516) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY > 0 && offsetX > 0;
        }
    },

    EAST(517) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetX > 0 && Math.abs(offsetY) <= offsetX;
        }
    },

    SOUTH_EAST(518) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY < 0 && offsetX > 0;
        }
    },

    SOUTH(519) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY < 0 && Math.abs(offsetX) <= -offsetY;
        }
    },

    SOUTH_WEST(520) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY < 0 && offsetX < 0;
        }
    },

    WEST(521) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetX < 0 && Math.abs(offsetY) <= -offsetX;
        }
    },

    NORTH_WEST(514) {
        @Override
        public boolean isInSight(int offsetX, int offsetY) {
            return offsetY > 0 && offsetX < 0;
        }
    };

    private final int animationId;

    DMCRevolution(int animationId) {
        this.animationId = animationId;
    }

    public int getAnimationId() {
        return animationId;
    }

    public boolean isInSight(int offsetX, int offsetY) {
        return false;
    }
}