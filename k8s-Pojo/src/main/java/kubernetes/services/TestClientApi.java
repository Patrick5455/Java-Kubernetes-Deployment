package kubernetes.services;


import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.*;
//import org.services.snakeyaml.Yaml;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;
import kubernetes.models.k8s.DeploymentYaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class TestClientApi {

   private Yaml yaml;

    public static void createSimpleDeployment(DeploymentYaml deploymentYaml) throws IOException, ApiException {

        V1Deployment deployment = createDeployment(deploymentYaml);

        ApiClient apiClient = Config.defaultClient();
        Configuration.setDefaultApiClient(apiClient);
        AppsV1Api appsV1Api = new AppsV1Api();

        deployment =  appsV1Api.createNamespacedDeployment(
                deploymentYaml.getMetadata().getName(), //default,
                deployment,
                null,
                null,
                null);


        System.out.println(Yaml.dump(deployment));
    }

    private static V1Deployment createDeployment(DeploymentYaml deploymentYaml)
    {
       DeploymentYaml newDeploymentYaml = DeploymentYaml.builder()
                .apiVersion(deploymentYaml.getApiVersion())
               .kind(deploymentYaml.getKind())
               .spec(deploymentYaml.getSpec())
               .metadata(deploymentYaml.getMetadata())
               .build();

        V1Deployment v1Deployment = new V1DeploymentBuilder()
                .withApiVersion(newDeploymentYaml.getApiVersion())
                .withKind(newDeploymentYaml.getKind().toString())
                .withNewMetadata()
                    .withName(newDeploymentYaml.getMetadata().getName())
                    //TODO: add more metadata params
                .endMetadata()
                .withNewSpec()
                    .withNewSelector()
//                        .addToMatchLabels(
//                                deploymentYaml.getSpec().getSelector()
//                                        .getMatchLabels()
//                        )
                    .endSelector()
                  .withNewTemplate()
//                    .withNewMetadata()
//                        .addToLabels(
//                                deploymentYaml.getSpec()
//                                    .getTemplate().getMetadata()
//                                     .getLabels()
//                        )
                 //   .endMetadata()
                    .withNewSpec()
                        .addNewContainer()
                            .withImage(
                                    deploymentYaml.getSpec()
                                        .getContainer()
                                            .getImage()
                                )
                            .withName(
                                    deploymentYaml.getSpec()
                                            .getContainer()
                                            .getName()
                            )
                            .addNewPort()
                                .withContainerPort(deploymentYaml.getSpec()
                                        .getContainer()
                                        .getPort().getContainerPort())
                            .endPort()
                        .endContainer()
                .endSpec()
        .endTemplate()
      .endSpec()
 .build();

        return v1Deployment;
    }







//        HashMap<String, String> map = new HashMap();
//
//        map.put("api", "nginx");
//
//        DeploymentYaml deploymentYaml =
//
//                DeploymentYaml.builder()
//                        .apiVersion("apps/V1")
//                        .kind(KindTypes.Deployment)
//                        .metadata(ObjectMeta.builder()
//                                .name("nginx-deployment")
//                                .build())
//                        .spec(DeploymentSpec.builder()
//                                .selector(
//                                        LabelSelector.builder()
//                                                .matchLabels(map).build())
//                                .template(
//                                        PodTemplateSpec.builder()
//                                                .metadata(ObjectMeta.builder()
//                                                        .labels(map).build()
//                                                ).build()
//                                ).build()
//                            )
//                        .spec(
//                                DeploymentSpec.builder()
//                                        .containers(Arrays.asList(Container.builder()
//                                                .image("nginx:1.7.0")
//                                                .name("nginx")
//                                                .containerPorts(
//                                                        Arrays.asList(
//                                                                ContainerPort.builder()
//                                                                        .containerPort(80)
//                                                                        .build()
//                                                        )
//                                                )
//                                                .build()))
//                                        .build()
//                        )
//                .build();

//        System.out.println(deploymentYaml.toString());
//        File file = new File("new2.yaml");
//       file.createNewFile();
//        FileWriter fileWriter = new FileWriter(file);
//
//        fileWriter.write(Yaml.dump(deploymentYaml));
//        fileWriter.flush();
//        fileWriter.close();
//       System.out.println(file.getAbsoluteFile());
//
//        TestClientApi.createSimpleDeployment(deploymentYaml);


    public static File testNow() throws IOException, ApiException {

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
        System.out.println(Yaml.dump(deployment));


        File file = new File("new.yaml");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);

        fileWriter.write(Yaml.dump(deployment));
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


}
