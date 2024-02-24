package videostreaming.uploadservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import videostreaming.uploadservice.data.VideoRepository;
import videostreaming.uploadservice.model.Video;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final VideoRepository videoRepository;

    @Autowired
    public UploadController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {

        Video video = new Video(
            "example original file name",
            "example video url",
            "example thumb url"
        );

        videoRepository.save(video);


        return ResponseEntity.ok(video);
    }
}
