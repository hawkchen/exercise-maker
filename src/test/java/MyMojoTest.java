import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.*;
import org.zkoss.training.ExcerciseMakerMojo;

import java.io.*;

/**
 * http://maven.apache.org/components/plugin-testing/maven-plugin-testing-harness/getting-started/index.html
 */
public class MyMojoTest{
    public static final String SRC_TEST_POM_XML = "src/test/pom.xml";

    //a wrapper for an embedded {@link AbstractMojoTestCase}
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * smoke test, test if plugin loads the configuration correctly
     * @throws Exception
     */
    @Test
    public void config() throws Exception {
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        Assert.assertNotNull(makerMojo);
        Assert.assertEquals("src/test", makerMojo.getSourceDirectory().getPath());
        Assert.assertEquals("src/exercise", makerMojo.getOutputDirectory().getPath());
    }

    /**
     * specify a range like 3-5
     * @throws Exception
     */
    @Test
    public void range() throws Exception{
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        makerMojo.execute();
        File exerciseFile = new File(makerMojo.getOutputDirectory(), "webapp/mvvm/hello.zul");
        Assert.assertTrue(exerciseFile.getAbsolutePath() ,exerciseFile.exists());
        Assert.assertEquals(13, countLineNumber(exerciseFile));
    }

    /**
     * invalid syntax: specify a character instead of line number
     * @throws Exception
     */
    @Test
    public void notaLineNumber() throws Exception{
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        makerMojo.execute();
        File exerciseFile = new File(makerMojo.getOutputDirectory(), "webapp/mvvm/invalidSyntax.zul");
        Assert.assertTrue(exerciseFile.getAbsolutePath() ,exerciseFile.exists());
        Assert.assertEquals(14, countLineNumber(exerciseFile));
    }

    /**
     * specify a range with 1 number e.g. 3
     * @throws Exception
     */
    @Test
    public void oneNumber() throws Exception{
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        makerMojo.execute();
        File exerciseFile = new File(makerMojo.getOutputDirectory(), "webapp/resources/style.css");
        Assert.assertEquals(6, countLineNumber(exerciseFile));
    }

    @Test
    public void combinedUsage() throws Exception{
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        makerMojo.execute();
        File exerciseFile = new File(makerMojo.getOutputDirectory(), "java/org/zkoss/training/HelloVM.java");
        Assert.assertTrue(exerciseFile.getAbsolutePath() ,exerciseFile.exists());
        Assert.assertEquals(19, countLineNumber(exerciseFile));
    }

    @Test
    public void invalid() throws Exception{
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo)rule.lookupMojo(ExcerciseMakerMojo.DEFAULT_GOAL, new File(SRC_TEST_POM_XML));
        makerMojo.execute();
        File exerciseFile = new File(makerMojo.getOutputDirectory(), "java/org/zkoss/training/InvalidVM.java");
        Assert.assertFalse(exerciseFile.getAbsolutePath() ,exerciseFile.exists());
    }


    /**
     * test parameter default value.
     * MojoRule.lookupConfiguredMojo() returns a ExcerciseMakerMojo filled with default values.
     * @throws Exception
     */
    @Test
    public void defaultParameter() throws Exception {
        String pomFolder = "src/test/resources/default";
        ExcerciseMakerMojo makerMojo = (ExcerciseMakerMojo) rule.lookupConfiguredMojo(new File(pomFolder),
                ExcerciseMakerMojo.DEFAULT_GOAL);
        Assert.assertTrue(makerMojo.getSourceDirectory().getPath().endsWith(pomFolder + "/src/main"));
        Assert.assertTrue(makerMojo.getOutputDirectory().getPath().endsWith(pomFolder + ExcerciseMakerMojo.EXERCISE_OUTPUT_PATH));
    }

    private int countLineNumber(File file) throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        reader.skip(Integer.MAX_VALUE);
        return reader.getLineNumber()+1;
    }

}