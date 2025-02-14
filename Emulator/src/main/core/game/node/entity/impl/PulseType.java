package core.game.node.entity.impl;

public enum PulseType {
    // Standard pulse slot, should be interrupted/replaced by most things.
    STANDARD,
    // Enforces itself as the only that can run.
    STRONG,
    // Custom slots for extra tasks that should run alongside standard tasks.
    CUSTOM_1,

    CUSTOM_2
}
