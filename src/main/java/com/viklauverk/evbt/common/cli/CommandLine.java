package com.viklauverk.evbt.common.cli;

import java.io.File;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;

public class CommandLine
{
    public static Log log = LogModule.lookup("evbt.commandline", CommandLine.class);

    private AvailableCommands available_commands_;
    private CommandSequence command_sequence_;

    public CommandLine()
    {
        available_commands_ = new AvailableCommands();
        command_sequence_ = new CommandSequence();
    }

    public void addCommand(Command c)
    {
        available_commands_.add(c);
    }

    public AvailableCommands availableCommands()
    {
        return available_commands_;
    }

    public CommandSequence parse(String[] args)
    {
        command_sequence_ = new CommandSequence();
        Environment env = new Environment(command_sequence_);

        for (int i = 0; i < args.length; ++i)
        {
            String s = args[i];
            Command ct = available_commands_.lookup(s);
            if (ct == null) return noSuchCommand(s);

            CommandWithArguments cwa = new CommandWithArguments(ct);
            i += cwa.parseExtras(args, i+1);
            command_sequence_.addCommandWithArguments(cwa);
        }
        return command_sequence_;
    }

    static CommandSequence moreArgsNeeded()
    {
        System.out.println("More arguments needed!");
        return null;
    }

    static CommandSequence expected(String keyword)
    {
        System.out.println("Expected \""+keyword+"\"");
        return null;
    }

    static CommandSequence noSuchCommand(String s)
    {
        log.info("prauk: no such command \"%s\"", s);
        return null;
    }

    static CommandSequence tooManyArguments()
    {
        System.out.println("Too many arguments!");
        return null;
    }

}
