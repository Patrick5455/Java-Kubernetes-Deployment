package kubernetes.models.k8s;


import java.util.List;
import java.util.Map;

public abstract class MasterYaml {
    private String apiVersion;
    private KindTypes kind;
    private String name;
    private String image;
    private String replicas;
    private String app;
    private String value;
    private String key;
    private String containerPort;
    private String nodePort;
    //   private  Map <String,String> metadata = new HashMap<>();
    private Map<String, String> secretKeyRef;
    private Map<String, String> configMapKeyRef;
    private Map<String, String> valueFrom;
    private int port;
    private int targetPort;
    private Map<String, String> spec;
    private Map<String, String> labels;
    private Map<String, String> selector;
    private Map<String, String> env;
    private String type;
    private List containers;
    private List ports;
    private ObjectMeta metadata;


    public MasterYaml() {}

    //most kubernetes services files needs this
    public MasterYaml(String apiVersion, KindTypes kind, ObjectMeta metadata){
        this.apiVersion =apiVersion;
        this.kind = kind;
        this.metadata = metadata;
    }



}




