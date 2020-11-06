package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.kubernetes.client.openapi.models.*;
import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Data
@JsonDeserialize
public class PresetDeployment {

    private String apiVersion;
    private KindTypes kind;
    private  Container container;
    private HashMap<String, String> labels;
    private String objectMetaDataName;


    public PresetDeployment(String apiVersion, KindTypes kind, Container container, HashMap<String, String> labels, String objectMetaDataName) {
        this.apiVersion = apiVersion;
        this.kind = kind;
        this.container = container;
        this.labels = labels;
        this.objectMetaDataName = objectMetaDataName;
    }

    public static void main(String[] args) throws IOException {
        PresetDeployment presetDeployment = new PresetDeployment("api/V1", KindTypes.Deployment, Container.builder()
                .name("nginx")
                .image("nginx:1.7.9")
                .port(ContainerPort.builder()
                        .containerPort(90)
                        .build())
                .ports(new ArrayList<>(Arrays.asList(ContainerPort.builder()
                        .containerPort(90)
                        .build())))
                .build(),
                LabelUtils.setAnyLabels("app", "nginx"),
                ("newest-nginx-deployment")
                );
        createDeploymentYamlFile(presetDeployment);
    }


    private static final String presetYamlFileName = "preset-deployment.yaml";


    public static HashMap<String, Object> getPresetDeploymentHashMap(PresetDeployment presetDeployment) throws IllegalArgumentException{

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> presetDeploymentMap = new HashMap<>();

        try {
            presetDeploymentMap = objectMapper.convertValue(presetDeployment, HashMap.class);
        }
        catch (IllegalArgumentException e){
          System.err.println(e.getMessage() + e.toString());
        }
        return presetDeploymentMap;
    }



    public static File createDeploymentYamlFile(PresetDeployment presetDeployment) throws IOException {

        HashMap<String, Object> map = PresetDeployment
                .getPresetDeploymentHashMap(presetDeployment);

        System.out.println(map.toString());

        V1ContainerPort nginxPort = new V1ContainerPort();
        nginxPort.setContainerPort(presetDeployment.getContainer().getPort().getContainerPort());

        //Nginx ContainerZ
        V1Container nginxContainer = new V1Container();
        nginxContainer.setName(presetDeployment.getContainer().getName());
        nginxContainer.setImage(presetDeployment.getContainer().getImage());
        nginxContainer.setPorts(Arrays.asList(nginxPort));

        //Pod spec
        V1PodSpec podSpec = new V1PodSpec();
        podSpec.addContainersItem(nginxContainer);

        //Pod metadata
        V1ObjectMeta podMetadata = new V1ObjectMeta();
        podMetadata.setLabels(presetDeployment.getLabels());

        //Pod template
        V1PodTemplateSpec podTemplateSpec = new V1PodTemplateSpec();
        podTemplateSpec.setMetadata(podMetadata);
        podTemplateSpec.setSpec(podSpec);

        //Pod Selector
        V1LabelSelector podSelector = new V1LabelSelector();
        podSelector.setMatchLabels(presetDeployment.getLabels());

        //Deployment spec - assemble Pod Selector and Pod Template
        V1DeploymentSpec deploymentSpec = new V1DeploymentSpec();
        deploymentSpec.setSelector(podSelector);
        deploymentSpec.setTemplate(podTemplateSpec);

        //Deployment metadata
        V1ObjectMeta deploymentMetadata = new V1ObjectMeta();
        deploymentMetadata.setName(presetDeployment.getObjectMetaDataName());

        //Assemble Deployment from deploymentMetadata + deploymentSpec
        V1Deployment deployment = new V1Deployment();
        deployment.setApiVersion(presetDeployment.getApiVersion());
        deployment.setKind(presetDeployment.getKind().toString());
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


        return file;
    }


}
