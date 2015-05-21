package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.jbehave.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jbehave.core.annotations.When;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AdminScriptSteps {

    private static final String SCRIPT_RESOURCE_BASE = "/scripts/administration";
    private static final String SCRIPT_RESOURCE_OS_PROFILE = SCRIPT_RESOURCE_BASE + "/register-osprofile.py";
    private static final String SCRIPT_RESOURCE_HW_PROFILE = SCRIPT_RESOURCE_BASE + "/register-hwprofile.py";

    @When("I want to register os-profile $name/$version using register script with following XML:$xmlContent")
    public void executeRegisterOsProfileScript(String name, String version, String xmlContent)
            throws IOException, InterruptedException, URISyntaxException {

        File scriptFile = extractAdminScript(SCRIPT_RESOURCE_OS_PROFILE);

        File metadataFile = new File(scriptFile.getParent(), "metadata.xml");
        metadataFile.deleteOnExit();
        FileUtils.writeStringToFile(metadataFile, xmlContent);

        URI serviceBaseAddress = new URI(Settings.getServiceBaseAddress().toString());

        ProcessBuilder builder = new ProcessBuilder()
                .command(
                        "python",
                        scriptFile.getCanonicalPath(),
                        "-f", metadataFile.getCanonicalPath(),
                        name,
                        State.currentAuthToken == null ? "\"\"" : State.currentAuthToken,
                        "-v", version,
                        "-s", serviceBaseAddress.getHost() + ":" + serviceBaseAddress.getPort())
                .redirectErrorStream(true);

        String command = StringUtils.join(builder.command(), " ");
        System.out.println("Running command: " + command);

        Process process = builder.start();

        IOUtils.copy(process.getInputStream(), System.out);
        process.waitFor();

        assertThat("script exit code", process.exitValue(), is(0));
    }

    @When("I want to register hw-profile named $name using register script with following XML:$xmlContent")
    public void executeRegisterHwProfileScript(String name, String xmlContent)
            throws IOException, InterruptedException, URISyntaxException {

        File scriptFile = extractAdminScript(SCRIPT_RESOURCE_HW_PROFILE);

        File metadataFile = new File(scriptFile.getParent(), "metadata.xml");
        metadataFile.deleteOnExit();
        FileUtils.writeStringToFile(metadataFile, xmlContent);

        URI serviceBaseAddress = new URI(Settings.getServiceBaseAddress().toString());

        ProcessBuilder builder = new ProcessBuilder()
                .command(
                        "python",
                        scriptFile.getCanonicalPath(),
                        "-f", metadataFile.getCanonicalPath(),
                        name,
                        State.currentAuthToken == null ? "\"\"" : State.currentAuthToken,
                        "-s", serviceBaseAddress.getHost() + ":" + serviceBaseAddress.getPort())
                .redirectErrorStream(true);

        String command = StringUtils.join(builder.command(), " ");
        System.out.println("Running command: " + command);

        Process process = builder.start();

        IOUtils.copy(process.getInputStream(), System.out);
        process.waitFor();

        assertThat("script exit code", process.exitValue(), is(0));
    }


    private File extractAdminScript(String scriptResource) throws IOException {
        File temp = new File(System.getProperty("java.io.tmpdir"));
        temp = new File(temp, UUID.randomUUID().toString());
        temp.deleteOnExit();

        return extractResource(temp, scriptResource);
    }

    private File extractResource(File destination, String resourceName) throws IOException {
        String fileName = resourceName.substring(resourceName.lastIndexOf('/') + 1);
        File file = new File(destination, fileName);
        file.deleteOnExit();

        InputStream input = null;
        OutputStream output = null;

        try {
            input = AdminScriptSteps.class.getResourceAsStream(resourceName);
            if (input == null) {
                throw new RuntimeException("Resource '" + resourceName + "' does not exist.");
            }
            output = FileUtils.openOutputStream(file);

            IOUtils.copy(input, output);
        } finally {
            if (input != null) {
                IOUtils.closeQuietly(input);
            }
            if (output != null) {
                IOUtils.closeQuietly(output);
            }
        }

        return file;
    }
}
