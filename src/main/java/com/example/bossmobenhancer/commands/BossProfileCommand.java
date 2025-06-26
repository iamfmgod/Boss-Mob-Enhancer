package com.example.bossmobenhancer.commands;

import com.example.bossmobenhancer.config.ConfigHandler;
import com.example.bossmobenhancer.data.ScalingProfileLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BossProfileCommand extends CommandBase {
    @Override
    public String getName() {
        return "bossprofile";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/bossprofile <ProfileName>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;  // operator-only
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String profile = args[0];
        if (!ScalingProfileLoader.profiles.containsKey(profile)) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Unknown profile: " + TextFormatting.AQUA + profile
            ));
            return;
        }

        // Update config, save, then apply in-memory
        ConfigHandler.difficultyPreset = profile;
        ConfigHandler.saveConfig();
        ScalingProfileLoader.applyProfile(profile);

        sender.sendMessage(new TextComponentString(
                TextFormatting.GREEN + "Boss profile set to " + TextFormatting.AQUA + profile
        ));
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            @Nullable BlockPos targetPos
    ) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return ScalingProfileLoader.profiles.keySet().stream()
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}