package de.tarent.maven.plugins.pkg;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MvnPkgPluginPackagingTest extends AbstractMvnPkgPluginTestCase {

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute the default target set on the configuration section of the pom
	 * file (ubuntu_lucid_target_simple) Use the only distribution defined for
	 * this target Create a DEB file Include a main artifact (JAR) in the DEB
	 * file
	 * 
	 * @throws Exception
	 */
	@Test
	public void runDefaultTarget() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertFalse(debIsSigned());
	}

	/**
	 * Runs the plugin against a configuration where two target configurations
	 * have a single common parent.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runTwoTargetsDependingOnSharedThird() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_sharedparent1,ubuntu_lucid_target_sharedparent2");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(2));
		assertTrue(debContainsMainArtifact());
		assertFalse(debIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute the default target set on the configuration section of the pom
	 * file (ubuntu_lucid_target_simple) Use the only distribution defined for
	 * this target Create a DEB file Include a main artifact (JAR) in the DEB
	 * file
	 * 
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void runAgainstUnkownTargetThrowsException() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"non_existent_target");
		packagingPlugin.execute();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which itself has parent configuration Use
	 * the only distribution defined for this target Create a DEB file Include a
	 * main artifact (JAR) in the DEB file At the end of the packaging process
	 * the directory pkg-tmp should not exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDebForUbuntuLucidWithoutDependenciesContainingJar()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertFalse(debIsSigned());
		assertEquals(0, returnFilesBasedOnFilename("pkg-tmp").length);
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which itself has parent configuration Use
	 * the only distribution defined for this target Create a DEB file with a
	 * specific revision Include a main artifact (JAR) in the DEB file At the
	 * end of the packaging process the directory pkg-tmp should not exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDebForUbuntuLucidWithoutDependenciesContainingJarWithRevision()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple_with_revision");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertFalse(debIsSigned());
		assertEquals(0, returnFilesBasedOnFilename("pkg-tmp").length);
		assertTrue(debRevisionIs("22")); // Magic number from DEBPOM
	}

	@Test
	public void createDebForUbuntuLucidKeepPkgTmpDir() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple");
		packagingPlugin.setKeepPkgTmp(true);
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		File pkgtmp = returnFilesBasedOnFilename("pkg-tmp")[0];
		assertTrue(pkgtmp.exists());
		FileUtils.deleteDirectory(pkgtmp);
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration for RPM without license
	 * 
	 * This test should fail.
	 * 
	 */
	@Test(expected = MojoExecutionException.class)
	public void createRpmForCentOS_5_6WithoutLicense() throws Exception {

		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_simple");
		packagingPlugin.project.setLicenses(null);
		packagingPlugin.execute();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration without parent Use the only distribution
	 * defined for this target Create a RPM file Include a main artifact (JAR)
	 * in the RPM file
	 * 
	 */
	@Test
	public void createRpmForCentOS_5_6WithoutDependenciesContainingJar()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertFalse(rpmIsSigned());
		assertTrue(rpmReleaseIs("1")); // No revision is the pom, so the rpm
										// equivalent "release" should be 1
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration without parent Use the only distribution
	 * defined for this target Create a RPM file with a specific release number
	 * Include a main artifact (JAR) in the RPM file
	 * 
	 */
	@Test
	public void createRpmForCentOS_5_6WithoutDependenciesContainingJarWithSpecificRevision()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_simple_with_revision");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertFalse(rpmIsSigned());
		assertTrue(rpmReleaseIs("22")); // Magic number comes from RPMPOM
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration without parent Use the only distribution
	 * defined for this target Create a IPK file Include a main artifact (JAR)
	 * in the RPM file
	 * 
	 */
	@Test
	@Ignore
	public void createIpkForOpenmokoWithoutDependenciesContainingJar()
			throws Exception {

		packagingPlugin = mockPackagingEnvironment(IPKPOM,
				"openmoko_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfIPKsIs(1));
		assertTrue(ipkContainsMainArtifact());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration without parent Use the only distribution
	 * defined for this target Try to create a RPM file without PackageName,
	 * Version, Description, Summary, License and Release.
	 * 
	 * This test should fail.
	 * 
	 */
	@Test(expected = MojoExecutionException.class)
	public void createRpmForCentOS_5_6WithoutNeededInformationFails()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_simple", false);
		packagingPlugin.execute();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which itself has parent configuration
	 * which contains manually added dependencies Use the only distribution
	 * defined for this target Create a DEB file Include a main artifact (JAR)
	 * in the DEB file
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDebForUbuntuLucidWithManualDependenciesContainingJar()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_manual_dependencies");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertTrue(debDependsOn("blackbox"));
		assertFalse(debIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which contains manually added dependencies
	 * Use the only distribution defined for this target
	 * 
	 * Create a RPM file Include a main artifact (JAR) in the RPM file
	 * 
	 * @throws Exception
	 */
	@Test
	public void createRpmForCentOS_5_6WithManualDependenciesContainingJar()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_manual_dependencies");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(rpmDependsOn("blackbox"));
		assertFalse(rpmIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which contains manually added dependencies
	 * Use the only distribution defined for this target
	 * 
	 * Create a IPK file Include a main artifact (JAR) in the IPK file
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void createIpkForOpenmokoWithManualDependenciesContainingJar()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(IPKPOM,
				"openmoko_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfIPKsIs(1));
		assertTrue(ipkContainsMainArtifact());
		assertTrue(ipkDependsOn("blackbox"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a target configuration which itself has parent configuration
	 * which contains two distributions, but no default
	 * 
	 * Create a DEB file Include a main artifact (JAR) in the DEB file
	 * 
	 * This test should fail.
	 * 
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void createDebForUbuntuKarmicOverridingDistroWithoutDependenciesContainingJarShouldFail()
			throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_double");
		packagingPlugin.execute();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute two different target configurations Create a DEB and a RPM file
	 * Include a main artifact (JAR) in the DEB file Include a main artifact
	 * (JAR) in the RPM file
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingMultipleTargetsWithoutDependenciesContainingJar()
			throws Exception, MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(MIXEDPOM,
				"ubuntu_lucid_target_simple,centos_5_6_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(debContainsMainArtifact());
		assertFalse(rpmIsSigned());
		assertFalse(debIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target configuration Create a RPM file Include a main
	 * artifact (JAR) in the RPM file Include external auxilary files in the
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	public void createRPMWithAuxFileDependenciesContainingJar()
			throws Exception, MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_external_artifact");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(rpmContainsArtifact("dummy.properties"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target configuration Create a RPM file Include a main
	 * artifact (JAR) in the RPM file Include external auxilary SBinFiles in the
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	public void createRPMSBinFileContainingJar() throws Exception,
			MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_external_sbinfile");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(rpmContainsArtifact("/sbin/dummy.properties"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target configuration Create a DEB file Include a main
	 * artifact (JAR) in the DEB file Include external auxilary files in the
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDEBWithAuxFileDependenciesContainingJar()
			throws Exception, MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_external_artifact");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertTrue(debContainsArtifact("dummy.properties"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target configuration Create a DEB file Include a main
	 * artifact (JAR) in the DEB file Include external auxilary SBinfiles in the
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	public void createDEBWithSBinFileContainingJar() throws Exception,
			MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_external_sbinfile");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertTrue(debContainsArtifact("/sbin/dummy.properties"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target configuration Create a IPK file Include a main
	 * artifact (JAR) in the IPK file Include external auxilary files in the
	 * package
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void createIpkWithAuxFileDependenciesContainingJar()
			throws Exception, MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(IPKPOM,
				"openmoko_target_external_artifact");
		packagingPlugin.execute();
		assertTrue(numberOfIPKsIs(1));
		assertTrue(ipkContainsMainArtifact());
		assertTrue(ipkContainsArtifact("dummy.properties"));
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target Create a signed DEB file
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void createSignedDEB() throws Exception, MojoExecutionException {
		CLEANTARGETDIRECTORYAFTERRUN = false;
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_sign");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertTrue(debIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target Create a signed DEB file
	 * 
	 * @throws Exception
	 */
	@Test
	public void createNotSignedDEB() throws Exception, MojoExecutionException {

		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(debContainsMainArtifact());
		assertFalse(debIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute two targets that would result in the final package file of the
	 * first to be overwritten.
	 * 
	 * This test should fail.
	 * 
	 * @throws Exception
	 */
	@Test(expected = MojoExecutionException.class)
	public void abortIfPackagesWouldOverwrite() throws Exception,
			MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_simple,ubuntu_lucid_target_simple");
		packagingPlugin.execute();
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target Create a signed DEB file
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore("At this time we are unable to automatically let Jenkins enter the passphrase for this key")
	public void createSignedRPM() throws Exception, MojoExecutionException {

		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_sign");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(rpmIsSigned());
	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute a single target Create a signed DEB file
	 * 
	 * @throws Exception
	 */
	@Test
	public void createNotSignedRPM() throws Exception, MojoExecutionException {

		packagingPlugin = mockPackagingEnvironment(RPMPOM,
				"centos_5_6_target_simple");
		packagingPlugin.execute();
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertFalse(rpmIsSigned());

	}

	/**
	 * This test attempts the following:
	 * 
	 * Execute two different target configurations Create a DEB and a RPM file
	 * Include a main artifact (JAR) in the DEB file Include a main artifact
	 * (JAR) in the RPM file
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void executingMultipleSignedTargetsWithoutDependenciesContainingJar()
			throws Exception, MojoExecutionException {
		packagingPlugin = mockPackagingEnvironment(MIXEDPOM,
				"ubuntu_lucid_target_sign,centos_5_6_target_sign");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(1));
		assertTrue(numberOfRPMsIs(1));
		assertTrue(rpmContainsMainArtifact());
		assertTrue(debContainsMainArtifact());
		assertTrue(rpmIsSigned());
		assertTrue(debIsSigned());
	}

	public Packaging mockPackagingEnvironment(String pomFilename)
			throws Exception {
		return (Packaging) mockEnvironment(pomFilename, "pkg", true);
	}

	public Packaging mockPackagingEnvironment(String pomFilename, String target)
			throws Exception {
		return (Packaging) mockEnvironment(pomFilename, "pkg", true, target);
	}

	private Packaging mockPackagingEnvironment(String pomFilename,
			String target, boolean b) throws Exception {
		return (Packaging) mockEnvironment(pomFilename, "pkg", b, target);
	}

	/**
	 * Runs a package phase with a target configuration which contains a
	 * relation. This means that there must be two binary packages in the end.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runTargetWithARelation() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_relation2");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(2));
		assertTrue(debDependsOn("libdummyproject-data-java",
				"libdummyproject-core-java_1.0.0_all.deb"));
	}

	/**
	 * Runs a package phase with a target configuration which contains a
	 * relation containing a relation. This means that there must be
	 * <em>three</em> binary packages in the end.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runTargetWithARelationWithARelation() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_relation3a");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(3));
		assertTrue(debDependsOn("libdummyproject-core-java",
				"libdummyproject-config-foo-java_1.0.0_all.deb"));
		assertTrue(debDependsOn("libdummyproject-data-java",
				"libdummyproject-core-java_1.0.0_all.deb"));
	}

	/**
	 * Runs a package phase with a target configuration which contains two
	 * relations.
	 * 
	 * This means that there must be <em>three</em> binary packages in the end.
	 * 
	 * @throws Exception
	 */
	@Test
	public void runTargetWithTwoRelations() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_relation3b");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(3));
		assertTrue(debDependsOn("libdummyproject-data-java",
				"libdummyproject-config-bar-java_1.0.0_all.deb"));
		assertTrue(debDependsOn("libdummyproject-core-java",
				"libdummyproject-config-bar-java_1.0.0_all.deb"));
		assertTrue(debDependsOn("libdummyproject-data-java",
				"libdummyproject-core-java_1.0.0_all.deb"));
	}

	/**
	 * This test checks for regressions on the logic that merges dependencies
	 * 
	 * @throws Exception
	 */
	@Test
	public void runTwoTargetsAndDontMixDependencies() throws Exception {
		packagingPlugin = mockPackagingEnvironment(DEBPOM,
				"ubuntu_lucid_target_sharedparent1,ubuntu_lucid_target_sharedparent2");
		packagingPlugin.execute();
		assertTrue(numberOfDEBsIs(2));
		assertFalse(debDependsOn("dependencysharedparent1",
				"libdummyproject-sharedparent2-java_1.0.0_all.deb"));
		assertTrue(debDependsOn("dependencysharedparent2",
				"libdummyproject-sharedparent2-java_1.0.0_all.deb"));
		assertFalse(debDependsOn("dependencysharedparent2",
				"libdummyproject-sharedparent1-java_1.0.0_all.deb"));
		assertTrue(debDependsOn("dependencysharedparent1",
				"libdummyproject-sharedparent1-java_1.0.0_all.deb"));
	}

}
