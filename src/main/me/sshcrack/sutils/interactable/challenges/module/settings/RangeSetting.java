package me.sshcrack.sutils.interactable.challenges.module.settings;

import me.sshcrack.sutils.interactable.challenges.module.Challenge;

import java.util.function.Function;

public class RangeSetting extends Setting {
    public RangeSetting(Challenge challenge, String id, Function onClick) {
        super(challenge, id, onClick);
    }
}
