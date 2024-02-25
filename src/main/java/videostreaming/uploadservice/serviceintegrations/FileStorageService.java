package videostreaming.uploadservice.serviceintegrations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import videostreaming.uploadservice.dto.VideoDto;

@Service
public class FileStorageService {
    private final String serviceUrl = "http://host.docker.internal:8080/cloud";

    private final RestTemplate restTemplate;

    @Autowired
    public FileStorageService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String saveInCloud(MultipartFile file) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate
                .exchange(serviceUrl + "/upload", HttpMethod.POST, requestEntity, String.class)
                .getBody();
    }

    public void deleteFromCloud(String fileUrl) {
        restTemplate.delete(serviceUrl + "/delete?fileUrl=" + fileUrl);
    }

}
