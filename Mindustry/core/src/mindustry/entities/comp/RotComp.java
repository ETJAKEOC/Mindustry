package mindustry.entities.comp;

import mindustry.annotations.Annotations.Component;
import mindustry.annotations.Annotations.SyncField;
import mindustry.annotations.Annotations.SyncLocal;
import mindustry.gen.*;

@Component
abstract class RotComp implements Entityc {
	@SyncField(false)
	@SyncLocal
	float rotation;
}
