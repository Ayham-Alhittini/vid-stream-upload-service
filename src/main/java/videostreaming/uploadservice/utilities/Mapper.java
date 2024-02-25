package videostreaming.uploadservice.utilities;

import videostreaming.uploadservice.dto.VideoDto;
import videostreaming.uploadservice.model.Video;

public class Mapper {
    public static VideoDto mapVideoToVideoDto(Video video) {
        return new VideoDto(
                video.getId(),
                video.getOwnerUserName(),
                video.getOriginalFileName(),
                video.getVideoUrl(),
                video.getThumbnailImageUrl()
        );
    }
}
