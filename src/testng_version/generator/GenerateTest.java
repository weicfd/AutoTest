package testng_version.generator;

import testng_version.entity.Method;
import testng_version.entity.Service;
import testng_version.parser.DataParser;
import testng_version.parser.MethodsParser;
import testng_version.parser.PatternParser;

import java.io.File;
import java.util.*;

import static testng_version.Config.*;

public class GenerateTest {
    private DataParser dataParser;
    private HashMap<Integer, String> dataMap;
    private PatternParser patternParser;
    private List<String> patterns;
    private List<Service> services;

    private static int testFileID = 0;

    public GenerateTest() {
        // parse dataMap
        dataParser = new DataParser();
        dataMap = dataParser.getDataCode(); // for the update method to modify this step
        // parse service
        File service_dir = new File(service_xml_dir);
        String[] service_paths = service_dir.list();
        for (String path :
                service_paths) {
            services.add(new MethodsParser(path).getService());
        }

        // parse patterns
        patternParser = new PatternParser(pattern_file_path);
        patterns = patternParser.getPatterns(); // for the update method to modify this step
    }

    public static void main(String[] args) {
        System.out.println("Step 1: initialize the dataMap, services and pattern");
        GenerateTest generateTest = new GenerateTest();

        System.out.println("Step 2: for each pattern, generate several testNG class files as one test");
        // sort the patterns first
        Collections.sort(generateTest.patterns);

        System.out.println("Step 3: Pattern - Methods Mapping");
        // group by service
        for (Service s :
                generateTest.services) {
            for (String pat :
                    generateTest.patterns) {
                List<Method> testPath = new ArrayList<>();
                generateTest.testPathDFS(0, testPath, pat, s);
            }
        }

    }

    private void testPathDFS(int loc, List<Method> testPath, String pat, Service s) {
        if (loc == pat.length())
            generateTest(testPath, s.getService_name(), pat);
        else {
            List<Method> methods = s.getMethods(pat.charAt(loc) - '0');
            for (Method method:
                    methods) {
                testPath.add(method);
                testPathDFS(loc + 1, testPath, pat, s);
                testPath.remove(testPath.size() - 1);
            }
        }
    }

    public void generateTest(List<Method> testPath, String service_name, String pat) {
        String targetPath = generate_test_dir + service_name;
        File targetDir = new File(targetPath);
        if (!targetDir.exists()) targetDir.mkdirs();

        System.out.println("Step 4: scan the target method with locator param");
        TemplateWriter writer1 = new TemplateWriter(testFileID++, testPath, service_name, true, pat, dataMap);
        TemplateWriter writer2 = new TemplateWriter(testFileID++, testPath, service_name, false, pat, dataMap);
    }


}
