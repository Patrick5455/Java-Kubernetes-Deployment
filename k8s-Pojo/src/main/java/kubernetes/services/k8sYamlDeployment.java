package kubernetes.services;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.*;
import kubernetes.models.k8s.DeploymentYaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class k8sYamlDeployment {


    private static String deploymentStatusMessage;

    private DeploymentStatus deploymentStatusGrpc;

    //kubectl bash commands
    public static final String SIMPLE_DEPLOY_COMMAND = "kubectl create deployment";
    public static final String COMPLEX_DEPLOY_COMMAND = "kubectl apply -f";
    public static final String GET_PODS_COMMAND = "kubectl get pods";
    public static final String GET_POD_COMMAND = "kubectl get pod";
    public static final String GET_EXTENDED_POD_COMMAND = "kubectl get pod -o wide";
    public static final String GET_SERVICE_COMMAND = "kubectl get services";
    public static final String GET_IMAGES_COMMAND = "kubectl get images";
    public static final String GET_ALL_COMMAND = "kubectl get all";
    private static final String DESCRIBE_POD_COMMAND = "kubectl describe pod";
    public static final String DESCRIBE_SERVICE_COMMAND = "kubectl describe services";
    public static final String ROLL_OUT_STATUS_COMMAND = "kubectl rollout status";

    //k8sYamlDeployment Status Code
    public static final Long NOT_SUCCESSFUL = 404L;


    //k8sYamlDeployment Status Message
    public static final String SUCCESS_DEPLOY_MESSAGE = "SUCCESSFUL_DEPLOYMENT";
    public static final String FAILURE_DEPLOY_MESSAGE = "FAILED DEPLOYMENT";
    public static final String PROCESSING_DEPLOY_MESSAGE = "PROCESSING_DEPLOYMENT";
    private static final String EXISTING_DEPLOYMENT = "DEPLOYMENT ALREADY EXISTS";

    //Filenames
    private static final String simpleDeployBashFileName = "simple-deploy.sh";
    private static final String complexDeployBashFileName = "complex-deploy.sh";
    private static final String customYamlFileName = "custom-deployment.yaml";
    private static final String presetYamlFileName = "preset-deployment.yaml";


    //kubectl commands to write to bash file
    public static String getSimpleDeployCommand(String deploymentName, String imageName){
        return String.format("%s %s --image=%s ", SIMPLE_DEPLOY_COMMAND, deploymentName, imageName); }

    public static String getComplexDeployCommand(String yamlFileName){
        return String.format("%s %s", COMPLEX_DEPLOY_COMMAND, yamlFileName); }

    //Write commands to a bash file
    private static String writeCommandToBashFile(String fileName, String command) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        fileWriter.write(command);
        fileWriter.flush();
        fileWriter.close();
        System.out.printf("Done writing command: %s to --> %nbash file:  %s%n%n",command ,file.getAbsoluteFile());
        return file.getAbsolutePath();
    }


    /**
     * Collects builder object of DeploymentYaml to convert to yaml file
     * **/
    public static File createCustomManifestFile(DeploymentYaml deploymentYamlParams) throws IOException {

        // Other Parameters will be added as needs be
        DeploymentYaml deploymentYaml = DeploymentYaml.builder()
                .apiVersion(deploymentYamlParams.getApiVersion())
                .kind(deploymentYamlParams.getKind())
                .metadata(deploymentYamlParams.getMetadata())
                .spec(deploymentYamlParams.getSpec())
                .build();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        File manifestFIle=null;

        try {
            File file = new File(customYamlFileName);
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            Yaml yaml = new Yaml(options);
            yaml.dump(deploymentYaml, fileWriter);
            manifestFIle = file.getAbsoluteFile();
            System.out.println(yaml.dump(deploymentYaml));
        } catch (IOException e){
            e.printStackTrace();
        }
        return manifestFIle ;
    }

    public static File createPresetManifestFile() throws IOException, ApiException {

        V1ContainerPort nginxPort = new V1ContainerPort();
        nginxPort.setContainerPort(90);

        //Nginx Container
        V1Container nginxContainer = new V1Container();
        nginxContainer.setName("nginx");
        nginxContainer.setImage("nginx:1.7.9");
        nginxContainer.setPorts(Arrays.asList(nginxPort));

        //Pod spec
        V1PodSpec podSpec = new V1PodSpec();
        podSpec.addContainersItem(nginxContainer);

        //Pod metadata
        HashMap<String, String> labels = new HashMap<>();
        labels.put("app", "nginx");
        V1ObjectMeta podMetadata = new V1ObjectMeta();
        podMetadata.setLabels(new HashMap<>(labels));

        //Pod template
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        podTemplateSpec.setMetadata(podMetadata);
        podTemplateSpec.setSpec(podSpec);

        //Pod Selector
        V1LabelSelector podSelector = new V1LabelSelector();
        podSelector.setMatchLabels(new HashMap<>(labels));

        //Deployment spec - assemble Pod Selector and Pod Template
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec();
        deploymentSpec.setSelector(podSelector);
        deploymentSpec.setTemplate(podTemplateSpec);

        //Deployment metadata
        V1ObjectMeta deploymentMetadata = new V1ObjectMeta();
        deploymentMetadata.setName("new-nginx-deployment");

        //Assemble Deployment from deploymentMetadata + deploymentSpec
        V1Deployment deployment = new V1Deployment();
        deployment.setApiVersion("apps/v1");
        deployment.setKind("Deployment");
        deployment.setMetadata(deploymentMetadata);
        deployment.setSpec(deploymentSpec);

//Optionally Dump the YAML manifest
        System.out.println(io.kubernetes.client.util.Yaml.dump(deployment));

        File file = new File(presetYamlFileName);
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(io.kubernetes.client.util.Yaml.dump(deployment));
        fileWriter.flush();
        fileWriter.close();
        System.out.println(file.getAbsoluteFile());


        //Optionally call K8S API server directly
//        ApiClient client = Config.defaultClient();
//        Configuration.setDefaultApiClient(client);
//        AppsV1Api appsApi = new AppsV1Api();
//        appsApi.createNamespacedDeployment(
//                "hello world",
//                deployment, //This is the V1Deployment object
//                null,
//                null,
//                null
//        );

        return file;
    }

    public  static String executeSimpleDeploy(String command) throws IOException, InterruptedException {
        deploymentStatusMessage = FAILURE_DEPLOY_MESSAGE;
        try {
            String[] cmd = {"sh", writeCommandToBashFile(simpleDeployBashFileName, command)};
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String line;
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                builder.append(line);
                System.out.println(line);
            }
            if(builder.toString().contains("created")){
                deploymentStatusMessage = SUCCESS_DEPLOY_MESSAGE;
            }
            else if (builder.toString().contains("configured")){
                deploymentStatusMessage = EXISTING_DEPLOYMENT;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return String.format( "Deployment Status:  %s", deploymentStatusMessage);
    }



    /**
     * Collects DeploymentYaml object and transform to yaml file internally using createCustomYamlFIle
     * */
    public  static String executeCustomComplexDeploy(DeploymentYaml deploymentYamlParams) throws IOException {
        deploymentStatusMessage = FAILURE_DEPLOY_MESSAGE;
        File file = createCustomManifestFile(deploymentYamlParams);

        try {
            String[] cmd = {"sh", writeCommandToBashFile(complexDeployBashFileName, getComplexDeployCommand(file.getAbsolutePath()))};
            Process p = Runtime.getRuntime().exec(cmd);
            System.out.println("Done Executing bash command!\n");

            p.waitFor();
            System.out.println("Getting Response -->");
            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            String line;
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                builder.append(line);
                System.out.println(line);
            }
            System.out.println(builder.toString());
            if(builder.toString().contains("created")){
                deploymentStatusMessage = SUCCESS_DEPLOY_MESSAGE;
            }
            else if (builder.toString().contains("configured")){
                deploymentStatusMessage = EXISTING_DEPLOYMENT;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return String.format( "\nDeployment Status:  %s", deploymentStatusMessage);
    }


    /**
     * Collects file of presetManifestFile from createPresetManifestFile
     * */
    public static String executePresetComplexDeploy(File file){
        deploymentStatusMessage = FAILURE_DEPLOY_MESSAGE;
        try {
            String[] cmd = {"sh", writeCommandToBashFile(complexDeployBashFileName, getComplexDeployCommand(file.getAbsolutePath()))};
            Process p = Runtime.getRuntime().exec(cmd);
            System.out.println("DOne Executing");
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            System.out.println("Getting Response");

            String line;
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                builder.append(line);
                System.out.println(line);
            }
            if(builder.toString().contains("created")){
                deploymentStatusMessage = SUCCESS_DEPLOY_MESSAGE;
            }
            else if (builder.toString().contains("configured")){
                deploymentStatusMessage = EXISTING_DEPLOYMENT;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return String.format( "Deployment Status:  %s", deploymentStatusMessage);
    }



}


