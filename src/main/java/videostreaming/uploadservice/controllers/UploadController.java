package videostreaming.uploadservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import videostreaming.uploadservice.data.VideoRepository;

import videostreaming.uploadservice.dto.VideoDto;
import videostreaming.uploadservice.model.Video;
import videostreaming.uploadservice.serviceintegrations.AuthenticationService;
import videostreaming.uploadservice.serviceintegrations.FileStorageService;
import videostreaming.uploadservice.utilities.Mapper;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final VideoRepository videoRepository;
    private final AuthenticationService authenticationService;
    private final FileStorageService fileStorageService;

    @Autowired
    public UploadController(VideoRepository videoRepository, AuthenticationService authenticationService, FileStorageService fileStorageService) {
        this.videoRepository = videoRepository;
        this.authenticationService = authenticationService;
        this.fileStorageService = fileStorageService;
    }



    @PostMapping("/upload")
    public ResponseEntity<?> upload(HttpServletRequest request,
                                           @RequestParam String originalFilename,
                                           @RequestParam MultipartFile videoFile,
                                           @RequestParam MultipartFile thumbnailImageFile) {


        if (!authenticationService.isUserAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");


        String ownerUserName = authenticationService.getUsernameFromToken(request);
        String videoUrl = fileStorageService.saveInCloud(videoFile);
        String thumbnailImageUrl = fileStorageService.saveInCloud(thumbnailImageFile);

        Video video = new Video(ownerUserName, originalFilename, videoUrl, thumbnailImageUrl);

        videoRepository.save(video);

        VideoDto videoDto = Mapper.mapVideoToVideoDto(video);

        return ResponseEntity.ok(videoDto);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request) {
        return null;
    }
}
