package com.codingame.game;

import com.codingame.gameengine.core.AbstractMultiplayerPlayer;
import com.codingame.gameengine.module.entities.Group;

public class Player extends AbstractMultiplayerPlayer {
    public Group hud;
    
    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public Action getAction() throws TimeoutException, NumberFormatException {
        String[] output = getOutputs().get(0).split(" ");
        if (output.length == 1) return new Action(this, output[0], "");
        else return new Action(this, output[0], output[1]);
    }
}
