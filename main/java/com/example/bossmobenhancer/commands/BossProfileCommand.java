package com.example.bossmobenhancer.commands;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BossProfileCommand extends CommandBase {

    @Override
    public String getName() {
        return "bossprofile";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bossprofile <ProfileName>";
    }

    /**
     * Executes the command, setting the boss scaling profile.
     *
     * @param server the Minecraft server instance
     * @param sender the command sender (typically a player or console)
     * @param args   the provided command arguments
     * @throws CommandException if an error occurs during command execution
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /bossprofile <ProfileName>"));
            return;
        }

        String name = args[0];
        if (!ScalingProfileLoader.profiles.containsKey(name)) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Unknown profile: " + name));
            return;
        }

        ConfigHandler.difficultyPreset = name;
        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Boss scaling profile set to "
                + TextFormatting.AQUA + name));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Operator-only command
    }

    /**
     * Provides tab-completion suggestions for available profile names.
     *
     * @param server    the Minecraft server instance
     * @param sender    the command sender
     * @param args      the currently entered command arguments
     * @param targetPos the block position target (if applicable)
     * @return a list of matching profile names
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        // Provide suggestions when the user is typing the first argument.
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            String typed = args[0].toLowerCase();
            for (Map.Entry<String, ?> entry : ScalingProfileLoader.profiles.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(typed)) {
                    suggestions.add(entry.getKey());
                }
            }
            return suggestions;
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}