package videostreaming.uploadservice.data;

import org.springframework.data.jpa.repository.JpaRepository;
import videostreaming.uploadservice.model.Video;

public interface VideoRepository extends JpaRepository<Video, Long> {

}
