package videostreaming.uploadservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import videostreaming.uploadservice.data.VideoRepository;

import videostreaming.uploadservice.dto.VideoDto;
import videostreaming.uploadservice.model.Video;
import videostreaming.uploadservice.externalservices.AuthenticationService;
import videostreaming.uploadservice.externalservices.FileStorageService;
import videostreaming.uploadservice.utilities.Mapper;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<VideoDto> upload(HttpServletRequest request,
                                           @RequestParam String originalFilename,
                                           @RequestParam MultipartFile videoFile,
                                           @RequestParam MultipartFile thumbnailImageFile,
                                           @RequestParam Long videoDuration,
                                           @RequestParam (required = false) String videoDescription) {

        if (!authenticationService.isUserAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);


        String ownerUserName = authenticationService.getUsernameFromToken(request);
        String videoUrl = fileStorageService.saveInCloud(videoFile);
        String thumbnailImageUrl = fileStorageService.saveInCloud(thumbnailImageFile);

        Video video = new Video(ownerUserName, originalFilename, videoUrl, thumbnailImageUrl, videoDuration, videoDescription);

        videoRepository.save(video);

        VideoDto videoDto = Mapper.mapVideoToVideoDto(video);

        return ResponseEntity.ok(videoDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable Long id) {

        if (!authenticationService.isUserAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Video video = videoRepository.findById(id).orElse(null);

        if (video == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (!authenticationService.getUsernameFromToken(request).equals(video.getOwnerUserName()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        fileStorageService.deleteFromCloud(video.getVideoUrl());
        fileStorageService.deleteFromCloud(video.getThumbnailImageUrl());

        videoRepository.delete(video);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/edit/{videoId}")
    public ResponseEntity<VideoDto> editVideo(HttpServletRequest request,
                                           @PathVariable Long videoId,
                                           @RequestParam String originalFilename,
                                           @RequestParam (required = false) MultipartFile thumbnailImageFile,
                                           @RequestParam (required = false) String videoDescription) {

        if (!authenticationService.isUserAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        String ownerUserName = authenticationService.getUsernameFromToken(request);
        if (!video.getOwnerUserName().equals(ownerUserName))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);


        video.setOriginalFileName(originalFilename);
        video.setVideoDescription(videoDescription);

        if (thumbnailImageFile != null) {
            fileStorageService.deleteFromCloud(video.getThumbnailImageUrl());
            video.setThumbnailImageUrl( fileStorageService.saveInCloud(thumbnailImageFile) );
        }
        videoRepository.save(video);

        VideoDto videoDto = Mapper.mapVideoToVideoDto(video);

        return ResponseEntity.ok(videoDto);
    }
}
