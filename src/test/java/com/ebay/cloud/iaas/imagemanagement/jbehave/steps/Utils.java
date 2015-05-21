package com.ebay.cloud.iaas.imagemanagement.jbehave.steps;

import com.ebay.cloud.iaas.imagemanagement.jbehave.Settings;
import com.ebay.cloud.iaas.imagemanagement.service.*;
import com.ebay.cloud.services.security.SecurityServiceException;
import com.ebay.cloud.services.security.consumers.EsamsChannelEnum;
import com.ebay.cloud.services.security.consumers.IAFTokenManager;
import com.google.common.base.Function;
import com.google.common.collect.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.jbehave.core.model.ExamplesTable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import static com.ebay.cloud.iaas.imagemanagement.TestUtils.matchesRegexp;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public final class Utils {

    private static final String HW_PROFILE_RESOURCE = "/testhwprofiles/%s/";
    private static final String HW_PROFILE_PROPERTIES_RESOURCE = HW_PROFILE_RESOURCE + "properties.table";

    private static final String OS_PROFILE_RESOURCE = "/testosprofiles/%s/%s/";
    private static final String OS_PROFILE_HW_PROFILES_RESOURCE = OS_PROFILE_RESOURCE + "hw-profiles.table";
    private static final String OS_PROFILE_PROPERTIES_RESOURCE = OS_PROFILE_RESOURCE + "properties.table";
    private static final String OS_PROFILE_FILES_RESOURCE = OS_PROFILE_RESOURCE + "files.table";

    private static final String OS_PROFILE_FILE_INDEX_RESOURCE = OS_PROFILE_RESOURCE + "files.index";
    private static final String OS_PROFILE_FILE_RESOURCE = OS_PROFILE_RESOURCE + "%s";

    private static final String OS_PROFILE_FILE_PATH = "%s/%s/%s";

    private static final String USERNAME_ADMINISTRATOR = "Administrator";
    private static final String USERNAME_USER = "User";

    public static final SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    static {
        ISO_8601.setCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));
    }

    public static String getAuthTokenForAdmin() {

        return getAuthTokenForUser(USERNAME_ADMINISTRATOR);
    }

    public static String getAuthTokenForUser(final String user) {

        if (!Settings.getAuthorizationEnabled()) {
            return null;
        }

        try {
            final EsamsChannelEnum esamsChannelEnum;
            if (user.compareToIgnoreCase(USERNAME_ADMINISTRATOR) == 0) {
                esamsChannelEnum = EsamsChannelEnum.SITE_CLOUD_ADMIN_TOKEN;
            } else if (user.compareToIgnoreCase(USERNAME_USER) == 0) {
                esamsChannelEnum = EsamsChannelEnum.SITE_CLOUD_USER_TOKEN;
            } else {
                throw new IllegalArgumentException("Unknown user: " + user);
            }
            return IAFTokenManager.getIAFTokenForUser(esamsChannelEnum.getChannelName());
        } catch (SecurityServiceException e) {
            throw new RuntimeException("Failed to get auth token for user '" + user + "'");
        }
    }

    // Common utilities.

    public static String readResource(String resourceName) throws IOException {

        InputStream stream = Utils.class.getResourceAsStream(resourceName);
        if (stream == null) {
            throw new RuntimeException("Resource '" + resourceName + "' is not found.");
        }

        return IOUtils.toString(stream);
    }

    public static Iterable<String> readResourceList(String indexResourceName) throws IOException {

        String contents = readResource(indexResourceName);
        return ImmutableList.copyOf(StringUtils.split(contents, "\r\n"));
    }

    // WebClient utilities.

    public static void get(URI url) throws IOException {

        WebClient client = WebClient.create(url);
        Response response;

        do {
            response = client.get();
            if (response.getStatus() != Response.Status.MOVED_PERMANENTLY.getStatusCode()) {
                break;
            }

            // Handle redirect responses.
            String location = (String) response.getMetadata().get("Location").get(0);
            client = WebClient.create(location);
        } while (true);

        State.httpStatus = response.getStatus();
        State.httpContent = IOUtils.toString((InputStream) response.getEntity());
    }

    // Reading HW profiles from resources.

    public static String getHWProfilePropertiesResourceName(String hwProfileName) {

        return String.format(HW_PROFILE_PROPERTIES_RESOURCE, hwProfileName);
    }

    public static Map<String, String> readHWProfilePropertiesFromResource(String hwProfileName) throws IOException {

        String resourceName = getHWProfilePropertiesResourceName(hwProfileName);
        ExamplesTable table = new ExamplesTable(readResource(resourceName));
        return readPropertiesFromTable(table);
    }

    // Reading OS profiles from resources.

    public static String getOSProfileHWProfilesResourceName(String osProfileName, String osProfileVersion) {

        return String.format(OS_PROFILE_HW_PROFILES_RESOURCE, osProfileName, osProfileVersion);
    }

    public static String getOSProfilePropertiesResourceName(String osProfileName, String osProfileVersion) {

        return String.format(OS_PROFILE_PROPERTIES_RESOURCE, osProfileName, osProfileVersion);
    }

    public static String getOSProfileFilesResourceName(String osProfileName, String osProfileVersion) {

        return String.format(OS_PROFILE_FILES_RESOURCE, osProfileName, osProfileVersion);
    }

    public static String getOSProfileFileIndexResourceName(String osProfileName, String osProfileVersion) {

        return String.format(OS_PROFILE_FILE_INDEX_RESOURCE, osProfileName, osProfileVersion);
    }

    public static String getOSProfileFileResourceName(String osProfileName, String osProfileVersion, String fileName) {

        return String.format(OS_PROFILE_FILE_RESOURCE, osProfileName, osProfileVersion, fileName);
    }

    public static String getOSProfileFilePath(String osProfileName, String osProfileVersion, String filePath) {

        return String.format(OS_PROFILE_FILE_PATH, osProfileName, osProfileVersion, filePath);
    }

    public static Iterable<String> readOSProfileHWProfilesFromResources(
            String osProfileName,
            String osProfileVersion) throws IOException {

        String resourceName = getOSProfileHWProfilesResourceName(osProfileName, osProfileVersion);
        ExamplesTable table = new ExamplesTable(readResource(resourceName));
        return readOSProfileHWProfilesFromTable(table);
    }

    public static Map<String, String> readOSProfilePropertiesFromResources(
            String osProfileName,
            String osProfileVersion) throws IOException {

        String resourceName = getOSProfilePropertiesResourceName(osProfileName, osProfileVersion);
        ExamplesTable table = new ExamplesTable(readResource(resourceName));
        return readPropertiesFromTable(table);
    }

    public static Iterable<OSProfileFile> readOSProfileFilesFromResources(
            String osProfileName,
            String osProfileVersion) throws IOException {

        String resourceName = getOSProfileFilesResourceName(osProfileName, osProfileVersion);
        ExamplesTable table = new ExamplesTable(readResource(resourceName));
        return readOSProfileFilesFromTable(table);
    }

    public static void createOSProfileFilesFromResources(String osProfileName, String osProfileVersion) throws
            IOException {

        final Map<String, String> files = readOSProfileFileContentsFromResources(osProfileName, osProfileVersion);

        for (final Entry<String, String> entry : files.entrySet()) {
            final String path = getOSProfileFilePath(osProfileName, osProfileVersion, entry.getKey());
            State.getTestHelper().putOSProfileFile(path, IOUtils.toInputStream(entry.getValue()));
        }
    }

    public static Map<String, String> readOSProfileFileContentsFromResources(
            String osProfileName,
            String osProfileVersion) throws
            IOException {

        String indexResourceName = getOSProfileFileIndexResourceName(osProfileName, osProfileVersion);

        Iterable<String> fileNames = readResourceList(indexResourceName);

        Map<String, String> files = Maps.newHashMap();

        for (String fileName : fileNames) {
            String fileResourceName = getOSProfileFileResourceName(osProfileName, osProfileVersion, fileName);
            files.put(fileName, readResource(fileResourceName));
        }

        return files;
    }

    public static Iterable<String> readOSProfileHWProfilesFromTable(ExamplesTable table) {

        return Iterables.transform(
                table.getRows(),

                new Function<Map<String, String>, String>() {
                    @Override
                    public String apply(Map<String, String> input) {

                        return input.get("name");
                    }
                });
    }

    public static Iterable<OSProfileFile> readOSProfileFilesFromTable(ExamplesTable table) {

        return Iterables.transform(
                table.getRows(),

                new Function<Map<String, String>, OSProfileFile>() {
                    @Override
                    public OSProfileFile apply(Map<String, String> input) {

                        return OSProfileFile.make(
                                input.get("path"),
                                input.get("type"),
                                Boolean.parseBoolean(input.get("template")),
                                null);
                    }
                });
    }

    // Reading properties.

    public static Map<String, String> readPropertiesFromTable(ExamplesTable table) {

        Map<String, String> result = Maps.newHashMap();

        for (Map<String, String> row : table.getRows()) {
            result.put(
                    row.get("name"),
                    row.get("value")
                            .replace("\\n", "\n")
                            .replace("_serviceHost_", Settings.getInstallServiceHost())
                            .replace("_param_base_address_", Settings.getParamConfBaseAddr())
                            .replace("_plain_base_address_", Settings.getPlainConfBaseAddr()));
        }

        return result;
    }

    public static String substituteSettings(String s) {

        return s
                .replace("$installServiceHost", Settings.getInstallServiceHost())
                .replace("$installServicePort", Settings.getInstallServicePort())
                .replace("$installServiceUrl", Settings.getInstallServiceURL())
                .replace("$paramBaseAddress", Settings.getPlainConfBaseAddr())
                .replace("\\n", "\n")
                .replace("$plainBaseAddress", Settings.getPlainConfBaseAddr());
    }

    public static void compareProperties(Map template, Map actual) {
        assertThat(
                "properties.keys",
                Sets.<Comparable>newTreeSet(actual.keySet()),
                is(Sets.<Comparable>newTreeSet(template.keySet()))
        );

        for (Map.Entry<String, String> property : ((Map<String, String>) template).entrySet()) {
            assertThat(
                    "properties[\"" + property.getKey() + "\"]",
                    (String) actual.get(property.getKey()),
                    matchesRegexp(Utils.substituteSettings(property.getValue()))
            );
        }
    }

    public static InputStream getChangedCommentAndOrderInXmlSteam(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(inputStream);

        // Mix properties
        {
            Element propertiesElement = (Element) doc.getElementsByTagName("properties").item(0);
            NodeList propertyList = propertiesElement.getElementsByTagName("property");

            if (propertyList.getLength() > 1) {
                Element firstElement = (Element) propertyList.item(0);

                propertiesElement.removeChild(firstElement);
                propertiesElement.appendChild(firstElement);
            }
        }

        // Mix files
        {
            Element filesElement = (Element) doc.getElementsByTagName("files").item(0);
            NodeList fileList = filesElement.getElementsByTagName("file");

            if (fileList.getLength() > 1) {
                Element firstElement = (Element) fileList.item(0);

                filesElement.removeChild(firstElement);
                filesElement.appendChild(firstElement);
            }
        }

        // Mix children of file
        {
            NodeList fileList = doc.getElementsByTagName("file");

            if (fileList.getLength() > 0) {
                Element fileElement = (Element) fileList.item(0);

                NodeList nodes = fileElement.getChildNodes();

                List<Element> elements = new ArrayList<Element>();

                for (int i = 0; i < nodes.getLength(); ++i) {
                    Node node = nodes.item(i);

                    if (node instanceof Element) {
                        elements.add((Element) node);
                    }
                }

                if (elements.size() > 1) {
                    Element firstElement = elements.get(0);

                    fileElement.removeChild(firstElement);
                    fileElement.appendChild(firstElement);
                }
            }
        }

        // Adds comment
        {
            Element rootElement = doc.getDocumentElement();
            Comment comment = doc.createComment("Comment for test");
            Text endLineText = doc.createTextNode("\n");

            rootElement.appendChild(comment);
            rootElement.appendChild(endLineText);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public static InputStream getChangedElementValueInXmlSteam(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException, TransformerException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(inputStream);

        // change property value
        {
            Element propertiesElement = (Element) doc.getElementsByTagName("properties").item(0);
            NodeList propertyList = propertiesElement.getElementsByTagName("property");

            if (propertyList.getLength() > 0) {
                Element firstElement = (Element) propertyList.item(0);

                Text propertyValue = (Text) firstElement.getFirstChild();
                propertyValue.setData("property that changed");
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public static Collection<OSProfile> convertToOSProfiles(ExamplesTable table) {

        Set<OSProfile> result = Sets.newHashSet();
        for (Map<String, String> row : table.getRows()) {

            OSProfile osProfile = new OSProfile();
            osProfile.name = row.get("profileName");
            osProfile.version = row.get("profileVersion");
            osProfile.properties.put("os", row.get("profileOS"));
            osProfile.properties.put("architecture", row.get("profileArch"));
            result.add(osProfile);
        }
        return result;
    }

    public static Collection<OSProfile> transformFoundOSProfiles(Collection<OSProfile> source) {

        return Lists.newArrayList(
                Iterables.transform(
                        source,
                        new Function<OSProfile, OSProfile>() {
                            @Override
                            public OSProfile apply(OSProfile input) {

                                OSProfile output = new OSProfile();
                                output.name = input.name;
                                output.version = input.version;
                                output.properties.put("os", input.properties.get("os"));
                                output.properties.put("architecture", input.properties.get("architecture"));
                                return output;
                            }
                        }));
    }

    public static Collection<OSProfile> retrieveOSProfilesByRefs(Collection<OSProfileRef> source) {

        final ImageManagementService consumer = State.getConsumer();

        return Lists.newArrayList(
                Iterables.transform(
                        source,
                        new Function<OSProfileRef, OSProfile>() {
                            @Override
                            public OSProfile apply(OSProfileRef input) {
                                OSProfile output = consumer.getOSProfile(input.name, input.version);
                                output.name = input.name;
                                output.version = input.version;
                                return output;
                            }
                        }));
    }

    @SuppressWarnings({"unchecked"})
    public static void awaitAsyncOperation() throws InterruptedException {

        while (State.operation.state == TaskState.SCHEDULED || State.operation.state == TaskState.IN_PROGRESS) {
            assertThat("State.operation.id", State.operation.id, notNullValue());

            Thread.sleep(100);
            State.operation = State.getConsumer().getTask(State.operation.id);
        }
    }

    public static void awaitAsyncOperationUsingHref() throws InterruptedException, IOException, JAXBException {

        while (State.operation.state == TaskState.SCHEDULED || State.operation.state == TaskState.IN_PROGRESS) {
            assertThat("State.operation.href", State.operation.href, notNullValue());

            Thread.sleep(100);
            Utils.get(State.operation.href);

            final JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            State.operation = (Task) unmarshaller.unmarshal(IOUtils.toInputStream(State.httpContent));
        }
    }
}
