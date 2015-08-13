/*
 * Copyright 2013 Indiana University
 * Mark H. Wood, IUPUI University Library, 25-Apr-2013
 */

package edu.iupui.ulib.dspace;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * Build an Ant project from an embedded build file.
 *
 * @author Mark H. Wood
 */
public class Installer
{
    private static final String OPT_HELP = "h";
    private static final String OPT_TARGET = "t";

    public static void main( String[] args )
            throws URISyntaxException, ParseException
    {
        // Collect options
        Options options = new Options();
        options.addOption(OPT_HELP, "help", false, "show this help");
        options.addOption(OPT_TARGET, "target", true, "which named install operation?");

        // Ideas:
        //   read answer file
        //   write answer file
        //   don't really install (may write answer file)
        //
        //   need a list of wizard questions

        CommandLineParser parser = new DefaultParser();
        CommandLine command = parser.parse(options, args);
        if (command.hasOption(OPT_HELP))
        {
            HelpFormatter help = new HelpFormatter();
            help.printHelp("java -jar THIS_JAR", "Installer for DSpace", options, null, true);
            return;
        }

        // Pass installation options to worker
        Map<String,String> choices = new HashMap();
        if (command.hasOption(OPT_TARGET))
            choices.put(OPT_TARGET, command.getOptionValue(OPT_TARGET));

        // Let the worker do the work
        install(choices);
    }

    static void install(Map<String, String> options)
    {
        // Initialize an Ant project from the build file
        Project project = new Project();
        project.setBaseDir(new File(System.getProperty("user.dir"))); // XXX current dir?

        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        project.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, projectHelper);
        URL buildResource = Installer.class.getResource("/build.xml");
        projectHelper.parse(project, buildResource);

        // Configure the project
        project.setProperty("ivySettings",
                    Installer.class.getResource("/ivysettings.xml").toString());

        // Select the target operation

        String target;
        if (options.containsKey(OPT_TARGET))
            target = options.get(OPT_TARGET);
        else
            target = project.getDefaultTarget();

        // Install
        project.executeTarget(target);
    }
}
