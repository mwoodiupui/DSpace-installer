/*
 * Copyright 2013 Indiana University
 * Mark H. Wood, IUPUI University Library, 25-Apr-2013
 */

package edu.iupui.ulib.dspace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * Build an Ant project from an embedded build file.
 *
 * @author Mark H. Wood
 */
public class App 
{
    public static void main( String[] args )
	throws Exception // TODO something nicer
    {
	// Initialize an Ant project from the build file
	Project project = new Project();
	ProjectHelper.configureProject(project, getBuildFile());

	// Configure the project
	project.setProperty("ivySettings",
			    App.class.getResource("/ivysettings.xml").toString());

	// Select the target operation
	String target = project.getDefaultTarget(); // TODO parameterize

	// Install
	project.executeTarget(target);
    }

    /**
     * Horrible hack because Ant can't cope with non-File buildfiles.
     * Copy the build.xml from an internal JAR resource to a temporary
     * file.
     *
     * @return path to the scratch copy.
     */
    static private File getBuildfile()
	throws Exception // TODO something nicer
    {
	InputStream configStream = App.class.getResourceAsStream("/build.xml");

	File configFile = File.createTempFile("build", "xml");
	configFile.deleteOnExit();
	FileOutputStream tempStream = new FileOutputStream(configFile);

	while (true)
	    {
		int next = configStream.read();
		if (next < 0) break;
		tempStream.write(next);
	    }

	configStream.close();
	tempStream.close();

	return configFile;
    }
}
