package arc.util.serialization;

import java.io.InputStream;

import arc.files.Fi;

public interface BaseJsonReader {
	JsonValue parse(InputStream input);

	default JsonValue parse(Fi file) {
		return parse(file.read());
	}
}
