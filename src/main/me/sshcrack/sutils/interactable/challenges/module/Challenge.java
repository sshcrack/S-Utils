package me.sshcrack.sutils.interactable.challenges.module;

import me.sshcrack.sutils.interactable.toggable.ToggleableListener;


public abstract class Challenge extends ToggleableListener {

    public Challenge(String id, Properties properties) {
        super(id, properties);
    }

    @Override
    public String getRoot() {
        return String.format("challenges.%s", getId());
    }
}
