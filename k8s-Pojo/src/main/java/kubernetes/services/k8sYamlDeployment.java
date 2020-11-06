package kubernetes.services;

import kubernetes.models.k8s.DeploymentYaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
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
    public static final String FAILURE_DEPLOY_MESSAGE = "PROCESSING_DEPLOYMENT";
    public static final String PROCESSING_DEPLOY_MESSAGE = "PROCESSING_DEPLOYMENT";
    //TODO: Add logic if deployment already exists. This returns "already exists on terminal"
    private static final String EXIST_DEPLOY = "k8sYamlDeployment already exists";

    //Filenames
    private static final String simpleDeployBashFileName = "simple-deploy.sh";
    private static final String complexDeployBashFileName = "complex-deploy.sh";
    private static final String yamlFileName = "deployment.yaml";

    //kubectl commands to write to bash file
    public static String getSimpleDeployCommand(String deploymentName, String imageName){
        return String.format("%s %s --image=%s ", SIMPLE_DEPLOY_COMMAND, deploymentName, imageName); }

    public static String getComplexDeployCommand(String yamlFileName){
        return String.format("%s %s", COMPLEX_DEPLOY_COMMAND, yamlFileName); }

    private static String writeCommandToBashFile(String fileName, String command) throws IOException {
        File file = new File(fileName);
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        fileWriter.write(command);

        fileWriter.flush();
        fileWriter.close();
        System.out.printf("Done writing command %s to bash file  %s%n",command ,file.getAbsoluteFile());
        return file.getAbsolutePath();
    }


    public  static String executeSimpleDeploy(String command) throws IOException {
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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return deploymentStatusMessage;
    }


    public static File createManifestFile(DeploymentYaml deploymentYamlParams) throws IOException {
        // Other Parameters will be added as needs be

        DeploymentYaml deploymentYaml = DeploymentYaml.builder()
                .apiVersion(deploymentYamlParams.getApiVersion())
                .kind(deploymentYamlParams.getKind())
                .metadata(deploymentYamlParams.getMetadata())
                .spec(deploymentYamlParams.getSpec())
                //   .spec(deploymentYamlParams.getSpec())
                .build();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        File manifestFIle=null;
        try {
            File file = new File(yamlFileName);
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

    public  static String executeComplexDeploy(DeploymentYaml deploymentYamlParams) throws IOException {
        deploymentStatusMessage = FAILURE_DEPLOY_MESSAGE;

        File file = createManifestFile(deploymentYamlParams);
        try {
            String[] cmd = {"sh", writeCommandToBashFile(complexDeployBashFileName, getComplexDeployCommand(file.getAbsolutePath()))};
            Process p = Runtime.getRuntime().exec(cmd);
            System.out.println("DOne Executing");

            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            System.out.println("Getting Response --> \n");


            String line;
            StringBuilder builder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                builder.append(line);
                System.out.println(line);
            }
            System.out.println(builder.toString());
            if(builder.toString().contains("created")){
                deploymentStatusMessage = SUCCESS_DEPLOY_MESSAGE;
                System.out.println("Created");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return deploymentStatusMessage;
    }

//    public static DeploymentStatus setGrpcDeploymentStatus(String deploymentStatusMessage){
//        return DeploymentStatus.valueOf(deploymentStatusMessage);
//    }



    public static String useK8sClient(File file){

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
                System.out.println("Created");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return deploymentStatusMessage;
    }


}


