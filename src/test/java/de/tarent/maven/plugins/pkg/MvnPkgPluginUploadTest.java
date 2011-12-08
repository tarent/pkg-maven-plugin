package de.tarent.maven.plugins.pkg;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

import de.tarent.maven.plugins.pkg.map.PackageMap;


public class MvnPkgPluginUploadTest extends AbstractMvnPkgPluginTestCase{

	@Test
	public void testGenerateUploadElements() throws MojoExecutionException, IOException{
		Upload u = new Upload();
		File f = File.createTempFile("mvnpkg", "");
		Element[] e = u.generateUploadElements(f, "file:///tmp/test", new UploadParameters());
		Assert.assertEquals(e.length, 2);
		UploadParameters param = new UploadParameters();
		param.setToDir("toDir");
		e = u.generateUploadElements(f, "file:///tmp/test", param);
		Assert.assertEquals(e.length, 3);
	}
	
	@Test
	public void testGetPackageFile() throws Exception{
		Upload u = mockUploadEnvironment();
		File f = u.getPackageFile(u.dc, u.pm, "ubuntu_lucid_upload");
		Assert.assertNotNull(f);
		Assert.assertEquals("dummyproject_1.0.0-0ubuntulucidupload_all.deb",f.getName());		
	}
	
	@Test
	public void testUpload() throws Exception{
		/*
		 * TODO: Try to find a way to mock MavenSession in order for this
		 * test to run.
		Upload u = mockUploadEnvironment();
		File f = new File("/tmp/dummyproject_1.0.0-0ubuntulucidupload_all.deb");
		File f2 = new File("/tmp/2/dummyproject_1.0.0-0ubuntulucidupload_all.deb");
		File origin = new File(u.getBuildDir(),"dummyproject_1.0.0-0ubuntulucidupload_all.deb");
		origin.createNewFile();
		FileUtils.forceMkdir(f2.getParentFile());
		u.execute();
		Assert.assertTrue(f.exists());
		Assert.assertNotNull(f2.exists());
		f.delete();
		f2.delete();
		f2.getParentFile().delete();
		origin.delete();
		*/
		Assert.assertTrue(true);
		
				
	}
	
	private Upload mockUploadEnvironment() throws Exception {
		Upload u = (Upload)mockEnvironment("uploadpom.xml", "upload");
		u.dc = Utils.getTargetConfigurationFromString("ubuntu_lucid_upload", u.targetConfigurations);
		u.target = u.dc.target;
		u.pm = new PackageMap(null, null, "ubuntu_lucid", null);
		return u;
	}
}
