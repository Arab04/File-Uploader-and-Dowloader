package uploaddownload.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperty {

	private String uploadDir;
}
