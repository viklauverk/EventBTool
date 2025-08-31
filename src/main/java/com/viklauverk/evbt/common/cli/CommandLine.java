package com.viklauverk.evbt.common.cli;

import java.io.File;

import com.viklauverk.evbt.common.log.Log;
import com.viklauverk.evbt.common.log.LogModule;

public class CommandLine
{
    public static Log log = LogModule.lookup("evbt.commandline", CommandLine.class);

    private Commands commands_;

    public CommandLine()
    {
        commands_ = new Commands();
    }

    public void addCommand(Command c)
    {
        commands_.add(c);
    }

    public Config parse(String[] args)
    {
        Config cfg = new Config();
        Environment env = new Environment(cfg);

        for (int i = 0; i < args.length; ++i)
        {
            String s = args[i];
            Command ct = commands_.lookup(s);
            if (ct == null) return noSuchCommand(s);

            CommandWithArguments cwa = new CommandWithArguments(ct);
            i += cwa.parseExtras(args, i);
            cfg.addCommandWithArguments(cwa);
        }
        return cfg;
    }

    static Config moreArgsNeeded()
    {
        System.out.println("More arguments needed!");
        return null;
    }

    static Config expected(String keyword)
    {
        System.out.println("Expected \""+keyword+"\"");
        return null;
    }

    static Config noSuchCommand(String s)
    {
        log.info("sprauk: no such command \"%s\"", s);
        return null;
    }

    static Config tooManyArguments()
    {
        System.out.println("Too many arguments!");
        return null;
    }

}
