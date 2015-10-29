package com.metapatrol.gitlab.ci.runner;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.metapatrol.gitlab.ci.runner.commands.AbstractBaseCommand;
import com.metapatrol.gitlab.ci.runner.commands.GitlabCIRunnerCommand;
import com.metapatrol.gitlab.ci.runner.commands.RegisterCommand;
import com.metapatrol.gitlab.ci.runner.commands.StartCommand;
import com.metapatrol.gitlab.ci.runner.entrypoint.EntryPoint;
import com.metapatrol.gitlab.ci.runner.entrypoint.GitlabCIRunnerEntryPoint;
import com.metapatrol.gitlab.ci.runner.entrypoint.RegisterEntryPoint;
import com.metapatrol.gitlab.ci.runner.entrypoint.StartEntryPoint;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class Launcher {

    static {
        //String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} [%-5p] %C{1}#%M():%L %m%n";
        String PATTERN = "%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n";

        FileSystem.getInstance().getLogDirectory();
        ConsoleAppender console = new ConsoleAppender(); //create appender
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.INFO);
        console.activateOptions();

        FileAppender fileAppender = new FileAppender();
        fileAppender.setFile(new File(FileSystem.getInstance().getLogDirectory(), "gitlab-ci-runner.log").getAbsolutePath());
        fileAppender.setThreshold((Level.INFO);
        fileAppender.setLayout(new PatternLayout(PATTERN));
        fileAppender.activateOptions();

        Logger.getLogger("com.google").setLevel(Level.ERROR);
        Logger.getLogger("com.beust").setLevel(Level.ERROR);
        Logger.getLogger("org.eclipse").setLevel(Level.ERROR);
        Logger.getLogger("org.apache").setLevel(Level.ERROR);
        Logger.getLogger("org.springframework").setLevel(Level.ERROR);
        Logger.getLogger("com.spotify.docker").setLevel(Level.ERROR);
        Logger.getLogger("com.metapatrol.gitlab.ci.runner.client").setLevel(Level.ERROR);

        Logger.getRootLogger().addAppender(console);
        Logger.getRootLogger().addAppender(fileAppender);
    }

    private static Map<String, CommandHolder> holderMap = new HashMap<String, CommandHolder>();

    public static void main(String[] args){
        JCommander jCommander = new JCommander();

        GitlabCIRunnerCommand gitlabCIRunnerCommand = new GitlabCIRunnerCommand();
        StartCommand startCommand = new StartCommand();
        RegisterCommand registerCommand = new RegisterCommand();

        jCommander = addCommand(
            jCommander
        ,   "gitlab-ci-runner"
        , gitlabCIRunnerCommand
        );
        holderMap.put("gitlab-ci-runner", new CommandHolder("gitlab-ci-runner", jCommander, gitlabCIRunnerCommand, new GitlabCIRunnerEntryPoint()));

        JCommander jCommanderStart = addCommand(
            jCommander
        ,   "start"
        ,   startCommand
        );
        holderMap.put("start", new CommandHolder("start", jCommanderStart, startCommand, new StartEntryPoint()));

        JCommander jCommanderRegister = addCommand(
            jCommander
        ,   "register"
        ,   registerCommand
        );
        holderMap.put("register", new CommandHolder("register", jCommanderRegister, registerCommand, new RegisterEntryPoint()));

        try {
            jCommander.parse(args);
        }catch (ParameterException parameterException){
            System.err.println(parameterException.getMessage());
            System.err.println();

            jCommander.usage();
            System.exit(1);
        }

        String parsedCommand = jCommander.getParsedCommand();
        if(gitlabCIRunnerCommand.isHelp() || parsedCommand == null){
            jCommander.usage();
            System.exit(1);
        }

        CommandHolder commandHolder = holderMap.get(parsedCommand);
        if(commandHolder == null || commandHolder.command.isHelp()){
            jCommander.usage();
            System.exit(1);
        }

        if(!EntryPoint.EntryPointInvoker.get(commandHolder.entryPoint).invoke(commandHolder.command)){
            System.exit(1);
        }
    }

    private static JCommander addCommand(JCommander parentCommand, String commandName, Object commandObject) {
        parentCommand.addCommand(commandName, commandObject);
        return parentCommand.getCommands().get(commandName);
    }

    protected static class CommandHolder{
        protected String name;
        protected JCommander jCommander;
        protected AbstractBaseCommand command;
        protected EntryPoint<?> entryPoint;
        protected CommandHolder(String name, JCommander jCommander, AbstractBaseCommand command, EntryPoint<?> entryPoint){
            this.name = name;
            this.jCommander = jCommander;
            this.command = command;
            this.entryPoint = entryPoint;
        }
    }
}
