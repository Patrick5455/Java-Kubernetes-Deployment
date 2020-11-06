import io.kubernetes.client.openapi.ApiException;
import kubernetes.models.k8s.*;
import kubernetes.services.k8sYamlDeployment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Service {

    public static void main(String[] args) throws IOException, ApiException {



        HashMap<String, String> labels = new HashMap<>();

        //Assemble Deployment from deploymentMetadata + deploymentSpec
        DeploymentYaml deploymentYaml = DeploymentYaml.builder()
                //apiVersion
                .apiVersion("apps/v1")
                //kinf
                .kind(KindTypes.Deployment)
                //Deployment spec - assemble Pod Selector and Pod Template
                .spec(DeploymentSpec.builder()
                        .selector(LabelSelector.builder()
                                .matchLabels(LabelUtils.setAnyLabels("app", "nginx"))
                                .build())
                        //Pod template
                        .template(
                                PodTemplateSpec.builder()
                                        //Pod metadata
                                        .metadata(ObjectMeta.builder()
                                                //Pod Selector
                                                .labels(LabelUtils.setAnyLabels("app", "nginx"))
                                                .build())
                                        //Pod Spec
                                        .spec(PodSpec.builder()
                                                //Nginx Container
                                                .containers(Arrays.asList(Container.builder()
                                                        .name("nginx")
                                                        .image("nginx:1.7.9")
                                                        .ports(Arrays.asList(ContainerPort.
                                                                builder()
                                                                .containerPort(90)
                                                                .build()))
                                                        .build())
                                                )
                                                .build())
                                        .build()
                        )
                        .build())
                //Deployment metadata
                .metadata(ObjectMeta.builder()
                        .name("latest-nginx-deployment4")
                        .build())
                .build();


        File file = k8sYamlDeployment.createCustomManifestFile(deploymentYaml);

     //   String preset = k8sYamlDeployment.executePresetComplexDeploy(k8sYamlDeployment.createPresetManifestFile());


        String custom = k8sYamlDeployment.executeCustomComplexDeploy(deploymentYaml);

//
    //   System.out.println("prseet: " + preset);
        System.out.println("custom"+custom);



    }

}


















































































































































































