package kubernetes.models.k8s;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PresetDeployment {

    private String name;
    private String image;
    private String apiVersion;
    private String kind;
    private List<ContainerPort> ports;
    private ContainerPort containerPort;
    private HashMap<String, String> labels;
    private String objectMetaDataName;



    ObjectMapper objectMapper = new ObjectMapper();



}
