package com.github.ginafro1.fpelotracker.elotracking;

import com.github.ginafro1.fpelotracker.EloTracker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class SetUsernameCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "setuser";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + " <username>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length < 1){
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please input a username"));
        }
        EloTracker.playerName = args[0];
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully set your username to " + args[0]));
    }
}
